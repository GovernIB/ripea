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

import es.caib.ripea.core.entity.EntitatEntity;
import es.caib.ripea.core.entity.MetaExpedientEntity;
import es.caib.ripea.core.entity.RegistreAnnexEntity;


public interface RegistreAnnexRepository extends JpaRepository<RegistreAnnexEntity, Long> {
	
	@Query(	"select " +
			"    a " +
			"from " +
			"    RegistreAnnexEntity a  " +
			"where " +
			"    a.id = :id ")
	public RegistreAnnexEntity findById(
			@Param("id") Long id);
	
	
	
	@Query(	"select " +
			"    a " +
			"from " +
			"    RegistreAnnexEntity a left join a.registre.expedientPeticions ep " +
			"where " +
			"    a.registre.entitat = :entitat " +
			"and ep.expedient is not null " +
			"and ep.expedient.esborrat = 0 " +
			"and (a.document is null) " +
			"and (:esNullMetaExpedient = true or ep.expedient.metaExpedient = :metaExpedient) " +
			"and (:esNullNom = true or lower(a.titol) like lower('%'||:nom||'%')) " +
			"and (:esNullNumero = true or lower(a.registre.identificador) like lower('%'||:numero||'%')) " +
			"and (:esNullDataInici = true or ep.expedient.createdDate >= :dataInici) " +
			"and (:esNullDataFi = true or ep.expedient.createdDate <= :dataFi) ")
	public Page<RegistreAnnexEntity> findPendentsProcesar(
			@Param("entitat") EntitatEntity entitat,
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
			Pageable pageable);
	
	
	
	
	@Query(	"select " +
			"    a.id " +
			"from " +
			"    RegistreAnnexEntity a left join a.registre.expedientPeticions ep " +
			"where " +
			"    a.registre.entitat = :entitat " +
			"and ep.expedient is not null " +
			"and ep.expedient.esborrat = 0 " +
			"and (a.document is null) " +
			"and (:esNullMetaExpedient = true or ep.expedient.metaExpedient = :metaExpedient) " +
			"and (:esNullNom = true or lower(a.titol) like lower('%'||:nom||'%')) " +
			"and (:esNullNumero = true or lower(a.registre.identificador) like lower('%'||:numero||'%')) " +
			"and (:esNullDataInici = true or ep.expedient.createdDate >= :dataInici) " +
			"and (:esNullDataFi = true or ep.expedient.createdDate <= :dataFi) ")
	public List<Long> findIdsPendentsProcesar(
			@Param("entitat") EntitatEntity entitat,
			@Param("esNullNom") boolean esNullNom,
			@Param("nom") String nom,
			@Param("esNullNumero") boolean esNullNumero,
			@Param("numero") String numero,
			@Param("esNullDataInici") boolean esNullDataInici,
			@Param("dataInici") Date dataInici,
			@Param("esNullDataFi") boolean esNullDataFi,
			@Param("dataFi") Date dataFi,
			@Param("esNullMetaExpedient") boolean esNullMetaExpedient,
			@Param("metaExpedient") MetaExpedientEntity metaExpedient);
	

	
	


	
	
}
