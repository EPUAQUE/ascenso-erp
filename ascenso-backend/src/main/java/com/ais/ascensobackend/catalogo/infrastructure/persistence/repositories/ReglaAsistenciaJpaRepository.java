package com.ais.ascensobackend.catalogo.infrastructure.persistence.repositories;

import com.ais.ascensobackend.catalogo.infrastructure.persistence.entities.ReglaAsistenciaEntity;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReglaAsistenciaJpaRepository extends JpaRepository<ReglaAsistenciaEntity, Long> {

    @Query("select r from ReglaAsistenciaEntity r where r.vigenteDesde <= :hoy order by r.vigenteDesde desc")
    List<ReglaAsistenciaEntity> findVigentesHastaOrdenDesc(@Param("hoy") LocalDate hoy);
}
