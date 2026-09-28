package com.agenciaviagens.destinos.security;

import com.agenciaviagens.destinos.exception.ErroRespostaDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Trata tentativas de acesso a recursos protegidos sem autenticacao
 * (ou com credenciais invalidas), devolvendo uma resposta 401 no
 * mesmo formato padronizado de erro usado pelo restante da API,
 * em vez da pagina de login padrao do Spring Security.
 */
@Component
public class ApiAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                          AuthenticationException authException) throws IOException {
        ErroRespostaDTO erro = new ErroRespostaDTO(
                HttpStatus.UNAUTHORIZED.value(),
                "Nao autenticado",
                "E necessario autenticar-se para acessar este recurso"
        );
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(erro));
    }
}
