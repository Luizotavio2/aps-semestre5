-- Banco de dados do sistema de agendamento da barbearia
-- Execute com: mysql -u root -p < src/database/init.sql
-- ATENÇÃO: o script apaga e recria o banco.

DROP DATABASE IF EXISTS agendamento_barbearia;
CREATE DATABASE agendamento_barbearia;
USE agendamento_barbearia;

-- Entidade 1
CREATE TABLE cliente (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    telefone VARCHAR(20) NOT NULL
);

-- Entidade 2
CREATE TABLE barbeiro (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    telefone VARCHAR(20) NOT NULL
);

-- Tabela associativa: liga um cliente a um barbeiro em um horário
CREATE TABLE agendamento (
    id INT AUTO_INCREMENT PRIMARY KEY,
    cliente_id INT NOT NULL,
    barbeiro_id INT NOT NULL,
    data_hora DATETIME NOT NULL,
    servico VARCHAR(50) NOT NULL,
    -- RESTRICT: não deixa excluir cliente/barbeiro que ainda tenha agendamentos
    FOREIGN KEY (cliente_id) REFERENCES cliente(id) ON DELETE RESTRICT,
    FOREIGN KEY (barbeiro_id) REFERENCES barbeiro(id) ON DELETE RESTRICT,
    -- um barbeiro não pode ter dois agendamentos no mesmo horário
    UNIQUE (barbeiro_id, data_hora)
);

-- Dados de exemplo
INSERT INTO barbeiro (nome, telefone) VALUES ('Carlos', '11987654321'), ('Rafael', '11912345678');
INSERT INTO cliente (nome, telefone) VALUES ('João', '19998887766');
