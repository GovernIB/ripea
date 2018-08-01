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

import es.caib.ripea.core.api.dto.MetaDadaDto;
import es.caib.ripea.core.api.dto.MetaExpedientDto;
import es.caib.ripea.core.api.dto.MetaExpedientMetaDocumentDto;
import es.caib.ripea.core.api.dto.MetaNodeMetaDadaDto;
import es.caib.ripea.core.api.dto.MultiplicitatEnumDto;
import es.caib.ripea.core.api.dto.PaginaDto;
import es.caib.ripea.core.api.dto.PaginacioParamsDto;
import es.caib.ripea.core.api.dto.PermisDto;
import es.caib.ripea.core.api.exception.NotFoundException;
import es.caib.ripea.core.api.service.MetaExpedientService;
import es.caib.ripea.core.entity.EntitatEntity;
import es.caib.ripea.core.entity.MetaDadaEntity;
import es.caib.ripea.core.entity.MetaDocumentEntity;
import es.caib.ripea.core.entity.MetaExpedientEntity;
import es.caib.ripea.core.entity.MetaExpedientMetaDocumentEntity;
import es.caib.ripea.core.entity.MetaNodeEntity;
import es.caib.ripea.core.entity.MetaNodeMetaDadaEntity;
import es.caib.ripea.core.helper.ConversioTipusHelper;
import es.caib.ripea.core.helper.EntityComprovarHelper;
import es.caib.ripea.core.helper.MetaNodeHelper;
import es.caib.ripea.core.helper.PaginacioHelper;
import es.caib.ripea.core.helper.PermisosHelper;
import es.caib.ripea.core.helper.PermisosHelper.ObjectIdentifierExtractor;
import es.caib.ripea.core.repository.EntitatRepository;
import es.caib.ripea.core.repository.MetaDadaRepository;
import es.caib.ripea.core.repository.MetaDocumentRepository;
import es.caib.ripea.core.repository.MetaExpedientMetaDocumentRepository;
import es.caib.ripea.core.repository.MetaExpedientRepository;
import es.caib.ripea.core.repository.MetaNodeMetaDadaRepository;
import es.caib.ripea.core.security.ExtendedPermission;

