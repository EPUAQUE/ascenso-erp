package com.ais.ascensobackend.seguridad.domain.model;

/**
 * Asigna un usuario a un destacamento con un rol. Determina el alcance de datos
 * sobre el que el usuario puede operar (no solo qué puede hacer, ver {@link Rol}).
 * {@code destacamentoId} es un identificador plano: el módulo Destacamentos es
 * dueño de esa tabla y de su validación de existencia; Seguridad solo la referencia.
 */
public class UsuarioDestacamento {

    private final Long id;
    private final Long usuarioId;
    private final Long destacamentoId;
    private final Rol rol;

    public UsuarioDestacamento(Long id, Long usuarioId, Long destacamentoId, Rol rol) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.destacamentoId = destacamentoId;
        this.rol = rol;
    }

    public Long getId() {
        return id;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public Long getDestacamentoId() {
        return destacamentoId;
    }

    public Rol getRol() {
        return rol;
    }
}
