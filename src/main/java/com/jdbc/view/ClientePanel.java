package com.jdbc.view;

import com.jdbc.controller.ClienteController;
import com.jdbc.controller.ValidacaoException;
import com.jdbc.dao.DAOException;
import com.jdbc.model.Cliente;

import java.util.List;

class ClientePanel extends CrudPanel<Cliente> {
    private final ClienteController controller = new ClienteController();

    ClientePanel() {
        super("cliente", List.of(
                new EntidadeTableModel.Coluna<>("Código", Cliente::getId),
                new EntidadeTableModel.Coluna<>("Nome", Cliente::getNome),
                new EntidadeTableModel.Coluna<>("Telefone", Cliente::getTelefone),
                new EntidadeTableModel.Coluna<>("E-mail", c -> c.getEmail() == null ? "" : c.getEmail()),
                new EntidadeTableModel.Coluna<>("Cadastrado em", c -> Formatos.formatar(c.getCriadoEm(), Formatos.DATA_HORA))));
        montarTela(controller.camposOrdenacao());
    }

    @Override
    protected String dicaBusca() {
        return "Busca por nome, telefone ou e-mail";
    }

    @Override
    protected List<Cliente> buscar() throws DAOException {
        return controller.listar(campoBusca.getText(), criteriosOrdenacao());
    }

    @Override
    protected boolean abrirFormulario(Cliente cliente) {
        return new ClienteFormDialog(this, controller, cliente).exibir();
    }

    @Override
    protected void excluir(Cliente cliente) throws ValidacaoException, DAOException {
        controller.excluir(cliente);
    }

    @Override
    protected String descrever(Cliente cliente) {
        return "o cliente " + cliente.getNome();
    }
}
