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

        JsonNode json = objectMapper.readTree(stockData);
        JsonNode timeSeries = json.get("Time Series (5min)");

        if (timeSeries == null) {
            throw new Exception("No time series data available.");
        }

        Iterator<String> fieldNames = timeSeries.fieldNames();
        String oldestTimestamp = null;
        String newestTimestamp = null;

        while (fieldNames.hasNext()) {
            if (oldestTimestamp == null) {
                oldestTimestamp = fieldNames.next();
            }
            newestTimestamp = fieldNames.next();
        }

        if (oldestTimestamp == null || newestTimestamp == null) {
            throw new Exception("Unable to determine timestamps.");
        }

        // API daje podatke od najnovijeg prema najstarijem
        String tmp = oldestTimestamp;
        oldestTimestamp = newestTimestamp;
        newestTimestamp = tmp;

        double oldestPrice = Double.parseDouble(timeSeries.get(oldestTimestamp).get("1. open").asText());
        double newestPrice = Double.parseDouble(timeSeries.get(newestTimestamp).get("4. close").asText());

        double change = ((newestPrice - oldestPrice) / oldestPrice) * 100;
        return Math.round(change * 100.0) / 100.0;
    }

    private boolean isRateLimitExceeded(String stockData) {
        try {
            JsonNode json = objectMapper.readTree(stockData);
            return json.has("Information");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
