package com.ais.ascensobackend.shared.infrastructure.alertas;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Alertas por correo (eventos de seguridad de alta severidad). Nunca propaga una
 * excepción hacia quien la llama — el envío de una alerta no debe romper el flujo
 * (login, refresh) que la disparó. Sin {@code ALERT_EMAIL_TO} configurado, cae a
 * solo-log.
 */
@Service
public class AlertaEmailService {

    private static final Logger log = LoggerFactory.getLogger(AlertaEmailService.class);

    private final JavaMailSender mailSender;
    private final String from;
    private final String to;

    public AlertaEmailService(
            JavaMailSender mailSender,
            @Value("${app.alertas.email.from:}") String from,
            @Value("${app.alertas.email.to:}") String to) {
        this.mailSender = mailSender;
        this.from = from;
        this.to = to;
    }

    public void enviar(String asunto, String cuerpo) {
        if (to == null || to.isBlank()) {
            log.warn("SMTP/destinatario no configurado (app.alertas.email.to vacío) — ALERTA solo-log: {} — {}",
                    asunto, cuerpo);
            return;
        }
        try {
            SimpleMailMessage mensaje = new SimpleMailMessage();
            if (from != null && !from.isBlank()) {
                mensaje.setFrom(from);
            }
            mensaje.setTo(to);
            mensaje.setSubject("[ascenso-backend] " + asunto);
            mensaje.setText(cuerpo);
            mailSender.send(mensaje);
        } catch (MailException e) {
            log.warn("No se pudo enviar la alerta por correo (revisar ALERT_SMTP_*) — ALERTA original: {} — {}",
                    asunto, cuerpo, e);
        }
    }
}
