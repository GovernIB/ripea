package es.caib.ripea.ejb;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import es.caib.comanda.ms.estadistica.model.DimensioDesc;
import es.caib.comanda.ms.estadistica.model.IndicadorDesc;
import es.caib.comanda.ms.estadistica.model.RegistresEstadistics;
import es.caib.ripea.ejb.base.AbstractServiceEjb;
import es.caib.ripea.service.intf.dto.ExplotFetsAmbDimensioDto;
import es.caib.ripea.service.intf.service.SegonPlaService;
import lombok.experimental.Delegate;

@Stateless
@RolesAllowed("**")
public class SegonPlaServiceEjb extends AbstractServiceEjb<SegonPlaService> implements SegonPlaService {

	@Delegate private SegonPlaService delegateService;

	protected void setDelegateService(SegonPlaService delegateService) {
		this.delegateService = delegateService;
	}

	@Override
	@RolesAllowed("**")
	public int consultarIGuardarAnotacionsPeticionsPendents() throws Throwable {
		return delegateService.consultarIGuardarAnotacionsPeticionsPendents();
	}

	@Override
	@RolesAllowed("**")
	public void buidarCacheDominis() {
		delegateService.buidarCacheDominis();
	}

	@Override
	@RolesAllowed("**")
	public int enviarEmailsPendentsAgrupats() {
		return delegateService.enviarEmailsPendentsAgrupats();
	}

	@Override
	@RolesAllowed("**")
	public void testEmailsAgrupats() {
		delegateService.testEmailsAgrupats();
	}

	@Override
	@RolesAllowed("**")
	public int guardarExpedientsDocumentsArxiu() {
		return delegateService.guardarExpedientsDocumentsArxiu();
	}

	@Override
	@RolesAllowed("**")
	public void guardarInteressatsArxiu() {
		delegateService.guardarInteressatsArxiu();
	}

    @Override
    @RolesAllowed("**")
    public void actualitzarProcediments() {
        delegateService.actualitzarProcediments();
    }

    @Override
    @RolesAllowed("**")
    public void consultaCanvisOrganigrama() {
        delegateService.consultaCanvisOrganigrama();
    }
    
	@Override
	@RolesAllowed("**")
	public void reintentarCanviEstatDistribucio() {
		delegateService.reintentarCanviEstatDistribucio();
	}

	@Override
	@RolesAllowed("**")
	public void enviarEmailPerComentariMetaExpedient() {
		delegateService.enviarEmailPerComentariMetaExpedient();
	}

	@Override
	@RolesAllowed("**")
	public void restartSchedulledTasks(String taskCodi) {
		delegateService.restartSchedulledTasks(taskCodi);
	}

	@Override
	@RolesAllowed("**")
	public void tancarExpedientsArxiu() {
		delegateService.tancarExpedientsArxiu();
	}
	
	@Override
	@RolesAllowed("**")
	public void generarJsonMetriques() throws Exception {
		delegateService.generarJsonMetriques();
	}
	
	@Override
	@PermitAll
	public List<ExplotFetsAmbDimensioDto> generarEstadistiquesDiaries(Date fecha) throws Exception {
		return delegateService.generarEstadistiquesDiaries(fecha);
	}
	
	@Override
	@PermitAll
	public List<DimensioDesc> getDimensionsInfo() {
		return delegateService.getDimensionsInfo();
	}
	
	@Override
	@PermitAll
	public List<IndicadorDesc> getIndicadorsInfo() {
		return delegateService.getIndicadorsInfo();
	}
	
	@Override
	@PermitAll
	public RegistresEstadistics consultaEstadistiques(LocalDate date) {
		return delegateService.consultaEstadistiques(date);
	}
	
	@Override
	@PermitAll
	public boolean existeixenEstadistiques(LocalDate date) {
		return delegateService.existeixenEstadistiques(date);
	}
}