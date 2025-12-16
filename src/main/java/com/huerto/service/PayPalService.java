package com.huerto.service;

import com.huerto.dto.PayPalCaptureResponse;
import com.huerto.dto.PayPalOrderRequest;
import com.huerto.dto.PayPalOrderResponse;
import com.huerto.model.Order;
import com.huerto.repository.OrderRepository;
import com.paypal.core.PayPalHttpClient;
import com.paypal.http.HttpResponse;
import com.paypal.orders.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PayPalService {

    private final PayPalHttpClient payPalHttpClient;
    private final OrderRepository orderRepository;

    @Value("${paypal.return-url}")
    private String returnUrl;

    @Value("${paypal.cancel-url}")
    private String cancelUrl;

    /**
     * Crea una orden de pago en PayPal
     */
    @Transactional
    public PayPalOrderResponse createPayPalOrder(PayPalOrderRequest request) {
        try {
            // Verificar que la orden existe en la base de datos
            Order order = orderRepository.findById(request.getOrderId())
                    .orElseThrow(() -> new RuntimeException("Orden no encontrada con id: " + request.getOrderId()));

            // Crear la orden en PayPal
            OrderRequest orderRequest = buildOrderRequest(request);
            OrdersCreateRequest ordersCreateRequest = new OrdersCreateRequest();
            ordersCreateRequest.prefer("return=representation");
            ordersCreateRequest.requestBody(orderRequest);

            HttpResponse<com.paypal.orders.Order> response = payPalHttpClient.execute(ordersCreateRequest);
            com.paypal.orders.Order paypalOrder = response.result();

            log.info("Orden de PayPal creada: {}", paypalOrder.id());

            // Guardar el ID de PayPal en la orden
            order.setPaypalOrderId(paypalOrder.id());
            order.setPaypalPaymentStatus("CREATED");
            order.setPaymentMethod("PayPal");
            orderRepository.save(order);

            // Obtener la URL de aprobación
            String approvalUrl = getApprovalUrl(paypalOrder);

            return PayPalOrderResponse.builder()
                    .paypalOrderId(paypalOrder.id())
                    .status(paypalOrder.status())
                    .approvalUrl(approvalUrl)
                    .orderId(order.getId())
                    .build();

        } catch (IOException e) {
            log.error("Error al crear orden de PayPal", e);
            throw new RuntimeException("Error al crear orden de PayPal: " + e.getMessage());
        }
    }

    /**
     * Captura el pago después de que el usuario apruebe la transacción
     */
    @Transactional
    public PayPalCaptureResponse capturePayPalOrder(String paypalOrderId) {
        try {
            // Capturar el pago
            OrdersCaptureRequest ordersCaptureRequest = new OrdersCaptureRequest(paypalOrderId);
            ordersCaptureRequest.prefer("return=representation");

            HttpResponse<com.paypal.orders.Order> response = payPalHttpClient.execute(ordersCaptureRequest);
            com.paypal.orders.Order paypalOrder = response.result();

            log.info("Pago de PayPal capturado: {}", paypalOrder.id());

            // Actualizar la orden en la base de datos
            Order order = orderRepository.findByPaypalOrderId(paypalOrderId)
                    .orElseThrow(() -> new RuntimeException("Orden no encontrada con PayPal ID: " + paypalOrderId));

            // Extraer información del pago
            Capture capture = paypalOrder.purchaseUnits().get(0).payments().captures().get(0);
            Payer payer = paypalOrder.payer();

            order.setPaypalCaptureId(capture.id());
            order.setPaypalPaymentStatus(paypalOrder.status());
            order.setPaypalPayerEmail(payer.email());
            order.setPaypalPayerName(payer.name().givenName() + " " + payer.name().surname());
            
            // Si el pago fue exitoso, actualizar el estado de la orden
            if ("COMPLETED".equals(paypalOrder.status())) {
                order.setStatus(Order.OrderStatus.PROCESANDO);
                order.addStatusChange(Order.OrderStatus.PROCESANDO, "PayPal Payment System");
            }

            orderRepository.save(order);

            return PayPalCaptureResponse.builder()
                    .paypalOrderId(paypalOrder.id())
                    .captureId(capture.id())
                    .status(paypalOrder.status())
                    .amount(new BigDecimal(capture.amount().value()))
                    .currency(capture.amount().currencyCode())
                    .orderId(order.getId())
                    .payerEmail(payer.email())
                    .payerName(payer.name().givenName() + " " + payer.name().surname())
                    .build();

        } catch (IOException e) {
            log.error("Error al capturar pago de PayPal", e);
            throw new RuntimeException("Error al capturar pago de PayPal: " + e.getMessage());
        }
    }

    /**
     * Obtiene los detalles de una orden de PayPal
     */
    public com.paypal.orders.Order getPayPalOrderDetails(String paypalOrderId) {
        try {
            OrdersGetRequest ordersGetRequest = new OrdersGetRequest(paypalOrderId);
            HttpResponse<com.paypal.orders.Order> response = payPalHttpClient.execute(ordersGetRequest);
            return response.result();
        } catch (IOException e) {
            log.error("Error al obtener detalles de la orden de PayPal", e);
            throw new RuntimeException("Error al obtener detalles de la orden de PayPal: " + e.getMessage());
        }
    }

    /**
     * Construye el request de la orden para PayPal
     */
    private OrderRequest buildOrderRequest(PayPalOrderRequest request) {
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.checkoutPaymentIntent("CAPTURE");

        // Configurar el monto
        AmountWithBreakdown amountBreakdown = new AmountWithBreakdown()
                .currencyCode(request.getCurrency())
                .value(request.getAmount().toString());

        // Crear la unidad de compra
        PurchaseUnitRequest purchaseUnitRequest = new PurchaseUnitRequest()
                .amountWithBreakdown(amountBreakdown)
                .description("Orden #" + request.getOrderId() + " - Huerto Hogar")
                .referenceId(request.getOrderId().toString());

        List<PurchaseUnitRequest> purchaseUnits = new ArrayList<>();
        purchaseUnits.add(purchaseUnitRequest);
        orderRequest.purchaseUnits(purchaseUnits);

        // Configurar las URLs de retorno
        ApplicationContext applicationContext = new ApplicationContext()
                .returnUrl(request.getReturnUrl() != null ? request.getReturnUrl() : returnUrl)
                .cancelUrl(request.getCancelUrl() != null ? request.getCancelUrl() : cancelUrl)
                .brandName("Huerto Hogar")
                .landingPage("BILLING")
                .shippingPreference("NO_SHIPPING")
                .userAction("PAY_NOW");

        orderRequest.applicationContext(applicationContext);

        return orderRequest;
    }

    /**
     * Extrae la URL de aprobación de la orden de PayPal
     */
    private String getApprovalUrl(com.paypal.orders.Order order) {
        List<LinkDescription> links = order.links();
        for (LinkDescription link : links) {
            if ("approve".equals(link.rel())) {
                return link.href();
            }
        }
        throw new RuntimeException("No se encontró la URL de aprobación en la respuesta de PayPal");
    }
}

