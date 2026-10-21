-- Banco de dados do sistema de agendamento da barbearia
-- Execute com: mysql -u root -p < src/database/init.sql
-- ATENÇÃO: o script recria o banco do zero (apaga os dados existentes).

SET NAMES utf8mb4;

DROP DATABASE IF EXISTS agendamento_barbearia;
CREATE DATABASE agendamento_barbearia
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE agendamento_barbearia;

-- Entidade: cliente da barbearia
CREATE TABLE cliente (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    telefone VARCHAR(20) NOT NULL,
    email VARCHAR(100) NULL
);

-- Entidade: barbeiro que atende os clientes
CREATE TABLE barbeiro (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    telefone VARCHAR(20) NOT NULL
);

-- Tabela associativa (N:N): cada agendamento liga um cliente a um barbeiro.
-- ON DELETE RESTRICT impede excluir cliente/barbeiro que ainda possua agendamentos.
-- UNIQUE impede que o mesmo barbeiro tenha dois agendamentos no mesmo horário.
CREATE TABLE agendamento (
    id INT AUTO_INCREMENT PRIMARY KEY,
    cliente_id INT NOT NULL,
    barbeiro_id INT NOT NULL,
    data_hora DATETIME NOT NULL,
    servico VARCHAR(50) NOT NULL,
    valor DECIMAL(10, 2) NOT NULL,
    CONSTRAINT fk_agendamento_cliente FOREIGN KEY (cliente_id) REFERENCES cliente(id)
        ON DELETE RESTRICT,
    CONSTRAINT fk_agendamento_barbeiro FOREIGN KEY (barbeiro_id) REFERENCES barbeiro(id)
        ON DELETE RESTRICT,
    CONSTRAINT uk_barbeiro_horario UNIQUE (barbeiro_id, data_hora)
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

INSERT INTO agendamento (cliente_id, barbeiro_id, data_hora, servico, valor) VALUES
    (1, 1, '2026-10-20 09:00:00', 'Corte', 40.00),
    (2, 2, '2026-10-20 10:00:00', 'Corte + Barba', 60.00),
    (3, 1, '2026-10-20 10:00:00', 'Barba', 30.00),
    (1, 3, '2026-10-22 14:00:00', 'Corte', 40.00),
    (4, 2, '2026-10-23 16:00:00', 'Sobrancelha', 20.00);
