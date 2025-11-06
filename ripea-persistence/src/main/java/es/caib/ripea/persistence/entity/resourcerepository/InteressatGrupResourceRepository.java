package es.caib.ripea.persistence.entity.resourcerepository;

import java.util.List;

import es.caib.ripea.persistence.base.repository.BaseRepository;
import es.caib.ripea.persistence.entity.resourceentity.ExpedientResourceEntity;
import es.caib.ripea.persistence.entity.resourceentity.InteressatGrupResourceEntity;

/**
 * Repositori per gestionar un grup d'interessats.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface InteressatGrupResourceRepository extends BaseRepository<InteressatGrupResourceEntity, Long> {
	
    List<InteressatGrupResourceEntity> findByExpedient(ExpedientResourceEntity expedient);
    
}