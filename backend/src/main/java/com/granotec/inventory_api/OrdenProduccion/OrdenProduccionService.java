package com.granotec.inventory_api.OrdenProduccion;

import com.granotec.inventory_api.ConsumoProduccion.ConsumoProduccion;
import com.granotec.inventory_api.ConsumoProduccion.ConsumoProduccionRepository;
import com.granotec.inventory_api.Kardex.Kardex;
import com.granotec.inventory_api.Kardex.KardexRepository;
import com.granotec.inventory_api.Kardex.KardexService;
import com.granotec.inventory_api.Lote.Lote;
import com.granotec.inventory_api.Lote.LoteRepository;
import com.granotec.inventory_api.Lote.dto.EstadoLote;
import com.granotec.inventory_api.OrdenProduccion.ListaMaterial.ItemListaMaterial.ItemListaMaterial;
import com.granotec.inventory_api.OrdenProduccion.ListaMaterial.ListaMaterial;
import com.granotec.inventory_api.OrdenProduccion.ListaMaterial.ListaMaterialRepository;
import com.granotec.inventory_api.OrdenProduccion.MermaProduccion.MermaProduccion;
import com.granotec.inventory_api.OrdenProduccion.MermaProduccion.MermaProduccionRepository;
import com.granotec.inventory_api.OrdenProduccion.dto.*;
import com.granotec.inventory_api.StockLote.StockLote;
import com.granotec.inventory_api.StockLote.StockLoteRepository;
import com.granotec.inventory_api.Stock_Almacen.StockAlmacen;
import com.granotec.inventory_api.Stock_Almacen.StockAlmacenRepository;
import com.granotec.inventory_api.common.enums.*;
import com.granotec.inventory_api.common.exception.BadRequestException;
import com.granotec.inventory_api.product.Product;
import com.granotec.inventory_api.product.ProductRepository;
import com.granotec.inventory_api.storage.Storage;
import com.granotec.inventory_api.storage.StorageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrdenProduccionService {

    private final OrdenProduccionRepository ordenProduccionRepository;
    private final ProductRepository productoRepository;
    private final ListaMaterialRepository listaMaterialRepo;
    private final LoteRepository loteRepo;
    private final StockLoteRepository stockLoteRepo;
    private final ConsumoProduccionRepository consumoRepo;
    private final MermaProduccionRepository mermaRepo;
    private final StorageRepository almacenRepo;
    private final KardexRepository kardexRepo;
    private final KardexService kardeService;
    private final StockAlmacenRepository stockAlmacenRepo;

    //1. Crear op
    @Transactional
    public OrdenProduccionResponse crearOrdenProduccion(CrearOrdenProduccionRequest request) {
        Product producto = productoRepository.findById(request.getProductoId())
                .orElseThrow(() -> new BadRequestException("Producto no encontrado"));

        ListaMaterial lista = null;
        if(request.getListaMaterialId() != null){
            lista = listaMaterialRepo.findById(request.getListaMaterialId())
                    .orElseThrow(()-> new BadRequestException("Lista de material no encontrada"));
            if(lista.getItems().isEmpty()){
                throw new BadRequestException("Lista de materiales vacia");
            }
        }else{
            lista = listaMaterialRepo.findByProductoId(producto.getId()).orElse(null);
        }

        Storage almacenDestino = almacenRepo.findById(request.getAlmacenDestinoId())
                .orElseThrow(() -> new BadRequestException("Almacén destino no encontrado"));

        String numero = String.valueOf(LocalDate.now().getYear()) + "-" + request.getNumero();

        OrdenProduccion op = OrdenProduccion.builder()
                .numero(numero)
                .producto(producto)
                .cantidadProgramada(request.getCantidadProgramada())
                .cantidadProducida(BigDecimal.ZERO)
                .listaMaterial(lista)
                .almacenDestino(almacenDestino)
                .estado(ProduccionStatus.CREADA)
                .estadoLaboratorio(EstadoLaboratorio.PENDIENTE)
                .fechaCreacion(LocalDate.now())
                .fechaInicio(null)
                .costoEstimado(BigDecimal.ZERO)
                .costoReal(BigDecimal.ZERO)
                .build();

        ordenProduccionRepository.save(op);
        return toDto(op);
    }


    //2. Inciar Op
    @Transactional
    public OrdenProduccionResponse iniciarOrdenProduccion(IniciarOrdenRequest request) {
        OrdenProduccion op = ordenProduccionRepository.findById(request.getOrdenId())
                .orElseThrow(() -> new BadRequestException("Op no encontrada"));

        if (op.getEstado() != ProduccionStatus.CREADA) {
            throw new BadRequestException("Op no se puede iniciar en su estado actual" + op.getEstado());
        }

        if (op.getListaMaterial() == null || op.getListaMaterial().getItems().isEmpty()) {
            throw new BadRequestException("BOM no asignada o vacia");
        }

        //Calcular requerimientos por insumo
        Map<Integer,BigDecimal> requerimientos = op.getListaMaterial().getItems().stream().collect(
                Collectors.toMap(
                        i -> i.getInsumo().getId(),
                        i -> i.getCantidadPorUnidad().multiply(op.getCantidadProgramada()),
                        BigDecimal::add,
                        LinkedHashMap::new
                ));

        //Revisar stock disponible total por insumo
        Map<Integer,BigDecimal> disponiblesTotales = new HashMap<>();
        for(Integer insumoId : requerimientos.keySet() ){
            BigDecimal disponible = stockLoteRepo.sumDisponibleByProducto(insumoId);
            disponiblesTotales.put(insumoId, disponible != null ? disponible : BigDecimal.ZERO);
        }

        //Detectar faltantes

        List<InsumoFaltanteDTO> faltantesDTOS = new ArrayList<>();

        for(Map.Entry<Integer,BigDecimal> e : requerimientos.entrySet()){
            Integer insumoId = e.getKey();
            BigDecimal requerido = e.getValue();
            BigDecimal disponible = disponiblesTotales.getOrDefault(insumoId, BigDecimal.ZERO);

            if(disponible.compareTo(requerido) < 0) {

                BigDecimal faltante = requerido.subtract(disponible);
                Product insumo = productoRepository.findById(insumoId).orElse(null);

                List<StockDetalleDTO> detalles = stockLoteRepo.findAvailableByProductoForUpdate(insumoId)
                        .stream()
                        .map(s -> new StockDetalleDTO(
                                s.getAlmacen().getId(),
                                s.getAlmacen().getNombre(),
                                s.getLote().getId(),
                                s.getLote().getCodigoLote(),
                                s.getCantidadDisponible()
                        )).toList();

                faltantesDTOS.add(new InsumoFaltanteDTO(
                        insumoId,
                        insumo != null ? insumo.getNombreComercial() : null,
                        requerido,
                        disponible,
                        faltante,
                        detalles
                ));
            }
        }

        //Si hay faltantes lanzar execpcion
        if(!faltantesDTOS.isEmpty()){
            throw new BadRequestException("Stock insuficiente para iniciar OP", faltantesDTOS);
        }

        //Reservar stock por insumo
        for (ItemListaMaterial item : op.getListaMaterial().getItems()) {
            BigDecimal requerido = item.getCantidadPorUnidad().multiply(op.getCantidadProgramada());
            BigDecimal restante = requerido;

            if(request.getAlmacenesPrioritarios() != null && !request.getAlmacenesPrioritarios().isEmpty()){
                for(Long almacenId: request.getAlmacenesPrioritarios()){
                    if(restante.compareTo(BigDecimal.ZERO) <= 0) break;
                    List<StockLote> sList = stockLoteRepo.findAvailableByProductoAndAlmacenForUpdate(item.getInsumo().getId(),almacenId);
                    for(StockLote s : sList){
                        if(restante.compareTo(BigDecimal.ZERO) <= 0) break;
                        BigDecimal take = s.getCantidadDisponible().min(restante);
                        reservarSobreStockLote(s,take,op,item.getInsumo());
                        restante = restante.subtract(take);
                    }
                }
            }
            if (restante.compareTo(BigDecimal.ZERO) > 0) {
                // completar desde cualquier almacen
                List<StockLote> stocks = stockLoteRepo.findAvailableByProductoForUpdate(item.getInsumo().getId());
                for (StockLote s : stocks) {
                    if (restante.compareTo(BigDecimal.ZERO) <= 0) break;
                    BigDecimal take = s.getCantidadDisponible().min(restante);
                    reservarSobreStockLote(s, take, op, item.getInsumo());
                    restante = restante.subtract(take);
                }
            }
        }


        op.setEstado(ProduccionStatus.IN_PRODUCTION);
        op.setFechaInicio(request.getFechaInicio() != null ? request.getFechaInicio() : LocalDate.now());

        ordenProduccionRepository.save(op);

        return toDto(op);
    }

    //Helper: reserva sobre stock lote
    private void reservarSobreStockLote(StockLote s, BigDecimal cantidad, OrdenProduccion op, Product insumo) {
        if (cantidad.compareTo(BigDecimal.ZERO) <= 0) return;

        //restar disponible y aumentar reservada
        s.setCantidadDisponible(s.getCantidadDisponible().subtract(cantidad));
        s.setCantidadReservada(s.getCantidadReservada().add(cantidad));
        stockLoteRepo.save(s);

        ConsumoProduccion consumo = ConsumoProduccion.builder()
                .ordenProduccion(op)
                .insumo(insumo)
                .stockLoteOrigen(s)
                .almacenOrigen(s.getAlmacen())
                .cantidadUsada(cantidad)
                .fechaConsumo(LocalDateTime.now())
                .costoUnitario(s.getLote().getCostoUnitario() != null ? s.getLote().getCostoUnitario() : BigDecimal.ZERO)
                .costoTotal((s.getLote().getCostoUnitario() != null ? s.getLote().getCostoUnitario() : BigDecimal.ZERO).multiply(cantidad))
                .estadoConsumo(EstadoConsumo.RESERVADO)
                .build();

        consumoRepo.save(consumo);
        op.getConsumos().add(consumo);
    }

    //3. Registrar consumos adicionales durante produccion
    @Transactional
    public ConsumoProduccion registrarConsumo(RegistrarConsumoRequest request) {
        OrdenProduccion op = ordenProduccionRepository.findById(request.getOrdenId())
                .orElseThrow(() -> new BadRequestException("Op no encontrada"));

        if (op.getEstado() != ProduccionStatus.IN_PRODUCTION) {
            throw new BadRequestException("Op no está en producción");
        }

        Product insumo = productoRepository.findById(request.getInsumoId())
                .orElseThrow(() -> new BadRequestException("Insumo no encontrado"));

        BigDecimal cantidad = request.getCantidad();
        if (cantidad.compareTo(BigDecimal.ZERO) <= 0) throw new BadRequestException("Cantidad debe ser mayor a cero");

        if (request.getStockLoteOrigenId() != null) {
            StockLote s = stockLoteRepo.findById(request.getStockLoteOrigenId())
                    .orElseThrow(() -> new BadRequestException("Stock Lote no encontrado"));
            if (s.getCantidadReservada().compareTo(cantidad) >= 0) {
                s.setCantidadReservada(s.getCantidadReservada().subtract(cantidad));
            } else {
                BigDecimal fromReservada = s.getCantidadReservada();
                if (fromReservada.compareTo(BigDecimal.ZERO) > 0) {
                    s.setCantidadReservada(BigDecimal.ZERO);
                    BigDecimal restante = cantidad.subtract(fromReservada);
                    s.setCantidadDisponible(s.getCantidadDisponible().subtract(restante));
                } else {
                    s.setCantidadDisponible(s.getCantidadDisponible().subtract(cantidad));
                }
            }
            stockLoteRepo.save(s);

            ConsumoProduccion consumo = ConsumoProduccion.builder()
                    .ordenProduccion(op)
                    .insumo(insumo)
                    .stockLoteOrigen(s)
                    .almacenOrigen(s.getAlmacen())
                    .cantidadUsada(cantidad)
                    .fechaConsumo(request.getFechaConsumo() != null ? request.getFechaConsumo() : LocalDateTime.now())
                    .costoUnitario(s.getLote().getCostoUnitario() != null ? s.getLote().getCostoUnitario() : BigDecimal.ZERO)
                    .costoTotal((s.getLote().getCostoUnitario() != null ? s.getLote().getCostoUnitario() : BigDecimal.ZERO).multiply(cantidad))
                    .estadoConsumo(EstadoConsumo.CONSUMIDO)
                    .build();
            consumoRepo.save(consumo);

            registrarKardexSalida(insumo, s.getLote(), s.getAlmacen(), cantidad, op.getNumero());
            op.setCostoReal(op.getCostoReal() == null ? consumo.getCostoTotal() : op.getCostoReal().add(consumo.getCostoTotal()));
            op.setCostoEstimado(op.getCostoEstimado() == null ? op.getCostoReal() : op.getCostoEstimado().add(consumo.getCostoTotal()));
            ordenProduccionRepository.save(op);
            return consumo;
        } else {
            List<StockLote> stocks = stockLoteRepo.findAvailableByProductoForUpdate(insumo.getId());
            BigDecimal restante = cantidad;
            List<ConsumoProduccion> consumosHechos = new ArrayList<>();
            for (StockLote s : stocks) {
                if (restante.compareTo(BigDecimal.ZERO) <= 0) break;
                BigDecimal take = s.getCantidadDisponible().min(restante);
                if (s.getCantidadReservada().compareTo(take) >= 0) {
                    s.setCantidadReservada(s.getCantidadReservada().subtract(take));
                } else {
                    BigDecimal fromReservada = s.getCantidadReservada();
                    if (fromReservada.compareTo(BigDecimal.ZERO) > 0) {
                        s.setCantidadReservada(BigDecimal.ZERO);
                        BigDecimal restanteLote = take.subtract(fromReservada);
                        s.setCantidadDisponible(s.getCantidadDisponible().subtract(restanteLote));
                    } else {
                        s.setCantidadDisponible(s.getCantidadDisponible().subtract(take));
                    }
                }
                stockLoteRepo.save(s);

                ConsumoProduccion consumo = ConsumoProduccion.builder()
                        .ordenProduccion(op)
                        .insumo(insumo)
                        .stockLoteOrigen(s)
                        .almacenOrigen(s.getAlmacen())
                        .cantidadUsada(take)
                        .fechaConsumo(request.getFechaConsumo() != null ? request.getFechaConsumo() : LocalDateTime.now())
                        .costoUnitario(s.getLote().getCostoUnitario() != null ? s.getLote().getCostoUnitario() : BigDecimal.ZERO)
                        .costoTotal((s.getLote().getCostoUnitario() != null ? s.getLote().getCostoUnitario() : BigDecimal.ZERO).multiply(take))
                        .estadoConsumo(EstadoConsumo.CONSUMIDO)
                        .build();
                consumoRepo.save(consumo);
                registrarKardexSalida(insumo, s.getLote(), s.getAlmacen(), take, op.getNumero());
                op.setCostoReal(op.getCostoReal() == null ? consumo.getCostoTotal() : op.getCostoReal().add(consumo.getCostoTotal()));
                op.setCostoEstimado(op.getCostoEstimado() == null ? op.getCostoReal() : op.getCostoEstimado().add(consumo.getCostoTotal()));
                restante = restante.subtract(take);
                consumosHechos.add(consumo);
            }
            if (restante.compareTo(BigDecimal.ZERO) > 0) {
                throw new BadRequestException("Stock insuficiente para insumo: " + insumo.getNombreComercial());
            }
            ordenProduccionRepository.save(op);
            return consumosHechos.isEmpty() ? null : consumosHechos.get(consumosHechos.size()-1);
        }
    }

    private void registrarKardexSalida(Product producto, Lote lote, Storage almacen, BigDecimal cantidad, String observacion) {
        BigDecimal stockAnterior = computeStockDisponible(producto, almacen);
        BigDecimal stockActual = stockAnterior.subtract(cantidad);
        Kardex kardex = Kardex.builder()
                .producto(producto)
                .lote(lote)
                .tipoMovimiento(TipoMovimiento.SALIDA)
                .tipoOperacion(TypeOperation.PRODUCCION)
                .cantidad(cantidad)
                .stockAnterior(stockAnterior)
                .stockActual(stockActual)
                .fechaMovimiento(LocalDate.now())
                .observacion("Salida por OP: " + observacion)
                .build();
        kardexRepo.save(kardex);

    }


    //4. Aprobar/rechazar laboratorio (antes de cerrar op)
    @Transactional
    public OrdenProduccionResponse aprobarCalidadLaboratorio(AprobarLaboratorioRequest request) {
        OrdenProduccion op = ordenProduccionRepository.findById(request.getOrdenId())
                .orElseThrow(() -> new BadRequestException("Op no encontrada"));

        if (request.isAprobado()) {
            op.setEstadoLaboratorio(EstadoLaboratorio.APROBADO);
            //op.setEstado(ProduccionStatus.QUALITY_CONTROL);
        } else {
            op.setEstadoLaboratorio(EstadoLaboratorio.RECHAZADO);
            op.setEstado(ProduccionStatus.TERMINADA);

            BigDecimal programada = op.getCantidadProgramada() == null ? BigDecimal.ZERO : op.getCantidadProgramada();
            BigDecimal costoReal = op.getCostoReal() == null ? BigDecimal.ZERO : op.getCostoReal();
            MermaProduccion merma = MermaProduccion.builder()
                    .ordenProduccion(op)
                    .cantidadProgramada(programada)
                    .cantidadProducida(BigDecimal.ZERO)
                    .cantidadMerma(programada)
                    .costoMerma(costoReal)
                    .fechaRegistro(LocalDate.now())
                    .build();
            mermaRepo.save(merma);
        }

        ordenProduccionRepository.save(op);
        return toDto(op);
    }

    //5. Cerrar op
    @Transactional
    public OrdenProduccionResponse cerrarOrden(CerrarOrdenRequest request) {
        OrdenProduccion op = ordenProduccionRepository.findById(request.getOrdenId())
                .orElseThrow(() -> new BadRequestException("Op no encontrada"));

        if (op.getEstado() != ProduccionStatus.IN_PRODUCTION && op.getEstadoLaboratorio() != EstadoLaboratorio.APROBADO) {
            throw new BadRequestException("OP no puede cerrarse: debe estar en proceso y aprobado por laboratorio");
        }

        Storage almacenDestino;
        if(request.getAlmacenDestinoId() != null){
            almacenDestino = almacenRepo.findById(request.getAlmacenDestinoId())
                    .orElseThrow(()-> new BadRequestException("Almacén destino no encontrado"));
            op.setAlmacenDestino(almacenDestino);
        }else{
            almacenDestino = op.getAlmacenDestino();
            if(almacenDestino != null){
                almacenDestino = almacenRepo.findById(almacenDestino.getId())
                        .orElseThrow(()-> new BadRequestException("Almacén destino no encontrado1"));
            }
        }
        if(almacenDestino == null) throw new BadRequestException("La OP no tiene un almacén destino definido");

        BigDecimal producida = request.getCantidadProducida() != null ? request.getCantidadProducida() : BigDecimal.ZERO;
        BigDecimal programada = op.getCantidadProgramada() == null ? BigDecimal.ZERO : op.getCantidadProgramada();
        BigDecimal mermaCant = programada.subtract(producida).max(BigDecimal.ZERO);

        BigDecimal costoReal = op.getCostoReal() == null ? BigDecimal.ZERO : op.getCostoReal();

        BigDecimal costoUnitario = BigDecimal.ZERO;
        if (producida.compareTo(BigDecimal.ZERO) > 0) {
            if(request.getCostoUnitarioFinal() != null){
                costoUnitario = request.getCostoUnitarioFinal();
            }else{
                costoUnitario = costoReal.divide(producida, 6, RoundingMode.HALF_UP);
            }
        }

        String codigo = request.getCodigoLote();
        Lote loteProd = Lote.builder()
                .ordenProduccion(op)
                .producto(op.getProducto())
                .codigoLote(codigo)
                .fechaProduccion(LocalDate.now())
                .cantidadProducida(producida)
                .costoTotal(costoReal)
                .costoUnitario(costoUnitario)
                .precioVentaUnitario(request.getPrecioVentaUnitario() != null ? request.getPrecioVentaUnitario() : BigDecimal.ZERO)
                .estado(EstadoLote.DISPONIBLE.name())
                .build();
        loteRepo.save(loteProd);

            StockLote stockProd = StockLote.builder()
                    .lote(loteProd)
                    .almacen(almacenDestino)
                    .cantidadDisponible(producida)
                    .cantidadReservada(BigDecimal.ZERO)
                    .build();
            stockLoteRepo.save(stockProd);

        actualizarStockAlmacen(almacenDestino, op.getProducto(), producida);
        kardeService.registrarEntradaProduccion(op.getProducto(), loteProd, almacenDestino, producida, "Ingreso producto terminado OP " + op.getNumero());

            //En caso de merma
            if(mermaCant.compareTo(BigDecimal.ZERO) > 0){
                BigDecimal costoMerma = BigDecimal.ZERO;
                if(programada.compareTo(BigDecimal.ZERO) > 0){
                    custoSafe:
                    try{
                        costoMerma = costoReal.multiply(mermaCant).divide(programada,6, RoundingMode.HALF_UP);
                    }catch (ArithmeticException ex){
                        costoMerma = BigDecimal.ZERO;
                    }
                }

                MermaProduccion merma = MermaProduccion.builder()
                        .ordenProduccion(op)
                        .cantidadProgramada(programada)
                        .cantidadProducida(producida)
                        .cantidadMerma(mermaCant)
                        .costoMerma(costoMerma)
                        .fechaRegistro(LocalDate.now())
                        .build();

                mermaRepo.save(merma);

                Storage almacenMerma = almacenRepo.findAllByIsDeletedFalse().stream()
                        .filter(a -> "MERMAS".equalsIgnoreCase(a.getNombre()))
                        .findFirst()
                        .orElseGet(()-> {
                            Storage m = new Storage();
                            m.setNombre("MERMAS");
                            return almacenRepo.save(m);
                        });

                Lote loteMerma = Lote.builder()
                        .ordenProduccion(op)
                        .producto(op.getProducto())
                        .codigoLote("MERMA-" + codigo)
                        .fechaProduccion(LocalDate.now())
                        .cantidadProducida(mermaCant)
                        .costoTotal(costoMerma)
                        .costoUnitario(producida.compareTo(BigDecimal.ZERO) > 0 ? custoSafeDivide(costoReal, programada) : BigDecimal.ZERO)
                        .estado(EstadoLote.MERMA.name())
                        .build();

                loteMerma = loteRepo.save(loteMerma);

                StockLote stockMerma = StockLote.builder()
                        .lote(loteMerma)
                        .almacen(almacenMerma)
                        .cantidadDisponible(mermaCant)
                        .cantidadReservada(BigDecimal.ZERO)
                        .build();

                stockLoteRepo.save(stockMerma);
                actualizarStockAlmacen(almacenMerma, op.getProducto(), mermaCant);
                kardeService.registrarEntradaProduccion(op.getProducto(), loteMerma, almacenMerma, mermaCant, "Merma OP " + op.getNumero());

            }


        List<ConsumoProduccion> reservados = op.getConsumos().stream()
                .filter(c -> c.getEstadoConsumo() == EstadoConsumo.RESERVADO)
                .collect(Collectors.toList());

        for (ConsumoProduccion c : reservados) {
            log.info("➡ Procesando consumo: Insumo={} Cant={} AlmacenOrigen={}",
                    c.getInsumo().getNombreComercial(),
                    c.getCantidadUsada(),
                    c.getAlmacenOrigen() != null ? c.getAlmacenOrigen().getId() : null);

            c.setEstadoConsumo(EstadoConsumo.CONSUMIDO);
            consumoRepo.save(c);

            if (c.getAlmacenOrigen() == null) {
                throw new BadRequestException("Un consumo no tiene almacén de origen definido, revisar insumo: "
                        + c.getInsumo().getNombreComercial());
            }

            actualizarStockAlmacen(c.getAlmacenOrigen(),c.getInsumo(),c.getCantidadUsada());
            kardeService.registrarSalidaProduccion(c.getInsumo(), c.getStockLoteOrigen().getLote(), c.getAlmacenOrigen(), c.getCantidadUsada(), "Consumo OP " + op.getNumero());
        }

        op.setCantidadProducida(producida);
        op.setEstado(ProduccionStatus.TERMINADA);
        op.setFechaFin(LocalDate.now());
        op.getLote().add(loteProd);
        ordenProduccionRepository.save(op);

        return toDto(op);
    }


    private BigDecimal custoSafeDivide(BigDecimal a, BigDecimal b){
         if(b == null || b.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;
         return a.divide(b,6,RoundingMode.HALF_UP);
    }

    @Transactional(readOnly = true)
    public OrdenProduccionResponse obtenerOrden(Integer id) {
        OrdenProduccion op = ordenProduccionRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Op no encontrada"));
        return toDto(op);
    }

    @Transactional(readOnly = true)
    public List<OrdenProduccionResponse> listarTodos() {
        return ordenProduccionRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    private BigDecimal computeStockDisponible(Product producto, Storage almacen) {
        BigDecimal s = stockLoteRepo.sumDisponibleByProductoAndAlmacen(producto.getId(), almacen.getId());
        return s == null ? BigDecimal.ZERO : s;
    }

    private OrdenProduccionResponse toDto (OrdenProduccion op){
        OrdenProduccionResponse opr = new OrdenProduccionResponse();
        opr.setId(op.getId());
        opr.setNumero(op.getNumero());
        opr.setProductoId(op.getProducto().getId());
        opr.setProductoNombre(op.getProducto().getNombreComercial());
        opr.setCantidadProgramada(op.getCantidadProgramada());
        opr.setCantidadProducida(op.getCantidadProducida());
        opr.setEstado(op.getEstado());
        opr.setEstadoLaboratorio(op.getEstadoLaboratorio());
        opr.setFechaCreacion(op.getFechaCreacion());
        opr.setFechaInicio(op.getFechaInicio());
        opr.setFechaFin(op.getFechaFin());
        opr.setCostoEstimado(op.getCostoEstimado());
        opr.setCostoReal(op.getCostoReal());

        if(op.getConsumos() != null){
            opr.setConsumos(op.getConsumos().stream().map(c -> {
                ConsumoProduccionDTO cd = new ConsumoProduccionDTO();
                cd.setId(c.getId());
                cd.setInsumoId(c.getInsumo().getId());
                cd.setInsumoNombre(c.getInsumo().getNombreComercial());
                cd.setStockLoteOrigenId(c.getStockLoteOrigen() != null ? c.getStockLoteOrigen().getId() : null);
                cd.setAlmacenOrigenId(c.getAlmacenOrigen() != null ? c.getAlmacenOrigen().getId() : null);
                cd.setCantidad(c.getCantidadUsada());
                cd.setCostoUnitario(c.getCostoUnitario());
                cd.setCostoTotal(c.getCostoTotal());
                cd.setEstado(c.getEstadoConsumo());
                cd.setFecha(c.getFechaConsumo());

                return cd;
            }).collect(Collectors.toList()));
        }

        if(op.getLote() != null){
            opr.setLotes(op.getLote().stream().map(l -> {
                LoteDTO ld = new LoteDTO();
                ld.setId(l.getId());
                ld.setCodigoLote(l.getCodigoLote());
                ld.setCantidadProducida(l.getCantidadProducida());
                ld.setCostoUnitario(l.getCostoUnitario());
                ld.setCostoTotal(l.getCostoTotal());
                ld.setEstado(l.getEstado());
                return ld;
            }).collect(Collectors.toList()));
        }
        return  opr;
    }

    private void actualizarStockAlmacen(Storage almacen, Product producto, BigDecimal cambio) {
        StockAlmacen stock = stockAlmacenRepo
                .findByAlmacenAndProducto(almacen, producto)
                .orElseGet(() -> StockAlmacen.builder()
                        .almacen(almacen)
                        .producto(producto)
                        .cantidad(BigDecimal.ZERO)
                        .build()
                );

        stock.setCantidad(stock.getCantidad().add(cambio));
        stockAlmacenRepo.save(stock);
    }


}
