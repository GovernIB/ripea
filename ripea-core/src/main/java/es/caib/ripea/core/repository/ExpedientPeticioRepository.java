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

import es.caib.ripea.core.api.dto.ExpedientPeticioAccioEnumDto;
import es.caib.ripea.core.api.dto.ExpedientPeticioEstatEnumDto;
import es.caib.ripea.core.entity.EntitatEntity;
import es.caib.ripea.core.entity.ExpedientEntity;
import es.caib.ripea.core.entity.ExpedientPeticioEntity;
import es.caib.ripea.core.entity.MetaExpedientEntity;
import es.caib.ripea.core.entity.OrganGestorEntity;

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

	
	@Query("select " +
			"	ep " +
			"from " +
			"    ExpedientPeticioEntity ep left join ep.metaExpedient me " +
			"where " +
			"ep.registre.entitat = :entitat " +
			"and ((:rolActual = 'IPA_ADMIN') " +
			"	or (:rolActual = 'IPA_ORGAN_ADMIN' and ((me.organGestor is not null and ep.metaExpedient in (:metaExpedientsPermesos)) or (me.organGestor is null and :hasPermisAdminComu = true and ep.registre.destiCodi in (:organsPermesos)))) " +
			"	or (:rolActual = 'tothom' and ep.metaExpedient in (:metaExpedientsPermesos))) " +	
			"and (:esNullMetaExpedient = true or ep.metaExpedient = :metaExpedient) " +
			"and (:esNullNumero = true or lower(ep.registre.identificador) like lower('%'||:numero||'%')) " +
			"and (:esNullExtracte = true or lower(ep.registre.extracte) like lower('%'||:extracte||'%')) " +
			"and (:esNullDestinacio = true or ep.registre.destiCodi = :destinacio) " + 
			"and (:esNullDataInicial = true or ep.registre.data >= :dataInicial) " +
			"and (:esNullDataFinal = true or ep.registre.data <= :dataFinal) " +
			"and (:esNullEstat = true or " +
			"							(:estat = 'PENDENT' and ep.estat = es.caib.ripea.core.api.dto.ExpedientPeticioEstatEnumDto.PENDENT) or " +
			"							(:estat = 'ACCEPTAT' and (ep.estat = es.caib.ripea.core.api.dto.ExpedientPeticioEstatEnumDto.PROCESSAT_PENDENT or ep.estat = es.caib.ripea.core.api.dto.ExpedientPeticioEstatEnumDto.PROCESSAT_NOTIFICAT)) or " +
			" 							(:estat = 'REBUTJAT' and ep.estat = es.caib.ripea.core.api.dto.ExpedientPeticioEstatEnumDto.REBUTJAT)) " +
			"and (:esNullAccio = true or ep.expedientPeticioAccioEnumDto = :accio) " +
			"and (:esNullInteressat = true " +
			"		or  ep.registre.id in (" +
			"			select interessat.registre.id " +
			"			from RegistreInteressatEntity interessat " +	
			"			where (lower(interessat.documentNumero||' '||interessat.nom||' '||interessat.llinatge1||' '||interessat.llinatge2) like lower('%'||:interessat||'%')" +
			"					or lower(interessat.raoSocial) like lower('%'||:interessat||'%')" +
			"					or lower(interessat.documentNumero) like lower('%'||:interessat||'%')))) "			
			)
	Page<ExpedientPeticioEntity> findByEntitatAndFiltre(
			@Param("entitat") EntitatEntity entitat,
			@Param("rolActual") String rolActual,
			@Param("metaExpedientsPermesos") List<MetaExpedientEntity> metaExpedientsPermesos,
			@Param("organsPermesos") List<String> organsPermesos,
			@Param("hasPermisAdminComu") boolean hasPermisAdminComu,
			@Param("esNullMetaExpedient") boolean esNullMetaExpedient,
			@Param("metaExpedient") MetaExpedientEntity metaExpedient,
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
			@Param("esNullInteressat") boolean esNullInteressat, 
			@Param("interessat") String interessat, 
			Pageable pageable);
	

	
	@Query("select " +
			"	count(ep) " +
			"from " +
			"    ExpedientPeticioEntity ep left join ep.metaExpedient me " +
			"where " +
			"ep.registre.entitat = :entitat " +
			"and ((:rolActual = 'IPA_ADMIN') " +
			"	or (:rolActual = 'IPA_ORGAN_ADMIN' and ((me.organGestor is not null and ep.metaExpedient in (:metaExpedientsPermesos)) or (me.organGestor is null and :hasPermisAdminComu = true and ep.registre.destiCodi in (:organsPermesos)))) " +
			"	or (:rolActual = 'tothom' and ep.metaExpedient in (:metaExpedientsPermesos))) " +	
			"and ep.estat='PENDENT' " 
			)
	long countAnotacionsPendentsPerMetaExpedients(
			@Param("entitat") EntitatEntity entitat,
			@Param("rolActual") String rolActual,
			@Param("metaExpedientsPermesos") List<MetaExpedientEntity> metaExpedientsPermesos,
			@Param("organsPermesos") List<String> organsPermesos,
			@Param("hasPermisAdminComu") boolean hasPermisAdminComu);



	@Query("select " +
			"	e.id " +
			"from " +
			"	ExpedientPeticioEntity e " +
			"where " +
			"e.pendentCanviEstatDistribucio = true " +
			"and e.reintentsCanviEstatDistribucio < :reintents")
	List<Long> findIdsPendentsCanviEstat(
			@Param("reintents") int reintents);

	
	@Query("from" +
			"    ExpedientPeticioEntity ep " +
			"where " +
			"ep.registre.entitat = :entitat " +
			"and (:nomesPendentEnviarDistribucio = false or ep.pendentCanviEstatDistribucio = true) " +
			"and (:esNullIdentificador = true or lower(ep.identificador) like lower('%' || :identificador || '%')) " +
			"and (:esNullDataInici = true or ep.dataAlta >= :dataInici) " +
			"and (:esNullDataFi = true or ep.dataAlta <= :dataFi) " +
			"and (:esNullEstat = true or " +
//			"							(:estat = 'CONSULTA_ERROR' and ep.estat = es.caib.ripea.core.api.dto.ExpedientPeticioEstatEnumDto.CREAT) or " +
			"							(:estat = 'PENDENT' and ep.estat = es.caib.ripea.core.api.dto.ExpedientPeticioEstatEnumDto.PENDENT) or " +
			"							(:estat = 'ACCEPTAT' and (ep.estat = es.caib.ripea.core.api.dto.ExpedientPeticioEstatEnumDto.PROCESSAT_PENDENT or ep.estat = es.caib.ripea.core.api.dto.ExpedientPeticioEstatEnumDto.PROCESSAT_NOTIFICAT)) or " +
			" 							(:estat = 'REBUTJAT' and ep.estat = es.caib.ripea.core.api.dto.ExpedientPeticioEstatEnumDto.REBUTJAT)) " )
	Page<ExpedientPeticioEntity> findPendentsCanviEstatDistribucio(
			@Param("entitat") EntitatEntity entitat,
			@Param("esNullIdentificador") boolean esNullIdentificador,
			@Param("identificador") String identificador,
			@Param("esNullDataInici") boolean esNullDataInici,
			@Param("dataInici") Date dataInici,
			@Param("esNullDataFi") boolean esNullDataFi,
			@Param("dataFi") Date dataFi,
			@Param("esNullEstat") boolean esNullEstat,
			@Param("estat") String estat,	
			@Param("nomesPendentEnviarDistribucio") boolean nomesPendentEnviarDistribucio,
			Pageable pageable);

	
	@Query(	"select " +
			"    ep.id " +
			"from " +
			"    ExpedientPeticioEntity ep " +
			"where " +
			"ep.registre.entitat = :entitat " +
			"and (:nomesPendentEnviarDistribucio = false or ep.pendentCanviEstatDistribucio = true) " +
			"and (:esNullIdentificador = true or lower(ep.identificador) like lower('%' || :identificador || '%')) " +
			"and (:esNullDataInici = true or ep.dataAlta >= :dataInici) " +
			"and (:esNullDataFi = true or ep.dataAlta <= :dataFi) " +
			"and (:esNullEstat = true or " +
//			"							(:estat = 'CONSULTA_ERROR' and ep.estat = es.caib.ripea.core.api.dto.ExpedientPeticioEstatEnumDto.CREAT) or " +
			"							(:estat = 'PENDENT' and ep.estat = es.caib.ripea.core.api.dto.ExpedientPeticioEstatEnumDto.PENDENT) or " +
			"							(:estat = 'ACCEPTAT' and (ep.estat = es.caib.ripea.core.api.dto.ExpedientPeticioEstatEnumDto.PROCESSAT_PENDENT or ep.estat = es.caib.ripea.core.api.dto.ExpedientPeticioEstatEnumDto.PROCESSAT_NOTIFICAT)) or " +
			" 							(:estat = 'REBUTJAT' and ep.estat = es.caib.ripea.core.api.dto.ExpedientPeticioEstatEnumDto.REBUTJAT)) " )
	List<Long> findIdsPendentsCanviEstatDistribucio(
			@Param("entitat") EntitatEntity entitat,
			@Param("esNullIdentificador") boolean esNullIdentificador,
			@Param("identificador") String identificador,
			@Param("esNullDataInici") boolean esNullDataInici,
			@Param("dataInici") Date dataInici,
			@Param("esNullDataFi") boolean esNullDataFi,
			@Param("dataFi") Date dataFi,
			@Param("esNullEstat") boolean esNullEstat,
			@Param("estat") String estat,	
			@Param("nomesPendentEnviarDistribucio") boolean nomesPendentEnviarDistribucio);
	


}
