/**
 * 
 */
package es.caib.ripea.plugin.caib.custodia;

import java.io.InputStream;

import es.caib.ripea.plugin.SistemaExternException;
import es.caib.ripea.plugin.custodia.CustodiaPlugin;
import es.caib.ripea.plugin.utils.PropertiesHelper;

/**
 * Implementació de proves del plugin de custòdia.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class CustodiaPluginMock implements CustodiaPlugin {

	@Override
	public void custodiarPdfFirmat(
			String documentId,
			String documentTipus,
			String arxiuNom,
			InputStream arxiuContingut) throws SistemaExternException {
		//throw new SistemaExternException("Això no acaba d'anar be");
	}

	@Override
	public byte[] obtenirDocumentCustodiat(
			String documentId) throws SistemaExternException {
		throw new SistemaExternException("Això no acaba d'anar be");
		//return new byte[0];
	}

	@Override
	public void esborrarDocumentCustodiat(
			String documentId,
			boolean ignorarNotFound) throws SistemaExternException {
		throw new SistemaExternException("Això no acaba d'anar be");
	}

	@Override
	public String reservarUrl(
			String documentId) throws SistemaExternException {
		return getVerificacioBaseUrl() + System.currentTimeMillis();
	}



	private String getVerificacioBaseUrl() {
		return PropertiesHelper.getProperties().getProperty(
				"es.caib.ripea.plugin.custodia.caib.verificacio.base.url");
	}

}
