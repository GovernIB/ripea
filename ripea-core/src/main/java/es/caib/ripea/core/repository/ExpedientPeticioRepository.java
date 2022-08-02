/**
 * 
 */
package es.caib.ripea.core.repository;

import java.util.Date;
import java.util.List;


import es.caib.ripea.core.api.dto.ExpedientPeticioPendentDist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.ripea.core.api.dto.ExpedientPeticioAccioEnumDto;
import es.caib.ripea.core.api.dto.ExpedientPeticioEstatEnumDto;
import es.caib.ripea.core.entity.EntitatEntity;
import es.caib.ripea.core.entity.ExpedientEntity;
import es.caib.ripea.core.entity.ExpedientPeticioEntity;
import es.caib.ripea.core.entity.MetaExpedientEntity;

public interface ExpedientPeticioRepository extends JpaRepository<ExpedientPeticioEntity, Long> {

	
	@Query("select peticio.registre.id from ExpedientPeticioEntity peticio where peticio.id = :id")
	Long getRegistreId(@Param("id") Long id);
	
	@Query("select annex.id from" +
			"    RegistreAnnexEntity annex " +
			"where " +
			"annex.registre.id = :id ")
	List<Long> getRegistreAnnexosId(@Param("id") Long id);
	
	@Query("select registre.justificantArxiuUuid from" +
			"    RegistreEntity registre " +
			"where " +
			"registre.id = :id")
	String getRegistreJustificantArxiuUuid(@Param("id") Long id);
	
	ExpedientPeticioEntity findByIdentificador(String identificador);

	List<ExpedientPeticioEntity> findByEstatAndConsultaWsErrorIsFalse(ExpedientPeticioEstatEnumDto estat);
	
	List<ExpedientPeticioEntity> findByExpedient(ExpedientEntity expedient, Pageable pageable);

	@Query("from" +
			"    ExpedientPeticioEntity e " +
			"where " +
			"e.registre.entitat = :entitat " +
			"and (:rolActual = 'IPA_ADMIN' or (:rolActual = 'IPA_ORGAN_ADMIN' and e.registre.destiCodi in (:organsCodisPermitted)) or e.metaExpedient.id in (:idMetaExpedientPermesos)) " +
			"and (:esNullMetaExpedient = true or e.metaExpedient = :metaExpedient) " +
			"and (:esNullProcediment = true or lower(e.registre.procedimentCodi) like lower('%'||:procediment||'%')) " +
			"and (:esNullNumero = true or lower(e.registre.identificador) like lower('%'||:numero||'%')) " +
			"and (:esNullExtracte = true or lower(e.registre.extracte) like lower('%'||:extracte||'%')) " +
			"and (:esNullDestinacio = true or e.registre.destiCodi = :destinacio) " + 
			"and (:esNullDataInicial = true or e.registre.data >= :dataInicial) " +
			"and (:esNullDataFinal = true or e.registre.data <= :dataFinal) " +
			"and (:esNullEstat = true or " +
			"							(:estat = 'PENDENT' and e.estat = es.caib.ripea.core.api.dto.ExpedientPeticioEstatEnumDto.PENDENT) or " +
			"							(:estat = 'ACCEPTAT' and (e.estat = es.caib.ripea.core.api.dto.ExpedientPeticioEstatEnumDto.PROCESSAT_PENDENT or e.estat = es.caib.ripea.core.api.dto.ExpedientPeticioEstatEnumDto.PROCESSAT_NOTIFICAT)) or " +
			" 							(:estat = 'REBUTJAT' and e.estat = es.caib.ripea.core.api.dto.ExpedientPeticioEstatEnumDto.REBUTJAT)) " +
			"and (:esNullAccio = true or e.expedientPeticioAccioEnumDto = :accio) "
			)
	Page<ExpedientPeticioEntity> findByEntitatAndFiltre(
			@Param("entitat") EntitatEntity entitat,
			@Param("rolActual") String rolActual,
			@Param("organsCodisPermitted") List<String> organsCodisPermitted,
			@Param("idMetaExpedientPermesos") List<Long> idMetaExpedientPermesos,
			@Param("esNullMetaExpedient") boolean esNullMetaExpedient,
			@Param("metaExpedient") MetaExpedientEntity metaExpedient,
			@Param("esNullProcediment") boolean esNullProcediment,
			@Param("procediment") String procediment,
			@Param("esNullNumero") boolean esNullNumero,
			@Param("numero") String numero,
			@Param("esNullExtracte") boolean esNullExtracte,
			@Param("extracte") String extracte,
			@Param("esNullDestinacio") boolean esNullDestinacio,
			@Param("destinacio") String destinacio,
			@Param("esNullDataInicial") boolean esNullDataInicial,
			@Param("dataInicial") Date dataInicial,
			@Param("esNullDataFinal") boolean esNullDataFinal,
			@Param("dataFinal") Date dataFinal,
			@Param("esNullEstat") boolean esNullEstat,
			@Param("estat") String estat,
			@Param("esNullAccio") boolean esNullAccio,
			@Param("accio") ExpedientPeticioAccioEnumDto accio,
			Pageable pageable);

