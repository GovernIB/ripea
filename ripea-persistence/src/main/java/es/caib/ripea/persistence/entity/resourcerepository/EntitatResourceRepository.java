package es.caib.ripea.persistence.entity.resourcerepository;

import es.caib.ripea.persistence.base.repository.BaseRepository;
import es.caib.ripea.persistence.entity.resourceentity.EntitatResourceEntity;

import java.util.List;

/**
 * Repositori per a la gestió d'expedients.
 * 
 * @author Límit Tecnologies
 */
public interface EntitatResourceRepository extends BaseRepository<EntitatResourceEntity, Long> {

    EntitatResourceEntity findByCodi(String codi);
}
