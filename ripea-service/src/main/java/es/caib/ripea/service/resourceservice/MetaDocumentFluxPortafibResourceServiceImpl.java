package es.caib.ripea.service.resourceservice;

import es.caib.ripea.persistence.entity.resourceentity.MetaDocumentFluxPortafibResourceEntity;
import es.caib.ripea.persistence.repository.UsuariRepository;
import es.caib.ripea.service.base.service.BaseMutableResourceService;
import es.caib.ripea.service.helper.ConfigHelper;
import es.caib.ripea.service.helper.ExcepcioLogHelper;
import es.caib.ripea.service.helper.PluginHelper;
import es.caib.ripea.service.intf.base.exception.ActionExecutionException;
import es.caib.ripea.service.intf.base.exception.AnswerRequiredException;
import es.caib.ripea.service.intf.config.PropertyConfig;
import es.caib.ripea.service.intf.model.MetaDocumentFluxPortafibResource;
import es.caib.ripea.service.intf.resourceservice.MetaDocumentFluxPortafibResourceService;
import es.caib.ripea.service.intf.utils.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class MetaDocumentFluxPortafibResourceServiceImpl extends BaseMutableResourceService<MetaDocumentFluxPortafibResource, Long, MetaDocumentFluxPortafibResourceEntity> implements MetaDocumentFluxPortafibResourceService {

    private final UsuariRepository usuariRepository;
    private final ExcepcioLogHelper excepcioLogHelper;
    private final ConfigHelper configHelper;
    private final PluginHelper pluginHelper;

    @PostConstruct
    public void init() {
        register(MetaDocumentFluxPortafibResource.ACTION_CREACIO_FLUXE_CODE, new CrearFluxeFirmaActionExecutor());
        register(MetaDocumentFluxPortafibResource.ACTION_EDITAR_FLUXE_CODE,  new EditarFluxeFirmaActionExecutor());
    }

    private class CrearFluxeFirmaActionExecutor implements ActionExecutor<MetaDocumentFluxPortafibResourceEntity, MetaDocumentFluxPortafibResource.UrlFluxForm, Serializable> {

        @Override
        public void onChange(Serializable id, MetaDocumentFluxPortafibResource.UrlFluxForm previous, String fieldName, Object fieldValue,
                             Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, MetaDocumentFluxPortafibResource.UrlFluxForm target) {
        }

        @Override
        public Serializable exec(String code, MetaDocumentFluxPortafibResourceEntity entity, MetaDocumentFluxPortafibResource.UrlFluxForm params) throws ActionExecutionException {
            Map<String, String> result = new HashMap<>();
            try {
                String dadesURL		= configHelper.getEntitatActualCodi()+"#"+params.getMetaDocumentId()+"#"+ SecurityContextHolder.getContext().getAuthentication().getName();
                String paramSecure	= Utils.encripta(dadesURL, configHelper.getConfig(PropertyConfig.CLAU_ENCRIPTACIO));
                String urlReturn	= configHelper.getConfig(PropertyConfig.BASE_URL) + "/metaDocument/flux/event/"+paramSecure+"/returnurl/";
                String url = pluginHelper.portafirmesIniciarFluxDeFirma(true, urlReturn).getUrlRedireccio();
                result.put("url", url);
                return (Serializable) result;
            } catch (Exception e) {
                excepcioLogHelper.addExcepcio("/meta-document/CrearFluxeFirmaActionExecutor", e);
                throw new ActionExecutionException(getResourceClass(), params.getMetaDocumentId(), code, e.getMessage());
            }
        }
    }

    private class EditarFluxeFirmaActionExecutor implements ActionExecutor<MetaDocumentFluxPortafibResourceEntity, Serializable, Serializable> {

        @Override
        public void onChange(Serializable id, Serializable previous, String fieldName, Object fieldValue,
                             Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, Serializable target) {
        }

        @Override
        public Serializable exec(String code, MetaDocumentFluxPortafibResourceEntity entity, Serializable params) throws ActionExecutionException {
            Map<String, String> result = new HashMap<>();
            try {
                String dadesURL		= configHelper.getEntitatActualCodi()+"#"+entity.getMetaDocument().getId()+"#"+SecurityContextHolder.getContext().getAuthentication().getName();
                String paramSecure	= Utils.encripta(dadesURL, configHelper.getConfig(PropertyConfig.CLAU_ENCRIPTACIO));
                String urlReturn = configHelper.getConfig(PropertyConfig.BASE_URL) + "/metaDocument/flux/event/"+paramSecure+"/returnurl/";
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                String idioma = usuariRepository.getOne(auth.getName()).getIdioma();
                String url = pluginHelper.portafirmesRecuperarUrlPlantilla(
                        String.valueOf(entity.getId()),
                        idioma!=null?idioma:"ca",
                        urlReturn,
                        true);
                result.put("url", url);
                return (Serializable) result;
            } catch (Exception e) {
                excepcioLogHelper.addExcepcio("/meta-document/EditarFluxeFirmaActionExecutor", e);
                throw new ActionExecutionException(getResourceClass(), entity.getId(), code, e.getMessage());
            }
        }
    }
}