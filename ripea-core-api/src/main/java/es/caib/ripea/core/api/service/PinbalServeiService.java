package es.caib.ripea.core.api.service;

import org.springframework.security.access.prepost.PreAuthorize;

import es.caib.ripea.core.api.dto.PaginaDto;
import es.caib.ripea.core.api.dto.PaginacioParamsDto;
import es.caib.ripea.core.api.dto.PinbalServeiDto;

public interface PinbalServeiService {

	@PreAuthorize("hasRole('IPA_SUPER')")
	public PaginaDto<PinbalServeiDto> findPaginat(PaginacioParamsDto paginacioParams);

	@PreAuthorize("hasRole('IPA_SUPER')")
	public void update(PinbalServeiDto pinbalServei);

	@PreAuthorize("hasRole('IPA_SUPER')")
	public PinbalServeiDto findById(Long id);



}