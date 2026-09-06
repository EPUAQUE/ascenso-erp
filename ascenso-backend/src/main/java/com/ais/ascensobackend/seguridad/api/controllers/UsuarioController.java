package com.ais.ascensobackend.seguridad.api.controllers;

import com.ais.ascensobackend.seguridad.api.dtos.requests.AsignarDestacamentoRolRequest;
import com.ais.ascensobackend.seguridad.api.dtos.requests.CrearUsuarioRequest;
import com.ais.ascensobackend.seguridad.api.dtos.responses.RestablecerPasswordResponse;
import com.ais.ascensobackend.seguridad.api.dtos.responses.UsuarioDestacamentoResponse;
import com.ais.ascensobackend.seguridad.api.dtos.responses.UsuarioNombreResponse;
import com.ais.ascensobackend.seguridad.api.dtos.responses.UsuarioResponse;
import com.ais.ascensobackend.seguridad.api.mappers.UsuarioApiMapper;
import com.ais.ascensobackend.seguridad.application.services.interfaces.UsuarioService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final UsuarioApiMapper mapper;

    @GetMapping
    @RequiresPermission("USUARIOS_VER")
    public ResponseEntity<List<UsuarioResponse>> listar() {
        List<UsuarioResponse> usuarios = usuarioService.listar().stream().map(mapper::toResponse).toList();
        return ResponseEntity.ok(usuarios);
    }

    @PostMapping
    @RequiresPermission("USUARIOS_CREAR")
    public ResponseEntity<UsuarioResponse> crear(@Valid @RequestBody CrearUsuarioRequest request) {
        UsuarioResponse creado = mapper.toResponse(usuarioService.crear(
                request.username(), request.password(), request.nombre(), request.telefono(), request.correo()));
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    /**
     * Autoservicio: cualquier usuario autenticado resuelve solo el nombre (nada más)
     * de un conjunto de usuarios, sin requerir {@code USUARIOS_VER} — usado por
     * ejemplo para mostrar nombres reales en la gestión de {@code nino_padre}, donde
     * quien administra el vínculo (vía NINOS_EDITAR) no tiene acceso al listado
     * completo de usuarios.
     */
    @GetMapping("/resolver-nombres")
    public ResponseEntity<List<UsuarioNombreResponse>> resolverNombres(@RequestParam List<Long> ids) {
        List<UsuarioNombreResponse> nombres = usuarioService.listarNombres(ids).stream()
                .map(mapper::toResponse)
                .toList();
        return ResponseEntity.ok(nombres);
    }

    @PostMapping("/{usuarioId}/destacamentos")
    @RequiresPermission("USUARIOS_ASIGNAR_DESTACAMENTO")
    public ResponseEntity<Void> asignarDestacamento(
            @PathVariable Long usuarioId, @Valid @RequestBody AsignarDestacamentoRolRequest request) {
        usuarioService.asignarDestacamento(usuarioId, request.destacamentoId(), request.rolId());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{usuarioId}/destacamentos")
    @RequiresPermission("USUARIOS_VER")
    public ResponseEntity<List<UsuarioDestacamentoResponse>> listarDestacamentos(@PathVariable Long usuarioId) {
        List<UsuarioDestacamentoResponse> asignaciones = usuarioService.listarDestacamentos(usuarioId).stream()
                .map(ud -> new UsuarioDestacamentoResponse(ud.id(), ud.destacamentoId(), ud.rolId(), ud.rolNombre()))
                .toList();
        return ResponseEntity.ok(asignaciones);
    }

    /**
     * Acción sobre OTRO usuario — a diferencia de {@code AuthController.cambiarMiPassword}
     * (autoservicio), esta exige un permiso administrativo dedicado.
     */
    @PostMapping("/{usuarioId}/password/restablecer")
    @RequiresPermission("USUARIOS_RESTABLECER_PASSWORD")
    public ResponseEntity<RestablecerPasswordResponse> restablecerPassword(@PathVariable Long usuarioId) {
        String passwordTemporal = usuarioService.restablecerPassword(usuarioId);
        return ResponseEntity.ok(RestablecerPasswordResponse.builder().passwordTemporal(passwordTemporal).build());
    }

    /**
     * Revoca todas las sesiones activas de OTRO usuario (refresh tokens + access
     * tokens ya emitidos, vía versión de seguridad) sin cambiar su contraseña ni
     * su estado.
     */
    @PostMapping("/{usuarioId}/sesiones/revocar")
    @RequiresPermission("USUARIOS_REVOCAR_SESIONES")
    public ResponseEntity<Void> revocarSesiones(@PathVariable Long usuarioId) {
        usuarioService.revocarSesiones(usuarioId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{usuarioId}/desactivar")
    @RequiresPermission("USUARIOS_CAMBIAR_ESTADO")
    public ResponseEntity<UsuarioResponse> desactivar(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(mapper.toResponse(usuarioService.desactivar(usuarioId)));
    }

    @PostMapping("/{usuarioId}/bloquear")
    @RequiresPermission("USUARIOS_CAMBIAR_ESTADO")
    public ResponseEntity<UsuarioResponse> bloquear(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(mapper.toResponse(usuarioService.bloquear(usuarioId)));
    }

    @PostMapping("/{usuarioId}/activar")
    @RequiresPermission("USUARIOS_CAMBIAR_ESTADO")
    public ResponseEntity<UsuarioResponse> activar(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(mapper.toResponse(usuarioService.activar(usuarioId)));
    }
}
