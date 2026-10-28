package com.jdbc.view;

import com.jdbc.controller.BarbeiroController;
import com.jdbc.controller.ClienteController;
import com.jdbc.model.Barbeiro;
import com.jdbc.model.Cliente;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;

/** Tela principal: pergunta se o usuário é cliente ou barbeiro. */
public class TelaInicial extends JFrame {
    private final ClienteController clienteController = new ClienteController();
    private final BarbeiroController barbeiroController = new BarbeiroController();

    public TelaInicial() {
        super("Barbearia");

        JButton botaoCliente = new JButton("Sou Cliente");
        JButton botaoBarbeiro = new JButton("Sou Barbeiro");
        botaoCliente.addActionListener(e -> entrarComoCliente());
        botaoBarbeiro.addActionListener(e -> entrarComoBarbeiro());

        JPanel botoes = new JPanel();
        botoes.add(botaoCliente);
        botoes.add(botaoBarbeiro);

        add(new JLabel("Você é cliente ou barbeiro?", SwingConstants.CENTER), BorderLayout.CENTER);
        add(botoes, BorderLayout.SOUTH);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(400, 150);
        setLocationRelativeTo(null); // centraliza na tela
    }

    /** O cliente escolhe seu nome na lista ou faz um novo cadastro. */
    private void entrarComoCliente() {
        try {
            JComboBox<Cliente> lista = new JComboBox<>(clienteController.listarTodos().toArray(new Cliente[0]));
            int opcao = escolherOuCadastrar(lista);

            Cliente cliente = null;
            if (opcao == 0) {
                cliente = (Cliente) lista.getSelectedItem();
            } else if (opcao == 1) {
                cliente = TelaCliente.formularioCliente(this, null);
            }

            if (cliente != null) {
                dispose();
                new TelaCliente(cliente).setVisible(true);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    /** O barbeiro escolhe seu nome na lista ou faz um novo cadastro. */
    private void entrarComoBarbeiro() {
        try {
            JComboBox<Barbeiro> lista = new JComboBox<>(barbeiroController.listarTodos().toArray(new Barbeiro[0]));
            int opcao = escolherOuCadastrar(lista);

            Barbeiro barbeiro = null;
            if (opcao == 0) {
                barbeiro = (Barbeiro) lista.getSelectedItem();
            } else if (opcao == 1) {
                barbeiro = cadastrarBarbeiro();
            }

            if (barbeiro != null) {
                dispose();
                new TelaBarbeiro(barbeiro).setVisible(true);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    /** Mostra a lista de nomes. Devolve 0 = Entrar, 1 = Novo cadastro, outro valor = fechou a janela. */
    private int escolherOuCadastrar(JComboBox<?> lista) {
        Object[] conteudo = {"Selecione seu nome:", lista};
        String[] botoes = {"Entrar", "Novo cadastro"};
        return JOptionPane.showOptionDialog(this, conteudo, "Entrar", JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE, null, botoes, botoes[0]);
    }

    private Barbeiro cadastrarBarbeiro() throws Exception {
        JTextField nome = new JTextField();
        JTextField telefone = new JTextField();
        Object[] campos = {"Nome:", nome, "Telefone (DDD + número):", telefone};
        int resposta = JOptionPane.showConfirmDialog(this, campos, "Cadastro de barbeiro", JOptionPane.OK_CANCEL_OPTION);
        if (resposta != JOptionPane.OK_OPTION) {
            return null;
        }
        return barbeiroController.cadastrar(nome.getText(), telefone.getText());
    }
}
