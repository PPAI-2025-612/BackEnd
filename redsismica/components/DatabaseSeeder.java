package com.dsi.ppai.redsismica.components;

import com.dsi.ppai.redsismica.model.*;
import com.dsi.ppai.redsismica.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private EmpleadoRepository empleadoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EstadoRepository estadoRepository;

    @Autowired
    private MotivoTipoRepository motivoTipoRepository;

    @Autowired
    private CambioEstadoRepository cambioEstadoRepository;

    @Autowired
    private SismografoRepository sismografoRepository;

    @Autowired
    private EstacionSismologicaRepository estacionSismologicaRepository;

    @Autowired
    private OrdenInspeccionRepository ordenInspeccionRepository;

    @Autowired
    private SesionRepository sesionRepository;

    @Override
    public void run(String... args) throws Exception {
        // Verificar si ya existen datos para evitar duplicados
        if (rolRepository.count() > 0) {
            return;
        }

        // 1. Crear Roles
        Rol rolResponsableInspeccion = new Rol();
        rolResponsableInspeccion.setNombre("ResponsableInspeccion");
        rolResponsableInspeccion.setDescripcion("Responsable de realizar inspecciones en estaciones sismológicas");

        Rol rolResponsableReparacion = new Rol();
        rolResponsableReparacion.setNombre("ResponsableReparacion");
        rolResponsableReparacion.setDescripcion("Responsable de reparación de equipos sísmicos");

        Rol rolAdmin = new Rol();
        rolAdmin.setNombre("Administrador");
        rolAdmin.setDescripcion("Administrador del sistema");

        rolRepository.save(rolResponsableInspeccion);
        rolRepository.save(rolResponsableReparacion);
        rolRepository.save(rolAdmin);

        // 2. Crear Empleados
        Empleado empleado1 = new Empleado();
        empleado1.setNombre("Juan");
        empleado1.setApellido("García");
        empleado1.setMail("juan.garcia@redsismica.com");
        empleado1.setRol(rolResponsableInspeccion);

        Empleado empleado2 = new Empleado();
        empleado2.setNombre("María");
        empleado2.setApellido("López");
        empleado2.setMail("maria.lopez@redsismica.com");
        empleado2.setRol(rolResponsableReparacion);

        Empleado empleado3 = new Empleado();
        empleado3.setNombre("Carlos");
        empleado3.setApellido("Rodríguez");
        empleado3.setMail("carlos.rodriguez@redsismica.com");
        empleado3.setRol(rolAdmin);

        empleadoRepository.save(empleado1);
        empleadoRepository.save(empleado2);
        empleadoRepository.save(empleado3);

        // 3. Crear Usuarios
        Usuario usuario1 = new Usuario();
        usuario1.setNombreUsuario("jgarcia");
        usuario1.setEmpleado(empleado1);

        Usuario usuario2 = new Usuario();
        usuario2.setNombreUsuario("mlopez");
        usuario2.setEmpleado(empleado2);

        Usuario usuario3 = new Usuario();
        usuario3.setNombreUsuario("crodriguez");
        usuario3.setEmpleado(empleado3);

        usuarioRepository.save(usuario1);
        usuarioRepository.save(usuario2);
        usuarioRepository.save(usuario3);

        // 4. Crear Estados
        Estado estadoActivo = new Estado();
        estadoActivo.setNombreEstado("Activo");
        estadoActivo.setAmbito("Sismografo");

        Estado estadoInactivo = new Estado();
        estadoInactivo.setNombreEstado("Inactivo");
        estadoInactivo.setAmbito("Sismografo");

        Estado estadoPendiente = new Estado();
        estadoPendiente.setNombreEstado("Pendiente");
        estadoPendiente.setAmbito("OrdenDeInspeccion");

        Estado estadoCompletada = new Estado();
        estadoCompletada.setNombreEstado("CompletamenteRealizada");
        estadoCompletada.setAmbito("OrdenDeInspeccion");

        Estado estadoCerrado = new Estado();
        estadoCerrado.setNombreEstado("Cerrado");
        estadoCerrado.setAmbito("OrdenDeInspeccion");

        estadoRepository.save(estadoActivo);
        estadoRepository.save(estadoInactivo);
        estadoRepository.save(estadoPendiente);
        estadoRepository.save(estadoCompletada);
        estadoRepository.save(estadoCerrado);

        // 5. Crear Motivos de Tipo
        MotivoTipo motivoMantenimiento = new MotivoTipo();
        motivoMantenimiento.setNombre("Mantenimiento");
        motivoMantenimiento.setDescripcion("Mantenimiento preventivo del equipo");

        MotivoTipo motivoReparacion = new MotivoTipo();
        motivoReparacion.setNombre("Reparación");
        motivoReparacion.setDescripcion("Reparación de componentes dañados");

        MotivoTipo motivoCalibracion = new MotivoTipo();
        motivoCalibracion.setNombre("Calibración");
        motivoCalibracion.setDescripcion("Calibración de sensores sísmicos");

        motivoTipoRepository.save(motivoMantenimiento);
        motivoTipoRepository.save(motivoReparacion);
        motivoTipoRepository.save(motivoCalibracion);

        // 6. Crear Cambios de Estado
        CambioEstado cambioEstado1 = new CambioEstado();
        cambioEstado1.setNombre("Cambio a Mantenimiento");
        cambioEstado1.setFechaHoraInicio(new Date());
        cambioEstado1.setRILogResponsable(usuario1);
        cambioEstado1.setMotivo(List.of(motivoMantenimiento));

        CambioEstado cambioEstado2 = new CambioEstado();
        cambioEstado2.setNombre("Cambio a Reparación");
        cambioEstado2.setFechaHoraInicio(new Date());
        cambioEstado2.setRILogResponsable(usuario2);
        cambioEstado2.setMotivo(List.of(motivoReparacion));

        cambioEstadoRepository.save(cambioEstado1);
        cambioEstadoRepository.save(cambioEstado2);

        // 7. Crear Sismógrafos
        Sismografo sismografo1 = new Sismografo();
        sismografo1.setCambioEstado(cambioEstado1);

        Sismografo sismografo2 = new Sismografo();
        sismografo2.setCambioEstado(cambioEstado2);

        Sismografo sismografo3 = new Sismografo();
        sismografo3.setCambioEstado(cambioEstado1);

        sismografoRepository.save(sismografo1);
        sismografoRepository.save(sismografo2);
        sismografoRepository.save(sismografo3);

        // 8. Crear Estaciones Sismológicas
        EstacionSismologica estacion1 = new EstacionSismologica();
        estacion1.setNombre("Estación Zona Centro");
        estacion1.setSismografo(sismografo1);

        EstacionSismologica estacion2 = new EstacionSismologica();
        estacion2.setNombre("Estación Zona Norte");
        estacion2.setSismografo(sismografo2);

        EstacionSismologica estacion3 = new EstacionSismologica();
        estacion3.setNombre("Estación Zona Sur");
        estacion3.setSismografo(sismografo3);

        estacionSismologicaRepository.save(estacion1);
        estacionSismologicaRepository.save(estacion2);
        estacionSismologicaRepository.save(estacion3);

        // 9. Crear Órdenes de Inspección
        OrdenDeInspeccion orden1 = new OrdenDeInspeccion();
        orden1.setNumeroOrden(1001);
        orden1.setUsuario(usuario1);
        orden1.setEstado(estadoPendiente);
        orden1.setEstacionSismologica(estacion1);
        orden1.setFechaHoraFinalizacion(LocalDateTime.now().plusDays(5));

        OrdenDeInspeccion orden2 = new OrdenDeInspeccion();
        orden2.setNumeroOrden(1002);
        orden2.setUsuario(usuario1);
        orden2.setEstado(estadoCompletada);
        orden2.setEstacionSismologica(estacion2);
        orden2.setFechaHoraFinalizacion(LocalDateTime.now().minusDays(2));
        orden2.setObservacionCierre("Inspección completada sin problemas");

        OrdenDeInspeccion orden3 = new OrdenDeInspeccion();
        orden3.setNumeroOrden(1003);
        orden3.setUsuario(usuario2);
        orden3.setEstado(estadoCerrado);
        orden3.setEstacionSismologica(estacion3);
        orden3.setFechaHoraCierre(LocalDateTime.now());
        orden3.setObservacionCierre("Reparación completada y equipo funcionando");

        ordenInspeccionRepository.save(orden1);
        ordenInspeccionRepository.save(orden2);
        ordenInspeccionRepository.save(orden3);

        // 10. Crear Sesiones
        Sesion sesion1 = new Sesion();
        sesion1.setFechaHoraDesde(new Date());
        sesion1.setFechaHoraHasta(new Date(System.currentTimeMillis() + 3600000)); // +1 hora
        sesion1.setUsuario(usuario1);

        Sesion sesion2 = new Sesion();
        sesion2.setFechaHoraDesde(new Date());  
        sesion2.setFechaHoraHasta(new Date(System.currentTimeMillis() + 7200000)); // +2 horas
        sesion2.setUsuario(usuario2);

        sesionRepository.save(sesion1);
        sesionRepository.save(sesion2);
    }
}    
    
    
    
    
    
    
    