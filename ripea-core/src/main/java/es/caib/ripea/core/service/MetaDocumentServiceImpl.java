/**
 * 
 */
package es.caib.ripea.core.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.caib.ripea.core.api.dto.ContingutTipusEnumDto;
import es.caib.ripea.core.api.dto.FitxerDto;
import es.caib.ripea.core.api.dto.MetaDocumentDto;
import es.caib.ripea.core.api.dto.MetaDocumentTipusGenericEnumDto;
import es.caib.ripea.core.api.dto.MultiplicitatEnumDto;
import es.caib.ripea.core.api.dto.PaginaDto;
import es.caib.ripea.core.api.dto.PaginacioParamsDto;
import es.caib.ripea.core.api.dto.PortafirmesDocumentTipusDto;
import es.caib.ripea.core.api.exception.ExisteixenDocumentsException;
import es.caib.ripea.core.api.exception.NotFoundException;
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
import es.caib.ripea.core.helper.MetaDocumentHelper;
import es.caib.ripea.core.helper.MetaExpedientHelper;
import es.caib.ripea.core.helper.MetaNodeHelper;
import es.caib.ripea.core.helper.PaginacioHelper;
import es.caib.ripea.core.helper.PermisosHelper;
import es.caib.ripea.core.helper.PluginHelper;
import es.caib.ripea.core.repository.DocumentRepository;
import es.caib.ripea.core.repository.EntitatRepository;
import es.caib.ripea.core.repository.MetaDadaRepository;
import es.caib.ripea.core.repository.MetaDocumentRepository;
import es.caib.ripea.core.repository.MetaExpedientRepository;

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
	@Resource
	private MetaExpedientHelper metaExpedientHelper;
	@Resource
	private MetaDocumentHelper metaDocumentHelper;
	@Resource
	private MetaExpedientRepository metaExpedientRepository;

	@Transactional
	@Override
	public MetaDocumentDto create(
			Long entitatId,
			Long metaExpedientId,
			MetaDocumentDto metaDocument,
			String plantillaNom,
			String plantillaContentType,
			byte[] plantillaContingut, String rolActual, Long organId) {
		
		MetaDocumentDto metaDocumentDto = metaDocumentHelper.create(
				entitatId,
				metaExpedientId,
				metaDocument,
				plantillaNom,
				plantillaContentType,
				plantillaContingut,
				rolActual,
				organId);


		return metaDocumentDto;
	}


	@Transactional
	@Override
	public MetaDocumentDto create(
			Long entitatId,
			MetaDocumentDto metaDocument,
			String plantillaNom,
			String plantillaContentType,
			byte[] plantillaContingut) {
		logger.debug("Creant un nou meta-document (" +
				"entitatId=" + entitatId + ", " +
				"metaDocument=" + metaDocument + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false, false, false);

		MetaDocumentEntity entity = MetaDocumentEntity.getBuilder(
				entitat,
				metaDocument.getCodi(),
				metaDocument.getNom(),
				metaDocument.getMultiplicitat(),
				null,
				metaDocument.getNtiOrigen(),
				metaDocument.getNtiEstadoElaboracion(),
				metaDocument.getNtiTipoDocumental(),
				metaDocument.isPinbalActiu(),
				metaDocument.getPinbalFinalitat()).
				biometricaLectura(metaDocument.isBiometricaLectura()).
				firmaBiometricaActiva(metaDocument.isFirmaBiometricaActiva()).
				firmaPortafirmesActiva(metaDocument.isFirmaPortafirmesActiva()).
				descripcio(metaDocument.getDescripcio()).
				portafirmesDocumentTipus(metaDocument.getPortafirmesDocumentTipus()).
				portafirmesFluxId(metaDocument.getPortafirmesFluxId()).
				portafirmesResponsables(metaDocument.getPortafirmesResponsables()).
				portafirmesSequenciaTipus(metaDocument.getPortafirmesSequenciaTipus()).
				portafirmesCustodiaTipus(metaDocument.getPortafirmesCustodiaTipus()).
				firmaPassarelaActiva(metaDocument.isFirmaPassarelaActiva()).
				firmaPassarelaCustodiaTipus(metaDocument.getFirmaPassarelaCustodiaTipus()).
				portafirmesFluxTipus(metaDocument.getPortafirmesFluxTipus()).
				pinbalServei(metaDocument.getPinbalServei()).
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
			byte[] plantillaContingut, String rolActual, Long organId) {
		logger.debug("Actualitzant meta-document existent (" +
				"entitatId=" + entitatId + ", " +
				"metaExpedientId=" + metaExpedientId + ", " +
				"metaDocument=" + metaDocument + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				false,
				false, 
				true, false);
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
				metaDocument.getPortafirmesSequenciaTipus(),
				metaDocument.getPortafirmesCustodiaTipus(),
				metaDocument.isFirmaPassarelaActiva(),
				metaDocument.getFirmaPassarelaCustodiaTipus(),
				metaDocument.getNtiOrigen(),
				metaDocument.getNtiEstadoElaboracion(),
				metaDocument.getNtiTipoDocumental(),
				metaDocument.isFirmaBiometricaActiva(),
				metaDocument.isBiometricaLectura(),
				metaDocument.getPortafirmesFluxTipus(),
				metaDocument.isPinbalActiu(),
				metaDocument.getPinbalServei(),
				metaDocument.getPinbalFinalitat());
		if (plantillaContingut != null) {
			entity.updatePlantilla(
					plantillaNom,
					plantillaContentType,
					plantillaContingut);
		}
		
		if (rolActual.equals("IPA_ORGAN_ADMIN")) {
			metaExpedientHelper.canviarRevisioADisseny(entitatId, metaExpedient.getId(), organId);
		}
		
		return conversioTipusHelper.convertir(
				entity,
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
		logger.debug("Actualitzant meta-document existent (" +
				"entitatId=" + entitatId + ", " +
				"metaDocument=" + metaDocument + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false, false, false);

		MetaDocumentEntity entity = entityComprovarHelper.comprovarMetaDocument(
				entitat,
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
				metaDocument.getPortafirmesSequenciaTipus(),
				metaDocument.getPortafirmesCustodiaTipus(),
				metaDocument.isFirmaPassarelaActiva(),
				metaDocument.getFirmaPassarelaCustodiaTipus(),
				metaDocument.getNtiOrigen(),
				metaDocument.getNtiEstadoElaboracion(),
				metaDocument.getNtiTipoDocumental(),
				metaDocument.isFirmaBiometricaActiva(),
				metaDocument.isBiometricaLectura(),
				metaDocument.getPortafirmesFluxTipus(),
				metaDocument.isPinbalActiu(),
				metaDocument.getPinbalServei(),
				metaDocument.getPinbalFinalitat());
		
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
			boolean actiu, String rolActual) {
		logger.debug("Actualitzant propietat activa d'un meta-document existent (" +
				"entitatId=" + entitatId + ", " +
				"metaExpedientId=" + metaExpedientId + ", " +
				"id=" + id + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				false,
				false, 
				false, 
				true);
		
		MetaExpedientEntity metaExpedient;
		metaExpedient = metaExpedientId == null ? null : entityComprovarHelper.comprovarMetaExpedient(
															entitat,
															metaExpedientId);
		MetaDocumentEntity metaDocument = entityComprovarHelper.comprovarMetaDocument(
				entitat,
				metaExpedient,
				id);
		metaDocument.updateActiu(actiu);
		if (rolActual.equals("IPA_ORGAN_ADMIN")) {
			metaExpedientHelper.canviarRevisioADisseny(entitatId, metaExpedient.getId(), null);
		}
		return conversioTipusHelper.convertir(
				metaDocument,
				MetaDocumentDto.class);
	}

	@Transactional
	@Override
	public MetaDocumentDto delete(
			Long entitatId,
			Long metaExpedientId,
			Long id, String rolActual, Long organId) {
		logger.debug("Esborrant meta-document (" +
				"entitatId=" + entitatId + ", " +
				"metaExpedientId=" + metaExpedientId + ", " +
				"id=" + id + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				false,
				false, 
				false, 
				true);

		MetaExpedientEntity metaExpedient;
		metaExpedient = metaExpedientId == null ? null : entityComprovarHelper.comprovarMetaExpedient(
															entitat,
															metaExpedientId);
		MetaDocumentEntity metaDocument = entityComprovarHelper.comprovarMetaDocument(
				entitat,
				metaExpedient,
				id);
		
		List<DocumentEntity> docs = documentRepository.findByMetaNode(metaDocument);
		if (docs != null && !docs.isEmpty()) {
			throw new ExisteixenDocumentsException();
		}
		
		metaDocumentRepository.delete(metaDocument);
		if (rolActual.equals("IPA_ORGAN_ADMIN")) {
			metaExpedientHelper.canviarRevisioADisseny(entitatId, metaExpedient.getId(), organId);
		}
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
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitatPerMetaExpedients(entitatId);
		MetaExpedientEntity metaExpedient;
		metaExpedient = metaExpedientId == null ? null : entityComprovarHelper.comprovarMetaExpedient(
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
			metaNodeHelper.omplirPermisosPerMetaNode(resposta, null, null);
		}
		return resposta;
	}
	
	
	@Transactional(readOnly = true)
	@Override
	public MetaDocumentDto findById(
			Long entitatId,
			Long metaDocumentId) {
		logger.debug("Consulta del meta-document (" +
				"entitatId=" + entitatId + ", " +
				"metaDocumentId=" + metaDocumentId + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				false,
				false, 
				true, 
				false);
		MetaDocumentEntity metaDocument = entityComprovarHelper.comprovarMetaDocument(
				entitat,
				metaDocumentId);
		
		MetaDocumentDto resposta = conversioTipusHelper.convertir(
				metaDocument,
				MetaDocumentDto.class);
		if (resposta != null) {
			metaNodeHelper.omplirMetaDadesPerMetaNode(resposta);
			metaNodeHelper.omplirPermisosPerMetaNode(resposta, null, null);
			resposta.setMetaExpedientId(metaDocument.getMetaExpedient() != null ? metaDocument.getMetaExpedient().getId() : null);
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
				false,
				false, 
				true, false);
		
		
		MetaDocumentEntity entity;
		if (metaExpedientId != null) {
			MetaExpedientEntity metaExpedient = entityComprovarHelper.comprovarMetaExpedient(
												entitat,
												metaExpedientId);
			entity = metaDocumentRepository.findByMetaExpedientAndCodi(
					metaExpedient,
					codi);	
		}
		else {
			entity = metaDocumentRepository.findByMetaExpedientAndCodi(
					codi);	
		}
		
		MetaDocumentDto resposta = conversioTipusHelper.convertir(
										entity,
										MetaDocumentDto.class);
		if (resposta != null) {
			metaNodeHelper.omplirMetaDadesPerMetaNode(resposta);
			metaNodeHelper.omplirPermisosPerMetaNode(resposta, null, null);
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
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitatPerMetaExpedients(entitatId);
		
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
	public PaginaDto<MetaDocumentDto> findWithoutMetaExpedient(
			Long entitatId,
			PaginacioParamsDto paginacioParams) {
		logger.debug("Consulta dels meta-documents sense meta-expedient (" +
				"entitatId=" + entitatId + ", " +
				"paginacioParams=" + paginacioParams + ")");

		PaginaDto<MetaDocumentDto> resposta;
			resposta = paginacioHelper.toPaginaDto(
					metaDocumentRepository.findWithoutMetaExpedient(
							paginacioParams.getFiltre() == null,
							paginacioParams.getFiltre(),
							paginacioHelper.toSpringDataSort(paginacioParams)),
					MetaDocumentDto.class);
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
				false,
				true, 
				false, 
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
				false, false, false);
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
				true, false, false);
		MetaDocumentEntity metaDocumentEntitiy = entityComprovarHelper.comprovarMetaDocument(entitat, id);
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
				false, 
				true, false);
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
			Long contingutId, 
			Long metaExpedientId, 
			boolean findAllMarkDisponiblesPerCreacio) {
		logger.debug("Consulta de meta-documents actius per a creació ("
				+ "entitatId=" + entitatId +  ", "
				+ "contingutId=" + contingutId +  ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				false,
				false, 
				true, 
				false);
		
		List<MetaDocumentEntity> metaDocuments = new ArrayList<>();
		if (contingutId != null) {
			ContingutEntity contingut = entityComprovarHelper.comprovarContingut(
					entitat,
					contingutId);
			ExpedientEntity expedient = contenidorHelper.getExpedientSuperior(
					contingut,
					true,
					false,
					false, 
					false, 
					null);
			metaDocuments = findMetaDocumentsDisponiblesPerCreacio(
					entitat,
					expedient, 
					null, 
					findAllMarkDisponiblesPerCreacio);
		} else {
			MetaExpedientEntity metaExpedient =  metaExpedientRepository.findOne(metaExpedientId);
			metaDocuments = findMetaDocumentsDisponiblesPerCreacio(
					entitat,
					null, 
					metaExpedient, 
					findAllMarkDisponiblesPerCreacio);
		}

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
				false,
				false,
				false, 
				true, 
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
				false, false, null);
		// Han de ser els mateixos que per a la creació però afegit el meta-document
		// del document que es vol modificar
		List<MetaDocumentEntity> metaDocuments = findMetaDocumentsDisponiblesPerCreacio(
				entitat,
				expedientSuperior, 
				null, 
				false);
		if (document.getMetaDocument() != null && !metaDocuments.contains(document.getMetaDocument())) {
			metaDocuments.add(document.getMetaDocument());
		}
		Collections.sort(metaDocuments, new Comparator<MetaDocumentEntity>(){
		     public int compare(MetaDocumentEntity o1, MetaDocumentEntity o2){
		         if(o1.getNom().toLowerCase() == o2.getNom().toLowerCase())
		             return 0;
		         return o1.getNom().toLowerCase().compareTo(o2.getNom().toLowerCase()) < -1 ? -1 : 1;
		     }
		});
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
			ExpedientEntity expedient, 
			MetaExpedientEntity metaExpedient, 
			boolean findAllMarkDisponiblesPerCreacio) {
		
		List<MetaDocumentEntity> metaDocuments = new ArrayList<MetaDocumentEntity>();
		// Dels meta-documents actius pel meta-expedient només deixa els que
		// encara es poden afegir segons la multiplicitat.
		List<MetaDocumentEntity> metaDocumentsDelMetaExpedient = metaDocumentRepository.findByMetaExpedientAndActiuTrue(
				expedient != null ? expedient.getMetaExpedient() : metaExpedient);
		
		if (expedient != null ? expedient.getMetaExpedient().isPermetMetadocsGenerals() : metaExpedient.isPermetMetadocsGenerals()) {
			metaDocumentsDelMetaExpedient.addAll(metaDocumentRepository.findWithoutMetaExpedient());
		}
		
		if (expedient != null) {
			// Nomes retorna els documents que no s'hagin esborrat
			List<DocumentEntity> documents = documentRepository.findByExpedientAndEsborrat(
					expedient,
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
				if (findAllMarkDisponiblesPerCreacio) {
					metaDocument.setLeftPerCreacio(afegir);
					metaDocuments.add(metaDocument);
				} else {
					if (afegir) {
						metaDocuments.add(metaDocument);
					}
				}
			}
			Collections.sort(metaDocuments, new Comparator<MetaDocumentEntity>(){
			     public int compare(MetaDocumentEntity o1, MetaDocumentEntity o2){
			         if(o1.getNom().toLowerCase() == o2.getNom().toLowerCase())
			             return 0;
			         return o1.getNom().toLowerCase().compareTo(o2.getNom().toLowerCase()) < -1 ? -1 : 1;
			     }
			});
		} else {
			metaDocuments = metaDocumentsDelMetaExpedient;
		}

		return metaDocuments;
	}

	@Override
	@Transactional
	public MetaDocumentDto findByTipusGeneric(
			Long entitatId, 
			MetaDocumentTipusGenericEnumDto tipusGeneric) {
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				true,
				false,
				false, false, false);
		
		MetaDocumentEntity metaDocumentEntity = metaDocumentRepository.findByEntitatAndTipusGeneric(
				false,
				entitat,
				tipusGeneric);
		
		if (metaDocumentEntity != null) {
			return conversioTipusHelper.convertir(
					metaDocumentEntity, 
					MetaDocumentDto.class);
		} else {
			logger.error(
					"Error a l'hora de recuperar el metaDocument (" +
					"entitatId=" + entitat.getId() + 
					"metaDocumentTipus=" + tipusGeneric+ ")");
			throw new RuntimeException(
					"Error a l'hora de recuperar el metaDocument (" +
							"entitatId=" + entitat.getId() + 
							"metaDocumentTipus=" + tipusGeneric+ ")");
		}
		
	}

	@Transactional(readOnly = true)
	@Override
	public List<MetaDocumentDto> findByMetaExpedientAndFirmaPortafirmesActiva(
			Long entitatId,
			Long metaExpedientId) {
		logger.debug("Consulta dels meta-documents del meta-expedient (" +
				"entitatId=" + entitatId + ", " +
				"metaExpedientId=" + metaExpedientId + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				false,
				false, 
				true, false);
		MetaExpedientEntity metaExpedient = entityComprovarHelper.comprovarMetaExpedient(
				entitat,
				metaExpedientId);

		return conversioTipusHelper.convertirList(
				metaDocumentRepository.findByMetaExpedientAndFirmaPortafirmesActivaAmbFluxOResponsable(
						metaExpedient),
				MetaDocumentDto.class);
	}
	
	@Transactional
	@Override
	public void marcarPerDefecte(
			Long entitatId, 
			Long metaExpedientId,
			Long metaDocumentId,
			boolean remove) throws NotFoundException {
		logger.debug("Marcant/desmarcant el tipus de document per defecte (" +
				"entitatId=" + entitatId + ", " +
				"metaDocumentId=" + metaDocumentId + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				false,
				false, 
				false, 
				true);
		MetaDocumentEntity currentMetaDocument = entityComprovarHelper.comprovarMetaDocument(
				entitat, 
				metaDocumentId);
		MetaExpedientEntity metaExpedientEntity = entityComprovarHelper.comprovarMetaExpedient(
				entitat, 
				metaExpedientId);
//		Recupera els metadocuments del mateix procediment
		Set<MetaDocumentEntity> metaDocuments = metaExpedientEntity.getMetaDocuments();
		
		for (MetaDocumentEntity metaDocumentEntity : metaDocuments) {
			if (metaDocumentEntity.isPerDefecte()) {
				metaDocumentEntity.updatePerDefecte(false);
			}
		}
		if (!remove)
			currentMetaDocument.updatePerDefecte(true);
	}
	
	@Transactional
	@Override
	public MetaDocumentDto findByMetaExpedientAndPerDefecteTrue(
			Long entitatId, 
			Long metaExpedientId) throws NotFoundException {
		
		entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				false,
				false, 
				true, 
				false);
		
		MetaExpedientEntity metaExpedientEntity = metaExpedientRepository.findOne(metaExpedientId);
		MetaDocumentEntity metaDocument = metaDocumentRepository.findByMetaExpedientAndPerDefecteTrue(metaExpedientEntity);
		return conversioTipusHelper.convertir(metaDocument, MetaDocumentDto.class);
	}

	
	private static final Logger logger = LoggerFactory.getLogger(MetaDocumentServiceImpl.class);

}