package es.caib.ripea.persistence.repository;

import es.caib.ripea.persistence.entity.EntitatEntity;
import es.caib.ripea.persistence.entity.MetaNodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface MetaNodeRepository extends JpaRepository<MetaNodeEntity, Long> {
	List<MetaNodeEntity> findByEntitat(EntitatEntity entitat);
}