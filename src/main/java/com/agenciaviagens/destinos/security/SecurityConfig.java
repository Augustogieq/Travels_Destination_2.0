package com.agenciaviagens.destinos.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuracao central de seguranca da API.
 *
 * Estrategia adotada: autenticacao HTTP Basic, sem estado de sessao
 * (STATELESS) — adequada para uma API REST consumida por outros
 * sistemas/parceiros, onde cada requisicao carrega suas proprias
 * credenciais. As senhas dos usuarios sao armazenadas com hash
 * BCrypt (nunca em texto puro).
 *
 * Regras de autorizacao (ver detalhes tambem em DestinoController via
 * @PreAuthorize, como reforco de defesa em profundidade):
 * - Leitura de destinos (GET) e publica, sem necessidade de login,
 *   pois trata-se de conteudo de vitrine da agencia (consulta).
 * - Cadastrar, atualizar e excluir destinos exigem perfil ADMIN.
 * - Registrar avaliacao exige apenas usuario autenticado (ADMIN ou USER).
 * - Qualquer outro endpoint nao mapeado exige autenticacao.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final ApiAuthenticationEntryPoint authenticationEntryPoint;
    private final ApiAccessDeniedHandler accessDeniedHandler;

    public SecurityConfig(ApiAuthenticationEntryPoint authenticationEntryPoint,
                           ApiAccessDeniedHandler accessDeniedHandler) {
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.accessDeniedHandler = accessDeniedHandler;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(UsuarioDetailsService usuarioDetailsService,
                                                              PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(usuarioDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                     DaoAuthenticationProvider authenticationProvider) throws Exception {
        http
                .authenticationProvider(authenticationProvider)
                // API stateless: nao usamos cookies/sessao, entao CSRF nao se aplica
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(handling -> handling
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler))
                .authorizeHttpRequests(auth -> auth
                        // Consultas de destinos sao publicas
                        .requestMatchers(HttpMethod.GET, "/api/destinos/**").permitAll()
                        // Cadastro, atualizacao e exclusao: somente ADMIN
                        .requestMatchers(HttpMethod.POST, "/api/destinos/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/destinos/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/destinos/**").hasRole("ADMIN")
                        // Avaliar um destino: qualquer usuario autenticado (ADMIN ou USER)
                        .requestMatchers(HttpMethod.PATCH, "/api/destinos/*/avaliacoes").authenticated()
                        // Endpoint utilitario para verificar o usuario logado
                        .requestMatchers("/api/auth/me").authenticated()
                        // Console H2 (perfil de testes) e demais recursos publicos, se houver
                        .requestMatchers("/actuator/health").permitAll()
                        .anyRequest().authenticated())
                .httpBasic(basic -> basic.authenticationEntryPoint(authenticationEntryPoint));

        return http.build();
    }
}
