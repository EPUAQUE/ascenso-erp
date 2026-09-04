package com.ais.ascensobackend.catalogo.api.controllers;

import com.ais.ascensobackend.catalogo.api.dtos.requests.GuardarAnioProgramaRequest;
import com.ais.ascensobackend.catalogo.api.dtos.responses.AnioProgramaResponse;
import com.ais.ascensobackend.catalogo.api.mappers.AnioProgramaApiMapper;
import com.ais.ascensobackend.catalogo.application.services.interfaces.AnioProgramaService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/catalogo/anios-programa")
@RequiredArgsConstructor
public class AnioProgramaController {

    private final AnioProgramaService anioProgramaService;
    private final AnioProgramaApiMapper mapper;

    @GetMapping
    @RequiresPermission("CATALOGO_VER")
    public ResponseEntity<List<AnioProgramaResponse>> listar(@RequestParam(required = false) Long grupoId) {
        return ResponseEntity.ok(anioProgramaService.listar(grupoId).stream().map(mapper::toResponse).toList());
    }

    @GetMapping("/{id}")
    @RequiresPermission("CATALOGO_VER")
    public ResponseEntity<AnioProgramaResponse> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(mapper.toResponse(anioProgramaService.obtener(id)));
    }

    @PostMapping
    @RequiresPermission("CATALOGO_EDITAR")
    public ResponseEntity<AnioProgramaResponse> crear(@Valid @RequestBody GuardarAnioProgramaRequest request) {
        AnioProgramaResponse creado = mapper.toResponse(anioProgramaService.crear(
                request.grupoId(), request.numero(), request.medalla(), request.minimoLibros(),
                request.minimoDestrezas(), request.minimoLiderazgo(), request.esAnioGracia()));
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PutMapping("/{id}")
    @RequiresPermission("CATALOGO_EDITAR")
    public ResponseEntity<AnioProgramaResponse> actualizar(
            @PathVariable Long id, @Valid @RequestBody GuardarAnioProgramaRequest request) {
        AnioProgramaResponse actualizado = mapper.toResponse(anioProgramaService.actualizar(
                id, request.numero(), request.medalla(), request.minimoLibros(), request.minimoDestrezas(),
                request.minimoLiderazgo(), request.esAnioGracia()));
        return ResponseEntity.ok(actualizado);
    }
}
