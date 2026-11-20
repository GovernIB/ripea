package es.caib.ripea.service.resourcehelper.inici;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Tuple;
import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import es.caib.ripea.persistence.entity.resourceentity.ExpedientTascaComentariResourceEntity;
import es.caib.ripea.persistence.entity.resourceentity.ExpedientTascaResourceEntity;
import es.caib.ripea.persistence.entity.resourceentity.MetaExpedientComentariResourceEntity;
import es.caib.ripea.persistence.entity.resourceentity.MetaExpedientResourceEntity;

/**
 * Consultes de les llistes resum de la pantalla d'inici que els repositoris no poden fer.
 *
 * Totes reben l'especificació dels elements visibles del llistat de destinació (veure {@link LlistesRecurs}) i
 * s'han de cridar dins de la transacció que l'ha generada. Totes posen el límit de files a la mateixa consulta:
 * amb una pàgina plena, els repositoris de Spring Data llancen a més una consulta de recompte que aquí no cal.
 */
@Component
public class ConsultesLlistesInici {

	@PersistenceContext
	private EntityManager entityManager;

	/** Ordre d'una consulta a partir de la seva arrel: permet ordenar per expressions (p. ex. un coalesce). */
	@FunctionalInterface
	public interface Ordre<E> {
		List<Order> ordres(Root<E> root, CriteriaBuilder cb);
	}

