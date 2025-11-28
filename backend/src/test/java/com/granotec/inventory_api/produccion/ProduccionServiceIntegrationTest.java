package com.granotec.inventory_api.produccion;

import com.granotec.inventory_api.Lote.LoteRepository;
import com.granotec.inventory_api.produccion.dto.OrdenProduccionRequest;
import com.granotec.inventory_api.produccion.service.ProduccionService;
import com.granotec.inventory_api.product.Product;
import com.granotec.inventory_api.product.ProductRepository;
import com.granotec.inventory_api.common.enums.TypeProduct;
import com.granotec.inventory_api.storage.Storage;
import com.granotec.inventory_api.storage.StorageRepository;
import com.granotec.inventory_api.Stock_Almacen.StockAlmacenRepository;
import com.granotec.inventory_api.StockLote.StockLoteRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@SpringBootTest
@Transactional
public class ProduccionServiceIntegrationTest {
    @Autowired
    private ProduccionService produccionService;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private StorageRepository storageRepository;
    @Autowired
    private LoteRepository loteRepository;
    @Autowired private StockAlmacenRepository stockAlmacenRepository;
    @Autowired private StockLoteRepository stockLoteRepository;

    @Test
    void crearOrdenProduccion_consumirInsumos_generarLote_actualizarStock() {
        // Preparar datos
        Product producto = productRepository.save(Product.builder().nombreComercial("Producto Test").tipoProducto(TypeProduct.PRODUCTO_TERMINADO).build());
        Storage almacen = storageRepository.save(Storage.builder().nombre("Almacen Test").build());
        OrdenProduccionRequest req = new OrdenProduccionRequest();
        req.setProductoId(producto.getId());
        req.setCantidadProducir(new BigDecimal("100"));
        req.setFechaInicio(LocalDate.now().toString());
        req.setAlmacenDestinoId(almacen.getId());
        req.setLoteCodigoManual("LOTE-MANUAL-TEST");
        // Ejecutar flujo
        var ordenResp = produccionService.crearOrden(req);
        Assertions.assertEquals(almacen.getId(), ordenResp.getAlmacenDestinoId());
        // Iniciar orden
        produccionService.iniciarOrden(ordenResp.getId());
        // Finalizar orden con cantidad y costo para generar lote en el almacén destino
        var finalizar = new com.granotec.inventory_api.produccion.dto.FinalizarProduccionRequest();
        finalizar.setOrdenProduccionId(ordenResp.getId());
        finalizar.setCantidadProducida(new BigDecimal("100"));
        finalizar.setCostoTotal(new BigDecimal("500"));
        finalizar.setUsuarioId(1L);
        finalizar.setAlmacenDestinoId(almacen.getId());
        finalizar.setLoteCodigoManual("LOTE-MANUAL-TEST");
        var finalResp = produccionService.finalizarOrden(finalizar);
        Assertions.assertEquals("LOTE-MANUAL-TEST", finalResp.getCodigoLoteGenerado());
        // Validar lote generado y stock en almacén
        var lotes = loteRepository.findAll();
        Assertions.assertFalse(lotes.isEmpty(), "Debe generarse al menos un lote");
        var stockAlmacen = stockAlmacenRepository.findByProductoIdAndAlmacenId(producto.getId(), almacen.getId());
        Assertions.assertFalse(stockAlmacen.isEmpty(), "Debe existir stock de producto terminado en el almacén destino");
        Assertions.assertEquals(new BigDecimal("100"), stockAlmacen.get(0).getCantidad());
        var stockLotes = stockLoteRepository.findByLoteProductoIdAndAlmacenId(producto.getId(), almacen.getId());
        BigDecimal totalDisponible = stockLotes.stream().map(s -> s.getCantidadDisponible()).reduce(BigDecimal.ZERO, BigDecimal::add);
        Assertions.assertEquals(new BigDecimal("100"), totalDisponible);
    }
}
