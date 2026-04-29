package com.accenture.franchise.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BranchRequest {
    @NotBlank(message = "El nombre de la sucursal es obligatorio")
    private String name;
}
