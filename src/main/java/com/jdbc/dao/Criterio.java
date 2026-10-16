package com.jdbc.dao;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;

/**
 * Um critério de ordenação: o nome de um campo (como exibido na interface) e a direção.
 * Vários critérios em sequência permitem ordenar por mais de um campo.
 */
public record Criterio(String campo, boolean crescente) {

    /**
     * Monta a cláusula ORDER BY a partir dos critérios. Apenas campos presentes no mapa
     * campo -> coluna são aceitos, o que impede injeção de SQL pela ordenação.
     */
    static String montarOrderBy(List<Criterio> criterios, Map<String, String> colunas, String padrao) {
        StringJoiner orderBy = new StringJoiner(", ", " ORDER BY ", "");
        orderBy.setEmptyValue(" ORDER BY " + padrao);
        Set<String> usadas = new HashSet<>();
        if (criterios != null) {
            for (Criterio criterio : criterios) {
                String coluna = colunas.get(criterio.campo());
                if (coluna != null && usadas.add(coluna)) {
                    orderBy.add(coluna + (criterio.crescente() ? " ASC" : " DESC"));
                }
            }
        }
        return orderBy.toString();
    }
}
