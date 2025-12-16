package com.huerto.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PayPalOrderRequest {
    
    @NotNull(message = "El ID de la orden es obligatorio")
    private Long orderId;
    
    @NotNull(message = "El monto es obligatorio")
    private BigDecimal amount;
    
    private String currency = "USD";
    
    private String returnUrl;
    
    private String cancelUrl;
}

