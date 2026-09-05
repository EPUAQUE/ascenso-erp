package com.ais.ascensobackend.ninos.api.controllers;

import com.ais.ascensobackend.ninos.api.dtos.requests.OtorgarMedallaRequest;
import com.ais.ascensobackend.ninos.api.dtos.responses.MedallaOtorgadaResponse;
import com.ais.ascensobackend.ninos.api.mappers.MedallaOtorgadaApiMapper;
import com.ais.ascensobackend.ninos.application.services.interfaces.MedallaOtorgadaService;
import com.ais.ascensobackend.seguridad.infrastructure.security.RequiresPermission;
import jakarta.validation.Valid;
import java.util.List;
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
@RequestMapping("/api/v1/destacamentos/{destacamentoId}/ninos/{ninoId}/medallas")
@RequiredArgsConstructor
public class MedallaOtorgadaController {

    private final MedallaOtorgadaService medallaOtorgadaService;
    private final MedallaOtorgadaApiMapper mapper;

    @GetMapping
    @RequiresPermission("NINOS_VER")
    public ResponseEntity<List<MedallaOtorgadaResponse>> listar(
            @PathVariable Long destacamentoId, @PathVariable Long ninoId) {
        return ResponseEntity.ok(
                medallaOtorgadaService.listar(destacamentoId, ninoId).stream().map(mapper::toResponse).toList());
    }

    @PostMapping
    @RequiresPermission("NINOS_EDITAR")
    public ResponseEntity<MedallaOtorgadaResponse> otorgar(
            @PathVariable Long destacamentoId, @PathVariable Long ninoId, @Valid @RequestBody OtorgarMedallaRequest request) {
        MedallaOtorgadaResponse creada = mapper.toResponse(medallaOtorgadaService.otorgar(
                destacamentoId, ninoId, request.anioProgramaId(), request.fechaOtorgada()));
        return ResponseEntity.status(HttpStatus.CREATED).body(creada);
    }
}
