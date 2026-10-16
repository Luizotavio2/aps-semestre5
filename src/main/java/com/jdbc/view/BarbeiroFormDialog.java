package com.jdbc.view;

import com.jdbc.controller.BarbeiroController;
import com.jdbc.controller.ValidacaoException;
import com.jdbc.dao.DAOException;
import com.jdbc.model.Barbeiro;

import javax.swing.JTextField;
import java.awt.Component;

class BarbeiroFormDialog extends FormularioDialog {
    private final BarbeiroController controller;
    private final Barbeiro barbeiro;
    private final JTextField nome = new JTextField(28);
    private final JTextField telefone = new JTextField(15);

    BarbeiroFormDialog(Component pai, BarbeiroController controller, Barbeiro barbeiro) {
        super(pai, barbeiro == null ? "Novo barbeiro" : "Editar barbeiro");
        this.controller = controller;
        this.barbeiro = barbeiro;
        if (barbeiro != null) {
            nome.setText(barbeiro.getNome());
            telefone.setText(barbeiro.getTelefone());
        }
        telefone.setToolTipText("DDD + número, ex.: (11) 98765-4321");
        adicionarCampo("Nome:", nome);
        adicionarCampo("Telefone:", telefone);
        montar();
    }

    @Override
    protected void salvar() throws ValidacaoException, DAOException {
        controller.salvar(barbeiro == null ? null : barbeiro.getId(), nome.getText(), telefone.getText());
    }
}
