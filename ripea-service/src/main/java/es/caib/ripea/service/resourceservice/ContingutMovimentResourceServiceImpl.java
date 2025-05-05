package es.caib.ripea.service.resourceservice;

import es.caib.ripea.persistence.entity.resourceentity.ContingutMovimentResourceEntity;
import es.caib.ripea.service.base.service.BaseMutableResourceService;
import es.caib.ripea.service.intf.model.ContingutMovimentResource;
import es.caib.ripea.service.intf.resourceservice.ContingutMovimentResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContingutMovimentResourceServiceImpl extends BaseMutableResourceService<ContingutMovimentResource, Long, ContingutMovimentResourceEntity> implements ContingutMovimentResourceService {

}