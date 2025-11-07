package com.bancotranquilo.listener;

import com.bancotranquilo.model.dto.TransacaoRequest;
import com.bancotranquilo.model.dto.TransacaoResponse;
import com.bancotranquilo.service.BancoTranquiloService;
import com.bancotranquilo.service.CompraService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.bancotranquilo.config.RabbitMQConfig.QUEUE_REQUISICOES;

@Component
@Slf4j
public class TransacaoListener {
    
    private final BancoTranquiloService bancoTranquiloService;
    private final CompraService compraService;
    
    @Autowired
    public TransacaoListener(BancoTranquiloService bancoTranquiloService,
                             CompraService compraService) {
        this.bancoTranquiloService = bancoTranquiloService;
        this.compraService = compraService;
    }
    
    @RabbitListener(queues = QUEUE_REQUISICOES)
    public void processarRequisicaoTransacao(TransacaoRequest request) {
        log.info("Processando requisição de transação recebida da fila RabbitMQ para compra ID: {}", 
                request.getCompraId());
        
        try {
            // Processar transação com a API do Banco Tranquilo
            TransacaoResponse response = bancoTranquiloService.processarTransacao(request);
            
            // Processar resposta e atualizar compra
            compraService.processarRespostaTransacao(response);
            
            log.info("Transação processada com sucesso para compra ID: {}", request.getCompraId());
            
        } catch (Exception e) {
            log.error("Erro ao processar requisição de transação: {}", e.getMessage(), e);
            
            // Criar resposta de erro
            TransacaoResponse errorResponse = new TransacaoResponse();
            errorResponse.setCompraId(request.getCompraId());
            errorResponse.setSucesso(false);
            errorResponse.setMensagem("Erro ao processar transação: " + e.getMessage());
            
            try {
                compraService.processarRespostaTransacao(errorResponse);
            } catch (Exception ex) {
                log.error("Erro ao processar resposta de erro: {}", ex.getMessage(), ex);
            }
        }
    }
}


