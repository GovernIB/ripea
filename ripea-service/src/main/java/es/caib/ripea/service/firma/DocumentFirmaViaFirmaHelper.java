package es.caib.ripea.service.firma;

import java.net.URL;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.caib.ripea.persistence.entity.DocumentEntity;
import es.caib.ripea.persistence.entity.DocumentViaFirmaEntity;
import es.caib.ripea.persistence.repository.DocumentViaFirmaRepository;
import es.caib.ripea.plugin.viafirma.ViaFirmaDocument;
import es.caib.ripea.service.helper.AlertaHelper;
import es.caib.ripea.service.helper.CacheHelper;
import es.caib.ripea.service.helper.ContingutHelper;
import es.caib.ripea.service.helper.ContingutLogHelper;
import es.caib.ripea.service.helper.DocumentHelper;
import es.caib.ripea.service.helper.EmailHelper;
import es.caib.ripea.service.helper.IntegracioHelper;
import es.caib.ripea.service.helper.OrganGestorHelper;
import es.caib.ripea.service.helper.PluginHelper;
import es.caib.ripea.service.intf.dto.ArxiuEstatEnumDto;
import es.caib.ripea.service.intf.dto.ArxiuFirmaDto;
import es.caib.ripea.service.intf.dto.DocumentEnviamentEstatEnumDto;
import es.caib.ripea.service.intf.dto.DocumentEstatEnumDto;
import es.caib.ripea.service.intf.dto.DocumentFirmaTipusEnumDto;
import es.caib.ripea.service.intf.dto.LogTipusEnumDto;
import es.caib.ripea.service.intf.dto.ViaFirmaCallbackEstatEnumDto;
import es.caib.ripea.service.intf.exception.SistemaExternException;

@Component
public class DocumentFirmaViaFirmaHelper extends DocumentFirmaHelper{

	@Autowired
	private CacheHelper cacheHelper;
	@Autowired
	private ContingutLogHelper contingutLogHelper;
	@Autowired
	private PluginHelper pluginHelper;
	@Autowired
	private DocumentHelper documentHelper;
	@Autowired
	private DocumentViaFirmaRepository documentViaFirmaRepository;
	@Autowired
	private AlertaHelper alertaHelper;
	@Autowired
	private EmailHelper emailHelper;
	@Autowired
	private OrganGestorHelper organGestorHelper;
	
	@Autowired
	private ContingutHelper contingutHelper;

	public void viaFirmaEnviar(DocumentViaFirmaEntity documentViaFirma) throws SistemaExternException {
		DocumentEntity document = documentViaFirma.getDocument();
		try {
			String messageCode = pluginHelper.viaFirmaUpload(
					document,
					documentViaFirma);
			documentViaFirma.updateEnviat(
					new Date(),
					messageCode);
			cacheHelper.evictEnviamentsPortafirmesPendentsPerExpedient(document.getExpedient());
		} catch (Exception ex) {
			Throwable rootCause = ExceptionUtils.getRootCause(ex);
			if (rootCause == null) rootCause = ex;
			throw new SistemaExternException(
					IntegracioHelper.INTCODI_VIAFIRMA,
					rootCause.getMessage());
		}
		
		documentViaFirmaRepository.save(documentViaFirma);
		document.updateEstat(
				DocumentEstatEnumDto.FIRMA_PENDENT_VIAFIRMA);
		contingutLogHelper.log(
				document,
				LogTipusEnumDto.VFIRMA_ENVIAMENT,
				documentViaFirma.getMessageCode(),
				documentViaFirma.getEstat().name(),
				false,
				false);
	}
	
