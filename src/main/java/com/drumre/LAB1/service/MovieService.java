package com.drumre.LAB1.service;

import com.drumre.LAB1.model.Movie;
import com.drumre.LAB1.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class MovieService {

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private RestTemplate restTemplate;

    public Movie fetchAndSaveMovie(String imdbID) {

        String url = String.format("http://www.omdbapi.com/?i=%s&apikey=73bf0fcd", imdbID);
        System.out.println("Fetching movie from URL: " + url);

        // Fetch the movie data as a Map or String first for debugging
        String jsonResponse = restTemplate.getForObject(url, String.class);
        System.out.println("Received response: " + jsonResponse);

        // Now, attempt to map the response to a Movie object
        Movie movie = restTemplate.getForObject(url, Movie.class);

        if (movie != null) {
            // Save the movie to MongoDB
            return movieRepository.save(movie);
        } else {
            // Handle the case where the movie is not found or the response is invalid
            throw new RuntimeException("Movie not found in OMDB API.");
        }
    }

    public Page<Movie> getAll(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return movieRepository.findAll(pageRequest);
    }

    @Autowired
    private MongoTemplate mongoTemplate;

    public Page<Movie> getMoviesByFilters(Map<String, String> filters,
                                          String sortBy, String sortDirection,
                                          int page, int size) {

        // Create a new query object
        Query query = new Query();

        // Loop through the filters map and apply each filter dynamically
        filters.forEach((key, value) -> {
            if (value != null && !value.isEmpty()) {
                switch (key.toLowerCase()) {
                    case "title":
                        query.addCriteria(Criteria.where("title").regex(value, "i")); // case insensitive regex
                        break;
                    case "genre":
                        query.addCriteria(Criteria.where("genre").regex(value, "i")); // case insensitive regex
                        break;
                    case "year":
                        query.addCriteria(Criteria.where("year").is(value)); // exact match
                        break;
                    case "director":
                        query.addCriteria(Criteria.where("director").regex(value, "i")); // case insensitive regex
                        break;
                    case "language":
                        query.addCriteria(Criteria.where("language").regex(value, "i"));
                        break;
                    case "boxoffice":
                        // Handle boxOffice filter: expect a numeric value
                        try {
                            double boxOfficeThreshold = Double.parseDouble(value.replaceAll("[^\\d.]", ""));
                            query.addCriteria(Criteria.where("boxOffice").gt(boxOfficeThreshold));
                        } catch (NumberFormatException e) {
                            throw new IllegalArgumentException("Invalid boxOffice value: " + value);
                        }
                        break;
                    default:
                        break;
                }
            }
        });

        if (sortBy != null && !sortBy.isEmpty()) {
            Sort.Direction direction = "desc".equalsIgnoreCase(sortDirection) ? Sort.Direction.DESC : Sort.Direction.ASC;
            query.with(Sort.by(direction, sortBy));
        }

        // Get total count for pagination metadata
        long total = mongoTemplate.count(Query.of(query).limit(-1).skip(-1), Movie.class);

        // Pagination logic: apply skip and limit
        query.skip((long) page * size).limit(size);

        // Fetch paginated results
        List<Movie> movies = mongoTemplate.find(query, Movie.class);

        return new PageImpl<>(movies, PageRequest.of(page, size), total);
    }

    public boolean deleteMovieById(String movieId) {
        if (movieRepository.existsById(movieId)) {
            movieRepository.deleteById(movieId);
            return true;
        }
        return false;
    }
}
