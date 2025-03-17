/**
 * 
 */
package es.caib.ripea.ejb;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import es.caib.ripea.ejb.base.AbstractServiceEjb;
import es.caib.ripea.service.intf.dto.EntitatDto;
import es.caib.ripea.service.intf.dto.PaginaDto;
import es.caib.ripea.service.intf.dto.PaginacioParamsDto;
import es.caib.ripea.service.intf.dto.PermisDto;
import es.caib.ripea.service.intf.service.EntitatService;
import lombok.experimental.Delegate;

@Stateless
public class EntitatServiceEjb extends AbstractServiceEjb<EntitatService> implements EntitatService {

	@Delegate private EntitatService delegateService;

	protected void setDelegateService(EntitatService delegateService) {
		this.delegateService = delegateService;
	}

	@Override
	@RolesAllowed("IPA_SUPER")
	public EntitatDto create(EntitatDto entitat) {
		return delegateService.create(entitat);
	}

	@Override
	@RolesAllowed("IPA_SUPER")
	public EntitatDto update(
			EntitatDto entitat) {
		return delegateService.update(entitat);
	}

	@Override
	@RolesAllowed("IPA_SUPER")
	public EntitatDto updateActiva(
			Long id,
			boolean activa) {
		return delegateService.updateActiva(id, activa);
	}

	@Override
	@RolesAllowed("IPA_SUPER")
	public EntitatDto delete(
			Long id) {
		return delegateService.delete(id);
	}

	@Override
	@RolesAllowed("**")
	public EntitatDto findById(Long id) {
		return delegateService.findById(id);
	}

	@Override
	@RolesAllowed("IPA_SUPER")
	public EntitatDto findByCodi(String codi) {
		return delegateService.findByCodi(codi);
	}

	@Override
	@RolesAllowed("IPA_SUPER")
	public PaginaDto<EntitatDto> findPaginat(PaginacioParamsDto paginacioParams) {
		return delegateService.findPaginat(paginacioParams);
	}

	@Override
	@RolesAllowed("**")
	public List<EntitatDto> findAccessiblesUsuariActual() {
		return delegateService.findAccessiblesUsuariActual();
	}

	@Override
	@RolesAllowed("IPA_SUPER")
	public List<PermisDto> findPermisSuper(Long id) {
		return delegateService.findPermisSuper(id);
	}

	@Override
	@RolesAllowed("IPA_SUPER")
	public void updatePermisSuper(
			Long id,
			PermisDto permis) {
		delegateService.updatePermisSuper(
				id,
				permis);
	}
	@Override
	@RolesAllowed("IPA_SUPER")
	public void deletePermisSuper(
			Long id,
			Long permisId) {
		delegateService.deletePermisSuper(
				id,
				permisId);
	}

	@Override
	@RolesAllowed("IPA_ADMIN")
	public List<PermisDto> findPermisAdmin(Long id) {
		return delegateService.findPermisAdmin(id);
	}

	@Override
	@RolesAllowed("IPA_ADMIN")
	public void updatePermisAdmin(
			Long id,
			PermisDto permis) {
		delegateService.updatePermisAdmin(
				id,
				permis);
	}
	@Override
	@RolesAllowed("IPA_ADMIN")
	public void deletePermisAdmin(
			Long id,
			Long permisId) {
		delegateService.deletePermisAdmin(
				id,
				permisId);
	}

	@Override
	@RolesAllowed("**")
	public byte[] getLogo() throws NoSuchFileException, IOException {
		return delegateService.getLogo();
	}

	@Override
	@RolesAllowed("**")
	public void evictEntitatsAccessiblesUsuari() {
		delegateService.evictEntitatsAccessiblesUsuari();
	}

	@Override
	@RolesAllowed("**")
	public EntitatDto findByUnitatArrel(String unitatArrel) {
		return delegateService.findByUnitatArrel(unitatArrel);
	}

	@Override
	@RolesAllowed({"IPA_SUPER", "IPA_ADMIN"})
	public List<EntitatDto> findAll() {
		return delegateService.findAll();
	}

	@Override
	@RolesAllowed("**")
	public void setConfigEntitat(EntitatDto entitatDto) {
		delegateService.setConfigEntitat(entitatDto);
	}

    @Override
	@RolesAllowed("**")
    public void removeEntitatPerDefecteUsuari(String usuariCodi) {
        delegateService.removeEntitatPerDefecteUsuari(usuariCodi);
    }

}
