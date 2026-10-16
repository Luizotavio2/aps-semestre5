package com.jdbc.view;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;

/** Janela principal: uma aba de CRUD para cada entidade. */
public class MainFrame extends JFrame {

    public MainFrame() {
        super("Barbearia - Sistema de Agendamentos");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JTabbedPane abas = new JTabbedPane();
        abas.addTab("Agendamentos", new AgendamentoPanel());
        abas.addTab("Clientes", new ClientePanel());
        abas.addTab("Barbeiros", new BarbeiroPanel());
        // ao trocar de aba, recarrega para refletir alterações feitas nas outras telas
        abas.addChangeListener(e -> ((CrudPanel<?>) abas.getSelectedComponent()).recarregar());

        JLabel titulo = new JLabel("Barbearia - Agendamentos");
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 18f));
        titulo.setBorder(BorderFactory.createEmptyBorder(10, 12, 0, 12));

        add(titulo, BorderLayout.NORTH);
        add(abas, BorderLayout.CENTER);
        setJMenuBar(criarMenu());

        setMinimumSize(new Dimension(900, 500));
        setSize(1100, 650);
        setLocationRelativeTo(null);
    }

    private JMenuBar criarMenu() {
        JMenuItem sair = new JMenuItem("Sair");
        sair.addActionListener(e -> dispose());
        JMenu arquivo = new JMenu("Arquivo");
        arquivo.add(sair);

        JMenuItem sobre = new JMenuItem("Sobre");
        sobre.addActionListener(e -> Mensagens.info(this, """
                Sistema de agendamento para barbearia.
                CRUD de clientes, barbeiros e agendamentos.
                Java + Swing, arquitetura MVC/DAO e MySQL."""));
        JMenu ajuda = new JMenu("Ajuda");
        ajuda.add(sobre);

        JMenuBar menu = new JMenuBar();
        menu.add(arquivo);
        menu.add(ajuda);
        return menu;
    }
}
