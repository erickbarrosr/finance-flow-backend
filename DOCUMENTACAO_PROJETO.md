# Finance Flow - Documentação Completa do Backend

## 📋 Índice
1. [Visão Geral do Projeto](#visão-geral-do-projeto)
2. [Estrutura de Pastas](#estrutura-de-pastas)
3. [Arquitetura da Aplicação](#arquitetura-da-aplicação)
4. [Fluxo de Requisições](#fluxo-de-requisições)
5. [Componentes Principais](#componentes-principais)
6. [Configurações](#configurações)
7. [Tecnologias e Dependências](#tecnologias-e-dependências)
8. [Como Tudo se Conecta](#como-tudo-se-conecta)

---

## 🎯 Visão Geral do Projeto

**Finance Flow** é um sistema de gerenciamento financeiro construído com **Spring Boot 4.0.1** e **Java 21**. O projeto segue a arquitetura **Clean Architecture** combinada com padrões de design como **Gateway Pattern**, **Data Mapper Pattern** e **DTO Pattern**.

O backend fornece APIs RESTful para:
- Autenticação de usuários (Login e Registro)
- Gerenciamento de usuários
- Segurança com JWT (JSON Web Tokens)

**Versão**: 0.0.1-SNAPSHOT  
**Banco de Dados**: PostgreSQL (produção) / H2 (desenvolvimento)  
**Build Tool**: Maven

---

## 📁 Estrutura de Pastas

```
finance-flow/backend/
├── src/
│   ├── main/
│   │   ├── java/br/com/ersoftwares/financeflow/
│   │   │   ├── FinanceFlowApplication.java          [Classe principal da aplicação]
│   │   │   ├── common/                              [Utilitários compartilhados]
│   │   │   │   ├── exception/
│   │   │   │   │   └── BusinessException.java       [Exceção customizada]
│   │   │   │   ├── response/                        [Respostas genéricas - VAZIO]
│   │   │   │   └── util/                            [Classes utilitárias - VAZIO]
│   │   │   ├── config/                              [Configurações da aplicação]
│   │   │   │   ├── interceptor/                     [Interceptadores - VAZIO]
│   │   │   │   ├── security/
│   │   │   │   │   ├── SecurityConfig.java          [Configuração de segurança]
│   │   │   │   │   ├── WebSecurityConfig.java       [Configuração da cadeia de filtros]
│   │   │   │   │   └── jwt/
│   │   │   │   │       └── JwtService.java          [Serviço de token JWT]
│   │   │   │   ├── swagger/                         [Documentação Swagger - VAZIO]
│   │   │   │   └── web/
│   │   │   │       └── WebConfig.java               [Configuração CORS]
│   │   │   ├── user/                                [Módulo de Usuários]
│   │   │   │   ├── controller/
│   │   │   │   │   ├── AuthController.java          [Endpoints de autenticação]
│   │   │   │   │   └── UserController.java          [Endpoints de usuário]
│   │   │   │   ├── service/
│   │   │   │   │   ├── AuthService.java             [Lógica de login]
│   │   │   │   │   └── UserService.java             [Lógica de registro]
│   │   │   │   ├── domain/
│   │   │   │   │   └── User.java                    [Entidade de domínio]
│   │   │   │   ├── dto/
│   │   │   │   │   ├── request/
│   │   │   │   │   │   ├── CreateUserRequestDTO.java    [DTO de registro]
│   │   │   │   │   │   └── LoginRequestDTO.java         [DTO de login]
│   │   │   │   │   └── response/
│   │   │   │   │       ├── UserResponseDTO.java         [DTO de resposta de usuário]
│   │   │   │   │       └── LoginResponseDTO.java        [DTO de resposta de login]
│   │   │   │   ├── gateway/
│   │   │   │   │   └── UserGateway.java             [Interface de acesso a dados]
│   │   │   │   ├── mapper/
│   │   │   │   │   └── UserDTOMapper.java           [Conversor DTO <-> Domain]
│   │   │   │   ├── dataprovider/
│   │   │   │   │   ├── UserDataProvider.java        [Implementação do Gateway]
│   │   │   │   │   ├── entity/
│   │   │   │   │   │   └── UserEntity.java          [Entidade JPA]
│   │   │   │   │   └── mapper/
│   │   │   │   │       └── UserEntityMapper.java    [Conversor Entity <-> Domain]
│   │   │   │   ├── repository/
│   │   │   │   │   └── UserRepository.java          [Interface JPA Repository]
│   │   │   │   └── security/
│   │   │   │       └── CustomUserDetailsService.java [Carregador de usuários]
│   │   │   └── transaction/                         [Módulo de Transações - EM DESENVOLVIMENTO]
│   │   │       ├── controller/
│   │   │       ├── dataprovider/
│   │   │       ├── domain/
│   │   │       ├── dto/
│   │   │       ├── gateway/
│   │   │       ├── mapper/
│   │   │       ├── repository/
│   │   │       └── service/
│   │   └── resources/
│   │       ├── application.yaml               [Configurações gerais]
│   │       ├── application-local.yaml         [Configurações locais (perfil)]
│   │       └── db/migration/
│   │           └── V1__create_users_table.sql [Script de criação da tabela de usuários]
│   └── test/
│       └── java/br/com/ersoftwares/financeflow/
│           ├── FinanceFlowApplicationTests.java     [Teste da aplicação]
│           └── user/
│               └── AuthIntegrationTest.java         [Testes de integração]
├── target/                                    [Diretório compilado (gerado)]
├── pom.xml                                    [Configuração Maven]
├── README.md                                  [Introdução ao projeto]
└── HELP.md                                    [Ajuda geral]
```

---

## 🏗️ Arquitetura da Aplicação

O projeto utiliza uma arquitetura em **camadas** com **Clean Architecture**, dividida em:

### 1️⃣ **Camada de Apresentação (Presentation Layer)**
   - **Localização**: `controller/`
   - **Responsabilidade**: Receber requisições HTTP e retornar respostas
   - **Componentes**:
     - `AuthController`: Endpoint `/api/v1/auth/login`
     - `UserController`: Endpoint `/api/v1/users` (POST)

### 2️⃣ **Camada de Aplicação (Application Layer)**
   - **Localização**: `service/` + `mapper/`
   - **Responsabilidade**: Orquestrar a lógica de negócio
   - **Componentes**:
     - `AuthService`: Implementa lógica de autenticação
     - `UserService`: Implementa lógica de registro
     - `UserDTOMapper`: Converte DTOs para entidades de domínio

### 3️⃣ **Camada de Domínio (Domain Layer)**
   - **Localização**: `domain/`
   - **Responsabilidade**: Representar conceitos de negócio puros
   - **Componentes**:
     - `User`: Classe de domínio com validações de negócio

### 4️⃣ **Camada de Gateway (Gateway Layer)**
   - **Localização**: `gateway/`
   - **Responsabilidade**: Abstrair o acesso a dados
   - **Componentes**:
     - `UserGateway`: Interface que define operações de usuário

### 5️⃣ **Camada de Acesso a Dados (Data Provider Layer)**
   - **Localização**: `dataprovider/`
   - **Responsabilidade**: Implementar o acesso ao banco de dados
   - **Componentes**:
     - `UserDataProvider`: Implementa `UserGateway`
     - `UserEntity`: Entidade JPA
     - `UserEntityMapper`: Converte Entity ↔ Domain
     - `UserRepository`: Interface JPA para queries

### 6️⃣ **Camada de Configuração (Config Layer)**
   - **Localização**: `config/`
   - **Responsabilidade**: Configurar componentes da aplicação
   - **Sub-camadas**:
     - `security/`: Configuração de autenticação e autorização
     - `web/`: Configuração CORS
     - `swagger/`: Documentação OpenAPI (não implementada)

### 7️⃣ **Camada Comum (Common/Shared Layer)**
   - **Localização**: `common/`
   - **Responsabilidade**: Utilitários e exceções compartilhadas
   - **Componentes**:
     - `BusinessException`: Exceção customizada

---

## 🔄 Fluxo de Requisições

Aqui está o fluxo completo desde a chegada de uma requisição até a resposta ao cliente:

### **Fluxo 1: Registro de Usuário (POST /api/v1/users)**

```
┌─────────────────────────────────────────────────────────────┐
│ 1. CLIENTE                                                  │
│    POST /api/v1/users                                       │
│    {                                                        │
│      "name": "João Silva",                                 │
│      "email": "joao@example.com",                          │
│      "password": "senha123"                                │
│    }                                                        │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────────┐
│ 2. WebSecurityConfig - Filtro de Segurança                 │
│    - Verifica se /api/v1/users está permitida (SIM)        │
│    - Permite requisição passar                             │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────────┐
│ 3. UserController.register()                                │
│    - Recebe CreateUserRequestDTO                           │
│    - Valida dados (Jakarta Validation)                     │
│    - Se inválido: retorna erro 400                         │
│    - Converte DTO para User (domínio)                      │
└────────────────────┬────────────────────────────────────────┘
                     │
         ┌───────────┴──────────────┐
         │ UserDTOMapper.toDomain()  │
         │ Cria User com:            │
         │ - name                    │
         │ - email                   │
         │ - passwordHash = null     │
         │ - createdAt               │
         │ - updatedAt               │
         └───────────┬──────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────────┐
│ 4. UserService.register()                                   │
│    - Verifica se email já existe via UserGateway           │
│    - Se existe: lança BusinessException                    │
│    - Encripta senha com BCryptPasswordEncoder              │
│    - Cria novo User com passwordHash                       │
│    - Chama userGateway.save()                              │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────────┐
│ 5. UserDataProvider (implementa UserGateway)                │
│    - Recebe User (domínio)                                  │
│    - UserEntityMapper.toEntity() → converte para UserEntity │
│    - Chama userRepository.save()                            │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────────┐
│ 6. UserRepository (JPA)                                     │
│    - Executar SQL INSERT na tabela 'users'                 │
│    - Banco gera ID auto-incremento                         │
│    - Retorna UserEntity salva                              │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────────┐
│ 7. UserDataProvider                                         │
│    - Recebe UserEntity do repository                        │
│    - UserEntityMapper.toDomain() → converte para User       │
│    - Retorna User                                           │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────────┐
│ 8. UserController                                           │
│    - UserDTOMapper.toResponse() → converte para DTO         │
│    - Retorna ResponseEntity com status 201 CREATED         │
│    {                                                        │
│      "id": 1,                                              │
│      "name": "João Silva",                                 │
│      "email": "joao@example.com",                          │
│      "createdAt": "2026-01-11T10:30:00"                    │
│    }                                                        │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────────┐
│ 9. CLIENTE RECEBE RESPOSTA 201 CREATED                      │
└─────────────────────────────────────────────────────────────┘
```

### **Fluxo 2: Login do Usuário (POST /api/v1/auth/login)**

```
┌─────────────────────────────────────────────────────────────┐
│ 1. CLIENTE                                                  │
│    POST /api/v1/auth/login                                 │
│    {                                                        │
│      "email": "joao@example.com",                          │
│      "password": "senha123"                                │
│    }                                                        │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────────┐
│ 2. WebSecurityConfig - Filtro de Segurança                 │
│    - Verifica se /api/v1/auth/login está permitida (SIM)   │
│    - Permite requisição passar                             │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────────┐
│ 3. AuthController.login()                                   │
│    - Recebe LoginRequestDTO                                │
│    - Valida dados                                          │
│    - Chama authService.login()                             │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────────┐
│ 4. AuthService.login()                                      │
│    - Busca usuário por email: userGateway.findByEmail()    │
│    - Se não encontrado: lança BusinessException            │
│    - Valida senha:                                         │
│      passwordEncoder.matches(rawPassword, hashArmazenado)  │
│    - Se não coincide: lança BusinessException              │
│    - Chama jwtService.generateToken()                      │
└────────────────────┬────────────────────────────────────────┘
│                    │
│    ┌───────────────┴──────────────┐
│    │ JwtService.generateToken()    │
│    │ - Cria token JWT              │
│    │ - Subject: email              │
│    │ - Claim: userId               │
│    │ - Assinado com HS256          │
│    │ - Expira em 1 hora (padrão)   │
│    └───────────────┬──────────────┘
│                    │
                     ▼
┌─────────────────────────────────────────────────────────────┐
│ 5. AuthController                                           │
│    - Retorna ResponseEntity com status 200 OK              │
│    {                                                        │
│      "token": "eyJhbGc...(token JWT)",                     │
│      "token_type": "Bearer"                                │
│    }                                                        │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────────┐
│ 6. CLIENTE RECEBE RESPOSTA 200 OK COM TOKEN                 │
│    - Armazena token localmente                             │
│    - Próximas requisições: Authorization: Bearer <token>   │
└─────────────────────────────────────────────────────────────┘
```

### **Fluxo 3: Requisição Autenticada**

```
┌─────────────────────────────────────────────────────────────┐
│ 1. CLIENTE                                                  │
│    GET /api/v1/users/profile                               │
│    Header: Authorization: Bearer eyJhbGc...                │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────────┐
│ 2. WebSecurityConfig - Filtro de Segurança                 │
│    - Verifica se token está válido (JwtService.isTokenValid)
│    - Se inválido: retorna 401 Unauthorized                 │
│    - Se expirado: retorna 401 Unauthorized                 │
│    - Se válido: extrai email do token                      │
│    - Carrega usuário: CustomUserDetailsService             │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────────┐
│ 3. CustomUserDetailsService.loadUserByUsername()           │
│    - Busca usuário por email: userGateway.findByEmail()    │
│    - Cria UserDetails do Spring Security                   │
│    - Atribui autoridades (ROLE_USER)                       │
│    - Retorna UserDetails                                   │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────────┐
│ 4. Controller com @PreAuthorize                             │
│    - Verifica permissões do usuário                        │
│    - Se autorizado: executa método                         │
│    - Se não: retorna 403 Forbidden                         │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────────┐
│ 5. CLIENTE RECEBE RESPOSTA                                  │
└─────────────────────────────────────────────────────────────┘
```

---

## 💾 Componentes Principais

### **1. Controller Layer (Apresentação)**

#### **AuthController**
```
📍 Localização: user/controller/AuthController.java
📌 Endpoints:
   POST /api/v1/auth/login
🔑 Parâmetros:
   - email (String): Email do usuário
   - password (String): Senha em texto plano
📤 Retorno:
   - token (String): JWT token
   - token_type (String): "Bearer"
⚙️ Validações:
   - Email válido e obrigatório
   - Senha obrigatória
```

#### **UserController**
```
📍 Localização: user/controller/UserController.java
📌 Endpoints:
   POST /api/v1/users
🔑 Parâmetros:
   - name (String): Nome completo
   - email (String): Email único
   - password (String): Senha em texto plano
📤 Retorno:
   - id (Long): ID gerado
   - name (String): Nome do usuário
   - email (String): Email
   - createdAt (LocalDateTime): Data de criação
⚙️ Validações:
   - Nome obrigatório
   - Email válido e obrigatório
   - Senha obrigatória
   - Email deve ser único
```

---

### **2. Service Layer (Lógica de Negócio)**

#### **UserService**
```
📍 Localização: user/service/UserService.java
🎯 Responsabilidades:
   1. Validar se email já existe (userGateway.existsByEmail)
   2. Encriptar senha com BCrypt
   3. Criar objeto User com dados completos
   4. Salvar no banco via userGateway.save()
   5. Retornar User salvo

🔄 Fluxo:
   register(User, String password)
   ├─ userGateway.existsByEmail() → bool
   ├─ passwordEncoder.encode() → hash
   ├─ User.builder().passwordHash(hash).build()
   ├─ userGateway.save() → User
   └─ return User

📌 Exceções:
   - BusinessException: "Email já cadastrado"
```

#### **AuthService**
```
📍 Localização: user/service/AuthService.java
🎯 Responsabilidades:
   1. Buscar usuário por email
   2. Validar senha com BCrypt
   3. Gerar token JWT
   4. Retornar LoginResponseDTO

🔄 Fluxo:
   login(LoginRequestDTO)
   ├─ userGateway.findByEmail() → Optional<User>
   ├─ passwordEncoder.matches() → bool
   ├─ jwtService.generateToken() → String
   └─ return LoginResponseDTO

📌 Exceções:
   - BusinessException: "E-mail ou senha inválidos"
```

---

### **3. Domain Layer (Lógica de Negócio Pura)**

#### **User (Entidade de Domínio)**
```
📍 Localização: user/domain/User.java
🏗️ Estrutura:
   - id (Long): Identificador único
   - name (String): Nome do usuário [OBRIGATÓRIO]
   - email (String): Email único [OBRIGATÓRIO, validado]
   - passwordHash (String): Hash da senha
   - createdAt (LocalDateTime): Timestamp de criação
   - updatedAt (LocalDateTime): Timestamp de atualização

✅ Validações de Domínio:
   - Email não pode ser null ou vazio
   - Lança IllegalArgumentException se email inválido
   - Imutável (Builder pattern)
   - NonNull: name e email
```

---

### **4. Gateway Layer (Abstração de Acesso a Dados)**

#### **UserGateway**
```
📍 Localização: user/gateway/UserGateway.java
🎯 Interface que define operações:

1. save(User) → User
   - Salva usuário e retorna com ID
   
2. existsByEmail(String) → boolean
   - Verifica se email já está cadastrado
   
3. findByEmail(String) → Optional<User>
   - Busca usuário por email
   - Retorna Optional (pode não encontrar)

💡 Design Pattern: Gateway Pattern
   - Desacopla lógica de negócio do banco de dados
   - Service não conhece detalhes de persistência
   - Fácil trocar implementação (mock, cache, etc)
```

---

### **5. Data Provider Layer (Implementação de Acesso a Dados)**

#### **UserDataProvider (Implementa UserGateway)**
```
📍 Localização: user/dataprovider/UserDataProvider.java
🎯 Implementa interface UserGateway:

1. save(User user) → User
   ├─ UserEntityMapper.toEntity(user) → UserEntity
   ├─ userRepository.save(entity) → UserEntity
   ├─ UserEntityMapper.toDomain(saved) → User
   └─ return User

2. existsByEmail(String email) → boolean
   └─ userRepository.existsByEmail(email)

3. findByEmail(String email) → Optional<User>
   ├─ userRepository.findByEmail(email) → Optional<UserEntity>
   ├─ map(UserEntityMapper::toDomain)
   └─ return Optional<User>

🔗 Conexões:
   - Usa UserRepository (JPA) para query
   - Usa UserEntityMapper para conversão
```

#### **UserEntity (Entidade JPA)**
```
📍 Localização: user/dataprovider/entity/UserEntity.java
🗄️ Configuração JPA:
   @Entity @Table(name = "users")
   
📊 Colunas:
   - id (Long): @Id @GeneratedValue(IDENTITY)
   - name (VARCHAR 150, NOT NULL)
   - email (VARCHAR 255, NOT NULL, UNIQUE)
   - password_hash (VARCHAR 255, NOT NULL)
   - created_at (TIMESTAMP, NOT NULL, updatable=false)
   - updated_at (TIMESTAMP, NOT NULL)

💡 Diferença de User (domínio):
   - User é pura, sem anotações JPA
   - UserEntity é hidratada com @Column, @Table
   - Ambas têm mesmos atributos
```

#### **UserRepository (JPA Repository)**
```
📍 Localização: user/repository/UserRepository.java
🎯 Interface JPA:
   extends JpaRepository<UserEntity, Long>
   
🔍 Métodos customizados:
   1. existsByEmail(String email) → boolean
   2. findByEmail(String email) → Optional<UserEntity>
   
🔎 Funcionamento:
   - Spring Data JPA gera implementação automática
   - Nomes de método criam queries SQL
   - existsByEmail → SELECT COUNT(*) FROM users WHERE email = ?
   - findByEmail → SELECT * FROM users WHERE email = ?
```

---

### **6. Mapper Layer (Conversões)**

#### **UserDTOMapper (DTO ↔ Domain)**
```
📍 Localização: user/mapper/UserDTOMapper.java
🔄 Conversões:

1. toDomain(CreateUserRequestDTO, String passwordHash) → User
   Entrada: CreateUserRequestDTO (name, email, password [não usado aqui])
   Saída: User (domínio)
   ├─ LocalDateTime.now() → createdAt, updatedAt
   └─ User.builder().build()

2. toResponse(User) → UserResponseDTO
   Entrada: User (domínio)
   Saída: UserResponseDTO (resposta HTTP)
   ├─ Copia: id, name, email, createdAt
   └─ Exclui: passwordHash, updatedAt

💡 Uso:
   - Controller recebe DTO
   - Converte para User para passar ao Service
   - Service trabalha com domínio puro
   - Controller converte User de volta para DTO
```

#### **UserEntityMapper (Entity ↔ Domain)**
```
📍 Localização: user/dataprovider/mapper/UserEntityMapper.java
🔄 Conversões:

1. toEntity(User) → UserEntity
   - Cria entidade hidratada com dados do domínio
   - Pronta para salvar no banco

2. toDomain(UserEntity) → User
   - Reconstrói objeto de domínio a partir do banco
   - Remove metadados JPA

💡 Uso no DataProvider:
   repository.save(UserEntityMapper.toEntity(user))
   UserEntityMapper.toDomain(entity)
```

---

### **7. DTO Layer (Transferência de Dados)**

#### **CreateUserRequestDTO**
```
📍 Localização: user/dto/request/CreateUserRequestDTO.java
🔑 Campos:
   - name (String): @NotBlank
   - email (String): @Email, @NotBlank
   - password (String): @NotBlank

✅ Validações (Jakarta Validation):
   - "Nome é obrigatório"
   - "Email deve ser válido"
   - "Email é obrigatório"
   - "Senha é obrigatória"

📤 Uso:
   Recebido no Controller (POST /api/v1/users)
```

#### **LoginRequestDTO**
```
📍 Localização: user/dto/request/LoginRequestDTO.java
🔑 Campos:
   - email (String): @Email, @NotBlank
   - password (String): @NotBlank

✅ Validações:
   - "Email deve ser válido"
   - "Email é obrigatório"
   - "Senha é obrigatória"

🔐 Segurança:
   @ToString(exclude = "password")
   - Evita logar senha em console

📤 Uso:
   Recebido no Controller (POST /api/v1/auth/login)
```

#### **UserResponseDTO**
```
📍 Localização: user/dto/response/UserResponseDTO.java
🔑 Campos:
   - id (Long)
   - name (String)
   - email (String)
   - createdAt (LocalDateTime): @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")

🚫 Não contém:
   - passwordHash (segurança)
   - updatedAt (informação desnecessária)

📤 Uso:
   Retornado em POST /api/v1/users (201 Created)
```

#### **LoginResponseDTO**
```
📍 Localização: user/dto/response/LoginResponseDTO.java
🔑 Campos:
   - token (String): Token JWT
   - type (String): @JsonProperty("token_type"), default = "Bearer"

📤 Uso:
   Retornado em POST /api/v1/auth/login (200 OK)
```

---

### **8. Security & JWT**

#### **SecurityConfig**
```
📍 Localização: config/security/SecurityConfig.java
🔧 Beans criados:

1. PasswordEncoder → BCryptPasswordEncoder
   - Encripta senhas com BCrypt
   - Usado em UserService e AuthService

2. DaoAuthenticationProvider
   - Autentica usando UserDetailsService
   - Valida senha com PasswordEncoder

3. AuthenticationManager
   - Gerencia autenticação

💡 Fluxo:
   Senha → BCryptPasswordEncoder.encode() → hash
   Input → BCryptPasswordEncoder.matches(input, hash) → boolean
```

#### **WebSecurityConfig**
```
📍 Localização: config/security/WebSecurityConfig.java
🔐 Configuração de Segurança:

1. CSRF desabilitado (para APIs REST)
2. Autorização HTTP:
   ✅ Permitidas:
      - POST /api/v1/auth/login
      - POST /api/v1/users
   🔒 Requer autenticação:
      - Qualquer outra rota

💡 Fluxo:
   Requisição → Filtro de Segurança → Verifica whitelist
   ├─ Se permitida: passa
   └─ Se não: requer token JWT válido
```

#### **JwtService**
```
📍 Localização: config/security/jwt/JwtService.java
🔑 Métodos:

1. generateToken(Long userId, String email) → String
   ├─ Cria claims: subject=email, userId=userId
   ├─ Data de emissão: agora
   ├─ Data de expiração: agora + jwt.expiration (3600000ms = 1h)
   ├─ Assina com HS256 + secret
   └─ Retorna token compacto

2. extractEmail(String token) → String
   ├─ Parse token
   ├─ Obtém subject (email)
   └─ Retorna email

3. extractUserId(String token) → Long
   ├─ Parse token
   ├─ Obtém claim "userId"
   └─ Retorna userId

4. isTokenValid(String token) → boolean
   ├─ Tenta fazer parse
   ├─ Se sucesso: retorna true
   ├─ Se erro (inválido/expirado): retorna false

5. isTokenExpired(String token) → boolean
   ├─ Extrai data de expiração
   ├─ Compara com agora
   └─ Retorna se expirou

🔐 Configurações (application.yaml):
   jwt.secret: String de assinatura (256+ bits)
   jwt.expiration: Tempo em milissegundos (padrão 1h)
```

#### **CustomUserDetailsService**
```
📍 Localização: user/security/CustomUserDetailsService.java
🎯 Implementa UserDetailsService (Spring Security)

📌 Método:
   loadUserByUsername(String username) → UserDetails
   ├─ username aqui é o email (não convencional mas possível)
   ├─ Busca usuário: userGateway.findByEmail(username)
   ├─ Se não encontrado: lança UsernameNotFoundException
   ├─ Cria UserDetails do Spring:
   │  ├─ username: email
   │  ├─ password: passwordHash
   │  ├─ authorities: [ROLE_USER]
   │  └─ enabled: true
   └─ Retorna UserDetails

💡 Uso:
   Spring Security chama este método quando precisa
   validar credenciais ou carregar permissões
```

---

### **9. Web Configuration**

#### **WebConfig**
```
📍 Localização: config/web/WebConfig.java
🌐 Configuração CORS:

Bean: WebMvcConfigurer
└─ addCorsMappings(CorsRegistry)
   ├─ addMapping("/**")
   ├─ allowedOrigins: ${cors.origins} (padrão: http://localhost:4200)
   ├─ allowedMethods: GET, POST, PUT, DELETE, PATCH
   ├─ allowedHeaders: "*"
   └─ allowCredentials: true

💡 Uso:
   Frontend (localhost:4200) pode fazer requisições
   Backend responde com headers CORS apropriados
```

---

### **10. Exception Handling**

#### **BusinessException**
```
📍 Localização: common/exception/BusinessException.java
🎯 Exceção customizada para regras de negócio:

🔑 Constructores:
   1. BusinessException(String message)
   2. BusinessException(String code, String message)
   3. BusinessException(String message, Throwable cause)
   4. BusinessException(String code, String message, Throwable cause)

📊 Uso:
   throw new BusinessException("Email já cadastrado")
   throw new BusinessException("INVALID_CREDENTIALS", "E-mail ou senha inválidos")

💡 Code:
   Código de erro para identificar tipo de exceção
   Pode ser usado pelo frontend para mensagens localizadas
```

---

## ⚙️ Configurações

### **application.yaml (Configurações Principais)**
```yaml
spring:
  application:
    name: finance-flow                    # Nome da aplicação
  profiles:
    active: ${APP_PROFILE:local}          # Perfil ativo
  jpa:
    open-in-view: false                   # Controle de sessão
    
cors:
  origins: ${CORS_ORIGINS:http://localhost:4200}  # Origens CORS

jwt:
  secret: ${JWT_SECRET:...}               # Chave de assinatura JWT
  expiration: ${JWT_EXPIRATION_MS:3600000}       # 1 hora em ms
```

### **application-local.yaml (Local)**
```
Configurações específicas para desenvolvimento
(Arquivo local, não versionado)
```

### **Variáveis de Ambiente**
```
APP_PROFILE=local              # Perfil Spring (local, dev, prod)
CORS_ORIGINS=...               # Origens permitidas
JWT_SECRET=...                 # Chave JWT (256+ bits)
JWT_EXPIRATION_MS=3600000      # Expiração JWT
DB_HOST=localhost              # Host do banco
DB_PORT=5432                   # Porta PostgreSQL
DB_NAME=financeflow            # Nome do banco
DB_USER=...                    # Usuário BD
DB_PASSWORD=...                # Senha BD
```

---

## 📚 Tecnologias e Dependências

### **Framework & Core**
- **Spring Boot 4.0.1**: Framework web principal
- **Spring MVC**: Para APIs REST
- **Spring Data JPA**: ORM e acesso a dados
- **Spring Security**: Autenticação e autorização

### **Banco de Dados**
- **PostgreSQL**: Banco de dados produção
- **H2**: Banco em memória para testes/dev
- **Flyway**: Migrations de banco de dados

### **Segurança**
- **JJWT (0.11.5)**: JSON Web Tokens
- **BCrypt**: Hash de senhas

### **Validação**
- **Jakarta Validation**: Validação de dados

### **Utilitários**
- **Lombok**: Reduce boilerplate (getters, setters, builders)
- **SLF4J**: Logging

### **Build**
- **Maven**: Gerenciador de dependências
- **Java 21**: Versão de linguagem

---

## 🔗 Como Tudo se Conecta

### **Diagrama de Fluxo Simplificado**

```
┌──────────────────────────────────────────────────────────────┐
│                      CLIENTE HTTP                            │
│              (Frontend em React/Angular/Vue)                 │
└────────────┬─────────────────────────────────────────────────┘
             │
             │ Requisição HTTP (JSON)
             ▼
┌──────────────────────────────────────────────────────────────┐
│          Spring Security - Filtro de Requisições             │
│  (WebSecurityConfig: valida se rota está permitida)         │
└────────────┬─────────────────────────────────────────────────┘
             │
             │ Se JWT, valida com JwtService
             ▼
┌──────────────────────────────────────────────────────────────┐
│              @RestController (Controller)                    │
│          (UserController ou AuthController)                  │
│  - Recebe @RequestBody (DTO)                                │
│  - Valida com Jakarta Validation                             │
│  - Converte DTO → Domain (UserDTOMapper)                    │
└────────────┬─────────────────────────────────────────────────┘
             │
             │ Chama serviço
             ▼
┌──────────────────────────────────────────────────────────────┐
│             @Service (Service Layer)                         │
│          (UserService ou AuthService)                        │
│  - Lógica de negócio                                         │
│  - Validações (ex: email único)                              │
│  - Encriptação de senhas (BCrypt)                            │
│  - Geração de tokens (JwtService)                            │
└────────────┬─────────────────────────────────────────────────┘
             │
             │ Usa gateway
             ▼
┌──────────────────────────────────────────────────────────────┐
│           Gateway Interface (UserGateway)                    │
│  - Abstração: service não conhece BD                         │
└────────────┬─────────────────────────────────────────────────┘
             │
             │ Implementação do gateway
             ▼
┌──────────────────────────────────────────────────────────────┐
│       @Component UserDataProvider                            │
│  - Implementa UserGateway                                    │
│  - Converte Domain → Entity (UserEntityMapper)               │
│  - Chama Repository                                          │
└────────────┬─────────────────────────────────────────────────┘
             │
             │ Usa JPA
             ▼
┌──────────────────────────────────────────────────────────────┐
│        JpaRepository (UserRepository)                        │
│  - Interface JPA gerada automaticamente                      │
│  - Cria SQL queries                                          │
└────────────┬─────────────────────────────────────────────────┘
             │
             │ Executa SQL
             ▼
┌──────────────────────────────────────────────────────────────┐
│             PostgreSQL / H2 Database                         │
│  - Armazena dados                                            │
│  - Retorna resultado                                         │
└────────────┬─────────────────────────────────────────────────┘
             │
             │ Retorna Entity
             ▼
┌──────────────────────────────────────────────────────────────┐
│       UserDataProvider                                       │
│  - Converte Entity → Domain (UserEntityMapper)               │
│  - Retorna User (domínio)                                    │
└────────────┬─────────────────────────────────────────────────┘
             │
             │ Retorna User
             ▼
┌──────────────────────────────────────────────────────────────┐
│          Service (UserService / AuthService)                 │
│  - Retorna resultado                                         │
└────────────┬─────────────────────────────────────────────────┘
             │
             │ Retorna User / Token
             ▼
┌──────────────────────────────────────────────────────────────┐
│             Controller                                       │
│  - Converte Domain → DTO (UserDTOMapper)                    │
│  - Retorna ResponseEntity<DTO>                              │
└────────────┬─────────────────────────────────────────────────┘
             │
             │ Serialização JSON
             ▼
┌──────────────────────────────────────────────────────────────┐
│                  CLIENTE HTTP                                │
│              Recebe JSON (Response)                          │
└──────────────────────────────────────────────────────────────┘
```

### **Padrões de Design Utilizados**

1. **Clean Architecture**
   - Camadas bem definidas
   - Cada camada tem responsabilidade clara
   - Dependências apontam para dentro

2. **Gateway Pattern**
   - `UserGateway`: Interface que abstrai acesso a dados
   - `UserDataProvider`: Implementação do gateway
   - Service usa gateway, não repository

3. **Data Mapper Pattern**
   - `UserDTOMapper`: DTO ↔ Domain
   - `UserEntityMapper`: Entity ↔ Domain
   - Camadas não conhecem uma à outra

4. **DTO Pattern**
   - `CreateUserRequestDTO`: Para receber dados
   - `UserResponseDTO`: Para retornar dados
   - Protege domínio de mudanças na API

5. **Dependency Injection**
   - @Autowired, @RequiredArgsConstructor
   - Spring gerencia dependências
   - Fácil para testes

6. **Builder Pattern**
   - `User.builder()`: Construção fluente
   - `Lombok @Builder`: Gerado automaticamente

---

## 📊 Modelo de Dados

### **Tabela: users**
```sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,              -- Auto-incremento
    name VARCHAR(150) NOT NULL,            -- Nome do usuário
    email VARCHAR(255) NOT NULL UNIQUE,    -- Email único
    password_hash VARCHAR(255) NOT NULL,   -- Senha encriptada
    created_at TIMESTAMP DEFAULT NOW(),    -- Criação
    updated_at TIMESTAMP DEFAULT NOW()     -- Última atualização
);
```

### **Migrations (Flyway)**
- `V1__create_users_table.sql`: Criação da tabela de usuários
- Outras migrations virão conforme o projeto evolua (V2, V3, etc)

---

## 🚀 Fluxo de Inicialização da Aplicação

1. **Maven Build**: `mvn clean install`
   - Compila código
   - Executa testes
   - Gera JAR

2. **Spring Boot Startup**: `mvn spring-boot:run`
   - Carrega `FinanceFlowApplication.java`
   - Spring cria contexto de aplicação
   - Escaneia @ComponentScan, @Configuration, @Bean
   - Cria beans: SecurityConfig, WebSecurityConfig, WebConfig
   - Inicializa DataSource, JPA, Repository
   - Executa migrations Flyway (V1__create_users_table.sql)
   - Inicia servidor embarcado Tomcat em http://localhost:8080

3. **Requisição Chega**:
   - Tomcat recebe HTTP request
   - Spring DispatcherServlet processa
   - SecurityFilterChain valida
   - Router encontra @RequestMapping apropriado
   - Controller é chamado
   - Fluxo de lógica inicia

---

## 📝 Resumo de Arquivo por Arquivo

| Arquivo | Localização | Responsabilidade |
|---------|-------------|------------------|
| FinanceFlowApplication | Raiz | Ponto de entrada, @SpringBootApplication |
| AuthController | controller/ | Endpoint POST /api/v1/auth/login |
| UserController | controller/ | Endpoint POST /api/v1/users |
| AuthService | service/ | Lógica de autenticação |
| UserService | service/ | Lógica de registro |
| User | domain/ | Entidade de domínio pura |
| UserGateway | gateway/ | Interface de acesso a dados |
| UserDataProvider | dataprovider/ | Implementação do gateway |
| UserEntity | dataprovider/entity/ | Entidade JPA do banco |
| UserRepository | repository/ | Interface JPA Repository |
| UserDTOMapper | mapper/ | Conversão DTO ↔ Domain |
| UserEntityMapper | dataprovider/mapper/ | Conversão Entity ↔ Domain |
| CreateUserRequestDTO | dto/request/ | DTO para registro |
| LoginRequestDTO | dto/request/ | DTO para login |
| UserResponseDTO | dto/response/ | DTO de resposta de usuário |
| LoginResponseDTO | dto/response/ | DTO de resposta de login |
| SecurityConfig | config/security/ | Configuração de segurança |
| WebSecurityConfig | config/security/ | Filtro de requisições HTTP |
| JwtService | config/security/jwt/ | Geração e validação de JWT |
| CustomUserDetailsService | user/security/ | Carregador de usuários |
| WebConfig | config/web/ | Configuração CORS |
| BusinessException | common/exception/ | Exceção customizada |
| application.yaml | resources/ | Configurações principais |
| V1__create_users_table.sql | resources/db/migration/ | Script de criação da tabela |

---

## 🔍 Próximas Camadas em Desenvolvimento

O projeto tem estrutura preparada para o módulo `transaction/`:
- `controller/`: Endpoints de transações
- `service/`: Lógica de negócio de transações
- `domain/`: Entidade Transaction
- `gateway/`: Interface TransactionGateway
- `dataprovider/`: Implementação do gateway
- `repository/`: JPA Repository para Transaction
- `dto/`: DTOs de requisição/resposta
- `mapper/`: Mapeadores

---

## 📖 Conclusão

O **Finance Flow Backend** é um projeto bem estruturado seguindo **Clean Architecture** com excelente separação de responsabilidades. Cada camada tem um propósito claro, facilitando manutenção, testes e expansão futura.

**Principais Características**:
- ✅ Autenticação com JWT
- ✅ Senhas encriptadas com BCrypt
- ✅ Validação de entrada com Jakarta Validation
- ✅ CORS configurável
- ✅ Migrations automáticas com Flyway
- ✅ Código limpo e bem documentado com Lombok
- ✅ Fácil de testar (camadas desacopladas)
- ✅ Escalável (estrutura pronta para novos módulos)


