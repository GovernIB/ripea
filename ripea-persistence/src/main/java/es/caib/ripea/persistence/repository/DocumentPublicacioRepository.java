/**
 * 
 */
package es.caib.ripea.persistence.repository;

import es.caib.ripea.persistence.entity.DocumentEntity;
import es.caib.ripea.persistence.entity.DocumentPublicacioEntity;
import es.caib.ripea.persistence.entity.ExpedientEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus documentPublicacio.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface DocumentPublicacioRepository extends JpaRepository<DocumentPublicacioEntity, Long> {

	List<DocumentPublicacioEntity> findByExpedientOrderByEnviatDataAsc(
			ExpedientEntity expedient);

	List<DocumentPublicacioEntity> findByDocumentOrderByEnviatDataAsc(
			DocumentEntity document);

}
