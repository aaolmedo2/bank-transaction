package com.banquito.core.bank.transaction.dto;

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
public class TransaccionesSolicitudDTO {

    private String numeroCuentaOrigen; // Cambiado de idCuentaClienteOrigen a numeroCuentaOrigen
    private String numeroCuentaDestino; // Cambiado de idCuentaClienteDestino a numeroCuentaDestino (solo para
                                        // transferencias)
    private String tipoTransaccion; // DEPOSITO, RETIRO, TRANSFERENCIA
    private BigDecimal monto;
    private String descripcion;
    private LocalDateTime timestamp;

}
