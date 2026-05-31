package com.tradinglog.app.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MarketPriceService {

    private static final String API_KEY = "PASTE_YOUR_API_KEY_HERE";

    public static double getLivePrice(String symbol) throws Exception {
        if (symbol == null || symbol.trim().isEmpty()) {
            throw new IllegalArgumentException("Symbol cannot be empty");
        }

        String encodedSymbol = URLEncoder.encode(symbol.trim(), StandardCharsets.UTF_8);
        String urlStr = "https://www.alphavantage.co/query?function=GLOBAL_QUOTE"
                + "&symbol=" + encodedSymbol
                + "&apikey=" + API_KEY;

        HttpURLConnection con = (HttpURLConnection) URI.create(urlStr).toURL().openConnection();
        con.setRequestMethod("GET");
        con.setConnectTimeout(10000);
        con.setReadTimeout(10000);

        int responseCode = con.getResponseCode();
        BufferedReader reader;

        if (responseCode >= 200 && responseCode < 300) {
            reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
        } else {
            reader = new BufferedReader(new InputStreamReader(con.getErrorStream()));
        }

        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();

        String json = response.toString();

        if (json.contains("Error Message")) {
            throw new RuntimeException("Invalid symbol or API request.");
        }

        if (json.contains("Note")) {
            throw new RuntimeException("API limit reached. Try again later.");
        }

        Pattern pattern = Pattern.compile("\"05\\. price\"\\s*:\\s*\"([0-9.]+)\"");
        Matcher matcher = pattern.matcher(json);

        if (matcher.find()) {
            return Double.parseDouble(matcher.group(1));
        }

        throw new RuntimeException("Live price not found in API response.");
    }
}