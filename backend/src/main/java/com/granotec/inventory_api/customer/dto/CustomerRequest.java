package com.granotec.inventory_api.customer.dto;

import com.granotec.inventory_api.common.enums.DocumentType;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerRequest {

    //@NotBlank(message = "El nombre es obligatorio.")
    private String nombre;

    //@NotBlank(message = "Los apellidos son obligatorios.")
    private String apellidos;

    private String razonSocial;

    @NotNull(message = "El campo TipoDocumento es obligatorio." )
    private DocumentType tipoDocumento;

    @NotBlank(message = "El documento es obligatorio.")
    @Pattern(regexp = "\\d{8}|\\d{11}", message = "El documento debe tener 8 dígitos para DNI o 11 dígitos para RUC.")
    private String documento;

    private String direccion;

    @Size(max = 9, message = "El teléfono no debe exceder los 9 caracteres.")
    private String telefono;

    @Email(message = "El email debe tener un formato válido.")
    private String email;

    @AssertTrue(message = "Los clientes con DNI deben tener nombre y apellidos.")
    public boolean isValidNatural() {
        if (tipoDocumento == DocumentType.DNI) {
            return nombre != null && !nombre.isBlank()
                    && apellidos != null && !apellidos.isBlank();
        }
        return true;
    }

    @AssertTrue(message = "Los clientes con RUC deben tener razón social.")
    public boolean isValidJuridico() {
        if (tipoDocumento == DocumentType.RUC) {
            return razonSocial != null && !razonSocial.isBlank();
        }
        return true;
    }


}

