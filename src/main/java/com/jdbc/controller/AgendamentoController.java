package com.jdbc.controller;

import com.jdbc.dao.AgendamentoDAO;
import com.jdbc.model.Agendamento;
import com.jdbc.model.Barbeiro;
import com.jdbc.model.Cliente;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AgendamentoController {
    /** Serviços oferecidos e seus preços. */
    public static final Map<String, BigDecimal> SERVICOS = new LinkedHashMap<>();

    static {
        SERVICOS.put("Corte", new BigDecimal("40.00"));
        SERVICOS.put("Barba", new BigDecimal("30.00"));
        SERVICOS.put("Corte + Barba", new BigDecimal("60.00"));
        SERVICOS.put("Sobrancelha", new BigDecimal("20.00"));
    }

    /** Campos que o usuário pode escolher para ordenar a agenda. */
    public static final String[] CAMPOS_ORDENACAO = AgendamentoDAO.CAMPOS_ORDENACAO;

    private final AgendamentoDAO dao = new AgendamentoDAO();

    public List<Agendamento> listarPorCliente(Cliente cliente) throws SQLException {
        return dao.listarPorCliente(cliente.getId());
    }

    /** @param barbeiro se não for nulo, lista apenas os agendamentos desse barbeiro */
    public List<Agendamento> listar(Barbeiro barbeiro, String busca, String ordem1, boolean decrescente1,
                                    String ordem2, boolean decrescente2) throws SQLException {
        return dao.listar(barbeiro == null ? null : barbeiro.getId(), busca, ordem1, decrescente1, ordem2, decrescente2);
    }

    /** Cadastra (id nulo) ou remarca um agendamento a partir dos dados digitados. */
    public void salvar(Integer id, Cliente cliente, Barbeiro barbeiro, String dataHora, String servico)
            throws SQLException {
        if (barbeiro == null) {
            throw new IllegalArgumentException("Selecione o barbeiro.");
        }
        if (!SERVICOS.containsKey(servico)) {
            throw new IllegalArgumentException("Selecione o serviço.");
        }
        LocalDateTime horario = Validador.dataHoraFutura(dataHora);
        Agendamento agendamento = new Agendamento(id, cliente, barbeiro, horario, servico, SERVICOS.get(servico));
        try {
            if (id == null) {
                dao.inserir(agendamento);
            } else {
                dao.atualizar(agendamento);
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            // chave única (barbeiro_id, data_hora): o barbeiro já está ocupado nesse horário
            throw new IllegalArgumentException(barbeiro.getNome() + " já tem um agendamento nesse horário. Escolha outro.");
        }
    }

    public void excluir(Agendamento agendamento) throws SQLException {
        dao.excluir(agendamento.getId());
    }

    public static String formatar(LocalDateTime dataHora) {
        return Validador.DATA_HORA.format(dataHora);
    }
}
