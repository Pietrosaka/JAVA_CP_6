# Guia de Testes - Banco Tranquilo API

Este guia fornece instruções detalhadas para testar a aplicação Banco Tranquilo usando Postman e o RabbitMQ Dashboard.

## Pré-requisitos

1. **Aplicação Spring Boot rodando** na porta 8080
2. **RabbitMQ rodando** na porta 5672 (Dashboard na porta 15672)
3. **MySQL rodando** na porta 3306
4. **Postman instalado** (ou qualquer cliente HTTP)

## 1. Testando via Postman

### Importar a Coleção

1. Abra o Postman
2. Clique em "Import"
3. Selecione o arquivo `postman-collection.json`
4. A coleção "Banco Tranquilo API" será importada

### 1.1. Criar uma Compra

**Endpoint:** `POST http://localhost:8080/api/compras`

**Headers:**
```
Content-Type: application/json
```

**Body (JSON):**
```json
{
  "numeroCartao": "1234567890123456",
  "cvv": "123",
  "dataValidade": "12/25",
  "valor": 100.50,
  "emailCliente": "cliente@example.com",
  "nomeCliente": "João Silva"
}
```

**Resposta Esperada (201 Created):**
```json
{
  "id": 1,
  "status": "PENDENTE",
  "mensagem": "Compra criada e em processamento",
  "dataCriacao": "2024-01-15T10:30:00"
}
```

**O que acontece:**
1. A compra é salva no banco de dados com status `PENDENTE`
2. Uma mensagem é enviada para a fila RabbitMQ `transacoes.requisicoes`
3. O listener processa a mensagem e chama a API do Banco Tranquilo
4. Se aprovada, o status muda para `CONFIRMADA` e um e-mail é enviado

### 1.2. Buscar Compra por ID

**Endpoint:** `GET http://localhost:8080/api/compras/{id}`

**Exemplo:** `GET http://localhost:8080/api/compras/1`

**Resposta Esperada (200 OK):**
```json
{
  "id": 1,
  "status": "CONFIRMADA",
  "mensagem": "Compra confirmada",
  "dataCriacao": "2024-01-15T10:30:00",
  "dataConfirmacao": "2024-01-15T10:30:05"
}
```

### 1.3. Listar Todas as Compras

**Endpoint:** `GET http://localhost:8080/api/compras`

**Resposta Esperada (200 OK):**
```json
[
  {
    "id": 1,
    "status": "CONFIRMADA",
    "mensagem": "Compra confirmada",
    "dataCriacao": "2024-01-15T10:30:00",
    "dataConfirmacao": "2024-01-15T10:30:05"
  },
  {
    "id": 2,
    "status": "REJEITADA",
    "mensagem": "Transação rejeitada: Saldo insuficiente ou cartão inválido",
    "dataCriacao": "2024-01-15T10:31:00"
  }
]
```

## 2. Testando via RabbitMQ Dashboard

### 2.1. Acessar o Dashboard

1. Abra o navegador e acesse: http://localhost:15672
2. Faça login com:
   - **Usuário:** guest
   - **Senha:** guest

### 2.2. Verificar Exchange

1. Clique na aba **"Exchanges"**
2. Procure por `transacoes.exchange`
3. Verifique os bindings:
   - `transacoes.requisicoes` → routing key: `transacoes.requisicao`
   - `transacoes.respostas` → routing key: `transacoes.resposta`

### 2.3. Monitorar Queues

1. Clique na aba **"Queues"**
2. Procure por `transacoes.requisicoes`
3. Clique na queue para ver detalhes:
   - **Messages:** Número de mensagens na fila
   - **Message rate:** Taxa de mensagens por segundo
   - **Consumers:** Número de consumidores (deve ser 1 quando a aplicação estiver rodando)

### 2.4. Publicar Mensagem Manualmente

1. Na queue `transacoes.requisicoes`, role até a seção **"Publish message"**
2. No campo **"Payload"**, cole o seguinte JSON:

```json
{
  "compraId": 1,
  "numeroCartao": "9876543210987654",
  "cvv": "456",
  "dataValidade": "06/26",
  "valor": 250.75,
  "emailCliente": "teste@example.com",
  "nomeCliente": "Maria Santos"
}
```

3. Clique em **"Publish message"**
4. A mensagem será consumida pelo listener da aplicação
5. Verifique os logs da aplicação para ver o processamento

### 2.5. Verificar Mensagens Processadas

1. Na aba **"Queues"**, clique em `transacoes.requisicoes`
2. Role até **"Get messages"**
3. Clique em **"Get Message(s)"** para ver mensagens não consumidas (se houver)

