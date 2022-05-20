/**
 * 
 */
package es.caib.ripea.core.helper;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
import es.caib.ripea.core.api.dto.IntegracioAccioTipusEnumDto;
import es.caib.ripea.core.api.dto.PinbalConsentimentEnumDto;
import es.caib.ripea.core.api.exception.PinbalException;
import es.caib.ripea.core.entity.EntitatEntity;
import es.caib.ripea.core.entity.ExpedientEntity;
import es.caib.ripea.core.entity.InteressatPersonaFisicaEntity;
import es.caib.ripea.core.entity.MetaDocumentEntity;
import es.caib.ripea.core.entity.MetaExpedientEntity;
import es.caib.ripea.core.entity.UsuariEntity;

/**
 * Mètodes comuns per a gestionar les alertes.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Component
public class PinbalHelper {

	@Autowired
	private UsuariHelper usuariHelper;
	@Autowired
	private IntegracioHelper integracioHelper;
	@Autowired
	private ExpedientHelper expedientHelper;
	@Autowired
	private ConfigHelper configHelper;

	public String novaPeticioSvddgpciws02(
			ExpedientEntity expedient,
			MetaDocumentEntity metaDocument,
			InteressatPersonaFisicaEntity interessat,
			String finalitat,
			PinbalConsentimentEnumDto consentiment) throws PinbalException {
		long t0 = System.currentTimeMillis();
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
			return processarScspRespuesta(solicitud, respuesta, "SVDDGPCIWS02", t0);
		} catch (Exception ex) {
			throw processarException(solicitud, ex, "SVDDGPCIWS02", t0);
		}
	}

	public String novaPeticioSvddgpviws02(
			ExpedientEntity expedient,
			MetaDocumentEntity metaDocument,
			InteressatPersonaFisicaEntity interessat,
			String finalitat,
			PinbalConsentimentEnumDto consentiment) throws PinbalException {
		long t0 = System.currentTimeMillis();
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
			return processarScspRespuesta(solicitud, respuesta, "SVDDGPVIWS02", t0);
		} catch (Exception ex) {
			throw processarException(solicitud, ex, "SVDDGPVIWS02", t0);
		}
	}

	public String novaPeticioSvdccaacpasws01(
			ExpedientEntity expedient,
			MetaDocumentEntity metaDocument,
			InteressatPersonaFisicaEntity interessat,
			String finalitat,
			PinbalConsentimentEnumDto consentiment,
			String comunitatAutonomaCodi,
			String provinciaCodi) throws PinbalException {
		long t0 = System.currentTimeMillis();
		SolicitudSvdccaacpasws01 solicitud = new SolicitudSvdccaacpasws01();
		emplenarSolicitudBase(
				solicitud,
				expedient,
				metaDocument,
				interessat,
				finalitat,
				consentiment);
		solicitud.setCodigoComunidadAutonoma(comunitatAutonomaCodi);
		solicitud.setCodigoProvincia(provinciaCodi);
		try {
			ScspRespuesta respuesta = getClientSvdccaacpasws01().peticionSincrona(Arrays.asList(solicitud));
			return processarScspRespuesta(solicitud, respuesta, "SVDCCAACPASWS01", t0);
		} catch (Exception ex) {
			throw processarException(solicitud, ex, "SVDCCAACPASWS01", t0);
		}
	}

	public FitxerDto getJustificante(String idPeticion) throws PinbalException {
		long t0 = System.currentTimeMillis();
		String accioDescripcio = "Consulta del justificant";
		String errorDescripcio = "Excepció en la petició a PINBAL";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("idPeticion", idPeticion);
		try {
			ScspJustificante justificante = getClientSvdccaacpasws01().getJustificante(idPeticion);
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_PINBAL,
					accioDescripcio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
			return new FitxerDto(
					justificante.getNom(),
					justificante.getContentType(),
					justificante.getContingut());
		} catch (Exception ex) {
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_PINBAL,
					accioDescripcio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio);
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
		String codiSia = getPinbalDefaultSia();
		solicitud.setNombreSolicitante(entitat.getNom());
		solicitud.setIdentificadorSolicitante(entitat.getCif());
		solicitud.setCodigoProcedimiento((codiSia != null && !codiSia.trim().isEmpty()) ? codiSia : metaExpedient.getClassificacioSia());
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
		solicitud.setIdExpediente(expedientHelper.calcularNumero(expedient));
		solicitud.setFuncionario(getFuncionariActual());
		solicitud.setTitular(getTitularFromInteressat(interessat, false));
	}

	private ScspFuncionario getFuncionariActual() {
		ScspFuncionario funcionario = new ScspFuncionario();
		UsuariEntity funcionari = usuariHelper.getUsuariAutenticat();
		funcionario.setNifFuncionario(funcionari.getNif());
		funcionario.setNombreCompletoFuncionario(funcionari.getNom());
		return funcionario;
	}

	private ScspTitular getTitularFromInteressat(
			InteressatPersonaFisicaEntity interessat,
			boolean ambNomSencer) {
		ScspTitular titular = new ScspTitular();
		switch (interessat.getDocumentTipus()) {
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

	private String processarScspRespuesta(
			SolicitudBase solicitud,
			ScspRespuesta respuesta,
			String serveiScsp,
			long t0) throws PinbalException {
		String accioDescripcio = "Petició síncrona";
		if (respuesta.getAtributos().getEstado().getCodigoEstado().equals("0003")) {
			Map<String, String> accioParams = getAccioParams(solicitud, serveiScsp);
			accioParams.put("idPeticion", respuesta.getAtributos().getIdPeticion());
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_PINBAL,
					accioDescripcio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
			return respuesta.getAtributos().getIdPeticion();
		} else {
			String errorDescripcio = "[" + respuesta.getAtributos().getEstado().getCodigoEstado() + "] " + respuesta.getAtributos().getEstado().getLiteralError();
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_PINBAL,
					accioDescripcio,
					getAccioParams(solicitud, serveiScsp),
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio);
			throw new PinbalException(errorDescripcio);
		}
	}

	private PinbalException processarException(
			SolicitudBase solicitud,
			Exception ex, 
			String serveiScsp,
			long t0) {
		String recobrimentExceptionPrefix = "es.caib.pinbal.client.recobriment.RecobrimentException: ";
		String accioDescripcio = "Petició síncrona";
		String missatge;
		if (ex.getMessage() != null && ex.getMessage().indexOf(recobrimentExceptionPrefix) != -1) {
			missatge = ex.getMessage().substring(ex.getMessage().indexOf(recobrimentExceptionPrefix) + recobrimentExceptionPrefix.length());
		} else {
			missatge = ex.getMessage();
		}
		String errorDescripcio = "Excepció en la petició a PINBAL: " + missatge;
		integracioHelper.addAccioError(
				IntegracioHelper.INTCODI_PINBAL,
				accioDescripcio,
				getAccioParams(solicitud, serveiScsp),
				IntegracioAccioTipusEnumDto.ENVIAMENT,
				System.currentTimeMillis() - t0,
				errorDescripcio,
				ex);
		return new PinbalException(
				missatge,
				ex);
	}

	private Map<String, String> getAccioParams(
			SolicitudBase solicitud,
			String serveiScsp) {
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("serveiScsp", serveiScsp);
		accioParams.put("identificadorSolicitante", solicitud.getIdentificadorSolicitante());
		accioParams.put("nombreSolicitante", solicitud.getNombreSolicitante());
		accioParams.put("codigoProcedimiento", solicitud.getCodigoProcedimiento());
		accioParams.put("codigoUnidadTramitadora", solicitud.getCodigoUnidadTramitadora());
		accioParams.put("unidadTramitadora", solicitud.getUnidadTramitadora());
		accioParams.put("idExpediente", solicitud.getIdExpediente());
		if (solicitud.getFuncionario() != null) {
			accioParams.put(
					"funcionario.nif",
					solicitud.getFuncionario().getNifFuncionario());
			accioParams.put(
					"funcionario.nombre",
					solicitud.getFuncionario().getNombreCompletoFuncionario());
		}
		if (solicitud.getTitular() != null) {
			if (solicitud.getTitular().getTipoDocumentacion() != null) {
				accioParams.put(
						"titular.tipoDocumentacion",
						solicitud.getTitular().getTipoDocumentacion().name());
			}
			accioParams.put(
					"titular.documentacion",
					solicitud.getTitular().getDocumentacion());
			accioParams.put(
					"titular.nombre",
					solicitud.getTitular().getNombre());
			accioParams.put(
					"titular.apellido1",
					solicitud.getTitular().getApellido1());
			accioParams.put(
					"titular.apellido2",
					solicitud.getTitular().getApellido2());
			if (solicitud.getTitular().getNombreCompleto() != null) {
				accioParams.put(
						"titular.nombreCompleto",
						solicitud.getTitular().getNombreCompleto());
			}
		}
		return accioParams;
	}

	private ClientSvddgpciws02 getClientSvddgpciws02() {
		ClientSvddgpciws02 clientSvddgpciws02 = new ClientSvddgpciws02(
				getPinbalBaseUrl(),
				getPinbalUser(),
				getPinbalPassword(),
				getPinbalBasicAuth(),
				null,
				null);
		if (log.isDebugEnabled())
			clientSvddgpciws02.enableLogginFilter();
		return clientSvddgpciws02;
	}

	private ClientSvddgpviws02 getClientSvddgpviws02() {
		ClientSvddgpviws02 clientSvddgpviws02 = new ClientSvddgpviws02(
				getPinbalBaseUrl(),
				getPinbalUser(),
				getPinbalPassword(),
				getPinbalBasicAuth(),
				null,
				null);
		if (log.isDebugEnabled())
			clientSvddgpviws02.enableLogginFilter();
		return clientSvddgpviws02;
	}

	private ClientSvdccaacpasws01 getClientSvdccaacpasws01() {
		ClientSvdccaacpasws01 clientSvdccaacpasws01 = new ClientSvdccaacpasws01(
				getPinbalBaseUrl(),
				getPinbalUser(),
				getPinbalPassword(),
				getPinbalBasicAuth(),
				null,
				null);
		if (log.isDebugEnabled())
			clientSvdccaacpasws01.enableLogginFilter();
		return clientSvdccaacpasws01;
	}

	private String getPinbalBaseUrl() {
		return configHelper.getConfig("es.caib.ripea.pinbal.base.url");
	}
	private String getPinbalUser() {
		return configHelper.getConfig("es.caib.ripea.pinbal.user");
	}
	private String getPinbalPassword() {
		return configHelper.getConfig("es.caib.ripea.pinbal.password");
	}
	private boolean getPinbalBasicAuth() {
		return configHelper.getAsBoolean("es.caib.ripea.pinbal.basic.auth");
	}
	private String getPinbalDefaultSia() {
		return configHelper.getConfig("es.caib.ripea.pinbal.codi.sia.peticions");
	}

}
