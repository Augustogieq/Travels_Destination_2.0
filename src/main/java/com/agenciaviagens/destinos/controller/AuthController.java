package com.agenciaviagens.destinos.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Endpoint utilitario para verificar rapidamente qual usuario esta
 * autenticado e quais permissoes (roles) ele possui — util para
 * validar a configuracao de autenticacao/autorizacao durante os testes.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> usuarioAtual(Authentication authentication) {
        List<String> perfis = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return ResponseEntity.ok(Map.of(
                "username", authentication.getName(),
                "perfis", perfis
        ));
    }
}
