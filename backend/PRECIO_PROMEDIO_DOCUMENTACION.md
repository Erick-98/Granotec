# 📊 Documentación: Sistema de Precio Promedio Ponderado

## 🎯 Resumen de Cambios Implementados

Se ha implementado un sistema de **Precio Promedio Ponderado** que calcula dinámicamente el costo unitario de los productos basándose en los lotes disponibles en almacén. Este precio se puede consultar desde el frontend para mostrarlo en las órdenes de compra.

---

## 🔧 Cambios Realizados

### 1. **StockLoteRepository** - Nuevos métodos de cálculo
Se añadieron dos métodos para calcular el precio promedio ponderado:

```java
// Calcula el precio promedio ponderado por almacén específico
@Query("SELECT CASE WHEN SUM(s.cantidadDisponible) > 0 " +
       "THEN SUM(s.cantidadDisponible * s.lote.costoUnitario) / SUM(s.cantidadDisponible) " +
       "ELSE 0 END " +
       "FROM StockLote s " +
       "WHERE s.lote.producto.id = :productoId " +
       "AND s.almacen.id = :almacenId " +
       "AND s.cantidadDisponible > 0 " +
       "AND s.lote.estado = 'DISPONIBLE'")
BigDecimal calcularPrecioPromedioPonderado(@Param("productoId") Integer productoId, 
                                            @Param("almacenId") Long almacenId);

// Calcula el precio promedio ponderado en todos los almacenes
BigDecimal calcularPrecioPromedioPonderadoGeneral(@Param("productoId") Integer productoId);
```

**Fórmula utilizada:**
```
Precio Promedio Ponderado = Σ(cantidad_disponible × costo_unitario) / Σ(cantidad_disponible)
```

---

### 2. **ProductPriceResponse** - Nuevo DTO
Creado para devolver información completa del precio promedio:

```java
{
  "productoId": 1,
  "nombreProducto": "Harina de Trigo",
  "almacenId": 1,
  "nombreAlmacen": "Almacén Central",
  "precioPromedioPonderado": 15.500000,
  "stockDisponible": 1500.000,
  "mensaje": null
}
```

---

### 3. **ProductResponse** - Campos añadidos
Se añadieron campos opcionales al DTO existente:

```java
private BigDecimal precioPromedioPonderado;  // Precio promedio calculado
private BigDecimal stockTotal;                // Stock total disponible
```

---

### 4. **ProductService** - Nuevos métodos

#### a) `calcularPrecioPromedio(Integer productoId, Long almacenId)`
Calcula el precio promedio para un producto en un almacén específico.

#### b) `calcularPrecioPromedioGeneral(Integer productoId)`
Calcula el precio promedio para un producto en todos los almacenes.

#### c) `getByIdWithPrice(Integer id, Long almacenId)`
Obtiene un producto con su precio promedio incluido en la respuesta.

---

### 5. **ProductController** - Nuevos endpoints

#### 🔹 Endpoint 1: Obtener producto con precio
```http
GET /product/{id}/with-price?almacenId={almacenId}
```

**Ejemplo de uso:**
```javascript
// Con almacén específico
GET /product/1/with-price?almacenId=1

// Sin almacén (todos los almacenes)
GET /product/1/with-price
```

**Respuesta:**
```json
{
  "message": "Producto encontrado con precio",
  "data": {
    "id": 1,
    "code": "P001",
    "name": "Harina de Trigo",
    "description": "Harina tipo 000",
    "unitOfMeasure": "KILOGRAMO",
    "precioPromedioPonderado": 15.500000,
    "stockTotal": 1500.000
  }
}
```

---

#### 🔹 Endpoint 2: Calcular precio promedio
```http
GET /product/{id}/precio-promedio?almacenId={almacenId}
```

**Ejemplo de uso:**
```javascript
// Precio promedio en un almacén específico
GET /product/1/precio-promedio?almacenId=1

// Precio promedio general (todos los almacenes)
GET /product/1/precio-promedio
```

**Respuesta:**
```json
{
  "message": "Precio promedio calculado",
  "data": {
    "productoId": 1,
    "nombreProducto": "Harina de Trigo",
    "almacenId": 1,
    "nombreAlmacen": "Almacén Central",
    "precioPromedioPonderado": 15.500000,
    "stockDisponible": 1500.000,
    "mensaje": null
  }
}
```

**Caso sin stock:**
```json
{
  "message": "Precio promedio calculado",
  "data": {
    "productoId": 5,
    "nombreProducto": "Producto Sin Stock",
    "almacenId": 1,
    "nombreAlmacen": "Almacén Central",
    "precioPromedioPonderado": 0.000000,
    "stockDisponible": 0.000,
    "mensaje": "No hay stock disponible en este almacén"
  }
}
```

---

## 💡 Casos de Uso

### 📝 Caso 1: Crear Orden de Compra en el Frontend

