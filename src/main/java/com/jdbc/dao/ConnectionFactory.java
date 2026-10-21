package com.jdbc.dao;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/** Cria conexões com o MySQL usando os dados de src/main/resources/db.properties. */
public final class ConnectionFactory {
    private static final Properties CONFIG = new Properties();

    static {
        try (InputStream in = ConnectionFactory.class.getResourceAsStream("/db.properties")) {
            if (in != null) {
                CONFIG.load(in);
            }
        } catch (IOException e) {
            System.err.println("Não foi possível ler db.properties: " + e.getMessage());
        }
    }

    private ConnectionFactory() {
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                CONFIG.getProperty("db.url", "jdbc:mysql://localhost:3306/agendamento_barbearia"),
                CONFIG.getProperty("db.usuario", "root"),
                CONFIG.getProperty("db.senha", ""));
    }
}
