package com.jdbc;

import com.jdbc.dao.ConnectionFactory;
import com.jdbc.view.TelaInicial;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.sql.Connection;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            } catch (Exception e) {
                // mantém o visual padrão do Swing
            }
            // testa a conexão antes de abrir o sistema
            try (Connection ignored = ConnectionFactory.getConnection()) {
                new TelaInicial().setVisible(true);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Não foi possível conectar ao banco de dados:\n" + e.getMessage()
                                + "\n\nVerifique se o MySQL está em execução, se o script src/database/init.sql"
                                + "\nfoi executado e se usuário/senha em db.properties estão corretos.",
                        "Falha na conexão", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        });
    }
}
