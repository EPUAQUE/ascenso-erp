package com.ais.ascensobackend.catalogo.infrastructure.persistence.entities;

import com.ais.ascensobackend.catalogo.domain.model.Medalla;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "anio_programa")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class AnioProgramaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "grupo_id", nullable = false)
    private Long grupoId;

    @Column(name = "numero", nullable = false)
    private short numero;

    @Enumerated(EnumType.STRING)
    @Column(name = "medalla", length = 10)
    private Medalla medalla;

    @Column(name = "minimo_libros", nullable = false)
    private short minimoLibros;

    @Column(name = "minimo_destrezas", nullable = false)
    private short minimoDestrezas;

    @Column(name = "minimo_liderazgo", nullable = false)
    private short minimoLiderazgo;

    @Column(name = "es_anio_gracia", nullable = false)
    private boolean esAnioGracia;
}
