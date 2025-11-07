package com.bancotranquilo.model.dto;

import com.bancotranquilo.model.StatusCompra;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompraResponse {
    private Long id;
    private StatusCompra status;
    private String mensagem;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataConfirmacao;
}


