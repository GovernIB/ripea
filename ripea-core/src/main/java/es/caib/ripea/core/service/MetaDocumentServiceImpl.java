/**
 * 
 */
package es.caib.ripea.core.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.caib.ripea.core.api.dto.ContingutTipusEnumDto;
import es.caib.ripea.core.api.dto.FitxerDto;
import es.caib.ripea.core.api.dto.MetaDocumentDto;
import es.caib.ripea.core.api.dto.MultiplicitatEnumDto;
import es.caib.ripea.core.api.dto.PaginaDto;
import es.caib.ripea.core.api.dto.PaginacioParamsDto;
import es.caib.ripea.core.api.dto.PortafirmesDocumentTipusDto;
import es.caib.ripea.core.api.service.MetaDocumentService;
import es.caib.ripea.core.entity.ContingutEntity;
import es.caib.ripea.core.entity.DocumentEntity;
import es.caib.ripea.core.entity.EntitatEntity;
import es.caib.ripea.core.entity.ExpedientEntity;
import es.caib.ripea.core.entity.MetaDocumentEntity;
import es.caib.ripea.core.entity.MetaExpedientEntity;
import es.caib.ripea.core.helper.ContingutHelper;
import es.caib.ripea.core.helper.ConversioTipusHelper;
import es.caib.ripea.core.helper.EntityComprovarHelper;
import es.caib.ripea.core.helper.MetaNodeHelper;
import es.caib.ripea.core.helper.PaginacioHelper;
import es.caib.ripea.core.helper.PermisosHelper;
import es.caib.ripea.core.helper.PluginHelper;
import es.caib.ripea.core.repository.DocumentRepository;
import es.caib.ripea.core.repository.EntitatRepository;
import es.caib.ripea.core.repository.MetaDadaRepository;
import es.caib.ripea.core.repository.MetaDocumentRepository;

