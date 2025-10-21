package com.granotec.inventory_api.vendor.dto;

import com.granotec.inventory_api.common.enums.VendorDocumentType;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VendorRequest {

    @NotBlank(message = "El nombre es obligatorio.")
    private String nombre;

    @NotNull(message = "El campo TipoDocumento es obligatorio." )
    private VendorDocumentType tipoDocumento;

    @NotBlank(message = "El documento es obligatorio.")
    @Pattern(regexp = "\\d{8}|\\d{11}", message = "El documento debe tener 8 dígitos para DNI o 11 dígitos para RUC.")
    private String documento;

    private String direccion;

    @Size(max = 9, message = "El teléfono no debe exceder los 9 caracteres.")
    private String telefono;

    @Email(message = "El email debe tener un formato válido.")
    private String email;


}
