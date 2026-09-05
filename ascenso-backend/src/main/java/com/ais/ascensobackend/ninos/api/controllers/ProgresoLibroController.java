package com.ais.ascensobackend.ninos.api.controllers;

import com.ais.ascensobackend.ninos.api.dtos.requests.RegistrarProgresoLibroRequest;
import com.ais.ascensobackend.ninos.api.dtos.responses.ProgresoLibroResponse;
import com.ais.ascensobackend.ninos.api.mappers.ProgresoLibroApiMapper;
import com.ais.ascensobackend.ninos.application.services.interfaces.ProgresoLibroService;
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
@RequestMapping("/api/v1/destacamentos/{destacamentoId}/ninos/{ninoId}/progreso-libros")
@RequiredArgsConstructor
public class ProgresoLibroController {

    private final ProgresoLibroService progresoLibroService;
    private final UsuarioService usuarioService;
    private final ProgresoLibroApiMapper mapper;

    @GetMapping
    @RequiresPermission("NINOS_VER")
    public ResponseEntity<List<ProgresoLibroResponse>> listar(
            @PathVariable Long destacamentoId, @PathVariable Long ninoId) {
        return ResponseEntity.ok(
                progresoLibroService.listar(destacamentoId, ninoId).stream().map(mapper::toResponse).toList());
    }

    @PostMapping
    @RequiresPermission("NINOS_EDITAR")
    public ResponseEntity<ProgresoLibroResponse> registrar(
            @PathVariable Long destacamentoId, @PathVariable Long ninoId,
            @Valid @RequestBody RegistrarProgresoLibroRequest request, @AuthenticationPrincipal Jwt jwt) {
        Long registradoPor = usuarioService.obtenerPorUsername(jwt.getSubject()).id();
        ProgresoLibroResponse creado = mapper.toResponse(progresoLibroService.registrar(
                destacamentoId, ninoId, request.libroBiblicoId(), request.anioProgramaObjetivoId(),
                request.fechaCompletado(), registradoPor));
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }
}
