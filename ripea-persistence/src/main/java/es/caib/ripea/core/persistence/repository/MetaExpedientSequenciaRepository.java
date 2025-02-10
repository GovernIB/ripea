/**
 * 
 */
package es.caib.ripea.core.persistence.repository;

import es.caib.ripea.core.persistence.entity.MetaExpedientEntity;
import es.caib.ripea.core.persistence.entity.MetaExpedientSequenciaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus meta-expedient-seqüencia.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface MetaExpedientSequenciaRepository extends JpaRepository<MetaExpedientSequenciaEntity, Long> {

	MetaExpedientSequenciaEntity findByMetaExpedientAndAny(
			MetaExpedientEntity metaExpedient,
			int any);

}
