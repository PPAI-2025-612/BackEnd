package com.dsi.ppai.redsismica.repository;

import com.dsi.ppai.redsismica.model.MotivoFueraServicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MotivoFueraServicioRepository extends JpaRepository<MotivoFueraServicio, Long> {
}