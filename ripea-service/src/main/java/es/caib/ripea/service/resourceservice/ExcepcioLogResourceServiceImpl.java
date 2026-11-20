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
import es.caib.ripea.service.resourcehelper.inici.LlistesRecurs;
import es.caib.ripea.service.resourcehelper.inici.RecomptesRecurs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExcepcioLogResourceServiceImpl extends BaseReadonlyResourceService<ExcepcioLogResource, Long, ExcepcioLogResourceEntity> implements ExcepcioLogService, RecomptesRecurs<ExcepcioLogResourceEntity>, LlistesRecurs<ExcepcioLogResourceEntity> {

	/** Recomptes de la pantalla d'inici sobre el registre d'excepcions (veure RecomptesRecurs). */
	@Override
	@Transactional(readOnly = true)
	public Map<String, Long> comptar(Map<String, Specification<ExcepcioLogResourceEntity>> criteris, String[] namedQueries) {
		return RecomptesRecurs.comptar(criteris, this.<ExcepcioLogResourceEntity>toFindProcessedSpecification(null, null, namedQueries), entityRepository);
	}

	/** Llistes de la pantalla d'inici amb la visibilitat del llistat (veure LlistesRecurs). */
	@Override
	@Transactional(readOnly = true)
	public <T> T ambVisibles(String[] namedQueries, Function<Specification<ExcepcioLogResourceEntity>, T> consulta) {
		return consulta.apply(this.<ExcepcioLogResourceEntity>toFindProcessedSpecification(null, null, namedQueries));
	}

}
