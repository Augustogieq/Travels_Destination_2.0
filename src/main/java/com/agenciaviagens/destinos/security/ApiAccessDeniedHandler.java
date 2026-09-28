package com.agenciaviagens.destinos.security;

import com.agenciaviagens.destinos.exception.ErroRespostaDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Trata tentativas de acesso a recursos para os quais o usuario
 * autenticado nao possui permissao (perfil incompativel), devolvendo
 * uma resposta 403 no formato padronizado de erro da API.
 */
@Component
public class ApiAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                        AccessDeniedException accessDeniedException) throws IOException {
        ErroRespostaDTO erro = new ErroRespostaDTO(
                HttpStatus.FORBIDDEN.value(),
                "Acesso negado",
                "Seu perfil de acesso nao tem permissao para realizar esta operacao"
        );
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(erro));
    }
}
