package com.ais.ascensobackend.ninos.infrastructure.persistence.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "progreso_libro")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ProgresoLibroEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nino_id", nullable = false)
    private Long ninoId;

    @Column(name = "libro_biblico_id", nullable = false)
    private Long libroBiblicoId;

    @Column(name = "anio_programa_objetivo_id", nullable = false)
    private Long anioProgramaObjetivoId;

    @Column(name = "fecha_completado", nullable = false)
    private LocalDate fechaCompletado;

    @Column(name = "registrado_por", nullable = false)
    private Long registradoPor;
}
