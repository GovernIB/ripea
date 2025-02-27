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
			"			p.document = :document) ")
	Boolean findErrorLastEnviamentPortafirmesByDocument(@Param("document") DocumentEntity document);
	
	DocumentPortafirmesEntity findByPortafirmesId(
			String portafirmesId);

	List<DocumentPortafirmesEntity> findByDocumentOrderByCreatedDateDesc(DocumentEntity document);

	List<DocumentPortafirmesEntity> findByDocumentAndEstatInAndErrorOrderByCreatedDateAsc(
			DocumentEntity document,
			DocumentEnviamentEstatEnumDto[] estat,
			boolean error);
	
	List<DocumentPortafirmesEntity> findByExpedientAndEstatInAndErrorOrderByEnviatDataDesc(
			ExpedientEntity expedient,
			DocumentEnviamentEstatEnumDto[] estat,
			boolean error);
	
	@Query(	"from " +
			"    DocumentPortafirmesEntity dp " +
			"where " +
			"    (dp.document.entitat = :entitat) " +
			"and (:rolActual = 'IPA_ADMIN' or (:esNullMetaExpedientPermesos = true or dp.expedient.metaExpedient.id in (:idMetaExpedientPermesos))) " +
			"and (:esNullExpedientNom = true or lower(dp.expedient.nom) like lower('%'||:expedientNom||'%')) " +
			"and (:esNullDocumentNom = true or lower(dp.document.nom) like lower('%'||:documentNom||'%'))" +
			"and (:esNullDataEnviamentInici = true or dp.enviatData >= :dataEnviamentInici) " +
			"and (:esNullDataEnviamentFinal = true or dp.enviatData <= :dataEnviamentFinal) " +
			"and (:esNullEstatEnviament = true or dp.estat = :estatEnviament) ")
	public Page<DocumentPortafirmesEntity> findAmbFiltrePaginat(
			@Param("entitat") EntitatEntity entitat,
			@Param("rolActual") String rolActual,
			@Param("esNullMetaExpedientPermesos") boolean esNullMetaExpedientPermesos,
			@Param("idMetaExpedientPermesos") List<Long> idMetaExpedientPermesos,
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
