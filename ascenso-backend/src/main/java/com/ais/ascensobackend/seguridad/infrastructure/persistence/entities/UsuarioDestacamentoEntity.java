package com.ais.ascensobackend.seguridad.infrastructure.persistence.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * {@code destacamento_id} es un identificador plano a nivel de Java (sin
 * {@code @ManyToOne} a una entidad Destacamento: ese agregado es propiedad del
 * módulo Destacamentos y Seguridad no debe depender de su infraestructura). La
 * restricción FK en base de datos sí existe — se agrega en
 * {@code destacamentos/001-destacamento.xml}, una vez que ese módulo publica su
 * changelog (mismo patrón "FK diferida" de market-backend).
 *
 * <p>{@code destacamento_id} es NULLABLE: una fila con este campo en {@code null}
 * representa la asignación de un rol de alcance global (ej. SUPERVISOR_GENERAL)
 * SIN destacamento — necesario porque {@code PermisosEfectivosResolverImpl} solo
 * puede descubrir qué rol(es) tiene un usuario iterando sus filas en esta tabla;
 * sin al menos una fila, un usuario de alcance global no tendría de dónde
 * resolver ningún permiso ni su propio {@code alcanceGlobal}. Ver
 * {@code AdminUserSeeder}/{@code UsuarioServiceImpl.asignarRolGlobalSistema}.
 */
@Entity
@Table(name = "usuario_destacamento")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UsuarioDestacamentoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Column(name = "destacamento_id")
    private Long destacamentoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rol_id", nullable = false)
    private RolEntity rol;
}
