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
import com.priceestimation.model.Product;
import com.priceestimation.model.SearchHistory;
import com.priceestimation.model.User;
import com.priceestimation.scraper.WebScraper;

@WebServlet("/search")
public class SearchServlet  extends HttpServlet{

	private WebScraper webScraper = new WebScraper();
	private SearchHistoryDAO historyDAO = new SearchHistoryDAO();
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession session = req.getSession(false);
		if (session == null || session.getAttribute("loggedInUser") == null) {
            resp.sendRedirect("login");
            return;
        }
		
		req.getRequestDispatcher("search.jsp").forward(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession session = req.getSession(false);
		User user = (User) session.getAttribute("loggedInUser");
		
		 String productName = req.getParameter("productName");
	     String brand = req.getParameter("brand");
	     String model = req.getParameter("model");
	     String specifications = req.getParameter("specifications");
         String searchType = req.getParameter("searchType"); // product or service
         
         if (productName == null || productName.trim().isEmpty()) {
             req.setAttribute("error", "Please enter a product or service name");
             req.getRequestDispatcher("search.jsp").forward(req, resp);
             return;
         }
         
         String searchKeyword = productName;
         if(brand != null && !brand.trim().isEmpty()) {
        	 searchKeyword += " " + brand;
         }
         if (model != null && !model.trim().isEmpty()) {
             searchKeyword += " " + model;
         }
         
         System.out.println("🔍 User: " + user.getUsername() + " searching for: " + searchKeyword);
         
         try {
        	 List<Product> results = webScraper.search(searchKeyword);
        	 
        	 SearchHistory history = new SearchHistory(user.getUserId(), searchKeyword, searchType);
        	 historyDAO.saveSearch(history, results);
        	 System.out.println("✅ Search saved to database");
        	 
        	 session.setAttribute("lastSearchResults", results);
        	 session.setAttribute("lastSearchResult", searchKeyword);
        	 
        	 req.setAttribute("results", results);
        	 req.setAttribute("searchKeyword", searchKeyword);
             req.getRequestDispatcher("results.jsp").forward(	req, resp);
        	 
         }catch (Exception e) {
        	 e.printStackTrace();
             req.setAttribute("error", "Error fetching prices: " + e.getMessage());
             req.getRequestDispatcher("search.jsp").forward(req, resp);
		} finally {
			if (webScraper != null) {
               webScraper.shutDown();
            }
		}
         
	}

}
