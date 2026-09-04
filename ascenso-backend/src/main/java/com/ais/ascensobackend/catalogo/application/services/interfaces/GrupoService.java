package com.ais.ascensobackend.catalogo.application.services.interfaces;

import com.ais.ascensobackend.catalogo.application.dtos.GrupoResumen;
import java.util.List;

public interface GrupoService {

    List<GrupoResumen> listar();

    GrupoResumen obtener(Long id);

    GrupoResumen actualizar(Long id, String nombre, short edadMin, short edadMax, short orden, String descripcion);
}
