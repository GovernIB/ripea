package es.caib.ripea.persistence.repository;

import es.caib.ripea.persistence.entity.ExpedientEstatEntity;
import es.caib.ripea.persistence.entity.MetaExpedientEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface ExpedientEstatRepository extends JpaRepository<ExpedientEstatEntity, Long> {

	Page<ExpedientEstatEntity> findByMetaExpedientOrderByOrdreAsc(MetaExpedientEntity metaExpedient, Pageable pageable);
	
	List<ExpedientEstatEntity> findByMetaExpedientOrderByOrdreAsc(MetaExpedientEntity metaExpedient);
	
	List<ExpedientEstatEntity> findByMetaExpedientIdOrderByOrdreAsc(Long metaExpedientId);
	
	ExpedientEstatEntity findByMetaExpedientAndOrdre(MetaExpedientEntity metaExpedient, int ordre);
	
	ExpedientEstatEntity findByMetaExpedientAndCodi(MetaExpedientEntity metaExpedient, String codi);
	
	int countByMetaExpedient(MetaExpedientEntity metaExpedient);	
}