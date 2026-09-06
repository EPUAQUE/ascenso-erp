package com.ais.ascensobackend.destacamentos.application.services.interfaces;

import com.ais.ascensobackend.destacamentos.application.dtos.DestacamentoResumen;
import com.ais.ascensobackend.destacamentos.domain.model.ModoCorteAnio;
import java.util.Collection;
import java.util.List;

public interface DestacamentoService {

    DestacamentoResumen crear(
            String numeroUnico, String nombre, String iglesiaNombre, String direccion, ModoCorteAnio modoCorteAnio);

    DestacamentoResumen actualizar(
            Long id, String nombre, String iglesiaNombre, String direccion, ModoCorteAnio modoCorteAnio);

    DestacamentoResumen obtener(Long id);

    void activar(Long id);

    void desactivar(Long id);

    List<DestacamentoResumen> listar();

    /** Resuelve solo los destacamentos cuyo id está en {@code ids} (ignora los que no existan). */
    List<DestacamentoResumen> listarPorIds(Collection<Long> ids);
}
