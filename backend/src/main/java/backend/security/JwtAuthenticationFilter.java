package backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.stereotype.Component;

import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String path = request.getRequestURI();

        /*
         * These endpoints are public.
         * Do not attempt JWT authentication for them.
         */
        if (
                path.equals("/api/hello") ||
                        path.equals("/api/auth/register") ||
                        path.equals("/api/auth/login") ||
                        path.equals("/api/ai/generate")
        ) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");

        System.out.println("AUTH HEADER = " + authHeader);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        try {

            String email = jwtService.extractEmail(token);

            System.out.println("JWT EMAIL = " + email);

            if (
                    email != null &&
                            SecurityContextHolder
                                    .getContext()
                                    .getAuthentication() == null
            ) {

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                email,
                                null,
                                Collections.emptyList()
                        );

                SecurityContextHolder
                        .getContext()
                        .setAuthentication(authentication);
            }

        } catch (Exception e) {

            System.out.println(
                    "JWT validation failed: " + e.getMessage()
            );

        }

        filterChain.doFilter(request, response);
    }
}