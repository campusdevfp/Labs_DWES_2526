package app.ejemplo02.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/test/public", "/h2-console/**").permitAll()
                        .requestMatchers("/api/auth/**").permitAll() // endpoints de autenticación públicos
                        .requestMatchers("/test/authenticated", "/test/me").authenticated()
                        .requestMatchers("/session/**").authenticated() // endpoints de sesión requieren autenticación
                        .requestMatchers("/ejemplos/**").permitAll() // ejemplos accesibles sin autenticación (para probar el carrito sin login)
                        .anyRequest().authenticated()
                )
                .headers(headers -> headers.frameOptions(frame -> frame.disable())) // para H2 console
                .httpBasic(httpBasic -> {})
                .formLogin(formLogin -> {})
                .logout(logout -> logout
                        .logoutUrl("/session/logout")
                        .logoutSuccessHandler((request, response, authentication) -> {
                            response.setStatus(200);
                            response.getWriter().write("Sesión cerrada exitosamente");
                        })
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                );
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }


}