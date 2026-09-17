package es.caib.ripea.service.resourceservice;

import com.turkraft.springfilter.FilterBuilder;
import com.turkraft.springfilter.parser.Filter;
import es.caib.ripea.persistence.entity.resourceentity.EntitatResourceEntity;
import es.caib.ripea.persistence.entity.resourceentity.TipusDocumentalResourceEntity;
import es.caib.ripea.persistence.entity.resourcerepository.EntitatResourceRepository;
import es.caib.ripea.service.base.service.BaseMutableResourceService;
import es.caib.ripea.service.helper.ConfigHelper;
import es.caib.ripea.service.helper.TipusDocumentalHelper;
import es.caib.ripea.service.intf.base.exception.ActionExecutionException;
import es.caib.ripea.service.intf.base.exception.AnswerRequiredException;
import es.caib.ripea.service.intf.base.exception.ResourceNotDeletedException;
import es.caib.ripea.service.intf.base.exception.ResourceNotUpdatedException;
import es.caib.ripea.service.intf.exception.ValidationException;
import es.caib.ripea.service.intf.model.EntitatResource;
import es.caib.ripea.service.intf.model.TipusDocumentalResource;
import es.caib.ripea.service.intf.resourceservice.TipusDocumentalResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TipusDocumentalResourceServiceImpl extends BaseMutableResourceService<TipusDocumentalResource, Long, TipusDocumentalResourceEntity> implements TipusDocumentalResourceService {

    private final ConfigHelper configHelper;
    private final EntitatResourceRepository entitatResourceRepository;
    private final TipusDocumentalHelper tipusDocumentalHelper;

    @Override
    protected String additionalSpringFilter(String currentSpringFilter, String[] namedQueries) {
        List<Filter> filters = new ArrayList<>();
        String entitatActualCodi = configHelper.getEntitatActualCodi();

        filters.add(
                (currentSpringFilter != null && !currentSpringFilter.isEmpty())?Filter.parse(currentSpringFilter):null
        );
        filters.add(
                FilterBuilder.equal(TipusDocumentalResource.Fields.entitat + "." + EntitatResource.Fields.codi,
                        entitatActualCodi != null?entitatActualCodi:"................................................................................")
        );

        List<Filter> result = filters.stream()
                .filter(f -> f!=null && !String.valueOf(f).isEmpty())
                .collect(Collectors.toList());

        return result.isEmpty() ? null : FilterBuilder.and(result).generate();
    }

    @PostConstruct
    public void init() {
        register(TipusDocumentalResource.ACTION_ACTIVAR_CODE, new UpdateActiuActionExecutor(true));
        register(TipusDocumentalResource.ACTION_DESACTIVAR_CODE, new UpdateActiuActionExecutor(false));
    }

    @Override
    protected void beforeCreateSave(TipusDocumentalResourceEntity entity, TipusDocumentalResource resource, Map<String, AnswerRequiredException.AnswerValue> answers) {
        String entitatActualCodi = configHelper.getEntitatActualCodi();
        EntitatResourceEntity entitat = entitatResourceRepository.findByCodi(entitatActualCodi);
        entity.setEntitat(entitat);
    }

    @Override
    protected void beforeUpdateEntity(TipusDocumentalResourceEntity entity, TipusDocumentalResource resource, Map<String, AnswerRequiredException.AnswerValue> answers) throws ResourceNotUpdatedException {
        // L'actiu només es canvia amb les accions ACTIVAR/DESACTIVAR: el formulari no l'envia i no l'ha de trepitjar.
        resource.setActiu(entity.isActiu());
        try {
            tipusDocumentalHelper.comprovarCanviCodi(entity.getEntitat().getId(), entity.getCodi(), resource.getCodi());
        } catch (ValidationException ex) {
            throw new ResourceNotUpdatedException(getResourceClass(), String.valueOf(entity.getId()), ex.getMessage(), ex);
        }
    }

    @Override
    protected void beforeDelete(TipusDocumentalResourceEntity entity, Map<String, AnswerRequiredException.AnswerValue> answers) throws ResourceNotDeletedException {
        // La relació amb tipus de document i documents és pel codi (sense FK): cal comprovar-ho aquí.
        try {
            tipusDocumentalHelper.comprovarEsborrable(entity.getEntitat().getId(), entity.getCodi());
        } catch (ValidationException ex) {
            throw new ResourceNotDeletedException(getResourceClass(), String.valueOf(entity.getId()), ex.getMessage(), ex);
        }
    }

    private class UpdateActiuActionExecutor implements ActionExecutor<TipusDocumentalResourceEntity, Serializable, Serializable> {
        private final boolean actiu;
        private UpdateActiuActionExecutor(boolean actiu) {
            this.actiu = actiu;
        }
        @Override
        public void onChange(Serializable id, Serializable previous, String fieldName, Object fieldValue,
                Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, Serializable target) {
        }
        @Override
        public Serializable exec(String code, TipusDocumentalResourceEntity entity, Serializable params) throws ActionExecutionException {
            // L'acció no aplica el filtre per entitat del llistat: només es permet sobre tipus documentals de l'entitat actual.
            if (entity.getEntitat() == null || !entity.getEntitat().getCodi().equals(configHelper.getEntitatActualCodi())) {
                throw new ActionExecutionException(
                        getResourceClass(),
                        entity.getId(),
                        code,
                        "El tipus documental no pertany a l'entitat actual");
            }
            entity.setActiu(actiu);
            return "{\"resultado\": \"OK\"}";
        }
    }
}