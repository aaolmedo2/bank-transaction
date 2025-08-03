package com.banquito.core.bank.transaction.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "cuentas_clientes", schema = "account")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CuentaCliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cuenta_cliente", nullable = false)
    private Integer id;

    @Column(name = "id_cliente", nullable = false)
    private String idCliente;

    @Column(name = "numero_cuenta", nullable = false, length = 10)
    private String numeroCuenta;

    @Column(name = "saldo_disponible", nullable = false, precision = 15, scale = 2)
    private BigDecimal saldoDisponible;

    @Column(name = "saldo_contable", nullable = false, precision = 15, scale = 2)
    private BigDecimal saldoContable;

    @Column(name = "fecha_apertura", nullable = false)
    private Instant fechaApertura;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 15)
    @Builder.Default
    private EstadoCuentaClienteEnum estado = EstadoCuentaClienteEnum.ACTIVO;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    public enum EstadoCuentaClienteEnum {
        ACTIVO,
        INACTIVO,
        BLOQUEADO
    }

    // Métodos de conveniencia para compatibilidad
    public BigDecimal getSaldo() {
        return saldoDisponible;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldoDisponible = saldo;
        // También actualizar saldo contable para mantener consistencia
        this.saldoContable = saldo;
    }

    public boolean isActiva() {
        return estado == EstadoCuentaClienteEnum.ACTIVO;
    }
}
