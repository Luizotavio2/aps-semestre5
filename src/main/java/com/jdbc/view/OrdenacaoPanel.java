package com.jdbc.view;

import com.jdbc.dao.Criterio;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;

/** Seleção de vários critérios de ordenação encadeados ("ordenar por X, depois por Y..."). */
class OrdenacaoPanel extends JPanel {
    private static final String NENHUM = "(nenhum)";
    private static final String[] DIRECOES = {"Crescente", "Decrescente"};

    private final List<JComboBox<String>> campos = new ArrayList<>();
    private final List<JComboBox<String>> direcoes = new ArrayList<>();

    OrdenacaoPanel(List<String> opcoes, int niveis) {
        super(new FlowLayout(FlowLayout.LEFT, 6, 2));
        for (int i = 0; i < niveis; i++) {
            JComboBox<String> campo = new JComboBox<>();
            campo.addItem(NENHUM);
            opcoes.forEach(campo::addItem);
            if (i == 0 && !opcoes.isEmpty()) {
                campo.setSelectedIndex(1);
            }
            JComboBox<String> direcao = new JComboBox<>(DIRECOES);
            add(new JLabel(i == 0 ? "Ordenar por:" : "depois por:"));
            add(campo);
            add(direcao);
            campos.add(campo);
            direcoes.add(direcao);
        }
    }

    /** Executa a ação sempre que algum critério ou direção for alterado. */
    void aoAlterar(Runnable acao) {
        campos.forEach(c -> c.addActionListener(e -> acao.run()));
        direcoes.forEach(d -> d.addActionListener(e -> acao.run()));
    }

    List<Criterio> getCriterios() {
        List<Criterio> criterios = new ArrayList<>();
        for (int i = 0; i < campos.size(); i++) {
            String campo = (String) campos.get(i).getSelectedItem();
            if (campo != null && !NENHUM.equals(campo)) {
                criterios.add(new Criterio(campo, direcoes.get(i).getSelectedIndex() == 0));
            }
        }
        return criterios;
    }
}
