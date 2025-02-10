/**
 * 
 */
package es.caib.ripea.core.helper;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.caib.ripea.core.persistence.AlertaEntity;
import es.caib.ripea.core.persistence.ContingutEntity;
import es.caib.ripea.core.repository.AlertaRepository;
import es.caib.ripea.core.repository.ContingutRepository;

/**
 * Mètodes comuns per a gestionar les alertes.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class AlertaHelper {

	@Autowired
	private AlertaRepository alertaRepository;
	@Autowired
	private ContingutRepository contingutRepository;



	public AlertaEntity crearAlerta(
			String text,
			String error,
			boolean llegida,
			Long contingutId) {
		ContingutEntity contingut = contingutRepository.findOne(contingutId);
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
