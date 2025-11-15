package com.dsi.ppai.redsismica.services;

import com.dsi.ppai.redsismica.dto.CierreOrdenRequest;
import com.dsi.ppai.redsismica.dto.MotivoSeleccionadoDTO;
import com.dsi.ppai.redsismica.model.Empleado;
import com.dsi.ppai.redsismica.model.Estado;
import com.dsi.ppai.redsismica.model.MotivoTipo;
import com.dsi.ppai.redsismica.model.OrdenDeInspeccion;
import com.dsi.ppai.redsismica.model.Sesion;
import com.dsi.ppai.redsismica.model.Usuario;
import com.dsi.ppai.redsismica.model.values.ordenInspeccion;
import com.dsi.ppai.redsismica.repository.EmpleadoRepository;
import com.dsi.ppai.redsismica.repository.EstadoRepository;
import com.dsi.ppai.redsismica.repository.MotivoTipoRepository;
import com.dsi.ppai.redsismica.repository.OrdenInspeccionRepository;
import com.dsi.ppai.redsismica.repository.SesionRepository;

// ¡IMPORT NUEVO!
import com.dsi.ppai.redsismica.observer.IObserverOrdenInspeccion; 

// ¡IMPORTS ELIMINADOS!
// import com.dsi.ppai.redsismica.services.mail.InterfaceMail;
// import com.dsi.ppai.redsismica.services.monitorccrs.InterfaceCCRS;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List; // <-- ¡MUY IMPORTANTE!
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional; // <-- IMPORTANTE para persistencia

@Service
public class GestorCierreOrdenInspeccion {

    // --- DEPENDENCIAS DE REPOSITORIO (igual que antes) ---
    @Autowired
    private SesionRepository sesionRepository;
    
    @Autowired
    private OrdenInspeccionRepository ordenInspeccionRepository;
    
    @Autowired
    private EstadoRepository estadoRepository;
    
    @Autowired
    private EmpleadoRepository empleadoRepository;
    
    @Autowired
    private MotivoTipoRepository motivoTipoRepository;
    
    // --- ¡DEPENDENCIAS AHORA DESACOPLADAS! ---
    
    // 1. ELIMINADAS:
    // private final InterfaceMail notificador;
    // private final InterfaceCCRS monitor;
    
    // 2. AÑADIDA:
    private final List<IObserverOrdenInspeccion> observadores;
    
    // --- Atributos de estado (igual que antes) ---
    private long ordenInspeccion;
    private String motivo;
    private String comentario;
        
    // 3. CONSTRUCTOR REFACTORIZADO
    @Autowired
    public GestorCierreOrdenInspeccion(List<IObserverOrdenInspeccion> observadores) {
        // Spring inyectará automáticamente NotificacionMail y PublicadorCCRS aquí
        this.observadores = observadores;
    }
        
    // ... (El método public String cerrarOrden(CierreOrdenRequest request) 
    // ...  queda exactamente igual, no se toca)
    public String cerrarOrden(CierreOrdenRequest request) throws IllegalArgumentException {
        if (request.getObservacionCierre() == null || request.getObservacionCierre().trim().isEmpty()) {
            throw new IllegalArgumentException("La observación de cierre es obligatoria");
        }

        if (request.getMotivosSeleccionados() == null || request.getMotivosSeleccionados().isEmpty()) {
            throw new IllegalArgumentException("Debe seleccionar al menos un motivo");
        }

        for (MotivoSeleccionadoDTO motivo : request.getMotivosSeleccionados()) {
            if (motivo.getComentario() == null || motivo.getComentario().trim().isEmpty()) {
                throw new IllegalArgumentException("Todos los motivos seleccionados deben tener un comentario");
            }
        }
        return "Orden " + request.getOrdenId() + " cerrada exitosamente por " + request.getResponsableNombre();
    }
    
    
    // ... (Todos los métodos públicos de lógica de negocio
    // ...  quedan exactamente igual, no se tocan)
    
    public List<ordenInspeccion> opcionCierreOrdenDeInspeccion() { //listo
        Usuario empleado = buscarRILogeado();
        List<ordenInspeccion> ordenFinales = buscarOrdenInspeccionCompletamenteRealizadaDelRI(empleado);
        ordenFinales = ordenarOrdenesDeInspeccion(ordenFinales);
        return ordenFinales;
    }
    
