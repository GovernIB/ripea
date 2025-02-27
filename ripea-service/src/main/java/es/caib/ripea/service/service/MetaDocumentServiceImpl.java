package es.caib.ripea.service.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.caib.ripea.persistence.entity.ContingutEntity;
import es.caib.ripea.persistence.entity.DocumentEntity;
import es.caib.ripea.persistence.entity.EntitatEntity;
import es.caib.ripea.persistence.entity.ExpedientEntity;
import es.caib.ripea.persistence.entity.MetaDocumentEntity;
import es.caib.ripea.persistence.entity.MetaExpedientEntity;
import es.caib.ripea.persistence.entity.MetaExpedientTascaValidacioEntity;
import es.caib.ripea.persistence.entity.PinbalServeiEntity;
import es.caib.ripea.persistence.repository.DocumentRepository;
import es.caib.ripea.persistence.repository.MetaDocumentRepository;
import es.caib.ripea.persistence.repository.MetaExpedientRepository;
import es.caib.ripea.persistence.repository.MetaExpedientTascaValidacioRepository;
import es.caib.ripea.persistence.repository.PinbalServeiRepository;
import es.caib.ripea.service.helper.CacheHelper;
import es.caib.ripea.service.helper.ContingutHelper;
import es.caib.ripea.service.helper.ConversioTipusHelper;
import es.caib.ripea.service.helper.EntityComprovarHelper;
import es.caib.ripea.service.helper.MetaDocumentHelper;
import es.caib.ripea.service.helper.MetaExpedientHelper;
import es.caib.ripea.service.helper.MetaNodeHelper;
import es.caib.ripea.service.helper.PaginacioHelper;
import es.caib.ripea.service.helper.PluginHelper;
import es.caib.ripea.service.intf.dto.ContingutTipusEnumDto;
import es.caib.ripea.service.intf.dto.FitxerDto;
import es.caib.ripea.service.intf.dto.ItemValidacioTascaEnum;
import es.caib.ripea.service.intf.dto.MetaDocumentDto;
import es.caib.ripea.service.intf.dto.MetaDocumentTipusGenericEnumDto;
import es.caib.ripea.service.intf.dto.MultiplicitatEnumDto;
import es.caib.ripea.service.intf.dto.PaginaDto;
import es.caib.ripea.service.intf.dto.PaginacioParamsDto;
import es.caib.ripea.service.intf.dto.PinbalServeiDto;
import es.caib.ripea.service.intf.dto.PortafirmesDocumentTipusDto;
import es.caib.ripea.service.intf.exception.ExisteixenDocumentsException;
import es.caib.ripea.service.intf.exception.NotFoundException;
import es.caib.ripea.service.intf.service.MetaDocumentService;
import es.caib.ripea.service.intf.utils.Utils;

@Service
public class MetaDocumentServiceImpl implements MetaDocumentService {

	@Autowired private MetaDocumentRepository metaDocumentRepository;
	@Autowired private DocumentRepository documentRepository;
	@Autowired private ConversioTipusHelper conversioTipusHelper;
	@Autowired private MetaNodeHelper metaNodeHelper;
	@Autowired private ContingutHelper contenidorHelper;
	@Autowired private PaginacioHelper paginacioHelper;
	@Autowired private PluginHelper pluginHelper;
	@Autowired private EntityComprovarHelper entityComprovarHelper;
	@Autowired private MetaExpedientHelper metaExpedientHelper;
	@Autowired private MetaDocumentHelper metaDocumentHelper;
	@Autowired private MetaExpedientRepository metaExpedientRepository;
	@Autowired private CacheHelper cacheHelper;
	@Autowired private PinbalServeiRepository pinbalServeiRepository;
	@Autowired private MetaExpedientTascaValidacioRepository metaExpedientTascaValidacioRepository;

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

