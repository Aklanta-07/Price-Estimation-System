package com.priceestimation.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter("/*")
public class AuthFilter implements Filter {
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        HttpSession session = httpRequest.getSession(false);
        String requestURI = httpRequest.getRequestURI();
        String contextPath = httpRequest.getContextPath();
        
        // ✅ DEFINE PUBLIC PAGES (NO LOGIN REQUIRED)
        boolean isLoginPage = requestURI.endsWith("login") || 
                              requestURI.endsWith("login.jsp");
        
        boolean isRegisterPage = requestURI.endsWith("register") || 
                                 requestURI.endsWith("register.jsp");
        
        // ✅ ADD FORGOT PASSWORD PAGES
        boolean isForgotPasswordPage = requestURI.endsWith("forgot-password") || 
                                       requestURI.endsWith("forgot-password.jsp") ||
                                       requestURI.endsWith("reset-password") ||
                                       requestURI.endsWith("reset-password.jsp");
        
        // Also allow static resources
        boolean isStaticResource = requestURI.endsWith(".css") || 
                                   requestURI.endsWith(".js") ||
                                   requestURI.endsWith(".png") ||
                                   requestURI.endsWith(".jpg") ||
                                   requestURI.endsWith(".ico");
        
        // ✅ CHECK IF PAGE IS PUBLIC
        boolean isPublicPage = isLoginPage || isRegisterPage || 
                               isForgotPasswordPage || isStaticResource;
        
        boolean isLoggedIn = (session != null && session.getAttribute("loggedInUser") != null);
        
        // ✅ LOGIC
        if (isLoggedIn) {
            // User is logged in - allow all access
            chain.doFilter(request, response);
            
        } else if (isPublicPage) {
            // Not logged in but on public page - allow access
            chain.doFilter(request, response);
            
        } else {
            // Not logged in and trying to access protected page - redirect to login
            httpResponse.sendRedirect(contextPath + "/login");
        }
    }
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println(" AuthFilter Initialized!");
    }
    
    @Override
    public void destroy() {
        System.out.println("AuthFilter Destroyed!");
    }
}