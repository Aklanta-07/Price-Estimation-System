package com.priceestimation.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.priceestimation.dbconnection.DBConnection;
import com.priceestimation.model.Product;
import com.priceestimation.model.SearchHistory;

public class SearchHistoryDAO {
	
	public void saveSearch(SearchHistory search, List<Product> products) throws Exception {
		Connection conn = null;
		PreparedStatement searchStmt = null;
		PreparedStatement resultStmt = null;
		ResultSet generatedKeys = null;
		
		try {
			conn = DBConnection.getConnection();
			conn.setAutoCommit(false);
			
			String searchSql = "INSERT INTO search_history (search_id, user_id, search_keyword, search_type) " +
                    "VALUES (search_seq.NEXTVAL, ?, ?, ?)";
			
		   searchStmt = conn.prepareStatement(searchSql, new String[] {"search_id"});
		   searchStmt.setLong(1, search.getUserId());
		   searchStmt.setString(2, search.getSearchKeyword());
		   searchStmt.setString(3, search.getSearchType());
           searchStmt.executeUpdate();
           
           generatedKeys = searchStmt.getGeneratedKeys();
           Long searchId = null;
           if(generatedKeys.next()) {
        	   searchId = generatedKeys.getLong(1);
           }
           if(searchId == null) {
        	   throw new SQLException("Failed to get search_id");
           }
           
           String resultSql = "INSERT INTO scraped_results (result_id, search_id, product_name, price, source_website, source_url) " +
                   "VALUES (results_seq.NEXTVAL, ?, ?, ?, ?, ?)";
           resultStmt = conn.prepareStatement(resultSql);
           
           for(Product product : products) {
        	   resultStmt.setLong(1, searchId);
               resultStmt.setString(2, product.getName());
               resultStmt.setDouble(3, product.getPrice());
               resultStmt.setString(4, product.getSourceWebsite());
               resultStmt.setString(5, product.getSourceUrl());
               resultStmt.addBatch();
           }
           
           resultStmt.executeBatch();
           conn.commit();
           System.out.println("✅ Saved " + products.size() + " products to database");
			
		}catch (Exception e) {
			try {
				if(conn != null) {
					conn.rollback();
				}
			}catch (SQLException rollback) {
				rollback.printStackTrace();
			}
			System.err.println("❌ Failed to save search: " + e.getMessage());
            e.printStackTrace();
            
		}finally {
			try {
                if (generatedKeys != null) generatedKeys.close();
                if (searchStmt != null) searchStmt.close();
                if (resultStmt != null) resultStmt.close();
                if (conn != null) conn.setAutoCommit(true);
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public List<SearchHistory> getSearchHistory(Long userId, int limit) {
		List<SearchHistory> historyList = new ArrayList<>();
		String sql = "SELECT search_id, search_keyword, search_type, search_date " +
                "FROM search_history WHERE user_id = ? " +
                "ORDER BY search_date DESC";
		
		if(limit > 0 ) {
			sql = "SELECT * FROM (" + sql + ") WHERE ROWNUM <= ?";
		}
		
		try (Connection conn = DBConnection.getConnection();
	             PreparedStatement pstmt = conn.prepareStatement(sql)) {
			
			pstmt.setLong(1, userId);
			if(limit > 0) {
				pstmt.setInt(2, limit);
			}
			
			ResultSet rs = pstmt.executeQuery();
			
			while(rs.next()) {
				SearchHistory history = new SearchHistory();
				 history.setSearchId(rs.getLong("search_id"));
	             history.setSearchKeyword(rs.getString("search_keyword"));
	             history.setSearchType(rs.getString("search_type"));
	             history.setSearchDate(rs.getTimestamp("search_date"));
	             history.setResults(getResultsForSearch(history.getSearchId()));
	             
	             historyList.add(history);
			}
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return historyList;
	}
	
	public List<Product> getResultsForSearch(Long searchId) {
		List<Product> products = new ArrayList<>();
		String sql = "SELECT product_name, price, source_website, source_url " +
                "FROM scraped_results WHERE search_id = ?";
		
		try (Connection conn = DBConnection.getConnection();
	             PreparedStatement pstmt = conn.prepareStatement(sql)) {
			
			pstmt.setLong(1, searchId);
			ResultSet rs = pstmt.executeQuery();
			
			while(rs.next()) {
				Product product = new Product();
				product.setName(rs.getString("product_name"));
                product.setPrice(rs.getDouble("price"));
                product.setSourceWebsite(rs.getString("source_website"));
                product.setSourceUrl(rs.getString("source_url"));
                
                products.add(product);
			}
			    
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return products;
	}
	
	public void cleanupOldHistory(Long userId, int keepCount) {
		String sql = "DELETE FROM search_history WHERE user_id = ? AND search_id NOT IN (" +
                "SELECT search_id FROM (" +
                "SELECT search_id FROM search_history WHERE user_id = ? " +
                "ORDER BY search_date DESC) WHERE ROWNUM <= ?)";
		
		try (Connection conn = DBConnection.getConnection();
	             PreparedStatement pstmt = conn.prepareStatement(sql)) {
			
			 pstmt.setLong(1, userId);
	         pstmt.setLong(2, userId);
	         pstmt.setInt(3, keepCount);
	         pstmt.executeUpdate();
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
