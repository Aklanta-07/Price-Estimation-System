package com.priceestimation.scraper;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.priceestimation.model.Product;

public class TestScraper {
	
	public static void main(String[] args) {
		TestScraper scraper = new TestScraper();
        List<Product> products = scraper.scrapeBooks("books");
        
        System.out.println("\n📚 Scraped " + products.size() + " products\n");
        System.out.println("Top 5 Results:");
        System.out.println("------------");
        
        for (int i = 0; i < Math.min(products.size(), 5); i++) {
            Product p = products.get(i);
            System.out.println((i+1) + ". " + p.getName());
            System.out.println("   Price: " + p.getPrice() + " " + p.getCurrency());
            System.out.println("   Source: " + p.getSourceWebsite());
            System.out.println();
        }
	}
	
	public List<Product> scrapeBooks(String keyword) {
		List<Product> products = new ArrayList<>();
		
		try {
			String url =  "http://books.toscrape.com/";
			System.out.println("🔍 Connecting to: " + url);
			
			org.jsoup.nodes.Document doc = Jsoup.connect(url).userAgent("Mozilla/5.0")
					.timeout(10000).get();
			
			//get all books
			Elements books = doc.select(".product_pod") ;
			
			for(Element book : books) {
				String title = book.select("h3 a").attr("title");
                String priceText = book.select(".price_color").text();
                
                double price = cleanPrice(priceText);
                
                Product product = new Product();
                product.setName(title);
                product.setPrice(price);
                product.setCurrency("GBP"); // books.toscrape.com uses GBP
                product.setSourceWebsite("Books to Scrape");
                product.setSerachKeyword(keyword);
                
                products.add(product);
                
		 }
			
		}catch (Exception e) {
			System.err.println("❌ Error: " + e.getMessage());
		}
		
		return products;
	}
	
	public double cleanPrice(String priceText) {
		String cleaned = priceText.replaceAll("[^0-9.]", "");
        try {
            return Double.parseDouble(cleaned);
        } catch (NumberFormatException e) {
            return 0.0;
        }
	}
	
}
