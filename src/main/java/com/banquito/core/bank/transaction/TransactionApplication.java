package com.banquito.core.bank.transaction;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableJms
@EnableTransactionManagement
@Slf4j
public class TransactionApplication {

	public static void main(String[] args) {
		log.info("=== Iniciando MS2 - Procesador de Transacciones Bancarias ===");
		SpringApplication.run(TransactionApplication.class, args);
		log.info("=== MS2 iniciado correctamente y escuchando cola ActiveMQ ===");
	}

}
