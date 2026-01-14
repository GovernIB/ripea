package es.caib.ripea.service.resourceservice;

import es.caib.ripea.persistence.entity.resourceentity.MetaDocumentFluxPortafibResourceEntity;
import es.caib.ripea.service.base.service.BaseMutableResourceService;
import es.caib.ripea.service.intf.model.MetaDocumentFluxPortafibResource;
import es.caib.ripea.service.intf.resourceservice.MetaDocumentFluxPortafibResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MetaDocumentFluxPortafibResourceServiceImpl extends BaseMutableResourceService<MetaDocumentFluxPortafibResource, Long, MetaDocumentFluxPortafibResourceEntity> implements MetaDocumentFluxPortafibResourceService {

}