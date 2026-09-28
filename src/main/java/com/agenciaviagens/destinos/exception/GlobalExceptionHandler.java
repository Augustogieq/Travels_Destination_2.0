package com.agenciaviagens.destinos.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Centraliza o tratamento de excecoes da API, garantindo respostas
 * HTTP consistentes (status code + corpo padronizado) para os
 * clientes, em vez de stack traces ou respostas genericas do Spring.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ErroRespostaDTO> tratarRecursoNaoEncontrado(RecursoNaoEncontradoException ex) {
        ErroRespostaDTO erro = new ErroRespostaDTO(
                HttpStatus.NOT_FOUND.value(),
                "Recurso nao encontrado",
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erro);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroRespostaDTO> tratarValidacao(MethodArgumentNotValidException ex) {
        Map<String, String> campos = new HashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            campos.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        ErroRespostaDTO erro = new ErroRespostaDTO(
                HttpStatus.BAD_REQUEST.value(),
                "Dados invalidos",
                "Um ou mais campos enviados sao invalidos",
                campos
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }

    /**
     * Capturado quando o @PreAuthorize dos endpoints do controller nega o
     * acesso (perfil incompativel). Mantem o mesmo formato de resposta do
     * ApiAccessDeniedHandler, que trata negacoes ocorridas antes de chegar
     * ao MVC (nivel de filtro de seguranca).
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErroRespostaDTO> tratarAcessoNegado(AccessDeniedException ex) {
        ErroRespostaDTO erro = new ErroRespostaDTO(
                HttpStatus.FORBIDDEN.value(),
                "Acesso negado",
                "Seu perfil de acesso nao tem permissao para realizar esta operacao"
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(erro);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroRespostaDTO> tratarErroGenerico(Exception ex) {
        ErroRespostaDTO erro = new ErroRespostaDTO(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Erro interno",
                "Ocorreu um erro inesperado ao processar a requisicao"
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(erro);
    }
}
