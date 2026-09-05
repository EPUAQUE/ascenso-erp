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
@Table(name = "asistencia")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class AsistenciaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nino_id", nullable = false)
    private Long ninoId;

    @Column(name = "trimestre_id", nullable = false)
    private Long trimestreId;

    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @Column(name = "presente", nullable = false)
    private boolean presente;

    @Column(name = "registrado_por", nullable = false)
    private Long registradoPor;
}
