package es.caib.ripea.service.resourceservice;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import es.caib.ripea.persistence.entity.resourceentity.DocumentPortafirmesResourceEntity;
import es.caib.ripea.service.base.service.BaseMutableResourceService;
import es.caib.ripea.service.intf.model.DocumentPortafirmesResource;
import es.caib.ripea.service.intf.resourceservice.DocumentPortafirmesResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentPortafirmesResourceServiceImpl extends BaseMutableResourceService<DocumentPortafirmesResource, Long, DocumentPortafirmesResourceEntity> implements DocumentPortafirmesResourceService {

    @PostConstruct
    public void init() {}

    @Override
    protected void afterConversion(DocumentPortafirmesResourceEntity entity, DocumentPortafirmesResource resource) {}
}