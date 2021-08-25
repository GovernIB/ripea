/**
 * 
 */
package es.caib.ripea.core.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.ripea.core.api.dto.DocumentEnviamentEstatEnumDto;
import es.caib.ripea.core.entity.DocumentEntity;
import es.caib.ripea.core.entity.DocumentPortafirmesEntity;
import es.caib.ripea.core.entity.EntitatEntity;

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
	
	
	
	@Query(	"from " +
			"    DocumentPortafirmesEntity dp " +
			"where " +
			"    (dp.document.entitat = :entitat) " +
			"and (:esNullExpedientNom = true or lower(dp.expedient.nom) like lower('%'||:expedientNom||'%')) " +
			"and (:esNullDocumentNom = true or lower(dp.document.nom) like lower('%'||:documentNom||'%'))" +
			"and (:esNullDataEnviamentInici = true or dp.enviatData >= :dataEnviamentInici) " +
			"and (:esNullDataEnviamentFinal = true or dp.enviatData <= :dataEnviamentFinal) " +
			"and (:esNullEstatEnviament = true or dp.estat = :estatEnviament) ")
	public Page<DocumentPortafirmesEntity> findAmbFiltrePaginat(
			@Param("entitat") EntitatEntity entitat,
			@Param("esNullExpedientNom") boolean esNullExpedientNom,
			@Param("expedientNom") String expedientNom,
			@Param("esNullDocumentNom") boolean esNullDocumentNom,
			@Param("documentNom") String documentNom,
			@Param("esNullDataEnviamentInici") boolean esNullDataEnviamentInici,
			@Param("dataEnviamentInici") Date dataEnviamentInici,
			@Param("esNullDataEnviamentFinal") boolean esNullDataEnviamentFinal,
			@Param("dataEnviamentFinal") Date dataEnviamentFinal,
			@Param("esNullEstatEnviament") boolean esNullEstatEnviament,
			@Param("estatEnviament") DocumentEnviamentEstatEnumDto estatEnviament,
			Pageable paginacio);
	
	
	
	
}
