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
	
	@Query(	"from " +
			"    DocumentNotificacioEntity dn " +
			"where " +
			"    (dn.document.entitat = :entitat) " +
			"and (:esNullExpedientNom = true or lower(dn.expedient.nom) like lower('%'||:expedientNom||'%')) " +
			"and (:esNullDocumentNom = true or lower(dn.document.nom) like lower('%'||:documentNom||'%'))" +
			"and (:esNullDataEnviamentInici = true or dn.createdDate >= :dataEnviamentInici) " +
			"and (:esNullDataEnviamentFinal = true or dn.createdDate <= :dataEnviamentFinal) " +
			"and (:esNullEstatEnviament = true or dn.notificacioEstat = :estatEnviament) ")
	public Page<DocumentNotificacioEntity> findAmbFiltrePaginat(
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
			@Param("estatEnviament") DocumentNotificacioEstatEnumDto estatEnviament,
			Pageable paginacio);
	
}
