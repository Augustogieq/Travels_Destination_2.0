package com.agenciaviagens.destinos.config;

import com.agenciaviagens.destinos.model.Destino;
import com.agenciaviagens.destinos.model.Role;
import com.agenciaviagens.destinos.model.Usuario;
import com.agenciaviagens.destinos.repository.DestinoRepository;
import com.agenciaviagens.destinos.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.List;

/**
 * Popula o banco de dados com usuarios e destinos de exemplo na
 * primeira execucao da aplicacao, facilitando os testes manuais dos
 * endpoints (autenticacao e operacoes sobre destinos) logo apos subir
 * o servidor. A carga so acontece se as tabelas ainda estiverem
 * vazias, evitando duplicar registros a cada reinicializacao.
 */
@Configuration
public class DadosIniciaisConfig {

    @Bean
    CommandLineRunner carregarDadosDeExemplo(UsuarioRepository usuarioRepository,
                                              DestinoRepository destinoRepository,
                                              PasswordEncoder passwordEncoder) {
        return args -> {
            if (usuarioRepository.count() == 0) {
                usuarioRepository.save(new Usuario("admin", passwordEncoder.encode("admin123"), Role.ADMIN));
                usuarioRepository.save(new Usuario("usuario", passwordEncoder.encode("usuario123"), Role.USER));
            }

            if (destinoRepository.count() == 0) {
                Destino rioDeJaneiro = new Destino(
                        null, "Rio de Janeiro", "Rio de Janeiro, Brasil",
                        "Cidade maravilhosa, com praias, montanhas e o Cristo Redentor.",
                        new BigDecimal("1899.90"), 42,
                        List.of("Passeio no Pao de Acucar", "Trilha na Floresta da Tijuca", "Tour pelas praias")
                );
                rioDeJaneiro.registrarAvaliacao(4.5);
                rioDeJaneiro.registrarAvaliacao(4.0);

                Destino lisboa = new Destino(
                        null, "Lisboa", "Lisboa, Portugal",
                        "Capital portuguesa com historia, gastronomia e vista para o rio Tejo.",
                        new BigDecimal("2599.00"), 30,
                        List.of("Passeio no eletrico 28", "Visita ao Mosteiro dos Jeronimos", "Fado ao vivo")
                );
                lisboa.registrarAvaliacao(5.0);

                destinoRepository.save(rioDeJaneiro);
                destinoRepository.save(lisboa);
            }
        };
    }
}