	public Exception viaFirmaProcessar(
			DocumentViaFirmaEntity documentViaFirma) {
		DocumentEntity document = documentViaFirma.getDocument();
		organGestorHelper.actualitzarOrganCodi(organGestorHelper.getOrganCodiFromContingutId(document.getId()));
		DocumentEstatEnumDto documentEstatAnterior = document.getEstat();
		ViaFirmaCallbackEstatEnumDto callbackEstat = documentViaFirma.getCallbackEstat();
		if (ViaFirmaCallbackEstatEnumDto.RESPONSED.equals(callbackEstat)) {
			cacheHelper.evictEnviamentsPortafirmesPendentsPerExpedient(document.getExpedient());
			if (documentViaFirma.isFirmaParcial())
				document.updateEstat(DocumentEstatEnumDto.FIRMA_PARCIAL);
			else
				document.updateEstat(DocumentEstatEnumDto.FIRMAT);
			logFirmat(document);

			ViaFirmaDocument viaFirmaDocument = null;
			// Descarrega el document firmat del portafirmes
			try {
				viaFirmaDocument = pluginHelper.viaFirmaDownload(documentViaFirma);
			} catch (Exception ex) {
				logger.error("Error al descarregar document de Viafirma (id=" + documentViaFirma.getId() + ")", ex);
				cacheHelper.evictEnviamentsPortafirmesAmbErrorPerExpedient(document.getExpedient());
				Throwable rootCause = ExceptionUtils.getRootCause(ex);
				if (rootCause == null) rootCause = ex;
				documentViaFirma.updateProcessatError(
						ExceptionUtils.getStackTrace(rootCause),
						null);
				return null;
			}
			try {
				// Actualitza la informació de firma a l'arxiu.
				if (viaFirmaDocument != null) {
					byte [] contingut = IOUtils.toByteArray((new URL(viaFirmaDocument.getLink())).openStream());
					documentViaFirma.updateProcessat(true, new Date());
					List<ArxiuFirmaDto> firmes = pluginHelper.validaSignaturaObtenirFirmes(viaFirmaDocument.getNomFitxer(), contingut, null, "application/pdf", true);
					document.updateDocumentFirmaTipus(DocumentFirmaTipusEnumDto.FIRMA_ADJUNTA);
					ArxiuEstatEnumDto arxiuEstat =  documentViaFirma.isFirmaParcial() ? ArxiuEstatEnumDto.ESBORRANY : documentHelper.getArxiuEstat(DocumentFirmaTipusEnumDto.FIRMA_ADJUNTA, null);
					contingutHelper.arxiuPropagarModificacio(
							document,
							firmes.get(0).getFitxer(),
							arxiuEstat == ArxiuEstatEnumDto.ESBORRANY ? DocumentFirmaTipusEnumDto.SENSE_FIRMA : DocumentFirmaTipusEnumDto.FIRMA_ADJUNTA,
							firmes,
							arxiuEstat);
				}
			} catch (Exception ex) {
				logger.error("Error al custodiar document de Viafirma (id=" + documentViaFirma.getId() + ")", ex);
				cacheHelper.evictEnviamentsPortafirmesAmbErrorPerExpedient(document.getExpedient());
				document.updateEstat(DocumentEstatEnumDto.FIRMA_PENDENT_VIAFIRMA);
				Throwable rootCause = ExceptionUtils.getRootCause(ex);
				if (rootCause == null) rootCause = ex;
				documentViaFirma.updateProcessatError(
						ExceptionUtils.getStackTrace(rootCause),
						null);
			}
		} 
		if (ViaFirmaCallbackEstatEnumDto.WAITING_CHECK.equals(callbackEstat)) {
			try {
				cacheHelper.evictEnviamentsPortafirmesPendentsPerExpedient(document.getExpedient());
				contingutLogHelper.log(
						documentViaFirma.getDocument(),
						LogTipusEnumDto.VFIRMA_WAITING_CHECK,
						documentViaFirma.getMessageCode(),
						null,
						false,
						false);
			} catch (Exception ex) {
				Throwable rootCause = ExceptionUtils.getRootCause(ex);
				if (rootCause == null) rootCause = ex;
				return ex;
			}
		}
		
		if (ViaFirmaCallbackEstatEnumDto.REJECTED.equals(callbackEstat)) {
			try {
				cacheHelper.evictEnviamentsPortafirmesPendentsPerExpedient(document.getExpedient());
				if (! document.getEstat().equals(DocumentEstatEnumDto.FIRMA_PARCIAL)) {
					documentViaFirma.getDocument().updateEstat(
							DocumentEstatEnumDto.REDACCIO);
				}
				documentViaFirma.updateProcessat(
						false,
						new Date());
				contingutLogHelper.log(
						documentViaFirma.getDocument(),
						LogTipusEnumDto.VFIRMA_REBUIG,
						documentViaFirma.getMessageCode(),
						null,
						false,
						false);
			} catch (Exception ex) {
				Throwable rootCause = ExceptionUtils.getRootCause(ex);
				if (rootCause == null) rootCause = ex;
				return ex;
			}
		} else if (ViaFirmaCallbackEstatEnumDto.ERROR.equals(callbackEstat)) {
			try {
				cacheHelper.evictEnviamentsPortafirmesPendentsPerExpedient(document.getExpedient());
				documentViaFirma.getDocument().updateEstat(
						DocumentEstatEnumDto.REDACCIO);
				documentViaFirma.updateProcessat(
						false,
						new Date());
				contingutLogHelper.log(
						documentViaFirma.getDocument(),
						LogTipusEnumDto.VFIRMA_ERROR,
						documentViaFirma.getMessageCode(),
						null,
						false,
						false);
			} catch (Exception ex) {
				Throwable rootCause = ExceptionUtils.getRootCause(ex);
				if (rootCause == null) rootCause = ex;
				return ex;
			}
		} else if (ViaFirmaCallbackEstatEnumDto.EXPIRED.equals(callbackEstat)) {
			try {
				cacheHelper.evictEnviamentsPortafirmesPendentsPerExpedient(document.getExpedient());
				documentViaFirma.getDocument().updateEstat(
						DocumentEstatEnumDto.REDACCIO);
				documentViaFirma.updateProcessat(
						false,
						new Date());
				contingutLogHelper.log(
						documentViaFirma.getDocument(),
						LogTipusEnumDto.VFIRMA_EXPIRED,
						documentViaFirma.getMessageCode(),
						null,
						false,
						false);
			} catch (Exception ex) {
				Throwable rootCause = ExceptionUtils.getRootCause(ex);
				if (rootCause == null) rootCause = ex;
				return ex;
			}
		}

		if (ViaFirmaCallbackEstatEnumDto.RESPONSED.equals(callbackEstat) || ViaFirmaCallbackEstatEnumDto.REJECTED.equals(callbackEstat) || 
				ViaFirmaCallbackEstatEnumDto.EXPIRED.equals(callbackEstat) || ViaFirmaCallbackEstatEnumDto.ERROR.equals(callbackEstat)) {
			DocumentEstatEnumDto documentEstatNou = document.getEstat();
			if (documentEstatAnterior != DocumentEstatEnumDto.CUSTODIAT && (documentEstatAnterior != documentEstatNou)) {
				alertaHelper.crearAlerta(
						"[ViaFirma] La firma del document " + document.getNom() + " ha finalitzat amb estat: " + callbackEstat,
						null,
						document.getExpedient().getId());
				if (documentViaFirma.isRebreCorreu())
					emailHelper.canviEstatDocumentViaFirma(documentViaFirma);
			}
		}
		return null;
	}

