package es.caib.ripea.ejb;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import es.caib.ripea.ejb.base.AbstractServiceEjb;
import es.caib.ripea.service.intf.dto.DigitalitzacioPerfilDto;
import es.caib.ripea.service.intf.dto.DigitalitzacioResultatDto;
import es.caib.ripea.service.intf.dto.DigitalitzacioTransaccioRespostaDto;
import es.caib.ripea.service.intf.service.DigitalitzacioService;
import lombok.experimental.Delegate;

@Stateless
@RolesAllowed("**")
public class DigitalitzacioServiceEjb extends AbstractServiceEjb<DigitalitzacioService> implements DigitalitzacioService {

	@Delegate private DigitalitzacioService delegateService;

	protected void setDelegateService(DigitalitzacioService delegateService) {
		this.delegateService = delegateService;
	}

	@Override
	@RolesAllowed("**")
	public List<DigitalitzacioPerfilDto> getPerfilsDisponibles() {
		return delegateService.getPerfilsDisponibles();
	}

	@Override
	@RolesAllowed("**")
	public DigitalitzacioTransaccioRespostaDto iniciarDigitalitzacio(String codiPerfil, String urlReturn) {
		return delegateService.iniciarDigitalitzacio(
				codiPerfil, 
				urlReturn);
	}

	@Override
	@RolesAllowed("**")
	public DigitalitzacioResultatDto recuperarResultat(String idTransaccio, boolean returnScannedFile,
			boolean returnSignedFile) {
		return delegateService.recuperarResultat(
				idTransaccio, 
				returnScannedFile, 
				returnSignedFile);
	}

	@Override
	@RolesAllowed("**")
	public void tancarTransaccio(String idTransaccio) {
		delegateService.tancarTransaccio(idTransaccio);
	}

}
