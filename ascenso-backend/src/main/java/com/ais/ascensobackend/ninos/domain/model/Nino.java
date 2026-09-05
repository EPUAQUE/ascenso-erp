package com.ais.ascensobackend.ninos.domain.model;

import com.ais.ascensobackend.ninos.domain.exception.NinoFechaNacimientoInvalidaException;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Agregado raíz de un niño registrado en un {@code Destacamento}. Cada niño
 * pertenece a un destacamento fijo (no cambia de destacamento) y avanza de
 * grupo etario / año de programa mediante {@link #promover}.
 */
public class Nino {

    private final Long id;
    private final Long destacamentoId;
    private String nombreCompleto;
    private LocalDate fechaNacimiento;
    private String fotoUrl;
    private String encargadoNombre;
    private String encargadoContacto;
    private String contactoEmergencia;
    private LocalDate fechaIngreso;
    private Long grupoActualId;
    private Long anioProgramaActualId;
    private boolean activo;

    public Nino(
            Long id, Long destacamentoId, String nombreCompleto, LocalDate fechaNacimiento, String fotoUrl,
            String encargadoNombre, String encargadoContacto, String contactoEmergencia, LocalDate fechaIngreso,
            Long grupoActualId, Long anioProgramaActualId, boolean activo) {
        this.id = id;
        this.destacamentoId = Objects.requireNonNull(destacamentoId, "destacamentoId");
        this.nombreCompleto = Objects.requireNonNull(nombreCompleto, "nombreCompleto");
        this.fechaNacimiento = requerirNoFutura(fechaNacimiento);
        this.fotoUrl = fotoUrl;
        this.encargadoNombre = Objects.requireNonNull(encargadoNombre, "encargadoNombre");
        this.encargadoContacto = Objects.requireNonNull(encargadoContacto, "encargadoContacto");
        this.contactoEmergencia = contactoEmergencia;
        this.fechaIngreso = Objects.requireNonNull(fechaIngreso, "fechaIngreso");
        this.grupoActualId = Objects.requireNonNull(grupoActualId, "grupoActualId");
        this.anioProgramaActualId = Objects.requireNonNull(anioProgramaActualId, "anioProgramaActualId");
        this.activo = activo;
    }

    public static Nino nuevo(
            Long destacamentoId, String nombreCompleto, LocalDate fechaNacimiento, String fotoUrl,
            String encargadoNombre, String encargadoContacto, String contactoEmergencia, LocalDate fechaIngreso,
            Long grupoActualId, Long anioProgramaActualId) {
        return new Nino(
                null, destacamentoId, nombreCompleto, fechaNacimiento, fotoUrl, encargadoNombre, encargadoContacto,
                contactoEmergencia, fechaIngreso, grupoActualId, anioProgramaActualId, true);
    }

    public void actualizarDatos(
            String nombreCompleto, LocalDate fechaNacimiento, String fotoUrl, String encargadoNombre,
            String encargadoContacto, String contactoEmergencia) {
        this.nombreCompleto = Objects.requireNonNull(nombreCompleto, "nombreCompleto");
        this.fechaNacimiento = requerirNoFutura(fechaNacimiento);
        this.fotoUrl = fotoUrl;
        this.encargadoNombre = Objects.requireNonNull(encargadoNombre, "encargadoNombre");
        this.encargadoContacto = Objects.requireNonNull(encargadoContacto, "encargadoContacto");
        this.contactoEmergencia = contactoEmergencia;
    }

    public void promover(Long grupoId, Long anioProgramaId) {
        this.grupoActualId = Objects.requireNonNull(grupoId, "grupoId");
        this.anioProgramaActualId = Objects.requireNonNull(anioProgramaId, "anioProgramaId");
    }

    public void activar() {
        this.activo = true;
    }

    public void desactivar() {
        this.activo = false;
    }

    private static LocalDate requerirNoFutura(LocalDate fechaNacimiento) {
        Objects.requireNonNull(fechaNacimiento, "fechaNacimiento");
        if (fechaNacimiento.isAfter(LocalDate.now())) {
            throw new NinoFechaNacimientoInvalidaException(fechaNacimiento);
        }
        return fechaNacimiento;
    }

    public Long getId() {
        return id;
    }

    public Long getDestacamentoId() {
        return destacamentoId;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public String getFotoUrl() {
        return fotoUrl;
    }

    public String getEncargadoNombre() {
        return encargadoNombre;
    }

    public String getEncargadoContacto() {
        return encargadoContacto;
    }

    public String getContactoEmergencia() {
        return contactoEmergencia;
    }

    public LocalDate getFechaIngreso() {
        return fechaIngreso;
    }

    public Long getGrupoActualId() {
        return grupoActualId;
    }

    public Long getAnioProgramaActualId() {
        return anioProgramaActualId;
    }

    public boolean isActivo() {
        return activo;
    }
}
