/**
 *
 */
package es.caib.ripea.core.service;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.caib.plugins.arxiu.api.Carpeta;
import es.caib.plugins.arxiu.api.ContingutArxiu;
import es.caib.plugins.arxiu.api.Document;
import es.caib.plugins.arxiu.api.DocumentEstat;
import es.caib.plugins.arxiu.api.DocumentMetadades;
import es.caib.plugins.arxiu.api.ExpedientMetadades;
import es.caib.plugins.arxiu.api.Firma;
import es.caib.ripea.core.api.dto.AlertaDto;
import es.caib.ripea.core.api.dto.ArxiuContingutDto;
import es.caib.ripea.core.api.dto.ArxiuContingutTipusEnumDto;
import es.caib.ripea.core.api.dto.ArxiuDetallDto;
import es.caib.ripea.core.api.dto.ArxiuEstatEnumDto;
import es.caib.ripea.core.api.dto.ArxiuFirmaDto;
import es.caib.ripea.core.api.dto.ArxiuFirmaPerfilEnumDto;
import es.caib.ripea.core.api.dto.ArxiuFirmaTipusEnumDto;
import es.caib.ripea.core.api.dto.ContingutDto;
import es.caib.ripea.core.api.dto.ContingutFiltreDto;
import es.caib.ripea.core.api.dto.ContingutLogDetallsDto;
import es.caib.ripea.core.api.dto.ContingutLogDto;
import es.caib.ripea.core.api.dto.ContingutMassiuFiltreDto;
import es.caib.ripea.core.api.dto.ContingutMovimentDto;
import es.caib.ripea.core.api.dto.DocumentDto;
import es.caib.ripea.core.api.dto.DocumentFirmaTipusEnumDto;
import es.caib.ripea.core.api.dto.DocumentTipusEnumDto;
import es.caib.ripea.core.api.dto.DominiDto;
import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.ExpedientEstatEnumDto;
import es.caib.ripea.core.api.dto.FitxerDto;
import es.caib.ripea.core.api.dto.LogObjecteTipusEnumDto;
import es.caib.ripea.core.api.dto.LogTipusEnumDto;
import es.caib.ripea.core.api.dto.MetaDadaTipusEnumDto;
import es.caib.ripea.core.api.dto.PaginaDto;
import es.caib.ripea.core.api.dto.PaginacioParamsDto;
import es.caib.ripea.core.api.dto.PermissionEnumDto;
import es.caib.ripea.core.api.dto.ResultDocumentsSenseContingut;
import es.caib.ripea.core.api.dto.ResultDocumentsSenseContingut.ResultDocumentSenseContingut;
import es.caib.ripea.core.api.dto.ResultatConsultaDto;
import es.caib.ripea.core.api.dto.TipusDocumentalDto;
import es.caib.ripea.core.api.dto.ValidacioErrorDto;
import es.caib.ripea.core.api.exception.NotFoundException;
import es.caib.ripea.core.api.exception.ValidationException;
import es.caib.ripea.core.api.service.ContingutService;
import es.caib.ripea.core.api.service.DominiService;
import es.caib.ripea.core.api.utils.Utils;
import es.caib.ripea.core.entity.AlertaEntity;
import es.caib.ripea.core.entity.CarpetaEntity;
import es.caib.ripea.core.entity.ContingutEntity;
import es.caib.ripea.core.entity.ContingutMovimentEntity;
import es.caib.ripea.core.entity.DadaEntity;
import es.caib.ripea.core.entity.DocumentEntity;
import es.caib.ripea.core.entity.EntitatEntity;
import es.caib.ripea.core.entity.ExpedientEntity;
import es.caib.ripea.core.entity.MetaDadaEntity;
import es.caib.ripea.core.entity.MetaDocumentEntity;
import es.caib.ripea.core.entity.MetaExpedientEntity;
import es.caib.ripea.core.entity.MetaNodeEntity;
import es.caib.ripea.core.entity.NodeEntity;
import es.caib.ripea.core.entity.OrganGestorEntity;
import es.caib.ripea.core.entity.TipusDocumentalEntity;
import es.caib.ripea.core.helper.ArxiuConversions;
import es.caib.ripea.core.helper.CacheHelper;
import es.caib.ripea.core.helper.ConfigHelper;
import es.caib.ripea.core.helper.ContingutHelper;
import es.caib.ripea.core.helper.ContingutLogHelper;
import es.caib.ripea.core.helper.ContingutsOrfesHelper;
import es.caib.ripea.core.helper.ConversioTipusHelper;
import es.caib.ripea.core.helper.DateHelper;
import es.caib.ripea.core.helper.DocumentHelper;
import es.caib.ripea.core.helper.EntityComprovarHelper;
import es.caib.ripea.core.helper.MetaExpedientHelper;
import es.caib.ripea.core.helper.OrganGestorHelper;
import es.caib.ripea.core.helper.PaginacioHelper;
import es.caib.ripea.core.helper.PaginacioHelper.Converter;
import es.caib.ripea.core.helper.PluginHelper;
import es.caib.ripea.core.repository.AlertaRepository;
import es.caib.ripea.core.repository.ContingutRepository;
import es.caib.ripea.core.repository.DadaRepository;
import es.caib.ripea.core.repository.DocumentRepository;
import es.caib.ripea.core.repository.ExpedientRepository;
import es.caib.ripea.core.repository.MetaDadaRepository;
import es.caib.ripea.core.repository.MetaNodeRepository;
import es.caib.ripea.core.repository.OrganGestorRepository;
import es.caib.ripea.core.repository.RegistreAnnexRepository;
import es.caib.ripea.core.repository.TipusDocumentalRepository;
import es.caib.ripea.core.repository.URLInstruccioRepository;
import es.caib.ripea.core.repository.UsuariRepository;

