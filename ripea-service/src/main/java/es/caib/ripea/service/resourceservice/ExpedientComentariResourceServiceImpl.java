package es.caib.ripea.service.resourceservice;

import java.util.Map;

import org.springframework.stereotype.Service;

import es.caib.ripea.persistence.entity.ExpedientEntity;
import es.caib.ripea.persistence.entity.resourceentity.ExpedientComentariResourceEntity;
import es.caib.ripea.service.base.service.BaseMutableResourceService;
import es.caib.ripea.service.helper.ConfigHelper;
import es.caib.ripea.service.helper.EmailHelper;
import es.caib.ripea.service.helper.EntityComprovarHelper;
import es.caib.ripea.service.helper.ExcepcioLogHelper;
import es.caib.ripea.service.helper.UsuariHelper;
import es.caib.ripea.service.intf.base.exception.AnswerRequiredException;
import es.caib.ripea.service.intf.dto.GenericDto;
import es.caib.ripea.service.intf.model.ExpedientComentariResource;
import es.caib.ripea.service.intf.resourceservice.ExpedientComentariResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExpedientComentariResourceServiceImpl extends BaseMutableResourceService<ExpedientComentariResource, Long, ExpedientComentariResourceEntity> implements ExpedientComentariResourceService {

	private final ExcepcioLogHelper excepcioLogHelper;
	private final EntityComprovarHelper entityComprovarHelper;
	private final EmailHelper emailHelper;
	private final UsuariHelper usuariHelper;
	private final ConfigHelper configHelper;

	/** Longitud màxima de la columna IPA_EXP_COMMENT.TEXT. */
	private static final int TEXT_MAX_LENGTH = 1024;

	@Override
	protected void beforeCreateSave(ExpedientComentariResourceEntity entity, ExpedientComentariResource resource, Map<String, AnswerRequiredException.AnswerValue> answers) {
		truncarText(entity);
	}

	@Override
	protected void beforeUpdateSave(ExpedientComentariResourceEntity entity, ExpedientComentariResource resource, Map<String, AnswerRequiredException.AnswerValue> answers) {
		truncarText(entity);
	}

	/**
	 * Recorta el text de l'entitat a la longitud màxima de la columna abans de persistir-lo.
	 * Només afecta l'entitat; el {@code resource} conserva el text íntegre perquè el correu
	 * enviat a {@link #afterCreateSave} inclogui el valor complet.
	 */
	private void truncarText(ExpedientComentariResourceEntity entity) {
		String text = entity.getText();
		if (text != null && text.length() > TEXT_MAX_LENGTH) {
			entity.setText(text.substring(0, TEXT_MAX_LENGTH));
		}
	}

    @Override
    public void afterCreateSave(ExpedientComentariResourceEntity entity, ExpedientComentariResource resource, Map<String, AnswerRequiredException.AnswerValue> answers, boolean anyOrderChanged) {
    	try {
    		ExpedientEntity expedient = entityComprovarHelper.comprovarExpedient(
    				entity.getExpedient().getId(),
    				false,
    				false,
    				true,
    				false,
    				false,
    				configHelper.getRolActual());
    		emailHelper.sendEmailAvisMencionatComentari(
    				usuariHelper.getUsuariAutenticat(), 
    				new GenericDto(expedient.getId(), " d'un expedient ", expedient.getNom()),
    				resource.getText());
    	} catch (Exception ex) {
    		excepcioLogHelper.addExcepcio("/expedientComentari/afterCreateSave", ex);
    		throw ex;
    	}
    }
}