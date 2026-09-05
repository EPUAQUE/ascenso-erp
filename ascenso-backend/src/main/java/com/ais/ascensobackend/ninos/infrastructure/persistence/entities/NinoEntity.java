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
@Table(name = "nino")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class NinoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "destacamento_id", nullable = false)
    private Long destacamentoId;

    @Column(name = "nombre_completo", nullable = false, length = 150)
    private String nombreCompleto;

    @Column(name = "fecha_nacimiento", nullable = false)
    private LocalDate fechaNacimiento;

    @Column(name = "foto_url", length = 255)
    private String fotoUrl;

    @Column(name = "encargado_nombre", nullable = false, length = 150)
    private String encargadoNombre;

    @Column(name = "encargado_contacto", nullable = false, length = 50)
    private String encargadoContacto;

    @Column(name = "contacto_emergencia", length = 50)
    private String contactoEmergencia;

    @Column(name = "fecha_ingreso", nullable = false)
    private LocalDate fechaIngreso;

    @Column(name = "grupo_actual_id", nullable = false)
    private Long grupoActualId;

    @Column(name = "anio_programa_actual_id", nullable = false)
    private Long anioProgramaActualId;

    @Column(name = "activo", nullable = false)
    private boolean activo;
}
