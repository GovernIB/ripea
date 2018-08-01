/**
 * 
 */
package es.caib.ripea.core.dialect;

import org.hibernate.dialect.PostgreSQLDialect;

/**
 * Dialecte de Hibernate per a la base de dades Postgres per a permetre
 * adaptar el nom de la seqüència HIBERNATE_SEQUENCE als estàndards de
 * nomenclatura de la DGDT.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class RipeaPostgreSQLDialect extends PostgreSQLDialect {

	@SuppressWarnings("rawtypes")
	public Class getNativeIdentifierGeneratorClass() {
		return TableNameSequenceGenerator.class;
	}

}
