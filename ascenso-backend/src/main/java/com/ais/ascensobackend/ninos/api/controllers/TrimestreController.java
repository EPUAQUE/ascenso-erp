package com.ais.ascensobackend.ninos.api.controllers;

import com.ais.ascensobackend.ninos.api.dtos.requests.GuardarTrimestreRequest;
import com.ais.ascensobackend.ninos.api.dtos.responses.TrimestreResponse;
import com.ais.ascensobackend.ninos.api.mappers.TrimestreApiMapper;
import com.ais.ascensobackend.ninos.application.services.interfaces.TrimestreService;
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
@RequestMapping("/api/v1/trimestres")
@RequiredArgsConstructor
public class TrimestreController {

    private final TrimestreService trimestreService;
    private final TrimestreApiMapper mapper;

    @GetMapping
    @RequiresPermission("TRIMESTRES_VER")
    public ResponseEntity<List<TrimestreResponse>> listar() {
        return ResponseEntity.ok(trimestreService.listar().stream().map(mapper::toResponse).toList());
    }

    @GetMapping("/{id}")
    @RequiresPermission("TRIMESTRES_VER")
    public ResponseEntity<TrimestreResponse> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(mapper.toResponse(trimestreService.obtener(id)));
    }

    @PostMapping
    @RequiresPermission("TRIMESTRES_EDITAR")
    public ResponseEntity<TrimestreResponse> crear(@Valid @RequestBody GuardarTrimestreRequest request) {
        TrimestreResponse creado = mapper.toResponse(trimestreService.crear(
                request.anioCalendario(), request.numero(), request.fechaInicio(), request.fechaFin()));
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PutMapping("/{id}")
    @RequiresPermission("TRIMESTRES_EDITAR")
    public ResponseEntity<TrimestreResponse> actualizar(
            @PathVariable Long id, @Valid @RequestBody GuardarTrimestreRequest request) {
        TrimestreResponse actualizado = mapper.toResponse(trimestreService.actualizar(
                id, request.anioCalendario(), request.numero(), request.fechaInicio(), request.fechaFin()));
        return ResponseEntity.ok(actualizado);
    }
}
