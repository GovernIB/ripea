/**
 * 
 */
package es.caib.ripea.persistence.repository;

import es.caib.ripea.persistence.entity.EntitatEntity;
import es.caib.ripea.persistence.entity.ExpedientEntity;
import es.caib.ripea.persistence.entity.MetaExpedientEntity;
import es.caib.ripea.persistence.entity.RegistreAnnexEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface RegistreAnnexRepository extends JpaRepository<RegistreAnnexEntity, Long> {
	
	@Query(	"select " +
			"    a.document.expedient.id " +
			"from " +
			"    RegistreAnnexEntity a  " +
			"where " +
			"    a.id = :id ")
	public Long findExpedientId(
			@Param("id") Long id);
	
	
	@Query(	"select " +
			"    a " +
			"from " +
			"    RegistreAnnexEntity a left join a.registre.expedientPeticions ep " +
			"where " +
			"    ep.expedient = :expedient " +
			"and (a.document is null or a.error is not null)" )
	List<RegistreAnnexEntity> findDocumentsDeAnotacionesNoMogutsASerieFinal(
			@Param("expedient") ExpedientEntity expedient);
	
	
	@Query(	"select " +
			"    a " +
			"from " +
			"    RegistreAnnexEntity a left join a.registre.expedientPeticions e " +
			"where " +
			"    a.registre.entitat = :entitat " +
			"and ((:rolActual = 'IPA_ADMIN') " +
			"	or (:rolActual = 'IPA_ORGAN_ADMIN' and ((e.metaExpedient.organGestor is not null and e.metaExpedient in (:metaExpedientsPermesos)) or (e.metaExpedient.organGestor is null and :hasPermisAdminComu = true and e.registre.destiCodi in (:organsPermesos)))) " +
			"	or (:rolActual = 'tothom' and e.metaExpedient in (:metaExpedientsPermesos))) " +				
			"and e.expedient is not null " +
			"and e.expedient.esborrat = 0 " +
			"and (a.document is null) " +
			"and (:esNullMetaExpedient = true or e.expedient.metaExpedient = :metaExpedient) " +
			"and (:esNullExpedient = true or e.expedient = :expedient) " +
			"and (:esNullNom = true or lower(a.titol) like lower('%'||:nom||'%')) " +
			"and (:esNullNumero = true or lower(a.registre.identificador) like lower('%'||:numero||'%')) " +
			"and (:esNullDataInici = true or e.expedient.createdDate >= :dataInici) " +
			"and (:esNullDataFi = true or e.expedient.createdDate <= :dataFi) ")
	public Page<RegistreAnnexEntity> findPendentsProcesar(
			@Param("entitat") EntitatEntity entitat,
			@Param("rolActual") String rolActual,
			@Param("metaExpedientsPermesos") List<MetaExpedientEntity> metaExpedientsPermesos,
			@Param("organsPermesos") List<String> organsPermesos,
			@Param("hasPermisAdminComu") boolean hasPermisAdminComu,
			@Param("esNullNom") boolean esNullNom,
			@Param("nom") String nom,
			@Param("esNullNumero") boolean esNullNumero,
			@Param("numero") String numero,
			@Param("esNullDataInici") boolean esNullDataInici,
			@Param("dataInici") Date dataInici,
			@Param("esNullDataFi") boolean esNullDataFi,
			@Param("dataFi") Date dataFi,
			@Param("esNullMetaExpedient") boolean esNullMetaExpedient,
			@Param("metaExpedient") MetaExpedientEntity metaExpedient,
			@Param("esNullExpedient") boolean esNullExpedient,
			@Param("expedient") ExpedientEntity expedient,
			Pageable pageable);
	
	
	
	
	@Query(	"select " +
			"    a.id " +
			"from " +
			"    RegistreAnnexEntity a left join a.registre.expedientPeticions e " +
			"where " +
			"    a.registre.entitat = :entitat " +
			"and ((:rolActual = 'IPA_ADMIN') " +
			"	or (:rolActual = 'IPA_ORGAN_ADMIN' and ((e.metaExpedient.organGestor is not null and e.metaExpedient in (:metaExpedientsPermesos)) or (e.metaExpedient.organGestor is null and :hasPermisAdminComu = true and e.registre.destiCodi in (:organsPermesos)))) " +
			"	or (:rolActual = 'tothom' and e.metaExpedient in (:metaExpedientsPermesos))) " +	
			"and e.expedient is not null " +
			"and e.expedient.esborrat = 0 " +
			"and (a.document is null) " +
			"and (:esNullMetaExpedient = true or e.expedient.metaExpedient = :metaExpedient) " +
			"and (:esNullExpedient = true or e.expedient = :expedient) " +
			"and (:esNullNom = true or lower(a.titol) like lower('%'||:nom||'%')) " +
			"and (:esNullNumero = true or lower(a.registre.identificador) like lower('%'||:numero||'%')) " +
			"and (:esNullDataInici = true or e.expedient.createdDate >= :dataInici) " +
			"and (:esNullDataFi = true or e.expedient.createdDate <= :dataFi) ")
	public List<Long> findIdsPendentsProcesar(
			@Param("entitat") EntitatEntity entitat,
			@Param("rolActual") String rolActual,
			@Param("metaExpedientsPermesos") List<MetaExpedientEntity> metaExpedientsPermesos,
			@Param("organsPermesos") List<String> organsPermesos,
			@Param("hasPermisAdminComu") boolean hasPermisAdminComu,
			@Param("esNullNom") boolean esNullNom,
			@Param("nom") String nom,
			@Param("esNullNumero") boolean esNullNumero,
			@Param("numero") String numero,
			@Param("esNullDataInici") boolean esNullDataInici,
			@Param("dataInici") Date dataInici,
			@Param("esNullDataFi") boolean esNullDataFi,
			@Param("dataFi") Date dataFi,
			@Param("esNullMetaExpedient") boolean esNullMetaExpedient,
			@Param("metaExpedient") MetaExpedientEntity metaExpedient,
			@Param("esNullExpedient") boolean esNullExpedient,
			@Param("expedient") ExpedientEntity expedient);


	@Query(	"select a.id " +
			"from RegistreAnnexEntity a " +
			"where a.annexArxiuEstat = es.caib.ripea.core.api.dto.ArxiuEstatEnumDto.ESBORRANY " +
			"and a.document is not null " )
	public List<Long> findIdsEsborranysAmbDocument();



	
	
}
