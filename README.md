# Banco Tranquilo - Integração com API

Sistema de integração com a API do Banco Tranquilo utilizando Spring Boot, RabbitMQ e envio de e-mails.

## Tecnologias Utilizadas

- **Spring Boot 3.1.5**
- **RabbitMQ** - Broker de mensagens
- **MySQL** - Banco de dados
- **Spring Mail** - Envio de e-mails
- **Spring Data JPA** - Persistência de dados
- **Lombok** - Redução de boilerplate

## Estrutura do Projeto

```
src/main/java/com/bancotranquilo/
├── BancoTranquiloApplication.java    # Classe principal
├── config/
│   ├── RabbitMQConfig.java          # Configuração do RabbitMQ
│   └── RestTemplateConfig.java      # Configuração do RestTemplate
├── controller/
│   └── CompraController.java        # Endpoints REST
├── listener/
│   └── TransacaoListener.java       # Listener do RabbitMQ
├── model/
│   ├── Compra.java                  # Entidade JPA
│   ├── StatusCompra.java            # Enum de status
│   └── dto/
│       ├── CompraRequest.java       # DTO de requisição
│       ├── CompraResponse.java      # DTO de resposta
│       ├── TransacaoRequest.java    # DTO de transação
│       └── TransacaoResponse.java   # DTO de resposta da transação
├── repository/
│   └── CompraRepository.java        # Repositório JPA
└── service/
    ├── BancoTranquiloService.java   # Serviço de integração com API
    ├── CompraService.java           # Serviço de compras
    └── EmailService.java            # Serviço de e-mail
```

## Configuração

### 1. Banco de Dados MySQL

Crie um banco de dados MySQL:

```sql
CREATE DATABASE banco_tranquilo_db;
```

Configure as credenciais no arquivo `application.properties`:

```properties
spring.datasource.username=root
spring.datasource.password=sua_senha
```

### 2. RabbitMQ

Instale e inicie o RabbitMQ:

```bash
# Windows (com Docker)
docker run -d --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:3-management

# Ou instale diretamente
# https://www.rabbitmq.com/download.html
```

Acesse o painel de gerenciamento em: http://localhost:15672 (guest/guest)

### 3. Configuração de E-mail

Configure suas credenciais de e-mail no `application.properties`:

```properties
spring.mail.username=seu-email@gmail.com
spring.mail.password=sua-senha-app
```

**Nota:** Para Gmail, você precisará usar uma "Senha de App" ao invés da senha normal.

## Como Executar

### 1. Clonar/Baixar o projeto

### 2. Configurar dependências

```bash
mvn clean install
```

### 3. Executar a aplicação

```bash
mvn spring-boot:run
```

A aplicação estará disponível em: `http://localhost:8080`

## Endpoints da API

### Criar Compra

```http
POST /api/compras
Content-Type: application/json

{
  "numeroCartao": "1234567890123456",
  "cvv": "123",
  "dataValidade": "12/25",
  "valor": 100.50,
  "emailCliente": "cliente@example.com",
  "nomeCliente": "João Silva"
}
```

### Buscar Compra por ID

```http
GET /api/compras/{id}
```

### Listar Todas as Compras

```http
GET /api/compras
```

## Fluxo de Funcionamento

1. **Cliente cria uma compra** através do endpoint POST `/api/compras`
2. **Compra é salva** no banco de dados com status `PENDENTE`
3. **Requisição é enviada** para a fila RabbitMQ `transacoes.requisicoes`
4. **Listener processa** a requisição e chama a API do Banco Tranquilo
5. **Resposta é processada**:
   - Se **confirmada**: Status muda para `CONFIRMADA` e e-mail é enviado
   - Se **rejeitada**: Status muda para `REJEITADA` com mensagem de erro
6. **Compra atualizada** no banco de dados

## Exchanges e Queues do RabbitMQ

- **Exchange**: `transacoes.exchange` (Direct Exchange)
- **Queue de Requisições**: `transacoes.requisicoes`
- **Routing Key Requisições**: `transacoes.requisicao`
- **Routing Key Respostas**: `transacoes.resposta`

## Status de Compras

- `PENDENTE` - Compra criada, aguardando processamento
- `PROCESSANDO` - Em processamento
- `CONFIRMADA` - Transação aprovada e e-mail enviado
- `REJEITADA` - Transação rejeitada
- `ERRO` - Erro no processamento

## Observações

- A API do Banco Tranquilo está simulada. Em produção, configure a URL real no `application.properties`
- O serviço de e-mail está configurado para Gmail. Para outros provedores, ajuste as configurações
- O sistema usa Hibernate para criar/atualizar as tabelas automaticamente

## RabbitMQ Dashboard

### Acessando o Dashboard

1. Inicie o RabbitMQ (se estiver usando Docker):
   ```bash
   docker run -d --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:3-management
   ```

2. Acesse o Dashboard em: http://localhost:15672
   - **Usuário:** guest
   - **Senha:** guest

### Monitorando as Filas

No Dashboard do RabbitMQ, você pode:

1. **Verificar as Queues criadas:**
   - Navegue até a aba "Queues"
   - Procure por:
     - `transacoes.requisicoes` - Fila de requisições de transação
     - `transacoes.respostas` - Fila de respostas (se configurada)

2. **Monitorar mensagens:**
   - Clique em uma queue para ver detalhes
   - Veja mensagens sendo consumidas em tempo real
   - Verifique a taxa de mensagens por segundo

3. **Verificar o Exchange:**
   - Navegue até a aba "Exchanges"
   - Procure por `transacoes.exchange`
   - Veja os bindings com as queues

4. **Testar envio de mensagens:**
   - Use a aba "Publish message" para enviar mensagens manualmente
   - Útil para testes e depuração

### Exemplo de Mensagem para Teste no RabbitMQ

No Dashboard, você pode publicar uma mensagem diretamente na queue `transacoes.requisicoes`:

```json
{
  "compraId": 1,
  "numeroCartao": "1234567890123456",
  "cvv": "123",
  "dataValidade": "12/25",
  "valor": 100.50,
  "emailCliente": "teste@example.com",
  "nomeCliente": "Teste Usuario"
}
```

## Configuração do Banco de Dados

Para informações detalhadas sobre a configuração do banco de dados, consulte o arquivo **CONFIGURACAO_BD.md**.

## Testando a Aplicação

### Exemplo com cURL:

```bash
curl -X POST http://localhost:8080/api/compras \
  -H "Content-Type: application/json" \
  -d '{
    "numeroCartao": "1234567890123456",
    "cvv": "123",
    "dataValidade": "12/25",
    "valor": 100.50,
    "emailCliente": "teste@example.com",
    "nomeCliente": "Teste Usuario"
  }'
```

## Desenvolvido com

- Java 17
- Spring Boot 3.1.5
- Maven
- IntelliJ IDEA


