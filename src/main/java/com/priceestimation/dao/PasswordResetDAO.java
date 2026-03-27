package com.priceestimation.dao;

import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Base64;
import java.util.Date;

import com.priceestimation.dbconnection.DBConnection;
import com.priceestimation.model.PasswordResetToken;

public class PasswordResetDAO {
	
	private UserDAO userDAO = new UserDAO();
	
	public String generateToken() {
		SecureRandom random = new SecureRandom();
		byte[] bytes = new byte[32];
		random.nextBytes(bytes);
		return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
	}
	
	public PasswordResetToken createToken(String email) {
		Long userId = userDAO.getUserIdByEmail(email);
		if(userId == null) {
			return null;
		}
		
		String token = generateToken();
		Date expiryDate = new Date(System.currentTimeMillis() + 3600000);
		
		String sql = "INSERT INTO password_reset_tokens (token_id, user_id, token, expiry_date) " +
                "VALUES (token_seq.NEXTVAL, ?, ?, ?)";
		
		 try (Connection conn = DBConnection.getConnection();
	             PreparedStatement pstmt = conn.prepareStatement(sql)) {
			 
			 pstmt.setLong(1, userId);
			 pstmt.setString(2, token);
			 pstmt.setTimestamp(3, new Timestamp(expiryDate.getTime()));
			 
			 int rows  = pstmt.executeUpdate();
			 
			 if(rows > 0) {
				 PasswordResetToken resetToken = new PasswordResetToken();
				 resetToken.setUserId(userId);
				 resetToken.setToken(token);
				 resetToken.setExpiryDate(expiryDate);
				 resetToken.setUsed(false);
				 return resetToken;
			 }
			 
		 }catch (Exception e) {
			e.printStackTrace();
		}
	         
		 return null;
	}
	
	public PasswordResetToken validateToken(String token) {
		String sql = "SELECT * FROM password_reset_tokens WHERE token = ? AND used = 0";
		
		try (Connection conn = DBConnection.getConnection();
	             PreparedStatement pstmt = conn.prepareStatement(sql)) {
			
			pstmt.setString(1, token);
			ResultSet rs = pstmt.executeQuery();
			
			if(rs.next()) {
				PasswordResetToken resetToken = new PasswordResetToken();
                resetToken.setTokenId(rs.getLong("token_id"));
                resetToken.setUserId(rs.getLong("user_id"));
                resetToken.setToken(rs.getString("token"));
                resetToken.setExpiryDate(rs.getTimestamp("expiry_date"));
                resetToken.setUsed(rs.getInt("used") == 1);
                
                if(resetToken.isExpired()) {
                	return null;
                }
                return resetToken;
			}
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void markTokenAsUsed(String token) {
        String sql = "UPDATE password_reset_tokens SET used = 1 WHERE token = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, token);
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
	
	public boolean resetPassword(String token, String newPassword) {
		PasswordResetToken resetToken = validateToken(token);
		if(resetToken == null) {
			return false;
		}
		
		boolean passwordUpdated = userDAO.updatePassword(resetToken.getUserId(), newPassword);
		
		if(passwordUpdated) {
			markTokenAsUsed(token);
			return true;	
		}
		
		return false;
	}

}
