package com.accenture.franchise.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Branch {
    /* 
       prefiero generar el id yo misma desde el código así tengo el 
       control total desde que nace el objeto sin esperar a la base de datos
    */
    private String id = UUID.randomUUID().toString();
    private String name;
    private List<Product> products = new ArrayList<>();

    public Branch(String name) {
        this.name = name;
        this.products = new ArrayList<>();
    }
}
