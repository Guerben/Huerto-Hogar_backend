package com.huerto.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private AddressDTO address;
    private Set<String> roles;
    private Boolean enabled;
    private String photoURL;
    private String displayName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

