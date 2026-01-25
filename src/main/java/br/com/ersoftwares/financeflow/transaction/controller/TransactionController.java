package br.com.ersoftwares.financeflow.transaction.controller;

import br.com.ersoftwares.financeflow.config.security.jwt.JwtService;
import br.com.ersoftwares.financeflow.transaction.dto.request.CreateTransactionRequestDTO;
import br.com.ersoftwares.financeflow.transaction.dto.request.UpdateTransactionRequestDTO;
import br.com.ersoftwares.financeflow.transaction.dto.response.TransactionResponseDTO;
import br.com.ersoftwares.financeflow.transaction.mapper.TransactionDTOMapper;
import br.com.ersoftwares.financeflow.transaction.service.TransactionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        log.info("Recebida requisição para criar transação");

        var userId = extractUserIdFromToken(request);
        var domain = TransactionDTOMapper.toDomain(dto, userId);
        var saved = transactionService.create(domain);
        var response = TransactionDTOMapper.toResponse(saved);

        log.info("Transação criada com sucesso. ID: {}", saved.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<TransactionResponseDTO>> listByUserId(
            HttpServletRequest request
    ) {
        log.info("Recebida requisição para listar transações");

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
        log.info("Recebida requisição para buscar transação com ID: {}", id);

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
        log.info("Recebida requisição para atualizar transação com ID: {}", id);

        var userId = extractUserIdFromToken(request);
        var existing = transactionService.findById(id, userId);
        var domain = TransactionDTOMapper.toDomain(dto, existing);
        var updated = transactionService.update(id, userId, domain);
        var response = TransactionDTOMapper.toResponse(updated);

        log.info("Transação atualizada com sucesso. ID: {}", id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable Long id,
            HttpServletRequest request
    ) {
        log.info("Recebida requisição para deletar transação com ID: {}", id);

        var userId = extractUserIdFromToken(request);
        transactionService.delete(id, userId);

        log.info("Transação deletada com sucesso. ID: {}", id);
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
