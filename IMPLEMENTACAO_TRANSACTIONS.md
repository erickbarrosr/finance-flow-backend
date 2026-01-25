# IMPLEMENTAÇÃO DO MÓDULO TRANSACTIONS

## 📋 RESUMO

Implementação completa do módulo **Transactions** seguindo rigorosamente o padrão arquitetural do projeto Finance Flow. A implementação inclui todas as camadas necessárias para um CRUD funcional com autenticação JWT.

---

## 📊 ESPECIFICAÇÃO DO BANCO DE DADOS

### Tabela: `transactions` (V2__create_transactions_table.sql)

```sql
CREATE TABLE transactions (
    id          BIGSERIAL PRIMARY KEY,
    user_id     BIGINT         NOT NULL,
    type        VARCHAR(255)   NOT NULL,
    description TEXT,
    amount      NUMERIC(15, 2) NOT NULL,
    category    VARCHAR(255)   NOT NULL,
    date        DATE,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_transactions_user FOREIGN KEY (user_id)
        REFERENCES users (id)
        ON DELETE CASCADE
);
```

### Campos

| Campo | Tipo | Constraints | Descrição |
|-------|------|-------------|-----------|
| `id` | BIGSERIAL | PRIMARY KEY | Identificador único (auto-incremento) |
| `user_id` | BIGINT | NOT NULL, FK | Referência ao usuário proprietário |
| `type` | VARCHAR(255) | NOT NULL | Tipo de transação (ex: INCOME, EXPENSE) |
| `description` | TEXT | Nullable | Descrição opcional da transação |
| `amount` | NUMERIC(15,2) | NOT NULL | Valor da transação (até 15 dígitos, 2 decimais) |
| `category` | VARCHAR(255) | NOT NULL | Categoria (ex: FOOD, TRANSPORT) |
| `date` | DATE | Nullable | Data da transação |
| `created_at` | TIMESTAMP | NOT NULL | Timestamp de criação (auto-gerado) |
| `updated_at` | TIMESTAMP | NOT NULL | Timestamp de última atualização |

---

## 🏗️ ARQUITETURA DO MÓDULO

### Estrutura de Diretórios

```
transaction/
├── domain/
│   └── Transaction.java                [Entidade de domínio pura]
│
├── dataprovider/
│   ├── entity/
│   │   └── TransactionEntity.java       [Mapeamento JPA]
│   ├── mapper/
│   │   └── TransactionEntityMapper.java [Entity ↔ Domain]
│   └── TransactionDataProvider.java     [Implementa TransactionGateway]
│
├── repository/
│   └── TransactionRepository.java       [JPA Repository]
│
├── gateway/
│   └── TransactionGateway.java          [Interface abstrata]
│
├── service/
│   └── TransactionService.java          [Lógica de negócio]
│
├── dto/
│   ├── request/
│   │   ├── CreateTransactionRequestDTO.java
│   │   └── UpdateTransactionRequestDTO.java
│   └── response/
│       └── TransactionResponseDTO.java
│
├── mapper/
│   └── TransactionDTOMapper.java        [DTO ↔ Domain]
│
└── controller/
    └── TransactionController.java       [Endpoints REST]
```

---

## 🔍 DETALHAMENTO DE CADA COMPONENTE

### 1. Domain Layer

#### `Transaction.java` (Entidade de Domínio Pura)

```java
@Getter
@Builder
public class Transaction {
    private final Long id;
    
    @NonNull
    private final Long userId;
    
    @NonNull
    private final String type;
    
    private final String description;
    
    @NonNull
    private final BigDecimal amount;
    
    @NonNull
    private final String category;
    
    private final LocalDate date;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    
    // Constructor privado com validações
    private Transaction(...) {
        // Validações de negócio
    }
}
```

**Responsabilidades:**
- Representar uma transação no domínio
- Validar regras de negócio (userId, type, amount, category obrigatórios)
- Ser imutável (final fields, sem setters)
- Sem anotações JPA

**Validações:**
- `userId` não pode ser null
- `type` não pode ser null ou em branco
- `amount` não pode ser null
- `category` não pode ser null ou em branco

---

### 2. Data Provider Layer

#### `TransactionEntity.java` (JPA Entity)

```java
@Entity
@Table(name = "transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(nullable = false, length = 255)
    private String type;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;
    
    @Column(nullable = false, length = 255)
    private String category;
    
    @Column(columnDefinition = "DATE")
    private LocalDate date;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
```

