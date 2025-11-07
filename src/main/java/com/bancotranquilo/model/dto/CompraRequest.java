package com.bancotranquilo.model.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompraRequest {
    
    @NotBlank(message = "Número do cartão é obrigatório")
    @Size(min = 13, max = 19, message = "Número do cartão deve ter entre 13 e 19 dígitos")
    private String numeroCartao;
    
    @NotBlank(message = "CVV é obrigatório")
    @Size(min = 3, max = 4, message = "CVV deve ter 3 ou 4 dígitos")
    private String cvv;
    
    @NotBlank(message = "Data de validade é obrigatória")
    @Pattern(regexp = "^(0[1-9]|1[0-2])\\/([0-9]{2})$", message = "Data de validade deve estar no formato MM/YY")
    private String dataValidade;
    
    @NotNull(message = "Valor é obrigatório")
    @DecimalMin(value = "0.01", message = "Valor deve ser maior que zero")
    private BigDecimal valor;
    
    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ter um formato válido")
    private String emailCliente;
    
    @NotBlank(message = "Nome do cliente é obrigatório")
    private String nomeCliente;
}


