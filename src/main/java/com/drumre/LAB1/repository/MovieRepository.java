package com.drumre.LAB1.repository;

import com.drumre.LAB1.model.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MovieRepository extends MongoRepository<Movie, String> {
    boolean existsByImdbID(String imdbID);
    List<Movie> findByGenreContaining(String genre);
    Page<Movie> findAll(Pageable pageable);
    List<Movie> findByIdIn(List<String> ids);
}
