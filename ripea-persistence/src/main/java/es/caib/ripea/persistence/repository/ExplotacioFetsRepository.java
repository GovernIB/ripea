package es.caib.ripea.persistence.repository;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import es.caib.ripea.persistence.entity.ExplotacioDimensioEntity;
import es.caib.ripea.persistence.entity.ExplotacioFetsEntity;
import es.caib.ripea.persistence.entity.ExplotacioTempsEntity;
import es.caib.ripea.service.intf.dto.explotacio.ExplotFetsAnotacionsDto;
import es.caib.ripea.service.intf.dto.explotacio.ExplotFetsExpedientsObertsDto;
import es.caib.ripea.service.intf.dto.explotacio.ExplotFetsExpedientsTancatsDto;
import es.caib.ripea.service.intf.dto.explotacio.ExplotFetsNotificacionsDto;
import es.caib.ripea.service.intf.dto.explotacio.ExplotFetsPinbalDto;
import es.caib.ripea.service.intf.dto.explotacio.ExplotFetsPortafirmesDto;
import es.caib.ripea.service.intf.dto.explotacio.ExplotFetsProcedimentsDto;
import es.caib.ripea.service.intf.dto.explotacio.ExplotFetsTasquesDto;

/**
 * Consultes per a la generació de les dades d'explotació.
 *
 * Cada grup d'indicadors es resol amb una única consulta agregada per taula d'origen: totes les
 * variants que abans es demanaven amb una consulta per estat (i una segona consulta per obtenir el
 * total del dia anterior) es calculen ara amb expressions "sum(case when ... then 1 else 0 end)"
 * dins d'un mateix "group by". Com que el conjunt del dia anterior sempre és un subconjunt del
 * d'avui (només canvia el tall de la data de creació), els dos talls caben a la mateixa consulta.
 */
@Component
public interface ExplotacioFetsRepository extends JpaRepository<ExplotacioFetsEntity, Long> {

	public List<ExplotacioFetsEntity> findByTemps(@Param("temps") ExplotacioTempsEntity temps);

	public ExplotacioFetsEntity findByDimensioAndTemps(
			@Param("dimensio") ExplotacioDimensioEntity dimensio,
			@Param("temps") ExplotacioTempsEntity temps);

	/**
	 * PROCEDIMENTS *
	 */

	//Procediments i serveis actius fins a la data especificada.
	//S'exclouen els metaexpedients sense tipus per no generar dimensions amb tots els indicadors a zero.
	@Query(	"select new es.caib.ripea.service.intf.dto.explotacio.ExplotFetsProcedimentsDto( "
			+ "e.entitat.id, e.id, e.organGestor.id, e.createdBy, "
			//PROCEDIMENTS_ACTIUS_TOTAL
			+ "sum(case when e.tipusProcedimentServei = es.caib.ripea.service.intf.dto.TipusProcedimentServeiEnum.PROCEDIMENT then 1 else 0 end), "
			//SERVEIS_ACTIUS_TOTAL
			+ "sum(case when e.tipusProcedimentServei = es.caib.ripea.service.intf.dto.TipusProcedimentServeiEnum.SERVEI then 1 else 0 end) ) " +
    "from MetaExpedientEntity e " +
	"where e.createdDate <= :dataFins and e.actiu = 1 and e.tipusProcedimentServei is not null " +
    "group by e.entitat.id, e.id, e.organGestor.id, e.createdBy " +
    "order by e.entitat.id, e.id, e.organGestor.id, e.createdBy")
	List<ExplotFetsProcedimentsDto> getProcedimentsPerDimensio(@Param("dataFins") LocalDateTime dataFins);

	/**
	 * EXPEDIENT *
	 */