**Responsabilidades:**
- Mapear para coluna da tabela `transactions`
- Representar estado persistido
- Ser mutável (setters para ORM)
- Sincronizar com banco de dados

**Anotações JPA:**
- `@Entity`: Marca como entidade JPA
- `@Table(name = "transactions")`: Nome da tabela
- `@Column(name = "...", nullable = false, ...)`: Configuração de coluna
- `@GeneratedValue(strategy = GenerationType.IDENTITY)`: Auto-incremento
- `@Id`: Chave primária

---

#### `TransactionRepository.java` (JPA Repository)

```java
public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {
    
    @Query("SELECT t FROM TransactionEntity t WHERE t.userId = :userId ORDER BY t.date DESC")
    List<TransactionEntity> findByUserId(@Param("userId") Long userId);
    
    @Query("SELECT t FROM TransactionEntity t WHERE t.userId = :userId AND t.id = :id")
    Optional<TransactionEntity> findByIdAndUserId(
            @Param("id") Long id,
            @Param("userId") Long userId
    );
}
```

**Responsabilidades:**
- Definir queries customizadas
- Herdar operações CRUD padrão (save, findById, delete, etc.)

**Queries:**
- `findByUserId`: Retorna transações do usuário ordenadas por data DESC
- `findByIdAndUserId`: Busca transação específica de um usuário (segurança)

---

#### `TransactionEntityMapper.java` (Data Mapper)

```java
@UtilityClass
public class TransactionEntityMapper {
    
    public TransactionEntity toEntity(Transaction transaction) {
        return TransactionEntity.builder()
                .id(transaction.getId())
                .userId(transaction.getUserId())
                .type(transaction.getType())
                .description(transaction.getDescription())
                .amount(transaction.getAmount())
                .category(transaction.getCategory())
                .date(transaction.getDate())
                .createdAt(transaction.getCreatedAt())
                .updatedAt(transaction.getUpdatedAt())
                .build();
    }
    
    public Transaction toDomain(TransactionEntity entity) {
        return Transaction.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .type(entity.getType())
                .description(entity.getDescription())
                .amount(entity.getAmount())
                .category(entity.getCategory())
                .date(entity.getDate())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
```

**Responsabilidades:**
- Converter `Transaction` (domínio) para `TransactionEntity` (JPA)
- Converter `TransactionEntity` (JPA) para `Transaction` (domínio)
- Isolar lógica de mapeamento

**Por que separado?**
- Entity é específica de JPA
- Domain é puro (sem framework)
- Mapper permite trocar implementação de banco facilmente

---

#### `TransactionDataProvider.java` (Gateway Implementation)

```java
@Component
@RequiredArgsConstructor
public class TransactionDataProvider implements TransactionGateway {
    
    private final TransactionRepository transactionRepository;
    
    @Override
    public Transaction save(Transaction transaction) {
        var entity = TransactionEntityMapper.toEntity(transaction);
        var saved = transactionRepository.save(entity);
        return TransactionEntityMapper.toDomain(saved);
    }
    
    @Override
    public Optional<Transaction> findById(Long id) {
        return transactionRepository.findById(id)
                .map(TransactionEntityMapper::toDomain);
    }
    
    @Override
    public Optional<Transaction> findByIdAndUserId(Long id, Long userId) {
        return transactionRepository.findByIdAndUserId(id, userId)
                .map(TransactionEntityMapper::toDomain);
    }
    
    @Override
    public List<Transaction> findByUserId(Long userId) {
        return transactionRepository.findByUserId(userId)
                .stream()
                .map(TransactionEntityMapper::toDomain)
                .toList();
    }
    
    @Override
    public void delete(Long id) {
        transactionRepository.deleteById(id);
    }
    
    @Override
    public boolean existsById(Long id) {
        return transactionRepository.existsById(id);
    }
}
```

**Responsabilidades:**
- Implementar interface `TransactionGateway`
- Orquestrar conversões Entity ↔ Domain
- Delegação ao Repository

**Por que existir?**
- Desacopla Service da implementação JPA
- Permite múltiplas implementações (arquivo, cache, etc.)
- Facilita testes (mock fácil)

---

### 3. Gateway Layer

#### `TransactionGateway.java` (Interface Abstrata)

