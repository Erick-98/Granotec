package com.granotec.inventory_api.OrdenCompra;

import com.granotec.inventory_api.OrdenCompra.detalle.DetalleOrdenCompra;
import com.granotec.inventory_api.OrdenCompra.dto.CompraRequest;
import com.granotec.inventory_api.OrdenCompra.dto.CompraResponse;
import com.granotec.inventory_api.common.exception.BadRequestException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class CompraMapperHelper {

    public CompraResponse toDto(OrdenCompra ordenCompra){

        CompraResponse dto = new CompraResponse();

        dto.setId(ordenCompra.getId());
        dto.setNumero(ordenCompra.getNumero());
        dto.setFecha(String.valueOf(ordenCompra.getFecha()));
        dto.setTotal(ordenCompra.getTotal());

        if(ordenCompra.getProveedor() != null){
            dto.setProveedorId(ordenCompra.getProveedor().getId());
            dto.setProveedorNombre(ordenCompra.getProveedor().getRazonSocial());
        }

        if(ordenCompra.getAlmacen() != null){
            dto.setAlmacenId(ordenCompra.getAlmacen().getId());
            dto.setAlmacenNombre(ordenCompra.getAlmacen().getNombre());
        }

        List<CompraResponse.DetalleCompraDTO> detalles = ordenCompra.getDetalles().stream()
                .map(this::mapDetalle)
                .toList();

        dto.setDetalles(detalles);
        dto.setEstado(calcularEstadoOrden(detalles));

        return dto;
    }

    private CompraResponse.DetalleCompraDTO mapDetalle(DetalleOrdenCompra det){

        CompraResponse.DetalleCompraDTO d = new CompraResponse.DetalleCompraDTO();

        d.setCantidad(det.getCantidadRecibida());
        d.setPrecioUnitario(det.getPrecioUnitario());
        d.setSubtotal(det.getSubtotal());
        d.setEstado(calcularEstadoDetalle(det));

        if(det.getProducto() != null){
            d.setProductoId(det.getProducto().getId().longValue());
            d.setProductoNombre(det.getProducto().getNombreComercial());
        }

        if(det.getLote() != null){
            d.setLoteId(det.getLote().getId());
            d.setCodigoLote(det.getLote().getCodigoLote());
            if(det.getLote().getFechaProduccion() != null){
                d.setFechaProduccion(det.getLote().getFechaProduccion().toString());
            }
            if(det.getLote().getFechaVencimiento() != null){
                d.setFechaVencimiento(det.getLote().getFechaVencimiento().toString());
            }
        }

        return d;
    }

    private String calcularEstadoDetalle(DetalleOrdenCompra detalle){

        int comp = detalle.getCantidadRecibida().compareTo(detalle.getCantidadOrdenada());

        if (detalle.getCantidadRecibida().compareTo(BigDecimal.ZERO) == 0) {
            return "PENDIENTE";
        }
        if (comp < 0) return "PARCIAL";
        if (comp > 0) return "EXCESO";

        return "COMPLETO";
    }

    private String calcularEstadoOrden(List<CompraResponse.DetalleCompraDTO> detalles) {

        boolean todosPendientes = detalles.stream().allMatch(d -> d.getEstado().equals("PENDIENTE"));
        if (todosPendientes) return "REGISTRADA";

        boolean todosCompletos = detalles.stream().allMatch(d -> d.getEstado().equals("COMPLETO"));
        if (todosCompletos) return "COMPLETADA";

        return "PARCIAL";
    }
}
