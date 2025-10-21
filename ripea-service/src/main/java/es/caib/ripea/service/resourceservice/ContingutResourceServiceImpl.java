package es.caib.ripea.service.resourceservice;

import es.caib.ripea.persistence.entity.resourceentity.*;
import es.caib.ripea.service.base.service.BaseMutableResourceService;
import es.caib.ripea.service.intf.model.*;
import es.caib.ripea.service.intf.resourceservice.ContingutResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContingutResourceServiceImpl extends BaseMutableResourceService<ContingutResource, Long, ContingutResourceEntity<ContingutResource>> implements ContingutResourceService {

}