	//Expedients oberts: el total són els creats fins a la data, i el parcial els creats dins del dia.
	@Query(	"select new es.caib.ripea.service.intf.dto.explotacio.ExplotFetsExpedientsObertsDto( "
			+ "e.entitat.id, e.metaNode.id, e.organGestor.id, e.createdBy, "
			//EXP_OBERTS_TOTAL
			+ "count(e), "
			//EXP_OBERTS
			+ "sum(case when e.createdDate > :dataDesde then 1 else 0 end) ) " +
    "from ExpedientEntity e " +
	"where e.createdDate <= :dataFins and e.tipus=0 and e.esborrat = 0 " +
    "group by e.entitat.id, e.metaNode.id, e.organGestor.id, e.createdBy " +
    "order by e.entitat.id, e.metaNode.id, e.organGestor.id, e.createdBy")
	List<ExplotFetsExpedientsObertsDto> getExpedientsObertsPerDimensio(
			@Param("dataDesde") LocalDateTime dataDesde,
			@Param("dataFins") LocalDateTime dataFins);

	//Expedients tancats: es consulten a part perquè s'ancoren a la data de tancament i no a la de creació.
	@Query(	"select new es.caib.ripea.service.intf.dto.explotacio.ExplotFetsExpedientsTancatsDto( "
			+ "e.entitat.id, e.metaNode.id, e.organGestor.id, e.createdBy, "
			//EXP_TANCATS_TOTAL
			+ "count(e), "
			//EXP_TANCATS
			+ "sum(case when e.tancatData > :dataDesde then 1 else 0 end) ) " +
    "from ExpedientEntity e " +
	"where e.tancatData <= :dataFins and e.tipus=0 and e.esborrat = 0 " +
    "group by e.entitat.id, e.metaNode.id, e.organGestor.id, e.createdBy " +
    "order by e.entitat.id, e.metaNode.id, e.organGestor.id, e.createdBy")
	List<ExplotFetsExpedientsTancatsDto> getExpedientsTancatsPerDimensio(
			@Param("dataDesde") Date dataDesde,
			@Param("dataFins") Date dataFins);

