package com.ais.ascensobackend.seguridad.infrastructure.security;

import com.ais.ascensobackend.seguridad.domain.service.SecurityAuditPublisher;
import com.ais.ascensobackend.seguridad.domain.service.TipoEventoAuditoria;
import com.ais.ascensobackend.shared.infrastructure.alertas.AlertaEmailService;
import com.ais.ascensobackend.shared.infrastructure.web.CorrelationIdFilter;
import io.micrometer.core.instrument.MeterRegistry;
import java.util.EnumSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

/**
 * Implementación de referencia: escribe al logger {@code SECURITY_AUDIT} y dispara
 * una alerta por correo para los dos tipos de severidad alta. A diferencia de
 * market-backend, esta fase no tiene un módulo de auditoría propio (tabla
 * {@code audit_event}) — se deja como punto de extensión documentado en
 * seguridad-desarrolladores.md si se decide agregarlo más adelante. Nunca registra
 * contraseñas, hashes ni tokens completos — solo el detalle ya sanitizado que
 * recibe.
 */
@Component
public class SecurityAuditPublisherImpl implements SecurityAuditPublisher {

    private static final Logger SECURITY_AUDIT = LoggerFactory.getLogger("SECURITY_AUDIT");
    private static final Set<TipoEventoAuditoria> TIPOS_QUE_ALERTAN =
            EnumSet.of(TipoEventoAuditoria.REFRESH_REUTILIZADO, TipoEventoAuditoria.RATE_LIMIT_ALCANZADO);

    private final MeterRegistry meterRegistry;
    private final AlertaEmailService alertaEmailService;

    public SecurityAuditPublisherImpl(MeterRegistry meterRegistry, AlertaEmailService alertaEmailService) {
        this.meterRegistry = meterRegistry;
        this.alertaEmailService = alertaEmailService;
    }

    @Override
    public void publicar(TipoEventoAuditoria tipo, String correlationId, String detalleSanitizado) {
        String correlationIdReal = correlationIdDeMdcOFallback(correlationId);
        SECURITY_AUDIT.info("evento={} correlationId={} detalle={}", tipo, correlationIdReal, detalleSanitizado);
        meterRegistry.counter("ascenso.security.evento", "tipo", tipo.name()).increment();

        if (TIPOS_QUE_ALERTAN.contains(tipo)) {
            alertaEmailService.enviar(
                    "Alerta de seguridad: " + tipo,
                    "Tipo: " + tipo + "\nCorrelationId: " + correlationIdReal + "\nDetalle: " + detalleSanitizado);
        }
    }

    private String correlationIdDeMdcOFallback(String correlationIdPasado) {
        String deMdc = MDC.get(CorrelationIdFilter.MDC_KEY);
        return (deMdc != null && !deMdc.isBlank()) ? deMdc : correlationIdPasado;
    }
}
