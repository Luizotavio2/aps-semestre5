package com.jdbc.view;

import com.jdbc.controller.AgendamentoController;
import com.jdbc.controller.ValidacaoException;
import com.jdbc.dao.DAOException;
import com.jdbc.model.Agendamento;
import com.jdbc.model.Barbeiro;
import com.jdbc.model.Cliente;

import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.Component;
import java.awt.FlowLayout;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

class AgendamentoFormDialog extends FormularioDialog {
    /** Serviços sugeridos e seus preços padrão (o usuário pode digitar outro serviço/valor). */
    private static final Map<String, BigDecimal> SERVICOS = new LinkedHashMap<>();

    static {
        SERVICOS.put("Corte", new BigDecimal("45.00"));
        SERVICOS.put("Barba", new BigDecimal("30.00"));
        SERVICOS.put("Corte + Barba", new BigDecimal("70.00"));
        SERVICOS.put("Sobrancelha", new BigDecimal("20.00"));
        SERVICOS.put("Pigmentação", new BigDecimal("50.00"));
    }

    private final AgendamentoController controller;
    private final Agendamento agendamento;
    private final JComboBox<Cliente> cliente;
    private final JComboBox<Barbeiro> barbeiro;
    private final JFormattedTextField data = Formatos.campoData();
    private final JFormattedTextField horaInicio = Formatos.campoHora();
    private final JFormattedTextField horaFim = Formatos.campoHora();
    private final JComboBox<String> servico = new JComboBox<>(SERVICOS.keySet().toArray(new String[0]));
    private final JTextField valor = new JTextField(10);

    AgendamentoFormDialog(Component pai, AgendamentoController controller, List<Cliente> clientes,
                          List<Barbeiro> barbeiros, Agendamento agendamento) {
        super(pai, agendamento == null ? "Novo agendamento" : "Editar agendamento");
        this.controller = controller;
        this.agendamento = agendamento;
        cliente = new JComboBox<>(clientes.toArray(new Cliente[0]));
        barbeiro = new JComboBox<>(barbeiros.toArray(new Barbeiro[0]));
        servico.setEditable(true);

        if (agendamento == null) {
            data.setText(Formatos.formatar(LocalDate.now(), Formatos.DATA));
            servico.setSelectedIndex(0);
            valor.setText(Formatos.decimal(SERVICOS.get("Corte")));
        } else {
            cliente.setSelectedItem(agendamento.getCliente());
            barbeiro.setSelectedItem(agendamento.getBarbeiro());
            data.setText(Formatos.formatar(agendamento.getInicio(), Formatos.DATA));
            horaInicio.setText(Formatos.formatar(agendamento.getInicio(), Formatos.HORA));
            horaFim.setText(Formatos.formatar(agendamento.getFim(), Formatos.HORA));
            servico.setSelectedItem(agendamento.getServico());
            valor.setText(Formatos.decimal(agendamento.getValor()));
        }
        // ao escolher um serviço da lista, sugere o preço padrão
        servico.addActionListener(e -> {
            BigDecimal preco = SERVICOS.get(String.valueOf(servico.getSelectedItem()));
            if (preco != null) {
                valor.setText(Formatos.decimal(preco));
            }
        });

        JPanel horario = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        horario.add(horaInicio);
        horario.add(new JLabel("até"));
        horario.add(horaFim);

        adicionarCampo("Cliente:", cliente);
        adicionarCampo("Barbeiro:", barbeiro);
        adicionarCampo("Data (dd/mm/aaaa):", data);
        adicionarCampo("Horário (hh:mm):", horario);
        adicionarCampo("Serviço:", servico);
        adicionarCampo("Valor (R$):", valor);
        montar();
    }

    @Override
    protected void salvar() throws ValidacaoException, DAOException {
        // lê o editor, pois o serviço pode ter sido digitado e não escolhido da lista
        Object servicoDigitado = servico.getEditor().getItem();
        controller.salvar(agendamento == null ? null : agendamento.getId(),
                (Cliente) cliente.getSelectedItem(), (Barbeiro) barbeiro.getSelectedItem(),
                data.getText(), horaInicio.getText(), horaFim.getText(),
                servicoDigitado == null ? null : servicoDigitado.toString(), valor.getText());
    }
}
