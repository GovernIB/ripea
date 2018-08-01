/**
 * 
 */
package es.caib.ripea.core.service.ws;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import es.caib.ripea.core.api.registre.RegistreAnnex;
import es.caib.ripea.core.api.registre.RegistreAnotacio;
import es.caib.ripea.core.api.service.bantel.ws.v2.model.ObjectFactory;
import es.caib.ripea.core.api.service.bantel.ws.v2.model.TramiteBTE;
import es.caib.ripea.core.api.service.bantel.ws.v2.model.TramiteSubsanacion;
import es.caib.ripea.core.helper.XmlHelper;
/**
 * Classe de proves per fer altes a les bústies i fer anotacions de registres amb regles de backoffices de 
 * Ripea o Sistra.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class BustiaWsAnotacioRegistreSistraTest extends BustiaBaseTest {

	public static void main(String[] args) {
		BustiaWsAnotacioRegistreSistraTest test = new BustiaWsAnotacioRegistreSistraTest();
		// Prova d'enviar el contingut a la bústia amb els arxius adjunts de Sistra
		try {
			test.provaEnviamentContingut();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/** Envia el contingut a Ripea amb la informació necessària per a que Ripea el tracti com
	 * una anotació de registre amb une regla de backoffice de Sistra.
	 * @throws Exception
	 */
	@Test
	public void provaEnviamentContingut() throws Exception {
		
		// Anotació de registre
		Date ara = new Date();
		String numeroIdentificador = new SimpleDateFormat("yyMMddHHmm").format(ara);
		RegistreAnotacio anotacio = new RegistreAnotacio();
		anotacio.setNumero(String.valueOf(ara.getTime()) + "_numero");
		anotacio.setData(ara);
		anotacio.setIdentificador(numeroIdentificador + "_identificador");
		anotacio.setExtracte("Extracte " + numeroIdentificador);
		anotacio.setOficinaCodi("0");
//		anotacio.setAssumpteTipusCodi("0");	// Regla Backoffice Ripea
		anotacio.setAssumpteTipusCodi("1"); // Regla Backoffice Sistra
		anotacio.setIdiomaCodi("CA");
		anotacio.setEntitatCodi("?");
		anotacio.setLlibreCodi("?");
		anotacio.setData(new Date());
		anotacio.setDataOrigen(new Date(0));
		anotacio.setOficinaOrigenCodi("ofi_origen");
		anotacio.setOficinaOrigenDescripcio("Oficina origen descripció");
				
		// Afegeix els adjunts
		List<RegistreAnnex> annexos = this.getAnnexos();
		anotacio.setAnnexos(annexos);
		
		// Envia l'anotació de registre.
		getBustiaService().enviarAnotacioRegistreEntrada(
				"A04003003", //usuari admin "A04019281", //LIM
				"A04003003",
				anotacio);
	}

	/** Construeix la llista d'annexos a partir del directori de proves que conté la llista d'arxius
	 * que enviaria Sistra. */
	private List<RegistreAnnex> getAnnexos() {
				
		List<RegistreAnnex> annexos = new ArrayList<RegistreAnnex>();
		RegistreAnnex annex;
		Date ara = new Date();

		try {
			//Llegeix el contingut del director;
			String directoriPath = "es/caib/ripea/core/sistra/";
			InputStream is = getClass().getClassLoader().getResourceAsStream(directoriPath);
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String arxiuNom;
			while ((arxiuNom = br.readLine()) != null) {
				// Afegeix tots els fitxers del directori com annexos
				System.out.println("Afegint annex \"" + arxiuNom + "\"");
				File arxiu = new File(getClass().getClassLoader().getResource(directoriPath + arxiuNom).getFile());
				File arxiuAux = new File(URLDecoder.decode(arxiu.getAbsolutePath(), "UTF-8")); // Problemes amb els espais				
								
				annex = new RegistreAnnex();
				annex.setTitol(arxiuAux.getName());
				annex.setFitxerNom(arxiuAux.getName());
				annex.setFitxerTamany((int) arxiuAux.length());
				annex.setEniDataCaptura(ara);
				annex.setEniOrigen("0");
				annex.setEniTipusDocumental("TD99");
				annex.setSicresTipusDocument("03");
				
				// Llegeix el contingut del fitxer i ho guarda en base 64
				byte[] contingut = Files.readAllBytes(arxiuAux.toPath());
				annex.setFitxerContingut(contingut);

				annexos.add(annex);
			}
		} catch (Exception e) {
			System.err.println("Error adjuntant els annexos a l'anotació: " + e.getLocalizedMessage());
			e.printStackTrace();
		}

		return annexos;
	}
	
	/** Mètode per provar d'obtenir dades dels arxius xml adjunts de Sistra */
	@Test
	public void provaLecturaAdjuntsXml () {
		TramiteBTE tramitBte = new TramiteBTE();
		try{
			DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc;
			ObjectFactory bantelObjectFactory = new ObjectFactory();

			File file;
			// Asiento.xml
			file = new File(getClass().getClassLoader().getResource("es/caib/ripea/core/sistra/Asiento.xml").getFile());
			file = new File(URLDecoder.decode(file.getAbsolutePath(), "UTF-8"));
			doc = dBuilder.parse(file);
			
			// Prova lectura amb ruta amb punts "."
			String ruta = "DATOS_ASUNTO.IDENTIFICADOR_TRAMITE";
			System.out.println("Ruta xml  " + ruta + " = " + XmlHelper.getNodeValue(doc.getDocumentElement(), ruta));

			// Prova lectura amb ruta amb punts "."
			ruta = "DATOS_ANEXO_DOCUMENTACION";
			NodeList documents = XmlHelper.getNodes(doc.getDocumentElement(), ruta);
			System.out.println("Número de documents: " + documents.getLength());

			// Prova lectura navegant pels nodes 
			Node datosAsunto = XmlHelper.getNode(doc.getDocumentElement(), "DATOS_ASUNTO");
			if (datosAsunto != null) {				
				// unidadAdministrativa	: ASIENTO_REGISTRAL.DATOS_ASUNTO.CODIGO_UNIDAD_ADMINISTRATIVA
				tramitBte.setUnidadAdministrativa(Long.parseLong(XmlHelper.getNodeValue(datosAsunto, "CODIGO_UNIDAD_ADMINISTRATIVA")));
				System.out.println("unidadAdministrativa : " + tramitBte.getUnidadAdministrativa());

				// fecha				: ASIENTO_REGISTRAL.DATOS_ASUNTO.FECHA_ASUNTO
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
				GregorianCalendar c = new GregorianCalendar();
				c.setTime(sdf.parse(XmlHelper.getNodeValue(datosAsunto, "FECHA_ASUNTO")));
				XMLGregorianCalendar fechaAsunto = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
				tramitBte.setFecha(fechaAsunto);
				System.out.println("fechaAsunto : " + tramitBte.getFecha());
								
				// identificadorTramite	: ASIENTO_REGISTRAL.DATOS_ASUNTO.IDENTIFICADOR_TRAMITE
				tramitBte.setIdentificadorTramite(XmlHelper.getNodeValue(datosAsunto, "IDENTIFICADOR_TRAMITE"));
				System.out.println("identificadorTramite : " + tramitBte.getIdentificadorTramite());

				// idioma				: ASIENTO_REGISTRAL.DATOS_ASUNTO.IDIOMA_ASUNTO
				tramitBte.setIdioma(XmlHelper.getNodeValue(datosAsunto, "IDIOMA_ASUNTO"));
				System.out.println("idioma : " + tramitBte.getIdioma());
				
				// descripcionTramite				: ASIENTO_REGISTRAL.DATOS_ASUNTO.EXTRACTO_ASUNTO
				tramitBte.setDescripcionTramite(XmlHelper.getNodeValue(datosAsunto, "EXTRACTO_ASUNTO"));
				System.out.println("descripcionTramite : " + tramitBte.getDescripcionTramite());
			}
			
			Node datosInteresado = XmlHelper.getNode(doc.getDocumentElement(), "DATOS_INTERESADO");
			if (datosInteresado != null) {				
				// nivelAutenticacion	: ASIENTO_REGISTRAL.DATOS_INTERESADO.NIVEL_AUTENTICACION
				tramitBte.setNivelAutenticacion(XmlHelper.getNodeValue(datosInteresado, "NIVEL_AUTENTICACION"));
				System.out.println("nivelAutenticacion : " + tramitBte.getNivelAutenticacion());

				// usuarioSeycon		: ASIENTO_REGISTRAL.DATOS_INTERESADO.USUARIO_SEYCON
				JAXBElement<String> usuariSeycon = bantelObjectFactory.createTramiteBTEUsuarioSeycon(XmlHelper.getNodeValue(datosInteresado, "USUARIO_SEYCON"));
				tramitBte.setUsuarioSeycon(usuariSeycon);
				System.out.println("usuarioSeycon : " + tramitBte.getUsuarioSeycon().getValue());

				// usuarioNif	:	ASIENTO_REGISTRAL.DATOS_INTERESADO.NUMERO_IDENTIFICACION
				JAXBElement<String> usuarioNif = bantelObjectFactory.createTramiteBTEUsuarioNif(XmlHelper.getNodeValue(datosInteresado, "NUMERO_IDENTIFICACION"));
				tramitBte.setUsuarioNif(usuarioNif);
				System.out.println("usuarioNif : " + tramitBte.getUsuarioNif().getValue());

				// usuarioNombre:	ASIENTO_REGISTRAL.DATOS_INTERESADO.IDENTIFICACION_INTERESADO
				JAXBElement<String> usuarioNombre = bantelObjectFactory.createTramiteBTEUsuarioNombre(XmlHelper.getNodeValue(datosInteresado, "IDENTIFICACION_INTERESADO"));
				tramitBte.setUsuarioNombre(usuarioNombre);
				System.out.println("usuarioNombre : " + tramitBte.getUsuarioNombre().getValue());

				// representadoNif	:	ASIENTO_REGISTRAL.DATOS_INTERESADO.NUMERO_IDENTIFICACION
				JAXBElement<String> representadoNif = bantelObjectFactory.createTramiteBTERepresentadoNif(XmlHelper.getNodeValue(datosInteresado, "NUMERO_IDENTIFICACION"));
				tramitBte.setRepresentadoNif(representadoNif);
				System.out.println("representadoNif : " + tramitBte.getRepresentadoNif().getValue());

				// representadoNombre:	ASIENTO_REGISTRAL.DATOS_INTERESADO.IDENTIFICACION_INTERESADO
				JAXBElement<String> representadoNombre = bantelObjectFactory.createTramiteBTERepresentadoNombre(XmlHelper.getNodeValue(datosInteresado, "IDENTIFICACION_INTERESADO"));
				tramitBte.setRepresentadoNombre(representadoNombre);
				System.out.println("representadoNombre : " + tramitBte.getRepresentadoNombre().getValue());

				// delegadoNif	:	ASIENTO_REGISTRAL.DATOS_INTERESADO.NUMERO_IDENTIFICACION
				JAXBElement<String> delegadoNif = bantelObjectFactory.createTramiteBTEDelegadoNif(XmlHelper.getNodeValue(datosInteresado, "NUMERO_IDENTIFICACION"));
				tramitBte.setDelegadoNif(delegadoNif);
				System.out.println("delegadoNif : " + tramitBte.getDelegadoNif().getValue());

				// delegadoNombre:	ASIENTO_REGISTRAL.DATOS_INTERESADO.IDENTIFICACION_INTERESADO
				JAXBElement<String> delegadoNombre = bantelObjectFactory.createTramiteBTEDelegadoNombre(XmlHelper.getNodeValue(datosInteresado, "IDENTIFICACION_INTERESADO"));
				tramitBte.setDelegadoNombre(delegadoNombre);
				System.out.println("delegadoNombre : " + tramitBte.getDelegadoNombre().getValue());
			}
			
			// DatosPropios.xml
			file = new File(getClass().getClassLoader().getResource("es/caib/ripea/core/sistra/DatosPropios.xml").getFile());
			file = new File(URLDecoder.decode(file.getAbsolutePath(), "UTF-8"));
			doc = dBuilder.parse(file);

			// Prova lectura amb ruta amb punts "."
			ruta = "INSTRUCCIONES.IDENTIFICADOR_PROCEDIMIENTO";
			System.out.println("Ruta xml  " + ruta + " = " + XmlHelper.getNodeValue(doc.getDocumentElement(), ruta));

			Node instrucciones = XmlHelper.getNode(doc.getDocumentElement(), "INSTRUCCIONES");
			if (instrucciones != null) {
				// habilitarAvisos					:	DATOS_PROPIOS.INSTRUCCIONES.HABILITAR_AVISOS
				JAXBElement<String> habilitarAvisos = bantelObjectFactory.createTramiteBTEHabilitarAvisos(XmlHelper.getNodeValue(instrucciones, "HABILITAR_AVISOS"));
				tramitBte.setHabilitarAvisos(habilitarAvisos);
				System.out.println("habilitarAvisos : " + tramitBte.getHabilitarAvisos().getValue());

				// avisoEmail						:	DATOS_PROPIOS.INSTRUCCIONES.AVISO_EMAIL
				JAXBElement<String> avisoEmail = bantelObjectFactory.createTramiteBTEAvisoEmail(XmlHelper.getNodeValue(instrucciones, "AVISO_EMAIL"));
				tramitBte.setAvisoEmail(avisoEmail);
				System.out.println("avisoEmail : " + tramitBte.getAvisoEmail().getValue());
				
				// avisoSMS						:	DATOS_PROPIOS.INSTRUCCIONES.AVISO_SMS
				JAXBElement<String> avisoSMS = bantelObjectFactory.createTramiteBTEAvisoSMS(XmlHelper.getNodeValue(instrucciones, "AVISO_SMS"));
				tramitBte.setAvisoSMS(avisoSMS);

				// habilitarNotificacionTelematica	:	DATOS_PROPIOS.INSTRUCCIONES.HABILITAR_NOTIFICACION_TELEMATICA
				JAXBElement<String> habilitarNotificacionTelematica = bantelObjectFactory.createTramiteBTEHabilitarNotificacionTelematica(XmlHelper.getNodeValue(instrucciones, "HABILITAR_NOTIFICACION_TELEMATICA"));
				tramitBte.setHabilitarNotificacionTelematica(habilitarNotificacionTelematica);
				System.out.println("habilitarNotificacionTelematica : " + tramitBte.getHabilitarNotificacionTelematica().getValue());

				// tramiteSubsanacion	:	DATOS_PROPIOS.INSTRUCCIONES.TRAMITE_SUBSANACION
				Node tramiteSubsanacionNode = XmlHelper.getNode(instrucciones, "TRAMITE_SUBSANACION");
				if (tramiteSubsanacionNode != null) {
					TramiteSubsanacion tramiteSubsanacion = bantelObjectFactory.createTramiteSubsanacion();
					
					// expedienteCodigo : DATOS_PROPIOS.INSTRUCCIONES.TRAMITE_SUBSANACION.EXPEDIENTE_CODIGO
					tramiteSubsanacion.setExpedienteCodigo(XmlHelper.getNodeValue(tramiteSubsanacionNode, "EXPEDIENTE_CODIGO"));
					
					// expedienteUnidadAdministrativa  : DATOS_PROPIOS.INSTRUCCIONES.TRAMITE_SUBSANACION.EXPEDIENTE_UNIDAD_ADMINISTRATIVA
					String expedienteUnidadAdministrativa = XmlHelper.getNodeValue(tramiteSubsanacionNode, "EXPEDIENTE_UNIDAD_ADMINISTRATIVA");
					if (expedienteUnidadAdministrativa != null)
						tramiteSubsanacion.setExpedienteUnidadAdministrativa(Long.parseLong(expedienteUnidadAdministrativa));
					
					tramitBte.setTramiteSubsanacion(bantelObjectFactory.createTramiteBTETramiteSubsanacion(tramiteSubsanacion));					
					System.out.println("tramiteSubsanacion.expedienteCodigo : " + tramitBte.getTramiteSubsanacion().getValue().getExpedienteCodigo());
					System.out.println("tramiteSubsanacion.expedienteUnidadAdministrativa : " + tramitBte.getTramiteSubsanacion().getValue().getExpedienteUnidadAdministrativa());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
