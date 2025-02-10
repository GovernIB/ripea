/**
 * 
 */
package es.caib.ripea.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import es.caib.ripea.core.persistence.DocumentEntity;
import es.caib.ripea.core.persistence.DocumentPublicacioEntity;
import es.caib.ripea.core.persistence.ExpedientEntity;

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
