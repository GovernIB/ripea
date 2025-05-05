package es.caib.ripea.service.resourceservice;

import es.caib.ripea.persistence.entity.resourceentity.ExecucioMassivaContingutResourceEntity;
import es.caib.ripea.service.base.service.BaseMutableResourceService;
import es.caib.ripea.service.intf.model.ExecucioMassivaContingutResource;
import es.caib.ripea.service.intf.resourceservice.ExecucioMassivaContingutResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExecucioMassivaContingutResourceServiceImpl extends BaseMutableResourceService<ExecucioMassivaContingutResource, Long, ExecucioMassivaContingutResourceEntity> implements ExecucioMassivaContingutResourceService {

}