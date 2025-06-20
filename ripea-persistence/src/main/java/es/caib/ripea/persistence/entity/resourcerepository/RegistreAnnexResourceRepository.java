package es.caib.ripea.persistence.entity.resourcerepository;

import java.util.List;

import es.caib.ripea.persistence.base.repository.BaseRepository;
import es.caib.ripea.persistence.entity.resourceentity.RegistreAnnexResourceEntity;

public interface RegistreAnnexResourceRepository extends BaseRepository<RegistreAnnexResourceEntity, Long> {
	List<RegistreAnnexResourceEntity> findByDocumentId(Long id);
}