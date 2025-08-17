package com.banquito.core.bank.transaction.controller;

import com.banquito.core.bank.transaction.dto.ErrorRespuestaDTO;
import com.banquito.core.bank.transaction.dto.HealthRespuestaDTO;
import com.banquito.core.bank.transaction.dto.TransaccionRespuestaDTO;
import com.banquito.core.bank.transaction.model.Transacciones;
import com.banquito.core.bank.transaction.repository.TransaccionesRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/transacciones/v1/transacciones")
@Slf4j
@Tag(name = "Transacciones", description = "API para gestión de transacciones bancarias")
public class TransaccionesController {

    private final TransaccionesRepository transaccionesRepository;

    public TransaccionesController(TransaccionesRepository transaccionesRepository) {
        this.transaccionesRepository = transaccionesRepository;
    }

    @Operation(summary = "Consultar estado de transacción", description = "Obtiene el estado actual y detalles de una transacción específica mediante su ID único", operationId = "consultarEstadoTransaccion")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transacción encontrada exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TransaccionRespuestaDTO.class), examples = @ExampleObject(name = "Transacción completada", value = """
                    {
                        "transaccionId": 12345,
                        "estado": "COMPLETADA",
                        "tipoTransaccion": "DEPOSITO",
                        "monto": 150.50,
                        "fechaTransaccion": "2025-08-11T07:10:45.123Z",
                        "descripcion": "Depósito por transferencia externa"
                    }
                    """))),
            @ApiResponse(responseCode = "404", description = "Transacción no encontrada", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRespuestaDTO.class), examples = @ExampleObject(name = "Transacción no encontrada", value = """
                    {
                        "mensaje": "Transacción no encontrada",
                        "transaccionId": 99999
                    }
                    """))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRespuestaDTO.class)))
    })
    @GetMapping("/estado/{transaccionId}")
    public ResponseEntity<?> consultarEstado(
            @Parameter(description = "ID único de la transacción a consultar", example = "12345", required = true) @PathVariable Long transaccionId) {

        log.info("Consultando estado de transacción: {}", transaccionId);

        Optional<Transacciones> transaccionOpt = transaccionesRepository.findById(transaccionId);

        if (transaccionOpt.isEmpty()) {
            ErrorRespuestaDTO errorRespuesta = ErrorRespuestaDTO.builder()
                    .mensaje("Transacción no encontrada")
                    .transaccionId(transaccionId)
                    .build();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorRespuesta);
        }

        Transacciones transaccion = transaccionOpt.get();

        TransaccionRespuestaDTO respuesta = TransaccionRespuestaDTO.builder()
                .transaccionId(transaccion.getId())
                .estado(transaccion.getEstado().name())
                .tipoTransaccion(transaccion.getTipoTransaccion().name())
                .monto(transaccion.getMonto())
                .fechaTransaccion(transaccion.getFechaTransaccion())
                .descripcion(transaccion.getDescripcion())
                .build();

        if (transaccion.getEstado() == Transacciones.EstadoTransaccionesEnum.ERROR) {
            respuesta.setMensajeError("Error en el procesamiento");
        }

        return ResponseEntity.ok(respuesta);
    }

    @Operation(summary = "Verificar estado de salud del servicio", description = "Endpoint para verificar que el microservicio de transacciones está funcionando correctamente", operationId = "verificarSaludServicio")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Servicio funcionando correctamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = HealthRespuestaDTO.class), examples = @ExampleObject(name = "Servicio saludable", value = """
                    {
                        "estado": "UP",
                        "servicio": "MS2 - Procesador de Transacciones"
                    }
                    """)))
    })
    @GetMapping("/health")
    public ResponseEntity<HealthRespuestaDTO> health() {
        HealthRespuestaDTO respuesta = HealthRespuestaDTO.builder()
                .estado("UP")
                .servicio("MS2 - Procesador de Transacciones")
                .build();
        return ResponseEntity.ok(respuesta);
    }
}
