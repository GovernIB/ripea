package es.caib.ripea.service.intf.service;

import es.caib.ripea.service.intf.dto.PaginaDto;
import es.caib.ripea.service.intf.dto.PaginacioParamsDto;
import es.caib.ripea.service.intf.dto.PinbalServeiDto;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface PinbalServeiService {

	@PreAuthorize("hasRole('IPA_SUPER')")
	public PaginaDto<PinbalServeiDto> findPaginat(PaginacioParamsDto paginacioParams);

	@PreAuthorize("hasRole('IPA_SUPER')")
	public PinbalServeiDto update(PinbalServeiDto pinbalServei);

	@PreAuthorize("hasRole('IPA_SUPER')")
	public PinbalServeiDto findById(Long id);

	@PreAuthorize("isAuthenticated()")
	public List<PinbalServeiDto> findActius();
	
	@PreAuthorize("isAuthenticated()")
	public List<PinbalServeiDto> findAll();
}