```java
public interface TransactionGateway {
    
    Transaction save(Transaction transaction);
    
    Optional<Transaction> findById(Long id);
    
    Optional<Transaction> findByIdAndUserId(Long id, Long userId);
    
    List<Transaction> findByUserId(Long userId);
    
    void delete(Long id);
    
    boolean existsById(Long id);
}
```

**Responsabilidades:**
- Definir contrato de acesso a dados
- Abstrair implementação

**Métodos:**
- `save()`: Criar ou atualizar
- `findById()`: Buscar por ID
- `findByIdAndUserId()`: Buscar com validação de propriedade
- `findByUserId()`: Listar do usuário
- `delete()`: Remover
- `existsById()`: Verificar existência

---

### 4. DTO Layer

#### `CreateTransactionRequestDTO.java` (Request)

```java
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateTransactionRequestDTO {
    
    @NotBlank(message = "Tipo de transação é obrigatório")
    private String type;
    
    private String description;
    
    @NotNull(message = "Valor é obrigatório")
    @Positive(message = "Valor deve ser positivo")
    private BigDecimal amount;
    
    @NotBlank(message = "Categoria é obrigatória")
    private String category;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;
}
```

**Validações:**
- `type`: Obrigatório, não em branco
- `amount`: Obrigatório, positivo
- `category`: Obrigatório, não em branco
- `description` e `date`: Opcionais

---

#### `UpdateTransactionRequestDTO.java` (Request)

```java
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateTransactionRequestDTO {
    
    private String type;
    
    private String description;
    
    @Positive(message = "Valor deve ser positivo")
    private BigDecimal amount;
    
    private String category;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;
}
```

**Diferença:**
- Todos os campos são opcionais
- Validação `@Positive` apenas se fornecido
- Permite atualizações parciais

---

#### `TransactionResponseDTO.java` (Response)

```java
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionResponseDTO {
    
    private Long id;
    private Long userId;
    private String type;
    private String description;
    private BigDecimal amount;
    private String category;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;
}
```

**Conteúdo:**
- Inclui todos os dados da transação
- Inclui timestamps para auditoria
- Formatos de data específicos

---

### 5. Mapper Layer

#### `TransactionDTOMapper.java` (DTO ↔ Domain)

```java
@UtilityClass
public class TransactionDTOMapper {
    
    public Transaction toDomain(CreateTransactionRequestDTO dto, Long userId) {
        var now = LocalDateTime.now();
        return Transaction.builder()
                .id(null)
                .userId(userId)
                .type(dto.getType())
                .description(dto.getDescription())
                .amount(dto.getAmount())
                .category(dto.getCategory())
                .date(dto.getDate())
                .createdAt(now)
                .updatedAt(now)
                .build();
    }
    
    public Transaction toDomain(UpdateTransactionRequestDTO dto, Transaction existing) {
        return Transaction.builder()
                .id(existing.getId())
                .userId(existing.getUserId())
                .type(dto.getType() != null ? dto.getType() : existing.getType())
                .description(dto.getDescription() != null ? dto.getDescription() : existing.getDescription())
                .amount(dto.getAmount() != null ? dto.getAmount() : existing.getAmount())
                .category(dto.getCategory() != null ? dto.getCategory() : existing.getCategory())
                .date(dto.getDate() != null ? dto.getDate() : existing.getDate())
                .createdAt(existing.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .build();
    }
    
    public TransactionResponseDTO toResponse(Transaction transaction) {
        return TransactionResponseDTO.builder()
                .id(transaction.getId())
                .userId(transaction.getUserId())
                .type(transaction.getType())
                .description(transaction.getDescription())
                .amount(transaction.getAmount())
                .category(transaction.getCategory())
                .date(transaction.getDate())
                .createdAt(transaction.getCreatedAt())
                .updatedAt(transaction.getUpdatedAt())
                .build();
    }
}
```

**Responsabilidades:**
- `toDomain(Create...)`: Converter DTO de criação para domínio
- `toDomain(Update...)`: Converter DTO de atualização, mantendo dados existentes
- `toResponse()`: Converter domínio para DTO de resposta

---

### 6. Service Layer

#### `TransactionService.java` (Lógica de Negócio)