    public List<String> tomarObservacionCierre() {
        return habilitarActualizarSituacionSismografo();
    }
    
    public void tomarSeleccionOrdenDeInspeccion(long idOrdenInspeccion){
        this.ordenInspeccion=idOrdenInspeccion;
    }
    
    public void tomarSeleccionMotivo(String motivo){
        this.motivo=motivo;
    }
    
    public void tomarIngresoComentario(String comentario){
        this.comentario=comentario;
    }
    
    public Objects tomarConfirmacionDeCierreInspeccion(long idOrdenInspeccion, List<MotivoTipo> motivos, Usuario empleado) {
        // (Asumo que el 'this.comentario' y 'this.motivo' se setearon antes con los métodos de arriba)
        OrdenDeInspeccion seleccionadaOrden = ordenInspeccionRepository.findById(idOrdenInspeccion).get();
        if(validarMotivo(motivos)) {
            cerrarOrdenInspeccion(seleccionadaOrden, motivos, empleado);
        }
        return null;
    }
    
    // 4. MÉTODOS DE NOTIFICACIÓN ACOPЛADOS (ELIMINADOS)
    /*
    private void publicarNotificacionEnMonitorCCRS(String mensaje) { ... }
    private void enviarNotificacionEmpleadoReparacion(List<String> listaMails, String asunto, String mensaje) { ... }
    */

    // 5. NUEVO MÉTODO NOTIFICAR (Patrón Observer)
    private void notificar(OrdenDeInspeccion orden, List<MotivoTipo> motivos) {
        System.out.println("Gestor: Notificando a " + observadores.size() + " observadores...");

        // 1. Recolectar datos para la notificación
        List<String> listaMails = buscarEmpleadoResponsableReparacion();
        String mailDestino = listaMails.isEmpty() ? "default@mail.com" : listaMails.get(0); 

        String motivoStr = (motivos != null && !motivos.isEmpty()) 
                           ? motivos.get(0).getMotivoTipo() 
                           : "N/A";
        
        // Usamos el comentario que se guardó en el estado del Gestor
        String comentarioStr = (this.comentario != null) ? this.comentario : "Cierre de orden.";
        
        String id = orden.getId().toString();
        String nroOrd = String.valueOf(orden.getNumeroOrden());
        String estado = orden.getEstadoActual().getNombre(); // Asumo que .getEstadoActual() existe
        String tipo = "Inspección"; // Dato placeholder, ajustar si lo tienes
        LocalDateTime fechaInicio = orden.getFechaCreacion(); // Asumo que es .getFechaCreacion()
        LocalDateTime fechaFin = orden.getFechaFinalizacion();
        
        // 2. Iterar y notificar a todos los observadores
        for (IObserverOrdenInspeccion obs : observadores) {
            obs.actualizar(id, nroOrd, estado, tipo, fechaInicio, fechaFin, motivoStr, comentarioStr, mailDestino);
        }
    }


    // ... (El método buscarEmpleadoResponsableReparacion() 
    // ...  queda exactamente igual, no se toca)
    private List<String> buscarEmpleadoResponsableReparacion() {
        List<String> listaMails = new ArrayList<String>();
        List<Empleado> empleadosReparacion = (List<Empleado>) empleadoRepository.findAll();
        for (Empleado empleado : empleadosReparacion) {
            if( empleado.esResponsableReparacion()) {
                listaMails.add(empleado.getMail());
            }
        }
        return listaMails;
    }

    // ... (El método actualizarSismografoAFueraDeServicio() 
    // ...  queda exactamente igual, no se toca)
    private void actualizarSismografoAFueraDeServicio(OrdenDeInspeccion seleccionadaOrden, List<MotivoTipo> motivos, LocalDateTime fechaActual, Usuario usuario) {
        seleccionadaOrden.actualizarSismografoAFueraDeServicio(motivos,fechaActual, usuario);
    }

