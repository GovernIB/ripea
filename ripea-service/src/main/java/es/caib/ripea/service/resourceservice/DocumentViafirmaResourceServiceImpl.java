package es.caib.ripea.service.resourceservice;

import java.io.Serializable;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import es.caib.ripea.persistence.entity.DocumentEntity;
import es.caib.ripea.persistence.entity.EntitatEntity;
import es.caib.ripea.persistence.entity.resourceentity.DocumentViaFirmaResourceEntity;
import es.caib.ripea.service.base.service.BaseMutableResourceService;
import es.caib.ripea.service.firma.DocumentFirmaViaFirmaHelper;
import es.caib.ripea.service.helper.ConfigHelper;
import es.caib.ripea.service.helper.DocumentHelper;
import es.caib.ripea.service.helper.EntityComprovarHelper;
import es.caib.ripea.service.helper.ExcepcioLogHelper;
import es.caib.ripea.service.intf.base.exception.ActionExecutionException;
import es.caib.ripea.service.intf.base.exception.AnswerRequiredException.AnswerValue;
import es.caib.ripea.service.intf.model.DocumentViaFirmaResource;
import es.caib.ripea.service.intf.resourceservice.DocumentViafirmaResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentViafirmaResourceServiceImpl extends BaseMutableResourceService<DocumentViaFirmaResource, Long, DocumentViaFirmaResourceEntity> implements DocumentViafirmaResourceService {

	private final EntityComprovarHelper entityComprovarHelper;
	private final ExcepcioLogHelper excepcioLogHelper;
	private final DocumentHelper documentHelper;
	private final ConfigHelper configHelper;
	private final DocumentFirmaViaFirmaHelper documentViaFirmaHelper;
	
    @PostConstruct
    public void init() {
    	register(DocumentViaFirmaResource.ACTION_CANCEL_FIRMA, new CancelFirmaActionExecutor());
    }
    
    private class CancelFirmaActionExecutor implements ActionExecutor<DocumentViaFirmaResourceEntity, Serializable, DocumentViaFirmaResource> {

		@Override
		public void onChange(Serializable id, Serializable previous, String fieldName, Object fieldValue, Map<String, AnswerValue> answers, String[] previousFieldNames, Serializable target) {}

		@Override
		public DocumentViaFirmaResource exec(String code, DocumentViaFirmaResourceEntity entity, Serializable params) throws ActionExecutionException {
			try {
				EntitatEntity entitatEntity = entityComprovarHelper.comprovarEntitat(configHelper.getEntitatActualCodi(), false, false, false, true, false);
				String rolActual = configHelper.getRolActual();
				DocumentEntity document = documentHelper.comprovarDocument(entitatEntity.getId(), entity.getDocument().getId(), false, true, false, false, false, rolActual);
				documentViaFirmaHelper.viaFirmaCancelar(document.getId());
			} catch (Exception e) {
				excepcioLogHelper.addExcepcio("/documentPortafirmes/CancelFirmaActionExecutor", e);
			}
			return objectMappingHelper.newInstanceMap(entity, DocumentViaFirmaResource.class);
		}
    }
}
