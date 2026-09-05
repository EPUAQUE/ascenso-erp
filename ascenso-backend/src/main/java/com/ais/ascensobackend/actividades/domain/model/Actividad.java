package com.ais.ascensobackend.actividades.domain.model;

import com.ais.ascensobackend.actividades.domain.exception.ActividadFechasInvalidasException;
import java.time.Instant;
import java.util.Objects;

/**
 * Una actividad concreta de un destacamento (ej. una acampada, una reunión
 * especial). A diferencia de {@code Anuncio}, siempre pertenece a un
 * destacamento — nunca es global.
 */
public class Actividad {

    private final Long id;
    private final Long destacamentoId;
    private String titulo;
    private String descripcion;
    private Instant fechaInicio;
    private Instant fechaFin;
    private final Long creadoPor;

    public Actividad(
            Long id, Long destacamentoId, String titulo, String descripcion, Instant fechaInicio, Instant fechaFin,
            Long creadoPor) {
        this.id = id;
        this.destacamentoId = Objects.requireNonNull(destacamentoId, "destacamentoId");
        this.titulo = Objects.requireNonNull(titulo, "titulo");
        this.descripcion = descripcion;
        this.fechaInicio = Objects.requireNonNull(fechaInicio, "fechaInicio");
        this.fechaFin = requerirFechaFinValida(fechaInicio, fechaFin);
        this.creadoPor = Objects.requireNonNull(creadoPor, "creadoPor");
    }

    public static Actividad nueva(
            Long destacamentoId, String titulo, String descripcion, Instant fechaInicio, Instant fechaFin,
            Long creadoPor) {
        return new Actividad(null, destacamentoId, titulo, descripcion, fechaInicio, fechaFin, creadoPor);
    }

    public void actualizarDatos(String titulo, String descripcion, Instant fechaInicio, Instant fechaFin) {
        this.titulo = Objects.requireNonNull(titulo, "titulo");
        this.descripcion = descripcion;
        this.fechaInicio = Objects.requireNonNull(fechaInicio, "fechaInicio");
        this.fechaFin = requerirFechaFinValida(fechaInicio, fechaFin);
    }

    private static Instant requerirFechaFinValida(Instant fechaInicio, Instant fechaFin) {
        if (fechaFin != null && !fechaFin.isAfter(fechaInicio)) {
            throw new ActividadFechasInvalidasException(fechaInicio, fechaFin);
        }
        return fechaFin;
    }

    public Long getId() {
        return id;
    }

    public Long getDestacamentoId() {
        return destacamentoId;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public Instant getFechaInicio() {
        return fechaInicio;
    }

    public Instant getFechaFin() {
        return fechaFin;
    }

    public Long getCreadoPor() {
        return creadoPor;
    }
}