Cuando el usuario está creando una orden de compra y necesita mostrar el precio unitario promedio:

```javascript
// React/Vue/Angular ejemplo
async function cargarPrecioPromedio(productoId, almacenId) {
  try {
    const response = await fetch(
      `/api/product/${productoId}/precio-promedio?almacenId=${almacenId}`,
      {
        headers: {
          'Authorization': `Bearer ${token}`
        }
      }
    );
    
    const data = await response.json();
    
    if (data.data.stockDisponible > 0) {
      // Mostrar el precio promedio en el formulario
      setPrecioUnitario(data.data.precioPromedioPonderado);
    } else {
      // Alertar que no hay stock
      alert(data.data.mensaje);
    }
  } catch (error) {
    console.error('Error al calcular precio:', error);
  }
}
```

---

### 📦 Caso 2: Listar productos con precios para selección

```javascript
async function cargarProductosConPrecios(almacenId) {
  try {
    // Primero obtener lista de productos
    const productos = await fetch('/api/product?page=0&size=100');
    const productosData = await productos.json();
    
    // Enriquecer con precios promedio
    const productosConPrecio = await Promise.all(
      productosData.data.content.map(async (producto) => {
        const precio = await fetch(
          `/api/product/${producto.id}/precio-promedio?almacenId=${almacenId}`
        );
        const precioData = await precio.json();
        
        return {
          ...producto,
          precioPromedio: precioData.data.precioPromedioPonderado,
          stock: precioData.data.stockDisponible
        };
      })
    );
    
    return productosConPrecio;
  } catch (error) {
    console.error('Error:', error);
  }
}
```

---

### 🔄 Caso 3: Validación antes de crear orden

```javascript
async function validarStockYPrecio(detalles, almacenId) {
  const validaciones = [];
  
  for (const detalle of detalles) {
    const response = await fetch(
      `/api/product/${detalle.productoId}/precio-promedio?almacenId=${almacenId}`
    );
    const data = await response.json();
    
    validaciones.push({
      productoId: detalle.productoId,
      tieneStock: data.data.stockDisponible > 0,
      precioSugerido: data.data.precioPromedioPonderado,
      stockDisponible: data.data.stockDisponible
    });
  }
  
  return validaciones;
}
```

---

## 🎨 Características Principales

### ✅ Ventajas del Sistema

1. **Cálculo Dinámico**: El precio se calcula en tiempo real basándose en los lotes disponibles
2. **No Afecta Lógica Existente**: Los cambios son aditivos, no modifican la funcionalidad actual
3. **Precisión**: Usa 6 decimales para mantener precisión en los cálculos
4. **Filtrado Inteligente**: Solo considera lotes con:
   - `cantidadDisponible > 0`
   - `estado = 'DISPONIBLE'`
5. **Flexible**: Puede calcular por almacén específico o para todos los almacenes

---

## 📐 Ejemplo de Cálculo

Supongamos que tenemos los siguientes lotes de "Harina de Trigo" en el Almacén 1:

| Lote | Cantidad Disponible | Costo Unitario | Total       |
|------|---------------------|----------------|-------------|
| L001 | 500 kg              | S/ 15.00       | S/ 7,500.00 |
| L002 | 300 kg              | S/ 16.50       | S/ 4,950.00 |
| L003 | 200 kg              | S/ 14.00       | S/ 2,800.00 |

**Cálculo del Precio Promedio Ponderado:**

```
Precio Promedio = (500 × 15.00 + 300 × 16.50 + 200 × 14.00) / (500 + 300 + 200)
                = (7,500 + 4,950 + 2,800) / 1,000
                = 15,250 / 1,000
                = S/ 15.25
```

---

## 🔒 Permisos Requeridos

Todos los nuevos endpoints requieren el permiso:
- `product:read`

Se respetan los permisos existentes de la aplicación.

---

## 🚀 Mejores Prácticas

### 1. **Cachear resultados en el frontend**
```javascript
// Cache simple para evitar llamadas repetidas
const priceCache = new Map();

async function getPrecioConCache(productoId, almacenId, ttl = 60000) {
  const key = `${productoId}-${almacenId}`;
  const cached = priceCache.get(key);
  
  if (cached && Date.now() - cached.timestamp < ttl) {
    return cached.data;
  }
  
  const data = await fetchPrecioPromedio(productoId, almacenId);
  priceCache.set(key, { data, timestamp: Date.now() });
  
  return data;
}
```

### 2. **Mostrar advertencias de stock bajo**
```javascript
if (data.stockDisponible < producto.stockMinimo) {
  showWarning(`Stock bajo: ${data.stockDisponible} disponible`);
}
```

