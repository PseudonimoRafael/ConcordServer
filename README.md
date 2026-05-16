# ConcordServer
Concord é uma aplicação de mensagem criada como um projeto para a cadeira de redes. Na UABJ/UFRPE.
O nome faz referência ao modelo de avião comercial supersônico (Concorde) e ao aplicativo de mensagem Discord.

## Pré-requisitos

- Java 17 ou superior
- Maven 3.8 ou superior

## Como compilar

```bash
mvn compile
```

## Como executar

```bash
mvn exec:java -Dexec.mainClass="server.Main"
```

O servidor iniciará na porta 8080.

## Funcionalidades

- Registro e autenticação de usuários
- Roteamento de mensagens em tempo real
- Armazenamento de mensagens para usuários offline
- Notificação de status online/offline
- Indicador de digitando
- Histórico de conversas persistente em SQLite

## Estrutura do projeto

```
src/main/java/
├── database/       — conexão e criação do banco de dados
├── handler/        — thread de atendimento por cliente
├── models/         — entidades User e Message
├── protocol/       — Packet e PacketType
├── repository/     — acesso ao banco de dados
├── server/         — inicialização do servidor
└── service/        — regras de negócio
```
