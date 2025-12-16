package com.huerto.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.huerto.dto.PayPalCaptureResponse;
import com.huerto.dto.PayPalOrderRequest;
import com.huerto.dto.PayPalOrderResponse;
import com.huerto.service.PayPalService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/paypal")
@RequiredArgsConstructor
@CrossOrigin(originPatterns = {"http://localhost:*", "http://127.0.0.1:*"})
@SecurityRequirement(name = "bearer-jwt")
@Tag(name = "PayPal", description = "Gestión de pagos con PayPal")
@Slf4j
public class PayPalController {

    private final PayPalService payPalService;

    @PostMapping("/create-order")
    @Operation(summary = "Crear orden de PayPal", 
               description = "Crea una orden de pago en PayPal y retorna la URL de aprobación")
    public ResponseEntity<PayPalOrderResponse> createOrder(@Valid @RequestBody PayPalOrderRequest request) {
        try {
            log.info("Creando orden de PayPal para la orden ID: {}", request.getOrderId());
            PayPalOrderResponse response = payPalService.createPayPalOrder(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("Error al crear orden de PayPal", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/capture-order/{paypalOrderId}")
    @Operation(summary = "Capturar pago de PayPal", 
               description = "Captura el pago después de que el usuario apruebe la transacción")
    public ResponseEntity<PayPalCaptureResponse> captureOrder(@PathVariable String paypalOrderId) {
        try {
            log.info("Capturando pago de PayPal: {}", paypalOrderId);
            PayPalCaptureResponse response = payPalService.capturePayPalOrder(paypalOrderId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error al capturar pago de PayPal", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/order/{paypalOrderId}")
    @Operation(summary = "Obtener detalles de orden de PayPal", 
               description = "Obtiene los detalles de una orden de PayPal")
    public ResponseEntity<?> getOrderDetails(@PathVariable String paypalOrderId) {
        try {
            log.info("Obteniendo detalles de la orden de PayPal: {}", paypalOrderId);
            com.paypal.orders.Order order = payPalService.getPayPalOrderDetails(paypalOrderId);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            log.error("Error al obtener detalles de la orden de PayPal", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

