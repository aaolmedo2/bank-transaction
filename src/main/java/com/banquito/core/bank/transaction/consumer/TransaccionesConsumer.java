package com.banquito.core.bank.transaction.consumer;

import com.banquito.core.bank.transaction.dto.TransaccionesSolicitudDTO;
import com.banquito.core.bank.transaction.model.Transacciones;
import com.banquito.core.bank.transaction.service.TransaccionesService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import jakarta.jms.Message;
import jakarta.jms.TextMessage;
import jakarta.jms.JMSException;

@Component
@Slf4j
public class TransaccionesConsumer {

    private final TransaccionesService transaccionesService;

    public TransaccionesConsumer(TransaccionesService transaccionesService) {
        this.transaccionesService = transaccionesService;
    }

    // Consumer para cola de DEPÓSITOS
    @JmsListener(destination = "${colas.deposito.queue-name}", concurrency = "2-5")
    public void procesarDeposito(Message message) {
        procesarTransaccion(message, "DEPOSITO");
    }

    // Consumer para cola de RETIROS
    @JmsListener(destination = "${colas.retiro.queue-name}", concurrency = "2-5")
    public void procesarRetiro(Message message) {
        procesarTransaccion(message, "RETIRO");
    }

    // Consumer para cola de TRANSFERENCIAS
    @JmsListener(destination = "${colas.transferencia.queue-name}", concurrency = "2-5")
    public void procesarTransferencia(Message message) {
        procesarTransaccion(message, "TRANSFERENCIA");
    }

    // Método común para procesar transacciones de cualquier tipo
    private void procesarTransaccion(Message message, String tipoEsperado) {
        TransaccionesSolicitudDTO dto = null;
        String jsonMessage = null;
        try {
            // Extraer el texto del mensaje JMS
            if (message instanceof TextMessage) {
                jsonMessage = ((TextMessage) message).getText();
                log.info("MS2 EJECUTOR - Recibiendo mensaje {} desde cola: {}", tipoEsperado, jsonMessage);
            } else {
                throw new IllegalArgumentException("Mensaje recibido no es un TextMessage");
            }

            // Deserializar manualmente el JSON a nuestro DTO
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            objectMapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
                    false);

            dto = objectMapper.readValue(jsonMessage, TransaccionesSolicitudDTO.class);

            log.info(
                    "MS2 EJECUTOR - Transacción {} deserializada: tipo={}, monto={}, cuentaOrigen={}, cuentaDestino={}",
                    tipoEsperado, dto.getTipoTransaccion(), dto.getMonto(), dto.getNumeroCuentaOrigen(),
                    dto.getNumeroCuentaDestino());

            // Validaciones básicas del DTO
            if (dto.getTipoTransaccion() == null || dto.getTipoTransaccion().trim().isEmpty()) {
                throw new IllegalArgumentException("TipoTransaccion es requerido");
            }

            // Validar que el tipo de transacción coincida con la cola esperada
            if (!tipoEsperado.equals(dto.getTipoTransaccion())) {
                throw new IllegalArgumentException(
                        String.format("Tipo de transacción incorrecto. Esperado: %s, Recibido: %s",
                                tipoEsperado, dto.getTipoTransaccion()));
            }

            if (dto.getNumeroCuentaOrigen() == null || dto.getNumeroCuentaOrigen().trim().isEmpty()) {
                throw new IllegalArgumentException("NumeroCuentaOrigen es requerido");
            }

            if (dto.getMonto() == null || dto.getMonto().compareTo(java.math.BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Monto debe ser mayor que cero");
            }

            // Validación específica para transferencias
            if ("TRANSFERENCIA".equals(tipoEsperado)) {
                if (dto.getNumeroCuentaDestino() == null || dto.getNumeroCuentaDestino().trim().isEmpty()) {
                    throw new IllegalArgumentException("NumeroCuentaDestino es requerido para transferencias");
                }
            }

            // EJECUTAR la transacción (MS1 ya validó todo)
            Transacciones resultado = transaccionesService.procesar(dto);

            log.info("MS2 EJECUTOR - Transacción {} ejecutada exitosamente: ID={}, Estado={}",
                    tipoEsperado, resultado.getId(), resultado.getEstado());

        } catch (JMSException e) {
            log.error("MS2 EJECUTOR - Error al extraer mensaje JMS {}: {}", tipoEsperado, e.getMessage(), e);
            throw new RuntimeException("Error procesando mensaje JMS: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("MS2 EJECUTOR - Error ejecutando transacción {} de cuenta {}: {}",
                    tipoEsperado, dto != null ? dto.getNumeroCuentaOrigen() : "unknown", e.getMessage(), e);

            // Re-lanzar para que ActiveMQ maneje retry/DLQ si está configurado
            throw new RuntimeException("Error ejecutando transacción: " + e.getMessage(), e);
        }
    }
}
