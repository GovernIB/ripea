package es.caib.ripea.ejb;

import es.caib.ripea.ejb.base.AbstractServiceEjb;
import es.caib.ripea.service.intf.resourceservice.DocumentPublicacioResourceService;
import lombok.experimental.Delegate;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

@Stateless
@RolesAllowed("**")
public class DocumentPublicacioResourceServiceEjb extends AbstractServiceEjb<DocumentPublicacioResourceService> implements DocumentPublicacioResourceService {

    @Delegate private DocumentPublicacioResourceService delegateService;

    protected void setDelegateService(DocumentPublicacioResourceService delegateService) {
        this.delegateService = delegateService;
    }

}
