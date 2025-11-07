# Configuração do Banco de Dados - Banco Tranquilo

Este documento contém as informações de configuração do banco de dados para o projeto de integração com a API do Banco Tranquilo.

## Banco de Dados Utilizado

**MySQL 8.0** (ou superior)

## Configurações de Conexão

### Arquivo: `src/main/resources/application.properties`

```properties
# Configurações do Banco de Dados MySQL
spring.datasource.url=jdbc:mysql://localhost:3306/banco_tranquilo_db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=root
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# Configurações JPA
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
spring.jpa.properties.hibernate.format_sql=true
```

## Criação do Banco de Dados

### Opção 1: Criação Automática (Recomendado)

O banco de dados será criado automaticamente pelo Hibernate quando a aplicação iniciar, devido à propriedade `createDatabaseIfNotExist=true` na URL de conexão.

### Opção 2: Criação Manual

Execute o seguinte comando SQL no MySQL:

```sql
CREATE DATABASE banco_tranquilo_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

## Estrutura da Tabela

A tabela `compras` será criada automaticamente pelo Hibernate. A estrutura é a seguinte:

```sql
CREATE TABLE compras (
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
```

### Índices Criados

- `idx_status` - Para consultas por status
- `idx_email_cliente` - Para consultas por e-mail do cliente
- `idx_data_criacao` - Para consultas por data de criação

## Configuração de Credenciais

**IMPORTANTE:** Altere as credenciais no arquivo `application.properties` conforme seu ambiente:

- `spring.datasource.username` - Nome de usuário do MySQL
- `spring.datasource.password` - Senha do MySQL

## Configurações Alternativas para Outros Bancos de Dados

### PostgreSQL

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/banco_tranquilo_db
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.datasource.driver-class-name=org.postgresql.Driver
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
```

### Microsoft SQL Server

```properties
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=banco_tranquilo_db;encrypt=false
spring.datasource.username=sa
spring.datasource.password=SuaSenha123
spring.datasource.driver-class-name=com.microsoft.sqlserver.jdbc.SQLServerDriver
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.SQLServerDialect
```

### Oracle Database

```properties
spring.datasource.url=jdbc:oracle:thin:@localhost:1521:XE
spring.datasource.username=SYSTEM
spring.datasource.password=oracle
spring.datasource.driver-class-name=oracle.jdbc.OracleDriver
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.OracleDialect
```

**Nota:** Para usar Oracle ou SQL Server, será necessário adicionar as dependências correspondentes no `pom.xml`.

## Script de Inicialização

O arquivo `database/init.sql` contém um script SQL opcional para criação manual do banco e tabelas, caso prefira não usar a criação automática do Hibernate.

## Verificação da Conexão

Para verificar se a conexão está funcionando:

1. Inicie a aplicação Spring Boot
2. Verifique os logs - deve aparecer: `HikariPool-1 - Starting...`
3. Se houver erro de conexão, verifique:
   - MySQL está rodando
   - Credenciais estão corretas
   - Porta 3306 está acessível
   - Banco de dados existe (ou `createDatabaseIfNotExist=true` está configurado)

## Dependências Maven

As seguintes dependências estão configuradas no `pom.xml`:

```xml
<!-- MySQL Driver -->
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <scope>runtime</scope>
</dependency>

<!-- PostgreSQL Driver (alternativa) -->
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
</dependency>
```

## Informações para o Professor

**Banco de Dados:** MySQL 8.0  
**Nome do Banco:** `banco_tranquilo_db`  
**Porta:** 3306 (padrão)  
**Usuário Padrão:** root  
**Senha Padrão:** root (deve ser alterada no `application.properties`)  
**Driver:** `com.mysql.cj.jdbc.Driver`  
**Dialeto Hibernate:** `org.hibernate.dialect.MySQL8Dialect`  

**Observação:** As credenciais devem ser ajustadas no arquivo `src/main/resources/application.properties` conforme o ambiente de instalação do MySQL.

