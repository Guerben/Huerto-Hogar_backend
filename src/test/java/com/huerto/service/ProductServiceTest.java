package com.huerto.service;

import com.huerto.dto.ProductDTO;
import com.huerto.model.Product;
import com.huerto.repository.ProductRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para ProductService
 * 
 * Prueba 4: Test de creación de producto
 * Prueba 5: Test de actualización de producto
 * Prueba 6: Test de eliminación de producto
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Tests de ProductService - Gestión de Productos")
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ProductService productService;

    private Product mockProduct;
    private ProductDTO mockProductDTO;

    @BeforeEach
    void setUp() {
        // Configurar producto de prueba
        mockProduct = new Product();
        mockProduct.setId(1L);
        mockProduct.setName("Tomate Orgánico");
        mockProduct.setDescription("Tomate fresco y orgánico");
        mockProduct.setPrice(new BigDecimal("5.99"));
        mockProduct.setStock(100);
        mockProduct.setCategory("Vegetales");
        mockProduct.setImage("tomate.jpg");

        mockProductDTO = new ProductDTO();
        mockProductDTO.setId(1L);
        mockProductDTO.setName("Tomate Orgánico");
        mockProductDTO.setDescription("Tomate fresco y orgánico");
        mockProductDTO.setPrice(new BigDecimal("5.99"));
        mockProductDTO.setStock(100);
        mockProductDTO.setCategory("Vegetales");
        mockProductDTO.setImage("tomate.jpg");
    }

    @Test
    @DisplayName("Prueba 4: Debe crear un nuevo producto exitosamente")
    void testCreateProduct_Success() {
        // Given
        when(modelMapper.map(any(ProductDTO.class), eq(Product.class))).thenReturn(mockProduct);
        when(productRepository.save(any(Product.class))).thenReturn(mockProduct);
        when(modelMapper.map(any(Product.class), eq(ProductDTO.class))).thenReturn(mockProductDTO);

        // When
        ProductDTO result = productService.createProduct(mockProductDTO);

        // Then
        assertNotNull(result, "El resultado no debe ser null");
        assertEquals("Tomate Orgánico", result.getName(), "El nombre debe coincidir");
        assertEquals(new BigDecimal("5.99"), result.getPrice(), "El precio debe coincidir");
        assertEquals(100, result.getStock(), "El stock debe coincidir");

        // Verificaciones
        verify(productRepository, times(1)).save(any(Product.class));
        verify(modelMapper, times(2)).map(any(), any());
    }

    @Test
    @DisplayName("Prueba 5: Debe actualizar un producto existente exitosamente")
    void testUpdateProduct_Success() {
        // Given
        Long productId = 1L;
        ProductDTO updateDTO = new ProductDTO();
        updateDTO.setName("Tomate Orgánico Premium");
        updateDTO.setPrice(new BigDecimal("7.99"));
        updateDTO.setStock(150);

        Product updatedProduct = new Product();
        updatedProduct.setId(productId);
        updatedProduct.setName("Tomate Orgánico Premium");
        updatedProduct.setPrice(new BigDecimal("7.99"));
        updatedProduct.setStock(150);

        ProductDTO updatedDTO = new ProductDTO();
        updatedDTO.setId(productId);
        updatedDTO.setName("Tomate Orgánico Premium");
        updatedDTO.setPrice(new BigDecimal("7.99"));
        updatedDTO.setStock(150);

        when(productRepository.findById(productId)).thenReturn(Optional.of(mockProduct));
        when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);
        when(modelMapper.map(any(Product.class), eq(ProductDTO.class))).thenReturn(updatedDTO);

        // When
        ProductDTO result = productService.updateProduct(productId, updateDTO);

        // Then
        assertNotNull(result, "El resultado no debe ser null");
        assertEquals("Tomate Orgánico Premium", result.getName(), "El nombre debe estar actualizado");
        assertEquals(new BigDecimal("7.99"), result.getPrice(), "El precio debe estar actualizado");
        assertEquals(150, result.getStock(), "El stock debe estar actualizado");

        // Verificaciones
        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Prueba 6: Debe eliminar un producto existente exitosamente")
    void testDeleteProduct_Success() {
        // Given
        Long productId = 1L;
        when(productRepository.existsById(productId)).thenReturn(true);
        doNothing().when(productRepository).deleteById(productId);

        // When
        productService.deleteProduct(productId);

        // Then
        // Verificar que se llamó al método deleteById
        verify(productRepository, times(1)).existsById(productId);
        verify(productRepository, times(1)).deleteById(productId);
    }

    @Test
    @DisplayName("Prueba Extra: Debe lanzar excepción al intentar actualizar producto inexistente")
    void testUpdateProduct_NotFound() {
        // Given
        Long productId = 999L;
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            productService.updateProduct(productId, mockProductDTO);
        });

        assertTrue(exception.getMessage().contains("Producto no encontrado") ||
                   exception.getMessage().contains("not found"),
                "El mensaje debe indicar que el producto no existe");

        // Verificar que NO se intentó guardar
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("Prueba Extra: Debe lanzar excepción al intentar eliminar producto inexistente")
    void testDeleteProduct_NotFound() {
        // Given
        Long productId = 999L;
        when(productRepository.existsById(productId)).thenReturn(false);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            productService.deleteProduct(productId);
        });

        assertTrue(exception.getMessage().contains("Producto no encontrado") ||
                   exception.getMessage().contains("not found"),
                "El mensaje debe indicar que el producto no existe");

        // Verificar que NO se intentó eliminar
        verify(productRepository, never()).deleteById(anyLong());
    }
}

