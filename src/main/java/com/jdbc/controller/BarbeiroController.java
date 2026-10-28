package com.jdbc.controller;

import com.jdbc.dao.BarbeiroDAO;
import com.jdbc.model.Barbeiro;

import java.sql.SQLException;
import java.util.List;

/** Valida os dados digitados na tela e chama o DAO. */
public class BarbeiroController {
    private final BarbeiroDAO dao = new BarbeiroDAO();

    public List<Barbeiro> listarTodos() throws SQLException {
        return dao.listarTodos();
    }

    public Barbeiro cadastrar(String nome, String telefone) throws Exception {
        telefone = telefone.replaceAll("[^0-9]", ""); // deixa só os números
        if (nome.isBlank()) {
            throw new Exception("Informe o nome.");
        }
        if (telefone.length() < 10 || telefone.length() > 11) {
            throw new Exception("Telefone inválido: informe DDD + número.");
        }

        Barbeiro barbeiro = new Barbeiro(0, nome.trim(), telefone);
        dao.inserir(barbeiro);
        return barbeiro;
    }
}
