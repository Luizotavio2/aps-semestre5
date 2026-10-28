package com.jdbc.dao;

import com.jdbc.model.Cliente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/** Todo o SQL da tabela cliente. */
public class ClienteDAO {

    public void inserir(Cliente cliente) throws SQLException {
        String sql = "INSERT INTO cliente (nome, telefone) VALUES (?, ?)";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, cliente.getNome());
            stmt.setString(2, cliente.getTelefone());
            stmt.executeUpdate();
            // pega o id gerado pelo AUTO_INCREMENT
            ResultSet chave = stmt.getGeneratedKeys();
            chave.next();
            cliente.setId(chave.getInt(1));
        }
    }

    public void atualizar(Cliente cliente) throws SQLException {
        String sql = "UPDATE cliente SET nome = ?, telefone = ? WHERE id = ?";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, cliente.getNome());
            stmt.setString(2, cliente.getTelefone());
            stmt.setInt(3, cliente.getId());
            stmt.executeUpdate();
        }
    }

    public void excluir(Cliente cliente) throws SQLException {
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement stmt = con.prepareStatement("DELETE FROM cliente WHERE id = ?")) {
            stmt.setInt(1, cliente.getId());
            stmt.executeUpdate();
        }
    }

    public List<Cliente> listarTodos() throws SQLException {
        List<Cliente> clientes = new ArrayList<>();
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement stmt = con.prepareStatement("SELECT * FROM cliente ORDER BY nome");
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                clientes.add(new Cliente(rs.getInt("id"), rs.getString("nome"), rs.getString("telefone")));
            }
        }
        return clientes;
    }
}
