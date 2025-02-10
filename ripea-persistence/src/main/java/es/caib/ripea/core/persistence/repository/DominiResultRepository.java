/**
 * 
 */
package es.caib.ripea.core.persistence.repository;

import es.caib.ripea.service.intf.dto.ResultatConsultaDto;

import javax.sql.DataSource;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus domini.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface DominiResultRepository {
	void setDataSource(DataSource dataSource,String consulta);
	ResultatConsultaDto findDominisByConsutla();
}
