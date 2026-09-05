package com.ais.ascensobackend.ninos.api.controllers;

import com.ais.ascensobackend.ninos.api.dtos.requests.RegistrarProgresoDestrezaRequest;
import com.ais.ascensobackend.ninos.api.dtos.responses.ProgresoDestrezaResponse;
import com.ais.ascensobackend.ninos.api.mappers.ProgresoDestrezaApiMapper;
import com.ais.ascensobackend.ninos.application.services.interfaces.ProgresoDestrezaService;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/destacamentos/{destacamentoId}/ninos/{ninoId}/progreso-destrezas")
@RequiredArgsConstructor
public class ProgresoDestrezaController {

    private final ProgresoDestrezaService progresoDestrezaService;
    private final UsuarioService usuarioService;
    private final ProgresoDestrezaApiMapper mapper;

    @GetMapping
    @RequiresPermission("NINOS_VER")
    public ResponseEntity<List<ProgresoDestrezaResponse>> listar(
            @PathVariable Long destacamentoId, @PathVariable Long ninoId) {
        return ResponseEntity.ok(
                progresoDestrezaService.listar(destacamentoId, ninoId).stream().map(mapper::toResponse).toList());
    }

    @PostMapping
    @RequiresPermission("NINOS_EDITAR")
    public ResponseEntity<ProgresoDestrezaResponse> registrar(
            @PathVariable Long destacamentoId, @PathVariable Long ninoId,
            @Valid @RequestBody RegistrarProgresoDestrezaRequest request, @AuthenticationPrincipal Jwt jwt) {
        Long registradoPor = usuarioService.obtenerPorUsername(jwt.getSubject()).id();
        ProgresoDestrezaResponse creado = mapper.toResponse(progresoDestrezaService.registrar(
                destacamentoId, ninoId, request.destrezaId(), request.anioProgramaObjetivoId(),
                request.fechaCompletado(), registradoPor));
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }
}
