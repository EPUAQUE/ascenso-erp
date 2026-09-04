package com.ais.ascensobackend.catalogo.api.controllers;

import com.ais.ascensobackend.catalogo.api.dtos.requests.GuardarPasoRequeridoRequest;
import com.ais.ascensobackend.catalogo.api.dtos.responses.PasoRequeridoResponse;
import com.ais.ascensobackend.catalogo.api.mappers.PasoRequeridoApiMapper;
import com.ais.ascensobackend.catalogo.application.services.interfaces.PasoRequeridoService;
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
@RequestMapping("/api/v1/catalogo/pasos-requeridos")
@RequiredArgsConstructor
public class PasoRequeridoController {

    private final PasoRequeridoService pasoRequeridoService;
    private final PasoRequeridoApiMapper mapper;

    @GetMapping
    @RequiresPermission("CATALOGO_VER")
    public ResponseEntity<List<PasoRequeridoResponse>> listar(@RequestParam(required = false) Long anioProgramaId) {
        return ResponseEntity.ok(pasoRequeridoService.listar(anioProgramaId).stream().map(mapper::toResponse).toList());
    }

    @GetMapping("/{id}")
    @RequiresPermission("CATALOGO_VER")
    public ResponseEntity<PasoRequeridoResponse> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(mapper.toResponse(pasoRequeridoService.obtener(id)));
    }

    @PostMapping
    @RequiresPermission("CATALOGO_EDITAR")
    public ResponseEntity<PasoRequeridoResponse> crear(@Valid @RequestBody GuardarPasoRequeridoRequest request) {
        PasoRequeridoResponse creado = mapper.toResponse(
                pasoRequeridoService.crear(request.anioProgramaId(), request.descripcion(), request.orden()));
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PutMapping("/{id}")
    @RequiresPermission("CATALOGO_EDITAR")
    public ResponseEntity<PasoRequeridoResponse> actualizar(
            @PathVariable Long id, @Valid @RequestBody GuardarPasoRequeridoRequest request) {
        PasoRequeridoResponse actualizado = mapper.toResponse(
                pasoRequeridoService.actualizar(id, request.descripcion(), request.orden()));
        return ResponseEntity.ok(actualizado);
    }
}
