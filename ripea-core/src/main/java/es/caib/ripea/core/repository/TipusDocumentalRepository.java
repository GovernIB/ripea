/**
 * 
 */
package es.caib.ripea.core.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import es.caib.ripea.core.entity.EntitatEntity;
import es.caib.ripea.core.entity.TipusDocumentalEntity;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus meta-document.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface TipusDocumentalRepository extends JpaRepository<TipusDocumentalEntity, Long> {

	List<TipusDocumentalEntity> findByEntitatOrderByNomAsc(EntitatEntity entitat);
	Page<TipusDocumentalEntity> findByEntitat(
			EntitatEntity entitat, 
			Pageable pageable);
	TipusDocumentalEntity findByCodiAndEntitat(String codi, EntitatEntity entitat);
}
