package es.caib.ripea.persistence.entity.resourcerepository;

import java.util.Optional;

import org.springframework.data.repository.query.Param;

import es.caib.ripea.persistence.base.repository.BaseRepository;
import es.caib.ripea.persistence.entity.resourceentity.MetaExpedientOrganGestorResourceEntity;
import es.caib.ripea.persistence.entity.resourceentity.MetaExpedientResourceEntity;
import es.caib.ripea.persistence.entity.resourceentity.OrganGestorResourceEntity;

public interface MetaExpedientOrganGestorResourceRepository extends BaseRepository<MetaExpedientOrganGestorResourceEntity, Long> {
	
	Optional<MetaExpedientOrganGestorResourceEntity> findByMetaExpedientAndOrganGestor(
			@Param("metaExpedient") MetaExpedientResourceEntity metaExpedient,
			@Param("organGestor") OrganGestorResourceEntity organGestor);
}