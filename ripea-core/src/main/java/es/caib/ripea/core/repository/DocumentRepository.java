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

import es.caib.ripea.core.aggregation.MetaExpedientCountAggregation;
import es.caib.ripea.core.api.dto.DocumentEstatEnumDto;
import es.caib.ripea.core.api.dto.DocumentNotificacioEstatEnumDto;
import es.caib.ripea.core.entity.ContingutEntity;
import es.caib.ripea.core.entity.DocumentEntity;
import es.caib.ripea.core.entity.EntitatEntity;
import es.caib.ripea.core.entity.ExpedientEntity;
import es.caib.ripea.core.entity.MetaExpedientEntity;
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
	
	
	@Query( "select   " +
			"    new es.caib.ripea.core.aggregation.MetaExpedientCountAggregation( " +
			"	     e.metaExpedient, " +
			"        count(d) " +
			"    ) " +
			"from     " +
	        "    DocumentEntity d JOIN d.expedient e " +
	        "where " +
	        "     d.estat = :estat " +
	        "group by" +
	        "     e.metaExpedient")
	List<MetaExpedientCountAggregation> countByEstatGroupByMetaExpedient(
			@Param("estat") DocumentEstatEnumDto estat);
	
	@Query(	" SELECT " +
			"    new es.caib.ripea.core.aggregation.MetaExpedientCountAggregation( " +
			"	     e.metaExpedient, " +
			"        count(d) " +
			"    ) " +
			" FROM " +
	        "    DocumentEntity d JOIN d.expedient e JOIN d.notificacions n " +
			" WHERE " +
			"     n.notificacioEstat IN :estat " +
	        "group by" +
	        "     e.metaExpedient")
	List<MetaExpedientCountAggregation> countByNotificacioEstatInGroupByMetaExpedient(
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
	
	@Query(	"select case when (count(c) > 0) then true else false end " +
			"from " +
			"    DocumentEntity c " +
			"where " +
			"    c.expedient = :expedient "  + 
			"and c.documentTipus = 0 " + //= DIGITAL
			"and c.esborrat = 0 " +
			"and c.estat = 0) ")
	Boolean hasFillsEsborranys(@Param("expedient") ExpedientEntity expedient);
	
	@Query(	"select case when (count(c) = 0) then true else false end " +
			"from " +
			"    DocumentEntity c " +
			"where " +
			"    c.expedient = :expedient "  + 
			"and c.documentTipus = 0 " + //= DIGITAL
			"and c.esborrat = 0 " +
			"and (c.estat = 0 or c.estat = 1 " + //!= REDACCIO || != FIRMA_PENDENT
			"or c.estat = 2 or c.estat = 4 " + //!= FIRMAT || != FIRMA_PENDENT_VIAFIRMA
			"or c.estat = 6 or c.estat = 7) ") //!= FIRMA_PARCIAL || != ADJUNT_FIRMAT
	Boolean hasAllDocumentsDefinitiu(@Param("expedient") ExpedientEntity expedient);

	@Query(	"select case when (count(c) > 0) then true else false end " +
			"from " +
			"    DocumentEntity c " +
			"where " +
			"    c.expedient = :expedient "  + 
			"and (c.documentTipus = 0 or c.documentTipus = 3)" + //= DIGITAL || = IMPORTAT
			"and c.esborrat = 0 " +
			"and (c.estat = 3 or c.estat = 5)) ") //= CUSTODIAT || = DEFINITIU
	Boolean hasAnyDocumentDefinitiu(@Param("expedient") ExpedientEntity expedient);
	
	List<DocumentEntity> findByEntitat(EntitatEntity entitat);
	
	List<DocumentEntity> findByExpedientAndMetaNodeAndEsborrat(
			ExpedientEntity expedient,
			MetaNodeEntity metaNode,
			int esborrat);
	
	List<DocumentEntity> findByMetaNode(
			MetaNodeEntity metaNode);
	
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
			"and d.gesDocAdjuntId is null " +
			"and d.documentTipus != 1 and d.documentTipus != 2 and d.documentTipus != 3 " +
			"and (:esNullMetaExpedient = true or d.expedient.metaNode = :metaExpedient) " +
			"and (:esNullExpedient = true or d.expedient = :expedient) " +
			"and (:esNullMetaDocument = true or d.metaNode = :metaDocument) " +
			"and (:esNullNom = true or lower(d.nom) like lower('%'||:nom||'%')) " +
			"and (:esNullDataInici = true or d.createdDate >= :dataInici) " +
			"and (:esNullDataFi = true or d.createdDate <= :dataFi) " +
			"and (d.metaNode.id in " + 
			"			(select metaDocument.id from MetaDocumentEntity metaDocument " +
			"				where metaDocument.firmaPortafirmesActiva = true))" )
