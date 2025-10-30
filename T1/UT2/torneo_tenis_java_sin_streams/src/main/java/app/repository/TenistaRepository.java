package app.repository;

import app.domain.Tenista;

import java.util.List;

public interface TenistaRepository {
    void initSchema(boolean drop) throws Exception;

    List<Tenista> findAll() throws Exception;
    Tenista findById(long id) throws Exception;
    List<Tenista> findByPais(String pais) throws Exception;
    List<Tenista> findAllOrderByPuntosDesc() throws Exception;

    Tenista insert(Tenista t) throws Exception;
    Tenista update(Tenista t) throws Exception;
    boolean delete(long id) throws Exception;
}
