package io.ghost.movieinfo.repository;

import io.ghost.movieinfo.domain.MovieInfo;
import io.ghost.movieinfo.repository.MovieInfoRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DataMongoTest
@ActiveProfiles("test")
//@ContextConfiguration(initializers = {MongodbContainerInitializer.class})
//@EnableAutoConfiguration(exclude = EmbeddedMongoAutoConfiguration.class)
@TestPropertySource(properties = "spring.mongodb.embedded.version=3.5.5")
@Slf4j
/**
 * The @ActiveProfiles test is required or else while running test, application will try to connect with actual DB creds
 * here we are using in memory Mongo embedded DB to test
 */
public class MovieInfoRepositoryIntegrationTest {

    @Autowired
    private MovieInfoRepository movieInfoRepository;

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
    public void findAll() {
        var flux = this.movieInfoRepository.findAll().log();

        StepVerifier.create(flux)
                .expectNextCount(3)
                .verifyComplete();
    }

    @Test
    public void findById() {
        var mono = this.movieInfoRepository.findById("TDR").log();

        StepVerifier.create(mono)
                .assertNext(movieInfo -> {
                    assertTrue(movieInfo.getMovieInfoId().equals("TDR"));
                })
                .verifyComplete();
    }

    @Test
    public void save() {
        var movieInfo = MovieInfo.builder()
                .movieInfoId("HP1").name("Harry Potter")
                .releaseDate(LocalDate.parse("2001-06-15")).casts(
                        List.of("Daniel Radcliff", "Snape"))
                .year(2001).build();

        var mono = this.movieInfoRepository.save(movieInfo).log();

        StepVerifier.create(mono)
                .assertNext(m -> {
                    assertTrue(m.getMovieInfoId().equals("HP1"));
                })
                .verifyComplete();
    }

    @Test
    public void saveAndUpdate() {
        var movieInfo = this.movieInfoRepository.findById("TDR").block();
        movieInfo.setMovieInfoId("TDR1");

        var mono = this.movieInfoRepository.save(movieInfo).log();

        StepVerifier.create(mono)
                .assertNext(m -> {
                    assertTrue(m.getMovieInfoId().equals("TDR1"));
                })
                .verifyComplete();
    }

    @Test
    public void delete() {
        var movieInfo = this.movieInfoRepository.findById("TDR").block();
        this.movieInfoRepository.delete(movieInfo).log().block();
        var mono = this.movieInfoRepository.findAll().log();


        StepVerifier.create(mono)
                .expectNextCount(2)
                .verifyComplete();
    }
}
