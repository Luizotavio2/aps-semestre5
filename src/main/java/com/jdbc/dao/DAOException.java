package com.jdbc.dao;

/** Erro de acesso a dados, com mensagem pronta para ser exibida ao usuário. */
public class DAOException extends Exception {
    public DAOException(String mensagem) {
        super(mensagem);
    }

    public DAOException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
