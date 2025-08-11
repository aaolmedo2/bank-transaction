package com.banquito.core.bank.transaction.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "transacciones", schema = "account")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Entidad que representa una transacción bancaria")
public class Transacciones {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_transaccion", nullable = false)
    @Schema(description = "ID único de la transacción", example = "12345")
    private Long id;

    @Column(name = "numero_cuenta_origen", nullable = false, length = 10)
    @Schema(description = "Número de cuenta origen", example = "1234567890")
    private String numeroCuentaOrigen;

    @Column(name = "numero_cuenta_destino", length = 10)
    @Schema(description = "Número de cuenta destino (solo para transferencias)", example = "0987654321")
    private String numeroCuentaDestino;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_transaccion", nullable = false, length = 20)
    @Schema(description = "Tipo de transacción", example = "DEPOSITO")
    private TipoTransaccionEnum tipoTransaccion;

    @Column(name = "monto", nullable = false, precision = 15, scale = 2)
    @Schema(description = "Monto de la transacción", example = "150.50")
    private BigDecimal monto;

    @Column(name = "descripcion", length = 150)
    @Schema(description = "Descripción de la transacción", example = "Depósito por transferencia externa")
    private String descripcion;

    @Column(name = "fecha_transaccion", nullable = false)
    @Schema(description = "Fecha y hora de la transacción", example = "2025-08-11T07:10:45.123Z")
    private Instant fechaTransaccion;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 15)
    @Schema(description = "Estado actual de la transacción", example = "COMPLETADA")
    private EstadoTransaccionesEnum estado;

    @Version
    @Column(name = "version", nullable = false, precision = 9)
    @Schema(description = "Versión para control de concurrencia optimista", example = "1")
    private Long version;

    @PrePersist
    protected void onCreate() {
        fechaTransaccion = Instant.now();
        if (estado == null) {
            estado = EstadoTransaccionesEnum.PENDIENTE;
        }
    }

    @Schema(description = "Tipos de transacción disponibles")
    public enum TipoTransaccionEnum {
        @Schema(description = "Depósito de dinero a una cuenta")
        DEPOSITO,
        @Schema(description = "Retiro de dinero de una cuenta")
        RETIRO
    }

    @Schema(description = "Estados posibles de una transacción")
    public enum EstadoTransaccionesEnum {
        @Schema(description = "Transacción creada pero no procesada")
        PENDIENTE,
        @Schema(description = "Transacción procesada exitosamente")
        COMPLETADA,
        @Schema(description = "Error durante el procesamiento de la transacción")
        ERROR
    }
}
