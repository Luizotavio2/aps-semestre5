package com.jdbc.view;

import com.jdbc.controller.AgendamentoController;
import com.jdbc.model.Agendamento;
import com.jdbc.model.Barbeiro;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

/** Área do barbeiro: agenda completa da barbearia, com busca e ordenação por dois critérios. */
class TelaBarbeiro extends JFrame {
    private final AgendamentoController agendamentoController = new AgendamentoController();

    private final JTextField busca = new JTextField(15);
    private final JComboBox<String> ordem1 = new JComboBox<>(AgendamentoController.ORDENACOES);
    private final JComboBox<String> ordem2 = new JComboBox<>(AgendamentoController.ORDENACOES);
    private List<Agendamento> agendamentos = new ArrayList<>(); // mesma ordem das linhas da tabela
    private final DefaultTableModel modelo = new DefaultTableModel(
            new String[]{"Data/hora", "Cliente", "Telefone", "Barbeiro", "Serviço"}, 0);
    private final JTable tabela = new JTable(modelo);

    TelaBarbeiro(Barbeiro barbeiro) {
        super("Área do Barbeiro - " + barbeiro.getNome());
        tabela.setDefaultEditor(Object.class, null); // impede editar as células
        ordem2.setSelectedItem("Cliente");

        JButton buscar = new JButton("Buscar");
        buscar.addActionListener(e -> carregar());

        JPanel filtros = new JPanel();
        filtros.add(new JLabel("Buscar (cliente, barbeiro ou serviço):"));
        filtros.add(busca);
        filtros.add(new JLabel("Ordenar por:"));
        filtros.add(ordem1);
        filtros.add(new JLabel("e depois por:"));
        filtros.add(ordem2);
        filtros.add(buscar);

        JButton cancelar = new JButton("Cancelar agendamento");
        JButton sair = new JButton("Sair");
        cancelar.addActionListener(e -> cancelar());
        sair.addActionListener(e -> sair());

        JPanel botoes = new JPanel();
        botoes.add(cancelar);
        botoes.add(sair);

        add(filtros, BorderLayout.NORTH);
        add(new JScrollPane(tabela), BorderLayout.CENTER);
        add(botoes, BorderLayout.SOUTH);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(950, 450);
        setLocationRelativeTo(null);
        carregar();
    }

    /** Busca os agendamentos no banco, com o filtro e a ordenação escolhidos, e preenche a tabela. */
    private void carregar() {
        try {
            agendamentos = agendamentoController.listar(busca.getText(),
                    (String) ordem1.getSelectedItem(), (String) ordem2.getSelectedItem());
            modelo.setRowCount(0); // limpa a tabela
            for (Agendamento a : agendamentos) {
                modelo.addRow(new Object[]{AgendamentoController.formatar(a.getDataHora()),
                        a.getCliente().getNome(), a.getCliente().getTelefone(), a.getBarbeiro().getNome(),
                        a.getServico()});
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private void cancelar() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) {
            JOptionPane.showMessageDialog(this, "Selecione um agendamento na tabela.");
            return;
        }
        int resposta = JOptionPane.showConfirmDialog(this, "Cancelar este agendamento?", "Confirmação",
                JOptionPane.YES_NO_OPTION);
        if (resposta == JOptionPane.YES_OPTION) {
            try {
                agendamentoController.excluir(agendamentos.get(linha));
                carregar();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, e.getMessage());
            }
        }
    }

    /** Fecha esta tela e volta para a tela inicial. */
    private void sair() {
        dispose();
        new TelaInicial().setVisible(true);
    }
}
