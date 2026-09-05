package com.ais.ascensobackend.actividades.infrastructure.persistence.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "nino_padre")
@IdClass(NinoPadreId.class)
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class NinoPadreEntity {

    @Id
    @Column(name = "nino_id")
    private Long ninoId;

    @Id
    @Column(name = "usuario_id")
    private Long usuarioId;
}
