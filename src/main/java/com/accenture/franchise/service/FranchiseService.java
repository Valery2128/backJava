package com.accenture.franchise.service;

import com.accenture.franchise.domain.Branch;
import com.accenture.franchise.domain.Franchise;
import com.accenture.franchise.domain.Product;
import com.accenture.franchise.dto.*;
import com.accenture.franchise.repository.FranchiseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FranchiseService {

    private final FranchiseRepository repository;

    /* 
       acá empiezo creando la franquicia de una vez guardo el nombre 
       y ya después miro lo demás
    */
    public Mono<Franchise> createFranchise(FranchiseRequest request) {
        return repository.save(new Franchise(request.getName()));
    }

    /* 
       primero chequeo que la franquicia sí exista si no la encuentro 
       por ningún lado de una vez boto error
    */
    public Mono<Franchise> addBranch(String franchiseId, BranchRequest request) {
        return repository.findById(franchiseId)
                .switchIfEmpty(Mono.error(new RuntimeException("No pudimos encontrar la franquicia con ID: " + franchiseId)))
                .flatMap(franchise -> {
                    franchise.getBranches().add(new Branch(request.getName()));
                    return repository.save(franchise);
                });
    }

    /* 
       aprovecho que mongo me deja meter listas embebidas para que 
       todo quede junto y no se me embolate nada
    */
    public Mono<Franchise> addProduct(String franchiseId, String branchId, ProductRequest request) {
        return repository.findById(franchiseId)
                .switchIfEmpty(Mono.error(new RuntimeException("La franquicia indicada no existe")))
                .flatMap(franchise -> {
                    Branch branch = findBranch(franchise, branchId);
                    branch.getProducts().add(new Product(null, request.getName(), request.getStock()));
                    return repository.save(franchise);
                });
    }

    /* 
       si toca borrar un producto lo saco de la lista y ya guardo 
       los cambios sin tanto complique
    */
    public Mono<Franchise> deleteProduct(String franchiseId, String branchId, String productId) {
        return repository.findById(franchiseId)
                .switchIfEmpty(Mono.error(new RuntimeException("No se encontr la franquicia para eliminar el producto")))
                .flatMap(franchise -> {
                    Branch branch = findBranch(franchise, branchId);
                    branch.getProducts().removeIf(p -> p.getId().equals(productId));
                    return repository.save(franchise);
                });
    }

    /* 
       uso streams para pillar el producto exacto y cambiar el 
       stock antes de guardar
    */
    public Mono<Franchise> updateStock(String franchiseId, String branchId, String productId, StockUpdateRequest request) {
        return repository.findById(franchiseId)
                .switchIfEmpty(Mono.error(new RuntimeException("Franquicia no encontrada para actualizar stock")))
                .flatMap(franchise -> {
                    Branch branch = findBranch(franchise, branchId);
                    Product product = branch.getProducts().stream()
                            .filter(p -> p.getId().equals(productId))
                            .findFirst()
                            .orElseThrow(() -> new RuntimeException("El producto solicitado no existe en esta sucursal"));
                    
                    product.setStock(request.getStock());
                    return repository.save(franchise);
                });
    }

    /* 
       el reporte estrella para sacar el que más stock tiene por sucursal 
       sale súper rápido con este esquema
    */
    public Mono<List<ProductPerBranchResponse>> getTopStockProducts(String franchiseId) {
        return repository.findById(franchiseId)
                .switchIfEmpty(Mono.error(new RuntimeException("No existe la franquicia para generar el reporte de stock")))
                .map(franchise -> franchise.getBranches().stream()
                        .map(branch -> {
                            Product topProduct = branch.getProducts().stream()
                                    .max(Comparator.comparingInt(Product::getStock))
                                    .orElse(null);
                            return new ProductPerBranchResponse(branch.getName(), topProduct);
                        })
                        .filter(res -> res.getProduct() != null)
                        .collect(Collectors.toList())
                );
    }

    /* --- Endpoints de Actualizacin (Plus de la prueba) --- */

    public Mono<Franchise> updateFranchiseName(String franchiseId, NameUpdateRequest request) {
        return repository.findById(franchiseId)
                .flatMap(f -> {
                    f.setName(request.getName());
                    return repository.save(f);
                });
    }

    public Mono<Branch> updateBranchName(String franchiseId, String branchId, NameUpdateRequest request) {
        return repository.findById(franchiseId)
                .flatMap(f -> {
                    Branch b = findBranch(f, branchId);
                    b.setName(request.getName());
                    return repository.save(f).thenReturn(b);
                });
    }

    public Mono<Product> updateProductName(String franchiseId, String branchId, String productId, NameUpdateRequest request) {
        return repository.findById(franchiseId)
                .flatMap(f -> {
                    Branch b = findBranch(f, branchId);
                    Product p = b.getProducts().stream()
                            .filter(prod -> prod.getId().equals(productId))
                            .findFirst()
                            .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
                    p.setName(request.getName());
                    return repository.save(f).thenReturn(p);
                });
    }

    /* 
       Mtodo de soporte para no repetir la bsqueda de sucursales en cada mtodo.
       Si la sucursal no est, cortamos el flujo con una excepcin.
    */
    private Branch findBranch(Franchise franchise, String branchId) {
        return franchise.getBranches().stream()
                .filter(b -> b.getId().equals(branchId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("La sucursal con ID " + branchId + " no pertenece a esta franquicia"));
    }
}
