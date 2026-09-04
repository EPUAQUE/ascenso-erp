package com.ais.ascensobackend.seguridad.application.services.impl;

import com.ais.ascensobackend.seguridad.application.dtos.RolResumen;
import com.ais.ascensobackend.seguridad.application.services.interfaces.AutorizacionDestacamentoService;
import com.ais.ascensobackend.seguridad.application.services.interfaces.RolService;
import com.ais.ascensobackend.seguridad.domain.model.Rol;
import com.ais.ascensobackend.seguridad.domain.repository.RolRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class RolServiceImpl implements RolService {

    private final RolRepository rolRepository;
    private final AutorizacionDestacamentoService autorizacionDestacamentoService;

    public RolServiceImpl(RolRepository rolRepository, AutorizacionDestacamentoService autorizacionDestacamentoService) {
        this.rolRepository = rolRepository;
        this.autorizacionDestacamentoService = autorizacionDestacamentoService;
    }

    /**
     * Un solicitante de alcance no global no ve roles de alcance global en este
     * selector — evita que pueda asignarle a otro usuario un rol con más alcance
     * del que el propio solicitante tiene.
     */
    @Override
    public List<RolResumen> listar() {
        boolean alcanceGlobal = autorizacionDestacamentoService.destacamentoIdsPermitidos().isEmpty();
        return rolRepository.findAll().stream()
                .filter(rol -> alcanceGlobal || !rol.isAlcanceGlobal())
                .map(this::toResumen)
                .toList();
    }

    private RolResumen toResumen(Rol rol) {
        return new RolResumen(rol.getId(), rol.getNombre(), rol.isAlcanceGlobal());
    }
}
