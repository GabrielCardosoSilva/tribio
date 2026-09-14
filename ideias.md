Quero começar o desenvolvimento do projeto "Trabio" — uma plataforma web que conecta
pessoas a trabalhadores autônomos locais (eletricistas, pedreiros, encanadores,
pintores, diaristas, jardineiros etc.) por cidade e categoria de serviço.

CONTEXTO DO PROBLEMA:
Quem chega em uma cidade nova não conhece prestadores de serviço confiáveis e
precisa recorrer a indicações informais ou grupos de WhatsApp desorganizados.
O Trabio resolve isso com um diretório pesquisável de profissionais autônomos
por região.

STACK:
- Backend: Java 17 + Spring Boot 3.x
- Banco de dados: PostgreSQL
- ORM: Spring Data JPA
- Autenticação: Spring Security + JWT (apenas para cadastro/login de prestadores;
  a busca deve ser aberta, sem necessidade de login)
- Documentação da API: Swagger/OpenAPI
- Frontend: React (ou Thymeleaf, se quisermos simplificar o MVP)

ESCOPO DO MVP:
1. Entidade Prestador: nome, categoria(s), cidade/bairro, descrição, telefone/WhatsApp,
   fotos de trabalhos anteriores
2. Entidade Categoria: lista pré-definida (eletricista, pedreiro, encanador, pintor,
   jardineiro, diarista, etc.)
3. Endpoint de cadastro/login de prestador (protegido por JWT)
4. Endpoint de busca pública: por categoria, por cidade, por texto livre (nome/descrição)
5. Endpoint de perfil público do prestador
6. Sistema simples de avaliação (nota + comentário) vinculado ao prestador

ARQUITETURA:
- Organizar em camadas: controller, service, repository, model/entity, dto
- Usar DTOs para requests/responses (não expor entidades diretamente)
- Validação de dados com Bean Validation (@Valid, @NotBlank, etc.)
- Tratamento global de exceções (@ControllerAdvice)

O QUE PRECISO AGORA:
1. Sugira a modelagem de dados (diagrama de entidades e relacionamentos)
2. Estruture o esqueleto do projeto Spring Boot (pastas e pacotes)
3. Gere o código inicial das entidades Prestador, Categoria e Avaliacao com JPA
4. Sugira os endpoints REST necessários para o MVP, com verbos HTTP e paths

Comece pela modelagem de dados antes de gerar código.

REGRAS ESSENCIAIS: 

vamos ter 3 tipos de contas a conta do usuario padrao que vai precisar de um serviço, do prestador que vai oferecer o serviço e do administrador que vai administrar a plataforma

o usuario nao pode se cadastrar como prestador e nem como administrador

o usuario so pode interagir no site se ele estiver logado

o prestador precisa informar todos os dados para poder aparecer na plataforma

vamos ter a tela inicial onde vai ser um resumo e que mostre o site visual e que encante o usuario e a sua ideia, depois do usuario logar ele vai ser pra tela onde acontece as coisas

a pagina do prestador vai ser publica para qualquer um ver, mas so logado pra interagir

vamos usar a API do whatzap para falar com o prestador

o adm vai poder fazer tudo e vai ter um painel para cadastrar prestadores, categorias, etc...

o adm vai ter paineis onde ve os prestadores cadastrados e vai poder aprovar ou reprovar o cadastro, ele tbm vai poder ver os usuarios cadastrados e vai poder aprovar ou reprovar o cadastro

o adm vai ter um painel que vai ver a movimentaçao do site vendo seus log e tudo

