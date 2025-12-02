package com.dsi.ppai.redsismica.services;

import com.dsi.ppai.redsismica.dto.CierreOrdenRequest;
import com.dsi.ppai.redsismica.dto.MotivoSeleccionadoDTO; // Import necesario
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

// Interface del Observer
import com.dsi.ppai.redsismica.observer.IObserverOrdenInspeccion; 

// OBSERVADORES CONCRETOS 
import com.dsi.ppai.redsismica.services.mail.NotificacionMail; 
import com.dsi.ppai.redsismica.services.monitorccrs.PublicadorCCRS;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List; 
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

@Service
public class GestorCierreOrdenInspeccion {

    // --- DEPENDENCIAS DE REPOSITORIO ---
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
    
    // Lista de observadores (Sujeto)
    private List<IObserverOrdenInspeccion> observadores;
    
    // --- Atributos de estado ---
    private long ordenInspeccion;
    private String motivo;
    private String comentario;
        
    /**
     * CONSTRUCTOR
     * Inicializamos la lista de observadores vacía.
     */
    public GestorCierreOrdenInspeccion() {
        this.observadores = new ArrayList<>();
    }
    
    // --- IMPLEMENTACIÓN PATRÓN OBSERVER (Sujeto) ---

    public void suscribir(IObserverOrdenInspeccion observador) {
        if (!observadores.contains(observador)) {
            this.observadores.add(observador);
        }
    }
    
    public void quitar(IObserverOrdenInspeccion observador) {
        this.observadores.remove(observador);
    }
        
    // --- MÉTODOS PÚBLICOS (API / CONTROLADOR) ---40)cerrarOrden()

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
    
    public List<ordenInspeccion> OrdenDeInspeccion() {
        Usuario empleado = buscarRILogeado();
        List<ordenInspeccion> ordenFinales = buscarOrdenInspeccionCompletamenteRealizadaDelRI(empleado);
        ordenFinales = ordenarOrdenesDeInspeccion(ordenFinales);
        return ordenFinales;
    }
    //25)tomarObservacionCierre()
    public List<String> tomarObservacionCierre() {
        return habilitarActualizarSituacionSismografo();
    }
    //22)tomarSeleccionOrdenDeInspeccion()
    public void tomarSeleccionOrdenDeInspeccion(long idOrdenInspeccion){
        this.ordenInspeccion=idOrdenInspeccion;
    }
    //32)tomarSeleccionMotivo()
    public void tomarSeleccionMotivo(String motivo){
        this.motivo=motivo;
    }
    //35)tomarIngresoComentario()
    public void tomarIngresoComentario(String comentario){
        this.comentario=comentario;
    }
    
    //38)tomarConfirmacionDeCierreInspeccion() 
    // --- CAMBIO: Se recibe la lista de DTOs (Motivos + Comentarios) y la observación General
    public Objects tomarConfirmacionDeCierreInspeccion(long idOrdenInspeccion, List<MotivoSeleccionadoDTO> motivosDTO, Usuario empleado, String observacionGeneral) {
        
        OrdenDeInspeccion seleccionadaOrden = ordenInspeccionRepository.findById(idOrdenInspeccion).get();
        
        // Llamamos al método de cierre interno con los datos nuevos
        cerrarOrdenInspeccion(seleccionadaOrden, motivosDTO, empleado, observacionGeneral);
        
        return null;
    }
    
