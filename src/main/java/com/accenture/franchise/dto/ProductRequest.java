package com.accenture.franchise.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ProductRequest {
    @NotBlank(message = "El nombre del producto es obligatorio")
    private String name;

    @Min(value = 0, message = "El stock no puede ser negativo")
    private int stock;
}
