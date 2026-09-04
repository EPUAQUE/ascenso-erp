package com.ais.ascensobackend.destacamentos.infrastructure.persistence.entities;

import com.ais.ascensobackend.destacamentos.domain.model.ModoCorteAnio;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "destacamento")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class DestacamentoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_unico", nullable = false, unique = true, length = 20)
    private String numeroUnico;

    @Column(name = "nombre", nullable = false, length = 150)
    private String nombre;

    @Column(name = "iglesia_nombre", nullable = false, length = 150)
    private String iglesiaNombre;

    @Column(name = "direccion", length = 255)
    private String direccion;

    @Enumerated(EnumType.STRING)
    @Column(name = "modo_corte_anio", nullable = false, length = 20)
    private ModoCorteAnio modoCorteAnio;

    @Column(name = "activo", nullable = false)
    private boolean activo;

    @Column(name = "creado_en", nullable = false)
    private Instant creadoEn;
}
