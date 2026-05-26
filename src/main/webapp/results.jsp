<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List, com.priceestimation.model.Product, com.priceestimation.model.User" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%
    User user = (User) session.getAttribute("loggedInUser");
    if (user == null) {
        response.sendRedirect("login");
        return;
    }
    
    List<Product> results = (List<Product>) request.getAttribute("results");
    String searchKeyword = (String) request.getAttribute("searchKeyword");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Search Results - Price Estimation System</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <style>
        body {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
        }
        .navbar {
            background: rgba(255,255,255,0.95);
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        }
        .card {
            border-radius: 15px;
            box-shadow: 0 10px 40px rgba(0,0,0,0.1);
            margin-bottom: 20px;
        }
        .price-highlight {
            font-size: 1.2rem;
            font-weight: bold;
            color: #28a745;
        }
        .lowest-price {
            background-color: #d4edda;
            border-left: 4px solid #28a745;
        }
        .table-hover tbody tr:hover {
            background-color: #f5f5f5;
        }
        .source-badge {
            padding: 5px 10px;
            border-radius: 20px;
            font-size: 0.8rem;
            font-weight: 500;
        }
        .badge-amazon { background-color: #ff9900; color: white; }
        .badge-flipkart { background-color: #2874f0; color: white; }
    </style>
</head>
<body>
    <nav class="navbar navbar-expand-lg navbar-light">
        <div class="container">
            <a class="navbar-brand fw-bold" href="search">
                <i class="fas fa-search"></i> Price Estimation System
            </a>
            <div class="navbar-nav ms-auto">
                <span class="nav-item nav-link">Welcome, <%= user.getFullName() != null ? user.getFullName() : user.getUsername() %>!</span>
                <a href="history" class="nav-link"><i class="fas fa-history"></i> History</a>
                <a href="logout" class="nav-link text-danger"><i class="fas fa-sign-out-alt"></i> Logout</a>
            </div>
        </div>
    </nav>
    
    <div class="container mt-4">
        <div class="card">
            <div class="card-body">
                <h3 class="mb-3">
                    <i class="fas fa-search"></i> Results for: "<%= searchKeyword %>"
                </h3>
                
                <% if (request.getAttribute("error") != null) { %>
                    <div class="alert alert-danger"><%= request.getAttribute("error") %></div>
                <% } %>
                
                <c:choose>
                    <c:when test="${empty results}">
                        <div class="alert alert-info text-center">
                            <i class="fas fa-info-circle"></i> No results found for "<%= searchKeyword %>". 
                            Try a different keyword or check back later.
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="alert alert-success">
                            <i class="fas fa-check-circle"></i> Found ${fn:length(results)} products from multiple sources
                        </div>
                        
                        <div class="table-responsive">
                            <table class="table table-hover">
                                <thead class="table-dark">
                                    <tr>
                                        <th>#</th>
                                        <th>Product Name</th>
                                        <th>Price (₹)</th>
                                        <th>Source</th>
                                        <th>Link</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach items="${results}" var="product" varStatus="status">
                                        <tr>
                                            <td>${status.index + 1}</td>
                                            <td>${product.name}</td>
                                            <td class="price-highlight">₹${product.price}</td>
                                            <td>
                                                <span class="source-badge 
                                                    <c:choose>
                                                        <c:when test="${product.sourceWebsite == 'Amazon India'}">badge-amazon</c:when>
                                                        <c:when test="${product.sourceWebsite == 'Flipkart'}">badge-flipkart</c:when>
                                                        <c:otherwise>bg-secondary</c:otherwise>
                                                    </c:choose>">
                                                    ${product.sourceWebsite}
                                                </span>
                                            </td>
                                            <td>
                                                <c:if test="${not empty product.sourceUrl}">
                                                    <a href="${product.sourceUrl}" target="_blank" class="btn btn-sm btn-outline-primary">
                                                        <i class="fas fa-external-link-alt"></i> View
                                                    </a>
                                                </c:if>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </c:otherwise>
                </c:choose>
                
                <div class="mt-3">
                    <a href="search" class="btn btn-primary">
                        <i class="fas fa-search"></i> New Search
                    </a>
                </div>
            </div>
        </div>
    </div>
    
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>