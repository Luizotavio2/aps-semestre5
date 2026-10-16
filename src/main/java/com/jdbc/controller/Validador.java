package com.jdbc.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.regex.Pattern;

/** Converte e valida os textos digitados na interface. */
final class Validador {
    static final DateTimeFormatter DATA = DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);
    static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm").withResolverStyle(ResolverStyle.STRICT);

    private static final Pattern NOME = Pattern.compile("[\\p{L} .'-]+");
    private static final Pattern EMAIL = Pattern.compile("[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+");
    private static final BigDecimal VALOR_MAXIMO = new BigDecimal("99999999.99");

    private Validador() {
    }

    static String texto(String valor, String campo, int tamanhoMaximo) throws ValidacaoException {
        String texto = valor == null ? "" : valor.trim().replaceAll("\\s+", " ");
        if (texto.isEmpty()) {
            throw new ValidacaoException("O campo \"" + campo + "\" é obrigatório.");
        }
        if (texto.length() > tamanhoMaximo) {
            throw new ValidacaoException("O campo \"" + campo + "\" deve ter no máximo " + tamanhoMaximo + " caracteres.");
        }
        return texto;
    }

    static String nome(String valor) throws ValidacaoException {
        String nome = texto(valor, "Nome", 256);
        if (nome.length() < 2 || !NOME.matcher(nome).matches()) {
            throw new ValidacaoException("Nome inválido: use ao menos 2 caracteres, apenas letras, espaços, ponto, apóstrofo ou hífen.");
        }
        return nome;
    }

    /** Aceita telefone com DDD em qualquer formato e devolve no padrão (11) 98765-4321. */
    static String telefone(String valor) throws ValidacaoException {
        String digitos = valor == null ? "" : valor.replaceAll("\\D", "");
        if (digitos.isEmpty()) {
            throw new ValidacaoException("O campo \"Telefone\" é obrigatório.");
        }
        if ((digitos.length() != 10 && digitos.length() != 11) || digitos.charAt(0) == '0') {
            throw new ValidacaoException("Telefone inválido: informe DDD + número (10 ou 11 dígitos), ex.: (11) 98765-4321.");
        }
        int meio = digitos.length() - 4;
        return "(" + digitos.substring(0, 2) + ") " + digitos.substring(2, meio) + "-" + digitos.substring(meio);
    }

    /** E-mail opcional: devolve null se vazio. */
    static String emailOpcional(String valor) throws ValidacaoException {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        String email = valor.trim();
        if (email.length() > 256 || !EMAIL.matcher(email).matches()) {
            throw new ValidacaoException("E-mail inválido. Exemplo de formato válido: nome@dominio.com");
        }
        return email;
    }

    static LocalDate data(String valor, String campo) throws ValidacaoException {
        LocalDate data = dataOpcional(valor, campo);
        if (data == null) {
            throw new ValidacaoException("O campo \"" + campo + "\" é obrigatório.");
        }
        return data;
    }

    /** Data no formato dd/MM/aaaa; campos vazios (ou só com a máscara) resultam em null. */
    static LocalDate dataOpcional(String valor, String campo) throws ValidacaoException {
        if (semDigitos(valor)) {
            return null;
        }
        try {
            return LocalDate.parse(valor.trim(), DATA);
        } catch (DateTimeParseException e) {
            throw new ValidacaoException("\"" + campo + "\" inválida: use o formato dd/mm/aaaa com uma data existente.");
        }
    }

    static LocalTime hora(String valor, String campo) throws ValidacaoException {
        if (semDigitos(valor)) {
            throw new ValidacaoException("O campo \"" + campo + "\" é obrigatório.");
        }
        try {
            return LocalTime.parse(valor.trim(), HORA);
        } catch (DateTimeParseException e) {
            throw new ValidacaoException("\"" + campo + "\" inválida: use o formato hh:mm (00:00 a 23:59).");
        }
    }

    /** Valor monetário: aceita "45", "45,50", "1.234,56", "45.5" ou "R$ 45,00". */
    static BigDecimal valor(String valor) throws ValidacaoException {
        String texto = valor == null ? "" : valor.replace("R$", "").replace(" ", "").trim();
        if (texto.isEmpty()) {
            throw new ValidacaoException("O campo \"Valor\" é obrigatório.");
        }
        if (texto.contains(",")) {
            texto = texto.replace(".", "").replace(',', '.');
        }
        BigDecimal numero;
        try {
            numero = new BigDecimal(texto);
        } catch (NumberFormatException e) {
            throw new ValidacaoException("Valor inválido: informe um número, ex.: 45,00");
        }
        if (numero.signum() < 0) {
            throw new ValidacaoException("O valor não pode ser negativo.");
        }
        if (numero.compareTo(VALOR_MAXIMO) > 0) {
            throw new ValidacaoException("O valor informado é muito alto.");
        }
        return numero.setScale(2, RoundingMode.HALF_UP);
    }

    private static boolean semDigitos(String valor) {
        return valor == null || valor.chars().noneMatch(Character::isDigit);
    }
}
