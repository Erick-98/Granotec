package com.granotec.inventory_api.movement;

import com.granotec.inventory_api.common.exception.BadRequestException;
import com.granotec.inventory_api.movement.dto.CreateMovementRequest;
import com.granotec.inventory_api.product.Product;
import com.granotec.inventory_api.product.ProductRepository;
import com.granotec.inventory_api.stock.Stock;
import com.granotec.inventory_api.stock.StockRepository;
import com.granotec.inventory_api.storage.Storage;
import com.granotec.inventory_api.storage.StorageRepository;
import com.granotec.inventory_api.vendor.VendorRepository;
import com.granotec.inventory_api.customer.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Service
public class MovementTransactionService {

    private final MovementRepository movementRepository;
    private final ProductRepository productRepository;
    private final StockRepository stockRepository;
    private final StorageRepository storageRepository;
    private final VendorRepository vendorRepository;
    private final CustomerRepository customerRepository;

    public MovementTransactionService(MovementRepository movementRepository,
                                      ProductRepository productRepository,
                                      StockRepository stockRepository,
                                      StorageRepository storageRepository,
                                      VendorRepository vendorRepository,
                                      CustomerRepository customerRepository) {
        this.movementRepository = movementRepository;
        this.productRepository = productRepository;
        this.stockRepository = stockRepository;
        this.storageRepository = storageRepository;
        this.vendorRepository = vendorRepository;
        this.customerRepository = customerRepository;
    }

    @Transactional
    public Movement createMovement(CreateMovementRequest req) {
        if (req == null) throw new BadRequestException("Request vacío");

        // Validaciones básicas según el tipo de movimiento
        if (req.tipoMovimiento == MovementKind.TRANSFERENCIA) {
            if (req.almacenOrigenId == null || req.almacenDestinoId == null) {
                throw new BadRequestException("Para TRANSFERENCIA se requiere almacenOrigen y almacenDestino");
            }
        }
        if (req.tipoMovimiento == MovementKind.SALDO_INICIAL) {
            if (req.almacenDestinoId == null) {
                throw new BadRequestException("Para SALDO_INICIAL se requiere almacenDestino");
            }
            if (req.almacenOrigenId != null) {
                throw new BadRequestException("Para SALDO_INICIAL, almacenOrigen debe ser null");
            }
        }

        // Cargar almacen(es)
        Storage almacenOrigen = null;
        Storage almacenDestino = null;
        if (req.almacenOrigenId != null) {
            almacenOrigen = storageRepository.findById(req.almacenOrigenId).orElseThrow(() -> new BadRequestException("Almacen origen no existe"));
        }
        if (req.almacenDestinoId != null) {
            almacenDestino = storageRepository.findById(req.almacenDestinoId).orElseThrow(() -> new BadRequestException("Almacen destino no existe"));
        }

        Movement m = new Movement();
        m.setFechaMovimiento(req.fechaMovimiento);
        m.setAlmacenOrigen(almacenOrigen);
        m.setAlmacenDestino(almacenDestino);
        m.setTipoMovimiento(req.tipoMovimiento);
        m.setTipoOperacion(req.tipoOperacion);
        m.setNumeroFactura(req.numeroFactura);
        m.setObservacion(req.observacion);
        m.setTotal(req.total);
        m.setDetalles(new ArrayList<>());

        // Guardar movement antes de detalles (cascade) o al final; guardo al final

        // Para cada detalle, actualizar stock según tipo (entrada +=, salida -=, transferencia: origen -=, destino +=)
        if (req.detalles != null) {
            for (CreateMovementRequest.CreateMovementDetail det : req.detalles) {
                Product p = productRepository.findById(det.productId).orElseThrow(() -> new BadRequestException("Producto no existe: " + det.productId));

                String lote = det.lote;
                java.math.BigDecimal cantidad = det.cantidad == null ? java.math.BigDecimal.ZERO : det.cantidad;

                if (req.tipoMovimiento == MovementKind.ENTRADA || req.tipoMovimiento == MovementKind.SALDO_INICIAL || req.tipoMovimiento == MovementKind.TRANSFERENCIA) {
                    // sumar a destino
                    if (almacenDestino == null) throw new BadRequestException("Almacen destino requerido para este tipo de movimiento");
                    Stock stock = stockRepository.findByAlmacenIdAndProductoIdAndLote(almacenDestino.getId(), p.getId(), lote).orElse(null);
                    if (stock == null) {
                        stock = new Stock();
                        stock.setAlmacen(almacenDestino);
                        stock.setProducto(p);
                        stock.setLote(lote);
                        stock.setCantidad(cantidad);
                    } else {
                        stock.setCantidad(stock.getCantidad().add(cantidad));
                    }
                    stockRepository.save(stock);
                }

                if (req.tipoMovimiento == MovementKind.SALIDA || req.tipoMovimiento == MovementKind.TRANSFERENCIA) {
                    // restar de origen
                    if (almacenOrigen == null) throw new BadRequestException("Almacen origen requerido para este tipo de movimiento");
                    if (Boolean.TRUE.equals(p.getIsLocked())) {
                        throw new BadRequestException("Producto bloqueado: " + p.getId());
                    }
                    Stock stock = stockRepository.findByAlmacenIdAndProductoIdAndLote(almacenOrigen.getId(), p.getId(), lote).orElseThrow(() -> new BadRequestException("Stock insuficiente en almacen origen"));
                    if (stock.getCantidad().compareTo(cantidad) < 0) {
                        throw new BadRequestException("Stock insuficiente para producto " + p.getId());
                    }
                    stock.setCantidad(stock.getCantidad().subtract(cantidad));
                    stockRepository.save(stock);
                }

                // crear MovementDetail y asociar
                MovementDetail md = new MovementDetail();
                md.setMovement(m);
                md.setProduct(p);
                md.setNombreComercial(det.nombreComercial);
                md.setCodigo(det.codigo);
                md.setLote(det.lote);
                md.setOrdenProduccion(det.ordenProduccion);
                md.setFechaIngreso(det.fechaIngreso);
                md.setFechaProduccion(det.fechaProduccion);
                md.setFechaVencimiento(det.fechaVencimiento);
                md.setPresentacion(det.presentacion);
                if (det.proveedorId != null) md.setProveedor(vendorRepository.findById(det.proveedorId).orElse(null));
                if (det.clienteDestinoId != null) md.setClienteDestino(customerRepository.findById(det.clienteDestinoId).orElse(null));
                md.setCantidad(det.cantidad);
                md.setTotal(det.total);

                m.getDetalles().add(md);
            }
        }

        movementRepository.save(m);
        return m;
    }
}
