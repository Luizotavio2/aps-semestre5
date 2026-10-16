package com.jdbc.view;

import com.jdbc.controller.AgendamentoController;
import com.jdbc.controller.BarbeiroController;
import com.jdbc.controller.ClienteController;
import com.jdbc.controller.ValidacaoException;
import com.jdbc.dao.DAOException;
import com.jdbc.model.Agendamento;
import com.jdbc.model.Barbeiro;
import com.jdbc.model.Cliente;

import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.util.List;

class AgendamentoPanel extends CrudPanel<Agendamento> {
    private final AgendamentoController controller = new AgendamentoController();
    private final ClienteController clienteController = new ClienteController();
    private final BarbeiroController barbeiroController = new BarbeiroController();
    private final JFormattedTextField dataDe = Formatos.campoData();
    private final JFormattedTextField dataAte = Formatos.campoData();

    AgendamentoPanel() {
        super("agendamento", List.of(
                new EntidadeTableModel.Coluna<>("Código", Agendamento::getId),
                new EntidadeTableModel.Coluna<>("Data", a -> Formatos.formatar(a.getInicio(), Formatos.DATA)),
                new EntidadeTableModel.Coluna<>("Início", a -> Formatos.formatar(a.getInicio(), Formatos.HORA)),
                new EntidadeTableModel.Coluna<>("Término", a -> Formatos.formatar(a.getFim(), Formatos.HORA)),
                new EntidadeTableModel.Coluna<>("Cliente", a -> a.getCliente().getNome()),
                new EntidadeTableModel.Coluna<>("Barbeiro", a -> a.getBarbeiro().getNome()),
                new EntidadeTableModel.Coluna<>("Serviço", Agendamento::getServico),
                new EntidadeTableModel.Coluna<>("Valor", a -> Formatos.moeda(a.getValor()))));
        montarTela(controller.camposOrdenacao());
    }

    @Override
    protected void adicionarFiltrosExtras(JPanel painel) {
        dataDe.addActionListener(e -> recarregar());
        dataAte.addActionListener(e -> recarregar());
        painel.add(new JLabel("De:"));
        painel.add(dataDe);
        painel.add(new JLabel("Até:"));
        painel.add(dataAte);
    }

    @Override
    protected void limparFiltrosExtras() {
        dataDe.setValue(null);
        dataAte.setValue(null);
    }

    @Override
    protected String dicaBusca() {
        return "Busca por nome do cliente, nome do barbeiro ou serviço";
    }

    @Override
    protected List<Agendamento> buscar() throws ValidacaoException, DAOException {
        return controller.listar(campoBusca.getText(), dataDe.getText(), dataAte.getText(), criteriosOrdenacao());
    }

    @Override
    protected boolean abrirFormulario(Agendamento agendamento) {
        try {
            List<Cliente> clientes = clienteController.listarTodos();
            List<Barbeiro> barbeiros = barbeiroController.listarTodos();
            if (clientes.isEmpty() || barbeiros.isEmpty()) {
                Mensagens.aviso(this, "Cadastre ao menos um cliente e um barbeiro antes de criar agendamentos.");
                return false;
            }
            return new AgendamentoFormDialog(this, controller, clientes, barbeiros, agendamento).exibir();
        } catch (DAOException e) {
            Mensagens.erro(this, e.getMessage());
            return false;
        }
    }

    @Override
    protected void excluir(Agendamento agendamento) throws DAOException {
        controller.excluir(agendamento);
    }

    @Override
    protected String descrever(Agendamento agendamento) {
        return "o agendamento de " + agendamento.getCliente().getNome() + " em "
                + Formatos.formatar(agendamento.getInicio(), Formatos.DATA_HORA);
    }
}
