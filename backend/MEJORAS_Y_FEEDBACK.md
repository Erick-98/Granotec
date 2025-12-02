# 🎯 Feedback y Mejoras Sugeridas

## 📊 Análisis del Sistema Actual

### ✅ Fortalezas Identificadas

1. **Arquitectura bien estructurada**
   - Separación clara de responsabilidades (Controller → Service → Repository)
   - Uso de DTOs para transferencia de datos
   - Buena implementación de seguridad con permisos

2. **Modelo de datos robusto**
   - Trazabilidad completa con Kardex
   - Sistema de lotes (FIFO implícito)
   - Auditoría con BaseEntity (createdAt, updatedAt)

3. **Manejo de transacciones**
   - Uso correcto de `@Transactional`
   - Bloqueos pesimistas para concurrencia (`PESSIMISTIC_WRITE`)

---

## 🚨 Áreas de Mejora Críticas

### 1. **Sincronización Stock_Almacen vs StockLote**

**Problema detectado:**
Actualmente tienes dos tablas de stock:
- `stock_almacen`: Stock por producto y almacén (total)
- `stock_lote`: Stock por lote y almacén (detallado)

**Riesgo:** Pueden desincronizarse si no se actualizan juntas.

**Solución recomendada:**

```java
// Crear un servicio centralizado para actualizar stock
@Service
@RequiredArgsConstructor
public class StockSyncService {
    
    private final StockAlmacenRepository stockAlmacenRepository;
    private final StockLoteRepository stockLoteRepository;
    
    /**
     * Sincroniza el stock general con la suma de stocks por lote
     * Debe ejecutarse después de cada operación que afecte el stock
     */
    @Transactional
    public void sincronizarStockAlmacen(Integer productoId, Long almacenId) {
        // Calcular stock total desde los lotes
        BigDecimal stockTotalLotes = stockLoteRepository
            .sumDisponibleByProductoAndAlmacen(productoId, almacenId);
        
        // Actualizar o crear registro en stock_almacen
        StockAlmacen stockAlmacen = stockAlmacenRepository
            .findByAlmacenIdAndProductoId(almacenId, productoId)
            .orElse(new StockAlmacen());
        
        stockAlmacen.setCantidad(stockTotalLotes);
        stockAlmacenRepository.save(stockAlmacen);
    }
    
    /**
     * Job programado para verificar sincronización
     * Ejecutar diariamente para detectar inconsistencias
     */
    @Scheduled(cron = "0 0 2 * * *") // 2 AM diario
    public void verificarConsistenciaStock() {
        List<StockAlmacen> todos = stockAlmacenRepository.findAll();
        
        for (StockAlmacen stock : todos) {
            BigDecimal stockLotes = stockLoteRepository
                .sumDisponibleByProductoAndAlmacen(
                    stock.getProducto().getId(),
                    stock.getAlmacen().getId()
                );
            
            if (!stock.getCantidad().equals(stockLotes)) {
                log.warn("Inconsistencia detectada: Producto {} Almacén {} - " +
                         "Stock Almacén: {} vs Stock Lotes: {}", 
                         stock.getProducto().getId(),
                         stock.getAlmacen().getId(),
                         stock.getCantidad(),
                         stockLotes);
                
                // Auto-corregir
                sincronizarStockAlmacen(
                    stock.getProducto().getId(),
                    stock.getAlmacen().getId()
                );
            }
        }
    }
}
```

**Integración en CompraServiceImpl:**
```java
@Override
@Transactional
public CompraResponse registrarCompra(CompraRequest request) {
    // ... código existente ...
    
    // AÑADIR al final de cada detalle:
    stockSyncService.sincronizarStockAlmacen(
        det.getProductoId(), 
        request.getAlmacenId()
    );
    
    return compraMapperHelper.toDto(ordenCompra);
}
```

---

### 2. **Validación de Stock Negativo**

