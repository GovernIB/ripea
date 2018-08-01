/**
 * 
 */
package es.caib.ripea.plugin.caib.tramitacio;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;

import org.junit.Test;

import es.caib.bantel.ws.v2.model.datosdocumentotelematico.DatosDocumentoTelematico;
import es.caib.bantel.ws.v2.model.documentobte.DocumentoBTE;
import es.caib.bantel.ws.v2.model.referenciaentrada.ReferenciaEntrada;
import es.caib.bantel.ws.v2.model.referenciaentrada.ReferenciasEntrada;
import es.caib.bantel.ws.v2.model.tramitebte.TramiteBTE;
import es.caib.bantel.ws.v2.services.BackofficeFacade;
import es.caib.bantel.ws.v2.services.BackofficeFacadeException;
import es.caib.bantel.ws.v2.services.BackofficeFacadeService;

/**
 * Client de test per al servei bustia de RIPEA.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class TramitsSistraTest {

	private static final String ENDPOINT_ADDRESS = "https://proves.caib.es/sistraback/bantelws/services/v2/BackofficeFacade";
	private static final String USERNAME = "u101334";
	private static final String PASSWORD = "pinbal222";



	@Test
	public void test() throws MalformedURLException, BackofficeFacadeException {
		ReferenciasEntrada refs = getBackofficeFacade().obtenerNumerosEntradas(
				"EC0001CTLA",
				"EC0001CTLA",
				"X", // S, N, X
				null,
				null);
		if (refs.getReferenciaEntrada() != null) {
			/*for (ReferenciaEntrada ref: refs.getReferenciaEntrada()) {
				System.out.println(">>> " + ref.getNumeroEntrada() + ":" + ref.getClaveAcceso());
			}*/
			if (refs.getReferenciaEntrada().size() > 0) {
				ReferenciaEntrada ref = refs.getReferenciaEntrada().get(0);
				TramiteBTE tramite = getBackofficeFacade().obtenerEntrada(ref);
				System.out.println(">>> Informació del tràmit: " + ref.getNumeroEntrada() + ", " + ref.getClaveAcceso().getValue());
				System.out.println("    numeroEntrada: " + tramite.getNumeroEntrada());
				System.out.println("    codigoEntrada: " + tramite.getCodigoEntrada());
				System.out.println("    unidadAdministrativa: " + tramite.getUnidadAdministrativa());
				System.out.println("    fecha: " + tramite.getFecha());
				System.out.println("    tipo: " + tramite.getTipo());
				System.out.println("    firmadaDigitalmente: " + tramite.isFirmadaDigitalmente());
				System.out.println("    procesada: " + tramite.getProcesada());
				System.out.println("    identificadorTramite: " + tramite.getIdentificadorTramite());
				System.out.println("    versionTramite: " + tramite.getVersionTramite());
				System.out.println("    nivelAutenticacion: " + tramite.getNivelAutenticacion());
				if (tramite.getUsuarioSeycon() != null)
					System.out.println("    usuarioSeycon: " + tramite.getUsuarioSeycon().getValue());
				System.out.println("    descripcionTramite: " + tramite.getDescripcionTramite());
				System.out.println("    codigoReferenciaRDSAsiento: " + tramite.getCodigoReferenciaRDSAsiento());
				System.out.println("    claveReferenciaRDSAsiento: " + tramite.getClaveReferenciaRDSAsiento());
				System.out.println("    codigoReferenciaRDSJustificante: " + tramite.getCodigoReferenciaRDSJustificante());
				System.out.println("    claveReferenciaRDSJustificante: " + tramite.getClaveReferenciaRDSJustificante());
				System.out.println("    numeroRegistro: " + tramite.getNumeroRegistro());
				System.out.println("    fechaRegistro: " + tramite.getFechaRegistro());
				if (tramite.getNumeroPreregistro() != null)
					System.out.println("    numeroPreregistro: " + tramite.getNumeroPreregistro().getValue());
				if (tramite.getFechaPreregistro() != null)
					System.out.println("    fechaPreregistro: " + tramite.getFechaPreregistro().getValue());
				if (tramite.getUsuarioNif() != null)
					System.out.println("    usuarioNif: " + tramite.getUsuarioNif().getValue());
				if (tramite.getUsuarioNombre() != null)
					System.out.println("    usuarioNombre: " + tramite.getUsuarioNombre().getValue());
				if (tramite.getRepresentadoNif() != null)
					System.out.println("    representadoNif: " + tramite.getRepresentadoNif().getValue());
				if (tramite.getRepresentadoNombre() != null)
					System.out.println("    representadoNombre: " + tramite.getRepresentadoNombre().getValue());
				if (tramite.getDelegadoNif() != null)
					System.out.println("    delegadoNif: " + tramite.getDelegadoNif().getValue());
				if (tramite.getDelegadoNombre() != null)
					System.out.println("    delegadoNombre: " + tramite.getDelegadoNombre().getValue());
				System.out.println("    idioma: " + tramite.getIdioma());
				if (tramite.getTipoConfirmacionPreregistro() != null)
					System.out.println("    tipoConfirmacionPreregistro: " + tramite.getTipoConfirmacionPreregistro().getValue());
				if (tramite.getHabilitarAvisos() != null)
					System.out.println("    habilitarAvisos: " + tramite.getDelegadoNombre().getValue());
				if (tramite.getAvisoSMS() != null)
					System.out.println("    avisoSMS: " + tramite.getDelegadoNombre().getValue());
				if (tramite.getAvisoEmail() != null)
					System.out.println("    avisoEmail: " + tramite.getDelegadoNombre().getValue());
				if (tramite.getHabilitarNotificacionTelematica() != null)
					System.out.println("    habilitarNotificacionTelematica: " + tramite.getHabilitarNotificacionTelematica().getValue());
				if (tramite.getTramiteSubsanacion() != null)
					System.out.println("    tramiteSubsanacion: " + tramite.getTramiteSubsanacion().getValue());
				if (tramite.getReferenciaGestorDocumentalAsiento() != null)
					System.out.println("    referenciaGestorDocumentalAsiento: " + tramite.getReferenciaGestorDocumentalAsiento().getValue());
				if (tramite.getCodigoDocumentoCustodiaAsiento() != null)
					System.out.println("    codigoDocumentoCustodiaAsiento: " + tramite.getCodigoDocumentoCustodiaAsiento().getValue());
				if (tramite.getReferenciaGestorDocumentalJustificante() != null)
					System.out.println("    referenciaGestorDocumentalJustificante: " + tramite.getReferenciaGestorDocumentalJustificante().getValue());
				if (tramite.getCodigoDocumentoCustodiaJustificante() != null)
					System.out.println("    codigoDocumentoCustodiaJustificante: " + tramite.getCodigoDocumentoCustodiaJustificante().getValue());
				if (tramite.getDocumentos() != null && tramite.getDocumentos().getDocumento() != null) {
					for (DocumentoBTE doc: tramite.getDocumentos().getDocumento()) {
						System.out.println(">>> Document " + doc.getIdentificador() + ", " + doc.getNombre());
						if (doc.getPresentacionTelematica() != null) {
							DatosDocumentoTelematico datosDocumento = doc.getPresentacionTelematica().getValue();
							System.out.println(">>>    nombre: " + datosDocumento.getNombre());
							System.out.println(">>>    extension: " + datosDocumento.getExtension());
							System.out.println(">>>    codigoReferenciaRds: " + datosDocumento.getCodigoReferenciaRds());
							System.out.println(">>>    claveReferenciaRds: " + datosDocumento.getClaveReferenciaRds());
							if (datosDocumento.getReferenciaGestorDocumental() != null && datosDocumento.getReferenciaGestorDocumental().getValue() != null) {
								System.out.println(">>>    referenciaGestorDocumental: " + datosDocumento.getReferenciaGestorDocumental().getValue());
							}
							if (datosDocumento.getContent() != null) {
								System.out.println(">>>    content: " + new String(datosDocumento.getContent()));
							}
						}
					}
				}
			}
		}
		
	}



	private BackofficeFacade getBackofficeFacade() throws MalformedURLException {
		BackofficeFacade client = null;
		URL url = new URL(ENDPOINT_ADDRESS + "?wsdl");
		BackofficeFacadeService service = new BackofficeFacadeService(
				url,
				new QName(
						"urn:es:caib:bantel:ws:v2:services",
						"BackofficeFacadeService"));
		client = service.getBackofficeFacade();
		BindingProvider bp = (BindingProvider)client;
		bp.getRequestContext().put(
				BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
				ENDPOINT_ADDRESS);
		if (USERNAME != null) {
			bp.getRequestContext().put(
					BindingProvider.USERNAME_PROPERTY,
					USERNAME);
			bp.getRequestContext().put(
					BindingProvider.PASSWORD_PROPERTY,
					PASSWORD);
		}
		return client;
	}

}
