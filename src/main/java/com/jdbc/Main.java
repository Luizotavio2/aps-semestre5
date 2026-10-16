
package com.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Main {
    public static void main(String[] args) {
        String usuario = "root";
        String senha = "Luizotavio@0307";
        String url = "jdbc:mysql://localhost:3306/agendamento_barbearia";

        String sql = "INSERT INTO cliente (nome, email, telefone) VALUES ('John Doe', 'john.doe@example.com', '1234567890')";
        try (Connection conexao = DriverManager.getConnection(url, usuario, senha);
            Statement stmt = conexao.createStatement()) {
            System.out.println("Conexão ON");
            int linhasAfetadas = stmt.executeUpdate(sql);

            System.out.println("Registro inserido com sucesso!");
            System.out.println("Linhas afetadas: " + linhasAfetadas);

        } catch (SQLException e) {
            System.out.println("Erro ao conectar ou executar SQL:");
            System.out.println(e.getMessage());
        }
    }
}
