/**
 * 
 */
package es.caib.ripea.plugin.caib.notificacio;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.filter.LoggingFilter;

import es.caib.notib.client.NotificacioRestClientFactory;
import es.caib.notib.client.NotificacioRestClientV2;
import es.caib.notib.client.domini.Certificacio;
import es.caib.notib.client.domini.DadesConsulta;
import es.caib.notib.client.domini.DocumentV2;
import es.caib.notib.client.domini.EntregaDeh;
import es.caib.notib.client.domini.EntregaPostal;
import es.caib.notib.client.domini.EntregaPostalViaTipusEnum;
import es.caib.notib.client.domini.EnviamentReferenciaV2;
import es.caib.notib.client.domini.EnviamentTipusEnum;
import es.caib.notib.client.domini.InteressatTipusEnumDto;
import es.caib.notib.client.domini.NotificaDomiciliConcretTipusEnumDto;
import es.caib.notib.client.domini.NotificaServeiTipusEnumDto;
import es.caib.notib.client.domini.NotificacioCanviClient;
import es.caib.notib.client.domini.NotificacioV2;
import es.caib.notib.client.domini.RespostaAltaV2;
import es.caib.notib.client.domini.RespostaConsultaDadesRegistreV2;
import es.caib.notib.client.domini.RespostaConsultaEstatEnviamentV2;
import es.caib.notib.client.domini.RespostaConsultaEstatNotificacioV2;
import es.caib.notib.client.domini.RespostaConsultaJustificantEnviament;
import es.caib.ripea.plugin.NotibRepostaException;
import es.caib.ripea.plugin.RipeaAbstractPluginProperties;
import es.caib.ripea.plugin.SistemaExternException;
import es.caib.ripea.plugin.notificacio.Enviament;
import es.caib.ripea.plugin.notificacio.EnviamentEstat;
import es.caib.ripea.plugin.notificacio.EnviamentReferencia;
import es.caib.ripea.plugin.notificacio.Notificacio;
import es.caib.ripea.plugin.notificacio.NotificacioEstat;
import es.caib.ripea.plugin.notificacio.NotificacioPlugin;
import es.caib.ripea.plugin.notificacio.Persona;
import es.caib.ripea.plugin.notificacio.RespostaConsultaEstatEnviament;
import es.caib.ripea.plugin.notificacio.RespostaConsultaEstatNotificacio;
import es.caib.ripea.plugin.notificacio.RespostaConsultaInfoRegistre;
import es.caib.ripea.plugin.notificacio.RespostaEnviar;
import es.caib.ripea.plugin.notificacio.RespostaJustificantEnviamentNotib;

