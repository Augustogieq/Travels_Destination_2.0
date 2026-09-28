package com.agenciaviagens.destinos.security;

import com.agenciaviagens.destinos.model.Role;
import com.agenciaviagens.destinos.model.Usuario;
import com.agenciaviagens.destinos.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Testes de integracao focados nas regras de autenticacao e
 * autorizacao por perfil (ADMIN/USER) da API de destinos.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class DestinoSegurancaTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void configurarUsuarios() {
        usuarioRepository.deleteAll();
        usuarioRepository.save(new Usuario("admin_teste", passwordEncoder.encode("senha123"), Role.ADMIN));
        usuarioRepository.save(new Usuario("user_teste", passwordEncoder.encode("senha123"), Role.USER));
    }

    @Test
    void listarDestinos_devePermitirAcessoSemAutenticacao() throws Exception {
        mockMvc.perform(get("/api/destinos"))
                .andExpect(status().isOk());
    }

    @Test
    void cadastrarDestino_semAutenticacao_deveRetornar401() throws Exception {
        mockMvc.perform(post("/api/destinos")
                        .contentType("application/json")
                        .content(corpoDestinoValido()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void cadastrarDestino_comPerfilUser_deveRetornar403() throws Exception {
        mockMvc.perform(post("/api/destinos")
                        .with(SecurityMockMvcRequestPostProcessors.httpBasic("user_teste", "senha123"))
                        .contentType("application/json")
                        .content(corpoDestinoValido()))
                .andExpect(status().isForbidden());
    }

    @Test
    void cadastrarDestino_comPerfilAdmin_devePermitir() throws Exception {
        mockMvc.perform(post("/api/destinos")
                        .with(SecurityMockMvcRequestPostProcessors.httpBasic("admin_teste", "senha123"))
                        .contentType("application/json")
                        .content(corpoDestinoValido()))
                .andExpect(status().isCreated());
    }

    private String corpoDestinoValido() {
        return """
                {
                  "nome": "Foz do Iguacu",
                  "localizacao": "Parana, Brasil",
                  "descricao": "Cataratas e natureza exuberante",
                  "precoPacote": 1500.00,
                  "hoteisDisponiveis": 10,
                  "atividadesTuristicas": ["Cataratas do Iguacu", "Parque das Aves"]
                }
                """;
    }
}