/**
 * Implementació dels mètodes per a gestionar continguts.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class ContingutServiceImpl implements ContingutService {

	@Autowired
	private UsuariRepository usuariRepository;
	@Autowired
	private ContingutRepository contingutRepository;
	@Autowired
	private MetaDadaRepository metaDadaRepository;
	@Autowired
	private DadaRepository dadaRepository;
	@Autowired
	private MetaNodeRepository metaNodeRepository;
	@Autowired
	private DocumentRepository documentRepository;
	@Autowired
	private AlertaRepository alertaRepository;
	@Autowired
	private RegistreAnnexRepository registreAnnexRepository;
	@Autowired
	private PaginacioHelper paginacioHelper;
	@Autowired
	private CacheHelper cacheHelper;
	@Autowired
	private ContingutHelper contingutHelper;
	@Autowired
	private DocumentHelper documentHelper;
	@Autowired
	private ContingutLogHelper contingutLogHelper;
	@Autowired
	private PluginHelper pluginHelper;
	@Autowired
	private EntityComprovarHelper entityComprovarHelper;
	@Autowired
	private ConversioTipusHelper conversioTipusHelper;
	@Autowired
	private TipusDocumentalRepository tipusDocumentalRepository;
	@Autowired
	private MetaExpedientHelper metaExpedientHelper;
	@Autowired
	private ExpedientRepository expedientRepository;
	@Autowired
	private ContingutsOrfesHelper contingutRepositoryHelper;
	@Autowired
	private OrganGestorHelper organGestorHelper;
	@Resource
	private OrganGestorRepository organGestorRepository;
	@Autowired
	private DominiService dominiService;
	@Autowired
	private ConfigHelper configHelper;
	@Autowired
	private URLInstruccioRepository urlInstruccionRepository;
	
	@Transactional
	@Override
	public void dadaSave(
			Long entitatId,
			Long contingutId,
			Map<String, Object> valors) throws NotFoundException {
		logger.debug("Guardant dades del node (" +
				"entitatId=" + entitatId + ", " +
				"contingutId=" + contingutId + ", " +
				"valors=" + valors + ")");
		NodeEntity node = contingutHelper.comprovarNodeDinsExpedientModificable(
				entitatId,
				contingutId,
				false,
				true,
				false,
				false, true, null);
		// Esborra les dades no especificades
		for (DadaEntity dada: dadaRepository.findByNode(node)) {
			if (!valors.keySet().contains(dada.getMetaDada().getCodi())) {
				dadaRepository.delete(dada);

				MetaDadaEntity metaDada = dada.getMetaDada();
				if (node instanceof ExpedientEntity && isPropagarMetadadesActiu() && metaDada.isEnviable()) {
					// Obtenir nom domini per guardar a l'arxiu per metadades 'enviables'
					if (metaDada.getTipus().equals(MetaDadaTipusEnumDto.DOMINI)) {
						pluginHelper.arxiuExpedientMetadadesActualitzar((ExpedientEntity)node, metaDada, "");
					}
				}
			}
		}
		// Modifica les dades existents
		for (String dadaCodi: valors.keySet()) {
			nodeDadaGuardar(
					node,
					dadaCodi,
					valors.get(dadaCodi),
					entitatId);
		}
		cacheHelper.evictErrorsValidacioPerNode(node);
	}

	

	
	
	@Transactional
	@Override
	@CacheEvict(value = "errorsValidacioNode", key = "#contingutId")
	public ContingutDto deleteReversible(
			Long entitatId,
			Long contingutId, 
			String rolActual, 
			Long tascaId) throws IOException {
		logger.debug("Esborrant el contingut ("
				+ "entitatId=" + entitatId + ", "
				+ "contingutId=" + contingutId + ")");
		
		ContingutEntity contingut = null;
		if (tascaId == null) {
			contingut = contingutHelper.comprovarContingutDinsExpedientModificable(
				entitatId,
				contingutId,
				false,
				false,
				false,
				true, 
				false, 
					true, 
					rolActual);
		} else {
		  contingut = contingutHelper.comprovarContingutPertanyTascaAccesible(
					entitatId,
					tascaId,
					contingutId);
		}

		
		if (contingut instanceof ExpedientEntity) {
			entityComprovarHelper.comprovarEstatExpedient(entitatId, contingutId, ExpedientEstatEnumDto.OBERT);
		}

		return contingutHelper.deleteReversible(
				entitatId,
				contingut, 
				rolActual);
	}

	@Transactional
	@Override
	@CacheEvict(value = "errorsValidacioNode", key = "#contingutId")
	public ContingutDto deleteDefinitiu(
			Long entitatId,
			Long contingutId) {
		logger.debug("Esborrant el contingut ("
				+ "entitatId=" + entitatId + ", "
				+ "contingutId=" + contingutId + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				true,
				false,
				false, false, false);
		ContingutEntity contingut = entityComprovarHelper.comprovarContingut(
				entitat,
				contingutId);
		// No es comproven permisos perquè això només ho pot fer l'administrador
		ContingutDto dto = contingutHelper.toContingutDto(
				contingut, false, false);
		if (contingut.getPare() != null) {
			contingut.getPare().getFills().remove(contingut);
		}
		
		if (contingut instanceof ExpedientEntity && contingut.getFills() != null && !contingut.getFills().isEmpty()) {
			List<ContingutEntity> descendants = new ArrayList<>();
			contingutHelper.findDescendants(contingut, descendants);
			
			Iterator<ContingutEntity> itr = descendants.iterator();
			while (itr.hasNext()) {
				ContingutEntity cont = itr.next();
				if (cont.getPare() != null) {
					cont.getPare().getFills().remove(cont);
				}
				if (cont instanceof DocumentEntity) {
					documentHelper.deleteDefinitiu((DocumentEntity) cont);
				} else {
					contingutRepository.delete(cont);
				}
				
			}
		} else if (contingut instanceof DocumentEntity) {
			documentHelper.deleteDefinitiu((DocumentEntity) contingut);
		} else {
			contingutRepository.delete(contingut);
		}


		return dto;
	}
	
	@Transactional
	@Override
	public ContingutDto undelete(
			Long entitatId,
			Long contingutId) throws IOException {
		logger.debug("Recuperant el contingut ("
				+ "entitatId=" + entitatId + ", "
				+ "contingutId=" + contingutId + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				true,
				false,
				false, false, false);
		ContingutEntity contingut = entityComprovarHelper.comprovarContingut(
				entitat,
				contingutId);
		// No es comproven permisos perquè això només ho pot fer l'administrador
		if (contingut.getEsborrat() == 0) {
			logger.error("Aquest contingut no està esborrat (contingutId=" + contingutId + ")");
			throw new ValidationException(
					contingutId,
					ContingutEntity.class,
					"Aquest contingut no està esborrat");
		}
		
		if (contingut instanceof DocumentEntity) {
			String uniqueNameInPare = contingutHelper.getUniqueNameInPare(contingut.getNom(), contingut.getPare().getId());
			contingut.updateNom(uniqueNameInPare);
		} else {
			boolean nomDuplicat = contingutRepository.findByPareAndNomAndEsborrat(
					contingut.getPare(),
					contingut.getNom(),
					0) != null;
			if (nomDuplicat) {
				throw new ValidationException(
						contingutId,
						ContingutEntity.class,
						"Ja existeix un altre contingut amb el mateix nom dins el mateix pare");
			}
		}

		// Recupera el contingut esborrat
		contingut.updateEsborrat(0);
		ContingutDto dto = contingutHelper.toContingutDto(
				contingut, false, false);
		// Registra al log la recuperació del contingut
		contingutLogHelper.log(
				contingut,
				LogTipusEnumDto.RECUPERACIO,
				null,
				null,
				true,
				true);

		if (!contingutHelper.conteDocumentsDefinitius(contingut) && !(contingut instanceof DocumentEntity && ((DocumentEntity) contingut).getGesDocAdjuntId() != null)) {

			// Propaga l'acció a l'arxiu
			FitxerDto fitxer = null;

			if (contingut instanceof ExpedientEntity) {
				contingutHelper.arxiuPropagarModificacio((ExpedientEntity) contingut);
			} else if (contingut instanceof DocumentEntity) {
				
				DocumentEntity document = (DocumentEntity)contingut;
				if (DocumentTipusEnumDto.DIGITAL.equals(document.getDocumentTipus())) {

					DocumentFirmaTipusEnumDto documentFirmaTipus = document.getDocumentFirmaTipus();
					List<ArxiuFirmaDto> firmes = null;
					
					if (documentFirmaTipus == DocumentFirmaTipusEnumDto.SENSE_FIRMA) {
						fitxer = contingutHelper.fitxerDocumentEsborratLlegir(document);
					} else if (documentFirmaTipus == DocumentFirmaTipusEnumDto.FIRMA_ADJUNTA) {
						
						fitxer = contingutHelper.fitxerDocumentEsborratLlegir(document);
						firmes = documentHelper.validaFirmaDocument(
								document, 
								fitxer,
								null, 
								false, 
								true);
						
					} else if (documentFirmaTipus == DocumentFirmaTipusEnumDto.FIRMA_SEPARADA) {
						
						fitxer = contingutHelper.fitxerDocumentEsborratLlegir(document);
						byte[] firmaContingut = contingutHelper.firmaSeparadaEsborratLlegir(document);
						firmes = documentHelper.validaFirmaDocument(
								document, 
								fitxer,
								firmaContingut, 
								false, 
								true);
						
					} 
					
					
					ArxiuEstatEnumDto arxiuEstat = documentHelper.getArxiuEstat(documentFirmaTipus, null);
					
					if (arxiuEstat == ArxiuEstatEnumDto.ESBORRANY && documentFirmaTipus == DocumentFirmaTipusEnumDto.FIRMA_SEPARADA) {
						pluginHelper.arxiuPropagarFirmaSeparada(
								document,
								firmes.get(0).getFitxer());
					}
					contingutHelper.arxiuPropagarModificacio(
							document,
							fitxer,
							arxiuEstat == ArxiuEstatEnumDto.ESBORRANY ? DocumentFirmaTipusEnumDto.SENSE_FIRMA : documentFirmaTipus,
							firmes,
							arxiuEstat);
					
				}
				

			} else if (contingut instanceof CarpetaEntity) {
				contingutHelper.arxiuPropagarModificacio(
						(CarpetaEntity) contingut,
						false);
			}

			if (fitxer != null) {
				contingutHelper.fitxerDocumentEsborratEsborrar((DocumentEntity)contingut);
			}
		}

		return dto;
	}

	@Transactional
	@Override
	public ContingutDto move(
			Long entitatId,
			Long contingutOrigenId,
			Long contingutDestiId, 
			String rolActual) {
		
		organGestorHelper.actualitzarOrganCodi(organGestorHelper.getOrganCodiFromContingutId(contingutOrigenId));
		logger.debug("Movent el contingut ("
				+ "entitatId=" + entitatId + ", "
				+ "contingutOrigenId=" + contingutOrigenId + ", "
				+ "contingutDestiId=" + contingutDestiId + ")");
		ContingutEntity contingutOrigen = contingutHelper.comprovarContingutDinsExpedientModificable(
				entitatId,
				contingutOrigenId,
				true,
				false,
				false,
				true, 
				false, 
				true, rolActual);
		ContingutEntity contingutDesti = contingutHelper.comprovarContingutDinsExpedientModificable(
				entitatId,
				contingutDestiId,
				false,
				false,
				true,
				false, 
				false, 
				true, rolActual);
		// Comprova el tipus del contingut que es vol moure
		if (contingutOrigen instanceof CarpetaEntity && !contingutHelper.isCarpetaLogica()) {
			throw new ValidationException(
					contingutOrigenId,
					contingutOrigen.getClass(),
					"Només es poden moure documents");
		}
		// No es poden moure documents firmats
		if (contingutOrigen instanceof DocumentEntity) {
			DocumentEntity documentOrigen = (DocumentEntity)contingutOrigen;
			if (documentOrigen.isFirmat() && !contingutHelper.isCarpetaLogica()) {
				throw new ValidationException(
						contingutOrigenId,
						contingutOrigen.getClass(),
						"No es poden moure documents firmats");
			}
		}
		// Es comprova que el procediment orígen i destí son el mateix
		ExpedientEntity expedientOrigen = contingutHelper.getExpedientSuperior(
				contingutOrigen,
				true,
				false,
				false, false, null);
		ExpedientEntity expedientDesti = contingutHelper.getExpedientSuperior(
				contingutDesti,
				true,
				false,
				false, false, null);
		if (!expedientOrigen.getMetaExpedient().equals(expedientDesti.getMetaExpedient())) {
			throw new ValidationException(
					contingutOrigenId,
					contingutOrigen.getClass(),
					"Només es pot moure contingut entre dos expedients del mateix tipus");
		}
		// Comprova que el nom no sigui duplicat
		boolean nomDuplicat = contingutRepository.findByPareAndNomAndEsborrat(
				contingutDesti,
				contingutOrigen.getNom(),
				0) != null;
		if (nomDuplicat) {
			throw new ValidationException(
					contingutOrigenId,
					ContingutEntity.class,
					"Ja existeix un altre contingut amb el mateix nom dins el contingut destí ("
							+ "contingutDestiId=" + contingutDestiId + ")");
		}
		// Realitza el moviment del contingut
		ContingutMovimentEntity contingutMoviment = contingutHelper.ferIEnregistrarMoviment(
				contingutOrigen,
				contingutDesti,
				null);
		contingutLogHelper.log(
				contingutOrigen,
				LogTipusEnumDto.MOVIMENT,
				contingutMoviment,
				true,
				true);
		ContingutDto dto = contingutHelper.toContingutDto(
				contingutOrigen, false, false);
		
		
		if (contingutOrigen instanceof DocumentEntity){
			contingutHelper.arxiuDocumentPropagarMoviment(
					contingutOrigen.getArxiuUuid(),
					contingutDesti,
					expedientDesti.getArxiuUuid());
		} else if (contingutOrigen instanceof CarpetaEntity && !contingutHelper.isCarpetaLogica()) {
			pluginHelper.arxiuCarpetaMoure(
					(CarpetaEntity)contingutOrigen,
					contingutDesti.getArxiuUuid());
		}
		

		return dto;
	}

	@Transactional
	@Override
	public ContingutDto copy(
			Long entitatId,
			Long contingutOrigenId,
			Long contingutDestiId,
			boolean recursiu) {
		logger.debug("Copiant el contingut ("
				+ "entitatId=" + entitatId + ", "
				+ "contingutOrigenId=" + contingutOrigenId + ", "
				+ "contingutDestiId=" + contingutDestiId + ", "
				+ "recursiu=" + recursiu + ")");
		ContingutEntity contingutOrigen = contingutHelper.comprovarContingutDinsExpedientModificable(
				entitatId,
				contingutOrigenId,
				true,
				false,
				false,
				false, 
				false, true, null);
		ContingutEntity contingutDesti = contingutHelper.comprovarContingutDinsExpedientModificable(
				entitatId,
				contingutDestiId,
				false,
				false,
				true,
				false, 
				false, true, null);
		// Comprova el tipus del contingut que es vol moure
		if (!(contingutOrigen instanceof DocumentEntity)) {
			throw new ValidationException(
					contingutOrigenId,
					contingutOrigen.getClass(),
					"Només es poden copiar documents");
		}
		// TODO Mirar què passa amb els documents firmats
		if (contingutOrigen instanceof DocumentEntity) {
			DocumentEntity documentOrigen = (DocumentEntity)contingutOrigen;
			if (documentOrigen.isFirmat()) {
				throw new ValidationException(
						contingutOrigenId,
						contingutOrigen.getClass(),
						"No es poden copiar documents firmats");
			}
		}
		// Es comprova que el procediment orígen i destí son el mateix
		ExpedientEntity expedientOrigen = contingutHelper.getExpedientSuperior(
				contingutOrigen,
				true,
				false,
				false, false, null);
		ExpedientEntity expedientDesti = contingutHelper.getExpedientSuperior(
				contingutDesti,
				true,
				false,
				false, false, null);
		if (!expedientOrigen.getMetaExpedient().equals(expedientDesti.getMetaExpedient())) {
			throw new ValidationException(
					contingutOrigenId,
					contingutOrigen.getClass(),
					"Només es pot moure contingut entre dos expedients del mateix tipus");
		}
		// Comprova que el nom no sigui duplicat
		boolean nomDuplicat = contingutRepository.findByPareAndNomAndEsborrat(
				contingutDesti,
				contingutOrigen.getNom(),
				0) != null;
		if (nomDuplicat) {
			throw new ValidationException(
					contingutOrigenId,
					ContingutEntity.class,
					"Ja existeix un altre contingut amb el mateix nom dins el contingut destí ("
							+ "contingutDestiId=" + contingutDestiId + ")");
		}
		// Realitza la còpia del contingut
		ContingutEntity contingutCopia = copiarContingut(
				contingutOrigen.getEntitat(),
				contingutOrigen,
				contingutDesti,
				recursiu);
		contingutLogHelper.log(
				contingutCopia,
				LogTipusEnumDto.COPIA,
				null,
				null,
				true,
				true);
		ContingutDto dto = contingutHelper.toContingutDto(
				contingutOrigen, false, false);
		contingutHelper.arxiuPropagarCopia(
				contingutOrigen,
				contingutDesti);
		return dto;
	}

	@Transactional
	@Override
	public ContingutDto link(
			Long entitatId,
			Long contingutOrigenId,
			Long contingutDestiId,
			boolean recursiu) {
		logger.debug("Copiant el contingut ("
				+ "entitatId=" + entitatId + ", "
				+ "contingutOrigenId=" + contingutOrigenId + ", "
				+ "contingutDestiId=" + contingutDestiId + ", "
				+ "recursiu=" + recursiu + ")");
		ContingutEntity contingutOrigen = contingutHelper.comprovarContingutDinsExpedientModificable(
				entitatId,
				contingutOrigenId,
				true,
				false,
				false,
				false, false, true, null);
		ContingutEntity contingutDesti = contingutHelper.comprovarContingutDinsExpedientModificable(
				entitatId,
				contingutDestiId,
				false,
				false,
				false,
				false, false, true, null);
		// Comprova el tipus del contingut que es vol moure
		if (!(contingutOrigen instanceof DocumentEntity)) {
			throw new ValidationException(
					contingutOrigenId,
					contingutOrigen.getClass(),
					"Només es poden enllaçar documents");
		}
		// Mirar què passa amb els documents firmats
		//if (contingutOrigen instanceof DocumentEntity) {
		//	DocumentEntity documentOrigen = (DocumentEntity)contingutOrigen;
		//	if (documentOrigen.isFirmat()) {
		//		throw new ValidationException(
		//				contingutOrigenId,
		//				contingutOrigen.getClass(),
		//				"No es poden enllaçar documents firmats");
		//	}
		//}
		// Es comprova que el procediment orígen i destí son el mateix
		ExpedientEntity expedientOrigen = contingutHelper.getExpedientSuperior(
				contingutOrigen,
				true,
				false,
				false, false, null);
		ExpedientEntity expedientDesti = contingutHelper.getExpedientSuperior(
				contingutDesti,
				true,
				false,
				false, false, null);
		if (!expedientOrigen.getMetaExpedient().equals(expedientDesti.getMetaExpedient())) {
			throw new ValidationException(
					contingutOrigenId,
					contingutOrigen.getClass(),
					"Només es pot enllaçar un contingut a un expedient del mateix tipus que l'actual");
		}
		// Comprova que el nom no sigui duplicat
		boolean nomDuplicat = contingutRepository.findByPareAndNomAndEsborrat(
				contingutDesti,
				contingutOrigen.getNom(),
				0) != null;
		if (nomDuplicat) {
			throw new ValidationException(
					contingutOrigenId,
					ContingutEntity.class,
					"Ja existeix un altre contingut amb el mateix nom dins el contingut destí ("
							+ "contingutDestiId=" + contingutDestiId + ")");
		}
		//Crea el link del document dins l'arxiu digital
		ContingutArxiu nouContingut = contingutHelper.arxiuPropagarLink(
				contingutOrigen,
				contingutDesti);

		// Realitza la còpia del contingut
		ContingutEntity contingutCopia = vincularContingut(
				contingutOrigen.getEntitat(),
				contingutOrigen,
				contingutDesti,
				nouContingut.getIdentificador(),
				recursiu);
		contingutLogHelper.log(
				contingutCopia,
				LogTipusEnumDto.COPIA,
				null,
				null,
				true,
				true);
		ContingutDto dto = contingutHelper.toContingutDto(
				contingutOrigen, false, false);
		return dto;
	}

	@Transactional(readOnly = true)
	@Override
	public ContingutDto findAmbIdUser(
			Long entitatId,
			Long contingutId,
			boolean ambFills,
			boolean ambVersions, 
			String rolActual, 
			Long organActualId) {
		return findAmbIdUser(
				entitatId,
				contingutId,
				ambFills,
				ambVersions,
				true, 
				rolActual, 
				organActualId);
	}
	
	@Transactional(readOnly = true)
	@Override
	public Long getPareId(
			Long contingutId) {
		
		ContingutEntity contingut = contingutRepository.findOne(contingutId);
		return contingut.getPare() != null ? contingut.getPare().getId() : null;
	}
	
	@Transactional(readOnly = true)
	@Override
	public Long getExpedientId(
			Long contingutId) {
		
		ContingutEntity contingut = contingutRepository.findOne(contingutId);
		return contingut.getExpedientPare().getId();
	}
	
	
	
	@Transactional(readOnly = true)
	@Override
	public ContingutDto findAmbIdUser(
			Long entitatId,
			Long contingutId,
			boolean ambFills,
			boolean ambVersions,
			boolean ambPermisos, 
			String rolActual, 
			Long organActualId) {
		
		return findAmbIdUser(
				entitatId,
				contingutId,
				ambFills,
				ambVersions,
				ambPermisos,
				rolActual,
				true,
				false, 
				false);

	}
	
	@Override
	public ContingutDto findAmbIdUserPerMoureCopiarVincular(Long entitatId, Long contingutId) throws NotFoundException {
		long t0 = System.currentTimeMillis();
		logger.debug("Obtenint contingut amb id per usuari ("
				+ "entitatId=" + entitatId + ", "
				+ "contingutId=" + contingutId + ", "
				+ "ambFills=onlyCarpetes, "
				+ "ambVersions=false)");
		ContingutEntity contingut = contingutRepository.findOne(contingutId);

		if (cacheHelper.mostrarLogsRendiment())
			logger.info("findAmbIdUserPerMoureCopiarVincular time (" + contingut.getId() + "):  " + (System.currentTimeMillis() - t0) + " ms");
		
		return contingutHelper.toContingutDtoSimplificat(contingut, true, null);
	}

	@Transactional(readOnly = true)
	@Override
	public ContingutDto findAmbIdUser(
			Long entitatId,
			Long contingutId,
			boolean ambFills,
			boolean ambVersions,
			boolean ambPermisos, 
			String rolActual, 
			boolean ambEntitat,
			boolean ambMapPerTipusDocument, 
			boolean ambMapPerEstat) {
		
		long t2 = System.currentTimeMillis();
		logger.debug("Obtenint contingut amb id per usuari ("
				+ "entitatId=" + entitatId + ", "
				+ "contingutId=" + contingutId + ", "
				+ "ambFills=" + ambFills + ", "
				+ "ambVersions=" + ambVersions + ")");
		ContingutEntity contingut;
		if (ambPermisos) {
			contingut = contingutHelper.comprovarContingutDinsExpedientAccessible(
					entitatId,
					contingutId,
					true,
					false);
		} else {
			contingut = contingutRepository.findOne(contingutId);
		}
		// ** #979 -> Es comprova cada camp dins documentEntity
		// Comprovar si hi ha notificacions del document
//		for (ContingutEntity document: contingut.getFills()) {
//			if (document instanceof DocumentEntity) {
//				List<DocumentNotificacioEntity> notificacions = documentNotificacioRepository.findByDocumentOrderByCreatedDateDesc((DocumentEntity)document);
//				List<DocumentPortafirmesEntity> enviaments = documentPortafirmesRepository.findByDocumentOrderByCreatedDateDesc((DocumentEntity)document);
//				if (notificacions != null && notificacions.size() > 0) {
//					document.setAmbNotificacions(true);
//					DocumentNotificacioEntity lastNofificacio = notificacions.get(0);
//					document.setEstatDarreraNotificacio(lastNofificacio.getNotificacioEstat() != null ? lastNofificacio.getNotificacioEstat().name() : "");
//					document.setErrorDarreraNotificacio(lastNofificacio.isError());
//				}
//				if (enviaments != null && enviaments.size() > 0) {
//					DocumentEnviamentEntity lastEnviament = enviaments.get(0);
//					document.setErrorEnviamentPortafirmes(lastEnviament.isError());
//				}
//			}
//		}
		Long t0 = System.currentTimeMillis();
		ContingutDto dto = contingutHelper.toContingutDto(
				contingut,
				ambPermisos,
				ambFills,
				true,
				true,
				true,
				ambVersions,
				rolActual,
				false,
				null,
				false,
				0,
				null,
				null,
				true,
				ambEntitat,
				ambMapPerTipusDocument,
				ambMapPerEstat);
		dto.setAlerta(alertaRepository.countByLlegidaAndContingutId(
				false,
				dto.getId()) > 0);

		if (cacheHelper.mostrarLogsRendiment())
			logger.info("findAmbIdUser time (" + contingut.getId() + "):  " + (System.currentTimeMillis() - t2) + " ms");
		return dto;
	}
	
	
	@Transactional(readOnly = true)
	@Override
	public void checkIfPermitted(
			Long contingutId,
			String rolActual, 
			PermissionEnumDto permission) {
		
		contingutHelper.checkIfPermitted(
				contingutId,
				rolActual,
				permission);
		
	}
	
	
	
	@Transactional(readOnly = true)
	@Override
	public boolean isExpedient(
			Long contingutId) {

		ContingutEntity contingut = contingutRepository.findOne(contingutId);

		return contingut instanceof ExpedientEntity;
	}


	@Transactional(readOnly = true)
	@Override
	public ContingutDto findAmbIdAdmin(
			Long entitatId,
			Long contingutId) {
		logger.debug("Obtenint contingut amb id per admin ("
				+ "entitatId=" + entitatId + ", "
				+ "contingutId=" + contingutId + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				true,
				false,
				false, false, false);
		ContingutEntity contingut = entityComprovarHelper.comprovarContingut(
				entitat,
				contingutId);
		return contingutHelper.toContingutDto(
				contingut,
				false, 
				false);
	}

	@Transactional(readOnly = true)
	@Override
	public ContingutDto getContingutAmbFillsPerPath(
			Long entitatId,
			String path) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		logger.debug("Obtenint contingut amb fills donat el seu path ("
				+ "entitatId=" + entitatId + ", "
				+ "path=" + path + ", "
				+ "usuariCodi=" + auth.getName() + ")");
		return null;
	}

	@Transactional(readOnly = true)
	@Override
	public List<ValidacioErrorDto> findErrorsValidacio(
			Long entitatId,
			Long contingutId) {
		logger.debug("Obtenint errors de validació del contingut ("
				+ "entitatId=" + entitatId + ", "
				+ "contingutId=" + contingutId + ")");
		NodeEntity node = contingutHelper.comprovarNodeDinsExpedientAccessible(
				entitatId,
				contingutId,
				true,
				false);
		return cacheHelper.findErrorsValidacioPerNode(node);
	}

	@Transactional(readOnly = true)
	@Override
	public List<AlertaDto> findAlertes(
			Long entitatId,
			Long contingutId) throws NotFoundException {
		logger.debug("Obtenint alertes associades al contingut ("
				+ "entitatId=" + entitatId + ", "
				+ "contingutId=" + contingutId + ")");
		contingutHelper.comprovarContingutDinsExpedientAccessible(
				entitatId,
				contingutId,
				true,
				false);
		List<AlertaEntity> alertes = alertaRepository.findByLlegidaAndContingutId(
				false,
				contingutId,
				new Sort(Sort.Direction.DESC, "createdDate"));
		List<AlertaDto> resposta = conversioTipusHelper.convertirList(
				alertes,
				AlertaDto.class);
		for (int i = 0; i < alertes.size(); i++) {
			resposta.get(i).setCreatedDate(
					alertes.get(i).getCreatedDate().toDate());
		}
		return resposta;
	}

	@Transactional(readOnly = true)
	@Override
	public List<ContingutLogDto> findLogsPerContingutAdmin(
			Long entitatId,
			Long contingutId) {
		logger.debug("Obtenint registre d'accions pel contingut usuari admin ("
				+ "entitatId=" + entitatId + ", "
				+ "nodeId=" + contingutId + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				true,
				false,
				false, false, false);
		ContingutEntity contingut = entityComprovarHelper.comprovarContingut(
				entitat,
				contingutId);
		return contingutLogHelper.findLogsContingut(contingut);
	}

	@Transactional(readOnly = true)
	@Override
	public List<ContingutLogDto> findLogsPerContingutUser(
			Long entitatId,
			Long contingutId) {
		logger.debug("Obtenint registre d'accions pel contingut usuari normal ("
				+ "entitatId=" + entitatId + ", "
				+ "nodeId=" + contingutId + ")");
		ContingutEntity contingut = contingutHelper.comprovarContingutDinsExpedientAccessible(
				entitatId,
				contingutId,
				true,
				false);
		return contingutLogHelper.findLogsContingut(contingut);
	}

	@Transactional(readOnly = true)
	@Override
	public ContingutLogDetallsDto findLogDetallsPerContingutAdmin(
			Long entitatId,
			Long contingutId,
			Long contingutLogId) {
		logger.debug("Obtenint registre d'accions pel contingut usuari normal ("
				+ "entitatId=" + entitatId + ", "
				+ "nodeId=" + contingutId + ")");
		entityComprovarHelper.comprovarEntitat(
				entitatId,
				true,
				false,
				false, false, false);
//		ContingutEntity contingut = entityComprovarHelper.comprovarContingut(
//				entitat,
//				contingutId);
		return contingutLogHelper.findLogDetalls(
				contingutId,
				contingutLogId);
	}

	@Transactional(readOnly = true)
	@Override
	public ContingutLogDetallsDto findLogDetallsPerContingutUser(
			Long entitatId,
			Long contingutId,
			Long contingutLogId) {
		logger.debug("Obtenint registre d'accions pel contingut usuari normal ("
				+ "entitatId=" + entitatId + ", "
				+ "nodeId=" + contingutId + ")");
		entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				false,
				false, 
				true, false);
//		ContingutEntity contingut = contingutHelper.comprovarContingutDinsExpedientAccessible(
//				entitatId,
//				contingutId,
//				true,
//				false);
		return contingutLogHelper.findLogDetalls(
				contingutId,
				contingutLogId);
	}

	@Transactional(readOnly = true)
	@Override
	public List<ContingutMovimentDto> findMovimentsPerContingutAdmin(
			Long entitatId,
			Long contingutId) {
		logger.debug("Obtenint registre de moviments pel contingut usuari admin ("
				+ "entitatId=" + entitatId + ", "
				+ "nodeId=" + contingutId + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				false,
				true, false, false);
		ContingutEntity contingut = entityComprovarHelper.comprovarContingut(
				entitat,
				contingutId);
		return contingutLogHelper.findMovimentsContingut(contingut);
	}

	@Transactional(readOnly = true)
	@Override
	public List<ContingutMovimentDto> findMovimentsPerContingutUser(
			Long entitatId,
			Long contingutId) {
		logger.debug("Obtenint registre de moviments pel contingut usuari normal ("
				+ "entitatId=" + entitatId + ", "
				+ "nodeId=" + contingutId + ")");
		ContingutEntity contingut = contingutHelper.comprovarContingutDinsExpedientAccessible(
				entitatId,
				contingutId,
				true,
				false);
		return contingutLogHelper.findMovimentsContingut(contingut);
	}

	@Transactional(readOnly = true)
	@Override
	public PaginaDto<ContingutDto> findAdmin(
			Long entitatId,
			ContingutFiltreDto filtre,
			PaginacioParamsDto paginacioParams) {
		logger.debug("Consulta de continguts per usuari admin ("
				+ "entitatId=" + entitatId + ", "
				+ "filtre=" + filtre + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false, false, false);
		MetaNodeEntity metaNode = null;
		if (filtre.getMetaNodeId() != null) {
			metaNode = metaNodeRepository.findOne(filtre.getMetaNodeId());
			if (metaNode == null) {
				throw new NotFoundException(
						filtre.getMetaNodeId(),
						MetaNodeEntity.class);
			}
		}
		boolean tipusCarpeta = true;
		boolean tipusDocument = true;
		boolean tipusExpedient = true;
		if (filtre.getTipus() != null) {
			switch (filtre.getTipus()) {
			case CARPETA:
				tipusCarpeta = true;
				tipusDocument = false;
				tipusExpedient = false;
				break;
			case DOCUMENT:
				tipusCarpeta = false;
				tipusDocument = true;
				tipusExpedient = false;
				break;
			case EXPEDIENT:
				tipusCarpeta = false;
				tipusDocument = false;
				tipusExpedient = true;
				break;
			case REGISTRE:
				tipusCarpeta = false;
				tipusDocument = false;
				tipusExpedient = false;
				break;
			}
		}
		Date dataCreacioInici = DateHelper.toDateInicialDia(filtre.getDataCreacioInici());
		Date dataCreacioFi = DateHelper.toDateFinalDia(filtre.getDataCreacioFi());
		
		Date dataEsborratInici = DateHelper.toDateInicialDia(filtre.getDataEsborratInici());
		Date dataEsborratFi = DateHelper.toDateFinalDia(filtre.getDataEsborratFi());
		
		ExpedientEntity expedient = null;
		if (filtre.getExpedientId() != null) {
			expedient = expedientRepository.findOne(filtre.getExpedientId());
			if (expedient == null) {
				throw new NotFoundException(
						filtre.getExpedientId(),
						ExpedientEntity.class);
			}
		}
		
		Map<String, String[]> ordenacioMap = new HashMap<String, String[]>();
		ordenacioMap.put("createdBy.codiAndNom", new String[] {"createdBy.nom"});
		
		
		
		return paginacioHelper.toPaginaDto(
				contingutRepository.findByFiltrePaginat(
						entitat,
						tipusCarpeta,
						tipusDocument,
						tipusExpedient,
						(filtre.getNom() == null),
						filtre.getNom() != null ? filtre.getNom().trim() : "",
						(filtre.getCreador() == null), filtre.getCreador() != null ?
						filtre.getCreador().trim() : "",
						(metaNode == null),
						metaNode,
						(dataCreacioInici == null),
						dataCreacioInici,
						(dataCreacioFi == null),
						dataCreacioFi,
						(dataEsborratInici == null),
						dataEsborratInici,
						(dataEsborratFi == null),
						dataEsborratFi,
						filtre.isMostrarEsborrats(),
						filtre.isMostrarNoEsborrats(),
						(expedient == null),
						expedient,
						paginacioHelper.toSpringDataPageable(paginacioParams, ordenacioMap)),
				ContingutDto.class,
				new Converter<ContingutEntity, ContingutDto>() {
					@Override
					public ContingutDto convert(ContingutEntity source) {
						return contingutHelper.toContingutDto(
								source,
								true, 
								false);
					}
				});
	}



	@SuppressWarnings("incomplete-switch")
	@Transactional(readOnly = true)
	@Override
	public ArxiuDetallDto getArxiuDetall(
			Long entitatId,
			Long contingutId) {
		organGestorHelper.actualitzarOrganCodi(organGestorHelper.getOrganCodiFromContingutId(contingutId));
		logger.debug("Obtenint informació de l'arxiu pel contingut ("
				+ "entitatId=" + entitatId + ", "
				+ "contingutId=" + contingutId + ")");
		ContingutEntity contingut = contingutHelper.comprovarContingutDinsExpedientAccessible(
				entitatId,
				contingutId,
				true,
				false);
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				false,
				false, 
				true, false);
		List<ContingutArxiu> continguts = null;
		List<Firma> firmes = null;
		ArxiuDetallDto arxiuDetall = new ArxiuDetallDto();
		
		// ##################### EXPEDIENT ##################################
		if (contingut instanceof ExpedientEntity) {
			es.caib.plugins.arxiu.api.Expedient arxiuExpedient = pluginHelper.arxiuExpedientConsultar(
					(ExpedientEntity)contingut);
			continguts = arxiuExpedient.getContinguts();
			arxiuDetall.setIdentificador(arxiuExpedient.getIdentificador());
			arxiuDetall.setNom(arxiuExpedient.getNom());
			ExpedientMetadades metadades = arxiuExpedient.getMetadades();
			if (metadades != null) {
				arxiuDetall.setEniVersio(metadades.getVersioNti());
				arxiuDetall.setEniIdentificador(metadades.getIdentificador());
				arxiuDetall.setSerieDocumental(metadades.getSerieDocumental());
				arxiuDetall.setEniDataObertura(metadades.getDataObertura());
				arxiuDetall.setEniClassificacio(metadades.getClassificacio());
				if (metadades.getEstat() != null) {
					switch (metadades.getEstat()) {
					case OBERT:
						arxiuDetall.setEniEstat(ExpedientEstatEnumDto.OBERT);
						break;
					case TANCAT:
						arxiuDetall.setEniEstat(ExpedientEstatEnumDto.TANCAT);
						break;
					case INDEX_REMISSIO:
						break;
					}
				}
				arxiuDetall.setEniInteressats(metadades.getInteressats());
				arxiuDetall.setEniOrgans(getOrgansAmbNoms(metadades.getOrgans()));
				arxiuDetall.setMetadadesAddicionals(metadades.getMetadadesAddicionals());		
			}
			
		// ##################### DOCUMENT ##################################
		} else if (contingut instanceof DocumentEntity) {
			Document arxiuDocument = pluginHelper.arxiuDocumentConsultar(
					(DocumentEntity) contingut,
					null,
					null,
					true);
			firmes = arxiuDocument.getFirmes();
			arxiuDetall.setIdentificador(arxiuDocument.getIdentificador());
			arxiuDetall.setNom(arxiuDocument.getNom());
			DocumentMetadades metadades = arxiuDocument.getMetadades();
			if (metadades != null) {
				arxiuDetall.setEniVersio(metadades.getVersioNti());
				arxiuDetall.setEniIdentificador(metadades.getIdentificador());
				arxiuDetall.setSerieDocumental(metadades.getSerieDocumental());
				arxiuDetall.setEniDataCaptura(metadades.getDataCaptura());
				
				arxiuDetall.setEniOrigen(ArxiuConversions.getOrigen(metadades.getOrigen()));

				arxiuDetall.setEniEstatElaboracio(ArxiuConversions.getEstatElaboracio(metadades.getEstatElaboracio()));
				
				if (metadades.getTipusDocumental() != null) {
					List<TipusDocumentalEntity> tipos = tipusDocumentalRepository.findByCodi(metadades.getTipusDocumental().toString());
					if (Utils.isNotEmpty(tipos)) {
						TipusDocumentalDto tipus = conversioTipusHelper.convertir(tipos.get(0), TipusDocumentalDto.class);
						arxiuDetall.setEniTipusDocumental(tipus.getCodiNom());
					} else {
						arxiuDetall.setEniTipusDocumental(metadades.getTipusDocumental().toString());
					}
				}

				if (metadades.getTipusDocumental() == null && metadades.getTipusDocumentalAddicional() != null) {
					logger.info("Tipus documental addicional: " + metadades.getTipusDocumentalAddicional());
					TipusDocumentalEntity tipusDocumental = tipusDocumentalRepository.findByCodiAndEntitat(
							metadades.getTipusDocumentalAddicional(),
							entitat);

					if (tipusDocumental != null) {
						arxiuDetall.setEniTipusDocumentalAddicional(tipusDocumental.getNomEspanyol());
					} else {
						List<TipusDocumentalDto> docsAddicionals = pluginHelper.documentTipusAddicionals();
						
						for (TipusDocumentalDto docAddicional : docsAddicionals) {
							if (docAddicional.getCodi().equals(metadades.getTipusDocumentalAddicional())) {
								arxiuDetall.setEniTipusDocumentalAddicional(docAddicional.getNom());
							}
						}
					}

					arxiuDetall.setEniTipusDocumentalAddicional(tipusDocumental.getNomEspanyol());
				}

				arxiuDetall.setEniOrgans(getOrgansAmbNoms(metadades.getOrgans()));
				if (metadades.getFormat() != null) {
					arxiuDetall.setEniFormat(metadades.getFormat().toString());
				}
				arxiuDetall.setEniDocumentOrigenId(metadades.getIdentificadorOrigen());
				
				final String fechaSelladoKey = "eni:fecha_sellado";
				if (metadades.getMetadadesAddicionals().containsKey(fechaSelladoKey)) {
					try {
						DateFormat dfIn= new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
						DateFormat dfOut = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
						Date fechaSelladoValor = dfIn.parse(metadades.getMetadadesAddicionals().get(fechaSelladoKey).toString());
						String fechaSelladoValorStr = dfOut.format(fechaSelladoValor);
						metadades.getMetadadesAddicionals().put(fechaSelladoKey, fechaSelladoValorStr);
					} catch (ParseException e) {
						logger.error(e.getMessage(), e);
					}		
				}
				arxiuDetall.setMetadadesAddicionals(metadades.getMetadadesAddicionals());
				
				if (arxiuDocument.getContingut() != null) {
					arxiuDetall.setContingutArxiuNom(
							arxiuDocument.getContingut().getArxiuNom());
					arxiuDetall.setContingutTipusMime(
							arxiuDocument.getContingut().getTipusMime());
				}

			}
			if (arxiuDocument.getEstat() != null) {
				if (DocumentEstat.ESBORRANY.equals(arxiuDocument.getEstat()))
					arxiuDetall.setArxiuEstat(ArxiuEstatEnumDto.ESBORRANY);
				else if (DocumentEstat.DEFINITIU.equals(arxiuDocument.getEstat()))
					arxiuDetall.setArxiuEstat(ArxiuEstatEnumDto.DEFINITIU);
			}
			
		// ##################### CARPETA ##################################
		} else if (contingut instanceof CarpetaEntity) {
			Carpeta arxiuCarpeta = pluginHelper.arxiuCarpetaConsultar(
					(CarpetaEntity)contingut);
			continguts = arxiuCarpeta.getContinguts();
			arxiuDetall.setIdentificador(arxiuCarpeta.getIdentificador());
			arxiuDetall.setNom(arxiuCarpeta.getNom());
		} else {
			throw new ValidationException(
					contingutId,
					ContingutEntity.class,
					"Tipus de contingut desconegut: " + contingut.getClass().getName());
		}
		
		// ##################### CONTINGUT ##################################
		if (continguts != null) {
			List<ArxiuContingutDto> detallFills = new ArrayList<ArxiuContingutDto>();
			for (ContingutArxiu cont: continguts) {
				ArxiuContingutDto detallFill = new ArxiuContingutDto();
				detallFill.setIdentificador(
						cont.getIdentificador());
				detallFill.setNom(
						cont.getNom());
				if (cont.getTipus() != null) {
					switch (cont.getTipus()) {
					case EXPEDIENT:
						detallFill.setTipus(ArxiuContingutTipusEnumDto.EXPEDIENT);
						break;
					case DOCUMENT:
						detallFill.setTipus(ArxiuContingutTipusEnumDto.DOCUMENT);
						break;
					case CARPETA:
						detallFill.setTipus(ArxiuContingutTipusEnumDto.CARPETA);
						break;
					}
				}
				detallFills.add(detallFill);
			}
			arxiuDetall.setFills(detallFills);
		}
		if (firmes != null) {
			List<ArxiuFirmaDto> dtos = new ArrayList<ArxiuFirmaDto>();
			for (Firma firma: firmes) {
				ArxiuFirmaDto dto = new ArxiuFirmaDto();
				
				if (firma.getTipus() != null) {
					switch (firma.getTipus()) {
					case CSV:
						dto.setTipus(ArxiuFirmaTipusEnumDto.CSV);
						break;
					case XADES_DET:
						dto.setTipus(ArxiuFirmaTipusEnumDto.XADES_DET);
						break;
					case XADES_ENV:
						dto.setTipus(ArxiuFirmaTipusEnumDto.XADES_ENV);
						break;
					case CADES_DET:
						dto.setTipus(ArxiuFirmaTipusEnumDto.CADES_DET);
						break;
					case CADES_ATT:
						dto.setTipus(ArxiuFirmaTipusEnumDto.CADES_ATT);
						break;
					case PADES:
						dto.setTipus(ArxiuFirmaTipusEnumDto.PADES);
						break;
					case SMIME:
						dto.setTipus(ArxiuFirmaTipusEnumDto.SMIME);
						break;
					case ODT:
						dto.setTipus(ArxiuFirmaTipusEnumDto.ODT);
						break;
					case OOXML:
						dto.setTipus(ArxiuFirmaTipusEnumDto.OOXML);
						break;
					}
				}
				if (firma.getPerfil() != null) {
					switch (firma.getPerfil()) {
					case BES:
						dto.setPerfil(ArxiuFirmaPerfilEnumDto.BES);
						break;
					case EPES:
						dto.setPerfil(ArxiuFirmaPerfilEnumDto.EPES);
						break;
					case LTV:
						dto.setPerfil(ArxiuFirmaPerfilEnumDto.LTV);
						break;
					case T:
						dto.setPerfil(ArxiuFirmaPerfilEnumDto.T);
						break;
					case C:
						dto.setPerfil(ArxiuFirmaPerfilEnumDto.C);
						break;
					case X:
						dto.setPerfil(ArxiuFirmaPerfilEnumDto.X);
						break;
					case XL:
						dto.setPerfil(ArxiuFirmaPerfilEnumDto.XL);
						break;
					case A:
						dto.setPerfil(ArxiuFirmaPerfilEnumDto.A);
						break;
					case BASIC:
						dto.setPerfil(ArxiuFirmaPerfilEnumDto.BASIC);
						break;
					case Basic:
						dto.setPerfil(ArxiuFirmaPerfilEnumDto.Basic);
						break;
					case BASELINE_B_LEVEL:
						dto.setPerfil(ArxiuFirmaPerfilEnumDto.BASELINE_B_LEVEL);
						break;
					case BASELINE_LTA_LEVEL:
						dto.setPerfil(ArxiuFirmaPerfilEnumDto.BASELINE_LTA_LEVEL);
						break;
					case BASELINE_LT_LEVEL:
						dto.setPerfil(ArxiuFirmaPerfilEnumDto.BASELINE_LT_LEVEL);
						break;
					case BASELINE_T:
						dto.setPerfil(ArxiuFirmaPerfilEnumDto.BASELINE_T);
						break;
					case BASELINE_T_LEVEL:
						dto.setPerfil(ArxiuFirmaPerfilEnumDto.BASELINE_T_LEVEL);
						break;
					case LTA:
						dto.setPerfil(ArxiuFirmaPerfilEnumDto.LTA);
						break;	
					}
				}
				dto.setFitxerNom(firma.getFitxerNom());
				if (ArxiuFirmaTipusEnumDto.CSV.equals(dto.getTipus())) {
					dto.setContingut(firma.getContingut());
				}
				dto.setTipusMime(firma.getTipusMime());
				dto.setCsvRegulacio(firma.getCsvRegulacio());
				dtos.add(dto);
			}
			arxiuDetall.setFirmes(dtos);
		}
		return arxiuDetall;
	}

	@Transactional(readOnly = true)
	@Override
	public FitxerDto exportacioEni(
			Long entitatId,
			Long contingutId) {
		logger.debug("Exportant document a format ENI (" +
				"entitatId=" + entitatId + ", " +
				"contingutId=" + contingutId + ")");
		ContingutEntity contingut = contingutHelper.comprovarContingutDinsExpedientAccessible(
				entitatId,
				contingutId,
				true,
				false);
		String exportacio;
		if (contingut instanceof ExpedientEntity) {
			exportacio = pluginHelper.arxiuExpedientExportar(
					(ExpedientEntity)contingut);
		} else if (contingut instanceof DocumentEntity) {
			exportacio = pluginHelper.arxiuDocumentExportar(
					(DocumentEntity)contingut);
		} else {
			throw new ValidationException(
					contingutId,
					ContingutEntity.class,
					"El contingut a exportar ha de ser un expedient o un document");
		}
		FitxerDto fitxer = new FitxerDto();
		fitxer.setNom("exportacio_ENI.xml");
		fitxer.setContentType("application/xml");
		fitxer.setContingut(exportacio.getBytes());
		return fitxer;
	}

	@Transactional(readOnly = true)
	@Override
	public PaginaDto<DocumentDto> findDocumentsPerFirmaMassiu(
			Long entitatId,
			ContingutMassiuFiltreDto filtre,
			PaginacioParamsDto paginacioParams, 
			String rolActual) throws NotFoundException {
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				false,
				false,
				true, false);

		
		boolean checkPerMassiuAdmin = false;
		if (rolActual.equals("IPA_ADMIN") || rolActual.equals("IPA_ORGAN_ADMIN")) {
			checkPerMassiuAdmin = true;
		} 

		MetaExpedientEntity metaExpedient = null;
		if (filtre.getMetaExpedientId() != null) {
			metaExpedient = entityComprovarHelper.comprovarMetaExpedient(entitat, filtre.getMetaExpedientId());
		}
		ExpedientEntity expedient = null;
		if (filtre.getExpedientId() != null) {
			expedient = entityComprovarHelper.comprovarExpedient(
					filtre.getExpedientId(),
					false,
					false,
					false,
					false,
					false,
					null);
		}
		MetaDocumentEntity metaDocument = null;
		if (filtre.getMetaDocumentId() != null) {
			metaDocument = entityComprovarHelper.comprovarMetaDocument(
					entitat,
					filtre.getMetaDocumentId());
		}
		List<MetaExpedientEntity> metaExpedientsPermesos = metaExpedientHelper.findPermesosAccioMassiva(entitatId, rolActual);
		if (!metaExpedientsPermesos.isEmpty()) {
			Date dataInici = DateHelper.toDateInicialDia(filtre.getDataInici());
			Date dataFi = DateHelper.toDateFinalDia(filtre.getDataFi());
			
			Map<String, String[]> ordenacioMap = new HashMap<String, String[]>();
			ordenacioMap.put("createdBy.codiAndNom", new String[] {"createdBy.nom"});
			ordenacioMap.put("metaDocument.nom", new String[] {"metaNode.nom"});
			Page<DocumentEntity> paginaDocuments = documentRepository.findDocumentsPerFirmaMassiu(
					entitat,
					metaExpedientsPermesos, 
					metaExpedient == null,
					metaExpedient,
					expedient == null,
					expedient,
					metaDocument == null,
					metaDocument,
					filtre.getNom() == null,
					filtre.getNom() != null ? filtre.getNom().trim() : "",
					dataInici == null,
					dataInici,
					dataFi == null,
					dataFi,
					paginacioHelper.toSpringDataPageable(paginacioParams, ordenacioMap));
			return paginacioHelper.toPaginaDto(
					paginaDocuments,
					DocumentDto.class,
					new Converter<DocumentEntity, DocumentDto>() {
						@Override
						public DocumentDto convert(DocumentEntity source) {
							DocumentDto dto = (DocumentDto)contingutHelper.toContingutDto(
									source,
									true,
									true);
							return dto;
						}
					});
		} else {
			return paginacioHelper.getPaginaDtoBuida(
					DocumentDto.class);
		}
	}

	@Transactional(readOnly = true)
	@Override
	public List<Long> findIdsDocumentsPerFirmaMassiu(
			Long entitatId,
			ContingutMassiuFiltreDto filtre, 
			String rolActual) throws NotFoundException {
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				true,
				false,
				false, false, false);
		
		boolean checkPerMassiuAdmin = false;
		if (rolActual.equals("IPA_ADMIN") || rolActual.equals("IPA_ORGAN_ADMIN")) {
			checkPerMassiuAdmin = true;
		} 
		
		MetaExpedientEntity metaExpedient = null;
		if (filtre.getMetaExpedientId() != null) {
			metaExpedient = entityComprovarHelper.comprovarMetaExpedient(entitat, filtre.getMetaExpedientId());
		}
		ExpedientEntity expedient = null;
		if (filtre.getExpedientId() != null) {
			expedient = entityComprovarHelper.comprovarExpedient(
					filtre.getExpedientId(),
					false,
					false,
					false,
					false,
					false,
					null);
		}
		MetaDocumentEntity metaDocument = null;
		if (filtre.getMetaDocumentId() != null) {
			metaDocument = entityComprovarHelper.comprovarMetaDocument(
					entitat,
					filtre.getMetaExpedientId());
		}
		List<MetaExpedientEntity> metaExpedientsPermesos = metaExpedientHelper.findPermesosAccioMassiva(entitatId, rolActual);
		if (!metaExpedientsPermesos.isEmpty()) {
			Date dataInici = DateHelper.toDateInicialDia(filtre.getDataInici());
			Date dataFi = DateHelper.toDateFinalDia(filtre.getDataFi());
			List<Long> idsDocuments = documentRepository.findIdsDocumentsPerFirmaMassiu(
					entitat,
					metaExpedientsPermesos, 
					metaExpedient == null,
					metaExpedient,
					expedient == null,
					expedient,
					metaDocument == null,
					metaDocument,
					filtre.getNom() == null,
					filtre.getNom() != null ? filtre.getNom().trim() : "",
					dataInici == null,
					dataInici,
					dataFi == null,
					dataFi);
			return idsDocuments;
		} else {
			return new ArrayList<>();
		}
	}
	
	@Transactional(readOnly = true)
	@Override
	public PaginaDto<DocumentDto> findDocumentsPerCopiarCsv(
			Long entitatId,
			ContingutMassiuFiltreDto filtre,
			PaginacioParamsDto paginacioParams, 
			String rolActual) throws NotFoundException {
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				false,
				false,
				true, false);

		
		boolean checkPerMassiuAdmin = false;
		if (rolActual.equals("IPA_ADMIN") || rolActual.equals("IPA_ORGAN_ADMIN")) {
			checkPerMassiuAdmin = true;
		} 

		MetaExpedientEntity metaExpedient = null;
		if (filtre.getMetaExpedientId() != null) {
			metaExpedient = entityComprovarHelper.comprovarMetaExpedient(entitat, filtre.getMetaExpedientId());
		}
		ExpedientEntity expedient = null;
		if (filtre.getExpedientId() != null) {
			expedient = entityComprovarHelper.comprovarExpedient(
					filtre.getExpedientId(),
					false,
					false,
					false,
					false,
					false,
					null);
		}
		MetaDocumentEntity metaDocument = null;
		if (filtre.getMetaDocumentId() != null) {
			metaDocument = entityComprovarHelper.comprovarMetaDocument(
					entitat,
					filtre.getMetaDocumentId());
		}
		List<MetaExpedientEntity> metaExpedientsPermesos = metaExpedientHelper.findPermesosAccioMassiva(entitatId, rolActual);
		if (!metaExpedientsPermesos.isEmpty()) {
			Date dataInici = DateHelper.toDateInicialDia(filtre.getDataInici());
			Date dataFi = DateHelper.toDateFinalDia(filtre.getDataFi());
			
			Map<String, String[]> ordenacioMap = new HashMap<String, String[]>();
			ordenacioMap.put("createdBy.codiAndNom", new String[] {"createdBy.nom"});
			ordenacioMap.put("metaDocument.nom", new String[] {"metaNode.nom"});
			Page<DocumentEntity> paginaDocuments = documentRepository.findDocumentsPerCopiarCsv(
					entitat,
					metaExpedientsPermesos, 
					metaExpedient == null,
					metaExpedient,
					expedient == null,
					expedient,
					metaDocument == null,
					metaDocument,
					filtre.getNom() == null,
					filtre.getNom() != null ? filtre.getNom().trim() : "",
					dataInici == null,
					dataInici,
					dataFi == null,
					dataFi,
					paginacioHelper.toSpringDataPageable(paginacioParams, ordenacioMap));
			return paginacioHelper.toPaginaDto(
					paginaDocuments,
					DocumentDto.class,
					new Converter<DocumentEntity, DocumentDto>() {
						@Override
						public DocumentDto convert(DocumentEntity source) {
							DocumentDto dto = (DocumentDto)contingutHelper.toContingutDto(
									source,
									true,
									true);
							return dto;
						}
					});
		} else {
			return paginacioHelper.getPaginaDtoBuida(
					DocumentDto.class);
		}
	}

	@Transactional
	@Override
	public void order(
			Long entitatId, 
			Long contingutId,
			Map<Integer, Long> orderedElements)
			throws NotFoundException, ValidationException {
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				true,
				false,
				false,
				false, 
				false);
		contingutHelper.comprovarContingutDinsExpedientModificable(
				entitatId,
				contingutId,
				false,
				true,
				false,
				false,
				false, true, null);
		for (Map.Entry<Integer, Long> fill: orderedElements.entrySet()) {
			Integer ordre = fill.getKey();
			Long fillId = fill.getValue();
			
			ContingutEntity contingut = entityComprovarHelper.comprovarContingut(entitat, fillId);
			contingut.updateOrdre(ordre);
		}
	}
	
	/*private ContingutEntity contingutHelper.comprovarContingutDinsExpedient(
			Long entitatId,
			Long contingutId,
			boolean comprovarEsNode,
			boolean comprovarPermisReadEnNode,
			boolean comprovarPermisWriteEnNode,
			boolean comprovarPermisDeleteEnNode) {
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				true,
				false,
				false);
		ContingutEntity contingut = entityComprovarHelper.comprovarContingut(
				entitat,
				contingutId);
		// Comprova el permís de modificació de l'expedient superior
		ExpedientEntity expedientSuperior = contingutHelper.getExpedientSuperior(
				contingut,
				true,
				false,
				true);
		if (expedientSuperior != null) {
			// Comprova que l'usuari actual te agafat l'expedient
			UsuariEntity agafatPer = expedientSuperior.getAgafatPer();
			if (agafatPer != null) {
				Authentication auth = SecurityContextHolder.getContext().getAuthentication();
				if (!auth.getName().equals(agafatPer.getCodi())) {
					throw new ValidationException(
							contingutId,
							ContingutEntity.class,
							"L'expedient al qual pertany el contingut no està agafat per l'usuari actual (" +
							"usuariActualCodi=" + auth.getName() + ")");
				}
			} else {
				throw new ValidationException(
						contingutId,
						ContingutEntity.class,
						"L'expedient al qual pertany el contingut no està agafat per cap usuari");
			}
			// Comprova els permisos per a modificar l'expedient
			contingutHelper.comprovarPermisosNode(
					expedientSuperior,
					false,
					true,
					false);
			if (comprovarEsNode) {
				if (!(contingut instanceof NodeEntity)) {
					throw new ValidationException(
							contingutId,
							ContingutEntity.class,
							"El contingut no és un node");
				}
				NodeEntity node = (NodeEntity)contingut;
				contingutHelper.comprovarPermisosNode(
						node,
						comprovarPermisReadEnNode,
						comprovarPermisWriteEnNode,
						comprovarPermisDeleteEnNode);
			}
		} else {
			throw new ValidationException(
					contingutId,
					ContingutEntity.class,
					"No es pot modificar un contingut que no està associat a un expedient");
		}
		return contingut;
	}*/



	private ContingutEntity copiarContingut(
			EntitatEntity entitat,
			ContingutEntity contingutOrigen,
			ContingutEntity contingutDesti,
			boolean recursiu) {
		ContingutEntity creat = null;
		if (contingutOrigen instanceof CarpetaEntity) {
			CarpetaEntity carpetaOrigen = (CarpetaEntity)contingutOrigen;
			CarpetaEntity carpetaNova = CarpetaEntity.getBuilder(
					carpetaOrigen.getNom(),
					contingutDesti,
					entitat,
					contingutDesti.getExpedient()).build();
			creat = contingutRepository.save(carpetaNova);
		} else if (contingutOrigen instanceof DocumentEntity) {
			DocumentEntity documentOrigen = (DocumentEntity)contingutOrigen;
			creat = documentHelper.crearDocumentDB(
					documentOrigen.getDocumentTipus(),
					documentOrigen.getNom(),
					documentOrigen.getDescripcio(),
					documentOrigen.getData(),
					documentOrigen.getDataCaptura(),
					documentOrigen.getNtiOrgano(),
					documentOrigen.getNtiOrigen(),
					documentOrigen.getNtiEstadoElaboracion(),
					documentOrigen.getNtiTipoDocumental(),
					documentOrigen.getMetaDocument(),
					contingutDesti,
					entitat,
					contingutDesti.getExpedient(),
					documentOrigen.getUbicacio(),
					documentOrigen.getNtiIdDocumentoOrigen(),
					null, 
					documentOrigen.getDocumentFirmaTipus(), 
					documentOrigen.getExpedientEstatAdditional());
		}
		if (creat != null) {
			if (creat instanceof NodeEntity) {
				NodeEntity nodeOrigen = (NodeEntity)contingutOrigen;
				NodeEntity nodeDesti = (NodeEntity)creat;
				for (DadaEntity dada: dadaRepository.findByNode(nodeOrigen)) {
					DadaEntity dadaNova = DadaEntity.getBuilder(
							dada.getMetaDada(),
							nodeDesti,
							dada.getValor(),
							dada.getOrdre()).build();
					dadaRepository.save(dadaNova);
				}
			}
			if (recursiu) {
				for (ContingutEntity fill: contingutOrigen.getFills()) {
					if (fill instanceof CarpetaEntity || fill instanceof DocumentEntity) {
						copiarContingut(
								entitat,
								fill,
								creat,
								recursiu);
					}
				}
			}
		}
		return creat;
	}

	private ContingutEntity vincularContingut(
			EntitatEntity entitat,
			ContingutEntity contingutOrigen,
			ContingutEntity contingutDesti,
			String uuidDocumentoOrigen,
			boolean recursiu) {
		ContingutEntity creat = null;
		if (contingutOrigen instanceof DocumentEntity) {
			DocumentEntity documentOrigen = (DocumentEntity)contingutOrigen;
			creat = documentHelper.crearDocumentDB(
					documentOrigen.getDocumentTipus(),
					documentOrigen.getNom(),
					documentOrigen.getDescripcio(),
					documentOrigen.getData(),
					documentOrigen.getDataCaptura(),
					documentOrigen.getNtiOrgano(),
					documentOrigen.getNtiOrigen(),
					documentOrigen.getNtiEstadoElaboracion(),
					documentOrigen.getNtiTipoDocumental(),
					documentOrigen.getMetaDocument(),
					contingutDesti,
					entitat,
					contingutDesti.getExpedient(),
					documentOrigen.getUbicacio(),
					uuidDocumentoOrigen,
					null, 
					documentOrigen.getDocumentFirmaTipus(), 
					documentOrigen.getExpedientEstatAdditional());
		}
		if (creat != null) {
			if (creat instanceof DocumentEntity) {
				DocumentEntity documentOrigen = (DocumentEntity)contingutOrigen;
				creat.updateArxiu(uuidDocumentoOrigen);
				creat.updateExpedient((ExpedientEntity)contingutDesti);
				creat.updatePare(contingutDesti);
				documentHelper.actualitzarFitxerDB(
						(DocumentEntity) creat,
						new FitxerDto(
								documentOrigen.getFitxerNom(),
								documentOrigen.getFitxerContentType(),
								documentOrigen.getFitxerTamany()));

			}
			if (creat instanceof NodeEntity) {
				NodeEntity nodeOrigen = (NodeEntity)contingutOrigen;
				NodeEntity nodeDesti = (NodeEntity)creat;
				for (DadaEntity dada: dadaRepository.findByNode(nodeOrigen)) {
					DadaEntity dadaNova = DadaEntity.getBuilder(
							dada.getMetaDada(),
							nodeDesti,
							dada.getValor(),
							dada.getOrdre()).build();
					dadaRepository.save(dadaNova);
				}
			}
			if (recursiu) {
				for (ContingutEntity fill: contingutOrigen.getFills()) {
					if (fill instanceof CarpetaEntity || fill instanceof DocumentEntity) {
						vincularContingut(
								entitat,
								fill,
								creat,
								uuidDocumentoOrigen,
								recursiu);
					}
				}
			}
		}
		return creat;
	}

	private void nodeDadaGuardar(
			NodeEntity node,
			String dadaCodi,
			Object dadaValor,
			Long entitatId) {
		MetaDadaEntity metaDada = metaDadaRepository.findByMetaNodeAndCodi(
				node.getMetaNode(),
				dadaCodi);
		if (metaDada == null) {
			throw new ValidationException(
					node.getId(),
					NodeEntity.class,
					"No s'ha trobat la metaDada amb el codi " + dadaCodi);
		}
		List<DadaEntity> dades = dadaRepository.findByNodeAndMetaDadaOrderByOrdreAsc(
				node,
				metaDada);
		Object[] valors = (dadaValor instanceof Object[]) ? (Object[])dadaValor : new Object[] {dadaValor};
		// Esborra els valors nulls
		List<Object> valorsSenseNull = new ArrayList<Object>();
		for (Object o: valors) {
			if (o != null)
				valorsSenseNull.add(o);
		}
		// Esborra les dades ja creades que sobren
		if (dades.size() > valorsSenseNull.size()) {
			for (int i = valorsSenseNull.size(); i < dades.size(); i++) {
				dadaRepository.delete(dades.get(i));
			}
		}
		// Modifica o crea les dades
		for (int i = 0; i < valorsSenseNull.size(); i++) {
			DadaEntity dada = (i < dades.size()) ? dades.get(i) : null;
			if (dada != null) {
				dada.update(
						valorsSenseNull.get(i),
						i);
				contingutLogHelper.log(
						node,
						LogTipusEnumDto.MODIFICACIO,
						dada,
						LogObjecteTipusEnumDto.DADA,
						LogTipusEnumDto.MODIFICACIO,
						dadaCodi,
						dada.getValorComString(),
						false,
						false);
			} else {
				dada = DadaEntity.getBuilder(
						metaDada,
						node,
						valorsSenseNull.get(i),
						i).build();
				dadaRepository.save(dada);
				contingutLogHelper.log(
						node,
						LogTipusEnumDto.MODIFICACIO,
						dada,
						LogObjecteTipusEnumDto.DADA,
						LogTipusEnumDto.CREACIO,
						dadaCodi,
						dada.getValorComString(),
						false,
						false);
			}
			
			if (node instanceof ExpedientEntity && isPropagarMetadadesActiu() && metaDada.isEnviable()) {
				// Obtenir nom domini per guardar a l'arxiu per metadades 'enviables'
				if (metaDada.getTipus().equals(MetaDadaTipusEnumDto.DOMINI)) {
					DominiDto domini = dominiService.findByCodiAndEntitat(metaDada.getCodi(), entitatId);
					
					ResultatConsultaDto resultat = dominiService.getSelectedDomini(entitatId, domini, dada.getValorComString());
					pluginHelper.arxiuExpedientMetadadesActualitzar((ExpedientEntity)node, metaDada, resultat.getId());
				} else {
					pluginHelper.arxiuExpedientMetadadesActualitzar((ExpedientEntity)node, metaDada, dada.getValorComString());
				}
			}
		}
	}



	// Mètodes per evitar errors al tenir continguts orfes en base de dades
	// ////////////////////////////////////////////////////////////////////

	@Override
	@Transactional
