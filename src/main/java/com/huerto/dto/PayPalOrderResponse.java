package com.huerto.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayPalOrderResponse {
    
    private String paypalOrderId;
    
    private String status;
    
    private String approvalUrl;
    
    private Long orderId;
}

