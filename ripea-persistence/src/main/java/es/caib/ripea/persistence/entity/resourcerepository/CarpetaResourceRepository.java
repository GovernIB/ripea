package es.caib.ripea.persistence.entity.resourcerepository;

import es.caib.ripea.persistence.base.repository.BaseRepository;
import es.caib.ripea.persistence.entity.resourceentity.CarpetaResourceEntity;

import java.util.List;

public interface CarpetaResourceRepository extends BaseRepository<CarpetaResourceEntity, Long> {

	List<CarpetaResourceEntity> findAllByPareIdOrderByOrdreAsc(Long pareId);

}