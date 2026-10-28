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

/** Todo o SQL da tabela agendamento. */
public class AgendamentoDAO {
    // traz o agendamento junto com os dados do cliente e do barbeiro (JOIN)
    private static final String SELECT = "SELECT a.id, a.data_hora, a.servico, "
            + "c.id AS c_id, c.nome AS c_nome, c.telefone AS c_telefone, "
            + "b.id AS b_id, b.nome AS b_nome, b.telefone AS b_telefone "
            + "FROM agendamento a "
            + "JOIN cliente c ON c.id = a.cliente_id "
            + "JOIN barbeiro b ON b.id = a.barbeiro_id ";

    public void inserir(Agendamento agendamento) throws SQLException {
        String sql = "INSERT INTO agendamento (cliente_id, barbeiro_id, data_hora, servico) VALUES (?, ?, ?, ?)";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, agendamento.getCliente().getId());
            stmt.setInt(2, agendamento.getBarbeiro().getId());
            stmt.setTimestamp(3, Timestamp.valueOf(agendamento.getDataHora()));
            stmt.setString(4, agendamento.getServico());
            stmt.executeUpdate();
        }
    }

    public void atualizar(Agendamento agendamento) throws SQLException {
        String sql = "UPDATE agendamento SET barbeiro_id = ?, data_hora = ?, servico = ? WHERE id = ?";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, agendamento.getBarbeiro().getId());
            stmt.setTimestamp(2, Timestamp.valueOf(agendamento.getDataHora()));
            stmt.setString(3, agendamento.getServico());
            stmt.setInt(4, agendamento.getId());
            stmt.executeUpdate();
        }
    }

    public void excluir(Agendamento agendamento) throws SQLException {
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement stmt = con.prepareStatement("DELETE FROM agendamento WHERE id = ?")) {
            stmt.setInt(1, agendamento.getId());
            stmt.executeUpdate();
        }
    }

    /** Agendamentos de um cliente, por data. */
    public List<Agendamento> listarPorCliente(Cliente cliente) throws SQLException {
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement stmt = con.prepareStatement(SELECT + "WHERE c.id = ? ORDER BY a.data_hora")) {
            stmt.setInt(1, cliente.getId());
            return lerResultado(stmt.executeQuery());
        }
    }

    /** Todos os agendamentos, filtrados pela busca e ordenados por dois critérios. */
    public List<Agendamento> listar(String busca, String ordem1, String ordem2) throws SQLException {
        String sql = SELECT + "WHERE c.nome LIKE ? OR b.nome LIKE ? OR a.servico LIKE ? "
                + "ORDER BY " + coluna(ordem1) + ", " + coluna(ordem2);
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            String termo = "%" + busca + "%"; // % = qualquer texto antes/depois
            stmt.setString(1, termo);
            stmt.setString(2, termo);
            stmt.setString(3, termo);
            return lerResultado(stmt.executeQuery());
        }
    }

    /** Traduz a opção escolhida na tela para a coluna do banco. */
    private String coluna(String opcao) {
        switch (opcao) {
            case "Cliente":
                return "c.nome";
            case "Barbeiro":
                return "b.nome";
            case "Serviço":
                return "a.servico";
            default:
                return "a.data_hora";
        }
    }

    /** Converte cada linha do resultado em um objeto Agendamento. */
    private List<Agendamento> lerResultado(ResultSet rs) throws SQLException {
        List<Agendamento> agendamentos = new ArrayList<>();
        while (rs.next()) {
            Cliente cliente = new Cliente(rs.getInt("c_id"), rs.getString("c_nome"), rs.getString("c_telefone"));
            Barbeiro barbeiro = new Barbeiro(rs.getInt("b_id"), rs.getString("b_nome"), rs.getString("b_telefone"));
            agendamentos.add(new Agendamento(rs.getInt("id"), cliente, barbeiro,
                    rs.getTimestamp("data_hora").toLocalDateTime(), rs.getString("servico")));
        }
        return agendamentos;
    }
}
