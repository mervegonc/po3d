package com.project.po3d.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.IOException;
import java.util.Enumeration;

@Component
public class RequestLoggingInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        System.out.println("===== Incoming Request =====");
        System.out.println("🔹 URL: " + request.getRequestURL());
        System.out.println("🔹 Method: " + request.getMethod());

        // Headers
        System.out.println("🔹 Headers:");
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String header = headerNames.nextElement();
            System.out.println("   ➤ " + header + ": " + request.getHeader(header));
        }

        // Multipart requestleri loglama
        if (request.getContentType() != null && request.getContentType().startsWith("multipart/")) {
            System.out.println("🔹 Multipart Form Request - Body loglanmadı.");
            return true;
        }

        // Eğer request cache edilmişse tekrar okumaya çalış
        if (request instanceof ContentCachingRequestWrapper cachingRequest) {
            String body = new String(cachingRequest.getContentAsByteArray());
            System.out.println("🔹 Body: " + body);
        }

        return true;
    }
}