/**
 * Implementació de del plugin d'enviament de notificacions
 * emprant NOTIB.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class NotificacioPluginNotib extends RipeaAbstractPluginProperties implements NotificacioPlugin {

	private boolean test = false; //test = true; //if true then automatic callback with testIdentificador and testReferncia
	private String testIdentificador = "047a9033-de0e-452e-aada-a51825d0a886";
	private String testReferencia = "4a1d3b1c-1e7b-4883-8968-54f770a39ee6";
	
	private NotificacioRestClientV2 clientV2;

	public NotificacioPluginNotib() {
		super();
	}
	public NotificacioPluginNotib(String propertyKeyBase, Properties properties) {
		super(propertyKeyBase, properties);
	}
	
	@Override
	public RespostaEnviar enviar(
			Notificacio notificacio) throws SistemaExternException {
		try {
			NotificacioV2 notificacioNotib = new NotificacioV2();

			
			notificacioNotib.setEmisorDir3Codi(notificacio.getEmisorDir3Codi());
			notificacioNotib.setEnviamentTipus(notificacio.getEnviamentTipus() != null ? EnviamentTipusEnum.valueOf(notificacio.getEnviamentTipus().toString()) : null);
			notificacioNotib.setConcepte(notificacio.getConcepte());
			notificacioNotib.setDescripcio(notificacio.getDescripcio());
			notificacioNotib.setEnviamentDataProgramada(notificacio.getEnviamentDataProgramada());
			notificacioNotib.setRetard(notificacio.getRetard());
			notificacioNotib.setCaducitat(notificacio.getCaducitat());
			notificacioNotib.setOrganGestor(notificacio.getOrganGestor());
			DocumentV2 document = new DocumentV2();
			document.setArxiuNom(notificacio.getDocumentArxiuNom());
			if (notificacio.getDocumentArxiuContingut() != null) {
				document.setContingutBase64(new String(Base64.encodeBase64(notificacio.getDocumentArxiuContingut())));
			} else {
				document.setUuid(notificacio.getDocumentArxiuUuid());
			}
			notificacioNotib.setDocument(document);
			notificacioNotib.setProcedimentCodi(notificacio.getProcedimentCodi());
			notificacioNotib.setUsuariCodi(notificacio.getUsuariCodi());
			notificacioNotib.setNumExpedient(notificacio.getNumExpedient());
			if (notificacio.getEnviaments() != null) {
				for (Enviament enviament : notificacio.getEnviaments()) {
					es.caib.notib.client.domini.Enviament enviamentNotib = new es.caib.notib.client.domini.Enviament();
					enviamentNotib.setTitular(
							toPersonaNotib(enviament.getTitular()));
					if (enviament.getDestinataris() != null) {
						for (Persona destinatari: enviament.getDestinataris()) {
							enviamentNotib.getDestinataris().add(
									toPersonaNotib(destinatari));
						}
					}
					if (enviament.isEntregaPostalActiva()) {
						EntregaPostal entregaPostal = new EntregaPostal();
						entregaPostal.setTipus(NotificaDomiciliConcretTipusEnumDto.valueOf(enviament.getEntregaPostalTipus().toString()));
						entregaPostal.setViaTipus(enviament.getEntregaPostalViaTipus() != null
								? EntregaPostalViaTipusEnum.valueOf(enviament.getEntregaPostalViaTipus().toString()) : null);
						entregaPostal.setViaNom(enviament.getEntregaPostalViaNom());
						entregaPostal.setNumeroCasa(enviament.getEntregaPostalNumeroCasa());
						entregaPostal.setNumeroQualificador(enviament.getEntregaPostalNumeroQualificador());
						entregaPostal.setPuntKm(enviament.getEntregaPostalPuntKm());
						entregaPostal.setApartatCorreus(enviament.getEntregaPostalApartatCorreus());
						entregaPostal.setPortal(enviament.getEntregaPostalPortal());
						entregaPostal.setEscala(enviament.getEntregaPostalEscala());
						entregaPostal.setPlanta(enviament.getEntregaPostalPlanta());
						entregaPostal.setPorta(enviament.getEntregaPostalPorta());
						entregaPostal.setBloc(enviament.getEntregaPostalBloc());
						entregaPostal.setComplement(enviament.getEntregaPostalComplement());
						entregaPostal.setCodiPostal(enviament.getEntregaPostalCodiPostal());
						entregaPostal.setPoblacio(enviament.getEntregaPostalPoblacio());
						entregaPostal.setMunicipiCodi(enviament.getEntregaPostalMunicipiCodi());
						entregaPostal.setProvincia(enviament.getEntregaPostalProvinciaCodi());
						entregaPostal.setPaisCodi(enviament.getEntregaPostalPaisCodi());
						entregaPostal.setLinea1(enviament.getEntregaPostalLinea1());
						entregaPostal.setLinea2(enviament.getEntregaPostalLinea2());
						entregaPostal.setCie(enviament.getEntregaPostalCie());
						entregaPostal.setFormatSobre(enviament.getEntregaPostalFormatSobre());
						entregaPostal.setFormatFulla(enviament.getEntregaPostalFormatFulla());
						enviamentNotib.setEntregaPostal(entregaPostal);
						enviamentNotib.setEntregaPostalActiva(true);
					}
					if (enviament.isEntregaDehActiva()) {
						EntregaDeh entregaDeh = new EntregaDeh();
						entregaDeh.setObligat(enviament.getEntregaDehObligat());
						entregaDeh.setProcedimentCodi(enviament.getEntregaDehProcedimentCodi());
						entregaDeh.setEmisorNif(enviament.getEntregaNif());
						enviamentNotib.setEntregaDeh(entregaDeh);
						enviamentNotib.setEntregaDehActiva(true);
					} else {
						enviamentNotib.setEntregaDehActiva(false);
					}
					
					enviamentNotib.setServeiTipus(NotificaServeiTipusEnumDto.valueOf(notificacio.getServeiTipusEnum().toString()));
					notificacioNotib.getEnviaments().add(enviamentNotib);
				}
			}

			ObjectMapper mapper  = new ObjectMapper();
			String body = mapper.writeValueAsString(notificacioNotib);
			System.out.println(body);

			//####### ALTA NOTIFICACIO ####################
			RespostaAltaV2 respostaAlta = getNotificacioRestClient().alta(notificacioNotib);
			
			String referenciesString ="";
			if(respostaAlta.getReferencies()!=null){
				for (EnviamentReferenciaV2 enviamentReferencia : respostaAlta.getReferencies()) {
					referenciesString += "[referencia="+enviamentReferencia.getReferencia()+ ",titular="+enviamentReferencia.getTitularNif()+"]";
				}
			}
			
			logger.debug("Es va enviar una notificació [concepte=" + notificacioNotib.getConcepte() + "] RespostaAlta: " + 
					 	 "error="+respostaAlta.isError() +
					 	 ",errorDescripcio="+respostaAlta.getErrorDescripcio() +
						 ",estat="+respostaAlta.getEstat() +
						 ",identificador="+respostaAlta.getIdentificador() +
						 ",referencies="+referenciesString);

			if (respostaAlta.isError() && (respostaAlta.getReferencies() == null || respostaAlta.getReferencies().isEmpty())) {
				throw new NotibRepostaException(respostaAlta.getErrorDescripcio());
				
			} else {
				RespostaEnviar resposta = new RespostaEnviar();
				resposta.setEstat(respostaAlta.getEstat() != null ? NotificacioEstat.valueOf(respostaAlta.getEstat().toString()) : null);
				resposta.setIdentificador(respostaAlta.getIdentificador());
				if (respostaAlta.getReferencies() != null) {
					List<EnviamentReferencia> referencies = new ArrayList<EnviamentReferencia>();
					for (EnviamentReferenciaV2 ref: respostaAlta.getReferencies()) {
						EnviamentReferencia referencia = new EnviamentReferencia();
						referencia.setTitularNif(ref.getTitularNif());
						referencia.setReferencia(ref.getReferencia());
						referencies.add(referencia);
					}
					resposta.setReferencies(referencies);
				}
				resposta.setError(respostaAlta.isError());
				resposta.setErrorDescripcio(respostaAlta.getErrorDescripcio());
				
				
				if (test) {
					final String identitifcador = respostaAlta.getIdentificador();
					final String referencia = respostaAlta.getReferencies().get(0).getReferencia();
					Thread t = new Thread(new Runnable() {
						public void run() {
							try {
								TimeUnit.SECONDS.sleep(5);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							testCallbackNotificaCanvi(
									identitifcador,
									referencia);
						}
					});
					t.start();
				}
				
				return resposta;
			}
			
		} catch (Exception ex) {
			logger.error(
					"No s'ha pogut enviar la notificació (" +
					"emisorDir3Codi=" + notificacio.getEmisorDir3Codi() + ", " +
					"enviamentTipus=" + notificacio.getEnviamentTipus() + ", " +
					"concepte=" + notificacio.getConcepte() + ")",
					ex);
			throw new SistemaExternException(
					"No s'ha pogut enviar la notificació (" +
					"emisorDir3Codi=" + notificacio.getEmisorDir3Codi() + ", " +
					"enviamentTipus=" + notificacio.getEnviamentTipus() + ", " +
					"concepte=" + notificacio.getConcepte() + ")",
					ex);
		}
	}

	@Override
	public RespostaConsultaEstatNotificacio consultarNotificacio(
			String identificador) throws SistemaExternException {
		try {
			
			if (test) {
				identificador = testIdentificador;
			}
			
			RespostaConsultaEstatNotificacioV2 respostaConsultaEstat = getNotificacioRestClient().consultaEstatNotificacio(identificador);

			RespostaConsultaEstatNotificacio resposta = new RespostaConsultaEstatNotificacio();
			resposta.setEstat(respostaConsultaEstat.getEstat() != null ? NotificacioEstat.valueOf(respostaConsultaEstat.getEstat().toString()) : null);
			resposta.setError(respostaConsultaEstat.isError());
			resposta.setErrorDescripcio(respostaConsultaEstat.getErrorDescripcio());
			resposta.setErrorData(respostaConsultaEstat.getErrorData());
			resposta.setDataEnviada(respostaConsultaEstat.getDataEnviada());
			resposta.setDataFinalitzada(respostaConsultaEstat.getDataFinalitzada());
			return resposta;

		} catch (Exception ex) {
			throw new SistemaExternException(
					"No s'ha pogut consultar l'estat de la notificació (" +
					"identificador=" + identificador + ")",
					ex);
		}
	}

	@Override
	public RespostaConsultaEstatEnviament consultarEnviament(
			String referencia) throws SistemaExternException {
		try {
			
			if (test) {
				referencia = testReferencia;
			}
			
			RespostaConsultaEstatEnviamentV2 respostaConsultaEstat = getNotificacioRestClient().consultaEstatEnviament(referencia);

			RespostaConsultaEstatEnviament resposta = new RespostaConsultaEstatEnviament();
			
			resposta.setEstat(respostaConsultaEstat.getEstat() != null ? EnviamentEstat.valueOf(respostaConsultaEstat.getEstat().toString()) : null);
			resposta.setEstatData(respostaConsultaEstat.getEstatData());
			resposta.setEstatDescripcio(respostaConsultaEstat.getEstatDescripcio());
			resposta.setEstatOrigen(respostaConsultaEstat.getDatat().getOrigen());
			resposta.setReceptorNif(respostaConsultaEstat.getDatat().getReceptorNif());
			resposta.setReceptorNom(respostaConsultaEstat.getDatat().getReceptorNom());
			if (respostaConsultaEstat.getCertificacio() != null) {
				Certificacio certificacio = respostaConsultaEstat.getCertificacio();
				resposta.setCertificacioData(certificacio.getData());
				resposta.setCertificacioOrigen(certificacio.getOrigen());
				resposta.setCertificacioContingut(
						Base64.decodeBase64(certificacio.getContingutBase64().getBytes()));
				resposta.setCertificacioHash(certificacio.getHash());
				resposta.setCertificacioMetadades(certificacio.getMetadades());
				resposta.setCertificacioCsv(certificacio.getCsv());
				resposta.setCertificacioTipusMime(certificacio.getTipusMime());
			}
			resposta.setError(respostaConsultaEstat.isError());
			resposta.setErrorDescripcio(respostaConsultaEstat.getErrorDescripcio());
			
			return resposta;
			
		} catch (Exception ex) {
			throw new SistemaExternException(
					"No s'ha pogut consultar l'estat de l'enviament (" +
					"referencia=" + referencia + ")",
					ex);
		}
	}
	
	
	



	public void testCallbackNotificaCanvi(String identificador, String referenciaEnviament) {
		
		final String NOTIFICACIO_SERVICE_PATH = "/notificaCanvi";
		NotificacioCanviClient notificacio;
		String baseUrl;
		baseUrl = "http://localhost:8080/ripea/rest/notib";
		notificacio = new NotificacioCanviClient(
				identificador, 
				referenciaEnviament);
		try {
			String urlAmbMetode = baseUrl + NOTIFICACIO_SERVICE_PATH;
			ObjectMapper mapper  = new ObjectMapper();
			String body = mapper.writeValueAsString(notificacio);
			Client jerseyClient = Client.create();
			jerseyClient.addFilter(new LoggingFilter(System.out));
			ClientResponse response = jerseyClient.
					resource(urlAmbMetode).
					type("application/json").
					post(ClientResponse.class, body);
				
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}
	
	

	@Override
	public RespostaConsultaInfoRegistre consultarRegistreInfo(
			String identificador,
			String referencia,
			boolean ambJustificant) throws SistemaExternException {
		RespostaConsultaInfoRegistre resposta = new RespostaConsultaInfoRegistre();
		try {
			DadesConsulta dadesConsulta = new DadesConsulta();
			dadesConsulta.setAmbJustificant(ambJustificant);
			dadesConsulta.setIdentificador(identificador);
			dadesConsulta.setReferencia(referencia);
			RespostaConsultaDadesRegistreV2  respostaConsultaInfoRegistre = getNotificacioRestClient().consultaDadesRegistre(dadesConsulta);
			if (respostaConsultaInfoRegistre != null) {
				resposta.setDataRegistre(respostaConsultaInfoRegistre.getDataRegistre());
				resposta.setNumRegistre(respostaConsultaInfoRegistre.getNumRegistre());
				resposta.setNumRegistreFormatat(respostaConsultaInfoRegistre.getNumRegistreFormatat());
				if (respostaConsultaInfoRegistre.getJustificant() != null) {
					resposta.setJustificant(respostaConsultaInfoRegistre.getJustificant());
				}
				resposta.setError(respostaConsultaInfoRegistre.isError());
				resposta.setErrorData(respostaConsultaInfoRegistre.getErrorData());
				resposta.setErrorDescripcio(respostaConsultaInfoRegistre.getErrorDescripcio());
			}
		return resposta;

		} catch (Exception ex) {
			throw new SistemaExternException(
					"No s'ha pogut consultar la informació del registre de la notificació (" +
					"identificador=" + identificador + ")",
					ex);
		}
	}
	
	
	@Override
	public RespostaJustificantEnviamentNotib consultaJustificantEnviament(
			String identificador) throws SistemaExternException {
		RespostaJustificantEnviamentNotib resposta = new RespostaJustificantEnviamentNotib();
		try {

			RespostaConsultaJustificantEnviament respostaConsultaJustificantEnviament = getNotificacioRestClient().consultaJustificantEnviament(identificador);
			if (respostaConsultaJustificantEnviament != null) {
				resposta.setError(respostaConsultaJustificantEnviament.isError());
				resposta.setErrorData(respostaConsultaJustificantEnviament.getErrorData());
				resposta.setErrorDescripcio(respostaConsultaJustificantEnviament.getErrorDescripcio());
				resposta.setJustificant(respostaConsultaJustificantEnviament.getJustificant() != null ? new Base64().decode(respostaConsultaJustificantEnviament.getJustificant().getContingut()) : null);
			}
		return resposta;

		} catch (Exception ex) {
			throw new SistemaExternException(
					"No s'ha pogut consultar justicant de enviament (" +
					"identificador=" + identificador + ")",
					ex);
		}
	}



	private XMLGregorianCalendar toXmlGregorianCalendar(Date date) throws DatatypeConfigurationException {
		if (date == null) {
			return null;
		}
		GregorianCalendar gc = new GregorianCalendar();
		gc.setTime(date);
		return DatatypeFactory.newInstance().newXMLGregorianCalendar(gc);
	}
	private Date toDate(XMLGregorianCalendar calendar) throws DatatypeConfigurationException {
		if (calendar == null) {
			return null;
		}
		return calendar.toGregorianCalendar().getTime();
	}

	private es.caib.notib.client.domini.Persona toPersonaNotib(
			Persona persona) {
		es.caib.notib.client.domini.Persona p = null;
		if (persona != null) {
			p = new es.caib.notib.client.domini.Persona();
			if (persona.getInteressatTipus() == es.caib.ripea.core.api.dto.InteressatTipusEnumDto.ADMINISTRACIO) {
				p.setDir3Codi(persona.getNif());
				
			} else {
				p.setNif(persona.getNif());
			}
			p.setNom(persona.getNom());
			p.setLlinatge1(persona.getLlinatge1());
			p.setLlinatge2(persona.getLlinatge2());
			p.setRaoSocial(persona.getRaoSocial());
			p.setTelefon(persona.getTelefon());
			p.setEmail(persona.getEmail());
			p.setInteressatTipus(toInteressatTipusEnumDto(persona.getInteressatTipus()));
			p.setIncapacitat(persona.isIncapacitat());
		}
		return p;
	}

	
	
	private InteressatTipusEnumDto toInteressatTipusEnumDto(es.caib.ripea.core.api.dto.InteressatTipusEnumDto interessatTipusEnumDto) {
		InteressatTipusEnumDto interessatTipusEnumDtoWS = null;
		if (interessatTipusEnumDto != null) {
			switch (interessatTipusEnumDto) {
			case PERSONA_FISICA:
				interessatTipusEnumDtoWS = InteressatTipusEnumDto.FISICA;
				break;
			case PERSONA_JURIDICA:
				interessatTipusEnumDtoWS = InteressatTipusEnumDto.JURIDICA;
				break;
			case ADMINISTRACIO:
				interessatTipusEnumDtoWS = InteressatTipusEnumDto.ADMINISTRACIO;
				break;				
			}
		}
		return interessatTipusEnumDtoWS;
	}	
	
	private NotificacioRestClientV2 getNotificacioRestClient() {
		// If Notib server uses basic authentication set autenticacioBasic=true, if uses form authentication (as in Jboss CAIB) set autenticacioBasic=false
		String autenticacioBasicString = getProperty("notificacio.autenticacioBasic");
		boolean autenticacioBasic;
		if (autenticacioBasicString != null) {
			autenticacioBasic = new Boolean(autenticacioBasicString).booleanValue();
		} else {
			autenticacioBasic = false;
		}
		if (clientV2 == null) {
			clientV2 = NotificacioRestClientFactory.getRestClientV2(
					getUrl(),
					getUsername(),
					getPassword(),
					autenticacioBasic);
		}
		return clientV2;
	}

	private String getUrl() {
		return getProperty(
				"plugin.notificacio.url");
	}
	private String getUsername() {
		return getProperty(
				"plugin.notificacio.username");
	}
	private String getPassword() {
		return getProperty(
				"plugin.notificacio.password");
	}

	
	private static final Logger logger = LoggerFactory.getLogger(NotificacioPluginNotib.class);
}
