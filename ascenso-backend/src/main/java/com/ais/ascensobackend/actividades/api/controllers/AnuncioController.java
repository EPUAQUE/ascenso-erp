package com.ais.ascensobackend.actividades.api.controllers;

import com.ais.ascensobackend.actividades.api.dtos.requests.GuardarAnuncioRequest;
import com.ais.ascensobackend.actividades.api.dtos.responses.AnuncioResponse;
import com.ais.ascensobackend.actividades.api.mappers.AnuncioApiMapper;
import com.ais.ascensobackend.actividades.application.services.interfaces.AnuncioService;
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

/**
 * Anuncios globales — visibles para todos los destacamentos, pensados para el
 * portal de padres. A diferencia de {@code ActividadController}, no lleva
 * variable de ruta {@code destacamentoId} (nada que alcanzar por destacamento).
 */
@RestController
@RequestMapping("/api/v1/anuncios")
@RequiredArgsConstructor
public class AnuncioController {

    private final AnuncioService anuncioService;
    private final UsuarioService usuarioService;
    private final AnuncioApiMapper mapper;

    @GetMapping
    @RequiresPermission("ANUNCIOS_VER")
    public ResponseEntity<List<AnuncioResponse>> listar() {
        return ResponseEntity.ok(anuncioService.listar().stream().map(mapper::toResponse).toList());
    }

    @GetMapping("/{id}")
    @RequiresPermission("ANUNCIOS_VER")
    public ResponseEntity<AnuncioResponse> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(mapper.toResponse(anuncioService.obtener(id)));
    }

    @PostMapping
    @RequiresPermission("ANUNCIOS_EDITAR")
    public ResponseEntity<AnuncioResponse> crear(
            @Valid @RequestBody GuardarAnuncioRequest request, @AuthenticationPrincipal Jwt jwt) {
        Long creadoPor = usuarioService.obtenerPorUsername(jwt.getSubject()).id();
        AnuncioResponse creado = mapper.toResponse(anuncioService.crear(
                request.titulo(), request.descripcion(), request.imagenUrl(), request.enlaceUrl(),
                request.fechaInicioVisible(), request.fechaFinVisible(), creadoPor));
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PutMapping("/{id}")
    @RequiresPermission("ANUNCIOS_EDITAR")
    public ResponseEntity<AnuncioResponse> actualizar(
            @PathVariable Long id, @Valid @RequestBody GuardarAnuncioRequest request) {
        AnuncioResponse actualizado = mapper.toResponse(anuncioService.actualizar(
                id, request.titulo(), request.descripcion(), request.imagenUrl(), request.enlaceUrl(),
                request.fechaInicioVisible(), request.fechaFinVisible()));
        return ResponseEntity.ok(actualizado);
    }

    @PostMapping("/{id}/activar")
    @RequiresPermission("ANUNCIOS_EDITAR")
    public ResponseEntity<Void> activar(@PathVariable Long id) {
        anuncioService.activar(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/desactivar")
    @RequiresPermission("ANUNCIOS_EDITAR")
    public ResponseEntity<Void> desactivar(@PathVariable Long id) {
        anuncioService.desactivar(id);
        return ResponseEntity.noContent().build();
    }
}
