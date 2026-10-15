package es.caib.ripea.persistence.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import es.caib.ripea.persistence.entity.ExecucioMassivaEntity;
import es.caib.ripea.service.intf.dto.ExecucioMassivaProcesEstatDto;
import es.caib.ripea.service.intf.dto.ExecucioMassivaTipusDto;

@Component
public interface ExecucioMassivaRepository extends JpaRepository<ExecucioMassivaEntity, Long> {

	Optional<ExecucioMassivaEntity> findByExpedientOrigenIdAndTipusAndDataFiNull(Long expedientOrigenId, ExecucioMassivaTipusDto tipus);
	
	List<ExecucioMassivaEntity> findByCreatedByAndEntitatIdOrderByCreatedDateDesc(String createdBy, Long entitatId, Pageable pageable);
	
	List<ExecucioMassivaEntity> findByEntitatIdOrderByCreatedDateDesc(Long entitatId, Pageable pageable);
	
	@Query("select min(id) " +
			"from 	ExecucioMassivaEntity " +
			"where 	dataInici <= :ara " +
			"	and dataFi is null ")
	Long getNextMassiu(@Param("ara") Date ara);
	
	@Query("select e " +
			"from 	ExecucioMassivaEntity e " +
			"where 	dataInici <= :ara " +
			"	and dataFi is null " +
			"	order by e.id asc")
	List<ExecucioMassivaEntity> getMassivesPerProcessar(@Param("ara") Date ara);
	
	/**
	 * Execucions que un node pot agafar: no tancades, ja iniciables, i pendents o en curs però abandonades
	 * (del mateix node, que no en processa cap altra en paral·lel, o d'un altre node sense batec recent).
	 * La compartiexen la consulta de candidates i l'update que les reclama, que l'ha de tornar a avaluar
	 * per ser atòmic.
	 */
	String CONDICIO_PROCESSABLE =
			"		e.dataInici <= :ara " +
			"	and e.dataFi is null " +
			"	and (e.estat is null " +
			"		or e.estat = :pendent " +
			"		or (e.estat = :enCurs " +
			"			and (e.node = :node or e.dataHeartbeat is null or e.dataHeartbeat < :limitHeartbeat))) ";

	@Query("select e.id " +
			"from 	ExecucioMassivaEntity e " +
			"where " + CONDICIO_PROCESSABLE +
			"order by e.id asc")
	List<Long> findIdsProcessables(
			@Param("ara") Date ara,
			@Param("pendent") ExecucioMassivaProcesEstatDto pendent,
			@Param("enCurs") ExecucioMassivaProcesEstatDto enCurs,
			@Param("node") String node,
			@Param("limitHeartbeat") Date limitHeartbeat,
			Pageable pageable);

	/** Marca l'execució com a EN_CURS pel node indicat. Retorna 1 si l'ha reclamada, 0 si un altre s'hi ha avançat. */
	@Modifying
	@Query("update ExecucioMassivaEntity e " +
			"set 	e.estat = :enCurs, e.node = :node, e.dataHeartbeat = :ara " +
			"where 	e.id = :id and " + CONDICIO_PROCESSABLE)
	int reclamar(
			@Param("id") Long id,
			@Param("ara") Date ara,
			@Param("pendent") ExecucioMassivaProcesEstatDto pendent,
			@Param("enCurs") ExecucioMassivaProcesEstatDto enCurs,
			@Param("node") String node,
			@Param("limitHeartbeat") Date limitHeartbeat);

	@Modifying
	@Query("update ExecucioMassivaEntity e " +
			"set 	e.dataHeartbeat = :ara " +
			"where 	e.id = :id and e.node = :node and e.estat = :enCurs and e.dataFi is null")
	int updateHeartbeat(
			@Param("id") Long id,
			@Param("node") String node,
			@Param("enCurs") ExecucioMassivaProcesEstatDto enCurs,
			@Param("ara") Date ara);

	/** Tanca com a cancel·lada una execució que encara no ha agafat cap node. Retorna 0 si ja l'han agafada o tancada. */
	@Modifying(flushAutomatically = true, clearAutomatically = true)
	@Query("update ExecucioMassivaEntity e " +
			"set 	e.estat = :cancelada, e.dataFi = :ara, e.cancelacioData = :ara, e.cancelacioUsuari = :usuari " +
			"where 	e.id = :id and e.dataFi is null and (e.estat is null or e.estat = :pendent)")
	int cancelarPendent(
			@Param("id") Long id,
			@Param("ara") Date ara,
			@Param("usuari") String usuari,
			@Param("pendent") ExecucioMassivaProcesEstatDto pendent,
			@Param("cancelada") ExecucioMassivaProcesEstatDto cancelada);

	/** Deixa constància que s'ha demanat cancel·lar una execució en curs. El procés ho comprova abans de cada element. */
	@Modifying(flushAutomatically = true, clearAutomatically = true)
	@Query("update ExecucioMassivaEntity e " +
			"set 	e.cancelacioData = :ara, e.cancelacioUsuari = :usuari " +
			"where 	e.id = :id and e.dataFi is null and e.cancelacioData is null")
	int solicitarCancelacio(
			@Param("id") Long id,
			@Param("ara") Date ara,
			@Param("usuari") String usuari);

	@Query("select e.cancelacioData from ExecucioMassivaEntity e where e.id = :id")
	Date getCancelacioData(@Param("id") Long id);
	
	@Modifying
 	@Query(value = "UPDATE IPA_EXECUCIO_MASSIVA " +
 			"SET CREATEDBY_CODI = CASE WHEN CREATEDBY_CODI = :codiAntic THEN :codiNou ELSE CREATEDBY_CODI END, " +
 			"    LASTMODIFIEDBY_CODI = CASE WHEN LASTMODIFIEDBY_CODI = :codiAntic THEN :codiNou ELSE LASTMODIFIEDBY_CODI END " +
 			"WHERE CREATEDBY_CODI = :codiAntic OR LASTMODIFIEDBY_CODI = :codiAntic",
 			nativeQuery = true)
	public int updateUsuariAuditoria(@Param("codiAntic") String codiAntic, @Param("codiNou") String codiNou);
}