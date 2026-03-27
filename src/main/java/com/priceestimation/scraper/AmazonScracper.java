package com.priceestimation.scraper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;

public class AmazonScracper {
    
    public static void main(String[] args) {
        try {
            // Test with a safe, scrape-friendly website
            String product = "mobile";
            String url = "https://www.amazon.in/s?k=" + product;
            
            System.out.println("Fetching prices for: " + product);
            System.out.println("Connecting to: " + url);
            
            // Connect with browser-like headers
            Document doc = Jsoup.connect(url)
            	    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
            	    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
            	    .header("Accept-Language", "en-US,en;q=0.5")
            	    .header("Accept-Encoding", "gzip, deflate")
            	    .header("Connection", "keep-alive")
            	    .timeout(10000)
            	    .get();
            
            // Try to find price elements (Amazon's selectors)
            Elements prices = doc.select(".a-price-whole");
            Elements titles = doc.select(".a-size-medium");
            
            System.out.println("\n--- RESULTS ---");
            int count = Math.min(prices.size(), 5); // Show first 5
            
            for (int i = 0; i < count; i++) {
                String title = titles.size() > i ? titles.get(i).text() : "N/A";
                String price = prices.size() > i ? prices.get(i).text() : "N/A";
                System.out.println((i+1) + ". " + title);
                System.out.println("   Price: ₹" + price + "\n");
            }
            
        } catch (IOException e) {
            System.err.println("Error scraping: " + e.getMessage());
        }
    }
}