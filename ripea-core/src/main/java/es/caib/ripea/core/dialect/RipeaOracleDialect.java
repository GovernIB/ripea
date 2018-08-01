/**
 * 
 */
package es.caib.ripea.core.dialect;

import org.hibernate.dialect.Oracle10gDialect;

/**
 * Dialecte de Hibernate per a la base de dades Oracle per a permetre
 * adaptar el nom de la seqüència HIBERNATE_SEQUENCE als estàndards de
 * nomenclatura de la DGDT.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class RipeaOracleDialect extends Oracle10gDialect {

	@SuppressWarnings("rawtypes")
	public Class getNativeIdentifierGeneratorClass() {
		return TableNameSequenceGenerator.class;
	}

}
