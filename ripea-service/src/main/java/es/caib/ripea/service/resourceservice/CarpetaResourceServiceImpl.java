package es.caib.ripea.service.resourceservice;

import es.caib.ripea.persistence.entity.resourceentity.CarpetaResourceEntity;
import es.caib.ripea.service.base.service.BaseMutableResourceService;
import es.caib.ripea.service.intf.model.CarpetaResource;
import es.caib.ripea.service.intf.resourceservice.CarpetaResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CarpetaResourceServiceImpl extends BaseMutableResourceService<CarpetaResource, Long, CarpetaResourceEntity> implements CarpetaResourceService {

}