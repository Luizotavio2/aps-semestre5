package com.jdbc.model;

/** Cliente da barbearia. Relaciona-se com {@link Barbeiro} por meio de {@link Agendamento}. */
public class Cliente {
    private Integer id;
    private String nome;
    private String telefone;
    private String email;

    public Cliente(Integer id, String nome, String telefone, String email) {
        this.id = id;
        this.nome = nome;
        this.telefone = telefone;
        this.email = email;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    /** Texto exibido nas listas de seleção da interface. */
    @Override
    public String toString() {
        return nome + " - " + telefone;
    }
}
