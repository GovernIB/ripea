package es.caib.ripea.service.resourcehelper.inici;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Recomptes de la pantalla d'inici sobre un recurs, amb les mateixes regles de visibilitat que el llistat
 * del recurs: cada recompte és el nombre d'elements que l'usuari veuria al llistat amb aquell criteri.
 *
 * El servei del recurs la implementa amb la seva pròpia especificació de cerca (toFindProcessedSpecification),
 * que inclou els permisos. Aquesta especificació es calcula un sol cop per a tots els criteris, de manera que
 * la part costosa (el càlcul de permisos) no es repeteix per cada targeta.
 *
 * Els criteris són especificacions JPA i no filtres springfilter: els camps java.util.Date (data límit, data de
 * tancament, data del monitor...) només admeten dates sense hora en springfilter.
 *
 * @param <E> entitat del recurs.
 */
public interface RecomptesRecurs<E> {

	/**
	 * @param criteris     codi de l'indicador → criteri addicional.
	 * @param namedQueries named queries del llistat de destinació (null si no en fa servir cap).
	 * @return codi de l'indicador → nombre d'elements visibles que compleixen el criteri.
	 */
	Map<String, Long> comptar(Map<String, Specification<E>> criteris, String[] namedQueries);

	/** Implementació comuna: un recompte per criteri sobre l'especificació de visibilitat ja calculada. */
	static <E> Map<String, Long> comptar(
			Map<String, Specification<E>> criteris,
			Specification<E> visibles,
			JpaSpecificationExecutor<E> repository) {
		Map<String, Long> recomptes = new LinkedHashMap<>();
		criteris.forEach((codi, criteri) -> recomptes.put(codi, repository.count(visibles.and(criteri))));
		return recomptes;
	}

}
