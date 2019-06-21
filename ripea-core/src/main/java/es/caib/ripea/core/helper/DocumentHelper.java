/**
 * 
 */
package es.caib.ripea.core.helper;

import java.nio.ByteBuffer;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import javax.annotation.Resource;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.sun.jersey.core.util.Base64;

import es.caib.plugins.arxiu.api.ContingutArxiu;
import es.caib.plugins.arxiu.api.Document;
import es.caib.plugins.arxiu.api.Firma;
import es.caib.plugins.arxiu.api.FirmaTipus;
import es.caib.ripea.core.api.dto.ArxiuFirmaDto;
import es.caib.ripea.core.api.dto.ContingutTipusEnumDto;
import es.caib.ripea.core.api.dto.DocumentEstatEnumDto;
import es.caib.ripea.core.api.dto.DocumentNtiEstadoElaboracionEnumDto;
import es.caib.ripea.core.api.dto.DocumentNtiTipoDocumentalEnumDto;
import es.caib.ripea.core.api.dto.DocumentNtiTipoFirmaEnumDto;
import es.caib.ripea.core.api.dto.DocumentTipusEnumDto;
import es.caib.ripea.core.api.dto.FitxerDto;
import es.caib.ripea.core.api.dto.LogTipusEnumDto;
import es.caib.ripea.core.api.dto.NtiOrigenEnumDto;
import es.caib.ripea.core.api.dto.PortafirmesCallbackEstatEnumDto;
import es.caib.ripea.core.api.exception.SistemaExternException;
import es.caib.ripea.core.api.exception.ValidationException;
import es.caib.ripea.core.entity.ContingutEntity;
import es.caib.ripea.core.entity.DocumentEntity;
import es.caib.ripea.core.entity.DocumentPortafirmesEntity;
import es.caib.ripea.core.entity.EntitatEntity;
import es.caib.ripea.core.entity.ExpedientEntity;
import es.caib.ripea.core.entity.MetaDocumentEntity;
import es.caib.ripea.core.entity.NodeEntity;
import es.caib.ripea.core.repository.DocumentRepository;
import es.caib.ripea.plugin.portafirmes.PortafirmesDocument;
import es.caib.ripea.plugin.portafirmes.PortafirmesPrioritatEnum;

