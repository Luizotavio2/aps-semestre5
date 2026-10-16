package com.jdbc.view;

import com.jdbc.controller.ValidacaoException;
import com.jdbc.dao.Criterio;
import com.jdbc.dao.DAOException;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * Tela base de CRUD: barra de busca, ordenação por múltiplos critérios, tabela de listagem
 * e botões Novo / Editar / Excluir. Cada entidade implementa apenas o que lhe é específico.
 */
abstract class CrudPanel<T> extends JPanel {
    protected final JTextField campoBusca = new JTextField(22);
    private final String nomeEntidade;
    private final EntidadeTableModel<T> modelo;
    private final JTable tabela;
    private final JLabel status = new JLabel();
    private OrdenacaoPanel ordenacao;

    protected CrudPanel(String nomeEntidade, List<EntidadeTableModel.Coluna<T>> colunas) {
        super(new BorderLayout(8, 8));
        this.nomeEntidade = nomeEntidade;
        this.modelo = new EntidadeTableModel<>(colunas);
        this.tabela = new JTable(modelo);
    }

    /** Monta os componentes e carrega os dados. Deve ser chamado ao final do construtor da subclasse. */
    protected void montarTela(List<String> camposOrdenacao) {
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 2));
        filtros.add(new JLabel("Buscar:"));
        filtros.add(campoBusca);
        adicionarFiltrosExtras(filtros);
        JButton buscar = new JButton("Buscar");
        JButton limpar = new JButton("Limpar filtros");
        filtros.add(buscar);
        filtros.add(limpar);
        campoBusca.setToolTipText(dicaBusca());
        campoBusca.addActionListener(e -> recarregar());
        buscar.addActionListener(e -> recarregar());
        limpar.addActionListener(e -> {
            campoBusca.setText("");
            limparFiltrosExtras();
            recarregar();
        });

        ordenacao = new OrdenacaoPanel(camposOrdenacao, 3);
        ordenacao.aoAlterar(this::recarregar);

        JPanel topo = new JPanel();
        topo.setLayout(new BoxLayout(topo, BoxLayout.Y_AXIS));
        topo.add(filtros);
        topo.add(ordenacao);
        add(topo, BorderLayout.NORTH);

        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.setRowHeight(24);
        tabela.setAutoCreateRowSorter(false);
        tabela.getTableHeader().setReorderingAllowed(false);
        tabela.getColumnModel().getColumn(0).setMaxWidth(70); // coluna "Código"
        tabela.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && tabela.getSelectedRow() >= 0) {
                    editar();
                }
            }
        });
        add(new JScrollPane(tabela), BorderLayout.CENTER);

        JButton novo = new JButton("Novo");
        JButton editar = new JButton("Editar");
        JButton excluir = new JButton("Excluir");
        JButton atualizar = new JButton("Atualizar lista");
        novo.addActionListener(e -> novo());
        editar.addActionListener(e -> editar());
        excluir.addActionListener(e -> excluirSelecionado());
        atualizar.addActionListener(e -> recarregar());

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        botoes.add(novo);
        botoes.add(editar);
        botoes.add(excluir);
        botoes.add(atualizar);
        JPanel rodape = new JPanel(new BorderLayout());
        rodape.add(botoes, BorderLayout.WEST);
        rodape.add(status, BorderLayout.EAST);
        add(rodape, BorderLayout.SOUTH);

        recarregar();
    }

    /** Ponto de extensão para filtros adicionais (ex.: período). */
    protected void adicionarFiltrosExtras(JPanel painel) {
    }

    protected void limparFiltrosExtras() {
    }

    protected abstract String dicaBusca();

    protected List<Criterio> criteriosOrdenacao() {
        return ordenacao.getCriterios();
    }

    /** Busca os dados no banco aplicando filtros e ordenação atuais e atualiza a tabela. */
    public void recarregar() {
        try {
            List<T> dados = buscar();
            modelo.setDados(dados);
            status.setText(dados.size() + " registro(s)");
        } catch (ValidacaoException e) {
            Mensagens.aviso(this, e.getMessage());
        } catch (DAOException e) {
            Mensagens.erro(this, e.getMessage());
        }
    }

    private T selecionado() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) {
            Mensagens.aviso(this, "Selecione um " + nomeEntidade + " na tabela.");
            return null;
        }
        return modelo.getLinha(tabela.convertRowIndexToModel(linha));
    }

    private void novo() {
        if (abrirFormulario(null)) {
            recarregar();
        }
    }

    private void editar() {
        T entidade = selecionado();
        if (entidade != null && abrirFormulario(entidade)) {
            recarregar();
        }
    }

    private void excluirSelecionado() {
        T entidade = selecionado();
        if (entidade == null || !Mensagens.confirmar(this, "Deseja realmente excluir " + descrever(entidade) + "?")) {
            return;
        }
        try {
            excluir(entidade);
            recarregar();
            Mensagens.info(this, "Registro excluído com sucesso.");
        } catch (ValidacaoException e) {
            Mensagens.aviso(this, e.getMessage());
        } catch (DAOException e) {
            Mensagens.erro(this, e.getMessage());
        }
    }

    protected abstract List<T> buscar() throws ValidacaoException, DAOException;

    /**
     * Abre o formulário de cadastro (entidade nula) ou de edição.
     *
     * @return true se o registro foi salvo
     */
    protected abstract boolean abrirFormulario(T entidade);

    protected abstract void excluir(T entidade) throws ValidacaoException, DAOException;

    /** Descrição curta usada na confirmação de exclusão. */
    protected abstract String descrever(T entidade);
}
