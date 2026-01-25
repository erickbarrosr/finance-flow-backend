package br.com.ersoftwares.financeflow.transaction.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

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
