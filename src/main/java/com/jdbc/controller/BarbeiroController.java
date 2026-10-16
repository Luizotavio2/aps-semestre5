package com.jdbc.controller;

import com.jdbc.dao.AgendamentoDAO;
import com.jdbc.dao.BarbeiroDAO;
import com.jdbc.dao.Criterio;
import com.jdbc.dao.DAOException;
import com.jdbc.model.Barbeiro;

import java.util.List;

public class BarbeiroController {
    private final BarbeiroDAO dao = new BarbeiroDAO();
    private final AgendamentoDAO agendamentoDAO = new AgendamentoDAO();

    public List<Barbeiro> listar(String filtro, List<Criterio> ordenacao) throws DAOException {
        return dao.listar(filtro, ordenacao);
    }

    public List<Barbeiro> listarTodos() throws DAOException {
        return dao.listar(null, null);
    }

    public List<String> camposOrdenacao() {
        return dao.camposOrdenacao();
    }

    /** Cadastra (id nulo) ou atualiza um barbeiro a partir dos dados digitados. */
    public Barbeiro salvar(Integer id, String nome, String telefone) throws ValidacaoException, DAOException {
        Barbeiro barbeiro = new Barbeiro(id, Validador.nome(nome), Validador.telefone(telefone));
        if (id == null) {
            dao.inserir(barbeiro);
        } else {
            dao.atualizar(barbeiro);
        }
        return barbeiro;
    }

    /** Exclui o barbeiro somente se não houver agendamentos vinculados (integridade da chave estrangeira). */
    public void excluir(Barbeiro barbeiro) throws ValidacaoException, DAOException {
        int agendamentos = agendamentoDAO.contarPorBarbeiro(barbeiro.getId());
        if (agendamentos > 0) {
            throw new ValidacaoException("Não é possível excluir " + barbeiro.getNome() + ": há " + agendamentos
                    + " agendamento(s) vinculado(s) a este barbeiro.\nExclua ou transfira os agendamentos antes.");
        }
        dao.excluir(barbeiro.getId());
    }
}
