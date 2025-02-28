/**
 * 
 */
package es.caib.ripea.ejb;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import es.caib.ripea.ejb.base.AbstractServiceEjb;
import es.caib.ripea.service.intf.dto.UsuariDto;
import es.caib.ripea.service.intf.service.ExpedientSeguidorService;
import lombok.experimental.Delegate;

@Stateless
public class ExpedientSeguidorServiceEjb extends AbstractServiceEjb<ExpedientSeguidorService> implements ExpedientSeguidorService {

 	@Delegate private ExpedientSeguidorService delegateService;

	protected void setDelegateService(ExpedientSeguidorService delegateService) {
		this.delegateService = delegateService;
	}
	
	@Override
	@RolesAllowed("**")
	public void follow(Long entitatId, Long expedientId) {
		delegateService.follow(
				entitatId, 
				expedientId);
	}

	@Override
	@RolesAllowed("**")
	public void unfollow(Long entitatId, Long expedientId) {
		delegateService.unfollow(
				entitatId,
				expedientId);
	}

	@Override
	@RolesAllowed("**")
	public List<UsuariDto> getFollowersExpedient(Long entitatId, Long expedientId) {
		return delegateService.getFollowersExpedient(
				entitatId, 
				expedientId);
	}

}
