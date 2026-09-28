package com.agenciaviagens.destinos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO utilizado para receber dados de cadastro (POST) e atualizacao (PUT)
 * de um destino. Separar o DTO da entidade evita expor detalhes internos
 * (como calculo de avaliacoes) e permite validar os dados de entrada.
 */
public class DestinoRequestDTO {

    @NotBlank(message = "O nome do destino e obrigatorio")
    private String nome;

    @NotBlank(message = "A localizacao do destino e obrigatoria")
    private String localizacao;

    private String descricao;

    @NotNull(message = "O preco do pacote e obrigatorio")
    @PositiveOrZero(message = "O preco do pacote nao pode ser negativo")
    private BigDecimal precoPacote;

    @PositiveOrZero(message = "A quantidade de hoteis disponiveis nao pode ser negativa")
    private Integer hoteisDisponiveis;

    private List<String> atividadesTuristicas;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getLocalizacao() {
        return localizacao;
    }

    public void setLocalizacao(String localizacao) {
        this.localizacao = localizacao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getPrecoPacote() {
        return precoPacote;
    }

    public void setPrecoPacote(BigDecimal precoPacote) {
        this.precoPacote = precoPacote;
    }

    public Integer getHoteisDisponiveis() {
        return hoteisDisponiveis;
    }

    public void setHoteisDisponiveis(Integer hoteisDisponiveis) {
        this.hoteisDisponiveis = hoteisDisponiveis;
    }

    public List<String> getAtividadesTuristicas() {
        return atividadesTuristicas;
    }

    public void setAtividadesTuristicas(List<String> atividadesTuristicas) {
        this.atividadesTuristicas = atividadesTuristicas;
    }
}
