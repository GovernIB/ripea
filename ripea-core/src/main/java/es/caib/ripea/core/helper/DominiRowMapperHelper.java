/**
 * 
 */
package es.caib.ripea.core.helper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import es.caib.ripea.core.api.dto.ResultatDominiDto;

/**
 * Mapeig del resultat d'una consulta a ResultatDominiDto
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class DominiRowMapperHelper implements RowMapper<ResultatDominiDto> {

	@Override
	public ResultatDominiDto mapRow(ResultSet rs, int rowNum) throws SQLException {
		ResultatDominiDto resultat = new ResultatDominiDto();
		resultat.setId(rs.getString("id"));
		resultat.setValor(rs.getString("valor"));
		return resultat;
	}
}
