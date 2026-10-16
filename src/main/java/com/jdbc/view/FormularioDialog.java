package com.jdbc.view;

import com.jdbc.controller.ValidacaoException;
import com.jdbc.dao.DAOException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyEvent;

/**
 * Diálogo base para cadastro/edição. Mantém a janela aberta enquanto houver erro de validação,
 * para que o usuário possa corrigir os dados sem perdê-los.
 */
abstract class FormularioDialog extends JDialog {
    private final JPanel campos = new JPanel(new GridBagLayout());
    private int linha;
    private boolean salvo;

    protected FormularioDialog(Component pai, String titulo) {
        super(SwingUtilities.getWindowAncestor(pai), titulo, ModalityType.APPLICATION_MODAL);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    protected void adicionarCampo(String rotulo, JComponent campo) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.gridy = linha++;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.LINE_END;
        campos.add(new JLabel(rotulo), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.LINE_START;
        campos.add(campo, gbc);
    }

    /** Finaliza o layout. Deve ser chamado ao final do construtor da subclasse. */
    protected void montar() {
        JButton salvar = new JButton("Salvar");
        JButton cancelar = new JButton("Cancelar");
        salvar.addActionListener(e -> confirmar());
        cancelar.addActionListener(e -> dispose());
        getRootPane().setDefaultButton(salvar);
        getRootPane().registerKeyboardAction(e -> dispose(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botoes.add(salvar);
        botoes.add(cancelar);

        JPanel conteudo = new JPanel(new BorderLayout(0, 8));
        conteudo.setBorder(BorderFactory.createEmptyBorder(12, 12, 8, 12));
        conteudo.add(campos, BorderLayout.CENTER);
        conteudo.add(botoes, BorderLayout.SOUTH);
        setContentPane(conteudo);
        pack();
        setMinimumSize(getSize());
        setLocationRelativeTo(getOwner());
    }

    private void confirmar() {
        try {
            salvar();
            salvo = true;
            dispose();
        } catch (ValidacaoException e) {
            Mensagens.aviso(this, e.getMessage());
        } catch (DAOException e) {
            Mensagens.erro(this, e.getMessage());
        }
    }

    /** Envia os dados ao controller; lança exceção se forem inválidos ou se o banco falhar. */
    protected abstract void salvar() throws ValidacaoException, DAOException;

    /** Exibe o diálogo (modal) e informa se o registro foi salvo. */
    boolean exibir() {
        setVisible(true);
        return salvo;
    }
}
