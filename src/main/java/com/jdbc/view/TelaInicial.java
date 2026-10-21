package com.jdbc.view;

import com.jdbc.controller.BarbeiroController;
import com.jdbc.controller.ClienteController;
import com.jdbc.model.Barbeiro;
import com.jdbc.model.Cliente;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

/** Tela principal: pergunta se o usuário é cliente ou barbeiro. */
public class TelaInicial extends JFrame {
    private static final String CADASTRAR = "Novo cadastro";

    private final ClienteController clienteController = new ClienteController();
    private final BarbeiroController barbeiroController = new BarbeiroController();

    public TelaInicial() {
        super("Barbearia - Agendamentos");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JLabel titulo = new JLabel("Bem-vindo à Barbearia!", SwingConstants.CENTER);
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 22f));
        JLabel pergunta = new JLabel("Você é cliente ou barbeiro?", SwingConstants.CENTER);
        pergunta.setFont(pergunta.getFont().deriveFont(16f));

        JButton cliente = new JButton("Sou Cliente");
        JButton barbeiro = new JButton("Sou Barbeiro");
        cliente.setFont(cliente.getFont().deriveFont(Font.BOLD, 16f));
        barbeiro.setFont(barbeiro.getFont().deriveFont(Font.BOLD, 16f));
        cliente.addActionListener(e -> entrarComoCliente());
        barbeiro.addActionListener(e -> entrarComoBarbeiro());

        JPanel textos = new JPanel(new GridLayout(2, 1, 0, 8));
        textos.add(titulo);
        textos.add(pergunta);
        JPanel botoes = new JPanel(new GridLayout(1, 2, 16, 0));
        botoes.add(cliente);
        botoes.add(barbeiro);

        JPanel conteudo = new JPanel(new BorderLayout(0, 24));
        conteudo.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        conteudo.add(textos, BorderLayout.NORTH);
        conteudo.add(botoes, BorderLayout.CENTER);
        setContentPane(conteudo);

        setSize(480, 260);
        setLocationRelativeTo(null);
    }

    /** O cliente escolhe seu nome na lista ou faz um novo cadastro. */
    private void entrarComoCliente() {
        List<Cliente> clientes = new ArrayList<>();
        if (!Telas.executar(this, () -> clientes.addAll(clienteController.listarTodos()))) {
            return;
        }
        Object escolha = escolher("Cliente", clientes.toArray());
        Cliente cliente = escolha == CADASTRAR
                ? TelaCliente.formulario(this, clienteController, null)
                : (Cliente) escolha;
        if (cliente != null) {
            setVisible(false);
            new TelaCliente(this, cliente).setVisible(true);
        }
    }

    /** O barbeiro escolhe seu nome na lista ou faz um novo cadastro. */
    private void entrarComoBarbeiro() {
        List<Barbeiro> barbeiros = new ArrayList<>();
        if (!Telas.executar(this, () -> barbeiros.addAll(barbeiroController.listarTodos()))) {
            return;
        }
        Object escolha = escolher("Barbeiro", barbeiros.toArray());
        Barbeiro barbeiro = escolha == CADASTRAR
                ? TelaBarbeiro.formulario(this, barbeiroController, null)
                : (Barbeiro) escolha;
        if (barbeiro != null) {
            setVisible(false);
            new TelaBarbeiro(this, barbeiro).setVisible(true);
        }
    }

    /**
     * Mostra os cadastros existentes com os botões Entrar / Novo cadastro / Cancelar.
     * Se não houver ninguém cadastrado, vai direto para o cadastro.
     *
     * @return o item escolhido (Entrar), {@link #CADASTRAR} (Novo cadastro) ou null (Cancelar)
     */
    private Object escolher(String tipo, Object[] itens) {
        if (itens.length == 0) {
            return CADASTRAR;
        }
        JComboBox<Object> lista = new JComboBox<>(itens);
        JPanel painel = new JPanel(new BorderLayout(0, 6));
        painel.add(new JLabel("Selecione seu nome ou faça um novo cadastro:"), BorderLayout.NORTH);
        painel.add(lista, BorderLayout.CENTER);
        String[] opcoes = {"Entrar", CADASTRAR, "Cancelar"};
        int opcao = JOptionPane.showOptionDialog(this, painel, "Entrar como " + tipo,
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, opcoes, opcoes[0]);
        return switch (opcao) {
            case 0 -> lista.getSelectedItem();
            case 1 -> CADASTRAR;
            default -> null;
        };
    }
}