**Problema:** No hay validación explícita para evitar stock negativo.

**Solución:**

```java
// En StockLoteService o similar
@Transactional
public void descontarStock(Integer productoId, Long almacenId, BigDecimal cantidad) {
    BigDecimal stockDisponible = stockLoteRepository
        .sumDisponibleByProductoAndAlmacen(productoId, almacenId);
    
    if (stockDisponible.compareTo(cantidad) < 0) {
        throw new InsufficientStockException(
            String.format("Stock insuficiente. Disponible: %s, Requerido: %s",
                stockDisponible, cantidad)
        );
    }
    
    // Proceder con descuento FIFO...
}
```

---

### 3. **Precio Promedio vs Precio FIFO**

**Observación actual:** 
- Usas `precioVentaUnitario` en Lote
- El precio promedio se calcula dinámicamente

**Mejora sugerida:** Implementar método de costeo configurable

```java
// Enum para métodos de costeo
public enum MetodoCosteo {
    PROMEDIO_PONDERADO,
    FIFO,
    ULTIMO_COSTO
}

// En application.properties
inventory.metodo.costeo=PROMEDIO_PONDERADO

// Strategy Pattern
public interface CosteoStrategy {
    BigDecimal calcularCosto(Integer productoId, Long almacenId);
}

@Service
public class PromedioStrategy implements CosteoStrategy {
    @Override
    public BigDecimal calcularCosto(Integer productoId, Long almacenId) {
        return stockLoteRepository
            .calcularPrecioPromedioPonderado(productoId, almacenId);
    }
}

@Service
public class FIFOStrategy implements CosteoStrategy {
    @Override
    public BigDecimal calcularCosto(Integer productoId, Long almacenId) {
        // Obtener el costo del lote más antiguo con stock
        return stockLoteRepository
            .findAvailableByProductoAndAlmacenForUpdate(productoId, almacenId)
            .stream()
            .findFirst()
            .map(sl -> sl.getLote().getCostoUnitario())
            .orElse(BigDecimal.ZERO);
    }
}
```

---

### 4. **Optimización de Consultas**

**Problema:** Consultas N+1 en relaciones ManyToOne

**Solución:** Usar fetch joins

```java
// En OrdenCompraRepository
@Query("SELECT oc FROM OrdenCompra oc " +
       "LEFT JOIN FETCH oc.proveedor " +
       "LEFT JOIN FETCH oc.almacen " +
       "LEFT JOIN FETCH oc.detalles d " +
       "LEFT JOIN FETCH d.producto " +
       "LEFT JOIN FETCH d.lote " +
       "WHERE oc.id = :id")
Optional<OrdenCompra> findByIdWithDetails(@Param("id") Integer id);
```

---

### 5. **Manejo de Tasa de Cambio**

**Problema:** Tasa de cambio hardcodeada en CompraServiceImpl

```java
requestK.setTasaCambio(BigDecimal.valueOf(3.5)); // ❌ Hardcoded
```

**Solución:**

```java
// Crear servicio de tipo de cambio
@Service
@RequiredArgsConstructor
public class TipoCambioService {
    
    private final TipoCambioRepository repository;
    
    /**
     * Obtiene la tasa de cambio del día o la más reciente
     */
    public BigDecimal getTasaCambioActual(String monedaOrigen, String monedaDestino) {
        LocalDate hoy = LocalDate.now();
        
        return repository
            .findByFechaAndMonedasOrderByFechaDesc(hoy, monedaOrigen, monedaDestino)
            .map(TipoCambio::getValor)
            .orElseGet(() -> {
                // Si no hay tasa del día, usar la más reciente
                return repository
                    .findTopByMonedasOrderByFechaDesc(monedaOrigen, monedaDestino)
                    .map(TipoCambio::getValor)
                    .orElseThrow(() -> new BadRequestException(
                        "No se encontró tipo de cambio para " + monedaOrigen + "/" + monedaDestino
                    ));
            });
    }
}

// Entidad TipoCambio
@Entity
@Table(name = "tipo_cambio")
public class TipoCambio extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private LocalDate fecha;
    private String monedaOrigen;
    private String monedaDestino;
    private BigDecimal valor;
}
```

