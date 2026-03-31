package io.github.ngtrphuc.smartphone_shop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import io.github.ngtrphuc.smartphone_shop.service.CustomUserDetailsService;

@Configuration(proxyBeanMethods = false)
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final LoginSuccessHandler loginSuccessHandler;

    public SecurityConfig(CustomUserDetailsService userDetailsService,
                         LoginSuccessHandler loginSuccessHandler) {
        this.userDetailsService = userDetailsService;
        this.loginSuccessHandler = loginSuccessHandler;
    }

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authProvider(PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(passwordEncoder);
        provider.setUserDetailsService(userDetailsService);
        return provider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                         DaoAuthenticationProvider authProvider) throws Exception {
        http
                .authenticationProvider(authProvider)
                .headers(headers -> headers
                        .frameOptions(frame -> frame.sameOrigin())
                        .contentSecurityPolicy(csp -> csp
                                .policyDirectives("default-src 'self'; "
                                        + "script-src 'self' 'unsafe-inline' cdn.jsdelivr.net; "
                                        + "style-src 'self' 'unsafe-inline'"))
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/css/**", "/images/**", "/js/**", "/actuator/health").permitAll()
                        .requestMatchers("/", "/product/**", "/register", "/login", "/admin/access-denied-admin").permitAll()
                        .requestMatchers("/cart/**", "/profile/**", "/my-orders/**").hasRole("USER")
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .successHandler(loginSuccessHandler)
                        .failureUrl("/login?error")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )
                .exceptionHandling(exception -> exception
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                            if (auth != null && auth.getAuthorities().stream()
                                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                                response.sendRedirect("/admin/access-denied-admin");
                            } else {
                                response.sendRedirect("/login");
                            }
                        })
                )
                .sessionManagement(session -> session
                        .sessionFixation().changeSessionId()
                        .maximumSessions(1)
                        .maxSessionsPreventsLogin(false)
                );

        return http.build();
    }
}