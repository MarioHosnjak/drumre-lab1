package com.drumre.LAB1.service;

import com.drumre.LAB1.model.Movie;
import com.drumre.LAB1.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MovieSeederService {

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private RestTemplate restTemplate;

    private static final String OMDB_API_KEY = "73bf0fcd";

    public Boolean seedMovies() throws IOException {
        Resource resource = new ClassPathResource("static/ids.txt");
        String content = new String(FileCopyUtils.copyToByteArray(resource.getInputStream()));

        List<String> movieIds = content.lines()
                .map(String::trim)
                .collect(Collectors.toList());

        for (String imdbID : movieIds) {
            try {
                System.out.println(imdbID);
                fetchAndSaveMovie(imdbID);
            } catch (Exception e) {
                System.err.println("Error processing movie ID: " + imdbID + ", " + e.getMessage());
                return false;
            }
        }
        return true;
    }

    private void fetchAndSaveMovie(String imdbID) {
        String url = String.format("http://www.omdbapi.com/?i=%s&apikey=%s", imdbID, OMDB_API_KEY);
        Movie movie = restTemplate.getForObject(url, Movie.class);

        if (movie != null) {
            if (!movieRepository.existsByImdbID(imdbID)) {
                movieRepository.save(movie);
                System.out.println("Movie saved: " + movie.getTitle());
            } else {
                System.out.println("Movie with imdbID " + imdbID + " already exists in the database.");
            }
        } else {
            System.err.println("Movie not found for imdbID: " + imdbID);
        }
    }
}
