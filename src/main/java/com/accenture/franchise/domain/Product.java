package com.accenture.franchise.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    /* 
       Este es el nivel más básico: el producto. Meto el stock aquí
       directamente para que cuando cambie, la actualización sea atómica y rápida.
    */
    private String id = UUID.randomUUID().toString();
    private String name;
    private int stock;
}
