package com.ais.ascensobackend.catalogo.api.controllers;

import com.ais.ascensobackend.catalogo.api.dtos.requests.GuardarDestrezaRequest;
import com.ais.ascensobackend.catalogo.api.dtos.responses.DestrezaResponse;
import com.ais.ascensobackend.catalogo.api.mappers.DestrezaApiMapper;
import com.ais.ascensobackend.catalogo.application.services.interfaces.DestrezaService;
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
@RequestMapping("/api/v1/catalogo/destrezas")
@RequiredArgsConstructor
public class DestrezaController {

    private final DestrezaService destrezaService;
    private final DestrezaApiMapper mapper;

    @GetMapping
    @RequiresPermission("CATALOGO_VER")
    public ResponseEntity<List<DestrezaResponse>> listar(@RequestParam(required = false) Long grupoId) {
        return ResponseEntity.ok(destrezaService.listar(grupoId).stream().map(mapper::toResponse).toList());
    }

    @GetMapping("/{id}")
    @RequiresPermission("CATALOGO_VER")
    public ResponseEntity<DestrezaResponse> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(mapper.toResponse(destrezaService.obtener(id)));
    }

    @PostMapping
    @RequiresPermission("CATALOGO_EDITAR")
    public ResponseEntity<DestrezaResponse> crear(@Valid @RequestBody GuardarDestrezaRequest request) {
        DestrezaResponse creado = mapper.toResponse(
                destrezaService.crear(request.grupoId(), request.nombre(), request.categoria()));
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PutMapping("/{id}")
    @RequiresPermission("CATALOGO_EDITAR")
    public ResponseEntity<DestrezaResponse> actualizar(
            @PathVariable Long id, @Valid @RequestBody GuardarDestrezaRequest request) {
        DestrezaResponse actualizado = mapper.toResponse(
                destrezaService.actualizar(id, request.nombre(), request.categoria()));
        return ResponseEntity.ok(actualizado);
    }

    @PostMapping("/{id}/activar")
    @RequiresPermission("CATALOGO_EDITAR")
    public ResponseEntity<Void> activar(@PathVariable Long id) {
        destrezaService.activar(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/desactivar")
    @RequiresPermission("CATALOGO_EDITAR")
    public ResponseEntity<Void> desactivar(@PathVariable Long id) {
        destrezaService.desactivar(id);
        return ResponseEntity.noContent().build();
    }
}
