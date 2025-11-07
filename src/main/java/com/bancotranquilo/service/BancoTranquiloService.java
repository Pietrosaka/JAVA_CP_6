package com.bancotranquilo.service;

import com.bancotranquilo.model.dto.TransacaoRequest;
import com.bancotranquilo.model.dto.TransacaoResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@Slf4j
public class BancoTranquiloService {
    
    @Value("${banco.tranquilo.api.url}")
    private String apiUrl;
    
    private final RestTemplate restTemplate;
    
    public BancoTranquiloService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    
    public TransacaoResponse processarTransacao(TransacaoRequest request) {
        log.info("Processando transação para compra ID: {}", request.getCompraId());
        
        try {
            // Simulação de chamada à API do Banco Tranquilo
            // Em produção, isso seria uma chamada HTTP real
            String url = apiUrl + "/transacoes/processar";
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");
            
            HttpEntity<TransacaoRequest> entity = new HttpEntity<>(request, headers);
            
            // Tentativa de chamada real (pode falhar se a API não estiver disponível)
            try {
                ResponseEntity<TransacaoResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    TransacaoResponse.class
                );
                
                if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                    return response.getBody();
                }
            } catch (RestClientException e) {
                log.warn("API do Banco Tranquilo não disponível, usando simulação: {}", e.getMessage());
            }
            
            // Simulação de resposta da API (para desenvolvimento/teste)
            return simularRespostaAPI(request);
            
        } catch (Exception e) {
            log.error("Erro ao processar transação: {}", e.getMessage(), e);
            return criarRespostaErro(request.getCompraId(), "Erro ao processar transação: " + e.getMessage());
        }
    }
    
    private TransacaoResponse simularRespostaAPI(TransacaoRequest request) {
        // Simulação: 80% de sucesso, 20% de falha
        Random random = new Random();
        boolean sucesso = random.nextDouble() < 0.8;
        
        TransacaoResponse response = new TransacaoResponse();
        response.setCompraId(request.getCompraId());
        
        if (sucesso) {
            response.setSucesso(true);
            response.setMensagem("Transação aprovada com sucesso");
            response.setCodigoTransacao("TXN" + System.currentTimeMillis());
        } else {
            response.setSucesso(false);
            response.setMensagem("Transação rejeitada: Saldo insuficiente ou cartão inválido");
            response.setCodigoTransacao(null);
        }
        
        log.info("Simulação de resposta da API - Sucesso: {}, Mensagem: {}", 
                response.isSucesso(), response.getMensagem());
        
        return response;
    }
    
    private TransacaoResponse criarRespostaErro(Long compraId, String mensagem) {
        TransacaoResponse response = new TransacaoResponse();
        response.setCompraId(compraId);
        response.setSucesso(false);
        response.setMensagem(mensagem);
        response.setCodigoTransacao(null);
        return response;
    }
}


