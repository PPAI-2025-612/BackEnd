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
     * Ya no usamos @Autowired como antes aquí porque los crearemos manualmente en el método new(), para validar consistencia con el diagrama.
     */
    public GestorCierreOrdenInspeccion() {
        this.observadores = new ArrayList<>();
    }
    
    // --- IMPLEMENTACIÓN PATRÓN OBSERVER (Sujeto) ---

    // Método para agregar suscripciones
    public void suscribir(IObserverOrdenInspeccion observador) {
        if (!observadores.contains(observador)) {
            this.observadores.add(observador);
        }
    }
    
    public void quitar(IObserverOrdenInspeccion observador) {
        this.observadores.remove(observador);
    }
        
    // --- MÉTODOS PÚBLICOS (API / CONTROLADOR) ---

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
    
    public List<ordenInspeccion> opcionCierreOrdenDeInspeccion() {
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
        OrdenDeInspeccion seleccionadaOrden = ordenInspeccionRepository.findById(idOrdenInspeccion).get();
        if(validarMotivo(motivos)) {
            cerrarOrdenInspeccion(seleccionadaOrden, motivos, empleado);
        }
        return null;
    }
    
   

    @Transactional
    private void cerrarOrdenInspeccion(OrdenDeInspeccion seleccionadaOrden, List<MotivoTipo> motivos, Usuario empleado) {
        LocalDateTime fechaActual = getFechaHoraActual();
        
        Estado estadoCerrado = buscarEstadoCerrado();
        
        // 1. Lógica de Negocio y Persistencia
        MotivoTipo motivoSeleccionado = (motivos != null && !motivos.isEmpty()) ? motivos.get(0) : null;
        
        seleccionadaOrden.cerrarOrdenInspeccion(estadoCerrado, fechaActual, this.comentario, motivoSeleccionado);
        
        actualizarSismografoAFueraDeServicio(seleccionadaOrden, motivos, fechaActual, empleado);
        
        ordenInspeccionRepository.save(seleccionadaOrden);
        System.out.println("Gestor: Orden guardada en BD. Iniciando notificaciones...");
        
        
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
        this.notificar(seleccionadaOrden, motivos);
    }

    private void notificar(OrdenDeInspeccion orden, List<MotivoTipo> motivos) {
        System.out.println("Gestor: Ejecutando notificar() a " + observadores.size() + " observadores.");

        // Recolectar datos para enviar a los observadores
        List<String> listaMails = buscarEmpleadoResponsableReparacion();
        String mailDestino = listaMails.isEmpty() ? "default@mail.com" : listaMails.get(0); 

        String motivoStr = (motivos != null && !motivos.isEmpty()) 
                                ? motivos.get(0).getMotivoTipo() 
                                : "N/A";
        
        String comentarioStr = (this.comentario != null) ? this.comentario : "Cierre de orden.";
        
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

    private void actualizarSismografoAFueraDeServicio(OrdenDeInspeccion seleccionadaOrden, List<MotivoTipo> motivos, LocalDateTime fechaActual, Usuario usuario) {
        seleccionadaOrden.actualizarSismografoAFueraDeServicio(motivos,fechaActual, usuario);
    }

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