package com.banquito.core.bank.transaction.service;

import com.banquito.core.bank.transaction.dto.TransaccionesSolicitudDTO;
import com.banquito.core.bank.transaction.exception.CuentaNoEncontradaException;
import com.banquito.core.bank.transaction.exception.SaldoInsuficienteException;
import com.banquito.core.bank.transaction.exception.TransaccionException;
import com.banquito.core.bank.transaction.model.CuentaCliente;
import com.banquito.core.bank.transaction.model.Transacciones;
import com.banquito.core.bank.transaction.repository.CuentaClienteRepository;
import com.banquito.core.bank.transaction.repository.TransaccionesRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Slf4j
public class TransaccionesService {

    private final TransaccionesRepository transaccionesRepository;
    private final CuentaClienteRepository cuentaClienteRepository;

    public TransaccionesService(TransaccionesRepository transaccionesRepository,
            CuentaClienteRepository cuentaClienteRepository) {
        this.transaccionesRepository = transaccionesRepository;
        this.cuentaClienteRepository = cuentaClienteRepository;
    }

    @Transactional
    public Transacciones procesar(TransaccionesSolicitudDTO dto) {
        log.info("MS2 EJECUTOR - Procesando transacción: tipo={}, monto={}, cuenta={}",
                dto.getTipoTransaccion(), dto.getMonto(), dto.getNumeroCuentaOrigen());

        // Crear registro de transacción
        Transacciones transaccion = crearTransaccion(dto);

        try {
            // MS2 como EJECUTOR: Procesa la transacción SIN validaciones complejas de
            // negocio
            // MS1 ya validó todo, nosotros solo ejecutamos la operación
            switch (Transacciones.TipoTransaccionEnum.valueOf(dto.getTipoTransaccion())) {
                case DEPOSITO:
                    procesarDeposito(dto);
                    break;
                case RETIRO:
                    procesarRetiro(dto);
                    break;
                default:
                    throw new TransaccionException("Tipo de transacción no válido: " + dto.getTipoTransaccion());
            }

            // Marcar como completada
            transaccion.setEstado(Transacciones.EstadoTransaccionesEnum.COMPLETADA);

            log.info("MS2 EJECUTOR - Transacción ejecutada exitosamente: ID={}", transaccion.getId());

        } catch (Exception e) {
            log.error("MS2 EJECUTOR - Error ejecutando transacción {}: {}", transaccion.getId(),
                    e.getMessage());

            // Marcar como error
            transaccion.setEstado(Transacciones.EstadoTransaccionesEnum.ERROR);

            throw e;
        } finally {
            // Guardar el estado final de la transacción
            transaccion = transaccionesRepository.save(transaccion);
        }

        return transaccion;
    }

    private Transacciones crearTransaccion(TransaccionesSolicitudDTO dto) {
        return transaccionesRepository.save(Transacciones.builder()
                .numeroCuentaOrigen(dto.getNumeroCuentaOrigen())
                .numeroCuentaDestino(dto.getNumeroCuentaDestino())
                .tipoTransaccion(Transacciones.TipoTransaccionEnum.valueOf(dto.getTipoTransaccion()))
                .monto(dto.getMonto())
                .descripcion(dto.getDescripcion())
                .estado(Transacciones.EstadoTransaccionesEnum.PENDIENTE)
                .build());
    }

    private void procesarDeposito(TransaccionesSolicitudDTO dto) {
        log.debug("Ejecutando depósito para cuenta: {}", dto.getNumeroCuentaOrigen());

        // MS2 como EJECUTOR: Solo verifica que la cuenta exista y esté activa
        // NO valida saldos (eso ya lo hizo MS1)
        CuentaCliente cuenta = obtenerCuentaPorNumero(dto.getNumeroCuentaOrigen());
        validarCuentaActiva(cuenta);

        // Ejecutar la operación: sumar el monto al saldo
        BigDecimal saldoAnterior = cuenta.getSaldo();
        BigDecimal nuevoSaldo = saldoAnterior.add(dto.getMonto());
        cuenta.setSaldo(nuevoSaldo);

        cuentaClienteRepository.save(cuenta);

        log.info("Depósito ejecutado - Cuenta: {}, Saldo anterior: {}, Monto: {}, Nuevo saldo: {}",
                cuenta.getNumeroCuenta(), saldoAnterior, dto.getMonto(), nuevoSaldo);
    }

    private void procesarRetiro(TransaccionesSolicitudDTO dto) {
        log.debug("Ejecutando retiro para cuenta: {}", dto.getNumeroCuentaOrigen());

        // MS2 como EJECUTOR: Solo verifica que la cuenta exista y esté activa
        // MS1 ya validó que hay saldo suficiente, nosotros solo ejecutamos
        CuentaCliente cuenta = obtenerCuentaPorNumero(dto.getNumeroCuentaOrigen());
        validarCuentaActiva(cuenta);

        // Validación mínima de seguridad: verificar que aún hay saldo
        // (por si algo cambió entre MS1 y MS2)
        if (cuenta.getSaldo().compareTo(dto.getMonto()) < 0) {
            log.warn("ALERTA: Saldo insuficiente al ejecutar retiro. Cuenta: {}, Saldo: {}, Monto: {}",
                    cuenta.getNumeroCuenta(), cuenta.getSaldo(), dto.getMonto());
            throw new SaldoInsuficienteException(
                    String.format("Saldo insuficiente al ejecutar retiro. Saldo: %s, Monto: %s",
                            cuenta.getSaldo(), dto.getMonto()));
        }

        // Ejecutar la operación: restar el monto del saldo
        BigDecimal saldoAnterior = cuenta.getSaldo();
        BigDecimal nuevoSaldo = saldoAnterior.subtract(dto.getMonto());
        cuenta.setSaldo(nuevoSaldo);

        cuentaClienteRepository.save(cuenta);

        log.info("Retiro ejecutado - Cuenta: {}, Saldo anterior: {}, Monto: {}, Nuevo saldo: {}",
                cuenta.getNumeroCuenta(), saldoAnterior, dto.getMonto(), nuevoSaldo);
    }

    private CuentaCliente obtenerCuentaPorNumero(String numeroCuenta) {
        return cuentaClienteRepository.findByNumeroCuentaWithLock(numeroCuenta)
                .orElseThrow(() -> new CuentaNoEncontradaException(
                        "Cuenta no encontrada con número: " + numeroCuenta));
    }

    private void validarCuentaActiva(CuentaCliente cuenta) {
        if (!cuenta.isActiva()) {
            throw new TransaccionException(
                    String.format("La cuenta %d no está activa. Estado actual: %s",
                            cuenta.getId(), cuenta.getEstado()));
        }
    }
}
