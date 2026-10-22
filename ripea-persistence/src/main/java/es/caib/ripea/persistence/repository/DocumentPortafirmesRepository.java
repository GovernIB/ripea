package es.caib.ripea.persistence.repository;

import es.caib.ripea.persistence.entity.DocumentEntity;
import es.caib.ripea.persistence.entity.DocumentPortafirmesEntity;
import es.caib.ripea.persistence.entity.EntitatEntity;
import es.caib.ripea.persistence.entity.ExpedientEntity;
import es.caib.ripea.service.intf.dto.DocumentEnviamentEstatEnumDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public interface DocumentPortafirmesRepository extends JpaRepository<DocumentPortafirmesEntity, Long> {

	List<DocumentPortafirmesEntity> findByDocument(DocumentEntity document);

	List<DocumentPortafirmesEntity> findByDocumentAndEstatInOrderByCreatedDateDesc(
			DocumentEntity document,
			DocumentEnviamentEstatEnumDto[] estat);

	List<DocumentPortafirmesEntity> findByDocumentAndEstatInAndErrorOrderByCreatedDateDesc(
			DocumentEntity document,
			DocumentEnviamentEstatEnumDto[] estat,
			boolean error);
	
	@Query( "select dp.error " +
			"from " +
			"	DocumentPortafirmesEntity dp " +
			"where dp.id = ( " +
			"		select " +
			"			max(p.id) " +
			"		from " +
			"			DocumentPortafirmesEntity p " +
			"		where " +
			"			p.document.id = :documentId) ")
	Boolean findErrorLastEnviamentPortafirmesByDocumentId(@Param("documentId") Long documentId);

	/**
	 * Versió per lots de findErrorLastEnviamentPortafirmesByDocumentId: una fila [documentId, error] pel
	 * darrer enviament (id màxim) de cada document. Els documents sense enviaments no hi apareixen.
	 * Màxim 1000 ids per crida (límit IN d'Oracle).
	 */
	@Query( "select dp.document.id, dp.error " +
			"from " +
			"	DocumentPortafirmesEntity dp " +
			"where dp.id in ( " +
			"		select " +
			"			max(p.id) " +
			"		from " +
			"			DocumentPortafirmesEntity p " +
			"		where " +
			"			p.document.id in (:documentIds) " +
			"		group by p.document.id) ")
	List<Object[]> findErrorDarrerEnviamentByDocumentIds(@Param("documentIds") List<Long> documentIds);
	
	DocumentPortafirmesEntity findByPortafirmesId(String portafirmesId);
	
	@Query( "select pf.portafirmesId " +
			"from DocumentPortafirmesEntity pf " +
			"where pf.id = (select max(n.id) from DocumentPortafirmesEntity n) ")
	String findLastEnviamentPortafirmes();

	List<DocumentPortafirmesEntity> findByDocumentOrderByCreatedDateDesc(DocumentEntity document);

	List<DocumentPortafirmesEntity> findByDocumentAndEstatInAndErrorOrderByCreatedDateAsc(
			DocumentEntity document,
			DocumentEnviamentEstatEnumDto[] estat,
			boolean error);
	
	List<DocumentPortafirmesEntity> findByExpedientAndEstatInAndErrorOrderByEnviatDataDesc(
			ExpedientEntity expedient,
			DocumentEnviamentEstatEnumDto[] estat,
			boolean error);
	
	@Query(	"select dp.id from DocumentPortafirmesEntity dp where dp.document.esborrat=1 and dp.document.estat IN (1, 4)")
	List<Long> findFirmaPendentDocumentEliminat();
	
	@Query(	"from " +
			"    DocumentPortafirmesEntity dp " +
			"where " +
			"    (dp.document.entitat = :entitat) " +
			// Un enviament es veu si es veu el seu expedient (mateixos permisos que el llistat d'expedients JSP)
			"and (dp.expedient.esborrat = 0) " +
			"and (:aplicaPermisos = false or dp.expedient.id in (" + ExpedientRepository.SUBCONSULTA_EXPEDIENTS_PERMESOS + ")) " +
			"and (:esNullExpedientNom = true or lower(dp.expedient.nom) like lower('%'||:expedientNom||'%')) " +
			"and (:esNullDocumentNom = true or lower(dp.document.nom) like lower('%'||:documentNom||'%'))" +
			"and (:esNullDataEnviamentInici = true or dp.enviatData >= :dataEnviamentInici) " +
			"and (:esNullDataEnviamentFinal = true or dp.enviatData <= :dataEnviamentFinal) " +
			"and (:esNullEstatEnviament = true or dp.estat = :estatEnviament) " +
			"and (dp.document.esborrat=0)")
	public Page<DocumentPortafirmesEntity> findAmbFiltrePaginat(
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
