package com.dsi.ppai.redsismica.model;

import java.time.LocalDateTime; // Usar LocalDateTime para manejo moderno de fechas
import java.util.Date;
import java.util.List;

// Importa las anotaciones de Jakarta si estás en Spring Boot 3+
// o de Javax si estás en Spring Boot 2.x
import jakarta.persistence.Column; // Cambiado a jakarta.persistence
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table; // Es buena práctica especificar el nombre de la tabla

import lombok.Data;
import lombok.NoArgsConstructor; // Lombok para constructor sin args
import lombok.AllArgsConstructor; // Lombok para constructor con todos los args

@Entity
@Table(name = "ordenes_inspeccion") // Asegúrate de que el nombre de la tabla coincida en tu DB
@Data // Genera getters, setters, toString, equals, hashCode
@NoArgsConstructor // Genera constructor sin argumentos
@AllArgsConstructor // Genera constructor con todos los argumentos
public class OrdenDeInspeccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Campos existentes (verificados en tu estructura)
    @Column(name = "fecha_hora_cierre") // Nombre de columna en DB
    private LocalDateTime fechaHoraCierre; // Cambiado a LocalDateTime

    @Column(name = "fecha_hora_finalizacion")
    private LocalDateTime fechaHoraFinalizacion; // Cambiado a LocalDateTime

    @Column(name = "fecha_hora_inicio")
    private LocalDateTime fechaHoraInicio; // Cambiado a LocalDateTime

    @Column(name = "numero_orden", nullable = false, unique = true)
    private int numeroOrden;

    // --- Nuevos campos basados en el frontend OrdenInspeccion DTO ---
    @Column(nullable = false)
    private String cliente; // Campo para el cliente

    @Column(name = "identificador_sismografo", nullable = false)
    private String identificadorSismografo; // Campo para el identificador del sismógrafo

    // Asumimos que Empleado ya tiene el nombre, pero el frontend envía un 'responsable' string.
    // Si 'Empleado' va a ser el responsable de la creación y también del cierre,
    // podríamos usar el mismo Empleado para la referencia.
    // Sin embargo, si el frontend envía un String simple, es mejor que coincida.
    @Column(nullable = false)
    private String responsable; // El responsable inicial (según frontend DTO)

    @Column(name = "tareas_completadas")
    private Integer tareasCompletadas; // Cantidad de tareas completadas

    @Column(name = "total_tareas")
    private Integer totalTareas; // Total de tareas

    @Column(nullable = false)
    private String estado; // El estado de la orden (ej. "Completada", "Cerrada")
    // Se cambia de @ManyToOne Estado a String para manejarlo más fácilmente como texto
    // Si mantienes @ManyToOne Estado, necesitarías gestionar la entidad Estado correctamente.
    // Para simplificar, lo definimos como String para coincidir con el DTO del frontend.


    // --- Campos específicos para el Cierre de Orden (ya existentes algunos, otros nuevos) ---
    @Column(name = "observacion_cierre", length = 1000)
    private String observacionCierre; // Ya lo tenías, mantenemos.

    @Column(name = "responsable_cierre_id")
    private String responsableCierreId; // ID del responsable que realiza el cierre

    @Column(name = "responsable_cierre_nombre")
    private String responsableCierreNombre; // Nombre del responsable que realiza el cierre

    // Para los motivos seleccionados: se guardan como un String JSON para simplificar la persistencia
    @Column(name = "motivos_seleccionados_json", columnDefinition = "TEXT")
    private String motivosSeleccionadosJson;

    // --- Relaciones existentes ---
    // Si Empleado es el responsable inicial de la orden, puedes mantenerlo así.
    // Asegúrate de que este Empleado pueda mapearse al campo 'responsable' del DTO del frontend.
    @ManyToOne
    private Empleado empleado; // Mapea al empleado que creó/gestiona la orden

    // Si 'Estado' es una entidad aparte y quieres mantener la relación,
    // debes gestionarla. Para este caso de uso, usar un String para 'estado'
    // es más simple si solo necesitas el texto del estado.
    // @ManyToOne
    // private Estado estadoEntidad; // Si mantienes la entidad Estado

    @ManyToOne
    private EstacionSismologica estacionSismologica;


    // NOTA IMPORTANTE:
    // Al usar Lombok @Data, @NoArgsConstructor y @AllArgsConstructor,
    // los getters, setters y constructores se generarán automáticamente.
    // Si no usas Lombok, deberías añadir manualmente:
    // - Un constructor sin argumentos (público)
    // - Getters y Setters para todos los campos
    // - toString(), equals(), hashCode() si los necesitas explícitamente.
    
    public Boolean esDelRI(Empleado empleado) {
		return this.empleado.equals(empleado);
	}
	
	public EstacionSismologica getIdSismografo() {
		return this.estacionSismologica;
	}

	public List<MotivoTipo> obtenerMotivoTipo() {
		return estacionSismologica.obtenerMotivoTipo();
		
	}

	public void cerrarOrdenInspeccion(Estado estadoCerrado, LocalDateTime fechaActual) {
		setEstado(estadoCerrado.getNombreEstado());
		setFechaHoraCierre(fechaActual);
		
	}

	public void actualizarSismografoAFueraDeServicio(List<MotivoFueraServicio> motivos, Date fechaActual) {
		this.estacionSismologica.actualizarSismografoAFueraDeServicio(motivos,fechaActual);
		
	}
}