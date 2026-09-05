package com.ais.ascensobackend.ninos.application.services.impl;

import com.ais.ascensobackend.catalogo.domain.repository.AnioProgramaRepository;
import com.ais.ascensobackend.ninos.application.dtos.MedallaOtorgadaResumen;
import com.ais.ascensobackend.ninos.application.services.interfaces.MedallaOtorgadaService;
import com.ais.ascensobackend.ninos.domain.exception.MedallaOtorgadaDuplicadaException;
import com.ais.ascensobackend.ninos.domain.model.MedallaOtorgada;
import com.ais.ascensobackend.ninos.domain.model.Nino;
import com.ais.ascensobackend.ninos.domain.repository.MedallaOtorgadaRepository;
import com.ais.ascensobackend.ninos.domain.repository.NinoRepository;
import com.ais.ascensobackend.shared.exceptions.ResourceNotFoundException;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MedallaOtorgadaServiceImpl implements MedallaOtorgadaService {

    private final MedallaOtorgadaRepository medallaOtorgadaRepository;
    private final NinoRepository ninoRepository;
    private final AnioProgramaRepository anioProgramaRepository;

    public MedallaOtorgadaServiceImpl(
            MedallaOtorgadaRepository medallaOtorgadaRepository, NinoRepository ninoRepository,
            AnioProgramaRepository anioProgramaRepository) {
        this.medallaOtorgadaRepository = medallaOtorgadaRepository;
        this.ninoRepository = ninoRepository;
        this.anioProgramaRepository = anioProgramaRepository;
    }

    @Override
    public List<MedallaOtorgadaResumen> listar(Long destacamentoId, Long ninoId) {
        requerirNinoDelDestacamento(destacamentoId, ninoId);
        return medallaOtorgadaRepository.findByNinoId(ninoId).stream().map(this::toResumen).toList();
    }

    @Override
    @Transactional
    public MedallaOtorgadaResumen otorgar(Long destacamentoId, Long ninoId, Long anioProgramaId, LocalDate fechaOtorgada) {
        requerirNinoDelDestacamento(destacamentoId, ninoId);
        anioProgramaRepository.findById(anioProgramaId)
                .orElseThrow(() -> new ResourceNotFoundException("Año de programa no encontrado: " + anioProgramaId));
        if (medallaOtorgadaRepository.existsByNinoIdAndAnioProgramaId(ninoId, anioProgramaId)) {
            throw new MedallaOtorgadaDuplicadaException(ninoId, anioProgramaId);
        }
        MedallaOtorgada medalla = MedallaOtorgada.nueva(ninoId, anioProgramaId, fechaOtorgada);
        return toResumen(medallaOtorgadaRepository.save(medalla));
    }

    private void requerirNinoDelDestacamento(Long destacamentoId, Long ninoId) {
        Nino nino = ninoRepository.findById(ninoId)
                .orElseThrow(() -> new ResourceNotFoundException("Niño no encontrado: " + ninoId));
        if (!nino.getDestacamentoId().equals(destacamentoId)) {
            throw new ResourceNotFoundException("Niño no encontrado: " + ninoId);
        }
    }

    private MedallaOtorgadaResumen toResumen(MedallaOtorgada medalla) {
        return new MedallaOtorgadaResumen(
                medalla.getId(), medalla.getNinoId(), medalla.getAnioProgramaId(), medalla.getFechaOtorgada());
    }
}
