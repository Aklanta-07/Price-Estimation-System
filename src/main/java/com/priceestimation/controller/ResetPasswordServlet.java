package com.priceestimation.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.priceestimation.dao.PasswordResetDAO;
import com.priceestimation.model.PasswordResetToken;

@WebServlet("/reset-password")
public class ResetPasswordServlet  extends HttpServlet{
	
	private PasswordResetDAO passwordResetDAO = new PasswordResetDAO();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String token = req.getParameter("token");
		
		if (token == null || token.trim().isEmpty()) {
            req.setAttribute("error", "Invalid reset link!");
            req.getRequestDispatcher("login.jsp").forward(req, resp);
            return;
        }
		
		PasswordResetToken resetToken = passwordResetDAO.validateToken(token);
		if (resetToken == null) {
            req.setAttribute("error", "Invalid or expired reset link. Please request a new one.");
            req.getRequestDispatcher("forgot-password.jsp").forward(req, resp);
            return;
        }
		
		req.getSession().setAttribute("resetToken", token);
		req.getRequestDispatcher("reset-password.jsp").forward(req, resp);
		
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String token = (String)req.getSession().getAttribute("resetToken");
		String newPassword = req.getParameter("newPassword");
		String confirmPassword = req.getParameter("confirmPassword");
		
		if (token == null) {
            req.setAttribute("error", "Invalid session. Please request a new reset link.");
            req.getRequestDispatcher("forgot-password.jsp").forward(req, resp);
            return;
        }
		
		if (newPassword == null || newPassword.trim().isEmpty()) {
            req.setAttribute("error", "Password is required!");
            req.getRequestDispatcher("reset-password.jsp").forward(req, resp);
            return;
        }
        
        if (!newPassword.equals(confirmPassword)) {
            req.setAttribute("error", "Passwords do not match!");
            req.getRequestDispatcher("reset-password.jsp").forward(req, resp);
            return;
        }
        
        boolean success = passwordResetDAO.resetPassword(token, newPassword);
        
        if(success) {
        	req.getSession().removeAttribute("resetToken");
            req.setAttribute("success", "Password reset successfully! Please login with your new password.");
            req.getRequestDispatcher("login.jsp").forward(req, resp);
        }else {
        	req.setAttribute("error", "Failed to reset password. Please request a new reset link.");
            req.getRequestDispatcher("forgot-password.jsp").forward(req, resp);
        }
        
	}

}
