# Trabio 🔧

> Plataforma que conecta pessoas a profissionais autônomos de confiança em todo o Brasil.

[![Java](https://img.shields.io/badge/Java-24-orange?logo=openjdk)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.4.5-brightgreen?logo=springboot)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Neon-blue?logo=postgresql)](https://neon.tech/)
[![License](https://img.shields.io/badge/license-MIT-blue)](LICENSE)

---

## 📋 Sobre o Projeto

O **Trabio** é uma plataforma web que facilita a conexão entre clientes e prestadores de serviços autônomos — eletricistas, pedreiros, encanadores, pintores, e muito mais. O foco é simplicidade, confiança e acesso direto, sem intermediários ou taxas escondidas.

### ✨ Funcionalidades Principais

- 🔍 **Busca de profissionais** por cidade, estado e categoria
- 👤 **Cadastro dual** — perfil de Cliente ou Prestador de Serviços
- ⭐ **Sistema de avaliações** — avaliações reais de clientes verificados
- 📸 **Portfólio de trabalhos** — galeria de fotos dos serviços do prestador
- 💬 **Contato direto via WhatsApp** — sem intermediários
- 🔒 **Autenticação JWT** — sessões seguras e stateless
- 🛡️ **Conformidade com LGPD** — política de privacidade e exclusão de conta
- 🚦 **Rate Limiting** — proteção contra ataques de força bruta

---

## 🛠️ Stack Tecnológica

### Backend
| Tecnologia | Versão | Função |
|---|---|---|
| Java | 24 | Linguagem principal |
| Spring Boot | 3.4.5 | Framework web |
| Spring Security | 6 | Autenticação e autorização |
| Spring Data JPA | — | Persistência de dados |
| Hibernate | 6.6 | ORM |
| JJWT | 0.12.6 | Geração e validação de tokens JWT |
| Bucket4j | 8.10.1 | Rate limiting por IP |
| SpringDoc OpenAPI | 2.6.0 | Documentação da API (Swagger) |
| PostgreSQL | — | Banco de dados (produção via Neon) |
| H2 | — | Banco de dados em memória (desenvolvimento) |

### Frontend
| Tecnologia | Função |
|---|---|
| HTML5 / CSS3 | Estrutura e estilização |
| JavaScript (Vanilla) | Interatividade e chamadas à API |
| Google Fonts (Outfit) | Tipografia |

### Infraestrutura
| Tecnologia | Função |
|---|---|
| Docker / Docker Compose | Containerização |
| Neon PostgreSQL | Banco de dados serverless na nuvem |
| Maven Wrapper | Build e gerenciamento de dependências |

---

## 🚀 Como Rodar Localmente

### Pré-requisitos

- [Java 24+](https://openjdk.org/)
- [Maven](https://maven.apache.org/) (ou use o `mvnw` incluso)
- Um banco PostgreSQL **ou** acesse gratuitamente via [Neon](https://neon.tech/)

### 1. Clone o repositório

```bash
git clone https://github.com/GabrielCardosoSilva/tribio.git
cd tribio
```

### 2. Configure as variáveis de ambiente

Crie um arquivo `.env` na pasta `Trabo/` com base no exemplo:

```bash
cp Trabo/.env.example Trabo/.env
```

Preencha as variáveis:

```env
# Banco de dados PostgreSQL
DB_URL=jdbc:postgresql://<host>/<database>?sslmode=require
DB_USER=seu_usuario
DB_PASSWORD=sua_senha

# JWT
JWT_SECRET=seu_secret_base64_de_256_bits
JWT_EXPIRATION_MS=86400000
```

### 3. Execute a aplicação

```bash
cd Trabo
./mvnw spring-boot:run
```

A aplicação estará disponível em: **http://localhost:8080**

### 4. (Opcional) Rodar com Docker

```bash
# Na raiz do projeto
docker compose up --build
```

---

## 📁 Estrutura do Projeto

```
Trabo/
├── src/
│   └── main/
│       ├── java/com/example/Trabo/
│       │   ├── config/          # Segurança, CORS, inicialização de dados
│       │   ├── controller/      # Endpoints REST
│       │   ├── dto/             # Data Transfer Objects
│       │   ├── model/entity/    # Entidades JPA
│       │   ├── repository/      # Spring Data Repositories
│       │   ├── security/        # JWT, Rate Limit, UserDetails
│       │   └── service/         # Lógica de negócio
│       └── resources/
│           ├── static/          # Frontend (HTML, CSS, JS, imagens)
│           └── application.properties
├── Dockerfile
├── .env.example
└── pom.xml
```

---

## 🔌 Endpoints da API

A documentação completa está disponível via Swagger em:
**http://localhost:8080/swagger-ui.html**

### Principais rotas

| Método | Endpoint | Descrição | Auth |
|---|---|---|---|
| `POST` | `/api/auth/registro/usuario` | Cadastro de cliente | ❌ |
| `POST` | `/api/auth/registro/prestador` | Cadastro de prestador | ❌ |
| `POST` | `/api/auth/login` | Login (retorna JWT) | ❌ |
| `POST` | `/api/auth/logout` | Logout (invalida token) | ✅ |
| `GET` | `/api/prestadores` | Lista prestadores | ❌ |
| `GET` | `/api/prestadores/{id}` | Perfil de um prestador | ❌ |
| `POST` | `/api/prestadores/{id}/avaliacoes` | Avaliar prestador | ✅ Cliente |
| `GET` | `/api/categorias` | Lista categorias | ❌ |
| `DELETE` | `/api/usuarios/me` | Excluir própria conta (LGPD) | ✅ |

---

## 🔒 Segurança

- **Autenticação** via JWT (stateless) com blacklist de tokens no logout
- **Rate Limiting** com Bucket4j — máximo de requisições por IP nos endpoints de login/cadastro
- **Senhas** armazenadas com hash BCrypt
- **CORS** configurado para origens específicas
- **Variáveis de ambiente** para todas as credenciais sensíveis — nada hardcoded no código
- **Validação de inputs** com Bean Validation (`@Valid`, `@NotBlank`) em todos os DTOs
- **LGPD** — Política de Privacidade, Termos de Uso e endpoint de exclusão de conta (`DELETE /api/usuarios/me`)

---

## 🤝 Contribuindo

1. Fork o repositório
2. Crie uma branch para sua feature: `git checkout -b feat/minha-feature`
3. Commit suas mudanças: `git commit -m "feat: adiciona minha feature"`
4. Push para a branch: `git push origin feat/minha-feature`
5. Abra um Pull Request

---

## 📄 Licença

Este projeto está licenciado sob a licença MIT. Veja o arquivo [LICENSE](LICENSE) para mais detalhes.

---

## 👨‍💻 Autores

Desenvolvido pela **Trinity Soluções em TI**.
