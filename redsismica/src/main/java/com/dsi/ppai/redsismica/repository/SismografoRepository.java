package com.dsi.ppai.redsismica.repository;
import com.dsi.ppai.redsismica.model.Sismografo;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SismografoRepository extends CrudRepository<Sismografo, Integer> { }