```java
@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService {
    
    private final TransactionGateway transactionGateway;
    
    public Transaction create(Transaction transaction) {
        log.debug("Criando transação para usuário: {}", transaction.getUserId());
        var saved = transactionGateway.save(transaction);
        log.info("Transação criada com sucesso. ID: {}, User ID: {}",
                saved.getId(), saved.getUserId());
        return saved;
    }
    
    public List<Transaction> listByUserId(Long userId) {
        log.debug("Listando transações para usuário: {}", userId);
        var transactions = transactionGateway.findByUserId(userId);
        log.info("Encontradas {} transações para usuário: {}", 
                transactions.size(), userId);
        return transactions;
    }
    
    public Transaction findById(Long id, Long userId) {
        log.debug("Buscando transação com ID: {}, User ID: {}", id, userId);
        var transaction = transactionGateway.findByIdAndUserId(id, userId)
                .orElseThrow(() -> {
                    log.warn("Transação não encontrada. ID: {}, User ID: {}", id, userId);
                    return new BusinessException("Transação não encontrada");
                });
        return transaction;
    }
    
    public Transaction update(Long id, Long userId, Transaction updatedTransaction) {
        log.debug("Atualizando transação com ID: {}, User ID: {}", id, userId);
        var existing = findById(id, userId); // Valida propriedade
        var updated = transactionGateway.save(updatedTransaction);
        log.info("Transação atualizada com sucesso. ID: {}", updated.getId());
        return updated;
    }
    
    public void delete(Long id, Long userId) {
        log.debug("Deletando transação com ID: {}, User ID: {}", id, userId);
        var transaction = findById(id, userId); // Valida propriedade
        transactionGateway.delete(id);
        log.info("Transação deletada com sucesso. ID: {}", id);
    }
}
```

**Responsabilidades:**
- Orquestrar operações
- Validar regras de negócio
- Logging estruturado
- Tratamento de exceções

**Operações:**
- `create()`: Criar nova transação
- `listByUserId()`: Listar transações do usuário
- `findById()`: Buscar transação específica (com validação de propriedade)
- `update()`: Atualizar transação existente
- `delete()`: Remover transação

---

### 7. Controller Layer

#### `TransactionController.java` (Endpoints REST)

```java
@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
@Slf4j
public class TransactionController {
    
    private final TransactionService transactionService;
    private final JwtService jwtService;
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<TransactionResponseDTO> create(
            @RequestBody @Valid CreateTransactionRequestDTO dto,
            HttpServletRequest request
    ) {
        // Extrai userId do token JWT
        var userId = extractUserIdFromToken(request);
        
        // Converte DTO para domínio
        var domain = TransactionDTOMapper.toDomain(dto, userId);
        
        // Cria via service
        var saved = transactionService.create(domain);
        
        // Retorna resposta
        var response = TransactionDTOMapper.toResponse(saved);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping
    public ResponseEntity<List<TransactionResponseDTO>> listByUserId(
            HttpServletRequest request
    ) {
        var userId = extractUserIdFromToken(request);
        var transactions = transactionService.listByUserId(userId);
        var response = transactions.stream()
                .map(TransactionDTOMapper::toResponse)
                .toList();
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponseDTO> findById(
            @PathVariable Long id,
            HttpServletRequest request
    ) {
        var userId = extractUserIdFromToken(request);
        var transaction = transactionService.findById(id, userId);
        var response = TransactionDTOMapper.toResponse(transaction);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<TransactionResponseDTO> update(
            @PathVariable Long id,
            @RequestBody @Valid UpdateTransactionRequestDTO dto,
            HttpServletRequest request
    ) {
        var userId = extractUserIdFromToken(request);
        var existing = transactionService.findById(id, userId);
        var domain = TransactionDTOMapper.toDomain(dto, existing);
        var updated = transactionService.update(id, userId, domain);
        var response = TransactionDTOMapper.toResponse(updated);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable Long id,
            HttpServletRequest request
    ) {
        var userId = extractUserIdFromToken(request);
        transactionService.delete(id, userId);
    }
    
    private Long extractUserIdFromToken(HttpServletRequest request) {
        var authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            var token = authHeader.substring(7);
            return jwtService.extractUserId(token);
        }
        throw new IllegalStateException("Token JWT não encontrado na requisição");
    }
}
```

**Endpoints:**

