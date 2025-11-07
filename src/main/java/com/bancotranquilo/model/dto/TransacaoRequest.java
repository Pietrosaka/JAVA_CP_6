package com.bancotranquilo.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransacaoRequest {
    private Long compraId;
    private String numeroCartao;
    private String cvv;
    private String dataValidade;
    private BigDecimal valor;
    private String emailCliente;
    private String nomeCliente;
}


