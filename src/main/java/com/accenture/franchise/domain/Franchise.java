package com.accenture.franchise.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "franchises")
public class Franchise {
    /* 
       acá guardo todo el esquema de la franquicia metí las sucursales 
       de una vez dentro del documento para que los reportes salgan 
       rápido y no me toque hacer cruces raros después
    */
    @Id
    private String id;
    private String name;
    private List<Branch> branches = new ArrayList<>();

    public Franchise(String name) {
        this.name = name;
        this.branches = new ArrayList<>();
    }
}
