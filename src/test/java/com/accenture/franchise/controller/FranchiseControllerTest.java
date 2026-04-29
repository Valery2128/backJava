package com.accenture.franchise.controller;

import com.accenture.franchise.domain.Franchise;
import com.accenture.franchise.dto.FranchiseRequest;
import com.accenture.franchise.service.FranchiseService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = FranchiseController.class)
class FranchiseControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private FranchiseService service;

    @Test
    void createFranchiseEndpointTest() {
        /* acá pruebo que el endpoint de crear franquicia responda un 201 created */
        Franchise franchise = new Franchise("1", "Accenture Corp", null);
        
        when(service.createFranchise(any())).thenReturn(Mono.just(franchise));

        webTestClient.post()
                .uri("/api/franchises")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new FranchiseRequest("Accenture Corp"))
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.name").isEqualTo("Accenture Corp");
    }

    @Test
    void getTopStockEndpointTest() {
        /* chequeo que el endpoint del reporte estrella responda bien */
        when(service.getTopStockProducts("1")).thenReturn(Mono.just(List.of()));

        webTestClient.get()
                .uri("/api/franchises/1/top-stock")
                .exchange()
                .expectStatus().isOk();
    }
}
