package com.ais.ascensobackend.catalogo.application.services.interfaces;

import com.ais.ascensobackend.catalogo.application.dtos.LiderazgoResumen;
import java.util.List;

public interface LiderazgoService {

    List<LiderazgoResumen> listar(Long grupoId);

    LiderazgoResumen obtener(Long id);

    LiderazgoResumen crear(Long grupoId, String nombre, String categoria);

    LiderazgoResumen actualizar(Long id, String nombre, String categoria);

    void activar(Long id);

    void desactivar(Long id);
}
