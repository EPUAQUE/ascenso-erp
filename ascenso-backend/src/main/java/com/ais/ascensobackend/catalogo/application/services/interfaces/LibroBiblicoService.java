package com.ais.ascensobackend.catalogo.application.services.interfaces;

import com.ais.ascensobackend.catalogo.application.dtos.LibroBiblicoResumen;
import java.util.List;

public interface LibroBiblicoService {

    List<LibroBiblicoResumen> listar(Long grupoId);

    LibroBiblicoResumen obtener(Long id);

    LibroBiblicoResumen crear(Long grupoId, String titulo, Short ordenSugerido);

    LibroBiblicoResumen actualizar(Long id, String titulo, Short ordenSugerido);

    void activar(Long id);

    void desactivar(Long id);
}
