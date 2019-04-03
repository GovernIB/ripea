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

import es.caib.ripea.core.api.dto.DocumentEstatEnumDto;
import es.caib.ripea.core.entity.DocumentEntity;
import es.caib.ripea.core.entity.EntitatEntity;
import es.caib.ripea.core.entity.ExpedientEntity;
import es.caib.ripea.core.entity.MetaNodeEntity;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus document.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface DocumentRepository extends JpaRepository<DocumentEntity, Long> {

	List<DocumentEntity> findByExpedient(ExpedientEntity expedient);

	List<DocumentEntity> findByExpedientAndEstat(
			ExpedientEntity expedient,
			DocumentEstatEnumDto estat);

	List<DocumentEntity> findByExpedientAndEsborrat(
			ExpedientEntity expedient,
			int esborrat);

	List<DocumentEntity> findByEntitat(EntitatEntity entitat);
	
	List<DocumentEntity> findByExpedientAndMetaNodeAndEsborrat(
			ExpedientEntity expedient,
			MetaNodeEntity metaNode,
			int esborrat);

	@Query(	"select " +
			"    c " +
			"from " +
			"    DocumentEntity c " +
			"where " +
			"    c.id in (:ids)")
	public Page<DocumentEntity> findDocumentMassiuByIdsPaginat(
			@Param("ids") List<Long> ids,
			Pageable pageable);
	
	
	@Query(	"select " +
			"    c " +
			"from " +
			"    DocumentEntity c " +
			"where " +
			"    c.entitat = :entitat " +
			"and c.estat = 0 " +
			"and (:esNullMetaExpedientId = true or c.expedient.metaNode.id = :metaExpedientId) " +
			"and (:esNullExpedientId = true or c.expedient.id = :expedientId) " +
			"and (:esNullMetaDocumentId = true or c.metaNode.id = :metaDocumentId) " +
			"and (:esNullNom = true or lower(c.nom) like lower('%'||:nom||'%')) " +
			"and (:esNullDataInici = true or c.createdDate >= :dataInici) " +
			"and (:esNullDataFi = true or c.createdDate <= :dataFi) " +
			"and ((:mostrarEsborrats = true and c.esborrat > 0) or (:mostrarNoEsborrats = true and c.esborrat = 0)) ")
	public List<DocumentEntity> findDocumentMassiuByFiltre(
			@Param("entitat") EntitatEntity entitat,
			@Param("esNullMetaExpedientId") boolean esNullMetaExpedientId,
			@Param("metaExpedientId") Long metaExpedientId,
			@Param("esNullExpedientId") boolean esNullExpedientId,
			@Param("expedientId") Long expedientId,
			@Param("esNullMetaDocumentId") boolean esNullMetaDocumentId,
			@Param("metaDocumentId") Long metaDocumentId,
			@Param("esNullNom") boolean esNullNom,
			@Param("nom") String nom,
			@Param("esNullDataInici") boolean esNullDataInici,
			@Param("dataInici") Date dataInici,
			@Param("esNullDataFi") boolean esNullDataFi,
			@Param("dataFi") Date dataFi,
			@Param("mostrarEsborrats") boolean mostrarEsborrats,
			@Param("mostrarNoEsborrats") boolean mostrarNoEsborrats);
	
	
	@Query(	"select " +
			"    c.id " +
			"from " +
			"    DocumentEntity c " +
			"where " +
			"    c.entitat = :entitat " +
			"and (:esNullTipusExpedient = true or c.expedient.metaNode.id = :tipusExpedientId) " +
			"and (:esNullExpedient = true or c.expedient.id = :expedientId) " +
			"and (:esNullTipusDocument = true or c.metaNode.id = :tipusDocumentId) " +
			"and (:esNullNom = true or lower(c.nom) like lower('%'||:nom||'%')) " +
			"and (:esNullDataInici = true or c.lastModifiedDate >= :dataInici) " +
			"and (:esNullDataFi = true or c.lastModifiedDate <= :dataFi) " +
			"and ((:mostrarEsborrats = true and c.esborrat > 0) or (:mostrarNoEsborrats = true and c.esborrat = 0)) ")
	public List<Long> findIdMassiuByEntitatAndFiltre(
			@Param("entitat") EntitatEntity entitat,
			@Param("esNullTipusExpedient") boolean esNullTipusExpedient,
			@Param("tipusExpedientId") Long tipusExpedientId,
			@Param("esNullExpedient") boolean esNullExpedient,
			@Param("expedientId") Long expedientId,
			@Param("esNullTipusDocument") boolean esNullTipusDocument,
			@Param("tipusDocumentId") Long tipusDocumentId,
			@Param("esNullNom") boolean esNullNom,
			@Param("nom") String nom,
			@Param("esNullDataInici") boolean esNullDataInici,
			@Param("dataInici") Date dataInici,
			@Param("esNullDataFi") boolean esNullDataFi,
			@Param("dataFi") Date dataFi,
			@Param("mostrarEsborrats") boolean mostrarEsborrats,
			@Param("mostrarNoEsborrats") boolean mostrarNoEsborrats);
}
