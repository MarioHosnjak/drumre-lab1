package com.drumre.LAB1.service;

import ch.qos.logback.core.net.SyslogOutputStream;
import com.drumre.LAB1.model.Movie;
import com.drumre.LAB1.model.User;
import com.drumre.LAB1.repository.MovieRepository;
import com.drumre.LAB1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class MovieService {

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RestTemplate restTemplate;

    public Movie fetchAndSaveMovie(String imdbID) {

        String url = String.format("http://www.omdbapi.com/?i=%s&apikey=73bf0fcd", imdbID);
        System.out.println("Fetching movie from URL: " + url);

        String jsonResponse = restTemplate.getForObject(url, String.class);
        System.out.println("Received response: " + jsonResponse);

        Movie movie = restTemplate.getForObject(url, Movie.class);

        if (movie != null) {
            return movieRepository.save(movie);
        } else {
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

        Query query = new Query();

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

        long total = mongoTemplate.count(Query.of(query).limit(-1).skip(-1), Movie.class);

        query.skip((long) page * size).limit(size);

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

    public List<Movie> getLikedMoviesByUser(String userId) {
        // Fetch the full User document
        Optional<User> optionalUser = userRepository.findById(userId);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            List<String> likedMovieIds = user.getLikedMovies();
            System.out.println("Liked Movie IDs: " + likedMovieIds);

            // If the user has liked no movies, return an empty list
            if (likedMovieIds == null || likedMovieIds.isEmpty()) {
                return Collections.emptyList();
            }

            // Use custom query to fetch movies by IDs
            Query query = new Query(Criteria.where("id").in(likedMovieIds));
            List<Movie> likedMovies = mongoTemplate.find(query, Movie.class);

            return likedMovies;
        } else {
            return Collections.emptyList();
        }
    }
}
