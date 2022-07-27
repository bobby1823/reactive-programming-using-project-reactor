package io.ghost.movieinfo.controller;

import io.ghost.movieinfo.domain.MovieInfo;
import io.ghost.movieinfo.service.MovieInfoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.Hashtable;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = MovieInfoController.class)
@AutoConfigureWebTestClient
public class MovieInfoControllerUnitTest {

    private static String MOVIE_BASE_URI = "/v1";

    @Autowired
    WebTestClient webTestClient;

    @MockBean
    MovieInfoService movieInfoService;

    @Test
    public void getAllMoviesTest() {
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
        when(this.movieInfoService.getAllMovieInfos())
                .thenReturn(Flux.fromIterable(movieInfoList));

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
    public void getMoviesByIdTest() {
        when(this.movieInfoService.getMovieInfoById("TDR"))
                .thenReturn(Mono.just(MovieInfo.builder()
                        .movieInfoId("TDR").name("The Dark Knight Rises")
                        .releaseDate(LocalDate.parse("2005-06-15")).casts(
                                List.of("Christian Bale", "Michael Cane"))
                        .year(2012).build()));
        Hashtable<Integer, String> hashtable = new Hashtable<>();
        hashtable.put(1, "");
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
    public void deleteMovieInfoByIdTest() {
        when(this.movieInfoService.deleteMovieInfoById("TDR"))
                .thenReturn(Mono.justOrEmpty(Optional.empty()));

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
