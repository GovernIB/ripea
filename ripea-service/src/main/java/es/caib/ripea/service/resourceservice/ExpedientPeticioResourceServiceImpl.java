package es.caib.ripea.service.resourceservice;

import es.caib.ripea.persistence.entity.resourceentity.ExpedientPeticioResourceEntity;
import es.caib.ripea.service.base.service.BaseMutableResourceService;
import es.caib.ripea.service.intf.base.exception.PerspectiveApplicationException;
import es.caib.ripea.service.intf.dto.ExpedientPeticioEstatViewEnumDto;
import es.caib.ripea.service.intf.model.ExpedientPeticioResource;
import es.caib.ripea.service.intf.model.RegistreResource;
import es.caib.ripea.service.intf.resourceservice.ExpedientPeticioResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * Implementació del servei de gestió de peticions d'expedients.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ExpedientPeticioResourceServiceImpl extends BaseMutableResourceService<ExpedientPeticioResource, Long, ExpedientPeticioResourceEntity> implements ExpedientPeticioResourceService {

    @PostConstruct
    public void init() {
        register(ExpedientPeticioResource.PERSPECTIVE_REGISTRE_CODE, new RegistrePerspectiveApplicator());
        register(ExpedientPeticioResource.PERSPECTIVE_ESTAT_VIEW_CODE, new EstatViewPerspectiveApplicator());
    }

    // PerspectiveApplicator
    private class RegistrePerspectiveApplicator implements PerspectiveApplicator<ExpedientPeticioResourceEntity, ExpedientPeticioResource> {
        @Override
        public void applySingle(String code, ExpedientPeticioResourceEntity entity, ExpedientPeticioResource resource) throws PerspectiveApplicationException {
            resource.setRegistreInfo(objectMappingHelper.newInstanceMap(Hibernate.unproxy(entity.getRegistre()), RegistreResource.class));
        }
    }

    private class EstatViewPerspectiveApplicator implements PerspectiveApplicator<ExpedientPeticioResourceEntity, ExpedientPeticioResource> {
        @Override
        public void applySingle(String code, ExpedientPeticioResourceEntity entity, ExpedientPeticioResource resource) throws PerspectiveApplicationException {
            if (resource.getEstat() != null) {
                switch (resource.getEstat()) {
                    case PENDENT:
                        resource.setEstatView(ExpedientPeticioEstatViewEnumDto.PENDENT);
                        break;
                    case PROCESSAT_PENDENT:
                    case PROCESSAT_NOTIFICAT:
                        resource.setEstatView(ExpedientPeticioEstatViewEnumDto.ACCEPTAT);
                        break;
                    case REBUTJAT:
                        resource.setEstatView(ExpedientPeticioEstatViewEnumDto.REBUTJAT);
                        break;
                }
            }
        }
    }
}