**Uso en CompraServiceImpl:**
```java
requestK.setTasaCambio(tipoCambioService.getTasaCambioActual("USD", "PEN"));
```

---

### 6. **Auditoría Mejorada**

**Mejora:** Registrar más contexto en las operaciones

```java
// Crear tabla de auditoría
@Entity
@Table(name = "auditoria_operaciones")
public class AuditoriaOperacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private LocalDateTime fecha;
    private String tipoOperacion; // COMPRA, VENTA, AJUSTE, etc.
    private String entidad;
    private Long entidadId;
    private String usuario;
    private String ip;
    private String detalles; // JSON con cambios
    private String estadoAnterior;
    private String estadoNuevo;
}

// Service
@Service
@Aspect
@RequiredArgsConstructor
public class AuditoriaService {
    
    private final AuditoriaRepository repository;
    
    @AfterReturning(pointcut = "@annotation(Auditable)", returning = "result")
    public void auditarOperacion(JoinPoint joinPoint, Object result) {
        // Extraer información del contexto
        String usuario = SecurityContextHolder.getContext()
            .getAuthentication().getName();
        
        AuditoriaOperacion auditoria = new AuditoriaOperacion();
        auditoria.setFecha(LocalDateTime.now());
        auditoria.setUsuario(usuario);
        // ... más campos
        
        repository.save(auditoria);
    }
}

// Uso con anotación
@Auditable(tipo = "COMPRA")
@Override
public CompraResponse registrarCompra(CompraRequest request) {
    // ...
}
```

---

### 7. **Validaciones de Negocio**

**Mejora:** Centralizar validaciones complejas

```java
@Service
@RequiredArgsConstructor
public class ValidacionInventarioService {
    
    /**
     * Valida que una compra sea consistente
     */
    public void validarOrdenCompra(CompraRequest request) {
        // 1. Validar proveedor activo
        Vendor proveedor = vendorRepository.findById(request.getProveedorId())
            .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado"));
        
        if (!proveedor.getActivo()) {
            throw new BadRequestException("El proveedor está inactivo");
        }
        
        // 2. Validar almacén activo
        Storage almacen = storageRepository.findById(request.getAlmacenId())
            .orElseThrow(() -> new ResourceNotFoundException("Almacén no encontrado"));
        
        if (!almacen.getActivo()) {
            throw new BadRequestException("El almacén está inactivo");
        }
        
        // 3. Validar productos
        for (DetalleCompraDTO detalle : request.getDetalles()) {
            Product producto = productRepository.findById(detalle.getProductoId())
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Producto " + detalle.getProductoId() + " no encontrado"));
            
            if (producto.getIsLocked()) {
                throw new BadRequestException(
                    "El producto " + producto.getNombreComercial() + " está bloqueado");
            }
            
            // 4. Validar cantidades
            if (detalle.getCantidadRecibida().compareTo(detalle.getCantidadOrdenada()) > 0) {
                throw new BadRequestException(
                    "La cantidad recibida no puede ser mayor a la ordenada");
            }
            
            // 5. Validar fechas de lote
            if (detalle.getFechaVencimiento() != null && 
                detalle.getFechaProduccion() != null) {
                if (detalle.getFechaVencimiento().isBefore(detalle.getFechaProduccion())) {
                    throw new BadRequestException(
                        "La fecha de vencimiento no puede ser anterior a la de producción");
                }
            }
            
            // 6. Validar código de lote único
            if (loteRepository.existsByCodigoLote(detalle.getLote())) {
                throw new BadRequestException(
                    "El código de lote " + detalle.getLote() + " ya existe");
            }
        }
    }
    
    /**
     * Valida que una venta sea factible
     */
    public void validarOrdenVenta(VentaRequest request) {
        for (DetalleVentaDTO detalle : request.getDetalles()) {
            BigDecimal stockDisponible = stockLoteRepository
                .sumDisponibleByProductoAndAlmacen(
                    detalle.getProductoId(),
                    request.getAlmacenId()
                );
            
            if (stockDisponible.compareTo(detalle.getCantidad()) < 0) {
                Product producto = productRepository.findById(detalle.getProductoId())
                    .orElseThrow();
                
                throw new InsufficientStockException(
                    String.format("Stock insuficiente para %s. Disponible: %s, Solicitado: %s",
                        producto.getNombreComercial(),
                        stockDisponible,
                        detalle.getCantidad())
                );
            }
        }
    }
}
```

