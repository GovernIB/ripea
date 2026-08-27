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
import es.caib.ripea.service.intf.dto.ConsultaPinbalEstatEnumDto;
import es.caib.ripea.service.intf.dto.DocumentNotificacioEstatEnumDto;
import es.caib.ripea.service.intf.dto.ExplotFetsAmbDimensioDto;
import es.caib.ripea.service.intf.dto.PortafirmesCallbackEstatEnumDto;
import es.caib.ripea.service.intf.dto.TascaEstatEnumDto;
import es.caib.ripea.service.intf.dto.TipusProcedimentServeiEnum;

@Component
public interface ExplotacioFetsRepository extends JpaRepository<ExplotacioFetsEntity, Long> {
	
	public List<ExplotacioFetsEntity> findByTemps(@Param("temps") ExplotacioTempsEntity temps);
	
	public ExplotacioFetsEntity findByDimensioAndTemps(
			@Param("dimensio") ExplotacioDimensioEntity dimensio,
			@Param("temps") ExplotacioTempsEntity temps);
	
	/**
	 * PROCEDIMENTS * 
	 */
	
	//Procediments actius totals fins a la data especificada
	@Query(	"select new es.caib.ripea.service.intf.dto.ExplotFetsAmbDimensioDto( "
			+ "e.entitat.id, e.id, e.organGestor.id, e.createdBy, count(e) ) " +
    "from MetaExpedientEntity e " +
	"where e.createdDate <= :dataFins and e.tipusProcedimentServei=:tipus and e.actiu = 1 " +
    "group by e.entitat.id, e.id, e.organGestor.id, e.createdBy " +
    "order by e.entitat.id, e.id, e.organGestor.id, e.createdBy")
	List<ExplotFetsAmbDimensioDto> getProcedimentsActiusPerDimensio(
			@Param("dataFins") LocalDateTime dataFins,
			@Param("tipus") TipusProcedimentServeiEnum tipus);
	
	/**
	 * EXPEDIENT * 
	 */
	
	//Expedients creats en un dia concret
	@Query(	"select new es.caib.ripea.service.intf.dto.ExplotFetsAmbDimensioDto( "
			+ "e.entitat.id, e.metaNode.id, e.organGestor.id, e.createdBy, count(e) ) " +
    "from ExpedientEntity e " +
	"where e.createdDate > :dataDesde and e.createdDate <= :dataFins and e.tipus=0 and e.esborrat = 0 " +
    "group by e.entitat.id, e.metaNode.id, e.organGestor.id, e.createdBy " +
    "order by e.entitat.id, e.metaNode.id, e.organGestor.id, e.createdBy")    
	List<ExplotFetsAmbDimensioDto> getExpedientsObertsPerDimensio(
			@Param("dataDesde") LocalDateTime dataDesde,
			@Param("dataFins") LocalDateTime dataFins);
	
	//Expedients creats fins un dia concret (sense data inicial)
	@Query(	"select new es.caib.ripea.service.intf.dto.ExplotFetsAmbDimensioDto( "
			+ "e.entitat.id, e.metaNode.id, e.organGestor.id, e.createdBy, count(e) ) " +
    "from ExpedientEntity e " +
	"where e.createdDate <= :dataFins and e.tipus=0 and e.esborrat = 0 " +
    "group by e.entitat.id, e.metaNode.id, e.organGestor.id, e.createdBy " +
    "order by e.entitat.id, e.metaNode.id, e.organGestor.id, e.createdBy")    
	List<ExplotFetsAmbDimensioDto> getExpedientsObertsTotalPerDimensio(@Param("dataFins") LocalDateTime dataFins);
	
	//Expedients finalitzats en un dia concret
	@Query(	"select new es.caib.ripea.service.intf.dto.ExplotFetsAmbDimensioDto( "
			+ "e.entitat.id, e.metaNode.id, e.organGestor.id, e.createdBy, count(e) ) " +
    "from ExpedientEntity e " +
	"where e.tancatData > :dataDesde and e.tancatData <= :dataFins and e.tipus=0 and e.esborrat = 0 " +
    "group by e.entitat.id, e.metaNode.id, e.organGestor.id, e.createdBy " +
    "order by e.entitat.id, e.metaNode.id, e.organGestor.id, e.createdBy")   
	List<ExplotFetsAmbDimensioDto> getExpedientsTancatsPerDimensio(
			@Param("dataDesde") Date dataDesde,
			@Param("dataFins") Date dataFins);
	
