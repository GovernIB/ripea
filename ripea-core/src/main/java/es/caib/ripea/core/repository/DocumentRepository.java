/**
 * 
 */
package es.caib.ripea.core.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.ripea.core.aggregation.MetaExpedientCountAggregation;
import es.caib.ripea.core.api.dto.ArxiuEstatEnumDto;
import es.caib.ripea.core.api.dto.DocumentEstatEnumDto;
import es.caib.ripea.core.api.dto.DocumentNotificacioEstatEnumDto;
import es.caib.ripea.core.entity.CarpetaEntity;
import es.caib.ripea.core.entity.ContingutEntity;
import es.caib.ripea.core.entity.DocumentEntity;
import es.caib.ripea.core.entity.EntitatEntity;
import es.caib.ripea.core.entity.ExpedientEntity;
import es.caib.ripea.core.entity.ExpedientEstatEntity;
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

	List<DocumentEntity> findByExpedientAndEstatInAndEsborrat(
			ExpedientEntity expedient,
			DocumentEstatEnumDto[] estat,
			int esborrat);
	
	int countByExpedient(ExpedientEntity expedient);

	int countByExpedientAndEstat(
			ExpedientEntity expedient,
			DocumentEstatEnumDto estat);
	
	@Query(	"select " +
			"    d.expedient.id " +
			"from " +
			"    DocumentEntity d " +
			"where " +
			"    d.id = :documentId ")
	Long findExpedientId(
			@Param("documentId") Long documentId);
	
	@Query(	"select " +
			"    c " +
			"from " +
			"    DocumentEntity c " +
			"where " +
			"c.expedient = :expedient " +
			"and c.esborrat = :esborrat " +
			"and c.ordre != 0")
	List<DocumentEntity> findByExpedientAndEsborratAndOrdenat(
			@Param("expedient") ExpedientEntity expedient,
			@Param("esborrat") int esborrat,
			Sort sort);
	
	@Query(	"select " +
			"    c " +
			"from " +
			"    DocumentEntity c " +
			"where " +
			"c.expedient = :expedient " +
			"and c.esborrat = :esborrat " +
			"and c.ordre = 0")
	List<DocumentEntity> findByExpedientAndEsborratSenseOrdre(
			@Param("expedient") ExpedientEntity expedient,
			@Param("esborrat") int esborrat,
			Sort sort);


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
	
	
	@Query(	"select " +
			"    d.id " +
			"from " +
			"    DocumentEntity d " +
			"where " +
			"d.expedient.id = :expedientId " +
			"and d.esborrat = :esborrat ")
	List<Long> findIdByExpedientIdAndEsborrat(
			@Param("expedientId") Long expedientId,
			@Param("esborrat") int esborrat);
	
	List<DocumentEntity> findByExpedient(
			ExpedientEntity expedient);
	
	
	@Query(	"select " +
			"    d " +
			"from " +
			"    DocumentEntity d " +
			"where " +
			"	 d.expedient = :expedient "  + 
			"and d.esborrat = 0 " +
			"and d.arxiuEstat = :arxiuEstat ")
	List<DocumentEntity> findByExpedientAndArxiuEstat(
			@Param("expedient") ExpedientEntity expedient,
			@Param("arxiuEstat") ArxiuEstatEnumDto arxiuEstat);
	
	@Query(	"select " +
			"    d " +
			"from " +
			"    DocumentEntity d " +
			"where " +
			"    d.expedient.id = :expedientId " )
	List<DocumentEntity> findByExpedientId(
			@Param("expedientId") Long expedientId);
	
	@Query(	"select " +
			"    d " +
			"from " +
			"    DocumentEntity d " +
			"where " +
			"    d.expedient.id = :expedientId " + 
			"and d.esborrat != 0 ")
	List<DocumentEntity> findDeleted(
			@Param("expedientId") Long expedientId);
	
	
	@Query(	"select " +
			"    d.id " +
			"from " +
			"    DocumentEntity d " +
			"where " +
			"	 d.expedient = :expedient "  + 
			"and d.esborrat = 0 " +
			"and d.arxiuEstat = es.caib.ripea.core.api.dto.ArxiuEstatEnumDto.ESBORRANY " + 
			"and d.documentFirmaTipus != es.caib.ripea.core.api.dto.DocumentFirmaTipusEnumDto.SENSE_FIRMA ")
	List<Long> findPendentsDeMarcarComADefinitius(
			@Param("expedient") ExpedientEntity expedient);
	
	@Query(	"select " +
			"    c " +
			"from " +
			"    DocumentEntity c " +
			"where " +
			"    c.expedient = :expedient " +
			"and c.esborrat = 0 " +
			"and (c.estat = es.caib.ripea.core.api.dto.DocumentEstatEnumDto.FIRMA_PENDENT " +
			"	or c.estat = es.caib.ripea.core.api.dto.DocumentEstatEnumDto.FIRMA_PENDENT_VIAFIRMA " +
			"	or c.estat = es.caib.ripea.core.api.dto.DocumentEstatEnumDto.FIRMA_PARCIAL)")
	List<DocumentEntity> findEnProccessDeFirma(
			@Param("expedient") ExpedientEntity expedient);
	
	
	@Query(	"select " +
			"    d " +
			"from " +
			"    DocumentEntity d " +
			"where " +
			"    d.expedient = :expedient " +
			"and d.esborrat = 0 " +
			"and d.gesDocFirmatId is not null")
	List<DocumentEntity> findDocumentsDePortafirmesNoCustodiats(
			@Param("expedient") ExpedientEntity expedient);
	
	
	
	@Query(	"select " +
			"    d " +
			"from " +
			"    DocumentEntity d " +
			"where " +
			"    d.expedient = :expedient " +
			"and d.esborrat = 0 " +
			"and d.arxiuUuid = null " +
			"and d.arxiuReintents < :arxiuMaxReintentsDocuments")
	List<DocumentEntity> findDocumentsPendentsReintentsArxiu(
			@Param("expedient") ExpedientEntity expedient,
			@Param("arxiuMaxReintentsDocuments") int arxiuMaxReintentsDocuments);
	
	
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
	
	List<DocumentEntity> findByExpedientAndExpedientEstatAdditionalAndEsborrat(
			ExpedientEntity expedient,
			ExpedientEstatEntity expedientEstatAdditional,
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
			"and d.esborrat = 0 " +
			"and (d.metaNode.id in " + 
			"			(select metaDocument.id from MetaDocumentEntity metaDocument " +
			"				where metaDocument.firmaPassarelaActiva = true))"  +
			"and d.estat = 0 " +	
			"and d.documentTipus = 0 " +
			"and d.fitxerNom not like '%.zip' " +
			"and d.gesDocAdjuntId is null " +
			"and (:esNullMetaExpedient = true or d.expedient.metaNode = :metaExpedient) " +	
			"and (:esNullExpedient = true or d.expedient = :expedient) " +
			"and (:esNullMetaDocument = true or d.metaNode = :metaDocument) " +
			"and (:esNullNom = true or lower(d.nom) like lower('%'||:nom||'%')) " +
			"and (:esNullDataInici = true or d.createdDate >= :dataInici) " +
			"and (:esNullDataFi = true or d.createdDate <= :dataFi) " )
	public Page<DocumentEntity> findDocumentsPerFirmaSimpleWebMassiu(
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
			"and d.esborrat = 0 " +
			"and (d.metaNode.id in " + 
			"			(select metaDocument.id from MetaDocumentEntity metaDocument " +
			"				where metaDocument.firmaPassarelaActiva = true))"  +
			"and d.estat = 0 " +	
			"and d.documentTipus = 0 " +
			"and d.fitxerNom not like '%.zip' " +
			"and d.gesDocAdjuntId is null " +
			"and (:esNullMetaExpedient = true or d.expedient.metaNode = :metaExpedient) " +	
			"and (:esNullExpedient = true or d.expedient = :expedient) " +
			"and (:esNullMetaDocument = true or d.metaNode = :metaDocument) " +
			"and (:esNullNom = true or lower(d.nom) like lower('%'||:nom||'%')) " +
			"and (:esNullDataInici = true or d.createdDate >= :dataInici) " +
			"and (:esNullDataFi = true or d.createdDate <= :dataFi) " )
	public List<Long> findIdsDocumentsPerFirmaSimpleWebMassiu(
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
			"and (d.estat = 2 or d.estat = 3 or d.estat = 5) "  +
			"and d.esborrat = 0 " +
			"and d.gesDocAdjuntId is null " +
			"and d.documentTipus != 1 and d.documentTipus != 2 and d.documentTipus != 3 " +
			"and (:esNullMetaExpedient = true or d.expedient.metaNode = :metaExpedient) " +
			"and (:esNullExpedient = true or d.expedient = :expedient) " +
			"and (:esNullMetaDocument = true or d.metaNode = :metaDocument) " +
			"and (:esNullNom = true or lower(d.nom) like lower('%'||:nom||'%')) " +
			"and (:esNullDataInici = true or d.createdDate >= :dataInici) " +
			"and (:esNullDataFi = true or d.createdDate <= :dataFi) " +
			"and (d.metaNode.id is not null)" )
	public Page<DocumentEntity> findDocumentsPerCopiarCsv(
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

	

	
	
	
	
	@Query(	"select " +
			"    d1 " +
			"from " +
			"    DocumentEntity d1 inner join d1.contingut c1 " +
			"where d1.id in " +
			"(select " +
			"    distinct d.id " +
			"from " +
			"    DocumentEntity d " +
			"	 inner join d.contingut c inner join c.expedient e left join e.metaexpedientOrganGestorPares meogp " + // d.contingut c is used in ordenacioMap
			"	 left join d.annexos a " + 
			"where " +
			"	 c.esborrat = 0 " +
			"and (c.arxiuUuid = null " + //documents uploaded manually in ripea not saved in arxiu
			"	  or a.error is not null " + //documents from distribucio not moved in arxiu to ripea expedient
			"	  or (d.id in " + // documents signed in portafirmes that arrived in callback not saved in arxiu 
			"			(select docPortafirmes.document.id from DocumentPortafirmesEntity docPortafirmes " +
			"				where (docPortafirmes.id, docPortafirmes.createdDate) in (select docPortaf.id, max(docPortaf.createdDate) from DocumentPortafirmesEntity docPortaf group by docPortaf.id) " +
			"				and docPortafirmes.estat = 'ENVIAT' " +
			"				and docPortafirmes.error = true)))" +			
			"and (" +
			"     (:esNullIdsMetaExpedientsPermesos = false and e.metaExpedient.id in (:idsMetaExpedientsPermesos)) " +
			"     or (:esNullIdsOrgansPermesos = false and meogp.organGestor.id in (:idsOrgansPermesos)) " +
			"     or (:esNullIdsMetaExpedientOrganPairsPermesos = false and meogp.id in (:idsMetaExpedientOrganPairsPermesos)) " +
			"     or (:esNullIdsOrgansAmbProcedimentsComunsPermesos = false and meogp.organGestor.id in (:idsOrgansAmbProcedimentsComunsPermesos) and e.metaExpedient.id in (:idsProcedimentsComuns))) " +
			//TODO if organ is in :idsOrgansAmbProcedimentsComunsPermesos it is also already in :idsOrgansPermesos as well so check :idsOrgansAmbProcedimentsComunsPermesos doesn't do anything, probably :idsOrgansPermesos check should be only allowed for procediments no comuns			
			"and c.entitat = :entitat " +
			"and (:nomesAgafats = false or e.agafatPer.codi = :usuariActual) " +
			"and (:esNullNom = true or lower(c.nom) like lower('%'||:nom||'%')) " +
			"and (:esNullExpedient = true or e = :expedient) " +
			"and (:esNullMetaExpedient = true or e.metaExpedient = :metaExpedient)) ")
	public Page<DocumentEntity> findArxiuPendents(
			@Param("entitat") EntitatEntity entitat,
			@Param("esNullIdsMetaExpedientsPermesos") boolean esNullIdsMetaExpedientsPermesos, 
			@Param("idsMetaExpedientsPermesos") List<Long> idsMetaExpedientsPermesos,
			@Param("esNullIdsOrgansPermesos") boolean esNullIdsOrgansPermesos, 
			@Param("idsOrgansPermesos") List<Long> idsOrgansPermesos,
			@Param("esNullIdsMetaExpedientOrganPairsPermesos") boolean esNullIdsMetaExpedientOrganPairsPermesos, 
			@Param("idsMetaExpedientOrganPairsPermesos") List<Long> idsMetaExpedientOrganPairsPermesos,
			@Param("esNullIdsOrgansAmbProcedimentsComunsPermesos") boolean esNullIdsOrgansAmbProcedimentsComunsPermesos, 
			@Param("idsOrgansAmbProcedimentsComunsPermesos") List<Long> idsOrgansAmbProcedimentsComunsPermesos,
			@Param("idsProcedimentsComuns") List<Long> idsProcedimentsComuns,
			@Param("nomesAgafats") boolean nomesAgafats,
			@Param("usuariActual") String usuariActual,
			@Param("esNullNom") boolean esNullNom,
			@Param("nom") String nom,
			@Param("esNullExpedient") boolean esNullExpedient,
			@Param("expedient") ExpedientEntity expedient,			
			@Param("esNullMetaExpedient") boolean esNullMetaExpedient,
			@Param("metaExpedient") MetaExpedientEntity metaExpedient,
			Pageable pageable);
	
	
	
	
	
	

	@Query(	"select " +
			"    d " +
			"from " +
			"    DocumentEntity d inner join d.contingut c1 " + // d.contingut c1 is used in ordenacioMap
			"    inner join d.expedient e " +
			"	 left join d.annexos a " + 
			"where " +
			"	 c1.esborrat = 0 " +
			"and (c1.arxiuUuid = null " + //documents uploaded manually in ripea that were not saved in arxiu
			"	  or a.error is not null " + //documents from distribucio that were not moved in arxiu to ripea expedient
			"	  or d.gesDocFirmatId is not null) " + // documents signed in portafirmes that arrived in callback and were not saved in arxiu 		
			"and d.entitat = :entitat " +
			"and e.esborrat = 0 " +
			"and e.estat = es.caib.ripea.core.api.dto.ExpedientEstatEnumDto.OBERT " +
			"and (c1.expedient.metaNode in (:metaExpedientsPermesos)) " +
			"and (:nomesAgafats = false or d.expedient.agafatPer.codi = :usuariActual) " +
			"and (:esNullNom = true or lower(d.nom) like lower('%'||:nom||'%')) " +
			"and (:esNullExpedient = true or c1.expedient = :expedient) " +
			"and (:esNullMetaExpedient = true or d.expedient.metaExpedient = :metaExpedient) " +
			"and (:esNullCreacioInici = true or d.createdDate >= :creacioInici) " +
			"and (:esNullCreacioFi = true or d.createdDate <= :creacioFi) ")
	public Page<DocumentEntity> findArxiuPendents(
			@Param("entitat") EntitatEntity entitat,
			@Param("metaExpedientsPermesos") List<? extends MetaNodeEntity> metaExpedientsPermesos,
			@Param("nomesAgafats") boolean nomesAgafats,
			@Param("usuariActual") String usuariActual,
			@Param("esNullNom") boolean esNullNom,
			@Param("nom") String nom,
			@Param("esNullExpedient") boolean esNullExpedient,
			@Param("expedient") ExpedientEntity expedient,			
			@Param("esNullMetaExpedient") boolean esNullMetaExpedient,
			@Param("metaExpedient") MetaExpedientEntity metaExpedient,
			@Param("esNullCreacioInici") boolean esNullCreacioInici,
			@Param("creacioInici") Date creacioInici,
			@Param("esNullCreacioFi") boolean esNullCreacioFi,
			@Param("creacioFi") Date creacioFi,
			Pageable pageable);
	
	
	@Query(	"select " +
			"    d.id " +
			"from " +
			"    DocumentEntity d inner join d.contingut c1 " + // d.contingut c1 is used in ordenacioMap
			"    inner join d.expedient e " +
			"	 left join d.annexos a " + 
			"where " +
			"	 c1.esborrat = 0 " +
			"and (c1.arxiuUuid = null " + //documents uploaded manually in ripea that were not saved in arxiu
			"	  or a.error is not null " + //documents from distribucio that were not moved in arxiu to ripea expedient
			"	  or d.gesDocFirmatId is not null) " + // documents signed in portafirmes that arrived in callback and were not saved in arxiu 		
			"and d.entitat = :entitat " +
			"and e.esborrat = 0 " +
			"and e.estat = es.caib.ripea.core.api.dto.ExpedientEstatEnumDto.OBERT " +
			"and (c1.expedient.metaNode in (:metaExpedientsPermesos)) " +
			"and (:nomesAgafats = false or d.expedient.agafatPer.codi = :usuariActual) " +
			"and (:esNullNom = true or lower(d.nom) like lower('%'||:nom||'%')) " +
			"and (:esNullExpedient = true or c1.expedient = :expedient) " +
			"and (:esNullMetaExpedient = true or d.expedient.metaExpedient = :metaExpedient) " +
			"and (:esNullCreacioInici = true or d.createdDate >= :creacioInici) " +
			"and (:esNullCreacioFi = true or d.createdDate <= :creacioFi) ")
	public List<Long> findIdsArxiuPendents(
			@Param("entitat") EntitatEntity entitat,
			@Param("metaExpedientsPermesos") List<? extends MetaNodeEntity> metaExpedientsPermesos,
			@Param("nomesAgafats") boolean nomesAgafats,
			@Param("usuariActual") String usuariActual,
			@Param("esNullNom") boolean esNullNom,
			@Param("nom") String nom,
			@Param("esNullExpedient") boolean esNullExpedient,
			@Param("expedient") ExpedientEntity expedient,			
			@Param("esNullMetaExpedient") boolean esNullMetaExpedient,
			@Param("metaExpedient") MetaExpedientEntity metaExpedient,
			@Param("esNullCreacioInici") boolean esNullCreacioInici,
			@Param("creacioInici") Date creacioInici,
			@Param("esNullCreacioFi") boolean esNullCreacioFi,
			@Param("creacioFi") Date creacioFi);
	
    @Query("select case when count(c) > 0 then true else false end " + 
    		"from DocumentEntity d " +
    		"join d.contingut c " +
            "where c.expedient = :expedient " +
            "and d.arxiuEstat = es.caib.ripea.core.api.dto.ArxiuEstatEnumDto.DEFINITIU")
    Boolean expedientHasDocumentsDefinitius(@Param("expedient") ExpedientEntity expedient);
    
    @Query("select case when count(c) > 0 then true else false end " + 
    		"from DocumentEntity d " +
    		"join d.contingut c " +
            "where c.pare = :carpeta " +
            "and d.arxiuEstat = es.caib.ripea.core.api.dto.ArxiuEstatEnumDto.DEFINITIU")
    Boolean carpetaHasDocumentsDefinitius(@Param("carpeta") CarpetaEntity carpeta);

}
