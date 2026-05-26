package com.priceestimation.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.priceestimation.dao.SearchHistoryDAO;
import com.priceestimation.model.SearchHistory;
import com.priceestimation.model.User;

@WebServlet("/history")
public class HistoryServlet extends HttpServlet {
    
	private SearchHistoryDAO historyDAO = new SearchHistoryDAO();
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession session = req.getSession(false);
		User user = 	(User) session.getAttribute("loggedInUser");
		
		if(user == null) {
			resp.sendRedirect("login");
			return;
		}
		
		List<SearchHistory> historyList = historyDAO.getSearchHistory(user.getUserId(), 20);
		
		req.setAttribute("historyList", historyList);
		req.getRequestDispatcher("history.jsp").forward(req, resp);
		
	}

}