		PinbalServeiEntity pinbalServeiEntity = null;
		if (metaDocument.getPinbalServei()!=null && metaDocument.getPinbalServei().getId()!=null) {
			pinbalServeiEntity = pinbalServeiRepository.getOne(metaDocument.getPinbalServei().getId());
		}
		
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
				metaDocument.getPinbalFinalitat(),
				0).
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
				pinbalServei(pinbalServeiEntity).
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
		PinbalServeiEntity pinbalServeiEntity = null;
		if (metaDocument.getPinbalServei()!=null && metaDocument.getPinbalServei().getId()!=null) {
			pinbalServeiEntity = pinbalServeiRepository.getOne(metaDocument.getPinbalServei().getId());
		}
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
				pinbalServeiEntity,
				metaDocument.getPinbalFinalitat(),
				metaDocument.isPinbalUtilitzarCifOrgan());
		if (plantillaContingut != null) {
			if (Utils.isNotEmpty(plantillaContingut)) { // file was changed
				entity.updatePlantilla(
						plantillaNom,
						plantillaContentType,
						plantillaContingut);
			} else {
				// file was not changed
			}
		} else { // file was removed
			entity.updatePlantilla(
					null,
					null,
					null);
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
		
		logger.debug("Actualitzant meta-document existent ( entitatId=" + entitatId + ", metaDocument=" + metaDocument + ")");
		
		entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false, false, false);

		MetaDocumentEntity entity = entityComprovarHelper.comprovarMetaDocument(metaDocument.getId());
		
		PinbalServeiEntity pinbalServeiEntity = null;
		if (metaDocument.getPinbalServei()!=null && metaDocument.getPinbalServei().getId()!=null) {
			pinbalServeiEntity = pinbalServeiRepository.getOne(metaDocument.getPinbalServei().getId());
		}
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
				pinbalServeiEntity,
				metaDocument.getPinbalFinalitat(),
				metaDocument.isPinbalUtilitzarCifOrgan());
		
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
		
		//Eliminar les possibles validacions sobre el document
		List<MetaExpedientTascaValidacioEntity> validacionsDoc = metaExpedientTascaValidacioRepository.findByItemValidacioAndItemId(
				ItemValidacioTascaEnum.DOCUMENT,
				id);
		
		if (validacionsDoc!=null && validacionsDoc.size()>0) {
			metaExpedientTascaValidacioRepository.deleteAll(validacionsDoc);
		}
		
		metaDocumentRepository.delete(metaDocument);
		if (rolActual.equals("IPA_ORGAN_ADMIN")) {
			metaExpedientHelper.canviarRevisioADisseny(entitatId, metaExpedient.getId(), organId);
		}
		return conversioTipusHelper.convertir(
				metaDocument,
				MetaDocumentDto.class);
	}
	
	
	@Override
	@Transactional
	public void moveTo(
			Long entitatId,
			Long metaDocumentId,
			int posicio) throws NotFoundException {

		entityComprovarHelper.comprovarEntitatPerMetaExpedients(entitatId);
		MetaDocumentEntity metaDocument = metaDocumentRepository.getOne(metaDocumentId);
		
		List<MetaDocumentEntity> metaDocuments = metaDocumentRepository.findByMetaExpedientOrderByOrdreAsc(metaDocument.getMetaExpedient());
		moveTo(
				metaDocument,
				metaDocuments,
				posicio);
	}
	
	public void moveTo(
			MetaDocumentEntity elementToMove,
			List<MetaDocumentEntity> elements,
			int posicio) {
		
		int anteriorIndex = -1; 
		for (int i = 0; i < elements.size(); i++) {
			if (elements.get(i).getId().equals(elementToMove.getId())) {
				anteriorIndex = i;
				break;
			}
		}
		elements.add(
				posicio,
				elements.remove(anteriorIndex));
		for (int i = 0; i < elements.size(); i++) {
			elements.get(i).updateOrdre(i);
		}
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
		}
		return resposta;
	}
	
	
	@Transactional(readOnly = true)
	@Override
	public MetaDocumentDto findById(
			Long metaDocumentId) {
		logger.debug("Consulta del meta-document (" +
				"metaDocumentId=" + metaDocumentId + ")");

		MetaDocumentEntity metaDocument = entityComprovarHelper.comprovarMetaDocument(
				metaDocumentId);
		
		entityComprovarHelper.comprovarEntitat(
				metaDocument.getEntitat().getId(),
				false,
				false,
				false, 
				true, 
				false);
		
		
		MetaDocumentDto resposta = conversioTipusHelper.convertir(
				metaDocument,
				MetaDocumentDto.class);
		if (resposta != null) {
			metaNodeHelper.omplirMetaDadesPerMetaNode(resposta);
			resposta.setMetaExpedientId(metaDocument.getMetaExpedient() != null ? metaDocument.getMetaExpedient().getId() : null);
		}
		return resposta;
	}	
	
	@Transactional(readOnly = true)
	@Override
	public PinbalServeiDto findPinbalServei(Long metaDocumentId) {
		MetaDocumentEntity metaDocument = entityComprovarHelper.comprovarMetaDocument(metaDocumentId);
		return conversioTipusHelper.convertir(metaDocument.getPinbalServei(), PinbalServeiDto.class);
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
			entity = metaDocumentRepository.findByMetaExpedientNullAndCodi(codi);	
		}
		
		MetaDocumentDto resposta = conversioTipusHelper.convertir(
										entity,
										MetaDocumentDto.class);

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
							paginacioParams.getFiltre() != null ? paginacioParams.getFiltre() : "",
							paginacioHelper.toSpringDataPageable(paginacioParams)),
					MetaDocumentDto.class);
		} else {
			resposta = paginacioHelper.toPaginaDto(
					metaDocumentRepository.findByMetaExpedient(
							metaExpedient,
							paginacioParams.getFiltre() == null,
							paginacioParams.getFiltre() != null ? paginacioParams.getFiltre() : "",
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
							paginacioParams.getFiltre() != null ? paginacioParams.getFiltre() : "",
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
		entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				false,
				true, false, false);
		MetaDocumentEntity metaDocumentEntitiy = entityComprovarHelper.comprovarMetaDocument(id);
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
		ContingutEntity contingut = entityComprovarHelper.comprovarContingut(contingutId);
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
					contingutId);
			ExpedientEntity expedient = contenidorHelper.getExpedientSuperior(
					contingut,
					true,
					false,
					false, 
					null);
			metaDocuments = findMetaDocumentsDisponiblesPerCreacio(
					entitat,
					expedient, 
					null, 
					findAllMarkDisponiblesPerCreacio);
		} else {
			MetaExpedientEntity metaExpedient =  metaExpedientRepository.getOne(metaExpedientId);
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
				false, null);
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
		
		long t1 = System.currentTimeMillis();
		
		List<MetaDocumentEntity> metaDocuments = new ArrayList<MetaDocumentEntity>();
		
		// Dels meta-documents actius pel meta-expedient només deixa els que encara es poden afegir segons la multiplicitat.
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

		
    	if (expedient != null && cacheHelper.mostrarLogsRendiment())
    		logger.info("findMetaDocumentsDisponiblesPerCreacio time (" + expedient.getId() + "):  " + (System.currentTimeMillis() - t1) + " ms");
		
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
	
	
	@Transactional(readOnly = true)
	@Override
	public List<MetaDocumentDto> findByMetaExpedientAndFirmaSimpleWebActiva(
			Long entitatId,
			Long metaExpedientId) {

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
				metaDocumentRepository.findByMetaExpedientAndFirmaPortafirmesActiva(
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
			Long metaExpedientId) throws NotFoundException {
		
		
		MetaExpedientEntity metaExpedientEntity = metaExpedientRepository.getOne(metaExpedientId);
		
		entityComprovarHelper.comprovarEntitat(
				metaExpedientEntity.getEntitat().getId(),
				false,
				false,
				false, 
				true, 
				false);
		
		MetaDocumentEntity metaDocument = metaDocumentRepository.findByMetaExpedientAndPerDefecteTrue(metaExpedientEntity);
		return conversioTipusHelper.convertir(metaDocument, MetaDocumentDto.class);
	}

	
	private static final Logger logger = LoggerFactory.getLogger(MetaDocumentServiceImpl.class);

}
