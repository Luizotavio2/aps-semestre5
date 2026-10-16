package com.jdbc.dao;

import com.jdbc.model.Cliente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ClienteDAO implements DAO<Cliente> {
    private static final Map<String, String> ORDENACAO = new LinkedHashMap<>();

    static {
        ORDENACAO.put("Nome", "nome");
        ORDENACAO.put("Telefone", "telefone");
        ORDENACAO.put("E-mail", "email");
        ORDENACAO.put("Data de cadastro", "criado_em");
        ORDENACAO.put("Código", "id");
    }

    @Override
    public void inserir(Cliente cliente) throws DAOException {
        String sql = "INSERT INTO cliente (nome, telefone, email) VALUES (?, ?, ?)";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preencher(stmt, cliente);
            stmt.executeUpdate();
            try (ResultSet chaves = stmt.getGeneratedKeys()) {
                if (chaves.next()) {
                    cliente.setId(chaves.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Erro ao cadastrar cliente: " + e.getMessage(), e);
        }
    }

    @Override
    public void atualizar(Cliente cliente) throws DAOException {
        String sql = "UPDATE cliente SET nome = ?, telefone = ?, email = ? WHERE id = ?";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            preencher(stmt, cliente);
            stmt.setInt(4, cliente.getId());
            if (stmt.executeUpdate() == 0) {
                throw new DAOException("Cliente não encontrado. Ele pode ter sido excluído.");
            }
        } catch (SQLException e) {
            throw new DAOException("Erro ao atualizar cliente: " + e.getMessage(), e);
        }
    }

    @Override
    public void excluir(int id) throws DAOException {
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement stmt = con.prepareStatement("DELETE FROM cliente WHERE id = ?")) {
            stmt.setInt(1, id);
            if (stmt.executeUpdate() == 0) {
                throw new DAOException("Cliente não encontrado. Ele pode já ter sido excluído.");
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new DAOException("Não é possível excluir o cliente: existem agendamentos vinculados a ele.", e);
        } catch (SQLException e) {
            throw new DAOException("Erro ao excluir cliente: " + e.getMessage(), e);
        }
    }

    @Override
    public Cliente buscarPorId(int id) throws DAOException {
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement stmt = con.prepareStatement("SELECT * FROM cliente WHERE id = ?")) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? mapear(rs, "") : null;
            }
        } catch (SQLException e) {
            throw new DAOException("Erro ao buscar cliente: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Cliente> listar(String filtro, List<Criterio> ordenacao) throws DAOException {
        StringBuilder sql = new StringBuilder("SELECT * FROM cliente");
        boolean filtrar = filtro != null && !filtro.isBlank();
        if (filtrar) {
            sql.append(" WHERE nome LIKE ? OR telefone LIKE ? OR email LIKE ?");
        }
        sql.append(Criterio.montarOrderBy(ordenacao, ORDENACAO, "nome"));

        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql.toString())) {
            if (filtrar) {
                String termo = "%" + filtro.trim() + "%";
                for (int i = 1; i <= 3; i++) {
                    stmt.setString(i, termo);
                }
            }
            List<Cliente> clientes = new ArrayList<>();
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    clientes.add(mapear(rs, ""));
                }
            }
            return clientes;
        } catch (SQLException e) {
            throw new DAOException("Erro ao listar clientes: " + e.getMessage(), e);
        }
    }

    @Override
    public List<String> camposOrdenacao() {
        return List.copyOf(ORDENACAO.keySet());
    }

    private void preencher(PreparedStatement stmt, Cliente cliente) throws SQLException {
        stmt.setString(1, cliente.getNome());
        stmt.setString(2, cliente.getTelefone());
        stmt.setString(3, cliente.getEmail());
    }

    /** Converte a linha atual em Cliente; o prefixo permite reaproveitar em consultas com JOIN. */
    static Cliente mapear(ResultSet rs, String prefixo) throws SQLException {
        Cliente cliente = new Cliente(rs.getInt(prefixo + "id"), rs.getString(prefixo + "nome"),
                rs.getString(prefixo + "telefone"), rs.getString(prefixo + "email"));
        Timestamp criadoEm = rs.getTimestamp(prefixo + "criado_em");
        cliente.setCriadoEm(criadoEm == null ? null : criadoEm.toLocalDateTime());
        return cliente;
    }
}
