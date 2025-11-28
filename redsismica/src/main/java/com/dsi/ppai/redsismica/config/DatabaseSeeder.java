package com.dsi.ppai.redsismica.config;

import com.dsi.ppai.redsismica.model.*;
import com.dsi.ppai.redsismica.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Date;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    // Inyectamos todos los repositorios necesarios
    @Autowired private RolRepository rolRepository;
    @Autowired private EmpleadoRepository empleadoRepository;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private EstadoRepository estadoRepository;
    @Autowired private SismografoRepository sismografoRepository;
    @Autowired private EstacionSismologicaRepository estacionRepository;
    @Autowired private OrdenInspeccionRepository ordenRepository; // Nombre corregido
    @Autowired private CambioEstadoRepository cambioEstadoRepository;

    @Override
    public void run(String... args) throws Exception {
        // Verificamos si ya existen datos para no duplicar
        if (usuarioRepository.count() == 0) {
            System.out.println("---------- INICIANDO SEEDER (CARGA DE DATOS) ----------");

            // 1. Crear ROL
            Rol rolResponsable = new Rol();
            rolResponsable.setNombre("Responsable de Inspección"); // Este nombre es clave para tu lógica
            rolResponsable.setDescripcion("Encargado de inspecciones técnicas");
            rolRepository.save(rolResponsable);

            // 2. Crear EMPLEADO (Juan Pérez)
            Empleado juan = new Empleado();
            juan.setNombre("Juan");
            juan.setApellido("Pérez");
            juan.setMail("juan.perez@empresa.com");
            juan.setRol(rolResponsable);
            empleadoRepository.save(juan);

            // 3. Crear USUARIO (Login: juan.perez)
            Usuario usuarioJuan = new Usuario();
            usuarioJuan.setNombreUsuario("juan.perez");
            usuarioJuan.setEmpleado(juan);
            usuarioRepository.save(usuarioJuan);

            // 4. Crear ESTADOS
            Estado estadoCompletada = new Estado();
            estadoCompletada.setNombreEstado("CompletamenteRealizada");
            estadoCompletada.setAmbito("OrdenDeInspeccion");
            estadoRepository.save(estadoCompletada);

            Estado estadoCerrada = new Estado();
            estadoCerrada.setNombreEstado("Cerrado");
            estadoCerrada.setAmbito("OrdenDeInspeccion");
            estadoRepository.save(estadoCerrada);

            // 5. Crear SISMOGRAFO (Requiere un CambioEstado inicial)
            CambioEstado cambioInicial = new CambioEstado(new Date(), null, usuarioJuan);
            cambioInicial.setNombre("En Servicio");
            cambioEstadoRepository.save(cambioInicial);

            Sismografo sismografo = new Sismografo();
            sismografo.setCambioEstado(cambioInicial);
            // Nota: Si Sismografo usa GenerationType.IDENTITY en ID, no seteamos ID manualmente.
            sismografoRepository.save(sismografo);

            // 6. Crear ESTACION SISMOLOGICA
            EstacionSismologica estacion = new EstacionSismologica();
            estacion.setNombre("Estación La Falda");
            estacion.setSismografo(sismografo);
            estacionRepository.save(estacion);

            // 7. Crear ORDEN DE INSPECCION (Lista para cerrar)
            OrdenDeInspeccion orden = new OrdenDeInspeccion();
            orden.setNumeroOrden(2025);
            orden.setFechaHoraFinalizacion(LocalDateTime.now().minusHours(5)); // Finalizó hace 5 horas
            orden.setEstado(estadoCompletada); // Importante: Estado "CompletamenteRealizada"
            orden.setUsuario(usuarioJuan);     // Asignada a Juan Pérez
            orden.setEstacionSismologica(estacion);
            // orden.setObservacionCierre(null); // Es null por defecto
            
            ordenRepository.save(orden);

            System.out.println("---------- DATOS CARGADOS EXITOSAMENTE ----------");
            System.out.println("Usuario: Juan Pérez (juan.perez)");
            System.out.println("Orden disponible: #2025");
        }
    }
}