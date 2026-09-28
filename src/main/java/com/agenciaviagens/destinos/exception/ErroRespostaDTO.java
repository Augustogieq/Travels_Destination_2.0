package com.agenciaviagens.destinos.exception;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Formato padrao de resposta de erro devolvido pela API, mantendo
 * consistencia entre os diferentes tipos de falha (validacao,
 * recurso nao encontrado, erros inesperados etc).
 */
public class ErroRespostaDTO {

    private LocalDateTime timestamp = LocalDateTime.now();
    private int status;
    private String erro;
    private String mensagem;
    private Map<String, String> camposInvalidos;

    public ErroRespostaDTO(int status, String erro, String mensagem) {
        this.status = status;
        this.erro = erro;
        this.mensagem = mensagem;
    }

    public ErroRespostaDTO(int status, String erro, String mensagem, Map<String, String> camposInvalidos) {
        this(status, erro, mensagem);
        this.camposInvalidos = camposInvalidos;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }

    public String getErro() {
        return erro;
    }

    public String getMensagem() {
        return mensagem;
    }

    public Map<String, String> getCamposInvalidos() {
        return camposInvalidos;
    }
}
