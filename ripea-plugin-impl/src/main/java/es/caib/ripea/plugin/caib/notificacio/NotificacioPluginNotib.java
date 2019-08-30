/**
 * 
 */
package es.caib.ripea.plugin.caib.notificacio;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.codec.binary.Base64;

import es.caib.notib.client.NotificacioRestClient;
import es.caib.notib.client.NotificacioRestClientFactory;
import es.caib.notib.ws.notificacio.Certificacio;
import es.caib.notib.ws.notificacio.DocumentV2;
import es.caib.notib.ws.notificacio.EntregaDeh;
import es.caib.notib.ws.notificacio.EntregaPostal;
import es.caib.notib.ws.notificacio.EntregaPostalViaTipusEnum;
import es.caib.notib.ws.notificacio.EnviamentTipusEnum;
import es.caib.notib.ws.notificacio.InteressatTipusEnumDto;
import es.caib.notib.ws.notificacio.NotificaDomiciliConcretTipusEnumDto;
import es.caib.notib.ws.notificacio.RespostaAlta;
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
import es.caib.ripea.plugin.notificacio.RespostaEnviar;
import es.caib.ripea.plugin.utils.PropertiesHelper;

/**
 * Implementació de del plugin d'enviament de notificacions
 * emprant NOTIB.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class NotificacioPluginNotib implements NotificacioPlugin {

	
	private NotificacioRestClient clientV2;


	@Override
	public RespostaEnviar enviar(
			Notificacio notificacio) throws SistemaExternException {
		try {
			es.caib.notib.ws.notificacio.NotificacioV2 notificacioNotib = new es.caib.notib.ws.notificacio.NotificacioV2();

			
			notificacioNotib.setEmisorDir3Codi(notificacio.getEmisorDir3Codi());
			notificacioNotib.setEnviamentTipus(notificacio.getEnviamentTipus() != null ? EnviamentTipusEnum.valueOf(notificacio.getEnviamentTipus().toString()) : null);
			notificacioNotib.setConcepte(notificacio.getConcepte());
			notificacioNotib.setDescripcio(notificacio.getDescripcio());
			notificacioNotib.setEnviamentDataProgramada(toXmlGregorianCalendar(notificacio.getEnviamentDataProgramada()));
			notificacioNotib.setRetard(notificacio.getRetard());
			notificacioNotib.setCaducitat(toXmlGregorianCalendar(notificacio.getCaducitat()));
			DocumentV2 document = new DocumentV2();
			document.setArxiuNom(notificacio.getDocumentArxiuNom());
			document.setContingutBase64(new String(Base64.encodeBase64(notificacio.getDocumentArxiuContingut())));
			notificacioNotib.setDocument(document);
			notificacioNotib.setProcedimentCodi(notificacio.getProcedimentCodi());
			notificacioNotib.setUsuariCodi(notificacio.getUsuariCodi());
			
			if (notificacio.getEnviaments() != null) {
				for (Enviament enviament : notificacio.getEnviaments()) {
					es.caib.notib.ws.notificacio.Enviament enviamentNotib = new es.caib.notib.ws.notificacio.Enviament();
					enviamentNotib.setTitular(
							toPersonaNotib(enviament.getTitular()));
					if (enviament.getDestinataris() != null) {
						for (Persona destinatari: enviament.getDestinataris()) {
							enviamentNotib.getDestinataris().add(
									toPersonaNotib(destinatari));
						}
					}
					if (enviament.getEntregaPostalTipus() != null) {
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
					}
					if (enviament.getEntregaDehProcedimentCodi() != null) {
						EntregaDeh entregaDeh = new EntregaDeh();
						entregaDeh.setObligat(enviament.isEntregaDehObligat());
						entregaDeh.setProcedimentCodi(enviament.getEntregaDehProcedimentCodi());
						enviamentNotib.setEntregaDeh(entregaDeh);
					}
					
					enviamentNotib.setServeiTipus(es.caib.notib.ws.notificacio.NotificaServeiTipusEnumDto.valueOf(notificacio.getServeiTipusEnum().toString()));
					notificacioNotib.getEnviaments().add(enviamentNotib);
				}
			}

			//####### send notificacio ####################
			RespostaAlta respostaAlta = getNotificacioService().alta(notificacioNotib);
			
			if (respostaAlta.isError()) {
				throw new SistemaExternException(respostaAlta.getErrorDescripcio());
			} else {
				RespostaEnviar resposta = new RespostaEnviar();
				resposta.setEstat(respostaAlta.getEstat() != null ? NotificacioEstat.valueOf(respostaAlta.getEstat().toString()) : null);
				resposta.setIdentificador(respostaAlta.getIdentificador());
				if (respostaAlta.getReferencies() != null) {
					List<EnviamentReferencia> referencies = new ArrayList<EnviamentReferencia>();
					for (es.caib.notib.ws.notificacio.EnviamentReferencia ref: respostaAlta.getReferencies()) {
						EnviamentReferencia referencia = new EnviamentReferencia();
						referencia.setTitularNif(ref.getTitularNif());
						referencia.setReferencia(ref.getReferencia());
						referencies.add(referencia);
					}
					resposta.setReferencies(referencies);
				}
				return resposta;
			}
			
		} catch (Exception ex) {
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
			es.caib.notib.ws.notificacio.RespostaConsultaEstatNotificacio respostaConsultaEstat = getNotificacioService().consultaEstatNotificacio(identificador);
			if (respostaConsultaEstat.isError()) {
				throw new SistemaExternException(respostaConsultaEstat.getErrorDescripcio());
			} else {
				RespostaConsultaEstatNotificacio resposta = new RespostaConsultaEstatNotificacio();
				if (respostaConsultaEstat.getEstat() != null) {
					switch (respostaConsultaEstat.getEstat()) {
					case PENDENT:
						resposta.setEstat(NotificacioEstat.PENDENT);
						break;
					case ENVIADA:
						resposta.setEstat(NotificacioEstat.ENVIADA);
						break;
					case FINALITZADA:
						resposta.setEstat(NotificacioEstat.FINALITZADA);
						break;
					}
				}
				return resposta;
			}
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
			es.caib.notib.ws.notificacio.RespostaConsultaEstatEnviament respostaConsultaEstat = getNotificacioService().consultaEstatEnviament(referencia);
			if (respostaConsultaEstat.isError()) {
				throw new SistemaExternException(respostaConsultaEstat.getErrorDescripcio());
			} else {
				RespostaConsultaEstatEnviament resposta = new RespostaConsultaEstatEnviament();
				if (respostaConsultaEstat.getEstat() != null) {
					switch (respostaConsultaEstat.getEstat()) {
					case NOTIB_ENVIADA:
						resposta.setEstat(EnviamentEstat.NOTIB_ENVIADA);
						break;
					case NOTIB_PENDENT:
						resposta.setEstat(EnviamentEstat.NOTIB_PENDENT);
						break;
					case ABSENT:
						resposta.setEstat(EnviamentEstat.ABSENT);
						break;
					case ADRESA_INCORRECTA:
						resposta.setEstat(EnviamentEstat.ADRESA_INCORRECTA);
						break;
					case DESCONEGUT:
						resposta.setEstat(EnviamentEstat.DESCONEGUT);
						break;
					case ENTREGADA_OP:
						resposta.setEstat(EnviamentEstat.ENTREGADA_OP);
						break;
					case ENVIADA_CI:
						resposta.setEstat(EnviamentEstat.ENVIADA_CI);
						break;
					case ENVIADA_DEH:
						resposta.setEstat(EnviamentEstat.ENVIADA_DEH);
						break;
					case ENVIAMENT_PROGRAMAT:
						resposta.setEstat(EnviamentEstat.ENVIAMENT_PROGRAMAT);
						break;
					case ERROR_ENTREGA:
						resposta.setEstat(EnviamentEstat.ERROR_ENTREGA);
						break;
					case EXPIRADA:
						resposta.setEstat(EnviamentEstat.EXPIRADA);
						break;
					case EXTRAVIADA:
						resposta.setEstat(EnviamentEstat.EXTRAVIADA);
						break;
					case LLEGIDA:
						resposta.setEstat(EnviamentEstat.LLEGIDA);
						break;
					case MORT:
						resposta.setEstat(EnviamentEstat.MORT);
						break;
					case NOTIFICADA:
						resposta.setEstat(EnviamentEstat.NOTIFICADA);
						break;
					case PENDENT_CIE:
						resposta.setEstat(EnviamentEstat.PENDENT_CIE);
						break;
					case PENDENT_DEH:
						resposta.setEstat(EnviamentEstat.PENDENT_DEH);
						break;
					case PENDENT_ENVIAMENT:
						resposta.setEstat(EnviamentEstat.PENDENT_ENVIAMENT);
						break;
					case PENDENT_SEU:
						resposta.setEstat(EnviamentEstat.PENDENT_SEU);
						break;
					case REBUTJADA:
						resposta.setEstat(EnviamentEstat.REBUTJADA);
						break;
					case SENSE_INFORMACIO:
						resposta.setEstat(EnviamentEstat.SENSE_INFORMACIO);
						break;
					}
				}
				resposta.setEstatData(toDate(respostaConsultaEstat.getEstatData()));
				resposta.setEstatDescripcio(respostaConsultaEstat.getEstatDescripcio());
				resposta.setEstatOrigen(respostaConsultaEstat.getEstatOrigen());
				resposta.setReceptorNif(respostaConsultaEstat.getReceptorNif());
				resposta.setReceptorNom(respostaConsultaEstat.getReceptorNom());
				if (respostaConsultaEstat.getCertificacio() != null) {
					Certificacio certificacio = respostaConsultaEstat.getCertificacio();
					resposta.setCertificacioData(toDate(certificacio.getData()));
					resposta.setCertificacioOrigen(certificacio.getOrigen());
					resposta.setCertificacioContingut(
							Base64.decodeBase64(certificacio.getContingutBase64().getBytes()));
					resposta.setCertificacioHash(certificacio.getHash());
					resposta.setCertificacioMetadades(certificacio.getMetadades());
					resposta.setCertificacioCsv(certificacio.getCsv());
					resposta.setCertificacioTipusMime(certificacio.getTipusMime());
				}
				return resposta;
			}
		} catch (Exception ex) {
			throw new SistemaExternException(
					"No s'ha pogut consultar l'estat de l'enviament (" +
					"referencia=" + referencia + ")",
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

	private es.caib.notib.ws.notificacio.Persona toPersonaNotib(
			Persona persona) {
		es.caib.notib.ws.notificacio.Persona p = null;
		if (persona != null) {
			p = new es.caib.notib.ws.notificacio.Persona();
			p.setNif(persona.getNif());
			p.setNom(persona.getNom());
			p.setLlinatge1(persona.getLlinatge1());
			p.setLlinatge2(persona.getLlinatge2());
			p.setTelefon(persona.getTelefon());
			p.setEmail(persona.getEmail());
			p.setInteressatTipus(toInteressatTipusEnumDto(persona.getInteressatTipus()));
		}
		return p;
	}
	
	
	
	private InteressatTipusEnumDto toInteressatTipusEnumDto(es.caib.ripea.core.api.dto.InteressatTipusEnumDto interessatTipusEnumDto) {
		es.caib.notib.ws.notificacio.InteressatTipusEnumDto interessatTipusEnumDtoWS = null;
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
	
	


	private NotificacioRestClient getNotificacioService() {
		if (clientV2 == null) {
			clientV2 = NotificacioRestClientFactory.getRestClientV2(
					getUrl(),
					getUsername(),
					getPassword());
		}
		return clientV2;
	}
	
	

	private String getUrl() {
		return PropertiesHelper.getProperties().getProperty(
				"es.caib.ripea.plugin.notificacio.url");
	}
	private String getUsername() {
		return PropertiesHelper.getProperties().getProperty(
				"es.caib.ripea.plugin.notificacio.username");
	}
	private String getPassword() {
		return PropertiesHelper.getProperties().getProperty(
				"es.caib.ripea.plugin.notificacio.password");
	}

}
