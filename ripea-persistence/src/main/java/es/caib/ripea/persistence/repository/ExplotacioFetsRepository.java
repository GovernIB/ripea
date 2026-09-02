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
import es.caib.ripea.service.intf.dto.explotacio.ExplotFetsExpedientsCreatsDto;
import es.caib.ripea.service.intf.dto.explotacio.ExplotFetsExpedientsTancatsDto;
import es.caib.ripea.service.intf.dto.explotacio.ExplotFetsNotificacionsDto;
import es.caib.ripea.service.intf.dto.explotacio.ExplotFetsPinbalDto;
import es.caib.ripea.service.intf.dto.explotacio.ExplotFetsPortafirmesDto;
import es.caib.ripea.service.intf.dto.explotacio.ExplotFetsProcedimentsDto;
import es.caib.ripea.service.intf.dto.explotacio.ExplotFetsTasquesDto;

/**
 * Consultes per a la generació de les dades d'explotació.
 *
 * Aquestes consultes només calculen els indicadors ACUMULATS (els acabats en _TOTAL): la foto de
 * cada dimensió a la data demanada, amb una única consulta agregada per taula d'origen que resol
 * totes les variants amb expressions "sum(case when ... then 1 else 0 end)" dins d'un mateix
 * "group by".
 *
 * Les dades DIÀRIES no es calculen aquí: s'obtenen restant a cada total el mateix indicador de la
 * mateixa dimensió a la foto del dia anterior, que ja està guardada a ipa_explot_fet (veure
 * SegonPlaServiceImpl.calcularParcialsDiaris). Abans es feien amb un segon tall per data de creació
 * dins de la mateixa consulta, però això comptava "files creades dins del dia que ARA estan en
 * l'estat X" en lloc de "files que han passat a l'estat X", i els canvis d'estat de files creades
 * dies abans no es comptabilitzaven mai.
 */
@Component
public interface ExplotacioFetsRepository extends JpaRepository<ExplotacioFetsEntity, Long> {

	public List<ExplotacioFetsEntity> findByTemps(@Param("temps") ExplotacioTempsEntity temps);

	public ExplotacioFetsEntity findByDimensioAndTemps(
			@Param("dimensio") ExplotacioDimensioEntity dimensio,
			@Param("temps") ExplotacioTempsEntity temps);

	//Fets d'un dia amb la clau de la seva dimensio (entitat-procediment-organ-usuari), per poder
	//comparar-los amb els del dia seguent sense haver d'inicialitzar la dimensio fila a fila.
	//Els join de procediment, organ i usuari han de ser LEFT: son opcionals a la dimensio.
	@Query(	"select d.entitat.id, dprc.id, dorg.id, dusu.codi, f " +
			"from ExplotacioFetsEntity f " +
			"join f.dimensio d " +
			"left join d.procediment dprc " +
			"left join d.organGestor dorg " +
			"left join d.usuari dusu " +
			"where f.temps = :temps")
	List<Object[]> findFetsAmbClauDimensioByTemps(@Param("temps") ExplotacioTempsEntity temps);

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

	//EXP_CREAT_TOTAL: expedients creats fins a la data, tant oberts com tancats.
	@Query(	"select new es.caib.ripea.service.intf.dto.explotacio.ExplotFetsExpedientsCreatsDto( "
			+ "e.entitat.id, e.metaNode.id, e.organGestor.id, e.createdBy, "
			+ "count(e) ) " +
    "from ExpedientEntity e " +
	"where e.createdDate <= :dataFins and e.tipus=0 and e.esborrat = 0 " +
    "group by e.entitat.id, e.metaNode.id, e.organGestor.id, e.createdBy " +
    "order by e.entitat.id, e.metaNode.id, e.organGestor.id, e.createdBy")
	List<ExplotFetsExpedientsCreatsDto> getExpedientsCreatsPerDimensio(@Param("dataFins") LocalDateTime dataFins);