	/**
	 * TASQUES *
	 */
	//No es correcte utilitzar la data de creació com a filtre per cercar tasques en un estat determinat.
	//Per tant s'obtenen els totals del dia actual i del dia anterior, i la dada parcial es calcula restant-los.
	//Dins de termini: la tasca no té data límit, o s'ha finalitzat abans o just a la data límit.
	//Si no es coneix la data de finalització no es pot afirmar que sigui fora de termini, i per tant es compta aquí.
	//Fora de termini: només es compta quan es pot demostrar, és a dir, quan les dues dates estan informades
	//i la data de finalització és posterior a la data límit (dins + fora = total de tasques finalitzades).
	@Query(	"select new es.caib.ripea.service.intf.dto.explotacio.ExplotFetsTasquesDto( "
			+ "e.expedient.entitat.id, e.metaExpedientTasca.metaExpedient.id, e.expedient.organGestor.id, e.createdBy, "
			//TAS_PENDENTS_TOTAL i el total d'ahir (per TAS_PENDENTS)
			+ "sum(case when e.estat = es.caib.ripea.service.intf.dto.TascaEstatEnumDto.PENDENT then 1 else 0 end), "
			+ "sum(case when e.estat = es.caib.ripea.service.intf.dto.TascaEstatEnumDto.PENDENT and e.createdDate <= :dataAhirFins then 1 else 0 end), "
			//TAS_INICIADES_TOTAL i el total d'ahir (per TAS_INICIADES)
			+ "sum(case when e.estat = es.caib.ripea.service.intf.dto.TascaEstatEnumDto.INICIADA then 1 else 0 end), "
			+ "sum(case when e.estat = es.caib.ripea.service.intf.dto.TascaEstatEnumDto.INICIADA and e.createdDate <= :dataAhirFins then 1 else 0 end), "
			//TAS_FIN_DINS_TERMINI_TOTAL i el total d'ahir (per TAS_FIN_DINS_TERMINI)
			+ "sum(case when e.estat = es.caib.ripea.service.intf.dto.TascaEstatEnumDto.FINALITZADA and (e.dataLimit is null or e.dataFi is null or e.dataFi <= e.dataLimit) then 1 else 0 end), "
			+ "sum(case when e.estat = es.caib.ripea.service.intf.dto.TascaEstatEnumDto.FINALITZADA and (e.dataLimit is null or e.dataFi is null or e.dataFi <= e.dataLimit) and e.createdDate <= :dataAhirFins then 1 else 0 end), "
			//TAS_FIN_FORA_TERMINI_TOTAL i el total d'ahir (per TAS_FIN_FORA_TERMINI)
			+ "sum(case when e.estat = es.caib.ripea.service.intf.dto.TascaEstatEnumDto.FINALITZADA and (e.dataLimit is not null and e.dataFi is not null and e.dataFi > e.dataLimit) then 1 else 0 end), "
			+ "sum(case when e.estat = es.caib.ripea.service.intf.dto.TascaEstatEnumDto.FINALITZADA and (e.dataLimit is not null and e.dataFi is not null and e.dataFi > e.dataLimit) and e.createdDate <= :dataAhirFins then 1 else 0 end), "
			//TAS_CANCELADES_TOTAL i el total d'ahir (per TAS_CANCELADES)
			+ "sum(case when e.estat = es.caib.ripea.service.intf.dto.TascaEstatEnumDto.CANCELLADA then 1 else 0 end), "
			+ "sum(case when e.estat = es.caib.ripea.service.intf.dto.TascaEstatEnumDto.CANCELLADA and e.createdDate <= :dataAhirFins then 1 else 0 end), "
			//TAS_REBUTJADES_TOTAL i el total d'ahir (per TAS_REBUTJADES)
			+ "sum(case when e.estat = es.caib.ripea.service.intf.dto.TascaEstatEnumDto.REBUTJADA then 1 else 0 end), "
			+ "sum(case when e.estat = es.caib.ripea.service.intf.dto.TascaEstatEnumDto.REBUTJADA and e.createdDate <= :dataAhirFins then 1 else 0 end), "
			//TAS_AGAFADES_TOTAL i el total d'ahir (per TAS_AGAFADES)
			+ "sum(case when e.estat = es.caib.ripea.service.intf.dto.TascaEstatEnumDto.AGAFADA then 1 else 0 end), "
			+ "sum(case when e.estat = es.caib.ripea.service.intf.dto.TascaEstatEnumDto.AGAFADA and e.createdDate <= :dataAhirFins then 1 else 0 end) ) " +
    "from ExpedientTascaEntity e " +
	"where e.createdDate <= :dataFins and e.expedient.esborrat = 0 " +
    "group by e.expedient.entitat.id, e.metaExpedientTasca.metaExpedient.id, e.expedient.organGestor.id, e.createdBy " +
    "order by e.expedient.entitat.id, e.metaExpedientTasca.metaExpedient.id, e.expedient.organGestor.id, e.createdBy")
	List<ExplotFetsTasquesDto> getTasquesPerDimensio(
			@Param("dataFins") LocalDateTime dataFins,
			@Param("dataAhirFins") LocalDateTime dataAhirFins);

	/**
	 * ANOTACIONS *
	 */
	//Les anotacions noves es compten pel rang de la data d'alta. Per als estats no és correcte
	//utilitzar la data d'alta com a filtre, i per això la dada parcial es calcula restant el total d'ahir.
	@Query(	"select new es.caib.ripea.service.intf.dto.explotacio.ExplotFetsAnotacionsDto( "
			+ "e.metaExpedient.entitat.id, e.metaExpedient.id, e.metaExpedient.organGestor.id, e.createdBy, "
			//ANO_NOVES_TOTAL
			+ "count(e), "
			//ANO_NOVES
			+ "sum(case when e.dataAlta > :dataDesde then 1 else 0 end), "
			//ANO_PROCESSADES_TOTAL i el total d'ahir (per ANO_PROCESSADES)
			+ "sum(case when e.estat in (es.caib.ripea.service.intf.dto.ExpedientPeticioEstatEnumDto.PROCESSAT_PENDENT, es.caib.ripea.service.intf.dto.ExpedientPeticioEstatEnumDto.PROCESSAT_NOTIFICAT) then 1 else 0 end), "
			+ "sum(case when e.estat in (es.caib.ripea.service.intf.dto.ExpedientPeticioEstatEnumDto.PROCESSAT_PENDENT, es.caib.ripea.service.intf.dto.ExpedientPeticioEstatEnumDto.PROCESSAT_NOTIFICAT) and e.dataAlta <= :dataAhirFins then 1 else 0 end), "
			//ANO_REBUTJADES_TOTAL i el total d'ahir (per ANO_REBUTJADES)
			+ "sum(case when e.estat = es.caib.ripea.service.intf.dto.ExpedientPeticioEstatEnumDto.REBUTJAT then 1 else 0 end), "
			+ "sum(case when e.estat = es.caib.ripea.service.intf.dto.ExpedientPeticioEstatEnumDto.REBUTJAT and e.dataAlta <= :dataAhirFins then 1 else 0 end) ) " +
    "from ExpedientPeticioEntity e " +
	"where e.dataAlta <= :dataFins " +
    "group by e.metaExpedient.entitat.id, e.metaExpedient.id, e.metaExpedient.organGestor.id, e.createdBy " +
    "order by e.metaExpedient.entitat.id, e.metaExpedient.id, e.metaExpedient.organGestor.id, e.createdBy")
	List<ExplotFetsAnotacionsDto> getAnotacionsPerDimensio(
			@Param("dataDesde") Date dataDesde,
			@Param("dataFins") Date dataFins,
			@Param("dataAhirFins") Date dataAhirFins);

