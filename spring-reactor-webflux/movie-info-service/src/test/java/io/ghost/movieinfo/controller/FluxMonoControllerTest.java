package io.ghost.movieinfo.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

@WebFluxTest(controllers = FluxMonoController.class)
@AutoConfigureWebTestClient
class FluxMonoControllerTest {

    @Autowired
    WebTestClient webTestClient;

    @Test
    void getMono() {
        webTestClient.get()
                .uri("/mono")
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(String.class)
                .hasSize(1);
    }

    @Test
    void stream() {
        webTestClient.get()
                .uri("/stream")
                .exchange()
                .expectStatus()
                .is2xxSuccessful();
    }

    @Test
    void getMono_approach2() {
       var mono = webTestClient.get()
                .uri("/mono")
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
               .returnResult(String.class)
               .getResponseBody();
       // Now we validate the content of the returned flux using StepVerifier
        StepVerifier.create(mono)
                .expectNext("Hello Mono")
                .verifyComplete();
    }

    @Test
    void getStream_approach2() {
        var mono = webTestClient.get()
                .uri("/stream")
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .returnResult(Long.class)
                .getResponseBody();

        // Now we validate the content of the returned flux using StepVerifier
        StepVerifier.create(mono)
                .expectNext(0L, 1L,2L,3L)
                .thenCancel()
                .verify();

    }
}