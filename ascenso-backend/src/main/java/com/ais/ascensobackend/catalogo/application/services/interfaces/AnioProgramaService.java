package com.ais.ascensobackend.catalogo.application.services.interfaces;

import com.ais.ascensobackend.catalogo.application.dtos.AnioProgramaResumen;
import com.ais.ascensobackend.catalogo.domain.model.Medalla;
import java.util.List;

public interface AnioProgramaService {

    List<AnioProgramaResumen> listar(Long grupoId);

    AnioProgramaResumen obtener(Long id);

    AnioProgramaResumen crear(
            Long grupoId, short numero, Medalla medalla, short minimoLibros, short minimoDestrezas,
            short minimoLiderazgo, boolean esAnioGracia);

    AnioProgramaResumen actualizar(
            Long id, short numero, Medalla medalla, short minimoLibros, short minimoDestrezas,
            short minimoLiderazgo, boolean esAnioGracia);
}
