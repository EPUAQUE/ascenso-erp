package com.ais.ascensobackend.ninos.domain.model;

import com.ais.ascensobackend.ninos.domain.exception.TrimestreFechasInvalidasException;
import com.ais.ascensobackend.ninos.domain.exception.TrimestreNumeroInvalidoException;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Un trimestre del calendario del programa (1 a 4 por año calendario), usado
 * para agrupar {@code asistencia}. Catálogo global compartido por todos los
 * destacamentos.
 */
public class Trimestre {

    private final Long id;
    private short anioCalendario;
    private short numero;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;

    public Trimestre(Long id, short anioCalendario, short numero, LocalDate fechaInicio, LocalDate fechaFin) {
        this.id = id;
        this.anioCalendario = anioCalendario;
        this.numero = requerirNumeroValido(numero);
        this.fechaInicio = Objects.requireNonNull(fechaInicio, "fechaInicio");
        this.fechaFin = requerirFechaFinValida(fechaInicio, fechaFin);
    }

    public static Trimestre nuevo(short anioCalendario, short numero, LocalDate fechaInicio, LocalDate fechaFin) {
        return new Trimestre(null, anioCalendario, numero, fechaInicio, fechaFin);
    }

    public void actualizarDatos(short anioCalendario, short numero, LocalDate fechaInicio, LocalDate fechaFin) {
        this.anioCalendario = anioCalendario;
        this.numero = requerirNumeroValido(numero);
        this.fechaInicio = Objects.requireNonNull(fechaInicio, "fechaInicio");
        this.fechaFin = requerirFechaFinValida(fechaInicio, fechaFin);
    }

    private static short requerirNumeroValido(short numero) {
        if (numero < 1 || numero > 4) {
            throw new TrimestreNumeroInvalidoException(numero);
        }
        return numero;
    }

    private static LocalDate requerirFechaFinValida(LocalDate fechaInicio, LocalDate fechaFin) {
        Objects.requireNonNull(fechaFin, "fechaFin");
        if (!fechaFin.isAfter(fechaInicio)) {
            throw new TrimestreFechasInvalidasException(fechaInicio, fechaFin);
        }
        return fechaFin;
    }

    public Long getId() {
        return id;
    }

    public short getAnioCalendario() {
        return anioCalendario;
    }

    public short getNumero() {
        return numero;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }
}
