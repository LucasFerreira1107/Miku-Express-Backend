# Miku Express - Sistema de Gestão de Entregas

## 📋 Sobre o Projeto

O **Miku Express** é um sistema backend desenvolvido em Spring Boot para gestão de entregas e rastreamento de pacotes. O sistema permite criar pedidos, calcular fretes automaticamente baseado em distância e peso, gerar códigos de rastreio únicos, atualizar status de entregas e enviar notificações por e-mail aos clientes.

### Principais Funcionalidades

- ✅ Criação de pedidos com cálculo automático de frete
- ✅ Rastreamento de pacotes por código único
- ✅ Atualização de status de entregas
- ✅ Integração com ViaCEP para validação de endereços
- ✅ Integração com Google Maps para cálculo de distâncias
- ✅ Sistema de autenticação JWT com roles (Admin e Cliente)
- ✅ Notificações por e-mail para clientes
- ✅ Gestão de usuários (Administradores e Clientes Públicos)

## 🛠️ Tecnologias e Integrações

### Framework e Dependências Principais

- **Spring Boot 3.5.5** - Framework Java para desenvolvimento de aplicações
- **Spring Security** - Sistema de autenticação e autorização
- **Spring Data JPA** - Abstração para acesso a dados
- **PostgreSQL** - Banco de dados relacional
- **Java 21** - Linguagem de programação

### Bibliotecas e Ferramentas

- **MapStruct 1.6.0** - Mapeamento entre objetos (DTOs e Entidades)
- **Lombok** - Redução de boilerplate code
- **JWT (jjwt 0.12.5)** - Tokens para autenticação
- **Google Maps Services 2.2.0** - API para cálculo de distâncias
- **Spring Mail** - Envio de e-mails
- **RestTemplate** - Cliente HTTP para integrações externas
- **Hypersistence Utils** - Utilitários para Hibernate

### Integrações Externas

#### 1. **ViaCEP API**
- **Uso**: Consulta de endereços completos através de CEP
- **Endpoint**: `https://viacep.com.br/ws/{cep}/json/`
- **Responsabilidade**: Validação e complementação de endereços de origem e destino

#### 2. **Google Maps Distance Matrix API**
- **Uso**: Cálculo de distância rodoviária entre dois endereços
- **Modo**: DRIVING (veículo)
- **Responsabilidade**: Cálculo preciso da distância para precificação do frete

#### 3. **Gmail SMTP**
- **Uso**: Envio de notificações por e-mail
- **Responsabilidade**: Notificar clientes sobre criação de pedidos e atualizações de status

## 📁 Estrutura do Projeto

### Controllers

- **`AdminController`** - Endpoints para administradores (gestão de pedidos, atualização de status)
- **`AuthController`** - Endpoints de autenticação (login, registro, recuperação de senha)
- **`ClientController`** - Endpoints para clientes autenticados (consultar seus pedidos)
- **`PublicController`** - Endpoints públicos (rastreamento de pedidos por código)

### Services

- **`OrderService`** - Lógica de negócio para pedidos (criação, consulta, atualização, exclusão)
- **`UserAdminService`** - Gestão de usuários administradores
- **`UserPublicService`** - Gestão de usuários clientes
- **`SecurityService`** - Serviços de segurança e autenticação
- **`GoogleMapsService`** - Integração com Google Maps API para cálculo de distâncias
- **`ViaCepService`** - Integração com ViaCEP para consulta de endereços
- **`EmailService`** - Envio de e-mails de notificação

### Entities

- **`Order`** - Entidade principal representando um pedido/pacote
- **`StatusUpdate`** - Histórico de atualizações de status de um pedido
- **`UserGeneric`** - Classe base para usuários (herança)
- **`UserAdmin`** - Usuário administrador (herda de UserGeneric)
- **`UserPublic`** - Usuário cliente público (herda de UserGeneric)

### Repositories

- **`OrderRepository`** - Acesso a dados de pedidos
- **`StatusUpdateRepository`** - Acesso a dados de atualizações de status
- **`UserAdminRepository`** - Acesso a dados de administradores
- **`UserPublicRepository`** - Acesso a dados de clientes
- **`UserGenericRepository`** - Acesso genérico a usuários

### DTOs (Data Transfer Objects)

- **`CreatePackageDTO`** - Dados para criação de pedido
- **`CreatedPackageDTO`** - Resposta após criação de pedido
- **`StatusUpdateDTO`** - Dados para atualização de status
- **`StatusUpdatedDTO`** - Resposta após atualização de status
- **`ViaCepResponseDTO`** - Resposta da API ViaCEP
- **`LoginRequestDTO`** / **`LoginResponseDTO`** - Dados de autenticação
- **`CreateUserAdminDTO`** / **`UpdateUserAdminDTO`** - Dados de administradores
- **`CreateUserPublicDTO`** / **`UpdateUserPublicDTO`** - Dados de clientes
- **`ResetPassword`** - Dados para recuperação de senha

### Mappers

- **`OrderMapper`** - Conversão entre Order e DTOs relacionados
- **`StatusUpdateMapper`** - Conversão entre StatusUpdate e DTOs
- **`UserAdminMapper`** - Conversão entre UserAdmin e DTOs
- **`UserPublicMapper`** - Conversão entre UserPublic e DTOs

### Security

