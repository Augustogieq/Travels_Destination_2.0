package com.agenciaviagens.destinos.model;

/**
 * Perfis de acesso suportados pela API.
 *
 * - ADMIN: acesso completo, incluindo operacoes sensiveis (cadastrar,
 *   atualizar e excluir destinos).
 * - USER: acesso de consulta e a operacoes de menor risco, como
 *   registrar avaliacoes de destinos.
 */
public enum Role {
    ADMIN,
    USER
}
