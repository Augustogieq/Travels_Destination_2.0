package com.agenciaviagens.destinos.repository;

import com.agenciaviagens.destinos.model.Destino;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repositorio Spring Data JPA para a entidade Destino.
 *
 * O Spring Data gera automaticamente a implementacao dos metodos
 * abaixo a partir da assinatura (query derivation), incluindo o CRUD
 * basico herdado de JpaRepository (save, findById, findAll,
 * deleteById, existsById etc).
 */
public interface DestinoRepository extends JpaRepository<Destino, Long> {

    List<Destino> findByNomeContainingIgnoreCase(String nome);

    List<Destino> findByLocalizacaoContainingIgnoreCase(String localizacao);

    List<Destino> findByNomeContainingIgnoreCaseAndLocalizacaoContainingIgnoreCase(
            String nome, String localizacao);
}
