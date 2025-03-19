package es.caib.ripea.service.intf.service;

import org.springframework.security.access.prepost.PreAuthorize;

@PreAuthorize("isAuthenticated()")
public interface SegonPlaService {

	void consultarIGuardarAnotacionsPeticionsPendents() throws Throwable;

	void buidarCacheDominis();

	void enviarEmailsPendentsAgrupats();

	void testEmailsAgrupats();

	void guardarExpedientsDocumentsArxiu();

	void guardarInteressatsArxiu();

	void actualitzarProcediments();

	void consultaCanvisOrganigrama();
	
	void reintentarCanviEstatDistribucio();

	void enviarEmailPerComentariMetaExpedient();

	void restartSchedulledTasks(String taskCodi);
	
	void tancarExpedientsArxiu();
}