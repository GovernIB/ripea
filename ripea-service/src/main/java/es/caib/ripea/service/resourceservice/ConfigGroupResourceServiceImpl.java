package es.caib.ripea.service.resourceservice;

import org.springframework.stereotype.Service;

import es.caib.ripea.persistence.entity.resourceentity.config.ConfigGroupResourceEntity;
import es.caib.ripea.service.base.service.BaseMutableResourceService;
import es.caib.ripea.service.intf.model.ConfigGroupResource;
import es.caib.ripea.service.intf.resourceservice.ConfigGroupResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConfigGroupResourceServiceImpl extends BaseMutableResourceService<ConfigGroupResource, String, ConfigGroupResourceEntity> implements ConfigGroupResourceService {
    @Override
    protected void afterConversion(ConfigGroupResourceEntity entity, ConfigGroupResource resource) {
        resource.setChildrens(entity.getChildren() != null ?entity.getChildren().size() :0);
    }
}
