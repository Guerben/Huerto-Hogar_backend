package com.huerto.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequest {
    @NotEmpty(message = "El carrito no puede estar vacío")
    private List<OrderItemDTO> items;
    
    @NotNull(message = "El total es obligatorio")
    private BigDecimal total;
    
    private BigDecimal subtotal;
    private BigDecimal shippingCost;
    private BigDecimal discount;
    
    @NotNull(message = "La información del cliente es obligatoria")
    private CustomerInfoDTO customer;
    
    private String paymentMethod;
}

