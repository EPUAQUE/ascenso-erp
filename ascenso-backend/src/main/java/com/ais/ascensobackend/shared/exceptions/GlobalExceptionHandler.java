package com.ais.ascensobackend.shared.exceptions;

import com.ais.ascensobackend.seguridad.domain.exception.RateLimitExcedidoException;
import com.ais.ascensobackend.seguridad.domain.service.SecurityAuditPublisher;
import com.ais.ascensobackend.seguridad.domain.service.TipoEventoAuditoria;
import com.ais.ascensobackend.shared.infrastructure.web.CorrelationIdFilter;
import com.ais.ascensobackend.shared.responses.ApiErrorResponse;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private static final String METRICA_CONFLICTO = "ascenso.business_exception";

    private final MeterRegistry meterRegistry;
    private final SecurityAuditPublisher auditPublisher;

    public GlobalExceptionHandler(MeterRegistry meterRegistry, SecurityAuditPublisher auditPublisher) {
        this.meterRegistry = meterRegistry;
        this.auditPublisher = auditPublisher;
    }

    @ExceptionHandler(RateLimitExcedidoException.class)
    public ResponseEntity<ApiErrorResponse> handleRateLimit(RateLimitExcedidoException ex, HttpServletRequest request) {
        String correlationId = correlationId(request);
        auditPublisher.publicar(
                TipoEventoAuditoria.RATE_LIMIT_ALCANZADO, correlationId, "path=" + request.getRequestURI());
        ApiErrorResponse body = errorBody(ex.httpStatus(), ex.errorCode(), ex.getMessage(), request);
        return ResponseEntity.status(ex.httpStatus())
                .header("Retry-After", String.valueOf(ex.getRetryAfter().toSeconds()))
                .body(body);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiErrorResponse> handleBusiness(BusinessException ex, HttpServletRequest request) {
        meterRegistry.counter(METRICA_CONFLICTO, "codigo", ex.errorCode()).increment();
        ApiErrorResponse body = errorBody(ex.httpStatus(), ex.errorCode(), ex.getMessage(), request);
        return ResponseEntity.status(ex.httpStatus()).body(body);
    }

    /**
     * Punto de traducción para conflictos de concurrencia a nivel de
     * infraestructura, no de negocio — un deadlock detectado o una espera de lock
     * que agota el tiempo, ambos traducidos por Spring a subclases de
     * {@code ConcurrencyFailureException}.
     */
    @ExceptionHandler(ConcurrencyFailureException.class)
    public ResponseEntity<ApiErrorResponse> handleConcurrencyConflict(
            ConcurrencyFailureException ex, HttpServletRequest request) {
        meterRegistry.counter(METRICA_CONFLICTO, "codigo", "CONFLICTO_CONCURRENCIA").increment();
        ApiErrorResponse body = errorBody(
                HttpStatus.CONFLICT, "CONFLICTO_CONCURRENCIA",
                "Otra operación está modificando el mismo recurso en este momento. Intente de nuevo.", request);
        log.warn(
                "Conflicto de concurrencia en {} [correlationId={}]", request.getRequestURI(), body.correlationId(),
                ex);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorResponse> handleAccessDenied(HttpServletRequest request) {
        meterRegistry.counter(METRICA_CONFLICTO, "codigo", "ACCESS_DENIED").increment();
        ApiErrorResponse body = errorBody(
                HttpStatus.FORBIDDEN, "ACCESS_DENIED", "No tiene permiso para esta operación.", request);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<ApiErrorResponse.FieldError> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> new ApiErrorResponse.FieldError(fe.getField(), fe.getDefaultMessage()))
                .toList();
        ApiErrorResponse body = ApiErrorResponse.withFieldErrors(
                HttpStatus.BAD_REQUEST.value(), "VALIDATION_ERROR", "Datos de entrada inválidos.",
                request.getRequestURI(), correlationId(request), fieldErrors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNoResourceFound(HttpServletRequest request) {
        ApiErrorResponse body = errorBody(HttpStatus.NOT_FOUND, "NOT_FOUND", "Recurso no encontrado.", request);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiErrorResponse> handleMaxUploadSizeExceeded(HttpServletRequest request) {
        ApiErrorResponse body = errorBody(
                HttpStatus.BAD_REQUEST, "ARCHIVO_INVALIDO", "El archivo excede el tamaño máximo permitido.",
                request);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleMalformedJson(HttpServletRequest request) {
        ApiErrorResponse body = errorBody(
                HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "Cuerpo de la solicitud ilegible o mal formado.", request);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnexpected(Exception ex, HttpServletRequest request) {
        ApiErrorResponse body = errorBody(
                HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", "Ocurrió un error inesperado.", request);
        log.error("Error no controlado en {} [correlationId={}]", request.getRequestURI(), body.correlationId(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    private ApiErrorResponse errorBody(HttpStatus status, String errorCode, String message, HttpServletRequest request) {
        return ApiErrorResponse.of(status.value(), errorCode, message, request.getRequestURI(), correlationId(request));
    }

    private String correlationId(HttpServletRequest request) {
        String deMdc = MDC.get(CorrelationIdFilter.MDC_KEY);
        if (deMdc != null && !deMdc.isBlank()) {
            return deMdc;
        }
        String header = request.getHeader(CorrelationIdFilter.HEADER);
        return header != null && !header.isBlank() ? header : UUID.randomUUID().toString();
    }
}
