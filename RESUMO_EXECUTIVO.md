# Resumo Executivo - Projeto Banco Tranquilo

## Visão Geral

Sistema de integração com a API do Banco Tranquilo desenvolvido em **Java com Spring Boot**, utilizando **RabbitMQ** como broker de mensagens para processamento assíncrono de transações bancárias, com envio automático de e-mails de confirmação e persistência em **MySQL**.

## Arquitetura

```
Cliente (Postman) 
    ↓
API REST (Spring Boot)
    ↓
Banco de Dados (MySQL) ← Salva compra com status PENDENTE
    ↓
RabbitMQ Queue (transacoes.requisicoes)
    ↓
Listener (TransacaoListener)
    ↓
API Banco Tranquilo (simulada)
    ↓
Processamento da Resposta
    ↓
Atualização no BD (CONFIRMADA/REJEITADA)
    ↓
Envio de E-mail (se confirmada)
```

## Tecnologias Utilizadas

- **Java 17**
- **Spring Boot 3.1.5**
- **Spring Data JPA** - Persistência
- **RabbitMQ** - Message Broker
- **MySQL 8.0** - Banco de Dados
- **Spring Mail** - Envio de E-mails
- **Maven** - Gerenciamento de Dependências
- **Lombok** - Redução de Boilerplate

## Componentes Principais

### 1. Controller (`CompraController`)
- `POST /api/compras` - Criar compra
- `GET /api/compras/{id}` - Buscar compra por ID
- `GET /api/compras` - Listar todas as compras

### 2. Service (`CompraService`)
- Gerencia o ciclo de vida das compras
- Envia mensagens para RabbitMQ
- Processa respostas de transação
- Atualiza status no banco de dados

### 3. Service (`BancoTranquiloService`)
- Integração com API do Banco Tranquilo
- Simulação de processamento de transações
- Retorna resposta de sucesso ou falha

### 4. Service (`EmailService`)
- Envio de e-mails de confirmação
- Template de e-mail personalizado

### 5. Listener (`TransacaoListener`)
- Consome mensagens da fila RabbitMQ
- Processa transações de forma assíncrona
- Trata erros e exceções

### 6. Config (`RabbitMQConfig`)
- Configuração de Exchange (Direct)
- Configuração de Queues
- Configuração de Bindings
- Conversor JSON para mensagens

## Configuração RabbitMQ

### Exchange
- **Nome:** `transacoes.exchange`
- **Tipo:** Direct Exchange

### Queues
- **transacoes.requisicoes** - Fila de requisições
- **transacoes.respostas** - Fila de respostas (configurada)

### Routing Keys
- `transacoes.requisicao` - Para requisições
- `transacoes.resposta` - Para respostas

## Fluxo de Processamento

1. **Cliente cria compra** via POST `/api/compras`
2. **Compra salva** no BD com status `PENDENTE`
3. **Mensagem enviada** para fila `transacoes.requisicoes`
4. **Listener consome** mensagem da fila
5. **API Banco Tranquilo** processa transação (simulada)
6. **Resposta processada:**
   - ✅ **Sucesso:** Status → `CONFIRMADA` + E-mail enviado
   - ❌ **Falha:** Status → `REJEITADA` + Mensagem de erro
7. **Compra atualizada** no banco de dados

## Status de Compras

- `PENDENTE` - Compra criada, aguardando processamento
- `PROCESSANDO` - Em processamento (não utilizado atualmente)
- `CONFIRMADA` - Transação aprovada e e-mail enviado
- `REJEITADA` - Transação rejeitada
- `ERRO` - Erro no processamento

## Configurações Importantes

### Banco de Dados
- **URL:** `jdbc:mysql://localhost:3306/banco_tranquilo_db`
- **Criação automática:** Habilitada via Hibernate
- **Dialeto:** MySQL8Dialect

### RabbitMQ
- **Host:** localhost:5672
- **Dashboard:** http://localhost:15672 (guest/guest)

### E-mail
- **SMTP:** smtp.gmail.com:587
- **Autenticação:** Requerida
- **TLS:** Habilitado

## Como Testar

### 1. Via Postman
- Importar coleção: `postman-collection.json`
- Executar requisição POST para criar compra
- Verificar resposta e status

### 2. Via RabbitMQ Dashboard
- Acessar http://localhost:15672
- Monitorar filas e mensagens
- Publicar mensagem manualmente para teste

### 3. Verificar Banco de Dados
- Consultar tabela `compras`
- Verificar status das transações

## Arquivos de Documentação

- **README.md** - Documentação completa
- **CONFIGURACAO_BD.md** - Configuração do banco de dados
- **GUIA_TESTES.md** - Guia detalhado de testes
- **exemplo-requisicao.json** - Exemplo de requisição JSON
- **postman-collection.json** - Coleção Postman completa

## Dependências Maven Principais

```xml
- spring-boot-starter-web
- spring-boot-starter-data-jpa
- spring-boot-starter-mail
- spring-boot-starter-amqp
- mysql-connector-j
- lombok
- spring-boot-starter-validation
```

## Endpoints da API

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | `/api/compras` | Criar nova compra |
| GET | `/api/compras/{id}` | Buscar compra por ID |
| GET | `/api/compras` | Listar todas as compras |

## Validações Implementadas

- Número do cartão: 13-19 dígitos
- CVV: 3-4 dígitos
- Data de validade: Formato MM/YY
- Valor: Maior que 0.01
- E-mail: Formato válido
- Nome: Não vazio

## Funcionalidades Implementadas

✅ Integração com API do Banco Tranquilo  
✅ Processamento assíncrono com RabbitMQ  
✅ Persistência em banco de dados MySQL  
✅ Envio de e-mail de confirmação  
✅ Validação de dados de entrada  
✅ Tratamento de erros  
✅ Logging detalhado  
✅ Documentação completa  

## Próximos Passos (Melhorias Futuras)

- Implementar fila de respostas para comunicação bidirecional
- Adicionar retry mechanism para falhas
- Implementar circuit breaker para API externa
- Adicionar métricas e monitoramento
- Implementar testes unitários e de integração
- Adicionar autenticação e autorização
- Implementar rate limiting

## Informações para Apresentação

### Demonstração Sugerida

1. **Mostrar estrutura do projeto** no IDE
2. **Executar aplicação** e verificar logs
3. **Acessar RabbitMQ Dashboard** e mostrar configuração
4. **Criar compra via Postman** e mostrar requisição/resposta
5. **Monitorar fila** no RabbitMQ Dashboard
6. **Verificar banco de dados** e mostrar compra criada
7. **Aguardar processamento** e mostrar status atualizado
8. **Verificar e-mail** (se configurado)

### Pontos de Destaque

- Arquitetura assíncrona
- Configuração completa do RabbitMQ
- Integração com banco de dados
- Sistema de e-mail funcional
- Tratamento de erros robusto
- Documentação detalhada

---

**Desenvolvido com Spring Boot e RabbitMQ para processamento de transações bancárias.**

