package com.ais.ascensobackend.seguridad.application.services.interfaces;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

/**
 * Puerto de aplicación para exigir el alcance de destacamento del usuario
 * autenticado desde dentro de un servicio de aplicación, no solo desde el
 * interceptor HTTP.
 */
public interface AutorizacionDestacamentoService {

    /** Lanza {@link org.springframework.security.access.AccessDeniedException} si el destacamento está fuera de alcance. */
    void exigirAcceso(Long destacamentoId);

    /** Igual que {@link #exigirAcceso(Long)} pero para varios destacamentos. */
    void exigirAccesoATodos(Collection<Long> destacamentoIds);

    /** Para filtrar listados: no lanza, solo indica si el usuario puede acceder a ese destacamento. */
    boolean tieneAcceso(Long destacamentoId);

    /**
     * Para construir consultas paginadas y filtradas por destacamento a nivel de
     * base de datos. Vacío = alcance global, sin restricción. Presente = solo esos
     * destacamentos.
     */
    Optional<Set<Long>> destacamentoIdsPermitidos();
}
