package com.ais.ascensobackend.actividades.api.controllers;

import com.ais.ascensobackend.actividades.api.dtos.requests.GuardarActividadRequest;
import com.ais.ascensobackend.actividades.api.dtos.responses.ActividadResponse;
import com.ais.ascensobackend.actividades.api.mappers.ActividadApiMapper;
import com.ais.ascensobackend.actividades.application.services.interfaces.ActividadService;
import com.ais.ascensobackend.seguridad.application.services.interfaces.UsuarioService;
import com.ais.ascensobackend.seguridad.infrastructure.security.RequiresPermission;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/destacamentos/{destacamentoId}/actividades")
@RequiredArgsConstructor
public class ActividadController {

    private final ActividadService actividadService;
    private final UsuarioService usuarioService;
    private final ActividadApiMapper mapper;

    @GetMapping
    @RequiresPermission("ACTIVIDADES_VER")
    public ResponseEntity<List<ActividadResponse>> listar(@PathVariable Long destacamentoId) {
        return ResponseEntity.ok(actividadService.listar(destacamentoId).stream().map(mapper::toResponse).toList());
    }

    @GetMapping("/{id}")
    @RequiresPermission("ACTIVIDADES_VER")
    public ResponseEntity<ActividadResponse> obtener(@PathVariable Long destacamentoId, @PathVariable Long id) {
        return ResponseEntity.ok(mapper.toResponse(actividadService.obtener(destacamentoId, id)));
    }

    @PostMapping
    @RequiresPermission("ACTIVIDADES_EDITAR")
    public ResponseEntity<ActividadResponse> crear(
            @PathVariable Long destacamentoId, @Valid @RequestBody GuardarActividadRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        Long creadoPor = usuarioService.obtenerPorUsername(jwt.getSubject()).id();
        ActividadResponse creada = mapper.toResponse(actividadService.crear(
                destacamentoId, request.titulo(), request.descripcion(), request.fechaInicio(), request.fechaFin(),
                creadoPor));
        return ResponseEntity.status(HttpStatus.CREATED).body(creada);
    }

    @PutMapping("/{id}")
    @RequiresPermission("ACTIVIDADES_EDITAR")
    public ResponseEntity<ActividadResponse> actualizar(
            @PathVariable Long destacamentoId, @PathVariable Long id, @Valid @RequestBody GuardarActividadRequest request) {
        ActividadResponse actualizada = mapper.toResponse(actividadService.actualizar(
                destacamentoId, id, request.titulo(), request.descripcion(), request.fechaInicio(), request.fechaFin()));
        return ResponseEntity.ok(actualizada);
    }
}
