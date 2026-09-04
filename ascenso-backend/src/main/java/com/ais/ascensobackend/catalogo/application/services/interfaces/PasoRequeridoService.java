package com.ais.ascensobackend.catalogo.application.services.interfaces;

import com.ais.ascensobackend.catalogo.application.dtos.PasoRequeridoResumen;
import java.util.List;

public interface PasoRequeridoService {

    List<PasoRequeridoResumen> listar(Long anioProgramaId);

    PasoRequeridoResumen obtener(Long id);

    PasoRequeridoResumen crear(Long anioProgramaId, String descripcion, short orden);

    PasoRequeridoResumen actualizar(Long id, String descripcion, short orden);
}
