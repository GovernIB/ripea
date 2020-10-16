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
import es.caib.ripea.core.api.dto.DocumentNotificacioEstatEnumDto;
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

	List<DocumentEntity> findByExpedientAndEstatAndEsborrat(
			ExpedientEntity expedient,
			DocumentEstatEnumDto estat,
			int esborrat);

	int countByExpedient(ExpedientEntity expedient);

	int countByExpedientAndEstat(
			ExpedientEntity expedient,
			DocumentEstatEnumDto estat);


	@Query(	" SELECT " +
			"    count(d) " +
			" FROM " +
			"    DocumentEntity d JOIN d.notificacions n " +
			" WHERE " +
			"        d.expedient = :expedient " +
			"    AND n.notificacioEstat IN :estat ")
	long countByExpedientAndNotificacionsNotificacioEstatIn(
			@Param("expedient") ExpedientEntity expedient,
			@Param("estat") DocumentNotificacioEstatEnumDto[] estats);
	
	@Query(	"select " +
			"    c " +
			"from " +
			"    DocumentEntity c " +
			"where " +
			"    c.entitat = :entitat " +
			"and c.expedient = :expedient "  + 
			"and c.documentTipus != 2 " +
			"and c.esborrat = 0 " +
			"and c.id != :documentId) ")
	List<DocumentEntity> findByExpedientAndTipus(
			@Param("entitat") EntitatEntity entitat,
			@Param("expedient") ExpedientEntity expedient,
			@Param("documentId") Long documentId);

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
	public List<DocumentEntity> findDocumentMassiuByIds(
			@Param("ids") List<Long> ids);

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
			"    d " +
			"from " +
			"    DocumentEntity d " +
			"where " +
			"    d.entitat = :entitat " +
			"and (d.expedient.metaNode in (:metaExpedientsPermesos)) " +
			"and d.estat = 0 "  + 
			"and d.esborrat = 0 " + 
			"and d.documentTipus != 2 and d.documentTipus != 3 " +
			"and (:esNullMetaExpedient = true or d.expedient.metaNode = :metaExpedient) " +
			"and (:esNullExpedient = true or d.expedient = :expedient) " +
			"and (:esNullMetaDocument = true or d.metaNode = :metaDocument) " +
			"and (:esNullNom = true or lower(d.nom) like lower('%'||:nom||'%')) " +
			"and (:esNullDataInici = true or d.createdDate >= :dataInici) " +
			"and (:esNullDataFi = true or d.createdDate <= :dataFi) " +
			"and (d.metaNode.id in " + 
			"			(select metaDocument.id from MetaDocumentEntity metaDocument " +
			"				where metaDocument.firmaPortafirmesActiva = 1" + 
			"				and (metaDocument.portafirmesResponsables != null or metaDocument.portafirmesFluxId != null)))")
	public Page<DocumentEntity> findDocumentsPerFirmaMassiu(
			@Param("entitat") EntitatEntity entitat,
			@Param("metaExpedientsPermesos") List<? extends MetaNodeEntity> metaExpedientsPermesos,
			@Param("esNullMetaExpedient") boolean esNullMetaExpedient,
			@Param("metaExpedient") MetaNodeEntity metaExpedient,	
			@Param("esNullExpedient") boolean esNullExpedient,
			@Param("expedient") ExpedientEntity expedient,
			@Param("esNullMetaDocument") boolean esNullMetaDocument,
			@Param("metaDocument") MetaNodeEntity metaDocument,
			@Param("esNullNom") boolean esNullNom,
			@Param("nom") String nom,
			@Param("esNullDataInici") boolean esNullDataInici,
			@Param("dataInici") Date dataInici,
			@Param("esNullDataFi") boolean esNullDataFi,
			@Param("dataFi") Date dataFi,
			Pageable pageable);
	
	
	@Query(	"select " +
			"    d.id " +
			"from " +
			"    DocumentEntity d " +
			"where " +
			"    d.entitat = :entitat " +
			"and (d.expedient.metaNode in (:metaExpedientsPermesos)) " +
			"and d.estat = 0 "  + 
			"and d.esborrat = 0 " + 
			"and d.documentTipus != 2 and d.documentTipus != 3 " +
			"and (:esNullMetaExpedient = true or d.expedient.metaNode = :metaExpedient) " +
			"and (:esNullExpedient = true or d.expedient = :expedient) " +
			"and (:esNullMetaDocument = true or d.metaNode = :metaDocument) " +
			"and (:esNullNom = true or lower(d.nom) like lower('%'||:nom||'%')) " +
			"and (:esNullDataInici = true or d.createdDate >= :dataInici) " +
			"and (:esNullDataFi = true or d.createdDate <= :dataFi) " +
			"and (d.metaNode.id in " + 
			"			(select metaDocument.id from MetaDocumentEntity metaDocument " +
			"				where metaDocument.firmaPortafirmesActiva = 1" + 
			"				and (metaDocument.portafirmesResponsables != null or metaDocument.portafirmesFluxId != null)))")
	public List<Long> findIdsDocumentsPerFirmaMassiu(
			@Param("entitat") EntitatEntity entitat,
			@Param("metaExpedientsPermesos") List<? extends MetaNodeEntity> metaExpedientsPermesos,
			@Param("esNullMetaExpedient") boolean esNullMetaExpedient,
			@Param("metaExpedient") MetaNodeEntity metaExpedient,	
			@Param("esNullExpedient") boolean esNullExpedient,
			@Param("expedient") ExpedientEntity expedient,
			@Param("esNullMetaDocument") boolean esNullMetaDocument,
			@Param("metaDocument") MetaNodeEntity metaDocument,
			@Param("esNullNom") boolean esNullNom,
			@Param("nom") String nom,
			@Param("esNullDataInici") boolean esNullDataInici,
			@Param("dataInici") Date dataInici,
			@Param("esNullDataFi") boolean esNullDataFi,
			@Param("dataFi") Date dataFi);

}
