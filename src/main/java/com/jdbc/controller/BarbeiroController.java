package com.jdbc.controller;

import com.jdbc.dao.BarbeiroDAO;
import com.jdbc.model.Barbeiro;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

public class BarbeiroController {
    private final BarbeiroDAO dao = new BarbeiroDAO();

    public List<Barbeiro> listarTodos() throws SQLException {
        return dao.listarTodos();
    }

    /** Cadastra (id nulo) ou atualiza um barbeiro a partir dos dados digitados. */
    public Barbeiro salvar(Integer id, String nome, String telefone) throws SQLException {
        Barbeiro barbeiro = new Barbeiro(id, Validador.nome(nome), Validador.telefone(telefone));
        if (id == null) {
            dao.inserir(barbeiro);
        } else {
            dao.atualizar(barbeiro);
        }
        return barbeiro;
    }

    /** O banco (ON DELETE RESTRICT) impede excluir um barbeiro que ainda tenha agendamentos. */
    public void excluir(Barbeiro barbeiro) throws SQLException {
        try {
            dao.excluir(barbeiro.getId());
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new IllegalArgumentException("Não é possível excluir a conta: ainda há agendamentos com você.");
        }
    }
}
