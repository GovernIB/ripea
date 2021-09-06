/**
 * 
 */
package es.caib.ripea.plugin.caib.ciutada;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBElement;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import es.caib.regtel.ws.v2.model.aviso.Aviso;
import es.caib.regtel.ws.v2.model.datosexpediente.DatosExpediente;
import es.caib.regtel.ws.v2.model.datosinteresado.DatosInteresado;
import es.caib.regtel.ws.v2.model.datosinteresado.IdentificacionInteresadoDesglosada;
import es.caib.regtel.ws.v2.model.datosnotificacion.DatosNotificacion;
import es.caib.regtel.ws.v2.model.datosregistrosalida.DatosRegistroSalida;
import es.caib.regtel.ws.v2.model.datosrepresentado.DatosRepresentado;
import es.caib.regtel.ws.v2.model.detalleacuserecibo.DetalleAcuseRecibo;
import es.caib.regtel.ws.v2.model.documento.Documento;
import es.caib.regtel.ws.v2.model.documento.Documentos;
import es.caib.regtel.ws.v2.model.oficinaregistral.OficinaRegistral;
import es.caib.regtel.ws.v2.model.oficioremision.OficioRemision;
import es.caib.regtel.ws.v2.model.resultadoregistro.ResultadoRegistro;
import es.caib.ripea.plugin.SistemaExternException;
import es.caib.ripea.plugin.ciutada.CiutadaDocument;
import es.caib.ripea.plugin.ciutada.CiutadaExpedientInformacio;
import es.caib.ripea.plugin.ciutada.CiutadaNotificacioEstat;
import es.caib.ripea.plugin.ciutada.CiutadaNotificacioEstat.ZonaperJustificantEstat;
import es.caib.ripea.plugin.ciutada.CiutadaNotificacioResultat;
import es.caib.ripea.plugin.ciutada.CiutadaPersona;
import es.caib.ripea.plugin.ciutada.CiutadaPlugin;
import es.caib.ripea.plugin.PropertiesHelper;
import es.caib.zonaper.ws.v2.model.configuracionavisosexpediente.ConfiguracionAvisosExpediente;
import es.caib.zonaper.ws.v2.model.documentoexpediente.DocumentoExpediente;
import es.caib.zonaper.ws.v2.model.documentoexpediente.DocumentosExpediente;
import es.caib.zonaper.ws.v2.model.eventoexpediente.EventoExpediente;
import es.caib.zonaper.ws.v2.model.expediente.Expediente;

