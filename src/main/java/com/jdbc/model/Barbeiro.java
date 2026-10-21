package com.jdbc.model;

/** Barbeiro que atende os clientes. Relaciona-se com {@link Cliente} por meio de {@link Agendamento}. */
public class Barbeiro {
    private Integer id;
    private String nome;
    private String telefone;

    public Barbeiro(Integer id, String nome, String telefone) {
        this.id = id;
        this.nome = nome;
        this.telefone = telefone;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    /** Texto exibido nas listas de seleção da interface. */
    @Override
    public String toString() {
        return nome;
    }
}
