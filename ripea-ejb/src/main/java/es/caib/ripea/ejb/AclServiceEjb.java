/**
 * 
 */
package es.caib.ripea.ejb;

import es.caib.ripea.ejb.base.AbstractServiceEjb;
import es.caib.ripea.service.intf.exception.NotFoundException;
import lombok.experimental.Delegate;
import org.springframework.security.acls.model.Acl;
import org.springframework.security.acls.model.AclService;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.acls.model.Sid;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import java.util.List;
import java.util.Map;

/**
 * Implementació de AclService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class AclServiceEjb extends AbstractServiceEjb<AclService> implements AclService {

	@Delegate
	private AclService delegateService = null;

	protected void setDelegateService(AclService delegateService) {
		this.delegateService = delegateService;
	}

	@Override
	@RolesAllowed("**")
	public List<ObjectIdentity> findChildren(ObjectIdentity parentIdentity) {
		return delegateService.findChildren(parentIdentity);
	}

	@Override
	@RolesAllowed("**")
	public Acl readAclById(
			ObjectIdentity object) throws NotFoundException {
		return delegateService.readAclById(object);
	}

	@Override
	@RolesAllowed("**")
	public Acl readAclById(
			ObjectIdentity object,
			List<Sid> sids) throws NotFoundException {
		return delegateService.readAclById(object, sids);
	}

	@Override
	@RolesAllowed("**")
	public Map<ObjectIdentity, Acl> readAclsById(
			List<ObjectIdentity> objects) throws NotFoundException {
		return delegateService.readAclsById(objects);
	}

	@Override
	@RolesAllowed("**")
	public Map<ObjectIdentity, Acl> readAclsById(
			List<ObjectIdentity> objects,
			List<Sid> sids) throws NotFoundException {
		return delegateService.readAclsById(objects, sids);
	}

}
