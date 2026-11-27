package es.caib.ripea.service.resourcehelper.inici;

import java.util.function.Function;

import org.springframework.data.jpa.domain.Specification;

/**
 * Consultes de la pantalla d'inici (targetes i llistes resum) sobre un recurs, amb les mateixes regles de visibilitat
 * que el llistat del recurs: un recompte és el nombre d'elements que l'usuari veuria al llistat amb aquell criteri, i
 * les files d'una llista, les que hi veuria amb el mateix criteri i el mateix ordre.
 *
 * El servei del recurs executa la consulta dins d'una transacció de lectura pròpia i li passa la seva especificació
 * de cerca (toFindProcessedSpecification), que inclou els permisos. Així la part costosa (el càlcul de permisos) es
 * fa un sol cop per a totes les consultes d'un mateix grup, i un grup que falla no afecta la transacció dels altres.
 * La conversió de les files s'ha de fer dins de la consulta, perquè s'hi puguin llegir les relacions LAZY.
 *
 * Les consultes es fan amb {@link ConsultesInici}, que hi aplica el límit de temps de la pantalla d'inici. Els
 * criteris són especificacions JPA i no filtres springfilter: els camps java.util.Date (data límit, data de tancament,
 * data del monitor...) només admeten dates sense hora en springfilter.
 *
 * @param <E> entitat del recurs.
 */
public interface VisibilitatRecurs<E> {

	/**
	 * @param namedQueries named queries del llistat de destinació (null si no en fa servir cap).
	 * @param consulta     consulta que rep l'especificació dels elements visibles i en retorna el resultat ja convertit.
	 */
	<T> T ambVisibles(String[] namedQueries, Function<Specification<E>, T> consulta);

}
