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

/**
 * Fornece conexões com o MySQL. A configuração é lida, nesta ordem de prioridade, de:
 * propriedades de sistema (-Ddb.url=...), arquivo db.properties na pasta de execução
 * e db.properties do classpath (src/main/resources).
 */
public final class ConnectionFactory {
    private static final String ARQUIVO = "db.properties";
    private static final Properties CONFIG = carregarConfiguracao();

    private ConnectionFactory() {
    }

    private static Properties carregarConfiguracao() {
        Properties config = new Properties();
        try (InputStream in = ConnectionFactory.class.getResourceAsStream("/" + ARQUIVO)) {
            if (in != null) {
                config.load(in);
            }
        } catch (IOException e) {
            System.err.println("Não foi possível ler " + ARQUIVO + " do classpath: " + e.getMessage());
        }
        Path externo = Path.of(ARQUIVO);
        if (Files.isRegularFile(externo)) {
            try (Reader in = Files.newBufferedReader(externo, StandardCharsets.UTF_8)) {
                config.load(in);
            } catch (IOException e) {
                System.err.println("Não foi possível ler " + externo.toAbsolutePath() + ": " + e.getMessage());
            }
        }
        return config;
    }

    private static String config(String chave, String padrao) {
        return System.getProperty(chave, CONFIG.getProperty(chave, padrao));
    }

    public static Connection getConnection() throws DAOException {
        String url = config("db.url", "jdbc:mysql://localhost:3306/agendamento_barbearia");
        try {
            return DriverManager.getConnection(url, config("db.usuario", "root"), config("db.senha", ""));
        } catch (SQLException e) {
            throw new DAOException("Não foi possível conectar ao banco de dados (" + url + "):\n" + e.getMessage(), e);
        }
    }

    /** Abre e fecha uma conexão apenas para verificar se o banco está acessível. */
    public static void testarConexao() throws DAOException {
        try (Connection ignored = getConnection()) {
            // conexão aberta com sucesso
        } catch (SQLException e) {
            throw new DAOException("Erro ao fechar a conexão de teste: " + e.getMessage(), e);
        }
    }
}
