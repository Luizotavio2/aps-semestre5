package com.jdbc.controller;

import com.jdbc.dao.AgendamentoDAO;
import com.jdbc.dao.Criterio;
import com.jdbc.dao.DAOException;
import com.jdbc.model.Agendamento;
import com.jdbc.model.Barbeiro;
import com.jdbc.model.Cliente;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class AgendamentoController {
    private final AgendamentoDAO dao = new AgendamentoDAO();

    /** Lista com filtro de texto e período opcional (datas no formato dd/mm/aaaa). */
    public List<Agendamento> listar(String filtro, String dataDe, String dataAte, List<Criterio> ordenacao)
            throws ValidacaoException, DAOException {
        LocalDate de = Validador.dataOpcional(dataDe, "Data inicial");
        LocalDate ate = Validador.dataOpcional(dataAte, "Data final");
        if (de != null && ate != null && ate.isBefore(de)) {
            throw new ValidacaoException("A data final do filtro não pode ser anterior à data inicial.");
        }
        return dao.listar(filtro, de, ate, ordenacao);
    }

    public List<String> camposOrdenacao() {
        return dao.camposOrdenacao();
    }

    /** Cadastra (id nulo) ou atualiza um agendamento a partir dos dados digitados. */
    public Agendamento salvar(Integer id, Cliente cliente, Barbeiro barbeiro, String data, String horaInicio,
                              String horaFim, String servico, String valor) throws ValidacaoException, DAOException {
        if (cliente == null) {
            throw new ValidacaoException("Selecione o cliente.");
        }
        if (barbeiro == null) {
            throw new ValidacaoException("Selecione o barbeiro.");
        }
        LocalDate dia = Validador.data(data, "Data");
        LocalDateTime inicio = dia.atTime(Validador.hora(horaInicio, "Hora de início"));
        LocalDateTime fim = dia.atTime(Validador.hora(horaFim, "Hora de término"));
        if (!fim.isAfter(inicio)) {
            throw new ValidacaoException("A hora de término deve ser posterior à hora de início.");
        }
        String descricao = Validador.texto(servico, "Serviço", 100);
        BigDecimal preco = Validador.valor(valor);

        if (dao.existeConflito(barbeiro.getId(), inicio, fim, id)) {
            throw new ValidacaoException(barbeiro.getNome() + " já possui um agendamento nesse horário.\n"
                    + "Escolha outro horário ou outro barbeiro.");
        }

        Agendamento agendamento = new Agendamento(id, cliente, barbeiro, inicio, fim, descricao, preco);
        if (id == null) {
            dao.inserir(agendamento);
        } else {
            dao.atualizar(agendamento);
        }
        return agendamento;
    }

    public void excluir(Agendamento agendamento) throws DAOException {
        dao.excluir(agendamento.getId());
    }
}
