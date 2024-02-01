/**
 * 
 */
package es.caib.ripea.core.helper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.lowagie.text.pdf.AcroFields;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfReader;

import es.caib.plugins.arxiu.api.Carpeta;
import es.caib.plugins.arxiu.api.ContingutArxiu;
import es.caib.plugins.arxiu.api.Document;
import es.caib.plugins.arxiu.caib.ArxiuCaibException;
import es.caib.ripea.core.api.dto.ArxiuEstatEnumDto;
import es.caib.ripea.core.api.dto.ArxiuFirmaDto;
import es.caib.ripea.core.api.dto.CarpetaDto;
import es.caib.ripea.core.api.dto.ContingutDto;
import es.caib.ripea.core.api.dto.ContingutTipusEnumDto;
import es.caib.ripea.core.api.dto.DadaDto;
import es.caib.ripea.core.api.dto.DocumentDto;
import es.caib.ripea.core.api.dto.DocumentEstatEnumDto;
import es.caib.ripea.core.api.dto.DocumentFirmaTipusEnumDto;
import es.caib.ripea.core.api.dto.DocumentNotificacioEstatEnumDto;
import es.caib.ripea.core.api.dto.DocumentTipusEnumDto;
import es.caib.ripea.core.api.dto.DocumentVersioDto;
import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.ExpedientDto;
import es.caib.ripea.core.api.dto.ExpedientEstatDto;
import es.caib.ripea.core.api.dto.FitxerDto;
import es.caib.ripea.core.api.dto.InteressatDto;
import es.caib.ripea.core.api.dto.LogTipusEnumDto;
import es.caib.ripea.core.api.dto.MetaDocumentDto;
import es.caib.ripea.core.api.dto.MetaExpedientDto;
import es.caib.ripea.core.api.dto.MetaNodeDto;
import es.caib.ripea.core.api.dto.NodeDto;
import es.caib.ripea.core.api.dto.PermissionEnumDto;
import es.caib.ripea.core.api.dto.ResultDocumentsSenseContingut.ResultDocumentSenseContingut;
import es.caib.ripea.core.api.dto.ResultDocumentsSenseContingut.ResultDocumentSenseContingut.ResultDocumentSenseContingutBuilder;
import es.caib.ripea.core.api.dto.TipusDocumentalDto;
import es.caib.ripea.core.api.dto.UsuariDto;
import es.caib.ripea.core.api.exception.NotFoundException;
import es.caib.ripea.core.api.exception.PermissionDeniedException;
import es.caib.ripea.core.api.exception.ValidationException;
import es.caib.ripea.core.api.registre.RegistreInteressat;
import es.caib.ripea.core.api.utils.Utils;
import es.caib.ripea.core.entity.CarpetaEntity;
import es.caib.ripea.core.entity.ContingutEntity;
import es.caib.ripea.core.entity.ContingutMovimentEntity;
import es.caib.ripea.core.entity.DadaEntity;
import es.caib.ripea.core.entity.DocumentEntity;
import es.caib.ripea.core.entity.DocumentEnviamentInteressatEntity;
import es.caib.ripea.core.entity.DocumentNotificacioEntity;
import es.caib.ripea.core.entity.EntitatEntity;
import es.caib.ripea.core.entity.ExpedientEntity;
import es.caib.ripea.core.entity.ExpedientEstatEntity;
import es.caib.ripea.core.entity.ExpedientTascaEntity;
import es.caib.ripea.core.entity.GrupEntity;
import es.caib.ripea.core.entity.InteressatAdministracioEntity;
import es.caib.ripea.core.entity.InteressatEntity;
import es.caib.ripea.core.entity.InteressatPersonaFisicaEntity;
import es.caib.ripea.core.entity.InteressatPersonaJuridicaEntity;
import es.caib.ripea.core.entity.MetaDocumentEntity;
import es.caib.ripea.core.entity.MetaExpedientEntity;
import es.caib.ripea.core.entity.NodeEntity;
import es.caib.ripea.core.entity.OrganGestorEntity;
import es.caib.ripea.core.entity.RegistreAnnexEntity;
import es.caib.ripea.core.entity.TipusDocumentalEntity;
import es.caib.ripea.core.entity.UsuariEntity;
import es.caib.ripea.core.firma.DocumentFirmaPortafirmesHelper;
import es.caib.ripea.core.repository.AlertaRepository;
import es.caib.ripea.core.repository.CarpetaRepository;
import es.caib.ripea.core.repository.ContingutMovimentRepository;
import es.caib.ripea.core.repository.ContingutRepository;
import es.caib.ripea.core.repository.DadaRepository;
import es.caib.ripea.core.repository.DocumentNotificacioRepository;
import es.caib.ripea.core.repository.DocumentPortafirmesRepository;
import es.caib.ripea.core.repository.DocumentRepository;
import es.caib.ripea.core.repository.ExpedientEstatRepository;
import es.caib.ripea.core.repository.ExpedientRepository;
import es.caib.ripea.core.repository.ExpedientTascaRepository;
import es.caib.ripea.core.repository.GrupRepository;
import es.caib.ripea.core.repository.MetaDocumentRepository;
import es.caib.ripea.core.repository.RegistreAnnexRepository;
import es.caib.ripea.core.repository.TipusDocumentalRepository;
import es.caib.ripea.core.repository.UsuariRepository;
import es.caib.ripea.core.security.ExtendedPermission;
import es.caib.ripea.plugin.PropertiesHelper;
import es.caib.ripea.plugin.arxiu.ArxiuContingutTipusEnum;
import es.caib.ripea.plugin.arxiu.ArxiuDocumentContingut;
import es.caib.ripea.plugin.notificacio.RespostaConsultaEstatEnviament;

