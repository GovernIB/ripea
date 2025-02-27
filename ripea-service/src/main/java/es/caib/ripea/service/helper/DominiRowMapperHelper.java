package es.caib.ripea.service.helper;

import es.caib.ripea.service.intf.dto.ResultatConsultaDto;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

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
