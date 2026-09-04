package com.ais.ascensobackend.catalogo.api.controllers;

import com.ais.ascensobackend.catalogo.api.dtos.requests.GuardarReglaAsistenciaRequest;
import com.ais.ascensobackend.catalogo.api.dtos.responses.ReglaAsistenciaResponse;
import com.ais.ascensobackend.catalogo.api.mappers.ReglaAsistenciaApiMapper;
import com.ais.ascensobackend.catalogo.application.services.interfaces.ReglaAsistenciaService;
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
@RequestMapping("/api/v1/catalogo/reglas-asistencia")
@RequiredArgsConstructor
public class ReglaAsistenciaController {

    private final ReglaAsistenciaService reglaAsistenciaService;
    private final ReglaAsistenciaApiMapper mapper;

    @GetMapping
    @RequiresPermission("CATALOGO_VER")
    public ResponseEntity<List<ReglaAsistenciaResponse>> listar() {
        return ResponseEntity.ok(reglaAsistenciaService.listar().stream().map(mapper::toResponse).toList());
    }

    @GetMapping("/vigente")
    @RequiresPermission("CATALOGO_VER")
    public ResponseEntity<ReglaAsistenciaResponse> obtenerVigente() {
        return ResponseEntity.ok(mapper.toResponse(reglaAsistenciaService.obtenerVigente()));
    }

    @GetMapping("/{id}")
    @RequiresPermission("CATALOGO_VER")
    public ResponseEntity<ReglaAsistenciaResponse> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(mapper.toResponse(reglaAsistenciaService.obtener(id)));
    }

    @PostMapping
    @RequiresPermission("CATALOGO_EDITAR")
    public ResponseEntity<ReglaAsistenciaResponse> crear(@Valid @RequestBody GuardarReglaAsistenciaRequest request) {
        ReglaAsistenciaResponse creada = mapper.toResponse(reglaAsistenciaService.crear(
                request.unidad(), request.minimoRequerido(), request.vigenteDesde()));
        return ResponseEntity.status(HttpStatus.CREATED).body(creada);
    }

    @PutMapping("/{id}")
    @RequiresPermission("CATALOGO_EDITAR")
    public ResponseEntity<ReglaAsistenciaResponse> actualizar(
            @PathVariable Long id, @Valid @RequestBody GuardarReglaAsistenciaRequest request) {
        ReglaAsistenciaResponse actualizada = mapper.toResponse(reglaAsistenciaService.actualizar(
                id, request.unidad(), request.minimoRequerido(), request.vigenteDesde()));
        return ResponseEntity.ok(actualizada);
    }
}
