package com.drumre.LAB1.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Arrays;
import java.util.List;

@Document(collection = "movies")
public class Movie {

    @Id
    private String id;

    @JsonProperty("Title")
    private String title;

    @JsonProperty("Year")
    private String year;

    @JsonProperty("Rated")
    private String rated;

    @JsonProperty("Released")
    private String released;

    @JsonProperty("Runtime")
    private String runtime;

    @JsonProperty("Genre")
    private String genre;

    @JsonProperty("Director")
    private String director;

    @JsonProperty("Writer")
    private String writer;

    @JsonProperty("Actors")
    private String actors;

    @JsonProperty("Plot")
    private String plot;

    @JsonProperty("Language")
    private String language;

    @JsonProperty("Country")
    private String country;

    @JsonProperty("Awards")
    private String awards;

    @JsonProperty("Poster")
    private String poster;

    @JsonProperty("Ratings")
    private List<Rating> ratings;

    @JsonProperty("Metascore")
    private String metascore;

    @JsonProperty("imdbRating")
    private String imdbRating;

    @JsonProperty("imdbVotes")
    private String imdbVotes;

    @Indexed(unique = true)
    @JsonProperty("imdbID")
    private String imdbID;

    @JsonProperty("Type")
    private String type;

    @JsonProperty("DVD")
    private String dvd;

    @JsonProperty("BoxOffice")
    private int boxOffice;

    @JsonProperty("Production")
    private String production;

    @JsonProperty("Website")
    private String website;

    @JsonProperty("Response")
    private String response;

    public static class Rating {
        @JsonProperty("Source")
        private String source;

        @JsonProperty("Value")
        private String value;

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getRated() {
        return rated;
    }

    public void setRated(String rated) {
        this.rated = rated;
    }

    public String getReleased() {
        return released;
    }

    public void setReleased(String released) {
        this.released = released;
    }

    public String getRuntime() {
        return runtime;
    }

    public void setRuntime(String runtime) {
        this.runtime = runtime;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getWriter() {
        return writer;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }

    public String getActors() {
        return actors;
    }

    public void setActors(String actors) {
        this.actors = actors;
    }

    public String getPlot() {
        return plot;
    }

    public void setPlot(String plot) {
        this.plot = plot;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getAwards() {
        return awards;
    }

    public void setAwards(String awards) {
        this.awards = awards;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public List<Rating> getRatings() {
        return ratings;
    }

    public void setRatings(List<Rating> ratings) {
        this.ratings = ratings;
    }

    public String getMetascore() {
        return metascore;
    }

    public void setMetascore(String metascore) {
        this.metascore = metascore;
    }

    public String getImdbRating() {
        return imdbRating;
    }

    public void setImdbRating(String imdbRating) {
        this.imdbRating = imdbRating;
    }

    public String getImdbVotes() {
        return imdbVotes;
    }

    public void setImdbVotes(String imdbVotes) {
        this.imdbVotes = imdbVotes;
    }

    public String getImdbID() {
        return imdbID;
    }

    public void setImdbID(String imdbID) {
        this.imdbID = imdbID;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDvd() {
        return dvd;
    }

    public void setDvd(String dvd) {
        this.dvd = dvd;
    }

    public int getBoxOffice() {
        return boxOffice;
    }

    public void setBoxOffice(int boxOffice) {
        this.boxOffice = boxOffice;
    }

    public String getProduction() {
        return production;
    }

    public void setProduction(String production) {
        this.production = production;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }



    public double calculateOverallRating(Movie movie, double stockMarketChange) {
        double imdbRatingValue = 0.0;
        double rottenTomatoesRating = 0.0;
        double metacriticRating = 0.0;

        imdbRatingValue = parseRating(movie.getImdbRating());

        for (Movie.Rating rating : movie.getRatings()) {
            if ("Rotten Tomatoes".equals(rating.getSource())) {
                rottenTomatoesRating = parseRating(rating.getValue());
            } else if ("Metacritic".equals(rating.getSource())) {
                metacriticRating = parseRating(rating.getValue());
            }
        }

        double baseRating = imdbRatingValue + rottenTomatoesRating + metacriticRating;

        double bonusPoints;
        if (stockMarketChange > 0.5 && movie.getTitle().equals("The Wolf of Wall Street")) {
            bonusPoints = 10;
        } else {
            bonusPoints = calculateBonusPoints(stockMarketChange, movie.getGenre());
        }

        return baseRating + bonusPoints;
    }

    private double calculateBonusPoints(double stockMarketChange, String genre) {
        double bonusPoints = 0.0;
        List<String> goodGenres = Arrays.asList("Comedy", "Action", "Adventure", "Sci-Fi");
        List<String> badGenres = Arrays.asList("Horror", "Crime", "War", "Drama");

        if (stockMarketChange > 0) {
            if (goodGenres.stream().anyMatch(genre::contains)) {
                bonusPoints = calculateRewardPoints(stockMarketChange);
            }
        } else if (stockMarketChange < 0) {
            if (badGenres.stream().anyMatch(genre::contains)) {
                bonusPoints = calculateRewardPoints(stockMarketChange);
            }
        }
        return bonusPoints;
    }

    private double calculateRewardPoints(double stockMarketChange) {
        if (stockMarketChange > 1.0) {
            return 4;
        } else if (stockMarketChange > 0.5) {
            return 3;
        } else if (stockMarketChange > 0.25) {
            return 2;
        } else if (stockMarketChange >= 0) {
            return 1;
        } else if (stockMarketChange < -1.0) {
            return 4;
        } else if (stockMarketChange < -0.5) {
            return 3;
        } else if (stockMarketChange < -0.25) {
            return 2;
        } else if (stockMarketChange < 0) {
            return 1;
        }
        return 0.0;
    }


    private double parseRating(String rating) {
        try {
            if (rating.contains("/100")) {
                String[] parts = rating.split("/");
                return Double.parseDouble(parts[0].trim())/10;
            }
            if (rating.contains("/10")) {
                String[] parts = rating.split("/");
                return Double.parseDouble(parts[0].trim());
            }
            if (rating.contains("%")) {
                return Double.parseDouble(rating.replace("%", "").trim()) / 10.0;
            }
            return Double.parseDouble(rating);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
}