    // 6. MÉTODO DE CIERRE REFACTORIZADO
    @Transactional // Asegura que el save() sea atómico
    private void cerrarOrdenInspeccion(OrdenDeInspeccion seleccionadaOrden, List<MotivoTipo> motivos, Usuario empleado) {
        LocalDateTime fechaActual = getFechaHoraActual();
        
        Estado estadoCerrado = buscarEstadoCerrado();
        
        // 1. Lógica de Negocio
        seleccionadaOrden.cerrarOrdenInspeccion(estadoCerrado,fechaActual);
        actualizarSismografoAFueraDeServicio(seleccionadaOrden, motivos, fechaActual, empleado);
        
        // 2. Persistencia (¡IMPORTANTE: ANTES de notificar!)
        ordenInspeccionRepository.save(seleccionadaOrden);
        System.out.println("Gestor: Orden persistida en BD.");
        
        // 3. Notificación (Patrón Observer)
        this.notificar(seleccionadaOrden, motivos);
        
        // --- (Toda la lógica de crear 'asunto', 'mensaje' y llamar a 
        // --- 'enviar...' y 'publicar...' fue eliminada) ---
    }

    // ... (Todos los demás métodos privados de búsqueda y utilidades
    // ...  quedan exactamente igual, no se tocan)

    private LocalDateTime getFechaHoraActual() {
        return LocalDateTime.now();
    }

    private Estado buscarEstadoCerrado() {
        List<Estado> estados = (List<Estado>) estadoRepository.findAll();
        for (Estado estado : estados) {
            if(estado.esAmbitoOrdenDeInspeccion()) {
                if (estado.esCerrado()) {
                    return estado;
                }
            }
        }
        return null;
    }

    private boolean validarMotivo(List<MotivoTipo> motivos) {
        for (MotivoTipo motivo : motivos) {
            if(motivo == null) {
                return false;
            }
        }
        return true;
    }

    private Usuario buscarRILogeado() {
        Sesion sesionAcual = sesionRepository.findById(1L).get();
        return sesionAcual.getRILogueado();
    }
    
    private List<ordenInspeccion> buscarOrdenInspeccionCompletamenteRealizadaDelRI(Usuario empleado) {
        List<OrdenDeInspeccion> ordenes = (List<OrdenDeInspeccion>) ordenInspeccionRepository.findAll();
        List<ordenInspeccion> ordenFinales = new ArrayList<>();
        
        for (OrdenDeInspeccion ordenDeInspeccion : ordenes) {
            if(ordenDeInspeccion.esDelRI(empleado)) {
                if(ordenDeInspeccion.esCompletamenteRealizada()) {
                    int nroOrden = ordenDeInspeccion.getNumeroOrden();
                    LocalDateTime fechaFinalizacion = ordenDeInspeccion.getFechaFinalizacion();
                    String nombreEstacionSismologica = ordenDeInspeccion.getNombreEstacionSismologica();
                    int idSismografo = ordenDeInspeccion.getIdSismografo();
                    
                    ordenFinales.add(new ordenInspeccion(nroOrden,fechaFinalizacion,nombreEstacionSismologica,idSismografo));
                }
            }
        }
        return ordenFinales;
    }
    
    private List<ordenInspeccion> ordenarOrdenesDeInspeccion(List<ordenInspeccion> ordenes){
        Collections.sort(ordenes, new Comparator<ordenInspeccion>() {
            
            @Override
            public int compare(ordenInspeccion o1, ordenInspeccion o2) {
                if (o1.getFechaFinalizacion() == null && o2.getFechaFinalizacion() == null) return 0;
                if (o1.getFechaFinalizacion() == null) return 1;
                if (o2.getFechaFinalizacion() == null) return -1;
                return o1.getFechaFinalizacion().compareTo(o2.getFechaFinalizacion());
            }
        });
        return ordenes;
    }
    
    private List<String> habilitarActualizarSituacionSismografo() {
        return buscarMotivosTipo() ;
    }
    
    private List<String> buscarMotivosTipo() {
        List<String> motivoTipo = new ArrayList<>();
        List<MotivoTipo> motivos = (List<MotivoTipo>) motivoTipoRepository.findAll();
        for (MotivoTipo motivo : motivos) {
            motivoTipo.add(motivo.getMotivoTipo());
        }
        return motivoTipo;
    }
}