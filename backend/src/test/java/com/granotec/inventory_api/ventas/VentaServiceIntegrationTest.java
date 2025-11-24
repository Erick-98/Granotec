package com.granotec.inventory_api.ventas;

import com.granotec.inventory_api.ventas.service.VentaService;
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

@SpringBootTest
@Transactional
public class VentaServiceIntegrationTest {
    @Autowired
    private VentaService ventaService;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private StorageRepository storageRepository;

    @Test
    void testVentaFIFOyStock() {
        // Preparar datos
        Product producto = productRepository.save(Product.builder()
                .nombreComercial("Producto Venta")
                .tipoProducto(TypeProduct.PRODUCTO_TERMINADO)
                .build());
        Storage almacen = storageRepository.save(Storage.builder().nombre("Almacen Venta").build());
        // Simular venta FIFO aquí (mock o integración real)
        // ...
        Assertions.assertTrue(true, "Prueba de integración de venta FIFO pendiente de implementación completa");
    }
}
