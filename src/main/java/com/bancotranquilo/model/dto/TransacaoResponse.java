package com.bancotranquilo.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransacaoResponse {
    private Long compraId;
    private boolean sucesso;
    private String mensagem;
    private String codigoTransacao;
}


