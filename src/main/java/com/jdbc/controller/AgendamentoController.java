package com.jdbc.controller;

import com.jdbc.dao.AgendamentoDAO;
import com.jdbc.model.Agendamento;
import com.jdbc.model.Barbeiro;
import com.jdbc.model.Cliente;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

/** Valida os dados digitados na tela e chama o DAO. */
public class AgendamentoController {
    public static final String[] SERVICOS = {"Corte", "Barba", "Corte + Barba", "Sobrancelha"};
    public static final String[] ORDENACOES = {"Data/hora", "Cliente", "Barbeiro", "Serviço"};
    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final AgendamentoDAO dao = new AgendamentoDAO();

    public List<Agendamento> listarPorCliente(Cliente cliente) throws SQLException {
        return dao.listarPorCliente(cliente);
    }

    public List<Agendamento> listar(String busca, String ordem1, String ordem2) throws SQLException {
        return dao.listar(busca.trim(), ordem1, ordem2);
    }

    /** Cria (id = 0) ou remarca um agendamento. */
    public void salvar(int id, Cliente cliente, Barbeiro barbeiro, String servico, String dataHoraTexto)
            throws Exception {
        if (barbeiro == null) {
            throw new Exception("Selecione um barbeiro.");
        }
        LocalDateTime dataHora;
        try {
            dataHora = LocalDateTime.parse(dataHoraTexto.trim(), FORMATO);
        } catch (DateTimeParseException e) {
            throw new Exception("Data/hora inválida. Use o formato dd/mm/aaaa hh:mm, ex.: 25/11/2026 14:30");
        }
        if (dataHora.isBefore(LocalDateTime.now())) {
            throw new Exception("Essa data/hora já passou.");
        }

        Agendamento agendamento = new Agendamento(id, cliente, barbeiro, dataHora, servico);
        try {
            if (id == 0) {
                dao.inserir(agendamento);
            } else {
                dao.atualizar(agendamento);
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            // a chave UNIQUE (barbeiro_id, data_hora) impede dois agendamentos no mesmo horário
            throw new Exception(barbeiro.getNome() + " já tem um agendamento nesse horário.");
        }
    }

    public void excluir(Agendamento agendamento) throws SQLException {
        dao.excluir(agendamento);
    }

    /** Converte a data/hora para texto, ex.: 25/11/2026 14:30 */
    public static String formatar(LocalDateTime dataHora) {
        return dataHora.format(FORMATO);
    }
}
