package io.ghost.movieinfo.service;

import io.ghost.movieinfo.domain.MovieInfo;
import io.ghost.movieinfo.repository.MovieInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class MovieInfoService {

    @Autowired
    private MovieInfoRepository movieInfoRepository;

    public Mono<MovieInfo> persistMovieInfo(MovieInfo movieInfo) {
        return this.movieInfoRepository.save(movieInfo);
    }

    public Flux<MovieInfo> getAllMovieInfos() {
        return this.movieInfoRepository.findAll();
    }

    public Mono<MovieInfo> getMovieInfoById(String movieInfoId) {
        return this.movieInfoRepository.findById(movieInfoId);
    }

    public Mono<Void> deleteMovieInfoById(String id) {
        return this.movieInfoRepository.deleteById(id);
    }
}
