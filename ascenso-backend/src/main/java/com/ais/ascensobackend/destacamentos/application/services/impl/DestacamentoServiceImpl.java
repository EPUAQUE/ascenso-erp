package com.ais.ascensobackend.destacamentos.application.services.impl;

import com.ais.ascensobackend.destacamentos.application.dtos.DestacamentoResumen;
import com.ais.ascensobackend.destacamentos.application.services.interfaces.DestacamentoService;
import com.ais.ascensobackend.destacamentos.domain.exception.DestacamentoDuplicadoException;
import com.ais.ascensobackend.destacamentos.domain.model.Destacamento;
import com.ais.ascensobackend.destacamentos.domain.model.ModoCorteAnio;
import com.ais.ascensobackend.destacamentos.domain.repository.DestacamentoRepository;
import com.ais.ascensobackend.shared.exceptions.ResourceNotFoundException;
import java.util.Collection;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DestacamentoServiceImpl implements DestacamentoService {

    private final DestacamentoRepository destacamentoRepository;

    public DestacamentoServiceImpl(DestacamentoRepository destacamentoRepository) {
        this.destacamentoRepository = destacamentoRepository;
    }

    @Override
    @Transactional
    public DestacamentoResumen crear(
            String numeroUnico, String nombre, String iglesiaNombre, String direccion, ModoCorteAnio modoCorteAnio) {
        String numeroCanonico = numeroUnico == null ? null : numeroUnico.trim();
        if (destacamentoRepository.existsByNumeroUnico(numeroCanonico)) {
            throw new DestacamentoDuplicadoException(numeroCanonico);
        }
        Destacamento destacamento = Destacamento.nuevo(numeroCanonico, nombre, iglesiaNombre, direccion, modoCorteAnio);
        return toResumen(destacamentoRepository.save(destacamento));
    }

    @Override
    @Transactional
    public DestacamentoResumen actualizar(
            Long id, String nombre, String iglesiaNombre, String direccion, ModoCorteAnio modoCorteAnio) {
        Destacamento destacamento = obtenerORequerido(id);
        destacamento.actualizarDatos(nombre, iglesiaNombre, direccion, modoCorteAnio);
        return toResumen(destacamentoRepository.save(destacamento));
    }

    @Override
    public DestacamentoResumen obtener(Long id) {
        return toResumen(obtenerORequerido(id));
    }

    @Override
    @Transactional
    public void activar(Long id) {
        Destacamento destacamento = obtenerORequerido(id);
        destacamento.activar();
        destacamentoRepository.save(destacamento);
    }

    @Override
    @Transactional
    public void desactivar(Long id) {
        Destacamento destacamento = obtenerORequerido(id);
        destacamento.desactivar();
        destacamentoRepository.save(destacamento);
    }

    @Override
    public List<DestacamentoResumen> listar() {
        return destacamentoRepository.findAll().stream().map(this::toResumen).toList();
    }

    @Override
    public List<DestacamentoResumen> listarPorIds(Collection<Long> ids) {
        return destacamentoRepository.findAllById(ids).stream().map(this::toResumen).toList();
    }

    private Destacamento obtenerORequerido(Long id) {
        return destacamentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Destacamento no encontrado: " + id));
    }

    private DestacamentoResumen toResumen(Destacamento destacamento) {
        return new DestacamentoResumen(
                destacamento.getId(), destacamento.getNumeroUnico(), destacamento.getNombre(),
                destacamento.getIglesiaNombre(), destacamento.getDireccion(), destacamento.getModoCorteAnio(),
                destacamento.isActivo());
    }
}
