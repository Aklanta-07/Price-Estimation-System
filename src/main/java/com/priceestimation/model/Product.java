package com.priceestimation.model;

import java.util.Date;

public class Product {
	private Long id;
	private String name;
	private double price;
	private String currency;
	private String sourceWebsite;
	private String sourceUrl;
	private String searchKeyword;
	private Date scrapedDate;
	
	public Product() {
		this.currency = "INR";
		this.scrapedDate = new Date();
	}
	
	public Product(String name, Double price, String sourceWebsite) {
		this();
		this.name = name;
		this.price = price;
		this.sourceWebsite = sourceWebsite;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getSourceWebsite() {
		return sourceWebsite;
	}

	public void setSourceWebsite(String sourceWebsite) {
		this.sourceWebsite = sourceWebsite;
	}

	public String getSourceUrl() {
		return sourceUrl;
	}

	public void setSourceUrl(String sourceUrl) {
		this.sourceUrl = sourceUrl;
	}

	public String getSerachKeyword() {
		return searchKeyword;
	}

	public void setSerachKeyword(String serachKeyword) {
		this.searchKeyword = serachKeyword;
	}

	public Date getScrapedDate() {
		return scrapedDate;
	}

	public void setScrapedDate(Date scrapedDate) {
		this.scrapedDate = scrapedDate;
	}
	
	@Override
	public String toString() {
		return "Product [name=" + name + ", price=" + price + ", sourceWebsite=" + sourceWebsite + "]";
	}
	
	}
