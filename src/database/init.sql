-- DDL para criação do banco de dados do sistema de agendamento da barbearia
-- Execute com: mysql -u root -p < src/database/init.sql

-- garante que os acentos do script sejam lidos corretamente, independente do cliente
SET NAMES utf8mb4;

CREATE DATABASE IF NOT EXISTS agendamento_barbearia
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE agendamento_barbearia;

-- Entidade: cliente da barbearia
CREATE TABLE IF NOT EXISTS cliente (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(256) NOT NULL,
    telefone VARCHAR(20) NOT NULL,
    email VARCHAR(256) NULL,
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Entidade: barbeiro que atende os clientes
CREATE TABLE IF NOT EXISTS barbeiro (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(256) NOT NULL,
    telefone VARCHAR(20) NOT NULL,
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Relação (N:N) entre cliente e barbeiro: cada agendamento liga um cliente a um barbeiro
-- ON DELETE RESTRICT impede excluir cliente/barbeiro que ainda possuam agendamentos
CREATE TABLE IF NOT EXISTS agendamento (
    id INT AUTO_INCREMENT PRIMARY KEY,
    cliente_id INT NOT NULL,
    barbeiro_id INT NOT NULL,
    inicio DATETIME NOT NULL,
    fim DATETIME NOT NULL,
    servico VARCHAR(100) NOT NULL,
    valor DECIMAL(10, 2) NOT NULL,
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_agendamento_cliente FOREIGN KEY (cliente_id) REFERENCES cliente(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_agendamento_barbeiro FOREIGN KEY (barbeiro_id) REFERENCES barbeiro(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT ck_agendamento_periodo CHECK (fim > inicio),
    CONSTRAINT ck_agendamento_valor CHECK (valor >= 0)
);

-- Dados de exemplo
INSERT INTO cliente (nome, telefone, email) VALUES
    ('João Silva', '(11) 98765-4321', 'joao.silva@email.com'),
    ('Maria Oliveira', '(11) 91234-5678', 'maria.oliveira@email.com'),
    ('Pedro Santos', '(21) 99876-1234', NULL),
    ('Ana Costa', '(31) 3456-7890', 'ana.costa@email.com');

INSERT INTO barbeiro (nome, telefone) VALUES
    ('Carlos Navalha', '(11) 97777-1111'),
    ('Rafael Tesoura', '(11) 96666-2222'),
    ('Bruno Degradê', '(11) 95555-3333');

INSERT INTO agendamento (cliente_id, barbeiro_id, inicio, fim, servico, valor) VALUES
    (1, 1, '2026-10-20 09:00:00', '2026-10-20 09:40:00', 'Corte', 45.00),
    (2, 2, '2026-10-20 10:00:00', '2026-10-20 11:00:00', 'Corte + Barba', 70.00),
    (3, 1, '2026-10-20 10:00:00', '2026-10-20 10:30:00', 'Barba', 30.00),
    (1, 3, '2026-10-22 14:00:00', '2026-10-22 14:40:00', 'Corte', 45.00),
    (4, 2, '2026-10-23 16:00:00', '2026-10-23 16:20:00', 'Sobrancelha', 20.00);
