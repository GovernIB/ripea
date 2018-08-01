/**
 * 
 */
package es.caib.ripea.core.dialect;

import java.util.Properties;

import org.hibernate.MappingException;
import org.hibernate.dialect.Dialect;
import org.hibernate.id.SequenceGenerator;
import org.hibernate.type.Type;

/**
 * SequenceGenerator de Hibernate per a la base de dades Oracle per a
 * permetre adaptar el nom de la seqüència HIBERNATE_SEQUENCE als estàndards
 * de nomenclatura de la DGDT.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class TableNameSequenceGenerator extends SequenceGenerator {

	public static final String CUSTOM_SEQUENCE_NAME = "IPA_HIBERNATE_SEQ";

	public void configure(Type type, Properties params, Dialect dialect) throws MappingException {
		if (params.getProperty(SEQUENCE) == null || params.getProperty(SEQUENCE).length() == 0) {
			String seqName = CUSTOM_SEQUENCE_NAME;
			params.setProperty(SEQUENCE, seqName);
		}
		super.configure(type, params, dialect);
	}

}