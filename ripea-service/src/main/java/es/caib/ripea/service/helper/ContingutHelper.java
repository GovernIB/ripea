/**
 * 
 */
package es.caib.ripea.service.helper;

import com.lowagie.text.pdf.AcroFields;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfReader;
import es.caib.plugins.arxiu.api.Carpeta;
import es.caib.plugins.arxiu.api.ContingutArxiu;
import es.caib.plugins.arxiu.api.Document;
import es.caib.plugins.arxiu.caib.ArxiuCaibException;
import es.caib.ripea.core.persistence.entity.*;
import es.caib.ripea.core.persistence.repository.*;
import es.caib.ripea.plugin.arxiu.ArxiuContingutTipusEnum;
import es.caib.ripea.plugin.arxiu.ArxiuDocumentContingut;
import es.caib.ripea.plugin.notificacio.RespostaConsultaEstatEnviament;
import es.caib.ripea.service.firma.DocumentFirmaPortafirmesHelper;
import es.caib.ripea.service.intf.dto.*;
import es.caib.ripea.service.intf.dto.ResultDocumentsSenseContingut.ResultDocumentSenseContingut;
import es.caib.ripea.service.intf.dto.ResultDocumentsSenseContingut.ResultDocumentSenseContingut.ResultDocumentSenseContingutBuilder;
import es.caib.ripea.service.intf.exception.ArxiuJaGuardatException;
import es.caib.ripea.service.intf.exception.NotFoundException;
import es.caib.ripea.service.intf.exception.PermissionDeniedException;
import es.caib.ripea.service.intf.exception.ValidationException;
import es.caib.ripea.service.intf.registre.RegistreInteressat;
import es.caib.ripea.service.intf.utils.Utils;
import es.caib.ripea.service.permission.ExtendedPermission;
import lombok.Builder;
import lombok.Data;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.ZoneId;
import java.util.*;
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
	private MetaDocumentRepository metaDocumentRepository;
	@Autowired
	private DocumentPortafirmesRepository documentPortafirmesRepository;
	@Autowired
	private DocumentNotificacioRepository documentNotificacioRepository;

	private static final int NO_ESBORRAT = 0;

	@Builder
	@Data
	public static class ToContingutParams {
		private boolean ambPermisos;
		private boolean ambFills;
		private boolean ambDades;
		private boolean ambPath;
		private boolean pathNomesFinsExpedientArrel;
		private boolean ambVersions;
		private String rolActual;
		private boolean onlyForList;
		private Long organActualId;
		private boolean onlyFirstDescendant;
		private int level;
		private ExpedientDto expedientDto;
		private List<ContingutDto> pathDto;
		private boolean ambExpedientPare;
		private boolean ambEntitat;
		private boolean ambMapPerTipusDocument;
		private boolean ambMapPerEstat;

		public void addLevel() {
			this.level++;
		}
	}


	public ContingutDto toContingutDto(
			ContingutEntity contingut,
			boolean ambPath, 
			boolean pathNomesFinsExpedientArrel) {
		return toContingutDto(
				contingut,
				ToContingutParams.builder()
						.ambPath(ambPath)
						.pathNomesFinsExpedientArrel(pathNomesFinsExpedientArrel)
						.build());
	}

	public ContingutDto toContingutDto(ContingutEntity contingut,
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
		return toContingutDto(
				contingut,
				ToContingutParams.builder()
						.ambPermisos(ambPermisos)
						.ambFills(ambFills)
						.ambDades(ambDades)
						.ambPath(ambPath)
						.pathNomesFinsExpedientArrel(pathNomesFinsExpedientArrel)
						.ambVersions(ambVersions)
						.rolActual(rolActual)
						.onlyForList(onlyForList)
						.organActualId(organActualId)
						.onlyFirstDescendant(onlyFirstDescendant)
						.level(level)
						.expedientDto(expedientDto)
						.pathDto(pathDto)
						.ambExpedientPare(ambExpedientPare)
						.ambEntitat(ambEntitat)
						.ambMapPerTipusDocument(ambMapPerTipusDocument)
						.ambMapPerEstat(ambMapPerEstat)
						.build());
	}

	public ContingutDto toContingutDto(ContingutEntity contingut, ToContingutParams params) {
		organGestorHelper.actualitzarOrganCodi(organGestorHelper.getOrganCodiFromContingutId(contingut.getId()));
		ContingutEntity deproxied = HibernateHelper.deproxy(contingut);

		params.addLevel();
		ContingutDto resposta = createDtoFromEntity(deproxied, params);

		long t1 = System.currentTimeMillis();
		String tipus = contingut.getClass().toString().replace("class es.caib.ripea.core.entity.", "").replace("Entity", "").toLowerCase() + "] start (" + contingut.getId();
		logMsg("toContingutDto[" + tipus + ", level=" + params.getLevel() + ") ");

		setBasicProperties(resposta, contingut);
		setAlerta(resposta, contingut);

		if (!params.isOnlyForList()) {
			setDetailedProperties(resposta, contingut, params);
			if (params.isAmbExpedientPare()) { 
				setExpedientPare(resposta, contingut, params);
			}
			List<ContingutDto> pathCalculatPerFills = calculaPathPerFills(resposta, contingut, params);
			if (params.isAmbFills()) {
				setFills(resposta, contingut, params, pathCalculatPerFills);
			}
		}
		logMsg("toContingutDto[" + tipus + "] end (" + contingut.getId() + ", level=" + params.getLevel() + "): "+ (System.currentTimeMillis() - t1) + " ms");
		
		return resposta;
	}

	private ContingutDto createDtoFromEntity(ContingutEntity deproxied, ToContingutParams params) {
		if (deproxied instanceof ExpedientEntity) {
			return createExpedientDto((ExpedientEntity) deproxied, params);
		} else if (deproxied instanceof DocumentEntity) {
			return createDocumentDto((DocumentEntity) deproxied, params);
		} else if (deproxied instanceof CarpetaEntity) {
			return createCarpetaDto((CarpetaEntity) deproxied, params);
		}
		return null;
	}


	// CONTINGUT
	// //////////////////////////////////////////////////////////////////////////////////////////

	private void setBasicProperties(ContingutDto resposta, ContingutEntity contingut) {
		resposta.setId(contingut.getId());
		resposta.setNom(contingut.getNom());
		resposta.setArxiuUuid(contingut.getArxiuUuid());
		resposta.setCreatedDate(
				Date.from(contingut.getCreatedDate().get().atZone(ZoneId.systemDefault()).toInstant()));
	}

	private void setAlerta(ContingutDto resposta, ContingutEntity contingut) {
		long t1 = System.currentTimeMillis();
		resposta.setAlerta(alertaRepository.countByLlegidaAndContingutId(false, contingut.getId()) > 0);
		logMsg("setAlerta time (" + contingut.getId() + "):  " + (System.currentTimeMillis() - t1) + " ms");
	}

	private void setDetailedProperties(ContingutDto resposta, ContingutEntity contingut, ToContingutParams params) {
		long t1 = System.currentTimeMillis();
		resposta.setEsborrat(contingut.getEsborrat());
		resposta.setEsborratData(contingut.getEsborratData());
		resposta.setArxiuDataActualitzacio(contingut.getArxiuDataActualitzacio());
		resposta.setHasFills(contingutRepository.hasFills(contingut, NO_ESBORRAT));
		logMsg("setDetailedProperties time (" + contingut.getId() + "):  " + (System.currentTimeMillis() - t1) + " ms");

		if (params.isAmbEntitat()) {
			t1 = System.currentTimeMillis();
			resposta.setEntitat(conversioTipusHelper.convertir(contingut.getEntitat(), EntitatDto.class));
			logMsg("setEntitat time (" + contingut.getId() + "):  " + (System.currentTimeMillis() - t1) + " ms");
		}

		if (contingut.getDarrerMoviment() != null) {
			setDarrerMoviment(resposta, contingut);
		}

		if (params.isAmbPermisos()) {
			t1 = System.currentTimeMillis();
			resposta.setAdmin(checkIfUserIsAdminOfContingut(contingut.getId(), params.getRolActual()));
			logMsg("ambPermisos time (" + contingut.getId() + "):  " + (System.currentTimeMillis() - t1) + " ms");
		}

		setAuditInfo(resposta, contingut);

		if (params.isAmbDades() && contingut instanceof NodeEntity) {
			setDades((NodeDto) resposta, (NodeEntity) contingut);
		}
	}

	private void setDarrerMoviment(ContingutDto resposta, ContingutEntity contingut) {
		long t1 = System.currentTimeMillis();
		ContingutMovimentEntity darrerMoviment = contingut.getDarrerMoviment();
		resposta.setDarrerMovimentUsuari(conversioTipusHelper.convertir(darrerMoviment.getRemitent(), UsuariDto.class));
		resposta.setDarrerMovimentData(
				Date.from(darrerMoviment.getCreatedDate().get().atZone(ZoneId.systemDefault()).toInstant()));
		resposta.setDarrerMovimentComentari(darrerMoviment.getComentari());
		logMsg("setDarrerMoviment time (" + contingut.getId() + "):  " + (System.currentTimeMillis() - t1) + " ms");
	}

	private void setAuditInfo(ContingutDto resposta, ContingutEntity contingut) {
		long t1 = System.currentTimeMillis();
		resposta.setCreatedBy(conversioTipusHelper.convertir(contingut.getCreatedBy(), UsuariDto.class));
		resposta.setLastModifiedBy(conversioTipusHelper.convertir(contingut.getLastModifiedBy(), UsuariDto.class));
		resposta.setLastModifiedDate(
				Date.from(contingut.getLastModifiedDate().get().atZone(ZoneId.systemDefault()).toInstant()));
		logMsg("setAuditInfo time (" + contingut.getId() + "):  " + (System.currentTimeMillis() - t1) + " ms");
	}

	private void setDades(NodeDto resposta, NodeEntity node) {
		long t1 = System.currentTimeMillis();
		List<DadaEntity> dades = dadaRepository.findByNode(node);
		resposta.setDades(conversioTipusHelper.convertirList(dades, DadaDto.class));
		for (int i = 0; i < dades.size(); i++) {
			resposta.getDades().get(i).setValor(dades.get(i).getValor());
		}
		logMsg("setDades time (" + node.getId() + "):  " + (System.currentTimeMillis() - t1) + " ms");
	}

	private void setExpedientPare(ContingutDto resposta, ContingutEntity contingut, ToContingutParams params) {
		ExpedientDto expedientCalculat = calculateExpedientPare(resposta, contingut, params);
		resposta.setExpedientPare(expedientCalculat);
	}

	private ExpedientDto calculateExpedientPare(ContingutDto resposta, ContingutEntity contingut, ToContingutParams params) {
		if (contingut instanceof ExpedientEntity) {
			return (ExpedientDto) resposta;
		} else if (params.getExpedientDto() != null) {
			return params.getExpedientDto();
		} else {
			long t1 = System.currentTimeMillis();
			logMsg("expedientPare (recursive) start (" + contingut.getId() + ") ");
			ExpedientDto expedientCalculat = (ExpedientDto) toContingutDto(
					contingut.getExpedient(),
					ToContingutParams.builder()
							.ambPermisos(params.isAmbPermisos())
							.rolActual(params.getRolActual())
							.onlyForList(params.isOnlyForList())
							.organActualId(params.getOrganActualId())
							.onlyFirstDescendant(params.isOnlyFirstDescendant())
							.level(params.getLevel())
							.ambExpedientPare(params.isAmbExpedientPare())
							.ambEntitat(params.isAmbEntitat())
							.ambMapPerTipusDocument(params.isAmbMapPerTipusDocument())
							.ambMapPerEstat(params.isAmbMapPerEstat())
							.build());
			logMsg("expedientPare (recursive) end (" + contingut.getId() + "):  " + (System.currentTimeMillis() - t1) + " ms");
			return expedientCalculat;
		}
	}

	private List<ContingutDto> calculaPathPerFills(ContingutDto resposta, ContingutEntity contingut, ToContingutParams params) {
		if (!params.isAmbPath()) return null;

		List<ContingutDto> pathCalculatPerFills = calculatePath(contingut, params);
		resposta.setPath(pathCalculatPerFills);

		if (pathCalculatPerFills == null) {
			pathCalculatPerFills = new ArrayList<>();
		}
		pathCalculatPerFills.add(resposta);
		return pathCalculatPerFills;
	}

	private List<ContingutDto> calculatePath(ContingutEntity contingut, ToContingutParams params) {
		if (contingut instanceof ExpedientEntity) {
			return null;
		} else if (params.getPathDto() != null) {
			return params.getPathDto();
		} else {
			long t1 = System.currentTimeMillis();
			logMsg("path (recursive) start (" + contingut.getId() + ") ");
			List<ContingutDto> pathCalculatPerThisContingut = getPathContingutComDto(contingut, params.isAmbPermisos(), params.isPathNomesFinsExpedientArrel(), params.getLevel());
			logMsg("path (recursive) end (" + contingut.getId() + "):  " + (System.currentTimeMillis() - t1) + " ms");
			return pathCalculatPerThisContingut;
		}
	}

	private void setFills(ContingutDto resposta, ContingutEntity contingut, ToContingutParams params, List<ContingutDto> pathCalculatPerFills) {
		long t1 = System.currentTimeMillis();
		logMsg("ambFills (recursive) start (" + contingut.getId() + ") ");

		List<ContingutEntity> fills = isOrdenacioPermesa() ?
				contingutRepository.findByPareAndEsborratAndOrdenatOrdre(contingut, NO_ESBORRAT) :
				contingutRepository.findByPareAndEsborratAndOrdenat(contingut, NO_ESBORRAT);

		List<ContingutDto> fillsDtos = convertFillsToDtos(fills, resposta.getExpedientPare(), params, pathCalculatPerFills);
		resposta.setFills(fillsDtos);

		logMsg("ambFills (recursive) end (" + contingut.getId() + "):  " + (System.currentTimeMillis() - t1) + " ms");
	}

	private List<ContingutDto> convertFillsToDtos(List<ContingutEntity> fills, ExpedientDto expedientCalculat, ToContingutParams params, List<ContingutDto> pathCalculatPerFills) {
		List<ContingutDto> fillsDtos = new ArrayList<>();
		for (ContingutEntity fill: fills) {
			if (fill.getEsborrat() == 0) {
				fillsDtos.add(toContingutDto(fill, createParamsForFill(fill, params, expedientCalculat, pathCalculatPerFills)));
			}
		}
		return fillsDtos;
	}

	private ToContingutParams createParamsForFill(ContingutEntity fill, ToContingutParams params, ExpedientDto expedientCalculat, List<ContingutDto> pathCalculatPerFills) {
		ToContingutParams aux = ToContingutParams.builder()
				.ambPermisos(params.isAmbPermisos())
				.ambFills(!params.isOnlyFirstDescendant())
				//.ambPath(!(fill instanceof DocumentEntity))
				.ambPath(params.isAmbPath())
				.rolActual(params.getRolActual())
				.onlyForList(params.isOnlyForList())
				.organActualId(params.getOrganActualId())
				.onlyFirstDescendant(params.isOnlyFirstDescendant())
				.level(params.getLevel())
				.expedientDto(expedientCalculat)
				.pathDto(pathCalculatPerFills)
				.ambExpedientPare(params.isAmbExpedientPare())
				.ambEntitat(params.isAmbEntitat())
				.ambMapPerTipusDocument(params.isAmbMapPerTipusDocument())
				.ambMapPerEstat(params.isAmbMapPerEstat())
				.build();
		
		return aux;
	}

	private ExpedientDto createExpedientDto(ExpedientEntity expedient, ToContingutParams params) {
		long t1 = System.currentTimeMillis();
		logMsg("toExpedientDto start (" + expedient.getId() + ", level=" + params.getLevel() + ") ");

		ExpedientDto dto = new ExpedientDto();
		setExpedientBasicProperties(dto, expedient);

		if (params.isAmbPermisos()) {
			setExpedientPermisos(dto, expedient.getId());
		}
		setExpedientSeguidor(dto, expedient);
		setExpedientEstatErrors(dto, expedient);
		setExpedientEstatDocuments(dto, expedient);

		if (params.isOnlyForList()) {
			setExpedientNomesPerLlista(dto, expedient, params.getRolActual());
		} else {
			setExpedientComplet(dto, expedient, params);
		}

		logMsg("toExpedientDto end (" + expedient.getId() + "):  " + (System.currentTimeMillis() - t1) + " ms");
		return dto;
	}

	private void setExpedientBasicProperties(ExpedientDto dto, ExpedientEntity expedient) {
		dto.setNumero(expedient.getNumero());
		dto.setEstat(expedient.getEstat());
		if (expedient.getEstatAdditional() != null) {
			dto.setExpedientEstat(conversioTipusHelper.convertir(expedient.getEstatAdditional(), ExpedientEstatDto.class));
		}
		dto.setAgafatPer(conversioTipusHelper.convertir(expedient.getAgafatPer(), UsuariDto.class));

        List<ValidacioErrorDto> errorsValidacio = cacheHelper.findErrorsValidacioPerNode(expedient);
        dto.setValid(errorsValidacio.isEmpty());
        
        boolean notificacionsCaducades = false;
        List<DocumentEntity> documents = documentRepository.findByExpedientAndEsborrat(expedient, 0);
        for (DocumentEntity document : documents) {
	        List<DocumentNotificacioEntity> notificacionsPendents = documentNotificacioRepository.findByDocumentOrderByCreatedDateDesc(document);
	        if (notificacionsPendents!=null && notificacionsPendents.size()>0) {
	        	if (notificacionsPendents.get(0).isCaducada() && !notificacionsPendents.get(0).isNotificacioFinalitzada()) {
	            	notificacionsCaducades = true;
	            	break;
	            }
	        }
	        if (notificacionsCaducades) break;
        }
        
        dto.setNotificacionsCaducades(notificacionsCaducades);
		dto.setNumSeguidors(expedient.getSeguidors().size());
		dto.setNumComentaris(expedient.getComentaris().size());
		dto.setMetaNode(conversioTipusHelper.convertir(expedient.getMetaNode(), MetaExpedientDto.class));
		dto.setInteressats(conversioTipusHelper.convertirSet(expedient.getInteressatsORepresentants(), InteressatDto.class));
		dto.setGrupId(expedient.getGrup() != null ? expedient.getGrup().getId() : null);
		dto.setGrupNom(expedient.getGrup() != null ? expedient.getGrup().getDescripcio() : null);
        dto.setPrioritat(expedient.getPrioritat());
        dto.setPrioritatMotiu(expedient.getPrioritatMotiu());
	}

	private void setExpedientPermisos(ExpedientDto dto, Long expedientId) {
		long t1 = System.currentTimeMillis();
		try {
			dto.setUsuariActualWrite(false);
			entityComprovarHelper.comprovarExpedientPermisWrite(expedientId);
			dto.setUsuariActualWrite(true);
		} catch (PermissionDeniedException ex) {}

		try {
			dto.setUsuariActualDelete(false);
			entityComprovarHelper.comprovarExpedientPermisDelete(expedientId);
			dto.setUsuariActualDelete(true);
		} catch (PermissionDeniedException ex) {}
		logMsg("toExpedientDto comprovarPermisos time:  " + (System.currentTimeMillis() - t1) + " ms");
	}

	private void setExpedientSeguidor(ExpedientDto dto, ExpedientEntity expedient) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null) {
			UsuariEntity usuariActual = usuariRepository.findByCodi(auth.getName());
			dto.setSeguidor(expedient.getSeguidors().contains(usuariActual));
		}
	}

	private void setExpedientEstatErrors(ExpedientDto dto, ExpedientEntity expedient) {
		dto.setErrorLastEnviament(cacheHelper.hasEnviamentsPortafirmesAmbErrorPerExpedient(expedient));
		dto.setErrorLastNotificacio(cacheHelper.hasNotificacionsAmbErrorPerExpedient(expedient));
		dto.setAmbEnviamentsPendents(cacheHelper.hasEnviamentsPortafirmesPendentsPerExpedient(expedient));
		dto.setAmbNotificacionsPendents(cacheHelper.hasNotificacionsPendentsPerExpedient(expedient));
	}

	private void setExpedientEstatDocuments(ExpedientDto dto, ExpedientEntity expedient) {
		dto.setConteDocuments(CollectionUtils.isNotEmpty(documentRepository.findByExpedientAndEsborrat(expedient, NO_ESBORRAT)));
		dto.setConteDocumentsDefinitius(documentRepository.expedientHasDocumentsDefinitius(expedient));
		dto.setConteDocumentsEnProcessDeFirma(CollectionUtils.isNotEmpty(documentRepository.findEnProccessDeFirma(expedient)));
		dto.setConteDocumentsDePortafirmesNoCustodiats(CollectionUtils.isNotEmpty(documentRepository.findDocumentsDePortafirmesNoCustodiats(expedient)));
		dto.setConteDocumentsPendentsReintentsArxiu(CollectionUtils.isNotEmpty(documentRepository.findDocumentsPendentsReintentsArxiu(expedient, getArxiuMaxReintentsDocuments())));
		dto.setConteDocumentsDeAnotacionesNoMogutsASerieFinal(CollectionUtils.isNotEmpty(registreAnnexRepository.findDocumentsDeAnotacionesNoMogutsASerieFinal(expedient)));
	}

	private void setExpedientNomesPerLlista(ExpedientDto dto, ExpedientEntity expedient, String rolActual) {
		dto.setDataDarrerEnviament(cacheHelper.getDataDarrerEnviament(expedient));
		dto.setRolActualAdminEntitatOAdminOrgan(entityComprovarHelper.comprovarRolActualAdminEntitatOAdminOrganDelExpedient(expedient, rolActual));
		dto.setPotModificar(entityComprovarHelper.comprovarSiEsPotModificarExpedient(expedient));
		dto.setExpedientAgafatPerUsuariActual(entityComprovarHelper.comprovarSiExpedientAgafatPerUsuariActual(expedient));
		dto.setRolActualPermisPerModificarExpedient(entityComprovarHelper.comprovarSiRolTePermisPerModificarExpedient(expedient, rolActual));
		dto.setPotReobrir(entityComprovarHelper.comprovarSiEsPotReobrirExpedient(expedient));
	}

	private void setExpedientComplet(ExpedientDto dto, ExpedientEntity expedient, ToContingutParams params) {
		setExpedientInformacioBasica(dto, expedient);
		setExpedientInformacioDocumental(dto, expedient);
		setExpedientNextEstat(dto, expedient);
//		dto.setInteressatsNotificable(conversioTipusHelper.convertirList(expedientInteressatHelper.findByExpedientAndNotRepresentantAndAmbDadesPerNotificacio(expedient), InteressatDto.class));
		setExpedientOrganGestor(dto, expedient);

		if (params.isAmbMapPerTipusDocument() && params.isAmbFills()) {
			setMapPerTipusDocument(dto, expedient, params);
		}

		if (params.isAmbMapPerEstat() && params.isAmbFills()) {
			setMapPerEstat(dto, expedient, params);
		}

	}

	private void setExpedientInformacioBasica(ExpedientDto dto, ExpedientEntity expedient) {
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
		dto.setPeticions(expedient.getPeticions() != null && !expedient.getPeticions().isEmpty());
	}

	private void setExpedientInformacioDocumental(ExpedientDto dto, ExpedientEntity expedient) {
		dto.setHasEsborranys(documentRepository.hasFillsEsborranys(expedient));
		dto.setConteDocumentsFirmats(documentRepository.countByExpedientAndEstat(expedient, DocumentEstatEnumDto.CUSTODIAT) > 0);
		dto.setHasAllDocumentsDefinitiu(documentRepository.hasAllDocumentsDefinitiu(expedient));
	}

	private void setExpedientNextEstat(ExpedientDto dto, ExpedientEntity expedient) {
		if (expedient.getEstatAdditional() != null) {
			ExpedientEstatEntity estat = expedientEstatRepository.findByMetaExpedientAndOrdre(expedient.getEstatAdditional().getMetaExpedient(), expedient.getEstatAdditional().getOrdre() + 1);
			dto.setExpedientEstatNextInOrder(estat != null ? estat.getId() : expedient.getEstatAdditional().getId());
		}
	}

	private void setExpedientOrganGestor(ExpedientDto dto, ExpedientEntity expedient) {
		OrganGestorEntity organGestor = expedient.getOrganGestor();
		dto.setOrganGestorId(organGestor != null ? organGestor.getId() : null);
		dto.setOrganGestorText(organGestor != null ? organGestor.getCodi() + " - " + organGestor.getNom() : "");
	}

	private void setMapPerTipusDocument(ExpedientDto dto, ExpedientEntity expedient, ToContingutParams params) {
		logMsg("ambMapPerTipusDocument start (" + expedient.getId() + ")");
		long t1 = System.currentTimeMillis();

		Map<MetaDocumentDto, List<ContingutDto>> mapPerTipusDocument = new LinkedHashMap<>();
		List<MetaDocumentEntity> metaDocuments = metaDocumentRepository.findByMetaExpedientAndActiuTrueOrderByOrdreAsc(expedient.getMetaExpedient());

        if (getPropertyGuardarCertificacioExpedient()) {
            MetaDocumentEntity metaDocumentAcuseRebut = metaDocumentRepository.findByEntitatAndTipusGeneric(
                    true,
                    null,
                    MetaDocumentTipusGenericEnumDto.ACUSE_RECIBO_NOTIFICACION);

            if (metaDocumentAcuseRebut != null)
                metaDocuments.add(metaDocumentAcuseRebut);
        }

		for (MetaDocumentEntity metaDocument : metaDocuments) {
			List<DocumentEntity> documents = documentRepository.findByExpedientAndMetaNodeAndEsborrat(expedient, metaDocument, NO_ESBORRAT);
			MetaDocumentDto metaDocumentDto = conversioTipusHelper.convertir(metaDocument, MetaDocumentDto.class);

			List<ContingutDto> docsDtos = new ArrayList<>();
			if (CollectionUtils.isNotEmpty(documents)) {
				for (DocumentEntity document : documents) {
					docsDtos.add(toContingutDto(document, params));
				}
			}

			mapPerTipusDocument.put(metaDocumentDto, docsDtos);
		}

		dto.setMapPerTipusDocument(mapPerTipusDocument);
		logMsg("ambMapPerTipusDocument end (" + expedient.getId() + "):  " + (System.currentTimeMillis() - t1) + " ms");
	}

	private void setMapPerEstat(ExpedientDto dto, ExpedientEntity expedient, ToContingutParams params) {
		logMsg("ambMapPerEstat start (" + expedient.getId() + ")");
		long t1 = System.currentTimeMillis();

		Map<ExpedientEstatDto, List<ContingutDto>> mapPerEstat = new LinkedHashMap<>();
		List<ExpedientEstatEntity> expedientEstats = expedientEstatRepository.findByMetaExpedientOrderByOrdreAsc(expedient.getMetaExpedient());

		for (ExpedientEstatEntity expedientEstat : expedientEstats) {
			List<DocumentEntity> documents = documentRepository.findByExpedientAndExpedientEstatAdditionalAndEsborrat(expedient, expedientEstat, NO_ESBORRAT);
			ExpedientEstatDto expedientEstatDto = conversioTipusHelper.convertir(expedientEstat, ExpedientEstatDto.class);

			List<ContingutDto> docsDtos = new ArrayList<>();
			if (CollectionUtils.isNotEmpty(documents)) {
				for (DocumentEntity document : documents) {
					docsDtos.add(toContingutDto(document, params));
				}
			}

			mapPerEstat.put(expedientEstatDto, docsDtos);
		}

		List<DocumentEntity> documents = documentRepository.findByExpedientAndExpedientEstatAdditionalIsNullAndEsborrat(expedient, NO_ESBORRAT);

		List<ContingutDto> docsDtos = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(documents)) {
			for (DocumentEntity document : documents) {
				docsDtos.add(toContingutDto(document, params));
			}

			mapPerEstat.put(new ExpedientEstatDto("Sense estat", 0L), docsDtos);
		}

		dto.setMapPerEstat(mapPerEstat);

		logMsg("ambMapPerEstat end (" + expedient.getId() + "):  " + (System.currentTimeMillis() - t1) + " ms");
	}

	// DOCUMENT
	// //////////////////////////////////////////////////////////////////////////////////////////

	private DocumentDto createDocumentDto(DocumentEntity document, ToContingutParams params) {
		logMsg("toDocumentDto start (" + document.getId() + ", level=" + params.getLevel() + ") ");
		long t1 = System.currentTimeMillis();

		DocumentDto dto = new DocumentDto();
		setDocumentBasicProperties(dto, document);
		setDocumentFitxerProperties(dto, document, params);

		logMsg("toDocumentDto 1/3 time (" + document.getId() + "):  " + (System.currentTimeMillis() - t1) + " ms");
		long t2 = System.currentTimeMillis();

		setNtiProperties(dto, document, params);
		setEnviamentProperties(dto, document);

		if (document.getAnnexos() != null && !document.getAnnexos().isEmpty()) {
			setAnnexProperties(dto, document, document.getAnnexos().get(0));
		}

		dto.setMetaNode(conversioTipusHelper.convertir(document.getMetaNode(), MetaDocumentDto.class));

		logMsg("toDocumentDto 2/3 time (" + document.getId() + "):  " + (System.currentTimeMillis() - t2) + " ms");
		long t3 = System.currentTimeMillis();

		setValidationProperties(dto, document);
		setArxiuProperties(dto, document);

		logMsg("toDocumentDto 3/3 time (" + document.getId() + "):  " + (System.currentTimeMillis() - t3) + " ms");
		logMsg("toDocumentDto end (" + document.getId() + "):  " + (System.currentTimeMillis() - t1) + " ms");

		return dto;
	}

	private void setDocumentBasicProperties(DocumentDto dto, DocumentEntity document) {
		dto.setDocumentTipus(document.getDocumentTipus());
		dto.setDescripcio(document.getDescripcio());
		dto.setEstat(document.getEstat());
		dto.setUbicacio(document.getUbicacio());
		dto.setData(document.getData());
		if(document.getMetaDocument()!=null && 
			document.getMetaDocument().getMultiplicitat()!=null &&				
			(document.getMetaDocument().getMultiplicitat().equals(MultiplicitatEnumDto.M_1) || 
			 document.getMetaDocument().getMultiplicitat().equals(MultiplicitatEnumDto.M_1_N))) {
				//Si la obligatorietat es M_1 o M1_N
				dto.setObligatori(true);
		}
	}

	private void setDocumentFitxerProperties(DocumentDto dto, DocumentEntity document, ToContingutParams params) {
		if (document.getFitxerNom() != null) {
			dto.setFitxerNom(document.getFitxerNom());
			dto.setFitxerNomEnviamentPortafirmes(pluginHelper.conversioConvertirPdfArxiuNom(document.getFitxerNom()));
		}
		dto.setFitxerContentType(document.getFitxerContentType());
		dto.setFitxerTamany(document.getFitxerTamany());
		dto.setDataCaptura(document.getDataCaptura());
		dto.setVersioDarrera(document.getVersioDarrera());
		dto.setVersioCount(document.getVersioCount());
		dto.setGesDocOriginalId(document.getGesDocOriginalId());
		
		if (params.isAmbVersions() && 
			pluginHelper.arxiuSuportaVersionsDocuments() && 
			document.getEsborrat() == NO_ESBORRAT && 
			Utils.hasValue(document.getArxiuUuid())) {
				setVersions(dto, document);
		}

		dto.setCustodiaId(document.getCustodiaId());
		dto.setCustodiaData(document.getCustodiaData());
	}

	private void setVersions(DocumentDto dto, DocumentEntity document) {
		List<ContingutArxiu> arxiuVersions = pluginHelper.arxiuDocumentObtenirVersions(document);
		if (arxiuVersions != null) {
			List<DocumentVersioDto> versions = new ArrayList<>();
			for (ContingutArxiu arxiuVersio : arxiuVersions) {
				DocumentVersioDto versio = new DocumentVersioDto();
				versio.setArxiuUuid(arxiuVersio.getIdentificador());
				versio.setId(arxiuVersio.getVersio());
				versions.add(versio);
			}
			dto.setVersions(versions);
		}
	}

	private void setNtiProperties(DocumentDto dto, DocumentEntity document, ToContingutParams params) {
		dto.setNtiVersion(document.getNtiVersion());
		dto.setNtiIdentificador(document.getNtiIdentificador());
		dto.setNtiOrgano(document.getNtiOrgano());
		dto.setNtiOrganoDescripcio(document.getNtiOrgano());
		dto.setNtiOrigen(document.getNtiOrigen());
		dto.setNtiEstadoElaboracion(document.getNtiEstadoElaboracion());
		dto.setNtiTipoDocumental(document.getNtiTipoDocumental());
		if (document.getNtiTipoDocumental() != null) {
			setNtiTipoDocumentalNom(dto, document);
		}
		dto.setNtiIdDocumentoOrigen(document.getNtiIdDocumentoOrigen());
		dto.setNtiTipoFirma(document.getNtiTipoFirma());
		dto.setNtiCsv(document.getNtiCsv());
		dto.setNtiCsvRegulacion(document.getNtiCsvRegulacion());
	}

	private void setNtiTipoDocumentalNom(DocumentDto dto, DocumentEntity document) {
		TipusDocumentalEntity tipusDocumental = tipusDocumentalRepository.findByCodiAndEntitat(document.getNtiTipoDocumental(), document.getEntitat());
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

	private void setEnviamentProperties(DocumentDto dto, DocumentEntity document) {
		dto.setAmbNotificacions(documentNotificacioRepository.countByDocument(document) > 0);

		DocumentNotificacioEstatEnumDto estatDarreraNotificacio = documentNotificacioRepository.findLastEstatNotificacioByDocument(document);
		dto.setEstatDarreraNotificacio(estatDarreraNotificacio != null ? estatDarreraNotificacio.name() : "");

		Boolean isErrorLastNotificacio = documentNotificacioRepository.findErrorLastNotificacioByDocument(document);
		dto.setErrorDarreraNotificacio(isErrorLastNotificacio != null ? isErrorLastNotificacio : false);

		Boolean isErrorLastEnviament = documentPortafirmesRepository.findErrorLastEnviamentPortafirmesByDocument(document);
		dto.setErrorEnviamentPortafirmes(isErrorLastEnviament != null ? isErrorLastEnviament : false);

		dto.setGesDocFirmatId(document.getGesDocFirmatId());
		dto.setGesDocAdjuntId(document.getGesDocAdjuntId());
		dto.setGesDocAdjuntFirmaId(document.getGesDocAdjuntFirmaId());

		dto.setDocFromAnnex(document.isDocFromAnnex());
	}

	private void setAnnexProperties(DocumentDto dto, DocumentEntity document, RegistreAnnexEntity annex) {
		dto.setPendentMoverArxiu(document.isPendentMoverArxiu());
		dto.setAnnexId(annex.getId());
		dto.setDocumentDeAnotacio(true);
	}

	private void setValidationProperties(DocumentDto dto, DocumentEntity document) {
		dto.setValid(cacheHelper.findErrorsValidacioPerNode(document).isEmpty());
		dto.setValidacioFirmaCorrecte(document.isValidacioFirmaCorrecte());
		dto.setValidacioFirmaErrorMsg(document.getValidacioFirmaErrorMsg());
	}

	private void setArxiuProperties(DocumentDto dto, DocumentEntity document) {
		dto.setEstat(document.getEstat());
		dto.setArxiuEstat(document.getArxiuEstat());
		dto.setArxiuEstatDefinitiu(document.isArxiuEstatDefinitiu());
		dto.setDocumentFirmaTipus(document.getDocumentFirmaTipus());
	}

	// CARPETA
	// //////////////////////////////////////////////////////////////////////////////////////////
	private CarpetaDto createCarpetaDto(CarpetaEntity carpeta, ToContingutParams params) {
		logMsg("toCarpetaDto start (" + carpeta.getId() + ", level=" + params.getLevel() + ") ");
		long t1 = System.currentTimeMillis();

		CarpetaDto dto = new CarpetaDto();
		if (carpeta.getExpedientRelacionat() != null)
			dto.setExpedientRelacionat(
					(ExpedientDto)toContingutDto(
							carpeta.getExpedientRelacionat(),
							ToContingutParams.builder()
									.onlyFirstDescendant(params.isOnlyFirstDescendant())
									.level(params.getLevel())
									.ambExpedientPare(params.isAmbExpedientPare())
									.ambEntitat(params.isAmbEntitat())
									.ambMapPerTipusDocument(params.isAmbMapPerTipusDocument())
									.ambMapPerEstat(params.isAmbMapPerEstat())
									.build()));

		dto.setConteDocumentsDefinitius(documentRepository.carpetaHasDocumentsDefinitius(carpeta));

		logMsg("toCarpetaDto end (" + carpeta.getId() + "):  " + (System.currentTimeMillis() - t1) + " ms");
		return dto;
	}

	private void logMsg(String message) {
		if (cacheHelper.mostrarLogsRendiment()) {
			logger.info(message);
		}
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

		ContingutEntity contingut = contingutRepository.getOne(contingutId);
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
			dto.setFitxerTamany(Long.valueOf(resposta.getCertificacioContingut().length));
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
		
		ContingutEntity contingut = entityComprovarHelper.comprovarContingut(contingutId);
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
		DocumentEntity document = documentRepository.getOne(
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
		
		ExpedientTascaEntity expedientTascaEntity = expedientTascaRepository.getOne(tascaId);
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		if (!expedientTascaEntity.getExpedient().getId().equals(contingut.getExpedientPare().getId())) {
			throw new SecurityException("La tasca no pertany al contingut especificat ("
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
			UsuariEntity delegat = expedientTascaEntity.getDelegat();
			if (delegat != null && delegat.getCodi().equals(auth.getName())) {
				pemitted = true;
			}
			if (!pemitted) {
				throw new SecurityException("La tasca a la qual intenta accedir no està assignada al seu usuari ("
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
			if (expedientPare != null) {
				cacheHelper.evictErrorsValidacioPerNode(expedientPare);
			}
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
			Long grupId,
			PrioritatEnumDto prioritat,
			String prioritatMotiu) {
		UsuariEntity agafatPer = null;
		if (agafar) {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			agafatPer = usuariRepository.getOne(auth.getName());
		}
		GrupEntity grupEntity = null;
		if (grupId != null) {
			grupEntity = grupRepository.getOne(grupId);
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
				organGestor,
				prioritat,
				prioritatMotiu).
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
		return contingutRepository.getOne(contingutActual.getId());
	}

	public void findDescendants(
			ContingutEntity contingut,
			List<ContingutEntity> descendants,
			boolean onlyDocuments,
			boolean includeEsborrats) {

		//Serà un document o bé una carpeta buida
		if (contingut.getFills() == null || contingut.getFills().isEmpty()) {
			if (!onlyDocuments || (contingut instanceof DocumentEntity)) {
				if (includeEsborrats || contingut.getEsborrat()==0) {
					descendants.add(contingut);
				}
			}
		} else {
			for (ContingutEntity contingutEntity : contingut.getFills()) {
				findDescendants(contingutEntity, descendants, onlyDocuments, includeEsborrats);
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
			CarpetaEntity carpeta) {
		boolean utilitzarCarpetesEnArxiu = !isCarpetaLogica();
		if (utilitzarCarpetesEnArxiu) {
				pluginHelper.arxiuCarpetaActualitzar( 
						carpeta,
						carpeta.getPare());
		}
	}

	@Transactional(propagation=Propagation.REQUIRES_NEW)
	public Exception guardarCarpetaArxiu(Long carpetaId) {

		Exception exception = null;
		CarpetaEntity carpeta = carpetaRepository.getOne(carpetaId);

		if (carpeta.getExpedient().getArxiuUuid() != null) {
			if (carpeta.getArxiuUuid() != null) {
				exception = new ArxiuJaGuardatException("La carpeta ja s'ha guardat en arxiu per otra persona o el process en segon pla");
			} else {

				try {
					expedientHelper.concurrencyCheckExpedientJaTancat(carpeta.getExpedient());
					pluginHelper.arxiuCarpetaActualitzar(
							carpeta,
							carpeta.getPare());
				} catch (Exception ex) {
					logger.error("Error al guardar carpeta en arxiu (" + carpetaId + ")", ex);
					exception = ExceptionHelper.getRootCauseException(ex);
				}
			}
		} else {
			exception = new RuntimeException("Expedient de aquest document no es guardat en arxiu");
		}
		carpeta.updateArxiuIntent(true);
		return exception;
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

			// Si està activada la carpeta lògica moure sempre a l'expedient
			if (desti instanceof CarpetaEntity && isCarpetaLogica()) {
				identificador = pluginHelper.arxiuDocumentMoure(
						uuid,
						null,
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
			ContingutEntity c = contingutRepository.getOne(contingutActual.getPare().getId());
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
	
	public FitxerDto generarIndexPdf(
			EntitatEntity entitatActual,
			List<CarpetaEntity> carpetes) throws IOException {

		byte[] indexGenerated = indexHelper.generarIndexPdfPerCarpetes(
				carpetes,
				entitatActual);

		FitxerDto fitxer = new FitxerDto();
		if (carpetes.size() > 1) {
			fitxer.setNom(messageHelper.getMessage("expedient.service.exportacio.index") + ".pdf");
		} else {
			String carpetaNom = carpetes.get(0).getNom();
			if (carpetaNom.contains("\"")) {
				carpetaNom = carpetaNom.replace("\"", "\\\"");
			}
			fitxer.setNom(messageHelper.getMessage("expedient.service.exportacio.index") + " " + carpetaNom + ".pdf");
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

	public FitxerDto generarIndexXlsx(
			EntitatEntity entitatActual,
			List<CarpetaEntity> carpetes) throws IOException {

		byte[] indexGenerated = indexHelper.generarIndexXlsxPerCarpetes(
				carpetes,
				entitatActual);

		FitxerDto fitxer = new FitxerDto();
		if (carpetes.size() > 1) {
			fitxer.setNom(messageHelper.getMessage("expedient.service.exportacio.index") + ".xlsx");
		} else {
			String carpetaNom = carpetes.get(0).getNom();
			if (carpetaNom.contains("\"")) {
				carpetaNom = "\"" + carpetaNom.replace("\"", "\\\"") + "\"";
			}
			fitxer.setNom(messageHelper.getMessage("expedient.service.exportacio.index") + " " + carpetaNom + ".xlsx");
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
		
		ContingutEntity contingut = contingutRepository.getOne(contingutId);
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
			RegistreAnnexEntity registreAnnex = registreAnnexRepository.getOne(annexId);
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

			CarpetaEntity carpetaEntity = carpetaRepository.getOne(document.getPareId());
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
	private boolean getPropertyGuardarCertificacioExpedient() {
		return configHelper.getAsBoolean("es.caib.ripea.notificacio.guardar.certificacio.expedient");
	}
	private static final Logger logger = LoggerFactory.getLogger(ContingutHelper.class);

}
