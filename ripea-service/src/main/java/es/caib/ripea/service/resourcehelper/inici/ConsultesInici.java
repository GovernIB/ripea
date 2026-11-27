package es.caib.ripea.service.resourcehelper.inici;

import java.sql.SQLException;
import java.sql.SQLTimeoutException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.QueryTimeoutException;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
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

import es.caib.ripea.persistence.entity.EntitatEntity;
import es.caib.ripea.persistence.entity.MetaNodeEntity;
import es.caib.ripea.persistence.entity.UsuariEntity;
import es.caib.ripea.persistence.entity.resourceentity.ExpedientTascaComentariResourceEntity;
import es.caib.ripea.persistence.entity.resourceentity.ExpedientTascaResourceEntity;
import es.caib.ripea.persistence.entity.resourceentity.MetaExpedientComentariResourceEntity;
import es.caib.ripea.persistence.entity.resourceentity.MetaExpedientResourceEntity;
import es.caib.ripea.persistence.repository.ExpedientRepository;
import es.caib.ripea.service.helper.ConfigHelper;
import es.caib.ripea.service.intf.config.PropertyConfig;
import lombok.RequiredArgsConstructor;

/**
 * Consultes de la pantalla d'inici: els recomptes de les targetes i les consultes de les llistes resum.
 *
 * Totes reben l'especificació dels elements visibles del llistat de destinació (veure {@link VisibilitatRecurs}) i
 * s'han de cridar dins de la transacció que l'ha generada. Les llistes posen el límit de files a la mateixa consulta:
 * amb una pàgina plena, els repositoris de Spring Data llancen a més una consulta de recompte que aquí no cal.
 *
 * <b>Límit de temps</b>: totes les consultes porten el límit de la propietat
 * {@link PropertyConfig#INICI_CONSULTA_TIMEOUT_SEGONS}. Si una consulta el supera, la base de dades la cancel·la i
 * la targeta o la llista que la feia surt amb l'avís de temps esgotat ({@link #esTempsEsgotat}), en lloc d'ocupar una
 * connexió fins que acabi. El càlcul de permisos del llistat de destinació (la part comuna amb el llistat) no hi entra.
 */
@Component
@RequiredArgsConstructor
public class ConsultesInici {

	/** Límit de temps per defecte de cada consulta, si la propietat no hi és o no és un enter. */
	public static final int TIMEOUT_DEFECTE_SEGONS = 15;

	/** Hint estàndard de JPA (en mil·lisegons): Hibernate el passa a la sentència JDBC (setQueryTimeout). */
	private static final String HINT_TIMEOUT = "javax.persistence.query.timeout";
	/** ORA-01013: la base de dades ha cancel·lat la sentència (és el que fa Oracle quan s'esgota el setQueryTimeout). */
	private static final int ORACLE_SENTENCIA_CANCELADA = 1013;
	/** query_canceled de PostgreSQL (també per statement_timeout). */
	private static final String POSTGRES_SENTENCIA_CANCELADA = "57014";

	@PersistenceContext
	private EntityManager entityManager;

	private final ConfigHelper configHelper;

	/** Ordre d'una consulta a partir de la seva arrel: permet ordenar per expressions (p. ex. un coalesce). */
	@FunctionalInterface
	public interface Ordre<E> {
		List<Order> ordres(Root<E> root, CriteriaBuilder cb);
	}

