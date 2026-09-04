package com.ais.ascensobackend.catalogo.api.controllers;

import com.ais.ascensobackend.catalogo.api.dtos.requests.ActualizarGrupoRequest;
import com.ais.ascensobackend.catalogo.api.dtos.responses.GrupoResponse;
import com.ais.ascensobackend.catalogo.api.mappers.GrupoApiMapper;
import com.ais.ascensobackend.catalogo.application.services.interfaces.GrupoService;
import com.ais.ascensobackend.seguridad.infrastructure.security.RequiresPermission;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/catalogo/grupos")
@RequiredArgsConstructor
public class GrupoController {

    private final GrupoService grupoService;
    private final GrupoApiMapper mapper;

    @GetMapping
    @RequiresPermission("CATALOGO_VER")
    public ResponseEntity<List<GrupoResponse>> listar() {
        return ResponseEntity.ok(grupoService.listar().stream().map(mapper::toResponse).toList());
    }

    @GetMapping("/{id}")
    @RequiresPermission("CATALOGO_VER")
    public ResponseEntity<GrupoResponse> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(mapper.toResponse(grupoService.obtener(id)));
    }

    @PutMapping("/{id}")
    @RequiresPermission("CATALOGO_EDITAR")
    public ResponseEntity<GrupoResponse> actualizar(@PathVariable Long id, @Valid @RequestBody ActualizarGrupoRequest request) {
        GrupoResponse actualizado = mapper.toResponse(grupoService.actualizar(
                id, request.nombre(), request.edadMin(), request.edadMax(), request.orden(), request.descripcion()));
        return ResponseEntity.ok(actualizado);
    }
}
