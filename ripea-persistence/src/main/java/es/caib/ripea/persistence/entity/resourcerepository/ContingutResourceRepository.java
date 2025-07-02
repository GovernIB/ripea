package es.caib.ripea.persistence.entity.resourcerepository;

import java.util.List;

import es.caib.ripea.persistence.base.repository.BaseRepository;
import es.caib.ripea.persistence.entity.resourceentity.ContingutResourceEntity;

public interface ContingutResourceRepository extends BaseRepository<ContingutResourceEntity, Long> {

	public List<ContingutResourceEntity> findByPareId(Long pareId);
}
