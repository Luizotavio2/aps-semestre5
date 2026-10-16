package com.jdbc.controller;

/** Entrada inválida ou regra de negócio violada; a mensagem é exibida ao usuário. */
public class ValidacaoException extends Exception {
    public ValidacaoException(String mensagem) {
        super(mensagem);
    }
}
