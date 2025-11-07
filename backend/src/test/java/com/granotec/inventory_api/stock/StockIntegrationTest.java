package com.granotec.inventory_api.stock;

import com.granotec.inventory_api.product.Product;
import com.granotec.inventory_api.product.ProductRepository;
import com.granotec.inventory_api.storage.StorageRepository;
import com.granotec.inventory_api.storage.Storage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootTest
public class StockIntegrationTest {

    @Autowired
    private StockService stockService;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private StorageRepository storageRepository;

    @Autowired
    private ProductRepository productRepository;

    @Test
    public void concurrentAdjustmentsShouldNotProduceNegativeStock() throws InterruptedException {
        Storage s = storageRepository.save(Storage.builder().nombre("Test").descripcion("desc").build());
        Product p = productRepository.save(Product.builder().name("P1").price(new BigDecimal("1.00")).stock(0).batch("B1").build());

        // crear stock inicial 10
        stockService.adjustStock(s.getId(), p.getId(), "L1", new BigDecimal("10"));

        int threads = 2;
        ExecutorService exec = Executors.newFixedThreadPool(threads);
        CountDownLatch latch = new CountDownLatch(threads);

        // cada hilo intentará disminuir 8 (total 16) -> uno debe fallar por stock insuficiente
        Runnable task = () -> {
            try {
                stockService.adjustStock(s.getId(), p.getId(), "L1", new BigDecimal("-8"));
            } catch (Exception ex) {
                // expected for one thread
            } finally {
                latch.countDown();
            }
        };

        exec.submit(task);
        exec.submit(task);

        latch.await();
        exec.shutdownNow();
        var finalStock = stockRepository.findByAlmacenIdAndProductoIdAndLote(s.getId(), p.getId(), "L1").orElseThrow();
        // final should be either 2 (one decreased) or 10 if both failed. Assert non-negative and <=10
        Assertions.assertTrue(finalStock.getCantidad().compareTo(BigDecimal.ZERO) >= 0);
        Assertions.assertTrue(finalStock.getCantidad().compareTo(new BigDecimal("10")) <= 0);
    }
}
