package es.caib.ripea.persistence.diag;

import org.hibernate.resource.jdbc.spi.StatementInspector;

import es.caib.ripea.service.intf.utils.TempsTraca;

/**
 * Passa el text de cada sentència SQL que prepara Hibernate a la traça de temps de la petició en curs, perquè
 * les consultes lentes surtin al log amb el seu text. No modifica la sentència.
 *
 * <p>Configurat a la propietat {@code spring.jpa.properties.hibernate.session_factory.statement_inspector}.</p>
 *
 * @see TempsTraca
 */
public class TempsTracaSqlInspector implements StatementInspector {

	private static final long serialVersionUID = 1L;

	@Override
	public String inspect(String sql) {
		TempsTraca.sqlPreparada(sql);
		return sql;
	}

}
