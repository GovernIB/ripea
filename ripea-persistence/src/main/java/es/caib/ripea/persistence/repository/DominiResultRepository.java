package es.caib.ripea.persistence.repository;

import es.caib.ripea.service.intf.dto.ResultatConsultaDto;

import javax.sql.DataSource;

import org.springframework.stereotype.Component;

@Component
public interface DominiResultRepository {
	void setDataSource(DataSource dataSource,String consulta);
	ResultatConsultaDto findDominisByConsutla();
}