//	@Scheduled(cron = "0 0 5 * * ?")
	public Boolean netejaContingutsOrfes() {
		try {
			contingutRepositoryHelper.deleteContingutsOrfes();
			return true;
		} catch (Exception ex) {
			logger.error("No s'han pogut eliminar els continguts orfes.", ex);
		}
		return false;
	}

    @Override
    public ResultDocumentsSenseContingut arreglaDocumentsSenseContingut() {
		ResultDocumentsSenseContingut result = ResultDocumentsSenseContingut.builder().build();

//		List<Long> idsAnnexEsborranysAmbDocument = registreAnnexRepository.findIdsEsborranysAmbDocument();
		List<Long> idsAnnexEsborranysAmbDocument = Arrays.asList(2124706L, 2124707L, 2124767L, 2124768L, 2124771L, 2124776L, 2124783L, 2124784L, 2130635L, 2130636L, 2130637L, 2130638L, 2130639L, 2121164L, 2106239L, 2125312L, 2121243L, 2121256L, 2121263L, 2121343L, 2121345L, 2121348L, 2121361L, 2130992L, 2131025L, 2121380L, 2122778L, 2122779L, 2122821L, 2122837L, 2121395L, 2122311L, 2122312L, 2122313L, 2150632L);

		logger.info("[DOCS_SENSE_CONT] Detectats {} esborranys amb document associat.", idsAnnexEsborranysAmbDocument.size());
		for (Long annexId : idsAnnexEsborranysAmbDocument) {
			logger.info("[DOCS_SENSE_CONT] Processant annex: {}", annexId);
			ResultDocumentSenseContingut resultat = contingutHelper.arreglaDocumentSenseContingut(annexId);
			result.addResultDocument(resultat);
			logger.info("[DOCS_SENSE_CONT] Resultat del procés: {}", resultat.toString());
		}

		return result;
    }

	public boolean isPropagarMetadadesActiu() {
		return configHelper.getAsBoolean("es.caib.ripea.expedient.propagar.metadades");
	}
	
	private List<String> getOrgansAmbNoms(List<String> organsCodis) {
		List<String> organsCodisNoms = new ArrayList<>();
		if (Utils.isNotEmpty(organsCodis)) {
			for (String organCodi : organsCodis) {
				OrganGestorEntity organ = organGestorRepository.findByCodi(organCodi);
				if (organ != null) {
					organsCodisNoms.add(organ.getCodiINom());
				} else {
					organsCodisNoms.add(organCodi);
				}
			}
		}

		return organsCodisNoms;
	}
	
    private static final Logger logger = LoggerFactory.getLogger(ContingutServiceImpl.class);

}
