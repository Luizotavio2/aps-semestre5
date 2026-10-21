package com.jdbc.view;

import com.jdbc.controller.AgendamentoController;
import com.jdbc.controller.BarbeiroController;
import com.jdbc.controller.ClienteController;
import com.jdbc.model.Agendamento;
import com.jdbc.model.Barbeiro;
import com.jdbc.model.Cliente;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.MaskFormatter;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** Área do cliente: lista os seus agendamentos e permite agendar, remarcar e cancelar. */
class TelaCliente extends JFrame {
    private final AgendamentoController agendamentoController = new AgendamentoController();
    private final BarbeiroController barbeiroController = new BarbeiroController();
    private final ClienteController clienteController = new ClienteController();

    private final TelaInicial telaInicial;
    private Cliente cliente;
    private final JLabel saudacao = new JLabel();
    private final JTable tabela = Telas.tabela("Data/hora", "Barbeiro", "Serviço", "Valor");
    /** Agendamentos exibidos, na mesma ordem das linhas da tabela. */
    private final List<Agendamento> agendamentos = new ArrayList<>();

    TelaCliente(TelaInicial telaInicial, Cliente cliente) {
        super("Barbearia - Área do Cliente");
        this.telaInicial = telaInicial;
        this.cliente = cliente;
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        saudacao.setFont(saudacao.getFont().deriveFont(Font.BOLD, 18f));
        atualizarSaudacao();

        JButton novo = new JButton("Novo agendamento");
        JButton remarcar = new JButton("Remarcar");
        JButton cancelar = new JButton("Cancelar agendamento");
        JButton meusDados = new JButton("Meus dados");
        JButton excluirConta = new JButton("Excluir conta");
        JButton sair = new JButton("Sair");
        novo.addActionListener(e -> agendar(null));
        remarcar.addActionListener(e -> {
            Agendamento selecionado = selecionado();
            if (selecionado != null) {
                agendar(selecionado);
            }
        });
        cancelar.addActionListener(e -> cancelar());
        meusDados.addActionListener(e -> editarDados());
        excluirConta.addActionListener(e -> excluirConta());
        sair.addActionListener(e -> dispose());

        JPanel acoes = new JPanel(new FlowLayout(FlowLayout.LEFT));
        acoes.add(novo);
        acoes.add(remarcar);
        acoes.add(cancelar);
        JPanel conta = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        conta.add(meusDados);
        conta.add(excluirConta);
        conta.add(sair);
        JPanel rodape = new JPanel(new BorderLayout());
        rodape.add(acoes, BorderLayout.WEST);
        rodape.add(conta, BorderLayout.EAST);

        JPanel conteudo = new JPanel(new BorderLayout(8, 8));
        conteudo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        conteudo.add(saudacao, BorderLayout.NORTH);
        conteudo.add(new JScrollPane(tabela), BorderLayout.CENTER);
        conteudo.add(rodape, BorderLayout.SOUTH);
        setContentPane(conteudo);

        setSize(820, 450);
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
        saudacao.setText("Olá, " + cliente.getNome() + "! Estes são os seus agendamentos:");
    }

    private void carregar() {
        Telas.executar(this, () -> {
            agendamentos.clear();
            agendamentos.addAll(agendamentoController.listarPorCliente(cliente));
            DefaultTableModel modelo = (DefaultTableModel) tabela.getModel();
            modelo.setRowCount(0);
            for (Agendamento a : agendamentos) {
                modelo.addRow(new Object[]{AgendamentoController.formatar(a.getDataHora()),
                        a.getBarbeiro().getNome(), a.getServico(), Telas.moeda(a.getValor())});
            }
        });
    }