	//EXP_TANCATS_TOTAL: expedients tancats fins a la data. Es consulten a part perquè s'ancoren a
	//la data de tancament i no a la de creació.
	@Query(	"select new es.caib.ripea.service.intf.dto.explotacio.ExplotFetsExpedientsTancatsDto( "
			+ "e.entitat.id, e.metaNode.id, e.organGestor.id, e.createdBy, "
			+ "count(e) ) " +
    "from ExpedientEntity e " +
	"where e.tancatData <= :dataFins and e.tipus=0 and e.esborrat = 0 " +
    "group by e.entitat.id, e.metaNode.id, e.organGestor.id, e.createdBy " +
    "order by e.entitat.id, e.metaNode.id, e.organGestor.id, e.createdBy")
	List<ExplotFetsExpedientsTancatsDto> getExpedientsTancatsPerDimensio(@Param("dataFins") Date dataFins);

	/**
	 * TASQUES *
	 */
	//Dins de termini: la tasca no té data límit, o s'ha finalitzat abans o just a la data límit.
	//Si no es coneix la data de finalització no es pot afirmar que sigui fora de termini, i per tant es compta aquí.
	//Fora de termini: només es compta quan es pot demostrar, és a dir, quan les dues dates estan informades
	//i la data de finalització és posterior a la data límit (dins + fora = total de tasques finalitzades).
	//Creades (TAS_CREADES_TOTAL): totes les tasques, sense mirar l'estat.
	//Fora de termini no finalitzades (TAS_NOTFIN_FORA_TERMINI_TOTAL): tasques amb data limit informada i ja
	//superada a la data de tall que encara no estan finalitzades. Es complementari de TAS_FIN_FORA_TERMINI:
	//la suma dels dos indicadors son totes les tasques fora de termini, sigui quin sigui l'estat.
	//Com que la data limit es un Date, el tall d'aquest indicador arriba amb un parametre propi.
	@Query(	"select new es.caib.ripea.service.intf.dto.explotacio.ExplotFetsTasquesDto( "
			+ "e.expedient.entitat.id, e.metaExpedientTasca.metaExpedient.id, e.expedient.organGestor.id, e.createdBy, "
			//TAS_PENDENTS_TOTAL
			+ "sum(case when e.estat = es.caib.ripea.service.intf.dto.TascaEstatEnumDto.PENDENT then 1 else 0 end), "
			//TAS_INICIADES_TOTAL
			+ "sum(case when e.estat = es.caib.ripea.service.intf.dto.TascaEstatEnumDto.INICIADA then 1 else 0 end), "
			//TAS_FIN_DINS_TERMINI_TOTAL
			+ "sum(case when e.estat = es.caib.ripea.service.intf.dto.TascaEstatEnumDto.FINALITZADA and (e.dataLimit is null or e.dataFi is null or e.dataFi <= e.dataLimit) then 1 else 0 end), "
			//TAS_FIN_FORA_TERMINI_TOTAL
			+ "sum(case when e.estat = es.caib.ripea.service.intf.dto.TascaEstatEnumDto.FINALITZADA and (e.dataLimit is not null and e.dataFi is not null and e.dataFi > e.dataLimit) then 1 else 0 end), "
			//TAS_CANCELADES_TOTAL
			+ "sum(case when e.estat = es.caib.ripea.service.intf.dto.TascaEstatEnumDto.CANCELLADA then 1 else 0 end), "
			//TAS_REBUTJADES_TOTAL
			+ "sum(case when e.estat = es.caib.ripea.service.intf.dto.TascaEstatEnumDto.REBUTJADA then 1 else 0 end), "
			//TAS_AGAFADES_TOTAL
			+ "sum(case when e.estat = es.caib.ripea.service.intf.dto.TascaEstatEnumDto.AGAFADA then 1 else 0 end), "
			//TAS_CREADES_TOTAL
			+ "count(e), "
			//TAS_NOTFIN_FORA_TERMINI_TOTAL
			+ "sum(case when e.estat <> es.caib.ripea.service.intf.dto.TascaEstatEnumDto.FINALITZADA and e.dataLimit is not null and e.dataLimit < :dataLimitFins then 1 else 0 end) ) " +
    "from ExpedientTascaEntity e " +
	"where e.createdDate <= :dataFins and e.expedient.esborrat = 0 " +
    "group by e.expedient.entitat.id, e.metaExpedientTasca.metaExpedient.id, e.expedient.organGestor.id, e.createdBy " +
    "order by e.expedient.entitat.id, e.metaExpedientTasca.metaExpedient.id, e.expedient.organGestor.id, e.createdBy")
	List<ExplotFetsTasquesDto> getTasquesPerDimensio(
			@Param("dataFins") LocalDateTime dataFins,
			@Param("dataLimitFins") Date dataLimitFins);

