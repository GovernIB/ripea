package es.caib.ripea.core.api.service;

public interface SegonPlaService {

	void consultarIGuardarAnotacionsPeticionsPendents();

	void buidarCacheDominis();

	void enviarEmailsPendentsAgrupats();

	void testEmailsAgrupats();

	void guardarExpedientsDocumentsArxiu();

	void guardarInteressatsArxiu();

	void actualitzarProcediments();

	void consultaCanvisOrganigrama();
	
	void reintentarCanviEstatDistribucio();

}
