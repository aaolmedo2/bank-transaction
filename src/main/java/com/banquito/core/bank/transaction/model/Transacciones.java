package com.banquito.core.bank.transaction.model;

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
public class Transacciones {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_transaccion", nullable = false)
    private Long id; // Cuenta origen - ahora por número de cuenta (String)
    @Column(name = "numero_cuenta_origen", nullable = false, length = 10)
    private String numeroCuentaOrigen;

    // Cuenta destino para transferencias - ahora por número de cuenta (String)
    @Column(name = "numero_cuenta_destino", length = 10)
    private String numeroCuentaDestino;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_transaccion", nullable = false, length = 20)
    private TipoTransaccionEnum tipoTransaccion;

    @Column(name = "monto", nullable = false, precision = 15, scale = 2)
    private BigDecimal monto;

    @Column(name = "descripcion", length = 150)
    private String descripcion;

    @Column(name = "fecha_transaccion", nullable = false)
    private Instant fechaTransaccion;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 15)
    private EstadoTransaccionesEnum estado;

    @Version
    @Column(name = "version", nullable = false, precision = 9)
    private Long version;

    @PrePersist
    protected void onCreate() {
        fechaTransaccion = Instant.now();
        if (estado == null) {
            estado = EstadoTransaccionesEnum.PENDIENTE;
        }
    }

    public enum TipoTransaccionEnum {
        DEPOSITO,
        RETIRO,
        TRANSFERENCIA
    }

    public enum EstadoTransaccionesEnum {
        PENDIENTE,
        COMPLETADA,
        ERROR
    }
}
