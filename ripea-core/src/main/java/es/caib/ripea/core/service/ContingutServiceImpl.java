/**
 * 
 */
package es.caib.ripea.core.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.caib.plugins.arxiu.api.Carpeta;
import es.caib.plugins.arxiu.api.ContingutArxiu;
import es.caib.plugins.arxiu.api.Document;
import es.caib.plugins.arxiu.api.DocumentMetadades;
import es.caib.plugins.arxiu.api.ExpedientMetadades;
import es.caib.plugins.arxiu.api.Firma;
import es.caib.ripea.core.api.dto.AnotacioRegistreFiltreDto;
import es.caib.ripea.core.api.dto.ArxiuContingutDto;
import es.caib.ripea.core.api.dto.ArxiuContingutTipusEnumDto;
import es.caib.ripea.core.api.dto.ArxiuDetallDto;
import es.caib.ripea.core.api.dto.ArxiuFirmaDto;
import es.caib.ripea.core.api.dto.ArxiuFirmaPerfilEnumDto;
import es.caib.ripea.core.api.dto.ArxiuFirmaTipusEnumDto;
import es.caib.ripea.core.api.dto.ContingutDto;
import es.caib.ripea.core.api.dto.ContingutFiltreDto;
import es.caib.ripea.core.api.dto.ContingutLogDetallsDto;
import es.caib.ripea.core.api.dto.ContingutLogDto;
import es.caib.ripea.core.api.dto.ContingutMassiuFiltreDto;
import es.caib.ripea.core.api.dto.ContingutMovimentDto;
import es.caib.ripea.core.api.dto.ContingutTipusEnumDto;
import es.caib.ripea.core.api.dto.DocumentDto;
import es.caib.ripea.core.api.dto.DocumentEstatEnumDto;
import es.caib.ripea.core.api.dto.DocumentNtiEstadoElaboracionEnumDto;
import es.caib.ripea.core.api.dto.DocumentNtiTipoDocumentalEnumDto;
import es.caib.ripea.core.api.dto.DocumentTipusEnumDto;
import es.caib.ripea.core.api.dto.ExpedientEstatEnumDto;
import es.caib.ripea.core.api.dto.FitxerDto;
import es.caib.ripea.core.api.dto.LogObjecteTipusEnumDto;
import es.caib.ripea.core.api.dto.LogTipusEnumDto;
import es.caib.ripea.core.api.dto.NtiOrigenEnumDto;
import es.caib.ripea.core.api.dto.PaginaDto;
import es.caib.ripea.core.api.dto.PaginacioParamsDto;
import es.caib.ripea.core.api.dto.RegistreAnotacioDto;
import es.caib.ripea.core.api.dto.ValidacioErrorDto;
import es.caib.ripea.core.api.exception.ConteDocumentsDefinitiusException;
import es.caib.ripea.core.api.exception.NotFoundException;
import es.caib.ripea.core.api.exception.ValidationException;
import es.caib.ripea.core.api.service.ContingutService;
import es.caib.ripea.core.entity.CarpetaEntity;
import es.caib.ripea.core.entity.ContingutEntity;
import es.caib.ripea.core.entity.ContingutMovimentEntity;
import es.caib.ripea.core.entity.DadaEntity;
import es.caib.ripea.core.entity.DocumentEntity;
import es.caib.ripea.core.entity.EntitatEntity;
import es.caib.ripea.core.entity.ExpedientEntity;
import es.caib.ripea.core.entity.MetaDadaEntity;
import es.caib.ripea.core.entity.MetaNodeEntity;
import es.caib.ripea.core.entity.NodeEntity;
import es.caib.ripea.core.entity.RegistreEntity;
import es.caib.ripea.core.entity.UsuariEntity;
import es.caib.ripea.core.helper.CacheHelper;
import es.caib.ripea.core.helper.ContingutHelper;
import es.caib.ripea.core.helper.ContingutLogHelper;
import es.caib.ripea.core.helper.DocumentHelper;
import es.caib.ripea.core.helper.EntityComprovarHelper;
import es.caib.ripea.core.helper.HibernateHelper;
import es.caib.ripea.core.helper.PaginacioHelper;
import es.caib.ripea.core.helper.PaginacioHelper.Converter;
import es.caib.ripea.core.helper.PluginHelper;
import es.caib.ripea.core.helper.PropertiesHelper;
import es.caib.ripea.core.repository.AlertaRepository;
import es.caib.ripea.core.repository.ContingutRepository;
import es.caib.ripea.core.repository.DadaRepository;
import es.caib.ripea.core.repository.DocumentRepository;
import es.caib.ripea.core.repository.MetaDadaRepository;
import es.caib.ripea.core.repository.MetaNodeRepository;
import es.caib.ripea.core.repository.RegistreRepository;
import es.caib.ripea.core.repository.UsuariRepository;
import es.caib.ripea.plugin.arxiu.ArxiuContingutTipusEnum;
import es.caib.ripea.plugin.arxiu.ArxiuDocumentContingut;

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
	private RegistreRepository registreRepository;

	@Autowired
	PaginacioHelper paginacioHelper;
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

	@Transactional
	@Override
	public ContingutDto rename(
			Long entitatId,
			Long contingutId,
			String nom) {
		logger.debug("Canviant el nom del contingut ("
				+ "entitatId=" + entitatId + ", "
				+ "contingutId=" + contingutId + ", "
				+ "nom=" + nom + ")");
		ContingutEntity contingut = contingutHelper.comprovarContingutDinsExpedientModificable(
				entitatId,
				contingutId,
				false,
				true,
				false,
				false);
		contingutHelper.comprovarNomValid(
				contingut.getPare(),
				nom,
				contingutId,
				ContingutEntity.class);
		contingut.update(nom);
		contingutLogHelper.log(
				contingut,
				LogTipusEnumDto.MODIFICACIO,
				nom,
				null,
				true,
				true);
		return contingutHelper.toContingutDto(
				contingut,
				true,
				true,
				true,
				false,
				false,
				false,
				false);
	}

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
				false);
		// Esborra les dades no especificades
		for (DadaEntity dada: dadaRepository.findByNode(node)) {
			if (!valors.keySet().contains(dada.getMetaDada().getCodi())) {
				dadaRepository.delete(dada);
			}
		}
		// Modifica les dades existents
		for (String dadaCodi: valors.keySet()) {
			nodeDadaGuardar(
					node,
					dadaCodi,
					valors.get(dadaCodi));
		}
		cacheHelper.evictErrorsValidacioPerNode(node);
	}

	@Transactional
	@Override
	@CacheEvict(value = "errorsValidacioNode", key = "#contingutId")
	public ContingutDto deleteReversible(
			Long entitatId,
			Long contingutId) throws IOException {
		logger.debug("Esborrant el contingut ("
				+ "entitatId=" + entitatId + ", "
				+ "contingutId=" + contingutId + ")");
		ContingutEntity contingut = contingutHelper.comprovarContingutDinsExpedientModificable(
				entitatId,
				contingutId,
				false,
				false,
				false,
				true);
		ContingutDto dto = contingutHelper.toContingutDto(
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
			logger.error("Aquest contingut ja està esborrat (contingutId=" + contingutId + ")");
			throw new ValidationException(
					contingutId,
					ContingutEntity.class,
					"Aquest contingut ja està esborrat");
		}
		// Valida si conté documents definitius
		if (conteDocumentsDefinitius(contingut)) {
			throw new ConteDocumentsDefinitiusException(
					contingutId,
					ContingutEntity.class);
		}
		// Marca el contingut i tots els seus fills com a esborrats
		//  de forma recursiva
		marcarEsborrat(contingut);
		// Si el contingut és un document guarda una còpia del fitxer esborrat
		// per a poder recuperar-lo posteriorment
		if (contingut instanceof DocumentEntity) {
			DocumentEntity document = (DocumentEntity)contingut;
			if (DocumentTipusEnumDto.DIGITAL.equals(document.getDocumentTipus())) {
				fitxerDocumentEsborratGuardar((DocumentEntity)contingut);
			}
		}
		// Propaga l'acció a l'arxiu
		contingutHelper.arxiuPropagarEliminacio(contingut);
		return dto;
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
				false);
		ContingutEntity contingut = entityComprovarHelper.comprovarContingut(
				entitat,
				contingutId);
		// No es comproven permisos perquè això només ho pot fer l'administrador
		ContingutDto dto = contingutHelper.toContingutDto(
				contingut,
				true,
				false,
				false,
				false,
				false,
				false,
				false);
		if (contingut.getPare() != null) {
			contingut.getPare().getFills().remove(contingut);
		}
		contingutRepository.delete(contingut);
