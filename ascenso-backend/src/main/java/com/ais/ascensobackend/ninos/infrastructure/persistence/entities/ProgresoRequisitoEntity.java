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
@Table(name = "progreso_requisito")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ProgresoRequisitoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nino_id", nullable = false)
    private Long ninoId;

    @Column(name = "paso_requerido_id", nullable = false)
    private Long pasoRequeridoId;

    @Column(name = "fecha_completado", nullable = false)
    private LocalDate fechaCompletado;

    @Column(name = "registrado_por", nullable = false)
    private Long registradoPor;
}
