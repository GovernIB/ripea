package es.caib.ripea.service.resourceservice;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import es.caib.ripea.persistence.entity.resourceentity.IntegracioResourceEntity;
import es.caib.ripea.service.base.service.BaseMutableResourceService;
import es.caib.ripea.service.helper.IntegracioHelper;
import es.caib.ripea.service.helper.PaginacioHelper;
import es.caib.ripea.service.intf.base.exception.ActionExecutionException;
import es.caib.ripea.service.intf.base.exception.AnswerRequiredException.AnswerValue;
import es.caib.ripea.service.intf.base.exception.ResourceNotFoundException;
import es.caib.ripea.service.intf.dto.IntegracioAccioDto;
import es.caib.ripea.service.intf.dto.IntegracioDto;
import es.caib.ripea.service.intf.dto.IntegracioFiltreDto;
import es.caib.ripea.service.intf.dto.PaginaDto;
import es.caib.ripea.service.intf.model.IntegracioResource;
import es.caib.ripea.service.intf.resourceservice.IntegracioResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class IntegracioResourceServiceImpl extends BaseMutableResourceService<IntegracioResource, Long, IntegracioResourceEntity> implements IntegracioResourceService {

	private final IntegracioHelper integracioHelper;
	private final PaginacioHelper paginacioHelper;
	
    @PostConstruct
    public void init() {
    	register(IntegracioResource.ACTION_INTEGRACIONS_LIST, new IntegracionsListActionExecutor());
    }
	
	@Override
	public Page<IntegracioResource> findPage(
			String quickFilter,
			String filter,
			String[] namedQueries,
			String[] perspectives,
			Pageable pageable) {
		
		//1.- Convertir el filter en un IntegracioFiltreDto
		IntegracioFiltreDto filtre = new IntegracioFiltreDto();
		//TODO: fer la conversió quant es pugui veurer el format del filtre.
		
		//2.- Fer la cerca
		List<IntegracioAccioDto> accions = integracioHelper.findAccionsByIntegracioCodi(perspectives[0], filtre);
		
		if (accions == null || accions.isEmpty()) {
			return null;
		}

		//3. Paginar
		List<List<IntegracioAccioDto>> pagines = paginacioHelper.getPages(accions, pageable.getPageSize());
		PaginaDto<IntegracioAccioDto> pagina = paginacioHelper.toPaginaDto(pagines.get(pageable.getPageNumber()), null);
		pagina.setContingut(pagines.get(pageable.getPageNumber()));
		PaginaDto<IntegracioAccioDto> aux = paginacioHelper.prepararPagina(pagina, pagines, accions);
		
		List<IntegracioResource> resultatResource = new ArrayList<IntegracioResource>();
		if (aux!=null && aux.getContingut()!=null) {
			for (IntegracioAccioDto accioDto: aux.getContingut()) {
				resultatResource.add(objectMappingHelper.newInstanceMap(accioDto, IntegracioResource.class));
			}
		}
		
		//4.- Convertir la paginacio dto a paginació spring
	    return new PageImpl<>(
	    	resultatResource,
	        pageable,
	        aux.getElementsTotal()
	    );
		
	}
    
	@Override
	public IntegracioResource getOne(
			Long id,
			String[] perspectives) throws ResourceNotFoundException {
		
		List<IntegracioAccioDto> accions = integracioHelper.findAccionsByIntegracioCodi(perspectives[0], null);
		if (accions != null) {
			for (IntegracioAccioDto accio: accions) {
				if (accio.getTimestamp() != null && accio.getTimestamp() == id) {
					return objectMappingHelper.newInstanceMap(accio, IntegracioResource.class);
				}
			}
		}
		
		return null;
	}
    
	@Override
	public boolean isEntityRepositoryOptional() {
		return true;
	}
	
	private class IntegracionsListActionExecutor implements ActionExecutor<IntegracioResourceEntity, Serializable, IntegracioDto[]> {
		@Override
		public void onChange(Serializable id, Serializable previous, String fieldName, Object fieldValue,
				Map<String, AnswerValue> answers, String[] previousFieldNames, Serializable target) {}
		@Override
		public IntegracioDto[] exec(String code, IntegracioResourceEntity entity, Serializable params) throws ActionExecutionException {
			return integracioHelper.findAll().toArray(new IntegracioDto[0]);
		}
	}
}