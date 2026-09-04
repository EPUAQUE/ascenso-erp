package com.ais.ascensobackend.catalogo.infrastructure.persistence.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "libro_biblico")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class LibroBiblicoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "grupo_id", nullable = false)
    private Long grupoId;

    @Column(name = "titulo", nullable = false, length = 120)
    private String titulo;

    @Column(name = "orden_sugerido")
    private Short ordenSugerido;

    @Column(name = "activo", nullable = false)
    private boolean activo;
}
