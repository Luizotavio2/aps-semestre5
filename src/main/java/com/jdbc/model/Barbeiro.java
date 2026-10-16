package com.jdbc.model;

import java.time.LocalDateTime;
import java.util.Objects;

/** Barbeiro que atende os clientes. Relaciona-se com {@link Cliente} por meio de {@link Agendamento}. */
public class Barbeiro {
    private Integer id;
    private String nome;
    private String telefone;
    private LocalDateTime criadoEm;

    public Barbeiro() {
    }

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

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }

    @Override
    public boolean equals(Object o) {
        return this == o || (o instanceof Barbeiro outro && id != null && id.equals(outro.id));
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    /** Usado para exibir o barbeiro em listas e combos da interface. */
    @Override
    public String toString() {
        return nome;
    }
}
