package es.caib.ripea.persistence.repository;

import es.caib.ripea.persistence.entity.ProcesosInicialsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProcessosInicialsRepository extends JpaRepository<ProcesosInicialsEntity, String> {

    List<ProcesosInicialsEntity> findProcesosInicialsEntityByInitTrue();

    @Modifying
    @Query("update ProcesosInicialsEntity p set p.init = :init where p.id = :id")
    void updateInit(@Param("id") Long id, @Param("init") boolean init);

}