package com.jdbc.model;

/** Representa a tabela cliente. Relaciona-se com Barbeiro por meio de Agendamento. */
public class Cliente {
    private int id; // 0 enquanto ainda não foi salvo no banco
    private String nome;
    private String telefone;

    public Cliente(int id, String nome, String telefone) {
        this.id = id;
        this.nome = nome;
        this.telefone = telefone;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public String getTelefone() {
        return telefone;
    }

    /** Texto mostrado nas listas de seleção. */
    @Override
    public String toString() {
        return nome;
    }
}