| Método | Endpoint | Status | Descrição |
|--------|----------|--------|-----------|
| `POST` | `/api/v1/transactions` | 201 | Criar transação |
| `GET` | `/api/v1/transactions` | 200 | Listar todas |
| `GET` | `/api/v1/transactions/{id}` | 200 | Buscar por ID |
| `PUT` | `/api/v1/transactions/{id}` | 200 | Atualizar |
| `DELETE` | `/api/v1/transactions/{id}` | 204 | Deletar |

---

## 🔐 SEGURANÇA

### Autenticação JWT

1. **Extração de Token**: Header `Authorization: Bearer <token>`
2. **Validação**: JwtService valida assinatura
3. **Extração de userId**: Claim `userId` armazenado no token
4. **Validação de Propriedade**: Service garante que usuário só pode acessar suas transações

### Proteção

```java
// Service valida propriedade antes de qualquer operação
public Transaction findById(Long id, Long userId) {
    return transactionGateway.findByIdAndUserId(id, userId)
            .orElseThrow(() -> new BusinessException("Transação não encontrada"));
}
```

---

## 📊 FLUXO COMPLETO: CRIAR TRANSAÇÃO

```
1. Cliente → POST /api/v1/transactions
   {
     "type": "EXPENSE",
     "description": "Mercado",
     "amount": 150.50,
     "category": "FOOD",
     "date": "2026-01-25"
   }
   Header: Authorization: Bearer eyJhbGc...

2. WebSecurityConfig
   ✓ Valida token JWT
   ✓ Não está em whitelist, requer autenticação

3. TransactionController.create()
   ✓ Valida CreateTransactionRequestDTO
   ✓ Extrai userId=123 do token
   ✓ Chama TransactionDTOMapper.toDomain(dto, 123)

4. TransactionDTOMapper.toDomain()
   Cria: Transaction {
     id: null,
     userId: 123,
     type: "EXPENSE",
     description: "Mercado",
     amount: 150.50,
     category: "FOOD",
     date: 2026-01-25,
     createdAt: 2026-01-25T10:30:00,
     updatedAt: 2026-01-25T10:30:00
   }

5. TransactionService.create(transaction)
   ✓ Log debug
   ✓ Chama transactionGateway.save(transaction)

6. TransactionDataProvider.save(transaction)
   ✓ Mapper.toEntity(transaction) → TransactionEntity
   ✓ Chama repository.save(entity)

7. TransactionRepository.save()
   Executa: INSERT INTO transactions 
            (user_id, type, description, amount, category, date, created_at, updated_at)
            VALUES (123, 'EXPENSE', 'Mercado', 150.50, 'FOOD', '2026-01-25', ..., ...)
   Resultado: id=456 (auto-gerado)

8. Volta em cadeia
   Entity → Mapper.toDomain() → Transaction
   → Controller → Mapper.toResponse() → DTO

9. Resposta HTTP 201 CREATED
   {
     "id": 456,
     "userId": 123,
     "type": "EXPENSE",
     "description": "Mercado",
     "amount": 150.50,
     "category": "FOOD",
     "date": "2026-01-25",
     "createdAt": "2026-01-25T10:30:00",
     "updatedAt": "2026-01-25T10:30:00"
   }
```

---

## ✅ VALIDAÇÕES EM CASCATA

### Criação de Transação

```
1. DTO Validation (entrada)
   @NotBlank private String type;
   @NotNull @Positive private BigDecimal amount;
   @NotBlank private String category;
   → Se falhar: HTTP 400 Bad Request

2. Domain Validation (domínio)
   if (userId == null) throw IllegalArgumentException("User ID é obrigatório");
   if (type == null || type.isBlank()) throw IllegalArgumentException("Tipo é obrigatório");
   if (amount == null) throw IllegalArgumentException("Valor é obrigatório");
   if (category == null || category.isBlank()) throw IllegalArgumentException("Categoria é obrigatória");
   → Se falhar: HTTP 500 (programação defensiva)

3. Service Validation (estado)
   // Implicitamente: findByIdAndUserId garante que transação pertence ao usuário
   → Se falhar: HTTP 404 Not Found
```

---

## 🚀 EXEMPLOS DE USO (cURL)

### 1. Criar Transação

```bash
curl -X POST http://localhost:8080/api/v1/transactions \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer eyJhbGc..." \
  -d '{
    "type": "EXPENSE",
    "description": "Supermercado",
    "amount": 150.50,
    "category": "FOOD",
    "date": "2026-01-25"
  }'
```

