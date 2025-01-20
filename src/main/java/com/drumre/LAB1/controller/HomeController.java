package com.drumre.LAB1.controller;

import com.drumre.LAB1.model.Movie;
import com.drumre.LAB1.model.User;
import com.drumre.LAB1.service.MovieService;
import com.drumre.LAB1.service.StockMarketService;
import com.drumre.LAB1.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@Controller
public class HomeController {

    private final UserService userService;
    private final MovieService movieService;
    private final StockMarketService stockMarketService;
    @Autowired
    public HomeController(UserService userService, MovieService movieService, StockMarketService stockMarketService) {
        this.userService = userService;
        this.movieService = movieService;
        this.stockMarketService = stockMarketService;
    }

    @GetMapping("/")
    public String homepage(Model model, @AuthenticationPrincipal OidcUser principal) {
        return "index";
    }
    @GetMapping("/movies")
    public String moviesPage(Model model, OAuth2AuthenticationToken authentication) {
        if (authentication != null) {
            String email = authentication.getPrincipal().getAttributes().get("email").toString();
            Optional<User> currentUser = userService.findUserByEmail(email);
            currentUser.ifPresent(user -> model.addAttribute("currentUserId", user.getId()));
        }
        return "movies";
    }
    @GetMapping("/movie-feed-dummy")
    public String getDummyStockMarketData(Model model, OAuth2AuthenticationToken authentication) throws Exception {

        Random random = new Random();
        double percentageChange = -1 + (2 * random.nextDouble());
        percentageChange = Math.round(percentageChange * 100.0) / 100.0;

        List<Movie> top30Popular = movieService.getTop30PopularMovies(percentageChange);

        List<Movie> personalRecommendation;
        if (authentication != null) {
            String email = authentication.getPrincipal().getAttributes().get("email").toString();
            personalRecommendation = movieService.getRecommendations(email);
        } else {
            personalRecommendation = top30Popular;
        }

        while (personalRecommendation.size() < 30) {
            Movie randomMovie = top30Popular.get(random.nextInt(top30Popular.size()));
            if (!personalRecommendation.contains(randomMovie)) {
                personalRecommendation.add(randomMovie);
            }
        }

        model.addAttribute("percentageChange", percentageChange);
        model.addAttribute("top30Popular", top30Popular);
        model.addAttribute("personalRecommendation", personalRecommendation);

        return "MovieFeed";
    }


    @GetMapping("/movie-feed")
    public String getStockMarketData(Model model, OAuth2AuthenticationToken authentication) throws Exception {
        String symbol = "SPY"; // S&P 500
        String interval = "5min";

        //String stockData = stockMarketService.getIntradayData(symbol, interval, "D1Q7F39T63SD58ZO");
        String stockData = stockMarketService.getIntradayData(symbol, interval, "6H5AE3I44954WNEK");
        double percentageChange = stockMarketService.calculateDailyPercentageChange(stockData);

        List<Movie> top30Popular = movieService.getTop30PopularMovies(percentageChange);

        List<Movie> personalRecommendation;
        if (authentication != null) {
            String email = authentication.getPrincipal().getAttributes().get("email").toString();
            personalRecommendation = movieService.getRecommendations(email);
        } else {
            personalRecommendation = top30Popular;
        }

        Random random = new Random();

        while (personalRecommendation.size() < 30) {
            Movie randomMovie = top30Popular.get(random.nextInt(top30Popular.size()));
            if (!personalRecommendation.contains(randomMovie)) {
                personalRecommendation.add(randomMovie);
            }
        }

        model.addAttribute("stockData", stockData);
        model.addAttribute("percentageChange", percentageChange);

        model.addAttribute("top30Popular", top30Popular);
        model.addAttribute("personalRecommendation", personalRecommendation);

        return "RealMovieFeed";
    }
}
