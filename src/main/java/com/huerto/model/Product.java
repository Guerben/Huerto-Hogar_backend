package com.huerto.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre del producto es obligatorio")
    @Column(unique = true)
    private String name;

    @Column(length = 1000)
    private String description;

    @NotNull(message = "El precio es obligatorio")
    @Min(value = 0, message = "El precio debe ser positivo")
    private BigDecimal price;

    private BigDecimal originalPrice;

    @NotBlank(message = "La categoría es obligatoria")
    private String category;

    private String image;

    @Min(value = 0, message = "El stock debe ser positivo")
    private Integer stock = 0;

    private Double rating = 0.0;

    private Integer reviews = 0;

    private Boolean isNew = false;

    private Boolean isSale = false;

    private Integer discount = 0;

    private String energy;

    @ElementCollection
    @CollectionTable(name = "product_benefits", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "benefit")
    private List<String> benefits = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "product_uses", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "use_case")
    private List<String> uses = new ArrayList<>();

    @Embedded
    private Recipe recipe;

    private Integer purchaseCount = 0;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}

