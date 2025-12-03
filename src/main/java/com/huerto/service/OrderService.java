package com.huerto.service;

import com.huerto.dto.CreateOrderRequest;
import com.huerto.dto.OrderDTO;
import com.huerto.model.Order;
import com.huerto.model.OrderItem;
import com.huerto.model.User;
import com.huerto.repository.OrderRepository;
import com.huerto.repository.UserRepository;
import com.huerto.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductService productService;
    private final SaleService saleService;
    private final ModelMapper modelMapper;

    @Transactional(readOnly = true)
    public List<OrderDTO> getAllOrders() {
        return orderRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(order -> modelMapper.map(order, OrderDTO.class))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<OrderDTO> getUserOrders(Long userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(order -> modelMapper.map(order, OrderDTO.class))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<OrderDTO> getMyOrders() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        return getUserOrders(userDetails.getId());
    }

    @Transactional(readOnly = true)
    public OrderDTO getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada con id: " + id));
        return modelMapper.map(order, OrderDTO.class);
    }

    @Transactional
    public OrderDTO createOrder(CreateOrderRequest request) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Order order = new Order();
        order.setUser(user);
        order.setTotal(request.getTotal());
        order.setSubtotal(request.getSubtotal());
        order.setShippingCost(request.getShippingCost());
        order.setDiscount(request.getDiscount());
        order.setPaymentMethod(request.getPaymentMethod());
        order.setStatus(Order.OrderStatus.PENDIENTE);
        
        // Mapeamos la información del cliente
        order.setCustomer(modelMapper.map(request.getCustomer(), com.huerto.model.CustomerInfo.class));

        // Mapeamos los items de la orden y establecemos la relación bidireccional
        List<OrderItem> items = request.getItems().stream()
                .map(itemDTO -> {
                    OrderItem item = new OrderItem();
                    item.setOrder(order); // Establecemos la relación con la orden
                    item.setProductId(itemDTO.getProductId());
                    item.setName(itemDTO.getName());
                    item.setPrice(itemDTO.getPrice());
                    item.setQuantity(itemDTO.getQuantity());
                    item.setImage(itemDTO.getImage());
                    return item;
                })
                .collect(Collectors.toList());
        
        order.setItems(items);

        // Agregamos el estado inicial al historial
        order.addStatusChange(Order.OrderStatus.PENDIENTE, userDetails.getEmail());

        Order savedOrder = orderRepository.save(order);
        return modelMapper.map(savedOrder, OrderDTO.class);
    }

    @Transactional
    public OrderDTO updateOrderStatus(Long id, String status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada con id: " + id));

        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        Order.OrderStatus newStatus = Order.OrderStatus.valueOf(status.toUpperCase());
        Order.OrderStatus oldStatus = order.getStatus();
        
        order.addStatusChange(newStatus, userDetails.getEmail());

        // Si la orden está siendo entregada, actualizamos el stock y creamos el registro de venta
        if (newStatus == Order.OrderStatus.ENTREGADO && oldStatus != Order.OrderStatus.ENTREGADO) {
            // Actualizamos el stock de productos solo si tienen productId válido
            order.getItems().forEach(item -> {
                if (item.getProductId() != null) {
                    productService.updateStock(item.getProductId(), item.getQuantity());
                }
            });

            // Creamos el registro de venta
            saleService.createSaleFromOrder(order);
        }

        Order updatedOrder = orderRepository.save(order);
        return modelMapper.map(updatedOrder, OrderDTO.class);
    }

    @Transactional
    public void deleteOrder(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new RuntimeException("Orden no encontrada con id: " + id);
        }
        orderRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<OrderDTO> getOrdersByStatus(String status) {
        Order.OrderStatus orderStatus = Order.OrderStatus.valueOf(status.toUpperCase());
        return orderRepository.findByStatus(orderStatus).stream()
                .map(order -> modelMapper.map(order, OrderDTO.class))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<OrderDTO> getOrdersByDateRange(LocalDateTime start, LocalDateTime end) {
        return orderRepository.findByCreatedAtBetween(start, end).stream()
                .map(order -> modelMapper.map(order, OrderDTO.class))
                .collect(Collectors.toList());
    }
}

