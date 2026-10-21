package com.jdbc.controller;

import com.jdbc.dao.ClienteDAO;
import com.jdbc.model.Cliente;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

public class ClienteController {
    private final ClienteDAO dao = new ClienteDAO();

    public List<Cliente> listarTodos() throws SQLException {
        return dao.listarTodos();
    }

    /** Cadastra (id nulo) ou atualiza um cliente a partir dos dados digitados. */
    public Cliente salvar(Integer id, String nome, String telefone, String email) throws SQLException {
        Cliente cliente = new Cliente(id, Validador.nome(nome), Validador.telefone(telefone), Validador.email(email));
        if (id == null) {
            dao.inserir(cliente);
        } else {
            dao.atualizar(cliente);
        }
        return cliente;
    }

    /** O banco (ON DELETE RESTRICT) impede excluir um cliente que ainda tenha agendamentos. */
    public void excluir(Cliente cliente) throws SQLException {
        try {
            dao.excluir(cliente.getId());
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new IllegalArgumentException("Não é possível excluir a conta: cancele seus agendamentos antes.");
        }
    }
}
