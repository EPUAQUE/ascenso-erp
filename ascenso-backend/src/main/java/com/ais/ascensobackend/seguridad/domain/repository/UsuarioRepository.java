package com.ais.ascensobackend.seguridad.domain.repository;

import com.ais.ascensobackend.seguridad.domain.model.Usuario;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UsuarioRepository {

    Usuario save(Usuario usuario);

    Optional<Usuario> findById(Long id);

    /**
     * Igual que {@link #findById}, pero bloquea la fila con {@code PESSIMISTIC_WRITE}
     * dentro de la transacción actual — usado por
     * {@code UsuarioServiceImpl.asignarDestacamento} para evitar condiciones de
     * carrera al validar el usuario antes de asignarle un destacamento.
     */
    Optional<Usuario> findByIdConBloqueo(Long id);

    Optional<Usuario> findByUsername(String username);

    boolean existsByUsername(String username);

    List<Usuario> findAll();

    List<Usuario> findAllById(Collection<Long> ids);
}
