package com.drumre.LAB1.controller;

import com.drumre.LAB1.model.Movie;
import com.drumre.LAB1.service.MovieSeederService;
import com.drumre.LAB1.service.MovieService;
import com.drumre.LAB1.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/movies")
public class MovieController {
    @Autowired
    private MovieService movieService;
    @Autowired
    private MovieSeederService movieSeederService;
    @Autowired
    private UserService userService;

    @GetMapping("/all")
    public Page<Movie> getAllMovies(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return movieService.getAll(page, size);
    }

    @GetMapping("/save/{imdbID}")
    public Movie saveMovie(@PathVariable String imdbID) {
        return movieService.fetchAndSaveMovie(imdbID);
    }

    @GetMapping("/save/seed")
    public Boolean seedMoviesFromTxt() throws IOException {
        return movieSeederService.seedMovies();
    }

    @GetMapping("/filter")
    public Page<Movie> getMoviesByFilters(@RequestParam Map<String, String> filters,
                                          @RequestParam(value = "sortBy", required = false) String sortBy,
                                          @RequestParam(value = "sortDirection", required = false) String sortDirection,
                                          @RequestParam(value = "page", defaultValue = "0") int page,
                                          @RequestParam(value = "size", defaultValue = "10") int size) {

        if (sortDirection == null || sortDirection.isEmpty()) {
            sortDirection = "asc";  // Default sort direction
        }

        return movieService.getMoviesByFilters(filters, sortBy, sortDirection, page, size);
    }

    @DeleteMapping("/delete/{movie_id}")
    public String deleteMovie(@PathVariable("movie_id") String movieId) {
        boolean isDeleted = movieService.deleteMovieById(movieId);

        if (isDeleted) {
            return "Movie with ID " + movieId + " has been successfully deleted.";
        } else {
            return "Failed to delete movie with ID " + movieId + ". It may not exist.";
        }
    }

    @PostMapping("/like")
    public void likeMovie(@RequestParam String userId, @RequestParam String movieId) {
        userService.likeMovie(userId, movieId);
    }

    @PostMapping("/unlike")
    public void unlikeMovie(@RequestParam String userId, @RequestParam String movieId) {
        userService.unlikeMovie(userId, movieId);
    }

    @GetMapping("/liked-movies")
    public ResponseEntity<List<Movie>> getLikedMovies(@RequestParam String userId) {
        List<Movie> likedMovies = movieService.getLikedMoviesByUser(userId);
        return ResponseEntity.ok(likedMovies);
    }

    @GetMapping("/top30")
    public List<Movie> getTop30Movies(@RequestParam double stockMarketChange) {
        return movieService.getTop30Movies(stockMarketChange);
    }
}

