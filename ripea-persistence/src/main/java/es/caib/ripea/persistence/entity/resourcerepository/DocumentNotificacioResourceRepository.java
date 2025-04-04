package es.caib.ripea.persistence.entity.resourcerepository;

import java.util.List;

import es.caib.ripea.persistence.base.repository.BaseRepository;
import es.caib.ripea.persistence.entity.resourceentity.DocumentNotificacioResourceEntity;
import es.caib.ripea.persistence.entity.resourceentity.DocumentResourceEntity;

public interface DocumentNotificacioResourceRepository extends BaseRepository<DocumentNotificacioResourceEntity, Long> {
	
	List<DocumentNotificacioResourceEntity> findByDocumentOrderByCreatedDateDesc(DocumentResourceEntity document);
}