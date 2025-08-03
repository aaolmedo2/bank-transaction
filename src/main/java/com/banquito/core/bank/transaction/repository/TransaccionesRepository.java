package com.banquito.core.bank.transaction.repository;

import com.banquito.core.bank.transaction.model.Transacciones;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransaccionesRepository extends JpaRepository<Transacciones, Long> {

}
