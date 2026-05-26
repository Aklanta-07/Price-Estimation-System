package com.priceestimation.model;

import java.util.Date;
import java.util.List;

public class SearchHistory {
	private Long searchId;
	private Long userId;
	private String searchKeyword;
	private String searchType;
	private Date searchDate;
	private List<Product> results;
	
	public SearchHistory() {}
	
	public SearchHistory(Long userId, String searchKeyword, String serachType) {
		super();
		this.userId = userId;
		this.searchKeyword = searchKeyword;
		this.searchType = serachType;
		this.searchDate = new Date();
	}

	public Long getSearchId() {
		return searchId;
	}

	public void setSearchId(Long serachId) {
		this.searchId = serachId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getSearchKeyword() {
		return searchKeyword;
	}

	public void setSearchKeyword(String searchKeyword) {
		this.searchKeyword = searchKeyword;
	}

	public String getSearchType() {
		return searchType;
	}

	public void setSearchType(String serachType) {
		this.searchType = serachType;
	}

	public Date getSearchDate() {
		return searchDate;
	}

	public void setSearchDate(Date searchDate) {
		this.searchDate = searchDate;
	}

	public List<Product> getResults() {
		return results;
	}

	public void setResults(List<Product> results) {
		this.results = results;
	}
	
	
}
