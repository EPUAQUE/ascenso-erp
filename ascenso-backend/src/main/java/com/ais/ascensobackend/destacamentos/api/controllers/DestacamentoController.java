package com.ais.ascensobackend.destacamentos.api.controllers;

import com.ais.ascensobackend.destacamentos.api.dtos.requests.ActualizarDestacamentoRequest;
import com.ais.ascensobackend.destacamentos.api.dtos.requests.CrearDestacamentoRequest;
import com.ais.ascensobackend.destacamentos.api.dtos.responses.DestacamentoResponse;
import com.ais.ascensobackend.destacamentos.api.mappers.DestacamentoApiMapper;
import com.ais.ascensobackend.destacamentos.application.services.interfaces.DestacamentoService;
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

/**
 * Gestión del catálogo de destacamentos — solo-supervisor: la asignación de un
 * usuario a un destacamento concreto (¿a qué destacamentos puede acceder?) vive en
 * {@code seguridad.UsuarioController}, este controller solo administra el
 * catálogo en sí (alta, edición, activar/desactivar).
 */
@RestController
@RequestMapping("/api/v1/destacamentos")
@RequiredArgsConstructor
public class DestacamentoController {

    private final DestacamentoService destacamentoService;
    private final DestacamentoApiMapper mapper;

    @GetMapping
    @RequiresPermission("DESTACAMENTOS_VER")
    public ResponseEntity<List<DestacamentoResponse>> listar() {
        return ResponseEntity.ok(destacamentoService.listar().stream().map(mapper::toResponse).toList());
    }

    @GetMapping("/{id}")
    @RequiresPermission("DESTACAMENTOS_VER")
    public ResponseEntity<DestacamentoResponse> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(mapper.toResponse(destacamentoService.obtener(id)));
    }

    @PostMapping
    @RequiresPermission("DESTACAMENTOS_CREAR")
    public ResponseEntity<DestacamentoResponse> crear(@Valid @RequestBody CrearDestacamentoRequest request) {
        DestacamentoResponse creado = mapper.toResponse(destacamentoService.crear(
                request.numeroUnico(), request.nombre(), request.iglesiaNombre(), request.direccion(),
                request.modoCorteAnio()));
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PutMapping("/{id}")
    @RequiresPermission("DESTACAMENTOS_EDITAR")
    public ResponseEntity<DestacamentoResponse> actualizar(
            @PathVariable Long id, @Valid @RequestBody ActualizarDestacamentoRequest request) {
        DestacamentoResponse actualizado = mapper.toResponse(destacamentoService.actualizar(
                id, request.nombre(), request.iglesiaNombre(), request.direccion(), request.modoCorteAnio()));
        return ResponseEntity.ok(actualizado);
    }

    @PostMapping("/{id}/activar")
    @RequiresPermission("DESTACAMENTOS_EDITAR")
    public ResponseEntity<Void> activar(@PathVariable Long id) {
        destacamentoService.activar(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/desactivar")
    @RequiresPermission("DESTACAMENTOS_EDITAR")
    public ResponseEntity<Void> desactivar(@PathVariable Long id) {
        destacamentoService.desactivar(id);
        return ResponseEntity.noContent().build();
    }
}
