# Barbearia – Sistema de Agendamentos

Sistema desktop em **Java 17 + Swing** para gerenciar os agendamentos de uma barbearia,
com persistência em **MySQL** e arquitetura **MVC + DAO**. Permite o CRUD completo de
clientes, barbeiros e agendamentos.

## Modelagem

```
┌──────────────┐        ┌──────────────────┐        ┌──────────────┐
│   cliente    │ 1    N │   agendamento    │ N    1 │   barbeiro   │
├──────────────┤────────├──────────────────┤────────├──────────────┤
│ id (PK)      │        │ id (PK)          │        │ id (PK)      │
│ nome         │        │ cliente_id (FK)  │        │ nome         │
│ telefone     │        │ barbeiro_id (FK) │        │ telefone     │
│ email        │        │ inicio, fim      │        │ criado_em    │
│ criado_em    │        │ servico, valor   │        └──────────────┘
└──────────────┘        │ criado_em        │
                        └──────────────────┘
```

- **cliente** e **barbeiro** são as entidades; **agendamento** é a tabela associativa
  que representa o relacionamento N:N entre elas (um cliente é atendido por vários
  barbeiros e um barbeiro atende vários clientes).
- Todas as tabelas têm chave primária `id` (AUTO_INCREMENT).
- As chaves estrangeiras usam `ON DELETE RESTRICT`: o banco impede excluir um cliente
  ou barbeiro que ainda tenha agendamentos. A aplicação verifica isso antes e mostra
  uma mensagem explicando o motivo.
- `CHECK (fim > inicio)` e `CHECK (valor >= 0)` reforçam as regras no próprio banco.

Cada tabela tem uma classe equivalente em `model`: `Cliente`, `Barbeiro` e
`Agendamento` (que referencia um `Cliente` e um `Barbeiro`).

## Arquitetura

```
src/main/java/com/jdbc
├── Main.java                 inicia a aplicação e testa a conexão
├── model/                    entidades (Cliente, Barbeiro, Agendamento)
├── dao/                      acesso a dados (JDBC)
│   ├── ConnectionFactory     cria conexões a partir de db.properties
│   ├── DAO<T>                interface genérica do CRUD
│   ├── ClienteDAO, BarbeiroDAO, AgendamentoDAO
│   ├── Criterio              critério de ordenação (campo + direção)
│   └── DAOException
├── controller/               regras de negócio e validação das entradas
│   ├── ClienteController, BarbeiroController, AgendamentoController
│   ├── Validador             converte/valida nome, telefone, e-mail, datas, horas, valores
│   └── ValidacaoException
└── view/                     interface gráfica Swing
    ├── MainFrame             janela com abas
    ├── CrudPanel<T>          tela base: busca, ordenação, tabela e botões
    ├── ClientePanel, BarbeiroPanel, AgendamentoPanel
    ├── FormularioDialog      diálogo base de cadastro/edição
    └── ClienteFormDialog, BarbeiroFormDialog, AgendamentoFormDialog
```

Fluxo: a **View** coleta o texto digitado e chama o **Controller**, que valida,
converte para objetos do **Model** e chama o **DAO**, único responsável pelo SQL.
Erros chegam à View como `ValidacaoException` (entrada inválida → aviso) ou
`DAOException` (falha no banco → erro), sempre com mensagem amigável.

## Funcionalidades (requisitos atendidos)

| Requisito | Onde |
|---|---|
| Listar todos os dados | Cada aba lista todos os registros ao abrir (botão *Atualizar lista*) |
| Ordenar por mais de um critério | "Ordenar por / depois por / depois por", cada um crescente ou decrescente (ex.: Barbeiro ↑, Valor ↓) |
| Filtrar por busca | Campo *Buscar* (nome, telefone, e-mail, serviço) e, em agendamentos, período *De/Até* |
| Adicionar | Botão *Novo* nas três abas |
| Modificar | Botão *Editar* ou duplo clique (ex.: telefone do cliente, horário ou valor do agendamento) |
| Excluir mantendo integridade | Botão *Excluir*; bloqueado para cliente/barbeiro com agendamentos |
| Validação e tratamento de erros | Campos obrigatórios, nome só com letras, telefone com DDD (normalizado para `(11) 98765-4321`), e-mail, datas existentes, término após início, valor numérico não negativo, conflito de horário do barbeiro, falha de conexão |

A ordenação é feita no SQL (`ORDER BY`) somente com campos de uma lista permitida,
e todos os valores digitados vão por `PreparedStatement`, evitando injeção de SQL.

## Como executar

Pré-requisitos: JDK 17+, Maven e MySQL 8.

1. Crie o banco (com dados de exemplo):
   ```bash
   mysql -u root -p < src/database/init.sql
   ```
2. Ajuste usuário e senha em `src/main/resources/db.properties`
   (ou crie um `db.properties` na pasta de onde o programa é executado, ou use
   `-Ddb.usuario=... -Ddb.senha=...`).
3. Compile e execute:
   ```bash
   mvn package
   java -jar target/demo-1.0-SNAPSHOT.jar
   ```
   ou, direto pelo Maven: `mvn compile exec:java`.
   Também é possível abrir o projeto no IntelliJ/Eclipse/NetBeans e executar `com.jdbc.Main`.
