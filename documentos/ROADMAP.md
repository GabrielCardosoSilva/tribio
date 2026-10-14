# 🚀 ROADMAP — Trabio
> Lista de melhorias e pendências para tornar a plataforma mais profissional, segura e escalável.

---

## 🔴 CRÍTICO — Fazer Antes de Lançar

### 🔒 Segurança

- [x] **Mover credenciais do `application.properties` para variáveis de ambiente** — A senha do banco, o JWT secret e outras configs sensíveis **nunca devem ficar hardcoded no código** (e muito menos no Git). Usar `.env` + Spring `@Value` ou um serviço como AWS Secrets Manager / Vault.
- [x] **Adicionar `application.properties` ao `.gitignore`** — Atualmente as credenciais do banco Neon e o JWT secret estão expostos no repositório público do GitHub.
- [x] **Implementar Rate Limiting** — Limitar requisições por IP nos endpoints de login e cadastro para evitar ataques de força bruta (ex: `bucket4j` ou Spring Actuator + filtro customizado).
- [x] **Validação e sanitização de inputs do backend** — Prevenir SQL Injection e XSS. Reforçar `@Valid` e `@NotBlank` em todos os DTOs.
- [x] **HTTPS obrigatório** — Em produção, nunca servir em HTTP puro. Configurar certificado SSL (Let's Encrypt) no servidor/proxy.
- [x] **CORS restrito** — A configuração de CORS deve liberar apenas as origens conhecidas (domínio do site), não `*` em produção.
- [x] **Logout invalidar token JWT** — Atualmente o logout só remove o token do `localStorage`. Implementar uma blacklist de tokens ou usar tokens de curta duração com refresh token.

### 🛡️ Proteção de Dados (LGPD)

- [x] **Página de Política de Privacidade** — Criada em `/privacidade.html` com todas as seções exigidas pela LGPD.
- [x] **Página de Termos de Uso** — Criada em `/termos.html`.
- [x] **Checkbox de aceite nos formulários** — Adicionado em `cadastro.html` com links para Termos e Privacidade.
- [x] **Endpoint para exclusão de conta** — `DELETE /api/usuarios/me` implementado (LGPD Art. 18).
- [x] **Hash de senhas com bcrypt** — Confirmado: `BCryptPasswordEncoder` ativo via Spring Security.
- [x] **Auditoria de acesso a dados sensíveis** — `AuditoriaService` + `AuditoriaLog` criados com logs assíncronos.

---

## 🟠 ALTA PRIORIDADE — Essencial para Credibilidade

### 🏗️ Organização & Arquitetura

- [x] **Separar ambientes: `dev`, `staging`, `prod`** — Configurados `application-dev.properties` (H2 em memória com compatibilidade Postgres), `application-staging.properties` e `application-prod.properties` (Neon PostgreSQL com pool Hikari ajustado para conexões serverless), além de `DataInitializer` ativo apenas no profile dev.
- [x] **Refatorar o `painel.html`** — Código desacoplado e modularizado: CSS extraído para `static/css/painel.css`, lógica JS extraída para `static/js/painel.js`, e `painel.html` reduzido de 1.320+ linhas para 380 linhas limpas.
- [x] **Mover lógica de negócio do Controller para o Service** — Criados `UsuarioService`, `UsuarioAutenticadoService` e `NotificacaoService`. Controllers refatorados para atuar exclusivamente como adaptadores de entrada HTTP.
- [x] **Padronizar tratamento de erros** — Implementado `GlobalExceptionHandler` e `SecurityErrorHandler` retornando DTO `ErrorResponse` padronizado (`error`, `status`, `path`, `timestamp`) cobrindo 400, 401, 403, 404, 409, 413, 422 e 500.
- [x] **Adicionar testes unitários e de integração** — Suíte completa implementada com JUnit 5 + MockMvc + Mockito (34 testes passando, cobrindo `AuthService`, `UsuarioService`, `PrestadorService`, `CategoriaService`, `AvaliacaoService`, `NotificacaoService`, fluxos de API e tratamento global de erros).
- [x] **Documentar a API com Swagger completo** — Configurado `OpenApiConfig` com especificação JWT `bearerAuth`, metadados OpenAPI 3.0 e anotações `@Operation`, `@ApiResponse`, `@Schema` e `@SecurityRequirement` em todos os Controllers e DTOs.

### ⚡ Velocidade & Performance

- [ ] **Otimizar imagens no upload** — Comprimir imagens com `Thumbnailator` ou `ImageIO` antes de salvar. Imagens grandes travam a galeria.
- [ ] **Lazy loading de imagens no frontend** — Adicionar `loading="lazy"` nas tags `<img>` das galerias para não carregar tudo de uma vez.
- [ ] **Paginação nas APIs** — Os endpoints de `/api/prestadores` e `/api/notificacoes` retornam **todos** os registros de uma vez. Implementar `Pageable` do Spring para paginar.
- [ ] **Índices no banco de dados** — Adicionar índices nas colunas mais consultadas: `cidade`, `estado`, `aprovado`, `email`.
- [ ] **Cache de respostas** — Usar `@Cacheable` do Spring (com `Caffeine` ou `Redis`) para cachear listagem de categorias e prestadores aprovados.
- [ ] **Servir arquivos estáticos via CDN** — Imagens, CSS e JS via Cloudflare ou AWS CloudFront para reduzir latência.

---

## 🟡 MÉDIA PRIORIDADE — Aumenta Profissionalismo

### 🔍 SEO & Visibilidade

- [ ] **Meta tags dinâmicas por prestador** — A página `profissional.html` deve ter título e description únicos por perfil para ranquear melhor no Google.
- [ ] **Sitemap.xml e robots.txt** — Ajuda os buscadores a indexar o site corretamente.
- [ ] **Open Graph (OG tags)** — Ao compartilhar um link do Trabio no WhatsApp/Facebook, exibir preview com imagem e descrição.
- [ ] **URL amigável para perfis** — Trocar `/profissional.html?id=12` por `/perfil/gabriel-silva` para SEO e UX.

### 💼 Funcionalidades que Aumentam Conversão

- [ ] **Sistema de agendamento** — Permitir que clientes marquem horários com prestadores diretamente pela plataforma.
- [ ] **Chat em tempo real** — A aba "Mensagens" está vazia. Implementar com WebSocket (STOMP) ou integração com WhatsApp Business API.
- [ ] **Sistema de pagamento** — Integrar Stripe ou Mercado Pago para cobrar comissão ou planos premium.
- [ ] **Verificação de identidade do prestador** — Upload de documento (CPF/CNPJ) para aumentar a confiança do cliente.
- [ ] **Notificações por e-mail** — Enviar e-mail quando o prestador receber uma avaliação, visita ou mensagem. Usar JavaMailSender + template HTML.
- [ ] **Recuperação de senha** — Funcionalidade de "Esqueci minha senha" via e-mail está faltando.
- [ ] **Confirmação de e-mail no cadastro** — Validar que o e-mail do usuário é real antes de ativar a conta.

### 📱 Mobile & UX

- [ ] **Layout responsivo para mobile** — O painel/dashboard não está adaptado para celular. Sidebar deve virar menu hambúrguer.
- [ ] **Progressive Web App (PWA)** — Adicionar `manifest.json` e Service Worker para o site funcionar offline e poder ser "instalado" no celular.
- [ ] **Estados de loading nos botões** — Ao clicar em "Salvar", o botão deve mostrar um spinner para evitar cliques duplos.
- [ ] **Confirmação de ações destrutivas** — Ao deletar conta ou remover foto, mostrar modal de confirmação.

---

## 🟢 LONGO PRAZO — Escalabilidade

- [ ] **CI/CD automatizado** — Configurar GitHub Actions para fazer build, testar e fazer deploy automático a cada push na `main`.
- [ ] **Deploy em nuvem** — Hospedar em Railway, Render, Fly.io ou AWS EC2 com domínio personalizado (ex: `trabio.com.br`).
- [ ] **Banco de dados com backup automático** — O Neon já tem isso, mas configurar alertas de uso e retenção de backup.
- [ ] **Monitoramento e logs** — Integrar com Sentry (erros frontend) e Grafana/Loki ou Datadog (logs backend).
- [ ] **Filas de processamento** — Operações pesadas (envio de e-mail, processamento de imagem) devem ir para uma fila assíncrona (RabbitMQ ou AWS SQS).
- [ ] **Arquitetura de microsserviços (futuro)** — Separar em serviços: auth, perfil, upload, notificações — quando o volume crescer.

---

## 📊 Resumo de Prioridades e Progresso

| Área | Concluídas | Pendências | Status | Prioridade |
|---|:---:|:---:|:---:|:---:|
| 🔒 Segurança | 7/7 | 0 | ✅ 100% Concluído | 🔴 Crítico |
| 🛡️ LGPD / Privacidade | 6/6 | 0 | ✅ 100% Concluído | 🔴 Crítico |
| 🏗️ Organização & Arquitetura | 6/6 | 0 | ✅ 100% Concluído | 🟠 Alta |
| ⚡ Velocidade & Performance | 0/6 | 6 | ⏳ Próximo Foco | 🟠 Alta |
| 🔍 SEO & Visibilidade | 0/4 | 4 | ⏳ Pendente | 🟡 Média |
| 💼 Funcionalidades de Conversão | 0/7 | 7 | ⏳ Pendente | 🟡 Média |
| 📱 Mobile & UX | 0/4 | 4 | ⏳ Pendente | 🟡 Média |
| 🚀 Escalabilidade | 0/6 | 6 | ⏳ Pendente | 🟢 Longo prazo |

**Progresso Total do Roadmap:** 19 de 46 itens concluídos (~41% global, 100% do bloco Crítico e Arquitetura).

> **💡 Dica:** Todas as etapas críticas de segurança, LGPD e arquitetura/testes já estão 100% resolvidas. O próximo passo recomendado é o bloco **⚡ Velocidade & Performance** (compressão de imagens, lazy loading, paginação com `Pageable` e índices no banco de dados).


