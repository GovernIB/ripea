/**
 * 
 */
package es.caib.ripea.core.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import es.caib.ripea.core.entity.EntitatEntity;
import es.caib.ripea.core.entity.MetaDocumentEntity;
import es.caib.ripea.core.entity.MetaExpedientEntity;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus meta-document.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface MetaDocumentRepository extends JpaRepository<MetaDocumentEntity, Long> {

	MetaDocumentEntity findByMetaExpedientAndCodi(
			MetaExpedientEntity metaExpedient,
			String codi);

	List<MetaDocumentEntity> findByMetaExpedient(
			MetaExpedientEntity metaExpedient);
	Page<MetaDocumentEntity> findByMetaExpedient(
			MetaExpedientEntity metaExpedient,
			Pageable pageable);
	List<MetaDocumentEntity> findByMetaExpedient(
			MetaExpedientEntity metaExpedient,
			Sort sort);

	List<MetaDocumentEntity> findByMetaExpedientIdIn(
			List<Long> metaExpedientIds);

	List<MetaDocumentEntity> findByEntitat(
			EntitatEntity entitat);

}
