package es.caib.ripea.ejb;

import es.caib.ripea.ejb.base.AbstractServiceEjb;
import es.caib.ripea.service.intf.resourceservice.DocumentNotificacioResourceService;
import lombok.experimental.Delegate;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

@Stateless
@RolesAllowed("**")
public class DocumentNotificacioResourceServiceEjb extends AbstractServiceEjb<DocumentNotificacioResourceService> implements DocumentNotificacioResourceService {

    @Delegate private DocumentNotificacioResourceService delegateService;

    protected void setDelegateService(DocumentNotificacioResourceService delegateService) {
        this.delegateService = delegateService;
    }

}
