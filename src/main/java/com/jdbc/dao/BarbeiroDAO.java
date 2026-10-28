package com.jdbc.dao;

import com.jdbc.model.Barbeiro;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/** Todo o SQL da tabela barbeiro. */
public class BarbeiroDAO {

    public void inserir(Barbeiro barbeiro) throws SQLException {
        String sql = "INSERT INTO barbeiro (nome, telefone) VALUES (?, ?)";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, barbeiro.getNome());
            stmt.setString(2, barbeiro.getTelefone());
            stmt.executeUpdate();
            // pega o id gerado pelo AUTO_INCREMENT
            ResultSet chave = stmt.getGeneratedKeys();
            chave.next();
            barbeiro.setId(chave.getInt(1));
        }
    }

    public List<Barbeiro> listarTodos() throws SQLException {
        List<Barbeiro> barbeiros = new ArrayList<>();
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement stmt = con.prepareStatement("SELECT * FROM barbeiro ORDER BY nome");
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                barbeiros.add(new Barbeiro(rs.getInt("id"), rs.getString("nome"), rs.getString("telefone")));
            }
        }
        return barbeiros;
    }
}
