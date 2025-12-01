# 📝 Ejemplos de Requests - Sistema de Transferencias

## Colección Postman/Insomnia

---

## 1️⃣ Obtener Productos Disponibles en Almacén

### Request
```http
GET http://localhost:8080/inventario/almacenes/1/productos
```

### Response Esperado
```json
[
  {
    "productoId": 3,
    "productoNombre": "ZYMOPAN AML ULTRA",
    "productoCodigo": "PRD-ZYM-001",
    "cantidadDisponible": 600.00
  },
  {
    "productoId": 5,
    "productoNombre": "LEVADURA ACTIVA",
    "productoCodigo": "PRD-LEV-002",
    "cantidadDisponible": 350.00
  }
]
```

---

## 2️⃣ Obtener Lotes Disponibles de un Producto

### Request
```http
GET http://localhost:8080/inventario/almacenes/1/productos/3/lotes
```

### Response Esperado
```json
[
  {
    "loteId": 6,
    "codigoLote": "LT-20251",
    "cantidadDisponible": 100.00,
    "fechaProduccion": "2025-01-15",
    "fechaVencimiento": "2026-01-15",
    "costoUnitario": 25.50
  },
  {
    "loteId": 7,
    "codigoLote": "LT-20252",
    "cantidadDisponible": 100.00,
    "fechaProduccion": "2025-02-10",
    "fechaVencimiento": "2026-02-10",
    "costoUnitario": 26.00
  },
  {
    "loteId": 8,
    "codigoLote": "LT-20253",
    "cantidadDisponible": 100.00,
    "fechaProduccion": "2025-03-05",
    "fechaVencimiento": "2026-03-05",
    "costoUnitario": 26.50
  }
]
```

---

## 3️⃣ Transferencia Manual - Selección de Lotes Específicos

### Request
```http
POST http://localhost:8080/inventario/transferencias
Content-Type: application/json

{
  "almacenOrigenId": 1,
  "almacenDestinoId": 2,
  "productoId": 3,
  "usuarioId": 1,
  "motivo": "Reposición de stock en sucursal norte por alta demanda",
  "lotes": [
    {
      "loteId": 6,
      "cantidad": 50.00
    },
    {
      "loteId": 7,
      "cantidad": 30.00
    }
  ]
}
```

### Response Esperado
```json
{
  "idMovimiento": 45,
  "tipoMovimiento": "TRANSFERENCIA",
  "tipoOperacion": "TRANSFERENCIA_ENTRE_ALMACENES",
  "almacenId": 2,
  "productoId": 3,
  "cantidad": 80.00,
  "fechaMovimiento": "2025-12-01",
  "observacion": "Reposición de stock en sucursal norte por alta demanda",
  "stockAnterior": 150.00,
  "stockActual": 230.00,
  "usuario": "1"
}
```

---

## 4️⃣ Transferencia FIFO Automática

### Request
```http
POST http://localhost:8080/inventario/transferencias
Content-Type: application/json

{
  "almacenOrigenId": 1,
  "almacenDestinoId": 3,
  "productoId": 3,
  "cantidad": 120.00,
  "usuarioId": 1,
  "motivo": "Distribución automática FIFO"
}
```

### Response Esperado
```json
{
  "idMovimiento": 46,
  "tipoMovimiento": "TRANSFERENCIA",
  "tipoOperacion": "TRANSFERENCIA_ENTRE_ALMACENES",
  "almacenId": 3,
  "productoId": 3,
  "cantidad": 120.00,
  "fechaMovimiento": "2025-12-01",
  "observacion": "Distribución automática FIFO",
  "stockAnterior": 0.00,
  "stockActual": 120.00,
  "usuario": "1"
}
```

**Nota:** El sistema tomará automáticamente primero del lote más antiguo (LT-20251), luego del siguiente (LT-20252) hasta completar las 120 unidades.

---

## 5️⃣ Transferencia de Múltiples Lotes (Caso Real)

### Scenario: Transferir 250 unidades de 3 lotes diferentes

### Request
```http
POST http://localhost:8080/inventario/transferencias
Content-Type: application/json

{
  "almacenOrigenId": 1,
  "almacenDestinoId": 2,
  "productoId": 3,
  "usuarioId": 1,
  "motivo": "Transferencia mensual programada - Diciembre 2025",
  "lotes": [
    {
      "loteId": 6,
      "cantidad": 100.00
    },
    {
      "loteId": 7,
      "cantidad": 100.00
    },
    {
      "loteId": 8,
      "cantidad": 50.00
    }
  ]
}
```

### Response Esperado
```json
{
  "idMovimiento": 47,
  "tipoMovimiento": "TRANSFERENCIA",
  "tipoOperacion": "TRANSFERENCIA_ENTRE_ALMACENES",
  "almacenId": 2,
  "productoId": 3,
  "cantidad": 250.00,
  "fechaMovimiento": "2025-12-01",
  "observacion": "Transferencia mensual programada - Diciembre 2025",
  "stockAnterior": 230.00,
  "stockActual": 480.00,
  "usuario": "1"
}
```

---

## ❌ Casos de Error

### Error 1: Almacenes Iguales

```http
POST http://localhost:8080/inventario/transferencias
Content-Type: application/json

{
  "almacenOrigenId": 1,
  "almacenDestinoId": 1,
  "productoId": 3,
  "cantidad": 50.00,
  "usuarioId": 1
}
```

