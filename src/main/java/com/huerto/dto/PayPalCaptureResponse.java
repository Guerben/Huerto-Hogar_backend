package com.huerto.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayPalCaptureResponse {
    
    private String paypalOrderId;
    
    private String captureId;
    
    private String status;
    
    private BigDecimal amount;
    
    private String currency;
    
    private Long orderId;
    
    private String payerEmail;
    
    private String payerName;
}

