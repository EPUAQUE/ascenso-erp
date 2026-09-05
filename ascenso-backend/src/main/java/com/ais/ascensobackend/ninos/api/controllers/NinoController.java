package com.ais.ascensobackend.ninos.api.controllers;

import com.ais.ascensobackend.ninos.api.dtos.requests.ActualizarNinoRequest;
import com.ais.ascensobackend.ninos.api.dtos.requests.CrearNinoRequest;
import com.ais.ascensobackend.ninos.api.dtos.requests.PromoverNinoRequest;
import com.ais.ascensobackend.ninos.api.dtos.responses.NinoResponse;
import com.ais.ascensobackend.ninos.api.mappers.NinoApiMapper;
import com.ais.ascensobackend.ninos.application.services.interfaces.NinoService;
import com.ais.ascensobackend.seguridad.infrastructure.security.RequiresPermission;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/destacamentos/{destacamentoId}/ninos")
@RequiredArgsConstructor
public class NinoController {

    private final NinoService ninoService;
    private final NinoApiMapper mapper;

    @GetMapping
    @RequiresPermission("NINOS_VER")
    public ResponseEntity<List<NinoResponse>> listar(@PathVariable Long destacamentoId) {
        return ResponseEntity.ok(ninoService.listar(destacamentoId).stream().map(mapper::toResponse).toList());
    }

    @GetMapping("/{id}")
    @RequiresPermission("NINOS_VER")
    public ResponseEntity<NinoResponse> obtener(@PathVariable Long destacamentoId, @PathVariable Long id) {
        return ResponseEntity.ok(mapper.toResponse(ninoService.obtener(destacamentoId, id)));
    }

    @PostMapping
    @RequiresPermission("NINOS_EDITAR")
    public ResponseEntity<NinoResponse> crear(
            @PathVariable Long destacamentoId, @Valid @RequestBody CrearNinoRequest request) {
        NinoResponse creado = mapper.toResponse(ninoService.crear(
                destacamentoId, request.nombreCompleto(), request.fechaNacimiento(), request.fotoUrl(),
                request.encargadoNombre(), request.encargadoContacto(), request.contactoEmergencia(),
                request.fechaIngreso(), request.grupoActualId(), request.anioProgramaActualId()));
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PutMapping("/{id}")
    @RequiresPermission("NINOS_EDITAR")
    public ResponseEntity<NinoResponse> actualizar(
            @PathVariable Long destacamentoId, @PathVariable Long id, @Valid @RequestBody ActualizarNinoRequest request) {
        NinoResponse actualizado = mapper.toResponse(ninoService.actualizar(
                destacamentoId, id, request.nombreCompleto(), request.fechaNacimiento(), request.fotoUrl(),
                request.encargadoNombre(), request.encargadoContacto(), request.contactoEmergencia()));
        return ResponseEntity.ok(actualizado);
    }

    @PostMapping("/{id}/promover")
    @RequiresPermission("NINOS_EDITAR")
    public ResponseEntity<NinoResponse> promover(
            @PathVariable Long destacamentoId, @PathVariable Long id, @Valid @RequestBody PromoverNinoRequest request) {
        NinoResponse promovido = mapper.toResponse(
                ninoService.promover(destacamentoId, id, request.grupoId(), request.anioProgramaId()));
        return ResponseEntity.ok(promovido);
    }

    @PostMapping("/{id}/activar")
    @RequiresPermission("NINOS_EDITAR")
    public ResponseEntity<Void> activar(@PathVariable Long destacamentoId, @PathVariable Long id) {
        ninoService.activar(destacamentoId, id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/desactivar")
    @RequiresPermission("NINOS_EDITAR")
    public ResponseEntity<Void> desactivar(@PathVariable Long destacamentoId, @PathVariable Long id) {
        ninoService.desactivar(destacamentoId, id);
        return ResponseEntity.noContent().build();
    }
}
