/**
 * 
 */
package es.caib.ripea.core.repository;

import javax.sql.DataSource;

import es.caib.ripea.core.api.dto.ResultatDominiDto;


/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus domini.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface DominiResultRepository {
	void setDataSource(DataSource dataSource,String consulta);
	ResultatDominiDto findDominisByConsutla();
}
