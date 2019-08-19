/**
 * 
 */
package es.caib.ripea.core.helper;

import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import es.caib.ripea.core.entity.MetaExpedientEntity;
import es.caib.ripea.core.entity.MetaExpedientSequenciaEntity;
import es.caib.ripea.core.repository.MetaExpedientSequenciaRepository;

/**
 * Utilitats comunes pels meta-expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class MetaExpedientHelper {

	@Autowired
	private MetaExpedientSequenciaRepository metaExpedientSequenciaRepository;

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public synchronized long obtenirProximaSequenciaExpedient(
			MetaExpedientEntity metaExpedient,
			Integer any,
			boolean incrementar) {
		int anyExpedient;
		if (any != null)
			anyExpedient = any.intValue();
		else
			anyExpedient = Calendar.getInstance().get(Calendar.YEAR);
		MetaExpedientSequenciaEntity sequencia = metaExpedientSequenciaRepository.findByMetaExpedientAndAny(
				metaExpedient,
				anyExpedient);
		if (sequencia == null) {
			sequencia = MetaExpedientSequenciaEntity.getBuilder(
					anyExpedient,
					metaExpedient).build();
			metaExpedientSequenciaRepository.save(sequencia);
			return sequencia.getValor();
		} else if (incrementar) {
			sequencia.incrementar();
			return sequencia.getValor();
		} else {
			return sequencia.getValor() + 1;
		}
	}

}