	//Expedients finalitzats fins un dia concret (sense data inicial)
	@Query(	"select new es.caib.ripea.service.intf.dto.ExplotFetsAmbDimensioDto( "
			+ "e.entitat.id, e.metaNode.id, e.organGestor.id, e.createdBy, count(e) ) " +
    "from ExpedientEntity e " +
	"where e.tancatData <= :dataFins and e.tipus=0 and e.esborrat = 0 " +
    "group by e.entitat.id, e.metaNode.id, e.organGestor.id, e.createdBy " +
    "order by e.entitat.id, e.metaNode.id, e.organGestor.id, e.createdBy")   
	List<ExplotFetsAmbDimensioDto> getExpedientsTancatsTotalPerDimensio(@Param("dataFins") Date dataFins);
	
	/**
	 * TASQUES *
	 */
	//No es correcte utilitzar la data de creació com a filtre per cercar tasques en un estat determinat.
	//Per tant s'obtendrá el total per el dia actual, i es restará de la mateixa dada del dia anterior.

	@Query(	"select new es.caib.ripea.service.intf.dto.ExplotFetsAmbDimensioDto( "
			+ "e.expedient.entitat.id, e.metaExpedientTasca.metaExpedient.id, e.expedient.organGestor.id, e.createdBy, count(e) ) " +
    "from ExpedientTascaEntity e " +
	"where e.createdDate <= :dataFins and e.estat = :estatTasca and e.expedient.esborrat = 0 " +
    "group by e.expedient.entitat.id, e.metaExpedientTasca.metaExpedient.id, e.expedient.organGestor.id, e.createdBy " +
    "order by e.expedient.entitat.id, e.metaExpedientTasca.metaExpedient.id, e.expedient.organGestor.id, e.createdBy")    
	List<ExplotFetsAmbDimensioDto> getTasquesTotalsByEstatPerDimensio(@Param("dataFins") LocalDateTime dataFins, @Param("estatTasca") TascaEstatEnumDto estatTasca);
	
	@Query(	"select new es.caib.ripea.service.intf.dto.ExplotFetsAmbDimensioDto( "
			+ "e.expedient.entitat.id, e.metaExpedientTasca.metaExpedient.id, e.expedient.organGestor.id, e.createdBy, count(e) ) " +
    "from ExpedientTascaEntity e " +
	"where e.createdDate <= :dataFins and e.estat = :estatTasca and e.expedient.esborrat = 0 " +
	//Dins de termini: la tasca no té data límit, o s'ha finalitzat abans o just a la data límit.
	//Si no es coneix la data de finalització no es pot afirmar que sigui fora de termini, i per tant es compta aquí
	//(les dues consultes són complementàries: dins + fora = total de tasques a l'estat indicat).
	"and (e.dataLimit is null or e.dataFi is null or e.dataFi <= e.dataLimit) " +
    "group by e.expedient.entitat.id, e.metaExpedientTasca.metaExpedient.id, e.expedient.organGestor.id, e.createdBy " +
    "order by e.expedient.entitat.id, e.metaExpedientTasca.metaExpedient.id, e.expedient.organGestor.id, e.createdBy")    
	List<ExplotFetsAmbDimensioDto> getTasquesTotalsByEstatPerDimensioDinsTermini(@Param("dataFins") LocalDateTime dataFins, @Param("estatTasca") TascaEstatEnumDto estatTasca);

	@Query(	"select new es.caib.ripea.service.intf.dto.ExplotFetsAmbDimensioDto( "
			+ "e.expedient.entitat.id, e.metaExpedientTasca.metaExpedient.id, e.expedient.organGestor.id, e.createdBy, count(e) ) " +
    "from ExpedientTascaEntity e " +
	"where e.createdDate <= :dataFins and e.estat = :estatTasca and e.expedient.esborrat = 0 " +
	//Fora de termini: només es compta com a fora de termini quan es pot demostrar, és a dir, quan les dues
	//dates estan informades i la data de finalització és posterior a la data límit.
	"and (e.dataLimit is not null and e.dataFi is not null and e.dataFi > e.dataLimit) " +
    "group by e.expedient.entitat.id, e.metaExpedientTasca.metaExpedient.id, e.expedient.organGestor.id, e.createdBy " +
    "order by e.expedient.entitat.id, e.metaExpedientTasca.metaExpedient.id, e.expedient.organGestor.id, e.createdBy")    
	List<ExplotFetsAmbDimensioDto> getTasquesTotalsByEstatPerDimensioForaTermini(@Param("dataFins") LocalDateTime dataFins, @Param("estatTasca") TascaEstatEnumDto estatTasca);
	
