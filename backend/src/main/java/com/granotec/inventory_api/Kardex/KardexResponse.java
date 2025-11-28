package com.granotec.inventory_api.Kardex;

import com.granotec.inventory_api.common.enums.TipoMovimiento;
import com.granotec.inventory_api.common.enums.TipoPresentacion;
import com.granotec.inventory_api.common.enums.TypeOperation;
import com.granotec.inventory_api.common.enums.TypeProduct;
import com.granotec.inventory_api.product.familiaProducto.familiaProducto;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class KardexResponse {
    private Long id;
    private LocalDate fechaMovimiento;
    private Long almacenId;
    private String almacenNombre;          // para mostrar en UI
    private TipoMovimiento tipoMovimiento;
    private TypeOperation tipoOperacion;
    private String referencia;
    private Integer productoId;
    private String productoCodigo;
    private String productoNombre;
    private familiaProducto familiaProducto;
    private TypeProduct tipoProducto;
    private Integer loteId;
    private String loteCodigo;
    private String numeroOp;
    private LocalDate fechaIngresoOp;     // fecha inicio/op ingreso si aplica
    private LocalDate fechaProduccion;     // desde Lote
    private LocalDate fechaVencimiento;    // desde Lote (si existe)
    private TipoPresentacion presentacion;           // desde product
    private String proveedor;              // desde product->proveedor
    private String destinoCliente;         // por ahora null o extraerse si aplica
    private BigDecimal cantidad;           // scale 3
    private BigDecimal costoUnitarioSoles; // scale 6
    private BigDecimal totalSoles;         // scale 3
    private BigDecimal costoUnitarioDolares; // scale 6 (si se calculó)
    private BigDecimal totalDolares;         // scale 3 (si se calculó)
    private BigDecimal stockAnterior;      // scale 3
    private BigDecimal stockActual;        // scale 3
    private String observacion;
    private Integer usuarioId;
    private String usuarioNombre;
}
