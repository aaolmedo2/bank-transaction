package com.banquito.core.bank.transaction.controller;

import com.banquito.core.bank.transaction.model.Transacciones;
import com.banquito.core.bank.transaction.repository.TransaccionesRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/transacciones/v1")
@Slf4j
public class TransaccionesController {

    private final TransaccionesRepository transaccionesRepository;

    public TransaccionesController(TransaccionesRepository transaccionesRepository) {
        this.transaccionesRepository = transaccionesRepository;
    }

    @GetMapping("/estado/{transaccionId}")
    public ResponseEntity<Map<String, Object>> consultarEstado(@PathVariable Long transaccionId) {
        log.info("Consultando estado de transacción: {}", transaccionId);

        Optional<Transacciones> transaccionOpt = transaccionesRepository.findById(transaccionId);

        if (transaccionOpt.isEmpty()) {
            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("mensaje", "Transacción no encontrada");
            respuesta.put("transaccionId", transaccionId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuesta);
        }

        Transacciones transaccion = transaccionOpt.get();

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("transaccionId", transaccion.getId());
        respuesta.put("estado", transaccion.getEstado());
        respuesta.put("tipoTransaccion", transaccion.getTipoTransaccion());
        respuesta.put("monto", transaccion.getMonto());
        respuesta.put("fechaTransaccion", transaccion.getFechaTransaccion());
        respuesta.put("descripcion", transaccion.getDescripcion());

        if (transaccion.getEstado() == Transacciones.EstadoTransaccionesEnum.ERROR) {
            // En el futuro podríamos agregar el campo mensajeError a la entidad si es
            // necesario
            respuesta.put("mensajeError", "Error en el procesamiento");
        }

        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> respuesta = new HashMap<>();
        respuesta.put("estado", "UP");
        respuesta.put("servicio", "MS2 - Procesador de Transacciones");
        return ResponseEntity.ok(respuesta);
    }
}
