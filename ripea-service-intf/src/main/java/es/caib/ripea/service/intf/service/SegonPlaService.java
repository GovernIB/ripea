package es.caib.ripea.service.intf.service;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

import es.caib.comanda.model.server.monitoring.DimensioDesc;
import es.caib.comanda.model.server.monitoring.EntitatDesc;
import es.caib.comanda.model.server.monitoring.IndicadorDesc;
import es.caib.comanda.model.server.monitoring.RegistresEstadistics;
import es.caib.ripea.service.intf.dto.ExplotFetsAmbDimensioDto;

@PreAuthorize("isAuthenticated()")
public interface SegonPlaService {

	int consultarIGuardarAnotacionsPeticionsPendents() throws Throwable;

	void buidarCacheDominis();

	int enviarEmailsPendentsAgrupats();

	void testEmailsAgrupats();

	int guardarExpedientsDocumentsArxiu();

	void guardarInteressatsArxiu();

	void actualitzarProcediments();

	void consultaCanvisOrganigrama();
	
	void reintentarCanviEstatDistribucio();

	void enviarEmailPerComentariMetaExpedient();

	void restartSchedulledTasks(String taskCodi);
	
	void tancarExpedientsArxiu();
	
	void generarJsonMetriques() throws Exception;
	
	@PreAuthorize("hasRole('IPA_COM')")
	List<ExplotFetsAmbDimensioDto> generarEstadistiquesDiaries(Date fecha) throws Exception;
	
	@PreAuthorize("hasRole('IPA_COM')")
	public boolean existeixenEstadistiques(LocalDate date);
	
	@PreAuthorize("hasRole('IPA_COM')")
	public RegistresEstadistics consultaEstadistiques(LocalDate date);	
	
	@PreAuthorize("hasRole('IPA_COM')")
	public List<DimensioDesc> getDimensionsInfo();
	
	@PreAuthorize("hasRole('IPA_COM')")
	public List<IndicadorDesc> getIndicadorsInfo();
	
	@PreAuthorize("hasRole('IPA_COM')")
	public List<EntitatDesc> getEntitatsInfo();
}