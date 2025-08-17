package com.banquito.core.bank.transaction.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Respuesta con información detallada de una transacción")
public class TransaccionRespuestaDTO {

    @Schema(description = "ID único de la transacción", example = "12345")
    private Long transaccionId;

    @Schema(description = "Estado actual de la transacción", allowableValues = { "PENDIENTE", "COMPLETADA",
            "ERROR" }, example = "COMPLETADA")
    private String estado;

    @Schema(description = "Tipo de transacción realizada", allowableValues = { "DEPOSITO",
            "RETIRO" }, example = "DEPOSITO")
    private String tipoTransaccion;

    @Schema(description = "Monto de la transacción", example = "150.50")
    private BigDecimal monto;

    @Schema(description = "Fecha y hora en que se realizó la transacción", example = "2025-08-11T07:10:45.123Z")
    private Instant fechaTransaccion;

    @Schema(description = "Descripción o concepto de la transacción", example = "Depósito por transferencia externa")
    private String descripcion;

    @Schema(description = "Mensaje de error en caso de que el estado sea ERROR", example = "Saldo insuficiente")
    private String mensajeError;
}
