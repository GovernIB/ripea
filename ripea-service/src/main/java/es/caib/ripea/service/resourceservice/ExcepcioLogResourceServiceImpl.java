package es.caib.ripea.service.resourceservice;

import java.util.Map;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.caib.ripea.persistence.entity.resourceentity.ExcepcioLogResourceEntity;
import es.caib.ripea.service.base.service.BaseReadonlyResourceService;
import es.caib.ripea.service.intf.model.ExcepcioLogResource;
import es.caib.ripea.service.intf.resourceservice.ExcepcioLogService;
import es.caib.ripea.service.resourcehelper.inici.RecomptesRecurs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExcepcioLogResourceServiceImpl extends BaseReadonlyResourceService<ExcepcioLogResource, Long, ExcepcioLogResourceEntity> implements ExcepcioLogService, RecomptesRecurs<ExcepcioLogResourceEntity> {

	/** Recomptes de la pantalla d'inici sobre el registre d'excepcions (veure RecomptesRecurs). */
	@Override
	@Transactional(readOnly = true)
	public Map<String, Long> comptar(Map<String, Specification<ExcepcioLogResourceEntity>> criteris, String[] namedQueries) {
		return RecomptesRecurs.comptar(criteris, this.<ExcepcioLogResourceEntity>toFindProcessedSpecification(null, null, namedQueries), entityRepository);
	}

}
