package com.dsi.ppai.redsismica.repository;
import com.dsi.ppai.redsismica.model.CambioEstado;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CambioEstadoRepository extends CrudRepository<CambioEstado, Long> { }