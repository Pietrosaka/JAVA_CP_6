-- Script de inicialização do banco de dados
-- Banco Tranquilo Integration

-- Criar banco de dados (se não existir)
CREATE DATABASE IF NOT EXISTS banco_tranquilo_db;

-- Usar o banco de dados
USE banco_tranquilo_db;

-- As tabelas serão criadas automaticamente pelo Hibernate
-- Mas você pode criar manualmente se preferir:

CREATE TABLE IF NOT EXISTS compras (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    numero_cartao VARCHAR(19) NOT NULL,
    cvv VARCHAR(4) NOT NULL,
    data_validade VARCHAR(5) NOT NULL,
    valor DECIMAL(10, 2) NOT NULL,
    email_cliente VARCHAR(255) NOT NULL,
    nome_cliente VARCHAR(255) NOT NULL,
    status VARCHAR(20) NOT NULL,
    data_criacao DATETIME NOT NULL,
    data_confirmacao DATETIME,
    mensagem_erro TEXT
);

-- Índices para melhor performance
CREATE INDEX IF NOT EXISTS idx_status ON compras(status);
CREATE INDEX IF NOT EXISTS idx_email_cliente ON compras(email_cliente);
CREATE INDEX IF NOT EXISTS idx_data_criacao ON compras(data_criacao);


