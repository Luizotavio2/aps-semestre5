package com.jdbc.view;

import com.jdbc.controller.AgendamentoController;
import com.jdbc.controller.BarbeiroController;
import com.jdbc.controller.ClienteController;
import com.jdbc.model.Agendamento;
import com.jdbc.model.Barbeiro;
import com.jdbc.model.Cliente;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

/** Área do cliente: vê seus agendamentos e pode agendar, remarcar e cancelar. */
class TelaCliente extends JFrame {
    private final AgendamentoController agendamentoController = new AgendamentoController();
    private final BarbeiroController barbeiroController = new BarbeiroController();
    private final ClienteController clienteController = new ClienteController();

    private Cliente cliente;
    private List<Agendamento> agendamentos = new ArrayList<>(); // mesma ordem das linhas da tabela
    private final DefaultTableModel modelo = new DefaultTableModel(new String[]{"Data/hora", "Barbeiro", "Serviço"}, 0);
    private final JTable tabela = new JTable(modelo);

    TelaCliente(Cliente cliente) {
        super("Área do Cliente - " + cliente.getNome());
        this.cliente = cliente;
        tabela.setDefaultEditor(Object.class, null); // impede editar as células

        JButton novo = new JButton("Novo agendamento");
        JButton remarcar = new JButton("Remarcar");
        JButton cancelar = new JButton("Cancelar agendamento");
        JButton meusDados = new JButton("Meus dados");
        JButton excluirConta = new JButton("Excluir conta");
        JButton sair = new JButton("Sair");

        novo.addActionListener(e -> agendar(null));
        remarcar.addActionListener(e -> remarcar());
        cancelar.addActionListener(e -> cancelar());
        meusDados.addActionListener(e -> editarDados());
        excluirConta.addActionListener(e -> excluirConta());
        sair.addActionListener(e -> sair());

        JPanel botoes = new JPanel();
        botoes.add(novo);
        botoes.add(remarcar);
        botoes.add(cancelar);
        botoes.add(meusDados);
        botoes.add(excluirConta);
        botoes.add(sair);

        add(new JScrollPane(tabela), BorderLayout.CENTER);
        add(botoes, BorderLayout.SOUTH);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 400);
        setLocationRelativeTo(null);
        carregar();
    }

    /** Busca os agendamentos do cliente no banco e preenche a tabela. */
    private void carregar() {
        try {
            agendamentos = agendamentoController.listarPorCliente(cliente);
            modelo.setRowCount(0); // limpa a tabela
            for (Agendamento a : agendamentos) {
                modelo.addRow(new Object[]{
                        AgendamentoController.formatar(a.getDataHora()), a.getBarbeiro().getNome(), a.getServico()});
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    /** Devolve o agendamento da linha selecionada, ou null se nenhuma estiver selecionada. */
    private Agendamento selecionado() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) {
            JOptionPane.showMessageDialog(this, "Selecione um agendamento na tabela.");
            return null;
        }
        return agendamentos.get(linha);
    }

    private void remarcar() {
        Agendamento agendamento = selecionado();
        if (agendamento != null) {
            agendar(agendamento);
        }
    }

    /** Formulário de novo agendamento (atual = null) ou de remarcação. */
    private void agendar(Agendamento atual) {
        try {
            JComboBox<Barbeiro> barbeiro = new JComboBox<>(barbeiroController.listarTodos().toArray(new Barbeiro[0]));
            JComboBox<String> servico = new JComboBox<>(AgendamentoController.SERVICOS);
            JTextField dataHora = new JTextField();
            int id = 0;

            // na remarcação, o formulário já vem preenchido
            if (atual != null) {
                id = atual.getId();
                for (int i = 0; i < barbeiro.getItemCount(); i++) {
                    if (barbeiro.getItemAt(i).getId() == atual.getBarbeiro().getId()) {
                        barbeiro.setSelectedIndex(i);
                    }
                }
                servico.setSelectedItem(atual.getServico());
                dataHora.setText(AgendamentoController.formatar(atual.getDataHora()));
            }

            Object[] campos = {"Barbeiro:", barbeiro, "Serviço:", servico, "Data e hora (dd/mm/aaaa hh:mm):", dataHora};
            int resposta = JOptionPane.showConfirmDialog(this, campos, "Agendamento", JOptionPane.OK_CANCEL_OPTION);
            if (resposta == JOptionPane.OK_OPTION) {
                agendamentoController.salvar(id, cliente, (Barbeiro) barbeiro.getSelectedItem(),
                        (String) servico.getSelectedItem(), dataHora.getText());
                carregar();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private void cancelar() {
        Agendamento agendamento = selecionado();
        if (agendamento == null) {
            return;
        }
        int resposta = JOptionPane.showConfirmDialog(this, "Cancelar este agendamento?", "Confirmação",
                JOptionPane.YES_NO_OPTION);
        if (resposta == JOptionPane.YES_OPTION) {
            try {
                agendamentoController.excluir(agendamento);
                carregar();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, e.getMessage());
            }
        }
    }

    private void editarDados() {
        Cliente atualizado = formularioCliente(this, cliente);
        if (atualizado != null) {
            cliente = atualizado;
            setTitle("Área do Cliente - " + cliente.getNome());
        }
    }

    private void excluirConta() {
        int resposta = JOptionPane.showConfirmDialog(this, "Deseja realmente excluir sua conta?", "Confirmação",
                JOptionPane.YES_NO_OPTION);
        if (resposta == JOptionPane.YES_OPTION) {
            try {
                clienteController.excluir(cliente);
                sair();
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

    /**
     * Formulário de cadastro (atual = null) ou de edição do cliente.
     * Devolve o cliente salvo, ou null se o usuário cancelar ou digitar algo inválido.
     */
    static Cliente formularioCliente(Component pai, Cliente atual) {
        JTextField nome = new JTextField(atual == null ? "" : atual.getNome());
        JTextField telefone = new JTextField(atual == null ? "" : atual.getTelefone());
        Object[] campos = {"Nome:", nome, "Telefone (DDD + número):", telefone};

        int resposta = JOptionPane.showConfirmDialog(pai, campos, "Dados do cliente", JOptionPane.OK_CANCEL_OPTION);
        if (resposta != JOptionPane.OK_OPTION) {
            return null;
        }
        try {
            int id = atual == null ? 0 : atual.getId();
            return new ClienteController().salvar(id, nome.getText(), telefone.getText());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(pai, e.getMessage());
            return null;
        }
    }
}
