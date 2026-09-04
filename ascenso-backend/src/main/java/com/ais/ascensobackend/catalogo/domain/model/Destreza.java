package com.ais.ascensobackend.catalogo.domain.model;

import java.util.Objects;

public class Destreza {

    private final Long id;
    private final Long grupoId;
    private String nombre;
    private String categoria;
    private boolean activo;

    public Destreza(Long id, Long grupoId, String nombre, String categoria, boolean activo) {
        this.id = id;
        this.grupoId = Objects.requireNonNull(grupoId, "grupoId");
        this.nombre = Objects.requireNonNull(nombre, "nombre");
        this.categoria = categoria;
        this.activo = activo;
    }

    public static Destreza nueva(Long grupoId, String nombre, String categoria) {
        return new Destreza(null, grupoId, nombre, categoria, true);
    }

    public void actualizarDatos(String nombre, String categoria) {
        this.nombre = Objects.requireNonNull(nombre, "nombre");
        this.categoria = categoria;
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

    public String getNombre() {
        return nombre;
    }

    public String getCategoria() {
        return categoria;
    }

    public boolean isActivo() {
        return activo;
    }
}