/**
 * Utilitat per a gestionar contenidors.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class ContingutHelper {

	@Autowired
	private ContingutRepository contingutRepository;
	@Autowired
	private DadaRepository dadaRepository;
	@Autowired
	private ExpedientRepository expedientRepository;
	@Autowired
	private UsuariRepository usuariRepository;
	@Autowired
	private ContingutMovimentRepository contenidorMovimentRepository;
	@Autowired
	private DocumentRepository documentRepository;
	@Autowired
	private CarpetaRepository carpetaRepository;
	@Autowired
	private ExpedientEstatRepository expedientEstatRepository;
	@Autowired
	private GrupRepository grupRepository;
	@Autowired
	private AlertaRepository alertaRepository;
	@Autowired
	private RegistreAnnexRepository registreAnnexRepository;
	@Autowired
	private ContingutLogHelper contingutLogHelper;
	@Autowired
	private EntityComprovarHelper entityComprovarHelper;
	@Autowired
	private ConversioTipusHelper conversioTipusHelper;
	@Autowired
	private MetaNodeHelper metaNodeHelper;
	@Autowired
	private DocumentHelper documentHelper;
	@Autowired
	private PluginHelper pluginHelper;
	@Autowired
	private PermisosHelper permisosHelper;
	@Autowired
	private CacheHelper cacheHelper;
	@Autowired
	private UsuariHelper usuariHelper;
	@Autowired
	private MetaExpedientHelper metaExpedientHelper;
	@Autowired
	private ExpedientHelper expedientHelper;
	@Autowired
	private ExpedientTascaRepository expedientTascaRepository;
	@Autowired
	private TipusDocumentalRepository tipusDocumentalRepository;
	@Autowired
	private IndexHelper indexHelper;
	@Autowired
	private MessageHelper messageHelper;
	@Autowired
	private DocumentFirmaPortafirmesHelper firmaPortafirmesHelper;
	@Autowired
	private ConfigHelper configHelper;
	@Autowired
	private OrganGestorHelper organGestorHelper;
	@Autowired
	private ExpedientInteressatHelper expedientInteressatHelper;
	@Autowired
	private MetaDocumentRepository metaDocumentRepository;
	@Autowired
	private DocumentPortafirmesRepository documentPortafirmesRepository;
	@Autowired
	private DocumentNotificacioRepository documentNotificacioRepository;



	public ContingutDto toContingutDto(
			ContingutEntity contingut, 
			boolean ambPath, 
			boolean pathNomesFinsExpedientArrel) {
		return toContingutDto(
				contingut,
				false,
				false,
				false,
				ambPath,
				pathNomesFinsExpedientArrel,
				false,
				null,
				false,
				null,
				false,
				0,
				null,
				null,
				false,
				false,
				false, 
				false);
	}
	
	
	public ContingutDto toContingutDto(
			ContingutEntity contingut,
			boolean ambPermisos,
			boolean ambFills,
			boolean ambDades,
			boolean ambPath,
			boolean pathNomesFinsExpedientArrel,
			boolean ambVersions,
			String rolActual,
			boolean onlyForList,
			Long organActualId,
			boolean onlyFirstDescendant, 
			int level, 
			ExpedientDto expedientDto, 
			List<ContingutDto> pathDto, 
			boolean ambExpedientPare, 
			boolean ambEntitat, 
			boolean ambMapPerTipusDocument, 
			boolean ambMapPerEstat) {
		level++;
		organGestorHelper.actualitzarOrganCodi(organGestorHelper.getOrganCodiFromContingutId(contingut.getId()));
		ContingutDto resposta = null;
		MetaNodeDto metaNode = null;
		// Crea el contenidor del tipus correcte
		ContingutEntity deproxied = HibernateHelper.deproxy(contingut);
		// ##################### EXPEDIENT ##################################
		if (deproxied instanceof ExpedientEntity) {
			if (cacheHelper.mostrarLogsRendiment())
				logger.info("toExpedientDto start (" + contingut.getId() + ", level=" + level + ") ");
			long t1 = System.currentTimeMillis();

			ExpedientEntity expedient = (ExpedientEntity)deproxied;
			ExpedientDto dto = new ExpedientDto();

			dto.setEstat(expedient.getEstat());
			dto.setNumero(expedient.getNumero());
			dto.setAgafatPer(
					conversioTipusHelper.convertir(
							expedient.getAgafatPer(),
							UsuariDto.class));
			dto.setValid(
					cacheHelper.findErrorsValidacioPerNode(expedient).isEmpty());

			// expedient estat
			if (expedient.getEstatAdditional() != null) {
				dto.setExpedientEstat(conversioTipusHelper.convertir(
						expedient.getEstatAdditional(),
						ExpedientEstatDto.class));
			}
			
			if (ambPermisos) {
				omplirPermisosPerExpedient(dto, rolActual, contingut.getId());
			}
			
			dto.setNumSeguidors(expedient.getSeguidors().size());
			dto.setNumComentaris(expedient.getComentaris().size());
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			if (auth != null) {
				UsuariEntity usuariActual = usuariRepository.findByCodi(auth.getName());
				if (expedient.getSeguidors().contains(usuariActual))
					dto.setSeguidor(true);
			}
			dto.setErrorLastEnviament(cacheHelper.hasEnviamentsPortafirmesAmbErrorPerExpedient(expedient));
			dto.setErrorLastNotificacio(cacheHelper.hasNotificacionsAmbErrorPerExpedient(expedient));
			dto.setAmbEnviamentsPendents(cacheHelper.hasEnviamentsPortafirmesPendentsPerExpedient(expedient));
			dto.setAmbNotificacionsPendents(cacheHelper.hasNotificacionsPendentsPerExpedient(expedient));
			metaNode = conversioTipusHelper.convertir(
					expedient.getMetaNode(),
					MetaExpedientDto.class);
			dto.setMetaNode(metaNode);

//			dto.setConteDocumentsDefinitius(conteDocumentsDefinitius(contingut));

			Boolean conteDocumentsDefinitiusSelect = documentRepository.expedientHasDocumentsDefinitius(expedient);
			dto.setConteDocumentsDefinitius(conteDocumentsDefinitiusSelect);
			dto.setConteDocuments(CollectionUtils.isNotEmpty(documentRepository.findByExpedientAndEsborrat(expedient, 0)));
			dto.setConteDocumentsEnProcessDeFirma(CollectionUtils.isNotEmpty(documentRepository.findEnProccessDeFirma(expedient)));	
			dto.setConteDocumentsDePortafirmesNoCustodiats(CollectionUtils.isNotEmpty(documentRepository.findDocumentsDePortafirmesNoCustodiats(expedient)));	
			dto.setConteDocumentsPendentsReintentsArxiu(CollectionUtils.isNotEmpty(documentRepository.findDocumentsPendentsReintentsArxiu(expedient, getArxiuMaxReintentsDocuments())));
			dto.setConteDocumentsDeAnotacionesNoMogutsASerieFinal(CollectionUtils.isNotEmpty(registreAnnexRepository.findDocumentsDeAnotacionesNoMogutsASerieFinal(expedient)));	


			if (onlyForList) {
				dto.setDataDarrerEnviament(cacheHelper.getDataDarrerEnviament(expedient));
				dto.setRolActualAdminEntitatOAdminOrgan(entityComprovarHelper.comprovarRolActualAdminEntitatOAdminOrganDelExpedient(expedient, rolActual));
				dto.setPotModificar(entityComprovarHelper.comprovarSiEsPotModificarExpedient(expedient));
				dto.setExpedientAgafatPerUsuariActual(entityComprovarHelper.comprovarSiExpedientAgafatPerUsuariActual(expedient));
				dto.setRolActualPermisPerModificarExpedient(entityComprovarHelper.comprovarSiRolTePermisPerModificarExpedient(expedient, rolActual));
				dto.setPotReobrir(entityComprovarHelper.comprovarSiEsPotReobrirExpedient(expedient));
			}
			
			if (!onlyForList) {
				dto.setTancatData(expedient.getTancatData());
				dto.setTancatMotiu(expedient.getTancatMotiu());
				dto.setAny(expedient.getAny());
				dto.setSequencia(expedient.getSequencia());
				dto.setCodi(expedient.getCodi());
				dto.setNtiVersion(expedient.getNtiVersion());
				dto.setNtiIdentificador(expedient.getNtiIdentificador());
				dto.setNtiOrgano(expedient.getNtiOrgano());
				dto.setNtiOrganoDescripcio(expedient.getNtiOrgano());
				dto.setNtiFechaApertura(expedient.getNtiFechaApertura());
				dto.setNtiClasificacionSia(expedient.getNtiClasificacionSia());
				dto.setSistraPublicat(expedient.isSistraPublicat());
				dto.setSistraUnitatAdministrativa(expedient.getSistraUnitatAdministrativa());
				dto.setSistraClau(expedient.getSistraClau());
				dto.setPeticions(expedient.getPeticions() != null && !expedient.getPeticions( ).isEmpty() ? true : false);


				dto.setHasEsborranys(documentRepository.hasFillsEsborranys(expedient));
				dto.setConteDocumentsFirmats(
						documentRepository.countByExpedientAndEstat(
								expedient,
								DocumentEstatEnumDto.CUSTODIAT) > 0);
				dto.setHasAllDocumentsDefinitiu(documentRepository.hasAllDocumentsDefinitiu(expedient));
				// expedient estat
				if (expedient.getEstatAdditional() != null) {
					ExpedientEstatEntity estat =  expedientEstatRepository.findByMetaExpedientAndOrdre(expedient.getEstatAdditional().getMetaExpedient(), expedient.getEstatAdditional().getOrdre()+1);
					if (estat != null) {
						dto.setExpedientEstatNextInOrder(estat.getId());
					} else {//if there is no estat with higher order, choose previous
						dto.setExpedientEstatNextInOrder(expedient.getEstatAdditional().getId());
					}
				}

				dto.setInteressats(conversioTipusHelper.convertirSet(expedient.getInteressatsORepresentants(),InteressatDto.class));
				dto.setInteressatsNotificable(conversioTipusHelper.convertirList(expedientInteressatHelper.findByExpedientAndNotRepresentantAndAmbDadesPerNotificacio(expedient), InteressatDto.class));
				dto.setGrupId(expedient.getGrup() != null ? expedient.getGrup().getId() : null);
				dto.setGrupNom(expedient.getGrup() != null ? expedient.getGrup().getDescripcio() : null);

				dto.setOrganGestorId(expedient.getOrganGestor() != null ? expedient.getOrganGestor().getId() : null);
				dto.setOrganGestorText(expedient.getOrganGestor() != null ?
						expedient.getOrganGestor().getCodi() + " - " + expedient.getOrganGestor().getNom() : "");
			
			
			
				if (ambMapPerTipusDocument) {
					if (cacheHelper.mostrarLogsRendiment())
						logger.info("ambMapPerTipusDocument start (" + contingut.getId() + ")");
					long t2 = System.currentTimeMillis();

					Map<MetaDocumentDto, List<ContingutDto>> mapPerTipusDocument = new LinkedHashMap<MetaDocumentDto, List<ContingutDto>>();

					List<MetaDocumentEntity> metaDocuments = metaDocumentRepository.findByMetaExpedientAndActiuTrueOrderByOrdreAsc(expedient.getMetaExpedient());
					
					for (MetaDocumentEntity metaDocument : metaDocuments) {
						
						List<DocumentEntity> documents = documentRepository.findByExpedientAndMetaNodeAndEsborrat(
								expedient,
								metaDocument,
								0);
						
						MetaDocumentDto metaDocumentDto = conversioTipusHelper.convertir(metaDocument, MetaDocumentDto.class);
						
						
						List<ContingutDto> docsDtos = new ArrayList<ContingutDto>(); 
						if (CollectionUtils.isNotEmpty(documents)) {
							for (DocumentEntity document : documents) {
								
								docsDtos.add(
										toContingutDto(
												document,
												ambPermisos,
												false,
												false,
												ambPath,
												false,
												false,
												rolActual,
												onlyForList,
												organActualId,
												onlyFirstDescendant,
												level,
												null,
												null,
												ambExpedientPare,
												ambEntitat,
												false,
												ambMapPerEstat));
								
							}
						} 
						mapPerTipusDocument.put(metaDocumentDto, docsDtos);
						
					}
					dto.setMapPerTipusDocument(mapPerTipusDocument);
					
					if (cacheHelper.mostrarLogsRendiment())
						logger.info("ambMapPerTipusDocument end (" + contingut.getId() + "):  " + (System.currentTimeMillis() - t2) + " ms");
				}		
				if (ambMapPerEstat) {
					if (cacheHelper.mostrarLogsRendiment())
						logger.info("ambMapPerEstat start (" + contingut.getId() + ")");
					long t2 = System.currentTimeMillis();

					Map<ExpedientEstatDto, List<ContingutDto>> mapPerEstat = new LinkedHashMap<ExpedientEstatDto, List<ContingutDto>>();

					List<ExpedientEstatEntity> expedientEstats = expedientEstatRepository.findByMetaExpedientOrderByOrdreAsc(expedient.getMetaExpedient());
					
					for (ExpedientEstatEntity expedientEstat : expedientEstats) {
						
						List<DocumentEntity> documents = documentRepository.findByExpedientAndExpedientEstatAdditionalAndEsborrat(
								expedient,
								expedientEstat,
								0);
						
						ExpedientEstatDto expedientEstatDto = conversioTipusHelper.convertir(expedientEstat, ExpedientEstatDto.class);
						
						
						List<ContingutDto> docsDtos = new ArrayList<ContingutDto>(); 
						if (CollectionUtils.isNotEmpty(documents)) {
							for (DocumentEntity document : documents) {
								
								docsDtos.add(
										toContingutDto(
												document,
												ambPermisos,
												false,
												false,
												ambPath,
												false,
												false,
												rolActual,
												onlyForList,
												organActualId,
												onlyFirstDescendant,
												level,
												null,
												null,
												ambExpedientPare,
												ambEntitat,
												false,
												false));
								
								
							}
						} 
						mapPerEstat.put(expedientEstatDto, docsDtos);
						
					}
					dto.setMapPerEstat(mapPerEstat);
					
					if (cacheHelper.mostrarLogsRendiment())
						logger.info("ambMapPerEstat end (" + contingut.getId() + "):  " + (System.currentTimeMillis() - t2) + " ms");
				}	
				

				
			}
			
			if (cacheHelper.mostrarLogsRendiment())
				logger.info("toExpedientDto end (" + expedient.getId() + "):  " + (System.currentTimeMillis() - t1) + " ms");

			resposta = dto;
		// ##################### DOCUMENT ##################################
		} else if (deproxied instanceof DocumentEntity) {
			if (cacheHelper.mostrarLogsRendiment())
				logger.info("toDocumentDto start (" + contingut.getId() + ", level=" + level + ") ");
			long t2 = System.currentTimeMillis();
			long t10 = System.currentTimeMillis();
			DocumentEntity document = (DocumentEntity)deproxied;
			DocumentDto dto = new DocumentDto();
			dto.setDescripcio(document.getDescripcio());
			dto.setDocumentTipus(document.getDocumentTipus());
			dto.setEstat(document.getEstat());
			dto.setUbicacio(document.getUbicacio());
			dto.setData(document.getData());
			dto.setCustodiaData(document.getCustodiaData());
			dto.setCustodiaId(document.getCustodiaId());
			if (document.getFitxerNom() != null) {
				dto.setFitxerNom(document.getFitxerNom());
				dto.setFitxerNomEnviamentPortafirmes(
						pluginHelper.conversioConvertirPdfArxiuNom(document.getFitxerNom()));
			}
			dto.setFitxerContentType(document.getFitxerContentType());
			dto.setFitxerTamany(document.getFitxerTamany());
			dto.setDataCaptura(document.getDataCaptura());
			dto.setVersioDarrera(document.getVersioDarrera());
			dto.setVersioCount(document.getVersioCount());
			if (ambVersions && pluginHelper.arxiuSuportaVersionsDocuments() && document.getEsborrat() == 0) {
				List<ContingutArxiu> arxiuVersions = pluginHelper.arxiuDocumentObtenirVersions(document);
				if (arxiuVersions != null) {
					List<DocumentVersioDto> versions = new ArrayList<DocumentVersioDto>();
					for (ContingutArxiu arxiuVersio: arxiuVersions) {
						DocumentVersioDto versio = new DocumentVersioDto();
						versio.setArxiuUuid(arxiuVersio.getIdentificador());
						versio.setId(arxiuVersio.getVersio());
						versions.add(versio);
					}
					dto.setVersions(versions);
				}
			}
			if (cacheHelper.mostrarLogsRendiment())
				logger.info("toDocumentDto 1/3 time (" + contingut.getId() + "):  " + (System.currentTimeMillis() - t10) + " ms");
			long t20 = System.currentTimeMillis();
			dto.setNtiVersion(document.getNtiVersion());
			dto.setNtiIdentificador(document.getNtiIdentificador());
			dto.setNtiOrgano(document.getNtiOrgano());
			dto.setNtiOrganoDescripcio(document.getNtiOrgano());
			dto.setNtiOrigen(document.getNtiOrigen());
			dto.setNtiEstadoElaboracion(document.getNtiEstadoElaboracion());
			dto.setNtiTipoDocumental(document.getNtiTipoDocumental());
			if (document.getNtiTipoDocumental() != null) {
				TipusDocumentalEntity tipusDocumental = tipusDocumentalRepository.findByCodiAndEntitat(
						document.getNtiTipoDocumental(),
						contingut.getEntitat());

				if (tipusDocumental != null) {
	            	if (LocaleContextHolder.getLocale().toString().equals("ca") && Utils.isNotEmpty(tipusDocumental.getNomCatala())) {
	            		dto.setNtiTipoDocumentalNom(tipusDocumental.getNomCatala());
					} else {
						dto.setNtiTipoDocumentalNom(tipusDocumental.getNomEspanyol());
					}
				} else {
					List<TipusDocumentalDto> docsAddicionals = pluginHelper.documentTipusAddicionals();

					for (TipusDocumentalDto docAddicional : docsAddicionals) {
						if (docAddicional.getCodi().equals(document.getNtiTipoDocumental())) {
							dto.setNtiTipoDocumentalNom(docAddicional.getNom());
						}
					}
				}
			}
			dto.setNtiIdDocumentoOrigen(document.getNtiIdDocumentoOrigen());
			dto.setNtiTipoFirma(document.getNtiTipoFirma());
			dto.setNtiCsv(document.getNtiCsv());
			dto.setNtiCsvRegulacion(document.getNtiCsvRegulacion());
//			dto.setAmbNotificacions(document.isAmbNotificacions());
			dto.setAmbNotificacions(documentNotificacioRepository.countByDocument(document) > 0);
//			dto.setEstatDarreraNotificacio(document.getEstatDarreraNotificacio());
			DocumentNotificacioEstatEnumDto estatDarreraNotificacio = documentNotificacioRepository.findLastEstatNotificacioByDocument(document);
			dto.setEstatDarreraNotificacio(estatDarreraNotificacio != null ? estatDarreraNotificacio.name() : "");
//			dto.setErrorDarreraNotificacio(document.isErrorDarreraNotificacio());
			Boolean isErrorLastNotificacio = documentNotificacioRepository.findErrorLastNotificacioByDocument(document);
			dto.setErrorDarreraNotificacio(isErrorLastNotificacio != null ? isErrorLastNotificacio : false);
//			dto.setErrorEnviamentPortafirmes(document.isErrorEnviamentPortafirmes());
			Boolean isErrorLastEnviament = documentPortafirmesRepository.findErrorLastEnviamentPortafirmesByDocument(document);
			dto.setErrorEnviamentPortafirmes(isErrorLastEnviament != null ? isErrorLastEnviament : false);
			dto.setGesDocFirmatId(document.getGesDocFirmatId());
			dto.setGesDocAdjuntId(document.getGesDocAdjuntId());
			dto.setGesDocAdjuntFirmaId(document.getGesDocAdjuntFirmaId());

			dto.setDocFromAnnex(document.isDocFromAnnex()); 
			dto.setEstat(document.getEstat());
			
			if (document.getAnnexos() != null && !document.getAnnexos().isEmpty()) {
				RegistreAnnexEntity annex = document.getAnnexos().get(0);
				String error = annex.getError();
				if (error != null && !error.isEmpty()) {
					dto.setPendentMoverArxiu(true);
				}
				dto.setAnnexId(annex.getId());
				dto.setDocumentDeAnotacio(true);
			}

			metaNode = conversioTipusHelper.convertir(
					document.getMetaNode(),
					MetaDocumentDto.class);
			dto.setMetaNode(metaNode);
			if (cacheHelper.mostrarLogsRendiment())
				logger.info("toDocumentDto 2/3 time (" + contingut.getId() + "):  " + (System.currentTimeMillis() - t20) + " ms");

			long t3 = System.currentTimeMillis();
			dto.setValid(
					cacheHelper.findErrorsValidacioPerNode(document).isEmpty());
			dto.setValidacioFirmaCorrecte(document.isValidacioFirmaCorrecte());
			dto.setValidacioFirmaErrorMsg(document.getValidacioFirmaErrorMsg());
			dto.setEstat(document.getEstat());
			dto.setArxiuEstat(document.getArxiuEstat());
			dto.setArxiuEstatDefinitiu(document.isArxiuEstatDefinitiu());
			dto.setDocumentFirmaTipus(document.getDocumentFirmaTipus());
			
			resposta = dto;
			if (cacheHelper.mostrarLogsRendiment())
				logger.info("toDocumentDto 3/3 time (" + contingut.getId() + "):  " + (System.currentTimeMillis() - t3) + " ms");

			if (cacheHelper.mostrarLogsRendiment())
				logger.info("toDocumentDto end (" + document.getId() + "):  " + (System.currentTimeMillis() - t2) + " ms");
		// ##################### CARPETA ##################################
		} else if (deproxied instanceof CarpetaEntity) {
			if (cacheHelper.mostrarLogsRendiment())
				logger.info("toCarpetaDto start (" + contingut.getId() + ", level=" + level + ") ");
			
			long t2 = System.currentTimeMillis();
			CarpetaDto dto = new CarpetaDto();
			CarpetaEntity carpeta = (CarpetaEntity)deproxied;
			if (carpeta.getExpedientRelacionat() != null)
				dto.setExpedientRelacionat(
						(ExpedientDto)toContingutDto(
								carpeta.getExpedientRelacionat(),
								false,
								false,
								false,
								false,
								false,
								false,
								null,
								true,
								null,
								onlyFirstDescendant,
								level,
								null,
								null,
								ambExpedientPare,
								ambEntitat,
								ambMapPerTipusDocument,
								ambMapPerEstat));
			
//			boolean conteDocsDef = conteDocumentsDefinitius(contingut);
			Boolean conteDocsDef = documentRepository.carpetaHasDocumentsDefinitius(carpeta);
			dto.setConteDocumentsDefinitius(conteDocsDef);
			resposta = dto;
			
			if (cacheHelper.mostrarLogsRendiment())
				logger.info("toCarpetaDto end (" + carpeta.getId() + "):  " + (System.currentTimeMillis() - t2) + " ms");
		}

		
		// ##################### CONTINGUT ##################################
		long t3 = System.currentTimeMillis();
		
		long t234 = System.currentTimeMillis();
		String tipus = contingut.getClass().toString().replace("class es.caib.ripea.core.entity.", "").replace("Entity", "").toLowerCase();
		if (cacheHelper.mostrarLogsRendiment())
			logger.info("propertiesContingut1 time (" + contingut.getId() + "):  " + (System.currentTimeMillis() - t234) + " ms");
		if (cacheHelper.mostrarLogsRendiment())
			logger.info("toContingutDto[" + tipus + "] start (" + contingut.getId() + ", level=" + level + ") ");
		
		long t23 = System.currentTimeMillis();
		resposta.setId(contingut.getId());
		resposta.setNom(contingut.getNom());
		resposta.setArxiuUuid(contingut.getArxiuUuid());
		resposta.setCreatedDate(contingut.getCreatedDate().toDate());
		if (cacheHelper.mostrarLogsRendiment())
			logger.info("propertiesContingut2 time (" + contingut.getId() + "):  " + (System.currentTimeMillis() - t23) + " ms");

		
		long t22 = System.currentTimeMillis();
		resposta.setAlerta(
				alertaRepository.countByLlegidaAndContingutId(
				false,
				contingut.getId()) > 0);
		if (cacheHelper.mostrarLogsRendiment())
			logger.info("setAlerta time (" + contingut.getId() + "):  " + (System.currentTimeMillis() - t22) + " ms");

		if (!onlyForList) {

			long t2341 = System.currentTimeMillis();

			resposta.setEsborrat(contingut.getEsborrat());
			resposta.setEsborratData(contingut.getEsborratData());
			resposta.setArxiuDataActualitzacio(contingut.getArxiuDataActualitzacio());

			Boolean hasFills = contingutRepository.hasFills(
					contingut, 
					0);
			if (hasFills) {
				resposta.setHasFills(true);
			} else {
				resposta.setHasFills(false);
			}
			if (cacheHelper.mostrarLogsRendiment())
				logger.info("propertiesContingut3 time (" + contingut.getId() + "):  " + (System.currentTimeMillis() - t2341) + " ms");

			
			if (ambEntitat) {
				long t2342 = System.currentTimeMillis();
				resposta.setEntitat(
						conversioTipusHelper.convertir(
								contingut.getEntitat(),
									EntitatDto.class));
				if (cacheHelper.mostrarLogsRendiment())
					logger.info("propertiesContingut4 time (" + contingut.getId() + "):  " + (System.currentTimeMillis() - t2342) + " ms");
			}

			
			if (contingut.getDarrerMoviment() != null) {
				long t1 = System.currentTimeMillis();
				ContingutMovimentEntity darrerMoviment = contingut.getDarrerMoviment();
				resposta.setDarrerMovimentUsuari(
						conversioTipusHelper.convertir(
								darrerMoviment.getRemitent(),
								UsuariDto.class));
				resposta.setDarrerMovimentData(darrerMoviment.getCreatedDate().toDate());
				resposta.setDarrerMovimentComentari(darrerMoviment.getComentari());
				if (cacheHelper.mostrarLogsRendiment())
					logger.info("propertiesContingut5 time (" + contingut.getId() + "):  " + (System.currentTimeMillis() - t1) + " ms");
			}

			if (ambPermisos) {
				
				long t2 = System.currentTimeMillis();
				resposta.setAdmin(checkIfUserIsAdminOfContingut(contingut.getId(), rolActual));
				
				if (cacheHelper.mostrarLogsRendiment())
					logger.info("ambPermisos time (" + contingut.getId() + "):  " + (System.currentTimeMillis() - t2) + " ms");
				
			}

			long t1 = System.currentTimeMillis();
			// Omple la informació d'auditoria
			resposta.setCreatedBy(
					conversioTipusHelper.convertir(
							contingut.getCreatedBy(),
							UsuariDto.class));
			resposta.setLastModifiedBy(
					conversioTipusHelper.convertir(
							contingut.getLastModifiedBy(),
							UsuariDto.class));
			resposta.setLastModifiedDate(contingut.getLastModifiedDate().toDate());
			if (cacheHelper.mostrarLogsRendiment())
				logger.info("propertiesContingut6 time (" + contingut.getId() + "):  " + (System.currentTimeMillis() - t1) + " ms");
			

			if (ambDades && contingut instanceof NodeEntity) {
				long t2 = System.currentTimeMillis();
				NodeEntity node = (NodeEntity)contingut;
				List<DadaEntity> dades = dadaRepository.findByNode(node);
				((NodeDto)resposta).setDades(
						conversioTipusHelper.convertirList(
								dades,
								DadaDto.class));
				for (int i = 0; i < dades.size(); i++) {
					((NodeDto)resposta).getDades().get(i).setValor(dades.get(i).getValor());
				}
				if (cacheHelper.mostrarLogsRendiment())
					logger.info("ambDades time (" + contingut.getId() + "):  " + (System.currentTimeMillis() - t2) + " ms");
			}
			
			
			
			ExpedientDto expedientCalculat = null;
			if (ambExpedientPare) {
				if (contingut instanceof ExpedientEntity) { //if is expedient
					expedientCalculat = (ExpedientDto) resposta;
				} else {
					if (expedientDto != null) { //if is called recursively from pare that already calculated expedient
						expedientCalculat = expedientDto;
					} else { //if is not called recursively from pare that already calculated expedient, calculate expedient now
						long t2 = System.currentTimeMillis();
						if (cacheHelper.mostrarLogsRendiment())
							logger.info("expedientPare (recursive) start (" + contingut.getId() + ") " );
	
						expedientCalculat = (ExpedientDto) toContingutDto(
								contingut.getExpedient(),
								ambPermisos,
								false,
								true,
								false,
								false,
								false,
								rolActual,
								onlyForList,
								organActualId,
								onlyFirstDescendant,
								level,
								null,
								null,
								ambExpedientPare, 
								ambEntitat, 
								ambMapPerTipusDocument, 
								ambMapPerEstat);
						if (cacheHelper.mostrarLogsRendiment())
							logger.info("expedientPare (recursive) end (" + contingut.getId() + "):  " + (System.currentTimeMillis() - t2) + " ms");
					}
					resposta.setExpedientPare(expedientCalculat);
				}
			}
			
			List<ContingutDto> pathCalculatPerFills = null;
			List<ContingutDto> pathCalculatPerThisContingut = null;
			if (ambPath) {
				if (contingut instanceof ExpedientEntity) { //if is expedient
					pathCalculatPerThisContingut = null;
				} else {
					if (pathDto != null) { //if is called recursively from pare that already calculated path
						pathCalculatPerThisContingut = pathDto;
					} else { //if is not called recursively from pare that already calculated path, calculate path now
						long t2 = System.currentTimeMillis();
						if (cacheHelper.mostrarLogsRendiment())
							logger.info("ambPath (recursive) start (" + contingut.getId() + ") " );
						pathCalculatPerThisContingut = getPathContingutComDto(
								contingut,
								ambPermisos,
								pathNomesFinsExpedientArrel, level);
						if (cacheHelper.mostrarLogsRendiment())
							logger.info("ambPath (recursive) end (" + contingut.getId() + "):  " + (System.currentTimeMillis() - t2) + " ms");
						
					}
				}
				resposta.setPath(pathCalculatPerThisContingut);
			
			
				pathCalculatPerFills = new ArrayList<>();
				if (pathCalculatPerThisContingut != null) {
					pathCalculatPerFills.addAll(pathCalculatPerThisContingut);
				}
				pathCalculatPerFills.add(resposta);

			}
			
			if (ambFills) {
				if (cacheHelper.mostrarLogsRendiment())
					logger.info("ambFills (recursive) start (" + contingut.getId() + ")");
				
				long t2 = System.currentTimeMillis();
				// Cerca els nodes fills
				Authentication auth = SecurityContextHolder.getContext().getAuthentication();

				List<ContingutEntity> fills = new ArrayList<ContingutEntity>();
				
				if (isOrdenacioPermesa()) {
					fills = contingutRepository.findByPareAndEsborratAndOrdenatOrdre(contingut, 0);
				} else {
					fills = contingutRepository.findByPareAndEsborratAndOrdenat(contingut, 0);
				}
				
				List<ContingutDto> fillsDtos = new ArrayList<ContingutDto>();
				for (ContingutEntity fill: fills) {
					if (fill.getEsborrat() == 0) {
						ContingutDto fillDto = toContingutDto(
								fill,
								ambPermisos,
								onlyFirstDescendant ? false : true,
								false,
								ambPath,
								false,
								false,
								rolActual,
								onlyForList,
								organActualId,
								onlyFirstDescendant,
								level,
								expedientCalculat,
								pathCalculatPerFills,
								ambExpedientPare,
								ambEntitat,
								ambMapPerTipusDocument,
								ambMapPerEstat);
						// Configura el pare de cada fill
						fillsDtos.add(fillDto);
					}
				}

				resposta.setFills(fillsDtos);
				
				if (cacheHelper.mostrarLogsRendiment())
					logger.info("ambFills (recursive) end (" + contingut.getId() + "):  " + (System.currentTimeMillis() - t2) + " ms");
			}


		}
		if (cacheHelper.mostrarLogsRendiment())
			logger.info("toContingutDto[" + tipus + "] end (" + contingut.getId() + ", level=" + level + "): "+ (System.currentTimeMillis() - t3) + " ms");
		return resposta;
	}
	
	
	
	public void omplirPermisosPerExpedient(
			ExpedientDto dto, 
			String rolActual, 
			Long expedientId) {

		
		long t10 = System.currentTimeMillis();
		try {
			dto.setUsuariActualWrite(false);
			
			
			entityComprovarHelper.comprovarExpedient(
					expedientId,
					false,
					false,
					true,
					false,
					false,
					null);
			
			dto.setUsuariActualWrite(true);
		} catch (PermissionDeniedException ex) {
		}

		try {
			dto.setUsuariActualDelete(false);
			entityComprovarHelper.comprovarExpedient(
					expedientId,
					false,
					false,
					false,
					false,
					true,
					null);
			dto.setUsuariActualDelete(true);
		} catch (PermissionDeniedException ex) {
		}
		if (cacheHelper.mostrarLogsRendiment())
			logger.info("toExpedientDto comprovarPermisos time:  " + (System.currentTimeMillis() - t10) + " ms");
	

	}
	
	
	public ContingutDto getBasicInfo(ContingutEntity contingut) {
		ContingutEntity deproxied = HibernateHelper.deproxy(contingut);
		ContingutDto resposta = null;
		if (deproxied instanceof ExpedientEntity) {
			ExpedientDto expedient = new ExpedientDto();
			resposta = expedient;
		} else if (deproxied instanceof CarpetaEntity) {
			CarpetaDto carpeta = new CarpetaDto();
			resposta = carpeta;
		} else if (deproxied instanceof DocumentEntity) {
			DocumentDto carpeta = new DocumentDto();
			resposta = carpeta;
		}
		resposta.setId(contingut.getId());
		resposta.setNom(contingut.getNom());
		
		return resposta;
	}
	
	
	public ContingutDto toContingutDtoSimplificat(ContingutEntity contingut, boolean nomesFinsExpedientArrel, List<ContingutDto> pathDto) {
		ContingutDto resposta = null;		
		if (contingut instanceof ExpedientEntity) {
			ExpedientDto expedient = new ExpedientDto();
			resposta = expedient;
		}
		
		if (contingut instanceof CarpetaEntity) {
			CarpetaDto carpeta = new CarpetaDto();
			resposta = carpeta;
		}
		
		List<ContingutDto> pathCalculatPerThisContingut = null;
		if (contingut instanceof ExpedientEntity) {
			pathCalculatPerThisContingut = null;
		} else {
			if (pathDto != null) { // if is called recursively from pare that already calculated path
				pathCalculatPerThisContingut = pathDto;
			} else {
				pathCalculatPerThisContingut = getPathContingutComDto(contingut, nomesFinsExpedientArrel);
			}
		}
		
		resposta.setId(contingut.getId());
		resposta.setNom(contingut.getNom());
		resposta.setPath(pathCalculatPerThisContingut);

		List<ContingutEntity> fills = new ArrayList<ContingutEntity>();
		if (isOrdenacioPermesa()) {
			fills = contingutRepository.findByPareAndEsborratAndOrdenatOrdre(contingut, 0);
		} else {
			fills = contingutRepository.findByPareAndEsborratAndOrdenat(contingut, 0);
		}
		
		List<ContingutDto> fillsDto = new ArrayList<ContingutDto>();
		for (ContingutEntity fill: fills) {
			if (fill instanceof CarpetaEntity) {
				CarpetaDto carpeta = new CarpetaDto();
				carpeta.setId(fill.getId());
				carpeta.setNom(fill.getNom());
				fillsDto.add(carpeta);
			}
			if (fill instanceof DocumentEntity) {
				DocumentDto document = new DocumentDto();
				document.setId(fill.getId());
				document.setNom(fill.getNom());
				document.setDocumentTipus(((DocumentEntity) fill).getDocumentTipus());
				fillsDto.add(document);
			}
		}
		resposta.setFills(fillsDto);
		return resposta;
	}

	public boolean checkIfUserIsAdminOfContingut(Long contingutId, String rolActual) {

		ContingutEntity contingut = contingutRepository.findOne(contingutId);
		boolean admin = false;
		if (rolActual != null) {
			if (rolActual.equals("IPA_ADMIN")) {
				admin = permisosHelper.isGrantedAll(
						contingut.getEntitat().getId(),
						EntitatEntity.class,
						new Permission[] { ExtendedPermission.ADMINISTRATION },
						SecurityContextHolder.getContext().getAuthentication());
			}
			if (rolActual.equals("IPA_ORGAN_ADMIN")) {
				if (contingut.getExpedientPare().getOrganGestor() != null) {
					boolean grantedOrgan = false;
					List<OrganGestorEntity> organsGestors = organGestorHelper.findPares(contingut.getExpedientPare().getOrganGestor(), true);
					permisosHelper.filterGrantedAny(
							organsGestors,
							OrganGestorEntity.class,
							new Permission[] { ExtendedPermission.ADMINISTRATION });
					grantedOrgan = !organsGestors.isEmpty();
					if (grantedOrgan) {
						admin = true;
					}
				}
			}
		}
		return admin;
	}

	public DocumentDto generarDocumentDto(
			DocumentEnviamentInteressatEntity documentEnviamentInteressatEntity,
			MetaDocumentEntity metaDocument,
			RespostaConsultaEstatEnviament resposta) {
		DocumentDto dto = new DocumentDto();
		MetaNodeDto metaNode = null;
		String interessatNif = null;
		String interessatNom = null;

		DocumentNotificacioEntity notificacio = documentEnviamentInteressatEntity.getNotificacio();
		InteressatEntity interessat = HibernateHelper.deproxy(documentEnviamentInteressatEntity.getInteressat());
		if (interessat instanceof InteressatPersonaFisicaEntity) {
			InteressatPersonaFisicaEntity interessatPf = (InteressatPersonaFisicaEntity)interessat;
			interessatNif = interessatPf.getDocumentNum();
			interessatNom = interessatPf.getNom() + " " + interessatPf.getLlinatge1();
			String llinatge2 = interessatPf.getLlinatge2();
			interessatNom += (llinatge2 != null && !llinatge2.isEmpty()) ? " " + llinatge2 : "";
		} else if (interessat instanceof InteressatPersonaJuridicaEntity) {
			InteressatPersonaJuridicaEntity interessatPj = (InteressatPersonaJuridicaEntity)interessat;
			interessatNif = interessatPj.getDocumentNum();
			interessatNom = interessatPj.getRaoSocial();
		} else if (interessat instanceof InteressatAdministracioEntity) {
			InteressatAdministracioEntity interessatA = (InteressatAdministracioEntity)interessat;
			interessatNif = interessatA.getDocumentNum();
			interessatNom = interessatA.getOrganNom();
		}

		if (interessatNif != null && interessatNom != null)
			dto.setNom("Certificació_" + notificacio.getAssumpte().replaceAll("\\s+","_") + "-" + interessatNif + "-" + interessatNom);
		else
			dto.setNom("Certificació_" + notificacio.getAssumpte().replaceAll("\\s+","_"));
		dto.setDocumentTipus(DocumentTipusEnumDto.DIGITAL);
		dto.setUbicacio(null);
		dto.setData(resposta.getCertificacioData());
		if (resposta.getCertificacioContingut() != null) {
			logger.debug("[CERT] Generant fitxer certificació...");
			if (interessatNif != null && interessatNom != null)
				dto.setFitxerNom("Certificació_" + notificacio.getAssumpte().replaceAll("\\s+","_") + "-" + interessatNif + "-" + interessatNom + ".pdf");
			else
				dto.setFitxerNom("Certificació_" + notificacio.getAssumpte().replaceAll("\\s+","_") + ".pdf");
			dto.setFitxerContentType("application/pdf");
			dto.setFitxerContingut(resposta.getCertificacioContingut());
			dto.setFitxerTamany(new Long(resposta.getCertificacioContingut().length));
			logger.debug("[CERT] El fitxer s'ha generat correctament amb nom: " + dto.getFitxerNom());

//			## Comprovar si la certificació està firmada
			if (isCertificacioAmbFirma(resposta.getCertificacioContingut())) {
				dto.setAmbFirma(true);
			}
		}
		dto.setVersioCount(0);
		dto.setDataCaptura(new Date());
		dto.setNtiVersion("1.0");
		dto.setNtiIdentificador(resposta.getCertificacioHash());
		dto.setNtiOrgano(resposta.getReceptorNif());
		dto.setNtiOrganoDescripcio(resposta.getReceptorNom());
		dto.setNtiOrigen(metaDocument.getNtiOrigen());
		dto.setNtiEstadoElaboracion(metaDocument.getNtiEstadoElaboracion());
		dto.setNtiTipoDocumental(metaDocument.getNtiTipoDocumental());

		dto.setNtiCsv(resposta.getCertificacioCsv());

		metaNode = conversioTipusHelper.convertir(
				metaDocument,
				MetaDocumentDto.class);
		dto.setMetaNode(metaNode);
		return dto;
	}

	public NodeEntity comprovarNodeDinsExpedientModificable(
			Long entitatId,
			Long contingutId,
			boolean comprovarPermisRead,
			boolean comprovarPermisWrite,
			boolean comprovarPermisCreate,
			boolean comprovarPermisDelete,
			boolean checkPerMassiuAdmin,
			String rolActual) {
		ContingutEntity contingut = comprovarContingutDinsExpedientModificable(
				entitatId,
				contingutId,
				comprovarPermisRead,
				comprovarPermisWrite,
				comprovarPermisCreate,
				comprovarPermisDelete,
				checkPerMassiuAdmin,
				true, rolActual);
		if (!(contingut instanceof NodeEntity)) {
			throw new ValidationException(
					contingut.getId(),
					ContingutEntity.class,
					"El contingut no és un node");
		}
		return (NodeEntity)contingut;
	}


	public ContingutEntity comprovarContingutDinsExpedientModificable(
			Long entitatId,
			Long contingutId,
			boolean comprovarPermisRead,
			boolean comprovarPermisWrite,
			boolean comprovarPermisCreate,
			boolean comprovarPermisDelete,
			boolean checkPerMassiuAdmin,
			boolean comprovarAgafatPerUsuariActual, 
			String rolActual) {
		entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				false,
				false,
				true, false);
		ContingutEntity contingut = entityComprovarHelper.comprovarContingut(
				contingutId);
		contingut = HibernateHelper.deproxy(contingut);
		// Comprova el permís de modificació de l'expedient superior
		ExpedientEntity expedient = getExpedientSuperior(
				contingut,
				true,
				false,
				true,
				rolActual);
		if (expedient == null) {
			throw new ValidationException(
					contingutId,
					ContingutEntity.class,
					"No es pot modificar un contingut que no està associat a un expedient");
		}

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		if (!checkPerMassiuAdmin && !checkIfUserIsAdminOfContingut(contingutId, rolActual) && !RolHelper.isAdminEntitat(rolActual) && !RolHelper.isAdminOrgan(rolActual) && comprovarAgafatPerUsuariActual) {
			// Comprova que l'usuari actual te agafat l'expedient
			UsuariEntity agafatPer = expedient.getAgafatPer();
			if (agafatPer == null) {
				throw new ValidationException(
						contingutId,
						ContingutEntity.class,
						"L'expedient al qual pertany el contingut no està agafat per cap usuari");
			}

			if (!auth.getName().equals(agafatPer.getCodi())) {
				throw new ValidationException(
						contingutId,
						ContingutEntity.class,
						"L'expedient al qual pertany el contingut no està agafat per l'usuari actual (" +
						"usuariActualCodi=" + auth.getName() + ")");
			}
		}


		if (ContingutTipusEnumDto.EXPEDIENT.equals(contingut.getTipus())) {
			ExpedientEntity expedientEntity = (ExpedientEntity)contingut;
			if (comprovarPermisWrite) {
				// if expedient estat has write permissions don't need to check metaExpedient permissions
				if (comprovarPermisWrite && expedientEntity.getEstatAdditional() != null) {
					if (hasEstatPermissons(expedientEntity.getEstatAdditional().getId()))
						comprovarPermisWrite = false;
				}
			}
			
			
			entityComprovarHelper.comprovarExpedient(
					expedientEntity.getId(),
					false,
					comprovarPermisRead,
					comprovarPermisWrite,
					comprovarPermisCreate,
					comprovarPermisDelete,
					null);

		}
		return contingut;
	}

	public NodeEntity comprovarNodeDinsExpedientAccessible(
			Long entitatId,
			Long contingutId,
			boolean comprovarPermisRead,
			boolean comprovarPermisWrite) {
		ContingutEntity contingut = comprovarContingutDinsExpedientAccessible(
				entitatId,
				contingutId,
				comprovarPermisRead,
				comprovarPermisWrite);
		if (!(contingut instanceof NodeEntity)) {
			throw new ValidationException(
					contingut.getId(),
					ContingutEntity.class,
					"El contingut no és un node");
		}
		return (NodeEntity)contingut;
	}

	public ContingutEntity comprovarContingutDinsExpedientAccessible(
			Long entitatId,
			Long contingutId,
			boolean comprovarPermisRead,
			boolean comprovarPermisWrite) {
		entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				false,
				false,
				true, false);
		ContingutEntity contingut = entityComprovarHelper.comprovarContingut(
				contingutId);
		// Comprova el permís de lectura de l'expedient superior
		getExpedientSuperior(
				contingut,
				true,
				false,
				false,
				null);
		if (ContingutTipusEnumDto.EXPEDIENT.equals(contingut.getTipus())) {
			
			entityComprovarHelper.comprovarExpedient(
					contingut.getId(),
					false,
					comprovarPermisRead,
					comprovarPermisWrite,
					false,
					false,
					null);

		}
		return contingut;
	}

	
	public DocumentEntity comprovarDocumentPerTasca(
			Long tascaId,
			Long documentId) {
		
		comprovarContingutPertanyTascaAccesible(tascaId, documentId);
		DocumentEntity document = documentRepository.findOne(
				documentId);

		return document;
	}

	public ContingutEntity comprovarContingutPertanyTascaAccesible(
			Long tascaId,
			Long contingutId) {

		ContingutEntity contingut = entityComprovarHelper.comprovarContingut(
				contingutId);
		
		entityComprovarHelper.comprovarEntitat(
				contingut.getEntitat().getId(),
				true,
				false,
				false, 
				false, 
				false);
		
		ExpedientTascaEntity expedientTascaEntity = expedientTascaRepository.findOne(tascaId);
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		if (!expedientTascaEntity.getExpedient().getId().equals(contingut.getExpedientPare().getId())) {
			throw new SecurityException("Contingut no pertany a la tasca accesible("
					+ "tascaId=" + expedientTascaEntity.getId() + ", "
					+ "contingutId=" + contingutId + ", "
					+ "usuari=" + auth.getName() + ")");
		}

		if (expedientTascaEntity.getResponsables() != null) {
			boolean pemitted = false;
			for (UsuariEntity responsable : expedientTascaEntity.getResponsables()) {
				if (responsable.getCodi().equals(auth.getName())) {
					pemitted = true;
				}
			}
			if (!pemitted) {
				throw new SecurityException("Sense permisos per accedir la tasca ("
						+ "tascaId=" + expedientTascaEntity.getId() + ", "
						+ "usuari=" + auth.getName() + ")");
			}
		}

		return contingut;
	}
	
	
	public NodeEntity comprovarNodePertanyTascaAccesible(
			Long tascaId,
			Long contingutId) {
		ContingutEntity contingut = comprovarContingutPertanyTascaAccesible(
				tascaId,
				contingutId);
		if (!(contingut instanceof NodeEntity)) {
			throw new ValidationException(
					contingut.getId(),
					ContingutEntity.class,
					"El contingut no és un node");
		}
		return (NodeEntity)contingut;
	}



	public void deleteReversible(
			Long entitatId,
			ContingutEntity contingut,
			String rolActual) throws IOException {
		
		organGestorHelper.actualitzarOrganCodi(organGestorHelper.getOrganCodiFromContingutId(contingut.getId()));

		// Comprova que el contingut no estigui esborrat
		if (contingut.getEsborrat() > 0) {
			logger.error("Aquest contingut ja està esborrat (contingutId=" + contingut.getId() + ")");
			throw new ValidationException(
					contingut.getId(),
					ContingutEntity.class,
					"Aquest contingut ja està esborrat");
		}

		// Actualitza camp registres importats per no deixar informació inconsistent durant la cerca per número de registre
		ExpedientEntity expedientPare = contingut.getExpedientPare();
		if (contingut.getNumeroRegistre() != null && expedientPare != null && expedientPare.getRegistresImportats() != null) { // Importat
			String[] registresImportatsArr = expedientPare.getRegistresImportats().split(",");
			
			List<String> registresImportats = new ArrayList<>();
			
			if (registresImportatsArr.length > 1)
				registresImportats = new ArrayList<>(Arrays.asList(registresImportatsArr));
			else	
				registresImportats.add(registresImportatsArr[0]);
				
			// Si és carpeta, esborrar número registre de l'expedient
			if (contingut instanceof CarpetaEntity)
				registresImportats.remove(contingut.getNumeroRegistre());
			
			// Si és document, comprovar que no hi ha hagi més documents relacionats amb el mateix registre
			if (contingut instanceof DocumentEntity) {
				boolean hasMultiplesDocumentsImportats = contingutRepository.hasMultiplesDocumentsImportatsRegistre(
						expedientPare, 
						contingut.getNumeroRegistre());
				
				// Si només hi ha un document importat, llavors esborrar el seu número de l'expedient
				if (! hasMultiplesDocumentsImportats) 
					registresImportats.remove(contingut.getNumeroRegistre());
			}
				
			expedientPare.removeRegistresImportats();
			
			for (String numeroRegistre : registresImportats) {
				expedientPare.updateRegistresImportats(numeroRegistre);
			}
		}
		
		if ((conteDocumentsDefinitius(contingut) && isPermesEsborrarFinals()) || !conteDocumentsDefinitius(contingut)) {
			// Marca el contingut i tots els seus fills com a esborrats
			//  de forma recursiva
			marcarEsborrat(contingut);
		} else {
			logger.error("Aquest contingut és definitiu o conté definitius i no es pot esborrar (contingutId=" + contingut.getId() + ")");
			throw new ValidationException(
					contingut.getId(),
					ContingutEntity.class,
					"Un contingut definitiu no es pot esborrar, verificau la propitat es.caib.ripea.document.esborrar.finals");
		}
		
		// Cancel·lar enviament si el document conté enviaments pendents
		if (contingut instanceof DocumentEntity) {
			DocumentEntity document = (DocumentEntity)contingut;
			if (document.getEstat().equals(DocumentEstatEnumDto.FIRMA_PENDENT)) {
				firmaPortafirmesHelper.portafirmesCancelar(
						entitatId,
						document, rolActual);
			}
		}
		
		// Valida si conté documents definitius
		if (!conteDocumentsDefinitius(contingut)) {

			// Si el contingut és un document guarda una còpia del fitxer esborrat
			// per a poder recuperar-lo posteriorment
			if (contingut instanceof DocumentEntity) {
				DocumentEntity document = (DocumentEntity)contingut;
				if (DocumentTipusEnumDto.DIGITAL.equals(document.getDocumentTipus()) && document.getGesDocAdjuntId() == null) {
					try {						
						fitxerDocumentEsborratGuardarEnTmp((DocumentEntity)contingut);
					} catch (Exception e) {
						Throwable root = Utils.getRootCauseOrItself(e);
						if (root.getMessage().contains("No s'ha trobat l'arxiu")) {
							logger.info("Al borrar el documento no se ha encontrado el contenido del documento " + document.getNom() + "del expediente " + document.getExpedient().getNom() + " amd id " + document.getExpedient().getId());
						} else {
							throw e;
						}
					}
					try {
						fitxerDocumentEsborratGuardarFirmaEnTmp((DocumentEntity)contingut);
					} catch (Exception e) {
						Throwable root = Utils.getRootCauseOrItself(e);
						if (root.getMessage().contains("Petición mal formada. No fue informado el identificador o localizador del documento a recuperar")) {
							logger.info("Al borrar el documento no se ha encontrado el contenido de firma del documento " + document.getNom() + "del expediente " + document.getExpedient().getNom() + " amd id " + document.getExpedient().getId());
						} else {
							throw e;
						}
					}					
				}
			} 
			
			// Elimina contingut a l'arxiu
			arxiuPropagarEliminacio(contingut);
		}

	}

	public boolean conteDocumentsDefinitius(ContingutEntity contingut) {
		boolean conteDefinitius = false;
		ContingutEntity deproxied = HibernateHelper.deproxy(contingut);
		if (deproxied instanceof ExpedientEntity || deproxied instanceof CarpetaEntity) {
			for (ContingutEntity contingutFill: contingut.getFills()) {
				conteDefinitius = conteDocumentsDefinitius(contingutFill);
				if (conteDefinitius)
					break;
			}
		} else if (deproxied instanceof DocumentEntity) {
			DocumentEntity document = (DocumentEntity)deproxied;
			conteDefinitius = document.isArxiuEstatDefinitiu();
		}
		return conteDefinitius;
	}

	private void fitxerDocumentEsborratGuardarEnTmp(
			DocumentEntity document) throws IOException {
		File fContent = new File(getBaseDir() + "/" + document.getId());
		fContent.getParentFile().mkdirs();
		FileOutputStream outContent = new FileOutputStream(fContent);
		FitxerDto fitxer = documentHelper.getFitxerAssociat(
				document,
				null);
		outContent.write(fitxer.getContingut());
		outContent.close();
	}
	
	private void fitxerDocumentEsborratGuardarFirmaEnTmp(
			DocumentEntity document) throws IOException {
		organGestorHelper.actualitzarOrganCodi(organGestorHelper.getOrganCodiFromContingutId(document.getId()));
		Document arxiuDocument = pluginHelper.arxiuDocumentConsultar(
				document,
				null,
				null,
				true,
				false);
	
		byte[]  firmaContingut =  documentHelper.getFirmaDetachedFromArxiuDocument(arxiuDocument);
		
		if (firmaContingut != null) {
			File fContent = new File(getBaseDirFirma() + "/" + document.getId());
			fContent.getParentFile().mkdirs();
			FileOutputStream outContent = new FileOutputStream(fContent);
			
			outContent.write(firmaContingut);
			outContent.close();
		}

	}


	public ExpedientEntity getExpedientSuperior(
			ContingutEntity contingut,
			boolean incloureActual,
			boolean comprovarPermisRead,
			boolean comprovarPermisWrite,
			String rolActual) {
		ExpedientEntity expedient = null;
		if (incloureActual && contingut instanceof ExpedientEntity) {
			expedient = (ExpedientEntity)contingut;
		} else {
			List<ContingutEntity> path = getPathContingut(contingut);
			// Si el contenidor és arrel el path retorna null i s'ha de comprovar
			if (path != null) {
				List<ContingutEntity> pathInvers = new ArrayList<ContingutEntity>(path);
				Collections.reverse(pathInvers);
				for (ContingutEntity contenidorPath: pathInvers) {
					if (contenidorPath instanceof ExpedientEntity) {
						expedient = (ExpedientEntity)contenidorPath;
						break;
					}
				}
			}
		}
		if (expedient != null) {
			// if user has write permissions to expedient estat don't need to check metaExpedient permissions
			if (expedient.getEstatAdditional() == null || !hasEstatPermissons(expedient.getEstatAdditional().getId())) {
				
				entityComprovarHelper.comprovarExpedient(
						expedient.getId(),
						false,
						comprovarPermisRead,
						comprovarPermisWrite,
						false,
						false,
						null);
			}
		}
		return expedient;
	}

	/**
	 * checking if expedient estat has modify permissions
	 * @param estatId
	 * @return
	 */
	private boolean hasEstatPermissons(Long estatId){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		return permisosHelper.isGrantedAll(
				estatId,
				ExpedientEstatEntity.class,
				new Permission[] {ExtendedPermission.WRITE},
				auth);
	}

	public ExpedientEntity crearNouExpedient(
			String nom,
			MetaExpedientEntity metaExpedient,
			ContingutEntity pare,
			EntitatEntity entitat,
			OrganGestorEntity organGestor,
			String ntiVersion,
			String ntiOrgano,
			Date ntiFechaApertura,
			Integer any,
			boolean agafar,
			Long grupId) {
		UsuariEntity agafatPer = null;
		if (agafar) {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			agafatPer = usuariRepository.getOne(auth.getName());
		}
		GrupEntity grupEntity = null;
		if (grupId != null) {
			grupEntity = grupRepository.findOne(grupId);
		}
		ExpedientEntity expedientCrear = ExpedientEntity.getBuilder(
				nom.trim(),
				metaExpedient,
				pare,
				entitat,
				"1.0",
				ntiOrgano,
				ntiFechaApertura,
				metaExpedient.getClassificacio(),
				organGestor).
				agafatPer(agafatPer).
				grup(grupEntity).
				build();
		
		// Calcula en número del nou expedient
		long sequenciaMetaExpedient = metaExpedientHelper.obtenirProximaSequenciaExpedient(
				metaExpedient,
				any,
				true);
		expedientCrear.updateAnySequenciaCodi(
				any,
				sequenciaMetaExpedient,
				metaExpedient.getCodi());

		ExpedientEntity expedientCreat = expedientRepository.save(expedientCrear);
		
		// Calcula número del nou expedient
		expedientCreat.updateNumero(expedientHelper.calcularNumero(expedientCreat));
		// Calcula l'identificador del nou expedient
		calcularIdentificadorExpedient(
				expedientCreat,
				entitat.getUnitatArrel(),
				any);
		return expedientCreat;
	}

	public void calcularIdentificadorExpedient(
			ExpedientEntity expedient,
			String organCodi,
			Integer any) {
		int anyExpedient;
		if (any != null) {
			anyExpedient = any.intValue();
		} else {
			anyExpedient = Calendar.getInstance().get(Calendar.YEAR);
		}
		String ntiIdentificador = "ES_" + organCodi + "_" + anyExpedient + "_EXP_RIP" + String.format("%027d", expedient.getId());
		expedient.updateNtiIdentificador(ntiIdentificador);
	}


	/*public Set<String> findUsuarisAmbPermisReadPerContenidor(
			ContingutEntity contingut) {
		List<PermisDto> permisos = new ArrayList<PermisDto>();
		if (contingut instanceof NodeEntity) {
			NodeEntity node = (NodeEntity)contingut;
			permisos = permisosHelper.findPermisos(
					node.getMetaNode().getId(),
					BustiaEntity.class);
		}
		Set<String> usuaris = new HashSet<String>();
		for (PermisDto permis: permisos) {
			switch (permis.getPrincipalTipus()) {
			case USUARI:
				usuaris.add(permis.getPrincipalNom());
				break;
			case ROL:
				List<DadesUsuari> usuarisGrup = pluginHelper.dadesUsuariFindAmbGrup(
						permis.getPrincipalNom());
				if (usuarisGrup != null) {
					for (DadesUsuari usuariGrup: usuarisGrup) {
						usuaris.add(usuariGrup.getCodi());
					}
				}
				break;
			}
		}
		return usuaris;
	}*/

	public ContingutMovimentEntity ferIEnregistrarMoviment(
			ContingutEntity contingut,
			ContingutEntity desti,
			String comentari) {
		UsuariEntity usuariAutenticat = usuariHelper.getUsuariAutenticat();
		if (usuariAutenticat == null && contingut.getDarrerMoviment() != null)
			usuariHelper.generarUsuariAutenticat(
					contingut.getDarrerMoviment().getRemitent().getCodi(),
					true);

		ContingutMovimentEntity contenidorMoviment = ContingutMovimentEntity.getBuilder(
				contingut.getId(),
				contingut.getPare().getId(),
				desti.getId(),
				usuariHelper.getUsuariAutenticat(),
				comentari).build();
		contingut.updateDarrerMoviment(
				contenidorMovimentRepository.save(contenidorMoviment));
		contingut.updatePare(desti);

		if (desti.getExpedient() == null) {
			contingut.updateExpedient((ExpedientEntity) desti);
		} else {
			contingut.updateExpedient(desti.getExpedient());
		}

		return contenidorMoviment;
	}

	public ContingutEntity findContingutArrel(
			ContingutEntity contingut) {
		ContingutEntity contingutActual = contingut;
		while (contingutActual != null && contingutActual.getPare() != null) {
			contingutActual = contingutActual.getPare();
		}
		return contingutRepository.findOne(contingutActual.getId());
	}

	public void findDescendants(
			ContingutEntity contingut,
			List<ContingutEntity> descendants) {

		if (contingut.getFills() == null || contingut.getFills().isEmpty()) {
			descendants.add(contingut);
		} else {
			for (ContingutEntity contingutEntity : contingut.getFills()) {
				findDescendants(contingutEntity,
						descendants);
			}
		}
	}

	/**
	 * Check if given name (@param nom) doesnt already exist inside given container (@param contingutPare)
	 * @param contingutPare
	 * @param nom
	 * @param id
	 * @param objectClass
	 */
	public void comprovarNomValid(
			ContingutEntity contingutPare,
			String nom,
			Long id,
			Class<?> objectClass) {
		if (nom.startsWith(".")) {
			throw new ValidationException(
					id,
					objectClass,
					"El nom del contingut no és vàlid (no pot començar amb un \".\")");
		}
		if (nom.endsWith(" ")) {
			throw new ValidationException(
					id,
					objectClass,
					"El nom del contingut no és vàlid (no pot acabar amb un \" \")");
		}
	}
	
	
	
	
	
	
	
	
	public void arxiuPropagarModificacio(
			ExpedientEntity expedient) {

		pluginHelper.arxiuExpedientActualitzar(expedient);
	}
	
	public void arxiuPropagarModificacio(
			CarpetaEntity carpeta,
			boolean fromAnotacio) {

		boolean forceUtilitzarCarpetesArxiu = PropertiesHelper.getProperties().getAsBoolean("es.caib.ripea.propagar.carpetes.arxiu", false);

		boolean utilitzarCarpetesEnArxiu = (fromAnotacio && !isCarpetaLogica()) || forceUtilitzarCarpetesArxiu;
		
		if (utilitzarCarpetesEnArxiu) {
				pluginHelper.arxiuCarpetaActualitzar( 
						carpeta,
						carpeta.getPare());
		}
	}
	
	
	

	public void arxiuPropagarModificacio(
			DocumentEntity document,
			FitxerDto fitxer,
			DocumentFirmaTipusEnumDto documentFirmaTipus,
			List<ArxiuFirmaDto> firmes,
			ArxiuEstatEnumDto arxiuEstat) {
		
		try {
			pluginHelper.arxiuDocumentActualitzar(
					document,
					fitxer,
					documentFirmaTipus,
					firmes,
					arxiuEstat);
		} catch (Exception e) {
			Exception root = ExceptionHelper.getRootCauseException(e);
			boolean exceptionJaFirmatEnArxiu = false;
			if (root instanceof ArxiuCaibException) {
				ArxiuCaibException ace = (ArxiuCaibException) root;
				if (ace.getMessage().contains("Can not add the draft aspect to the node because is a final document")) {
					exceptionJaFirmatEnArxiu = true;
				}
			}
			if (!exceptionJaFirmatEnArxiu) {
				throw e;
			}
		}
		
		documentHelper.actualitzarVersionsDocument((DocumentEntity) document);
		
		// Arxiu changes file size of some signed documents so we have to consult it after sending document to arxiu
		Document documentArxiu = pluginHelper.arxiuDocumentConsultar(document.getArxiuUuid());
		long tamany = documentArxiu.getContingut().getTamany();
		document.updateFitxerTamany(tamany);
		
		if (arxiuEstat == ArxiuEstatEnumDto.DEFINITIU) {
			boolean ambFirmes = firmes != null && ! firmes.isEmpty();
			if ((!document.getEstat().equals(DocumentEstatEnumDto.FIRMA_PARCIAL) && ! isConversioDefinitiuActiu())
					|| (!document.getEstat().equals(DocumentEstatEnumDto.FIRMA_PARCIAL) && isConversioDefinitiuActiu() && ambFirmes)) {
				document.updateEstat(DocumentEstatEnumDto.CUSTODIAT);
			}

			// Registra al log la custòdia de la firma del document
			contingutLogHelper.log((
					(DocumentEntity) document),
					LogTipusEnumDto.ARXIU_CUSTODIAT,
					document.getArxiuUuid(),
					null,
					false,
					false);
		}
	}

	public void arxiuPropagarEliminacio(ContingutEntity contingut) {
		if (contingut.getArxiuUuid() != null) {
			if (contingut instanceof ExpedientEntity) {
				pluginHelper.arxiuExpedientEsborrar(
						(ExpedientEntity)contingut);
			} else if (contingut instanceof DocumentEntity) {
				DocumentTipusEnumDto documentTipus = ((DocumentEntity)contingut).getDocumentTipus();
				if (!documentTipus.equals(DocumentTipusEnumDto.IMPORTAT)) {
					pluginHelper.arxiuDocumentEsborrar(
							(DocumentEntity)contingut);
				}
			} else if (contingut instanceof CarpetaEntity) {
				pluginHelper.arxiuCarpetaEsborrar(
						(CarpetaEntity)contingut);
			} else {
				throw new ValidationException(
						contingut.getId(),
						ContingutEntity.class,
						"El contingut que es vol esborrar de l'arxiu no és del tipus expedient, document o carpeta");
			}

		//Carpeta lògica
		} else if (contingut.getArxiuUuid() == null && contingut instanceof CarpetaEntity) {
			for (ContingutEntity fill : contingut.getFills()) {
				arxiuPropagarEliminacio(fill);
			}
		}
	}

	public void arxiuPropagarCopia(
			ContingutEntity contingut,
			ContingutEntity desti) {
		if (contingut instanceof DocumentEntity) {
			pluginHelper.arxiuDocumentCopiar(
					(DocumentEntity)contingut,
					desti.getArxiuUuid());
		} else if (contingut instanceof CarpetaEntity) {
			pluginHelper.arxiuCarpetaCopiar(
					(CarpetaEntity)contingut,
					desti.getArxiuUuid());
		}
	}

	public ContingutArxiu arxiuPropagarLink(
			ContingutEntity contingut,
			ContingutEntity desti) {
		if (contingut instanceof DocumentEntity) {
			ContingutArxiu nouContingut = pluginHelper.arxiuDocumentLink(
					(DocumentEntity)contingut,
					desti.getArxiuUuid());
			return nouContingut;
		} else {
			throw new ValidationException(
					contingut.getId(),
					contingut.getClass(),
					"Només es pot enllaçar un contingut del tipus document");
		}
	}
	
	
	public String arxiuDocumentPropagarMoviment(
			String uuid,
			ContingutEntity desti,
			String uuidExpedientDesti) {
			String identificador = null;
			if (desti instanceof ExpedientEntity || (desti instanceof CarpetaEntity && !isCarpetaLogica())) {
				identificador = pluginHelper.arxiuDocumentMoure(
						uuid,
						desti.getArxiuUuid(),
						uuidExpedientDesti);
			}
			return identificador;
	}



	private List<ContingutEntity> getPathContingut(
			ContingutEntity contingut) {
		List<ContingutEntity> path = null;
		ContingutEntity contingutActual = contingut;
		while (contingutActual != null && contingutActual.getPare() != null) {
			if (path == null)
				path = new ArrayList<ContingutEntity>();
			ContingutEntity c = contingutRepository.findOne(contingutActual.getPare().getId());
			path.add(c);
			contingutActual = c;
		}
		if (path != null) {
			Collections.reverse(path);
		}
		return path;
	}

	public List<ContingutDto> getPathContingutComDto(
			ContingutEntity contingut,
			boolean ambPermisos,
			boolean nomesFinsExpedientArrel, int level) {
		List<ContingutEntity> path = getPathContingut(contingut);
		List<ContingutDto> pathDto = null;
		if (path != null) {
			pathDto = new ArrayList<ContingutDto>();
			boolean expedientArrelTrobat = !nomesFinsExpedientArrel;
			for (ContingutEntity contingutPath: path) {
				if (!expedientArrelTrobat && contingutPath instanceof ExpedientEntity)
					expedientArrelTrobat = true;
				if (expedientArrelTrobat) {
					pathDto.add(
							toContingutDto(
									contingutPath,
									ambPermisos,
									false,
									false,
									false,
									false,
									false,
									null,
									false,
									null,
									false,
									level,
									null,
									null,
									false,
									false,
									false,
									false));
				}
			}
		}
		return pathDto;
	}
	
	public List<ContingutDto> getPathContingutComDto(ContingutEntity contingut, boolean nomesFinsExpedientArrel) {
		List<ContingutEntity> path = getPathContingut(contingut);
		List<ContingutDto> pathDto = null;
		if (path != null) {
			pathDto = new ArrayList<ContingutDto>();
			boolean expedientArrelTrobat = !nomesFinsExpedientArrel;
			for (ContingutEntity contingutPath: path) {
				if (!expedientArrelTrobat && contingutPath instanceof ExpedientEntity)
					expedientArrelTrobat = true;
				if (expedientArrelTrobat) {
					pathDto.add(toContingutDtoSimplificat(contingutPath, false, null));
				}
			}
		}
		return pathDto;
	}

	public FitxerDto generarIndexPdf(
			EntitatEntity entitatActual,
			List<ExpedientEntity> expedients,
			boolean exportar) throws IOException {

		byte[] indexGenerated = indexHelper.generarIndexPdfPerExpedient(
				expedients,
				entitatActual,
				exportar);

		FitxerDto fitxer = new FitxerDto();
		if (expedients.size() > 1) {
			fitxer.setNom(messageHelper.getMessage("expedient.service.exportacio.index") + ".pdf");
		} else {
			String expedientNom = expedients.get(0).getNom();
			if (expedientNom.contains("\"")) {
				expedientNom = expedientNom.replace("\"", "\\\"");
			}
			fitxer.setNom(messageHelper.getMessage("expedient.service.exportacio.index") + " " + expedientNom + ".pdf");
		}
		fitxer.setContentType("application/pdf");
		if (indexGenerated != null)
			fitxer.setContingut(indexGenerated);
		return fitxer;
	}
	
	public FitxerDto generarIndexXlsx(
			EntitatEntity entitatActual,
			List<ExpedientEntity> expedients,
			boolean exportar) throws IOException {

		byte[] indexGenerated = indexHelper.generarIndexXlsxPerExpedient(
				expedients,
				entitatActual,
				exportar);

		FitxerDto fitxer = new FitxerDto();
		if (expedients.size() > 1) {
			fitxer.setNom(messageHelper.getMessage("expedient.service.exportacio.index") + ".xlsx");
		} else {
			String expedientNom = expedients.get(0).getNom();
			if (expedientNom.contains("\"")) {
				expedientNom = "\"" + expedientNom.replace("\"", "\\\"") + "\"";
			}
			fitxer.setNom(messageHelper.getMessage("expedient.service.exportacio.index") + " " + expedientNom + ".xlsx");
		}
		fitxer.setContentType("application/vnd.ms-excel");
		if (indexGenerated != null)
			fitxer.setContingut(indexGenerated);
		return fitxer;
	}

	public void crearNovaEntrada(
			String nom,
			FitxerDto fitxer,
			ZipOutputStream zos) throws IOException {
		ZipEntry entrada = new ZipEntry(nom);
		entrada.setSize(fitxer.getTamany());
		zos.putNextEntry(entrada);
		if (fitxer.getContingut() != null)
			zos.write(fitxer.getContingut());
		zos.closeEntry();
	}

	public void tractarInteressats(List<RegistreInteressat> interessats) {
		ListIterator<RegistreInteressat> iter = interessats.listIterator();
		while(iter.hasNext()){
		    if(iter.next().getRepresentat() != null){
		        iter.remove();
		    }
		}
	}


	public void marcarEsborrat(ContingutEntity contingut) {

		if (contingut.getEsborrat() == 0) {

			for (ContingutEntity contingutFill: contingut.getFills()) {
				marcarEsborrat(contingutFill);
			}

			List<ContingutEntity> continguts = contingutRepository.findByPareAndNomOrderByEsborratAsc(
					contingut.getPare(),
					contingut.getNom());
			// Per evitar errors de restricció única violada hem de
			// posar al camp esborrat un nombre != 0 i que sigui diferent
			// dels altres fills esborrats amb el mateix nom.
			int index = 0;
			for (ContingutEntity c: continguts) {
					if (index < c.getEsborrat()) {
						index = c.getEsborrat();
					}
			}
			contingut.updateEsborrat(index + 1);
			contingut.updateEsborratData(new Date());
			contingutLogHelper.log(
					contingut,
					LogTipusEnumDto.ELIMINACIO,
					null,
					null,
					true,
					true);
			Long pareId = contingut.getPare() != null ? contingut.getPare().getId() : null;
			logger.debug("Contingut amb id: " + contingut.getId() + " amb pareId: " + pareId + " marcada com esborrat amb num: " + contingut.getEsborrat());
		}

	}

	public FitxerDto fitxerDocumentEsborratLlegir(
			DocumentEntity document)  {
		File fContent = new File(getBaseDir() + "/" + document.getId());
		fContent.getParentFile().mkdirs();
		if (fContent.exists()) {
			byte fileContent[] = null;
			try {
				FileInputStream inContent = new FileInputStream(fContent);
				fileContent = new byte[(int)fContent.length()];
				inContent.read(fileContent);
				inContent.close();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			ArxiuDocumentContingut contingut = new ArxiuDocumentContingut(
					ArxiuContingutTipusEnum.CONTINGUT,
					null,
					fileContent);
			List<ArxiuDocumentContingut> continguts = new ArrayList<ArxiuDocumentContingut>();
			continguts.add(contingut);
			FitxerDto fitxer = new FitxerDto();
			fitxer.setNom(document.getFitxerNom());
			fitxer.setContentType(document.getFitxerContentType());
			fitxer.setContingut(fileContent);
			return fitxer;
		} else {
			return null;
		}
	}
	
	
	public byte[] firmaSeparadaEsborratLlegir(
			DocumentEntity document)  {
		File fContent = new File(getBaseDirFirma() + "/" + document.getId());
		fContent.getParentFile().mkdirs();
		if (fContent.exists()) {
			byte fileContent[] = null;
			try {
				FileInputStream inContent = new FileInputStream(fContent);
				fileContent = new byte[(int)fContent.length()];
				inContent.read(fileContent);
				inContent.close();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}

			return fileContent;
		} else {
			return null;
		}
	}

	public void fitxerDocumentEsborratEsborrar(
			DocumentEntity document) {
		File fContent = new File(getBaseDir() + "/" + document.getId());
		fContent.getParentFile().mkdirs();
		if (fContent.exists()) {
			fContent.delete();
		}
	}
	

	public String getUniqueNameInPare(String nomPerComprovar, Long pareId) {

		List<ContingutEntity> continguts = contingutRepository.findByPareIdAndEsborrat(pareId, 0);
		if (continguts != null) {
			int ocurrences = 0;
			List<String> noms = new ArrayList<String>();
			for(ContingutEntity contingut : continguts) {
				noms.add(contingut.getNom());
			}
			String newName = new String(nomPerComprovar);
			while(noms.indexOf(newName) >= 0) {
				ocurrences ++;
				newName = nomPerComprovar + " (" + ocurrences + ")";
			}
			return newName;
		}
		return nomPerComprovar;
	}
	
	

	public void checkIfPermitted(
			Long contingutId,
			String rolActual, 
			PermissionEnumDto permission) {
		
		ContingutEntity contingut = contingutRepository.findOne(contingutId);
		if (contingut == null) {
			throw new NotFoundException(contingutId, ContingutEntity.class);
		}
		
		entityComprovarHelper.comprovarExpedient(
				contingut.getExpedientPare().getId(),
				false,
				permission == PermissionEnumDto.READ,
				permission == PermissionEnumDto.WRITE,
				permission == PermissionEnumDto.CREATE,
				permission == PermissionEnumDto.DELETE,
				rolActual);
	
	}
	
	
	public void firmaSeparadaEsborratEsborrar(
			DocumentEntity document)  {
		
		File fContent = new File(getBaseDirFirma() + "/" + document.getId());
		fContent.getParentFile().mkdirs();
		if (fContent.exists()) {
			fContent.delete();
		}
	}

	public boolean checkUniqueContraint (String nom, ContingutEntity pare, EntitatEntity entitat, ContingutTipusEnumDto tipus) {
		List<ContingutEntity> items = contingutRepository.findByNomAndTipusAndPareAndEntitatAndEsborrat(
				nom,
				tipus,
				pare,
				entitat,
				0);
		return items.size() == 0;
	}

	public String getBaseDir() {
		return configHelper.getConfig("es.caib.ripea.app.data.dir") + "/esborrats-tmp";
	}
	
	public String getBaseDirFirma() {
		return configHelper.getConfig("es.caib.ripea.app.data.dir") + "/esborrats-firma-tmp";
	}

	public boolean isCarpetaLogica() {
		return configHelper.getAsBoolean("es.caib.ripea.carpetes.logiques");
	}

	public boolean isOrdenacioPermesa() {
		return configHelper.getAsBoolean("es.caib.ripea.ordenacio.contingut.habilitada");
	}

	private boolean isPermesEsborrarFinals() {
		return configHelper.getAsBoolean("es.caib.ripea.document.esborrar.finals");
	}
	
	public boolean isConversioDefinitiuActiu() {
		return configHelper.getAsBoolean("es.caib.ripea.conversio.definitiu");
	}
	
	private boolean isCertificacioAmbFirma(byte[] certificacioContingut) {
		boolean hasFirma = false;
		try {
			PdfReader reader = new PdfReader(certificacioContingut);
			AcroFields fields = reader.getAcroFields();

			@SuppressWarnings("unchecked")
			List<String> signatureNames = fields.getSignatureNames();
			if (signatureNames != null) {
				for (String name: signatureNames) {
//					### comprovar si és una firma o un segell
					PdfDictionary dictionary = fields.getSignatureDictionary(name);
					if (dictionary != null && dictionary.get(PdfName.TYPE).toString().equalsIgnoreCase("/Sig")) {
						hasFirma = true;
						break;
					}
				}
			}
		} catch (Exception ex) {
			logger.error("Hi ha hagut un error comprovant si la certificació està firmada", ex);
		}
		return hasFirma;
	}


//	@Transactional(propagation = Propagation.REQUIRES_NEW)
//	public ResultDocumentSenseContingut arreglaDocumentSenseContingut(Long annexId) {
//		ResultDocumentSenseContingutBuilder resultBuilder = ResultDocumentSenseContingut.builder();
//
//		try {
//			RegistreAnnexEntity registreAnnex = registreAnnexRepository.findById(annexId);
//			String annexUuid = registreAnnex.getUuid();
//			resultBuilder.uuidOrigen(annexUuid);
//
//			DocumentEntity document = registreAnnex.getDocument();
//			if (document == null) {
//				logger.info("[DOCS_SENSE_CONT] L'annex no té un document associat a l'expedient");
//				return resultBuilder.error(true).errorMessage("L'annex no té un document associat a l'expedient").build();
//			}
//			if (document.getArxiuUuid() == null) {
//				if (registreAnnex.getError() != null && !registreAnnex.getError().isEmpty()) {
//					return resultBuilder.error(true).errorMessage("El document associat a l'annex no té UUID de l'arxiu, i està pendent de reintent.").build();
//				} else {
//					logger.info("[DOCS_SENSE_CONT] El document associat a l'annex no té UUID de l'arxiu, i no està pendnet de reintent. El crearem!");
//					resultBuilder.errorMessage("El document associat a l'annex no té UUID de l'arxiu, i no està pendent de reintent. El crearem!").build();
//				}
//			}
//
//			String documentUuid = document.getArxiuUuid();
//			resultBuilder.documentId(document.getId()).documentNom(document.getNom()).expedient(document.getExpedient().getCodi());
//
//			if (documentUuid != null && annexUuid.equals(documentUuid)) {
//				logger.info("[DOCS_SENSE_CONT] El document de l'annex i del document actual són el mateix a l'arxiu");
//				return resultBuilder.uuidDesti(documentUuid).error(true).errorMessage("El document de l'annex i del document actual són el mateix a l'arxiu").build();
//			}
//
//			if (documentUuid != null) {
//				Document documentArxiu = pluginHelper.arxiuDocumentConsultar(document, documentUuid, null, true);
//				if (documentArxiu.getContingut() != null) {
//					logger.info("[DOCS_SENSE_CONT] El document associat ja té contingut");
//					return resultBuilder.uuidDesti(documentUuid).error(false).errorMessage("El document associat ja té contingut").build();
//				}
//				resultBuilder.uuidDestiSenseContingut(documentUuid);
//			}
//
//			Document documentAnnex = pluginHelper.arxiuDocumentConsultar(null, annexUuid, null, true);
//			if (documentAnnex.getContingut() == null) {
//				logger.info("[DOCS_SENSE_CONT]");
//				return resultBuilder.error(true).errorMessage("El document de l'annex no té contingut a l'arxiu").build();
//			}
//
//			// Annex associat amb document sense uuid
//			// 1. Eliminam el document incorrecte de l'arxiu
//			if (documentUuid != null) {
//				pluginHelper.arxiuDocumentEsborrar(document);
//				logger.info("[DOCS_SENSE_CONT] El document sense contingut amb uuid '{}' ha estat esborrat.", documentUuid);
//			}
//
//			// 2. Posam l'uuid de l'annex al document
//			document.updateArxiu(annexUuid);
//
//			// 3. Tornam a crear el document a partir de l'annex
//			String uuidDesti = pluginHelper.arxiuDocumentMoure(
//					document,
//					document.getPare().getArxiuUuid(),
//					document.getExpedient().getArxiuUuid());
//
//			// 4. Assignam el nou uuid al document
//			if (uuidDesti == null) {
//				logger.info("[DOCS_SENSE_CONT] No s'ha generat un uuid per un nou document a l'arxiu");
//				return resultBuilder.error(true).errorMessage("No s'ha generat un uuid per un nou document a l'arxiu").build();
//			}
//			document.updateArxiu(uuidDesti);
//			logger.info("[DOCS_SENSE_CONT] S'ha assignat un nou document a l'arxiu per al document, amb uuid '{}'", uuidDesti);
//			return resultBuilder.uuidDesti(uuidDesti).build();
//		} catch (Exception ex) {
//			logger.info("[DOCS_SENSE_CONT] Error inesperat", ex);
//			return resultBuilder.error(true).errorMessage("Error inesperat: " + ex.getMessage()).build();
//		}
//
//	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public ResultDocumentSenseContingut arreglaDocumentSenseContingut(Long annexId) {
		ResultDocumentSenseContingutBuilder resultBuilder = ResultDocumentSenseContingut.builder();
		organGestorHelper.actualitzarOrganCodi(organGestorHelper.getOrganCodiFromAnnexId(annexId));
		try {
			RegistreAnnexEntity registreAnnex = registreAnnexRepository.findById(annexId);
			String annexUuid = registreAnnex.getUuid();
			resultBuilder.uuidOrigen(annexUuid);

			DocumentEntity document = registreAnnex.getDocument();
			resultBuilder.documentId(document.getId()).documentNom(document.getNom()).expedient(document.getExpedient().getCodi());

			Document documentAnnex = pluginHelper.arxiuDocumentConsultar(null, annexUuid, null, true);
			if (documentAnnex.getContingut() == null) {
				logger.info("[DOCS_SENSE_CONT]");
				return resultBuilder.error(true).errorMessage("El document de l'annex no té contingut a l'arxiu").build();
			}

			// Annex associat amb document sense uuid

			CarpetaEntity carpetaEntity = carpetaRepository.findOne(document.getPareId());
			Carpeta carpeta = pluginHelper.arxiuCarpetaConsultar(carpetaEntity);

			logger.info("Contingut de la carpeta '{}';", carpeta.getNom());
			resultBuilder.carpeta(carpeta.getNom() + " (" + carpeta.getIdentificador() + ")");
			List<String> documentsCarpeta = new ArrayList<>();
			Document documentArxiu = null;
			for (ContingutArxiu contingut : carpeta.getContinguts()) {
				logger.info(" - {}: UUID {}", contingut.getNom(), contingut.getIdentificador());
				documentsCarpeta.add(contingut.getNom() + " (" + contingut.getIdentificador() + ")");
//				if (ArxiuConversioHelper.revisarContingutNom(document.getNom()).equals(contingut.getNom())) {
				if (document.getFitxerNom().equals(contingut.getNom())) {
					documentArxiu = pluginHelper.arxiuDocumentConsultar(null, contingut.getIdentificador(), null, true);
				}
			}
			resultBuilder.documentsCarpeta(documentsCarpeta);
			if (documentArxiu != null && documentArxiu.getContingut() != null && Arrays.equals(documentArxiu.getContingut().getContingut(), documentAnnex.getContingut().getContingut())) {
				if (documentArxiu.getIdentificador().equals(documentAnnex.getIdentificador())) {
					resultBuilder.errorMessage("El document de la carpeta és el mateix que el de l'annex.");
				}
				document.updateArxiu(documentArxiu.getIdentificador());
				logger.info("[DOCS_SENSE_CONT] S'ha assignat el document que ja es trobava a l'arxiu amb uuid '{}'", documentArxiu.getIdentificador());
				return resultBuilder.uuidDesti(documentArxiu.getIdentificador()).build();
			} else if (documentArxiu != null) {
				if (documentArxiu.getContingut() == null) {
					resultBuilder.errorMessage("El document trobat a l'arxiu no té contingut.");
					// 1. Eliminam el document que es troba actualment a l'arxiu
					document.updateArxiu(documentArxiu.getIdentificador());
					pluginHelper.arxiuDocumentEsborrar(document);

					// 2. Posam l'uuid de l'annex al document
					document.updateArxiu(annexUuid);

					// 3. Crear el document a partir de l'annex
					organGestorHelper.actualitzarOrganCodi(organGestorHelper.getOrganCodiFromContingutId(document.getId()));
					String uuidDesti = pluginHelper.arxiuDocumentMoure(
							document.getArxiuUuid(),
							document.getPare().getArxiuUuid(),
							document.getExpedient().getArxiuUuid());

					// 4. Assignam el nou uuid al document
					if (uuidDesti == null) {
						logger.info("[DOCS_SENSE_CONT] No s'ha generat un uuid per un nou document a l'arxiu");
						return resultBuilder.error(true).errorMessage("El document trobat a l'arxiu no té contingut, però no s'ha generat un uuid per un nou document a l'arxiu").build();
					}
					document.updateArxiu(uuidDesti);
					logger.info("[DOCS_SENSE_CONT] S'ha assignat un nou document a l'arxiu per al document, amb uuid '{}'", uuidDesti);
					return resultBuilder.uuidDesti(uuidDesti).build();
				} else {
					resultBuilder.error(true).errorMessage("El document trobat a l'arxiu no té el mateix contingut que el document de l'annex. S'ha de revisar manualment.");
				}
				return resultBuilder.build();
			} else {
				return resultBuilder.error(true).errorMessage("No s'ha trobat document amb el mateix nom a la carpeta de l'anotació").build();
			}

		} catch (Exception ex) {
			logger.info("[DOCS_SENSE_CONT] Error inesperat", ex);
			return resultBuilder.error(true).errorMessage("Error inesperat: " + ex.getMessage()).build();
		}

	}

	
	public int getArxiuMaxReintentsDocuments() {
		String arxiuMaxReintentsDocuments = configHelper.getConfig("es.caib.ripea.segonpla.guardar.arxiu.max.reintents.documents");
		return arxiuMaxReintentsDocuments != null && !arxiuMaxReintentsDocuments.isEmpty() ? Integer.valueOf(arxiuMaxReintentsDocuments) : 0;
	}
	private static final Logger logger = LoggerFactory.getLogger(ContingutHelper.class);

}
