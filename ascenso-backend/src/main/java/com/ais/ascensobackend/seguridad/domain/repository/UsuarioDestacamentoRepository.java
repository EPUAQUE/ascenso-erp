package com.ais.ascensobackend.seguridad.domain.repository;

import com.ais.ascensobackend.seguridad.domain.model.UsuarioDestacamento;
import java.util.Collection;
import java.util.List;

public interface UsuarioDestacamentoRepository {

    UsuarioDestacamento save(UsuarioDestacamento usuarioDestacamento);

    List<UsuarioDestacamento> findByUsuarioId(Long usuarioId);

    /** Usado por Seguridad para filtrar el listado de usuarios por alcance del solicitante. */
    List<Long> listarUsuarioIdsPorDestacamentos(Collection<Long> destacamentoIds);
}
