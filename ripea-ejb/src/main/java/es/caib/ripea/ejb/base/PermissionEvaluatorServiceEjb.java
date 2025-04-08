package es.caib.ripea.ejb.base;

import es.caib.ripea.service.intf.base.service.PermissionEvaluatorService;
import lombok.experimental.Delegate;
import org.springframework.security.core.Authentication;

import javax.ejb.Stateless;
import java.io.Serializable;

@Stateless
public class PermissionEvaluatorServiceEjb extends AbstractServiceEjb<PermissionEvaluatorService> implements PermissionEvaluatorService {


    @Delegate
    private PermissionEvaluatorService delegateService;

    protected void setDelegateService(PermissionEvaluatorService delegateService) {
        this.delegateService = delegateService;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        return delegateService.hasPermission(authentication, targetDomainObject, permission);
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        return delegateService.hasPermission(authentication, targetId, targetType, permission);
    }
}