**Response:**
```json
{
  "timestamp": "2025-12-01T17:25:00",
  "status": 400,
  "error": "Bad Request",
  "message": "El almacén origen y destino no pueden ser iguales",
  "path": "/inventario/transferencias"
}
```

---

### Error 2: Stock Insuficiente

```http
POST http://localhost:8080/inventario/transferencias
Content-Type: application/json

{
  "almacenOrigenId": 1,
  "almacenDestinoId": 2,
  "productoId": 3,
  "usuarioId": 1,
  "lotes": [
    {
      "loteId": 6,
      "cantidad": 500.00
    }
  ]
}
```

**Response:**
```json
{
  "timestamp": "2025-12-01T17:26:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Cantidad insuficiente en lote LT-20251. Disponible: 100.00, Solicitado: 500.00",
  "path": "/inventario/transferencias"
}
```

---

### Error 3: Lote No Encontrado en Almacén Origen

```http
POST http://localhost:8080/inventario/transferencias
Content-Type: application/json

{
  "almacenOrigenId": 1,
  "almacenDestinoId": 2,
  "productoId": 3,
  "usuarioId": 1,
  "lotes": [
    {
      "loteId": 999,
      "cantidad": 50.00
    }
  ]
}
```

**Response:**
```json
{
  "timestamp": "2025-12-01T17:27:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Lote ID 999 no encontrado en almacén origen",
  "path": "/inventario/transferencias"
}
```

---

### Error 4: Validación de Campos Requeridos

```http
POST http://localhost:8080/inventario/transferencias
Content-Type: application/json

{
  "almacenOrigenId": 1,
  "productoId": 3,
  "usuarioId": 1
}
```

**Response:**
```json
{
  "timestamp": "2025-12-01T17:28:00",
  "status": 400,
  "error": "Bad Request",
  "message": "El almacén destino es requerido",
  "path": "/inventario/transferencias"
}
```

---

## 🔍 Endpoints de Verificación

### Verificar Stock Después de Transferencia

#### Stock Consolidado
```http
GET http://localhost:8080/inventario/stock?almacenId=2&productoId=3
```

#### Stock por Lote
```http
GET http://localhost:8080/inventario/lotes?almacenId=2&productoId=3
```

#### Movimientos en Kardex
```http
GET http://localhost:8080/kardex?almacenId=2&productoId=3
```

---

## 🧪 Suite de Pruebas Completa

### Test Case 1: Flujo Completo de Transferencia Manual

1. **Listar productos disponibles en almacén origen**
   ```
   GET /inventario/almacenes/1/productos
   ```

2. **Obtener lotes del producto seleccionado**
   ```
   GET /inventario/almacenes/1/productos/3/lotes
   ```

3. **Realizar transferencia**
   ```
   POST /inventario/transferencias
   { ... lotes específicos ... }
   ```

4. **Verificar stock en origen (debe haber disminuido)**
   ```
   GET /inventario/lotes?almacenId=1&productoId=3
   ```

5. **Verificar stock en destino (debe haber aumentado)**
   ```
   GET /inventario/lotes?almacenId=2&productoId=3
   ```

6. **Verificar movimientos en Kardex**
   ```
   GET /kardex?tipoOperacion=TRANSFERENCIA_ENTRE_ALMACENES
   ```

---

### Test Case 2: Transferencia FIFO con Múltiples Lotes

**Precondición:** Almacén 1 tiene 3 lotes con 100 unidades cada uno

1. **Transferir 250 unidades (FIFO automático)**
   ```
   POST /inventario/transferencias
   {
     "almacenOrigenId": 1,
     "almacenDestinoId": 2,
     "productoId": 3,
     "cantidad": 250.00,
     "usuarioId": 1
   }
   ```

2. **Verificar que se consumieron:**
   - Lote 1: 100 unidades completas
   - Lote 2: 100 unidades completas
   - Lote 3: 50 unidades parciales

3. **Verificar que en destino se crearon/actualizaron los 3 lotes**

---

## 📊 Dashboard de Pruebas

| Test | Endpoint | Estado Esperado | Validación |
|------|----------|-----------------|------------|
| Listar productos | GET /inventario/almacenes/{id}/productos | 200 OK | Lista no vacía |
| Listar lotes | GET /inventario/almacenes/{id}/productos/{id}/lotes | 200 OK | Ordenados por fecha |
| Transferencia manual | POST /inventario/transferencias | 200 OK | Stock actualizado |
| Transferencia FIFO | POST /inventario/transferencias | 200 OK | Respeta orden FIFO |
| Almacenes iguales | POST /inventario/transferencias | 400 Bad Request | Error descriptivo |
| Stock insuficiente | POST /inventario/transferencias | 400 Bad Request | Error con detalles |
| Lote inexistente | POST /inventario/transferencias | 400 Bad Request | Error claro |

---

## 🔑 Variables de Entorno Sugeridas (Postman)

```json
{
  "base_url": "http://localhost:8080",
  "almacen_general_id": 1,
  "almacen_sucursal_norte_id": 2,
  "almacen_sucursal_sur_id": 3,
  "producto_zymopan_id": 3,
  "usuario_admin_id": 1
}
```

**Uso en requests:**
```
{{base_url}}/inventario/almacenes/{{almacen_general_id}}/productos
```

---

**Última Actualización:** 2025-12-01  
**Versión:** 1.0.0

