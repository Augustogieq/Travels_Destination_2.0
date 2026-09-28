package com.agenciaviagens.destinos;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class DestinosApiApplicationTests {

    @Test
    void contextLoads() {
        // Verifica que o contexto Spring sobe corretamente, com todos
        // os beans (controller, service, repository) configurados.
    }
}
