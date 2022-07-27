package io.ghost.movieinfo.controller;

import io.ghost.movieinfo.domain.MovieInfo;
import io.ghost.movieinfo.service.MovieInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v1")
public class MovieInfoController {

    @Autowired
    private MovieInfoService movieInfoService;

    @PostMapping(value = "/movie-info")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @ResponseBody
    public Mono<MovieInfo> postMovieInfo(@RequestBody(required = true) MovieInfo movieInfo) {
        return this.movieInfoService.persistMovieInfo(movieInfo);
    }

    @GetMapping(value = "/movie-infos")
    public Flux<MovieInfo> getAllMovieInfos() {
        return this.movieInfoService.getAllMovieInfos();
    }

    @GetMapping(value = "/movie-infos/{id}")
    public Mono<MovieInfo> getMovieInfosById(@PathVariable String id) {
        return this.movieInfoService.getMovieInfoById(id);
    }

    @DeleteMapping(value = "/movie-infos/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteMovieInfosById(@PathVariable String id) {
        return this.movieInfoService.deleteMovieInfoById(id);
    }
}
