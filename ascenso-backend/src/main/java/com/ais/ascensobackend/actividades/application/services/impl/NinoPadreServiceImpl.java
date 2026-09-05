package com.ais.ascensobackend.actividades.application.services.impl;

import com.ais.ascensobackend.actividades.application.dtos.NinoPadreResumen;
import com.ais.ascensobackend.actividades.application.services.interfaces.NinoPadreService;
import com.ais.ascensobackend.actividades.domain.exception.NinoPadreDuplicadoException;
import com.ais.ascensobackend.actividades.domain.model.NinoPadre;
import com.ais.ascensobackend.actividades.domain.repository.NinoPadreRepository;
import com.ais.ascensobackend.ninos.domain.model.Nino;
import com.ais.ascensobackend.ninos.domain.repository.NinoRepository;
import com.ais.ascensobackend.seguridad.domain.repository.UsuarioRepository;
import com.ais.ascensobackend.shared.exceptions.ResourceNotFoundException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NinoPadreServiceImpl implements NinoPadreService {

    private final NinoPadreRepository ninoPadreRepository;
    private final NinoRepository ninoRepository;
    private final UsuarioRepository usuarioRepository;

    public NinoPadreServiceImpl(
            NinoPadreRepository ninoPadreRepository, NinoRepository ninoRepository, UsuarioRepository usuarioRepository) {
        this.ninoPadreRepository = ninoPadreRepository;
        this.ninoRepository = ninoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public List<NinoPadreResumen> listar(Long destacamentoId, Long ninoId) {
        requerirNinoDelDestacamento(destacamentoId, ninoId);
        return ninoPadreRepository.findByNinoId(ninoId).stream().map(this::toResumen).toList();
    }

    @Override
    @Transactional
    public NinoPadreResumen vincular(Long destacamentoId, Long ninoId, Long usuarioId) {
        requerirNinoDelDestacamento(destacamentoId, ninoId);
        usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + usuarioId));
        if (ninoPadreRepository.existsByNinoIdAndUsuarioId(ninoId, usuarioId)) {
            throw new NinoPadreDuplicadoException(ninoId, usuarioId);
        }
        return toResumen(ninoPadreRepository.save(NinoPadre.nuevo(ninoId, usuarioId)));
    }

    @Override
    @Transactional
    public void desvincular(Long destacamentoId, Long ninoId, Long usuarioId) {
        requerirNinoDelDestacamento(destacamentoId, ninoId);
        ninoPadreRepository.deleteByNinoIdAndUsuarioId(ninoId, usuarioId);
    }

    private void requerirNinoDelDestacamento(Long destacamentoId, Long ninoId) {
        Nino nino = ninoRepository.findById(ninoId)
                .orElseThrow(() -> new ResourceNotFoundException("Niño no encontrado: " + ninoId));
        if (!nino.getDestacamentoId().equals(destacamentoId)) {
            throw new ResourceNotFoundException("Niño no encontrado: " + ninoId);
        }
    }

    private NinoPadreResumen toResumen(NinoPadre ninoPadre) {
        return new NinoPadreResumen(ninoPadre.getNinoId(), ninoPadre.getUsuarioId());
    }
}
