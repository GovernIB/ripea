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
import es.caib.ripea.core.api.dto.DocumentNotificacioEstatEnumDto;
import es.caib.ripea.core.api.dto.DocumentNotificacioTipusEnumDto;
import es.caib.ripea.core.entity.DocumentEntity;
import es.caib.ripea.core.entity.DocumentNotificacioEntity;
import es.caib.ripea.core.entity.EntitatEntity;
import es.caib.ripea.core.entity.ExpedientEntity;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus documentNotificacio.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface DocumentNotificacioRepository extends JpaRepository<DocumentNotificacioEntity, Long> {

	List<DocumentNotificacioEntity> findByEstatAndTipusIn(
			DocumentEnviamentEstatEnumDto estat,
			DocumentNotificacioTipusEnumDto[] tipus);

	List<DocumentNotificacioEntity> findByExpedientOrderByEnviatDataAsc(
			ExpedientEntity expedient);
	List<DocumentNotificacioEntity> findByExpedientOrderByCreatedDateDesc(
			ExpedientEntity expedient);
	

	List<DocumentNotificacioEntity> findByDocumentOrderByEnviatDataAsc(
			DocumentEntity document);
	List<DocumentNotificacioEntity> findByDocumentOrderByCreatedDateAsc(
			DocumentEntity document);

	List<DocumentNotificacioEntity> findByDocumentOrderByCreatedDateDesc(DocumentEntity document);
	
	List<DocumentNotificacioEntity> findByDocumentAndNotificacioEstatInAndErrorOrderByCreatedDateAsc(
			DocumentEntity document,
			DocumentNotificacioEstatEnumDto[] estat,
			boolean error);
	
	long countByDocument(DocumentEntity document);

	DocumentNotificacioEntity findByNotificacioIdentificador(String notificacioIdentificador);

	
	@Query( "select dn.notificacioEstat " +
			"from " +
			"	DocumentNotificacioEntity dn " +
			"where dn.id = ( " +
			"		select " +
			"			max(n.id) " +
			"		from " +
			"			DocumentNotificacioEntity n " +
			"		where " +
			"			n.document = :document) ")
	DocumentNotificacioEstatEnumDto findLastEstatNotificacioByDocument(@Param("document") DocumentEntity document);
	
	@Query( "select dn.error " +
			"from " +
			"	DocumentNotificacioEntity dn " +
			"where dn.id = ( " +
			"		select " +
			"			max(n.id) " +
			"		from " +
			"			DocumentNotificacioEntity n " +
			"		where " +
			"			n.document = :document) ")
	Boolean findErrorLastNotificacioByDocument(@Param("document") DocumentEntity document);
	
	
	@Query("select " +
			"	dn " +
			"from " +
			"    DocumentNotificacioEntity dn " +
			"    left join dn.documentEnviamentInteressats envInt " +
			"where " +
			"    (dn.document.entitat = :entitat) " +
			"and (:esNullExpedientId = true or dn.expedient.id = :expedientId) " +
			"and (:esNullDocumentNom = true or lower(dn.document.nom) like lower('%'||:documentNom||'%'))" +
			"and (:esNullDataEnviamentInici = true or dn.createdDate >= :dataEnviamentInici) " +
			"and (:esNullDataEnviamentFinal = true or dn.createdDate <= :dataEnviamentFinal) " +
			"and (:esNullEstatNotificacio = true or dn.notificacioEstat = :estatNotificacio) " + 
			"and (:esNullEstatEnviament = true or envInt.enviamentDatatEstat = :estatEnviament) " + 			
			"and (:esNullEnviamentTipus = true or dn.tipus = :enviamentTipus) " + 
			"and (:esNullConcepte = true or lower(dn.assumpte) like lower('%'||:concepte||'%'))" +
			"and (:esNullInteressat = true " +
			"		or  envInt.interessat.esRepresentant = false " +
			"				and (lower(envInt.interessat.documentNum||' '||envInt.interessat.nom||' '||envInt.interessat.llinatge1||' '||envInt.interessat.llinatge2) like lower('%'||:interessat||'%')" +
			"					or lower(envInt.interessat.raoSocial) like lower('%'||:interessat||'%')" +
			"					or lower(envInt.interessat.organNom) like lower('%'||:interessat||'%'))) " +
			"and (:esNullOrganId = true or dn.expedient.organGestor.id = :organId) " +
			"and (:esNullProcedimentId = true or dn.expedient.metaExpedient.id = :procedimentId) " + 
			"and (:nomesAmbError = false or dn.error = true) ")
	public Page<DocumentNotificacioEntity> findAmbFiltrePaginat(
			@Param("entitat") EntitatEntity entitat,
			@Param("esNullExpedientId") boolean esNullExpedientId,
			@Param("expedientId") Long expedientId,
			@Param("esNullDocumentNom") boolean esNullDocumentNom,
			@Param("documentNom") String documentNom,
			@Param("esNullDataEnviamentInici") boolean esNullDataEnviamentInici,
			@Param("dataEnviamentInici") Date dataEnviamentInici,
			@Param("esNullDataEnviamentFinal") boolean esNullDataEnviamentFinal,
			@Param("dataEnviamentFinal") Date dataEnviamentFinal,
			@Param("esNullEstatNotificacio") boolean esNullEstatNotificacio,
			@Param("estatNotificacio") DocumentNotificacioEstatEnumDto estatNotificacio,
			@Param("esNullEstatEnviament") boolean esNullEstatEnviament,
			@Param("estatEnviament") String estatEnviament,
			@Param("esNullEnviamentTipus") boolean esNullEnviamentTipus,
			@Param("enviamentTipus") DocumentNotificacioTipusEnumDto enviamentTipus,
			@Param("esNullConcepte") boolean esNullConcepte,
			@Param("concepte") String concepte,
			@Param("esNullInteressat") boolean esNullInteressat,
			@Param("interessat") String interessat,
			@Param("esNullOrganId") boolean esNullOrganId,
			@Param("organId") Long organId,
			@Param("esNullProcedimentId") boolean esNullProcedimentId,
			@Param("procedimentId") Long procedimentId,		
			@Param("nomesAmbError") boolean nomesAmbError,
			Pageable paginacio);
	
	
	@Query("select " +
			"	dn.id " +
			"from " +
			"    DocumentNotificacioEntity dn " +
			"    left join dn.documentEnviamentInteressats envInt " +
			"where " +
			"    (dn.document.entitat = :entitat) " +
			"and (:esNullExpedientId = true or dn.expedient.id = :expedientId) " +
			"and (:esNullDocumentNom = true or lower(dn.document.nom) like lower('%'||:documentNom||'%'))" +
			"and (:esNullDataEnviamentInici = true or dn.createdDate >= :dataEnviamentInici) " +
			"and (:esNullDataEnviamentFinal = true or dn.createdDate <= :dataEnviamentFinal) " +
			"and (:esNullEstatNotificacio = true or dn.notificacioEstat = :estatNotificacio) " + 
			"and (:esNullEstatEnviament = true or envInt.enviamentDatatEstat = :estatEnviament) " + 			
			"and (:esNullEnviamentTipus = true or dn.tipus = :enviamentTipus) " + 
			"and (:esNullConcepte = true or lower(dn.assumpte) like lower('%'||:concepte||'%'))" +
			"and (:esNullInteressat = true " +
			"		or  envInt.interessat.esRepresentant = false " +
			"				and (lower(envInt.interessat.documentNum||' '||envInt.interessat.nom||' '||envInt.interessat.llinatge1||' '||envInt.interessat.llinatge2) like lower('%'||:interessat||'%')" +
			"					or lower(envInt.interessat.raoSocial) like lower('%'||:interessat||'%')" +
			"					or lower(envInt.interessat.organNom) like lower('%'||:interessat||'%'))) " +
			"and (:esNullOrganId = true or dn.expedient.organGestor.id = :organId) " +
			"and (:esNullProcedimentId = true or dn.expedient.metaExpedient.id = :procedimentId) ")
	public List<Long> findIdsAmbFiltrePaginat(
			@Param("entitat") EntitatEntity entitat,
			@Param("esNullExpedientId") boolean esNullExpedientId,
			@Param("expedientId") Long expedientId,
			@Param("esNullDocumentNom") boolean esNullDocumentNom,
			@Param("documentNom") String documentNom,
			@Param("esNullDataEnviamentInici") boolean esNullDataEnviamentInici,
			@Param("dataEnviamentInici") Date dataEnviamentInici,
			@Param("esNullDataEnviamentFinal") boolean esNullDataEnviamentFinal,
			@Param("dataEnviamentFinal") Date dataEnviamentFinal,
			@Param("esNullEstatNotificacio") boolean esNullEstatNotificacio,
			@Param("estatNotificacio") DocumentNotificacioEstatEnumDto estatNotificacio,
			@Param("esNullEstatEnviament") boolean esNullEstatEnviament,
			@Param("estatEnviament") String estatEnviament,
			@Param("esNullEnviamentTipus") boolean esNullEnviamentTipus,
			@Param("enviamentTipus") DocumentNotificacioTipusEnumDto enviamentTipus,
			@Param("esNullConcepte") boolean esNullConcepte,
			@Param("concepte") String concepte,
			@Param("esNullInteressat") boolean esNullInteressat,
			@Param("interessat") String interessat,
			@Param("esNullOrganId") boolean esNullOrganId,
			@Param("organId") Long organId,
			@Param("esNullProcedimentId") boolean esNullProcedimentId,
			@Param("procedimentId") Long procedimentId);

}
