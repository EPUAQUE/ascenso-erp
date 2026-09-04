package com.ais.ascensobackend.catalogo.domain.model;

import java.util.Objects;

/**
 * Un año dentro del programa de un {@link Grupo} (ej. Navegantes año 1 = medalla
 * BRONCE) con sus mínimos de avance requeridos para completarlo.
 */
public class AnioPrograma {

    private final Long id;
    private final Long grupoId;
    private short numero;
    private Medalla medalla;
    private short minimoLibros;
    private short minimoDestrezas;
    private short minimoLiderazgo;
    private boolean esAnioGracia;

    public AnioPrograma(
            Long id, Long grupoId, short numero, Medalla medalla, short minimoLibros, short minimoDestrezas,
            short minimoLiderazgo, boolean esAnioGracia) {
        this.id = id;
        this.grupoId = Objects.requireNonNull(grupoId, "grupoId");
        this.numero = numero;
        this.medalla = medalla;
        this.minimoLibros = minimoLibros;
        this.minimoDestrezas = minimoDestrezas;
        this.minimoLiderazgo = minimoLiderazgo;
        this.esAnioGracia = esAnioGracia;
    }

    public static AnioPrograma nuevo(
            Long grupoId, short numero, Medalla medalla, short minimoLibros, short minimoDestrezas,
            short minimoLiderazgo, boolean esAnioGracia) {
        return new AnioPrograma(null, grupoId, numero, medalla, minimoLibros, minimoDestrezas, minimoLiderazgo, esAnioGracia);
    }

    public void actualizarDatos(
            short numero, Medalla medalla, short minimoLibros, short minimoDestrezas, short minimoLiderazgo,
            boolean esAnioGracia) {
        this.numero = numero;
        this.medalla = medalla;
        this.minimoLibros = minimoLibros;
        this.minimoDestrezas = minimoDestrezas;
        this.minimoLiderazgo = minimoLiderazgo;
        this.esAnioGracia = esAnioGracia;
    }

    public Long getId() {
        return id;
    }

    public Long getGrupoId() {
        return grupoId;
    }

    public short getNumero() {
        return numero;
    }

    public Medalla getMedalla() {
        return medalla;
    }

    public short getMinimoLibros() {
        return minimoLibros;
    }

    public short getMinimoDestrezas() {
        return minimoDestrezas;
    }

    public short getMinimoLiderazgo() {
        return minimoLiderazgo;
    }

    public boolean isEsAnioGracia() {
        return esAnioGracia;
    }
}