	/**
	 * CONSULTES PINBAL *
	 */
	//Els parcials del dia es compten pel rang de la data de creació, igual que abans.
	@Query(	"select new es.caib.ripea.service.intf.dto.explotacio.ExplotFetsPinbalDto( "
			+ "e.entitat.id, e.metaExpedient.id, e.metaExpedient.organGestor.id, e.createdBy, "
			//PIN_ENVIAMENTS_OK_TOTAL i PIN_ENVIAMENTS_OK
			+ "sum(case when e.estat = es.caib.ripea.service.intf.dto.ConsultaPinbalEstatEnumDto.TRAMITADA then 1 else 0 end), "
			+ "sum(case when e.estat = es.caib.ripea.service.intf.dto.ConsultaPinbalEstatEnumDto.TRAMITADA and e.createdDate > :dataDesde then 1 else 0 end), "
			//PIN_ENVIAMENTS_ERROR_TOTAL i PIN_ENVIAMENTS_ERROR
			+ "sum(case when e.estat = es.caib.ripea.service.intf.dto.ConsultaPinbalEstatEnumDto.ERROR then 1 else 0 end), "
			+ "sum(case when e.estat = es.caib.ripea.service.intf.dto.ConsultaPinbalEstatEnumDto.ERROR and e.createdDate > :dataDesde then 1 else 0 end) ) " +
    "from ConsultaPinbalEntity e " +
	"where e.createdDate <= :dataFins and e.expedient.esborrat = 0 " +
    "group by e.entitat.id, e.metaExpedient.id, e.metaExpedient.organGestor.id, e.createdBy " +
    "order by e.entitat.id, e.metaExpedient.id, e.metaExpedient.organGestor.id, e.createdBy")
	List<ExplotFetsPinbalDto> getPinbalEnviamentsPerDimensio(
			@Param("dataDesde") LocalDateTime dataDesde,
			@Param("dataFins") LocalDateTime dataFins);

