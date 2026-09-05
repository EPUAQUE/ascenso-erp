package com.ais.ascensobackend.actividades.application.services.interfaces;

import com.ais.ascensobackend.actividades.application.dtos.NinoPadreResumen;
import java.util.List;

public interface NinoPadreService {

    List<NinoPadreResumen> listar(Long destacamentoId, Long ninoId);

    NinoPadreResumen vincular(Long destacamentoId, Long ninoId, Long usuarioId);

    void desvincular(Long destacamentoId, Long ninoId, Long usuarioId);
}
