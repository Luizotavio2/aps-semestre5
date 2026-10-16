package com.jdbc.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/** Classe associativa: um horário marcado de um {@link Cliente} com um {@link Barbeiro}. */
public class Agendamento {
    private Integer id;
    private Cliente cliente;
    private Barbeiro barbeiro;
    private LocalDateTime inicio;
    private LocalDateTime fim;
    private String servico;
    private BigDecimal valor;
    private LocalDateTime criadoEm;

    public Agendamento() {
    }

    public Agendamento(Integer id, Cliente cliente, Barbeiro barbeiro, LocalDateTime inicio,
                       LocalDateTime fim, String servico, BigDecimal valor) {
        this.id = id;
        this.cliente = cliente;
        this.barbeiro = barbeiro;
        this.inicio = inicio;
        this.fim = fim;
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

    public LocalDateTime getInicio() {
        return inicio;
    }

    public void setInicio(LocalDateTime inicio) {
        this.inicio = inicio;
    }

    public LocalDateTime getFim() {
        return fim;
    }

    public void setFim(LocalDateTime fim) {
        this.fim = fim;
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

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }

    @Override
    public boolean equals(Object o) {
        return this == o || (o instanceof Agendamento outro && id != null && id.equals(outro.id));
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
