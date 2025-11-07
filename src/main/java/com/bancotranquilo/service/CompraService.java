package com.bancotranquilo.service;

import com.bancotranquilo.model.Compra;
import com.bancotranquilo.model.StatusCompra;
import com.bancotranquilo.model.dto.CompraRequest;
import com.bancotranquilo.model.dto.CompraResponse;
import com.bancotranquilo.model.dto.TransacaoRequest;
import com.bancotranquilo.model.dto.TransacaoResponse;
import com.bancotranquilo.repository.CompraRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.bancotranquilo.config.RabbitMQConfig.*;

@Service
@Slf4j
public class CompraService {
    
    private final CompraRepository compraRepository;
    private final RabbitTemplate rabbitTemplate;
    private final BancoTranquiloService bancoTranquiloService;
    private final EmailService emailService;
    
    @Autowired
    public CompraService(CompraRepository compraRepository,
                        RabbitTemplate rabbitTemplate,
                        BancoTranquiloService bancoTranquiloService,
                        EmailService emailService) {
        this.compraRepository = compraRepository;
        this.rabbitTemplate = rabbitTemplate;
        this.bancoTranquiloService = bancoTranquiloService;
        this.emailService = emailService;
    }
    
    @Transactional
    public CompraResponse criarCompra(CompraRequest request) {
        log.info("Criando nova compra para cliente: {}", request.getEmailCliente());
        
        Compra compra = new Compra();
        compra.setNumeroCartao(request.getNumeroCartao());
        compra.setCvv(request.getCvv());
        compra.setDataValidade(request.getDataValidade());
        compra.setValor(request.getValor());
        compra.setEmailCliente(request.getEmailCliente());
        compra.setNomeCliente(request.getNomeCliente());
        compra.setStatus(StatusCompra.PENDENTE);
        
        compra = compraRepository.save(compra);
        log.info("Compra criada com ID: {}", compra.getId());
        
        // Enviar requisição para a fila do RabbitMQ
        TransacaoRequest transacaoRequest = new TransacaoRequest(
            compra.getId(),
            compra.getNumeroCartao(),
            compra.getCvv(),
            compra.getDataValidade(),
            compra.getValor(),
            compra.getEmailCliente(),
            compra.getNomeCliente()
        );
        
        rabbitTemplate.convertAndSend(
            EXCHANGE_TRANSACOES,
            ROUTING_KEY_REQUISICOES,
            transacaoRequest
        );
        
        log.info("Requisição de transação enviada para a fila RabbitMQ");
        
        CompraResponse response = new CompraResponse();
        response.setId(compra.getId());
        response.setStatus(compra.getStatus());
        response.setMensagem("Compra criada e em processamento");
        response.setDataCriacao(compra.getDataCriacao());
        
        return response;
    }
    
    @Transactional
    public void processarRespostaTransacao(TransacaoResponse transacaoResponse) {
        log.info("Processando resposta de transação para compra ID: {}", transacaoResponse.getCompraId());
        
        Compra compra = compraRepository.findById(transacaoResponse.getCompraId())
            .orElseThrow(() -> new RuntimeException("Compra não encontrada: " + transacaoResponse.getCompraId()));
        
        if (transacaoResponse.isSucesso()) {
            compra.setStatus(StatusCompra.CONFIRMADA);
            compra.setDataConfirmacao(LocalDateTime.now());
            compraRepository.save(compra);
            
            log.info("Compra ID {} confirmada, enviando e-mail de confirmação", compra.getId());
            
            // Enviar e-mail de confirmação
            try {
                emailService.enviarEmailConfirmacao(compra);
            } catch (Exception e) {
                log.error("Erro ao enviar e-mail de confirmação: {}", e.getMessage(), e);
                // Não falha a transação se o e-mail falhar
            }
        } else {
            compra.setStatus(StatusCompra.REJEITADA);
            compra.setMensagemErro(transacaoResponse.getMensagem());
            compraRepository.save(compra);
            log.warn("Compra ID {} rejeitada: {}", compra.getId(), transacaoResponse.getMensagem());
        }
    }
    
    public CompraResponse buscarCompraPorId(Long id) {
        Compra compra = compraRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Compra não encontrada: " + id));
        
        CompraResponse response = new CompraResponse();
        response.setId(compra.getId());
        response.setStatus(compra.getStatus());
        response.setMensagem(compra.getStatus() == StatusCompra.CONFIRMADA ? 
                           "Compra confirmada" : 
                           compra.getMensagemErro() != null ? compra.getMensagemErro() : 
                           "Compra em processamento");
        response.setDataCriacao(compra.getDataCriacao());
        response.setDataConfirmacao(compra.getDataConfirmacao());
        
        return response;
    }
    
    public List<CompraResponse> listarCompras() {
        return compraRepository.findAll().stream()
            .map(compra -> {
                CompraResponse response = new CompraResponse();
                response.setId(compra.getId());
                response.setStatus(compra.getStatus());
                response.setMensagem(compra.getStatus() == StatusCompra.CONFIRMADA ? 
                                   "Compra confirmada" : 
                                   compra.getMensagemErro() != null ? compra.getMensagemErro() : 
                                   "Compra em processamento");
                response.setDataCriacao(compra.getDataCriacao());
                response.setDataConfirmacao(compra.getDataConfirmacao());
                return response;
            })
            .collect(Collectors.toList());
    }
}


