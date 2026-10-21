package com.jdbc.dao;

import com.jdbc.model.Agendamento;
import com.jdbc.model.Barbeiro;
import com.jdbc.model.Cliente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/** Acesso à tabela agendamento (associativa entre cliente e barbeiro). */
public class AgendamentoDAO {
    /** Campos que podem ser escolhidos na ordenação da interface. */
    public static final String[] CAMPOS_ORDENACAO = {"Data/hora", "Cliente", "Barbeiro", "Serviço", "Valor"};

    /** Agendamento junto com os dados do cliente e do barbeiro relacionados. */
    private static final String SELECT = """
            SELECT a.id, a.data_hora, a.servico, a.valor,
                   c.id AS c_id, c.nome AS c_nome, c.telefone AS c_telefone, c.email AS c_email,
                   b.id AS b_id, b.nome AS b_nome, b.telefone AS b_telefone
              FROM agendamento a
              JOIN cliente c ON c.id = a.cliente_id
              JOIN barbeiro b ON b.id = a.barbeiro_id
            """;

    /** Lança SQLIntegrityConstraintViolationException se o barbeiro já tiver agendamento no horário. */
    public void inserir(Agendamento agendamento) throws SQLException {
        String sql = "INSERT INTO agendamento (cliente_id, barbeiro_id, data_hora, servico, valor) "
                + "VALUES (?, ?, ?, ?, ?)";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            preencher(stmt, agendamento);
            stmt.executeUpdate();
        }
    }

    public void atualizar(Agendamento agendamento) throws SQLException {
        String sql = "UPDATE agendamento SET cliente_id = ?, barbeiro_id = ?, data_hora = ?, servico = ?, "
                + "valor = ? WHERE id = ?";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            preencher(stmt, agendamento);
            stmt.setInt(6, agendamento.getId());
            stmt.executeUpdate();
        }
    }

    public void excluir(int id) throws SQLException {
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement stmt = con.prepareStatement("DELETE FROM agendamento WHERE id = ?")) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public List<Agendamento> listarPorCliente(int clienteId) throws SQLException {
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement stmt = con.prepareStatement(SELECT + " WHERE c.id = ? ORDER BY a.data_hora")) {
            stmt.setInt(1, clienteId);
            return executar(stmt);
        }
    }

    /**
     * Lista agendamentos filtrando pelo texto de busca (cliente, barbeiro ou serviço) e
     * ordenando por dois critérios escolhidos em {@link #CAMPOS_ORDENACAO}.
     *
     * @param barbeiroId se não for nulo, lista apenas os agendamentos desse barbeiro
     */
    public List<Agendamento> listar(Integer barbeiroId, String busca, String ordem1, boolean decrescente1,
                                    String ordem2, boolean decrescente2) throws SQLException {
        String sql = SELECT + " WHERE (c.nome LIKE ? OR b.nome LIKE ? OR a.servico LIKE ?)"
                + (barbeiroId != null ? " AND b.id = ?" : "")
                + " ORDER BY " + coluna(ordem1) + (decrescente1 ? " DESC" : " ASC")
                + ", " + coluna(ordem2) + (decrescente2 ? " DESC" : " ASC");
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            String termo = "%" + (busca == null ? "" : busca.trim()) + "%";
            stmt.setString(1, termo);
            stmt.setString(2, termo);
            stmt.setString(3, termo);
            if (barbeiroId != null) {
                stmt.setInt(4, barbeiroId);
            }
            return executar(stmt);
        }
    }

    /** Converte o nome exibido na tela em coluna SQL; só aceita valores conhecidos (evita injeção de SQL). */
    private String coluna(String campo) {
        return switch (campo) {
            case "Cliente" -> "c.nome";
            case "Barbeiro" -> "b.nome";
            case "Serviço" -> "a.servico";
            case "Valor" -> "a.valor";
            default -> "a.data_hora";
        };
    }

    private void preencher(PreparedStatement stmt, Agendamento agendamento) throws SQLException {
        stmt.setInt(1, agendamento.getCliente().getId());
        stmt.setInt(2, agendamento.getBarbeiro().getId());
        stmt.setTimestamp(3, Timestamp.valueOf(agendamento.getDataHora()));
        stmt.setString(4, agendamento.getServico());
        stmt.setBigDecimal(5, agendamento.getValor());
    }

    private List<Agendamento> executar(PreparedStatement stmt) throws SQLException {
        List<Agendamento> agendamentos = new ArrayList<>();
        try (ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Cliente cliente = new Cliente(rs.getInt("c_id"), rs.getString("c_nome"),
                        rs.getString("c_telefone"), rs.getString("c_email"));
                Barbeiro barbeiro = new Barbeiro(rs.getInt("b_id"), rs.getString("b_nome"),
                        rs.getString("b_telefone"));
                agendamentos.add(new Agendamento(rs.getInt("id"), cliente, barbeiro,
                        rs.getTimestamp("data_hora").toLocalDateTime(), rs.getString("servico"),
                        rs.getBigDecimal("valor")));
            }
        }
        return agendamentos;
    }
}