	/**
	 * Primers elements visibles en l'ordre indicat.
	 *
	 * @param relacions relacions «a un» que es carreguen a la mateixa consulta (LEFT JOIN FETCH), per no fer-ne
	 *                  una consulta per fila en convertir-les.
	 */
	public <E> List<E> primeres(
			Class<E> classe,
			Specification<E> visibles,
			Ordre<E> ordre,
			int maxim,
			String... relacions) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<E> query = cb.createQuery(classe);
		Root<E> root = query.from(classe);
		for (String relacio: relacions) {
			root.fetch(relacio, JoinType.LEFT);
		}
		where(query, visibles != null ? visibles.toPredicate(root, query, cb) : null);
		query.select(root).orderBy(ordre.ordres(root, cb));
		return entityManager.createQuery(query).setMaxResults(maxim).getResultList();
	}

	/**
	 * Tasques visibles de la que fa més temps que no té activitat a la que menys. L'activitat d'una tasca és la
	 * data més recent entre la seva darrera modificació (o la creació) i el seu darrer comentari: el mateix
	 * criteri que {@link CriterisIndicadorsInici#tasquesSenseActivitatDesDe}.
	 *
	 * @return id de la tasca → data de la darrera activitat, en l'ordre de la llista.
	 */
	public Map<Long, LocalDateTime> tasquesMesInactives(
			Specification<ExpedientTascaResourceEntity> visibles,
			int maxim) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Tuple> query = cb.createTupleQuery();
		Root<ExpedientTascaResourceEntity> tasca = query.from(ExpedientTascaResourceEntity.class);
		Join<ExpedientTascaResourceEntity, ExpedientTascaComentariResourceEntity> comentari = tasca.join("comentaris", JoinType.LEFT);
		Path<Long> id = tasca.get("id");
		Path<LocalDateTime> modificacio = tasca.get("lastModifiedDate");
		Path<LocalDateTime> creacio = tasca.get("createdDate");
		Expression<LocalDateTime> darreraModificacio = cb.coalesce(modificacio, creacio);
		Expression<LocalDateTime> darrerComentari = cb.greatest(comentari.<LocalDateTime>get("createdDate"));
		// Sense comentaris, el màxim és null i la comparació no es compleix: queda la darrera modificació
		Expression<LocalDateTime> darreraActivitat = cb.<LocalDateTime>selectCase()
				.when(cb.greaterThan(darrerComentari, darreraModificacio), darrerComentari)
				.otherwise(darreraModificacio);
		where(query, visibles != null ? visibles.toPredicate(tasca, query, cb) : null);
		query.multiselect(id, darreraActivitat)
				.groupBy(id, modificacio, creacio)
				.orderBy(cb.asc(darreraActivitat), cb.asc(id));
		Map<Long, LocalDateTime> resultat = new LinkedHashMap<>();
		for (Tuple fila: entityManager.createQuery(query).setMaxResults(maxim).getResultList()) {
			resultat.put(fila.get(0, Long.class), toLocalDateTime(fila.get(1)));
		}
		return resultat;
	}

	/**
	 * Tasques amb l'expedient, el tipus de tasca i els responsables carregats en una sola consulta.
	 *
	 * @return id → tasca (sense ordre: l'ordre el dona la consulta de la llista).
	 */
	public Map<Long, ExpedientTascaResourceEntity> tasquesAmbResponsables(Collection<Long> ids) {
		Map<Long, ExpedientTascaResourceEntity> resultat = new HashMap<>();
		if (ids.isEmpty()) {
			return resultat;
		}
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<ExpedientTascaResourceEntity> query = cb.createQuery(ExpedientTascaResourceEntity.class);
		Root<ExpedientTascaResourceEntity> tasca = query.from(ExpedientTascaResourceEntity.class);
		tasca.fetch("expedient", JoinType.LEFT);
		tasca.fetch("metaExpedientTasca", JoinType.LEFT);
		tasca.fetch("responsables", JoinType.LEFT);
		query.select(tasca).where(tasca.get("id").in(ids));
		// El join amb els responsables repeteix la tasca per a cada responsable
		for (ExpedientTascaResourceEntity entitat: entityManager.createQuery(query).getResultList()) {
			resultat.put(entitat.getId(), entitat);
		}
		return resultat;
	}

	/** Darrers comentaris dels procediments visibles, del més recent al més antic, amb el procediment carregat. */
	public List<MetaExpedientComentariResourceEntity> darrersComentaris(
			Specification<MetaExpedientResourceEntity> procedimentsVisibles,
			int maxim) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Tuple> query = cb.createTupleQuery();
		Root<MetaExpedientResourceEntity> procediment = query.from(MetaExpedientResourceEntity.class);
		Join<MetaExpedientResourceEntity, MetaExpedientComentariResourceEntity> comentari = procediment.join("comentaris");
		where(query, procedimentsVisibles != null ? procedimentsVisibles.toPredicate(procediment, query, cb) : null);
		// El procediment se selecciona per carregar-lo a la mateixa consulta
		query.multiselect(comentari, procediment)
				.orderBy(cb.desc(comentari.get("createdDate")), cb.desc(comentari.get("id")));
		return entityManager.createQuery(query).setMaxResults(maxim).getResultList().stream()
				.map(fila -> fila.get(0, MetaExpedientComentariResourceEntity.class))
				.collect(Collectors.toList());
	}

	/** Darrer comentari de cada procediment indicat (els que no en tenen no hi són). */
	public Map<Long, MetaExpedientComentariResourceEntity> darrerComentariPerProcediment(Collection<Long> procedimentIds) {
		Map<Long, MetaExpedientComentariResourceEntity> resultat = new HashMap<>();
		if (procedimentIds.isEmpty()) {
			return resultat;
		}
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Tuple> query = cb.createTupleQuery();
		Root<MetaExpedientComentariResourceEntity> comentari = query.from(MetaExpedientComentariResourceEntity.class);
		Path<Long> procedimentId = comentari.get("metaExpedient").get("id");
		query.multiselect(procedimentId, comentari)
				.where(procedimentId.in(procedimentIds))
				.orderBy(cb.desc(comentari.get("createdDate")), cb.desc(comentari.get("id")));
		for (Tuple fila: entityManager.createQuery(query).getResultList()) {
			resultat.putIfAbsent(fila.get(0, Long.class), fila.get(1, MetaExpedientComentariResourceEntity.class));
		}
		return resultat;
	}

	private static void where(AbstractQuery<?> query, Predicate predicat) {
		if (predicat != null) {
			query.where(predicat);
		}
	}

	/** Segons el dialecte, el CASE de dates pot tornar un Timestamp en lloc d'un LocalDateTime. */
	private static LocalDateTime toLocalDateTime(Object valor) {
		if (valor instanceof Timestamp) {
			return ((Timestamp)valor).toLocalDateTime();
		}
		return (LocalDateTime)valor;
	}

}
