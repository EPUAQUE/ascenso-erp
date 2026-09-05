package com.ais.ascensobackend.ninos.api.controllers;

import com.ais.ascensobackend.ninos.api.dtos.requests.OtorgarLogroMayorRequest;
import com.ais.ascensobackend.ninos.api.dtos.responses.LogroMayorResponse;
import com.ais.ascensobackend.ninos.api.mappers.LogroMayorApiMapper;
import com.ais.ascensobackend.ninos.application.services.interfaces.LogroMayorService;
import com.ais.ascensobackend.seguridad.infrastructure.security.RequiresPermission;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/destacamentos/{destacamentoId}/ninos/{ninoId}/logro-mayor")
@RequiredArgsConstructor
public class LogroMayorController {

    private final LogroMayorService logroMayorService;
    private final LogroMayorApiMapper mapper;

    @GetMapping
    @RequiresPermission("NINOS_VER")
    public ResponseEntity<LogroMayorResponse> obtener(@PathVariable Long destacamentoId, @PathVariable Long ninoId) {
        return ResponseEntity.ok(mapper.toResponse(logroMayorService.obtener(destacamentoId, ninoId)));
    }

    @PostMapping
    @RequiresPermission("NINOS_EDITAR")
    public ResponseEntity<LogroMayorResponse> otorgar(
            @PathVariable Long destacamentoId, @PathVariable Long ninoId, @Valid @RequestBody OtorgarLogroMayorRequest request) {
        LogroMayorResponse creado = mapper.toResponse(
                logroMayorService.otorgar(destacamentoId, ninoId, request.fechaOtorgada()));
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }
}