/**
 * Mètodes per a gestionar els arxius associats a un document
 * tenint en compte que hi pot haver configurat (o no) un plugin
 * de gestió documental.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class DocumentHelper {

	@Resource
	private DocumentRepository documentRepository;

	@Resource
	private ContingutHelper contingutHelper;
	@Resource
	private ContingutLogHelper contingutLogHelper;
	@Resource
	private PluginHelper pluginHelper;
	@Resource
	private CacheHelper cacheHelper;



	public DocumentEntity crearNouDocument(
			DocumentTipusEnumDto documentTipus,
			String nom,
			Date data,
			Date dataCaptura,
			String ntiOrgano,
			NtiOrigenEnumDto ntiOrigen,
			DocumentNtiEstadoElaboracionEnumDto ntiEstadoElaboracion,
			DocumentNtiTipoDocumentalEnumDto ntiTipoDocumental,
			MetaDocumentEntity metaDocument,
			ContingutEntity pare,
			EntitatEntity entitat,
			ExpedientEntity expedient,
			String ubicacio,
			String ntiIdDocumentoOrigen) {
		DocumentEntity documentCrear = DocumentEntity.getBuilder(
				documentTipus,
				DocumentEstatEnumDto.REDACCIO,
				nom,
				data,
				dataCaptura,
				ntiIdDocumentoOrigen,
				"1.0",
				ntiOrgano,
				ntiOrigen,
				ntiEstadoElaboracion,
				ntiTipoDocumental,
				metaDocument,
				pare,
				entitat,
				expedient
				).
				ubicacio(ubicacio).
				build();
		DocumentEntity documentCreat = documentRepository.save(documentCrear);
		calcularIdentificadorDocument(
				documentCreat,
				entitat.getUnitatArrel());
		if (expedient != null) {
			cacheHelper.evictErrorsValidacioPerNode(expedient);
		}
		return documentCreat;
	}

	public void actualitzarFitxerDocument(
			DocumentEntity document,
			FitxerDto fitxer) {
		if (pluginHelper.isArxiuPluginActiu()) {
			document.updateFitxer(
					fitxer.getNom(),
					fitxer.getContentType(),
					null);
		} else {
			document.updateFitxer(
					fitxer.getNom(),
					fitxer.getContentType(),
					fitxer.getContingut());
		}
	}

	public FitxerDto getFitxerAssociat(
			DocumentEntity document,
			String versio) {
		FitxerDto fitxer = null;
		if (document.getArxiuUuid() != null) {
			if (pluginHelper.isArxiuPluginActiu()) {
				fitxer = new FitxerDto();
				fitxer.setContentType(document.getFitxerContentType());
				fitxer.setNom(document.getFitxerNom());
				Document arxiuDocument = pluginHelper.arxiuDocumentConsultar(
						document,
						null,
						versio,
						true,
						false);
				fitxer.setContingut(getContingutFromArxiuDocument(arxiuDocument));
			} else {
				throw new SistemaExternException(
						IntegracioHelper.INTCODI_ARXIU,
						"S'està intentant obtenir l'arxiu associat a un document pujat a l'arxiu i el plugin d'arxiu no està activat");
			}
		} else {
			fitxer = new FitxerDto();
			fitxer.setNom(document.getFitxerNom());
			fitxer.setContentType(document.getFitxerContentType());
			fitxer.setContingut(document.getFitxerContingut());
		}
		if (versio == null && DocumentEstatEnumDto.CUSTODIAT.equals(document.getEstat())) {
			fitxer.setNom(
					pluginHelper.conversioConvertirPdfArxiuNom(
							document.getFitxerNom()));
		}
		return fitxer;
	}

	@SuppressWarnings("incomplete-switch")
	public byte[] getContingutFromArxiuDocument(Document arxiuDocument) {
		byte[] contingut = null;
		if (arxiuDocument.getFirmes() == null) {
			contingut = arxiuDocument.getContingut().getContingut();
		} else {
			for (Firma firma: arxiuDocument.getFirmes()) {
				if (firma.getTipus() != FirmaTipus.CSV) {
					switch(firma.getTipus()) {
					case CADES_ATT:
					case XADES_ENV:
					case PADES:
					case ODT:
					case OOXML:
					case SMIME:
						contingut = firma.getContingut();
						break;
					case CADES_DET:
					case XADES_DET:
						contingut = arxiuDocument.getContingut().getContingut();
						break;
					}
				}
			}
		}
		return contingut;
	}
	@SuppressWarnings("incomplete-switch")
	public byte[] getFirmaDetachedFromArxiuDocument(Document arxiuDocument) {
		byte[] firmaDetached = null;
		if (arxiuDocument.getFirmes() != null) {
			for (Firma firma: arxiuDocument.getFirmes()) {
				if (firma.getTipus() != FirmaTipus.CSV) {
					switch(firma.getTipus()) {
					case CADES_DET:
					case XADES_DET:
						firmaDetached = firma.getContingut();
						break;
					}
				}
			}
		}
		return firmaDetached;
	}

	public void calcularIdentificadorDocument(
			DocumentEntity document,
			String organCodi) {
		int any = Calendar.getInstance().get(Calendar.YEAR);
		UUID uuid = UUID.randomUUID();
		ByteBuffer bb = ByteBuffer.wrap(new byte[20]);
		bb.putLong(uuid.getMostSignificantBits());
		bb.putLong(uuid.getLeastSignificantBits());
		Random rand = new Random(System.currentTimeMillis());
		bb.putInt(rand.nextInt());
		String identificador = "ES_" + organCodi + "_" + any + "_" +  new String(Base64.encode(bb.array()));
		document.updateNtiIdentificador(identificador);
	}

	public String getExtensioArxiu(String arxiuNom) {
		int indexPunt = arxiuNom.lastIndexOf(".");
		if (indexPunt != -1 && indexPunt < arxiuNom.length() - 1) {
			return arxiuNom.substring(indexPunt + 1);
		} else {
			return null;
		}
	}

	public SistemaExternException portafirmesEnviar(
			DocumentPortafirmesEntity documentPortafirmes) {
		DocumentEntity document = documentPortafirmes.getDocument();
		try {
			String portafirmesId = pluginHelper.portafirmesUpload(
					document,
					documentPortafirmes.getAssumpte(),
					PortafirmesPrioritatEnum.valueOf(documentPortafirmes.getPrioritat().name()),
					documentPortafirmes.getCaducitatData(),
					documentPortafirmes.getDocumentTipus(),
					documentPortafirmes.getResponsables(),
					documentPortafirmes.getFluxTipus(),
					documentPortafirmes.getFluxId(),
					null);
			documentPortafirmes.updateEnviat(
					new Date(),
					portafirmesId);
			return null;
		} catch (SistemaExternException ex) {
			Throwable rootCause = ExceptionUtils.getRootCause(ex);
			if (rootCause == null) rootCause = ex;
			documentPortafirmes.updateEnviatError(
					ExceptionUtils.getStackTrace(rootCause),
					null);
			return ex;
		}
	}

	public Exception portafirmesProcessar(
			DocumentPortafirmesEntity documentPortafirmes) {
		DocumentEntity document = documentPortafirmes.getDocument();
		PortafirmesCallbackEstatEnumDto callbackEstat = documentPortafirmes.getCallbackEstat();
		if (PortafirmesCallbackEstatEnumDto.FIRMAT.equals(callbackEstat)) {
			document.updateEstat(
					DocumentEstatEnumDto.FIRMAT);
			PortafirmesDocument portafirmesDocument = null;
			// Descarrega el document firmat del portafirmes
			try {
				portafirmesDocument = pluginHelper.portafirmesDownload(
						documentPortafirmes);
			} catch (Exception ex) {
				Throwable rootCause = ExceptionUtils.getRootCause(ex);
				if (rootCause == null) rootCause = ex;
				documentPortafirmes.updateProcessatError(
						ExceptionUtils.getStackTrace(rootCause),
						null);
				return null;
			}
			try {
				if (portafirmesDocument.isCustodiat()) {
					// Si el document ja ha estat custodiat pel portafirmes
					// actualitza la informació de custòdia.
					document.updateInformacioCustodia(
							new Date(),
							portafirmesDocument.getCustodiaId(),
							portafirmesDocument.getCustodiaUrl());
					documentPortafirmes.updateProcessat(
							true,
							new Date());
				} else {
					// Si el document no ha estat custodiat pel portafirmes
					// actualitza la informació de firma a l'arxiu.
					FitxerDto fitxer = new FitxerDto();
					fitxer.setNom(portafirmesDocument.getArxiuNom());
					fitxer.setContingut(portafirmesDocument.getArxiuContingut());
					fitxer.setContentType("application/pdf");
					documentPortafirmes.updateProcessat(
							true,
							new Date());
					String custodiaDocumentId = pluginHelper.arxiuDocumentGuardarPdfFirmat(
							document,
							fitxer);
					document.updateInformacioCustodia(
							new Date(),
							custodiaDocumentId,
							document.getCustodiaCsv());
					actualitzarVersionsDocument(document);
					actualitzarInformacioFirma(document);
					contingutLogHelper.log(
							documentPortafirmes.getDocument(),
							LogTipusEnumDto.ARXIU_CUSTODIAT,
							custodiaDocumentId,
							null,
							false,
							false);
				}
			} catch (Exception ex) {
				Throwable rootCause = ExceptionUtils.getRootCause(ex);
				if (rootCause == null) rootCause = ex;
				documentPortafirmes.updateProcessatError(
						ExceptionUtils.getStackTrace(rootCause),
						null);
			}
		}
		if (PortafirmesCallbackEstatEnumDto.REBUTJAT.equals(callbackEstat)) {
			try {
				documentPortafirmes.getDocument().updateEstat(
						DocumentEstatEnumDto.REDACCIO);
				documentPortafirmes.updateProcessat(
						false,
						new Date());
				contingutLogHelper.log(
						documentPortafirmes.getDocument(),
						LogTipusEnumDto.PFIRMA_REBUIG,
						documentPortafirmes.getPortafirmesId(),
						null,
						false,
						false);
			} catch (Exception ex) {
				Throwable rootCause = ExceptionUtils.getRootCause(ex);
				if (rootCause == null) rootCause = ex;
				return ex;
			}
		}
		return null;
	}

	public void actualitzarVersionsDocument(
			DocumentEntity document) {
		if (pluginHelper.arxiuSuportaVersionsDocuments()) {
			try {
				List<ContingutArxiu> versions = pluginHelper.arxiuDocumentObtenirVersions(
						document);
				if (versions != null) {
					ContingutArxiu darreraVersio = null;
					Float versioNum = null;
					for (ContingutArxiu versio: versions) {
						if (versioNum == null || new Float(versio.getVersio()).floatValue() > versioNum.floatValue()) {
							versioNum = new Float(versio.getVersio());
							darreraVersio = versio;
						}
					}
					document.updateVersio(
							darreraVersio.getVersio(),
							versions.size());
				}
			} catch (Exception ex) {
				logger.error(
						"Error al actualitzar les versions del document (" + 
						"entitatId=" + document.getEntitat().getId() + ", " +
						"documentId=" + document.getId() + ", " +
						"documentTitol=" + document.getNom() + ")",
						ex);
			}
		}
	}

	public void actualitzarInformacioFirma(
			DocumentEntity document) {
		if (pluginHelper.arxiuSuportaVersionsDocuments()) {
			try {
				Document documentArxiu = pluginHelper.arxiuDocumentConsultar(
						document,
						null,
						null,
						false);
				DocumentNtiTipoFirmaEnumDto tipoFirma = null;
				String csv = null;
				String csvDef = null;
				if (documentArxiu.getFirmes() != null) {
					for (Firma firma: documentArxiu.getFirmes()) {
						if (FirmaTipus.CSV.equals(firma.getTipus())) {
							csv = new String(firma.getContingut());
							csvDef = firma.getCsvRegulacio();
						} else {
							switch (firma.getTipus()) {
							case CSV:
								tipoFirma = DocumentNtiTipoFirmaEnumDto.TF01;
								break;
							case XADES_DET:
								tipoFirma = DocumentNtiTipoFirmaEnumDto.TF02;
								break;
							case XADES_ENV:
								tipoFirma = DocumentNtiTipoFirmaEnumDto.TF03;
								break;
							case CADES_DET:
								tipoFirma = DocumentNtiTipoFirmaEnumDto.TF04;
								break;
							case CADES_ATT:
								tipoFirma = DocumentNtiTipoFirmaEnumDto.TF05;
								break;
							case PADES:
								tipoFirma = DocumentNtiTipoFirmaEnumDto.TF06;
								break;
							case SMIME:
								tipoFirma = DocumentNtiTipoFirmaEnumDto.TF07;
								break;
							case ODT:
								tipoFirma = DocumentNtiTipoFirmaEnumDto.TF08;
								break;
							case OOXML:
								tipoFirma = DocumentNtiTipoFirmaEnumDto.TF09;
								break;
							}
						}
					}
				}
				document.updateNti(
						document.getNtiVersion(),
						document.getNtiIdentificador(),
						document.getNtiOrgano(),
						document.getNtiOrigen(),
						document.getNtiEstadoElaboracion(),
						document.getNtiTipoDocumental(),
						document.getNtiIdDocumentoOrigen(),
						tipoFirma,
						csv,
						csvDef);
			} catch (Exception ex) {
				logger.error(
						"Error al actualitzar les metadades NTI de firma (" + 
						"entitatId=" + document.getEntitat().getId() + ", " +
						"documentId=" + document.getId() + ", " +
						"documentTitol=" + document.getNom() + ")",
						ex);
			}
		}
	}

	public DocumentEntity comprovarDocumentDinsExpedientModificable(
			Long entitatId,
			Long id,
			boolean comprovarPermisRead,
			boolean comprovarPermisWrite,
			boolean comprovarPermisCreate,
			boolean comprovarPermisDelete) {
		NodeEntity node = contingutHelper.comprovarNodeDinsExpedientModificable(
				entitatId,
				id,
				comprovarPermisRead,
				comprovarPermisWrite,
				comprovarPermisCreate,
				comprovarPermisDelete);
		if (!ContingutTipusEnumDto.DOCUMENT.equals(node.getTipus())) {
			throw new ValidationException(
					id,
					DocumentEntity.class,
					"El contingut especificat no és un document");
		}
		return (DocumentEntity)node;
	}

	public DocumentEntity comprovarDocumentDinsExpedientAccessible(
			Long entitatId,
			Long id,
			boolean comprovarPermisRead,
			boolean comprovarPermisWrite) {
		NodeEntity node = contingutHelper.comprovarNodeDinsExpedientAccessible(
				entitatId,
				id,
				comprovarPermisRead,
				comprovarPermisWrite);
		if (!ContingutTipusEnumDto.DOCUMENT.equals(node.getTipus())) {
			throw new ValidationException(
					id,
					DocumentEntity.class,
					"El contingut especificat no és un document");
		}
		return (DocumentEntity)node;
	}
	
	public List<ArxiuFirmaDto> validaFirmaDocument(
			DocumentEntity document,
			FitxerDto fitxer,
			byte[] contingutFirma) {
		logger.debug("Recuperar la informació de les firmes amb el plugin ValidateSignature ("
				+ "documentID=" + document.getId() + ")");
		List<ArxiuFirmaDto> firmes = pluginHelper.validaSignaturaObtenirFirmes(
				fitxer.getContingut(), 
				(contingutFirma != null && contingutFirma.length > 0) ? contingutFirma : null,
				fitxer.getContentType());
		document.updateEstat(DocumentEstatEnumDto.FIRMAT);
		contingutLogHelper.log(
				document,
				LogTipusEnumDto.DOC_FIRMAT,
				null,
				null,
				false,
				false);
		return firmes;
	}
	
	
	

	private static final Logger logger = LoggerFactory.getLogger(DocumentHelper.class);

}
