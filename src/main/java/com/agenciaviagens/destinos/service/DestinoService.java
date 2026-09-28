package com.agenciaviagens.destinos.service;

import com.agenciaviagens.destinos.dto.AvaliacaoRequestDTO;
import com.agenciaviagens.destinos.dto.DestinoRequestDTO;
import com.agenciaviagens.destinos.dto.DestinoResponseDTO;
import com.agenciaviagens.destinos.exception.RecursoNaoEncontradoException;
import com.agenciaviagens.destinos.model.Destino;
import com.agenciaviagens.destinos.repository.DestinoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Camada de servico: concentra as regras de negocio relacionadas a
 * destinos de viagem (cadastro, consulta, atualizacao, avaliacao e
 * exclusao), mantendo o controller livre de logica e delegando toda
 * a persistencia (agora em PostgreSQL) ao DestinoRepository via
 * Spring Data JPA. O controller nunca acessa o repository diretamente.
 */
@Service
@Transactional
public class DestinoService {

    private final DestinoRepository destinoRepository;

    public DestinoService(DestinoRepository destinoRepository) {
        this.destinoRepository = destinoRepository;
    }

    public DestinoResponseDTO cadastrar(DestinoRequestDTO requestDTO) {
        Destino destino = new Destino(
                null,
                requestDTO.getNome(),
                requestDTO.getLocalizacao(),
                requestDTO.getDescricao(),
                requestDTO.getPrecoPacote(),
                requestDTO.getHoteisDisponiveis(),
                requestDTO.getAtividadesTuristicas()
        );
        Destino salvo = destinoRepository.save(destino);
        return DestinoResponseDTO.fromEntity(salvo);
    }

    @Transactional(readOnly = true)
    public List<DestinoResponseDTO> listarTodos() {
        return destinoRepository.findAll().stream()
                .map(DestinoResponseDTO::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public DestinoResponseDTO buscarPorId(Long id) {
        Destino destino = buscarEntidadeOuFalhar(id);
        return DestinoResponseDTO.fromEntity(destino);
    }

    /**
     * Pesquisa destinos por nome e/ou localizacao (busca parcial,
     * sem diferenciar maiusculas de minusculas), delegando o filtro
     * diretamente ao banco de dados por meio de query methods do
     * Spring Data JPA. Os dois parametros sao opcionais; quando
     * ausentes, retorna todos os destinos.
     */
    @Transactional(readOnly = true)
    public List<DestinoResponseDTO> pesquisar(String nome, String localizacao) {
        boolean temNome = nome != null && !nome.isBlank();
        boolean temLocalizacao = localizacao != null && !localizacao.isBlank();

        List<Destino> resultado;
        if (temNome && temLocalizacao) {
            resultado = destinoRepository.findByNomeContainingIgnoreCaseAndLocalizacaoContainingIgnoreCase(
                    nome.trim(), localizacao.trim());
        } else if (temNome) {
            resultado = destinoRepository.findByNomeContainingIgnoreCase(nome.trim());
        } else if (temLocalizacao) {
            resultado = destinoRepository.findByLocalizacaoContainingIgnoreCase(localizacao.trim());
        } else {
            resultado = destinoRepository.findAll();
        }

        return resultado.stream().map(DestinoResponseDTO::fromEntity).toList();
    }

    public DestinoResponseDTO atualizar(Long id, DestinoRequestDTO requestDTO) {
        Destino destino = buscarEntidadeOuFalhar(id);

        destino.setNome(requestDTO.getNome());
        destino.setLocalizacao(requestDTO.getLocalizacao());
        destino.setDescricao(requestDTO.getDescricao());
        destino.setPrecoPacote(requestDTO.getPrecoPacote());
        destino.setHoteisDisponiveis(requestDTO.getHoteisDisponiveis());
        destino.setAtividadesTuristicas(requestDTO.getAtividadesTuristicas());

        Destino atualizado = destinoRepository.save(destino);
        return DestinoResponseDTO.fromEntity(atualizado);
    }

    /**
     * Registra uma nova avaliacao para o destino, recalculando a media
     * com base no historico de notas ja recebidas.
     */
    public DestinoResponseDTO avaliar(Long id, AvaliacaoRequestDTO avaliacaoDTO) {
        Destino destino = buscarEntidadeOuFalhar(id);
        destino.registrarAvaliacao(avaliacaoDTO.getNota());
        Destino atualizado = destinoRepository.save(destino);
        return DestinoResponseDTO.fromEntity(atualizado);
    }

    public void excluir(Long id) {
        if (!destinoRepository.existsById(id)) {
            throw new RecursoNaoEncontradoException("Destino nao encontrado para o id: " + id);
        }
        destinoRepository.deleteById(id);
    }

    private Destino buscarEntidadeOuFalhar(Long id) {
        return destinoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Destino nao encontrado para o id: " + id));
    }
}
