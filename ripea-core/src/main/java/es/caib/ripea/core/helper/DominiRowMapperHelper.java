/**
 * 
 */
package es.caib.ripea.core.helper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import es.caib.ripea.core.api.dto.ResultatConsultaDto;

/**
 * Mapeig del resultat d'una consulta a ResultatDominiDto
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class DominiRowMapperHelper implements RowMapper<ResultatConsultaDto> {

	@Override
	public ResultatConsultaDto mapRow(ResultSet rs, int rowNum) throws SQLException {
		ResultatConsultaDto resultat = new ResultatConsultaDto();
		resultat.setId(rs.getString("id"));
		resultat.setText(rs.getString("valor"));
		return resultat;
	}
}
