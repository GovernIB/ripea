package es.caib.ripea.service.resourceservice;

import java.util.Map;

import org.springframework.stereotype.Service;

import es.caib.ripea.persistence.entity.resourceentity.ExpedientTascaComentariResourceEntity;
import es.caib.ripea.service.base.service.BaseMutableResourceService;
import es.caib.ripea.service.helper.ConfigHelper;
import es.caib.ripea.service.helper.EmailHelper;
import es.caib.ripea.service.helper.EntityComprovarHelper;
import es.caib.ripea.service.helper.ExcepcioLogHelper;
import es.caib.ripea.service.helper.UsuariHelper;
import es.caib.ripea.service.intf.base.exception.AnswerRequiredException;
import es.caib.ripea.service.intf.dto.GenericDto;
import es.caib.ripea.service.intf.model.ExpedientTascaComentariResource;
import es.caib.ripea.service.intf.resourceservice.ExpedientTascaComentariResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExpedientTascaComentariResourceServiceImpl extends BaseMutableResourceService<ExpedientTascaComentariResource, Long, ExpedientTascaComentariResourceEntity> implements ExpedientTascaComentariResourceService {
	
	private final ExcepcioLogHelper excepcioLogHelper;
	private final EntityComprovarHelper entityComprovarHelper;
	private final EmailHelper emailHelper;
	private final UsuariHelper usuariHelper;
	private final ConfigHelper configHelper;

	/** Longitud màxima de la columna IPA_EXP_COMMENT.TEXT. */
	private static final int TEXT_MAX_LENGTH = 1024;

	@Override
	protected void beforeCreateSave(ExpedientTascaComentariResourceEntity entity, ExpedientTascaComentariResource resource, Map<String, AnswerRequiredException.AnswerValue> answers) {
		truncarText(entity);
	}

	@Override
	protected void beforeUpdateSave(ExpedientTascaComentariResourceEntity entity, ExpedientTascaComentariResource resource, Map<String, AnswerRequiredException.AnswerValue> answers) {
		truncarText(entity);
	}

	/**
	 * Recorta el text de l'entitat a la longitud màxima de la columna abans de persistir-lo.
	 * Només afecta l'entitat; el {@code resource} conserva el text íntegre perquè el correu
	 * enviat a {@link #afterCreateSave} inclogui el valor complet.
	 */
	private void truncarText(ExpedientTascaComentariResourceEntity entity) {
		String text = entity.getText();
		if (text != null && text.length() > TEXT_MAX_LENGTH) {
			entity.setText(text.substring(0, TEXT_MAX_LENGTH));
		}
	}

    @Override
    public void afterCreateSave(ExpedientTascaComentariResourceEntity entity, ExpedientTascaComentariResource resource, Map<String, AnswerRequiredException.AnswerValue> answers, boolean anyOrderChanged) {
    	try {
    		entityComprovarHelper.comprovarExpedient(
    				entity.getExpedientTasca().getExpedient().getId(),
    				false,
    				false,
    				true,
    				false,
    				false,
    				configHelper.getRolActual());
    		emailHelper.sendEmailAvisMencionatComentari(
    				usuariHelper.getUsuariAutenticat(), 
    				new GenericDto(entity.getExpedientTasca().getId(), " d'una tasca ", entity.getExpedientTasca().getMetaExpedientTasca().getNom()),
    				resource.getText());
    	} catch (Exception ex) {
    		excepcioLogHelper.addExcepcio("/expedientTascaComentari/afterCreateSave", ex);
    		throw ex;
    	}
    }
}