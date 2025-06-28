package com.dsi.ppai.redsismica.repository;

import com.dsi.ppai.redsismica.model.OrdenDeInspeccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrdenInspeccionRepository extends JpaRepository<OrdenDeInspeccion, Long> {

    // Método para buscar órdenes por estado
    List<OrdenDeInspeccion> findByEstado(String estado);
}