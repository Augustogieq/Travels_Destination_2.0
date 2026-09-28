package com.agenciaviagens.destinos.controller;

import com.agenciaviagens.destinos.dto.AvaliacaoRequestDTO;
import com.agenciaviagens.destinos.dto.DestinoRequestDTO;
import com.agenciaviagens.destinos.dto.DestinoResponseDTO;
import com.agenciaviagens.destinos.service.DestinoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

/**
 * Controller REST responsavel por expor os endpoints de gerenciamento
 * de destinos de viagem. Esta classe apenas recebe as requisicoes,
 * delega o processamento para a camada de servico e monta a resposta
 * HTTP adequada — nenhuma regra de negocio deve residir aqui.
 *
 * A autorizacao por perfil e definida centralmente em SecurityConfig
 * (por URL + metodo HTTP). As anotacoes @PreAuthorize abaixo repetem
 * a mesma regra no nivel do metodo, como uma camada extra de defesa
 * (defesa em profundidade), garantindo protecao mesmo que a
 * configuracao de rotas mude no futuro.
 */
@RestController
@RequestMapping("/api/destinos")
public class DestinoController {

    private final DestinoService destinoService;

    public DestinoController(DestinoService destinoService) {
        this.destinoService = destinoService;
    }

    /**
     * Cadastra um novo destino de viagem. Somente ADMIN.
     * POST /api/destinos
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DestinoResponseDTO> cadastrar(@Valid @RequestBody DestinoRequestDTO requestDTO) {
        DestinoResponseDTO criado = destinoService.cadastrar(requestDTO);
        URI location = URI.create("/api/destinos/" + criado.getId());
        return ResponseEntity.created(location).body(criado);
    }

    /**
     * Lista todos os destinos cadastrados.
     * GET /api/destinos
     */
    @GetMapping
    public ResponseEntity<List<DestinoResponseDTO>> listarTodos() {
        return ResponseEntity.ok(destinoService.listarTodos());
    }

    /**
     * Pesquisa destinos por nome e/ou localizacao.
     * GET /api/destinos/pesquisa?nome=...&localizacao=...
     */
    @GetMapping("/pesquisa")
    public ResponseEntity<List<DestinoResponseDTO>> pesquisar(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String localizacao) {
        return ResponseEntity.ok(destinoService.pesquisar(nome, localizacao));
    }

    /**
     * Retorna os detalhes de um destino especifico.
     * GET /api/destinos/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<DestinoResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(destinoService.buscarPorId(id));
    }

    /**
     * Atualiza os dados cadastrais de um destino existente. Somente ADMIN.
     * PUT /api/destinos/{id}
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DestinoResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody DestinoRequestDTO requestDTO) {
        return ResponseEntity.ok(destinoService.atualizar(id, requestDTO));
    }

    /**
     * Registra uma nova avaliacao para o destino, recalculando sua media.
     * Qualquer usuario autenticado (ADMIN ou USER) pode avaliar.
     * PATCH /api/destinos/{id}/avaliacoes
     */
    @PatchMapping("/{id}/avaliacoes")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<DestinoResponseDTO> avaliar(
            @PathVariable Long id,
            @Valid @RequestBody AvaliacaoRequestDTO avaliacaoDTO) {
        return ResponseEntity.ok(destinoService.avaliar(id, avaliacaoDTO));
    }

    /**
     * Remove um destino do sistema. Somente ADMIN.
     * DELETE /api/destinos/{id}
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        destinoService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
