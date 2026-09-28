package com.agenciaviagens.destinos.exception;

/**
 * Lancada quando um destino solicitado (por id) nao existe no repositorio.
 * E traduzida pelo GlobalExceptionHandler para uma resposta HTTP 404.
 */
public class RecursoNaoEncontradoException extends RuntimeException {

    public RecursoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}
