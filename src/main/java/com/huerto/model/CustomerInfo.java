package com.huerto.model;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerInfo {
    private String name;
    private String email;
    private String phone;
    
    @Embedded
    private Address address;
}

