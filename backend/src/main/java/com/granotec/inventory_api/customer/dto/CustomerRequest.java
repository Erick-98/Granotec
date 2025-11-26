package com.granotec.inventory_api.customer.dto;

import com.granotec.inventory_api.common.enums.CondicionPago;
import com.granotec.inventory_api.common.enums.DocumentType;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerRequest {

    private String nombre;
    private String apellidos;
    private String razonSocial;

    @Size(max = 150, message = "La zona no debe superar los 150 caracteres")
    private String zona;

    @NotNull(message = "Debe seleccionar un distrito")
    private Integer distritoId;

    @NotNull(message = "Debe seleccionar un tipo de cliente")
    private Long tipoClienteId;

    @Size(max = 100, message = "El rubro no debe superar los 100 caracteres")
    private String rubro;

    @NotBlank(message = "Debe indicar la condición de pago")
    private String condicionPago;

    @DecimalMin(value = "0.0", inclusive = true, message = "El límite en dólares debe ser positivo")
    @Digits(integer = 10, fraction = 6, message = "Máximo 6 decimales permitidos")
    private BigDecimal limiteDolares;

    @DecimalMin(value = "0.0", inclusive = true, message = "El límite en soles debe ser positivo")
    @Digits(integer = 10, fraction = 6, message = "Máximo 6 decimales permitidos")
    private BigDecimal limiteCreditoSoles;

    private String notas;

    // ✅ CORREGIDO - Cambiado a String
    @NotBlank(message = "El tipo de documento es obligatorio")
    private String tipoDocumento;

    @NotNull(message = "El documento es obligatorio")
    @Pattern(regexp = "\\d{8}|\\d{11}", message = "El documento debe tener 8 dígitos para DNI o 11 dígitos para RUC")
    private String nroDocumento;

    private String direccion;

    @Size(max = 9, message = "El teléfono no debe exceder los 9 caracteres")
    private String telefono;

    @Email(message = "El email debe tener un formato válido")
    private String email;

    @AssertTrue(message = "Los clientes con DNI deben tener nombre y apellidos")
    public boolean isValidNatural() {
        if ("DNI".equals(tipoDocumento)) {
            return nombre != null && !nombre.isBlank()
                    && apellidos != null && !apellidos.isBlank();
        }
        return true;
    }

    @AssertTrue(message = "Los clientes con RUC deben tener razón social")
    public boolean isValidJuridico() {
        if ("RUC".equals(tipoDocumento)) {
            return razonSocial != null && !razonSocial.isBlank();
        }
        return true;
    }

    @AssertTrue(message = "La condición de pago no es válida")
    public boolean isCondicionPagoValid(){
        if(condicionPago == null || condicionPago.isBlank()) return false;
        try{
            CondicionPago.valueOf(condicionPago.toUpperCase());
            return true;
        }catch (IllegalArgumentException e){
            return false;
        }
    }

    @AssertTrue(message = "El tipo de documento no es válido")
    public boolean isTipoDocumentoValid(){
        if(tipoDocumento == null || tipoDocumento.isBlank()) return false;
        try{
            DocumentType.valueOf(tipoDocumento);
            return true;
        }catch (IllegalArgumentException e){
            return false;
        }
    }
}