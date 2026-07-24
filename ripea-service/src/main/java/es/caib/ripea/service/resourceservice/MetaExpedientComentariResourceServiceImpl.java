package es.caib.ripea.service.resourceservice;

import java.util.Map;

import org.springframework.stereotype.Service;

import es.caib.ripea.persistence.entity.resourceentity.MetaExpedientComentariResourceEntity;
import es.caib.ripea.service.base.service.BaseMutableResourceService;
import es.caib.ripea.service.helper.EmailHelper;
import es.caib.ripea.service.helper.ExcepcioLogHelper;
import es.caib.ripea.service.helper.UsuariHelper;
import es.caib.ripea.service.intf.base.exception.AnswerRequiredException;
import es.caib.ripea.service.intf.dto.GenericDto;
import es.caib.ripea.service.intf.model.MetaExpedientComentariResource;
import es.caib.ripea.service.intf.resourceservice.MetaExpedientComentariResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MetaExpedientComentariResourceServiceImpl extends BaseMutableResourceService<MetaExpedientComentariResource, Long, MetaExpedientComentariResourceEntity> implements MetaExpedientComentariResourceService {
	
	private final ExcepcioLogHelper excepcioLogHelper;
	private final EmailHelper emailHelper;
	private final UsuariHelper usuariHelper;

	/** Longitud màxima de la columna IPA_EXP_COMMENT.TEXT. */
	private static final int TEXT_MAX_LENGTH = 1024;

	@Override
	protected void beforeCreateSave(MetaExpedientComentariResourceEntity entity, MetaExpedientComentariResource resource, Map<String, AnswerRequiredException.AnswerValue> answers) {
		truncarText(entity);
	}

	@Override
	protected void beforeUpdateSave(MetaExpedientComentariResourceEntity entity, MetaExpedientComentariResource resource, Map<String, AnswerRequiredException.AnswerValue> answers) {
		truncarText(entity);
	}

	/**
	 * Recorta el text de l'entitat a la longitud màxima de la columna abans de persistir-lo.
	 * Només afecta l'entitat; el {@code resource} conserva el text íntegre perquè el correu
	 * enviat a {@link #afterCreateSave} inclogui el valor complet.
	 */
	private void truncarText(MetaExpedientComentariResourceEntity entity) {
		String text = entity.getText();
		if (text != null && text.length() > TEXT_MAX_LENGTH) {
			entity.setText(text.substring(0, TEXT_MAX_LENGTH));
		}
	}

    @Override
    public void afterCreateSave(MetaExpedientComentariResourceEntity entity, MetaExpedientComentariResource resource, Map<String, AnswerRequiredException.AnswerValue> answers, boolean anyOrderChanged) {
    	try {
    		emailHelper.sendEmailAvisMencionatComentari(
    				usuariHelper.getUsuariAutenticat(), 
    				new GenericDto(entity.getMetaExpedient().getId(), " d'un procediment ", entity.getMetaExpedient().getNom()),
    				resource.getText());
    	} catch (Exception ex) {
    		excepcioLogHelper.addExcepcio("/metaExpedientComentari/afterCreateSave", ex);
    		throw ex;
    	}
    }
}