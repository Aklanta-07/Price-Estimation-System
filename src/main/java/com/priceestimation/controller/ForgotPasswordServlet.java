package com.priceestimation.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.priceestimation.dao.PasswordResetDAO;
import com.priceestimation.dao.UserDAO;
import com.priceestimation.model.PasswordResetToken;
import com.priceestimation.utils.EmailUtils;

@WebServlet("/forgot-password")
public class ForgotPasswordServlet  extends HttpServlet{
	
	private UserDAO userDAO =  new UserDAO();
	private PasswordResetDAO passwordResetDAO = new PasswordResetDAO();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.getRequestDispatcher("forgot-password.jsp").forward(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String email = req.getParameter("email");
		
		if (email == null || email.trim().isEmpty()) {
            req.setAttribute("error", "Email is required!");
            req.getRequestDispatcher("forgot-password.jsp").forward(req, resp);
            return;
        }
		
		if(!userDAO.emailExists(email)) {
			req.setAttribute("error", "Email not found in our records!");
            req.getRequestDispatcher("forgot-password.jsp").forward(req, resp);
            return;
		}
		
		PasswordResetToken resetToken = passwordResetDAO.createToken(email);
		
		if(resetToken != null) {
			String resetLink = req.getRequestURL().toString()
					.replace("forgot-password", "reset-password")
					+ "?token=" + resetToken.getToken();
			
			EmailUtils.sendPasswordResetEmail(email, resetLink);
			req.setAttribute("success", "Password reset link sent to your email. Check your inbox!");
			req.getRequestDispatcher("forgot-password.jsp").forward(req, resp);

		} else {
            req.setAttribute("error", "Failed to generate reset token. Please try again.");
            req.getRequestDispatcher("forgot-password.jsp").forward(req, resp);
		}
		
	}

}