	/**
	 * NOTIFICACIONS I COMUNICACIONS *
	 */
	//No es correcte utilitzar la data de creació com a filtre per cercar notificacions en un estat determinat.
	//Per tant s'obtenen els totals del dia actual i del dia anterior, i la dada parcial es calcula restant-los.
	//S'exclouen les notificacions sense estat per no generar dimensions amb tots els indicadors a zero.
	@Query(	"select new es.caib.ripea.service.intf.dto.explotacio.ExplotFetsNotificacionsDto( "
			+ "e.expedient.entitat.id, e.expedient.metaExpedient.id, e.expedient.organGestor.id, e.createdBy, "
			//NOT_ENVIADES_TOTAL i el total d'ahir (per NOT_ENVIADES)
			+ "sum(case when e.notificacioEstat = es.caib.ripea.service.intf.dto.DocumentNotificacioEstatEnumDto.ENVIADA then 1 else 0 end), "
			+ "sum(case when e.notificacioEstat = es.caib.ripea.service.intf.dto.DocumentNotificacioEstatEnumDto.ENVIADA and e.createdDate <= :dataAhirFins then 1 else 0 end), "
			//NOT_PENDENTS_TOTAL i el total d'ahir (per NOT_PENDENTS)
			+ "sum(case when e.notificacioEstat = es.caib.ripea.service.intf.dto.DocumentNotificacioEstatEnumDto.PENDENT then 1 else 0 end), "
			+ "sum(case when e.notificacioEstat = es.caib.ripea.service.intf.dto.DocumentNotificacioEstatEnumDto.PENDENT and e.createdDate <= :dataAhirFins then 1 else 0 end), "
			//NOT_REGISTRADES_TOTAL i el total d'ahir (per NOT_REGISTRADES)
			+ "sum(case when e.notificacioEstat = es.caib.ripea.service.intf.dto.DocumentNotificacioEstatEnumDto.REGISTRADA then 1 else 0 end), "
			+ "sum(case when e.notificacioEstat = es.caib.ripea.service.intf.dto.DocumentNotificacioEstatEnumDto.REGISTRADA and e.createdDate <= :dataAhirFins then 1 else 0 end), "
			//NOT_FINALITZADES_TOTAL i el total d'ahir (per NOT_FINALITZADES)
			+ "sum(case when e.notificacioEstat = es.caib.ripea.service.intf.dto.DocumentNotificacioEstatEnumDto.FINALITZADA then 1 else 0 end), "
			+ "sum(case when e.notificacioEstat = es.caib.ripea.service.intf.dto.DocumentNotificacioEstatEnumDto.FINALITZADA and e.createdDate <= :dataAhirFins then 1 else 0 end), "
			//NOT_PROCESSADES_TOTAL i el total d'ahir (per NOT_PROCESSADES)
			+ "sum(case when e.notificacioEstat = es.caib.ripea.service.intf.dto.DocumentNotificacioEstatEnumDto.PROCESSADA then 1 else 0 end), "
			+ "sum(case when e.notificacioEstat = es.caib.ripea.service.intf.dto.DocumentNotificacioEstatEnumDto.PROCESSADA and e.createdDate <= :dataAhirFins then 1 else 0 end), "
			//NOT_ENVIADES_ERROR_TOTAL i el total d'ahir (per NOT_ENVIADES_ERROR)
			+ "sum(case when e.notificacioEstat = es.caib.ripea.service.intf.dto.DocumentNotificacioEstatEnumDto.ENVIADA_AMB_ERRORS then 1 else 0 end), "
			+ "sum(case when e.notificacioEstat = es.caib.ripea.service.intf.dto.DocumentNotificacioEstatEnumDto.ENVIADA_AMB_ERRORS and e.createdDate <= :dataAhirFins then 1 else 0 end), "
			//NOT_FINALITZADES_ERROR_TOTAL i el total d'ahir (per NOT_FINALITZADES_ERROR)
			+ "sum(case when e.notificacioEstat = es.caib.ripea.service.intf.dto.DocumentNotificacioEstatEnumDto.FINALITZADA_AMB_ERRORS then 1 else 0 end), "
			+ "sum(case when e.notificacioEstat = es.caib.ripea.service.intf.dto.DocumentNotificacioEstatEnumDto.FINALITZADA_AMB_ERRORS and e.createdDate <= :dataAhirFins then 1 else 0 end) ) " +
    "from DocumentNotificacioEntity e " +
	"where e.createdDate <= :dataFins and e.expedient.esborrat = 0 and e.notificacioEstat is not null " +
    "group by e.expedient.entitat.id, e.expedient.metaExpedient.id, e.expedient.organGestor.id, e.createdBy " +
    "order by e.expedient.entitat.id, e.expedient.metaExpedient.id, e.expedient.organGestor.id, e.createdBy")
	List<ExplotFetsNotificacionsDto> getNotificacionsPerDimensio(
			@Param("dataFins") LocalDateTime dataFins,
			@Param("dataAhirFins") LocalDateTime dataAhirFins);

