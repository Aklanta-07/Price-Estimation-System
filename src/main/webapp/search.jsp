<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.priceestimation.model.User" %>
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
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Search - Price Estimation System</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
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
        }
        .search-btn {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            border: none;
            padding: 12px;
        }
        .search-btn:hover {
            transform: translateY(-2px);
            box-shadow: 0 5px 20px rgba(0,0,0,0.2);
        }
    </style>
</head>
<body>
    <nav class="navbar navbar-expand-lg navbar-light">
        <div class="container">
            <a class="navbar-brand fw-bold" href="#">🔍 Price Estimation System</a>
            <div class="navbar-nav ms-auto">
                <span class="nav-item nav-link">Welcome, <%= user.getFullName() != null ? user.getFullName() : user.getUsername() %>!</span>
                <a href="logout" class="nav-link text-danger">Logout</a>
            </div>
        </div>
    </nav>
    
    <div class="container mt-5">
        <div class="row justify-content-center">
            <div class="col-md-8">
                <div class="card">
                    <div class="card-body p-5">
                        <h3 class="text-center mb-4">Search for Products & Services</h3>
                        <p class="text-center text-muted mb-4">Enter product details to get estimated market prices</p>
                        
                        <form action="search" method="get">
                            <div class="mb-4">
                                <label for="productName" class="form-label fw-bold">Product / Service Name *</label>
                                <input type="text" class="form-control form-control-lg" 
                                       id="productName" name="productName" 
                                       placeholder="e.g., Cisco Router, 24 Port Switch, Network Installation Service"
                                       required>
                            </div>
                            
                            <div class="row">
                                <div class="col-md-4 mb-3">
                                    <label for="brand" class="form-label">Brand / Make</label>
                                    <input type="text" class="form-control" id="brand" name="brand" 
                                           placeholder="e.g., Cisco, Dell, HP">
                                </div>
                                <div class="col-md-4 mb-3">
                                    <label for="model" class="form-label">Model Number</label>
                                    <input type="text" class="form-control" id="model" name="model" 
                                           placeholder="e.g., C9200L-24T-4X">
                                </div>
                                <div class="col-md-4 mb-3">
                                    <label for="searchType" class="form-label">Search Type</label>
                                    <select class="form-control" id="searchType" name="searchType">
                                        <option value="PRODUCT">Product</option>
                                        <option value="SERVICE">Service</option>
                                    </select>
                                </div>
                            </div>
                            
                            <div class="mb-4">
                                <label for="specifications" class="form-label">Specifications (Optional)</label>
                                <textarea class="form-control" id="specifications" name="specifications" 
                                          rows="3" placeholder="e.g., 24 ports, Gigabit Ethernet, Layer 3"></textarea>
                            </div>
                            
                            <button type="submit" class="btn btn-primary search-btn w-100">
                                🔍 Search Prices
                            </button>
                        </form>
                    </div>
                </div>
                
                <div class="text-center mt-4">
                    <a href="history" class="text-white">View Search History</a>
                </div>
            </div>
        </div>
    </div>
    
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>