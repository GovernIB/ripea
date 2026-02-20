package es.caib.ripea.service.resourceservice;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import es.caib.ripea.persistence.entity.resourceentity.ExcepcioLogResourceEntity;
import es.caib.ripea.service.base.service.BaseReadonlyResourceService;
import es.caib.ripea.service.helper.ExcepcioLogHelper;
import es.caib.ripea.service.intf.base.exception.ResourceNotFoundException;
import es.caib.ripea.service.intf.dto.ExcepcioLogDto;
import es.caib.ripea.service.intf.model.ExcepcioLogResource;
import es.caib.ripea.service.intf.resourceservice.ExcepcioLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExcepcioLogResourceServiceImpl extends BaseReadonlyResourceService<ExcepcioLogResource, Long, ExcepcioLogResourceEntity> implements ExcepcioLogService {

	private final ExcepcioLogHelper excepcioLogHelper;
	
	@Override
	public boolean isEntityRepositoryOptional() {
		return true;
	}
	
	@Override
	public Page<ExcepcioLogResource> findPage(
			String quickFilter,
			String filter,
			String[] namedQueries,
			String[] perspectives,
			Pageable pageable) {
		List<ExcepcioLogDto> excepcions = excepcioLogHelper.findAll();
		if (excepcions!=null) {
			List<ExcepcioLogResource> aux = new ArrayList<ExcepcioLogResource>();
			for (ExcepcioLogDto ex: excepcions) {
				aux.add(objectMappingHelper.newInstanceMap(ex, ExcepcioLogResource.class,"serialVersionUID"));
			}
			Page<ExcepcioLogResource> page = new PageImpl<>(aux, pageable, excepcions.size());
			return page;
		}
		return null;
	}
	
	@Override
	public ExcepcioLogResource getOne(
			Long id,
			String[] perspectives) throws ResourceNotFoundException {
		ExcepcioLogDto aux = excepcioLogHelper.findAll().get(id.intValue());
		return objectMappingHelper.newInstanceMap(aux, ExcepcioLogResource.class);
	}
}
