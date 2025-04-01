package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.demo.service.CustomUserDetailsService;

import javax.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    private final CustomUserDetailsService customUserDetailsService;

    public SecurityConfig(CustomUserDetailsService customUserDetailsService) {
        this.customUserDetailsService = customUserDetailsService;
    }

    

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        logger.info("Configuring security filter chain...");
        http
            .csrf(csrf -> csrf.disable()) // Disable CSRF for stateless APIs (optional)
            .authorizeHttpRequests(registry -> {
                logger.info("Configuring authorization rules...");
                registry.requestMatchers("/css/**", "/js/**", "/images/**", "favicon.ico", "/api/email/send").permitAll();
                registry.requestMatchers("/", "/auth/aboutus", "/api/job/**", "/api/admin/**", "/api/check-session1", "/logout", "/api/job/description/**").permitAll(); // ✅ Allow public access
                registry.requestMatchers("/auth/admin-register", "/auth/admin-login").permitAll();
                registry.requestMatchers("/api/handymen").permitAll();
                registry.requestMatchers(HttpMethod.POST, "/api/auth/admin-register").permitAll();
                registry.requestMatchers(HttpMethod.POST, "/api/auth/admin-login").permitAll();
                registry.requestMatchers(HttpMethod.POST, "/api/auth/register").permitAll();
                registry.requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll();
                registry.requestMatchers("/auth/login", "/auth/register", "/handymen").permitAll();
                registry.requestMatchers("/api/auth/admin-login").permitAll();
                registry.requestMatchers("/api/admin/**").hasRole("ADMIN");
                registry.requestMatchers("/customer-dashboard", "/handyman-dashboard", "/default-dashboard").permitAll();
                registry.requestMatchers("/profile").authenticated();
            })
            .formLogin(httpform -> {
                logger.info("Configuring form login...");
            
                httpform
                    .loginPage("/auth/login").permitAll() // Default login page for customers/handymen 
                    .successHandler(authenticationSuccessHandler()) // Common success handler
                    .failureHandler(authenticationFailureHandler()); // Common failure handler
            })
            
            .logout(logout -> {
                logger.info("Configuring logout...");
                logout
                    .logoutUrl("/logout") // Custom logout URL
                    .logoutSuccessUrl("/") // Redirect after logout
                    .invalidateHttpSession(true) // Invalidate session on logout
                    .deleteCookies("JSESSIONID") // Delete session cookie
                    .permitAll();
            })
            .sessionManagement(session -> {
                logger.info("Configuring session management...");
                session
                    .sessionFixation().migrateSession()
                    .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED);
            })
            .userDetailsService(customUserDetailsService) // Set custom user details service
            .exceptionHandling(exception -> {
                logger.info("Configuring exception handling...");
                exception.authenticationEntryPoint((request, response, authException) -> {
                    logger.error("Authentication failed: {}", authException.getMessage());
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"success\": false, \"message\": \"Unauthorized\"}");
                });
            });

        logger.info("Security filter chain configuration completed.");
        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        return (request, response, exception) -> {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"success\": false, \"message\": \"Invalid email or password\"}");
        };
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return (request, response, authentication) -> {
            // Determine the redirect URL based on the user's role
            String redirectUrl = determineRedirectUrl(authentication);

            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/json");
            response.getWriter().write("{\"success\": true, \"redirectUrl\": \"" + redirectUrl + "\"}");
        };
    }
    
    private String determineRedirectUrl(Authentication authentication) {
        if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return "/admin-dashboard"; // Redirect admin to admin dashboard
        } else if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_Customer"))) {
            return "/customer-dashboard"; // Redirect customer to customer dashboard
        } else if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_Handyman"))) {
            return "/handyman-dashboard"; // Redirect handyman to handyman dashboard
        } else {
            return "/default-dashboard"; // Fallback for other roles
        }
    }

    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http, UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
            .userDetailsService(userDetailsService)
            .passwordEncoder(passwordEncoder)
            .and()
            .build();
    }
}