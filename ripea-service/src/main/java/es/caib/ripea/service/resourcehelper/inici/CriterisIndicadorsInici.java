package es.caib.ripea.service.resourcehelper.inici;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.springframework.data.jpa.domain.Specification;

import es.caib.ripea.persistence.entity.resourceentity.AlertaResourceEntity;
import es.caib.ripea.persistence.entity.resourceentity.ExcepcioLogResourceEntity;
import es.caib.ripea.persistence.entity.resourceentity.ExpedientResourceEntity;
import es.caib.ripea.persistence.entity.resourceentity.ExpedientTascaComentariResourceEntity;
import es.caib.ripea.persistence.entity.resourceentity.ExpedientTascaResourceEntity;
import es.caib.ripea.persistence.entity.resourceentity.IntegracioResourceEntity;
import es.caib.ripea.persistence.entity.resourceentity.MetaExpedientResourceEntity;
import es.caib.ripea.service.intf.dto.ExpedientEstatEnumDto;
import es.caib.ripea.service.intf.dto.IntegracioAccioEstatEnumDto;
import es.caib.ripea.service.intf.dto.MetaExpedientRevisioEstatEnumDto;
import es.caib.ripea.service.intf.dto.TascaEstatEnumDto;

/**
 * Criteris de les targetes de la pantalla d'inici. S'apliquen a sobre de la visibilitat del llistat de
 * destinació (veure {@link RecomptesRecurs}): aquí només hi ha el que distingeix cada targeta.
 */
public final class CriterisIndicadorsInici {

	/** Estats d'una tasca que encara s'ha de fer. */
	public static final List<TascaEstatEnumDto> ESTATS_TASCA_PENDENT = Arrays.asList(
			TascaEstatEnumDto.PENDENT, TascaEstatEnumDto.INICIADA, TascaEstatEnumDto.AGAFADA);
	public static final List<TascaEstatEnumDto> ESTATS_TASCA_EN_CURS = Arrays.asList(
			TascaEstatEnumDto.INICIADA, TascaEstatEnumDto.AGAFADA);

	private CriterisIndicadorsInici() {}

	// --- Expedients ------------------------------------------------------------------------------------

	public static Specification<ExpedientResourceEntity> expedientsOberts() {
		return (root, query, cb) -> cb.equal(root.get("estat"), ExpedientEstatEnumDto.OBERT);
	}

	/** Oberts i agafats per l'usuari (agafatPerCodi és la mateixa columna que agafatPer, sense join). */
	public static Specification<ExpedientResourceEntity> expedientsAgafats(String usuariCodi) {
		return expedientsOberts().and((root, query, cb) -> cb.equal(root.get("agafatPerCodi"), usuariCodi));
	}

	/** Oberts creats abans de la data. */
	public static Specification<ExpedientResourceEntity> expedientsAntics(LocalDateTime creatsAbansDe) {
		return expedientsOberts().and((root, query, cb) -> cb.lessThan(root.<LocalDateTime>get("createdDate"), creatsAbansDe));
	}

	/** Tancats a partir de la data. */
	public static Specification<ExpedientResourceEntity> expedientsTancatsDesDe(Date des) {
		return (root, query, cb) -> cb.and(
				cb.equal(root.get("estat"), ExpedientEstatEnumDto.TANCAT),
				cb.greaterThanOrEqualTo(root.<Date>get("tancatData"), des));
	}

	// --- Alertes ---------------------------------------------------------------------------------------

	public static Specification<AlertaResourceEntity> alertesNoLlegides() {
		return (root, query, cb) -> cb.isFalse(root.<Boolean>get("llegida"));
	}

	// --- Tasques ---------------------------------------------------------------------------------------

	public static Specification<ExpedientTascaResourceEntity> tasquesEnEstat(List<TascaEstatEnumDto> estats) {
		return (root, query, cb) -> root.get("estat").in(estats);
	}

	/** Tasques d'expedients no esborrats (el llistat de tasques ho filtra des del cercador). */
	public static Specification<ExpedientTascaResourceEntity> tasquesExpedientNoEsborrat() {
		return (root, query, cb) -> cb.equal(root.get("expedient").get("esborrat"), 0);
	}

	/*
	 * La data límit és un dia sense hora (@Temporal(DATE): els paràmetres també es trunquen al dia), de manera que
	 * els límits es passen en dies sencers. Amb la data límit a les 00:00 del dia D i "ara" dins d'avui:
	 *  - vençuda (llistat: dataLimit.before(ara))         ⇔ D <= avui            ⇔ D < demà
	 *  - preavís (TascaHelper.shouldNotifyAboutDeadline:
	 *    ara > D - N dies) i no vençuda                    ⇔ demà <= D <= avui + N ⇔ demà <= D < avui + N + 1
	 */

	/** Data límit dins del preavís i encara no vençuda: entre demà (inclòs) i el dia indicat (exclòs). */
	public static Specification<ExpedientTascaResourceEntity> tasquesVencimentProper(Date dema, Date diaFinalExclos) {
		return (root, query, cb) -> cb.and(
				cb.greaterThanOrEqualTo(root.<Date>get("dataLimit"), dema),
				cb.lessThan(root.<Date>get("dataLimit"), diaFinalExclos));
	}

	/** Data límit vençuda (mateix criteri que la marca dataLimitExpirada del llistat): anterior a demà. */
	public static Specification<ExpedientTascaResourceEntity> tasquesForaTermini(Date dema) {
		return (root, query, cb) -> cb.lessThan(root.<Date>get("dataLimit"), dema);
	}

	/**
	 * Sense activitat des de la data: ni la tasca s'ha modificat (la data de modificació es desa també en crear-la)
	 * ni s'hi ha afegit cap comentari.
	 */
	public static Specification<ExpedientTascaResourceEntity> tasquesSenseActivitatDesDe(LocalDateTime des) {
		return (root, query, cb) -> {
			Subquery<Long> comentari = query.subquery(Long.class);
			Root<ExpedientTascaComentariResourceEntity> c = comentari.from(ExpedientTascaComentariResourceEntity.class);
			comentari.select(c.<Long>get("id")).where(
					cb.equal(c.get("expedientTasca"), root),
					cb.greaterThanOrEqualTo(c.<LocalDateTime>get("createdDate"), des));
			return cb.and(
					cb.lessThan(cb.coalesce(root.<LocalDateTime>get("lastModifiedDate"), root.<LocalDateTime>get("createdDate")), des),
					cb.not(cb.exists(comentari)));
		};
	}

	// --- Procediments ----------------------------------------------------------------------------------

	public static Specification<MetaExpedientResourceEntity> procedimentsEnEstatRevisio(MetaExpedientRevisioEstatEnumDto estat) {
		return (root, query, cb) -> cb.equal(root.get("revisioEstat"), estat);
	}

	// --- Monitor ---------------------------------------------------------------------------------------

	public static Specification<IntegracioResourceEntity> integracionsErrorDesDe(Date des) {
		return (root, query, cb) -> cb.and(
				cb.equal(root.get("estat"), IntegracioAccioEstatEnumDto.ERROR),
				cb.greaterThanOrEqualTo(root.<Date>get("data"), des));
	}

	/** Registres amb alguna aparició des de la data (els repetits en desen la darrera a dataDarrera). */
	public static Specification<ExcepcioLogResourceEntity> excepcionsDesDe(Date des) {
		return (root, query, cb) -> cb.greaterThanOrEqualTo(
				cb.coalesce(root.<Date>get("dataDarrera"), root.<Date>get("data")), des);
	}

}
