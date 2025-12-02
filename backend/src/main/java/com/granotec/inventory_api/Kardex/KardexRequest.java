package com.granotec.inventory_api.Kardex;

import com.granotec.inventory_api.common.enums.TipoMovimiento;
import com.granotec.inventory_api.common.enums.TypeOperation;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class KardexRequest {
    private Long almacenId;                // id del almacén (obligatorio)
    private Integer productoId;            // id del producto (obligatorio)
    private String lote;                // id del lote (opcional)
    private String numeroOp;               // numero OP (opcional)
    private TipoMovimiento tipoMovimiento; // ENTRADA / SALIDA / AJUSTE
    private TypeOperation tipoOperacion;   // enum de operación
    private String referencia;             // factura / guia
    private BigDecimal cantidad;           // cantidad (negativa o positiva según tu flujo) precision 3
    private BigDecimal costoUnitarioSoles; // si null, se toma desde lote/op (si está disponible)
    private BigDecimal tasaCambio;         // soles->usd (opcional). Si null, no calcula USD
    private String observacion;            // observación
    private Integer usuarioId;
}
