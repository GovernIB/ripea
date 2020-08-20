/**
 * 
 */
package es.caib.ripea.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import es.caib.ripea.core.api.dto.DocumentEnviamentEstatEnumDto;
import es.caib.ripea.core.entity.DocumentEntity;
import es.caib.ripea.core.entity.DocumentPortafirmesEntity;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus document-portafirmes.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface DocumentPortafirmesRepository extends JpaRepository<DocumentPortafirmesEntity, Long> {

	List<DocumentPortafirmesEntity> findByDocument(DocumentEntity document);

	List<DocumentPortafirmesEntity> findByDocumentAndEstatInOrderByCreatedDateDesc(
			DocumentEntity document,
			DocumentEnviamentEstatEnumDto[] estat);

	List<DocumentPortafirmesEntity> findByDocumentAndEstatInAndErrorOrderByCreatedDateDesc(
			DocumentEntity document,
			DocumentEnviamentEstatEnumDto[] estat,
			boolean error);
	
	DocumentPortafirmesEntity findByPortafirmesId(
			String portafirmesId);

	List<DocumentPortafirmesEntity> findByDocumentOrderByCreatedDateDesc(DocumentEntity document);

	List<DocumentPortafirmesEntity> findByDocumentAndEstatInAndErrorOrderByCreatedDateAsc(
			DocumentEntity document,
			DocumentEnviamentEstatEnumDto[] estat,
			boolean error);
}
