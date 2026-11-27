package es.caib.ripea.service.resourceservice;

import java.util.Map;
import java.util.function.Function;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.caib.ripea.persistence.entity.resourceentity.ExcepcioLogResourceEntity;
import es.caib.ripea.service.base.service.BaseReadonlyResourceService;
import es.caib.ripea.service.intf.model.ExcepcioLogResource;
import es.caib.ripea.service.intf.resourceservice.ExcepcioLogService;
import es.caib.ripea.service.resourcehelper.inici.VisibilitatRecurs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExcepcioLogResourceServiceImpl extends BaseReadonlyResourceService<ExcepcioLogResource, Long, ExcepcioLogResourceEntity> implements ExcepcioLogService, VisibilitatRecurs<ExcepcioLogResourceEntity> {

	/** Consultes de la pantalla d'inici (targetes i llistes) amb la visibilitat del llistat (veure VisibilitatRecurs). */
	@Override
	@Transactional(readOnly = true)
	public <T> T ambVisibles(String[] namedQueries, Function<Specification<ExcepcioLogResourceEntity>, T> consulta) {
		return consulta.apply(this.<ExcepcioLogResourceEntity>toFindProcessedSpecification(null, null, namedQueries));
	}

}
