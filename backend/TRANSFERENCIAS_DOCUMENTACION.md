# 📦 Sistema de Transferencias entre Almacenes

## 🎯 Descripción General

Sistema completo para transferir productos entre almacenes con dos modalidades:
1. **Transferencia Manual**: Selección específica de lotes y cantidades
2. **Transferencia FIFO**: Automática usando First-In-First-Out

---

## 🔗 Endpoints Disponibles

### 1. Obtener Productos Disponibles en un Almacén

**GET** `/inventario/almacenes/{almacenId}/productos`

Retorna todos los productos con stock disponible (cantidad > 0) en el almacén especificado.

**Parámetros:**
- `almacenId` (path): ID del almacén

**Respuesta:**
```json
[
  {
    "productoId": 3,
    "productoNombre": "ZYMOPAN AML ULTRA",
    "productoCodigo": "PRD-001",
    "cantidadDisponible": 500.00
  }
]
```

---

### 2. Obtener Lotes Disponibles de un Producto

**GET** `/inventario/almacenes/{almacenId}/productos/{productoId}/lotes`

Retorna todos los lotes disponibles de un producto específico en un almacén.

**Parámetros:**
- `almacenId` (path): ID del almacén
- `productoId` (path): ID del producto

**Respuesta:**
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
    "cantidadDisponible": 150.00,
    "fechaProduccion": "2025-02-10",
    "fechaVencimiento": "2026-02-10",
    "costoUnitario": 26.00
  }
]
```

**Nota:** Los lotes se ordenan por fecha de producción (FIFO).

---

### 3. Realizar Transferencia entre Almacenes

**POST** `/inventario/transferencias`

Transfiere productos entre dos almacenes.

#### Modo 1: Transferencia Manual (con lotes específicos)

**Request Body:**
```json
{
  "almacenOrigenId": 1,
  "almacenDestinoId": 2,
  "productoId": 3,
  "usuarioId": 1,
  "motivo": "Reposición de stock en sucursal",
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

#### Modo 2: Transferencia FIFO Automática

**Request Body:**
```json
{
  "almacenOrigenId": 1,
  "almacenDestinoId": 2,
  "productoId": 3,
  "cantidad": 80.00,
  "usuarioId": 1,
  "motivo": "Transferencia automática"
}
```

**Respuesta:**
```json
{
  "idMovimiento": 123,
  "tipoMovimiento": "TRANSFERENCIA",
  "tipoOperacion": "TRANSFERENCIA_ENTRE_ALMACENES",
  "almacenId": 2,
  "productoId": 3,
  "cantidad": 80.00,
  "fechaMovimiento": "2025-12-01",
  "observacion": "Transferencia desde almacén GENERAL",
  "stockAnterior": 200.00,
  "stockActual": 280.00,
  "usuario": "1"
}
```

---

## ✅ Validaciones Implementadas

### Validaciones Generales
- ✅ Almacén origen y destino no pueden ser iguales
- ✅ Almacén origen debe existir
- ✅ Almacén destino debe existir
- ✅ Producto debe existir
- ✅ Usuario debe ser proporcionado
- ✅ Stock suficiente en almacén origen

### Validaciones para Transferencia Manual (por lotes)
- ✅ Cada lote debe existir en el almacén origen
- ✅ Cada lote debe pertenecer al producto seleccionado
- ✅ Cada lote debe tener estado "DISPONIBLE"
- ✅ Cantidad solicitada no debe exceder la disponible por lote
- ✅ Al menos un lote debe ser especificado

### Validaciones para Transferencia FIFO
- ✅ Cantidad total debe ser mayor a cero
- ✅ Debe haber suficiente stock total en almacén origen
- ✅ Respeta el orden FIFO (First-In-First-Out) por fecha de producción

---

## 🔄 Flujo de Uso Recomendado para Frontend

### Paso 1: Seleccionar Almacén Origen
```
Usuario selecciona: Almacén ID = 1 (GENERAL)
```

### Paso 2: Obtener Productos Disponibles
```
GET /inventario/almacenes/1/productos
```

Muestra al usuario los productos disponibles en ese almacén.

### Paso 3: Seleccionar Producto
```
Usuario selecciona: Producto ID = 3 (ZYMOPAN AML ULTRA)
```

### Paso 4: Obtener Lotes Disponibles
```
GET /inventario/almacenes/1/productos/3/lotes
```

Muestra al usuario los lotes disponibles del producto en ese almacén.

### Paso 5: Seleccionar Almacén Destino
```
Usuario selecciona: Almacén ID = 2 (SUCURSAL NORTE)
```

### Paso 6: Seleccionar Lotes y Cantidades
```
Usuario selecciona:
- Lote LT-20251: 50 unidades
- Lote LT-20252: 30 unidades
```

### Paso 7: Realizar Transferencia
```
POST /inventario/transferencias
{
  "almacenOrigenId": 1,
  "almacenDestinoId": 2,
  "productoId": 3,
  "usuarioId": 1,
  "motivo": "Reposición de stock",
  "lotes": [
    { "loteId": 6, "cantidad": 50.00 },
    { "loteId": 7, "cantidad": 30.00 }
  ]
}
```

---

## 📊 Qué Hace el Sistema Internamente

### Para Cada Transferencia:

1. **Validaciones Completas**
   - Verifica existencia de almacenes, producto y lotes
   - Valida disponibilidad y pertenencia
   - Verifica stock suficiente

2. **Actualización de Stock por Lote** (`stock_lote`)
   - Descuenta cantidad del almacén origen
   - Incrementa o crea registro en almacén destino

3. **Actualización de Stock Consolidado** (`stock_almacen`)
   - Descuenta del total del almacén origen
   - Incrementa en el total del almacén destino

4. **Registro en Kardex** (2 movimientos)
   - **Salida** del almacén origen (cantidad negativa)
   - **Entrada** al almacén destino (cantidad positiva)
   - Ambos con referencia cruzada y observaciones

5. **Respuesta al Usuario**
   - Confirmación de la transferencia
   - Stock actualizado en almacén destino
   - ID del movimiento para trazabilidad

---

## 🧪 Ejemplos de Uso

### Ejemplo 1: Transferencia Manual de 2 Lotes

```bash
POST /inventario/transferencias
Content-Type: application/json

{
  "almacenOrigenId": 1,
  "almacenDestinoId": 3,
  "productoId": 5,
  "usuarioId": 10,
  "motivo": "Redistribución de inventario",
  "lotes": [
    { "loteId": 15, "cantidad": 100.00 },
    { "loteId": 18, "cantidad": 75.50 }
  ]
}
```

### Ejemplo 2: Transferencia FIFO de 200 Unidades

```bash
POST /inventario/transferencias
Content-Type: application/json

{
  "almacenOrigenId": 2,
  "almacenDestinoId": 4,
  "productoId": 8,
  "cantidad": 200.00,
  "usuarioId": 10,
  "motivo": "Abastecimiento automático"
}
```

---

## ⚠️ Mensajes de Error Comunes

| Error | Causa | Solución |
|-------|-------|----------|
| "El almacén origen y destino no pueden ser iguales" | IDs iguales | Seleccionar almacenes diferentes |
| "Lote ID X no encontrado en almacén origen" | Lote no existe en origen | Verificar que el lote esté en el almacén correcto |
| "El lote XXX no pertenece al producto seleccionado" | Lote de otro producto | Verificar selección de lotes |
| "Cantidad insuficiente en lote XXX" | Stock insuficiente | Reducir cantidad o seleccionar otro lote |
| "Stock insuficiente para transferir. Faltante: X" | Stock total insuficiente | Verificar disponibilidad total |
| "El lote XXX no está disponible" | Lote bloqueado/vencido | Seleccionar solo lotes disponibles |

---

## 🔐 Seguridad y Trazabilidad

### Registro en Kardex
Cada transferencia genera **2 movimientos en Kardex**:
- Uno para el almacén origen (SALIDA)
- Uno para el almacén destino (ENTRADA)

### Campos Registrados:
- Fecha del movimiento
- Almacén
- Producto
- Lote (si aplica)
- Cantidad
- Tipo de movimiento (ENTRADA/SALIDA)
- Tipo de operación (TRANSFERENCIA_ENTRE_ALMACENES)
- Stock anterior y actual
- Observación con motivo
- Referencia cruzada (TRANSFER-{origenId}-{destinoId})

### Auditoría:
Todas las entidades extienden de `BaseEntity` que incluye:
- `createdAt`: Fecha de creación
- `updatedAt`: Fecha de última actualización
- `isDeleted`: Bandera de eliminación lógica
- `deletedAt`: Fecha de eliminación (si aplica)

---

## 📈 Mejoras Futuras Sugeridas

1. **Reserva de Stock**: Permitir reservar stock antes de confirmar transferencia
2. **Transferencias en Tránsito**: Estado intermedio entre origen y destino
3. **Aprobación de Transferencias**: Workflow de aprobación multinivel
4. **Notificaciones**: Alertas por email/SMS al completar transferencias
5. **Reportes**: Dashboard de transferencias por período
6. **Transferencias Masivas**: Múltiples productos en una sola operación
7. **Reversión de Transferencias**: Deshacer transferencias dentro de un período

---

## 📞 Soporte

Para dudas o problemas con las transferencias:
1. Verificar los logs de Kardex: `GET /kardex?tipoOperacion=TRANSFERENCIA_ENTRE_ALMACENES`
2. Revisar stock actual: `GET /inventario/stock?almacenId={id}&productoId={id}`
3. Consultar lotes: `GET /inventario/lotes?almacenId={id}&productoId={id}`

---

**Fecha de Actualización:** 2025-12-01
**Versión:** 1.0.0

