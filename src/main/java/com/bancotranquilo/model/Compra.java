package com.bancotranquilo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "compras")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Compra {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String numeroCartao;
    
    @Column(nullable = false)
    private String cvv;
    
    @Column(nullable = false)
    private String dataValidade;
    
    @Column(nullable = false)
    private BigDecimal valor;
    
    @Column(nullable = false)
    private String emailCliente;
    
    @Column(nullable = false)
    private String nomeCliente;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusCompra status;
    
    @Column(nullable = false)
    private LocalDateTime dataCriacao;
    
    private LocalDateTime dataConfirmacao;
    
    private String mensagemErro;
    
    @PrePersist
    protected void onCreate() {
        dataCriacao = LocalDateTime.now();
        if (status == null) {
            status = StatusCompra.PENDENTE;
        }
    }
}