    private Agendamento selecionado() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) {
            Telas.info(this, "Selecione um agendamento na tabela.");
            return null;
        }
        return agendamentos.get(linha);
    }

    /** Formulário de novo agendamento (atual nulo) ou de remarcação. */
    private void agendar(Agendamento atual) {
        List<Barbeiro> barbeiros = new ArrayList<>();
        if (!Telas.executar(this, () -> barbeiros.addAll(barbeiroController.listarTodos()))) {
            return;
        }
        JComboBox<Barbeiro> barbeiro = new JComboBox<>(barbeiros.toArray(new Barbeiro[0]));
        List<String> servicos = new ArrayList<>(AgendamentoController.SERVICOS.keySet());
        JComboBox<String> servico = new JComboBox<>();
        for (Map.Entry<String, BigDecimal> s : AgendamentoController.SERVICOS.entrySet()) {
            servico.addItem(s.getKey() + " - " + Telas.moeda(s.getValue()));
        }
        JFormattedTextField dataHora = campoDataHora();

        if (atual != null) {
            for (Barbeiro b : barbeiros) {
                if (b.getId().equals(atual.getBarbeiro().getId())) {
                    barbeiro.setSelectedItem(b);
                }
            }
            servico.setSelectedIndex(servicos.indexOf(atual.getServico()));
            dataHora.setText(AgendamentoController.formatar(atual.getDataHora()));
        }

        String titulo = atual == null ? "Novo agendamento" : "Remarcar agendamento";
        String[] rotulos = {"Barbeiro:", "Serviço:", "Data e hora (dd/mm/aaaa hh:mm):"};
        JComponent[] campos = {barbeiro, servico, dataHora};
        // repete o formulário até salvar com sucesso ou o usuário cancelar
        while (Telas.formulario(this, titulo, rotulos, campos)) {
            Integer id = atual == null ? null : atual.getId();
            String nomeServico = servico.getSelectedIndex() < 0 ? null : servicos.get(servico.getSelectedIndex());
            if (Telas.executar(this, () -> agendamentoController.salvar(id, cliente,
                    (Barbeiro) barbeiro.getSelectedItem(), dataHora.getText(), nomeServico))) {
                Telas.info(this, "Agendamento salvo com sucesso!");
                carregar();
                return;
            }
        }
    }

    private void cancelar() {
        Agendamento agendamento = selecionado();
        if (agendamento != null && Telas.confirmar(this, "Cancelar o agendamento de "
                + AgendamentoController.formatar(agendamento.getDataHora()) + "?")
                && Telas.executar(this, () -> agendamentoController.excluir(agendamento))) {
            carregar();
        }
    }

    private void editarDados() {
        Cliente atualizado = formulario(this, clienteController, cliente);
        if (atualizado != null) {
            cliente = atualizado;
            atualizarSaudacao();
        }
    }

    private void excluirConta() {
        if (Telas.confirmar(this, "Deseja realmente excluir sua conta?")
                && Telas.executar(this, () -> clienteController.excluir(cliente))) {
            Telas.info(this, "Conta excluída.");
            dispose();
        }
    }

    /**
     * Formulário de cadastro (atual nulo) ou de edição dos dados do cliente.
     *
     * @return o cliente salvo, ou null se o usuário cancelar
     */
    static Cliente formulario(Component pai, ClienteController controller, Cliente atual) {
        JTextField nome = new JTextField(atual == null ? "" : atual.getNome(), 20);
        JTextField telefone = new JTextField(atual == null ? "" : atual.getTelefone());
        JTextField email = new JTextField(atual == null || atual.getEmail() == null ? "" : atual.getEmail());
        String[] rotulos = {"Nome:", "Telefone (com DDD):", "E-mail (opcional):"};
        JComponent[] campos = {nome, telefone, email};
        String titulo = atual == null ? "Cadastro de cliente" : "Meus dados";

        Cliente[] salvo = new Cliente[1];
        while (Telas.formulario(pai, titulo, rotulos, campos)) {
            Integer id = atual == null ? null : atual.getId();
            if (Telas.executar(pai, () -> salvo[0] = controller.salvar(id, nome.getText(), telefone.getText(),
                    email.getText()))) {
                return salvo[0];
            }
        }
        return null;
    }

    /** Campo com máscara para data e hora: __/__/____ __:__ */
    private static JFormattedTextField campoDataHora() {
        try {
            MaskFormatter mascara = new MaskFormatter("##/##/#### ##:##");
            mascara.setPlaceholderCharacter('_');
            JFormattedTextField campo = new JFormattedTextField(mascara);
            campo.setFocusLostBehavior(JFormattedTextField.COMMIT); // mantém o texto digitado para ser validado
            return campo;
        } catch (ParseException e) {
            throw new IllegalStateException(e); // a máscara é fixa e válida
        }
    }
}
