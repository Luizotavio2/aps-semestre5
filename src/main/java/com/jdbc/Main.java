package com.jdbc;

import com.jdbc.dao.ConnectionFactory;
import com.jdbc.view.TelaInicial;

import javax.swing.JOptionPane;
import java.sql.Connection;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        // testa a conexão com o banco antes de abrir o sistema
        try (Connection conexao = ConnectionFactory.getConnection()) {
            new TelaInicial().setVisible(true);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Não foi possível conectar ao banco de dados:\n" + e.getMessage());
        }
    }
}
