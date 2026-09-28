package com.agenciaviagens.destinos.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

/**
 * DTO utilizado para registrar uma nova avaliacao (nota) de um destino.
 * A nota deve estar entre 0 e 5, seguindo o padrao comum de avaliacoes
 * de destinos turisticos e hoteis.
 */
public class AvaliacaoRequestDTO {

    @NotNull(message = "A nota da avaliacao e obrigatoria")
    @DecimalMin(value = "0.0", message = "A nota minima e 0")
    @DecimalMax(value = "5.0", message = "A nota maxima e 5")
    private Double nota;

    public Double getNota() {
        return nota;
    }

    public void setNota(Double nota) {
        this.nota = nota;
    }
}
