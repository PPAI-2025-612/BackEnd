package com.dsi.ppai.redsismica.repository;
import com.dsi.ppai.redsismica.model.EstacionSismologica;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EstacionSismologicaRepository extends CrudRepository<EstacionSismologica, Long> { }