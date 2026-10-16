package com.jdbc.view;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/** Modelo de tabela genérico: cada coluna sabe extrair seu valor de um objeto do modelo. */
class EntidadeTableModel<T> extends AbstractTableModel {

    record Coluna<T>(String titulo, Function<T, Object> valor) {
    }

    private final List<Coluna<T>> colunas;
    private List<T> dados = new ArrayList<>();

    EntidadeTableModel(List<Coluna<T>> colunas) {
        this.colunas = colunas;
    }

    void setDados(List<T> dados) {
        this.dados = new ArrayList<>(dados);
        fireTableDataChanged();
    }

    T getLinha(int linha) {
        return dados.get(linha);
    }

    @Override
    public int getRowCount() {
        return dados.size();
    }

    @Override
    public int getColumnCount() {
        return colunas.size();
    }

    @Override
    public String getColumnName(int coluna) {
        return colunas.get(coluna).titulo();
    }

    @Override
    public Object getValueAt(int linha, int coluna) {
        return colunas.get(coluna).valor().apply(dados.get(linha));
    }
}
