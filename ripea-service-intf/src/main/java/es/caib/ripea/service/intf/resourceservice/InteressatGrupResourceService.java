package es.caib.ripea.service.intf.resourceservice;

import java.util.List;

import es.caib.ripea.service.intf.base.service.MutableResourceService;
import es.caib.ripea.service.intf.model.InteressatGrupResource;

public interface InteressatGrupResourceService extends MutableResourceService<InteressatGrupResource, Long> {

	List<InteressatGrupResource> findBySpringFilter(String springFilter);
	
}
