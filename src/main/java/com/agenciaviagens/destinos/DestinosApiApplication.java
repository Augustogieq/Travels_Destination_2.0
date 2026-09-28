package com.agenciaviagens.destinos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Classe principal da API de Destinos de Viagem.
 *
 * Esta aplicacao expoe endpoints REST para que a agencia de viagens
 * possa cadastrar, consultar, atualizar, avaliar e remover destinos
 * turisticos, servindo como base para integracao com aplicativos de
 * turismo, parceiros comerciais e futuras plataformas digitais.
 */
@SpringBootApplication
public class DestinosApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(DestinosApiApplication.class, args);
    }
}