### 2. Listar Transações

```bash
curl http://localhost:8080/api/v1/transactions \
  -H "Authorization: Bearer eyJhbGc..."
```

### 3. Buscar Transação Específica

```bash
curl http://localhost:8080/api/v1/transactions/456 \
  -H "Authorization: Bearer eyJhbGc..."
```

### 4. Atualizar Transação

```bash
curl -X PUT http://localhost:8080/api/v1/transactions/456 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer eyJhbGc..." \
  -d '{
    "description": "Supermercado - Atualizado",
    "amount": 175.75
  }'
```

### 5. Deletar Transação

```bash
curl -X DELETE http://localhost:8080/api/v1/transactions/456 \
  -H "Authorization: Bearer eyJhbGc..."
```

---

## 📝 NOTAS DE IMPLEMENTAÇÃO

### Por que BigDecimal para amount?

```java
// ❌ Ruim: Perda de precisão com float/double
double amount = 150.50;
amount = amount * 2; // 301.0000000000002

// ✅ Bom: Precisão exata com BigDecimal
BigDecimal amount = new BigDecimal("150.50");
amount = amount.multiply(new BigDecimal("2")); // 301.00 (exato)
```

### Por que findByIdAndUserId?

```java
// ❌ Ruim: User A pode acessar transação de User B
Long transactionId = 456;
var tx = transactionGateway.findById(transactionId); // Sem validação!

// ✅ Bom: Garantir que user só acessa suas transações
Long userId = 123;
Long transactionId = 456;
var tx = transactionGateway.findByIdAndUserId(transactionId, userId); // Validado!
```

### Por que Logger em Debug e Info?

```java
// Debug: Informações internas (desenvolvimento)
log.debug("Buscando transação com ID: {}, User ID: {}", id, userId);

// Info: Eventos importantes (produção)
log.info("Transação criada com sucesso. ID: {}, User ID: {}", id, userId);

// Warn: Situações anormais
log.warn("Transação não encontrada. ID: {}, User ID: {}", id, userId);

// Error: Erros inesperados
log.error("Erro ao processar transação", exception);
```

---

## 🔄 POSSÍVEIS EXTENSÕES

1. **Paginação**: Adicionar `Pageable` em `listByUserId()`
2. **Filtros**: Buscar por intervalo de datas, categoria, tipo
3. **Soft Delete**: Marcar como deletado em vez de remover
4. **Auditoria**: Quem alterou e quando
5. **Validação de categoria**: Enum com valores pré-definidos
6. **Saldo do usuário**: Cache de saldo total
7. **Relatórios**: Gastos por categoria, período, etc.
8. **Testes**: Unit, Integration, e2e

---

## ✅ CHECKLIST DE IMPLEMENTAÇÃO

- ✅ Transaction domain com validações
- ✅ TransactionEntity com JPA mappings
- ✅ TransactionRepository com queries customizadas
- ✅ TransactionGateway interface abstrata
- ✅ TransactionDataProvider implementação
- ✅ TransactionEntityMapper Entity ↔ Domain
- ✅ Create/Update DTOs com validações
- ✅ TransactionResponseDTO
- ✅ TransactionDTOMapper DTO ↔ Domain
- ✅ TransactionService com lógica de negócio
- ✅ TransactionController CRUD endpoints
- ✅ Extração de userId via JWT
- ✅ Validação de propriedade (usuario só acessa suas transações)
- ✅ Logging estruturado
- ✅ Código compilando sem erros
- ✅ Padrão consistente com User module
- ✅ Lombok utilizado extensivamente
- ✅ Código limpo e legível

---

## 🎓 CONCLUSÃO

A implementação de Transactions segue rigorosamente o padrão do projeto, oferecendo:

- ✅ **CRUD Completo**: Create, Read, Update, Delete
- ✅ **Segurança**: Validação JWT + propriedade
- ✅ **Validações em Cascata**: DTO → Domain → Service
- ✅ **Padrão Consistente**: Igual ao User module
- ✅ **Código Limpo**: Lombok, logging, responsabilidade única
- ✅ **Manutenibilidade**: Fácil para novo desenvolvedor entender
- ✅ **Extensibilidade**: Pronto para futuras expansões

Qualquer desenvolvedor consegue rapidamente compreender e estender este módulo.