---

## 🎨 Mejoras de Arquitectura

### 1. **DTOs Reutilizables**

Crear DTOs base para evitar duplicación:

```java
@Data
public class ProductoBasicoDTO {
    private Integer id;
    private String codigo;
    private String nombre;
}

@Data
public class AlmacenBasicoDTO {
    private Long id;
    private String nombre;
    private String ubicacion;
}

// Usar composición
@Data
public class StockInfoDTO {
    private ProductoBasicoDTO producto;
    private AlmacenBasicoDTO almacen;
    private BigDecimal cantidad;
    private BigDecimal precioPromedio;
}
```

---

### 2. **Eventos de Dominio**

Implementar eventos para desacoplar lógica:

```java
// Evento cuando se registra una compra
@Getter
@AllArgsConstructor
public class CompraRegistradaEvent {
    private final OrdenCompra ordenCompra;
    private final LocalDateTime timestamp;
}

// Publisher
@Service
@RequiredArgsConstructor
public class CompraServiceImpl implements CompraService {
    
    private final ApplicationEventPublisher eventPublisher;
    
    @Override
    @Transactional
    public CompraResponse registrarCompra(CompraRequest request) {
        // ... lógica de compra ...
        
        // Publicar evento
        eventPublisher.publishEvent(
            new CompraRegistradaEvent(ordenCompra, LocalDateTime.now())
        );
        
        return response;
    }
}

// Listeners
@Component
@RequiredArgsConstructor
public class CompraEventListener {
    
    private final EmailService emailService;
    private final NotificationService notificationService;
    
    @EventListener
    @Async
    public void onCompraRegistrada(CompraRegistradaEvent event) {
        // Enviar email al proveedor
        emailService.enviarConfirmacionCompra(event.getOrdenCompra());
        
        // Notificar al encargado de almacén
        notificationService.notificarNuevaCompra(event.getOrdenCompra());
    }
}
```

---

### 3. **Caché para Consultas Frecuentes**

```java
@Configuration
@EnableCaching
public class CacheConfig {
    
    @Bean
    public CacheManager cacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        cacheManager.setCaches(Arrays.asList(
            new ConcurrentMapCache("productos"),
            new ConcurrentMapCache("precios-promedio"),
            new ConcurrentMapCache("stock")
        ));
        return cacheManager;
    }
}

// En ProductService
@Cacheable(value = "precios-promedio", key = "#productoId + '-' + #almacenId")
public BigDecimal calcularPrecioPromedio(Integer productoId, Long almacenId) {
    // ...
}

// Invalidar cache cuando cambie el stock
@CacheEvict(value = "precios-promedio", allEntries = true)
public void actualizarStock(...) {
    // ...
}
```

---

## 📊 Métricas y Monitoreo

### Implementar métricas con Micrometer:

