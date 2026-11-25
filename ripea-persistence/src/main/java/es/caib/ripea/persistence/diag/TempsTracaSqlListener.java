package es.caib.ripea.persistence.diag;

import org.hibernate.BaseSessionEventListener;

import es.caib.ripea.service.intf.utils.TempsTraca;

/**
 * Compta les sentències SQL que executa Hibernate i el temps d'execució de cada una, i ho afegeix al pas en curs
 * de la traça de temps de la petició (si n'hi ha). Hibernate en crea una instància per sessió.
 *
 * <p>Només inclou les consultes fetes per Hibernate: les que es fan amb JDBC directe (per exemple, les ACL de
 * Spring Security amb JdbcTemplate) no hi surten. El temps és el de l'execució de la sentència; no inclou la
 * lectura de les files del ResultSet.</p>
 *
 * <p>Configurat a la propietat {@code spring.jpa.properties.hibernate.session.events.auto}.</p>
 *
 * @see TempsTraca
 */
public class TempsTracaSqlListener extends BaseSessionEventListener {

	private static final long serialVersionUID = 1L;

	private long iniciExecucio;

	@Override
	public void jdbcExecuteStatementStart() {
		iniciExecucio = System.nanoTime();
	}

	@Override
	public void jdbcExecuteStatementEnd() {
		TempsTraca.sqlExecutada(System.nanoTime() - iniciExecucio);
	}

}
