package com.ais.ascensobackend.catalogo.infrastructure.persistence.entities;

import com.ais.ascensobackend.catalogo.domain.model.UnidadAsistencia;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "regla_asistencia")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ReglaAsistenciaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "unidad", nullable = false, length = 12)
    private UnidadAsistencia unidad;

    @Column(name = "minimo_requerido", nullable = false, precision = 5, scale = 2)
    private BigDecimal minimoRequerido;

    @Column(name = "vigente_desde", nullable = false)
    private LocalDate vigenteDesde;
}