	/**
	 * Nombre d'elements visibles que compleixen l'especificació. Mateixa consulta que el recompte de Spring Data
	 * (count, o count distinct si l'especificació ho demana) però amb el límit de temps.
	 */
	public <E> long comptar(Class<E> classe, Specification<E> especificacio) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> query = cb.createQuery(Long.class);
		Root<E> root = query.from(classe);
		// L'especificació s'aplica abans de triar el recompte: és ella qui pot demanar el distinct
		where(query, especificacio != null ? especificacio.toPredicate(root, query, cb) : null);
		query.select(query.isDistinct() ? cb.countDistinct(root) : cb.count(root));
		query.orderBy(Collections.<Order>emptyList());
		// Si l'especificació agrupa, hi ha un recompte per grup (com Spring Data, se sumen)
		long total = 0;
		for (Long parcial: ambLimit(entityManager.createQuery(query)).getResultList()) {
			total += parcial != null ? parcial : 0;
		}
		return total;
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
		return ambLimit(entityManager.createQuery(query)).setMaxResults(maxim).getResultList();
	}

	/**
	 * Identificadors dels expedients més antics (per data de creació) que l'acció massiva de tancament permet tancar:
	 * la mateixa consulta que el seu llistat ({@link ExpedientRepository#CONSULTA_IDS_EXPEDIENTS_PER_TANCAMENT_MASSIU}),
	 * sense cap filtre de la pantalla, ordenada i limitada a la base de dades.
	 *
	 * @param procedimentsPermesos procediments en què l'usuari pot fer l'acció massiva (no buit).
	 */
	public List<Long> idsExpedientsPerTancarMesAntics(
			EntitatEntity entitat,
			boolean nomesAgafats,
			UsuariEntity usuariActual,
			List<? extends MetaNodeEntity> procedimentsPermesos,
			int maxim) {
		TypedQuery<Long> query = entityManager.createQuery(
				ExpedientRepository.CONSULTA_IDS_EXPEDIENTS_PER_TANCAMENT_MASSIU + " order by e.createdDate asc, e.id asc",
				Long.class);
		query.setParameter("entitat", entitat);
		query.setParameter("nomesAgafats", nomesAgafats);
		query.setParameter("usuariActual", usuariActual);
		query.setParameter("metaExpedientsPermesos", procedimentsPermesos);
		query.setParameter("esNullMetaExpedient", true);
		query.setParameter("metaExpedient", null);
		query.setParameter("esNullNom", true);
		query.setParameter("nom", null);
		query.setParameter("esNullDataInici", true);
		query.setParameter("dataInici", null);
		query.setParameter("esNullDataFi", true);
		query.setParameter("dataFi", null);
		return ambLimit(query).setMaxResults(maxim).getResultList();
	}

	/**
	 * Tasques visibles de la que fa més temps que no té activitat a la que menys. L'activitat d'una tasca és la
	 * data més recent entre la seva darrera modificació (o la creació) i el seu darrer comentari: el mateix
	 * criteri que {@link CriterisIndicadorsInici#tasquesSenseActivitat}.
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
		for (Tuple fila: ambLimit(entityManager.createQuery(query)).setMaxResults(maxim).getResultList()) {
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
		for (ExpedientTascaResourceEntity entitat: ambLimit(entityManager.createQuery(query)).getResultList()) {
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
		return ambLimit(entityManager.createQuery(query)).setMaxResults(maxim).getResultList().stream()
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
		for (Tuple fila: ambLimit(entityManager.createQuery(query)).getResultList()) {
			resultat.putIfAbsent(fila.get(0, Long.class), fila.get(1, MetaExpedientComentariResourceEntity.class));
		}
		return resultat;
	}

	/**
	 * Cert si l'error (o alguna de les seves causes) és una consulta cancel·lada per haver superat el límit de temps,
	 * tal com arriba d'Hibernate, de Spring o directament del controlador JDBC.
	 */
	public static boolean esTempsEsgotat(Throwable error) {
		Set<Throwable> vistos = Collections.newSetFromMap(new IdentityHashMap<>());
		for (Throwable causa = error; causa != null && vistos.add(causa); causa = causa.getCause()) {
			if (causa instanceof SQLTimeoutException
					|| causa instanceof QueryTimeoutException
					|| causa instanceof org.springframework.dao.QueryTimeoutException) {
				return true;
			}
			if (causa instanceof SQLException) {
				SQLException sql = (SQLException)causa;
				if (sql.getErrorCode() == ORACLE_SENTENCIA_CANCELADA || POSTGRES_SENTENCIA_CANCELADA.equals(sql.getSQLState())) {
					return true;
				}
			}
		}
		return false;
	}

	/** Límit de temps de cada consulta, en segons (0 o negatiu: sense límit). Es llegeix a cada consulta. */
	private int timeoutSegons() {
		return configHelper.getAsInt(PropertyConfig.INICI_CONSULTA_TIMEOUT_SEGONS, TIMEOUT_DEFECTE_SEGONS);
	}

	private <Q extends Query> Q ambLimit(Q query) {
		int segons = timeoutSegons();
		if (segons > 0) {
			query.setHint(HINT_TIMEOUT, segons * 1000);
		}
		return query;
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
