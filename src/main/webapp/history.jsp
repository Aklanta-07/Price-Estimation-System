<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.priceestimation.model.User, com.priceestimation.model.SearchHistory, com.priceestimation.model.Product" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%
    User user = (User) session.getAttribute("loggedInUser");
    if (user == null) {
        response.sendRedirect("login");
        return;
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Search History - Price Estimation System</title>
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
        .history-item {
            cursor: pointer;
            transition: all 0.3s ease;
        }
        .history-item:hover {
            background-color: #f8f9fa;
            transform: translateX(5px);
        }
        .price-tag {
            color: #28a745;
            font-weight: bold;
        }
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
                <a href="search" class="nav-link"><i class="fas fa-search"></i> New Search</a>
                <a href="logout" class="nav-link text-danger"><i class="fas fa-sign-out-alt"></i> Logout</a>
            </div>
        </div>
    </nav>
    
    <div class="container mt-4">
        <div class="card">
            <div class="card-body">
                <h3 class="mb-4">
                    <i class="fas fa-history"></i> Your Search History
                </h3>
                
                <c:choose>
                    <c:when test="${empty historyList}">
                        <div class="alert alert-info text-center">
                            <i class="fas fa-info-circle"></i> No search history found. 
                            <a href="search" class="alert-link">Start searching now!</a>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="alert alert-success">
                            <i class="fas fa-check-circle"></i> Showing last ${fn:length(historyList)} searches
                        </div>
                        
                        <div class="accordion" id="historyAccordion">
                            <c:forEach items="${historyList}" var="history" varStatus="status">
                                <div class="accordion-item">
                                    <h2 class="accordion-header" id="heading${status.index}">
                                        <button class="accordion-button ${status.index > 0 ? 'collapsed' : ''}" 
                                                type="button" data-bs-toggle="collapse" 
                                                data-bs-target="#collapse${status.index}">
                                            <div class="d-flex justify-content-between w-100 me-3">
                                                <span>
                                                    <i class="fas fa-search"></i> 
                                                    <strong>${history.searchKeyword}</strong>
                                                    <span class="badge bg-secondary ms-2">${history.searchType}</span>
                                                </span>
                                                <span class="text-muted">
                                                    <i class="fas fa-calendar-alt"></i> ${history.searchDate}
                                                    <span class="ms-3">
                                                        <i class="fas fa-box"></i> ${fn:length(history.results)} results
                                                    </span>
                                                </span>
                                            </div>
                                        </button>
                                    </h2>
                                    <div id="collapse${status.index}" class="accordion-collapse collapse ${status.index == 0 ? 'show' : ''}" 
                                         data-bs-parent="#historyAccordion">
                                        <div class="accordion-body">
                                            <div class="table-responsive">
                                                <table class="table table-sm table-hover">
                                                    <thead>
                                                        <tr>
                                                            <th>Product</th>
                                                            <th>Price</th>
                                                            <th>Source</th>
                                                            <th>Link</th>
                                                        </tr>
                                                    </thead>
                                                    <tbody>
                                                        <c:forEach items="${history.results}" var="product">
                                                            <tr>
                                                                <td>${product.name}</td>
                                                                <td class="price-tag">₹${product.price}</td>
                                                                <td>${product.sourceWebsite}</td>
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
                                        </div>
                                    </div>
                                </div>
                            </c:forEach>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
        
        <div class="text-center mt-3">
            <a href="search" class="btn btn-primary">
                <i class="fas fa-search"></i> New Search
            </a>
        </div>
    </div>
    
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>