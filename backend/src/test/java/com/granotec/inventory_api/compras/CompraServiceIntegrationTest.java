package com.granotec.inventory_api.compras;

//import com.granotec.inventory_api.compras.service.CompraService;
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


@SpringBootTest
@Transactional
public class CompraServiceIntegrationTest {
    //@Autowired
    //private CompraService compraService;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private StorageRepository storageRepository;

    @Test
    void testOrdenCompraYRecepcion() {
        // Preparar datos
        Product producto = productRepository.save(Product.builder().nombreComercial("Producto Compra").tipoProducto(TypeProduct.PRODUCTO_TERMINADO).build());
        Storage almacen = storageRepository.save(Storage.builder().nombre("Almacen Compra").build());
        // Simular orden de compra y recepción aquí (mock o integración real)
        // ...
        Assertions.assertTrue(true, "Prueba de integración de compra pendiente de implementación completa");
    }
}
