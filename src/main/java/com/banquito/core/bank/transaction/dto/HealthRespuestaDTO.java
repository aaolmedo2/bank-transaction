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
@Schema(description = "Respuesta del estado de salud del servicio")
public class HealthRespuestaDTO {

    @Schema(description = "Estado del servicio", example = "UP")
    private String estado;

    @Schema(description = "Nombre descriptivo del servicio", example = "MS2 - Procesador de Transacciones")
    private String servicio;
}
