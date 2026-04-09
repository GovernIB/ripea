package es.caib.ripea.service.resourceservice;

import org.springframework.stereotype.Service;

import es.caib.ripea.persistence.entity.resourceentity.ExcepcioLogResourceEntity;
import es.caib.ripea.service.base.service.BaseReadonlyResourceService;
import es.caib.ripea.service.intf.model.ExcepcioLogResource;
import es.caib.ripea.service.intf.resourceservice.ExcepcioLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExcepcioLogResourceServiceImpl extends BaseReadonlyResourceService<ExcepcioLogResource, Long, ExcepcioLogResourceEntity> implements ExcepcioLogService {}