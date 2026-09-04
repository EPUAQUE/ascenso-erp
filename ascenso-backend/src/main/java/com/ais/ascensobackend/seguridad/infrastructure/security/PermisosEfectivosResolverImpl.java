package com.ais.ascensobackend.seguridad.infrastructure.security;

import com.ais.ascensobackend.seguridad.domain.model.PermisosEfectivos;
import com.ais.ascensobackend.seguridad.domain.model.Rol;
import com.ais.ascensobackend.seguridad.domain.model.UsuarioDestacamento;
import com.ais.ascensobackend.seguridad.domain.repository.UsuarioDestacamentoRepository;
import com.ais.ascensobackend.seguridad.domain.repository.UsuarioRepository;
import com.ais.ascensobackend.seguridad.domain.service.PermisosEfectivosResolver;
import com.ais.ascensobackend.shared.exceptions.ResourceNotFoundException;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

/**
 * Unión de permisos de todos los roles asignados a un usuario (globales o por
 * destacamento). Cachea el resultado con TTL corto — la base de datos sigue siendo
 * la fuente de verdad. {@code UsuarioServiceImpl} llama a {@link #invalidar} tras
 * cada cambio que puede afectar los permisos de un usuario.
 *
 * <p>A diferencia del proyecto de referencia (market-backend), esta fase no
 * soporta asignación de grupos de destacamentos — solo asignación individual.
 */
@Component
public class PermisosEfectivosResolverImpl implements PermisosEfectivosResolver {

    private static final long CACHE_TTL_SEGUNDOS = 30;

    private final UsuarioRepository usuarioRepository;
    private final UsuarioDestacamentoRepository usuarioDestacamentoRepository;
    private final ConcurrentHashMap<Long, Entrada> cache = new ConcurrentHashMap<>();

    public PermisosEfectivosResolverImpl(
            UsuarioRepository usuarioRepository, UsuarioDestacamentoRepository usuarioDestacamentoRepository) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioDestacamentoRepository = usuarioDestacamentoRepository;
    }

    @Override
    public PermisosEfectivos resolver(Long usuarioId) {
        Entrada entrada = cache.get(usuarioId);
        Instant ahora = Instant.now();
        if (entrada != null && entrada.expiraEn.isAfter(ahora)) {
            return entrada.valor;
        }
        PermisosEfectivos calculado = calcular(usuarioId);
        cache.put(usuarioId, new Entrada(calculado, ahora.plusSeconds(CACHE_TTL_SEGUNDOS)));
        return calculado;
    }

    @Override
    public void invalidar(Long usuarioId) {
        cache.remove(usuarioId);
    }

    private PermisosEfectivos calcular(Long usuarioId) {
        String username = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + usuarioId))
                .getUsername();

        List<UsuarioDestacamento> asignaciones = usuarioDestacamentoRepository.findByUsuarioId(usuarioId);

        Set<String> permisos = new HashSet<>();
        Set<Long> destacamentoIds = new HashSet<>();
        boolean alcanceGlobal = false;

        for (UsuarioDestacamento asignacion : asignaciones) {
            Rol rol = asignacion.getRol();
            rol.getPermisos().forEach(p -> permisos.add(p.getCodigo()));
            // destacamentoId es nullable — una fila con destacamentoId=null es una
            // asignación de rol global sin destacamento (ver AdminUserSeeder), no
            // aporta ningún id concreto al conjunto de alcance.
            if (asignacion.getDestacamentoId() != null) {
                destacamentoIds.add(asignacion.getDestacamentoId());
            }
            if (rol.isAlcanceGlobal()) {
                alcanceGlobal = true;
            }
        }

        return new PermisosEfectivos(usuarioId, username, Set.copyOf(permisos), Set.copyOf(destacamentoIds), alcanceGlobal);
    }

    private record Entrada(PermisosEfectivos valor, Instant expiraEn) {
    }
}
