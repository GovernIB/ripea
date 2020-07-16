/**
 * 
 */
package es.caib.ripea.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
	
	/*List<DocumentPortafirmesEntity> findByDocumentAndEstatInOrderByCreatedDateDesc(
			DocumentEntity document,
			DocumentEnviamentEstatEnumDto[] estat);*/

	/*List<DocumentPortafirmesEntity> findByDocumentOrderByCreatedDateDesc(
			DocumentEntity document);*/

	

	DocumentPortafirmesEntity findByPortafirmesId(
			String portafirmesId);

	@Query(	"from" +
			"    DocumentPortafirmesEntity dpe " +
			"where" +
			"	dpe.document = :document " +
			"and" +
			"	rownum = 1"+
			"order by" +
			"	dpe.createdDate desc")
	DocumentPortafirmesEntity findTopByDocumentOrderByCreatedDateDesc(
			@Param("document") DocumentEntity document);
}
