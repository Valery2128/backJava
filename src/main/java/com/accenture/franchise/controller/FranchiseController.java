package com.accenture.franchise.controller;

import com.accenture.franchise.domain.Branch;
import com.accenture.franchise.domain.Product;
import com.accenture.franchise.domain.Franchise;
import com.accenture.franchise.dto.*;
import com.accenture.franchise.service.FranchiseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/franchises")
@RequiredArgsConstructor
@Tag(name = "Franchise API", description = "Endpoints para la gestin integral de franquicias")
public class FranchiseController {

    private final FranchiseService service;

    /* 
       Aquí es donde entran las peticiones para crear franquicias.
    */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear una nueva franquicia")
    public Mono<Franchise> create(@Valid @RequestBody FranchiseRequest request) {
        return service.createFranchise(request);
    }

    /* 
       Con este agrego sucursales a una franquicia que ya tenga creada.
    */
    @PostMapping("/{id}/branches")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Agregar una sucursal a una franquicia")
    public Mono<Franchise> addBranch(@PathVariable String id, @Valid @RequestBody BranchRequest request) {
        return service.addBranch(id, request);
    }

    /* 
       Para meter productos nuevos en el inventario.
    */
    @PostMapping("/{id}/branches/{branchId}/products")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Agregar un producto a una sucursal")
    public Mono<Franchise> addProduct(@PathVariable String id, @PathVariable String branchId, @Valid @RequestBody ProductRequest request) {
        return service.addProduct(id, branchId, request);
    }

    /* 
       Si algún producto ya no se vende, con este lo saco de la lista.
    */
    @DeleteMapping("/{id}/branches/{branchId}/products/{productId}")
    @Operation(summary = "Eliminar un producto de una sucursal")
    public Mono<Franchise> deleteProduct(@PathVariable String id, @PathVariable String branchId, @PathVariable String productId) {
        return service.deleteProduct(id, branchId, productId);
    }

    /* 
       Este es para cambiar el número de stock cuando llega mercancía nueva.
    */
    @PatchMapping("/{id}/branches/{branchId}/products/{productId}/stock")
    @Operation(summary = "Actualizar el stock de un producto")
    public Mono<Franchise> updateStock(@PathVariable String id, @PathVariable String branchId, @PathVariable String productId, @Valid @RequestBody StockUpdateRequest request) {
        return service.updateStock(id, branchId, productId, request);
    }

    /* 
       Aquí saco el listado de los productos con más stock por sucursal.
    */
    @GetMapping("/{id}/top-stock")
    @Operation(summary = "Obtener el producto con ms stock por cada sucursal")
    public Mono<List<ProductPerBranchResponse>> getTopStock(@PathVariable String id) {
        return service.getTopStockProducts(id);
    }

    /* --- Endpoints de Actualizacin de Nombres (Plus de la Prueba) --- */

    @PatchMapping("/{id}/name")
    @Operation(summary = "Cambiar nombre de la franquicia")
    public Mono<Franchise> updateFranchiseName(@PathVariable String id, @Valid @RequestBody NameUpdateRequest request) {
        return service.updateFranchiseName(id, request);
    }

    @PatchMapping("/{id}/branches/{branchId}/name")
    @Operation(summary = "Cambiar nombre de una sucursal")
    public Mono<Branch> updateBranchName(@PathVariable String id, @PathVariable String branchId, @Valid @RequestBody NameUpdateRequest request) {
        return service.updateBranchName(id, branchId, request);
    }

    @PatchMapping("/{id}/branches/{branchId}/products/{productId}/name")
    @Operation(summary = "Cambiar nombre de un producto")
    public Mono<Product> updateProductName(@PathVariable String id, @PathVariable String branchId, @PathVariable String productId, @Valid @RequestBody NameUpdateRequest request) {
        return service.updateProductName(id, branchId, productId, request);
    }
}
