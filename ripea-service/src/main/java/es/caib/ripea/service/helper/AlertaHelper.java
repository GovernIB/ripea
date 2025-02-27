package es.caib.ripea.service.helper;

import es.caib.ripea.persistence.entity.AlertaEntity;
import es.caib.ripea.persistence.entity.ContingutEntity;
import es.caib.ripea.persistence.repository.AlertaRepository;
import es.caib.ripea.persistence.repository.ContingutRepository;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AlertaHelper {

	@Autowired private AlertaRepository alertaRepository;
	@Autowired private ContingutRepository contingutRepository;

	public AlertaEntity crearAlerta(
			String text,
			String error,
			boolean llegida,
			Long contingutId) {
		ContingutEntity contingut = contingutRepository.getOne(contingutId);
		AlertaEntity entity = AlertaEntity.getBuilder(
				text,
				error,
				llegida,
				contingut).build();
		return alertaRepository.save(entity);
	}

	public AlertaEntity crearAlerta(
			String text,
			Exception ex,
			Long contingutId) {
		String error = null;
		if (ex != null) {
			error = ExceptionUtils.getStackTrace(ex).substring(0, 2048);
		}
		return crearAlerta(
				text,
				error,
				false,
				contingutId);
	}

}
