package com.priceestimation.scraper;

public class SiteConfig {
	
	private String name;
	private String baseUrl;
    private String searchPath;
    private String priceSelector;
    private String nameSelector;
    private String linkSelector;
    private String nextPageSelector;
    
    public SiteConfig(String name, String baseUrl, String searchPath, 
            String priceSelector, String nameSelector, String linkSelector) {
            this.name = name;
            this.baseUrl = baseUrl;
            this.searchPath = searchPath;
            this.priceSelector = priceSelector;
            this.nameSelector = nameSelector;
            this.linkSelector = linkSelector;
   }
    
    public String getSearchUrl(String keyword) {
        return baseUrl + searchPath.replace("{keyword}", keyword);
    }
    
    public String getName() { return name; }
    public String getBaseUrl() { return baseUrl; }
    public String getSearchPath() { return searchPath; }
    public String getPriceSelector() { return priceSelector; }
    public String getNameSelector() { return nameSelector; }
    public String getLinkSelector() { return linkSelector; }
    public String getNextPageSelector() { return nextPageSelector; }
    
    public void setNextPageSelector(String nextPageSelector) {
        this.nextPageSelector = nextPageSelector;
    }

}
