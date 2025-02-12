package es.caib.ripea.persistence.repository;

import es.caib.ripea.persistence.entity.ExpedientEstatEntity;
import es.caib.ripea.persistence.entity.MetaExpedientEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExpedientEstatRepository extends JpaRepository<ExpedientEstatEntity, Long> {

	Page<ExpedientEstatEntity> findByMetaExpedientOrderByOrdreAsc(MetaExpedientEntity metaExpedient, Pageable pageable);
	
	List<ExpedientEstatEntity> findByMetaExpedientOrderByOrdreAsc(MetaExpedientEntity metaExpedient);
	
	ExpedientEstatEntity findByMetaExpedientAndOrdre(MetaExpedientEntity metaExpedient, int ordre);
	
	ExpedientEstatEntity findByMetaExpedientAndCodi(MetaExpedientEntity metaExpedient, String codi);
	
	int countByMetaExpedient(MetaExpedientEntity metaExpedient);	
}