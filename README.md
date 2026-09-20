# API REST de Destinos de Viagem

API RESTful desenvolvida em **Java + Spring Boot** para uma agência de
viagens que está modernizando seus serviços digitais.

Este projeto está em sua **segunda versão (evolução)**: a partir de
agora os dados são **persistidos em PostgreSQL** via **Spring Data
JPA**, e a API conta com **autenticação e autorização por perfil**
(ADMIN/USER) via **Spring Security**. A primeira versão (armazenamento
em memória, sem segurança) está preservada no histórico do Git, no
commit `Primeira versao da API REST de destinos de viagem (Spring Boot)`.

---

## 1. Visão geral da evolução

A primeira versão da API permitia cadastrar e gerenciar destinos
turísticos, mas os dados eram mantidos apenas em memória (perdidos a
cada reinício) e qualquer pessoa podia executar qualquer operação,
sem controle de acesso. Isso é inaceitável em um ambiente real, onde
a API é consumida por parceiros, aplicativos externos e equipes
internas.

Nesta evolução:

- Os destinos passaram a ser **persistidos em um banco de dados
  PostgreSQL real**, usando **Spring Data JPA** — os dados agora
  sobrevivem a reinicializações da aplicação;
- Foi criada uma entidade `Usuario`, também persistida no banco, com
  senha criptografada (BCrypt) e um perfil de acesso (`ADMIN` ou
  `USER`);
- A API agora exige **autenticação HTTP Basic** para operações
  sensíveis, e aplica **autorização por perfil**: apenas usuários
  `ADMIN` podem cadastrar, atualizar ou excluir destinos; qualquer
  usuário autenticado (`ADMIN` ou `USER`) pode registrar avaliações;
  a consulta de destinos continua pública.

## 2. Arquitetura

A arquitetura em camadas da primeira versão foi mantida e ganhou uma
nova camada transversal de **segurança**:

```
Cliente HTTP (com ou sem credenciais)
     │
     ▼
┌───────────────────────────┐
│   Spring Security Filter    │  ← Autentica (HTTP Basic) e autoriza
│  (SecurityConfig + filtros) │    a requisicao antes mesmo do Controller
└─────────────┬───────────────┘
              ▼
┌───────────────────────────┐
│        Controller           │  ← Recebe a requisicao ja autenticada,
│  (DestinoController,        │    valida entrada, delega ao Service.
│   AuthController)           │    @PreAuthorize reforca a regra de perfil.
└─────────────┬───────────────┘
              ▼
┌───────────────────────────┐
│         Service             │  ← Regras de negocio (calculo de media,
│    (DestinoService)         │    pesquisa, validacoes de fluxo)
└─────────────┬───────────────┘
              ▼
┌───────────────────────────┐
│   Repository (Spring Data JPA)│  ← DestinoRepository, UsuarioRepository
└─────────────┬───────────────┘
              ▼
┌───────────────────────────┐
│         PostgreSQL           │  ← Persistencia real dos dados
└───────────────────────────┘
```

Pacotes principais:

```
src/main/java/com/agenciaviagens/destinos/
├── DestinosApiApplication.java
├── controller/
│   ├── DestinoController.java      # Endpoints de destinos (com @PreAuthorize)
│   └── AuthController.java         # GET /api/auth/me (usuario autenticado)
├── service/
│   └── DestinoService.java         # Regras de negocio, agora sobre JPA
├── repository/
│   ├── DestinoRepository.java      # JpaRepository<Destino, Long>
│   └── UsuarioRepository.java      # JpaRepository<Usuario, Long>
├── model/
│   ├── Destino.java                # Entidade JPA (@Entity)
│   ├── Usuario.java                # Entidade JPA (@Entity)
│   └── Role.java                   # Enum ADMIN / USER
├── dto/
│   ├── DestinoRequestDTO.java
│   ├── DestinoResponseDTO.java
│   └── AvaliacaoRequestDTO.java
├── security/
│   ├── SecurityConfig.java             # Regras de autenticacao/autorizacao
│   ├── UsuarioDetailsService.java      # Carrega usuarios do banco
│   ├── ApiAuthenticationEntryPoint.java # Resposta 401 padronizada
│   └── ApiAccessDeniedHandler.java     # Resposta 403 padronizada
├── exception/
│   ├── RecursoNaoEncontradoException.java
│   ├── ErroRespostaDTO.java
│   └── GlobalExceptionHandler.java
└── config/
    └── DadosIniciaisConfig.java    # Semeia usuarios e destinos de exemplo
```

