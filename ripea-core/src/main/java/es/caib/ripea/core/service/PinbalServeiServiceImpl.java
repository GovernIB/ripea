package es.caib.ripea.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.caib.ripea.core.api.dto.PaginaDto;
import es.caib.ripea.core.api.dto.PaginacioParamsDto;
import es.caib.ripea.core.api.dto.PinbalServeiDto;
import es.caib.ripea.core.api.service.PinbalServeiService;
import es.caib.ripea.core.entity.PinbalServeiEntity;
import es.caib.ripea.core.helper.ConversioTipusHelper;
import es.caib.ripea.core.helper.PaginacioHelper;
import es.caib.ripea.core.repository.PinbalServeiRepository;

@Service
public class PinbalServeiServiceImpl implements PinbalServeiService {
	

	@Autowired
	private PaginacioHelper paginacioHelper;
	@Autowired
	private PinbalServeiRepository pinbalServeiRepository;
	@Autowired
	private ConversioTipusHelper conversioTipusHelper;

	
	
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

		PinbalServeiEntity pinbalServei = pinbalServeiRepository.findOne(id);
		return conversioTipusHelper.convertir(pinbalServei, PinbalServeiDto.class);
	}
	
	@Transactional(readOnly = true)
	@Override
	public PinbalServeiDto findByCodi(String codi) {

		PinbalServeiEntity pinbalServei = pinbalServeiRepository.findByCodi(codi);
		return conversioTipusHelper.convertir(pinbalServei, PinbalServeiDto.class);
	}
	
	
	@Transactional
	@Override
	public void update(PinbalServeiDto pinbalServei) {
		PinbalServeiEntity pinbalServeiEntity =  pinbalServeiRepository.findOne(pinbalServei.getId());
		pinbalServeiEntity.update(pinbalServei.getPinbalServeiDocsPermesos());

	}
	
}