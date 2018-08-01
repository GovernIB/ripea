/**
 * 
 */
package es.caib.ripea.core.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.caib.ripea.core.api.dto.FitxerDto;
import es.caib.ripea.core.api.dto.MetaDadaDto;
import es.caib.ripea.core.api.dto.MetaDocumentDto;
import es.caib.ripea.core.api.dto.MetaNodeMetaDadaDto;
import es.caib.ripea.core.api.dto.MultiplicitatEnumDto;
import es.caib.ripea.core.api.dto.PaginaDto;
import es.caib.ripea.core.api.dto.PaginacioParamsDto;
import es.caib.ripea.core.api.dto.PermisDto;
import es.caib.ripea.core.api.dto.PortafirmesDocumentTipusDto;
import es.caib.ripea.core.api.exception.NotFoundException;
import es.caib.ripea.core.api.service.MetaDocumentService;
import es.caib.ripea.core.entity.ContingutEntity;
import es.caib.ripea.core.entity.DocumentEntity;
import es.caib.ripea.core.entity.EntitatEntity;
import es.caib.ripea.core.entity.ExpedientEntity;
import es.caib.ripea.core.entity.MetaDadaEntity;
import es.caib.ripea.core.entity.MetaDocumentEntity;
import es.caib.ripea.core.entity.MetaExpedientMetaDocumentEntity;
import es.caib.ripea.core.entity.MetaNodeEntity;
import es.caib.ripea.core.entity.MetaNodeMetaDadaEntity;
import es.caib.ripea.core.helper.ContingutHelper;
import es.caib.ripea.core.helper.ConversioTipusHelper;
import es.caib.ripea.core.helper.EntityComprovarHelper;
import es.caib.ripea.core.helper.MetaNodeHelper;
import es.caib.ripea.core.helper.PaginacioHelper;
import es.caib.ripea.core.helper.PermisosHelper;
import es.caib.ripea.core.helper.PermisosHelper.ObjectIdentifierExtractor;
import es.caib.ripea.core.helper.PluginHelper;
import es.caib.ripea.core.repository.DocumentRepository;
import es.caib.ripea.core.repository.EntitatRepository;
import es.caib.ripea.core.repository.MetaDadaRepository;
import es.caib.ripea.core.repository.MetaDocumentRepository;
import es.caib.ripea.core.repository.MetaExpedientMetaDocumentRepository;
import es.caib.ripea.core.repository.MetaNodeMetaDadaRepository;
import es.caib.ripea.core.security.ExtendedPermission;

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
	private MetaNodeMetaDadaRepository metaNodeMetaDadaRepository;
	@Resource
	private MetaExpedientMetaDocumentRepository metaExpedientMetaDocumentRepository;
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
			MetaDocumentDto metaDocument,
			String plantillaNom,
			String plantillaContentType,
			byte[] plantillaContingut) {
		logger.debug("Creant un nou meta-document ("
				+ "entitatId=" + entitatId + ", "
				+ "metaDocument=" + metaDocument + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);
		MetaDocumentEntity entity = MetaDocumentEntity.getBuilder(
				entitat,
				metaDocument.getCodi(),
				metaDocument.getNom()).
				descripcio(metaDocument.getDescripcio()).
				globalExpedient(metaDocument.isGlobalExpedient()).
				globalMultiplicitat(metaDocument.getGlobalMultiplicitat()).
				globalReadOnly(metaDocument.isGlobalReadOnly()).
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
			MetaDocumentDto metaDocument,
			String plantillaNom,
			String plantillaContentType,
			byte[] plantillaContingut) {
		logger.debug("Actualitzant meta-document existent ("
				+ "entitatId=" + entitatId + ", "
				+ "metaDocument=" + metaDocument + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);
		MetaDocumentEntity metaDocumentEntitiy = entityComprovarHelper.comprovarMetaDocument(
				entitat,
				metaDocument.getId(),
				false,
				false,
				false,
				false);
		metaDocumentEntitiy.update(
				metaDocument.getCodi(),
				metaDocument.getNom(),
				metaDocument.getDescripcio(),
				metaDocument.isGlobalExpedient(),
				metaDocument.getGlobalMultiplicitat(),
				metaDocument.isGlobalReadOnly(),
				metaDocument.isFirmaPortafirmesActiva(),
				metaDocument.getPortafirmesDocumentTipus(),
				metaDocument.getPortafirmesFluxId(),
				metaDocument.getPortafirmesResponsables(),
				metaDocument.getPortafirmesFluxTipus(),
				metaDocument.getPortafirmesCustodiaTipus(),
				metaDocument.isFirmaPassarelaActiva(),
				metaDocument.getFirmaPassarelaCustodiaTipus());
		if (plantillaContingut != null) {
			metaDocumentEntitiy.updatePlantilla(
					plantillaNom,
					plantillaContentType,
					plantillaContingut);
		}
		return conversioTipusHelper.convertir(
				metaDocumentEntitiy,
				MetaDocumentDto.class);
	}

	@Transactional
	@Override
	public MetaDocumentDto updateActiu(
			Long entitatId,
			Long id,
			boolean actiu) {
		logger.debug("Actualitzant propietat activa d'un meta-document existent ("
				+ "entitatId=" + entitatId + ", "
				+ "id=" + id + ", "
				+ "actiu=" + actiu + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);
		MetaDocumentEntity metaDocument = entityComprovarHelper.comprovarMetaDocument(
				entitat,
				id,
				false,
				false,
				false,
				false);
		metaDocument.updateActiu(actiu);
		return conversioTipusHelper.convertir(
				metaDocument,
				MetaDocumentDto.class);
	}

	@Transactional
	@Override
	public MetaDocumentDto delete(
			Long entitatId,
			Long id) {
		logger.debug("Esborrant meta-document (id=" + id +  ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);
		MetaDocumentEntity metaDocumentEntitiy = entityComprovarHelper.comprovarMetaDocument(
				entitat,
				id,
				false,
				false,
				false,
				false);
		metaDocumentRepository.delete(metaDocumentEntitiy);
		return conversioTipusHelper.convertir(
				metaDocumentEntitiy,
				MetaDocumentDto.class);
	}

	@Transactional(readOnly=true)
	@Override
	public MetaDocumentDto findById(
			Long entitatId,
			Long id) {
		logger.debug("Consulta del meta-document ("
				+ "entitatId=" + entitatId + ", "
				+ "id=" + id + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);
		MetaDocumentEntity metaDocumentEntitiy = entityComprovarHelper.comprovarMetaDocument(
				entitat,
				id,
				false,
				false,
				false,
				false);
		MetaDocumentDto resposta = conversioTipusHelper.convertir(
				metaDocumentEntitiy,
				MetaDocumentDto.class);
		if (resposta != null) {
			metaNodeHelper.omplirMetaDadesPerMetaNode(resposta);
			metaNodeHelper.omplirPermisosPerMetaNode(resposta, false);
		}
		return resposta;
	}

	@Transactional(readOnly=true)
	@Override
	public MetaDocumentDto findByEntitatCodi(
			Long entitatId,
			String codi) {
		logger.debug("Consulta del meta-document per entitat i codi ("
				+ "entitatId=" + entitatId + ", "
				+ "codi=" + codi + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);
		MetaDocumentDto resposta = conversioTipusHelper.convertir(
				metaDocumentRepository.findByEntitatAndCodi(entitat, codi),
				MetaDocumentDto.class);
		if (resposta != null) {
			metaNodeHelper.omplirMetaDadesPerMetaNode(resposta);
			metaNodeHelper.omplirPermisosPerMetaNode(resposta, false);
		}
		return resposta;
	}

	@Transactional(readOnly=true)
	@Override
	public PaginaDto<MetaDocumentDto> findByEntitatPaginat(
			Long entitatId,
			PaginacioParamsDto paginacioParams) {
		logger.debug("Consulta paginada dels meta-documents de l'entitat (entitatId=" + entitatId + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);
		PaginaDto<MetaDocumentDto> resposta;
		if (paginacioHelper.esPaginacioActivada(paginacioParams)) {
			resposta = paginacioHelper.toPaginaDto(
					metaDocumentRepository.findByEntitat(
							entitat,
							paginacioHelper.toSpringDataPageable(paginacioParams)),
					MetaDocumentDto.class);
		} else {
			resposta = paginacioHelper.toPaginaDto(
					metaDocumentRepository.findByEntitat(
							entitat,
							paginacioHelper.toSpringDataSort(paginacioParams)),
					MetaDocumentDto.class);
		}
		metaNodeHelper.omplirMetaDadesPerMetaNodes(resposta.getContingut());
		metaNodeHelper.omplirPermisosPerMetaNodes(
				resposta.getContingut(),
				true);
		return resposta;
	}

	@Transactional(readOnly=true)
	@Override
	public List<MetaDocumentDto> findByEntitat(
			Long entitatId) {
		logger.debug("Consulta de tots els meta-documents de l'entitat ("
				+ "entitatId=" + entitatId + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);
		return conversioTipusHelper.convertirList(
				metaDocumentRepository.findByEntitatOrderByNomAsc(
						entitat),
						MetaDocumentDto.class);
	}

	@Transactional(readOnly=true)
	@Override
	public List<MetaDocumentDto> findByEntitatAndActiveTrue(
			Long entitatId,
			boolean incloureGlobalsExpedient) {
		logger.debug("Consulta dels meta-documents actius de l'entitat ("
				+ "entitatId=" + entitatId + ", "
				+ "incloureGlobalsExpedient=" + incloureGlobalsExpedient + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);
		return conversioTipusHelper.convertirList(
				metaDocumentRepository.findByEntitatAndActiuTrueAndGlobalsOrderByNomAsc(
						entitat,
						incloureGlobalsExpedient),
						MetaDocumentDto.class);
	}

	@Transactional(readOnly=true)
	@Override
	public FitxerDto getPlantilla(
			Long entitatId,
			Long id) {
		logger.debug("Obtenint plantilla del meta-document (" +
				"entitatId=" + entitatId + ", " +
				"id=" + id +  ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				false,
				true);
		MetaDocumentEntity metaDocumentEntitiy = entityComprovarHelper.comprovarMetaDocument(
				entitat,
				id,
				false,
				false,
				false,
				false);
		FitxerDto fitxer = new FitxerDto();
		fitxer.setNom(metaDocumentEntitiy.getPlantillaNom());
		fitxer.setContentType(metaDocumentEntitiy.getPlantillaContentType());
		fitxer.setContingut(metaDocumentEntitiy.getPlantillaContingut());
		return fitxer;
	}

	@Transactional
	@Override
	public void metaDadaCreate(
			Long entitatId,
			Long id,
			Long metaDadaId,
			MultiplicitatEnumDto multiplicitat,
			boolean readOnly) {
		logger.debug("Afegint meta-dada al meta-document ("
				+ "entitatId=" + entitatId +  ", "
				+ "id=" + id +  ", "
				+ "metaDadaId=" + metaDadaId +  ", "
				+ "readOnly=" + readOnly +  ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);
		MetaDocumentEntity metaDocument = entityComprovarHelper.comprovarMetaDocument(
				entitat,
				id,
				false,
				false,
				false,
				false);
		MetaDadaEntity metaDada = entityComprovarHelper.comprovarMetaDada(
				entitat,
				metaDadaId);
		metaDocument.metaDadaAdd(
				metaDada,
				multiplicitat,
				readOnly);
	}

	@Transactional
	@Override
	public void metaDadaUpdate(
			Long entitatId,
			Long id,
			Long metaNodeMetaDadaId,
			MultiplicitatEnumDto multiplicitat,
			boolean readOnly) {
		logger.debug("Actualitzant meta-dada del meta-document ("
				+ "entitatId=" + entitatId +  ", "
				+ "id=" + id +  ", "
				+ "metaNodeMetaDadaId=" + metaNodeMetaDadaId +  ","
				+ "multiplicitat=" + multiplicitat +  ", "
				+ "readOnly=" + readOnly +  ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);
		MetaDocumentEntity metaDocument = entityComprovarHelper.comprovarMetaDocument(
				entitat,
				id,
				false,
				false,
				false,
				false);
		MetaNodeMetaDadaEntity metaNodeMetaDada = entityComprovarHelper.comprovarMetaNodeMetaDada(
				entitat,
				metaDocument,
				metaNodeMetaDadaId);
		metaNodeMetaDada.update(
				multiplicitat,
				readOnly);
	}

	@Transactional
	@Override
	public void metaDadaDelete(
			Long entitatId,
			Long id,
			Long metaDocumentMetaDadaId) {
		logger.debug("Afegint meta-dada al meta-document ("
				+ "entitatId=" + entitatId +  ", "
				+ "id=" + id +  ", "
				+ "metaDocumentMetaDadaId=" + metaDocumentMetaDadaId +  ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);
		MetaDocumentEntity metaDocument = entityComprovarHelper.comprovarMetaDocument(
				entitat,
				id,
				false,
				false,
				false,
				false);
		MetaNodeMetaDadaEntity metaDocumentMetaDada = entityComprovarHelper.comprovarMetaNodeMetaDada(
				entitat,
				metaDocument,
				metaDocumentMetaDadaId);
		metaDocument.metaDadaDelete(metaDocumentMetaDada);
		metaNodeHelper.reordenarMetaDades(metaDocument);
	}

	@Transactional
	@Override
	public void metaDadaMoveUp(
			Long entitatId,
			Long id,
			Long metaDocumentMetaDadaId) {
		logger.debug("Movent meta-dada al meta-document cap amunt ("
				+ "entitatId=" + entitatId +  ", "
				+ "id=" + id +  ", "
				+ "metaDocumentMetaDadaId=" + metaDocumentMetaDadaId + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);
		MetaDocumentEntity metaDocument = entityComprovarHelper.comprovarMetaDocument(
				entitat,
				id,
				false,
				false,
				false,
				false);
		MetaNodeMetaDadaEntity metaDocumentMetaDada = entityComprovarHelper.comprovarMetaNodeMetaDada(
				entitat,
				metaDocument,
				metaDocumentMetaDadaId);
		metaNodeHelper.moureMetaNodeMetaDada(
				metaDocument,
				metaDocumentMetaDada,
				metaDocumentMetaDada.getOrdre() - 1);
	}

	@Transactional
	@Override
	public void metaDadaMoveDown(
			Long entitatId,
			Long id,
			Long metaDocumentMetaDadaId) {
		logger.debug("Movent meta-dada al meta-document cap avall ("
				+ "entitatId=" + entitatId +  ", "
				+ "id=" + id +  ", "
				+ "metaDocumentMetaDadaId=" + metaDocumentMetaDadaId +  ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);
		MetaDocumentEntity metaDocument = entityComprovarHelper.comprovarMetaDocument(
				entitat,
				id,
				false,
				false,
				false,
				false);
		MetaNodeMetaDadaEntity metaDocumentMetaDada = entityComprovarHelper.comprovarMetaNodeMetaDada(
				entitat,
				metaDocument,
				metaDocumentMetaDadaId);
		metaNodeHelper.moureMetaNodeMetaDada(
				metaDocument,
				metaDocumentMetaDada,
				metaDocumentMetaDada.getOrdre() + 1);
	}

	@Transactional
	@Override
	public void metaDadaMoveTo(
			Long entitatId,
			Long id,
			Long metaDocumentMetaDadaId,
			int posicio) {
		logger.debug("Movent meta-dada al meta-document ("
				+ "entitatId=" + entitatId +  ", "
				+ "id=" + id +  ", "
				+ "metaDocumentMetaDadaId=" + metaDocumentMetaDadaId +  ", "
				+ "posicio=" + posicio +  ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);
		MetaDocumentEntity metaDocument = entityComprovarHelper.comprovarMetaDocument(
				entitat,
				id,
				false,
				false,
				false,
				false);
		MetaNodeMetaDadaEntity metaDocumentMetaDada = entityComprovarHelper.comprovarMetaNodeMetaDada(
				entitat,
				metaDocument,
				metaDocumentMetaDadaId);
		metaNodeHelper.moureMetaNodeMetaDada(
				metaDocument,
				metaDocumentMetaDada,
				posicio);
	}

	@Transactional(readOnly = true)
	@Override
	public MetaNodeMetaDadaDto metaDadaFind(
			Long entitatId,
			Long id,
			Long metaNodeMetaDadaId) {
		logger.debug("Cercant la meta-dada del meta-expedient ("
				+ "entitatId=" + entitatId +  ", "
				+ "id=" + id +  ", "
				+ "metaNodeMetaDadaId=" + metaNodeMetaDadaId +  ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);
		MetaDocumentEntity metaDocument = entityComprovarHelper.comprovarMetaDocument(
				entitat,
				id,
				false,
				false,
				false,
				false);
		MetaNodeMetaDadaEntity metaNodeMetaDada = entityComprovarHelper.comprovarMetaNodeMetaDada(
				entitat,
				metaDocument,
				metaNodeMetaDadaId);
		return conversioTipusHelper.convertir(
				metaNodeMetaDada,
				MetaNodeMetaDadaDto.class);
	}

	public List<MetaDadaDto> metaDadaFindGlobals(
			Long entitatId) throws NotFoundException {
		logger.debug("Cercant les meta-dades globals del meta-document ("
				+ "entitatId=" + entitatId +  ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);
		return conversioTipusHelper.convertirList(
				metaDadaRepository.findByEntitatAndGlobalDocumentTrueOrderByIdAsc(entitat),
				MetaDadaDto.class);
	}

	@Transactional
	@Override
	public List<PermisDto> findPermis(
			Long entitatId,
			Long id) {
		logger.debug("Consulta dels permisos del meta-document ("
				+ "entitatId=" + entitatId +  ", "
				+ "id=" + id +  ")"); 
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);
		entityComprovarHelper.comprovarMetaDocument(
				entitat,
				id,
				false,
				false,
				false,
				false);
		return permisosHelper.findPermisos(
				id,
				MetaNodeEntity.class);
	}

	@Transactional
	@Override
	public void updatePermis(
			Long entitatId,
			Long id,
			PermisDto permis) {
		logger.debug("Modificació del permis del meta-document ("
				+ "entitatId=" + entitatId +  ", "
				+ "id=" + id + ", "
				+ "permis=" + permis + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);
		entityComprovarHelper.comprovarMetaDocument(
				entitat,
				id,
				false,
				false,
				false,
				false);
		permisosHelper.updatePermis(
				id,
				MetaNodeEntity.class,
				permis);
	}

	@Transactional
	@Override
	public void deletePermis(
			Long entitatId,
			Long id,
			Long permisId) {
		logger.debug("Eliminació del permis del meta-document ("
				+ "entitatId=" + entitatId +  ", "
				+ "id=" + id + ", "
				+ "permisId=" + permisId + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);
		entityComprovarHelper.comprovarMetaDocument(
				entitat,
				id,
				false,
				false,
				false,
				false);
		permisosHelper.deletePermis(
				id,
				MetaNodeEntity.class,
				permisId);
	}

	@Transactional(readOnly = true)
	@Override
	public List<MetaDocumentDto> findActiveByEntitatAndContenidorPerCreacio(
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
	public List<MetaDocumentDto> findActiveByEntitatAndDocumentPerModificacio(
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
	public List<PortafirmesDocumentTipusDto> findPortafirmesDocumentTipus() {
		return pluginHelper.portafirmesFindDocumentTipus();
	}



	private List<MetaDocumentEntity> findMetaDocumentsDisponiblesPerCreacio(
			EntitatEntity entitat,
			ExpedientEntity expedientSuperior) {
		List<MetaDocumentEntity> metaDocuments;
		if (expedientSuperior != null) {
			// A dins els expedients només es poden crear documents amb meta-document 
			metaDocuments = new ArrayList<MetaDocumentEntity>();
			// Dels meta-documents actius pel meta-expedient només deixa els que
			// encara es poden afegir segons la multiplicitat.
			List<MetaExpedientMetaDocumentEntity> metaExpedientMetaDocuments = metaExpedientMetaDocumentRepository.findByMetaExpedientAndMetaDocumentActiuTrueOrderByMetaDocumentNomAsc(
					expedientSuperior.getMetaExpedient());
			// Nomes retorna els documents que no s'hagin esborrat
			List<DocumentEntity> documents = documentRepository.findByExpedientAndEsborrat(
					expedientSuperior,
					0);
			for (MetaExpedientMetaDocumentEntity metaExpedientMetaDocument: metaExpedientMetaDocuments) {
				boolean afegir = true;
				for (DocumentEntity document: documents) {
					if (document.getMetaNode() != null && document.getMetaNode().equals(metaExpedientMetaDocument.getMetaDocument())) {
						if (metaExpedientMetaDocument.getMultiplicitat().equals(MultiplicitatEnumDto.M_0_1) || metaExpedientMetaDocument.getMultiplicitat().equals(MultiplicitatEnumDto.M_1))
							afegir = false;
						break;
					}
				}
				if (afegir)
					metaDocuments.add(metaExpedientMetaDocument.getMetaDocument());
			}
			// ...comprova que es tengui el permis CREATE
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			permisosHelper.filterGrantedAll(
					metaDocuments,
					new ObjectIdentifierExtractor<MetaNodeEntity>() {
						public Long getObjectIdentifier(MetaNodeEntity metaNode) {
							return metaNode.getId();
						}
					},
					MetaNodeEntity.class,
					new Permission[] {ExtendedPermission.CREATE},
					auth);
			// Afegeix els meta-documents globals actius
			metaDocuments.addAll(
					metaDocumentRepository.findByEntitatAndGlobalExpedientTrueAndActiuTrueOrderByNomAsc(
							entitat));
		} else {
			// Consulta els metadocuments de l'entitat...
			metaDocuments = metaDocumentRepository.findByEntitatAndActiuTrueOrderByNomAsc(entitat);
			// ...i comprova que es tengui el permis CREATE
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			permisosHelper.filterGrantedAll(
					metaDocuments,
					new ObjectIdentifierExtractor<MetaNodeEntity>() {
						public Long getObjectIdentifier(MetaNodeEntity metaNode) {
							return metaNode.getId();
						}
					},
					MetaNodeEntity.class,
					new Permission[] {ExtendedPermission.CREATE},
					auth);
			
		}
		return metaDocuments;
	}

	private static final Logger logger = LoggerFactory.getLogger(MetaDocumentServiceImpl.class);

}
