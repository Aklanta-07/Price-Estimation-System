package com.priceestimation.dao;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;

import com.priceestimation.dbconnection.DBConnection;
import com.priceestimation.model.User;

public class UserDAO {
	
	public boolean register(User user	) {
		String sql =
				"INSERT INTO users (user_id, username, password, email, full_name) VALUES (users_seq.NEXTVAL, ?, ?, ?, ?)";
				
		try(Connection con = DBConnection.getConnection();
				PreparedStatement pstmt = con.prepareStatement(sql)) {
			
			String hashedPassword = hashPassword(user.getPassword());
			
			pstmt.setString(1, user.getUsername());
			pstmt.setString(2, hashedPassword);
            pstmt.setString(3, user.getEmail());
            pstmt.setString(4, user.getFullName());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
			
		}catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public User login(String userName, String password) {
		String sql = "SELECT user_id, username, password, email, full_name, created_date " +
                "FROM users WHERE username = ? ";
		
		try(Connection con = DBConnection.getConnection();
				PreparedStatement pstmt = con.prepareStatement(sql)) {
			
			pstmt.setString(1, userName);
			ResultSet rs = pstmt.executeQuery();
			
			if(rs.next()) {
				String storedHash = rs.getString("password");
				
				if(verifyPassword(password, storedHash)) {
					User user = new User();
					user.setUserId(rs.getLong("user_id"));
					user.setUsername(rs.getString("username"));
                    user.setEmail(rs.getString("email"));
                    user.setFullName(rs.getString("full_name"));
                    user.setCreatedDate(rs.getTimestamp("created_date"));
                    
                    updateLastLogin(user.getUserId());
                    
                    return user;
				}
			}
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public boolean usernameExists(String username) {
		String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
		
		try (Connection conn = DBConnection.getConnection();
	             PreparedStatement pstmt = conn.prepareStatement(sql)) {
			
			pstmt.setString(1, username);
			ResultSet rs = pstmt.executeQuery();	
			
			if(rs.next()) {
				return rs.getInt(1) > 0 ;	
			}
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	private void updateLastLogin(Long userId) {
        String sql = "UPDATE users SET last_login = SYSTIMESTAMP WHERE user_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, userId);
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
	
	private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
	
	private boolean verifyPassword(String inputPassword, String storedHash) {
        String inputHash = hashPassword(inputPassword);
        return inputHash != null && inputHash.equals(storedHash);
    }
	
	public Long getUserIdByEmail(String email) {
		String sql = "SELECT user_id FROM users WHERE email = ?";
		
		try (Connection conn = DBConnection.getConnection();
		         PreparedStatement pstmt = conn.prepareStatement(sql)) {
			
			pstmt.setString(1, email );
			ResultSet rs = pstmt.executeQuery();
			
			if(rs.next()) {
				return rs.getLong("user_id");
			}
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public boolean updatePassword(Long userId, String password) {
		String hashedPassword = hashPassword(password);
		String sql = "UPDATE users SET password = ? WHERE user_id = ?";
		
		try (Connection conn = DBConnection.getConnection();
		         PreparedStatement pstmt = conn.prepareStatement(sql)) {
			
			pstmt.setString(1, hashedPassword);
			pstmt.setLong(2, userId);
			
			int rows = pstmt.executeUpdate();
			return rows > 0;
			
		}catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
	}
	
	public boolean emailExists(String email) {
	    String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
	    
	    try (Connection conn = DBConnection.getConnection();
	         PreparedStatement pstmt = conn.prepareStatement(sql)) {
	        
	        pstmt.setString(1, email);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            return rs.getInt(1) > 0;
	        }
	        
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return false;
	}

}
