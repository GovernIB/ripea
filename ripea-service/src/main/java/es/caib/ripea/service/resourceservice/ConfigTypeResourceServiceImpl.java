package es.caib.ripea.service.resourceservice;

import org.springframework.stereotype.Service;

import es.caib.ripea.persistence.entity.resourceentity.config.ConfigTypeResourceEntity;
import es.caib.ripea.service.base.service.BaseMutableResourceService;
import es.caib.ripea.service.intf.model.ConfigTypeResource;
import es.caib.ripea.service.intf.resourceservice.ConfigTypeResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConfigTypeResourceServiceImpl extends BaseMutableResourceService<ConfigTypeResource, String, ConfigTypeResourceEntity> implements ConfigTypeResourceService {

}