/**
 * Implementació de del plugin de comunicació amb el ciutadà
 * emprant SISTRA.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class CiutadaPluginSistra implements CiutadaPlugin {

	private static final String NTI_EXPID_PREFIX = "ES_"; 
	
	@Override
	public CiutadaExpedientInformacio expedientCrear(
			String expedientIdentificador,
			String unitatAdministrativa,
			String identificadorProcedimiento,
			String idioma,
			String descripcio,
			CiutadaPersona destinatari,
			CiutadaPersona representat,
			String bantelNumeroEntrada,
			boolean avisosHabilitats,
			String avisosEmail,
			String avisosMobil) throws SistemaExternException {
		comprovarZonaPersonalCreada(destinatari);
		String clauSistra = "<buit>";
		String identificadorSistra = "<buit>";
		try {
			clauSistra = getExpedientClau(
					expedientIdentificador,
					unitatAdministrativa);
			identificadorSistra = getExpedientIdentificadorPerSistra(expedientIdentificador);
			Expediente expediente = new Expediente();
			expediente.setIdentificadorExpediente(identificadorSistra);
			expediente.setUnidadAdministrativa(new Long(unitatAdministrativa).longValue());
			expediente.setClaveExpediente(clauSistra);
			expediente.setIdioma(idioma);
			expediente.setDescripcion(descripcio);
			expediente.setAutenticado(true);
			expediente.setIdentificadorProcedimiento(
					newJAXBElement(
							"identificadorProcedimiento",
							identificadorProcedimiento,
							String.class));
			expediente.setNifRepresentante(
					newJAXBElement(
							"nifRepresentante",
							destinatari.getNif(),
							String.class));

			if (representat != null) {
				expediente.setNifRepresentado(
						newJAXBElement(
								"nifRepresentado",
								representat.getNif(),
								String.class));
				expediente.setNombreRepresentado(
						newJAXBElement(
								"nombreRepresentado",
								representat.getNom(),
								String.class));
			}
			expediente.setNumeroEntradaBTE(
					newJAXBElement(
							"numeroEntradaBTE",
							bantelNumeroEntrada,
							String.class));
			ConfiguracionAvisosExpediente configuracionAvisos = new ConfiguracionAvisosExpediente();
			configuracionAvisos.setHabilitarAvisos(
					newJAXBElement(
							"habilitarAvisos",
							new Boolean(avisosHabilitats),
							Boolean.class));
			configuracionAvisos.setAvisoEmail(
					newJAXBElement(
							"avisoEmail",
							avisosEmail,
							String.class));
			configuracionAvisos.setAvisoSMS(
					newJAXBElement(
							"avisoSMS",
							avisosMobil,
							String.class));
			expediente.setConfiguracionAvisos(
					newJAXBElement(
							"configuracionAvisos",
							configuracionAvisos,
							ConfiguracionAvisosExpediente.class));
			getZonaperWs().altaExpediente(expediente);
			return new CiutadaExpedientInformacio(
					identificadorSistra,
					clauSistra);
		} catch (Exception ex) {
			throw new SistemaExternException(
					"No s'ha pogut crear l'expedient a la zona personal (" +
					"identificadorSistra=" + identificadorSistra + ", " +
					"clauSistra=" + clauSistra + ", " +
					"unitatAdministrativa=" + unitatAdministrativa + ", " +
					"descripcio=" + descripcio + ", " +
					"destinatariNif=" + destinatari.getNif() + ")",
					ex);
		}
	}

	@Override
	public void avisCrear(
			String expedientIdentificador,
			String unitatAdministrativa,
			String titol,
			String text,
			String textSms,
			List<CiutadaDocument> annexos) throws SistemaExternException {
		String clauSistra = "<buit>";
		String identificadorSistra = "<buit>";
		try {
			clauSistra = getExpedientClau(
					expedientIdentificador,
					unitatAdministrativa);
			identificadorSistra = getExpedientIdentificadorPerSistra(expedientIdentificador);
			EventoExpediente evento = new EventoExpediente();
			evento.setTitulo(titol);
			evento.setTexto(text);
			evento.setTextoSMS(
					newJAXBElement(
							"textoSMS",
							textSms,
							String.class));
			evento.setFecha(
					newJAXBElement(
							"fecha",
							new SimpleDateFormat("dd/MM/yyyy").format(new Date()),
							String.class));
			if (annexos != null && !annexos.isEmpty()) {
				DocumentosExpediente documentos = new DocumentosExpediente();
				for (CiutadaDocument annex: annexos) {
					DocumentoExpediente documento = new DocumentoExpediente();
					documento.setTitulo(
							newJAXBElement(
									"titulo",
									annex.getTitol(),
									String.class));
					documento.setNombre(
							newJAXBElement(
									"nombre",
									annex.getArxiuNom(),
									String.class));
					documento.setContenidoFichero(
							newJAXBElement(
									"contenidoFichero",
									annex.getArxiuContingut(),
									byte[].class));
					documento.setEstructurado(
							newJAXBElement(
									"estructurado",
									new Boolean(false),
									Boolean.class));
					documentos.getDocumento().add(documento);
				}
				evento.setDocumentos(
						newJAXBElement(
								"documentos",
								documentos,
								DocumentosExpediente.class));
			}
			getZonaperWs().altaEventoExpediente(
					new Long(unitatAdministrativa).longValue(),
					identificadorSistra,
					clauSistra,
					evento);
		} catch (Exception ex) {
			throw new SistemaExternException(
					"No s'ha pogut crear l'event a la zona personal (" +
					"identificadorSistra=" + identificadorSistra + ", " +
					"clauSistra=" + clauSistra + ", " +
					"unitatAdministrativa=" + unitatAdministrativa + ", " +
					"titol=" + titol + ")",
					ex);
		}
	}

	@Override
	public CiutadaNotificacioResultat notificacioCrear(
			String expedientIdentificador,
			String unitatAdministrativa,
			String registreOficinaCodi,
			String registreOficinaOrganCodi,
			CiutadaPersona destinatari,
			CiutadaPersona representat,
			String idioma,
			String oficiTitol,
			String oficiText,
			String avisTitol,
			String avisText,
			String avisTextSms,
			boolean confirmarRecepcio,
			List<CiutadaDocument> annexos) throws SistemaExternException {
		String clauSistra = "<buit>";
		String identificadorSistra = "<buit>";
		try {
			clauSistra = getExpedientClau(
					expedientIdentificador,
					unitatAdministrativa);
			identificadorSistra = getExpedientIdentificadorPerSistra(expedientIdentificador);
			DatosRegistroSalida notificacion = new DatosRegistroSalida();
			DatosExpediente datosExpediente = new DatosExpediente();
			datosExpediente.setIdentificadorExpediente(identificadorSistra);
			datosExpediente.setUnidadAdministrativa(new Long(unitatAdministrativa).longValue());
			datosExpediente.setClaveExpediente(clauSistra);
			notificacion.setDatosExpediente(datosExpediente);
			OficinaRegistral oficinaRegistral = new OficinaRegistral();
			oficinaRegistral.setCodigoOficina(registreOficinaCodi);
			oficinaRegistral.setCodigoOrgano(registreOficinaOrganCodi);
			notificacion.setOficinaRegistral(oficinaRegistral);
			DatosInteresado datosInteresado = new DatosInteresado();
			datosInteresado.setNif(destinatari.getNif());
			IdentificacionInteresadoDesglosada idInteresado = new IdentificacionInteresadoDesglosada();
			idInteresado.setNombre(destinatari.getNom());
			idInteresado.setApellido1(destinatari.getLlinatge1());
			idInteresado.setApellido2(destinatari.getLlinatge2());
			datosInteresado.setNombreApellidosDesglosado(idInteresado);
			datosInteresado.setCodigoPais(
					newJAXBElement(
							"codigoPais",
							destinatari.getPaisCodi(),
							String.class));
			datosInteresado.setNombrePais(
					newJAXBElement(
							"nombrePais",
							destinatari.getPaisNom(),
							String.class));
			datosInteresado.setCodigoProvincia(
					newJAXBElement(
							"codigoProvincia",
							destinatari.getProvinciaCodi(),
							String.class));
			datosInteresado.setNombreProvincia(
					newJAXBElement(
							"nombreProvincia",
							destinatari.getProvinciaNom(),
							String.class));
			datosInteresado.setCodigoLocalidad(
					newJAXBElement(
							"codigoLocalidad",
							destinatari.getMunicipiCodi(),
							String.class));
			datosInteresado.setNombreLocalidad(
					newJAXBElement(
							"nombreLocalidad",
							destinatari.getMunicipiNom(),
							String.class));
			notificacion.setDatosInteresado(datosInteresado);
			DatosNotificacion datosNotificacion = new DatosNotificacion();
			String assumpteTipus = getAssumpteTipus();
			datosNotificacion.setTipoAsunto(
					(assumpteTipus != null) ? assumpteTipus : "OT");
			datosNotificacion.setIdioma(idioma);
			OficioRemision oficioRemision = new OficioRemision();
			oficioRemision.setTitulo(oficiTitol);
			oficioRemision.setTexto(oficiText);
			datosNotificacion.setOficioRemision(oficioRemision);
			if (avisTitol != null) {
				Aviso aviso = new Aviso();
				aviso.setTitulo(avisTitol);
				aviso.setTexto(avisText);
				aviso.setTextoSMS(
						newJAXBElement(
								"textoSMS",
								avisTextSms,
								String.class));
				datosNotificacion.setAviso(aviso);
			}
			datosNotificacion.setAcuseRecibo(confirmarRecepcio);
			notificacion.setDatosNotificacion(datosNotificacion);
			if (representat != null) {
				DatosRepresentado datosRepresentado = new DatosRepresentado();
				datosRepresentado.setNif(representat.getNif());
				datosRepresentado.setNombreApellidos(representat.getLlinatgesComaNom());
				notificacion.setDatosRepresentado(datosRepresentado);
			}
			if (annexos != null && !annexos.isEmpty()) {
				Documentos documentos = new Documentos();
				for (CiutadaDocument annex: annexos) {
					Documento documento = new Documento();
					documento.setDatosFichero(
							newJAXBElement(
									"datosFichero",
									annex.getArxiuContingut(),
									byte[].class));
					documento.setNombre(
							newJAXBElement(
									"nombre",
									annex.getArxiuNomSenseExtensio(),
									String.class));
					documento.setExtension(
							newJAXBElement(
									"extension",
									annex.getArxiuExtensio(),
									String.class));
					documentos.getDocumentos().add(documento);
				}
				notificacion.setDocumentos(
						newJAXBElement(
								"documentos",
								documentos,
								Documentos.class));
			}
			ResultadoRegistro resultado = getRegtelWs().registroSalida(notificacion);
			CiutadaNotificacioResultat resultat = new CiutadaNotificacioResultat();
			resultat.setRegistreData(
					resultado.getFechaRegistro().toGregorianCalendar().getTime());
			resultat.setRegistreNumero(
					resultado.getNumeroRegistro());
			return resultat;
		} catch (Exception ex) {
			throw new SistemaExternException(
					"No s'ha pogut crear la notificació (" +
							"identificadorSistra=" + identificadorSistra + ", " +
							"clauSistra=" + clauSistra + ", " +
							"unitatAdministrativa=" + unitatAdministrativa + ", " +
							"oficiTitol=" + oficiTitol + ", " +
							"destinatariNif=" + destinatari.getNif() + ")",
					ex);
		}
	}

	public CiutadaNotificacioEstat notificacioObtenirJustificantRecepcio(
			String registreNumero) throws SistemaExternException {
		try {
			DetalleAcuseRecibo acuseRecibo = getRegtelWs().obtenerDetalleAcuseRecibo(registreNumero);
			CiutadaNotificacioEstat notificacioEstat = new CiutadaNotificacioEstat();
			if (acuseRecibo.getFechaAcuseRecibo() != null) {
				XMLGregorianCalendar cal = acuseRecibo.getFechaAcuseRecibo().getValue();
				notificacioEstat.setData(cal.toGregorianCalendar().getTime());
			}
			if (acuseRecibo.getEstado() != null) {
				switch (acuseRecibo.getEstado()) {
				case ENTREGADA:
					notificacioEstat.setEstat(ZonaperJustificantEstat.ENTREGADA);
					break;
				case PENDIENTE:
					notificacioEstat.setEstat(ZonaperJustificantEstat.PENDENT);
					break;
				case RECHAZADA:
					notificacioEstat.setEstat(ZonaperJustificantEstat.REBUTJADA);
					break;
				default:
					notificacioEstat.setEstat(ZonaperJustificantEstat.PENDENT);
					break;
				}
			}
			return notificacioEstat;
		} catch (Exception ex) {
			throw new SistemaExternException(
					"No s'ha pogut obtenir el justificant de recepció (" +
							"registreNumero=" + registreNumero + ")",
					ex);
		}
	}



	private void comprovarZonaPersonalCreada(
			CiutadaPersona persona) throws SistemaExternException {
		boolean existeix;
		try {
			existeix = getZonaperWs().existeZonaPersonalUsuario(
					persona.getNif());
		} catch (Exception ex) {
			throw new SistemaExternException(
					"No s'ha pogut verificar l'existència de la zona personal (" +
					"nif=" + persona.getNif() + ")",
					ex);
		}
		if (!existeix) {
			try {
				getZonaperWs().altaZonaPersonalUsuario(
						persona.getNif(),
						persona.getNom(),
						persona.getLlinatge1(),
						persona.getLlinatge2());
			} catch (Exception ex) {
				throw new SistemaExternException(
						"No s'ha pogut donar d'alta la zona personal (" +
						"nif=" + persona.getNif() + ", " +
						"nom=" + persona.getNom() + ", " +
						"llinatge1=" + persona.getLlinatge1() + ", " +
						"llinatge2=" + persona.getLlinatge2() + ")",
						ex);
			}
		}
	}

	private es.caib.zonaper.ws.v2.services.BackofficeFacade getZonaperWs() throws MalformedURLException {
		String webServiceUrl = getBaseUrl() + "/zonaperws/services/v2/BackofficeFacade";
		URL wsdlUrl = new URL(webServiceUrl + "?wsdl");
		es.caib.zonaper.ws.v2.services.BackofficeFacadeService service = new es.caib.zonaper.ws.v2.services.BackofficeFacadeService(
				wsdlUrl,
				new QName(
						"urn:es:caib:zonaper:ws:v2:services",
						"BackofficeFacadeService"));
		es.caib.zonaper.ws.v2.services.BackofficeFacade backofficeFacade = service.getBackofficeFacade();
		BindingProvider bp = (BindingProvider)backofficeFacade;
		Map<String, Object> reqContext = bp.getRequestContext();
		reqContext.put(
				BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
				webServiceUrl);
		reqContext.put(
				BindingProvider.USERNAME_PROPERTY,
				getUsername());
		reqContext.put(
				BindingProvider.PASSWORD_PROPERTY,
				getPassword());
		if (isLogMissatgesActiu()) {
			@SuppressWarnings("rawtypes")
			List<Handler> handlerChain = new ArrayList<Handler>();
			handlerChain.add(new LogMessageHandler());
			bp.getBinding().setHandlerChain(handlerChain);
		}
		return backofficeFacade;
	}

	private es.caib.regtel.ws.v2.services.BackofficeFacade getRegtelWs() throws MalformedURLException {
		String webServiceUrl = getBaseUrl() + "/regtelws/services/v2/BackofficeFacade";
		URL wsdlUrl = new URL(webServiceUrl + "?wsdl");
		es.caib.regtel.ws.v2.services.BackofficeFacadeService service = new es.caib.regtel.ws.v2.services.BackofficeFacadeService(
				wsdlUrl,
				new QName(
						"urn:es:caib:regtel:ws:v2:services",
						"BackofficeFacadeService"));
		es.caib.regtel.ws.v2.services.BackofficeFacade backofficeFacade = service.getBackofficeFacade();
		BindingProvider bp = (BindingProvider)backofficeFacade;
		Map<String, Object> reqContext = bp.getRequestContext();
		reqContext.put(
				BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
				webServiceUrl);
		reqContext.put(
				BindingProvider.USERNAME_PROPERTY,
				getUsername());
		reqContext.put(
				BindingProvider.PASSWORD_PROPERTY,
				getPassword());
		if (isLogMissatgesActiu()) {
			@SuppressWarnings("rawtypes")
			List<Handler> handlerChain = new ArrayList<Handler>();
			handlerChain.add(new LogMessageHandler());
			bp.getBinding().setHandlerChain(handlerChain);
		}
		return backofficeFacade;
	}

	private <T> JAXBElement<T> newJAXBElement(
			String qname,
			T valor,
			Class<T> tipus) {
		return new JAXBElement<T>(
				new QName(qname),
				tipus,
				valor);
	}

	private String getExpedientClau(
			String expedientIdentificador,
			String unitatAdministrativa) throws NoSuchAlgorithmException {
		String missatge = expedientIdentificador + "/" + unitatAdministrativa;
		MessageDigest md = MessageDigest.getInstance("MD5");
		byte[] digest = md.digest(missatge.getBytes());
		StringBuilder hexString = new StringBuilder();
	    for (int i = 0; i < digest.length; i++) {
	        String hex = Integer.toHexString(0xFF & digest[i]);
	        if (hex.length() == 1) {
	            hexString.append('0');
	        }
	        hexString.append(hex);
	    }
	    return hexString.toString().toUpperCase();
	}

	private String getExpedientIdentificadorPerSistra(
			String expedientIdentificador) {
		if (expedientIdentificador.length() > 50 && expedientIdentificador.startsWith(NTI_EXPID_PREFIX)) {
			return expedientIdentificador.substring(NTI_EXPID_PREFIX.length() + 19);
			//return expedientIdentificador.substring(NTI_EXPID_PREFIX.length());
		} else {
			return expedientIdentificador;
		}
	}

	private String getBaseUrl() {
		return PropertiesHelper.getProperties().getProperty(
				"es.caib.ripea.plugin.ciutada.sistra.base.url");
	}
	private String getUsername() {
		return PropertiesHelper.getProperties().getProperty(
				"es.caib.ripea.plugin.ciutada.sistra.username");
	}
	private String getPassword() {
		return PropertiesHelper.getProperties().getProperty(
				"es.caib.ripea.plugin.ciutada.sistra.password");
	}
	private boolean isLogMissatgesActiu() {
		return PropertiesHelper.getProperties().getAsBoolean(
				"es.caib.ripea.plugin.ciutada.sistra.log.actiu");
	}
	private String getAssumpteTipus() {
		return PropertiesHelper.getProperties().getProperty(
				"es.caib.ripea.plugin.ciutada.sistra.assumpte.tipus");
	}

	private class LogMessageHandler implements SOAPHandler<SOAPMessageContext> {
		public boolean handleMessage(SOAPMessageContext messageContext) {
			log(messageContext);
			return true;
		}
		public Set<QName> getHeaders() {
			return Collections.emptySet();
		}
		public boolean handleFault(SOAPMessageContext messageContext) {
			log(messageContext);
			return true;
		}
		public void close(MessageContext context) {
		}
		private void log(SOAPMessageContext messageContext) {
			SOAPMessage msg = messageContext.getMessage();
			try {
				Boolean outboundProperty = (Boolean)messageContext.get(
						MessageContext.MESSAGE_OUTBOUND_PROPERTY);
				if (outboundProperty)
					System.out.print("Missatge SOAP petició: ");
				else
					System.out.print("Missatge SOAP resposta: ");
				msg.writeTo(System.out);
				System.out.println();
			} catch (SOAPException ex) {
				Logger.getLogger(LogMessageHandler.class.getName()).log(
						Level.SEVERE,
						null,
						ex);
			} catch (IOException ex) {
				Logger.getLogger(LogMessageHandler.class.getName()).log(
						Level.SEVERE,
						null,
						ex);
			}
		}
	}

}
