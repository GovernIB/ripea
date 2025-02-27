package es.caib.ripea.ejb;

import java.io.Serializable;
import java.util.List;

import javax.ejb.Stateless;

import es.caib.ripea.service.intf.base.model.Resource;
import es.caib.ripea.service.intf.base.permission.ResourcePermissions;
import es.caib.ripea.service.intf.base.service.ResourceApiService;
import lombok.experimental.Delegate;

@Stateless
public class ResourceApiServiceEjb implements ResourceApiService {

	@Delegate private ResourceApiService delegateService;
	
	protected void delegate(ResourceApiService delegateService) {
		this.delegateService = delegateService;
	}
	
	@Override
	public void resourceRegister(Class<? extends Resource<?>> resourceClass) {
		delegateService.resourceRegister(resourceClass);
	}

	@Override
	public List<Class<? extends Resource<?>>> resourceFindAllowed() {
		return delegateService.resourceFindAllowed();
	}

	@Override
	public ResourcePermissions permissionsCurrentUser(Class<?> resourceClass, Serializable resourceId) {
		return delegateService.permissionsCurrentUser(resourceClass, resourceId);
	}

}
