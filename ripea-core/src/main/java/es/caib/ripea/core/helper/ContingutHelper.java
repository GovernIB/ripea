/**
 * 
 */
package es.caib.ripea.core.helper;

import com.lowagie.text.pdf.AcroFields;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfReader;
import es.caib.plugins.arxiu.api.Carpeta;
import es.caib.plugins.arxiu.api.ContingutArxiu;
import es.caib.plugins.arxiu.api.Document;
import es.caib.ripea.core.api.dto.*;
import es.caib.ripea.core.api.dto.ResultDocumentsSenseContingut.ResultDocumentSenseContingut;
import es.caib.ripea.core.api.dto.ResultDocumentsSenseContingut.ResultDocumentSenseContingut.ResultDocumentSenseContingutBuilder;
import es.caib.ripea.core.api.exception.PermissionDeniedException;
import es.caib.ripea.core.api.exception.ValidationException;
import es.caib.ripea.core.api.registre.RegistreInteressat;
import es.caib.ripea.core.entity.*;
import es.caib.ripea.core.firma.DocumentFirmaPortafirmesHelper;
import es.caib.ripea.core.repository.*;
import es.caib.ripea.core.security.ExtendedPermission;
import es.caib.ripea.plugin.arxiu.ArxiuContingutTipusEnum;
import es.caib.ripea.plugin.arxiu.ArxiuDocumentContingut;
import es.caib.ripea.plugin.notificacio.RespostaConsultaEstatEnviament;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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



	public ContingutDto toContingutDto(
			ContingutEntity contingut) {
		return toContingutDto(
				contingut,
				false,
				false,
				false,
				false,
				false,
				false,
				false, null, false, null, false, 0, null, null, true);
	}
	
	public ContingutDto toContingutDto(
			ContingutEntity contingut,
			boolean ambPermisos,
			boolean ambFills,
			boolean filtrarFillsSegonsPermisRead,
			boolean ambDades,
			boolean ambPath,
			boolean pathNomesFinsExpedientArrel,
			boolean ambVersions,
			String rolActual,
			boolean onlyForList,
			Long organActualId, 
			boolean onlyFirstDescendant, int level, ExpedientDto expedientDto, List<ContingutDto> pathDto, boolean ambExpedientPare) {
		
		return toContingutDto(
				contingut,
				ambPermisos,
				ambFills,
				filtrarFillsSegonsPermisRead,
				ambDades,
				ambPath,
				pathNomesFinsExpedientArrel,
				ambVersions,
				rolActual,
				onlyForList,
				organActualId,
				onlyFirstDescendant,
				level,
				expedientDto,
				pathDto,
				ambExpedientPare,
				true);
	}
	
	
	public ContingutDto toContingutDto(
			ContingutEntity contingut,
			boolean ambPermisos,
			boolean ambFills,
			boolean filtrarFillsSegonsPermisRead,
			boolean ambDades,
			boolean ambPath,
			boolean pathNomesFinsExpedientArrel,
			boolean ambVersions,
			String rolActual,
			boolean onlyForList,
			Long organActualId, 
			boolean onlyFirstDescendant, int level, ExpedientDto expedientDto, List<ContingutDto> pathDto, boolean ambExpedientPare, boolean ambEntitat) {
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
			if (expedient.getExpedientEstat() != null) {
				dto.setExpedientEstat(conversioTipusHelper.convertir(
						expedient.getExpedientEstat(),
						ExpedientEstatDto.class));
			}
			long t10 = System.currentTimeMillis();
			try {
				dto.setUsuariActualWrite(false);
				entityComprovarHelper.comprovarPermisosMetaNode(
						expedient.getMetaNode(),
						expedient.getId(),
						false,
						true,
						false,
						false,
						false,
						rolActual,
						null);
				dto.setUsuariActualWrite(true);
			} catch (PermissionDeniedException ex) {
			}

			try {
				dto.setUsuariActualDelete(false);
				entityComprovarHelper.comprovarPermisosMetaNode(
						expedient.getMetaNode(),
						expedient.getId(),
						false,
						false,
						false,
						true,
						false,
						rolActual,
						null);
				dto.setUsuariActualDelete(true);
			} catch (PermissionDeniedException ex) {
			}
			if (cacheHelper.mostrarLogsRendiment())
				logger.info("toExpedientDto comprovarPermisos time:  " + (System.currentTimeMillis() - t10) + " ms");

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

			dto.setConteDocumentsDefinitius(conteDocumentsDefinitius(contingut));

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

				dto.setConteDocuments(documentRepository.findByExpedientAndEsborrat(expedient, 0).size() > 0);
				dto.setConteDocumentsEnProcessDeFirma(documentRepository.findEnProccessDeFirma(expedient).size() > 0);	
				dto.setConteDocumentsPendentsReintentsArxiu(documentRepository.findDocumentsPendentsReintentsArxiu(expedient, getArxiuMaxReintentsDocuments()).size() > 0);

				dto.setHasEsborranys(documentRepository.hasFillsEsborranys(expedient));
				dto.setConteDocumentsFirmats(
						documentRepository.countByExpedientAndEstat(
								expedient,
								DocumentEstatEnumDto.CUSTODIAT) > 0);
				dto.setHasAllDocumentsDefinitiu(documentRepository.hasAllDocumentsDefinitiu(expedient));
				// expedient estat
				if (expedient.getExpedientEstat() != null) {
					ExpedientEstatEntity estat =  expedientEstatRepository.findByMetaExpedientAndOrdre(expedient.getExpedientEstat().getMetaExpedient(), expedient.getExpedientEstat().getOrdre()+1);
					if (estat != null) {
						dto.setExpedientEstatNextInOrder(estat.getId());
					} else {//if there is no estat with higher order, choose previous
						dto.setExpedientEstatNextInOrder(expedient.getExpedientEstat().getId());
					}
				}

				dto.setInteressats(conversioTipusHelper.convertirSet(expedient.getInteressats(),InteressatDto.class));
				dto.setInteressatsNotificable(conversioTipusHelper.convertirList(expedientInteressatHelper.findByExpedientAndNotRepresentantAndAmbDadesPerNotificacio(expedient), InteressatDto.class));
				dto.setGrupId(expedient.getGrup() != null ? expedient.getGrup().getId() : null);

				dto.setOrganGestorId(expedient.getOrganGestor() != null ? expedient.getOrganGestor().getId() : null);
				dto.setOrganGestorText(expedient.getOrganGestor() != null ?
						expedient.getOrganGestor().getCodi() + " - " + expedient.getOrganGestor().getNom() : "");
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
				dto.setFitxerContentType(document.getFitxerContentType());
				//dto.setFitxerContingut(document.getFitxerContingut());
			}
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
					dto.setNtiTipoDocumentalNom(tipusDocumental.getNom());
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
			dto.setAmbNotificacions(document.isAmbNotificacions());
			dto.setEstatDarreraNotificacio(document.getEstatDarreraNotificacio());
			dto.setErrorDarreraNotificacio(document.isErrorDarreraNotificacio());
			dto.setErrorEnviamentPortafirmes(document.isErrorEnviamentPortafirmes());
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
								false, null, true, null, onlyFirstDescendant, level, null, null, ambExpedientPare, ambEntitat));
			
			boolean conteDocsDef = conteDocumentsDefinitius(contingut);
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

			if (!contingut.getFills().isEmpty()) {
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

			if (ambPermisos && metaNode != null) {
				long t2 = System.currentTimeMillis();
				// Omple els permisos
				metaNodeHelper.omplirPermisosPerMetaNode(metaNode, rolActual, contingut.getId());
				if (cacheHelper.mostrarLogsRendiment())
					logger.info("ambPermisosmetaNode time (" + contingut.getId() + "):  " + (System.currentTimeMillis() - t2) + " ms");
				
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
								ambEntitat);
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
				List<ContingutEntity> fillsOrder1 = contingutRepository.findByPareAndEsborratAndOrdenat(
						contingut,
						0,
						isOrdenacioPermesa() ? new Sort("ordre") : new Sort("createdDate"));

				List<ContingutEntity> fillsOrder2 = contingutRepository.findByPareAndEsborratSenseOrdre(
						contingut,
						0,
						new Sort("createdDate"));

				fills.addAll(fillsOrder1);
				fills.addAll(fillsOrder2);

				if (filtrarFillsSegonsPermisRead) {
					// Filtra els fills que no tenen permis de lectura
					Iterator<ContingutEntity> it = fills.iterator();
					while (it.hasNext()) {
						ContingutEntity c = it.next();
						if (c instanceof ExpedientEntity) {
							ExpedientEntity n = (ExpedientEntity)c;
							if (n.getMetaNode() != null && !permisosHelper.isGrantedAll(
									n.getMetaNode().getId(),
									MetaNodeEntity.class,
									new Permission[] {ExtendedPermission.READ},
									auth)) {
								it.remove();
							}
						}
					}
				}
				
				List<ContingutDto> fillsDtos = new ArrayList<ContingutDto>();
				for (ContingutEntity fill: fills) {
					if (fill.getEsborrat() == 0) {
						ContingutDto fillDto = toContingutDto(
								fill,
								ambPermisos,
								onlyFirstDescendant ? false : true,
								false,
								false,
								ambPath,
								false,
								false, rolActual, onlyForList, organActualId, onlyFirstDescendant, level, expedientCalculat, pathCalculatPerFills, ambExpedientPare, ambEntitat);
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
		List<ContingutEntity> fillsOrder1 = contingutRepository.findByPareAndEsborratAndOrdenat(
				contingut,
				0,
				isOrdenacioPermesa() ? new Sort("ordre") : new Sort("createdDate"));
		List<ContingutEntity> fillsOrder2 = contingutRepository.findByPareAndEsborratSenseOrdre(
				contingut,
				0,
				new Sort("createdDate"));

		fills.addAll(fillsOrder1);
		fills.addAll(fillsOrder2);
		
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
			dto.setFitxerContentType(resposta.getCertificacioTipusMime());
			dto.setFitxerContingut(resposta.getCertificacioContingut());
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
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				false,
				false,
				true, false);
		ContingutEntity contingut = entityComprovarHelper.comprovarContingut(
				entitat,
				contingutId);
		contingut = HibernateHelper.deproxy(contingut);
		// Comprova el permís de modificació de l'expedient superior
		ExpedientEntity expedient = getExpedientSuperior(
				contingut,
				true,
				false,
				true,
				checkPerMassiuAdmin,
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
				if (comprovarPermisWrite && expedientEntity.getExpedientEstat() != null) {
					if (hasEstatPermissons(expedientEntity.getExpedientEstat().getId()))
						comprovarPermisWrite = false;
				}
			}
			comprovarPermisosExpedient(
					expedientEntity,
					comprovarPermisRead,
					comprovarPermisWrite,
					comprovarPermisCreate,
					comprovarPermisDelete,
					checkPerMassiuAdmin,
					rolActual);
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
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				false,
				false,
				true, false);
		ContingutEntity contingut = entityComprovarHelper.comprovarContingut(
				entitat,
				contingutId);
		// Comprova el permís de lectura de l'expedient superior
		getExpedientSuperior(
				contingut,
				true,
				false,
				false,
				false, null);
		if (ContingutTipusEnumDto.EXPEDIENT.equals(contingut.getTipus())) {
			comprovarPermisosExpedient(
					(ExpedientEntity)contingut,
					comprovarPermisRead,
					comprovarPermisWrite,
					false,
					false, false, null);
		}
		return contingut;
	}


	public ContingutEntity comprovarContingutPertanyTascaAccesible(
			Long entitatId,
			Long tascaId,
			Long contingutId) {

		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				true,
				false,
				false, false, false);
		ContingutEntity contingut = entityComprovarHelper.comprovarContingut(
				entitat,
				contingutId);
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



	public ContingutDto deleteReversible(
			Long entitatId,
			ContingutEntity contingut,
			String rolActual) throws IOException {
		logger.debug("Esborrant el contingut ("
				+ "entitatId=" + entitatId + ", "
				+ "contingutId=" + contingut.getId() + ")");

		ContingutDto dto = toContingutDto(
				contingut,
				true,
				false,
				false,
				false,
				false,
				false,
				false, null, false, null, false, 0, null, null, true);
		// Comprova que el contingut no estigui esborrat
		if (contingut.getEsborrat() > 0) {
			logger.error("Aquest contingut ja està esborrat (contingutId=" + contingut.getId() + ")");
			throw new ValidationException(
					contingut.getId(),
					ContingutEntity.class,
					"Aquest contingut ja està esborrat");
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
					fitxerDocumentEsborratGuardarEnTmp((DocumentEntity)contingut);
					fitxerDocumentEsborratGuardarFirmaEnTmp((DocumentEntity)contingut);
				}
				// Elimina contingut a l'arxiu
				arxiuPropagarEliminacio(contingut);
			} else {
				// Elimina contingut a l'arxiu
				arxiuPropagarEliminacio(contingut);
			}
		}

		return dto;
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
			conteDefinitius = document.isArxiuEstatDefinitu();
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


	private void comprovarPermisosExpedient(
			ExpedientEntity expedient,
			boolean comprovarPermisRead,
			boolean comprovarPermisWrite,
			boolean comprovarPermisCreate,
			boolean comprovarPermisDelete,
			boolean checkPerMassiuAdmin,
			String rolActual) {
		if (expedient.getMetaNode() != null) {
			entityComprovarHelper.comprovarPermisosMetaNode(
					expedient.getMetaNode(),
					expedient.getId(),
					comprovarPermisRead,
					comprovarPermisWrite,
					comprovarPermisCreate,
					comprovarPermisDelete,
					checkPerMassiuAdmin,
					rolActual,
					null);
		} else {
			throw new ValidationException(
					expedient.getId(),
					ContingutEntity.class,
					"L'expedient no te meta-node associat (expedientId=" + expedient.getId() + ")");
		}
	}

	public ExpedientEntity getExpedientSuperior(
			ContingutEntity contingut,
			boolean incloureActual,
			boolean comprovarPermisRead,
			boolean comprovarPermisWrite,
			boolean checkPerMassiuAdmin,
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
			if (comprovarPermisRead) {
				entityComprovarHelper.comprovarMetaExpedientPerExpedient(
						expedient.getEntitat(),
						expedient.getMetaExpedient().getId(),
						true,
						false,
						false,
						false,
						false,
						rolActual,
						null);
			}
			if (comprovarPermisWrite && !checkPerMassiuAdmin) {

				// if user has write permissions to expedient estat don't need to check metaExpedient permissions
				if (expedient.getExpedientEstat() == null || !hasEstatPermissons(expedient.getExpedientEstat().getId())) {

					comprovarPermisosExpedient(
							expedient,
							false,
							true,
							false,
							false,
							false,
							rolActual);

				}
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
			Long sequencia,
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
				metaExpedient.getClassificacioSia(),
				organGestor).
				agafatPer(agafatPer).
				grup(grupEntity).
				build();
		// Calcula en número del nou expedient
		long sequenciaMetaExpedient = metaExpedientHelper.obtenirProximaSequenciaExpedient(
				metaExpedient,
				any,
				true);
		if (sequencia == null) {
			expedientCrear.updateAnySequenciaCodi(
					any,
					sequenciaMetaExpedient,
					metaExpedient.getCodi());
		} else {
			if (sequencia.longValue() == sequenciaMetaExpedient) {
				metaExpedientHelper.obtenirProximaSequenciaExpedient(
						metaExpedient,
						any,
						true);
				expedientCrear.updateAnySequenciaCodi(
						any,
						sequenciaMetaExpedient,
						metaExpedient.getCodi());
			} else {
				throw new ValidationException("Ja existeix un altre expedient amb el número de seqüència " + sequenciaMetaExpedient);
			}
		}
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

		boolean utilitzarCarpetesEnArxiu = fromAnotacio && !isCarpetaLogica();
		
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
		
		pluginHelper.arxiuDocumentActualitzar(
				(DocumentEntity) document,
				fitxer,
				documentFirmaTipus,
				firmes,
				arxiuEstat);
		documentHelper.actualitzarVersionsDocument((DocumentEntity) document);
		
		if (arxiuEstat == ArxiuEstatEnumDto.DEFINITIU) {
			
			if (!document.getEstat().equals(DocumentEstatEnumDto.FIRMA_PARCIAL)) {
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
								false, null, false, null, false, level, null, null, false, false));
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

	public FitxerDto generarIndex(
			EntitatEntity entitatActual,
			List<ExpedientEntity> expedients,
			boolean exportar) throws IOException {

		byte[] indexGenerated = indexHelper.generarIndexPerExpedient(
				expedients,
				entitatActual,
				exportar);

		FitxerDto fitxer = new FitxerDto();
		if (expedients.size() > 1)
			fitxer.setNom(messageHelper.getMessage("expedient.service.exportacio.index") + ".pdf");
		else
			fitxer.setNom(messageHelper.getMessage("expedient.service.exportacio.index") + " " + expedients.get(0).getNom() + ".pdf");
		fitxer.setContentType("application/pdf");
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
		fContent.delete();
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
					if (dictionary != null && dictionary.get(PdfName.TYPE).toString().equals("/sig")) {
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

	
	private int getArxiuMaxReintentsDocuments() {
		String arxiuMaxReintentsDocuments = configHelper.getConfig("es.caib.ripea.segonpla.guardar.arxiu.max.reintents.documents");
		return arxiuMaxReintentsDocuments != null && !arxiuMaxReintentsDocuments.isEmpty() ? Integer.valueOf(arxiuMaxReintentsDocuments) : 0;
	}
	private static final Logger logger = LoggerFactory.getLogger(ContingutHelper.class);

}
