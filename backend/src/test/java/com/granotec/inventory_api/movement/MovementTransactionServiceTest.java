package com.granotec.inventory_api.movement;

import com.granotec.inventory_api.common.exception.BadRequestException;
import com.granotec.inventory_api.movement.dto.CreateMovementRequest;
import com.granotec.inventory_api.product.Product;
import com.granotec.inventory_api.product.ProductRepository;
import com.granotec.inventory_api.stock.StockRepository;
import com.granotec.inventory_api.storage.entity.Storage;
import com.granotec.inventory_api.storage.StorageRepository;
import com.granotec.inventory_api.vendor.VendorRepository;
import com.granotec.inventory_api.customer.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class MovementTransactionServiceTest {

    private MovementRepository movementRepository;
    private ProductRepository productRepository;
    private StockRepository stockRepository;
    private StorageRepository storageRepository;
    private VendorRepository vendorRepository;
    private CustomerRepository customerRepository;

    private MovementTransactionService transactionService;

    @BeforeEach
    void setUp() {
        movementRepository = Mockito.mock(MovementRepository.class);
        productRepository = Mockito.mock(ProductRepository.class);
        stockRepository = Mockito.mock(StockRepository.class);
        storageRepository = Mockito.mock(StorageRepository.class);
        vendorRepository = Mockito.mock(VendorRepository.class);
        customerRepository = Mockito.mock(CustomerRepository.class);

        transactionService = new MovementTransactionService(movementRepository, productRepository, stockRepository, storageRepository, vendorRepository, customerRepository);
    }

    @Test
    void createMovement_transfer_without_storages_throws() {
        CreateMovementRequest req = new CreateMovementRequest();
        req.tipoMovimiento = MovementKind.TRANSFERENCIA;

        BadRequestException ex = assertThrows(BadRequestException.class, () -> transactionService.createMovement(req));
        assertTrue(ex.getMessage().contains("TRANSF"));
    }

    @Test
    void createMovement_entry_updates_stock() {
        CreateMovementRequest req = new CreateMovementRequest();
        req.tipoMovimiento = MovementKind.ENTRADA;
        req.almacenDestinoId = 1L;
        req.fechaMovimiento = LocalDate.now();

        CreateMovementRequest.CreateMovementDetail d = new CreateMovementRequest.CreateMovementDetail();
        d.productId = 1;
        d.lote = "L1";
        d.cantidad = BigDecimal.TEN;
        req.detalles = java.util.List.of(d);

        when(storageRepository.findById(1L)).thenReturn(Optional.of(new Storage()));
        Product p = new Product(); p.setId(1); p.setStock(0); p.setName("P");
        when(productRepository.findById(1)).thenReturn(Optional.of(p));
        when(stockRepository.findByAlmacenIdAndProductoIdAndLote(any(), any(), any())).thenReturn(Optional.empty());
        when(movementRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Movement created = transactionService.createMovement(req);
        assertNotNull(created);
        // verifying product stock updated
        assertEquals(10, p.getStock());
    }
}

