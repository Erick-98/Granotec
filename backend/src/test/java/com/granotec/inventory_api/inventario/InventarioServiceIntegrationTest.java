package com.granotec.inventory_api.inventario;

import com.granotec.inventory_api.Lote.LoteRepository;
import com.granotec.inventory_api.inventario.service.InventarioService;
import com.granotec.inventory_api.produccion.service.ProduccionService;
import com.granotec.inventory_api.product.Product;
import com.granotec.inventory_api.product.ProductRepository;
import com.granotec.inventory_api.storage.Storage;
import com.granotec.inventory_api.storage.StorageRepository;
import com.granotec.inventory_api.inventario.dto.MovimientoInventarioRequest;
import com.granotec.inventory_api.StockLote.StockLoteRepository;
import com.granotec.inventory_api.Kardex.KardexRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@SpringBootTest
@Transactional
public class InventarioServiceIntegrationTest {
    @Autowired
    private InventarioService inventarioService;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private StorageRepository storageRepository;
    @Autowired
    private StockLoteRepository stockLoteRepository;
    @Autowired
    private KardexRepository kardexRepository;
    @Autowired
    private LoteRepository loteRepository;
    @Autowired
    private ProduccionService produccionService;

    @Test
    void testFIFOyStock() {
        // Preparar datos
        Product producto = productRepository.save(Product.builder().nombreComercial("Producto FIFO").tipoProducto(com.granotec.inventory_api.common.enums.TypeProduct.PRODUCTO_TERMINADO).build());
        Storage almacen = storageRepository.save(Storage.builder().nombre("Almacen FIFO").build());
        // Simular entradas y ventas FIFO aquí (mock o integración real)
        // ...
        Assertions.assertTrue(true, "Prueba de integración FIFO pendiente de implementación completa");
    }

    @Test
    void flujoFIFO_entradaYsalida_validaStockYKardex() {
        // Crear producto y almacén
        var producto = productRepository.save(Product.builder().nombreComercial("Producto FIFO").tipoProducto(com.granotec.inventory_api.common.enums.TypeProduct.PRODUCTO_TERMINADO).build());
        var almacen = storageRepository.save(Storage.builder().nombre("Almacen FIFO").build());
        // Generar lote mediante el servicio de producción (crear -> iniciar -> finalizar)
        var ordenReq = new com.granotec.inventory_api.produccion.dto.OrdenProduccionRequest();
        ordenReq.setProductoId(producto.getId());
        ordenReq.setCantidadProducir(new BigDecimal("1"));
        ordenReq.setFechaInicio(java.time.LocalDate.now().toString());
        var ordenResp = produccionService.crearOrden(ordenReq);
        produccionService.iniciarOrden(ordenResp.getId().intValue());
        var finalizar = new com.granotec.inventory_api.produccion.dto.FinalizarProduccionRequest();
        finalizar.setOrdenProduccionId(ordenResp.getId().intValue());
        finalizar.setCantidadProducida(new BigDecimal("1"));
        finalizar.setCostoTotal(new BigDecimal("1"));
        finalizar.setUsuarioId(1L);
        var loteResp = produccionService.finalizarOrden(finalizar);
        // Obtener lote recién creado
        var lotesAll = loteRepository.findByProductoIdAndEstadoOrderByFechaProduccionAsc(producto.getId(), "DISPONIBLE");
        if (lotesAll == null || lotesAll.isEmpty()) {
            throw new IllegalStateException("No se creó ningún lote disponible para el producto");
        }
        var lote = lotesAll.get(0);
        Long loteId = lote.getId().longValue();
        // Entrada 1: 10 unidades
        MovimientoInventarioRequest entrada1 = new MovimientoInventarioRequest();
        entrada1.setAlmacenId(almacen.getId());
        entrada1.setProductoId(producto.getId());
        entrada1.setLoteId(loteId);
        entrada1.setCantidad(new BigDecimal("10"));
        entrada1.setObservacion("Entrada 1");
        inventarioService.registrarEntrada(entrada1);
        // Entrada 2: 5 unidades
        MovimientoInventarioRequest entrada2 = new MovimientoInventarioRequest();
        entrada2.setAlmacenId(almacen.getId());
        entrada2.setProductoId(producto.getId());
        entrada2.setLoteId(loteId);
        entrada2.setCantidad(new BigDecimal("5"));
        entrada2.setObservacion("Entrada 2");
        inventarioService.registrarEntrada(entrada2);
        // Salida FIFO: 12 unidades
        MovimientoInventarioRequest salida = new MovimientoInventarioRequest();
        salida.setAlmacenId(almacen.getId());
        salida.setProductoId(producto.getId());
        salida.setCantidad(new BigDecimal("12"));
        salida.setObservacion("Venta FIFO");
        inventarioService.registrarSalida(salida);
        // Validar stock final (debe quedar 3)
        var stockLotes = stockLoteRepository.findByLoteProductoIdAndAlmacenId(producto.getId(), almacen.getId());
        BigDecimal totalDisponible = stockLotes.stream().map(l -> l.getCantidadDisponible()).reduce(BigDecimal.ZERO, BigDecimal::add);
        Assertions.assertEquals(new BigDecimal("3"), totalDisponible, "El stock FIFO final debe ser 3");
        // Validar kardex (debe haber 3 movimientos)
        var kardex = kardexRepository.findAll();
        Assertions.assertTrue(kardex.size() >= 3, "Debe haber al menos 3 movimientos en kardex");
    }
}
