package es.caib.ripea.service.service;

import es.caib.ripea.persistence.entity.PinbalServeiEntity;
import es.caib.ripea.persistence.repository.PinbalServeiRepository;
import es.caib.ripea.service.helper.ConversioTipusHelper;
import es.caib.ripea.service.helper.PaginacioHelper;
import es.caib.ripea.service.intf.dto.PaginaDto;
import es.caib.ripea.service.intf.dto.PaginacioParamsDto;
import es.caib.ripea.service.intf.dto.PinbalServeiDto;
import es.caib.ripea.service.intf.service.PinbalServeiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PinbalServeiServiceImpl implements PinbalServeiService {

	@Autowired private PaginacioHelper paginacioHelper;
	@Autowired private PinbalServeiRepository pinbalServeiRepository;
	@Autowired private ConversioTipusHelper conversioTipusHelper;
	
	@Transactional(readOnly = true)
	@Override
	public PaginaDto<PinbalServeiDto> findPaginat(PaginacioParamsDto paginacioParams) {
		PaginaDto<PinbalServeiDto> resposta = paginacioHelper.toPaginaDto(
				pinbalServeiRepository.findPaginat(paginacioHelper.toSpringDataPageable(paginacioParams)),
				PinbalServeiDto.class);
		return resposta;
	}
	
	@Transactional(readOnly = true)
	@Override
	public PinbalServeiDto findById(Long id) {
		PinbalServeiEntity pinbalServei = pinbalServeiRepository.getOne(id);
		return conversioTipusHelper.convertir(pinbalServei, PinbalServeiDto.class);
	}
	
	@Transactional
	@Override
	public PinbalServeiDto update(PinbalServeiDto pinbalServei) {
		PinbalServeiEntity pinbalServeiEntity =  pinbalServeiRepository.getOne(pinbalServei.getId());
		pinbalServeiEntity.update(pinbalServei.getPinbalServeiDocsPermesos());
		pinbalServeiEntity.setActiu(pinbalServei.isActiu());
		pinbalServeiEntity.setNom(pinbalServei.getNom());
		return pinbalServei;
	}

	@Transactional(readOnly = true)
	@Override
	public List<PinbalServeiDto> findActius() {
		return conversioTipusHelper.convertirList(pinbalServeiRepository.findActiusOrderByNom(), PinbalServeiDto.class);
	}
	
	@Transactional(readOnly = true)
	@Override
	public List<PinbalServeiDto> findAll() {
		return conversioTipusHelper.convertirList(pinbalServeiRepository.findAll(), PinbalServeiDto.class);
	}
}