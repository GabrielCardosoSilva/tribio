# Trabio 🔧

> Plataforma profissional que conecta clientes a prestadores de serviços autônomos em todo o Brasil.

[![Java](https://img.shields.io/badge/Java-24-orange?logo=openjdk)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.4.5-brightgreen?logo=springboot)](https://spring.io/projects/spring-boot)
[![Spring Security](https://img.shields.io/badge/Spring_Security-6-green?logo=springsecurity)](https://spring.io/projects/spring-security)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Neon_Serverless-blue?logo=postgresql)](https://neon.tech/)
[![H2 Database](https://img.shields.io/badge/H2-In_Memory_(Dev)-darkblue)](https://www.h2database.com/)
[![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?logo=docker)](https://www.docker.com/)
[![OpenAPI](https://img.shields.io/badge/OpenAPI-Swagger_3.0-85EA2D?logo=swagger)](http://localhost:8080/swagger-ui/index.html)
[![Tests](https://img.shields.io/badge/Tests-34_Passed_(JUnit_5)-success)](https://junit.org/junit5/)
[![License](https://img.shields.io/badge/license-MIT-blue)](LICENSE)

---

## 📋 Sobre o Projeto

O **Trabio** é uma solução completa para aproximar clientes e profissionais autônomos — eletricistas, encanadores, pintores, marceneiros, pedreiros, chaveiros e outros prestadores. Sem taxas abusivas ou intermediários desnecessários, o contato é direto e seguro via WhatsApp e canais digitais.

A plataforma conta com arquitetura robusta em **Spring Boot 3.4.5**, rigorosa observância à **LGPD**, autenticação **JWT com blacklist no logout**, proteção contra força bruta por **Rate Limiting (Bucket4j)**, suíte de **testes automatizados (unitários e de integração)** e documentação OpenAPI 3.0 via **Swagger**.

---

## ✨ Funcionalidades Principais

- 🔍 **Busca e Descoberta Inteligente**: Filtre profissionais por estado, cidade, categoria de serviço e termos de busca com contadores em tempo real.
- 👤 **Perfis Especializados**:
  - **Cliente**: Busca prestadores, gerencia favoritos, publica avaliações e visualiza notificações.
  - **Prestador**: Gerencia dados comerciais, áreas atendidas, links sociais, portfólio de fotos e pacotes de serviços.
- ⭐ **Avaliações com Média Dinâmica**: Sistema com nota de 1 a 5 estrelas e comentários, com recálculo automático da média do prestador.
- ❤️ **Favoritos**: Salve prestadores preferidos com um clique para rápido contato posterior.
- 📸 **Portfólio com Upload Seguro**: Galeria de fotos de serviços com validação de formato/tamanho e proteção contra *path traversal*.
- 🔔 **Central de Notificações**: Notificações automáticas para novas avaliações e adições a listas de favoritos.
- 📱 **Painel de Controle Modular**: Dashboard moderno com abas desacopladas (Perfil, Portfólio, Notificações, Favoritos).
- 🛡️ **Conformidade LGPD**:
  - Aceite explícito de Termos de Uso e Política de Privacidade no cadastro.
  - Endpoint para autodestruição/exclusão de conta conforme o Art. 18 da LGPD (`DELETE /api/usuarios/me`).
  - Auditoria assíncrona de eventos sensíveis via `AuditoriaService`.

---

## 🏗️ Arquitetura e Engenharia de Software

O backend foi reestruturado seguindo princípios de **Clean Code**, **camadas limpas** e **responsabilidade única**:

```
[ Cliente HTTP / Frontend ]
          │
          ▼
   [ RateLimitFilter ]          ──▶ Limitação por IP (Bucket4j)
          │
          ▼
   [ JwtAuthenticationFilter ]  ──▶ Validação JWT + Blacklist
          │
          ▼
   [ REST Controllers ]         ──▶ Entrada e DTOs documentados com Swagger
          │
          ▼
   [ Service Layer ]            ──▶ Regras de negócio, auditoria e transações
    ├── AuthService (logout, blacklist, login)
    ├── UsuarioService (perfil, upload de avatar, exclusão LGPD)
    ├── UsuarioAutenticadoService (contexto do usuário logado)
    ├── PrestadorService (favoritos, busca, portfólio)
    ├── AvaliacaoService (médias, cálculo de rating)
    ├── NotificacaoService (mensagens e eventos)
    ├── FileService (armazenamento e higienização de arquivos)
    └── AuditoriaService (logs assíncronos)
          │
          ▼
   [ Spring Data JPA Repositories ]
          │
          ▼
   [ Banco de Dados ] ────────────▶ H2 (Dev) | Neon PostgreSQL (Prod/Staging)
```

### 🎯 Tratamento Global de Erros (`@ControllerAdvice`)
Nenhum stacktrace de erro é exposto ao cliente. A API padroniza respostas de falha no formato `ErrorResponse`:

```json
{
  "timestamp": "2026-10-13T21:30:00.000Z",
  "status": 404,
  "error": "Prestador não encontrado com o ID: 42",
  "path": "/api/prestadores/42"
}
```

Tratamento customizado para:
- `400 Bad Request` (validação de campos `@Valid`, JSON malformado)
- `401 Unauthorized` (`SecurityErrorHandler` para tokens ausentes/inválidos)
- `403 Forbidden` (permissões insuficientes por Role)
- `404 Not Found` (recursos inexistentes)
- `409 Conflict` (e-mails duplicados)
- `413 Payload Too Large` (uploads além do limite de tamanho)
- `422 Unprocessable Entity` (regras de negócio violadas)
- `429 Too Many Requests` (bloqueio do Rate Limiting)
- `500 Internal Server Error` (erros genéricos protegidos)

---

## ⚙️ Separação de Ambientes

O projeto conta com perfis Spring dedicados:

| Perfil | Banco de Dados | Características | Como Usar |
|---|---|---|---|
| **`dev`** *(padrão)* | **H2 (em memória)** | Inicialização instantânea, recriação automática do schema e carga de dados de exemplo via `DataInitializer`. | `mvn spring-boot:run` (já ativo por padrão) |
| **`staging`** | **PostgreSQL (Neon)** | Ambiente de homologação em nuvem com logs SQL detalhados. | `SPRING_PROFILES_ACTIVE=staging` |
| **`prod`** | **PostgreSQL (Neon)** | Otimizado para produção com pool HikariCP configurado para conexões serverless (`max-lifetime=240000`, `idle-timeout=120000`). | `SPRING_PROFILES_ACTIVE=prod` |

---

## 🛠️ Stack Tecnológica

### Backend
- **Java 24** com modern switch expressions e records
- **Spring Boot 3.4.5** (Web, Data JPA, Security, Validation, Actuator)
- **Spring Security 6** com **JJWT 0.12.6** (Tokens JWT assinados com HMAC-SHA256)
- **Bucket4j 8.10.1** (Token bucket rate limiting)
- **SpringDoc OpenAPI 2.6.0** (OpenAPI 3.0 e interface Swagger UI)
- **PostgreSQL Driver** (comunicação com instâncias Neon Serverless)
- **H2 Database** (para execução e testes isolados em memória)

### Frontend
- **HTML5 Semântico** com foco em acessibilidade e SEO
- **Vanilla CSS3** modularizado ([painel.css](file:///c:/Users/Silva/Downloads/Trabo/Trabo/src/main/resources/static/css/painel.css)) com design responsivo, variáveis CSS e tema consistente
- **Vanilla JavaScript moderno** ([painel.js](file:///c:/Users/Silva/Downloads/Trabo/Trabo/src/main/resources/static/js/painel.js)) assíncrono com `fetch`, manipulação modular de abas e feedback dinâmico
- **Tipografia**: Outfit (Google Fonts)

### DevOps & Qualidade
- **Docker & Docker Compose** (imagem baseada em `eclipse-temurin:24-jre`)
- **JUnit 5 + Mockito** (testes unitários dos serviços)
- **MockMvc + SpringBootTest** (testes de integração ponta a ponta dos controllers e filtros)

---

## 🚀 Como Executar o Projeto

### Pré-requisitos
- **Java 24** instalado (ou execute via Docker)
- **Git**

---

### Opção 1: Rodar Localmente em Modo DEV (Zero Configuração)

O perfil `dev` utiliza banco **H2 em memória** e insere automaticamente categorias e prestadores de exemplo para testes:

```bash
# Clone o repositório
git clone https://github.com/GabrielCardosoSilva/tribio.git
cd tribio/Trabo

# No Windows:
.\mvnw.cmd spring-boot:run

# No Linux / macOS:
./mvnw spring-boot:run
```

Acesse no navegador:
- 🌐 **Aplicação Web**: [http://localhost:8080](http://localhost:8080)
- 📑 **Swagger UI**: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
- 🗄️ **Console H2**: [http://localhost:8080/h2-console](http://localhost:8080/h2-console) (JDBC URL: `jdbc:h2:mem:trabio`)

---

### Opção 2: Rodar com Docker Compose (Modo Produção)

1. Crie o arquivo `.env` na pasta `Trabo/`:
   ```bash
   cp Trabo/.env.example Trabo/.env
   ```
2. Preencha com as credenciais do seu banco PostgreSQL (ex: [Neon](https://neon.tech)):
   ```env
   DB_URL=jdbc:postgresql://<neon-host>:5432/<dbname>?sslmode=require
   DB_USER=seu_usuario
   DB_PASSWORD=sua_senha
   JWT_SECRET=seu_secret_base64_com_pelo_menos_256_bits
   JWT_EXPIRATION_MS=86400000
   ```
3. Suba o container na raiz do projeto:
   ```bash
   docker compose up -d --build
   ```

Acesse: **[http://localhost:8080](http://localhost:8080)**

---

## 🧪 Testes Automatizados

O projeto conta com suíte automatizada cobrindo fluxos essenciais de ponta a ponta:

```bash
cd Trabo
.\mvnw.cmd test    # Windows
./mvnw test        # Linux/macOS
```

### Cobertura dos Testes:
- `AuthServiceTest` (6 testes): Cadastro de clientes, duplicidade de e-mail, login com credenciais válidas/inválidas e blacklist no logout.
- `CategoriaServiceTest` (5 testes): Criação, listagem, busca por ID/nome e validações.
- `AvaliacaoServiceTest` (4 testes): Registro de avaliação, bloqueio de autoavaliação, cálculo dinâmico de nota média.
- `UsuarioServiceTest` (3 testes): Obtenção de perfil, atualização de avatar e exclusão de conta LGPD.
- `PrestadorServiceTest` (4 testes): Cadastro de prestadores, listagem, filtros de busca e sistema de favoritos.
- `NotificacaoServiceTest` (2 testes): Criação e listagem de notificações de usuários.
- `AuthControllerIntegrationTest` (3 testes MockMvc): Fluxo completo HTTP de cadastro, login e obtenção de token Bearer.
- `PrestadorControllerIntegrationTest` (4 testes MockMvc): Busca pública, validação de rotas protegidas e favoritos com JWT.
- `GlobalExceptionHandlerIntegrationTest` (2 testes MockMvc): Respostas padronizadas para DTOs inválidos e JSON malformado.

**Resultado:** `34 testes executados, 0 falhas, 0 erros (BUILD SUCCESS)`.

---

## 🔌 Documentação da API (OpenAPI / Swagger)

A especificação completa está documentada com anotações `@Operation`, `@ApiResponse`, `@Schema` e autenticação JWT `bearerAuth`:

- **Interface Interativa:** [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
- **JSON OpenAPI 3.0:** [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

### Principais Rotas

| Método | Endpoint | Descrição | Permissão |
|---|---|---|:---:|
| `POST` | `/api/auth/registro/usuario` | Cadastro de novo cliente | Público |
| `POST` | `/api/auth/registro/prestador` | Cadastro de novo prestador | Público |
| `POST` | `/api/auth/login` | Login e emissão de JWT | Público |
| `POST` | `/api/auth/logout` | Invalidação do token (blacklist) | Autenticado |
| `GET` | `/api/prestadores` | Busca com filtros (cidade, estado, categoria, busca) | Público |
| `GET` | `/api/prestadores/{id}` | Perfil completo do prestador com portfólio | Público |
| `POST` | `/api/prestadores/{id}/favoritar` | Adiciona/remove prestador dos favoritos | `ROLE_USUARIO` |
| `POST` | `/api/prestadores/{id}/avaliacoes` | Avalia o prestador (1 a 5 estrelas) | `ROLE_USUARIO` |
| `GET` | `/api/categorias` | Lista categorias ativas | Público |
| `GET` | `/api/usuarios/me` | Dados do perfil do usuário logado | Autenticado |
| `POST` | `/api/usuarios/me/foto` | Upload da foto de perfil | Autenticado |
| `DELETE` | `/api/usuarios/me` | Exclusão definitiva de conta (LGPD Art. 18) | Autenticado |
| `GET` | `/api/notificacoes` | Lista notificações do usuário | Autenticado |
| `POST` | `/api/files/upload` | Upload seguro de imagens com validação | Autenticado |
| `GET` | `/api/files/{filename}` | Visualização de imagens enviadas | Público |

---

## 📁 Estrutura de Diretórios

```
Trabo/
├── src/
│   ├── main/
│   │   ├── java/com/example/Trabo/
│   │   │   ├── config/              # SecurityConfig, OpenApiConfig, DataInitializer
│   │   │   ├── controller/          # REST Controllers com OpenAPI anotado
│   │   │   ├── dto/
│   │   │   │   ├── request/         # Records e classes de request com @NotBlank/@Valid
│   │   │   │   └── response/        # DTOs de resposta tipados e ErrorResponse
│   │   │   ├── exception/           # GlobalExceptionHandler, BadRequestException
│   │   │   ├── model/
│   │   │   │   ├── entity/          # Entidades JPA (Usuario, Prestador, Avaliacao, etc.)
│   │   │   │   └── enums/           # Role, TipoNotificacao
│   │   │   ├── repository/          # Spring Data JPA Repositories
│   │   │   ├── security/            # JwtTokenProvider, RateLimitFilter, SecurityErrorHandler
│   │   │   └── service/             # Regras de negócio e UsuarioAutenticadoService
│   │   └── resources/
│   │       ├── static/              # Páginas HTML (index, painel, login, cadastro, termos)
│   │       │   ├── css/             # painel.css, styles globais
│   │       │   ├── js/              # painel.js, app.js
│   │       │   └── assets/          # Ícones e imagens
│   │       ├── application.properties         # Configurações globais
│   │       ├── application-dev.properties     # Configuração de desenvolvimento (H2)
│   │       ├── application-staging.properties # Homologação (Neon PostgreSQL)
│   │       └── application-prod.properties    # Produção (Neon PostgreSQL)
│   └── test/
│       ├── java/com/example/Trabo/
│       │   ├── controller/          # Testes de integração (MockMvc)
│       │   └── service/             # Testes unitários (Mockito)
│       └── resources/
│           └── application.properties # H2 isolado para testes
├── documentos/
│   └── ROADMAP.md                   # Roadmap detalhado de evolução
├── docker-compose.yml               # Orquestração do container de produção
├── Dockerfile                       # Build da imagem Java 24 JRE
├── pom.xml                          # Dependências Maven
└── README.md
```

---

## 📈 Status do Roadmap

O projeto segue um plano de evolução estruturado em [documentos/ROADMAP.md](documentos/ROADMAP.md):

- ✅ **Segurança (100% Concluído)**: Variáveis de ambiente, Rate Limiting, sanitização de inputs, CORS restrito, BCrypt e token blacklist no logout.
- ✅ **LGPD & Proteção de Dados (100% Concluído)**: Termos de Uso, Política de Privacidade, checkbox de consentimento, exclusão de conta e auditoria assíncrona.
- ✅ **Organização & Arquitetura (100% Concluído)**: Ambientes `dev`/`staging`/`prod`, modularização do `painel.html`, controllers desacoplados em services, tratamento global de exceções, suíte de 34 testes e Swagger completo.
- ⏳ **Próximo Marco — Velocidade & Performance**: Otimização e compressão de imagens no upload, lazy loading nas galerias, paginação com `Pageable` e índices nos campos de maior busca.

---

## 📄 Licença

Distribuído sob a licença **MIT**. Consulte o arquivo [LICENSE](LICENSE) para mais detalhes.

---

## 👨‍💻 Autoria e Desenvolvimento

Desenvolvido por **Gabriel Cardoso da Silva** — Projeto **Trabio**.
Contribuições, sugestões e issues são sempre bem-vindas!