	/**
	 * PORTAFIRMES *
	 */
	//No es correcte utilitzar la data de creació com a filtre per cercar firmes en un estat determinat.
	//Per tant s'obtenen els totals del dia actual i del dia anterior, i la dada parcial es calcula restant-los.
	//Els enviaments sense estat de callback són els que s'han enviat a portafirmes però encara no
	//n'hem rebut resposta: es compten a FIR_ENVIADES i no s'exclouen de la consulta.
	@Query(	"select new es.caib.ripea.service.intf.dto.explotacio.ExplotFetsPortafirmesDto( "
			+ "e.expedient.entitat.id, e.expedient.metaExpedient.id, e.expedient.organGestor.id, e.createdBy, "
			//FIR_ENVIADES_TOTAL i el total d'ahir (per FIR_ENVIADES)
			+ "sum(case when e.callbackEstat is null then 1 else 0 end), "
			+ "sum(case when e.callbackEstat is null and e.createdDate <= :dataAhirFins then 1 else 0 end), "
			//FIR_INICIADES_TOTAL i el total d'ahir (per FIR_INICIADES)
			+ "sum(case when e.callbackEstat = es.caib.ripea.service.intf.dto.PortafirmesCallbackEstatEnumDto.INICIAT then 1 else 0 end), "
			+ "sum(case when e.callbackEstat = es.caib.ripea.service.intf.dto.PortafirmesCallbackEstatEnumDto.INICIAT and e.createdDate <= :dataAhirFins then 1 else 0 end), "
			//FIR_PAUSADES_TOTAL i el total d'ahir (per FIR_PAUSADES)
			+ "sum(case when e.callbackEstat = es.caib.ripea.service.intf.dto.PortafirmesCallbackEstatEnumDto.PAUSAT then 1 else 0 end), "
			+ "sum(case when e.callbackEstat = es.caib.ripea.service.intf.dto.PortafirmesCallbackEstatEnumDto.PAUSAT and e.createdDate <= :dataAhirFins then 1 else 0 end), "
			//FIR_FIRMADES_TOTAL i el total d'ahir (per FIR_FIRMADES)
			+ "sum(case when e.callbackEstat = es.caib.ripea.service.intf.dto.PortafirmesCallbackEstatEnumDto.FIRMAT then 1 else 0 end), "
			+ "sum(case when e.callbackEstat = es.caib.ripea.service.intf.dto.PortafirmesCallbackEstatEnumDto.FIRMAT and e.createdDate <= :dataAhirFins then 1 else 0 end), "
			//FIR_REBUTJADES_TOTAL i el total d'ahir (per FIR_REBUTJADES)
			+ "sum(case when e.callbackEstat = es.caib.ripea.service.intf.dto.PortafirmesCallbackEstatEnumDto.REBUTJAT then 1 else 0 end), "
			+ "sum(case when e.callbackEstat = es.caib.ripea.service.intf.dto.PortafirmesCallbackEstatEnumDto.REBUTJAT and e.createdDate <= :dataAhirFins then 1 else 0 end), "
			//FIR_PARCIALS_TOTAL i el total d'ahir (per FIR_PARCIALS)
			+ "sum(case when e.callbackEstat = es.caib.ripea.service.intf.dto.PortafirmesCallbackEstatEnumDto.PARCIAL then 1 else 0 end), "
			+ "sum(case when e.callbackEstat = es.caib.ripea.service.intf.dto.PortafirmesCallbackEstatEnumDto.PARCIAL and e.createdDate <= :dataAhirFins then 1 else 0 end) ) " +
    "from DocumentPortafirmesEntity e " +
	"where e.createdDate <= :dataFins and e.expedient.esborrat = 0 " +
    "group by e.expedient.entitat.id, e.expedient.metaExpedient.id, e.expedient.organGestor.id, e.createdBy " +
    "order by e.expedient.entitat.id, e.expedient.metaExpedient.id, e.expedient.organGestor.id, e.createdBy")
	List<ExplotFetsPortafirmesDto> getPortafirmesPerDimensio(
			@Param("dataFins") LocalDateTime dataFins,
			@Param("dataAhirFins") LocalDateTime dataAhirFins);
}