	/**
	 * ANOTACIONS *
	 */
	@Query(	"select new es.caib.ripea.service.intf.dto.explotacio.ExplotFetsAnotacionsDto( "
			+ "e.metaExpedient.entitat.id, e.metaExpedient.id, e.metaExpedient.organGestor.id, e.createdBy, "
			//ANO_NOVES_TOTAL
			+ "count(e), "
			//ANO_PROCESSADES_TOTAL
			+ "sum(case when e.estat in (es.caib.ripea.service.intf.dto.ExpedientPeticioEstatEnumDto.PROCESSAT_PENDENT, es.caib.ripea.service.intf.dto.ExpedientPeticioEstatEnumDto.PROCESSAT_NOTIFICAT) then 1 else 0 end), "
			//ANO_REBUTJADES_TOTAL
			+ "sum(case when e.estat = es.caib.ripea.service.intf.dto.ExpedientPeticioEstatEnumDto.REBUTJAT then 1 else 0 end) ) " +
    "from ExpedientPeticioEntity e " +
	"where e.dataAlta <= :dataFins " +
    "group by e.metaExpedient.entitat.id, e.metaExpedient.id, e.metaExpedient.organGestor.id, e.createdBy " +
    "order by e.metaExpedient.entitat.id, e.metaExpedient.id, e.metaExpedient.organGestor.id, e.createdBy")
	List<ExplotFetsAnotacionsDto> getAnotacionsPerDimensio(@Param("dataFins") Date dataFins);

	/**
	 * CONSULTES PINBAL *
	 */
	@Query(	"select new es.caib.ripea.service.intf.dto.explotacio.ExplotFetsPinbalDto( "
			+ "e.entitat.id, e.metaExpedient.id, e.metaExpedient.organGestor.id, e.createdBy, "
			//PIN_ENVIAMENTS_OK_TOTAL
			+ "sum(case when e.estat = es.caib.ripea.service.intf.dto.ConsultaPinbalEstatEnumDto.TRAMITADA then 1 else 0 end), "
			//PIN_ENVIAMENTS_ERROR_TOTAL
			+ "sum(case when e.estat = es.caib.ripea.service.intf.dto.ConsultaPinbalEstatEnumDto.ERROR then 1 else 0 end) ) " +
    "from ConsultaPinbalEntity e " +
	"where e.createdDate <= :dataFins and e.expedient.esborrat = 0 " +
    "group by e.entitat.id, e.metaExpedient.id, e.metaExpedient.organGestor.id, e.createdBy " +
    "order by e.entitat.id, e.metaExpedient.id, e.metaExpedient.organGestor.id, e.createdBy")
	List<ExplotFetsPinbalDto> getPinbalEnviamentsPerDimensio(@Param("dataFins") LocalDateTime dataFins);