	@Query(	"select " +
			"    count(pet) " +
			"from " +
			"    ExpedientPeticioEntity pet " +
			"where " +
			":entitatActual = pet.registre.entitat " +
			"and (pet.metaExpedient.id in (:idMetaExpedientPermesos)) " +
			"and pet.estat='PENDENT' " )
	long countAnotacionsPendentsUser(
			@Param("entitatActual") EntitatEntity entitatActual,
			@Param("idMetaExpedientPermesos") List<Long> idMetaExpedientPermesos);
	
	@Query(	"select " +
			"    count(pet) " +
			"from " +
			"    ExpedientPeticioEntity pet " +
			"where " +
			":entitatActual = pet.registre.entitat " +
			"and pet.estat='PENDENT' " )
	long countAnotacionsPendentsAdminEntitat(
			@Param("entitatActual") EntitatEntity entitatActual);
	
	
	@Query(	"select " +
			"    count(pet) " +
			"from " +
			"    ExpedientPeticioEntity pet " +
			"where " +
			":entitatActual = pet.registre.entitat " +
			"and (pet.registre.destiCodi in (:organsCodisPermitted)) " +
			"and pet.estat='PENDENT' " )
	long countAnotacionsPendentsAdminOrgan(
			@Param("entitatActual") EntitatEntity entitatActual,
			@Param("organsCodisPermitted") List<String> organsCodisPermitted);

	@Query("FROM ExpedientPeticioEntity e WHERE e.pendentEnviarDistribucio = true AND e.reintentsEnviarDistribucio > 0")
	List<ExpedientPeticioEntity> findPendentsCanviEstat();

	@Query("SELECT new es.caib.ripea.core.api.dto.ExpedientPeticioPendentDist(ep.id, ep.identificador, ep.dataAlta, ep.expedient.nom, ep.expedient.id) " +
			"FROM ExpedientPeticioEntity ep " +
			"WHERE ep.pendentEnviarDistribucio = true AND ep.reintentsEnviarDistribucio > 0 AND ep.expedient.entitat = :entitat " +
			"AND (:identificadorNull = true or lower(ep.identificador) like lower('%' || :identificador || '%')) " +
			"AND (:nomNull = true or lower(ep.expedient.nom) like lower('%' || :nom || '%')) " +
			"AND (:dataIniciNull = true or ep.dataAlta >= :dataInici) " +
			"AND (:dataFiNull = true or ep.dataAlta <= :dataFi)")
	Page<ExpedientPeticioPendentDist> findPendentsCanviEstat(@Param("entitat") EntitatEntity entitat,
															 @Param("identificadorNull") boolean identificadorNull,
															 @Param("identificador") String identificador,
															 @Param("nomNull") boolean nomNull,
															 @Param("nom") String nom,
															 @Param("dataIniciNull") boolean dataIniciNull,
															 @Param("dataInici") Date dataInici,
															 @Param("dataFiNull") boolean dataFiNull,
															 @Param("dataFi") Date dataFi,
															 Pageable pageable);

	@Query("SELECT ep.id FROM ExpedientPeticioEntity ep " +
			"WHERE ep.pendentEnviarDistribucio = true AND ep.reintentsEnviarDistribucio > 0 AND ep.expedient.entitat = :entitat " +
			"AND (:identificadorNull = true or lower(ep.identificador) like lower('%' || :identificador || '%')) " +
			"AND (:nomNull = true or lower(ep.expedient.nom) like lower('%' || :nom || '%')) " +
			"AND (:dataIniciNull = true or ep.dataAlta >= :dataInici) " +
			"AND (:dataFiNull = true or ep.dataAlta <= :dataFi)")
	List<Long> findIdsPendentsCanviEstat(@Param("entitat") EntitatEntity entitat,
										 @Param("identificadorNull") boolean identificadorNull,
										 @Param("identificador") String identificador,
										 @Param("nomNull") boolean nomNull,
										 @Param("nom") String nom,
										 @Param("dataIniciNull") boolean dataIniciNull,
										 @Param("dataInici") Date dataInici,
										 @Param("dataFiNull") boolean dataFiNull,
										 @Param("dataFi") Date dataFi);

}
