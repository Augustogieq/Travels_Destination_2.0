package com.agenciaviagens.destinos.dto;

import com.agenciaviagens.destinos.model.Destino;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO utilizado para devolver os dados de um destino ao cliente da API.
 * Expoe, alem dos dados cadastrais, a media de avaliacoes e a quantidade
 * de avaliacoes recebidas.
 */
public class DestinoResponseDTO {

    private Long id;
    private String nome;
    private String localizacao;
    private String descricao;
    private BigDecimal precoPacote;
    private Integer hoteisDisponiveis;
    private List<String> atividadesTuristicas;
    private double mediaAvaliacao;
    private int quantidadeAvaliacoes;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;

    public static DestinoResponseDTO fromEntity(Destino destino) {
        DestinoResponseDTO dto = new DestinoResponseDTO();
        dto.id = destino.getId();
        dto.nome = destino.getNome();
        dto.localizacao = destino.getLocalizacao();
        dto.descricao = destino.getDescricao();
        dto.precoPacote = destino.getPrecoPacote();
        dto.hoteisDisponiveis = destino.getHoteisDisponiveis();
        dto.atividadesTuristicas = destino.getAtividadesTuristicas();
        dto.mediaAvaliacao = Math.round(destino.getMediaAvaliacao() * 100.0) / 100.0;
        dto.quantidadeAvaliacoes = destino.getQuantidadeAvaliacoes();
        dto.dataCriacao = destino.getDataCriacao();
        dto.dataAtualizacao = destino.getDataAtualizacao();
        return dto;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getLocalizacao() {
        return localizacao;
    }

    public String getDescricao() {
        return descricao;
    }

    public BigDecimal getPrecoPacote() {
        return precoPacote;
    }

    public Integer getHoteisDisponiveis() {
        return hoteisDisponiveis;
    }

    public List<String> getAtividadesTuristicas() {
        return atividadesTuristicas;
    }

    public double getMediaAvaliacao() {
        return mediaAvaliacao;
    }

    public int getQuantidadeAvaliacoes() {
        return quantidadeAvaliacoes;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public LocalDateTime getDataAtualizacao() {
        return dataAtualizacao;
    }
}
