package com.jdbc.dao;

import com.jdbc.model.Barbeiro;

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

public class BarbeiroDAO implements DAO<Barbeiro> {
    private static final Map<String, String> ORDENACAO = new LinkedHashMap<>();

    static {
        ORDENACAO.put("Nome", "nome");
        ORDENACAO.put("Telefone", "telefone");
        ORDENACAO.put("Data de cadastro", "criado_em");
        ORDENACAO.put("Código", "id");
    }

    @Override
    public void inserir(Barbeiro barbeiro) throws DAOException {
        String sql = "INSERT INTO barbeiro (nome, telefone) VALUES (?, ?)";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, barbeiro.getNome());
            stmt.setString(2, barbeiro.getTelefone());
            stmt.executeUpdate();
            try (ResultSet chaves = stmt.getGeneratedKeys()) {
                if (chaves.next()) {
                    barbeiro.setId(chaves.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Erro ao cadastrar barbeiro: " + e.getMessage(), e);
        }
    }

    @Override
    public void atualizar(Barbeiro barbeiro) throws DAOException {
        String sql = "UPDATE barbeiro SET nome = ?, telefone = ? WHERE id = ?";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, barbeiro.getNome());
            stmt.setString(2, barbeiro.getTelefone());
            stmt.setInt(3, barbeiro.getId());
            if (stmt.executeUpdate() == 0) {
                throw new DAOException("Barbeiro não encontrado. Ele pode ter sido excluído.");
            }
        } catch (SQLException e) {
            throw new DAOException("Erro ao atualizar barbeiro: " + e.getMessage(), e);
        }
    }

    @Override
    public void excluir(int id) throws DAOException {
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement stmt = con.prepareStatement("DELETE FROM barbeiro WHERE id = ?")) {
            stmt.setInt(1, id);
            if (stmt.executeUpdate() == 0) {
                throw new DAOException("Barbeiro não encontrado. Ele pode já ter sido excluído.");
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new DAOException("Não é possível excluir o barbeiro: existem agendamentos vinculados a ele.", e);
        } catch (SQLException e) {
            throw new DAOException("Erro ao excluir barbeiro: " + e.getMessage(), e);
        }
    }

    @Override
    public Barbeiro buscarPorId(int id) throws DAOException {
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement stmt = con.prepareStatement("SELECT * FROM barbeiro WHERE id = ?")) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? mapear(rs, "") : null;
            }
        } catch (SQLException e) {
            throw new DAOException("Erro ao buscar barbeiro: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Barbeiro> listar(String filtro, List<Criterio> ordenacao) throws DAOException {
        StringBuilder sql = new StringBuilder("SELECT * FROM barbeiro");
        boolean filtrar = filtro != null && !filtro.isBlank();
        if (filtrar) {
            sql.append(" WHERE nome LIKE ? OR telefone LIKE ?");
        }
        sql.append(Criterio.montarOrderBy(ordenacao, ORDENACAO, "nome"));

        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql.toString())) {
            if (filtrar) {
                String termo = "%" + filtro.trim() + "%";
                stmt.setString(1, termo);
                stmt.setString(2, termo);
            }
            List<Barbeiro> barbeiros = new ArrayList<>();
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    barbeiros.add(mapear(rs, ""));
                }
            }
            return barbeiros;
        } catch (SQLException e) {
            throw new DAOException("Erro ao listar barbeiros: " + e.getMessage(), e);
        }
    }

    @Override
    public List<String> camposOrdenacao() {
        return List.copyOf(ORDENACAO.keySet());
    }

    /** Converte a linha atual em Barbeiro; o prefixo permite reaproveitar em consultas com JOIN. */
    static Barbeiro mapear(ResultSet rs, String prefixo) throws SQLException {
        Barbeiro barbeiro = new Barbeiro(rs.getInt(prefixo + "id"), rs.getString(prefixo + "nome"),
                rs.getString(prefixo + "telefone"));
        Timestamp criadoEm = rs.getTimestamp(prefixo + "criado_em");
        barbeiro.setCriadoEm(criadoEm == null ? null : criadoEm.toLocalDateTime());
        return barbeiro;
    }
}