/**
 * Implementació del servei de gestió de meta-expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class MetaExpedientServiceImpl implements MetaExpedientService {

	@Resource
	private MetaExpedientRepository metaExpedientRepository;
	@Resource
	private EntitatRepository entitatRepository;
	@Resource
	private MetaDadaRepository metaDadaRepository;
	@Resource
	private MetaDocumentRepository metaDocumentRepository;
	@Resource
	private MetaExpedientMetaDocumentRepository metaExpedientMetaDocumentRepository;
	@Resource
	private MetaNodeMetaDadaRepository metaNodeMetaDadaRepository;

	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	@Resource
	private MetaNodeHelper metaNodeHelper;
	@Resource
	private PaginacioHelper paginacioHelper;
	@Resource
	private PermisosHelper permisosHelper;
	@Resource
	private EntityComprovarHelper entityComprovarHelper;



	@Transactional
	@Override
	public MetaExpedientDto create(
			Long entitatId,
			MetaExpedientDto metaExpedient) {
		logger.debug("Creant un nou meta-expedient ("
				+ "entitatId=" + entitatId + ", "
				+ "metaExpedient=" + metaExpedient + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);
		MetaExpedientEntity metaExpedientPare = null;
		if (metaExpedient.getPareId() != null) {
			metaExpedientPare = entityComprovarHelper.comprovarMetaExpedient(
					entitatId,
					metaExpedient.getPareId(),
					false,
					false,
					false,
					false);
		}
		MetaExpedientEntity entity = MetaExpedientEntity.getBuilder(
				metaExpedient.getCodi(),
				metaExpedient.getNom(),
				metaExpedient.getDescripcio(),
				metaExpedient.getClassificacioSia(),
				metaExpedient.getSerieDocumental(),
				metaExpedient.isNotificacioActiva(),
				entitat,
				metaExpedientPare).
				notificacioSeuProcedimentCodi(metaExpedient.getNotificacioSeuProcedimentCodi()).
				notificacioSeuRegistreLlibre(metaExpedient.getNotificacioSeuRegistreLlibre()).
				notificacioSeuRegistreOficina(metaExpedient.getNotificacioSeuRegistreOficina()).
				notificacioSeuRegistreOrgan(metaExpedient.getNotificacioSeuRegistreOrgan()).
				notificacioSeuExpedientUnitatOrganitzativa(metaExpedient.getNotificacioSeuExpedientUnitatOrganitzativa()).
				notificacioAvisTitol(metaExpedient.getNotificacioAvisTitol()).
				notificacioAvisText(metaExpedient.getNotificacioAvisText()).
				notificacioAvisTextMobil(metaExpedient.getNotificacioAvisTextMobil()).
				notificacioOficiTitol(metaExpedient.getNotificacioOficiTitol()).
				notificacioOficiText(metaExpedient.getNotificacioOficiText()).
				build();
		return conversioTipusHelper.convertir(
				metaExpedientRepository.save(entity),
				MetaExpedientDto.class);
	}

	@Transactional
	@Override
	public MetaExpedientDto update(
			Long entitatId,
			MetaExpedientDto metaExpedient) {
		logger.debug("Actualitzant meta-expedient existent ("
				+ "entitatId=" + entitatId + ", "
				+ "metaExpedient=" + metaExpedient + ")");
		MetaExpedientEntity metaExpedientEntity = entityComprovarHelper.comprovarMetaExpedient(
				entitatId,
				metaExpedient.getId(),
				false,
				false,
				false,
				false);
		MetaExpedientEntity metaExpedientPare = null;
		if (metaExpedient.getPareId() != null) {
			metaExpedientPare = entityComprovarHelper.comprovarMetaExpedient(
					entitatId,
					metaExpedient.getPareId(),
					false,
					false,
					false,
					false);
		}
		metaExpedientEntity.update(
				metaExpedient.getCodi(),
				metaExpedient.getNom(),
				metaExpedient.getDescripcio(),
				metaExpedient.getClassificacioSia(),
				metaExpedient.getSerieDocumental(),
				metaExpedient.isNotificacioActiva(),
				metaExpedient.getNotificacioSeuProcedimentCodi(),
				metaExpedient.getNotificacioSeuRegistreLlibre(),
				metaExpedient.getNotificacioSeuRegistreOficina(),
				metaExpedient.getNotificacioSeuRegistreOrgan(),
				metaExpedient.getNotificacioSeuExpedientUnitatOrganitzativa(),
				metaExpedient.getNotificacioAvisTitol(),
				metaExpedient.getNotificacioAvisText(),
				metaExpedient.getNotificacioAvisTextMobil(),
				metaExpedient.getNotificacioOficiTitol(),
				metaExpedient.getNotificacioOficiText(),
				metaExpedientPare);
		return conversioTipusHelper.convertir(
				metaExpedientEntity,
				MetaExpedientDto.class);
	}

	@Transactional
	@Override
	public MetaExpedientDto updateActiu(
			Long entitatId,
			Long id,
			boolean actiu) {
		logger.debug("Actualitzant propietat activa d'un meta-expedient existent ("
				+ "entitatId=" + entitatId + ", "
				+ "id=" + id + ", "
				+ "actiu=" + actiu + ")");
		MetaExpedientEntity metaExpedient = entityComprovarHelper.comprovarMetaExpedient(
				entitatId,
				id,
				false,
				false,
				false,
				false);
		metaExpedient.updateActiu(actiu);
		return conversioTipusHelper.convertir(
				metaExpedient,
				MetaExpedientDto.class);
	}

	@Transactional
	@Override
	public MetaExpedientDto delete(
			Long entitatId,
			Long id) {
		logger.debug("Esborrant meta-expedient (id=" + id +  ")");
		MetaExpedientEntity metaExpedient = entityComprovarHelper.comprovarMetaExpedient(
				entitatId,
				id,
				false,
				false,
				false,
				false);
		metaExpedientRepository.delete(metaExpedient);
		return conversioTipusHelper.convertir(
				metaExpedient,
				MetaExpedientDto.class);
	}

	@Transactional(readOnly = true)
	@Override
	public MetaExpedientDto findById(
			Long entitatId,
			Long id) {
		logger.debug("Consulta del meta-expedient ("
				+ "entitatId=" + entitatId + ", "
				+ "id=" + id + ")");
		MetaExpedientEntity metaExpedient = entityComprovarHelper.comprovarMetaExpedient(
				entitatId,
				id,
				false,
				false,
				false,
				false);
		MetaExpedientDto resposta = conversioTipusHelper.convertir(
				metaExpedient,
				MetaExpedientDto.class);
		if (resposta != null) {
			metaNodeHelper.omplirMetaDadesPerMetaNode(resposta);
			metaNodeHelper.omplirPermisosPerMetaNode(resposta, false);
			omplirMetaDocumentsPerMetaExpedient(
					metaExpedient,
					resposta);
		}
		return resposta;
	}

	@Transactional(readOnly = true)
	@Override
	public MetaExpedientDto findByEntitatCodi(
			Long entitatId,
			String codi) {
		logger.debug("Consulta del meta-expedient per entitat i codi ("
				+ "entitatId=" + entitatId + ", "
				+ "codi=" + codi + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);
		MetaExpedientEntity metaExpedient = metaExpedientRepository.findByEntitatAndCodi(
				entitat,
				codi);
		MetaExpedientDto resposta = conversioTipusHelper.convertir(
				metaExpedient,
				MetaExpedientDto.class);
		if (resposta != null) {
			metaNodeHelper.omplirMetaDadesPerMetaNode(resposta);
			metaNodeHelper.omplirPermisosPerMetaNode(resposta, false);
			omplirMetaDocumentsPerMetaExpedient(
					metaExpedient,
					resposta);
		}
		return resposta;
	}

	@Transactional(readOnly = true)
	@Override
	public PaginaDto<MetaExpedientDto> findByEntitatPaginat(
			Long entitatId,
			PaginacioParamsDto paginacioParams) {
		logger.debug("Consulta paginada dels meta-expedients de l'entitat (entitatId=" + entitatId + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);
		PaginaDto<MetaExpedientDto> resposta;
		if (paginacioHelper.esPaginacioActivada(paginacioParams)) {
			resposta = paginacioHelper.toPaginaDto(
					metaExpedientRepository.findByEntitat(
							entitat,
							paginacioHelper.toSpringDataPageable(paginacioParams)),
					MetaExpedientDto.class);
		} else {
			resposta = paginacioHelper.toPaginaDto(
					metaExpedientRepository.findByEntitat(
							entitat,
							paginacioHelper.toSpringDataSort(paginacioParams)),
					MetaExpedientDto.class);
		}
		metaNodeHelper.omplirMetaDadesPerMetaNodes(resposta.getContingut());
		omplirMetaDocumentsPerMetaExpedients(resposta.getContingut());
		metaNodeHelper.omplirPermisosPerMetaNodes(
				resposta.getContingut(),
				true);
		return resposta;
	}

	@Transactional
	@Override
	public void metaDadaCreate(
			Long entitatId,
			Long id,
			Long metaDadaId,
			MultiplicitatEnumDto multiplicitat,
			boolean readOnly) {
		logger.debug("Afegint meta-dada al meta-expedient ("
				+ "entitatId=" + entitatId +  ", "
				+ "id=" + id +  ", "
				+ "metaDadaId=" + metaDadaId +  ","
				+ "multiplicitat=" + multiplicitat +  ", "
				+ "readOnly=" + readOnly +  ")");
		MetaExpedientEntity metaExpedient = entityComprovarHelper.comprovarMetaExpedient(
				entitatId,
				id,
				false,
				false,
				false,
				false);
		MetaDadaEntity metaDada = entityComprovarHelper.comprovarMetaDada(
				metaExpedient.getEntitat(),
				metaDadaId);
		metaExpedient.metaDadaAdd(
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
		logger.debug("Actualitzant meta-dada del meta-expedient ("
				+ "entitatId=" + entitatId +  ", "
				+ "id=" + id +  ", "
				+ "metaNodeMetaDadaId=" + metaNodeMetaDadaId +  ","
				+ "multiplicitat=" + multiplicitat +  ", "
				+ "readOnly=" + readOnly +  ")");
		MetaExpedientEntity metaExpedient = entityComprovarHelper.comprovarMetaExpedient(
				entitatId,
				id,
				false,
				false,
				false,
				false);
		MetaNodeMetaDadaEntity metaNodeMetaDada = entityComprovarHelper.comprovarMetaNodeMetaDada(
				metaExpedient.getEntitat(),
				metaExpedient,
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
			Long metaExpedientMetaDadaId) {
		logger.debug("Esborrant meta-dada al meta-expedient ("
				+ "entitatId=" + entitatId +  ", "
				+ "id=" + id +  ", "
				+ "metaExpedientMetaDadaId=" + metaExpedientMetaDadaId +  ")");
		MetaExpedientEntity metaExpedient = entityComprovarHelper.comprovarMetaExpedient(
				entitatId,
				id,
				false,
				false,
				false,
				false);
		MetaNodeMetaDadaEntity metaExpedientMetaDada = entityComprovarHelper.comprovarMetaNodeMetaDada(
				metaExpedient.getEntitat(),
				metaExpedient,
				metaExpedientMetaDadaId);
		metaExpedient.metaDadaDelete(metaExpedientMetaDada);
		metaNodeHelper.reordenarMetaDades(metaExpedient);
	}

	@Transactional
	@Override
	public void metaDadaMoveUp(
			Long entitatId,
			Long id,
			Long metaExpedientMetaDadaId) {
		logger.debug("Movent meta-dada al meta-expedient cap amunt ("
				+ "entitatId=" + entitatId +  ", "
				+ "id=" + id +  ", "
				+ "metaExpedientMetaDadaId=" + metaExpedientMetaDadaId +  ")");
		MetaExpedientEntity metaExpedient = entityComprovarHelper.comprovarMetaExpedient(
				entitatId,
				id,
				false,
				false,
				false,
				false);
		MetaNodeMetaDadaEntity metaExpedientMetaDada = entityComprovarHelper.comprovarMetaNodeMetaDada(
				metaExpedient.getEntitat(),
				metaExpedient,
				metaExpedientMetaDadaId);
		metaNodeHelper.moureMetaNodeMetaDada(
				metaExpedient,
				metaExpedientMetaDada,
				metaExpedientMetaDada.getOrdre() - 1);
	}

	@Transactional
	@Override
	public void metaDadaMoveDown(
			Long entitatId,
			Long id,
			Long metaExpedientMetaDadaId) {
		logger.debug("Movent meta-dada al meta-expedient cap avall ("
				+ "entitatId=" + entitatId +  ", "
				+ "id=" + id +  ", "
				+ "metaExpedientMetaDadaId=" + metaExpedientMetaDadaId +  ")");
		MetaExpedientEntity metaExpedient = entityComprovarHelper.comprovarMetaExpedient(
				entitatId,
				id,
				false,
				false,
				false,
				false);
		MetaNodeMetaDadaEntity metaExpedientMetaDada = entityComprovarHelper.comprovarMetaNodeMetaDada(
				metaExpedient.getEntitat(),
				metaExpedient,
				metaExpedientMetaDadaId);
		metaNodeHelper.moureMetaNodeMetaDada(
				metaExpedient,
				metaExpedientMetaDada,
				metaExpedientMetaDada.getOrdre() + 1);
	}

	@Transactional
	@Override
	public void metaDadaMoveTo(
			Long entitatId,
			Long id,
			Long metaExpedientMetaDadaId,
			int posicio) {
		logger.debug("Movent meta-dada al meta-expedient ("
				+ "entitatId=" + entitatId +  ", "
				+ "id=" + id +  ", "
				+ "metaExpedientMetaDadaId=" + metaExpedientMetaDadaId +  ", "
				+ "posicio=" + posicio +  ")");
		MetaExpedientEntity metaExpedient = entityComprovarHelper.comprovarMetaExpedient(
				entitatId,
				id,
				false,
				false,
				false,
				false);
		MetaNodeMetaDadaEntity metaExpedientMetaDada = entityComprovarHelper.comprovarMetaNodeMetaDada(
				metaExpedient.getEntitat(),
				metaExpedient,
				metaExpedientMetaDadaId);
		metaNodeHelper.moureMetaNodeMetaDada(
				metaExpedient,
				metaExpedientMetaDada,
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
		MetaExpedientEntity metaExpedient = entityComprovarHelper.comprovarMetaExpedient(
				entitatId,
				id,
				false,
				false,
				false,
				false);
		MetaNodeMetaDadaEntity metaNodeMetaDada = entityComprovarHelper.comprovarMetaNodeMetaDada(
				metaExpedient.getEntitat(),
				metaExpedient,
				metaNodeMetaDadaId);
		return conversioTipusHelper.convertir(
				metaNodeMetaDada,
				MetaNodeMetaDadaDto.class);
	}

	public List<MetaDadaDto> metaDadaFindGlobals(
			Long entitatId) throws NotFoundException {
		logger.debug("Cercant les meta-dades globals del meta-expedient ("
				+ "entitatId=" + entitatId +  ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);
		return conversioTipusHelper.convertirList(
				metaDadaRepository.findByEntitatAndGlobalExpedientTrueOrderByIdAsc(entitat),
				MetaDadaDto.class);
	}

	@Transactional
	@Override
	public void metaDocumentCreate(
			Long entitatId,
			Long id,
			Long metaDocumentId,
			MultiplicitatEnumDto multiplicitat,
			boolean readOnly) {
		logger.debug("Afegint meta-document al meta-expedient ("
				+ "entitatId=" + entitatId +  ", "
				+ "id=" + id +  ", "
				+ "metaDocumentId=" + metaDocumentId +  ", "
				+ "multiplicitat=" + multiplicitat +  ", "
				+ "readOnly=" + readOnly +  ")");
		MetaExpedientEntity metaExpedient = entityComprovarHelper.comprovarMetaExpedient(
				entitatId,
				id,
				false,
				false,
				false,
				false);
		MetaDocumentEntity metaDocument = entityComprovarHelper.comprovarMetaDocument(
				metaExpedient.getEntitat(),
				metaDocumentId,
				false,
				false,
				false,
				false);
		metaExpedient.metaDocumentAdd(
				metaDocument,
				multiplicitat,
				readOnly);
	}

	@Transactional
	@Override
	public void metaDocumentUpdate(
			Long entitatId,
			Long id,
			Long metaExpedientMetaDocumentId,
			MultiplicitatEnumDto multiplicitat,
			boolean readOnly) {
		logger.debug("Actualitzant meta-document del meta-expedient ("
				+ "entitatId=" + entitatId +  ", "
				+ "id=" + id +  ", "
				+ "metaNodeMetaDadaId=" + metaExpedientMetaDocumentId +  ","
				+ "multiplicitat=" + multiplicitat +  ", "
				+ "readOnly=" + readOnly +  ")");
		MetaExpedientEntity metaExpedient = entityComprovarHelper.comprovarMetaExpedient(
				entitatId,
				id,
				false,
				false,
				false,
				false);
		MetaExpedientMetaDocumentEntity metaExpedientMetaDocument = entityComprovarHelper.comprovarMetaExpedientMetaDocument(
				metaExpedient.getEntitat(),
				metaExpedient,
				metaExpedientMetaDocumentId);
		metaExpedientMetaDocument.update(
				multiplicitat,
				readOnly);
	}

	@Transactional
	@Override
	public void metaDocumentDelete(
			Long entitatId,
			Long id,
			Long metaExpedientMetaDocumentId) {
		logger.debug("Afegint meta-document al meta-expedient ("
				+ "entitatId=" + entitatId +  ", "
				+ "id=" + id +  ", "
				+ "metaExpedientMetaDocumentId=" + metaExpedientMetaDocumentId + ")");
		MetaExpedientEntity metaExpedient = entityComprovarHelper.comprovarMetaExpedient(
				entitatId,
				id,
				false,
				false,
				false,
				false);
		MetaExpedientMetaDocumentEntity metaExpedientMetaDocument = entityComprovarHelper.comprovarMetaExpedientMetaDocument(
				metaExpedient.getEntitat(),
				metaExpedient,
				metaExpedientMetaDocumentId);
		metaExpedient.metaDocumentDelete(metaExpedientMetaDocument);
		reordenarMetaDocuments(metaExpedient);
	}

	@Transactional
	@Override
	public void metaDocumentMove(
			Long entitatId,
			Long id,
			Long metaExpedientMetaDocumentId,
			int posicio) {
		logger.debug("Movent meta-document al meta-expedient ("
				+ "entitatId=" + entitatId +  ", "
				+ "id=" + id +  ", "
				+ "metaExpedientMetaDocumentId=" + metaExpedientMetaDocumentId +  ", "
				+ "posicio=" + posicio +  ")");
		MetaExpedientEntity metaExpedient = entityComprovarHelper.comprovarMetaExpedient(
				entitatId,
				id,
				false,
				false,
				false,
				false);
		MetaExpedientMetaDocumentEntity metaExpedientMetaDocument = entityComprovarHelper.comprovarMetaExpedientMetaDocument(
				metaExpedient.getEntitat(),
				metaExpedient,
				metaExpedientMetaDocumentId);
		moureMetaDocument(
				metaExpedient,
				metaExpedientMetaDocument,
				posicio);
	}

	@Transactional(readOnly = true)
	@Override
	public MetaExpedientMetaDocumentDto findMetaDocument(
			Long entitatId,
			Long id,
			Long metaExpedientMetaDocumentId) {
		logger.debug("Cercant el meta-document del meta-expedient ("
				+ "entitatId=" + entitatId +  ", "
				+ "id=" + id +  ", "
				+ "metaExpedientMetaDocumentId=" + metaExpedientMetaDocumentId +  ")");
		MetaExpedientEntity metaExpedient = entityComprovarHelper.comprovarMetaExpedient(
				entitatId,
				id,
				false,
				false,
				false,
				false);
		MetaExpedientMetaDocumentEntity metaExpedientMetaDocument = entityComprovarHelper.comprovarMetaExpedientMetaDocument(
				metaExpedient.getEntitat(),
				metaExpedient,
				metaExpedientMetaDocumentId);
		return conversioTipusHelper.convertir(
				metaExpedientMetaDocument,
				MetaExpedientMetaDocumentDto.class);
	}

	@Transactional
	@Override
	public List<PermisDto> findPermis(
			Long entitatId,
			Long id) {
		logger.debug("Consulta dels permisos del meta-expedient ("
				+ "entitatId=" + entitatId +  ", "
				+ "id=" + id +  ")"); 
		entityComprovarHelper.comprovarMetaExpedient(
				entitatId,
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
		logger.debug("Modificació del permis del meta-expedient ("
				+ "entitatId=" + entitatId +  ", "
				+ "id=" + id + ", "
				+ "permis=" + permis + ")");
		entityComprovarHelper.comprovarMetaExpedient(
				entitatId,
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
		logger.debug("Eliminació del permis del meta-expedient ("
				+ "entitatId=" + entitatId +  ", "
				+ "id=" + id + ", "
				+ "permisId=" + permisId + ")");
		entityComprovarHelper.comprovarMetaExpedient(
				entitatId,
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
	public List<MetaExpedientDto> findByEntitat(
			Long entitatId) {
		logger.debug("Consulta de meta-expedients de l'entitat ("
				+ "entitatId=" + entitatId +  ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);
		List<MetaExpedientEntity> metaExpedients = metaExpedientRepository.findByEntitatOrderByNomAsc(
				entitat);
		return conversioTipusHelper.convertirList(
				metaExpedients,
				MetaExpedientDto.class);
	}

	@Transactional(readOnly = true)
	@Override
	public List<MetaExpedientDto> findActiusAmbEntitatPerAdmin(
			Long entitatId) {
		logger.debug("Consulta de meta-expedients actius de l'entitat per admins ("
				+ "entitatId=" + entitatId +  ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);
		return conversioTipusHelper.convertirList(
				metaExpedientRepository.findByEntitatAndActiuTrueOrderByNomAsc(entitat),
				MetaExpedientDto.class);
	}

	@Transactional(readOnly = true)
	@Override
	public List<MetaExpedientDto> findActiusAmbEntitatPerCreacio(
			Long entitatId) {
		logger.debug("Consulta de meta-expedients actius de l'entitat amb el permis CREATE ("
				+ "entitatId=" + entitatId +  ")");
		return findActiusAmbEntitatPermis(
				entitatId, 
				new Permission[] {ExtendedPermission.CREATE});
	}

	@Transactional(readOnly = true)
	@Override
	public List<MetaExpedientDto> findActiusAmbEntitatPerModificacio(
			Long entitatId) {
		logger.debug("Consulta de meta-expedients actius de l'entitat amb el permis WRITE ("
				+ "entitatId=" + entitatId +  ")");
		return findActiusAmbEntitatPermis(
				entitatId, 
				new Permission[] {ExtendedPermission.WRITE});
	}	

	@Transactional(readOnly = true)
	@Override
	public List<MetaExpedientDto> findActiusAmbEntitatPerLectura(
			Long entitatId) {
		logger.debug("Consulta de meta-expedients de l'entitat amb el permis READ ("
				+ "entitatId=" + entitatId +  ")");
		return findActiusAmbEntitatPermis(
				entitatId, 
				new Permission[] {ExtendedPermission.READ});
	}



	private void omplirMetaDocumentsPerMetaExpedients(
			List<MetaExpedientDto> metaExpedients) {
		List<Long> metaExpedientIds = new ArrayList<Long>();
		for (MetaExpedientDto metaExpedient: metaExpedients)
			metaExpedientIds.add(metaExpedient.getId());
		List<MetaExpedientMetaDocumentEntity> metaExpedientMetaDocuments = null;
		// Si passam una llista buida dona un error a la consulta.
		if (metaExpedientIds.size() > 0)
			metaExpedientMetaDocuments = metaExpedientMetaDocumentRepository.findByMetaExpedientIdIn(metaExpedientIds);
		for (MetaExpedientDto metaExpedient: metaExpedients) {
			List<MetaExpedientMetaDocumentDto> metaDocuments = new ArrayList<MetaExpedientMetaDocumentDto>();
			if (metaExpedientMetaDocuments != null) {
				for (MetaExpedientMetaDocumentEntity metaExpedientMetaDocument: metaExpedientMetaDocuments) {
					if (metaExpedientMetaDocument.getMetaExpedient().getId().equals(metaExpedient.getId())) {
						metaDocuments.add(conversioTipusHelper.convertir(
								metaExpedientMetaDocument,
								MetaExpedientMetaDocumentDto.class));
					}
				}
			}
			metaExpedient.setMetaDocuments(metaDocuments);
		}
	}
	private void omplirMetaDocumentsPerMetaExpedient(
			MetaExpedientEntity metaExpedient,
			MetaExpedientDto dto) {
		List<MetaExpedientMetaDocumentEntity> metaExpedientMetaDocuments = metaExpedientMetaDocumentRepository.findByMetaExpedient(
				metaExpedient);
		List<MetaExpedientMetaDocumentDto> metaDocuments = new ArrayList<MetaExpedientMetaDocumentDto>();
		for (MetaExpedientMetaDocumentEntity metaExpedientMetaDocument: metaExpedientMetaDocuments) {
			metaDocuments.add(conversioTipusHelper.convertir(
					metaExpedientMetaDocument,
					MetaExpedientMetaDocumentDto.class));
		}
		dto.setMetaDocuments(metaDocuments);
	}
	private void moureMetaDocument(
			MetaExpedientEntity metaExpedient,
			MetaExpedientMetaDocumentEntity metaExpedientMetaDocument,
			int posicio) {
		List<MetaExpedientMetaDocumentEntity> metaExpedientMetaDocuments = metaExpedientMetaDocumentRepository.findByMetaExpedient(
				metaExpedient);
		int indexOrigen = -1;
		for (MetaExpedientMetaDocumentEntity memd: metaExpedientMetaDocuments) {
			if (memd.getId().equals(metaExpedientMetaDocument.getId())) {
				indexOrigen = memd.getOrdre();
				break;
			}
		}
		metaExpedientMetaDocuments.add(
				posicio,
				metaExpedientMetaDocuments.remove(indexOrigen));
		for (int i = 0; i < metaExpedientMetaDocuments.size(); i++)
			metaExpedientMetaDocuments.get(i).updateOrdre(i);
	}
	private void reordenarMetaDocuments(
			MetaExpedientEntity metaExpedient) {
		List<MetaExpedientMetaDocumentEntity> metaExpedientMetaDocuments = metaExpedientMetaDocumentRepository.findByMetaExpedient(
				metaExpedient);
		int ordre = 0;
		for (MetaExpedientMetaDocumentEntity metaExpedientMetaDocument: metaExpedientMetaDocuments)
			metaExpedientMetaDocument.updateOrdre(ordre++);
	}

	private List<MetaExpedientDto> findActiusAmbEntitatPermis(
			Long entitatId,
			Permission[] permisos) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				false,
				true);
		List<MetaExpedientEntity> metaExpedients = metaExpedientRepository.findByEntitatAndActiuTrueOrderByNomAsc(entitat);
		permisosHelper.filterGrantedAll(
				metaExpedients,
				new ObjectIdentifierExtractor<MetaNodeEntity>() {
					public Long getObjectIdentifier(MetaNodeEntity metaNode) {
						return metaNode.getId();
					}
				},
				MetaNodeEntity.class,
				permisos,
				auth);
		return conversioTipusHelper.convertirList(
				metaExpedients,
				MetaExpedientDto.class);		
	}

	private static final Logger logger = LoggerFactory.getLogger(MetaExpedientServiceImpl.class);

}
