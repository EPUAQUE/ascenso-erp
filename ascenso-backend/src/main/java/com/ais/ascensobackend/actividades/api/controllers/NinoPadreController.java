package com.ais.ascensobackend.actividades.api.controllers;

import com.ais.ascensobackend.actividades.api.dtos.requests.VincularPadreRequest;
import com.ais.ascensobackend.actividades.api.dtos.responses.NinoPadreResponse;
import com.ais.ascensobackend.actividades.api.mappers.NinoPadreApiMapper;
import com.ais.ascensobackend.actividades.application.services.interfaces.NinoPadreService;
import com.ais.ascensobackend.seguridad.infrastructure.security.RequiresPermission;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Administra qué usuarios (rol PADRE) pueden ver el progreso de un niño —
 * reusa NINOS_VER/NINOS_EDITAR (mismo alcance operativo que el resto de la
 * ficha del niño), sin permiso propio.
 */
@RestController
@RequestMapping("/api/v1/destacamentos/{destacamentoId}/ninos/{ninoId}/padres")
@RequiredArgsConstructor
public class NinoPadreController {

    private final NinoPadreService ninoPadreService;
    private final NinoPadreApiMapper mapper;

    @GetMapping
    @RequiresPermission("NINOS_VER")
    public ResponseEntity<List<NinoPadreResponse>> listar(
            @PathVariable Long destacamentoId, @PathVariable Long ninoId) {
        return ResponseEntity.ok(
                ninoPadreService.listar(destacamentoId, ninoId).stream().map(mapper::toResponse).toList());
    }

    @PostMapping
    @RequiresPermission("NINOS_EDITAR")
    public ResponseEntity<NinoPadreResponse> vincular(
            @PathVariable Long destacamentoId, @PathVariable Long ninoId, @Valid @RequestBody VincularPadreRequest request) {
        NinoPadreResponse creado = mapper.toResponse(
                ninoPadreService.vincular(destacamentoId, ninoId, request.usuarioId()));
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @DeleteMapping("/{usuarioId}")
    @RequiresPermission("NINOS_EDITAR")
    public ResponseEntity<Void> desvincular(
            @PathVariable Long destacamentoId, @PathVariable Long ninoId, @PathVariable Long usuarioId) {
        ninoPadreService.desvincular(destacamentoId, ninoId, usuarioId);
        return ResponseEntity.noContent().build();
    }
}
