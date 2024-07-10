package org.microservices.user.security.config;


import lombok.AllArgsConstructor;
import org.microservices.user.security.filters.AuthenticationFilter;
import org.microservices.user.security.filters.AuthorizationFilter;
import org.microservices.user.security.filters.CustomAuthenticationEntryPoint;
import org.microservices.user.security.util.TokenUtil;
import org.microservices.user.service.user.UserServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
@Profile("run")
public class WebSecurityConfig  {

    private final UserServiceImpl userService;
    private final PasswordEncoder passwordEncoder;
    private final TokenUtil tokenUtil;


    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http, PasswordEncoder passwordEncoder, UserDetailsService userService)
            throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userService)
                .passwordEncoder(passwordEncoder)
                .and()
                .authenticationProvider(daoAuthenticationProvider())
                .build();
    }

    @Bean
    public AuthenticationFilter authenticationFilter(AuthenticationManager authenticationManager)  {
        AuthenticationFilter appAuthenticationFilter = new AuthenticationFilter(
                authenticationManager,
                userService,
                tokenUtil);
        appAuthenticationFilter.setFilterProcessesUrl("/auth/login");
        appAuthenticationFilter.setAuthenticationManager(authenticationManager);
        return appAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeHttpRequests((authorize) -> authorize
                        .antMatchers("/auth/**").permitAll()
                        .antMatchers("/api/**").permitAll()
                        .antMatchers("/api/admin/**").hasRole("ADMIN")
                        .anyRequest()
                        .authenticated()
                )
                .cors((cors) -> cors.configurationSource(corsConfigurationSource()))
                .addFilter(authenticationFilter(authenticationManager(http, passwordEncoder, userService)))
                .addFilterBefore(new AuthorizationFilter(tokenUtil), UsernamePasswordAuthenticationFilter.class)
                .authenticationProvider(daoAuthenticationProvider())
                .exceptionHandling((exceptions) -> exceptions
                        .authenticationEntryPoint(new CustomAuthenticationEntryPoint()))
                .httpBasic(Customizer.withDefaults())
                .sessionManagement().sessionCreationPolicy(STATELESS);

        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder);
        provider.setUserDetailsService(userService);
        return provider;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Content-Type", "Authorization", "Access-Control-Allow-Headers",
                "Access-Control-Allow-Methods", "Access-Control-Allow-Origin"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}