	public void viaFirmaReintentar(DocumentViaFirmaEntity documentPortafirmes) {
		contingutLogHelper.log(
				documentPortafirmes.getDocument(),
				LogTipusEnumDto.VFIRMA_REINTENT,
				documentPortafirmes.getMessageCode(),
				documentPortafirmes.getEstat().name(),
				false,
				false);
		if (DocumentEnviamentEstatEnumDto.PENDENT.equals(documentPortafirmes.getEstat())) {
			viaFirmaEnviar(documentPortafirmes);
		} else if (DocumentEnviamentEstatEnumDto.ENVIAT.equals(documentPortafirmes.getEstat())) {
			viaFirmaProcessar(documentPortafirmes);
		}
	}
		
	public void viaFirmaCancelar(DocumentViaFirmaEntity documentViaFirma) {
		DocumentEntity document = documentViaFirma.getDocument();
		documentViaFirma.updateMessageCode(null);
		documentViaFirma.updateCancelat(new Date());
		document.updateEstat(
				DocumentEstatEnumDto.REDACCIO);
		contingutLogHelper.log(
				document,
				LogTipusEnumDto.VFIRMA_CANCELACIO,
				documentViaFirma.getMessageCode(),
				documentViaFirma.getEstat().name(),
				false,
				false);
	}
	
	public Exception viaFirmaCallback(DocumentViaFirmaEntity documentViaFirma, ViaFirmaCallbackEstatEnumDto callbackEstat) {
//		DocumentEntity document = documentViaFirma.getDocument();
//		documentViaFirma.updateMessageCode(null);
//		documentViaFirma.updateCancelat(new Date());
//		document.updateEstat(
//				DocumentEstatEnumDto.REDACCIO);
//		contingutLogHelper.log(
//				document,
//				LogTipusEnumDto.VFIRMA_CANCELACIO,
//				documentViaFirma.getMessageCode(),
//				documentViaFirma.getEstat().name(),
//				false,
//				false);
		contingutLogHelper.log(
				documentViaFirma.getDocument(),
				LogTipusEnumDto.VFIRMA_CALLBACK,
				documentViaFirma.getMessageCode(),
				documentViaFirma.getEstat().name(),
				false,
				false);
		documentViaFirma.updateCallbackEstat(callbackEstat);
		return viaFirmaProcessar(documentViaFirma);
	}
	

	private static final Logger logger = LoggerFactory.getLogger(DocumentFirmaViaFirmaHelper.class);
}
