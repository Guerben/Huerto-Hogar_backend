package com.huerto.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaleDTO {
    private Long id;
    private Long orderId;
    private Long userId;
    private List<OrderItemDTO> items;
    private BigDecimal total;
    private BigDecimal subtotal;
    private BigDecimal discount;
    private CustomerInfoDTO customer;
    private String paymentMethod;
    private LocalDateTime createdAt;
    private LocalDateTime deliveredAt;
    private String status;
}

