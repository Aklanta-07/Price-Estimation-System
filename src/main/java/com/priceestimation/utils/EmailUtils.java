package com.priceestimation.utils;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailUtils {
	private static final String SMTP_HOST = "smtp.gmail.com";
	private static final String SMTP_PORT = "587";
	private static final String FROM_EMAIL = "aklantaswain@gmail.com";  
    private static final String FROM_PASSWORD = "tlsp vexk aina jqpt";
    
    public static void sendPasswordResetEmail(String toEmail, String resetLink) {
    	Properties props = new Properties();
    	props.put("mail.smtp.host", SMTP_HOST);
    	props.put("mail.smtp.port", SMTP_PORT);
    	props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        
        Session session = Session.getInstance(props, new Authenticator() {
        	@Override
        	protected PasswordAuthentication getPasswordAuthentication() {
        		return new PasswordAuthentication(FROM_EMAIL, FROM_PASSWORD);
        	}
		});
        
        try {
        	
        	Message message = new MimeMessage(session);	
        	message.setFrom(new InternetAddress(FROM_EMAIL));
        	message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
        	message.setSubject("Password Reset - Price Estimation System");
        	
        	String htmlContent = "<!DOCTYPE html>" +
                    "<html>" +
                    "<body style='font-family: Arial, sans-serif;'>" +
                    "<div style='max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #ddd;'>" +
                    "<h2 style='color: #667eea;'>🔍 Price Estimation System</h2>" +
                    "<h3>Password Reset Request</h3>" +
                    "<p>We received a request to reset your password. Click the link below to set a new password:</p>" +
                    "<p><a href='" + resetLink + "' style='display: inline-block; padding: 10px 20px; " +
                    "background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; " +
                    "text-decoration: none; border-radius: 5px;'>Reset Password</a></p>" +
                    "<p>This link will expire in <strong>1 hour</strong>.</p>" +
                    "<p>If you didn't request this, please ignore this email.</p>" +
                    "<hr>" +
                    "<p style='color: #777; font-size: 12px;'>Price Estimation System - Automated Price Benchmarking</p>" +
                    "</div>" +
                    "</body>" +
                    "</html>";
        	
        	message.setContent(htmlContent, "text/html; charset=utf-8");
        	
        	Transport.send(message);
        	System.out.println("✅ Password reset email sent to: " + toEmail);
        	
        }catch (Exception e) {
        	System.err.println("❌ Failed to send email: " + e.getMessage());
            e.printStackTrace();
		}
        
    }
    
    
}
