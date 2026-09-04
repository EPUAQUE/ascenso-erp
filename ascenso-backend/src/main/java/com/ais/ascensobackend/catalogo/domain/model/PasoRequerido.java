package com.ais.ascensobackend.catalogo.domain.model;

import java.util.Objects;

public class PasoRequerido {

    private final Long id;
    private final Long anioProgramaId;
    private String descripcion;
    private short orden;

    public PasoRequerido(Long id, Long anioProgramaId, String descripcion, short orden) {
        this.id = id;
        this.anioProgramaId = Objects.requireNonNull(anioProgramaId, "anioProgramaId");
        this.descripcion = Objects.requireNonNull(descripcion, "descripcion");
        this.orden = orden;
    }

    public static PasoRequerido nuevo(Long anioProgramaId, String descripcion, short orden) {
        return new PasoRequerido(null, anioProgramaId, descripcion, orden);
    }

    public void actualizarDatos(String descripcion, short orden) {
        this.descripcion = Objects.requireNonNull(descripcion, "descripcion");
        this.orden = orden;
    }

    public Long getId() {
        return id;
    }

    public Long getAnioProgramaId() {
        return anioProgramaId;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public short getOrden() {
        return orden;
    }
}
