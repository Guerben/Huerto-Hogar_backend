package com.huerto.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerInfoDTO {
    private String name;
    private String email;
    private String phone;
    private AddressDTO address;
}

