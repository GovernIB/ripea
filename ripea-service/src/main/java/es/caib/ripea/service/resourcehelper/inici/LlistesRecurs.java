package es.caib.ripea.service.resourcehelper.inici;

import java.util.function.Function;

import org.springframework.data.jpa.domain.Specification;

/**
 * Llistes resum de la pantalla d'inici sobre un recurs, amb les mateixes regles de visibilitat que el llistat del
 * recurs: les files de la llista són les que l'usuari veuria al llistat amb el mateix criteri i el mateix ordre.
 *
 * El servei del recurs executa la consulta dins d'una transacció de lectura pròpia i li passa la seva especificació
 * de cerca (toFindProcessedSpecification), que inclou els permisos. La conversió de les files s'ha de fer dins de
 * la consulta, perquè s'hi puguin llegir les relacions LAZY.
 *
 * @param <E> entitat del recurs.
 */
public interface LlistesRecurs<E> {

	/**
	 * @param namedQueries named queries del llistat de destinació (null si no en fa servir cap).
	 * @param consulta     consulta que rep l'especificació dels elements visibles i en retorna el resultat ja convertit.
	 */
	<T> T ambVisibles(String[] namedQueries, Function<Specification<E>, T> consulta);

}
