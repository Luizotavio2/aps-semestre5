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

CREATE TABLE barbeiro (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    telefone VARCHAR(20) NOT NULL
);

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