package com.bancotranquilo.service;

import com.bancotranquilo.model.Compra;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailService {
    
    private final JavaMailSender mailSender;
    
    @Value("${spring.mail.username}")
    private String remetente;
    
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }
    
    public void enviarEmailConfirmacao(Compra compra) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(remetente);
            message.setTo(compra.getEmailCliente());
            message.setSubject("Confirmação de Pagamento - Banco Tranquilo");
            message.setText(construirCorpoEmail(compra));
            
            mailSender.send(message);
            log.info("E-mail de confirmação enviado com sucesso para: {}", compra.getEmailCliente());
            
        } catch (Exception e) {
            log.error("Erro ao enviar e-mail de confirmação para {}: {}", 
                     compra.getEmailCliente(), e.getMessage(), e);
            throw new RuntimeException("Erro ao enviar e-mail: " + e.getMessage(), e);
        }
    }
    
    private String construirCorpoEmail(Compra compra) {
        return String.format(
            "Olá %s,\n\n" +
            "Sua compra foi confirmada com sucesso!\n\n" +
            "Detalhes da transação:\n" +
            "ID da Compra: %d\n" +
            "Valor: R$ %.2f\n" +
            "Data de Confirmação: %s\n\n" +
            "Agradecemos pela preferência!\n\n" +
            "Atenciosamente,\n" +
            "Banco Tranquilo",
            compra.getNomeCliente(),
            compra.getId(),
            compra.getValor(),
            compra.getDataConfirmacao() != null ? compra.getDataConfirmacao().toString() : "N/A"
        );
    }
}


