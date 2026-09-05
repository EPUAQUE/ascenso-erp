package com.ais.ascensobackend.ninos.application.services.interfaces;

import com.ais.ascensobackend.ninos.application.dtos.TrimestreResumen;
import java.time.LocalDate;
import java.util.List;

public interface TrimestreService {

    List<TrimestreResumen> listar();

    TrimestreResumen obtener(Long id);

    TrimestreResumen crear(short anioCalendario, short numero, LocalDate fechaInicio, LocalDate fechaFin);

    TrimestreResumen actualizar(Long id, short anioCalendario, short numero, LocalDate fechaInicio, LocalDate fechaFin);
}
