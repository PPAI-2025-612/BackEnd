package com.dsi.ppai.redsismica.dto;

import java.util.List;

public class CierreOrdenRequest {

    private String ordenId;
    private String responsableId;
    private String responsableNombre;
    private String observacionCierre;
    private List<MotivoSeleccionadoDTO> motivosSeleccionados;
    private String fechaCierre;

    public CierreOrdenRequest() {}

    public String getOrdenId() {
        return ordenId;
    }

    public void setOrdenId(String ordenId) {
        this.ordenId = ordenId;
    }

    public String getResponsableId() {
        return responsableId;
    }

    public void setResponsableId(String responsableId) {
        this.responsableId = responsableId;
    }

    public String getResponsableNombre() {
        return responsableNombre;
    }

    public void setResponsableNombre(String responsableNombre) {
        this.responsableNombre = responsableNombre;
    }

    public String getObservacionCierre() {
        return observacionCierre;
    }

    public void setObservacionCierre(String observacionCierre) {
        this.observacionCierre = observacionCierre;
    }

    public List<MotivoSeleccionadoDTO> getMotivosSeleccionados() {
        return motivosSeleccionados;
    }

    public void setMotivosSeleccionados(List<MotivoSeleccionadoDTO> motivosSeleccionados) {
        this.motivosSeleccionados = motivosSeleccionados;
    }

    public String getFechaCierre() {
        return fechaCierre;
    }

    public void setFechaCierre(String fechaCierre) {
        this.fechaCierre = fechaCierre;
    }
}