package com.ais.ascensobackend.ninos.application.services.interfaces;

import com.ais.ascensobackend.ninos.application.dtos.NinoResumen;
import java.time.LocalDate;
import java.util.List;

public interface NinoService {

    List<NinoResumen> listar(Long destacamentoId);

    NinoResumen obtener(Long destacamentoId, Long id);

    NinoResumen crear(
            Long destacamentoId, String nombreCompleto, LocalDate fechaNacimiento, String fotoUrl,
            String encargadoNombre, String encargadoContacto, String contactoEmergencia, LocalDate fechaIngreso,
            Long grupoActualId, Long anioProgramaActualId);

    NinoResumen actualizar(
            Long destacamentoId, Long id, String nombreCompleto, LocalDate fechaNacimiento, String fotoUrl,
            String encargadoNombre, String encargadoContacto, String contactoEmergencia);

    NinoResumen promover(Long destacamentoId, Long id, Long grupoId, Long anioProgramaId);

    void activar(Long destacamentoId, Long id);

    void desactivar(Long destacamentoId, Long id);
}
