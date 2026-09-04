package com.ais.ascensobackend.destacamentos.domain.model;

import java.util.Objects;

/**
 * Agregado raíz de un destacamento (grupo local del programa, típicamente ligado a
 * una iglesia). {@code numeroUnico} es el identificador de negocio público y
 * estable — inmutable una vez creado el destacamento.
 */
public class Destacamento {

    private final Long id;
    private final String numeroUnico;
    private String nombre;
    private String iglesiaNombre;
    private String direccion;
    private ModoCorteAnio modoCorteAnio;
    private boolean activo;

    public Destacamento(
            Long id, String numeroUnico, String nombre, String iglesiaNombre, String direccion,
            ModoCorteAnio modoCorteAnio, boolean activo) {
        this.id = id;
        this.numeroUnico = Objects.requireNonNull(numeroUnico, "numeroUnico");
        this.nombre = Objects.requireNonNull(nombre, "nombre");
        this.iglesiaNombre = Objects.requireNonNull(iglesiaNombre, "iglesiaNombre");
        this.direccion = direccion;
        this.modoCorteAnio = Objects.requireNonNull(modoCorteAnio, "modoCorteAnio");
        this.activo = activo;
    }

    public static Destacamento nuevo(
            String numeroUnico, String nombre, String iglesiaNombre, String direccion, ModoCorteAnio modoCorteAnio) {
        return new Destacamento(null, numeroUnico, nombre, iglesiaNombre, direccion, modoCorteAnio, true);
    }

    public void actualizarDatos(String nombre, String iglesiaNombre, String direccion, ModoCorteAnio modoCorteAnio) {
        this.nombre = Objects.requireNonNull(nombre, "nombre");
        this.iglesiaNombre = Objects.requireNonNull(iglesiaNombre, "iglesiaNombre");
        this.direccion = direccion;
        this.modoCorteAnio = Objects.requireNonNull(modoCorteAnio, "modoCorteAnio");
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

    public String getNumeroUnico() {
        return numeroUnico;
    }

    public String getNombre() {
        return nombre;
    }

    public String getIglesiaNombre() {
        return iglesiaNombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public ModoCorteAnio getModoCorteAnio() {
        return modoCorteAnio;
    }

    public boolean isActivo() {
        return activo;
    }
}