### Por que persistir com Spring Data JPA?

Armazenamento em memória (versão 1) perde todos os dados a cada
reinício e não escala para múltiplas instâncias da aplicação. Com
**Spring Data JPA + PostgreSQL**:

- os dados são duráveis e compartilhados entre instâncias da API;
- o Spring Data JPA gera automaticamente a implementação de operações
  de CRUD e de consultas derivadas (`findByNomeContainingIgnoreCase`,
  por exemplo), reduzindo código repetitivo de acesso a dados;
- o `DestinoRepository`/`UsuarioRepository` isolam completamente o
  acesso a dados: `Controller` e `Service` não sabem (nem precisam
  saber) que o banco é PostgreSQL — poderia ser outro banco relacional
  sem impactar as demais camadas.

### Por que Spring Security com HTTP Basic + perfis?

- **Spring Security** é o padrão de mercado para autenticação/
  autorização em aplicações Spring, com integração nativa ao Spring
  Boot e ao Spring Data JPA (via `UserDetailsService`).
- **HTTP Basic, sem sessão (stateless)** foi escolhido por ser simples
  de configurar, testar (`curl -u usuario:senha`) e adequado para uma
  API REST consumida por outros sistemas — cada requisição carrega
  suas próprias credenciais, sem necessidade de gerenciar sessões no
  servidor. (Ver seção 10 "Próximos passos" para evolução futura com
  JWT.)
- **Perfis ADMIN/USER** modelam de forma simples e direta o requisito
  de negócio: apenas administradores da agência podem alterar o
  catálogo de destinos; qualquer usuário autenticado pode avaliar um
  destino que já viajou.
- As senhas são sempre armazenadas com **hash BCrypt**
  (`PasswordEncoder`), nunca em texto puro.

## 3. Tecnologias utilizadas (atualizado)

| Tecnologia | Papel nesta evolução |
|---|---|
| **Java 17** | Linguagem da aplicação. |
| **Spring Boot 3** | Framework base da API. |
| **Spring Web (MVC)** | Endpoints REST. |
| **Spring Data JPA** | Mapeamento objeto-relacional e repositórios de acesso a dados. |
| **Hibernate** (via Spring Data JPA) | Implementação de JPA utilizada por baixo dos panos. |
| **PostgreSQL** | Banco de dados relacional onde os dados são persistidos. |
| **Spring Security** | Autenticação (HTTP Basic) e autorização (perfis ADMIN/USER) dos endpoints. |
| **BCrypt** (via Spring Security Crypto) | Hash seguro de senhas dos usuários. |
| **H2 Database** | Banco em memória usado **apenas** nos testes automatizados (`mvn test`), para não depender de PostgreSQL no ambiente de build/CI. |
| **Maven** | Build e gerenciamento de dependências. |

## 4. Modelo de dados

### Tabela `destinos`

| Coluna | Tipo | Observações |
|---|---|---|
| `id` | BIGINT | Chave primária, auto-incremento. |
| `nome` | VARCHAR(150) | Obrigatório. |
| `localizacao` | VARCHAR(150) | Obrigatório. |
| `descricao` | VARCHAR(2000) | Opcional. |
| `preco_pacote` | NUMERIC(12,2) | Obrigatório. |
| `hoteis_disponiveis` | INTEGER | Opcional. |
| `soma_avaliacoes` / `quantidade_avaliacoes` / `media_avaliacao` | DOUBLE / INT / DOUBLE | Controle interno da média de avaliações. |
| `data_criacao` / `data_atualizacao` | TIMESTAMP | Preenchidos automaticamente (`@PrePersist` / `@PreUpdate`). |

