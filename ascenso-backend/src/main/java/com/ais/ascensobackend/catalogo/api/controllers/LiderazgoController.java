package com.ais.ascensobackend.catalogo.api.controllers;

import com.ais.ascensobackend.catalogo.api.dtos.requests.GuardarLiderazgoRequest;
import com.ais.ascensobackend.catalogo.api.dtos.responses.LiderazgoResponse;
import com.ais.ascensobackend.catalogo.api.mappers.LiderazgoApiMapper;
import com.ais.ascensobackend.catalogo.application.services.interfaces.LiderazgoService;
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
@RequestMapping("/api/v1/catalogo/liderazgos")
@RequiredArgsConstructor
public class LiderazgoController {

    private final LiderazgoService liderazgoService;
    private final LiderazgoApiMapper mapper;

    @GetMapping
    @RequiresPermission("CATALOGO_VER")
    public ResponseEntity<List<LiderazgoResponse>> listar(@RequestParam(required = false) Long grupoId) {
        return ResponseEntity.ok(liderazgoService.listar(grupoId).stream().map(mapper::toResponse).toList());
    }

    @GetMapping("/{id}")
    @RequiresPermission("CATALOGO_VER")
    public ResponseEntity<LiderazgoResponse> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(mapper.toResponse(liderazgoService.obtener(id)));
    }

    @PostMapping
    @RequiresPermission("CATALOGO_EDITAR")
    public ResponseEntity<LiderazgoResponse> crear(@Valid @RequestBody GuardarLiderazgoRequest request) {
        LiderazgoResponse creado = mapper.toResponse(
                liderazgoService.crear(request.grupoId(), request.nombre(), request.categoria()));
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PutMapping("/{id}")
    @RequiresPermission("CATALOGO_EDITAR")
    public ResponseEntity<LiderazgoResponse> actualizar(
            @PathVariable Long id, @Valid @RequestBody GuardarLiderazgoRequest request) {
        LiderazgoResponse actualizado = mapper.toResponse(
                liderazgoService.actualizar(id, request.nombre(), request.categoria()));
        return ResponseEntity.ok(actualizado);
    }

    @PostMapping("/{id}/activar")
    @RequiresPermission("CATALOGO_EDITAR")
    public ResponseEntity<Void> activar(@PathVariable Long id) {
        liderazgoService.activar(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/desactivar")
    @RequiresPermission("CATALOGO_EDITAR")
    public ResponseEntity<Void> desactivar(@PathVariable Long id) {
        liderazgoService.desactivar(id);
        return ResponseEntity.noContent().build();
    }
}
