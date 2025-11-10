package com.granotec.inventory_api.Movimientos.dto;

import com.granotec.inventory_api.common.enums.TipoMovimiento;
import com.granotec.inventory_api.common.enums.TypeOperation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovimientoRequest {
    private LocalDate fechaDocumento;
    private Long almacenId;
    private TipoMovimiento tipoMovimiento;
    private TypeOperation tipoOperacion;
    private String nroFactura;
    private Long proveedorId;
    private Long clienteId;
    private List<MovimientoLineRequest> detalles;
}

