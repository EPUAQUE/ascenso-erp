package com.ais.ascensobackend.seguridad.application.services.interfaces;

import com.ais.ascensobackend.seguridad.application.dtos.RolResumen;
import java.util.List;

public interface RolService {

    List<RolResumen> listar();
}
