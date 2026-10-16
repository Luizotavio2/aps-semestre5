package com.jdbc.controller;

import com.jdbc.dao.AgendamentoDAO;
import com.jdbc.dao.ClienteDAO;
import com.jdbc.dao.Criterio;
import com.jdbc.dao.DAOException;
import com.jdbc.model.Cliente;

import java.util.List;

public class ClienteController {
    private final ClienteDAO dao = new ClienteDAO();
    private final AgendamentoDAO agendamentoDAO = new AgendamentoDAO();

    public List<Cliente> listar(String filtro, List<Criterio> ordenacao) throws DAOException {
        return dao.listar(filtro, ordenacao);
    }

    public List<Cliente> listarTodos() throws DAOException {
        return dao.listar(null, null);
    }

    public List<String> camposOrdenacao() {
        return dao.camposOrdenacao();
    }

    /** Cadastra (id nulo) ou atualiza um cliente a partir dos dados digitados. */
    public Cliente salvar(Integer id, String nome, String telefone, String email)
            throws ValidacaoException, DAOException {
        Cliente cliente = new Cliente(id, Validador.nome(nome), Validador.telefone(telefone),
                Validador.emailOpcional(email));
        if (id == null) {
            dao.inserir(cliente);
        } else {
            dao.atualizar(cliente);
        }
        return cliente;
    }

    /** Exclui o cliente somente se não houver agendamentos vinculados (integridade da chave estrangeira). */
    public void excluir(Cliente cliente) throws ValidacaoException, DAOException {
        int agendamentos = agendamentoDAO.contarPorCliente(cliente.getId());
        if (agendamentos > 0) {
            throw new ValidacaoException("Não é possível excluir " + cliente.getNome() + ": há " + agendamentos
                    + " agendamento(s) vinculado(s) a este cliente.\nExclua ou transfira os agendamentos antes.");
        }
        dao.excluir(cliente.getId());
    }
}
