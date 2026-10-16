package com.jdbc.view;

import javax.swing.JFormattedTextField;
import javax.swing.text.MaskFormatter;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.temporal.TemporalAccessor;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/** Formatação de valores para exibição e campos com máscara. */
final class Formatos {
    static final DateTimeFormatter DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm");
    static final DateTimeFormatter DATA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final NumberFormat MOEDA = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR"));
    private static final NumberFormat DECIMAL = NumberFormat.getNumberInstance(Locale.forLanguageTag("pt-BR"));

    static {
        DECIMAL.setMinimumFractionDigits(2);
        DECIMAL.setMaximumFractionDigits(2);
        DECIMAL.setGroupingUsed(false);
    }

    private Formatos() {
    }

    static String formatar(TemporalAccessor valor, DateTimeFormatter formato) {
        return valor == null ? "" : formato.format(valor);
    }

    static String moeda(BigDecimal valor) {
        return valor == null ? "" : MOEDA.format(valor);
    }

    /** Valor no formato digitável, ex.: 45,00 */
    static String decimal(BigDecimal valor) {
        return valor == null ? "" : DECIMAL.format(valor);
    }

    static JFormattedTextField campoData() {
        return campoComMascara("##/##/####", 9);
    }

    static JFormattedTextField campoHora() {
        return campoComMascara("##:##", 5);
    }

    private static JFormattedTextField campoComMascara(String mascara, int colunas) {
        try {
            MaskFormatter formatter = new MaskFormatter(mascara);
            formatter.setPlaceholderCharacter('_');
            JFormattedTextField campo = new JFormattedTextField(formatter);
            // mantém o texto digitado mesmo incompleto; a validação fica a cargo do controller
            campo.setFocusLostBehavior(JFormattedTextField.PERSIST);
            campo.setColumns(colunas);
            return campo;
        } catch (ParseException e) {
            throw new IllegalArgumentException("Máscara inválida: " + mascara, e);
        }
    }
}
