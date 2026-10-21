package com.jdbc.view;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import java.awt.Component;
import java.awt.GridLayout;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.Locale;

/** Funções comuns às telas: mensagens, formulários, tabelas e tratamento de erros. */
final class Telas {
    private static final NumberFormat MOEDA = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

    /** Ação que acessa o banco de dados, executada por {@link #executar}. */
    interface Acao {
        void executar() throws SQLException;
    }

    private Telas() {
    }

    /**
     * Executa a ação e mostra a mensagem adequada se algo der errado:
     * entrada inválida (IllegalArgumentException) gera um aviso; falha no banco, um erro.
     *
     * @return true se a ação foi concluída sem erros
     */
    static boolean executar(Component pai, Acao acao) {
        try {
            acao.executar();
            return true;
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(pai, e.getMessage(), "Atenção", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(pai, "Erro no banco de dados:\n" + e.getMessage(), "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }

    /** Exibe um formulário com um rótulo para cada campo; devolve true se o usuário clicou em OK. */
    static boolean formulario(Component pai, String titulo, String[] rotulos, JComponent[] campos) {
        JPanel painel = new JPanel(new GridLayout(0, 2, 8, 8));
        for (int i = 0; i < campos.length; i++) {
            painel.add(new JLabel(rotulos[i]));
            painel.add(campos[i]);
        }
        return JOptionPane.showConfirmDialog(pai, painel, titulo, JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION;
    }

    static boolean confirmar(Component pai, String pergunta) {
        return JOptionPane.showConfirmDialog(pai, pergunta, "Confirmação", JOptionPane.YES_NO_OPTION)
                == JOptionPane.YES_OPTION;
    }

    static void info(Component pai, String mensagem) {
        JOptionPane.showMessageDialog(pai, mensagem);
    }

    /** Tabela somente leitura com seleção de uma linha por vez. */
    static JTable tabela(String... colunas) {
        JTable tabela = new JTable(new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int linha, int coluna) {
                return false;
            }
        });
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.getTableHeader().setReorderingAllowed(false);
        tabela.setRowHeight(24);
        return tabela;
    }

    static String moeda(BigDecimal valor) {
        return MOEDA.format(valor);
    }
}
