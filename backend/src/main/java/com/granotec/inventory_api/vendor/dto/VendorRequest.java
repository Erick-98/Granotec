package com.granotec.inventory_api.vendor.dto;

import com.granotec.inventory_api.common.enums.DocumentType;
import com.granotec.inventory_api.common.enums.CondicionPago;
import com.granotec.inventory_api.common.enums.Currency;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VendorRequest {

    @NotBlank(message = "La razón social es obligatoria.")
    private String razonSocial;  // ← CAMBIA 'nombre' por 'razonSocial'

    //@NotNull(message = "El campo TipoDocumento es obligatorio." )
    private DocumentType tipoDocumento;

    //@NotBlank(message = "El documento es obligatorio.")
    //@Pattern(regexp = "\\d{8}|\\d{11}", message = "El documento debe tener 8 dígitos para DNI o 11 dígitos para RUC.")
    private String nroDocumento;

    private String direccion;

    @Size(max = 9, message = "El teléfono no debe exceder los 9 caracteres.")
    private String telefono;

    @Email(message = "El email debe tener un formato válido.")
    private String email;

    @Size(max = 500, message = "Las notas no deben exceder los 500 caracteres.")
    private String notas;

    @NotNull(message = "La moneda es obligatoria.")
    private Currency moneda;

    private CondicionPago condicionPago;
}