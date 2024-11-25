package com.drumre.LAB1.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.minidev.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Iterator;

@Service
public class StockMarketService {

    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;

    public StockMarketService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.objectMapper = new ObjectMapper();
    }

    public String getIntradayData(String symbol, String interval, String apiKey) {
        String url = String.format(
                "https://www.alphavantage.co/query?function=TIME_SERIES_INTRADAY&symbol=%s&interval=%s&apikey=%s",
                symbol, interval, apiKey
        );

        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            return response.getBody();
        } catch (HttpClientErrorException e) {
            e.printStackTrace();
            return "Error fetching data";
        }
    }

    public double calculateDailyPercentageChange(String stockData) throws Exception {
        if (isRateLimitExceeded(stockData)) {
            return 0;
        }
        String openPriceStr = getOpenPrice(stockData);
        String latestPriceStr = getLatestPrice(stockData);

        if (openPriceStr != null && latestPriceStr != null) {
            double openPrice = Double.parseDouble(openPriceStr);
            double latestPrice = Double.parseDouble(latestPriceStr);

            // Calculate the percentage change
            double change = ((latestPrice - openPrice) / openPrice) * 100;
            return Math.round(change * 100.0) / 100.0;
        }
        return 0;
    }

    // Method to get the opening price (first data point for the day)
    private String getOpenPrice(String stockData) throws Exception {
        JsonNode json = objectMapper.readTree(stockData);
        JsonNode timeSeries = json.get("Time Series (5min)");
        if (timeSeries == null) {
            throw new Exception("No time series data available.");
        }
        String firstTimestamp = timeSeries.fieldNames().next();
        return timeSeries.get(firstTimestamp).get("1. open").asText();
    }

    private String getLatestPrice(String stockData) throws Exception {
        JsonNode json = objectMapper.readTree(stockData);
        JsonNode timeSeries = json.get("Time Series (5min)");

        System.out.println("Time series: " + timeSeries);
        if (timeSeries == null) {
            throw new Exception("No time series data available.");
        }

        // Get the latest timestamp (most recent data)
        String latestTimestamp = getLatestTimestamp(timeSeries);
        return timeSeries.get(latestTimestamp).get("4. close").asText();
    }

    // Helper method to get the latest timestamp
    private String getLatestTimestamp(JsonNode timeSeries) {
        // Iterate through the keys (timestamps) and return the last one
        Iterator<String> fieldNames = timeSeries.fieldNames();
        String latestTimestamp = null;
        while (fieldNames.hasNext()) {
            latestTimestamp = fieldNames.next(); // Last one in the iteration is the latest timestamp
        }
        return latestTimestamp;
    }

    private boolean isRateLimitExceeded(String stockData) {
        try {
            JsonNode json = objectMapper.readTree(stockData);
            return json.has("Information"); // Check if the "Information" field exists
        } catch (Exception e) {
            e.printStackTrace();
            return false; // If there is an issue parsing, assume no rate limit error
        }
    }
}
