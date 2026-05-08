package es.caib.ripea.persistence.entity.resourcerepository;

import java.util.List;

import es.caib.ripea.persistence.base.repository.BaseRepository;
import es.caib.ripea.persistence.entity.resourceentity.ContingutResourceEntity;
import es.caib.ripea.service.intf.model.ContingutResource;

public interface ContingutResourceRepository extends BaseRepository<ContingutResourceEntity<ContingutResource>, Long> {

	public List<ContingutResourceEntity> findByPareId(Long pareId);
    List<ContingutResourceEntity<ContingutResource>> findAllByPareIdAndEsborratOrderByOrdreAsc(Long pareId, Integer esborrat);
}
