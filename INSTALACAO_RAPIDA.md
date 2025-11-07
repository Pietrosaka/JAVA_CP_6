# Instalação Rápida - Banco Tranquilo

## Pré-requisitos

- Java 17 ou superior
- Maven 3.6+
- MySQL 8.0+
- RabbitMQ (ou Docker)

## Passo 1: Instalar RabbitMQ

### Opção A: Usando Docker (Recomendado)

```bash
docker run -d --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:3-management
```

### Opção B: Instalação Manual

Baixe e instale do site oficial: https://www.rabbitmq.com/download.html

## Passo 2: Configurar MySQL

1. Inicie o MySQL
2. Crie o banco de dados (opcional - será criado automaticamente):

```sql
CREATE DATABASE banco_tranquilo_db;
```

3. Configure as credenciais no arquivo `src/main/resources/application.properties`:

```properties
spring.datasource.username=seu_usuario
spring.datasource.password=sua_senha
```

## Passo 3: Configurar E-mail (Opcional)

Edite `src/main/resources/application.properties`:

```properties
spring.mail.username=seu-email@gmail.com
spring.mail.password=sua-senha-app
```

**Nota:** Para Gmail, use uma "Senha de App" ao invés da senha normal.

## Passo 4: Compilar o Projeto

```bash
mvn clean install
```

## Passo 5: Executar a Aplicação

```bash
mvn spring-boot:run
```

A aplicação estará disponível em: **http://localhost:8080**

## Passo 6: Verificar Funcionamento

### 6.1. RabbitMQ Dashboard

Acesse: http://localhost:15672
- Usuário: `guest`
- Senha: `guest`

Verifique se as queues foram criadas:
- `transacoes.requisicoes`
- `transacoes.respostas`

### 6.2. Testar API

Use o Postman ou cURL:

```bash
curl -X POST http://localhost:8080/api/compras \
  -H "Content-Type: application/json" \
  -d '{
    "numeroCartao": "1234567890123456",
    "cvv": "123",
    "dataValidade": "12/25",
    "valor": 100.50,
    "emailCliente": "teste@example.com",
    "nomeCliente": "João Silva"
  }'
```

### 6.3. Verificar Banco de Dados

```sql
SELECT * FROM compras ORDER BY data_criacao DESC;
```

## Troubleshooting Rápido

### Erro: "Connection refused" no RabbitMQ
- Verifique se o RabbitMQ está rodando
- Verifique a porta 5672

### Erro: "Access denied" no MySQL
- Verifique usuário e senha no `application.properties`
- Verifique se o MySQL está rodando

### Erro: "Port 8080 already in use"
- Altere a porta no `application.properties`: `server.port=8081`

### Mensagens não sendo consumidas
- Verifique se a aplicação está rodando
- Verifique os logs da aplicação
- Verifique se o RabbitMQ está acessível

## Estrutura de Arquivos Importantes

```
.
├── pom.xml                          # Dependências Maven
├── src/main/resources/
│   └── application.properties       # Configurações
├── exemplo-requisicao.json          # Exemplo de requisição
├── postman-collection.json          # Coleção Postman
├── README.md                        # Documentação completa
├── CONFIGURACAO_BD.md              # Configuração do BD
├── GUIA_TESTES.md                  # Guia de testes
└── RESUMO_EXECUTIVO.md             # Resumo do projeto
```

## Comandos Úteis

### Parar RabbitMQ (Docker)
```bash
docker stop rabbitmq
docker rm rabbitmq
```

### Ver logs da aplicação
Os logs aparecem no console onde a aplicação foi iniciada.

### Reiniciar aplicação
Pare a aplicação (Ctrl+C) e execute novamente:
```bash
mvn spring-boot:run
```

## Próximos Passos

1. Leia o **README.md** para documentação completa
2. Consulte **GUIA_TESTES.md** para testes detalhados
3. Veja **CONFIGURACAO_BD.md** para configuração do banco
4. Importe **postman-collection.json** no Postman

---

**Pronto! Sua aplicação está configurada e pronta para uso.**

