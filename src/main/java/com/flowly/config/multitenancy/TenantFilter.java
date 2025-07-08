// package com.flowly.config.multitenancy;

// import jakarta.servlet.*;
// import jakarta.servlet.http.HttpServletRequest;
// import org.springframework.stereotype.Component;

// import java.io.IOException;

// @Component
// public class TenantFilter implements Filter {

//     private static final String TENANT_HEADER = "X-Tenant-ID";

//     @Override
//     public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
//             throws IOException, ServletException {

//         HttpServletRequest httpRequest = (HttpServletRequest) request;

//         String path = httpRequest.getRequestURI();

//         if (path.startsWith("/api/tenants/register") ) {
//             chain.doFilter(request, response);
//             return;
//         }
//         String schemaName = httpRequest.getHeader(TENANT_HEADER);
        
//         if (schemaName != null && !schemaName.isBlank()) {
//             TenantContextHolder.setCurrentTenant(schemaName);
//         }

//         try {
//             chain.doFilter(request, response);
//         } finally {
//             TenantContextHolder.clear();
//         }
//     }
// }
