package com.ais.ascensobackend.seguridad.domain.service;

/**
 * Publica eventos de seguridad. La implementación de referencia escribe al logger
 * {@code SECURITY_AUDIT} y dispara una alerta por correo para los eventos de alta
 * severidad.
 */
public interface SecurityAuditPublisher {

    void publicar(TipoEventoAuditoria tipo, String correlationId, String detalleSanitizado);
}
