package com.jdbc.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

/**
 * Valida e converte os textos digitados na interface.
 * Entradas inválidas geram IllegalArgumentException com uma mensagem para o usuário.
 */
final class Validador {
    static final DateTimeFormatter DATA_HORA =
            DateTimeFormatter.ofPattern("dd/MM/uuuu HH:mm").withResolverStyle(ResolverStyle.STRICT);

    private Validador() {
    }

    static String nome(String valor) {
        String nome = valor == null ? "" : valor.trim();
        if (nome.length() < 2 || nome.length() > 100 || !nome.matches("[\\p{L} .'-]+")) {
            throw new IllegalArgumentException("Nome inválido: use de 2 a 100 caracteres, apenas letras e espaços.");
        }
        return nome;
    }

    /** Aceita telefone com DDD em qualquer formato e devolve no padrão (11) 98765-4321. */
    static String telefone(String valor) {
        String digitos = valor == null ? "" : valor.replaceAll("\\D", "");
        if (digitos.length() != 10 && digitos.length() != 11) {
            throw new IllegalArgumentException("Telefone inválido: informe DDD + número, ex.: (11) 98765-4321.");
        }
        int meio = digitos.length() - 4;
        return "(" + digitos.substring(0, 2) + ") " + digitos.substring(2, meio) + "-" + digitos.substring(meio);
    }

    /** E-mail é opcional: devolve null se estiver vazio. */
    static String email(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        String email = valor.trim();
        if (email.length() > 100 || !email.matches("[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+")) {
            throw new IllegalArgumentException("E-mail inválido, ex.: nome@dominio.com");
        }
        return email;
    }

    /** Data e hora no formato dd/mm/aaaa hh:mm, que não pode estar no passado. */
    static LocalDateTime dataHoraFutura(String valor) {
        LocalDateTime dataHora;
        try {
            dataHora = LocalDateTime.parse(valor == null ? "" : valor.trim(), DATA_HORA);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Data/hora inválida: use o formato dd/mm/aaaa hh:mm, ex.: 25/11/2026 14:30.");
        }
        if (dataHora.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Não é possível agendar em uma data/hora que já passou.");
        }
        return dataHora;
    }
}
