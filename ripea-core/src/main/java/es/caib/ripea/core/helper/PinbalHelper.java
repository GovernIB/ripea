/**
 * 
 */
package es.caib.ripea.core.helper;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.caib.pinbal.client.recobriment.ecot103.ClientEcot103;
import es.caib.pinbal.client.recobriment.ecot103.ClientEcot103.SolicitudEcot103;
import es.caib.pinbal.client.recobriment.model.ScspFuncionario;
import es.caib.pinbal.client.recobriment.model.ScspJustificante;
import es.caib.pinbal.client.recobriment.model.ScspRespuesta;
import es.caib.pinbal.client.recobriment.model.ScspSolicitante.ScspConsentimiento;
import es.caib.pinbal.client.recobriment.model.ScspTitular;
import es.caib.pinbal.client.recobriment.model.ScspTitular.ScspTipoDocumentacion;
import es.caib.pinbal.client.recobriment.model.SolicitudBase;
import es.caib.pinbal.client.recobriment.nivrenti.ClientNivrenti;
import es.caib.pinbal.client.recobriment.nivrenti.ClientNivrenti.SolicitudNivrenti;
import es.caib.pinbal.client.recobriment.q2827003atgss001.ClientQ2827003atgss001;
import es.caib.pinbal.client.recobriment.q2827003atgss001.ClientQ2827003atgss001.SolicitudQ2827003atgss001;
import es.caib.pinbal.client.recobriment.scdcpaju.ClientScdcpaju;
import es.caib.pinbal.client.recobriment.scdcpaju.ClientScdcpaju.SolicitudScdcpaju;
import es.caib.pinbal.client.recobriment.scdhpaju.ClientScdhpaju;
import es.caib.pinbal.client.recobriment.scdhpaju.ClientScdhpaju.SolicitudScdhpaju;
import es.caib.pinbal.client.recobriment.svdccaacpasws01.ClientSvdccaacpasws01;
import es.caib.pinbal.client.recobriment.svdccaacpasws01.ClientSvdccaacpasws01.SolicitudSvdccaacpasws01;
import es.caib.pinbal.client.recobriment.svdccaacpcws01.ClientSvdccaacpcws01;
import es.caib.pinbal.client.recobriment.svdccaacpcws01.ClientSvdccaacpcws01.SolicitudSvdccaacpcws01;
import es.caib.pinbal.client.recobriment.svddelsexws01.ClientSvddelsexws01;
import es.caib.pinbal.client.recobriment.svddelsexws01.ClientSvddelsexws01.SolicitudSvddelsexws01;
import es.caib.pinbal.client.recobriment.svddelsexws01.ClientSvddelsexws01.SolicitudSvddelsexws01.Sexe;
import es.caib.pinbal.client.recobriment.svddgpciws02.ClientSvddgpciws02;
import es.caib.pinbal.client.recobriment.svddgpciws02.ClientSvddgpciws02.SolicitudSvddgpciws02;
import es.caib.pinbal.client.recobriment.svddgpresidencialegaldocws01.ClientSvddgpresidencialegaldocws01;
import es.caib.pinbal.client.recobriment.svddgpresidencialegaldocws01.ClientSvddgpresidencialegaldocws01.SolicitudSvddgpresidencialegaldocws01;
import es.caib.pinbal.client.recobriment.svddgpresidencialegaldocws01.ClientSvddgpresidencialegaldocws01.SolicitudSvddgpresidencialegaldocws01.TipusPassaport;
import es.caib.pinbal.client.recobriment.svddgpviws02.ClientSvddgpviws02;
import es.caib.pinbal.client.recobriment.svddgpviws02.ClientSvddgpviws02.SolicitudSvddgpviws02;
import es.caib.pinbal.client.recobriment.svdscddws01.ClientSvdscddws01;
import es.caib.pinbal.client.recobriment.svdscddws01.ClientSvdscddws01.SolicitudSvdscddws01;
import es.caib.pinbal.client.recobriment.svdsctfnws01.ClientSvdsctfnws01;
import es.caib.pinbal.client.recobriment.svdsctfnws01.ClientSvdsctfnws01.SolicitudSvdsctfnws01;
import es.caib.ripea.core.api.dto.FitxerDto;
import es.caib.ripea.core.api.dto.IntegracioAccioTipusEnumDto;
import es.caib.ripea.core.api.dto.PinbalConsentimentEnumDto;
import es.caib.ripea.core.api.dto.PinbalConsultaDto;
import es.caib.ripea.core.api.dto.PinbalServeiDocPermesEnumDto;
import es.caib.ripea.core.api.dto.SiNoEnumDto;
import es.caib.ripea.core.api.exception.PinbalException;
import es.caib.ripea.core.api.utils.Utils;
import es.caib.ripea.core.entity.EntitatEntity;
import es.caib.ripea.core.entity.ExpedientEntity;
import es.caib.ripea.core.entity.InteressatEntity;
import es.caib.ripea.core.entity.InteressatPersonaFisicaEntity;
import es.caib.ripea.core.entity.InteressatPersonaJuridicaEntity;
import es.caib.ripea.core.entity.MetaDocumentEntity;
import es.caib.ripea.core.entity.MetaExpedientEntity;
import es.caib.ripea.core.entity.OrganGestorEntity;
import es.caib.ripea.core.entity.UsuariEntity;
import lombok.extern.slf4j.Slf4j;

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
	private ConfigHelper configHelper;
	@Resource
	private OrganGestorHelper organGestorHelper;

	/** SVDDGPCIWS02 - Consulta de datos de identidad */
	public String novaPeticioSvddgpciws02(
			ExpedientEntity expedient,
			MetaDocumentEntity metaDocument,
			InteressatEntity interessat,
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
	/** SVDDGPVIWS02 - Verificación de datos de identidad */
	public String novaPeticioSvddgpviws02(
			ExpedientEntity expedient,
			MetaDocumentEntity metaDocument,
			InteressatEntity interessat,
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
	/** SVDCCAACPASWS01 - Estar al corriente de obligaciones tributarias para solicitud de subvenciones y ayudas de la CCAA */
	public String novaPeticioSvdccaacpasws01(
			ExpedientEntity expedient,
			MetaDocumentEntity metaDocument,
			InteressatEntity interessat,
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
	
	/** SVDSCDDWS01 - Servei de consulta de dades de discapacitat */
	public String novaPeticioSvdscddws01(
			ExpedientEntity expedient,
			MetaDocumentEntity metaDocument,
			InteressatEntity interessat,
			String finalitat,
			PinbalConsentimentEnumDto consentiment,
			String comunitatAutonomaCodi,
			String provinciaCodi,
			String dataConsulta,
			String dataNaixement,
			SiNoEnumDto consentimentTipusDiscapacitat) throws PinbalException {
		long t0 = System.currentTimeMillis();
		SolicitudSvdscddws01 solicitud = new SolicitudSvdscddws01();
		emplenarSolicitudBase(
				solicitud,
				expedient,
				metaDocument,
				interessat,
				finalitat,
				consentiment);
		solicitud.setCodigoComunidadAutonoma(comunitatAutonomaCodi);
		solicitud.setCodigoProvincia(provinciaCodi);
		solicitud.setFechaConsulta(dataConsulta);
		solicitud.setFechaNacimiento(dataNaixement);
		solicitud.setConsentimientoTiposDiscapacidad(toSNString(consentimentTipusDiscapacitat));
		try {
			ScspRespuesta respuesta = getClientSvdscddws01().peticionSincrona(Arrays.asList(solicitud));
			return processarScspRespuesta(solicitud, respuesta, "SVDSCDDWS01", t0);
		} catch (Exception ex) {
			throw processarException(solicitud, ex, "SVDSCDDWS01", t0);
		}
	}
	
	
	/** SCDCPAJU - Servei de consulta de padró de convivència */
	public String novaPeticioScdcpaju(
			ExpedientEntity expedient,
			MetaDocumentEntity metaDocument,
			InteressatEntity interessat,
			String finalitat,
			PinbalConsentimentEnumDto consentiment,
			String provinciaCodi,
			String municipiCodi) throws PinbalException {
		long t0 = System.currentTimeMillis();
		SolicitudScdcpaju solicitud = new SolicitudScdcpaju();
		emplenarSolicitudBase(
				solicitud,
				expedient,
				metaDocument,
				interessat,
				finalitat,
				consentiment);
		solicitud.setLlocSolicitud(provinciaCodi, municipiCodi);
		solicitud.setConsultaPerDocumentIdentitat(interessat.getDocumentTipus().toString(), interessat.getDocumentNum(), null);

		try {
			ScspRespuesta respuesta = getClientScdcpaju().peticionSincrona(Arrays.asList(solicitud));
			return processarScspRespuesta(solicitud, respuesta, "SCDCPAJU", t0);
		} catch (Exception ex) {
			throw processarException(solicitud, ex, "SCDCPAJU", t0);
		}
	}
	
	/** SVDSCTFNWS01 - Servei de consulta de família nombrosa */
	public String novaPeticioSvdsctfnws01(
			ExpedientEntity expedient,
			MetaDocumentEntity metaDocument,
			InteressatEntity interessat,
			PinbalConsultaDto pinbalConsulta) throws PinbalException {
		long t0 = System.currentTimeMillis();
		SolicitudSvdsctfnws01 solicitud = new SolicitudSvdsctfnws01();
		emplenarSolicitudBase(
				solicitud,
				expedient,
				metaDocument,
				interessat,
				pinbalConsulta.getFinalitat(),
				pinbalConsulta.getConsentiment());
		
		solicitud.setCodigoComunidadAutonoma(pinbalConsulta.getComunitatAutonomaCodi());
		solicitud.setFechaConsulta(pinbalConsulta.getDataConsulta());
		solicitud.setFechaNacimiento(pinbalConsulta.getDataNaixement());
		solicitud.setNumeroTitulo(pinbalConsulta.getNumeroTitol());

		try {
			ScspRespuesta respuesta = getClientSvdsctfnws01().peticionSincrona(Arrays.asList(solicitud));
			return processarScspRespuesta(solicitud, respuesta, "SVDSCTFNWS01", t0);
		} catch (Exception ex) {
			throw processarException(solicitud, ex, "SVDSCTFNWS01", t0);
		}
	}
	
	/** SVDCCAACPCWS01 - Estar al corriente de obligaciones tributarias para contratación con la CCAA */
	public String novaPeticioSvdccaacpcws01(
			ExpedientEntity expedient,
			MetaDocumentEntity metaDocument,
			InteressatEntity interessat,
			PinbalConsultaDto pinbalConsulta) throws PinbalException {
		long t0 = System.currentTimeMillis();
		SolicitudSvdccaacpcws01 solicitud = new SolicitudSvdccaacpcws01();
		emplenarSolicitudBase(
				solicitud,
				expedient,
				metaDocument,
				interessat,
				pinbalConsulta.getFinalitat(),
				pinbalConsulta.getConsentiment());
		
		solicitud.setCodigoComunidadAutonoma(pinbalConsulta.getComunitatAutonomaCodi());
		solicitud.setCodigoProvincia(pinbalConsulta.getProvinciaCodi());

		try {
			ScspRespuesta respuesta = getClientSvdccaacpcws01().peticionSincrona(Arrays.asList(solicitud));
			return processarScspRespuesta(solicitud, respuesta, "SVDCCAACPCWS01", t0);
		} catch (Exception ex) {
			throw processarException(solicitud, ex, "SVDCCAACPCWS01", t0);
		}
	}
	

	
	/** Q2827003ATGSS001  - Estar al corriente de pago con la Seguridad Social */
	public String novaPeticioQ2827003atgss001(
			ExpedientEntity expedient,
			MetaDocumentEntity metaDocument,
			InteressatEntity interessat,
			PinbalConsultaDto pinbalConsulta) throws PinbalException {
		long t0 = System.currentTimeMillis();
		SolicitudQ2827003atgss001 solicitud = new SolicitudQ2827003atgss001();
		emplenarSolicitudBase(
				solicitud,
				expedient,
				metaDocument,
				interessat,
				pinbalConsulta.getFinalitat(),
				pinbalConsulta.getConsentiment());
		

		try {
			ScspRespuesta respuesta = getClientQ2827003atgss001().peticionSincrona(Arrays.asList(solicitud));
			return processarScspRespuesta(solicitud, respuesta, "Q2827003ATGSS001", t0);
		} catch (Exception ex) {
			throw processarException(solicitud, ex, "Q2827003ATGSS001", t0);
		}
	}
	
	
	/** SVDDELSEXWS01  - Consulta de inexistencia de delitos sexuales por datos de filiación */
	public String novaPeticioSvddelsexws01(
			ExpedientEntity expedient,
			MetaDocumentEntity metaDocument,
			InteressatEntity interessat,
			PinbalConsultaDto pinbalConsulta) throws PinbalException {
		long t0 = System.currentTimeMillis();
		SolicitudSvddelsexws01 solicitud = new SolicitudSvddelsexws01();
		emplenarSolicitudBase(
				solicitud,
				expedient,
				metaDocument,
				interessat,
				pinbalConsulta.getFinalitat(),
				pinbalConsulta.getConsentiment());
		

		solicitud.setNacionalidad(pinbalConsulta.getCodiNacionalitat());
		solicitud.setPaisNacimiento(pinbalConsulta.getPaisNaixament());
		solicitud.setProvinciaNacimiento(pinbalConsulta.getProvinciaNaixament());
		solicitud.setPoblacionNacimiento(pinbalConsulta.getPoblacioNaixament());
		solicitud.setCodPoblacionNacimiento(pinbalConsulta.getCodiPoblacioNaixament());
		solicitud.setSexo(pinbalConsulta.getSexe() != null ? Sexe.valueOf(pinbalConsulta.getSexe().toString()) : null);
		solicitud.setNombrePadre(pinbalConsulta.getNomPare());
		solicitud.setNombreMadre(pinbalConsulta.getNomMare());
		solicitud.setFechaNacimiento(pinbalConsulta.getDataNaixementObligatori());
		solicitud.setTelefono(pinbalConsulta.getTelefon());
		solicitud.setMail(pinbalConsulta.getEmail());
		try {
			ScspRespuesta respuesta = getClientSvddelsexws01().peticionSincrona(Arrays.asList(solicitud));
			return processarScspRespuesta(solicitud, respuesta, "SVDDELSEXWS01", t0);
		} catch (Exception ex) {
			throw processarException(solicitud, ex, "SVDDELSEXWS01", t0);
		}
	}
	
	
	/** SCDHPAJU - Servei de consulta de padró històric */
	public String novaPeticioScdhpaju(
			ExpedientEntity expedient,
			MetaDocumentEntity metaDocument,
			InteressatEntity interessat,
			PinbalConsultaDto pinbalConsulta) throws PinbalException {
		long t0 = System.currentTimeMillis();
		SolicitudScdhpaju solicitud = new SolicitudScdhpaju();
		emplenarSolicitudBase(
				solicitud,
				expedient,
				metaDocument,
				interessat,
				pinbalConsulta.getFinalitat(),
				pinbalConsulta.getConsentiment());
		
		solicitud.setProvinciaSolicitud(pinbalConsulta.getProvinciaCodi());
		solicitud.setMunicipioSolicitud(pinbalConsulta.getMunicipiCodi());
		if (pinbalConsulta.getNombreAnysHistoric() != null) {
			solicitud.setNumeroAnyos(String.valueOf(pinbalConsulta.getNombreAnysHistoric()));
		}
		solicitud.setConsultaPerDocumentIdentitat(
				solicitud.getTitular().getTipoDocumentacion().toString(),
				solicitud.getTitular().getDocumentacion(),
				null);

		try {
			ScspRespuesta respuesta = getClientScdhpaju().peticionSincrona(Arrays.asList(solicitud));
			return processarScspRespuesta(solicitud, respuesta, "SCDHPAJU", t0);
		} catch (Exception ex) {
			throw processarException(solicitud, ex, "SCDHPAJU", t0);
		}
	}
	
	/** NIVRENTI - Consulta del nivel de renta */
	public String novaPeticioNivrenti(
			ExpedientEntity expedient,
			MetaDocumentEntity metaDocument,
			InteressatEntity interessat,
			PinbalConsultaDto pinbalConsulta) throws PinbalException {
		long t0 = System.currentTimeMillis();
		SolicitudNivrenti solicitud = new SolicitudNivrenti();
		emplenarSolicitudBase(
				solicitud,
				expedient,
				metaDocument,
				interessat,
				pinbalConsulta.getFinalitat(),
				pinbalConsulta.getConsentiment());

		solicitud.setEjercicio(pinbalConsulta.getExercici());

		try {
			ScspRespuesta respuesta = getClientNivrenti().peticionSincrona(Arrays.asList(solicitud));
			return processarScspRespuesta(solicitud, respuesta, "SCDHPAJU", t0);
		} catch (Exception ex) {
			throw processarException(solicitud, ex, "SCDHPAJU", t0);
		}
	}
	
	/** ECOT103 - Estar al corriente de obligaciones tributarias para solicitud de subvenciones y ayudas con indicación de incumplimientos */
	public String novaPeticioEcot103(
			ExpedientEntity expedient,
			MetaDocumentEntity metaDocument,
			InteressatEntity interessat,
			PinbalConsultaDto pinbalConsulta) throws PinbalException {
		long t0 = System.currentTimeMillis();
		SolicitudEcot103 solicitud = new SolicitudEcot103();
		emplenarSolicitudBase(
				solicitud,
				expedient,
				metaDocument,
				interessat,
				pinbalConsulta.getFinalitat(),
				pinbalConsulta.getConsentiment());


		try {
			ScspRespuesta respuesta = getClientEcot103().peticionSincrona(Arrays.asList(solicitud));
			return processarScspRespuesta(solicitud, respuesta, "ECOT103", t0);
		} catch (Exception ex) {
			throw processarException(solicitud, ex, "ECOT103", t0);
		}
	}
	
	/** SVDDGPRESIDENCIALEGALDOCWS01 - Servei de consulta de dades de residència legal d'estrangers per documentació */
	public String novaPeticioSvddgpresidencialegaldocws01(
			ExpedientEntity expedient,
			MetaDocumentEntity metaDocument,
			InteressatEntity interessat,
			PinbalConsultaDto pinbalConsulta) throws PinbalException {
		long t0 = System.currentTimeMillis();
		SolicitudSvddgpresidencialegaldocws01 solicitud = new SolicitudSvddgpresidencialegaldocws01();
		emplenarSolicitudBase(
				solicitud,
				expedient,
				metaDocument,
				interessat,
				pinbalConsulta.getFinalitat(),
				pinbalConsulta.getConsentiment());

		solicitud.setNumeroSoporte(pinbalConsulta.getNumeroSoporte());
		solicitud.setTipo(Utils.convertEnum(pinbalConsulta.getTipusPassaport(), TipusPassaport.class));
		solicitud.setFechaCaducidad(pinbalConsulta.getFechaCaducidad());
		solicitud.setNacionalidad(pinbalConsulta.getCodiNacionalitat2());
		solicitud.setFechaExpedicion(pinbalConsulta.getFechaExpedicion());
		
		try {
			ScspRespuesta respuesta = getClientSvddgpresidencialegaldocws01().peticionSincrona(Arrays.asList(solicitud));
			return processarScspRespuesta(solicitud, respuesta, "SVDDGPRESIDENCIALEGALDOCWS01", t0);
		} catch (Exception ex) {
			throw processarException(solicitud, ex, "SVDDGPRESIDENCIALEGALDOCWS01", t0);
		}
	}
	
	
	
	private String toSNString(SiNoEnumDto consentimentTipusDiscapacitat) {
		String sn = null;
		if (consentimentTipusDiscapacitat != null) {
			if (consentimentTipusDiscapacitat == SiNoEnumDto.SI) {
				sn = "S";
			} else if (consentimentTipusDiscapacitat == SiNoEnumDto.NO) {
				sn = "N";
			}
		}
		return sn;
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
					errorDescripcio,
					null);
			throw new PinbalException(ex, "getJustificante");
		}
	}

	private void emplenarSolicitudBase(
			SolicitudBase solicitud,
			ExpedientEntity expedient,
			MetaDocumentEntity metaDocument,
			InteressatEntity interessat,
			String finalitat,
			PinbalConsentimentEnumDto consentiment) {
		EntitatEntity entitat = metaDocument.getEntitat();
		MetaExpedientEntity metaExpedient = metaDocument.getMetaExpedient();
		String codiSia = getPinbalDefaultSia();

		String identificadorSolicitante;
		String nombreSolicitante;
		OrganGestorEntity organGestorCIF = null;
		if (metaDocument.isPinbalUtilitzarCifOrgan()) {
			List<OrganGestorEntity> organsAmbPares = organGestorHelper.findPares(expedient.getOrganGestor(), true);
			
			for (OrganGestorEntity organ : organsAmbPares) {
				if (organ.isUtilitzarCifPinbal() && Utils.isNotEmpty(organ.getCif())) {
					organGestorCIF = organ;
					break;
				}
			}
		}
		if (organGestorCIF != null) {
			identificadorSolicitante = organGestorCIF.getCif();
			nombreSolicitante = organGestorCIF.getNom();
		} else {
			identificadorSolicitante = entitat.getCif();
			nombreSolicitante = entitat.getNom();
		}
		
		solicitud.setIdentificadorSolicitante(identificadorSolicitante);
		solicitud.setNombreSolicitante(nombreSolicitante);
		
		solicitud.setCodigoProcedimiento((codiSia != null && !codiSia.trim().isEmpty()) ? codiSia : metaExpedient.getClassificacioSia());
		solicitud.setUnidadTramitadora(expedient.getOrganGestor().getNom());
		
		// If there is character: ’ in finalitat field, some of the services (for example: SVDCCAACPASWS01, SVDCCAACPCWS01) return an error:
		// [0227] S'ha produït un error al intentar generar la resposta a la consulta: Error processant petició síncrona: SoapFault: System Error.|||El servidor ha devuelto un mensaje SOAP Fault. Error al generar la respuesta. BackofficeException: Error processant petició síncrona: SoapFault: System Error
		// if ’ (UNICODE: U+2019) is replaced by ' (UNICODE: U+0027) the problem doesn't happen
		finalitat = finalitat.replace("’", "'"); // 
		solicitud.setFinalidad(finalitat);
		switch (consentiment) {
		case LLEI:
			solicitud.setConsentimiento(ScspConsentimiento.Ley);
			break;
		case SI:
			solicitud.setConsentimiento(ScspConsentimiento.Si);
			break;
		}
		solicitud.setIdExpediente(expedient.getNumero());
		solicitud.setFuncionario(getFuncionariActual());
		solicitud.setTitular(getTitularFromInteressat(interessat, false, metaDocument.getPinbalServeiDocsPermesos()));
	}

	private ScspFuncionario getFuncionariActual() {
		ScspFuncionario funcionario = new ScspFuncionario();
		UsuariEntity funcionari = usuariHelper.getUsuariAutenticat();
		funcionario.setNifFuncionario(funcionari.getNif());
		funcionario.setNombreCompletoFuncionario(funcionari.getNom());
		return funcionario;
	}

	private ScspTitular getTitularFromInteressat(
			InteressatEntity interessat,
			boolean ambNomSencer,
			List<PinbalServeiDocPermesEnumDto> pinbalServeiDocsPermesos) {
		ScspTitular titular = new ScspTitular();

		titular.setDocumentacion(interessat.getDocumentNum());
		
		if (interessat instanceof InteressatPersonaFisicaEntity) {
			
			switch (interessat.getDocumentTipus()) {
			case DOCUMENT_IDENTIFICATIU_ESTRANGERS:
				titular.setTipoDocumentacion(ScspTipoDocumentacion.NIE);
				break;
			case NIF:
				if (CollectionUtils.isEmpty(pinbalServeiDocsPermesos) || pinbalServeiDocsPermesos.contains(PinbalServeiDocPermesEnumDto.DNI)) {
					titular.setTipoDocumentacion(ScspTipoDocumentacion.DNI);
				} else {
					titular.setTipoDocumentacion(ScspTipoDocumentacion.NIF);
				}
				break;
			case PASSAPORT:
				titular.setTipoDocumentacion(ScspTipoDocumentacion.Pasaporte);
				break;
			default:
				titular.setTipoDocumentacion(ScspTipoDocumentacion.Otros);
			}
			
			InteressatPersonaFisicaEntity interessatPersonaFisica = (InteressatPersonaFisicaEntity) interessat;
			titular.setNombre(interessatPersonaFisica.getNom());
			titular.setApellido1(interessatPersonaFisica.getLlinatge1());
			titular.setApellido2(interessatPersonaFisica.getLlinatge2());
			if (ambNomSencer) {
				StringBuilder nomSencer = new StringBuilder(interessatPersonaFisica.getNom());
				if (interessatPersonaFisica.getLlinatge1() != null) {
					nomSencer.append(" ");
					nomSencer.append(interessatPersonaFisica.getLlinatge1().trim());
				}
				if (interessatPersonaFisica.getLlinatge2() != null) {
					nomSencer.append(" ");
					nomSencer.append(interessatPersonaFisica.getLlinatge2().trim());
				}
				titular.setNombreCompleto(nomSencer.toString());
			}
		} else if (interessat instanceof InteressatPersonaJuridicaEntity) {
			InteressatPersonaJuridicaEntity interessatPersonaJuridica = (InteressatPersonaJuridicaEntity) interessat;
			
			if (CollectionUtils.isEmpty(pinbalServeiDocsPermesos) || pinbalServeiDocsPermesos.contains(PinbalServeiDocPermesEnumDto.CIF)) {
				titular.setTipoDocumentacion(ScspTipoDocumentacion.CIF);
			} else {
				titular.setTipoDocumentacion(ScspTipoDocumentacion.NIF);
			}
			titular.setNombreCompleto(interessatPersonaJuridica.getRaoSocial());
			titular.setDocumentacion(interessatPersonaJuridica.getDocumentNum());
			
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
					errorDescripcio,
					null);
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
				ex,
				"peticionSincrona");
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
	
	private ClientSvdscddws01 getClientSvdscddws01() {
		ClientSvdscddws01 clientSvdscddws01 = new ClientSvdscddws01(
				getPinbalBaseUrl(),
				getPinbalUser(),
				getPinbalPassword(),
				getPinbalBasicAuth(),
				null,
				null);
		if (log.isDebugEnabled())
			clientSvdscddws01.enableLogginFilter();
		return clientSvdscddws01;
	}
	
	private ClientScdcpaju getClientScdcpaju() {
		ClientScdcpaju clientScdcpaju = new ClientScdcpaju(
				getPinbalBaseUrl(),
				getPinbalUser(),
				getPinbalPassword(),
				getPinbalBasicAuth(),
				null,
				null);
		if (log.isDebugEnabled())
			clientScdcpaju.enableLogginFilter();
		return clientScdcpaju;
	}
	
	
	private ClientSvdsctfnws01 getClientSvdsctfnws01() {
		ClientSvdsctfnws01 clientSvdsctfnws01 = new ClientSvdsctfnws01(
				getPinbalBaseUrl(),
				getPinbalUser(),
				getPinbalPassword(),
				getPinbalBasicAuth(),
				null,
				null);
		if (log.isDebugEnabled())
			clientSvdsctfnws01.enableLogginFilter();
		return clientSvdsctfnws01;
	}
	
	
	private ClientSvdccaacpcws01 getClientSvdccaacpcws01() {
		ClientSvdccaacpcws01 client = new ClientSvdccaacpcws01(
				getPinbalBaseUrl(),
				getPinbalUser(),
				getPinbalPassword(),
				getPinbalBasicAuth(),
				null,
				null);
		if (log.isDebugEnabled())
			client.enableLogginFilter();
		return client;
	}

	private ClientQ2827003atgss001 getClientQ2827003atgss001() {
		ClientQ2827003atgss001 client = new ClientQ2827003atgss001(
				getPinbalBaseUrl(),
				getPinbalUser(),
				getPinbalPassword(),
				getPinbalBasicAuth(),
				null,
				null);
		if (log.isDebugEnabled())
			client.enableLogginFilter();
		return client;
	}

	private ClientSvddelsexws01 getClientSvddelsexws01() {
		ClientSvddelsexws01 client = new ClientSvddelsexws01(
				getPinbalBaseUrl(),
				getPinbalUser(),
				getPinbalPassword(),
				getPinbalBasicAuth(),
				null,
				null);
		if (log.isDebugEnabled())
			client.enableLogginFilter();
		return client;
	}
	
	private ClientScdhpaju getClientScdhpaju() {
		ClientScdhpaju client = new ClientScdhpaju(
				getPinbalBaseUrl(),
				getPinbalUser(),
				getPinbalPassword(),
				getPinbalBasicAuth(),
				null,
				null);
		if (log.isDebugEnabled())
			client.enableLogginFilter();
		return client;
	}
	
	private ClientNivrenti getClientNivrenti() {
		ClientNivrenti client = new ClientNivrenti(
				getPinbalBaseUrl(),
				getPinbalUser(),
				getPinbalPassword(),
				getPinbalBasicAuth(),
				null,
				null);
		if (log.isDebugEnabled())
			client.enableLogginFilter();
		return client;
	}
	
	private ClientEcot103 getClientEcot103() {
		ClientEcot103 client = new ClientEcot103(
				getPinbalBaseUrl(),
				getPinbalUser(),
				getPinbalPassword(),
				getPinbalBasicAuth(),
				null,
				null);
		if (log.isDebugEnabled())
			client.enableLogginFilter();
		return client;
	}
	
	private ClientSvddgpresidencialegaldocws01 getClientSvddgpresidencialegaldocws01() {
		ClientSvddgpresidencialegaldocws01 client = new ClientSvddgpresidencialegaldocws01(
				getPinbalBaseUrl(),
				getPinbalUser(),
				getPinbalPassword(),
				getPinbalBasicAuth(),
				null,
				null);
		if (log.isDebugEnabled())
			client.enableLogginFilter();
		return client;
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
