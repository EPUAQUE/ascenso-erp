package com.ais.ascensobackend.catalogo.domain.model;

import java.util.Objects;

/**
 * Agregado raíz de un grupo etario del programa (Pre-Navegantes, Navegantes,
 * Pioneros, Seguidores, Exploradores). Catálogo global, gestionado únicamente por
 * SUPERVISOR_GENERAL — mismos 5 grupos para todos los destacamentos.
 */
public class Grupo {

    private final Long id;
    private String nombre;
    private short edadMin;
    private short edadMax;
    private short orden;
    private String descripcion;

    public Grupo(Long id, String nombre, short edadMin, short edadMax, short orden, String descripcion) {
        this.id = id;
        this.nombre = Objects.requireNonNull(nombre, "nombre");
        this.edadMin = edadMin;
        this.edadMax = edadMax;
        this.orden = orden;
        this.descripcion = descripcion;
    }

    public static Grupo nuevo(String nombre, short edadMin, short edadMax, short orden, String descripcion) {
        return new Grupo(null, nombre, edadMin, edadMax, orden, descripcion);
    }

    public void actualizarDatos(String nombre, short edadMin, short edadMax, short orden, String descripcion) {
        this.nombre = Objects.requireNonNull(nombre, "nombre");
        this.edadMin = edadMin;
        this.edadMax = edadMax;
        this.orden = orden;
        this.descripcion = descripcion;
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public short getEdadMin() {
        return edadMin;
    }

    public short getEdadMax() {
        return edadMax;
    }

    public short getOrden() {
        return orden;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
