package com.dsi.ppai.redsismica.repository;
import com.dsi.ppai.redsismica.model.Rol;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RolRepository extends CrudRepository<Rol, Long> { }