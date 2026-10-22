package es.caib.ripea.persistence.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import es.caib.ripea.persistence.entity.DocumentEntity;
import es.caib.ripea.persistence.entity.DocumentNotificacioEntity;
import es.caib.ripea.persistence.entity.EntitatEntity;
import es.caib.ripea.persistence.entity.ExpedientEntity;
import es.caib.ripea.service.intf.dto.DocumentEnviamentEstatEnumDto;
import es.caib.ripea.service.intf.dto.DocumentNotificacioEstatEnumDto;
import es.caib.ripea.service.intf.dto.DocumentNotificacioTipusEnumDto;

@Component
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
			"			n.document.id = :documentId) ")
	DocumentNotificacioEstatEnumDto findLastEstatNotificacioByDocumentId(@Param("documentId") Long documentId);

	@Query( "select dn.error " +
			"from " +
			"	DocumentNotificacioEntity dn " +
			"where dn.id = ( " +
			"		select " +
			"			max(n.id) " +
			"		from " +
			"			DocumentNotificacioEntity n " +
			"		where " +
			"			n.document.id = :documentId) ")
	Boolean findErrorLastNotificacioByDocumentId(@Param("documentId") Long documentId);

	/**
	 * Versió per lots de findLastEstatNotificacioByDocumentId i findErrorLastNotificacioByDocumentId:
	 * una fila [documentId, notificacioEstat, error] per la darrera notificació (id màxim) de cada document.
	 * Els documents sense notificacions no hi apareixen. Màxim 1000 ids per crida (límit IN d'Oracle).
	 */
	@Query( "select dn.document.id, dn.notificacioEstat, dn.error " +
			"from " +
			"	DocumentNotificacioEntity dn " +
			"where dn.id in ( " +
			"		select " +
			"			max(n.id) " +
			"		from " +
			"			DocumentNotificacioEntity n " +
			"		where " +
			"			n.document.id in (:documentIds) " +
			"		group by n.document.id) ")
	List<Object[]> findDarreraNotificacioByDocumentIds(@Param("documentIds") List<Long> documentIds);

	@Query("select " +
			"	dn " +
			"from " +
			"    DocumentNotificacioEntity dn " +
			"where " +
			"    (dn.document.entitat = :entitat) " +
			// Un enviament es veu si es veu el seu expedient (mateixos permisos que el llistat d'expedients JSP)
			"and (dn.expedient.esborrat = 0) " +
			"and (:aplicaPermisos = false or dn.expedient.id in (" + ExpedientRepository.SUBCONSULTA_EXPEDIENTS_PERMESOS + ")) " +
			"and (:esNullExpedientId = true or dn.expedient.id = :expedientId) " +
			"and (:esNullDocumentNom = true or lower(dn.document.nom) like lower('%'||:documentNom||'%'))" +
			"and (:esNullDataEnviamentInici = true or dn.createdDate >= :dataEnviamentInici) " +
			"and (:esNullDataEnviamentFinal = true or dn.createdDate <= :dataEnviamentFinal) " +
			"and (:esNullEstatNotificacio = true or dn.notificacioEstat = :estatNotificacio) " +
			"and (:esNullEnviamentTipus = true or dn.tipus = :enviamentTipus) " +
			"and (:esNullConcepte = true or lower(dn.assumpte) like lower('%'||:concepte||'%'))" +
			// Estat de l'enviament i interessat es comproven sobre la mateixa fila d'enviament-interessat. Amb exists, i no amb
			// un join, una notificacio amb diversos interessats no surt repetida i les que no en tenen no queden excloses.
			"and ((:esNullEstatEnviament = true and :esNullInteressat = true) " +
			"		or exists (select envInt.id from DocumentEnviamentInteressatEntity envInt join envInt.interessat inter " +
			"			where envInt.notificacio = dn " +
			"			and (:esNullEstatEnviament = true or envInt.enviamentDatatEstat = :estatEnviament) " +
			"			and (:esNullInteressat = true " +
			"				or inter.esRepresentant = false " +
			"					and (lower(inter.documentNum||' '||inter.nom||' '||inter.llinatge1||' '||inter.llinatge2) like lower('%'||:interessat||'%')" +
			"						or lower(inter.raoSocial) like lower('%'||:interessat||'%')" +
			"						or lower(inter.organNom) like lower('%'||:interessat||'%'))))) " +
			"and (:esNullOrganId = true or dn.expedient.organGestor.id = :organId) " +
			"and (:esNullProcedimentId = true or dn.expedient.metaExpedient.id = :procedimentId) " +
			"and (:nomesAmbError = false or dn.error = true) ")
	public Page<DocumentNotificacioEntity> findAmbFiltrePaginat(
			@Param("entitat") EntitatEntity entitat,
			@Param("aplicaPermisos") boolean aplicaPermisos,
			@Param("esNullIdsMetaExpedientsPermesos") boolean esNullIdsMetaExpedientsPermesos,
			@Param("idsMetaExpedientsPermesos0") List<Long> idsMetaExpedientsPermesos0,
			@Param("idsMetaExpedientsPermesos1") List<Long> idsMetaExpedientsPermesos1,
			@Param("idsMetaExpedientsPermesos2") List<Long> idsMetaExpedientsPermesos2,
			@Param("idsMetaExpedientsPermesos3") List<Long> idsMetaExpedientsPermesos3,
			@Param("esNullIdsOrgansPermesos") boolean esNullIdsOrgansPermesos,
			@Param("idsOrgansPermesos0") List<Long> idsOrgansPermesos0,
			@Param("idsOrgansPermesos1") List<Long> idsOrgansPermesos1,
			@Param("idsOrgansPermesos2") List<Long> idsOrgansPermesos2,
			@Param("idsOrgansPermesos3") List<Long> idsOrgansPermesos3,
			@Param("esNullIdsMetaExpedientOrganPairsPermesos") boolean esNullIdsMetaExpedientOrganPairsPermesos,
			@Param("idsMetaExpedientOrganPairsPermesos") List<Long> idsMetaExpedientOrganPairsPermesos,
			@Param("esNullIdsOrgansAmbProcedimentsComunsPermesos") boolean esNullIdsOrgansAmbProcedimentsComunsPermesos,
			@Param("idsOrgansAmbProcedimentsComunsPermesos") List<Long> idsOrgansAmbProcedimentsComunsPermesos,
			@Param("idsProcedimentsComuns") List<Long> idsProcedimentsComuns,
			@Param("esNullIdsGrupsPermesos") boolean esNullIdsGrupsPermesos,
			@Param("idsGrupsPermesos") List<Long> idsGrupsPermesos,
			@Param("isAdmin") boolean isAdmin,
			@Param("noFiltreGrups") boolean noFiltreGrups,
			@Param("esNullExpedientId") boolean esNullExpedientId,
			@Param("expedientId") Long expedientId,
			@Param("esNullDocumentNom") boolean esNullDocumentNom,
			@Param("documentNom") String documentNom,
			@Param("esNullDataEnviamentInici") boolean esNullDataEnviamentInici,
			@Param("dataEnviamentInici") LocalDateTime dataEnviamentInici,
			@Param("esNullDataEnviamentFinal") boolean esNullDataEnviamentFinal,
			@Param("dataEnviamentFinal") LocalDateTime dataEnviamentFinal,
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
			"where " +
			"    (dn.document.entitat = :entitat) " +
			// Un enviament es veu si es veu el seu expedient (mateixos permisos que el llistat d'expedients JSP)
			"and (dn.expedient.esborrat = 0) " +
			"and (:aplicaPermisos = false or dn.expedient.id in (" + ExpedientRepository.SUBCONSULTA_EXPEDIENTS_PERMESOS + ")) " +
			"and (:esNullExpedientId = true or dn.expedient.id = :expedientId) " +
			"and (:esNullDocumentNom = true or lower(dn.document.nom) like lower('%'||:documentNom||'%'))" +
			"and (:esNullDataEnviamentInici = true or dn.createdDate >= :dataEnviamentInici) " +
			"and (:esNullDataEnviamentFinal = true or dn.createdDate <= :dataEnviamentFinal) " +
			"and (:esNullEstatNotificacio = true or dn.notificacioEstat = :estatNotificacio) " +
			"and (:esNullEnviamentTipus = true or dn.tipus = :enviamentTipus) " +
			"and (:esNullConcepte = true or lower(dn.assumpte) like lower('%'||:concepte||'%'))" +
			// Estat de l'enviament i interessat es comproven sobre la mateixa fila d'enviament-interessat. Amb exists, i no amb
			// un join, una notificacio amb diversos interessats no surt repetida i les que no en tenen no queden excloses.
			"and ((:esNullEstatEnviament = true and :esNullInteressat = true) " +
			"		or exists (select envInt.id from DocumentEnviamentInteressatEntity envInt join envInt.interessat inter " +
			"			where envInt.notificacio = dn " +
			"			and (:esNullEstatEnviament = true or envInt.enviamentDatatEstat = :estatEnviament) " +
			"			and (:esNullInteressat = true " +
			"				or inter.esRepresentant = false " +
			"					and (lower(inter.documentNum||' '||inter.nom||' '||inter.llinatge1||' '||inter.llinatge2) like lower('%'||:interessat||'%')" +
			"						or lower(inter.raoSocial) like lower('%'||:interessat||'%')" +
			"						or lower(inter.organNom) like lower('%'||:interessat||'%'))))) " +
			"and (:esNullOrganId = true or dn.expedient.organGestor.id = :organId) " +
			"and (:esNullProcedimentId = true or dn.expedient.metaExpedient.id = :procedimentId) ")
	public List<Long> findIdsAmbFiltrePaginat(
			@Param("entitat") EntitatEntity entitat,
			@Param("aplicaPermisos") boolean aplicaPermisos,
			@Param("esNullIdsMetaExpedientsPermesos") boolean esNullIdsMetaExpedientsPermesos,
			@Param("idsMetaExpedientsPermesos0") List<Long> idsMetaExpedientsPermesos0,
			@Param("idsMetaExpedientsPermesos1") List<Long> idsMetaExpedientsPermesos1,
			@Param("idsMetaExpedientsPermesos2") List<Long> idsMetaExpedientsPermesos2,
			@Param("idsMetaExpedientsPermesos3") List<Long> idsMetaExpedientsPermesos3,
			@Param("esNullIdsOrgansPermesos") boolean esNullIdsOrgansPermesos,
			@Param("idsOrgansPermesos0") List<Long> idsOrgansPermesos0,
			@Param("idsOrgansPermesos1") List<Long> idsOrgansPermesos1,
			@Param("idsOrgansPermesos2") List<Long> idsOrgansPermesos2,
			@Param("idsOrgansPermesos3") List<Long> idsOrgansPermesos3,
			@Param("esNullIdsMetaExpedientOrganPairsPermesos") boolean esNullIdsMetaExpedientOrganPairsPermesos,
			@Param("idsMetaExpedientOrganPairsPermesos") List<Long> idsMetaExpedientOrganPairsPermesos,
			@Param("esNullIdsOrgansAmbProcedimentsComunsPermesos") boolean esNullIdsOrgansAmbProcedimentsComunsPermesos,
			@Param("idsOrgansAmbProcedimentsComunsPermesos") List<Long> idsOrgansAmbProcedimentsComunsPermesos,
			@Param("idsProcedimentsComuns") List<Long> idsProcedimentsComuns,
			@Param("esNullIdsGrupsPermesos") boolean esNullIdsGrupsPermesos,
			@Param("idsGrupsPermesos") List<Long> idsGrupsPermesos,
			@Param("isAdmin") boolean isAdmin,
			@Param("noFiltreGrups") boolean noFiltreGrups,
			@Param("esNullExpedientId") boolean esNullExpedientId,
			@Param("expedientId") Long expedientId,
			@Param("esNullDocumentNom") boolean esNullDocumentNom,
			@Param("documentNom") String documentNom,
			@Param("esNullDataEnviamentInici") boolean esNullDataEnviamentInici,
			@Param("dataEnviamentInici") LocalDateTime dataEnviamentInici,
			@Param("esNullDataEnviamentFinal") boolean esNullDataEnviamentFinal,
			@Param("dataEnviamentFinal") LocalDateTime dataEnviamentFinal,
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