### 3. **Validar antes de enviar orden**
```javascript
// Validar que el precio ingresado no difiera mucho del promedio
const diferencia = Math.abs(precioIngresado - precioPromedio);
const porcentajeDiferencia = (diferencia / precioPromedio) * 100;

if (porcentajeDiferencia > 10) {
  showWarning(
    `El precio ingresado difiere en ${porcentajeDiferencia.toFixed(2)}% del precio promedio`
  );
}
```

---

## 🐛 Manejo de Errores

### Producto sin stock
```json
{
  "precioPromedioPonderado": 0.000000,
  "stockDisponible": 0.000,
  "mensaje": "No hay stock disponible en este almacén"
}
```

### Producto no encontrado
```json
{
  "message": "Producto no encontrado",
  "status": 404
}
```

### Almacén no encontrado
```json
{
  "message": "Almacén no encontrado",
  "status": 404
}
```

---

## 📊 Integración con Orden de Compra

### Frontend - Formulario de Orden de Compra

```javascript
// Componente de detalle de orden de compra
const DetalleCompraForm = ({ almacenId }) => {
  const [productoSeleccionado, setProductoSeleccionado] = useState(null);
  const [precioSugerido, setPrecioSugerido] = useState(0);
  const [stockDisponible, setStockDisponible] = useState(0);
  
  const handleProductoChange = async (productoId) => {
    if (!productoId || !almacenId) return;
    
    try {
      const response = await fetch(
        `/api/product/${productoId}/precio-promedio?almacenId=${almacenId}`
      );
      const data = await response.json();
      
      setPrecioSugerido(data.data.precioPromedioPonderado);
      setStockDisponible(data.data.stockDisponible);
      
      // Auto-llenar el precio unitario con el promedio
      setValue('precioUnitario', data.data.precioPromedioPonderado);
      
    } catch (error) {
      console.error('Error al obtener precio:', error);
    }
  };
  
  return (
    <div>
      <select onChange={(e) => handleProductoChange(e.target.value)}>
        {/* productos */}
      </select>
      
      <div className="info-box">
        <p>Precio Promedio: S/ {precioSugerido.toFixed(2)}</p>
        <p>Stock Actual: {stockDisponible} unidades</p>
      </div>
      
      <input 
        type="number" 
        step="0.01"
        defaultValue={precioSugerido}
        placeholder="Precio Unitario"
      />
    </div>
  );
};
```

---

## 🔄 Actualización Automática del Precio Promedio

El precio promedio se actualiza automáticamente cuando:

1. ✅ Se registra una nueva **orden de compra** (nuevo lote con costo unitario)
2. ✅ Se consume stock en una **orden de venta** (reduce cantidades disponibles)
3. ✅ Se produce un **ajuste de inventario**
4. ✅ Se genera una **orden de producción**

**No requiere mantenimiento manual**, el cálculo es dinámico en cada consulta.

---

## 📈 Rendimiento

### Optimizaciones implementadas:

1. **Query eficiente**: Usa agregaciones SQL nativas (SUM, CASE WHEN)
2. **Índices sugeridos**: 
   - `stock_lote(lote_id, almacen_id, cantidad_disponible)`
   - `lote(producto_id, estado)`
3. **Transacciones de solo lectura**: `@Transactional(readOnly = true)`
4. **Redondeo controlado**: 6 decimales para precisión vs rendimiento

---

## 🎯 Casos de Prueba

### Prueba 1: Producto con múltiples lotes
```bash
# Crear varios lotes con diferentes precios
POST /api/orden-compra
{
  "almacenId": 1,
  "detalles": [
    {"productoId": 1, "cantidad": 100, "precioUnitario": 15.00},
    {"productoId": 1, "cantidad": 50, "precioUnitario": 16.00}
  ]
}

# Consultar precio promedio
GET /api/product/1/precio-promedio?almacenId=1
# Esperado: (100*15 + 50*16) / 150 = 15.333333
```

### Prueba 2: Producto sin stock
```bash
GET /api/product/999/precio-promedio?almacenId=1
# Esperado: precioPromedioPonderado = 0, mensaje de error
```

### Prueba 3: Múltiples almacenes
```bash
# Precio en almacén específico
GET /api/product/1/precio-promedio?almacenId=1

# Precio en todos los almacenes
GET /api/product/1/precio-promedio
```

---

## 🎓 Conclusión

Este sistema proporciona una manera eficiente y precisa de calcular el costo promedio de los productos, facilitando la toma de decisiones en las órdenes de compra y manteniendo la integridad de los datos del inventario.

**Beneficios clave:**
- ✅ Sin impacto en código existente
- ✅ Cálculo en tiempo real
- ✅ Fácil integración con frontend
- ✅ Precisión en costos
- ✅ Escalable y mantenible

---

## 📞 Soporte

Para cualquier duda sobre la implementación, revisar:
- `StockLoteRepository.java` - Queries de cálculo
- `ProductService.java` - Lógica de negocio
- `ProductController.java` - Endpoints REST

