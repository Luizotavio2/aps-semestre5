package com.jdbc.dao;

import com.jdbc.model.Agendamento;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AgendamentoDAO implements DAO<Agendamento> {
    private static final Map<String, String> ORDENACAO = new LinkedHashMap<>();

    static {
        ORDENACAO.put("Data/hora", "a.inicio");
        ORDENACAO.put("Cliente", "c.nome");
        ORDENACAO.put("Barbeiro", "b.nome");
        ORDENACAO.put("Serviço", "a.servico");
        ORDENACAO.put("Valor", "a.valor");
        ORDENACAO.put("Código", "a.id");
    }

    /** Agendamento junto com os dados do cliente e do barbeiro relacionados. */
    private static final String SELECT = """
            SELECT a.id, a.inicio, a.fim, a.servico, a.valor, a.criado_em,
                   c.id AS c_id, c.nome AS c_nome, c.telefone AS c_telefone,
                   c.email AS c_email, c.criado_em AS c_criado_em,
                   b.id AS b_id, b.nome AS b_nome, b.telefone AS b_telefone,
                   b.criado_em AS b_criado_em
              FROM agendamento a
              JOIN cliente c ON c.id = a.cliente_id
              JOIN barbeiro b ON b.id = a.barbeiro_id
            """;

    @Override
    public void inserir(Agendamento agendamento) throws DAOException {
        String sql = "INSERT INTO agendamento (cliente_id, barbeiro_id, inicio, fim, servico, valor) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preencher(stmt, agendamento);
            stmt.executeUpdate();
            try (ResultSet chaves = stmt.getGeneratedKeys()) {
                if (chaves.next()) {
                    agendamento.setId(chaves.getInt(1));
                }
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new DAOException("O cliente ou o barbeiro selecionado não existe mais. Atualize as listas.", e);
        } catch (SQLException e) {
            throw new DAOException("Erro ao cadastrar agendamento: " + e.getMessage(), e);
        }
    }

    @Override
    public void atualizar(Agendamento agendamento) throws DAOException {
        String sql = "UPDATE agendamento SET cliente_id = ?, barbeiro_id = ?, inicio = ?, fim = ?, "
                + "servico = ?, valor = ? WHERE id = ?";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            preencher(stmt, agendamento);
            stmt.setInt(7, agendamento.getId());
            if (stmt.executeUpdate() == 0) {
                throw new DAOException("Agendamento não encontrado. Ele pode ter sido excluído.");
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new DAOException("O cliente ou o barbeiro selecionado não existe mais. Atualize as listas.", e);
        } catch (SQLException e) {
            throw new DAOException("Erro ao atualizar agendamento: " + e.getMessage(), e);
        }
    }

    @Override
    public void excluir(int id) throws DAOException {
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement stmt = con.prepareStatement("DELETE FROM agendamento WHERE id = ?")) {
            stmt.setInt(1, id);
            if (stmt.executeUpdate() == 0) {
                throw new DAOException("Agendamento não encontrado. Ele pode já ter sido excluído.");
            }
        } catch (SQLException e) {
            throw new DAOException("Erro ao excluir agendamento: " + e.getMessage(), e);
        }
    }

    @Override
    public Agendamento buscarPorId(int id) throws DAOException {
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement stmt = con.prepareStatement(SELECT + " WHERE a.id = ?")) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        } catch (SQLException e) {
            throw new DAOException("Erro ao buscar agendamento: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Agendamento> listar(String filtro, List<Criterio> ordenacao) throws DAOException {
        return listar(filtro, null, null, ordenacao);
    }

    /**
     * Lista agendamentos filtrando por texto (cliente, barbeiro ou serviço) e por período.
     * Qualquer filtro nulo é ignorado.
     */
    public List<Agendamento> listar(String filtro, LocalDate de, LocalDate ate, List<Criterio> ordenacao)
            throws DAOException {
        StringBuilder sql = new StringBuilder(SELECT).append(" WHERE 1 = 1");
        List<Object> parametros = new ArrayList<>();
        if (filtro != null && !filtro.isBlank()) {
            sql.append(" AND (c.nome LIKE ? OR b.nome LIKE ? OR a.servico LIKE ?)");
            String termo = "%" + filtro.trim() + "%";
            parametros.add(termo);
            parametros.add(termo);
            parametros.add(termo);
        }
        if (de != null) {
            sql.append(" AND a.inicio >= ?");
            parametros.add(Timestamp.valueOf(de.atStartOfDay()));
        }
        if (ate != null) {
            sql.append(" AND a.inicio < ?");
            parametros.add(Timestamp.valueOf(ate.plusDays(1).atStartOfDay()));
        }
        sql.append(Criterio.montarOrderBy(ordenacao, ORDENACAO, "a.inicio"));

        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql.toString())) {
            for (int i = 0; i < parametros.size(); i++) {
                stmt.setObject(i + 1, parametros.get(i));
            }
            List<Agendamento> agendamentos = new ArrayList<>();
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    agendamentos.add(mapear(rs));
                }
            }
            return agendamentos;
        } catch (SQLException e) {
            throw new DAOException("Erro ao listar agendamentos: " + e.getMessage(), e);
        }
    }

    /**
     * Verifica se o barbeiro já tem outro agendamento que se sobrepõe ao período informado.
     *
     * @param ignorarId agendamento a desconsiderar (o próprio, em uma edição); null em um cadastro
     */
    public boolean existeConflito(int barbeiroId, LocalDateTime inicio, LocalDateTime fim, Integer ignorarId)
            throws DAOException {
        String sql = "SELECT COUNT(*) FROM agendamento WHERE barbeiro_id = ? AND inicio < ? AND fim > ?"
                + (ignorarId != null ? " AND id <> ?" : "");
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, barbeiroId);
            stmt.setTimestamp(2, Timestamp.valueOf(fim));
            stmt.setTimestamp(3, Timestamp.valueOf(inicio));
            if (ignorarId != null) {
                stmt.setInt(4, ignorarId);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new DAOException("Erro ao verificar disponibilidade do barbeiro: " + e.getMessage(), e);
        }
    }

    public int contarPorCliente(int clienteId) throws DAOException {
        return contar("cliente_id", clienteId);
    }

    public int contarPorBarbeiro(int barbeiroId) throws DAOException {
        return contar("barbeiro_id", barbeiroId);
    }

    private int contar(String coluna, int id) throws DAOException {
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement stmt = con.prepareStatement(
                     "SELECT COUNT(*) FROM agendamento WHERE " + coluna + " = ?")) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new DAOException("Erro ao contar agendamentos: " + e.getMessage(), e);
        }
    }

    @Override
    public List<String> camposOrdenacao() {
        return List.copyOf(ORDENACAO.keySet());
    }

    private void preencher(PreparedStatement stmt, Agendamento agendamento) throws SQLException {
        stmt.setInt(1, agendamento.getCliente().getId());
        stmt.setInt(2, agendamento.getBarbeiro().getId());
        stmt.setTimestamp(3, Timestamp.valueOf(agendamento.getInicio()));
        stmt.setTimestamp(4, Timestamp.valueOf(agendamento.getFim()));
        stmt.setString(5, agendamento.getServico());
        stmt.setBigDecimal(6, agendamento.getValor());
    }

    private Agendamento mapear(ResultSet rs) throws SQLException {
        Agendamento agendamento = new Agendamento(rs.getInt("id"),
                ClienteDAO.mapear(rs, "c_"), BarbeiroDAO.mapear(rs, "b_"),
                rs.getTimestamp("inicio").toLocalDateTime(), rs.getTimestamp("fim").toLocalDateTime(),
                rs.getString("servico"), rs.getBigDecimal("valor"));
        Timestamp criadoEm = rs.getTimestamp("criado_em");
        agendamento.setCriadoEm(criadoEm == null ? null : criadoEm.toLocalDateTime());
        return agendamento;
    }
}