    @Transactional
    // --- CAMBIO: Firma actualizada para recibir DTOs y observación general
    private void cerrarOrdenInspeccion(OrdenDeInspeccion seleccionadaOrden, List<MotivoSeleccionadoDTO> motivosDTO, Usuario empleado, String obsGeneral) {
        LocalDateTime fechaActual = getFechaHoraActual();
        Estado estadoCerrado = buscarEstadoCerrado();
        
        // 1. Lógica de Negocio y Persistencia
        
        // Seteamos estado, fecha y observación general
        seleccionadaOrden.cerrarOrdenInspeccion(estadoCerrado, fechaActual, obsGeneral);
        
        // --- PROCESAMIENTO DE MÚLTIPLES MOTIVOS (NUEVO) ---
        // Lista auxiliar para usar en la actualización del sismógrafo (mantenemos compatibilidad de tipos)
        List<MotivoTipo> listaTiposParaSismografo = new ArrayList<>();
        
        if (motivosDTO != null) {
            for (MotivoSeleccionadoDTO dto : motivosDTO) {
                // Buscamos el MotivoTipo real en la BD usando el ID del DTO
                Long idTipo = Long.parseLong(dto.getId());
                MotivoTipo tipo = motivoTipoRepository.findById(idTipo).orElse(null);
                
                if (tipo != null) {
                    // IMPLEMENTACIÓN: Agregamos el motivo a la orden usando la nueva clase intermedia
                    // Esto guarda el motivo Y su comentario específico
                    seleccionadaOrden.agregarMotivoConComentario(tipo, dto.getComentario());
                    
                    // Agregamos a la lista auxiliar para actualizar sismógrafo
                    listaTiposParaSismografo.add(tipo);
                }
            }
        }
        // ----------------------------------------------------
        
        // Actualizamos sismógrafo pasando la lista de tipos
        actualizarSismografoAFueraDeServicio(seleccionadaOrden, listaTiposParaSismografo, fechaActual, empleado);
        
        // Guardamos todo (CascadeType.ALL se encarga de los motivos)
        ordenInspeccionRepository.save(seleccionadaOrden);
        System.out.println("Gestor: Orden guardada en BD. Iniciando notificaciones...");
        
        
        // --- MANEJO MANUAL DE OBSERVADORES (COMO EN TU CÓDIGO ORIGINAL) ---
        this.observadores.clear();

        //  62: new() NotificacionMail
        NotificacionMail notificacionMail = new NotificacionMail();
        
        //  63: suscribir(o:IObserverNotCierreOrdenInsp)
        this.suscribir(notificacionMail);

        //  64: new() PublicadorCCRS
        PublicadorCCRS publicadorCCRS = new PublicadorCCRS();
        
        //  65: suscribir(o:IObserverNotCierreOrdenInsp)
        this.suscribir(publicadorCCRS);

        //  66: notificar()
        this.notificar(seleccionadaOrden, listaTiposParaSismografo);
    }

    //66)notificar()
    private void notificar(OrdenDeInspeccion orden, List<MotivoTipo> motivos) {
        System.out.println("Gestor: Ejecutando notificar() a " + observadores.size() + " observadores.");

        List<String> listaMails = buscarEmpleadoResponsableReparacion();
        String mailDestino = listaMails.isEmpty() ? "default@mail.com" : listaMails.get(0); 

        String motivoStr = (motivos != null && !motivos.isEmpty()) 
                                ? motivos.get(0).getMotivoTipo() 
                                : "N/A";
        
        // Usamos el comentario de la orden directamente
        String comentarioStr = (orden.getObservacionCierre() != null) ? orden.getObservacionCierre() : "Cierre de orden.";
        
        String id = orden.getId().toString();
        String nroOrd = String.valueOf(orden.getNumeroOrden());
        String estado = orden.getEstado().toString(); 
        String tipo = "Inspección"; 
        LocalDateTime fechaInicio = orden.getFechaHoraCierre(); 
        LocalDateTime fechaFin = orden.getFechaFinalizacion();
      
        // [Bucle del Observer]: Iterar y actualizar
        for (IObserverOrdenInspeccion obs : observadores) {
            // Pasos 68 (enviarMail) y 70 (publicarEnMonitor) ocurren DENTRO de este método actualizar en cada clase
            obs.actualizar(id, nroOrd, estado, tipo, fechaInicio, fechaFin, motivoStr, comentarioStr, mailDestino);
        }
    }

    // --- MÉTODOS PRIVADOS  ---
    //57)buscarEmpleadoResponsableReparacion()
    
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
    //48)actualizarSismografoAFueraDeServicio()
    private void actualizarSismografoAFueraDeServicio(OrdenDeInspeccion seleccionadaOrden, List<MotivoTipo> motivos, LocalDateTime fechaActual, Usuario usuario) {
        seleccionadaOrden.actualizarSismografoAFueraDeServicio(motivos,fechaActual, usuario);
    }
    //41)getFechaHoraActual()
    private LocalDateTime getFechaHoraActual() {
        return LocalDateTime.now();
    }
    //42)buscarEstadoCerrado()
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
    //39)validarMotivo()
    private boolean validarMotivo(List<MotivoTipo> motivos) {
        for (MotivoTipo motivo : motivos) {
            if(motivo == null) {
                return false;
            }
        }
        return true;
    }
    //4)buscarRIlogueado()
    private Usuario buscarRILogeado() {
        Sesion sesionAcual = sesionRepository.findById(1L).get();
        return sesionAcual.getRILogueado();
    }
    //7)buscarOrdenInspeccionCompletamenteRealizadaDelRI
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
    //18)ordenarOrdenesDeInspeccion()
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
    //26)habilitarActualizarSituacionSismografo()
    private List<String> habilitarActualizarSituacionSismografo() {
        return buscarMotivosTipo() ;
    }
    //27)buscarMotivosTipo()
    private List<String> buscarMotivosTipo() {
        List<String> motivoTipo = new ArrayList<>();
        List<MotivoTipo> motivos = (List<MotivoTipo>) motivoTipoRepository.findAll();
        for (MotivoTipo motivo : motivos) {
            motivoTipo.add(motivo.getMotivoTipo());
        }
        return motivoTipo;
    }
}