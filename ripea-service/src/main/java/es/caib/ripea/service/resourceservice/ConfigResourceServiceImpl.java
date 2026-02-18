package es.caib.ripea.service.resourceservice;

import org.springframework.stereotype.Service;

import es.caib.ripea.persistence.entity.resourceentity.config.ConfigResourceEntity;
import es.caib.ripea.service.base.service.BaseMutableResourceService;
import es.caib.ripea.service.intf.model.ConfigResource;
import es.caib.ripea.service.intf.resourceservice.ConfigResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConfigResourceServiceImpl extends BaseMutableResourceService<ConfigResource, String, ConfigResourceEntity> implements ConfigResourceService {

}
