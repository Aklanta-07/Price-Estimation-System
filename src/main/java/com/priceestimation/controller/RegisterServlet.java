package com.priceestimation.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.priceestimation.dao.UserDAO;
import com.priceestimation.model.User;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
	
	private UserDAO userDAO =  new UserDAO();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.getRequestDispatcher("register.jsp").forward(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String username = req.getParameter("username");
		String password = req.getParameter("password");
        String confirmPassword = req.getParameter("confirmPassword");
        String email = req.getParameter("email");
        String fullName = req.getParameter("fullName");
        
        if(username == null || username.trim().isEmpty()) {
        	req.setAttribute("error", "Password is required!");
            req.getRequestDispatcher("register.jsp").forward(req, resp);
            return;
        }
        
        if (password == null || password.trim().isEmpty()) {
            req.setAttribute("error", "Password is required!");
            req.getRequestDispatcher("register.jsp").forward(req, resp);
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            req.setAttribute("error", "Passwords do not match!");
            req.getRequestDispatcher("register.jsp").forward(req, resp);
            return;
        }
        
        if(userDAO.usernameExists(username)) {
        	req.setAttribute("error", "Username already exists! Please choose another.");
            req.getRequestDispatcher("register.jsp").forward(req, resp);
            return;
        }
        
        User user = new User(username, confirmPassword, email, fullName);
        
        if(userDAO.register(user)) {
        	req.setAttribute("success", "Registration successful! Please login.");
            req.getRequestDispatcher("login.jsp").forward(req, resp);
        }else {
            req.setAttribute("error", "Registration failed! Please try again.");
            req.getRequestDispatcher("register.jsp").forward(req, resp);
        }
	}

}
