package com.dsi.ppai.redsismica.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.dsi.ppai.redsismica.model.Sesion;

@Repository
public interface SesionRepository extends CrudRepository<Sesion, Long> {

}
