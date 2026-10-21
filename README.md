# Barbearia – Sistema de Agendamentos

Sistema desktop em **Java 17 + Swing** para agendar horários em uma barbearia,
com persistência em **MySQL** e arquitetura **MVC + DAO**.

Ao abrir, o sistema pergunta **"Você é cliente ou barbeiro?"**:

- **Cliente**: escolhe seu nome (ou faz um novo cadastro) e vê os seus agendamentos.
  Pode agendar, remarcar e cancelar horários, editar seus dados e excluir a conta.
- **Barbeiro**: escolhe seu nome (ou faz um novo cadastro) e vê a agenda da barbearia
  (de todos ou só a sua), com busca e ordenação por dois critérios. Pode cancelar
  agendamentos, editar seus dados e excluir a conta.

## Modelagem

```
┌──────────────┐        ┌──────────────────┐        ┌──────────────┐
│   cliente    │ 1    N │   agendamento    │ N    1 │   barbeiro   │
├──────────────┤────────├──────────────────┤────────├──────────────┤
│ id (PK)      │        │ id (PK)          │        │ id (PK)      │
│ nome         │        │ cliente_id (FK)  │        │ nome         │
│ telefone     │        │ barbeiro_id (FK) │        │ telefone     │
│ email        │        │ data_hora        │        └──────────────┘
└──────────────┘        │ servico, valor   │
                        └──────────────────┘
```

- **cliente** e **barbeiro** são as entidades; **agendamento** é a tabela associativa
  (relacionamento N:N entre elas).
- Todas as tabelas têm chave primária `id` (AUTO_INCREMENT).
- As chaves estrangeiras usam `ON DELETE RESTRICT`: o banco impede excluir um cliente
  ou barbeiro que ainda tenha agendamentos, e o sistema mostra uma mensagem explicando.
- `UNIQUE (barbeiro_id, data_hora)` impede marcar dois clientes no mesmo horário
  com o mesmo barbeiro.

Cada tabela tem uma classe equivalente em `model`: `Cliente`, `Barbeiro` e
`Agendamento` (que referencia um `Cliente` e um `Barbeiro`).

## Arquitetura

```
src/main/java/com/jdbc
├── Main.java            testa a conexão e abre a tela inicial
├── model/               Cliente, Barbeiro, Agendamento
├── dao/                 ConnectionFactory e um DAO por tabela (todo o SQL fica aqui)
├── controller/          um controller por entidade + Validador (valida as entradas)
└── view/
    ├── TelaInicial      "Você é cliente ou barbeiro?"
    ├── TelaCliente      área do cliente (agendar, remarcar, cancelar)
    ├── TelaBarbeiro     agenda com busca e ordenação
    └── Telas            mensagens, formulários e tabelas comuns às telas
```

Fluxo: a **View** coleta os dados digitados e chama o **Controller**, que valida,
monta os objetos do **Model** e chama o **DAO**, único responsável pelo SQL.
Entrada inválida gera `IllegalArgumentException` (mostrada como aviso) e falha no
banco gera `SQLException` (mostrada como erro).

## Requisitos atendidos

| Requisito | Onde |
|---|---|
| Listar todos os dados | Área do barbeiro: agenda com todos os agendamentos |
| Ordenar por mais de um critério | Área do barbeiro: "Ordenar por" + "Depois por", cada um crescente ou decrescente |
| Filtrar por busca | Área do barbeiro: busca por cliente, barbeiro ou serviço; opção "Somente os meus" |
| Adicionar | Novo cadastro (cliente/barbeiro) e "Novo agendamento" |
| Modificar | "Meus dados" (nome, telefone, e-mail) e "Remarcar" (barbeiro, serviço, data/hora) |
| Excluir mantendo integridade | "Cancelar agendamento"; "Excluir conta" é bloqueado se houver agendamentos |
| Validação e tratamento de erros | Nome, telefone com DDD, e-mail, data/hora existente e futura, horário ocupado, falha de conexão |

## Como executar

Pré-requisitos: JDK 17+, Maven e MySQL 8.

1. Crie o banco (com dados de exemplo). **Atenção:** o script apaga e recria o banco:
   ```bash
   mysql -u root -p < src/database/init.sql
   ```
2. Ajuste usuário e senha em `src/main/resources/db.properties`
3. Compile e execute:
   ```bash
   mvn package
   java -jar target/demo-1.0-SNAPSHOT.jar
   ```
   ou, direto pelo Maven: `mvn compile exec:java`.
   Também é possível abrir o projeto no IntelliJ/Eclipse/NetBeans e executar `com.jdbc.Main`.
