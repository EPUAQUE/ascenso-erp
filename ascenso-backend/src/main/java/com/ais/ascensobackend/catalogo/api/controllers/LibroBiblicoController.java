package com.ais.ascensobackend.catalogo.api.controllers;

import com.ais.ascensobackend.catalogo.api.dtos.requests.GuardarLibroBiblicoRequest;
import com.ais.ascensobackend.catalogo.api.dtos.responses.LibroBiblicoResponse;
import com.ais.ascensobackend.catalogo.api.mappers.LibroBiblicoApiMapper;
import com.ais.ascensobackend.catalogo.application.services.interfaces.LibroBiblicoService;
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
@RequestMapping("/api/v1/catalogo/libros-biblicos")
@RequiredArgsConstructor
public class LibroBiblicoController {

    private final LibroBiblicoService libroBiblicoService;
    private final LibroBiblicoApiMapper mapper;

    @GetMapping
    @RequiresPermission("CATALOGO_VER")
    public ResponseEntity<List<LibroBiblicoResponse>> listar(@RequestParam(required = false) Long grupoId) {
        return ResponseEntity.ok(libroBiblicoService.listar(grupoId).stream().map(mapper::toResponse).toList());
    }

    @GetMapping("/{id}")
    @RequiresPermission("CATALOGO_VER")
    public ResponseEntity<LibroBiblicoResponse> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(mapper.toResponse(libroBiblicoService.obtener(id)));
    }

    @PostMapping
    @RequiresPermission("CATALOGO_EDITAR")
    public ResponseEntity<LibroBiblicoResponse> crear(@Valid @RequestBody GuardarLibroBiblicoRequest request) {
        LibroBiblicoResponse creado = mapper.toResponse(
                libroBiblicoService.crear(request.grupoId(), request.titulo(), request.ordenSugerido()));
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PutMapping("/{id}")
    @RequiresPermission("CATALOGO_EDITAR")
    public ResponseEntity<LibroBiblicoResponse> actualizar(
            @PathVariable Long id, @Valid @RequestBody GuardarLibroBiblicoRequest request) {
        LibroBiblicoResponse actualizado = mapper.toResponse(
                libroBiblicoService.actualizar(id, request.titulo(), request.ordenSugerido()));
        return ResponseEntity.ok(actualizado);
    }

    @PostMapping("/{id}/activar")
    @RequiresPermission("CATALOGO_EDITAR")
    public ResponseEntity<Void> activar(@PathVariable Long id) {
        libroBiblicoService.activar(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/desactivar")
    @RequiresPermission("CATALOGO_EDITAR")
    public ResponseEntity<Void> desactivar(@PathVariable Long id) {
        libroBiblicoService.desactivar(id);
        return ResponseEntity.noContent().build();
    }
}
