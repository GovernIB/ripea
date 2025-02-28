package es.caib.ripea.service.resourceservice;

import es.caib.ripea.persistence.entity.resourceentity.ExpedientResourceEntity;
import es.caib.ripea.persistence.entity.resourceentity.ExpedientTascaResourceEntity;
import es.caib.ripea.persistence.entity.resourceentity.UsuariResourceEntity;
import es.caib.ripea.persistence.entity.resourcerepository.InteressatResourceRepository;
import es.caib.ripea.persistence.entity.resourcerepository.UsuariResourceRepository;
import es.caib.ripea.service.base.service.BaseMutableResourceService;
import es.caib.ripea.service.intf.dto.UsuariDto;
import es.caib.ripea.service.intf.model.ExpedientResource;
import es.caib.ripea.service.intf.model.ExpedientTascaResource;
import es.caib.ripea.service.intf.model.InteressatResource;
import es.caib.ripea.service.intf.model.UsuariResource;
import es.caib.ripea.service.intf.resourceservice.ExpedientTascaResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementació del servei de gestió de tasques.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ExpedientTascaResourceServiceImpl extends BaseMutableResourceService<ExpedientTascaResource, Long, ExpedientTascaResourceEntity> implements ExpedientTascaResourceService {

    @Override
    protected ExpedientTascaResource applyPerspectives(ExpedientTascaResourceEntity entity, ExpedientTascaResource resource, String[] perspectives) {
        if(Arrays.asList(perspectives).contains("RESPONSABLES_RESUM")){
            if (entity.getResponsables() != null && !entity.getResponsables().isEmpty()) {
                List<String> responsablesStr = new ArrayList<String>();
                for (UsuariResourceEntity responsable : entity.getResponsables()) {
                    responsablesStr.add(responsable.getCodi());
                }
                resource.setResponsablesStr(StringUtils.join(responsablesStr, ","));
            }
        }
        return resource;
    }
}
