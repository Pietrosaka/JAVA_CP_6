package com.bancotranquilo.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    
    public static final String EXCHANGE_TRANSACOES = "transacoes.exchange";
    public static final String QUEUE_REQUISICOES = "transacoes.requisicoes";
    public static final String QUEUE_RESPOSTAS = "transacoes.respostas";
    public static final String ROUTING_KEY_REQUISICOES = "transacoes.requisicao";
    public static final String ROUTING_KEY_RESPOSTAS = "transacoes.resposta";
    
    @Bean
    public DirectExchange transacoesExchange() {
        return new DirectExchange(EXCHANGE_TRANSACOES);
    }
    
    @Bean
    public Queue requisicoesQueue() {
        return QueueBuilder.durable(QUEUE_REQUISICOES).build();
    }
    
    @Bean
    public Queue respostasQueue() {
        return QueueBuilder.durable(QUEUE_RESPOSTAS).build();
    }
    
    @Bean
    public Binding requisicoesBinding() {
        return BindingBuilder
                .bind(requisicoesQueue())
                .to(transacoesExchange())
                .with(ROUTING_KEY_REQUISICOES);
    }
    
    @Bean
    public Binding respostasBinding() {
        return BindingBuilder
                .bind(respostasQueue())
                .to(transacoesExchange())
                .with(ROUTING_KEY_RESPOSTAS);
    }
    
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
    
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}


