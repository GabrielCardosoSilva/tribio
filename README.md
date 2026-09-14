<div align="center">
  <img src="https://via.placeholder.com/150x150/1B4332/FFFFFF?text=T" alt="Trabio Logo" width="100">
  <h1>Trabio</h1>
  <p><strong>A plataforma ideal para conectar clientes e prestadores de serviços de forma rápida e segura.</strong></p>
</div>

---

## 📌 Sobre o Projeto

**Trabio** é um sistema web desenvolvido para revolucionar a forma como profissionais autônomos divulgam seus serviços e como clientes encontram a ajuda ideal para suas necessidades. 
O foco da plataforma é entregar uma interface rica, responsiva e com uma experiência de usuário (UX) premium baseada em uma paleta de cores elegantes (tons de verde) e usabilidade intuitiva.

## 🚀 Funcionalidades

### 🔹 Para Clientes
- **Busca Detalhada:** Encontre prestadores por cidade e categoria (ex: Eletricista, Encanador, Pintor).
- **Perfis Premium:** Visualize perfis detalhados de cada profissional com nome, localização, avaliações (estrelas) e portfólio de serviços.
- **Contato Rápido:** Entre em contato com o profissional diretamente pelo WhatsApp via modal interativo.

### 🔹 Para Prestadores (Autônomos)
- **Painel de Controle:** Dashboard exclusivo para gerenciar seu perfil.
- **Gestão de Serviços:** Cadastre seus serviços informando título, descrição, valor base.
- **Upload de Mídia:** Personalize o seu perfil com uma **Foto de Perfil** e gerencie as fotos do seu portfólio de serviços para impressionar os clientes.
- **Visibilidade:** Assim que o perfil é aprovado e os serviços cadastrados, o prestador se torna visível na busca principal.

## 🛠️ Tecnologias Utilizadas

### Backend
- **Java 24** + **Spring Boot (3.4.5)**
- **Spring Security** para autenticação baseada em JWT
- **Spring Data JPA** / **Hibernate** para mapeamento relacional
- **H2 Database** (in-memory) para desenvolvimento

### Frontend
- **HTML5 & CSS3** com design "Glassmorphism" e componentes com cantos arredondados
- **Vanilla JavaScript** (ES6+) para iterações do cliente, controle do Painel e consumo das APIs RESTful
- **Google Fonts (Outfit)** para tipografia moderna e legível

## 💻 Como Rodar o Projeto

### Pré-requisitos
- Java 17+ instalado
- Maven instalado (opcional, o projeto possui o `mvnw`)

### Passos para Execução
1. Clone o repositório:
```bash
git clone https://github.com/GabrielCardosoSilva/tribio.git
```
2. Entre na pasta do backend:
```bash
cd tribio/Trabo
```
3. Execute a aplicação usando o Maven Wrapper:
```bash
./mvnw spring-boot:run
```
*(No Windows use `.\mvnw.cmd spring-boot:run`)*

4. Acesso a Aplicação:
Abra o seu navegador e acesse: [http://localhost:8080](http://localhost:8080)

## 📄 Licença
Este projeto está sob a licença MIT. Sinta-se livre para usá-lo e modificá-lo.

---
<div align="center">
  Feito com ❤️ para facilitar conexões e serviços!
</div>
