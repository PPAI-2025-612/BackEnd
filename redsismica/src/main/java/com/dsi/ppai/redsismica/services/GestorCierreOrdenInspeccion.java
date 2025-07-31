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
import com.dsi.ppai.redsismica.services.mail.InterfaceMail;
import com.dsi.ppai.redsismica.services.monitorccrs.InterfaceCCRS;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GestorCierreOrdenInspeccion {

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
	
	private final InterfaceMail notificador;
	
	private final InterfaceCCRS monitor;
	
	private long ordenInspeccion;
	
	private String motivo;
	
	private String comentario;
		
	// Inyección por constructor
	public GestorCierreOrdenInspeccion(InterfaceMail notificador, InterfaceCCRS monitor) {
		this.notificador = notificador;
		this.monitor = monitor;
	}
	    
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

        // No persistimos todavía, sólo simulamos éxito
        return "Orden " + request.getOrdenId() + " cerrada exitosamente por " + request.getResponsableNombre();
    }
    
    
	public List<ordenInspeccion> opcionCierreOrdenDeInspeccion() { //listo
		
		//buscar empleado logueado
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
			
			cerrarOrdenInspeccion(seleccionadaOrden,motivos, empleado);
			
		}
		return null;
	}
	
	private void publicarNotificacionEnMonitorCCRS(String mensaje) {
		monitor.publicarEnMonitor(mensaje);
		
	}

	private void enviarNotificacionEmpleadoReparacion(List<String> listaMails, String asunto, String mensaje) {		
		notificador.enviarmail(listaMails, asunto, mensaje);
	}


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

	private void cerrarOrdenInspeccion(OrdenDeInspeccion seleccionadaOrden, List<MotivoTipo> motivos, Usuario empleado) {
		LocalDateTime fechaActual = getFechaHoraActual();
		
		Estado estadoCerrado = buscarEstadoCerrado();
		
		seleccionadaOrden.cerrarOrdenInspeccion(estadoCerrado,fechaActual);
		
		actualizarSismografoAFueraDeServicio(seleccionadaOrden, motivos, fechaActual, empleado);
		
		List<String> listaMails = buscarEmpleadoResponsableReparacion();
		
		String asunto = "Sismografo Fuera de servicio";
		String mensaje = "Hola, se le informa que el sismografo " + seleccionadaOrden.getIdSismografo() 
				+ " se encuentra en estado Fuera de Servicio"
				+ " al momento de "+ fechaActual.toString() + " por los motivos " + motivos.toString();
		enviarNotificacionEmpleadoReparacion(listaMails,asunto,mensaje);
		
		publicarNotificacionEnMonitorCCRS(mensaje);
		//actu
		ordenInspeccionRepository.save(seleccionadaOrden);
		
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