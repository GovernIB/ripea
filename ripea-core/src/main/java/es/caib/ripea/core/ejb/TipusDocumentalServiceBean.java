package es.caib.ripea.core.ejb;

import java.util.List;

import javax.annotation.security.RolesAllowed;

import org.springframework.beans.factory.annotation.Autowired;

import es.caib.ripea.core.api.dto.PaginaDto;
import es.caib.ripea.core.api.dto.PaginacioParamsDto;
import es.caib.ripea.core.api.dto.TipusDocumentalDto;
import es.caib.ripea.core.api.exception.NotFoundException;
import es.caib.ripea.core.api.service.TipusDocumentalService;

public class TipusDocumentalServiceBean implements TipusDocumentalService {

	@Autowired
	private TipusDocumentalService delegate;
	
	@Override
	@RolesAllowed("IPA_ADMIN")
	public TipusDocumentalDto create(
			Long entitatId, 
			TipusDocumentalDto tipusDocumental) throws NotFoundException {
		return delegate.create(
				entitatId, 
				tipusDocumental);
	}

	@Override
	@RolesAllowed("IPA_ADMIN")
	public TipusDocumentalDto update(
			Long entitatId, 
			TipusDocumentalDto tipusDocumental) throws NotFoundException {
		return delegate.update(
				entitatId,
				tipusDocumental);
	}

	@Override
	@RolesAllowed("IPA_ADMIN")
	public TipusDocumentalDto delete(
			Long entitatId, 
			Long id) throws NotFoundException {
		return delegate.delete(
				entitatId, 
				id);
	}

	@Override
	@RolesAllowed("IPA_ADMIN")
	public TipusDocumentalDto findById(
			Long entitatId, 
			Long id) throws NotFoundException {
		return delegate.findById(
				entitatId, 
				id);
	}

	@Override
	@RolesAllowed("IPA_ADMIN")
	public PaginaDto<TipusDocumentalDto> findByEntitatPaginat(
			Long entitatId,
			PaginacioParamsDto paginacioParams)
			throws NotFoundException {
		return delegate.findByEntitatPaginat(
				entitatId, 
				paginacioParams);
	}

	@Override
	@RolesAllowed("IPA_ADMIN")
	public List<TipusDocumentalDto> findByEntitat(
			Long entitatId) throws NotFoundException {
		return delegate.findByEntitat(entitatId);
	}
}
