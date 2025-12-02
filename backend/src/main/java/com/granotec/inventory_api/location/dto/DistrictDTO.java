package com.granotec.inventory_api.location.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DistrictDTO {
    private Integer id;
    private String name;
    private Integer provinceId;
}
