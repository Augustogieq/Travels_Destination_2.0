package com.agenciaviagens.destinos.repository;

import com.agenciaviagens.destinos.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repositorio Spring Data JPA para a entidade Usuario. Usado pela
 * camada de seguranca (UserDetailsService) para carregar os dados de
 * autenticacao a partir do banco de dados.
 */
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByUsername(String username);

    boolean existsByUsername(String username);
}
