package com.ais.ascensobackend.seguridad.infrastructure.security;

import com.ais.ascensobackend.seguridad.application.dtos.UsuarioResumen;
import com.ais.ascensobackend.seguridad.application.services.interfaces.UsuarioService;
import com.ais.ascensobackend.seguridad.domain.model.Rol;
import com.ais.ascensobackend.seguridad.domain.repository.RolRepository;
import com.ais.ascensobackend.seguridad.domain.repository.UsuarioRepository;
import com.ais.ascensobackend.seguridad.domain.service.UsernameCanonicalizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * Conveniencia de desarrollo/local: crea un usuario SUPERVISOR_GENERAL si el
 * catálogo de usuarios está vacío. A diferencia de market-backend (que asigna su
 * admin sembrado a una tienda "CENTRAL" fija), este seed NO asigna ningún
 * destacamento — el rol {@code SUPERVISOR_GENERAL} es de alcance global
 * ({@code alcanceGlobal=true}) y no necesita ninguna fila en
 * {@code usuario_destacamento} para resolver sus permisos (ver
 * {@code PermisosEfectivosResolverImpl}). Desactivable con
 * {@code app.seed.enabled=false} (obligatorio en producción).
 */
@Component
public class AdminUserSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminUserSeeder.class);
    private static final String ROL_SUPERVISOR_GENERAL = "SUPERVISOR_GENERAL";

    private final SeedProperties seedProperties;
    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final UsuarioService usuarioService;

    public AdminUserSeeder(
            SeedProperties seedProperties,
            UsuarioRepository usuarioRepository,
            RolRepository rolRepository,
            UsuarioService usuarioService) {
        this.seedProperties = seedProperties;
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.usuarioService = usuarioService;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!seedProperties.enabled()) {
            return;
        }
        String usernameCanonico = UsernameCanonicalizer.canonicalizar(seedProperties.adminUsername());
        if (usuarioRepository.existsByUsername(usernameCanonico)) {
            return;
        }

        Rol rolSupervisorGeneral = rolRepository.findByNombre(ROL_SUPERVISOR_GENERAL)
                .orElseThrow(() -> new IllegalStateException(
                        "Rol " + ROL_SUPERVISOR_GENERAL + " no encontrado; revisar migraciones."));
        UsuarioResumen admin = usuarioService.crear(
                usernameCanonico, seedProperties.adminPassword(), "Supervisor General", null, null);
        // Sin esto, PermisosEfectivosResolverImpl no tendría ninguna fila de la que
        // resolver el rol del usuario — ver Javadoc de
        // UsuarioService.asignarRolGlobalSistema.
        usuarioService.asignarRolGlobalSistema(admin.id(), rolSupervisorGeneral.getId());

        log.warn(
                "Usuario SUPERVISOR_GENERAL de desarrollo creado: {}. Deshabilite app.seed.enabled en producción.",
                usernameCanonico);
    }
}
