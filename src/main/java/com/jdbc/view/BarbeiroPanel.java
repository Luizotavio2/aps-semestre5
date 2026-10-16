package com.jdbc.view;

import com.jdbc.controller.BarbeiroController;
import com.jdbc.controller.ValidacaoException;
import com.jdbc.dao.DAOException;
import com.jdbc.model.Barbeiro;

import java.util.List;

class BarbeiroPanel extends CrudPanel<Barbeiro> {
    private final BarbeiroController controller = new BarbeiroController();

    BarbeiroPanel() {
        super("barbeiro", List.of(
                new EntidadeTableModel.Coluna<>("Código", Barbeiro::getId),
                new EntidadeTableModel.Coluna<>("Nome", Barbeiro::getNome),
                new EntidadeTableModel.Coluna<>("Telefone", Barbeiro::getTelefone),
                new EntidadeTableModel.Coluna<>("Cadastrado em", b -> Formatos.formatar(b.getCriadoEm(), Formatos.DATA_HORA))));
        montarTela(controller.camposOrdenacao());
    }

    @Override
    protected String dicaBusca() {
        return "Busca por nome ou telefone";
    }

    @Override
    protected List<Barbeiro> buscar() throws DAOException {
        return controller.listar(campoBusca.getText(), criteriosOrdenacao());
    }

    @Override
    protected boolean abrirFormulario(Barbeiro barbeiro) {
        return new BarbeiroFormDialog(this, controller, barbeiro).exibir();
    }

    @Override
    protected void excluir(Barbeiro barbeiro) throws ValidacaoException, DAOException {
        controller.excluir(barbeiro);
    }

    @Override
    protected String descrever(Barbeiro barbeiro) {
        return "o barbeiro " + barbeiro.getNome();
    }
}