	/**
	 * ANOTACIONS * 
	 */
	
	@Query(	"select new es.caib.ripea.service.intf.dto.ExplotFetsAmbDimensioDto("
			+ "e.metaExpedient.entitat.id, e.metaExpedient.id, e.metaExpedient.organGestor.id, e.createdBy, count(e) ) " +
    "from ExpedientPeticioEntity e " +
	"where e.dataAlta > :dataDesde and e.dataAlta <= :dataFins " +
    "group by e.metaExpedient.entitat.id, e.metaExpedient.id, e.metaExpedient.organGestor.id, e.createdBy " +
    "order by e.metaExpedient.entitat.id, e.metaExpedient.id, e.metaExpedient.organGestor.id, e.createdBy")    
	List<ExplotFetsAmbDimensioDto> getNovesAnotacionsPerDimensio(@Param("dataDesde") Date dataDesde, @Param("dataFins") Date dataFins);
	
	@Query(	"select new es.caib.ripea.service.intf.dto.ExplotFetsAmbDimensioDto("
			+ "e.metaExpedient.entitat.id, e.metaExpedient.id, e.metaExpedient.organGestor.id, e.createdBy, count(e) ) " +
    "from ExpedientPeticioEntity e " +
	"where e.dataAlta <= :dataFins " +
    "group by e.metaExpedient.entitat.id, e.metaExpedient.id, e.metaExpedient.organGestor.id, e.createdBy " +
    "order by e.metaExpedient.entitat.id, e.metaExpedient.id, e.metaExpedient.organGestor.id, e.createdBy")    
	List<ExplotFetsAmbDimensioDto> getNovesAnotacionsTotalsPerDimensio(@Param("dataFins") Date dataFins);
	
	//No es correcte utilitzar la data de creació com a filtre per cercar anotacions en un estat determinat.
	//Per tant s'obtendrá el total per el dia actual, i es restará de la mateixa dada del dia anterior.
	@Query(	"select new es.caib.ripea.service.intf.dto.ExplotFetsAmbDimensioDto( " +
		    "e.metaExpedient.entitat.id, e.metaExpedient.id, e.metaExpedient.organGestor.id, e.createdBy, count(e) ) " +
    "from ExpedientPeticioEntity e " +
	"where (e.dataAlta <= :dataFins and (e.estat = 'PROCESSAT_PENDENT' or e.estat = 'PROCESSAT_NOTIFICAT')) " +
    "group by e.metaExpedient.entitat.id, e.metaExpedient.id, e.metaExpedient.organGestor.id, e.createdBy " +
    "order by e.metaExpedient.entitat.id, e.metaExpedient.id, e.metaExpedient.organGestor.id, e.createdBy ")    
	List<ExplotFetsAmbDimensioDto> getAnotacionsProcessadesTotalsPerDimensio(@Param("dataFins") Date dataFins);	
	
	@Query(	"select new es.caib.ripea.service.intf.dto.ExplotFetsAmbDimensioDto( " +
		    "e.metaExpedient.entitat.id, e.metaExpedient.id, e.metaExpedient.organGestor.id, e.createdBy, count(e) ) " +
    "from ExpedientPeticioEntity e " +
	"where e.dataAlta <= :dataFins and e.estat = 'REBUTJAT' " +
    "group by e.metaExpedient.entitat.id, e.metaExpedient.id, e.metaExpedient.organGestor.id, e.createdBy " +
    "order by e.metaExpedient.entitat.id, e.metaExpedient.id, e.metaExpedient.organGestor.id, e.createdBy ")    
	List<ExplotFetsAmbDimensioDto> getAnotacionsRebutjadesTotalsPerDimensio(@Param("dataFins") Date dataFins);
	
	/**
	 * CONSULTES PINBAL * 
	 */
	
