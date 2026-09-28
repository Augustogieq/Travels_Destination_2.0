package com.agenciaviagens.destinos.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa um destino de viagem oferecido pela agencia.
 *
 * A partir desta versao (evolucao com banco de dados), os objetos
 * desta classe sao persistidos em PostgreSQL por meio do Spring Data
 * JPA. A entidade concentra as informacoes essenciais de um destino:
 * nome, localizacao, descricao, preco do pacote, disponibilidade de
 * hoteis, atividades turisticas oferecidas e a media de avaliacoes
 * recebidas.
 */
@Entity
@Table(name = "destinos")
public class Destino {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(nullable = false, length = 150)
    private String localizacao;

    @Column(length = 2000)
    private String descricao;

    @Column(name = "preco_pacote", nullable = false, precision = 12, scale = 2)
    private BigDecimal precoPacote;

    @Column(name = "hoteis_disponiveis")
    private Integer hoteisDisponiveis;

    /**
     * Lista de atividades turisticas armazenada em uma tabela auxiliar
     * (destino_atividades), relacionada ao destino por chave estrangeira.
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "destino_atividades", joinColumns = @JoinColumn(name = "destino_id"))
    @Column(name = "atividade", length = 200)
    private List<String> atividadesTuristicas = new ArrayList<>();

    // Controle de avaliacoes: guardamos soma e quantidade para
    // recalcular a media de forma consistente a cada nova avaliacao.
    @Column(name = "soma_avaliacoes", nullable = false)
    private double somaAvaliacoes = 0.0;

    @Column(name = "quantidade_avaliacoes", nullable = false)
    private int quantidadeAvaliacoes = 0;

    @Column(name = "media_avaliacao", nullable = false)
    private double mediaAvaliacao = 0.0;

    @Column(name = "data_criacao", nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    @Column(name = "data_atualizacao", nullable = false)
    private LocalDateTime dataAtualizacao;

    public Destino() {
    }

    public Destino(Long id, String nome, String localizacao, String descricao,
                    BigDecimal precoPacote, Integer hoteisDisponiveis,
                    List<String> atividadesTuristicas) {
        this.id = id;
        this.nome = nome;
        this.localizacao = localizacao;
        this.descricao = descricao;
        this.precoPacote = precoPacote;
        this.hoteisDisponiveis = hoteisDisponiveis;
        this.atividadesTuristicas = atividadesTuristicas != null ? atividadesTuristicas : new ArrayList<>();
    }

    @PrePersist
    protected void aoPersistir() {
        LocalDateTime agora = LocalDateTime.now();
        this.dataCriacao = agora;
        this.dataAtualizacao = agora;
    }

    @PreUpdate
    protected void aoAtualizar() {
        this.dataAtualizacao = LocalDateTime.now();
    }

    /**
     * Registra uma nova avaliacao (nota de 0 a 5) e recalcula a media
     * do destino de forma incremental.
     */
    public void registrarAvaliacao(double nota) {
        this.somaAvaliacoes += nota;
        this.quantidadeAvaliacoes += 1;
        this.mediaAvaliacao = this.somaAvaliacoes / this.quantidadeAvaliacoes;
    }

    // ---- Getters e Setters ----

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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
        this.atividadesTuristicas = atividadesTuristicas != null ? atividadesTuristicas : new ArrayList<>();
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
