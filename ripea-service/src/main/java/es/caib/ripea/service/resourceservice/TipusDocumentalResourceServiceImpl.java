package es.caib.ripea.service.resourceservice;

import com.turkraft.springfilter.FilterBuilder;
import com.turkraft.springfilter.parser.Filter;
import es.caib.ripea.persistence.entity.resourceentity.EntitatResourceEntity;
import es.caib.ripea.persistence.entity.resourceentity.TipusDocumentalResourceEntity;
import es.caib.ripea.persistence.entity.resourcerepository.EntitatResourceRepository;
import es.caib.ripea.service.base.service.BaseMutableResourceService;
import es.caib.ripea.service.helper.ConfigHelper;
import es.caib.ripea.service.intf.base.exception.AnswerRequiredException;
import es.caib.ripea.service.intf.model.EntitatResource;
import es.caib.ripea.service.intf.model.TipusDocumentalResource;
import es.caib.ripea.service.intf.resourceservice.TipusDocumentalResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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

    @Override
    protected void beforeCreateSave(TipusDocumentalResourceEntity entity, TipusDocumentalResource resource, Map<String, AnswerRequiredException.AnswerValue> answers) {
        String entitatActualCodi = configHelper.getEntitatActualCodi();
        EntitatResourceEntity entitat = entitatResourceRepository.findByCodi(entitatActualCodi);
        entity.setEntitat(entitat);
    }
}