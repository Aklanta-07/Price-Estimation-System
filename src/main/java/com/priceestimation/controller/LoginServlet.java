package com.priceestimation.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.priceestimation.dao.UserDAO;
import com.priceestimation.model.User;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
	
	private UserDAO userDAO =  new UserDAO();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.getRequestDispatcher("login.jsp").forward(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String username = req.getParameter("username");
		String password = req.getParameter("password");
		
		if(username == null || username.trim().isEmpty() ||
				password == null || password.trim().isEmpty()) {
			
			req.setAttribute("error", "Username and password are required!");
            req.getRequestDispatcher("login.jsp").forward(req, resp);
            return;
		}
		
		User user = userDAO.login(username, password);
		
		if(user != null) {
			HttpSession session  = req.getSession();
			session.setAttribute("loggedInUser", user);
			session.setAttribute("userId", user.getUserId());
			session.setAttribute("username", user.getUsername());
			
			resp.sendRedirect("search");
		} else {
            // Login failed
            req.setAttribute("error", "Invalid username or password!");
            req.getRequestDispatcher("login.jsp").forward(req, resp);
	     }

    }
}
