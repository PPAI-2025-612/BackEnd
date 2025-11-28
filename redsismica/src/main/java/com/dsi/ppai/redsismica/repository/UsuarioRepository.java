package com.dsi.ppai.redsismica.repository;
import com.dsi.ppai.redsismica.model.Usuario;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends CrudRepository<Usuario, Long> { 
    // Esto te servirá para validar si ya existe el usuario
    Usuario findByNombreUsuario(String nombreUsuario);
}