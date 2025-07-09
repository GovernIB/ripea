package es.caib.ripea.service.intf.resourceservice;

import es.caib.ripea.service.intf.base.service.MutableResourceService;
import es.caib.ripea.service.intf.model.InteressatResource;

import java.util.List;

public interface InteressatResourceService extends MutableResourceService<InteressatResource, Long> {

    List<InteressatResource> findBySpringFilter(String springFilter);

}