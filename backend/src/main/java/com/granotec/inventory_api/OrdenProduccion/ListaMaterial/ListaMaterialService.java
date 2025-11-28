package com.granotec.inventory_api.OrdenProduccion.ListaMaterial;

import com.granotec.inventory_api.OrdenProduccion.ListaMaterial.ItemListaMaterial.ItemListaMaterial;
import com.granotec.inventory_api.OrdenProduccion.ListaMaterial.ItemListaMaterial.ItemListaMaterialRepository;
import com.granotec.inventory_api.OrdenProduccion.ListaMaterial.dto.ActualizarListaMaterialRequest;
import com.granotec.inventory_api.OrdenProduccion.ListaMaterial.dto.CrearListaMaterialRequest;
import com.granotec.inventory_api.OrdenProduccion.ListaMaterial.dto.ListaMaterialResponse;
import com.granotec.inventory_api.common.exception.BadRequestException;
import com.granotec.inventory_api.product.Product;
import com.granotec.inventory_api.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ListaMaterialService {

    private final ListaMaterialRepository listaRepo;
    private final ItemListaMaterialRepository itemRepo;
    private final ProductRepository productRepo;

    @Transactional
    public ListaMaterialResponse crearLista(CrearListaMaterialRequest request){
        Product producto = productRepo.findById(request.getProductoId())
                .orElseThrow(() -> new BadRequestException("Producto no encontrado"));

        if(producto.getTipoProducto() == null || !producto.getTipoProducto().name().equalsIgnoreCase("PRODUCTO_TERMINADO")){
            throw new BadRequestException("El producto debe ser de tipo PRODUCTO TERMINADO");
        }

        ListaMaterial lista = ListaMaterial.builder()
                .producto(producto)
                .version(request.getVersion())
                .build();

        if(request.getItems() != null){
            for(CrearListaMaterialRequest.ItemMaterialRequest ir : request.getItems()){
                Product insumo = productRepo.findById(ir.getInsumoId())
                        .orElseThrow(()-> new BadRequestException("Insumo no encontrado id=" + ir.getInsumoId()));
                if(insumo.getTipoProducto() == null || !insumo.getTipoProducto().name().equalsIgnoreCase("INSUMO")){
                    throw new BadRequestException("El producto id=" + ir.getInsumoId() + " no es INSUMO");
                }

                ItemListaMaterial item = ItemListaMaterial.builder()
                        .listaMaterial(lista)
                        .insumo(insumo)
                        .cantidadPorUnidad(ir.getCantidadPorUnidad())
                        .build();
                lista.getItems().add(item);
            }
        }

        lista = listaRepo.save(lista);
        return toResponse(lista);
    }

    @Transactional(readOnly = true)
    public ListaMaterialResponse obtenerPorId(Integer id){
        ListaMaterial lista = listaRepo.findById(id)
                .orElseThrow(() -> new BadRequestException("Lista Material no encontrada"));
        return toResponse(lista);
    }

    @Transactional(readOnly = true)
    public ListaMaterialResponse obtenerPorProducto(Integer productoId) {
        ListaMaterial lista = listaRepo.findByProductoId(productoId)
                .orElseThrow(() -> new BadRequestException("No existe BOM para este producto"));
        return toResponse(lista);
    }

    @Transactional
    public ListaMaterialResponse actualizar(Integer id, ActualizarListaMaterialRequest request){
        ListaMaterial lista = listaRepo.findById(id)
                .orElseThrow(() -> new BadRequestException("Lista Material no encontrada"));

        if(request.getVersion() != null) lista.setVersion(request.getVersion());

        lista.getItems().clear();

        if(request.getItems() != null){
            for(CrearListaMaterialRequest.ItemMaterialRequest ir : request.getItems()){
                Product insumo = productRepo.findById(ir.getInsumoId())
                        .orElseThrow(()-> new BadRequestException("Insumo no encontrado id=" + ir.getInsumoId()));
                if(insumo.getTipoProducto() == null || !insumo.getTipoProducto().name().equalsIgnoreCase("INSUMO")){
                    throw new BadRequestException("El producto id=" + ir.getInsumoId() + "no es INSUMO");
                }

                ItemListaMaterial item = ItemListaMaterial.builder()
                        .listaMaterial(lista)
                        .insumo(insumo)
                        .cantidadPorUnidad(ir.getCantidadPorUnidad())
                        .build();
                lista.getItems().add(item);
            }
        }

        lista = listaRepo.save(lista);
        return toResponse(lista);
    }

    @Transactional
    public void eliminar(Integer id){
        ListaMaterial lista = listaRepo.findById(id)
                .orElseThrow(() -> new BadRequestException("ListaMaterial no encontrada"));
        lista.softDelete();
        listaRepo.save(lista);
    }

    private ListaMaterialResponse toResponse(ListaMaterial lista){
        ListaMaterialResponse r = new ListaMaterialResponse();
        r.setId(lista.getId());
        r.setProductoId(lista.getProducto().getId());
        r.setProductoNombre(lista.getProducto().getNombreComercial());
        r.setVersion(lista.getVersion());
        List<ListaMaterialResponse.ItemListaResponse> items = lista.getItems().stream().map(it -> {
            ListaMaterialResponse.ItemListaResponse ir = new ListaMaterialResponse.ItemListaResponse();
            ir.setId(it.getId());
            ir.setInsumoId(it.getInsumo().getId());
            ir.setInsumoNombre(it.getInsumo().getNombreComercial());
            ir.setCantidadPorUnidad(it.getCantidadPorUnidad());
            return ir;
        }).collect(Collectors.toList());
        r.setItems(items);
        return r;
    }

    // helper: calcula requerimiento total dado qty de producto final
    public Map<Integer, BigDecimal> calcularRequerimiento(Integer listaMaterialId, BigDecimal cantidadProducto) {
        ListaMaterial lista = listaRepo.findById(listaMaterialId)
                .orElseThrow(() -> new BadRequestException("ListaMaterial no encontrada"));
        Map<Integer, BigDecimal> requerimiento = new HashMap<>();
        for (ItemListaMaterial it : lista.getItems()) {
            BigDecimal total = it.getCantidadPorUnidad().multiply(cantidadProducto);
            requerimiento.put(it.getInsumo().getId(), total);
        }
        return requerimiento;
    }



}
