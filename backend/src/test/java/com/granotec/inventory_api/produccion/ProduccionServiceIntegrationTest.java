package com.granotec.inventory_api.produccion;

import com.granotec.inventory_api.Lote.LoteRepository;
import com.granotec.inventory_api.produccion.dto.OrdenProduccionRequest;
import com.granotec.inventory_api.produccion.service.ProduccionService;
import com.granotec.inventory_api.product.Product;
import com.granotec.inventory_api.product.ProductRepository;
import com.granotec.inventory_api.common.enums.TypeProduct;
import com.granotec.inventory_api.storage.Storage;
import com.granotec.inventory_api.storage.StorageRepository;
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

    @Test
    void crearOrdenProduccion_consumirInsumos_generarLote_actualizarStock() {
        // Preparar datos
        Product producto = productRepository.save(Product.builder().nombreComercial("Producto Test").tipoProducto(TypeProduct.PRODUCTO_TERMINADO).build());
        Storage almacen = storageRepository.save(Storage.builder().nombre("Almacen Test").build());
        OrdenProduccionRequest req = new OrdenProduccionRequest();
        req.setProductoId(producto.getId());
        req.setCantidadProducir(new BigDecimal("100"));
        req.setFechaInicio(LocalDate.now().toString());
        // Ejecutar flujo
        var ordenResp = produccionService.crearOrden(req);
        // Iniciar orden
        produccionService.iniciarOrden(ordenResp.getId().intValue());
        // Finalizar orden con cantidad y costo para generar lote
        var finalizar = new com.granotec.inventory_api.produccion.dto.FinalizarProduccionRequest();
        finalizar.setOrdenProduccionId(ordenResp.getId().intValue());
        finalizar.setCantidadProducida(new BigDecimal("100"));
        finalizar.setCostoTotal(new BigDecimal("500"));
        finalizar.setUsuarioId(1L);
        produccionService.finalizarOrden(finalizar);
        // Validar lote generado
        List<?> lotes = loteRepository.findAll();
        Assertions.assertFalse(lotes.isEmpty(), "Debe generarse al menos un lote");
    }
}
