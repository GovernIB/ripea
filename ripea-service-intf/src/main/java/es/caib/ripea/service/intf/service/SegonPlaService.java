package es.caib.ripea.service.intf.service;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

import es.caib.comanda.model.v1.estadistica.DimensioDesc;
import es.caib.comanda.model.v1.estadistica.IndicadorDesc;
import es.caib.comanda.model.v1.estadistica.RegistresEstadistics;
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
	
	List<ExplotFetsAmbDimensioDto> generarEstadistiquesDiaries(Date fecha) throws Exception;
	
	public boolean existeixenEstadistiques(LocalDate date);
	
	public RegistresEstadistics consultaEstadistiques(LocalDate date);	
	
	public List<DimensioDesc> getDimensionsInfo();
	
	public List<IndicadorDesc> getIndicadorsInfo();
}