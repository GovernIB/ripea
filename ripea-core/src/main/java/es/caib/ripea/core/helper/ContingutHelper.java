/**
 * 
 */
package es.caib.ripea.core.helper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.lowagie.text.pdf.AcroFields;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfReader;

import es.caib.plugins.arxiu.api.ContingutArxiu;
import es.caib.ripea.core.api.dto.ArxiuFirmaDto;
import es.caib.ripea.core.api.dto.CarpetaDto;
import es.caib.ripea.core.api.dto.ContingutDto;
import es.caib.ripea.core.api.dto.ContingutTipusEnumDto;
import es.caib.ripea.core.api.dto.DadaDto;
import es.caib.ripea.core.api.dto.DocumentDto;
import es.caib.ripea.core.api.dto.DocumentEstatEnumDto;
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
import es.caib.ripea.core.api.dto.TipusDocumentalDto;
import es.caib.ripea.core.api.dto.UsuariDto;
import es.caib.ripea.core.api.exception.PermissionDeniedException;
import es.caib.ripea.core.api.exception.ValidationException;
import es.caib.ripea.core.api.registre.RegistreInteressat;
import es.caib.ripea.core.entity.CarpetaEntity;
import es.caib.ripea.core.entity.ContingutEntity;
import es.caib.ripea.core.entity.ContingutMovimentEntity;
import es.caib.ripea.core.entity.DadaEntity;
import es.caib.ripea.core.entity.DocumentEntity;
import es.caib.ripea.core.entity.DocumentNotificacioEntity;
import es.caib.ripea.core.entity.EntitatEntity;
import es.caib.ripea.core.entity.ExpedientEntity;
import es.caib.ripea.core.entity.ExpedientEstatEntity;
import es.caib.ripea.core.entity.ExpedientTascaEntity;
import es.caib.ripea.core.entity.GrupEntity;
import es.caib.ripea.core.entity.MetaDocumentEntity;
import es.caib.ripea.core.entity.MetaExpedientEntity;
import es.caib.ripea.core.entity.MetaNodeEntity;
import es.caib.ripea.core.entity.NodeEntity;
import es.caib.ripea.core.entity.OrganGestorEntity;
import es.caib.ripea.core.entity.TipusDocumentalEntity;
import es.caib.ripea.core.entity.UsuariEntity;
import es.caib.ripea.core.firma.DocumentFirmaPortafirmesHelper;
import es.caib.ripea.core.repository.AlertaRepository;
import es.caib.ripea.core.repository.ContingutMovimentRepository;
import es.caib.ripea.core.repository.ContingutRepository;
import es.caib.ripea.core.repository.DadaRepository;
import es.caib.ripea.core.repository.DocumentRepository;
import es.caib.ripea.core.repository.ExpedientEstatRepository;
import es.caib.ripea.core.repository.ExpedientRepository;
import es.caib.ripea.core.repository.ExpedientTascaRepository;
import es.caib.ripea.core.repository.GrupRepository;
import es.caib.ripea.core.repository.InteressatRepository;
import es.caib.ripea.core.repository.TipusDocumentalRepository;
import es.caib.ripea.core.repository.UsuariRepository;
import es.caib.ripea.core.security.ExtendedPermission;
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
	private ExpedientEstatRepository expedientEstatRepository;
	@Autowired
	private GrupRepository grupRepository;
	@Autowired
	private AlertaRepository alertaRepository;
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
	private InteressatRepository interessatRepository;
	
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
				false);
	}
	public ContingutDto toContingutDto(
			ContingutEntity contingut,
			boolean ambPermisos,
			boolean ambFills,
			boolean filtrarFillsSegonsPermisRead,
			boolean ambDades,
			boolean ambPath,
			boolean pathNomesFinsExpedientArrel,
			boolean ambVersions) {
		ContingutDto resposta = null;
		MetaNodeDto metaNode = null;
		// Crea el contenidor del tipus correcte
		ContingutEntity deproxied = HibernateHelper.deproxy(contingut);
		// ##################### EXPEDIENT ##################################
		if (deproxied instanceof ExpedientEntity) {
			ExpedientEntity expedient = (ExpedientEntity)deproxied;
			ExpedientDto dto = new ExpedientDto();
			dto.setEstat(expedient.getEstat());
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
			dto.setNumero(expedientHelper.calcularNumero(expedient));
			dto.setPeticions(expedient.getPeticions() != null && !expedient.getPeticions( ).isEmpty() ? true : false);
			dto.setAgafatPer(
					conversioTipusHelper.convertir(
							expedient.getAgafatPer(),
							UsuariDto.class));
			metaNode = conversioTipusHelper.convertir(
					expedient.getMetaNode(),
					MetaExpedientDto.class);
			dto.setMetaNode(metaNode);
			dto.setValid(
					cacheHelper.findErrorsValidacioPerNode(expedient).isEmpty());
			dto.setHasEsborranys(
					documentHelper.hasFillsEsborranys(expedient));
			dto.setConteDocumentsFirmats(
					documentRepository.countByExpedientAndEstat(
							expedient,
							DocumentEstatEnumDto.CUSTODIAT) > 0);
			dto.setHasAllDocumentsDefinitiu(documentHelper.hasAllDocumentsDefinitiu(expedient));
			// expedient estat
			if (expedient.getExpedientEstat() != null) {
				ExpedientEstatEntity estat =  expedientEstatRepository.findByMetaExpedientAndOrdre(expedient.getExpedientEstat().getMetaExpedient(), expedient.getExpedientEstat().getOrdre()+1);
				if (estat != null) {
					dto.setExpedientEstatNextInOrder(estat.getId());
				} else {//if there is no estat with higher order, choose previous 
					dto.setExpedientEstatNextInOrder(expedient.getExpedientEstat().getId());
				}
				dto.setExpedientEstat(conversioTipusHelper.convertir(
						expedient.getExpedientEstat(),
						ExpedientEstatDto.class));
			}
			try {
				dto.setUsuariActualWrite(false);
				entityComprovarHelper.comprovarPermisosMetaNode(
						expedient.getMetaNode(),
						expedient.getId(),
						false,
						true,
						false,
						false, 
						false);
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
						false);
				dto.setUsuariActualDelete(true);
			} catch (PermissionDeniedException ex) {
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
			dto.setInteressats(conversioTipusHelper.convertirSet(expedient.getInteressats(),InteressatDto.class));
			dto.setInteressatsNotificable(conversioTipusHelper.convertirList(interessatRepository.findByExpedientAndNotRepresentantAndNomesAmbNotificacioActiva(
					expedient), InteressatDto.class));
			dto.setGrupId(expedient.getGrup() != null ? expedient.getGrup().getId() : null);
			
			dto.setOrganGestorId(expedient.getOrganGestor() != null ? expedient.getOrganGestor().getId() : null);
			
			resposta = dto;
		// ##################### DOCUMENT ##################################
		} else if (deproxied instanceof DocumentEntity) {
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
			if (ambVersions && pluginHelper.isArxiuPluginActiu() && pluginHelper.arxiuSuportaVersionsDocuments() && document.getEsborrat() == 0) {
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
			metaNode = conversioTipusHelper.convertir(
					document.getMetaNode(),
					MetaDocumentDto.class);
			dto.setMetaNode(metaNode);
			dto.setValid(
					cacheHelper.findErrorsValidacioPerNode(document).isEmpty());
			resposta = dto;
		// ##################### CARPETA ##################################
		} else if (deproxied instanceof CarpetaEntity) {
			CarpetaDto dto = new CarpetaDto();
			resposta = dto;
		} 
		// ##################### CONTINGUT ##################################
		resposta.setId(contingut.getId());
		resposta.setNom(contingut.getNom());
		resposta.setEsborrat(contingut.getEsborrat());
		resposta.setEsborratData(contingut.getEsborratData());
		resposta.setArxiuUuid(contingut.getArxiuUuid());
		resposta.setArxiuDataActualitzacio(contingut.getArxiuDataActualitzacio());
		if (!contingut.getFills().isEmpty()) {
			resposta.setHasFills(true);
		} else {
			resposta.setHasFills(false);
		}
		if (contingut.getExpedient() != null) {
			resposta.setExpedientPare(
					(ExpedientDto)toContingutDto(
							contingut.getExpedient(),
							ambPermisos,
							false,
							false,
							false,
							false,
							false,
							false));
		}
		resposta.setEntitat(
				conversioTipusHelper.convertir(
						contingut.getEntitat(),
							EntitatDto.class));
		if (contingut.getDarrerMoviment() != null) {
			ContingutMovimentEntity darrerMoviment = contingut.getDarrerMoviment();
			resposta.setDarrerMovimentUsuari(
					conversioTipusHelper.convertir(
							darrerMoviment.getRemitent(),
							UsuariDto.class));
			resposta.setDarrerMovimentData(darrerMoviment.getCreatedDate().toDate());
			resposta.setDarrerMovimentComentari(darrerMoviment.getComentari());
		}
		if (ambPermisos && metaNode != null) {
			// Omple els permisos
			metaNodeHelper.omplirPermisosPerMetaNode(metaNode);
		}
		if (resposta != null) {
			// Omple la informació d'auditoria
			resposta.setCreatedBy(
					conversioTipusHelper.convertir(
							contingut.getCreatedBy(),
							UsuariDto.class));
			resposta.setCreatedDate(contingut.getCreatedDate().toDate());
			resposta.setLastModifiedBy(
					conversioTipusHelper.convertir(
							contingut.getLastModifiedBy(),
							UsuariDto.class));
			resposta.setLastModifiedDate(contingut.getLastModifiedDate().toDate());
		}
		if (resposta != null) {
			resposta.setAlerta(
					alertaRepository.countByLlegidaAndContingutId(
					false,
					contingut.getId()) > 0);
			if (ambPath) {
				// Calcula el path
				List<ContingutDto> path = getPathContingutComDto(
						contingut,
						ambPermisos,
						pathNomesFinsExpedientArrel);
				resposta.setPath(path);
			}
			if (ambFills) {
				// Cerca els nodes fills
				Authentication auth = SecurityContextHolder.getContext().getAuthentication();
				List<ContingutDto> contenidorDtos = new ArrayList<ContingutDto>();
				List<ContingutEntity> fills = contingutRepository.findByPareAndEsborrat(
						contingut,
						0,
						isOrdenacioPermesa() ? new Sort("ordre") : new Sort("createdDate"));
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
				List<ContingutDto> fillPath = null;
				if (ambPath) {
					fillPath = new ArrayList<ContingutDto>();
					if (resposta.getPath() != null)
						fillPath.addAll(resposta.getPath());
					fillPath.add(toContingutDto(
							contingut,
							ambPermisos,
							false,
							false,
							false,
							false,
							false,
							false));
				}
				for (ContingutEntity fill: fills) {
					if (fill.getEsborrat() == 0) {
						ContingutDto fillDto = toContingutDto(
								fill,
								ambPermisos,
								false,
								false,
								false,
								false,
								false,
								false);
						// Configura el pare de cada fill
						fillDto.setPath(fillPath);
						contenidorDtos.add(fillDto);
					}
				}
				resposta.setFills(contenidorDtos);
			}
			if (ambDades && contingut instanceof NodeEntity) {
				NodeEntity node = (NodeEntity)contingut;
				List<DadaEntity> dades = dadaRepository.findByNode(node);
				((NodeDto)resposta).setDades(
						conversioTipusHelper.convertirList(
								dades,
								DadaDto.class));
				for (int i = 0; i < dades.size(); i++) {
					((NodeDto)resposta).getDades().get(i).setValor(dades.get(i).getValor());
				}
			}
		}
		return resposta;
	}
	
	public DocumentDto generarDocumentDto(
			DocumentNotificacioEntity notificacio,
			MetaDocumentEntity metaDocument,
			RespostaConsultaEstatEnviament resposta) {
		DocumentDto dto = new DocumentDto();
		MetaNodeDto metaNode = null;
		dto.setNom("Certificació_" + notificacio.getAssumpte().replaceAll("\\s+","_"));
		dto.setDocumentTipus(DocumentTipusEnumDto.DIGITAL);
		dto.setUbicacio(null);
		dto.setData(resposta.getCertificacioData());
		if (resposta.getCertificacioContingut() != null) {
			logger.debug("[CERT] Generant fitxer certificació...");
			dto.setFitxerNom("Certificació_" + notificacio.getAssumpte().replaceAll("\\s+","_") + ".pdf");
			dto.setFitxerContentType(resposta.getCertificacioTipusMime());
			dto.setFitxerContingut(resposta.getCertificacioContingut());
			logger.debug("[CERT] El fitxer s'ha generat correctament amb nom: " + dto.getFitxerNom());
			
//			## Comprovar si la certificació està firmada
			if (resposta.getCertificacioTipusMime() != null && resposta.getCertificacioTipusMime().equals("application/pdf") && isCertificacioAmbFirma(resposta.getCertificacioContingut())) {
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
			boolean checkPerMassiuAdmin) {
		ContingutEntity contingut = comprovarContingutDinsExpedientModificable(
				entitatId,
				contingutId,
				comprovarPermisRead,
				comprovarPermisWrite,
				comprovarPermisCreate,
				comprovarPermisDelete, 
				checkPerMassiuAdmin);
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
			boolean checkPerMassiuAdmin) {
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
				checkPerMassiuAdmin);
		if (expedient == null) {
			throw new ValidationException(
					contingutId,
					ContingutEntity.class,
					"No es pot modificar un contingut que no està associat a un expedient");
		}
		
		if (!checkPerMassiuAdmin) {
		// Comprova que l'usuari actual te agafat l'expedient
		UsuariEntity agafatPer = expedient.getAgafatPer();
		if (agafatPer == null) {
			throw new ValidationException(
					contingutId,
					ContingutEntity.class,
					"L'expedient al qual pertany el contingut no està agafat per cap usuari");
		}
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
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
					checkPerMassiuAdmin);
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
				false);
		if (ContingutTipusEnumDto.EXPEDIENT.equals(contingut.getTipus())) {
			comprovarPermisosExpedient(
					(ExpedientEntity)contingut,
					comprovarPermisRead,
					comprovarPermisWrite,
					false,
					false, false);
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
		
		if (!expedientTascaEntity.getResponsable().getCodi().equals(auth.getName())) {
			throw new SecurityException("Sense permisos per accedir la tasca ("
					+ "tascaId=" + expedientTascaEntity.getId() + ", "
					+ "usuari=" + auth.getName() + ")");
		}
		
		return contingut;
	}
	
	

	public ContingutDto deleteReversible(
			Long entitatId,
			ContingutEntity contingut) throws IOException {
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
				false);
		// Comprova que el contingut no estigui esborrat
		if (contingut.getEsborrat() > 0) {
			logger.error("Aquest contingut ja està esborrat (contingutId=" + contingut.getId() + ")");
			throw new ValidationException(
					contingut.getId(),
					ContingutEntity.class,
					"Aquest contingut ja està esborrat");
		}

		// Marca el contingut i tots els seus fills com a esborrats
		//  de forma recursiva
		marcarEsborrat(contingut);
		
		// Valida si conté documents definitius
		if (!conteDocumentsDefinitius(contingut)) {
			
			// Si el contingut és un document guarda una còpia del fitxer esborrat
			// per a poder recuperar-lo posteriorment
			if (contingut instanceof DocumentEntity) {
				DocumentEntity document = (DocumentEntity)contingut;
				if (DocumentTipusEnumDto.DIGITAL.equals(document.getDocumentTipus()) && document.getGesDocAdjuntId() == null) {
					fitxerDocumentEsborratGuardarEnTmp((DocumentEntity)contingut);
				}
				if (document.getGesDocAdjuntId() == null) {
					// Elimina contingut a l'arxiu
					arxiuPropagarEliminacio(contingut);
				}
			} else {
				// Elimina contingut a l'arxiu
				arxiuPropagarEliminacio(contingut);
			}

		}
		// Cancel·lar enviament si el document conté enviaments pendents
		if (contingut instanceof DocumentEntity) {
			DocumentEntity document = (DocumentEntity)contingut;
			if (document.getEstat().equals(DocumentEstatEnumDto.FIRMA_PENDENT)) {
				firmaPortafirmesHelper.portafirmesCancelar(
						entitatId,
						document);
			}
		}
		return dto;
	}
	
	private boolean conteDocumentsDefinitius(ContingutEntity contingut) {
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
			conteDefinitius = !DocumentEstatEnumDto.REDACCIO.equals(document.getEstat()) && !DocumentEstatEnumDto.FIRMA_PARCIAL.equals(document.getEstat());
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


	private void comprovarPermisosExpedient(
			ExpedientEntity expedient,
			boolean comprovarPermisRead,
			boolean comprovarPermisWrite,
			boolean comprovarPermisCreate,
			boolean comprovarPermisDelete, 
			boolean checkPerMassiuAdmin) {
		if (expedient.getMetaNode() != null) {
			entityComprovarHelper.comprovarPermisosMetaNode(
					expedient.getMetaNode(),
					expedient.getId(),
					comprovarPermisRead,
					comprovarPermisWrite,
					comprovarPermisCreate,
					comprovarPermisDelete, 
					checkPerMassiuAdmin);
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
			boolean checkPerMassiuAdmin) {
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
						false);
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
							false);
					
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

	/**
	 * Check if given name (@param nom) doesnt already exist inside given container (@param contingutPare)
	 * @param contingutPare
	 * @param nom
	 * @param objectId
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
			ContingutEntity contingut,
			FitxerDto fitxer,
			boolean documentAmbFirma,
			boolean firmaSeparada,
			List<ArxiuFirmaDto> firmes) {
		String serieDocumental = null;
		ExpedientEntity expedient = contingut.getExpedient();
		if (expedient != null) {
			serieDocumental = expedient.getMetaExpedient().getSerieDocumental();
		}
		if (pluginHelper.isArxiuPluginActiu()) {
			//##################### EXPEDIENT #####################
			if (contingut instanceof ExpedientEntity) {
				pluginHelper.arxiuExpedientActualitzar((ExpedientEntity)contingut);
			//##################### DOCUMENT #####################
			} else if (contingut instanceof DocumentEntity) {
				//No actualizar dins SGD si és un document importat de Regweb
				String custodiaDocumentId = null;
				DocumentEntity document = (DocumentEntity) contingut;
				custodiaDocumentId = pluginHelper.arxiuDocumentActualitzar(
						(DocumentEntity) contingut,
						isCarpetaLogica() ? contingut.getExpedientPare() : contingut.getPare(),
						serieDocumental,
						fitxer,
						documentAmbFirma,
						firmaSeparada,
						firmes);
				documentHelper.actualitzarVersionsDocument((DocumentEntity) contingut);
				if (firmes != null) {
					// Custodia el document firmat
					((DocumentEntity) contingut).updateEstat(DocumentEstatEnumDto.CUSTODIAT);
					((DocumentEntity) contingut).updateInformacioCustodia(
							new Date(),
							custodiaDocumentId != null ? custodiaDocumentId : document.getArxiuUuid(),
							((DocumentEntity) contingut).getCustodiaCsv());
					// Registra al log la custòdia de la firma del document
					contingutLogHelper.log((
							(DocumentEntity) contingut),
							LogTipusEnumDto.ARXIU_CUSTODIAT,
							custodiaDocumentId != null ? custodiaDocumentId : document.getArxiuUuid(),
							null,
							false,
							false);
				}
			//##################### CARPETA #####################
			} else if (contingut instanceof CarpetaEntity) {
				if (!isCarpetaLogica()) {
					pluginHelper.arxiuCarpetaActualitzar((CarpetaEntity) contingut,
							contingut.getPare());
				}
			} else {
				throw new ValidationException(
						contingut.getId(),
						ContingutEntity.class,
						"El contingut que es vol propagar a l'arxiu no és del tipus expedient, document o carpeta");
			}
		}
	}

	public void arxiuPropagarEliminacio(ContingutEntity contingut) {
		if (contingut.getArxiuUuid() != null) {
			if (pluginHelper.isArxiuPluginActiu()) {
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
			} else {
				throw new ValidationException(
						contingut.getId(),
						ContingutEntity.class,
						"S'ha d'esborrar un contingut de l'arxiu però el plugin no està habilitat");
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

	public String arxiuPropagarMoviment(
			ContingutEntity contingut,
			ContingutEntity desti,
			String expedientDestiUuid) {
		if (contingut instanceof DocumentEntity) {
			return pluginHelper.arxiuDocumentMoure(
					(DocumentEntity)contingut,
					desti.getArxiuUuid(),
					expedientDestiUuid);
		} else if (contingut instanceof CarpetaEntity && !isCarpetaLogica()) {
			pluginHelper.arxiuCarpetaMoure(
					(CarpetaEntity)contingut,
					desti.getArxiuUuid());
		}
		return null;
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
			boolean nomesFinsExpedientArrel) {
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
								false));
				}
			}
		}
		return pathDto;
	}
	
	public FitxerDto generarIndex(
			EntitatEntity entitatActual, 
			ExpedientEntity expedient,
			boolean exportar) throws IOException {
		
		byte[] indexGenerated = indexHelper.generarIndexPerExpedient(
					expedient,
					entitatActual,
					exportar);
		
		FitxerDto fitxer = new FitxerDto();
		fitxer.setNom(messageHelper.getMessage("expedient.service.exportacio.index") + " " + expedient.getNom() + ".pdf");
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
	
	/*private Long getCountByContingut(
			ContingutEntity contingut,
			List<Object[]> counts) {
		for (Object[] count: counts) {
			Long contingutId = (Long)count[0];
			if (contingutId.equals(contingut.getId())) {
				return (Long)count[1];
			}
		}
		return new Long(0);
	}*/
	
	public String getBaseDir() {
		return PropertiesHelper.getProperties().getProperty("es.caib.ripea.app.data.dir") + "/esborrats-tmp";
	}
	
	public boolean isCarpetaLogica() {
		String carpetesLogiques = PropertiesHelper.getProperties().getProperty("es.caib.ripea.carpetes.logiques");
		return Boolean.valueOf(carpetesLogiques);
	}
	
	public boolean isOrdenacioPermesa() {
		String isOrdenacioPermesa = PropertiesHelper.getProperties().getProperty("es.caib.ripea.ordenacio.contingut.habilitada");
		return Boolean.valueOf(isOrdenacioPermesa);
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
	
	private static final Logger logger = LoggerFactory.getLogger(ContingutHelper.class);

}
