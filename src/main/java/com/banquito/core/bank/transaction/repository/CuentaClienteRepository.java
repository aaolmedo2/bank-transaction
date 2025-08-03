package com.banquito.core.bank.transaction.repository;

import com.banquito.core.bank.transaction.model.CuentaCliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import java.util.Optional;

@Repository
public interface CuentaClienteRepository extends JpaRepository<CuentaCliente, Integer> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM CuentaCliente c WHERE c.id = :id")
    Optional<CuentaCliente> findByIdWithLock(@Param("id") Integer id);

    // Nuevo método para buscar por número de cuenta
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM CuentaCliente c WHERE c.numeroCuenta = :numeroCuenta")
    Optional<CuentaCliente> findByNumeroCuentaWithLock(@Param("numeroCuenta") String numeroCuenta);

    // Método sin bloqueo para consultas simples
    Optional<CuentaCliente> findByNumeroCuenta(String numeroCuenta);

}
