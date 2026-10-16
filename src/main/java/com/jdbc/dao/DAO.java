package com.jdbc.dao;

import java.util.List;

/** Operações de persistência (CRUD) comuns a todas as entidades. */
public interface DAO<T> {
    void inserir(T entidade) throws DAOException;

    void atualizar(T entidade) throws DAOException;

    void excluir(int id) throws DAOException;

    T buscarPorId(int id) throws DAOException;

    /**
     * @param filtro    texto de busca (vazio ou null lista tudo)
     * @param ordenacao critérios de ordenação, aplicados na ordem da lista
     */
    List<T> listar(String filtro, List<Criterio> ordenacao) throws DAOException;

    /** Nomes dos campos que podem ser usados em {@link Criterio}. */
    List<String> camposOrdenacao();
}