A lista de atividades turísticas é armazenada em uma tabela auxiliar
`destino_atividades` (relacionamento `@ElementCollection`), ligada a
`destinos` pela chave estrangeira `destino_id`.

### Tabela `usuarios`

| Coluna | Tipo | Observações |
|---|---|---|
| `id` | BIGINT | Chave primária, auto-incremento. |
| `username` | VARCHAR(100) | Único, usado no login. |
| `password` | VARCHAR | Hash BCrypt da senha — nunca texto puro. |
| `role` | VARCHAR(20) | `ADMIN` ou `USER`. |
| `ativo` | BOOLEAN | Permite desativar um usuário sem excluí-lo. |

As tabelas são criadas/ajustadas automaticamente pelo Hibernate
(`spring.jpa.hibernate.ddl-auto=update`) a partir das entidades JPA —
não é necessário criar o schema manualmente.

## 5. Configuração do banco de dados (PostgreSQL)

### Opção A — Subir o PostgreSQL com Docker (recomendado)

O projeto já inclui um `docker-compose.yml`:

```bash
docker compose up -d
```

Isso sobe um PostgreSQL 16 em `localhost:5432`, com:
- banco: `agencia_viagens`
- usuário: `postgres`
- senha: `postgres`

### Opção B — PostgreSQL instalado localmente

Crie o banco manualmente:

```sql
CREATE DATABASE agencia_viagens;
```

### Configurando a conexão

A conexão é definida em `src/main/resources/application.properties`,
com valores padrão que já funcionam com a Opção A/B acima:

```properties
spring.datasource.url=${DB_URL:jdbc:postgresql://localhost:5432/agencia_viagens}
spring.datasource.username=${DB_USERNAME:postgres}
spring.datasource.password=${DB_PASSWORD:postgres}
spring.datasource.driver-class-name=org.postgresql.Driver

spring.jpa.hibernate.ddl-auto=update
```

Para usar outras credenciais/host sem editar o arquivo, basta definir
variáveis de ambiente antes de subir a aplicação:

```bash
export DB_URL=jdbc:postgresql://localhost:5432/agencia_viagens
export DB_USERNAME=postgres
export DB_PASSWORD=minha_senha
```

## 6. Autenticação e usuários de teste

A API usa **HTTP Basic Authentication**. Em cada requisição protegida,
envie o cabeçalho `Authorization` com usuário e senha (ou use a opção
`-u` do `curl`).

Ao iniciar a aplicação pela primeira vez (banco vazio), dois usuários
de teste são criados automaticamente:

| Username | Senha | Perfil |
|---|---|---|
| `admin` | `admin123` | `ADMIN` |
| `usuario` | `usuario123` | `USER` |

> As senhas são armazenadas com hash BCrypt no banco — os valores
> acima são apenas as senhas em texto puro para uso no login/testes.

### Verificando o usuário autenticado

```bash
curl -u admin:admin123 http://localhost:8080/api/auth/me
```

Resposta esperada:

```json
{
  "username": "admin",
  "perfis": ["ROLE_ADMIN"]
}
```

## 7. Regras de autorização por endpoint

| Método | Endpoint | Quem pode acessar |
|---|---|---|
| `GET` | `/api/destinos` | Público (sem autenticação) |
| `GET` | `/api/destinos/pesquisa` | Público (sem autenticação) |
| `GET` | `/api/destinos/{id}` | Público (sem autenticação) |
| `POST` | `/api/destinos` | Somente `ADMIN` |
| `PUT` | `/api/destinos/{id}` | Somente `ADMIN` |
| `DELETE` | `/api/destinos/{id}` | Somente `ADMIN` |
| `PATCH` | `/api/destinos/{id}/avaliacoes` | Qualquer usuário autenticado (`ADMIN` ou `USER`) |
| `GET` | `/api/auth/me` | Qualquer usuário autenticado |

