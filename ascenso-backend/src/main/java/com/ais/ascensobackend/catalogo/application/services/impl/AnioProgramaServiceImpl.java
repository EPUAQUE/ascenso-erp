package com.ais.ascensobackend.catalogo.application.services.impl;

import com.ais.ascensobackend.catalogo.application.dtos.AnioProgramaResumen;
import com.ais.ascensobackend.catalogo.application.services.interfaces.AnioProgramaService;
import com.ais.ascensobackend.catalogo.domain.model.AnioPrograma;
import com.ais.ascensobackend.catalogo.domain.model.Medalla;
import com.ais.ascensobackend.catalogo.domain.repository.AnioProgramaRepository;
import com.ais.ascensobackend.catalogo.domain.repository.GrupoRepository;
import com.ais.ascensobackend.shared.exceptions.ResourceNotFoundException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AnioProgramaServiceImpl implements AnioProgramaService {

    private final AnioProgramaRepository anioProgramaRepository;
    private final GrupoRepository grupoRepository;

    public AnioProgramaServiceImpl(AnioProgramaRepository anioProgramaRepository, GrupoRepository grupoRepository) {
        this.anioProgramaRepository = anioProgramaRepository;
        this.grupoRepository = grupoRepository;
    }

    @Override
    public List<AnioProgramaResumen> listar(Long grupoId) {
        List<AnioPrograma> anios = grupoId == null
                ? anioProgramaRepository.findAll() : anioProgramaRepository.findByGrupoId(grupoId);
        return anios.stream().map(this::toResumen).toList();
    }

    @Override
    public AnioProgramaResumen obtener(Long id) {
        return toResumen(obtenerORequerido(id));
    }

    @Override
    @Transactional
    public AnioProgramaResumen crear(
            Long grupoId, short numero, Medalla medalla, short minimoLibros, short minimoDestrezas,
            short minimoLiderazgo, boolean esAnioGracia) {
        grupoRepository.findById(grupoId)
                .orElseThrow(() -> new ResourceNotFoundException("Grupo no encontrado: " + grupoId));
        AnioPrograma anio = AnioPrograma.nuevo(
                grupoId, numero, medalla, minimoLibros, minimoDestrezas, minimoLiderazgo, esAnioGracia);
        return toResumen(anioProgramaRepository.save(anio));
    }

    @Override
    @Transactional
    public AnioProgramaResumen actualizar(
            Long id, short numero, Medalla medalla, short minimoLibros, short minimoDestrezas,
            short minimoLiderazgo, boolean esAnioGracia) {
        AnioPrograma anio = obtenerORequerido(id);
        anio.actualizarDatos(numero, medalla, minimoLibros, minimoDestrezas, minimoLiderazgo, esAnioGracia);
        return toResumen(anioProgramaRepository.save(anio));
    }

    private AnioPrograma obtenerORequerido(Long id) {
        return anioProgramaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Año de programa no encontrado: " + id));
    }

    private AnioProgramaResumen toResumen(AnioPrograma anio) {
        return new AnioProgramaResumen(
                anio.getId(), anio.getGrupoId(), anio.getNumero(), anio.getMedalla(), anio.getMinimoLibros(),
                anio.getMinimoDestrezas(), anio.getMinimoLiderazgo(), anio.isEsAnioGracia());
    }
}