	/**
	 * NOTIFICACIONS I COMUNICACIONS *
	 */
	//S'exclouen les notificacions sense estat per no generar dimensions amb tots els indicadors a zero.
	@Query(	"select new es.caib.ripea.service.intf.dto.explotacio.ExplotFetsNotificacionsDto( "
			+ "e.expedient.entitat.id, e.expedient.metaExpedient.id, e.expedient.organGestor.id, e.createdBy, "
			//NOT_ENVIADES_TOTAL
			+ "sum(case when e.notificacioEstat = es.caib.ripea.service.intf.dto.DocumentNotificacioEstatEnumDto.ENVIADA then 1 else 0 end), "
			//NOT_PENDENTS_TOTAL
			+ "sum(case when e.notificacioEstat = es.caib.ripea.service.intf.dto.DocumentNotificacioEstatEnumDto.PENDENT then 1 else 0 end), "
			//NOT_REGISTRADES_TOTAL
			+ "sum(case when e.notificacioEstat = es.caib.ripea.service.intf.dto.DocumentNotificacioEstatEnumDto.REGISTRADA then 1 else 0 end), "
			//NOT_FINALITZADES_TOTAL
			+ "sum(case when e.notificacioEstat = es.caib.ripea.service.intf.dto.DocumentNotificacioEstatEnumDto.FINALITZADA then 1 else 0 end), "
			//NOT_PROCESSADES_TOTAL
			+ "sum(case when e.notificacioEstat = es.caib.ripea.service.intf.dto.DocumentNotificacioEstatEnumDto.PROCESSADA then 1 else 0 end), "
			//NOT_ENVIADES_ERROR_TOTAL
			+ "sum(case when e.notificacioEstat = es.caib.ripea.service.intf.dto.DocumentNotificacioEstatEnumDto.ENVIADA_AMB_ERRORS then 1 else 0 end), "
			//NOT_FINALITZADES_ERROR_TOTAL
			+ "sum(case when e.notificacioEstat = es.caib.ripea.service.intf.dto.DocumentNotificacioEstatEnumDto.FINALITZADA_AMB_ERRORS then 1 else 0 end) ) " +
    "from DocumentNotificacioEntity e " +
	"where e.createdDate <= :dataFins and e.expedient.esborrat = 0 and e.notificacioEstat is not null " +
    "group by e.expedient.entitat.id, e.expedient.metaExpedient.id, e.expedient.organGestor.id, e.createdBy " +
    "order by e.expedient.entitat.id, e.expedient.metaExpedient.id, e.expedient.organGestor.id, e.createdBy")
	List<ExplotFetsNotificacionsDto> getNotificacionsPerDimensio(@Param("dataFins") LocalDateTime dataFins);

	/**
	 * PORTAFIRMES *
	 */
	//Els enviaments sense estat de callback són els que s'han enviat a portafirmes però encara no
	//n'hem rebut resposta: es compten a FIR_ENVIADES i no s'exclouen de la consulta.
	@Query(	"select new es.caib.ripea.service.intf.dto.explotacio.ExplotFetsPortafirmesDto( "
			+ "e.expedient.entitat.id, e.expedient.metaExpedient.id, e.expedient.organGestor.id, e.createdBy, "
			//FIR_ENVIADES_TOTAL
			+ "sum(case when e.callbackEstat is null then 1 else 0 end), "
			//FIR_INICIADES_TOTAL
			+ "sum(case when e.callbackEstat = es.caib.ripea.service.intf.dto.PortafirmesCallbackEstatEnumDto.INICIAT then 1 else 0 end), "
			//FIR_PAUSADES_TOTAL
			+ "sum(case when e.callbackEstat = es.caib.ripea.service.intf.dto.PortafirmesCallbackEstatEnumDto.PAUSAT then 1 else 0 end), "
			//FIR_FIRMADES_TOTAL
			+ "sum(case when e.callbackEstat = es.caib.ripea.service.intf.dto.PortafirmesCallbackEstatEnumDto.FIRMAT then 1 else 0 end), "
			//FIR_REBUTJADES_TOTAL
			+ "sum(case when e.callbackEstat = es.caib.ripea.service.intf.dto.PortafirmesCallbackEstatEnumDto.REBUTJAT then 1 else 0 end), "
			//FIR_PARCIALS_TOTAL
			+ "sum(case when e.callbackEstat = es.caib.ripea.service.intf.dto.PortafirmesCallbackEstatEnumDto.PARCIAL then 1 else 0 end) ) " +
    "from DocumentPortafirmesEntity e " +
	"where e.createdDate <= :dataFins and e.expedient.esborrat = 0 " +
    "group by e.expedient.entitat.id, e.expedient.metaExpedient.id, e.expedient.organGestor.id, e.createdBy " +
    "order by e.expedient.entitat.id, e.expedient.metaExpedient.id, e.expedient.organGestor.id, e.createdBy")
	List<ExplotFetsPortafirmesDto> getPortafirmesPerDimensio(@Param("dataFins") LocalDateTime dataFins);
}