Respostas de erro relacionadas à segurança seguem o mesmo formato
padronizado do restante da API:

- **401 Unauthorized** — requisição sem credenciais (ou credenciais
  inválidas) em um endpoint protegido;
- **403 Forbidden** — usuário autenticado, mas com perfil sem
  permissão para a operação (ex.: `USER` tentando excluir um destino).

```json
{
  "timestamp": "2026-09-20T10:20:00",
  "status": 403,
  "erro": "Acesso negado",
  "mensagem": "Seu perfil de acesso nao tem permissao para realizar esta operacao"
}
```

## 8. Como executar o projeto

### Pré-requisitos

- Java 17 ou superior (`java -version`);
- Maven 3.8+ (`mvn -version`) — ou use a IDE para rodar diretamente;
- PostgreSQL disponível (via Docker, conforme seção 5, ou instalação local).

### Passo a passo

```bash
# 1. Suba o banco de dados
docker compose up -d

# 2. Rode a aplicação
mvn spring-boot:run
```

A API sobe em `http://localhost:8080`. Ao subir pela primeira vez,
os usuários e destinos de exemplo (seção 6 e histórico da versão 1)
são criados automaticamente no banco.

### Executando o JAR gerado

```bash
mvn clean package
java -jar target/destinos-api-1.0.0.jar
```

### Rodando os testes automatizados

Os testes usam um banco **H2 em memória** (perfil `test`), então não
é necessário ter o PostgreSQL rodando para executá-los:

```bash
mvn test
```

Os testes incluem verificações específicas de segurança (classe
`DestinoSegurancaTest`): acesso público à listagem, `401` sem
credenciais, `403` para perfil `USER` tentando cadastrar, e `201`
para perfil `ADMIN`.

## 9. Exemplos de acesso (curl)

```bash
# Consulta publica (sem autenticacao)
curl http://localhost:8080/api/destinos

# Cadastrar destino (requer ADMIN)
curl -X POST http://localhost:8080/api/destinos \
  -u admin:admin123 \
  -H "Content-Type: application/json" \
  -d '{
        "nome": "Gramado",
        "localizacao": "Rio Grande do Sul, Brasil",
        "descricao": "Cidade serrana com clima europeu",
        "precoPacote": 1200.00,
        "hoteisDisponiveis": 20,
        "atividadesTuristicas": ["Mini Mundo", "Rota Romantica"]
      }'

# Tentar cadastrar com perfil USER -> 403 Forbidden
curl -X POST http://localhost:8080/api/destinos \
  -u usuario:usuario123 \
  -H "Content-Type: application/json" \
  -d '{"nome":"Teste","localizacao":"Teste","precoPacote":100}'

# Registrar avaliacao (qualquer usuario autenticado)
curl -X PATCH http://localhost:8080/api/destinos/1/avaliacoes \
  -u usuario:usuario123 \
  -H "Content-Type: application/json" \
  -d '{"nota": 4.5}'

# Excluir destino (requer ADMIN)
curl -X DELETE http://localhost:8080/api/destinos/1 -u admin:admin123
```

## 10. Próximos passos (evolução futura)

- Migrar de HTTP Basic para **autenticação via JWT** (token com
  expiração), evitando reenviar usuário/senha em toda requisição;
- Adicionar endpoint de **registro de novos usuários** (hoje os
  usuários são pré-cadastrados via `DadosIniciaisConfig`);
- Adotar **Flyway** ou **Liquibase** para versionar o schema do banco
  em vez de `ddl-auto=update`;
- Adicionar paginação e ordenação na listagem/pesquisa de destinos;
- Documentação interativa via **springdoc-openapi (Swagger UI)**;
- Testes de integração adicionais cobrindo a camada de serviço com
  banco H2.

---

Projeto desenvolvido como evolução do desafio de planejamento de
arquitetura e desenvolvimento inicial de API REST, agora com
persistência em PostgreSQL e segurança via Spring Security.
