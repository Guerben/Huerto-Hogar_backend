package com.huerto.service;

import com.huerto.dto.ProductDTO;
import com.huerto.model.Product;
import com.huerto.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;

    @Transactional(readOnly = true)
    public List<ProductDTO> getAllProducts() {
        return productRepository.findAll().stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProductDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con id: " + id));
        return modelMapper.map(product, ProductDTO.class);
    }

    @Transactional(readOnly = true)
    public List<ProductDTO> getProductsByCategory(String category) {
        return productRepository.findByCategoryContainingIgnoreCase(category).stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductDTO> searchProducts(String query) {
        return productRepository.findByNameContainingIgnoreCase(query).stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductDTO> getNewProducts() {
        return productRepository.findByIsNew(true).stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductDTO> getSaleProducts() {
        return productRepository.findByIsSale(true).stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .collect(Collectors.toList());
    }

    @Transactional
    public ProductDTO createProduct(ProductDTO productDTO) {
        Product product = modelMapper.map(productDTO, Product.class);
        Product savedProduct = productRepository.save(product);
        return modelMapper.map(savedProduct, ProductDTO.class);
    }

    @Transactional
    public ProductDTO updateProduct(Long id, ProductDTO productDTO) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con id: " + id));

        // Actualizamos los campos
        existingProduct.setName(productDTO.getName());
        existingProduct.setDescription(productDTO.getDescription());
        existingProduct.setPrice(productDTO.getPrice());
        existingProduct.setOriginalPrice(productDTO.getOriginalPrice());
        existingProduct.setCategory(productDTO.getCategory());
        existingProduct.setImage(productDTO.getImage());
        existingProduct.setStock(productDTO.getStock());
        existingProduct.setRating(productDTO.getRating());
        existingProduct.setReviews(productDTO.getReviews());
        existingProduct.setIsNew(productDTO.getIsNew());
        existingProduct.setIsSale(productDTO.getIsSale());
        existingProduct.setDiscount(productDTO.getDiscount());
        existingProduct.setEnergy(productDTO.getEnergy());
        existingProduct.setBenefits(productDTO.getBenefits());
        existingProduct.setUses(productDTO.getUses());
        
        if (productDTO.getRecipe() != null) {
            existingProduct.setRecipe(modelMapper.map(productDTO.getRecipe(), com.huerto.model.Recipe.class));
        }

        Product updatedProduct = productRepository.save(existingProduct);
        return modelMapper.map(updatedProduct, ProductDTO.class);
    }

    @Transactional
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Producto no encontrado con id: " + id);
        }
        productRepository.deleteById(id);
    }

    @Transactional
    public void updateStock(Long productId, Integer quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con id: " + productId));
        
        int newStock = Math.max(0, product.getStock() - quantity);
        product.setStock(newStock);
        product.setPurchaseCount(product.getPurchaseCount() + quantity);
        
        productRepository.save(product);
    }
}

