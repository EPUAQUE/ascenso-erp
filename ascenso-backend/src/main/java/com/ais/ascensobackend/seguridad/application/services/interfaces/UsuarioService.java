package com.ais.ascensobackend.seguridad.application.services.interfaces;

import com.ais.ascensobackend.seguridad.application.dtos.UsuarioDestacamentoResumen;
import com.ais.ascensobackend.seguridad.application.dtos.UsuarioResumen;
import com.ais.ascensobackend.seguridad.domain.model.PermisosEfectivos;
import java.util.List;

public interface UsuarioService {

    UsuarioResumen crear(String username, String passwordPlano, String nombre, String telefono, String correo);

    /** Usado por otros módulos para resolver el usuario autenticado desde el JWT. */
    UsuarioResumen obtenerPorUsername(String username);

    /**
     * Exige que quien llama (el usuario autenticado actual) tenga acceso a
     * {@code destacamentoId} y no pueda escalar a un rol de alcance global —
     * pensado para el flujo HTTP de un supervisor asignando destacamento a otro
     * usuario. Nunca llamar desde código sin una autenticación real en
     * {@code SecurityContextHolder} (p. ej. un {@code ApplicationRunner} de
     * arranque): usar {@link #asignarDestacamentoSistema(Long, Long, Long)} para eso.
     */
    void asignarDestacamento(Long usuarioId, Long destacamentoId, Long rolId);

    /**
     * Misma operación que {@link #asignarDestacamento(Long, Long, Long)} pero SIN
     * exigir acceso del llamador ni bloquear escalación de alcance global. Uso
     * exclusivo de bootstrap de sistema; nunca exponerla vía un endpoint HTTP.
     */
    void asignarDestacamentoSistema(Long usuarioId, Long destacamentoId, Long rolId);

    /**
     * Asigna un rol de alcance global (ej. SUPERVISOR_GENERAL) a un usuario SIN
     * destacamento — crea una fila en {@code usuario_destacamento} con
     * {@code destacamentoId = null}. Necesario porque
     * {@code PermisosEfectivosResolverImpl} solo descubre el rol de un usuario
     * iterando esa tabla: sin al menos una fila, un usuario de alcance global no
     * podría resolver ningún permiso. Uso exclusivo de bootstrap de sistema
     * ({@code AdminUserSeeder}); nunca exponerla vía un endpoint HTTP — no valida
     * que el rol sea realmente de alcance global, ni exige acceso del llamador.
     */
    void asignarRolGlobalSistema(Long usuarioId, Long rolId);

    List<UsuarioDestacamentoResumen> listarDestacamentos(Long usuarioId);

    PermisosEfectivos obtenerPermisosEfectivos(Long usuarioId);

    PermisosEfectivos obtenerPermisosEfectivosPorUsername(String username);

    List<UsuarioResumen> listar();

    /**
     * Autoservicio: el usuario autenticado cambia su propia contraseña, verificando
     * la actual. Revoca todas sus sesiones activas (refresh tokens).
     */
    void cambiarMiPassword(Long usuarioId, String passwordActual, String passwordNueva);

    /**
     * Restablecimiento administrativo: genera una contraseña temporal aleatoria, la
     * aplica y marca la cuenta para forzar el cambio en el próximo login. Devuelve
     * la contraseña en texto plano — la única vez que existe fuera del hash.
     */
    String restablecerPassword(Long usuarioId);

    /**
     * Revoca todas las sesiones activas de otro usuario: invalida de inmediato
     * todos sus access tokens ya emitidos (versión de seguridad) y revoca todos
     * sus refresh tokens — sin cambiar su contraseña ni su estado.
     */
    void revocarSesiones(Long usuarioId);

    /** Baja de miembro: marca la cuenta {@code INACTIVO} y revoca todas sus sesiones activas. */
    UsuarioResumen desactivar(Long usuarioId);

    /** Bloqueo administrativo inmediato (sospecha de compromiso). Reversible con {@link #activar(Long)}. */
    UsuarioResumen bloquear(Long usuarioId);

    /** Reactiva una cuenta {@code INACTIVO} o {@code BLOQUEADO} — vuelve a {@code ACTIVO}. */
    UsuarioResumen activar(Long usuarioId);
}
