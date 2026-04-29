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
                           list.get(0).getProductName().equals("Monitor") &&
                           list.get(0).getStock() == 50;
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
}
