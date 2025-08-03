# MS2 - Ejecutor de Transacciones Bancarias

Este microservicio (MS2) actúa como **EJECUTOR** de transacciones bancarias. Recibe órdenes de transacciones desde una cola ActiveMQ (enviadas por el MS1) y las ejecuta directamente en la base de datos.

## Rol en la Arquitectura

### MS1 (Validador):
- ✅ Valida cuentas existentes
- ✅ Valida saldos suficientes  
- ✅ Valida reglas de negocio complejas
- ✅ Envía a cola ActiveMQ
- ❌ NO procesa la transacción

### MS2 (Ejecutor):
- ✅ Recibe órdenes desde la cola
- ✅ Ejecuta la operación SIN re-validar
- ✅ Actualiza saldos en BD
- ✅ Registra el resultado
- ❌ NO valida reglas de negocio (confía en MS1)

## Principio de Funcionamiento

**MS1**: "Validé que puedes retirar $100, aquí está la orden" → Cola  
**MS2**: "Recibí orden, ejecutando retiro de $100" → BD

El MS2 **confía** en que MS1 ya validó todo correctamente.

## Funcionalidades

- **Ejecuta transacciones**: Depositos, Retiros y Transferencias recibidos desde cola
- **Actualiza saldos**: Modifica los saldos directamente en la base de datos
- **Manejo de concurrencia**: Utiliza bloqueos pesimistas para evitar condiciones de carrera
- **Registro de transacciones**: Mantiene un historial de todas las transacciones ejecutadas
- **Validaciones mínimas**: Solo verifica que las cuentas existan y estén activas
- **Confianza en MS1**: No re-valida reglas de negocio, confía en las validaciones del MS1

## Compatibilidad con MS1

Este MS2 está **completamente compatible** con el MS1:

- ✅ **Misma base de datos**: `prestamosautomotrices` en AWS RDS
- ✅ **Mismo esquema**: `account` 
- ✅ **Misma tabla**: `cuentas_clientes`
- ✅ **Misma cola ActiveMQ**: `transacciones.procesar`
- ✅ **Mismo broker**: `tcp://localhost:61616`
- ✅ **Mismo formato de DTO**: Campos Integer, estructura idéntica
- ✅ **Configuración idéntica**: ActiveMQ, Jackson, TimeZone

## Configuración requerida

### Base de datos PostgreSQL (AWS RDS)
```
Host: arqui-postgres-db.cjq62wwgssut.us-east-2.rds.amazonaws.com:5432
Database: prestamosautomotrices  
Schema: account
Usuario: postgres
Password: postgres
```

### ActiveMQ
- Debe estar ejecutándose en `tcp://localhost:61616`
- Usuario: `admin`
- Password: `admin`
- Cola: `transacciones.procesar`

## Endpoints disponibles

### Consultar estado de transacción
```
GET /api/v1/transacciones/estado/{transaccionId}
```

### Health check
```
GET /api/v1/transacciones/health
```

## Tipos de transacciones procesadas

### 1. DEPOSITO
- Suma el monto al saldo de la cuenta origen
- Solo requiere `idCuentaClienteOrigen`

### 2. RETIRO
- Resta el monto del saldo de la cuenta origen
- Valida saldo suficiente
- Solo requiere `idCuentaClienteOrigen`

### 3. TRANSFERENCIA
- Resta monto de cuenta origen
- Suma monto a cuenta destino
- Requiere `idCuentaClienteOrigen` e `idCuentaClienteDestino`
- Valida saldo suficiente en cuenta origen

## Estructura del mensaje desde MS1

```json
{
  "transaccionId": "uuid-123",
  "idCuentaClienteOrigen": 2,
  "idCuentaClienteDestino": 3,
  "tipoTransaccion": "DEPOSITO",
  "monto": 100.00,
  "descripcion": "Descripción",
  "timestamp": "2025-01-01T10:00:00"
}
```

## Estados de transacción

- **EN_COLA**: Enviada por MS1, aún no procesada
- **PROCESANDO**: MS2 está procesando la transacción
- **COMPLETADA**: Transacción procesada exitosamente
- **ERROR**: Error durante el procesamiento

## Inicialización de datos

Para pruebas, puedes ejecutar el script `data.sql` para crear cuentas de prueba:

```sql
-- Cuentas con saldos iniciales
ID 1: $1,000.00
ID 2: $1,500.00
ID 3: $2,000.00
ID 4: $500.00
ID 5: $0.00
```

## Ejecución

1. Asegurar que PostgreSQL esté ejecutándose
2. Asegurar que ActiveMQ esté ejecutándose
3. Ejecutar la aplicación:
```bash
./mvnw spring-boot:run
```

El servicio estará disponible en `http://localhost:8086`

## Logs

El servicio genera logs detallados de:
- Transacciones recibidas desde la cola
- Procesamiento de cada tipo de transacción
- Errores y excepciones
- Cambios en saldos de cuentas

## Manejo de errores

- **Cuenta no encontrada**: Marca la transacción como ERROR
- **Saldo insuficiente**: Marca la transacción como ERROR
- **Datos inválidos**: Marca la transacción como ERROR
- **Errores de sistema**: Re-lanza la excepción para que ActiveMQ maneje retry/DLQ

## Integración con MS1

El MS1 debe:
1. Validar las cuentas y saldos (sin procesar)
2. Enviar el mensaje a la cola `transacciones.procesar`
3. Responder inmediatamente con HTTP 202
4. Este MS2 procesará la transacción de forma asíncrona
