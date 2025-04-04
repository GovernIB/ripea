package es.caib.ripea.persistence.entity.resourcerepository;

import java.util.List;

import es.caib.ripea.persistence.base.repository.BaseRepository;
import es.caib.ripea.persistence.entity.resourceentity.DocumentPortafirmesResourceEntity;
import es.caib.ripea.persistence.entity.resourceentity.DocumentResourceEntity;
import es.caib.ripea.service.intf.dto.DocumentEnviamentEstatEnumDto;

public interface DocumentPortafirmesResourceRepository extends BaseRepository<DocumentPortafirmesResourceEntity, Long> {

	List<DocumentPortafirmesResourceEntity> findByDocumentAndEstatInAndErrorOrderByCreatedDateAsc(
			DocumentResourceEntity document,
			DocumentEnviamentEstatEnumDto[] estat,
			boolean error);
	
	List<DocumentPortafirmesResourceEntity> findByDocumentAndEstatInOrderByCreatedDateDesc(
			DocumentResourceEntity document,
			DocumentEnviamentEstatEnumDto[] estat);
}
