package es.caib.ripea.core.api.service;

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