## 3. Fluxo Completo de Teste

### Passo a Passo:

1. **Inicie a aplicação Spring Boot**
   ```bash
   mvn spring-boot:run
   ```

2. **Verifique se o RabbitMQ está rodando**
   - Acesse http://localhost:15672
   - Verifique se as queues foram criadas

3. **Crie uma compra via Postman**
   - Use o endpoint `POST /api/compras`
   - Anote o ID retornado

4. **Monitore o RabbitMQ Dashboard**
   - Veja a mensagem sendo consumida da fila
   - Verifique a taxa de processamento

5. **Verifique os logs da aplicação**
   - Deve aparecer: "Processando requisição de transação..."
   - Deve aparecer: "Transação processada com sucesso..."

6. **Busque a compra novamente**
   - Use `GET /api/compras/{id}`
   - Verifique se o status mudou para `CONFIRMADA` ou `REJEITADA`

7. **Verifique o e-mail** (se configurado)
   - Se a compra foi confirmada, um e-mail deve ser enviado

## 4. Exemplos de Requisições com Diferentes Valores

### Compra com Valor Alto
```json
{
  "numeroCartao": "4111111111111111",
  "cvv": "123",
  "dataValidade": "12/25",
  "valor": 1000.00,
  "emailCliente": "cliente.premium@example.com",
  "nomeCliente": "Carlos Oliveira"
}
```

### Compra com Valor Baixo
```json
{
  "numeroCartao": "5555555555554444",
  "cvv": "456",
  "dataValidade": "03/26",
  "valor": 15.99,
  "emailCliente": "cliente.basico@example.com",
  "nomeCliente": "Ana Costa"
}
```

## 5. Validações de Erro

### Teste de Validação - Número de Cartão Inválido
```json
{
  "numeroCartao": "123",  // Muito curto
  "cvv": "123",
  "dataValidade": "12/25",
  "valor": 100.50,
  "emailCliente": "cliente@example.com",
  "nomeCliente": "João Silva"
}
```

**Resposta Esperada (400 Bad Request):**
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Número do cartão deve ter entre 13 e 19 dígitos"
}
```

### Teste de Validação - Email Inválido
```json
{
  "numeroCartao": "1234567890123456",
  "cvv": "123",
  "dataValidade": "12/25",
  "valor": 100.50,
  "emailCliente": "email-invalido",  // Email inválido
  "nomeCliente": "João Silva"
}
```

## 6. Verificando o Banco de Dados

### Consulta SQL para Ver Compras

```sql
SELECT * FROM compras ORDER BY data_criacao DESC;
```

### Consulta por Status

```sql
SELECT * FROM compras WHERE status = 'CONFIRMADA';
```

### Consulta por Cliente

```sql
SELECT * FROM compras WHERE email_cliente = 'cliente@example.com';
```

## 7. Troubleshooting

### Problema: Mensagens não estão sendo consumidas

**Solução:**
1. Verifique se a aplicação está rodando
2. Verifique os logs da aplicação
3. Verifique se o RabbitMQ está acessível
4. Verifique as configurações no `application.properties`

### Problema: E-mail não está sendo enviado

**Solução:**
1. Verifique as configurações de e-mail no `application.properties`
2. Para Gmail, use uma "Senha de App" ao invés da senha normal
3. Verifique os logs da aplicação para erros específicos

### Problema: Erro de conexão com o banco de dados

**Solução:**
1. Verifique se o MySQL está rodando
2. Verifique as credenciais no `application.properties`
3. Verifique se o banco de dados existe ou se `createDatabaseIfNotExist=true` está configurado

## 8. Métricas e Monitoramento

### RabbitMQ Dashboard - Métricas Importantes

- **Message rate:** Taxa de mensagens processadas por segundo
- **Consumer utilization:** Utilização dos consumidores
- **Queue length:** Tamanho da fila (deve estar próximo de 0 em operação normal)

### Logs da Aplicação

Os logs mostram:
- Criação de compras
- Envio de mensagens para RabbitMQ
- Processamento de transações
- Envio de e-mails
- Erros e exceções

## 9. Testes de Carga (Opcional)

Para testar com múltiplas requisições simultâneas:

1. Use o Postman Runner
2. Configure múltiplas iterações
3. Monitore o RabbitMQ Dashboard
4. Verifique o desempenho do banco de dados

## Conclusão

Este guia cobre os principais cenários de teste. Para mais informações, consulte:
- `README.md` - Documentação geral
- `CONFIGURACAO_BD.md` - Configuração do banco de dados
- `exemplo-requisicao.json` - Exemplo de requisição JSON