- **`SecurityConfiguration`** - Configuração de segurança e filtros
- **`JwtTokenProvider`** - Geração e validação de tokens JWT
- **`JwtCustomAuthenticationFilter`** - Filtro para autenticação JWT
- **`CustomAuthentication`** - Implementação customizada de autenticação

### Config

- **`SecurityConfiguration`** - Configuração de segurança Spring Security
- **`GoogleMapsConfig`** - Configuração do cliente Google Maps API

### Enums

- **`Role`** - Papéis de usuário (ADMIN, PUBLIC)
- **`Gender`** - Gênero para usuários públicos

## 🚀 Como Iniciar o Projeto

### Pré-requisitos

- Java 21 ou superior
- Maven 3.6+ ou superior
- PostgreSQL 12+ ou superior
- Conta no Google Cloud Platform (para API Key do Google Maps)
- Conta Gmail (para envio de e-mails)

### Configuração do Banco de Dados

1. Crie um banco de dados PostgreSQL:
```sql
CREATE DATABASE mikuexpress;
```

2. O Spring Boot irá criar as tabelas automaticamente (usando `ddl-auto=update`)

### Variáveis de Ambiente

Crie um arquivo `.env` ou configure as seguintes variáveis de ambiente:

```bash
# Banco de Dados
DB_URL=jdbc:postgresql://localhost:5432/mikuexpress
DB_USERNAME=seu_usuario
DB_PASSWORD=sua_senha

# JWT
JWT_SECRET_KEY=sua_chave_secreta_jwt_minimo_256_bits

# Google Maps API
GOOGLE_MAPS_API_KEY=sua_api_key_do_google_maps

# E-mail (Gmail)
USERNAME_EMAIL=seu_email@gmail.com
PASSWORD_EMAIL=sua_senha_de_app_do_gmail
```

**Nota sobre senha do Gmail**: Para usar Gmail, você precisará criar uma "Senha de App" nas configurações de segurança da sua conta Google, não use sua senha normal.

### Passos para Executar

1. **Clone o repositório** (se ainda não tiver):
```bash
git clone <url-do-repositorio>
cd mikuexpress
```

2. **Configure as variáveis de ambiente** conforme descrito acima

3. **Compile o projeto**:
```bash
./mvnw clean install
```

4. **Execute a aplicação**:
```bash
./mvnw spring-boot:run
```

Ou usando o Maven wrapper:
```bash
./mvnw.cmd spring-boot:run  # Windows
```

5. **Acesse a aplicação**:
   - A aplicação estará rodando em: `http://localhost:8080`
   - Endpoints disponíveis em: `http://localhost:8080/api/`

### Executando com JAR

Após compilar, você pode executar o JAR gerado:

```bash
./mvnw clean package
java -jar target/mikuexpress-0.0.1-SNAPSHOT.jar
```

## 📝 Endpoints Principais

### Públicos (sem autenticação)
- `GET /api/publics/orders/tracking?code={codigo}` - Rastrear pedido por código

### Autenticação
- `POST /api/auth/login` - Login
- `POST /api/auth/signup` - Registro de cliente
- `POST /api/auth/reset-password` - Recuperação de senha

### Cliente (autenticado)
- `GET /api/clients/orders` - Listar pedidos do cliente
- `GET /api/clients/orders/{id}` - Detalhes de um pedido

### Administrador (autenticado)
- `POST /api/admins/orders` - Criar novo pedido
- `GET /api/admins/orders` - Listar todos os pedidos
- `GET /api/admins/orders/{id}` - Detalhes de um pedido
- `PUT /api/admins/orders/{id}/status` - Atualizar status de um pedido
- `DELETE /api/admins/orders/{id}` - Excluir um pedido

## 🔐 Autenticação

O sistema utiliza JWT (JSON Web Tokens) para autenticação. Após fazer login, você receberá um token que deve ser incluído no header `Authorization` das requisições:

```
Authorization: Bearer {seu_token_jwt}
```

O token tem validade de 1 hora (3600000ms) conforme configurado.

## 📊 Cálculo de Frete

O cálculo de frete é realizado automaticamente ao criar um pedido:

```
Preço = (Distância em km × R$ 0,50) + (Peso em kg × R$ 10,50)
```

O sistema:
1. Consulta os endereços completos no ViaCEP usando os CEPs informados
2. Calcula a distância rodoviária via Google Maps API
3. Aplica a fórmula de precificação
4. Gera um código de rastreio único (formato: `MIKUXXXXXXXXBR`)

## 📧 Notificações

O sistema envia e-mails automaticamente:
- Quando um novo pedido é criado
- Quando há atualização de status do pedido

Os e-mails são enviados usando a configuração SMTP do Gmail configurada nas variáveis de ambiente.

## 🔧 Desenvolvimento

### Estrutura de Herança de Usuários

O projeto utiliza herança de entidades JPA:
- `UserGeneric` (classe base) - contém campos comuns (email, senha, nome, etc.)
- `UserAdmin` (herda de UserGeneric) - adiciona CNPJ e nome da organização
- `UserPublic` (herda de UserGeneric) - adiciona CPF, data de nascimento e gênero

### MapStruct

O projeto utiliza MapStruct para mapeamento automático entre entidades e DTOs, reduzindo código boilerplate e melhorando a manutenibilidade.

### Logging

O projeto utiliza SLF4J com Lombok para logging. Logs importantes são registrados durante operações críticas como criação de pedidos, cálculos de distância e envio de e-mails.


---

**Desenvolvido com Spring Boot** 🚀
