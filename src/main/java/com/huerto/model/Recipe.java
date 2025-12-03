package com.huerto.model;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Recipe {
    private String title;
    
    @ElementCollection
    private List<String> ingredients = new ArrayList<>();
    
    @ElementCollection
    private List<String> steps = new ArrayList<>();
}

