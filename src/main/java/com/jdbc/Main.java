package com.jdbc;

import com.jdbc.dao.ConnectionFactory;
import com.jdbc.dao.DAOException;
import com.jdbc.view.MainFrame;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            usarVisualNimbus();
            try {
                ConnectionFactory.testarConexao();
            } catch (DAOException e) {
                JOptionPane.showMessageDialog(null, e.getMessage()
                                + "\n\nVerifique se o MySQL está em execução, se o script src/database/init.sql foi executado"
                                + "\ne se usuário/senha em db.properties estão corretos.",
                        "Falha na conexão", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
            new MainFrame().setVisible(true);
        });
    }

    private static void usarVisualNimbus() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    return;
                }
            }
        } catch (Exception e) {
            // mantém o visual padrão do Swing
        }
    }
}
