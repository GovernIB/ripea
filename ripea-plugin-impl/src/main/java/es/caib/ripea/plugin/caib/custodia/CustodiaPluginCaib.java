/**
 * 
 */
package es.caib.ripea.plugin.caib.custodia;

import java.io.InputStream;

import es.caib.ripea.plugin.SistemaExternException;
import es.caib.ripea.plugin.caib.custodia.ClienteCustodia.CustodiaResponse;
import es.caib.ripea.plugin.custodia.CustodiaPlugin;
import es.caib.ripea.plugin.PropertiesHelper;

/**
 * Implementació del plugin de custòdia de documents emprant
 * la custòdia de la CAIB.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class CustodiaPluginCaib implements CustodiaPlugin {

	@Override
	public void custodiarPdfFirmat(
			String documentId,
			String documentTipus,
			String arxiuNom,
			InputStream arxiuContingut) throws SistemaExternException {
		String errorDescripcio = "No s'ha pogut custodiar el PDF firmat (" +
				"documentId=" + documentId + ", " +
				"documentTipus=" + documentTipus + ", " +
				"arxiuNom=" + arxiuNom + ", " +
				"arxiuContingut=" + arxiuContingut + ")";
		if (documentTipus == null || documentTipus.isEmpty()) {
			throw new SistemaExternException(
					errorDescripcio + ": no s'ha especificat cap tipus de document");
		}
		try {
			CustodiaResponse response = getClienteCustodia().custodiarPDFFirmado(
					arxiuContingut,
					arxiuNom,
					documentId,
					documentTipus);
			detectarErrorCustodiaResponse(
					response,
					errorDescripcio,
					null);
		} catch (SistemaExternException sex) {
			throw sex;
		} catch (Exception ex) {
			throw new SistemaExternException(
					errorDescripcio,
					ex);
		}
	}

	@Override
	public byte[] obtenirDocumentCustodiat(
			String documentId) throws SistemaExternException {
		String errorDescripcio = "No s'ha pogut obtenir el document custodiat (" +
				"documentId=" + documentId + ")";
		try {
			CustodiaResponse response = getClienteCustodia().recuperarDocumento(
					documentId);
			detectarErrorCustodiaResponse(
					response,
					errorDescripcio,
					null);
			return response.getBytes();
		} catch (SistemaExternException sex) {
			throw sex;
		} catch (Exception ex) {
			throw new SistemaExternException(
					errorDescripcio,
					ex);
		}
	}

	@Override
	public void esborrarDocumentCustodiat(
			String documentId,
			boolean ignorarNotFound) throws SistemaExternException {
		String errorDescripcio = "No s'ha pogut esborrar el document (" +
				"documentId=" + documentId + ")";
		try {
			CustodiaResponse response = getClienteCustodia().eliminarDocumento(
					documentId);
			String[] resulMinorIgnorats = null;
			if (ignorarNotFound) {
				resulMinorIgnorats = new String[] {"DOCUMENTO_NO_ENCONTRADO"};
			}
			detectarErrorCustodiaResponse(
					response,
					errorDescripcio,
					resulMinorIgnorats);
		} catch (SistemaExternException sex) {
			throw sex;
		} catch (Exception ex) {
			throw new SistemaExternException(
					errorDescripcio,
					ex);
		}
	}

	@Override
	public String reservarUrl(
			String documentId) throws SistemaExternException {
		String errorDescripcio = "No s'ha pogut reservar la URL (" +
				"documentId=" + documentId + ")";
		try {
			CustodiaResponse response = getClienteCustodia().reservarDocumento(
					documentId);
			detectarErrorCustodiaResponse(
					response,
					errorDescripcio,
					null);
			return getVerificacioBaseUrl() + response.getBytesAsString();
		} catch (SistemaExternException sex) {
			throw sex;
		} catch (Exception ex) {
			throw new SistemaExternException(
					errorDescripcio,
					ex);
		}
	}



	private void detectarErrorCustodiaResponse(
			CustodiaResponse response,
			String errorDescripcio,
			String[] resulMinorIgnorats) throws SistemaExternException {
		if (response.isError()) {
			boolean ignorar = false;
			if (resulMinorIgnorats != null) {
				for (String rmi: resulMinorIgnorats) {
					if (rmi.equals(response.getResultMinor())) {
						ignorar = true;
						break;
					}
				}
			}
			if (!ignorar) {
				throw new SistemaExternException(
						errorDescripcio + ": " + response.errorToString());
			}
		}
	}

	private ClienteCustodia clienteCustodia;
	private ClienteCustodia getClienteCustodia() {
		if (clienteCustodia == null) {
			clienteCustodia = new ClienteCustodia(
					getServiceUrl(),
					getUsername(),
					getPassword());
		}
		return clienteCustodia;
	}

	private String getServiceUrl() {
		return PropertiesHelper.getProperties().getProperty(
				"es.caib.ripea.plugin.custodia.caib.service.url");
	}
	private String getUsername() {
		return PropertiesHelper.getProperties().getProperty(
				"es.caib.ripea.plugin.custodia.caib.username");
	}
	private String getPassword() {
		return PropertiesHelper.getProperties().getProperty(
				"es.caib.ripea.plugin.custodia.caib.password");
	}
	private String getVerificacioBaseUrl() {
		return PropertiesHelper.getProperties().getProperty(
				"es.caib.ripea.plugin.custodia.caib.verificacio.base.url");
	}

}
