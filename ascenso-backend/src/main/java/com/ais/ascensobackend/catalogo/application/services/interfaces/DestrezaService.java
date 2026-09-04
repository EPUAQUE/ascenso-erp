package com.ais.ascensobackend.catalogo.application.services.interfaces;

import com.ais.ascensobackend.catalogo.application.dtos.DestrezaResumen;
import java.util.List;

public interface DestrezaService {

    List<DestrezaResumen> listar(Long grupoId);

    DestrezaResumen obtener(Long id);

    DestrezaResumen crear(Long grupoId, String nombre, String categoria);

    DestrezaResumen actualizar(Long id, String nombre, String categoria);

    void activar(Long id);

    void desactivar(Long id);
}
