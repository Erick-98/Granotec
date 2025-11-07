package com.granotec.inventory_api.location.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProvinceDTO {
    private Integer id;
    private String name;
    private Integer departmentId;
}
