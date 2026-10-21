package com.jdbc.view;

import com.jdbc.controller.AgendamentoController;
import com.jdbc.controller.BarbeiroController;
import com.jdbc.model.Agendamento;
import com.jdbc.model.Barbeiro;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

/** Área do barbeiro: agenda da barbearia com busca e ordenação por dois critérios. */
class TelaBarbeiro extends JFrame {
    private final AgendamentoController agendamentoController = new AgendamentoController();
    private final BarbeiroController barbeiroController = new BarbeiroController();

    private final TelaInicial telaInicial;
    private Barbeiro barbeiro;
    private final JLabel saudacao = new JLabel();
    private final JTextField busca = new JTextField(18);
    private final JCheckBox somenteMeus = new JCheckBox("Somente os meus agendamentos");
    private final JComboBox<String> ordem1 = new JComboBox<>(AgendamentoController.CAMPOS_ORDENACAO);
    private final JComboBox<String> ordem2 = new JComboBox<>(AgendamentoController.CAMPOS_ORDENACAO);
    private final JCheckBox decrescente1 = new JCheckBox("Decrescente");
    private final JCheckBox decrescente2 = new JCheckBox("Decrescente");
    private final JLabel total = new JLabel();
    private final JTable tabela = Telas.tabela("Data/hora", "Cliente", "Telefone", "Barbeiro", "Serviço", "Valor");
    /** Agendamentos exibidos, na mesma ordem das linhas da tabela. */
    private final List<Agendamento> agendamentos = new ArrayList<>();

    TelaBarbeiro(TelaInicial telaInicial, Barbeiro barbeiro) {
        super("Barbearia - Área do Barbeiro");
        this.telaInicial = telaInicial;
        this.barbeiro = barbeiro;
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        saudacao.setFont(saudacao.getFont().deriveFont(Font.BOLD, 18f));
        atualizarSaudacao();
        ordem2.setSelectedItem("Cliente");

        // qualquer alteração nos filtros ou na ordenação recarrega a tabela
        JButton buscar = new JButton("Buscar");
        buscar.addActionListener(e -> carregar());
        busca.addActionListener(e -> carregar());
        somenteMeus.addActionListener(e -> carregar());
        decrescente1.addActionListener(e -> carregar());
        decrescente2.addActionListener(e -> carregar());
        ordem1.addActionListener(e -> carregar());
        ordem2.addActionListener(e -> carregar());

        JPanel linhaBusca = new JPanel(new FlowLayout(FlowLayout.LEFT));
        linhaBusca.add(new JLabel("Buscar (cliente, barbeiro ou serviço):"));
        linhaBusca.add(busca);
        linhaBusca.add(buscar);
        linhaBusca.add(somenteMeus);
        JPanel linhaOrdem = new JPanel(new FlowLayout(FlowLayout.LEFT));
        linhaOrdem.add(new JLabel("Ordenar por:"));
        linhaOrdem.add(ordem1);
        linhaOrdem.add(decrescente1);
        linhaOrdem.add(new JLabel("   Depois por:"));
        linhaOrdem.add(ordem2);
        linhaOrdem.add(decrescente2);

        JPanel topo = new JPanel();
        topo.setLayout(new BoxLayout(topo, BoxLayout.Y_AXIS));
        saudacao.setAlignmentX(LEFT_ALIGNMENT);
        linhaBusca.setAlignmentX(LEFT_ALIGNMENT);
        linhaOrdem.setAlignmentX(LEFT_ALIGNMENT);
        topo.add(saudacao);
        topo.add(linhaBusca);
        topo.add(linhaOrdem);

        JButton cancelar = new JButton("Cancelar agendamento");
        JButton meusDados = new JButton("Meus dados");
        JButton excluirConta = new JButton("Excluir conta");
        JButton sair = new JButton("Sair");
        cancelar.addActionListener(e -> cancelar());
        meusDados.addActionListener(e -> editarDados());
        excluirConta.addActionListener(e -> excluirConta());
        sair.addActionListener(e -> dispose());

        JPanel acoes = new JPanel(new FlowLayout(FlowLayout.LEFT));
        acoes.add(cancelar);
        acoes.add(total);
        JPanel conta = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        conta.add(meusDados);
        conta.add(excluirConta);
        conta.add(sair);
        JPanel rodape = new JPanel(new BorderLayout());
        rodape.add(acoes, BorderLayout.WEST);
        rodape.add(conta, BorderLayout.EAST);

        JPanel conteudo = new JPanel(new BorderLayout(8, 8));
        conteudo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        conteudo.add(topo, BorderLayout.NORTH);
        conteudo.add(new JScrollPane(tabela), BorderLayout.CENTER);
        conteudo.add(rodape, BorderLayout.SOUTH);
        setContentPane(conteudo);

        setSize(980, 520);
        setLocationRelativeTo(null);
        carregar();
    }

