package com.banquito.core.bank.transaction.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO para solicitudes de transacciones bancarias")
public class TransaccionesSolicitudDTO {

    @Schema(description = "Número de cuenta origen (10 dígitos)", example = "1234567890", pattern = "^[0-9]{10}$", required = true)
    private String numeroCuentaOrigen;

    @Schema(description = "Número de cuenta destino para transferencias (10 dígitos)", example = "0987654321", pattern = "^[0-9]{10}$")
    private String numeroCuentaDestino;

    @Schema(description = "Tipo de transacción a realizar", allowableValues = { "DEPOSITO", "RETIRO",
            "TRANSFERENCIA" }, example = "DEPOSITO", required = true)
    private String tipoTransaccion;

    @Schema(description = "Monto de la transacción (debe ser positivo)", example = "150.50", minimum = "0.01", required = true)
    private BigDecimal monto;

    @Schema(description = "Descripción o concepto de la transacción", example = "Depósito por transferencia externa", maxLength = 150)
    private String descripcion;

    @Schema(description = "Timestamp de cuando se creó la solicitud", example = "2025-08-11T12:30:45")
    private LocalDateTime timestamp;
}
