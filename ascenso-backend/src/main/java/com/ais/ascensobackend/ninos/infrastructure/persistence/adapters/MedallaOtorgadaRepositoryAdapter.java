package com.ais.ascensobackend.ninos.infrastructure.persistence.adapters;

import com.ais.ascensobackend.ninos.domain.model.MedallaOtorgada;
import com.ais.ascensobackend.ninos.domain.repository.MedallaOtorgadaRepository;
import com.ais.ascensobackend.ninos.infrastructure.persistence.mappers.MedallaOtorgadaEntityMapper;
import com.ais.ascensobackend.ninos.infrastructure.persistence.repositories.MedallaOtorgadaJpaRepository;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class MedallaOtorgadaRepositoryAdapter implements MedallaOtorgadaRepository {

    private final MedallaOtorgadaJpaRepository jpaRepository;
    private final MedallaOtorgadaEntityMapper mapper;

    public MedallaOtorgadaRepositoryAdapter(
            MedallaOtorgadaJpaRepository jpaRepository, MedallaOtorgadaEntityMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public MedallaOtorgada save(MedallaOtorgada medallaOtorgada) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(medallaOtorgada)));
    }

    @Override
    public List<MedallaOtorgada> findByNinoId(Long ninoId) {
        return jpaRepository.findByNinoId(ninoId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public boolean existsByNinoIdAndAnioProgramaId(Long ninoId, Long anioProgramaId) {
        return jpaRepository.existsByNinoIdAndAnioProgramaId(ninoId, anioProgramaId);
    }
}
