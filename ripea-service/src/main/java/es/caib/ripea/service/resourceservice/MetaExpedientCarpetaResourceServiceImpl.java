package es.caib.ripea.service.resourceservice;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import es.caib.ripea.persistence.entity.resourceentity.MetaExpedientCarpetaResourceEntity;
import es.caib.ripea.service.base.service.BaseMutableResourceService;
import es.caib.ripea.service.intf.model.MetaExpedientCarpetaResource;
import es.caib.ripea.service.intf.resourceservice.MetaExpedientCarpetaResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MetaExpedientCarpetaResourceServiceImpl extends BaseMutableResourceService<MetaExpedientCarpetaResource, Long, MetaExpedientCarpetaResourceEntity> implements MetaExpedientCarpetaResourceService {

//	private final ConfigHelper configHelper;
	
    @PostConstruct
    public void init() {}

}