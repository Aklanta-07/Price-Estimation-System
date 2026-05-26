package com.priceestimation.scraper;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.priceestimation.model.Product;

public class WebScraper {
	
	private List<SiteConfig> sites;
	private ExecutorService executor;
	
	public  WebScraper() {
		initializeSites();
		executor = Executors.newFixedThreadPool(sites.size());
	}
	
	private void initializeSites() {
		sites = Arrays.asList(
				
				new SiteConfig(
		                "Flipkart",
		                "https://www.flipkart.com",
		                "/search?q={keyword}",
		                "._30jeq3._1_WHN1",
		                "._4rR01T",
		                "._1fQZEK"
		            ),
				
	            // Amazon India
	            new SiteConfig(
	                "Amazon India",
	                "https://www.amazon.in",
	                "/s?k={keyword}",
	                ".a-price-whole",
	                ".a-size-medium",
	                ".a-link-normal.s-link-style"
	            )
	           
	            // Add more sites as needed
	        );
	}
	
	public List<Product> search(String keyword) throws Exception {
		String encodedKeyword = URLEncoder.encode(keyword, StandardCharsets.UTF_8);
		List<Product> allProduct = new CopyOnWriteArrayList<>();
		CountDownLatch latch = new CountDownLatch(sites.size());
		
		for(SiteConfig site : sites) {
			executor.submit(() -> {
				try {
					List<Product> products = scrapeSite(site, encodedKeyword);
					allProduct.addAll(products);
					System.out.println("✅ Scraped " + products.size() + " products from " + site.getName());
					
				}catch (Exception e) {
					System.err.println("❌ Error scraping " + site.getName() + ": " + e.getMessage());
					
				}finally {
					latch.countDown();
				}
			});
		}
		
	        latch.await(30, TimeUnit.SECONDS);
		 
	        return allProduct;
	}
	
	private List<Product> scrapeSite(SiteConfig site, String keyword) {
		List<Product> products = new ArrayList<>();
		String url = site.getSearchUrl(keyword);
		
		try {
			System.out.println("🌐 Connecting to: " + site.getName());
			
			Document doc = Jsoup.connect(url)
				    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
				    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8")
				    .header("Accept-Language", "en-US,en;q=0.9")
				    .header("Accept-Encoding", "gzip, deflate, br")
				    .header("Connection", "keep-alive")
				    .header("Upgrade-Insecure-Requests", "1")
				    .header("Sec-Fetch-Dest", "document")
				    .header("Sec-Fetch-Mode", "navigate")
				    .header("Sec-Fetch-Site", "none")
				    .header("Cache-Control", "max-age=0")
				    .timeout(15000)
				    .get();
			
			Elements priceElements = doc.select(site.getPriceSelector());
			Elements nameElements = doc.select(site.getNameSelector());
			Elements linkElements = doc.select(site.getLinkSelector());
			
			int resultCount = Math.min(priceElements.size(), 10); //Limit 10 per site
			
			for(int i = 0; i < resultCount; i++) {
				try {
					String name = nameElements.size() > i ? 	nameElements.get(i).text() : "N/A";
					String priceText = priceElements.size() > i ? priceElements.get(i).text() : "0";
					String link = linkElements.size() > i ? linkElements.get(i).absUrl("href") : "";
					
					double price = cleanPrice(priceText);
					
					if(price > 0) {
						Product product = new Product();
						product.setName(name);
                        product.setPrice(price);
                        product.setSourceWebsite(site.getName());
                        product.setSourceUrl(link);
                        product.setSearchKeyword(keyword);
                        products.add(product);
					}
					
				}catch (Exception e) {
					System.err.println("Error parsing product from " + site.getName() + ": " + e.getMessage());
				}
				
				Thread.sleep(1000);
			}
			
		} catch (IOException e) {
            System.err.println("Failed to connect to " + site.getName() + ": " + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
		
		return products;
	}
	
	private double cleanPrice(String priceText) {
        // Remove currency symbols, commas, and non-numeric characters
        String cleaned = priceText.replaceAll("[^0-9.]", "");
        try {
            return Double.parseDouble(cleaned);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
	
	public void shutDown() {
		executor.shutdown();
		try {
			executor.awaitTermination(10, TimeUnit.SECONDS);
		}catch (Exception e) {
			executor.shutdown();
		}
	}
	
	
	
}
