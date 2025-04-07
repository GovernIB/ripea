package es.caib.ripea.ejb;

import es.caib.ripea.ejb.base.AbstractServiceEjb;
import es.caib.ripea.service.intf.resourceservice.DocumentEnviamentInteressatResourceService;
import lombok.experimental.Delegate;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

@Stateless
@RolesAllowed("**")
public class DocumentEnviamentInteressatResourceServiceEjb extends AbstractServiceEjb<DocumentEnviamentInteressatResourceService> implements DocumentEnviamentInteressatResourceService {

    @Delegate private DocumentEnviamentInteressatResourceService delegateService;

    protected void setDelegateService(DocumentEnviamentInteressatResourceService delegateService) {
        this.delegateService = delegateService;
    }
}
