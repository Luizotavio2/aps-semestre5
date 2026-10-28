package com.jdbc.model;

import java.time.LocalDateTime;

/** Representa a tabela associativa agendamento: um Cliente marcado com um Barbeiro. */
public class Agendamento {
    private int id; // 0 enquanto ainda não foi salvo no banco
    private Cliente cliente;
    private Barbeiro barbeiro;
    private LocalDateTime dataHora;
    private String servico;

    public Agendamento(int id, Cliente cliente, Barbeiro barbeiro, LocalDateTime dataHora, String servico) {
        this.id = id;
        this.cliente = cliente;
        this.barbeiro = barbeiro;
        this.dataHora = dataHora;
        this.servico = servico;
    }

    public int getId() {
        return id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public Barbeiro getBarbeiro() {
        return barbeiro;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public String getServico() {
        return servico;
    }
}
