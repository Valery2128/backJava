package com.accenture.franchise.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class NameUpdateRequest {
    @NotBlank(message = "El nombre no puede estar vaco")
    private String name;
}
