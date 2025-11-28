package com.granotec.inventory_api.Kardex;

import com.granotec.inventory_api.Lote.Lote;
import com.granotec.inventory_api.Lote.LoteRepository;
import com.granotec.inventory_api.OrdenProduccion.OrdenProduccion;
import com.granotec.inventory_api.OrdenProduccion.OrdenProduccionRepository;
import com.granotec.inventory_api.StockLote.StockLoteRepository;
import com.granotec.inventory_api.Stock_Almacen.StockAlmacen;
import com.granotec.inventory_api.Stock_Almacen.StockAlmacenRepository;
import com.granotec.inventory_api.common.enums.TipoMovimiento;
import com.granotec.inventory_api.common.enums.TypeOperation;
import com.granotec.inventory_api.common.exception.BadRequestException;
import com.granotec.inventory_api.product.Product;
import com.granotec.inventory_api.product.ProductRepository;
import com.granotec.inventory_api.storage.Storage;
import com.granotec.inventory_api.storage.StorageRepository;
import com.granotec.inventory_api.user.User;
import com.granotec.inventory_api.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class KardexService {

    private final KardexRepository kardexRepo;
    private final ProductRepository productRepo;
    private final LoteRepository loteRepo;
    private final StorageRepository almacenRepo;
    private final OrdenProduccionRepository ordenRepo;
    private final UserRepository userRepo;
    private final StockLoteRepository stockLoteRepo;
    private final StockAlmacenRepository stockAlmacenRepo;
    private final KardexRepository kardexRepository;

    // escala y precisión recomendadas
    private static final int SCALE_CANTIDAD = 3;
    private static final int SCALE_COSTO = 6;
    private static final int SCALE_TOTAL = 3;


    @Transactional
    public KardexResponse registrarMovimientos(KardexRequest request){

        Storage almacen = almacenRepo.findById(request.getAlmacenId())
                .orElseThrow(()-> new BadRequestException("Almacen no encontrado"));

        Product producto = productRepo.findById(request.getProductoId())
                .orElseThrow(()-> new BadRequestException("Producto no encontrado"));

        Lote lote = null;
        if(request.getLoteId() != null){
            lote = loteRepo.findById(request.getLoteId())
                    .orElseThrow(()-> new BadRequestException("Lote no encontrado"));
        }

        User usuario = null;
        if(request.getUsuarioId() != null){
            usuario = userRepo.findById(request.getUsuarioId()).orElse(null);
        }

        //Calcular costo unitario
        BigDecimal costoUnitarioSoles = request.getCostoUnitarioSoles();
        if(costoUnitarioSoles == null){
            if(lote != null && lote.getCostoUnitario() != null){
                costoUnitarioSoles = lote.getCostoUnitario();
            }else{
                if(request.getNumeroOp() != null){
                    OrdenProduccion op = ordenRepo.findByNumero(request.getNumeroOp()).orElse(null);

                    if(op != null && op.getCostoReal() != null && op.getCantidadProducida() != null && op.getCantidadProducida().compareTo(BigDecimal.ZERO) > 0){
                        costoUnitarioSoles = op.getCostoReal().divide(op.getCantidadProducida(), SCALE_COSTO, RoundingMode.HALF_UP);
                    }
                }
            }
        }

        if(costoUnitarioSoles == null){
            throw new BadRequestException("No se pudo determinar el costo unitario en soles");
        }

        BigDecimal cantidad = request.getCantidad().setScale(SCALE_CANTIDAD, RoundingMode.HALF_UP);
        BigDecimal totalSoles = costoUnitarioSoles.multiply(cantidad).setScale(SCALE_TOTAL, RoundingMode.HALF_UP);
        BigDecimal costoUnitarioUSD = null;
        BigDecimal totalUSD = null;

        if(request.getTasaCambio() != null && request.getTasaCambio().compareTo(BigDecimal.ZERO) > 0){
            costoUnitarioUSD  = costoUnitarioSoles.divide(request.getTasaCambio(), SCALE_COSTO, RoundingMode.HALF_UP);
            totalUSD = costoUnitarioUSD.multiply(cantidad).setScale(SCALE_TOTAL, RoundingMode.HALF_UP);
        }

        BigDecimal stockAnterior = stockAlmacenRepo.findByAlmacenAndProducto(almacen,producto)
                .map(StockAlmacen::getCantidad)
                .orElse(BigDecimal.ZERO);

        BigDecimal stockActual = stockAnterior.add(cantidad);

        Kardex k = Kardex.builder()
                .fechaMovimiento(LocalDate.now())
                .almacen(almacen)
                .tipoMovimiento(request.getTipoMovimiento())
                .tipoOperacion(request.getTipoOperacion())
                .referencia(request.getReferencia())
                .producto(producto)
                .lote(lote)
                .OP(request.getNumeroOp())
                .cantidad(cantidad)
                .costoUnitarioSoles(costoUnitarioSoles.setScale(SCALE_COSTO, RoundingMode.HALF_UP))
                .totalSoles(totalSoles)
                .costoUnitarioDolares(costoUnitarioUSD)
                .totalDolares(totalUSD)
                .stockAnterior(stockAnterior.setScale(SCALE_CANTIDAD, RoundingMode.HALF_UP))
                .stockActual(stockActual.setScale(SCALE_CANTIDAD, RoundingMode.HALF_UP))
                .observacion(request.getObservacion())
                .usuario(usuario)
                .build();

        k = kardexRepository.save(k);

        actualizarStockAlmacen(almacen, producto, cantidad, request.getTipoMovimiento());

        if(lote != null){
            stockLoteRepo.findFirstByLoteIdAndIsDeletedFalse(Long.valueOf(lote.getId())).ifPresent(stockLote -> {
                stockLote.setCantidadDisponible(stockLote.getCantidadDisponible().add(cantidad));
                stockLoteRepo.save(stockLote);
            });
        }

        return buildKardexResponse(k);
    }


    private void actualizarStockAlmacen(Storage almacen, Product producto, BigDecimal delta, TipoMovimiento tipoMovimiento) {
        // delta es la cantidad (puede ser negativa)
        StockAlmacen stock = stockAlmacenRepo.findByAlmacenAndProducto(almacen, producto)
                .orElseGet(() -> StockAlmacen.builder()
                        .almacen(almacen)
                        .producto(producto)
                        .cantidad(BigDecimal.ZERO)
                        .build()
                );

        BigDecimal nueva = stock.getCantidad().add(delta);
        if (nueva.compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException("Stock insuficiente para producto " + producto.getNombreComercial());
        }
        stock.setCantidad(nueva);
        stockAlmacenRepo.save(stock);
    }

    private KardexResponse buildKardexResponse(Kardex k){
        KardexResponse r = new KardexResponse();
        r.setId(k.getId());
        r.setFechaMovimiento(k.getFechaMovimiento());
        r.setAlmacenId(k.getAlmacen().getId());
        r.setAlmacenNombre(k.getAlmacen().getNombre());
        r.setTipoMovimiento(k.getTipoMovimiento());
        r.setTipoOperacion(k.getTipoOperacion());
        r.setReferencia(k.getReferencia());
        r.setProductoId(k.getProducto().getId());
        r.setProductoCodigo(k.getProducto().getCode());
        r.setProductoNombre(k.getProducto().getNombreComercial());
        r.setFamiliaProducto(k.getProducto().getFamilia() != null ? k.getProducto().getFamilia() : null);
        r.setTipoProducto(k.getProducto().getTipoProducto());
        if (k.getLote() != null) {
            r.setLoteId(k.getLote().getId());
            r.setLoteCodigo(k.getLote().getCodigoLote());
            r.setFechaProduccion(k.getLote().getFechaProduccion());
            // si tienes campo fechaVencimiento en Lote
            // r.setFechaVencimiento(k.getLote().getFechaVencimiento());
            // setea costo desde lote si quieres
        }
        r.setNumeroOp(k.getOP());
        if (k.getOP() != null) {
            ordenRepo.findByNumero(k.getOP()).ifPresent(op -> r.setFechaIngresoOp(op.getFechaInicio()));
        }
        r.setPresentacion(k.getProducto().getTipoPresentacion());
        r.setProveedor(k.getProducto().getProveedor() != null ? k.getProducto().getProveedor().getRazonSocial() : null);
        r.setDestinoCliente(null);
        r.setCantidad(k.getCantidad().setScale(SCALE_CANTIDAD, RoundingMode.HALF_UP));
        r.setCostoUnitarioSoles(k.getCostoUnitarioSoles());
        r.setTotalSoles(k.getTotalSoles());
        r.setCostoUnitarioDolares(k.getCostoUnitarioDolares());
        r.setTotalDolares(k.getTotalDolares());
        r.setStockAnterior(k.getStockAnterior());
        r.setStockActual(k.getStockActual());
        r.setObservacion(k.getObservacion());
        if (k.getUsuario() != null) {
            r.setUsuarioId(k.getUsuario().getId());
            r.setUsuarioNombre(k.getUsuario().getName());
        }
        return r;
    }


    public Page<KardexResponse> searchKardex(Integer productoId, Long almacenId, LocalDate desde, LocalDate hasta, Pageable pageable) {
        Page<Kardex> page = kardexRepo.search(productoId, almacenId, desde, hasta, pageable);
        return page.map(this::buildKardexResponse);
    }


    public void registrarSalidaProduccion(Product producto, Lote lote, Storage almacen, BigDecimal cantidad, String obs)    {
        BigDecimal stockAnterior = BigDecimal.ZERO;
        BigDecimal stockActual = stockAnterior.subtract(cantidad);
        Kardex k = Kardex.builder()
                .producto(producto)
                .lote(lote)
                .tipoMovimiento(TipoMovimiento.SALIDA)
                .tipoOperacion(TypeOperation.PRODUCCION)
                .cantidad(cantidad)
                .almacen(almacen)
                .stockAnterior(stockAnterior)
                .stockActual(stockActual)
                .fechaMovimiento(LocalDate.now())
                .observacion(obs)
                .build();
        kardexRepo.save(k);
    }

    public void registrarEntradaProduccion(Product producto, Lote lote, Storage almacen, BigDecimal cantidad, String obs){
        BigDecimal stockAnterior = BigDecimal.ZERO;
        BigDecimal stockActual = stockAnterior.add(cantidad);
        Kardex k = Kardex.builder()
                .producto(producto)
                .lote(lote)
                .tipoMovimiento(TipoMovimiento.ENTRADA)
                .tipoOperacion(TypeOperation.PRODUCCION)
                .cantidad(cantidad)
                .almacen(almacen)
                .stockAnterior(stockAnterior)
                .stockActual(stockActual)
                .fechaMovimiento(LocalDate.now())
                .observacion(obs)
                .build();
        kardexRepo.save(k);
    }


}