```java
@Service
@RequiredArgsConstructor
public class MetricasInventarioService {
    
    private final MeterRegistry meterRegistry;
    
    public void registrarCompra(BigDecimal valor) {
        meterRegistry.counter("inventario.compras.total").increment();
        meterRegistry.gauge("inventario.compras.valor", valor);
    }
    
    public void registrarVenta(BigDecimal valor) {
        meterRegistry.counter("inventario.ventas.total").increment();
        meterRegistry.timer("inventario.ventas.tiempo")
            .record(() -> {
                // operación
            });
    }
}
```

---

## 🔒 Seguridad Adicional

### 1. **Rate Limiting**

```java
@Configuration
public class RateLimitConfig {
    
    @Bean
    public RateLimiter rateLimiter() {
        return RateLimiter.create(100.0); // 100 requests/segundo
    }
}

@Aspect
@Component
@RequiredArgsConstructor
public class RateLimitAspect {
    
    private final RateLimiter rateLimiter;
    
    @Before("@annotation(RateLimited)")
    public void rateLimit() {
        if (!rateLimiter.tryAcquire()) {
            throw new TooManyRequestsException("Rate limit excedido");
        }
    }
}
```

### 2. **Validación de Entrada Robusta**

```java
@Component
public class InputSanitizer {
    
    public String sanitize(String input) {
        if (input == null) return null;
        
        // Remover scripts y caracteres peligrosos
        return input.replaceAll("<script.*?>.*?</script>", "")
                   .replaceAll("[<>\"']", "")
                   .trim();
    }
}
```

---

## 📱 APIs Adicionales Útiles

### 1. **Reportes**

```java
@RestController
@RequestMapping("/api/reportes")
public class ReporteController {
    
    @GetMapping("/kardex/pdf")
    public ResponseEntity<byte[]> exportarKardexPDF(
            @RequestParam Integer productoId,
            @RequestParam LocalDate desde,
            @RequestParam LocalDate hasta) {
        
        byte[] pdf = reporteService.generarKardexPDF(productoId, desde, hasta);
        
        return ResponseEntity.ok()
            .header("Content-Type", "application/pdf")
            .header("Content-Disposition", "attachment; filename=kardex.pdf")
            .body(pdf);
    }
    
    @GetMapping("/valorizado")
    public ResponseEntity<ValorizadoResponse> reporteValorizado(
            @RequestParam Long almacenId) {
        
        return ResponseEntity.ok(reporteService.calcularStockValorizado(almacenId));
    }
}
```

### 2. **Dashboard/Estadísticas**

```java
@GetMapping("/api/dashboard/stats")
public ResponseEntity<DashboardStats> getStats() {
    DashboardStats stats = new DashboardStats();
    stats.setTotalProductos(productRepository.count());
    stats.setValorInventario(calcularValorTotal());
    stats.setProductosBajoStock(contarProductosBajoMinimo());
    stats.setComprasMes(contarComprasMes());
    stats.setVentasMes(contarVentasMes());
    
    return ResponseEntity.ok(stats);
}
```

---

## 🎓 Conclusión y Próximos Pasos

### Prioridad Alta 🔴
1. ✅ Implementar sincronización Stock_Almacen ↔ StockLote
2. ✅ Validar stock negativo
3. ✅ Externalizar tasa de cambio

### Prioridad Media 🟡
4. ✅ Optimizar consultas (fetch joins)
5. ✅ Centralizar validaciones de negocio
6. ✅ Implementar auditoría mejorada

### Prioridad Baja 🟢
7. ✅ Eventos de dominio
8. ✅ Caché
9. ✅ Métricas y monitoreo

---

## 💡 Recomendaciones Finales

1. **Testing**: Implementar tests unitarios e integración para las funcionalidades críticas
2. **Documentación**: Mantener Swagger/OpenAPI actualizado
3. **CI/CD**: Configurar pipeline de despliegue automático
4. **Backup**: Estrategia de respaldo de base de datos
5. **Logs**: Implementar logging estructurado (ELK Stack o similar)

**El sistema está bien diseñado. Estas mejoras lo harán enterprise-ready! 🚀**