//			"				and (metaDocument.portafirmesFluxTipus = 'PORTAFIB' and metaDocument.portafirmesFluxId != null)" +
//			"				or (metaDocument.portafirmesFluxTipus = 'SIMPLE' and metaDocument.portafirmesResponsables != null)))")
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
			"and d.gesDocAdjuntId is null " +
			"and d.documentTipus != 1 and d.documentTipus != 2 and d.documentTipus != 3 " +
			"and (:esNullMetaExpedient = true or d.expedient.metaNode = :metaExpedient) " +
			"and (:esNullExpedient = true or d.expedient = :expedient) " +
			"and (:esNullMetaDocument = true or d.metaNode = :metaDocument) " +
			"and (:esNullNom = true or lower(d.nom) like lower('%'||:nom||'%')) " +
			"and (:esNullDataInici = true or d.createdDate >= :dataInici) " +
			"and (:esNullDataFi = true or d.createdDate <= :dataFi) " +
			"and (d.metaNode.id in " + 
			"			(select metaDocument.id from MetaDocumentEntity metaDocument " +
			"				where metaDocument.firmaPortafirmesActiva = true))" )
//			"				and (metaDocument.portafirmesFluxTipus = 'PORTAFIB' and metaDocument.portafirmesFluxId != null)" +
//			"				or (metaDocument.portafirmesFluxTipus = 'SIMPLE' and metaDocument.portafirmesResponsables != null)))")
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
	
	
	
	
	
	
	
	
	@Query(	"select " +
			"    d " +
			"from " +
			"    DocumentEntity d " +
			"where " +
			"    d.entitat = :entitat " +
			"and (d.expedient.metaNode in (:metaExpedientsPermesos)) " +
			"and ((d.estat = 1 or d.estat = 2) or ((d.estat = 0 or d.estat = 7) and d.gesDocAdjuntId!=null)) "  + 
			"and d.esborrat = 0 " + 
			"and d.documentTipus = 0 " +
			"and (:nomesAgafats = false or d.expedient.agafatPer.codi = :usuariActual) " +
			"and (:esNullMetaExpedient = true or d.expedient.metaNode = :metaExpedient) " +
			"and (:esNullExpedientNom = true or lower(d.expedient.nom) like lower('%'||:expedientNom||'%')) " +
			"and (:esNullMetaDocument = true or d.metaNode = :metaDocument) " +
			"and (:esNullNom = true or lower(d.nom) like lower('%'||:nom||'%')) " +
			"and (:esNullDataInici = true or d.createdDate >= :dataInici) " +
			"and (:esNullDataFi = true or d.createdDate <= :dataFi) " +
			"and (d.id in " + 
			"			(select docPortafirmes.document.id from DocumentPortafirmesEntity docPortafirmes " +
			"				where (docPortafirmes.id, docPortafirmes.createdDate) in (select docPortaf.id, max(docPortaf.createdDate) from DocumentPortafirmesEntity docPortaf group by docPortaf.id) " +
			"				and docPortafirmes.estat = 'ENVIAT' " +
			"				and docPortafirmes.error = true) or d.gesDocAdjuntId!=null)")
	public Page<DocumentEntity> findDocumentsPerCustodiarMassiu(
			@Param("entitat") EntitatEntity entitat,
			@Param("metaExpedientsPermesos") List<? extends MetaNodeEntity> metaExpedientsPermesos,
			@Param("nomesAgafats") boolean nomesAgafats,
			@Param("usuariActual") String usuariActual,
			@Param("esNullMetaExpedient") boolean esNullMetaExpedient,
			@Param("metaExpedient") MetaNodeEntity metaExpedient,	
			@Param("esNullExpedientNom") boolean esNullExpedientNom,
			@Param("expedientNom") String expedientNom,
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
			"and (d.estat = 1 or d.estat = 2) "  + 
			"and d.esborrat = 0 " + 
			"and d.documentTipus = 0 " +
			"and (:nomesAgafats = false or d.expedient.agafatPer.codi = :usuariActual) " +
			"and (:esNullMetaExpedient = true or d.expedient.metaNode = :metaExpedient) " +
			"and (:esNullExpedient = true or d.expedient = :expedient) " +
			"and (:esNullMetaDocument = true or d.metaNode = :metaDocument) " +
			"and (:esNullNom = true or lower(d.nom) like lower('%'||:nom||'%')) " +
			"and (:esNullDataInici = true or d.createdDate >= :dataInici) " +
			"and (:esNullDataFi = true or d.createdDate <= :dataFi) " +
			"and (d.id in " + 
			"			(select docPortafirmes.document.id from DocumentPortafirmesEntity docPortafirmes " +
			"				where (docPortafirmes.id, docPortafirmes.createdDate) in (select docPortaf.id, max(docPortaf.createdDate) from DocumentPortafirmesEntity docPortaf group by docPortaf.id) " +
			"				and docPortafirmes.estat = 'ENVIAT' " +
			"				and docPortafirmes.error = 1))")
	public List<Long> findDocumentsIdsPerCustodiarMassiu(
			@Param("entitat") EntitatEntity entitat,
			@Param("metaExpedientsPermesos") List<? extends MetaNodeEntity> metaExpedientsPermesos,
			@Param("nomesAgafats") boolean nomesAgafats,
			@Param("usuariActual") String usuariActual,
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
	
	public List<DocumentEntity> findByArxiuUuidAndEsborrat(String arxiuUuid, int esborrat);
	
	public List<DocumentEntity> findByPareAndEsborrat(
			ContingutEntity pare, 
			int esborrat);
//	@Query(	"select " +
//			"    d " +
//			"from " +
//			"    DocumentEntity d " +
//			"where " +
//			"    d.entitat = :entitat " +
//			"and (d.id in " + 
//			"			(select docPortafirmes.document.id from DocumentPortafirmesEntity docPortafirmes " +
//			"				where docPortafirmes.createdDate = (select max(docPortaf.createdDate) from DocumentPortafirmesEntity docPortaf where docPortaf.id = docPortafirmes.id)))")
//	public Page<DocumentEntity> findDocumentsPerCustodiarMassiu(
//			@Param("entitat") EntitatEntity entitat,
//			Pageable pageable);
	
	
	
	@Query(	"select " +
			"    d " +
			"from " +
			"    DocumentEntity d " +
			"where " +
			"    d.entitat = :entitat " +
			"and d.arxiuUuid = null " +
			"and d.esborrat = 0 " +
			"and (:esNullNom = true or lower(d.nom) like lower('%'||:nom||'%')) " +
			"and (:esNullExpedient = true or d.expedient = :expedient) " +
			"and (:esNullMetaExpedient = true or d.expedient.metaExpedient = :metaExpedient) ")
	public Page<DocumentEntity> findArxiuPendents(
			@Param("entitat") EntitatEntity entitat,
			@Param("esNullNom") boolean esNullNom,
			@Param("nom") String nom,
			@Param("esNullExpedient") boolean esNullExpedient,
			@Param("expedient") ExpedientEntity expedient,			
			@Param("esNullMetaExpedient") boolean esNullMetaExpedient,
			@Param("metaExpedient") MetaExpedientEntity metaExpedient,
			Pageable pageable);

	@Query(	"select " +
			"    d.id " +
			"from " +
			"    DocumentEntity d " +
			"where " +
			"    d.entitat = :entitat " +
			"and d.arxiuUuid = null " +
			"and d.esborrat = 0 " +
			"and (:esNullNom = true or lower(d.nom) like lower('%'||:nom||'%')) " +
			"and (:esNullExpedient = true or d.expedient = :expedient) " +
			"and (:esNullMetaExpedient = true or d.expedient.metaExpedient = :metaExpedient) ")
	public List<Long> findArxiuPendents(
			@Param("entitat") EntitatEntity entitat,
			@Param("esNullNom") boolean esNullNom,
			@Param("nom") String nom,
			@Param("esNullExpedient") boolean esNullExpedient,
			@Param("expedient") ExpedientEntity expedient,
			@Param("esNullMetaExpedient") boolean esNullMetaExpedient,
			@Param("metaExpedient") MetaExpedientEntity metaExpedient);
	
	

}
