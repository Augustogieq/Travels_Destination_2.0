package com.agenciaviagens.destinos.security;

import com.agenciaviagens.destinos.model.Usuario;
import com.agenciaviagens.destinos.repository.UsuarioRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementacao de UserDetailsService que carrega os usuarios
 * cadastrados no banco de dados (tabela "usuarios") para o processo
 * de autenticacao do Spring Security.
 *
 * O perfil (Role) do usuario e convertido em uma GrantedAuthority no
 * formato "ROLE_ADMIN" / "ROLE_USER", padrao esperado pelo Spring
 * Security ao usar expressoes como hasRole("ADMIN").
 */
@Service
public class UsuarioDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Usuario nao encontrado: " + username));

        return User.builder()
                .username(usuario.getUsername())
                .password(usuario.getPassword())
                .disabled(!usuario.isAtivo())
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getRole().name())))
                .build();
    }
}
