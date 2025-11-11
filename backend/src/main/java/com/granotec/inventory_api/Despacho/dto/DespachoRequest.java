package com.granotec.inventory_api.Despacho.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DespachoRequest {
    private String ordenVenta;
    private String tipoOV;
    private String cliente;
    private String destino;
    private String vendedor;
    private LocalDate fechaDespacho;
    private String choferAsignado;
    private String placa;
    private String transportista;
    private BigDecimal costoFlete;
    private List<DespachoLineRequest> detalles;
}