    /** Ao fechar esta tela, volta para a tela inicial. */
    @Override
    public void dispose() {
        super.dispose();
        telaInicial.setVisible(true);
    }

    private void atualizarSaudacao() {
        saudacao.setText("Olá, " + barbeiro.getNome() + "! Agenda da barbearia:");
    }

    private void carregar() {
        Telas.executar(this, () -> {
            agendamentos.clear();
            agendamentos.addAll(agendamentoController.listar(somenteMeus.isSelected() ? barbeiro : null,
                    busca.getText(), (String) ordem1.getSelectedItem(), decrescente1.isSelected(),
                    (String) ordem2.getSelectedItem(), decrescente2.isSelected()));
            DefaultTableModel modelo = (DefaultTableModel) tabela.getModel();
            modelo.setRowCount(0);
            for (Agendamento a : agendamentos) {
                modelo.addRow(new Object[]{AgendamentoController.formatar(a.getDataHora()),
                        a.getCliente().getNome(), a.getCliente().getTelefone(), a.getBarbeiro().getNome(),
                        a.getServico(), Telas.moeda(a.getValor())});
            }
            total.setText(agendamentos.size() + " agendamento(s)");
        });
    }

    private void cancelar() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) {
            Telas.info(this, "Selecione um agendamento na tabela.");
            return;
        }
        Agendamento agendamento = agendamentos.get(linha);
        if (Telas.confirmar(this, "Cancelar o agendamento de " + agendamento.getCliente().getNome() + " em "
                + AgendamentoController.formatar(agendamento.getDataHora()) + "?")
                && Telas.executar(this, () -> agendamentoController.excluir(agendamento))) {
            carregar();
        }
    }

    private void editarDados() {
        Barbeiro atualizado = formulario(this, barbeiroController, barbeiro);
        if (atualizado != null) {
            barbeiro = atualizado;
            atualizarSaudacao();
            carregar();
        }
    }

    private void excluirConta() {
        if (Telas.confirmar(this, "Deseja realmente excluir sua conta?")
                && Telas.executar(this, () -> barbeiroController.excluir(barbeiro))) {
            Telas.info(this, "Conta excluída.");
            dispose();
        }
    }

    /**
     * Formulário de cadastro (atual nulo) ou de edição dos dados do barbeiro.
     *
     * @return o barbeiro salvo, ou null se o usuário cancelar
     */
    static Barbeiro formulario(Component pai, BarbeiroController controller, Barbeiro atual) {
        JTextField nome = new JTextField(atual == null ? "" : atual.getNome(), 20);
        JTextField telefone = new JTextField(atual == null ? "" : atual.getTelefone());
        String[] rotulos = {"Nome:", "Telefone (com DDD):"};
        JComponent[] campos = {nome, telefone};
        String titulo = atual == null ? "Cadastro de barbeiro" : "Meus dados";

        Barbeiro[] salvo = new Barbeiro[1];
        while (Telas.formulario(pai, titulo, rotulos, campos)) {
            Integer id = atual == null ? null : atual.getId();
            if (Telas.executar(pai, () -> salvo[0] = controller.salvar(id, nome.getText(), telefone.getText()))) {
                return salvo[0];
            }
        }
        return null;
    }
}
