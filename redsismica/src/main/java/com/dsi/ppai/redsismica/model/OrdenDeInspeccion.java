package com.dsi.ppai.redsismica.model;

import java.time.LocalDateTime; // Usar LocalDateTime para manejo moderno de fechas
import java.util.ArrayList;
import java.util.List;

// Importa las anotaciones de Jakarta si estás en Spring Boot 3+
// o de Javax si estás en Spring Boot 2.x
import jakarta.persistence.Column; // Cambiado a jakarta.persistence
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.*; // Import necesario para OneToMany y CascadeType
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table; // Es buena práctica especificar el nombre de la tabla
import lombok.AllArgsConstructor; // Lombok para constructor con todos los args
import lombok.Data;
import lombok.NoArgsConstructor; // Lombok para constructor sin args

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

    @Column(name = "numero_orden", nullable = false, unique = true)
    private int numeroOrden;

    // --- Campos específicos para el Cierre de Orden (ya existentes algunos, otros nuevos) ---
    @Column(name = "observacion_cierre", length = 1000)
    private String observacionCierre; // Ya lo tenías, mantenemos.

    @ManyToOne
    @JoinColumn(name = "estado_id", nullable = false) // Nombre de la columna FK que hace referencia a Estado
    private Estado estado; // El estado de la orden (ej. "Completada", "Cerrada")
    // Se cambia de @ManyToOne Estado a String para manejarlo más fácilmente como texto
    // Si mantienes @ManyToOne Estado, necesitarías gestionar la entidad Estado correctamente.
    // Para simplificar, lo definimos como String para coincidir con el DTO del frontend.

    // --- Relaciones existentes ---
    // Si Empleado es el responsable inicial de la orden, puedes mantenerlo así.
    // Asegúrate de que este Empleado pueda mapearse al campo 'responsable' del DTO del frontend.
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario; // Mapea al empleado que creó/gestiona la orden

    // Si 'Estado' es una entidad aparte y quieres mantener la relación,
    // debes gestionarla. Para este caso de uso, usar un String para 'estado'
    // es más simple si solo necesitas el texto del estado.
    // @ManyToOne
    // private Estado estadoEntidad; // Si mantienes la entidad Estado

    @ManyToOne
    private EstacionSismologica estacionSismologica;

    // -------------------------------------------------------------------------
    // CAMBIO IMPLEMENTADO: Relación 1 a N con la clase intermedia MotivoFueraServicio
    // -------------------------------------------------------------------------
    // Esta lista almacena todos los motivos seleccionados y sus comentarios específicos.
    // cascade = ALL permite que al guardar la Orden, se guarden los motivos automáticamente.
    @OneToMany(mappedBy = "orden", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MotivoFueraServicio> motivosDetalle = new ArrayList<>();


    // NOTA IMPORTANTE:
    // Al usar Lombok @Data, @NoArgsConstructor y @AllArgsConstructor,
    // los getters, setters y constructores se generarán automáticamente.
    // Si no usas Lombok, deberías añadir manualmente:
    // - Un constructor sin argumentos (público)
    // - Getters y Setters para todos los campos
    // - toString(), equals(), hashCode() si los necesitas explícitamente.
    
    //8)esDelRI()
    public Boolean esDelRI(Usuario usuario) {
        return this.usuario.equals(usuario);
    }
    //15)getIdSismografo()
    public int getIdSismografo() {
        return this.estacionSismologica.getIdSismografo();
    }

    // --- MÉTODO ACTUALIZADO ---45)cerrarOrdenInspeccion()
    // Ahora solo recibe los datos generales de la orden. 
    // Los motivos específicos se agregan usando 'agregarMotivoConComentario' antes o durante el proceso.
    public void cerrarOrdenInspeccion(Estado estadoCerrado, LocalDateTime fechaActual, String observacion) {
        setEstado(estadoCerrado);//46)setEstado()
        setFechaHoraCierre(fechaActual);//47)setFechaHoraCierre()
        
        // Seteamos los nuevos valores para que se guarden en la BD
        setObservacionCierre(observacion);
    }
    
    // --- NUEVO MÉTODO HELPER ---
    // Permite agregar un motivo a la lista 'motivosDetalle' creando la instancia de la clase intermedia.
    public void agregarMotivoConComentario(MotivoTipo tipo, String comentario) {
        MotivoFueraServicio nuevoMotivo = new MotivoFueraServicio(this, tipo, comentario);
        this.motivosDetalle.add(nuevoMotivo);
    }

    //49)actualizarSismografoAFueraDeServicio()
    public void actualizarSismografoAFueraDeServicio(List<MotivoTipo> motivos, LocalDateTime fechaActual, Usuario usuario) {
        this.estacionSismologica.actualizarSismografoAFueraDeServicio(motivos,fechaActual, usuario);
    }

    public List<Empleado> buscarEmpleadoResponsableReparacion() {
        return null;
    }
    //9)esCompletamenteRealizada()
    public boolean esCompletamenteRealizada() {
        return estado.esCompletamenteRealizada();
    }
    //12)getFechaFinalizacion()
    public LocalDateTime getFechaFinalizacion() {
        return fechaHoraFinalizacion;
    }
    //13)getNombreEstacionSismologica()
    public String getNombreEstacionSismologica() {
        return estacionSismologica.getNombre(); 
    }
}