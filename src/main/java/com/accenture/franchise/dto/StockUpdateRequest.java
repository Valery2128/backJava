package com.accenture.franchise.dto;

import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class StockUpdateRequest {
    @Min(value = 0, message = "El stock no puede ser negativo")
    private int stock;
}
