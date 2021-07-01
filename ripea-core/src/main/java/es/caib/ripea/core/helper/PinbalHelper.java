/**
 * 
 */
package es.caib.ripea.core.helper;

import java.util.Arrays;

import org.springframework.stereotype.Component;

import es.caib.pinbal.client.recobriment.model.ScspFuncionario;
import es.caib.pinbal.client.recobriment.model.ScspJustificante;
import es.caib.pinbal.client.recobriment.model.ScspRespuesta;
import es.caib.pinbal.client.recobriment.model.ScspSolicitante.ScspConsentimiento;
import es.caib.pinbal.client.recobriment.model.ScspTitular;
import es.caib.pinbal.client.recobriment.model.ScspTitular.ScspTipoDocumentacion;
import es.caib.pinbal.client.recobriment.model.SolicitudBase;
import es.caib.pinbal.client.recobriment.svdccaacpasws01.ClientSvdccaacpasws01;
import es.caib.pinbal.client.recobriment.svdccaacpasws01.ClientSvdccaacpasws01.SolicitudSvdccaacpasws01;
import es.caib.pinbal.client.recobriment.svddgpciws02.ClientSvddgpciws02;
import es.caib.pinbal.client.recobriment.svddgpciws02.ClientSvddgpciws02.SolicitudSvddgpciws02;
import es.caib.pinbal.client.recobriment.svddgpviws02.ClientSvddgpviws02;
import es.caib.pinbal.client.recobriment.svddgpviws02.ClientSvddgpviws02.SolicitudSvddgpviws02;
import es.caib.ripea.core.api.dto.FitxerDto;
import es.caib.ripea.core.api.dto.PinbalConsentimentEnumDto;
import es.caib.ripea.core.api.exception.PinbalException;
import es.caib.ripea.core.entity.EntitatEntity;
import es.caib.ripea.core.entity.ExpedientEntity;
import es.caib.ripea.core.entity.InteressatPersonaFisicaEntity;
import es.caib.ripea.core.entity.MetaDocumentEntity;
import es.caib.ripea.core.entity.MetaExpedientEntity;

