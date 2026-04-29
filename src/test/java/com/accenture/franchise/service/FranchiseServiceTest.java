package com.accenture.franchise.service;

import com.accenture.franchise.domain.Branch;
import com.accenture.franchise.domain.Franchise;
import com.accenture.franchise.domain.Product;
import com.accenture.franchise.dto.FranchiseRequest;
import com.accenture.franchise.repository.FranchiseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FranchiseServiceTest {

    @Mock
    private FranchiseRepository repository;

    @InjectMocks
    private FranchiseService service;

    private Franchise franchise;

    @BeforeEach
    void setUp() {
        // armo una franquicia de prueba con una sucursal y dos productos
        franchise = new Franchise("662e84", "Accenture Corp", null);
        Branch branch = new Branch("Sede Bogota");
        branch.setProducts(List.of(
            new Product("p1", "Laptop", 10),
            new Product("p2", "Monitor", 50) // este es el que tiene más stock para el reporte
        ));
        franchise.setBranches(List.of(branch));
    }

    @Test
    void createFranchiseTest() {
        /* acá chequeo que el servicio sí guarde la franquicia nueva de una */
        FranchiseRequest request = new FranchiseRequest();
        request.setName("Accenture Corp");

        when(repository.save(any())).thenReturn(Mono.just(franchise));

        StepVerifier.create(service.createFranchise(request))
                .expectNextMatches(f -> f.getName().equals("Accenture Corp"))
                .verifyComplete();
    }

    @Test
    void getTopStockProductsTest() {
        /* este es el que importa para ver si el reporte sí saca el producto con más stock */
        when(repository.findById("662e84")).thenReturn(Mono.just(franchise));

        StepVerifier.create(service.getTopStockProducts("662e84"))
                .expectNextMatches(list -> {
                    return list.size() == 1 && 
                           list.get(0).getProduct().getName().equals("Monitor") &&
                           list.get(0).getProduct().getStock() == 50;
                })
                .verifyComplete();
    }

    @Test
    void franchiseNotFoundTest() {
        /* pruebo que si no existe la franquicia me bote el error que puse en el service */
        when(repository.findById("no-existe")).thenReturn(Mono.empty());

        StepVerifier.create(service.getTopStockProducts("no-existe"))
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException &&
                        throwable.getMessage().contains("No pudimos encontrar"))
                .verify();
    }

    @Test
    void updateStockTest() {
        /* chequeo que el stock sí cambie en el producto que toca */
        when(repository.findById("662e84")).thenReturn(Mono.just(franchise));
        when(repository.save(any())).thenReturn(Mono.just(franchise));

        com.accenture.franchise.dto.StockUpdateRequest req = new com.accenture.franchise.dto.StockUpdateRequest();
        req.setStock(100);

        StepVerifier.create(service.updateStock("662e84", "Sede Bogota", "p1", req))
                .expectNextMatches(f -> f.getBranches().get(0).getProducts().get(0).getStock() == 100)
                .verifyComplete();
    }

    @Test
    void deleteProductTest() {
        /* acá pruebo que si borro un producto la lista se actualice de una */
        when(repository.findById("662e84")).thenReturn(Mono.just(franchise));
        when(repository.save(any())).thenReturn(Mono.just(franchise));

        StepVerifier.create(service.deleteProduct("662e84", "Sede Bogota", "p1"))
                .expectNextMatches(f -> f.getBranches().get(0).getProducts().size() == 1)
                .verifyComplete();
    }
}
