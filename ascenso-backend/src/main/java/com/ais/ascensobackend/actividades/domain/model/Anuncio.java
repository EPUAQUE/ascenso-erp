package com.ais.ascensobackend.actividades.domain.model;

import com.ais.ascensobackend.actividades.domain.exception.AnuncioFechasInvalidasException;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Anuncio global, visible para todos los destacamentos (a diferencia de
 * {@code Actividad}) — pensado para el portal de padres.
 */
public class Anuncio {

    private final Long id;
    private String titulo;
    private String descripcion;
    private String imagenUrl;
    private String enlaceUrl;
    private LocalDate fechaInicioVisible;
    private LocalDate fechaFinVisible;
    private boolean activo;
    private final Long creadoPor;

    public Anuncio(
            Long id, String titulo, String descripcion, String imagenUrl, String enlaceUrl,
            LocalDate fechaInicioVisible, LocalDate fechaFinVisible, boolean activo, Long creadoPor) {
        this.id = id;
        this.titulo = Objects.requireNonNull(titulo, "titulo");
        this.descripcion = descripcion;
        this.imagenUrl = imagenUrl;
        this.enlaceUrl = enlaceUrl;
        this.fechaInicioVisible = Objects.requireNonNull(fechaInicioVisible, "fechaInicioVisible");
        this.fechaFinVisible = requerirFechaFinValida(fechaInicioVisible, fechaFinVisible);
        this.activo = activo;
        this.creadoPor = Objects.requireNonNull(creadoPor, "creadoPor");
    }

    public static Anuncio nuevo(
            String titulo, String descripcion, String imagenUrl, String enlaceUrl, LocalDate fechaInicioVisible,
            LocalDate fechaFinVisible, Long creadoPor) {
        return new Anuncio(
                null, titulo, descripcion, imagenUrl, enlaceUrl, fechaInicioVisible, fechaFinVisible, true, creadoPor);
    }

    public void actualizarDatos(
            String titulo, String descripcion, String imagenUrl, String enlaceUrl, LocalDate fechaInicioVisible,
            LocalDate fechaFinVisible) {
        this.titulo = Objects.requireNonNull(titulo, "titulo");
        this.descripcion = descripcion;
        this.imagenUrl = imagenUrl;
        this.enlaceUrl = enlaceUrl;
        this.fechaInicioVisible = Objects.requireNonNull(fechaInicioVisible, "fechaInicioVisible");
        this.fechaFinVisible = requerirFechaFinValida(fechaInicioVisible, fechaFinVisible);
    }

    public void activar() {
        this.activo = true;
    }

    public void desactivar() {
        this.activo = false;
    }

    private static LocalDate requerirFechaFinValida(LocalDate fechaInicioVisible, LocalDate fechaFinVisible) {
        if (fechaFinVisible != null && !fechaFinVisible.isAfter(fechaInicioVisible)) {
            throw new AnuncioFechasInvalidasException(fechaInicioVisible, fechaFinVisible);
        }
        return fechaFinVisible;
    }

    public Long getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public String getEnlaceUrl() {
        return enlaceUrl;
    }

    public LocalDate getFechaInicioVisible() {
        return fechaInicioVisible;
    }

    public LocalDate getFechaFinVisible() {
        return fechaFinVisible;
    }

    public boolean isActivo() {
        return activo;
    }

    public Long getCreadoPor() {
        return creadoPor;
    }
}
