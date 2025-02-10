/**
 * 
 */
package es.caib.ripea.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import es.caib.ripea.core.api.dto.DocumentEnviamentEstatEnumDto;
import es.caib.ripea.core.persistence.DocumentEntity;
import es.caib.ripea.core.persistence.DocumentViaFirmaEntity;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus document-portafirmes.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface DocumentViaFirmaRepository extends JpaRepository<DocumentViaFirmaEntity, Long> {

	List<DocumentViaFirmaEntity> findByDocument(DocumentEntity document);

	List<DocumentViaFirmaEntity> findByDocumentAndEstatInOrderByCreatedDateDesc(
			DocumentEntity document,
			DocumentEnviamentEstatEnumDto[] estat);

	List<DocumentViaFirmaEntity> findByDocumentAndEstatInAndErrorOrderByCreatedDateDesc(
			DocumentEntity document,
			DocumentEnviamentEstatEnumDto[] estat,
			boolean error);

	DocumentViaFirmaEntity findByMessageCode(
			String messageCode);

}
