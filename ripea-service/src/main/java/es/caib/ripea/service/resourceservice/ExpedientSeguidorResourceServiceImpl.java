package es.caib.ripea.service.resourceservice;

import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;

import es.caib.ripea.persistence.entity.resourceentity.ExpedientSeguidorResourceEntity;
import es.caib.ripea.persistence.entity.resourcerepository.UsuariResourceRepository;
import es.caib.ripea.service.base.service.BaseMutableResourceService;
import es.caib.ripea.service.intf.dto.ExpedientSeguidorId;
import es.caib.ripea.service.intf.model.ExpedientSeguidorResource;
import es.caib.ripea.service.intf.model.UsuariResource;
import es.caib.ripea.service.intf.resourceservice.ExpedientSeguidorResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExpedientSeguidorResourceServiceImpl extends BaseMutableResourceService<ExpedientSeguidorResource, ExpedientSeguidorId, ExpedientSeguidorResourceEntity> implements ExpedientSeguidorResourceService {

//	private final ExpedientSeguidorResourceRepository expedientSeguidorResourceRepository;
	private final UsuariResourceRepository usuariResourceRepository;
	
    @Override
    protected void afterConversion(ExpedientSeguidorResourceEntity entity, ExpedientSeguidorResource resource) {
//    	UsuariResourceEntity seguidor = usuariResourceRepository.findById(entity.getId().getSeguidor_codi()).get();
//    	UsuariResource ur = objectMappingHelper.newInstanceMap(Hibernate.unproxy(entity.getSeguidor()), UsuariResource.class);
//    	resource.setSeguidor(ur);
    }
}
