package com.drumre.LAB1.controller;

import com.drumre.LAB1.service.StockMarketService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StockMarketController {

    /*@Value("${stock.api.key}") // Store your API key securely
    private String apiKey;*/

    private final StockMarketService stockMarketService;

    public StockMarketController(StockMarketService stockMarketService) {
        this.stockMarketService = stockMarketService;
    }

    @GetMapping("/market")
    public String getStockMarketData(Model model) throws Exception {
        String symbol = "SPY"; // S&P 500
        String interval = "5min"; // Set to 5min for current day intraday data

        // Get the stock data as JSON
        //String stockData = stockMarketService.getIntradayData(symbol, interval, "D1Q7F39T63SD58ZO");
        String stockData = stockMarketService.getIntradayData(symbol, interval, "6H5AE3I44954WNEK");

        // Calculate the daily percentage change and add to the model
        double percentageChange = stockMarketService.calculateDailyPercentageChange(stockData);
        model.addAttribute("stockData", stockData);
        model.addAttribute("percentageChange", percentageChange);

        return "market"; // Return the Thymeleaf template name
    }

}
