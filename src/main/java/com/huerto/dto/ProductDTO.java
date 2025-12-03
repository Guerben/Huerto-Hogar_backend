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
public class ProductDTO {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private String category;
    private String image;
    private Integer stock;
    private Double rating;
    private Integer reviews;
    private Boolean isNew;
    private Boolean isSale;
    private Integer discount;
    private String energy;
    private List<String> benefits;
    private List<String> uses;
    private RecipeDTO recipe;
    private Integer purchaseCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

