package io.ghost.movieinfo.controller;

import io.ghost.movieinfo.domain.MovieInfo;
import io.ghost.movieinfo.repository.MovieInfoRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.net.http.HttpRequest;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
// This give the application context for testing
@ActiveProfiles("test")
// This provides the testing profile for test DB (Embedded Mongo)
//@WebFluxTest(controllers = MovieInfoController.class)
@TestPropertySource(properties = "spring.mongodb.embedded.version=3.5.5")
@AutoConfigureWebTestClient
class MovieInfoControllerTest {

    @Autowired
    private MovieInfoRepository movieInfoRepository;

    @Autowired
    private WebTestClient webTestClient;

    private static String MOVIE_BASE_URI = "/v1";
    // This is the setup method which pushes the data in embedded DB
    @BeforeEach
    void setUp() {
        var movieInfoList = List.of(
                MovieInfo.builder()
                        .movieInfoId(null).name("Batman Begins")
                        .releaseDate(LocalDate.parse("2005-06-15")).casts(
                                List.of("Christian Bale", "Michael Cane"))
                        .year(2005).build(),
                MovieInfo.builder()
                        .movieInfoId(null).name("The Dark Knight")
                        .releaseDate(LocalDate.parse("2005-06-15")).casts(
                                List.of("Christian Bale", "Michael Cane"))
                        .year(2008).build(),
                MovieInfo.builder()
                        .movieInfoId("TDR").name("The Dark Knight Rises")
                        .releaseDate(LocalDate.parse("2005-06-15")).casts(
                                List.of("Christian Bale", "Michael Cane"))
                        .year(2012).build()
        );
        /**
         * Here we are making the statment block so that first setUp() is done then only test case starts
         * blockLast() is used.
         */
        this.movieInfoRepository.saveAll(movieInfoList)
                .blockLast();
    }

    @AfterEach
    void tearDown() {
        this.movieInfoRepository.deleteAll().block();
    }


    @Test
    void postMovieInfo() {
         webTestClient.post()
                .uri(MOVIE_BASE_URI + "/movie-info")
                .bodyValue(MovieInfo.builder()
                        .movieInfoId(null).name("Andhadun")
                        .releaseDate(LocalDate.parse("2018-06-15")).casts(
                                List.of("Ayushmann Khuranna", "Tabu"))
                        .year(2018).build())
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    var movieInfo = movieInfoEntityExchangeResult.getResponseBody();
                    assertTrue(movieInfo != null);
                    assertTrue(movieInfo.getMovieInfoId() != null);
                });

    }

    @Test
    void getAllMovieInfos() {
        var movieInfosFlux = webTestClient.get()
                .uri(MOVIE_BASE_URI+"/movie-infos")
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .returnResult(MovieInfo.class)
                .getResponseBody();

        //Then
        StepVerifier.create(movieInfosFlux)
                .expectNextCount(3)
                .verifyComplete();

    }

    @Test
    void getMovieInfosBy() {
        webTestClient.get()
                .uri(MOVIE_BASE_URI+"/movie-infos/TDR")
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfoFluxExchangeResult -> {
                            var mono = movieInfoFluxExchangeResult.getResponseBody();
                            assertTrue(mono != null);
                            assertTrue("TDR".equals(mono.getMovieInfoId()));
                });
    }

    @Test
    void deleteMovieInfosBy() {
        webTestClient.delete()
                .uri(MOVIE_BASE_URI+"/movie-infos/TDR")
                .exchange()
                .expectStatus()
                .isNoContent()
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfoFluxExchangeResult -> {
                    var mono = movieInfoFluxExchangeResult.getResponseBody();
                    assertTrue(mono == null);
                });
    }
}