	@Query(	"select new es.caib.ripea.service.intf.dto.ExplotFetsAmbDimensioDto("
			+ "e.entitat.id, e.metaExpedient.id, e.metaExpedient.organGestor.id, e.createdBy, count(e) ) " +
    "from ConsultaPinbalEntity e " +
	"where e.createdDate > :dataDesde and e.createdDate <= :dataFins and e.expedient.esborrat = 0 and e.estat = :estat " +
    "group by e.entitat.id, e.metaExpedient.id, e.metaExpedient.organGestor.id, e.createdBy " +
    "order by e.entitat.id, e.metaExpedient.id, e.metaExpedient.organGestor.id, e.createdBy")    
	List<ExplotFetsAmbDimensioDto> getPinbalEnviamentsPerDimensio(
			@Param("dataDesde") LocalDateTime dataDesde,
			@Param("dataFins") LocalDateTime dataFins,
			@Param("estat") ConsultaPinbalEstatEnumDto estat);
	
	@Query(	"select new es.caib.ripea.service.intf.dto.ExplotFetsAmbDimensioDto("
			+ "e.entitat.id, e.metaExpedient.id, e.metaExpedient.organGestor.id, e.createdBy, count(e) ) " +
    "from ConsultaPinbalEntity e " +
	"where e.createdDate <= :dataFins and e.expedient.esborrat = 0 and e.estat = :estat " +
    "group by e.entitat.id, e.metaExpedient.id, e.metaExpedient.organGestor.id, e.createdBy " +
    "order by e.entitat.id, e.metaExpedient.id, e.metaExpedient.organGestor.id, e.createdBy")    
	List<ExplotFetsAmbDimensioDto> getPinbalEnviamentsTotalsPerDimensio(
			@Param("dataFins") LocalDateTime dataFins,
			@Param("estat") ConsultaPinbalEstatEnumDto estat);
	
	/**
	 * NOTIFICACIONS I COMUNICACIONS * 
	 */
	//No es correcte utilitzar la data de creació com a filtre per cercar notificacions en un estat determinat.
	//Per tant s'obtendrá el total per el dia actual, i es restará de la mateixa dada del dia anterior.
	
	@Query(	"select new es.caib.ripea.service.intf.dto.ExplotFetsAmbDimensioDto("
			+ "e.expedient.entitat.id, e.expedient.metaExpedient.id, e.expedient.organGestor.id, e.createdBy, count(e) ) " +
    "from DocumentNotificacioEntity e " +
	"where e.createdDate <= :dataFins and e.expedient.esborrat = 0 and e.notificacioEstat = :estatNotificacio " +
    "group by e.expedient.entitat.id, e.expedient.metaExpedient.id, e.expedient.organGestor.id, e.createdBy " +
    "order by e.expedient.entitat.id, e.expedient.metaExpedient.id, e.expedient.organGestor.id, e.createdBy")    
	List<ExplotFetsAmbDimensioDto> getNotificacionsEnviadesTotalsByEstatPerDimensio(
			@Param("dataFins") LocalDateTime dataFins,
			@Param("estatNotificacio") DocumentNotificacioEstatEnumDto estatNotificacio);
	
	/**
	 * PORTAFIRMES * 
	 */
	//No es correcte utilitzar la data de creació com a filtre per cercar firmes en un estat determinat.
	//Per tant s'obtendrá el total per el dia actual, i es restará de la mateixa dada del dia anterior.

	@Query(	"select new es.caib.ripea.service.intf.dto.ExplotFetsAmbDimensioDto("
			+ "e.expedient.entitat.id, e.expedient.metaExpedient.id, e.expedient.organGestor.id, e.createdBy, count(e) ) " +
    "from DocumentPortafirmesEntity e " +
	"where e.createdDate <= :dataFins and e.expedient.esborrat = 0 and e.callbackEstat = :estatPortafirmes " +
    "group by e.expedient.entitat.id, e.expedient.metaExpedient.id, e.expedient.organGestor.id, e.createdBy " +
    "order by e.expedient.entitat.id, e.expedient.metaExpedient.id, e.expedient.organGestor.id, e.createdBy")
	List<ExplotFetsAmbDimensioDto> getPortafirmesTotalsByEstatPerDimensio(
			@Param("dataFins") LocalDateTime dataFins,
			@Param("estatPortafirmes") PortafirmesCallbackEstatEnumDto estatPortafirmes);
}