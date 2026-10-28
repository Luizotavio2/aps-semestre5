# Barbearia – Sistema de Agendamentos

Sistema desktop em **Java + Swing** para agendar horários em uma barbearia,
com banco de dados **MySQL** e arquitetura **MVC + DAO**.

Ao abrir, o sistema pergunta **"Você é cliente ou barbeiro?"**:

- **Cliente**: escolhe seu nome (ou se cadastra) e vê os seus agendamentos.
  Pode agendar, remarcar, cancelar, editar seus dados e excluir a conta.
- **Barbeiro**: escolhe seu nome (ou se cadastra) e vê a agenda completa,
  com busca e ordenação por dois critérios. Pode cancelar agendamentos.

## Banco de dados

```
cliente (id PK, nome, telefone)
barbeiro (id PK, nome, telefone)
agendamento (id PK, cliente_id FK, barbeiro_id FK, data_hora, servico)
```

- `cliente` e `barbeiro` são as entidades; `agendamento` é a tabela associativa entre elas.
- As chaves estrangeiras usam `ON DELETE RESTRICT`: não é possível excluir um cliente
  que ainda tenha agendamentos (o sistema mostra uma mensagem explicando).
- `UNIQUE (barbeiro_id, data_hora)` impede dois agendamentos com o mesmo barbeiro no mesmo horário.

## Organização do código (MVC + DAO)

```
src/main/java/com/jdbc
├── Main.java        testa a conexão e abre a tela inicial
├── model/           Cliente, Barbeiro, Agendamento (uma classe por tabela)
├── dao/             ConnectionFactory + um DAO por tabela (todo o SQL fica aqui)
├── controller/      valida o que foi digitado e chama o DAO
└── view/            TelaInicial, TelaCliente, TelaBarbeiro (Swing)
```

Fluxo: a **View** pega o que o usuário digitou e chama o **Controller**, que valida,
cria o objeto do **Model** e chama o **DAO**, que executa o SQL. Se algo estiver errado,
o Controller lança uma `Exception` com a mensagem, e a View mostra essa mensagem na tela.

## Requisitos atendidos

| Requisito | Onde |
|---|---|
| Listar todos os dados | Área do barbeiro: tabela com todos os agendamentos |
| Ordenar por mais de um critério | Área do barbeiro: "Ordenar por" + "e depois por" |
| Filtrar por busca | Área do barbeiro: busca por cliente, barbeiro ou serviço |
| Adicionar | Novo cadastro (cliente/barbeiro) e "Novo agendamento" |
| Modificar | "Meus dados" e "Remarcar" (área do cliente) |
| Excluir mantendo a integridade | "Cancelar agendamento"; "Excluir conta" é bloqueado se houver agendamentos |
| Validação de entradas | Nome vazio, telefone sem DDD, data/hora inválida ou passada, horário ocupado |

## Como executar

Pré-requisitos: JDK 17+, Maven e MySQL 8.

1. Crie o banco (**atenção:** apaga e recria o banco):
   ```bash
   mysql -u root -p < src/database/init.sql
   ```
2. Ajuste usuário e senha em `src/main/java/com/jdbc/dao/ConnectionFactory.java`
3. Execute: `mvn compile exec:java`
   (ou abra o projeto no IntelliJ/Eclipse/NetBeans e rode `com.jdbc.Main`).
