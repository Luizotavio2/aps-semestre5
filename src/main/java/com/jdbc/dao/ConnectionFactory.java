package com.jdbc.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/** Cria conexões com o banco MySQL. */
public class ConnectionFactory {
    private static final String URL = "jdbc:mysql://localhost:3306/agendamento_barbearia";
    private static final String USUARIO = "root";
    private static final String SENHA = "Luizotavio@0307";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, SENHA);
    }
}
