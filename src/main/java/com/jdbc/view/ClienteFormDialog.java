package com.jdbc.view;

import com.jdbc.controller.ClienteController;
import com.jdbc.controller.ValidacaoException;
import com.jdbc.dao.DAOException;
import com.jdbc.model.Cliente;

import javax.swing.JTextField;
import java.awt.Component;

class ClienteFormDialog extends FormularioDialog {
    private final ClienteController controller;
    private final Cliente cliente;
    private final JTextField nome = new JTextField(28);
    private final JTextField telefone = new JTextField(15);
    private final JTextField email = new JTextField(28);

    ClienteFormDialog(Component pai, ClienteController controller, Cliente cliente) {
        super(pai, cliente == null ? "Novo cliente" : "Editar cliente");
        this.controller = controller;
        this.cliente = cliente;
        if (cliente != null) {
            nome.setText(cliente.getNome());
            telefone.setText(cliente.getTelefone());
            email.setText(cliente.getEmail());
        }
        telefone.setToolTipText("DDD + número, ex.: (11) 98765-4321");
        adicionarCampo("Nome:", nome);
        adicionarCampo("Telefone:", telefone);
        adicionarCampo("E-mail (opcional):", email);
        montar();
    }

    @Override
    protected void salvar() throws ValidacaoException, DAOException {
        controller.salvar(cliente == null ? null : cliente.getId(), nome.getText(), telefone.getText(), email.getText());
    }
}