/**
 * Implementació del servei de gestió de meta-documents.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class MetaDocumentServiceImpl implements MetaDocumentService {

	@Resource
	private MetaDocumentRepository metaDocumentRepository;
	@Resource
	private EntitatRepository entitatRepository;
	@Resource
	private MetaDadaRepository metaDadaRepository;
	@Resource
	private DocumentRepository documentRepository;

	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	@Resource
	private MetaNodeHelper metaNodeHelper;
	@Resource
	private ContingutHelper contenidorHelper;
	@Resource
	private PaginacioHelper paginacioHelper;
	@Resource
	private PermisosHelper permisosHelper;
	@Resource
	private PluginHelper pluginHelper;
	@Resource
	private EntityComprovarHelper entityComprovarHelper;

	@Transactional
	@Override
	public MetaDocumentDto create(
			Long entitatId,
			Long metaExpedientId,
			MetaDocumentDto metaDocument,
			String plantillaNom,
			String plantillaContentType,
			byte[] plantillaContingut) {
		logger.debug("Creant un nou meta-document (" +
				"entitatId=" + entitatId + ", " +
				"metaExpedientId=" + metaExpedientId + ", " +
				"metaDocument=" + metaDocument + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);
		MetaExpedientEntity metaExpedient = entityComprovarHelper.comprovarMetaExpedient(
				entitat,
				metaExpedientId);
		MetaDocumentEntity entity = MetaDocumentEntity.getBuilder(
				entitat,
				metaDocument.getCodi(),
				metaDocument.getNom(),
				metaDocument.getMultiplicitat(),
				metaExpedient,
				metaDocument.getNtiOrigen(),
				metaDocument.getNtiEstadoElaboracion(),
				metaDocument.getNtiTipoDocumental()).
				firmaPortafirmesActiva(metaDocument.isFirmaPortafirmesActiva()).
				descripcio(metaDocument.getDescripcio()).
				portafirmesDocumentTipus(metaDocument.getPortafirmesDocumentTipus()).
				portafirmesFluxId(metaDocument.getPortafirmesFluxId()).
				portafirmesResponsables(metaDocument.getPortafirmesResponsables()).
				portafirmesFluxTipus(metaDocument.getPortafirmesFluxTipus()).
				portafirmesCustodiaTipus(metaDocument.getPortafirmesCustodiaTipus()).
				firmaPassarelaActiva(metaDocument.isFirmaPassarelaActiva()).
				firmaPassarelaCustodiaTipus(metaDocument.getFirmaPassarelaCustodiaTipus()).
				build();
		if (plantillaContingut != null) {
			entity.updatePlantilla(
					plantillaNom,
					plantillaContentType,
					plantillaContingut);
		}
		return conversioTipusHelper.convertir(
				metaDocumentRepository.save(entity),
				MetaDocumentDto.class);
	}

	@Transactional
	@Override
	public MetaDocumentDto update(
			Long entitatId,
			Long metaExpedientId,
			MetaDocumentDto metaDocument,
			String plantillaNom,
			String plantillaContentType,
			byte[] plantillaContingut) {
		logger.debug("Actualitzant meta-document existent (" +
				"entitatId=" + entitatId + ", " +
				"metaExpedientId=" + metaExpedientId + ", " +
				"metaDocument=" + metaDocument + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);
		MetaExpedientEntity metaExpedient = entityComprovarHelper.comprovarMetaExpedient(
				entitat,
				metaExpedientId);
		MetaDocumentEntity entity = entityComprovarHelper.comprovarMetaDocument(
				entitat,
				metaExpedient,
				metaDocument.getId());
		entity.update(
				metaDocument.getCodi(),
				metaDocument.getNom(),
				metaDocument.getDescripcio(),
				metaDocument.getMultiplicitat(),
				metaDocument.isFirmaPortafirmesActiva(),
				metaDocument.getPortafirmesDocumentTipus(),
				metaDocument.getPortafirmesFluxId(),
				metaDocument.getPortafirmesResponsables(),
				metaDocument.getPortafirmesFluxTipus(),
				metaDocument.getPortafirmesCustodiaTipus(),
				metaDocument.isFirmaPassarelaActiva(),
				metaDocument.getFirmaPassarelaCustodiaTipus(),
				metaDocument.getNtiOrigen(),
				metaDocument.getNtiEstadoElaboracion(),
				metaDocument.getNtiTipoDocumental());
		if (plantillaContingut != null) {
			entity.updatePlantilla(
					plantillaNom,
					plantillaContentType,
					plantillaContingut);
		}
		return conversioTipusHelper.convertir(
				entity,
				MetaDocumentDto.class);
	}

	@Transactional
	@Override
	public MetaDocumentDto updateActiu(
			Long entitatId,
			Long metaExpedientId,
			Long id,
			boolean actiu) {
		logger.debug("Actualitzant propietat activa d'un meta-document existent (" +
				"entitatId=" + entitatId + ", " +
				"metaExpedientId=" + metaExpedientId + ", " +
				"id=" + id + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);
		MetaExpedientEntity metaExpedient = entityComprovarHelper.comprovarMetaExpedient(
				entitat,
				metaExpedientId);
		MetaDocumentEntity metaDocument = entityComprovarHelper.comprovarMetaDocument(
				entitat,
				metaExpedient,
				id);
		metaDocument.updateActiu(actiu);
		return conversioTipusHelper.convertir(
				metaDocument,
				MetaDocumentDto.class);
	}

	@Transactional
	@Override
	public MetaDocumentDto delete(
			Long entitatId,
			Long metaExpedientId,
			Long id) {
		logger.debug("Esborrant meta-document (" +
				"entitatId=" + entitatId + ", " +
				"metaExpedientId=" + metaExpedientId + ", " +
				"id=" + id + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);
		MetaExpedientEntity metaExpedient = entityComprovarHelper.comprovarMetaExpedient(
				entitat,
				metaExpedientId);
		MetaDocumentEntity metaDocument = entityComprovarHelper.comprovarMetaDocument(
				entitat,
				metaExpedient,
				id);
		metaDocumentRepository.delete(metaDocument);
		return conversioTipusHelper.convertir(
				metaDocument,
				MetaDocumentDto.class);
	}

	@Transactional(readOnly = true)
	@Override
	public MetaDocumentDto findById(
			Long entitatId,
			Long metaExpedientId,
			Long id) {
		logger.debug("Consulta del meta-document (" +
				"entitatId=" + entitatId + ", " +
				"metaExpedientId=" + metaExpedientId + ", " +
				"id=" + id + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);
		MetaExpedientEntity metaExpedient = entityComprovarHelper.comprovarMetaExpedient(
				entitat,
				metaExpedientId);
		MetaDocumentEntity metaDocument = entityComprovarHelper.comprovarMetaDocument(
				entitat,
				metaExpedient,
				id);
		MetaDocumentDto resposta = conversioTipusHelper.convertir(
				metaDocument,
				MetaDocumentDto.class);
		if (resposta != null) {
			metaNodeHelper.omplirMetaDadesPerMetaNode(resposta);
			metaNodeHelper.omplirPermisosPerMetaNode(resposta, false);
		}
		return resposta;
	}

	@Transactional(readOnly = true)
	@Override
	public MetaDocumentDto findByCodi(
			Long entitatId,
			Long metaExpedientId,
			String codi) {
		logger.debug("Consulta del meta-document amb codi (" +
				"entitatId=" + entitatId + ", " +
				"metaExpedientId=" + metaExpedientId + ", " +
				"codi=" + codi + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);
		MetaExpedientEntity metaExpedient = entityComprovarHelper.comprovarMetaExpedient(
				entitat,
				metaExpedientId);
		MetaDocumentDto resposta = conversioTipusHelper.convertir(
				metaDocumentRepository.findByMetaExpedientAndCodi(
						metaExpedient,
						codi),
				MetaDocumentDto.class);
		if (resposta != null) {
			metaNodeHelper.omplirMetaDadesPerMetaNode(resposta);
			metaNodeHelper.omplirPermisosPerMetaNode(resposta, false);
		}
		return resposta;
	}

	@Transactional(readOnly = true)
	@Override
	public PaginaDto<MetaDocumentDto> findByMetaExpedient(
			Long entitatId,
			Long metaExpedientId,
			PaginacioParamsDto paginacioParams) {
		logger.debug("Consulta dels meta-documents del meta-expedient (" +
				"entitatId=" + entitatId + ", " +
				"metaExpedientId=" + metaExpedientId + ", " +
				"paginacioParams=" + paginacioParams + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);
		MetaExpedientEntity metaExpedient = entityComprovarHelper.comprovarMetaExpedient(
				entitat,
				metaExpedientId);
		PaginaDto<MetaDocumentDto> resposta;
		if (paginacioHelper.esPaginacioActivada(paginacioParams)) {
			resposta = paginacioHelper.toPaginaDto(
					metaDocumentRepository.findByMetaExpedient(
							metaExpedient,
							paginacioParams.getFiltre() == null,
							paginacioParams.getFiltre(),
							paginacioHelper.toSpringDataPageable(paginacioParams)),
					MetaDocumentDto.class);
		} else {
			resposta = paginacioHelper.toPaginaDto(
					metaDocumentRepository.findByMetaExpedient(
							metaExpedient,
							paginacioParams.getFiltre() == null,
							paginacioParams.getFiltre(),
							paginacioHelper.toSpringDataSort(paginacioParams)),
					MetaDocumentDto.class);
		}
		metaNodeHelper.omplirMetaDadesPerMetaNodes(resposta.getContingut());
		metaNodeHelper.omplirPermisosPerMetaNodes(
				resposta.getContingut(),
				true);
		return resposta;
	}
	
	
	
	@Transactional(readOnly = true)
	@Override
	public List<MetaDocumentDto> findByMetaExpedient(
			Long entitatId,
			Long metaExpedientId) {
		logger.debug("Consulta dels meta-documents del meta-expedient (" +
				"entitatId=" + entitatId + ", " +
				"metaExpedientId=" + metaExpedientId + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);
		MetaExpedientEntity metaExpedient = entityComprovarHelper.comprovarMetaExpedient(
				entitat,
				metaExpedientId);

		return conversioTipusHelper.convertirList(
				metaDocumentRepository.findByMetaExpedient(metaExpedient),
				MetaDocumentDto.class);
	}
	
	
	

	@Transactional(readOnly = true)
	@Override
	public List<MetaDocumentDto> findByEntitat(
			Long entitatId) {
		logger.debug("Consulta dels meta-documents de l'entitat (" +
				"entitatId=" + entitatId + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);
		return conversioTipusHelper.convertirList(
				metaDocumentRepository.findByEntitat(entitat),
				MetaDocumentDto.class);
	}

	@Transactional(readOnly = true)
	@Override
	public FitxerDto getPlantilla(
			Long entitatId,
			Long contingutId,
			Long id) {
		logger.debug("Obtenint plantilla del meta-document (" +
				"entitatId=" + entitatId + ", " +
				"contingutId=" + contingutId + ", " +
				"id=" + id +  ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				false,
				true);
		ContingutEntity contingut = entityComprovarHelper.comprovarContingut(entitat, id);
		ExpedientEntity expedientSuperior;
		if (ContingutTipusEnumDto.EXPEDIENT.equals(contingut.getTipus())) {
			expedientSuperior = (ExpedientEntity)contingut;
		} else {
			expedientSuperior = contingut.getExpedient();
		}
		MetaExpedientEntity metaExpedient = expedientSuperior.getMetaExpedient();
		MetaDocumentEntity metaDocumentEntitiy = entityComprovarHelper.comprovarMetaDocument(
				entitat,
				metaExpedient,
				id);
		FitxerDto fitxer = new FitxerDto();
		fitxer.setNom(metaDocumentEntitiy.getPlantillaNom());
		fitxer.setContentType(metaDocumentEntitiy.getPlantillaContentType());
		fitxer.setContingut(metaDocumentEntitiy.getPlantillaContingut());
		return fitxer;
	}
	
	@Transactional(readOnly = true)
	@Override
	public MetaDocumentDto getDadesNti(
			Long entitatId,
			Long contingutId,
			Long id) {
		logger.debug("Obtenint plantilla del meta-document (" +
				"entitatId=" + entitatId + ", " +
				"contingutId=" + contingutId + ", " +
				"id=" + id +  ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				false,
				true);
		ContingutEntity contingut = entityComprovarHelper.comprovarContingut(entitat, contingutId);
		ExpedientEntity expedientSuperior;
		if (ContingutTipusEnumDto.EXPEDIENT.equals(contingut.getTipus())) {
			expedientSuperior = (ExpedientEntity)contingut;
		} else {
			expedientSuperior = contingut.getExpedient();
		}
		MetaExpedientEntity metaExpedient = expedientSuperior.getMetaExpedient();
		MetaDocumentEntity metaDocumentEntitiy = entityComprovarHelper.comprovarMetaDocument(
				entitat,
				metaExpedient,
				id);
		return conversioTipusHelper.convertir(
				metaDocumentEntitiy, 
				MetaDocumentDto.class);
	}

	@Transactional(readOnly = true)
	@Override
	public List<MetaDocumentDto> findActiusPerCreacio(
			Long entitatId,
			Long contenidorId) {
		logger.debug("Consulta de meta-documents actius per a creació ("
				+ "entitatId=" + entitatId +  ", "
				+ "contenidorId=" + contenidorId +  ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				true,
				false,
				false);
		ContingutEntity contingut = entityComprovarHelper.comprovarContingut(
				entitat,
				contenidorId);
		ExpedientEntity expedientSuperior = contenidorHelper.getExpedientSuperior(
				contingut,
				true,
				false,
				false);
		List<MetaDocumentEntity> metaDocuments = findMetaDocumentsDisponiblesPerCreacio(
				entitat,
				expedientSuperior);
		return conversioTipusHelper.convertirList(
				metaDocuments,
				MetaDocumentDto.class);
	}

	@Transactional(readOnly = true)
	@Override
	public List<MetaDocumentDto> findActiusPerModificacio(
			Long entitatId,
			Long documentId) {
		logger.debug("Consulta de meta-documents actius per a modificació ("
				+ "entitatId=" + entitatId +  ", "
				+ "documentId=" + documentId +  ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				true,
				false,
				false);
		DocumentEntity document = entityComprovarHelper.comprovarDocument(
				entitat,
				null,
				documentId,
				false,
				false,
				false,
				false);
		ExpedientEntity expedientSuperior = contenidorHelper.getExpedientSuperior(
				document,
				true,
				false,
				false);
		// Han de ser els mateixos que per a la creació però afegit el meta-document
		// del document que es vol modificar
		List<MetaDocumentEntity> metaDocuments = findMetaDocumentsDisponiblesPerCreacio(
				entitat,
				expedientSuperior);
		if (document.getMetaDocument() != null && !metaDocuments.contains(document.getMetaDocument())) {
			metaDocuments.add(document.getMetaDocument());
		}
		return conversioTipusHelper.convertirList(
				metaDocuments,
				MetaDocumentDto.class);
	}

	@Transactional(readOnly = true)
	@Override
	public List<PortafirmesDocumentTipusDto> portafirmesFindDocumentTipus() {
		return pluginHelper.portafirmesFindDocumentTipus();
	}



	private List<MetaDocumentEntity> findMetaDocumentsDisponiblesPerCreacio(
			EntitatEntity entitat,
			ExpedientEntity expedientSuperior) {
		List<MetaDocumentEntity> metaDocuments = new ArrayList<MetaDocumentEntity>();
		// Dels meta-documents actius pel meta-expedient només deixa els que
		// encara es poden afegir segons la multiplicitat.
		List<MetaDocumentEntity> metaDocumentsDelMetaExpedient = metaDocumentRepository.findByMetaExpedient(
				expedientSuperior.getMetaExpedient());
		// Nomes retorna els documents que no s'hagin esborrat
		List<DocumentEntity> documents = documentRepository.findByExpedientAndEsborrat(
				expedientSuperior,
				0);
		for (MetaDocumentEntity metaDocument: metaDocumentsDelMetaExpedient) {
			boolean afegir = true;
			for (DocumentEntity document: documents) {
				if (document.getMetaNode() != null && document.getMetaNode().equals(metaDocument)) {
					if (metaDocument.getMultiplicitat().equals(MultiplicitatEnumDto.M_0_1) || metaDocument.getMultiplicitat().equals(MultiplicitatEnumDto.M_1))
						afegir = false;
					break;
				}
			}
			if (afegir) {
				metaDocuments.add(metaDocument);
			}
		}
		return metaDocuments;
	}

	private static final Logger logger = LoggerFactory.getLogger(MetaDocumentServiceImpl.class);

}