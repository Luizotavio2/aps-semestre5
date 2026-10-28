package com.jdbc.controller;

import com.jdbc.dao.ClienteDAO;
import com.jdbc.model.Cliente;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

/** Valida os dados digitados na tela e chama o DAO. */
public class ClienteController {
    private final ClienteDAO dao = new ClienteDAO();

    public List<Cliente> listarTodos() throws SQLException {
        return dao.listarTodos();
    }

    /** Cadastra (id = 0) ou atualiza um cliente. */
    public Cliente salvar(int id, String nome, String telefone) throws Exception {
        telefone = telefone.replaceAll("[^0-9]", ""); // deixa só os números
        if (nome.isBlank()) {
            throw new Exception("Informe o nome.");
        }
        if (telefone.length() < 10 || telefone.length() > 11) {
            throw new Exception("Telefone inválido: informe DDD + número.");
        }

        Cliente cliente = new Cliente(id, nome.trim(), telefone);
        if (id == 0) {
            dao.inserir(cliente);
        } else {
            dao.atualizar(cliente);
        }
        return cliente;
    }

    public void excluir(Cliente cliente) throws Exception {
        try {
            dao.excluir(cliente);
        } catch (SQLIntegrityConstraintViolationException e) {
            // a chave estrangeira (ON DELETE RESTRICT) impede excluir quem tem agendamentos
            throw new Exception("Não é possível excluir a conta: cancele seus agendamentos antes.");
        }
    }
}