/**
 * Mètodes comuns per a gestionar les alertes.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class PinbalHelper {

	private ClientSvddgpciws02 clientSvddgpciws02;
	private ClientSvddgpviws02 clientSvddgpviws02;
	private ClientSvdccaacpasws01 clientSvdccaacpasws01;

	public String novaPeticioSvddgpciws02(
			ExpedientEntity expedient,
			MetaDocumentEntity metaDocument,
			InteressatPersonaFisicaEntity interessat,
			String finalitat,
			PinbalConsentimentEnumDto consentiment) throws PinbalException {
		SolicitudSvddgpciws02 solicitud = new SolicitudSvddgpciws02();
		emplenarSolicitudBase(
				solicitud,
				expedient,
				metaDocument,
				interessat,
				finalitat,
				consentiment);
		try {
			ScspRespuesta respuesta = getClientSvddgpciws02().peticionSincrona(Arrays.asList(solicitud));
			if (respuesta.getAtributos().getEstado().getCodigoEstado().equals("0003")) {
				return respuesta.getAtributos().getIdPeticion();
			} else {
				throw new PinbalException("[" + respuesta.getAtributos().getEstado().getCodigoEstado() + "] " + respuesta.getAtributos().getEstado().getLiteralError());
			}
		} catch (Exception ex) {
			throw new PinbalException(ex);
		}
	}

	public String novaPeticioSvddgpviws02(
			ExpedientEntity expedient,
			MetaDocumentEntity metaDocument,
			InteressatPersonaFisicaEntity interessat,
			String finalitat,
			PinbalConsentimentEnumDto consentiment) throws PinbalException {
		SolicitudSvddgpviws02 solicitud = new SolicitudSvddgpviws02();
		emplenarSolicitudBase(
				solicitud,
				expedient,
				metaDocument,
				interessat,
				finalitat,
				consentiment);
		try {
			ScspRespuesta respuesta = getClientSvddgpviws02().peticionSincrona(Arrays.asList(solicitud));
			if (respuesta.getAtributos().getEstado().getCodigoEstado().equals("0003")) {
				return respuesta.getAtributos().getIdPeticion();
			} else {
				throw new PinbalException("[" + respuesta.getAtributos().getEstado().getCodigoEstado() + "] " + respuesta.getAtributos().getEstado().getLiteralError());
			}
		} catch (Exception ex) {
			throw new PinbalException(ex);
		}
	}

	public String novaPeticioSvdccaacpasws01(
			ExpedientEntity expedient,
			MetaDocumentEntity metaDocument,
			InteressatPersonaFisicaEntity interessat,
			String finalitat,
			PinbalConsentimentEnumDto consentiment) throws PinbalException {
		SolicitudSvdccaacpasws01 solicitud = new SolicitudSvdccaacpasws01();
		emplenarSolicitudBase(
				solicitud,
				expedient,
				metaDocument,
				interessat,
				finalitat,
				consentiment);
		try {
			ScspRespuesta respuesta = getClientSvdccaacpasws01().peticionSincrona(Arrays.asList(solicitud));
			if (respuesta.getAtributos().getEstado().getCodigoEstado().equals("0003")) {
				return respuesta.getAtributos().getIdPeticion();
			} else {
				throw new PinbalException("[" + respuesta.getAtributos().getEstado().getCodigoEstado() + "] " + respuesta.getAtributos().getEstado().getLiteralError());
			}
		} catch (Exception ex) {
			throw new PinbalException(ex);
		}
	}

	public FitxerDto getJustificante(String idPeticion) throws PinbalException {
		try {
			ScspJustificante justificante = getClientSvdccaacpasws01().getJustificante(idPeticion);
			return new FitxerDto(
					justificante.getNom(),
					justificante.getContentType(),
					justificante.getContingut());
		} catch (Exception ex) {
			throw new PinbalException(ex);
		}
	}

	private void emplenarSolicitudBase(
			SolicitudBase solicitud,
			ExpedientEntity expedient,
			MetaDocumentEntity metaDocument,
			InteressatPersonaFisicaEntity interessat,
			String finalitat,
			PinbalConsentimentEnumDto consentiment) {
		EntitatEntity entitat = metaDocument.getEntitat();
		MetaExpedientEntity metaExpedient = metaDocument.getMetaExpedient();
		solicitud.setNombreSolicitante(entitat.getNom());
		solicitud.setIdentificadorSolicitante(entitat.getCif());
		solicitud.setCodigoProcedimiento(metaExpedient.getClassificacioSia());
		solicitud.setIdentificadorSolicitante("S0711001H");
		solicitud.setCodigoProcedimiento("CODSVDR_GBA_20121107");
		solicitud.setUnidadTramitadora(expedient.getOrganGestor().getNom());
		solicitud.setFinalidad(finalitat);
		switch (consentiment) {
		case LLEI:
			solicitud.setConsentimiento(ScspConsentimiento.Ley);
			break;
		case SI:
			solicitud.setConsentimiento(ScspConsentimiento.Si);
			break;
		}
		solicitud.setFuncionario(getFuncionariActual());
		solicitud.setTitular(getTitularFromInteressat(interessat, false));
	}

	private ScspFuncionario getFuncionariActual() {
		ScspFuncionario funcionario = new ScspFuncionario();
		funcionario.setNifFuncionario("00000000T");
		funcionario.setNombreCompletoFuncionario("Funcionari CAIB");
		return funcionario;
	}

	private ScspTitular getTitularFromInteressat(
			InteressatPersonaFisicaEntity interessat,
			boolean ambNomSencer) {
		ScspTitular titular = new ScspTitular();
		switch (interessat.getDocumentTipus()) {
		case CIF:
			titular.setTipoDocumentacion(ScspTipoDocumentacion.CIF);
			break;
		case DOCUMENT_IDENTIFICATIU_ESTRANGERS:
			titular.setTipoDocumentacion(ScspTipoDocumentacion.NIE);
			break;
		case NIF:
			titular.setTipoDocumentacion(ScspTipoDocumentacion.DNI);
			break;
		case PASSAPORT:
			titular.setTipoDocumentacion(ScspTipoDocumentacion.Pasaporte);
			break;
		default:
			titular.setTipoDocumentacion(ScspTipoDocumentacion.Otros);
		}
		titular.setDocumentacion(interessat.getDocumentNum());
		titular.setNombre(interessat.getNom());
		titular.setApellido1(interessat.getLlinatge1());
		titular.setApellido2(interessat.getLlinatge2());
		if (ambNomSencer) {
			StringBuilder nomSencer = new StringBuilder(interessat.getNom());
			if (interessat.getLlinatge1() != null) {
				nomSencer.append(" ");
				nomSencer.append(interessat.getLlinatge1().trim());
			}
			if (interessat.getLlinatge2() != null) {
				nomSencer.append(" ");
				nomSencer.append(interessat.getLlinatge2().trim());
			}
			titular.setNombreCompleto(nomSencer.toString());
		}
		return titular;
	}

	private ClientSvddgpciws02 getClientSvddgpciws02() {
		if (clientSvddgpciws02 == null) {
			clientSvddgpciws02 = new ClientSvddgpciws02(
				getPinbalBaseUrl(),
				getPinbalUser(),
				getPinbalPassword(),
				getPinbalBasicAuth(),
				null,
				null);
		}
		return clientSvddgpciws02;
	}

	private ClientSvddgpviws02 getClientSvddgpviws02() {
		if (clientSvddgpviws02 == null) {
			clientSvddgpviws02 = new ClientSvddgpviws02(
				getPinbalBaseUrl(),
				getPinbalUser(),
				getPinbalPassword(),
				getPinbalBasicAuth(),
				null,
				null);
		}
		return clientSvddgpviws02;
	}

	private ClientSvdccaacpasws01 getClientSvdccaacpasws01() {
		if (clientSvdccaacpasws01 == null) {
			clientSvdccaacpasws01 = new ClientSvdccaacpasws01(
					getPinbalBaseUrl(),
					getPinbalUser(),
					getPinbalPassword(),
					getPinbalBasicAuth(),
					null,
					null);
		}
		return clientSvdccaacpasws01;
	}

	private String getPinbalBaseUrl() {
		return PropertiesHelper.getProperties().getProperty("es.caib.ripea.pinbal.base.url");
	}
	private String getPinbalUser() {
		return PropertiesHelper.getProperties().getProperty("es.caib.ripea.pinbal.user");
	}
	private String getPinbalPassword() {
		return PropertiesHelper.getProperties().getProperty("es.caib.ripea.pinbal.password");
	}
	private boolean getPinbalBasicAuth() {
		return PropertiesHelper.getProperties().getAsBoolean("es.caib.ripea.pinbal.basic.auth");
	}

}
