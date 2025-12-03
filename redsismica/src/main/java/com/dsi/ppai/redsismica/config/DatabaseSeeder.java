package com.dsi.ppai.redsismica.config;

import com.dsi.ppai.redsismica.model.*;
import com.dsi.ppai.redsismica.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    @Autowired private RolRepository rolRepository;
    @Autowired private EmpleadoRepository empleadoRepository;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private EstadoRepository estadoRepository;
    @Autowired private SismografoRepository sismografoRepository;
    @Autowired private EstacionSismologicaRepository estacionRepository;
    @Autowired private OrdenInspeccionRepository ordenRepository;
    // CambioEstadoRepository ya no es estrictamente necesario si se guarda por cascada, 
    // pero lo dejamos por si acaso.
    @Autowired private CambioEstadoRepository cambioEstadoRepository;
    @Autowired private MotivoTipoRepository motivoTipoRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (usuarioRepository.count() == 0) {
            System.out.println("---------- INICIANDO SEEDER (CARGA DE DATOS) ----------");

            // 1. ROL Y ESTADOS
            Rol rolResponsable = new Rol();
            rolResponsable.setNombre("Responsable de Inspección");
            rolResponsable.setDescripcion("Encargado de inspecciones técnicas");
            rolResponsable = rolRepository.save(rolResponsable);

            Estado estadoCompletada = new Estado();
            estadoCompletada.setNombreEstado("CompletamenteRealizada");
            estadoCompletada.setAmbito("OrdenDeInspeccion");
            estadoCompletada = estadoRepository.save(estadoCompletada);

            Estado estadoCerrada = new Estado();
            estadoCerrada.setNombreEstado("Cerrado");
            estadoCerrada.setAmbito("OrdenDeInspeccion");
            estadoCerrada = estadoRepository.save(estadoCerrada);

            // 2. MOTIVOS
            MotivoTipo m1 = new MotivoTipo();
            m1.setNombre("Avería por vibración");
            m1.setDescripcion("El equipo presentó fallas debido a vibraciones excesivas detectadas");
            motivoTipoRepository.save(m1);

            MotivoTipo m2 = new MotivoTipo();
            m2.setNombre("Desgaste de componentes");
            m2.setDescripcion("Componentes críticos muestran signos de desgaste significativo");
            motivoTipoRepository.save(m2);

            MotivoTipo m3 = new MotivoTipo();
            m3.setNombre("Fallo en el sistema de registro");
            m3.setDescripcion("El sistema de registro de datos presentó fallas o inconsistencias");
            motivoTipoRepository.save(m3);

            MotivoTipo m4 = new MotivoTipo();
            m4.setNombre("Vandalismo");
            m4.setDescripcion("Daños ocasionados por intervención externa no autorizada");
            motivoTipoRepository.save(m4);

            MotivoTipo m5 = new MotivoTipo();
            m5.setNombre("Fallo en la fuente de alimentación");
            m5.setDescripcion("La fuente de energía falló o fue interrumpida");
            motivoTipoRepository.save(m5);

            MotivoTipo m6 = new MotivoTipo();
            m6.setNombre("Otro motivo");
            m6.setDescripcion("Motivo no contemplado en las opciones anteriores. Especifique en el comentario.");
            motivoTipoRepository.save(m6);

            // 3. EMPLEADOS Y USUARIOS
            
            // --- Usuario 1: Juan ---
            Empleado juan = new Empleado();
            juan.setNombre("Juan");
            juan.setApellido("Pérez");
            juan.setMail("juan.perez@empresa.com");
            juan.setRol(rolResponsable);
            juan = empleadoRepository.save(juan);

            Usuario usuarioJuan = new Usuario();
            usuarioJuan.setNombreUsuario("juan.perez");
            usuarioJuan.setEmpleado(juan);
            usuarioJuan = usuarioRepository.save(usuarioJuan);

            // --- Usuario 2: María ---
            Empleado maria = new Empleado();
            maria.setNombre("María");
            maria.setApellido("Rodríguez");
            maria.setMail("maria.rodriguez@empresa.com");
            maria.setRol(rolResponsable);
            maria = empleadoRepository.save(maria);

            Usuario usuarioMaria = new Usuario();
            usuarioMaria.setNombreUsuario("maria.rodriguez");
            usuarioMaria.setEmpleado(maria);
            usuarioMaria = usuarioRepository.save(usuarioMaria);

            // --- Usuario 3: Carlos ---
            Empleado carlos = new Empleado();
            carlos.setNombre("Carlos");
            carlos.setApellido("Gómez");
            carlos.setMail("carlos.gomez@empresa.com");
            carlos.setRol(rolResponsable);
            carlos = empleadoRepository.save(carlos);

            Usuario usuarioCarlos = new Usuario();
            usuarioCarlos.setNombreUsuario("carlos.gomez");
            usuarioCarlos.setEmpleado(carlos);
            usuarioCarlos = usuarioRepository.save(usuarioCarlos);

            // 4. ESTACIONES Y SISMÓGRAFOS
            // CAMBIO IMPORTANTE: NO guardamos CambioEstado manualmente. Dejamos que Sismografo lo haga.

            // --- Estación 1 (La Falda) ---
            CambioEstado ce1 = new CambioEstado(new Date(), null, usuarioJuan);
            ce1.setNombre("En Servicio");
            // ce1 NO SE GUARDA AQUÍ. Es "transient" (nuevo) hasta que se guarde el sismógrafo.

            Sismografo sismo1 = new Sismografo();
            sismo1.setCambioEstado(ce1); // Asignamos el objeto nuevo
            sismo1 = sismografoRepository.save(sismo1); // ESTO guarda el Sismografo Y el CambioEstado automáticamente.

            EstacionSismologica estacion1 = new EstacionSismologica();
            estacion1.setNombre("Estación La Falda");
            estacion1.setSismografo(sismo1);
            estacion1 = estacionRepository.save(estacion1);

            // --- Estación 2 (Cerro Uritorco) ---
            CambioEstado ce2 = new CambioEstado(new Date(), null, usuarioMaria);
            ce2.setNombre("En Servicio");
            // NO guardar ce2 manualmente

            Sismografo sismo2 = new Sismografo();
            sismo2.setCambioEstado(ce2);
            sismo2 = sismografoRepository.save(sismo2);

            EstacionSismologica estacion2 = new EstacionSismologica();
            estacion2.setNombre("Estación Cerro Uritorco");
            estacion2.setSismografo(sismo2);
            estacion2 = estacionRepository.save(estacion2);

            // --- Estación 3 (Villa Carlos Paz) ---
            CambioEstado ce3 = new CambioEstado(new Date(), null, usuarioCarlos);
            ce3.setNombre("En Servicio");
            // NO guardar ce3 manualmente

            Sismografo sismo3 = new Sismografo();
            sismo3.setCambioEstado(ce3);
            sismo3 = sismografoRepository.save(sismo3);

            EstacionSismologica estacion3 = new EstacionSismologica();
            estacion3.setNombre("Estación V. Carlos Paz");
            estacion3.setSismografo(sismo3);
            estacion3 = estacionRepository.save(estacion3);

            // 5. ÓRDENES DE INSPECCIÓN
            
            // Orden 1
            OrdenDeInspeccion orden1 = new OrdenDeInspeccion();
            orden1.setNumeroOrden(2025);
            orden1.setFechaHoraFinalizacion(LocalDateTime.now().minusHours(5));
            orden1.setEstado(estadoCompletada);
            orden1.setUsuario(usuarioJuan);
            orden1.setEstacionSismologica(estacion1);
            ordenRepository.save(orden1);

            // Orden 2
            OrdenDeInspeccion orden2 = new OrdenDeInspeccion();
            orden2.setNumeroOrden(2026);
            orden2.setFechaHoraFinalizacion(LocalDateTime.now().minusDays(2));
            orden2.setEstado(estadoCompletada);
            orden2.setUsuario(usuarioMaria);
            orden2.setEstacionSismologica(estacion2);
            ordenRepository.save(orden2);

            // Orden 3
            OrdenDeInspeccion orden3 = new OrdenDeInspeccion();
            orden3.setNumeroOrden(2027);
            orden3.setFechaHoraFinalizacion(LocalDateTime.now().minusMinutes(30));
            orden3.setEstado(estadoCompletada);
            orden3.setUsuario(usuarioCarlos);
            orden3.setEstacionSismologica(estacion3);
            ordenRepository.save(orden3);

            // Orden 4
            OrdenDeInspeccion orden4 = new OrdenDeInspeccion();
            orden4.setNumeroOrden(2028);
            orden4.setFechaHoraFinalizacion(LocalDateTime.now().minusWeeks(1));
            orden4.setEstado(estadoCompletada);
            orden4.setUsuario(usuarioJuan);
            orden4.setEstacionSismologica(estacion2);
            ordenRepository.save(orden4);

            System.out.println("---------- CARGA DE DATOS FINALIZADA ----------");
        }
    }
}
