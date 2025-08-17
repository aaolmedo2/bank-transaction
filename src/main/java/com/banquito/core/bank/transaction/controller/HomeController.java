package com.banquito.core.bank.transaction.controller;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@Tag(name = "Sistema", description = "Endpoints de monitoreo y estado del sistema")
public class HomeController {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Operation(summary = "Mensaje de bienvenida del servicio", description = "Endpoint básico que confirma que el servicio está ejecutándose correctamente", operationId = "mensajeBienvenida")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Servicio ejecutándose correctamente", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"), examples = @ExampleObject(value = "Welcome to CORE BANK TRANSACTION project, SERVER ✅")))
    })
    @GetMapping
    public ResponseEntity<String> home() {
        return new ResponseEntity<>("Welcome to CORE BANK TRANSACTION project, SERVER ✅", HttpStatus.OK);
    }

    @Operation(summary = "Verificar conexión a base de datos", description = "Endpoint para verificar la conectividad con la base de datos consultando todas las transacciones", operationId = "verificarBaseDatos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Conexión a base de datos exitosa", content = @Content(mediaType = "application/json", schema = @Schema(type = "array", implementation = Map.class))),
            @ApiResponse(responseCode = "500", description = "Error de conexión a base de datos", content = @Content(mediaType = "application/json"))
    })
    @Hidden // Oculta este endpoint de la documentación pública por seguridad
    @GetMapping("/check")
    public List<Map<String, Object>> checkDB() {
        return jdbcTemplate.queryForList("SELECT * FROM account.transacciones");
    }
}
