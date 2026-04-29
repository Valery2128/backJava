package com.accenture.franchise.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class FranchiseRequest {
    @NotBlank(message = "El nombre de la franquicia es obligatorio")
    private String name;
}