//		// Propaga l'acció a l'arxiu
//		contingutHelper.arxiuPropagarEliminacio(contingut);
//		// Registra al log l'eliminació definitiva del contingut
//		contingutLogHelper.log(
//				contingut,
//				LogTipusEnumDto.ELIMINACIODEF,
//				null,
//				null,
//				true,
//				true);
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
				false);
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
//		if (contingut.getPare() == null) {
//			logger.error("Aquest contingut no te pare (contingutId=" + contingutId + ")");
//			throw new ValidationException(
//					contingutId,
//					ContingutEntity.class,
//					"Aquest contingut no te pare");
//		}
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
		// Recupera el contingut esborrat
		contingut.updateEsborrat(0);
		ContingutDto dto = contingutHelper.toContingutDto(
				contingut,
				true,
				false,
				false,
				false,
				false,
				false,
				false);
		// Registra al log la recuperació del contingut
		contingutLogHelper.log(
				contingut,
				LogTipusEnumDto.RECUPERACIO,
				null,
				null,
				true,
				true);
		// Propaga l'acció a l'arxiu
		FitxerDto fitxer = null;
		if (contingut instanceof DocumentEntity) {
			DocumentEntity document = (DocumentEntity)contingut;
			if (DocumentTipusEnumDto.DIGITAL.equals(document.getDocumentTipus())) {
				fitxer = fitxerDocumentEsborratLlegir((DocumentEntity)contingut);
			}
		}
		contingutHelper.arxiuPropagarModificacio(
				contingut,
				null,
				false,
				false,
				null);
		if (fitxer != null) {
			fitxerDocumentEsborratEsborrar((DocumentEntity)contingut);
		}
		return dto;
	}

	@Transactional
	@Override
	public ContingutDto move(
			Long entitatId,
			Long contingutOrigenId,
			Long contingutDestiId) {
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
				true);
		ContingutEntity contingutDesti = contingutHelper.comprovarContingutDinsExpedientModificable(
				entitatId,
				contingutDestiId,
				false,
				false,
				false,
				false);
		// Comprova el tipus del contingut que es vol moure
		if (!(contingutOrigen instanceof DocumentEntity)) {
			throw new ValidationException(
					contingutOrigenId,
					contingutOrigen.getClass(),
					"Només es poden moure documents");
		}
		// No es poden moure documents firmats
		if (contingutOrigen instanceof DocumentEntity) {
			DocumentEntity documentOrigen = (DocumentEntity)contingutOrigen;
			if (documentOrigen.isFirmat()) {
				throw new ValidationException(
						contingutOrigenId,
						contingutOrigen.getClass(),
						"No es poden moure documents firmats");
			}
		}
		// Es comprova que es poden crear elements d'aquest tipus a l'expedient destí
		if (contingutOrigen instanceof DocumentEntity) {
			DocumentEntity documentOrigen = (DocumentEntity)contingutOrigen;
			entityComprovarHelper.comprovarPermisosMetaNode(
					documentOrigen.getMetaDocument(),
					documentOrigen.getId(),
					false,
					false,
					true,
					false);
		}
		// Es comprova que el tipus d'expedient orígen i destí son el mateix
		ExpedientEntity expedientOrigen = contingutHelper.getExpedientSuperior(
				contingutOrigen,
				true,
				false,
				false);
		ExpedientEntity expedientDesti = contingutHelper.getExpedientSuperior(
				contingutDesti,
				true,
				false,
				false);
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
				contingutOrigen,
				true,
				false,
				false,
				false,
				false,
				false,
				false);
		contingutHelper.arxiuPropagarMoviment(
				contingutOrigen,
				contingutDesti,
				expedientDesti.getArxiuUuid());
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
				false);
		ContingutEntity contingutDesti = contingutHelper.comprovarContingutDinsExpedientModificable(
				entitatId,
				contingutDestiId,
				false,
				false,
				false,
				false);
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
		// Es comprova que es poden crear elements d'aquest tipus a l'expedient destí
		if (contingutOrigen instanceof DocumentEntity) {
			DocumentEntity documentOrigen = (DocumentEntity)contingutOrigen;
			entityComprovarHelper.comprovarPermisosMetaNode(
					documentOrigen.getMetaDocument(),
					documentOrigen.getId(),
					false,
					false,
					true,
					false);
		}
		// Es comprova que el tipus d'expedient orígen i destí son el mateix
		ExpedientEntity expedientOrigen = contingutHelper.getExpedientSuperior(
				contingutOrigen,
				true,
				false,
				false);
		ExpedientEntity expedientDesti = contingutHelper.getExpedientSuperior(
				contingutDesti,
				true,
				false,
				false);
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
				contingutOrigen,
				true,
				false,
				false,
				false,
				false,
				false,
				false);
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
				false);
		ContingutEntity contingutDesti = contingutHelper.comprovarContingutDinsExpedientModificable(
				entitatId,
				contingutDestiId,
				false,
				false,
				false,
				false);
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
		// Es comprova que es poden crear elements d'aquest tipus a l'expedient destí
		if (contingutOrigen instanceof DocumentEntity) {
			DocumentEntity documentOrigen = (DocumentEntity)contingutOrigen;
			entityComprovarHelper.comprovarPermisosMetaNode(
					documentOrigen.getMetaDocument(),
					documentOrigen.getId(),
					false,
					false,
					true,
					false);
		}
		// Es comprova que el tipus d'expedient orígen i destí son el mateix
		ExpedientEntity expedientOrigen = contingutHelper.getExpedientSuperior(
				contingutOrigen,
				true,
				false,
				false);
		ExpedientEntity expedientDesti = contingutHelper.getExpedientSuperior(
				contingutDesti,
				true,
				false,
				false);
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
				contingutOrigen,
				true,
				false,
				false,
				false,
				false,
				false,
				false);
		return dto;
	}
	
	@Transactional(readOnly = true)
	@Override
	public ContingutDto findAmbIdUser(
			Long entitatId,
			Long contingutId,
			boolean ambFills,
			boolean ambVersions) {
		logger.debug("Obtenint contingut amb id per usuari ("
				+ "entitatId=" + entitatId + ", "
				+ "contingutId=" + contingutId + ", "
				+ "ambFills=" + ambFills + ", "
				+ "ambVersions=" + ambVersions + ")");
		ContingutEntity contingut = contingutHelper.comprovarContingutDinsExpedientAccessible(
				entitatId,
				contingutId,
				true,
				false);
		ContingutDto dto = contingutHelper.toContingutDto(
				contingut,
				true,
				ambFills,
				ambFills,
				true,
				true,
				true,
				ambVersions);
		dto.setAlerta(alertaRepository.countByLlegidaAndContingutId(
				false,
				dto.getId()) > 0);

		return dto;
	}

	@Transactional(readOnly = true)
	@Override
	public ContingutDto findAmbIdAdmin(
			Long entitatId,
			Long contingutId,
			boolean ambFills) {
		logger.debug("Obtenint contingut amb id per admin ("
				+ "entitatId=" + entitatId + ", "
				+ "contingutId=" + contingutId + ", "
				+ "ambFills=" + ambFills + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				true,
				false,
				false);
		ContingutEntity contingut = entityComprovarHelper.comprovarContingut(
				entitat,
				contingutId);
		return contingutHelper.toContingutDto(
				contingut,
				true,
				ambFills,
				ambFills,
				true,
				true,
				false,
				true);
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
		// TODO
		/*EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				true,
				false,
				false);
		EscriptoriEntity escriptori = escriptoriRepository.findByEntitatAndUsuari(
				entitat,
				usuariHelper.getUsuariAutenticat());
		ContingutEntity contingutActual = escriptori;
		if (!path.isEmpty() && !path.equals("/")) {
			String[] pathParts;
			if (path.startsWith("/")) {
				pathParts = path.substring(1).split("/");
			} else {
				pathParts = path.split("/");
			}
			for (String pathPart: pathParts) {
				Long idActual = contingutActual.getId();
				contingutActual = contingutRepository.findByPareAndNomAndEsborrat(
						contingutActual,
						pathPart,
						0);
				if (contingutActual == null) {
					logger.error("No s'ha trobat el contingut (pareId=" + idActual + ", nom=" + pathPart + ")");
					throw new NotFoundException(
							"(pareId=" + idActual + ", nom=" + pathPart + ")",
							ContingutEntity.class);
				}
				// Si el contingut actual és un document ens aturam
				// perquè el següent element del path serà la darrera
				// versió i no la trobaría com a contingut.
				if (contingutActual instanceof DocumentEntity)
					break;
			}
		}
		// Comprova que el contingut arrel és l'escriptori de l'usuari actual
		contingutHelper.comprovarContingutArrelEsEscriptoriUsuariActual(
				entitat,
				contingutActual);
		// Comprova l'accés al path del contingut
		contingutHelper.comprovarPermisosPathContingut(
				contingutActual,
				true,
				false,
				false,
				true);
		return contingutHelper.toContingutDto(
				contingutActual,
				true,
				true,
				true,
				true,
				true,
				false,
				true);*/
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
				false);
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
				false);
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
				true,
				false,
				false);
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
				true);
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
				false);
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
		boolean tipusRegistre = true;
		if (filtre.getTipus() != null) {
			tipusCarpeta = false;
			tipusDocument = false;
			tipusRegistre = false;
			switch (filtre.getTipus()) {
			case CARPETA:
				tipusCarpeta = true;
				break;
			case DOCUMENT:
				tipusDocument = true;
				break;
			case EXPEDIENT:
				tipusExpedient = true;
				break;
			case REGISTRE:
				tipusRegistre = true;
				break;
			}
		}
		Date dataCreacioInici = toDateInicialDia(filtre.getDataCreacioInici());
		Date dataCreacioFi = toDateFinalDia(filtre.getDataCreacioFi());
		return paginacioHelper.toPaginaDto(
				contingutRepository.findByFiltrePaginat(
						entitat,
						tipusCarpeta,
						tipusDocument,
						tipusExpedient,
						(filtre.getNom() == null),
						filtre.getNom(),
						(metaNode == null),
						metaNode,
						(dataCreacioInici == null),
						dataCreacioInici,
						(dataCreacioFi == null),
						dataCreacioFi,
						filtre.isMostrarEsborrats(),
						filtre.isMostrarNoEsborrats(),
						paginacioHelper.toSpringDataPageable(paginacioParams)),
				ContingutDto.class,
				new Converter<ContingutEntity, ContingutDto>() {
					@Override
					public ContingutDto convert(ContingutEntity source) {
						return contingutHelper.toContingutDto(
								source,
								false,
								false,
								false,
								false,
								true,
								false,
								false);
					}
				});
	}
	


	@Transactional(readOnly = true)
	@Override
	public PaginaDto<ContingutDto> findEsborrats(
			Long entitatId,
			String nom,
			String usuariCodi,
			Date dataInici,
			Date dataFi,
			PaginacioParamsDto paginacioParams) {
		logger.debug("Obtenint elements esborrats ("
				+ "entitatId=" + entitatId + ", "
				+ "nom=" + nom + ", "
				+ "usuariCodi=" + usuariCodi + ", "
				+ "dataInici=" + dataInici + ", "
				+ "dataFi=" + dataFi + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);
		UsuariEntity usuari = null;
		if (usuariCodi != null && !usuariCodi.isEmpty()) {
			usuari = usuariRepository.findOne(usuariCodi);
			if (usuari == null) {
				logger.error("No s'ha trobat l'usuari (codi=" + usuariCodi + ")");
				throw new NotFoundException(
						usuariCodi,
						UsuariEntity.class);
			}
		}
		return paginacioHelper.toPaginaDto(
				contingutRepository.findEsborratsByFiltrePaginat(
						entitat,
						(nom == null),
						(nom != null) ? '%' + nom + '%' : nom,
						(usuari == null),
						usuari,
						(dataInici == null),
						toDateInicialDia(dataInici),
						(dataFi == null),
						toDateFinalDia(dataFi),
						paginacioHelper.toSpringDataPageable(paginacioParams)),
				ContingutDto.class,
				new Converter<ContingutEntity, ContingutDto>() {
					@Override
					public ContingutDto convert(ContingutEntity source) {
						return contingutHelper.toContingutDto(
								source,
								false,
								false,
								false,
								false,
								false,
								false,
								false);
					}
				});
	}

	@Transactional(readOnly = true)
	@Override
	public ArxiuDetallDto getArxiuDetall(
			Long entitatId,
			Long contingutId) {
		logger.debug("Obtenint informació de l'arxiu pel contingut ("
				+ "entitatId=" + entitatId + ", "
				+ "contingutId=" + contingutId + ")");
		ContingutEntity contingut = contingutHelper.comprovarContingutDinsExpedientAccessible(
				entitatId,
				contingutId,
				true,
				false);
		List<ContingutArxiu> continguts = null;
		List<Firma> firmes = null;
		ArxiuDetallDto arxiuDetall = new ArxiuDetallDto();
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
				arxiuDetall.setEniOrgans(metadades.getOrgans());
				arxiuDetall.setMetadadesAddicionals(metadades.getMetadadesAddicionals());
			}
		} else if (contingut instanceof DocumentEntity) {
			Document arxiuDocument = pluginHelper.arxiuDocumentConsultar(
					contingut,
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
				arxiuDetall.setEniDataCaptura(metadades.getDataCaptura());
				if (metadades.getOrigen() != null) {
					switch (metadades.getOrigen()) {
					case CIUTADA:
						arxiuDetall.setEniOrigen(NtiOrigenEnumDto.O0);
						break;
					case ADMINISTRACIO:
						arxiuDetall.setEniOrigen(NtiOrigenEnumDto.O1);
						break;
					}
				}
				if (metadades.getEstatElaboracio() != null) {
					switch (metadades.getEstatElaboracio()) {
					case ORIGINAL:
						arxiuDetall.setEniEstatElaboracio(DocumentNtiEstadoElaboracionEnumDto.EE01);
						break;
					case COPIA_CF:
						arxiuDetall.setEniEstatElaboracio(DocumentNtiEstadoElaboracionEnumDto.EE02);
						break;
					case COPIA_DP:
						arxiuDetall.setEniEstatElaboracio(DocumentNtiEstadoElaboracionEnumDto.EE03);
						break;
					case COPIA_PR:
						arxiuDetall.setEniEstatElaboracio(DocumentNtiEstadoElaboracionEnumDto.EE04);
						break;
					case ALTRES:
						arxiuDetall.setEniEstatElaboracio(DocumentNtiEstadoElaboracionEnumDto.EE99);
						break;
					}
				}
				if (metadades.getTipusDocumental() != null) {
					switch (metadades.getTipusDocumental()) {
					case RESOLUCIO:
						arxiuDetall.setEniTipusDocumental(DocumentNtiTipoDocumentalEnumDto.TD01);
						break;
					case ACORD:
						arxiuDetall.setEniTipusDocumental(DocumentNtiTipoDocumentalEnumDto.TD02);
						break;
					case CONTRACTE:
						arxiuDetall.setEniTipusDocumental(DocumentNtiTipoDocumentalEnumDto.TD03);
						break;
					case CONVENI:
						arxiuDetall.setEniTipusDocumental(DocumentNtiTipoDocumentalEnumDto.TD04);
						break;
					case DECLARACIO:
						arxiuDetall.setEniTipusDocumental(DocumentNtiTipoDocumentalEnumDto.TD05);
						break;
					case COMUNICACIO:
						arxiuDetall.setEniTipusDocumental(DocumentNtiTipoDocumentalEnumDto.TD06);
						break;
					case NOTIFICACIO:
						arxiuDetall.setEniTipusDocumental(DocumentNtiTipoDocumentalEnumDto.TD07);
						break;
					case PUBLICACIO:
						arxiuDetall.setEniTipusDocumental(DocumentNtiTipoDocumentalEnumDto.TD08);
						break;
					case JUSTIFICANT_RECEPCIO:
						arxiuDetall.setEniTipusDocumental(DocumentNtiTipoDocumentalEnumDto.TD09);
						break;
					case ACTA:
						arxiuDetall.setEniTipusDocumental(DocumentNtiTipoDocumentalEnumDto.TD10);
						break;
					case CERTIFICAT:
						arxiuDetall.setEniTipusDocumental(DocumentNtiTipoDocumentalEnumDto.TD11);
						break;
					case DILIGENCIA:
						arxiuDetall.setEniTipusDocumental(DocumentNtiTipoDocumentalEnumDto.TD12);
						break;
					case INFORME:
						arxiuDetall.setEniTipusDocumental(DocumentNtiTipoDocumentalEnumDto.TD13);
						break;
					case SOLICITUD:
						arxiuDetall.setEniTipusDocumental(DocumentNtiTipoDocumentalEnumDto.TD14);
						break;
					case DENUNCIA:
						arxiuDetall.setEniTipusDocumental(DocumentNtiTipoDocumentalEnumDto.TD15);
						break;
					case ALEGACIO:
						arxiuDetall.setEniTipusDocumental(DocumentNtiTipoDocumentalEnumDto.TD16);
						break;
					case RECURS:
						arxiuDetall.setEniTipusDocumental(DocumentNtiTipoDocumentalEnumDto.TD17);
						break;
					case COMUNICACIO_CIUTADA:
						arxiuDetall.setEniTipusDocumental(DocumentNtiTipoDocumentalEnumDto.TD18);
						break;
					case FACTURA:
						arxiuDetall.setEniTipusDocumental(DocumentNtiTipoDocumentalEnumDto.TD19);
						break;
					case ALTRES_INCAUTATS:
						arxiuDetall.setEniTipusDocumental(DocumentNtiTipoDocumentalEnumDto.TD20);
						break;
					case ALTRES:
						arxiuDetall.setEniTipusDocumental(DocumentNtiTipoDocumentalEnumDto.TD99);
						break;
					}
				}
				arxiuDetall.setEniOrgans(metadades.getOrgans());
				if (metadades.getFormat() != null) {
					arxiuDetall.setEniFormat(metadades.getFormat().toString());
				}
				if (metadades.getExtensio() != null) {
					arxiuDetall.setEniExtensio(metadades.getExtensio().toString());
				}
				arxiuDetall.setEniDocumentOrigenId(metadades.getIdentificadorOrigen());
				arxiuDetall.setMetadadesAddicionals(metadades.getMetadadesAddicionals());
				if (arxiuDocument.getContingut() != null) {
					arxiuDetall.setContingutArxiuNom(
							arxiuDocument.getContingut().getArxiuNom());
					arxiuDetall.setContingutTipusMime(
							arxiuDocument.getContingut().getTipusMime());
				}
				
			}
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
	public PaginaDto<DocumentDto> documentMassiuFindAmbFiltre(
			Long entitatId, 
			ContingutMassiuFiltreDto filtre,
			PaginacioParamsDto paginacioParams) throws NotFoundException {
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				true,
				false,
				false);
		Long idMetaNode = null;
		MetaNodeEntity metaNode = null;
		if (filtre.getTipusElement() == ContingutTipusEnumDto.EXPEDIENT && filtre.getTipusExpedient() != null) {
			idMetaNode = filtre.getTipusExpedient();
		} else if (filtre.getTipusElement() == ContingutTipusEnumDto.DOCUMENT && filtre.getTipusDocument() != null) {
			idMetaNode = filtre.getTipusDocument();
		}
		if (idMetaNode != null) {
			metaNode = metaNodeRepository.findOne(idMetaNode);
			if (metaNode == null) {
				throw new NotFoundException(
						idMetaNode,
						MetaNodeEntity.class);
			}
		}
		Date dataInici = toDateInicialDia(filtre.getDataInici());
		Date dataFi = toDateFinalDia(filtre.getDataFi());
		List<DocumentEntity> preDocuments = documentRepository.findDocumentMassiuByFiltre(
				entitat,
				(filtre.getTipusExpedient() == null),
				filtre.getTipusExpedient(),
				(filtre.getExpedientId() == null),
				filtre.getExpedientId(),
				(filtre.getTipusDocument() == null),
				filtre.getTipusDocument(),
				(filtre.getNom() == null),
				filtre.getNom(),
				(dataInici == null),
				dataInici,
				(dataFi == null),
				dataFi,
				false,
				true);
		List<Long> docIds = new ArrayList<Long>();
		for (DocumentEntity document: preDocuments) {
			docIds.add(document.getId());
		}
		if (!docIds.isEmpty()) {
			return paginacioHelper.toPaginaDto(
					documentRepository.findDocumentMassiuByIdsPaginat(
							docIds,
							paginacioHelper.toSpringDataPageable(paginacioParams)),
					DocumentDto.class,
					new Converter<DocumentEntity, DocumentDto>() {
						@Override
						public DocumentDto convert(DocumentEntity source) {
							DocumentDto dto = (DocumentDto)contingutHelper.toContingutDto(
									source,
									false,
									false,
									false,
									false,
									true,
									true,
									false);
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
	public List<Long> findIdsMassiusAmbFiltre(
			Long entitatId,
			ContingutMassiuFiltreDto filtre) throws NotFoundException {
		logger.debug("Consultant els ids d'expedient segons el filtre ("
				+ "entitatId=" + entitatId + ", "
				+ "filtre=" + filtre + ")");
		entityComprovarHelper.comprovarEntitat(
				entitatId,
				true,
				false,
				false);
		return findIdsAmbFiltrePaginat(
				entitatId,
				filtre);
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

	private List<Long> findIdsAmbFiltrePaginat(
			Long entitatId,
			ContingutMassiuFiltreDto filtre) {
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				true,
				false,
				false);
		Long idMetaNode = null;
		MetaNodeEntity metaNode = null;
		if (filtre.getTipusElement() == ContingutTipusEnumDto.EXPEDIENT && filtre.getTipusExpedient() != null)
			idMetaNode = filtre.getTipusExpedient();
		else if (filtre.getTipusElement() == ContingutTipusEnumDto.DOCUMENT && filtre.getTipusDocument() != null)
			idMetaNode = filtre.getTipusDocument();
		
		if (idMetaNode != null) {
			metaNode = metaNodeRepository.findOne(idMetaNode);
			if (metaNode == null) {
				throw new NotFoundException(
						idMetaNode,
						MetaNodeEntity.class);
			}
		}
		Date dataInici = toDateInicialDia(filtre.getDataInici());
		Date dataFi = toDateFinalDia(filtre.getDataFi());
		return documentRepository.findIdMassiuByEntitatAndFiltre(
				entitat,
				(filtre.getTipusExpedient() == null),
				filtre.getTipusExpedient(),
				(filtre.getExpedientId() == null),
				filtre.getExpedientId(),
				(filtre.getTipusDocument() == null),
				filtre.getTipusDocument(),
				(filtre.getNom() == null),
				filtre.getNom(),
				(dataInici == null),
				dataInici,
				(dataFi == null),
				dataFi,
				false,
				true);
	}

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
			creat = documentHelper.crearNouDocument(
					documentOrigen.getDocumentTipus(),
					documentOrigen.getNom(),
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
					documentOrigen.getNtiIdDocumentoOrigen());
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
			creat = documentHelper.crearNouDocument(
					documentOrigen.getDocumentTipus(),
					documentOrigen.getNom(),
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
					uuidDocumentoOrigen);
		}
		if (creat != null) {
			if (creat instanceof DocumentEntity) {
				DocumentEntity documentOrigen = (DocumentEntity)contingutOrigen;		
				creat.updateArxiu(uuidDocumentoOrigen);
				creat.updateExpedient((ExpedientEntity)contingutDesti);
				creat.updatePare(contingutDesti);
				((DocumentEntity) creat).updateFitxer(
						documentOrigen.getFitxerNom(), 
						documentOrigen.getFitxerContentType(), 
						null);
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
			Object dadaValor) {
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
		}
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
			conteDefinitius = !DocumentEstatEnumDto.REDACCIO.equals(document.getEstat());
		}
		return conteDefinitius;
	}

	private void marcarEsborrat(ContingutEntity contingut) {
		for (ContingutEntity contingutFill: contingut.getFills()) {
			marcarEsborrat(contingutFill);
		}
		List<ContingutEntity> continguts = contingutRepository.findByPareAndNomOrderByEsborratAsc(
				contingut.getPare(),
				contingut.getNom());
		// Per evitar errors de restricció única violada hem de
		// posar al camp esborrat un nombre != 0 i que sigui diferent
		// dels altres fills esborrats amb el mateix nom.
		int index = 1;
		for (ContingutEntity c: continguts) {
			if (c.getEsborrat() > 0) {
				if (index < c.getEsborrat()) {
					break;
				}
				index++;
			}
		}
		contingut.updateEsborrat(continguts.size() + 1);
		contingutLogHelper.log(
				contingut,
				LogTipusEnumDto.ELIMINACIO,
				null,
				null,
				true,
				true);
	}

	private void fitxerDocumentEsborratGuardar(
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

	private FitxerDto fitxerDocumentEsborratLlegir(
			DocumentEntity document) throws IOException {
		File fContent = new File(getBaseDir() + "/" + document.getId());
		fContent.getParentFile().mkdirs();
		if (fContent.exists()) {
			FileInputStream inContent = new FileInputStream(fContent);
			byte fileContent[] = new byte[(int)fContent.length()];
			inContent.read(fileContent);
			inContent.close();
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

	private void fitxerDocumentEsborratEsborrar(
			DocumentEntity document) {
		File fContent = new File(getBaseDir() + "/" + document.getId());
		fContent.getParentFile().mkdirs();
		fContent.delete();
	}

	private Date toDateInicialDia(Date data) {
		if (data == null) {
			return null;
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(data);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}
	private Date toDateFinalDia(Date data) {
		if (data == null) {
			return null;
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(data);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		cal.set(Calendar.MILLISECOND, 999);
		return cal.getTime();
	}

	private String getBaseDir() {
		return PropertiesHelper.getProperties().getProperty("es.caib.ripea.app.data.dir") + "/esborrats-tmp";
	}

	private static final Logger logger = LoggerFactory.getLogger(ContingutServiceImpl.class);

}
