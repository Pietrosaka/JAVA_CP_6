package com.bancotranquilo.controller;

import com.bancotranquilo.model.dto.CompraRequest;
import com.bancotranquilo.model.dto.CompraResponse;
import com.bancotranquilo.service.CompraService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/compras")
@Slf4j
public class CompraController {
    
    private final CompraService compraService;
    
    @Autowired
    public CompraController(CompraService compraService) {
        this.compraService = compraService;
    }
    
    @PostMapping
    public ResponseEntity<CompraResponse> criarCompra(@Valid @RequestBody CompraRequest request) {
        log.info("Recebida requisição para criar compra");
        try {
            CompraResponse response = compraService.criarCompra(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("Erro ao criar compra: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<CompraResponse> buscarCompra(@PathVariable Long id) {
        try {
            CompraResponse response = compraService.buscarCompraPorId(id);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Erro ao buscar compra: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping
    public ResponseEntity<List<CompraResponse>> listarCompras() {
        try {
            List<CompraResponse> compras = compraService.listarCompras();
            return ResponseEntity.ok(compras);
        } catch (Exception e) {
            log.error("Erro ao listar compras: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}


