package com.jdbc.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Classe associativa: um horário marcado de um {@link Cliente} com um {@link Barbeiro}. */
public class Agendamento {
    private Integer id;
    private Cliente cliente;
    private Barbeiro barbeiro;
    private LocalDateTime dataHora;
    private String servico;
    private BigDecimal valor;

    public Agendamento(Integer id, Cliente cliente, Barbeiro barbeiro, LocalDateTime dataHora,
                       String servico, BigDecimal valor) {
        this.id = id;
        this.cliente = cliente;
        this.barbeiro = barbeiro;
        this.dataHora = dataHora;
        this.servico = servico;
        this.valor = valor;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Barbeiro getBarbeiro() {
        return barbeiro;
    }

    public void setBarbeiro(Barbeiro barbeiro) {
        this.barbeiro = barbeiro;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public String getServico() {
        return servico;
    }

    public void setServico(String servico) {
        this.servico = servico;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }
}
