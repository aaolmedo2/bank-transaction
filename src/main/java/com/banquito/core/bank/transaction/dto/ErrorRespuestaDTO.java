package com.banquito.core.bank.transaction.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Respuesta de error estándar de la API")
public class ErrorRespuestaDTO {

    @Schema(description = "Mensaje descriptivo del error", example = "Transacción no encontrada")
    private String mensaje;

    @Schema(description = "ID de la transacción relacionada al error", example = "12345")
    private Long transaccionId;
}
