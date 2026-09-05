package com.ais.ascensobackend.actividades.infrastructure.persistence.entities;

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
@Table(name = "anuncio")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class AnuncioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "titulo", nullable = false, length = 150)
    private String titulo;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "imagen_url", length = 255)
    private String imagenUrl;

    @Column(name = "enlace_url", length = 255)
    private String enlaceUrl;

    @Column(name = "fecha_inicio_visible", nullable = false)
    private LocalDate fechaInicioVisible;

    @Column(name = "fecha_fin_visible")
    private LocalDate fechaFinVisible;

    @Column(name = "activo", nullable = false)
    private boolean activo;

    @Column(name = "creado_por", nullable = false)
    private Long creadoPor;
}
