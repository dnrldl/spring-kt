package com.springkt.global.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val jwtAuthenticationFilter: JwtAuthenticationFilter,
    private val restAuthenticationEntryPoint: RestAuthenticationEntryPoint,
    private val restAccessDeniedHandler: RestAccessDeniedHandler,
) {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .cors { }
            .csrf { csrf -> csrf.disable() }
            .formLogin { formLogin -> formLogin.disable() }
            .httpBasic { httpBasic -> httpBasic.disable() }
            .sessionManagement { session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .exceptionHandling { exception ->
                exception.authenticationEntryPoint(restAuthenticationEntryPoint)
                exception.accessDeniedHandler(restAccessDeniedHandler)
            }
            .authorizeHttpRequests { authorize ->
                authorize
                    .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                    .requestMatchers(HttpMethod.POST, "/api/users", "/api/users/").permitAll()
                    .requestMatchers(HttpMethod.POST, "/api/auth/login", "/api/auth/login/").permitAll()
                    .requestMatchers(
                        "/swagger-ui.html",
                        "/swagger-ui/**",
                        "/v3/api-docs",
                        "/v3/api-docs/**",
                    ).permitAll()
                    .requestMatchers("/oauth2/**", "/login/oauth2/**").permitAll()
                    .anyRequest().authenticated()
            }
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()
        configuration.allowedOrigins = listOf("http://localhost:3000")
        configuration.allowedMethods = listOf("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
        configuration.allowedHeaders = listOf("Authorization", "Content-Type", "DPoP")
        configuration.exposedHeaders = listOf("DPoP")

        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }
}
