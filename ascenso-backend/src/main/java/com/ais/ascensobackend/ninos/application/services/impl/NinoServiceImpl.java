package com.ais.ascensobackend.ninos.application.services.impl;

import com.ais.ascensobackend.catalogo.domain.repository.AnioProgramaRepository;
import com.ais.ascensobackend.catalogo.domain.repository.GrupoRepository;
import com.ais.ascensobackend.destacamentos.domain.repository.DestacamentoRepository;
import com.ais.ascensobackend.ninos.application.dtos.NinoResumen;
import com.ais.ascensobackend.ninos.application.services.interfaces.NinoService;
import com.ais.ascensobackend.ninos.domain.model.Nino;
import com.ais.ascensobackend.ninos.domain.repository.NinoRepository;
import com.ais.ascensobackend.shared.exceptions.ResourceNotFoundException;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NinoServiceImpl implements NinoService {

    private final NinoRepository ninoRepository;
    private final DestacamentoRepository destacamentoRepository;
    private final GrupoRepository grupoRepository;
    private final AnioProgramaRepository anioProgramaRepository;

    public NinoServiceImpl(
            NinoRepository ninoRepository, DestacamentoRepository destacamentoRepository,
            GrupoRepository grupoRepository, AnioProgramaRepository anioProgramaRepository) {
        this.ninoRepository = ninoRepository;
        this.destacamentoRepository = destacamentoRepository;
        this.grupoRepository = grupoRepository;
        this.anioProgramaRepository = anioProgramaRepository;
    }

    @Override
    public List<NinoResumen> listar(Long destacamentoId) {
        return ninoRepository.findByDestacamentoId(destacamentoId).stream().map(this::toResumen).toList();
    }

    @Override
    public NinoResumen obtener(Long destacamentoId, Long id) {
        return toResumen(obtenerORequerido(destacamentoId, id));
    }

    @Override
    @Transactional
    public NinoResumen crear(
            Long destacamentoId, String nombreCompleto, LocalDate fechaNacimiento, String fotoUrl,
            String encargadoNombre, String encargadoContacto, String contactoEmergencia, LocalDate fechaIngreso,
            Long grupoActualId, Long anioProgramaActualId) {
        requerirDestacamentoExistente(destacamentoId);
        requerirGrupoExistente(grupoActualId);
        requerirAnioProgramaExistente(anioProgramaActualId);
        Nino nino = Nino.nuevo(
                destacamentoId, nombreCompleto, fechaNacimiento, fotoUrl, encargadoNombre, encargadoContacto,
                contactoEmergencia, fechaIngreso, grupoActualId, anioProgramaActualId);
        return toResumen(ninoRepository.save(nino));
    }

    @Override
    @Transactional
    public NinoResumen actualizar(
            Long destacamentoId, Long id, String nombreCompleto, LocalDate fechaNacimiento, String fotoUrl,
            String encargadoNombre, String encargadoContacto, String contactoEmergencia) {
        Nino nino = obtenerORequerido(destacamentoId, id);
        nino.actualizarDatos(nombreCompleto, fechaNacimiento, fotoUrl, encargadoNombre, encargadoContacto, contactoEmergencia);
        return toResumen(ninoRepository.save(nino));
    }

    @Override
    @Transactional
    public NinoResumen promover(Long destacamentoId, Long id, Long grupoId, Long anioProgramaId) {
        Nino nino = obtenerORequerido(destacamentoId, id);
        requerirGrupoExistente(grupoId);
        requerirAnioProgramaExistente(anioProgramaId);
        nino.promover(grupoId, anioProgramaId);
        return toResumen(ninoRepository.save(nino));
    }

    @Override
    @Transactional
    public void activar(Long destacamentoId, Long id) {
        Nino nino = obtenerORequerido(destacamentoId, id);
        nino.activar();
        ninoRepository.save(nino);
    }

    @Override
    @Transactional
    public void desactivar(Long destacamentoId, Long id) {
        Nino nino = obtenerORequerido(destacamentoId, id);
        nino.desactivar();
        ninoRepository.save(nino);
    }

    private Nino obtenerORequerido(Long destacamentoId, Long id) {
        Nino nino = ninoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Niño no encontrado: " + id));
        if (!nino.getDestacamentoId().equals(destacamentoId)) {
            throw new ResourceNotFoundException("Niño no encontrado: " + id);
        }
        return nino;
    }

    private void requerirDestacamentoExistente(Long destacamentoId) {
        destacamentoRepository.findById(destacamentoId)
                .orElseThrow(() -> new ResourceNotFoundException("Destacamento no encontrado: " + destacamentoId));
    }

    private void requerirGrupoExistente(Long grupoId) {
        grupoRepository.findById(grupoId)
                .orElseThrow(() -> new ResourceNotFoundException("Grupo no encontrado: " + grupoId));
    }

    private void requerirAnioProgramaExistente(Long anioProgramaId) {
        anioProgramaRepository.findById(anioProgramaId)
                .orElseThrow(() -> new ResourceNotFoundException("Año de programa no encontrado: " + anioProgramaId));
    }

    private NinoResumen toResumen(Nino nino) {
        return new NinoResumen(
                nino.getId(), nino.getDestacamentoId(), nino.getNombreCompleto(), nino.getFechaNacimiento(),
                nino.getFotoUrl(), nino.getEncargadoNombre(), nino.getEncargadoContacto(),
                nino.getContactoEmergencia(), nino.getFechaIngreso(), nino.getGrupoActualId(),
                nino.getAnioProgramaActualId(), nino.isActivo());
    }
}
