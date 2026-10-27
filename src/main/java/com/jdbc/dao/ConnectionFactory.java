package com.jdbc.dao;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/** Cria conexões com o MySQL usando a configuração externa, o classpath ou valores padrão. */
public final class ConnectionFactory {
    private static final Properties CONFIG = loadConfiguration();

    private ConnectionFactory() {
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                config("db.url", "jdbc:mysql://localhost:3306/agendamento_barbearia"),
                config("db.usuario", "root"),
                config("db.senha", ""));
    }

    private static Properties loadConfiguration() {
        Properties config = new Properties();
        try (InputStream in = ConnectionFactory.class.getResourceAsStream("/db.properties")) {
            if (in != null) {
                config.load(in);
            }
        } catch (IOException e) {
            System.err.println("Não foi possível ler db.properties do classpath: " + e.getMessage());
        }

        Path externalFile = Path.of("db.properties");
        if (Files.isRegularFile(externalFile)) {
            try (Reader in = Files.newBufferedReader(externalFile, StandardCharsets.UTF_8)) {
                config.load(in);
            } catch (IOException e) {
                System.err.println("Não foi possível ler " + externalFile.toAbsolutePath() + ": " + e.getMessage());
            }
        }
        return config;
    }

    private static String config(String key, String defaultValue) {
        return System.getProperty(key, CONFIG.getProperty(key, defaultValue));
    }
}
