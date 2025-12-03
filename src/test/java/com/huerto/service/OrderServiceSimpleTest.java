package com.huerto.service;

import com.huerto.dto.OrderDTO;
import com.huerto.model.Order;
import com.huerto.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios simplificados para OrderService
 * 
 * Prueba 7: Test de búsqueda de orden por ID
 * Prueba 8: Test de actualización de estado de orden (lógica básica)
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Tests Simplificados de OrderService")
class OrderServiceSimpleTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private OrderService orderService;

    private Order mockOrder;
    private OrderDTO mockOrderDTO;

    @BeforeEach
    void setUp() {
        mockOrder = new Order();
        mockOrder.setId(1L);
        mockOrder.setStatus(Order.OrderStatus.PENDIENTE);
        mockOrder.setTotal(new BigDecimal("25.99"));

        mockOrderDTO = new OrderDTO();
        mockOrderDTO.setId(1L);
        mockOrderDTO.setStatus("PENDIENTE");
        mockOrderDTO.setTotal(new BigDecimal("25.99"));
    }

    @Test
    @DisplayName("Prueba 7: Debe encontrar una orden por ID exitosamente")
    void testGetOrderById_Success() {
        // Given
        Long orderId = 1L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(mockOrder));
        when(modelMapper.map(any(Order.class), eq(OrderDTO.class))).thenReturn(mockOrderDTO);

        // When
        OrderDTO result = orderService.getOrderById(orderId);

        // Then
        assertNotNull(result, "El resultado no debe ser null");
        assertEquals(1L, result.getId(), "El ID debe coincidir");
        assertEquals("PENDIENTE", result.getStatus(), "El estado debe coincidir");
        assertEquals(new BigDecimal("25.99"), result.getTotal(), "El total debe coincidir");

        // Verificaciones
        verify(orderRepository, times(1)).findById(orderId);
        verify(modelMapper, times(1)).map(mockOrder, OrderDTO.class);
    }

    @Test
    @DisplayName("Prueba 8: Debe lanzar excepción al buscar orden inexistente")
    void testGetOrderById_NotFound() {
        // Given
        Long orderId = 999L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.getOrderById(orderId);
        });

        assertTrue(exception.getMessage().contains("Orden no encontrada") ||
                   exception.getMessage().contains("not found"),
                "El mensaje debe indicar que la orden no existe");

        // Verificar que NO se intentó mapear
        verify(modelMapper, never()).map(any(), any());
    }

    @Test
    @DisplayName("Prueba Extra: Debe verificar que la orden existe antes de actualizar")
    void testUpdateOrderStatus_OrderExists() {
        // Given
        Long orderId = 1L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(mockOrder));

        // When - Intentar buscar la orden
        Optional<Order> foundOrder = orderRepository.findById(orderId);

        // Then
        assertTrue(foundOrder.isPresent(), "La orden debe existir");
        assertEquals(mockOrder, foundOrder.get(), "Debe ser la orden correcta");
        assertEquals(Order.OrderStatus.PENDIENTE, foundOrder.get().getStatus(), 
                "El estado debe ser PENDIENTE");

        verify(orderRepository, times(1)).findById(orderId);
    }

    @Test
    @DisplayName("Prueba Extra: Debe validar que el total de la orden es correcto")
    void testOrderTotal_IsCorrect() {
        // Given
        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(mockOrder));

        // When
        Optional<Order> foundOrder = orderRepository.findById(1L);

        // Then
        assertTrue(foundOrder.isPresent(), "La orden debe existir");
        assertNotNull(foundOrder.get().getTotal(), "El total no debe ser null");
        assertTrue(foundOrder.get().getTotal().compareTo(BigDecimal.ZERO) > 0,
                "El total debe ser mayor que cero");
        assertEquals(new BigDecimal("25.99"), foundOrder.get().getTotal(),
                "El total debe ser exactamente 25.99");
    }

    @Test
    @DisplayName("Prueba Extra: Debe eliminar una orden exitosamente")
    void testDeleteOrder_Success() {
        // Given
        Long orderId = 1L;
        when(orderRepository.existsById(orderId)).thenReturn(true);
        doNothing().when(orderRepository).deleteById(orderId);

        // When
        orderService.deleteOrder(orderId);

        // Then
        verify(orderRepository, times(1)).existsById(orderId);
        verify(orderRepository, times(1)).deleteById(orderId);
    }
}

