package com.ais.ascensobackend.catalogo.domain.model;

import java.util.Objects;

public class LibroBiblico {

    private final Long id;
    private final Long grupoId;
    private String titulo;
    private Short ordenSugerido;
    private boolean activo;

    public LibroBiblico(Long id, Long grupoId, String titulo, Short ordenSugerido, boolean activo) {
        this.id = id;
        this.grupoId = Objects.requireNonNull(grupoId, "grupoId");
        this.titulo = Objects.requireNonNull(titulo, "titulo");
        this.ordenSugerido = ordenSugerido;
        this.activo = activo;
    }

    public static LibroBiblico nuevo(Long grupoId, String titulo, Short ordenSugerido) {
        return new LibroBiblico(null, grupoId, titulo, ordenSugerido, true);
    }

    public void actualizarDatos(String titulo, Short ordenSugerido) {
        this.titulo = Objects.requireNonNull(titulo, "titulo");
        this.ordenSugerido = ordenSugerido;
    }

    public void activar() {
        this.activo = true;
    }

    public void desactivar() {
        this.activo = false;
    }

    public Long getId() {
        return id;
    }

    public Long getGrupoId() {
        return grupoId;
    }

    public String getTitulo() {
        return titulo;
    }

    public Short getOrdenSugerido() {
        return ordenSugerido;
    }

    public boolean isActivo() {
        return activo;
    }
}
