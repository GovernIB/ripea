package es.caib.ripea.service.helper;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import es.caib.pinbal.client.recobriment.model.SolicitudBaseSvdrrcc;
import es.caib.pinbal.client.recobriment.model.SolicitudBaseSvdrrcc.FetRegistral;
import es.caib.pinbal.client.recobriment.model.SolicitudBaseSvdrrcc.Lloc;
import es.caib.pinbal.client.recobriment.model.SolicitudBaseSvdrrcc.TitularDadesAdicionals;
import es.caib.pinbal.client.recobriment.nivrenti.ClientNivrenti;
import es.caib.pinbal.client.recobriment.nivrenti.ClientNivrenti.SolicitudNivrenti;
import es.caib.pinbal.client.recobriment.q2827003atgss001.ClientQ2827003atgss001;
import es.caib.pinbal.client.recobriment.q2827003atgss001.ClientQ2827003atgss001.SolicitudQ2827003atgss001;
import es.caib.pinbal.client.recobriment.scdcpaju.ClientScdcpaju;
import es.caib.pinbal.client.recobriment.scdcpaju.ClientScdcpaju.SolicitudScdcpaju;
import es.caib.pinbal.client.recobriment.scdhpaju.ClientScdhpaju;
import es.caib.pinbal.client.recobriment.scdhpaju.ClientScdhpaju.SolicitudScdhpaju;
import es.caib.pinbal.client.recobriment.svdbecaws01.ClientSvdbecaws01;
import es.caib.pinbal.client.recobriment.svdbecaws01.ClientSvdbecaws01.SolicitudSvdbecaws01;
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
import es.caib.pinbal.client.recobriment.svdrrccdefuncionws01.ClientSvdrrccdefuncionws01;
import es.caib.pinbal.client.recobriment.svdrrccdefuncionws01.ClientSvdrrccdefuncionws01.SolicitudSvdrrccdefuncionws01;
import es.caib.pinbal.client.recobriment.svdrrccmatrimoniows01.ClientSvdrrccmatrimoniows01;
import es.caib.pinbal.client.recobriment.svdrrccmatrimoniows01.ClientSvdrrccmatrimoniows01.SolicitudSvdrrccmatrimoniows01;
import es.caib.pinbal.client.recobriment.svdrrccnacimientows01.ClientSvdrrccnacimientows01;
import es.caib.pinbal.client.recobriment.svdrrccnacimientows01.ClientSvdrrccnacimientows01.SolicitudSvdrrccnacimientows01;
import es.caib.pinbal.client.recobriment.svdscddws01.ClientSvdscddws01;
import es.caib.pinbal.client.recobriment.svdscddws01.ClientSvdscddws01.SolicitudSvdscddws01;
import es.caib.pinbal.client.recobriment.svdsctfnws01.ClientSvdsctfnws01;
import es.caib.pinbal.client.recobriment.svdsctfnws01.ClientSvdsctfnws01.SolicitudSvdsctfnws01;
import es.caib.ripea.persistence.entity.EntitatEntity;
import es.caib.ripea.persistence.entity.ExpedientEntity;
import es.caib.ripea.persistence.entity.InteressatEntity;
import es.caib.ripea.persistence.entity.InteressatPersonaFisicaEntity;
import es.caib.ripea.persistence.entity.InteressatPersonaJuridicaEntity;
import es.caib.ripea.persistence.entity.MetaDocumentEntity;
import es.caib.ripea.persistence.entity.MetaExpedientEntity;
import es.caib.ripea.persistence.entity.OrganGestorEntity;
import es.caib.ripea.persistence.entity.PinbalServeiEntity;
import es.caib.ripea.persistence.entity.UsuariEntity;
import es.caib.ripea.service.intf.config.PropertyConfig;
import es.caib.ripea.service.intf.dto.DiagnosticFiltreDto;
import es.caib.ripea.service.intf.dto.FitxerDto;
import es.caib.ripea.service.intf.dto.IntegracioAccioTipusEnumDto;
import es.caib.ripea.service.intf.dto.PinbalConsentimentEnumDto;
import es.caib.ripea.service.intf.dto.PinbalConsultaDto;
import es.caib.ripea.service.intf.dto.PinbalServeiDocPermesEnumDto;
import es.caib.ripea.service.intf.dto.SiNoEnumDto;
import es.caib.ripea.service.intf.exception.PinbalException;
import es.caib.ripea.service.intf.utils.Utils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class PinbalHelper {

	@Autowired private UsuariHelper usuariHelper;
	@Autowired private IntegracioHelper integracioHelper;
	@Autowired private ConfigHelper configHelper;
	@Autowired private OrganGestorHelper organGestorHelper;

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
		
		if (pinbalConsulta.getPaisNaixament().equals("724")) {
			solicitud.setCodPoblacionNacimiento(pinbalConsulta.getProvinciaNaixament() + pinbalConsulta.getMunicipiNaixamentSVDDELSEXWS01());
		} else {
			solicitud.setCodPoblacionNacimiento(pinbalConsulta.getCodiPoblacioNaixament());
		}

		solicitud.setSexo(pinbalConsulta.getSexe() != null ? Sexe.valueOf(pinbalConsulta.getSexe().toString()) : null);
		solicitud.setNombrePadre(pinbalConsulta.getNomPare());
		solicitud.setNombreMadre(pinbalConsulta.getNomMare());
		solicitud.setFechaNacimiento(pinbalConsulta.getDataNaixement());
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
		solicitud.setFechaCaducidad(pinbalConsulta.getDataCaducidad());
		solicitud.setNacionalidad(pinbalConsulta.getCodiNacionalitat());
		solicitud.setFechaExpedicion(pinbalConsulta.getDataExpedicion());
		
		try {
			ScspRespuesta respuesta = getClientSvddgpresidencialegaldocws01().peticionSincrona(Arrays.asList(solicitud));
			return processarScspRespuesta(solicitud, respuesta, "SVDDGPRESIDENCIALEGALDOCWS01", t0);
		} catch (Exception ex) {
			throw processarException(solicitud, ex, "SVDDGPRESIDENCIALEGALDOCWS01", t0);
		}
	}
	
	/** SVDRRCCNACIMIENTOWS01 - Servei de consulta de naixement */
	public String novaPeticioSvdrrccnacimientows01(
			ExpedientEntity expedient,
			MetaDocumentEntity metaDocument,
			InteressatEntity interessat,
			PinbalConsultaDto pinbalConsulta) throws PinbalException {
		long t0 = System.currentTimeMillis();
		SolicitudSvdrrccnacimientows01 solicitud = new SolicitudSvdrrccnacimientows01();
		emplenarSolicitudBase(
				solicitud,
				expedient,
				metaDocument,
				interessat,
				pinbalConsulta.getFinalitat(),
				pinbalConsulta.getConsentiment());
		
		
        solicitud.setDadesRegistrals(SolicitudBaseSvdrrcc.DadesRegistrals.builder()
                .registreCivil(pinbalConsulta.getRegistreCivil())
                .tom(pinbalConsulta.getTom())
                .pagina(pinbalConsulta.getPagina())
                .build());
        

        solicitud.setTitularDadesAdicionals(TitularDadesAdicionals.builder()
                .fetregistral(FetRegistral.builder()
                        .data(pinbalConsulta.getDataRegistre())
                        .municipi(Lloc.builder()
                                .codi(Utils.isNotEmpty(pinbalConsulta.getMunicipiRegistreSVDRRCCNACIMIENTOWS01()) ? "07" + pinbalConsulta.getMunicipiRegistreSVDRRCCNACIMIENTOWS01() : null)
                                .build())
                        .build())
                .naixement(SolicitudBaseSvdrrcc.Naixement.builder()
                        .data(Utils.convertStringToDate(pinbalConsulta.getDataNaixement(), "dd/MM/yyyy"))
                        .municipi(Lloc.builder()
                                .codi(Utils.isNotEmpty(pinbalConsulta.getMunicipiNaixamentSVDRRCCNACIMIENTOWS01()) ? "07" + pinbalConsulta.getMunicipiNaixamentSVDRRCCNACIMIENTOWS01() : null)
                                .build())
                        .build())
                .ausenciaSegonLlinatge(pinbalConsulta.isAusenciaSegundoApellido())
                .nomMare(pinbalConsulta.getNomMare())
                .nomPare(pinbalConsulta.getNomPare())
                .sexe(Utils.convertEnum(pinbalConsulta.getSexe(), es.caib.pinbal.client.recobriment.model.Sexe.class))
                .build());
        
		
		try {
			ScspRespuesta respuesta = getClientSvdrrccnacimientows01().peticionSincrona(Arrays.asList(solicitud));
			return processarScspRespuesta(solicitud, respuesta, "SSVDRRCCNACIMIENTOWS01", t0);
		} catch (Exception ex) {
			throw processarException(solicitud, ex, "SSVDRRCCNACIMIENTOWS01", t0);
		}
	}
	
	
	/** SVDRRCCMATRIMONIOWS01 - Servei de consulta de matrimoni  */
	public String novaPeticioSvdrrccmatrimoniows01(
			ExpedientEntity expedient,
			MetaDocumentEntity metaDocument,
			InteressatEntity interessat,
			PinbalConsultaDto pinbalConsulta) throws PinbalException {
		long t0 = System.currentTimeMillis();
		SolicitudSvdrrccmatrimoniows01 solicitud = new SolicitudSvdrrccmatrimoniows01();
		emplenarSolicitudBase(
				solicitud,
				expedient,
				metaDocument,
				interessat,
				pinbalConsulta.getFinalitat(),
				pinbalConsulta.getConsentiment());
		
        solicitud.setDadesRegistrals(SolicitudBaseSvdrrcc.DadesRegistrals.builder()
                .registreCivil(pinbalConsulta.getRegistreCivil())
                .tom(pinbalConsulta.getTom())
                .pagina(pinbalConsulta.getPagina())
                .build());
        

        solicitud.setTitularDadesAdicionals(TitularDadesAdicionals.builder()
                .fetregistral(FetRegistral.builder()
                        .data(pinbalConsulta.getDataRegistre())
                        .municipi(Lloc.builder()
                                .codi(Utils.isNotEmpty(pinbalConsulta.getMunicipiRegistreSVDRRCCMATRIMONIOWS01()) ? "07" + pinbalConsulta.getMunicipiRegistreSVDRRCCMATRIMONIOWS01() : null)
                                .build())
                        .build())
                .naixement(SolicitudBaseSvdrrcc.Naixement.builder()
                        .data(Utils.convertStringToDate(pinbalConsulta.getDataNaixement(), "dd/MM/yyyy"))
                        .municipi(Lloc.builder()
                                .codi(Utils.isNotEmpty(pinbalConsulta.getMunicipiNaixamentSVDRRCCMATRIMONIOWS01()) ? "07" + pinbalConsulta.getMunicipiNaixamentSVDRRCCMATRIMONIOWS01() : null)
                                .build())
                        .build())
                .ausenciaSegonLlinatge(pinbalConsulta.isAusenciaSegundoApellido())
                .nomMare(pinbalConsulta.getNomMare())
                .nomPare(pinbalConsulta.getNomPare())
                .sexe(Utils.convertEnum(pinbalConsulta.getSexe(), es.caib.pinbal.client.recobriment.model.Sexe.class))
                .build());

        
		try {
			ScspRespuesta respuesta = getClientSvdrrccmatrimoniows01().peticionSincrona(Arrays.asList(solicitud));
			return processarScspRespuesta(solicitud, respuesta, "SVDRRCCMATRIMONIOWS01", t0);
		} catch (Exception ex) {
			throw processarException(solicitud, ex, "SVDRRCCMATRIMONIOWS01", t0);
		}
	}
	
	
	/** SVDRRCCDEFUNCIONWS01 - Servei de consulta de defunció */
	public String novaPeticioSvdrrccdefuncionws01(
			ExpedientEntity expedient,
			MetaDocumentEntity metaDocument,
			InteressatEntity interessat,
			PinbalConsultaDto pinbalConsulta) throws PinbalException {
		long t0 = System.currentTimeMillis();
		SolicitudSvdrrccdefuncionws01 solicitud = new SolicitudSvdrrccdefuncionws01();
		emplenarSolicitudBase(
				solicitud,
				expedient,
				metaDocument,
				interessat,
				pinbalConsulta.getFinalitat(),
				pinbalConsulta.getConsentiment());
		
		
        solicitud.setDadesRegistrals(SolicitudBaseSvdrrcc.DadesRegistrals.builder()
                .registreCivil(pinbalConsulta.getRegistreCivil())
                .tom(pinbalConsulta.getTom())
                .pagina(pinbalConsulta.getPagina())
                .build());
        

        solicitud.setTitularDadesAdicionals(TitularDadesAdicionals.builder()
                .fetregistral(FetRegistral.builder()
                        .data(pinbalConsulta.getDataRegistre())
                        .municipi(Lloc.builder()
                                .codi(Utils.isNotEmpty(pinbalConsulta.getMunicipiRegistreSVDRRCCDEFUNCIONWS01()) ? "07" + pinbalConsulta.getMunicipiRegistreSVDRRCCDEFUNCIONWS01() : null)
                                .build())
                        .build())
                .naixement(SolicitudBaseSvdrrcc.Naixement.builder()
                        .data(Utils.convertStringToDate(pinbalConsulta.getDataNaixement(), "dd/MM/yyyy"))
                        .municipi(Lloc.builder()
                                .codi(Utils.isNotEmpty(pinbalConsulta.getMunicipiNaixamentSVDRRCCDEFUNCIONWS01()) ? "07" + pinbalConsulta.getMunicipiNaixamentSVDRRCCDEFUNCIONWS01() : null)
                                .build())
                        .build())
                .ausenciaSegonLlinatge(pinbalConsulta.isAusenciaSegundoApellido())
                .nomMare(pinbalConsulta.getNomMare())
                .nomPare(pinbalConsulta.getNomPare())
                .sexe(Utils.convertEnum(pinbalConsulta.getSexe(), es.caib.pinbal.client.recobriment.model.Sexe.class))
                .build());
        
		try {
			ScspRespuesta respuesta = getClientSvdrrccdefuncionws01().peticionSincrona(Arrays.asList(solicitud));
			return processarScspRespuesta(solicitud, respuesta, "SVDRRCCDEFUNCIONWS01", t0);
		} catch (Exception ex) {
			throw processarException(solicitud, ex, "SVDRRCCDEFUNCIONWS01", t0);
		}
	}
	
	/** SVDBECAWS01 - Servei de consulta de condició de becat */
	public String novaPeticioSvdbecaws01(
			ExpedientEntity expedient,
			MetaDocumentEntity metaDocument,
			InteressatEntity interessat,
			PinbalConsultaDto pinbalConsulta) throws PinbalException {
		long t0 = System.currentTimeMillis();
		SolicitudSvdbecaws01 solicitud = new SolicitudSvdbecaws01();
		emplenarSolicitudBase(
				solicitud,
				expedient,
				metaDocument,
				interessat,
				pinbalConsulta.getFinalitat(),
				pinbalConsulta.getConsentiment());
		
		solicitud.setCurso(pinbalConsulta.getCurs().toString());
        
		try {
			ScspRespuesta respuesta = getClientSvdbecaws01().peticionSincrona(Arrays.asList(solicitud));
			return processarScspRespuesta(solicitud, respuesta, "SVDRRCSVDBECAWS01CDEFUNCIONWS01", t0);
		} catch (Exception ex) {
			throw processarException(solicitud, ex, "SVDBECAWS01", t0);
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

	public String pinbalDiagnostic(DiagnosticFiltreDto filtre) {
		try {
			Map<Integer, String> resultat = Utils.peticioRest(
					getPinbalBaseUrl(filtre.getEntitatCodi(), filtre.getOrganCodi())+"/interna/recobriment/test",
					getPinbalUser(filtre.getEntitatCodi(), filtre.getOrganCodi()),
					getPinbalPassword(filtre.getEntitatCodi(), filtre.getOrganCodi()));
			if (resultat!=null && resultat.get(200)!=null) {
				return null;
			} else {
				return "La resposta del mètode test no ha estat l'esperada: "+resultat.toString();
			}
		} catch (Exception ex) {
			return ex.getMessage();
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
					getPinbalEndpointName(),
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
					getPinbalEndpointName(),
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
		
		//Per proves en local cap a SE, utilitzar: S0711001H - Govern de les Illes Balears
		if (organGestorCIF != null) {
			identificadorSolicitante = organGestorCIF.getCif();
			nombreSolicitante = organGestorCIF.getNom();
		} else {
			identificadorSolicitante = entitat.getCif();
			nombreSolicitante = entitat.getNom();
		}
		
		solicitud.setIdentificadorSolicitante(identificadorSolicitante);
		solicitud.setNombreSolicitante(nombreSolicitante);
		
		solicitud.setCodigoProcedimiento((codiSia != null && !codiSia.trim().isEmpty()) ? codiSia : metaExpedient.getClassificacio());
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
		solicitud.setTitular(getTitularFromInteressat(interessat, false, metaDocument.getPinbalServei()));
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
			PinbalServeiEntity pinbalServei) {
		
		List<PinbalServeiDocPermesEnumDto> pinbalServeiDocsPermesos = pinbalServei.getPinbalServeiDocsPermesos();
		
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
					getPinbalEndpointName(),
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
			return respuesta.getAtributos().getIdPeticion();
		} else {
			String errorDescripcio = "[" + respuesta.getAtributos().getEstado().getCodigoEstado() + "] " + respuesta.getAtributos().getEstado().getLiteralError();
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_PINBAL,
					accioDescripcio,
					getPinbalEndpointName(),
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
				getPinbalEndpointName(),
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
		String  baseURL = getPinbalBaseUrl();
		String  baseUsr = getPinbalUser();
		String  basePas = getPinbalPassword();
		boolean baseAut = getPinbalBasicAuth();
		ClientSvddgpciws02 clientSvddgpciws02 = new ClientSvddgpciws02(baseURL, baseUsr, basePas, baseAut, null, null);
		if (log.isDebugEnabled()) clientSvddgpciws02.enableLogginFilter();
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
	
	private ClientSvdrrccnacimientows01 getClientSvdrrccnacimientows01() {
		ClientSvdrrccnacimientows01 client = new ClientSvdrrccnacimientows01(
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
	
	private ClientSvdrrccmatrimoniows01 getClientSvdrrccmatrimoniows01() {
		ClientSvdrrccmatrimoniows01 client = new ClientSvdrrccmatrimoniows01(
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
	
	private ClientSvdrrccdefuncionws01 getClientSvdrrccdefuncionws01() {
		ClientSvdrrccdefuncionws01 client = new ClientSvdrrccdefuncionws01(
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
	
	private ClientSvdbecaws01 getClientSvdbecaws01() {
		ClientSvdbecaws01 client = new ClientSvdbecaws01(
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
		return configHelper.getConfig(PropertyConfig.PINBAL_BASE_URL);
	}
	private String getPinbalBaseUrl(String entitatCodi, String organCodi) {
		return configHelper.getConfig(PropertyConfig.PINBAL_BASE_URL, entitatCodi, organCodi);
	}
	private String getPinbalEndpointName() {
		String resultat = configHelper.getConfig(PropertyConfig.PINBAL_ENDPOINT_DESC);
		if (Utils.isEmpty(resultat)) {
			resultat = configHelper.getConfig(PropertyConfig.PINBAL_BASE_URL);
		}
		return resultat;
	}
	private String getPinbalUser() {
		return configHelper.getConfig(PropertyConfig.PINBAL_USER);
	}
	private String getPinbalPassword() {
		return configHelper.getConfig(PropertyConfig.PINBAL_PASS);
	}
	private String getPinbalUser(String entitatCodi, String organCodi) {
		return configHelper.getConfig(PropertyConfig.PINBAL_USER, entitatCodi, organCodi);
	}
	private String getPinbalPassword(String entitatCodi, String organCodi) {
		return configHelper.getConfig(PropertyConfig.PINBAL_PASS, entitatCodi, organCodi);
	}	
	private boolean getPinbalBasicAuth() {
		return configHelper.getAsBoolean(PropertyConfig.PINBAL_BASIC_AUTH);
	}
	private String getPinbalDefaultSia() {
		return configHelper.getConfig(PropertyConfig.PINBAL_DEFAULT_SIA);
	}

}
