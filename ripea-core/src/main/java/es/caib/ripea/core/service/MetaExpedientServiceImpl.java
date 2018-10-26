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

import es.caib.ripea.core.api.dto.MetaDocumentDto;
import es.caib.ripea.core.api.dto.MetaExpedientDto;
import es.caib.ripea.core.api.dto.PaginaDto;
import es.caib.ripea.core.api.dto.PaginacioParamsDto;
import es.caib.ripea.core.api.dto.PermisDto;
import es.caib.ripea.core.api.service.MetaExpedientService;
import es.caib.ripea.core.entity.EntitatEntity;
import es.caib.ripea.core.entity.MetaDocumentEntity;
import es.caib.ripea.core.entity.MetaExpedientEntity;
import es.caib.ripea.core.entity.MetaNodeEntity;
import es.caib.ripea.core.helper.ConversioTipusHelper;
import es.caib.ripea.core.helper.EntityComprovarHelper;
import es.caib.ripea.core.helper.MetaNodeHelper;
import es.caib.ripea.core.helper.PaginacioHelper;
import es.caib.ripea.core.helper.PermisosHelper;
import es.caib.ripea.core.helper.PermisosHelper.ObjectIdentifierExtractor;
import es.caib.ripea.core.repository.EntitatRepository;
import es.caib.ripea.core.repository.MetaDadaRepository;
import es.caib.ripea.core.repository.MetaDocumentRepository;
import es.caib.ripea.core.repository.MetaExpedientRepository;
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
					entitat,
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
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);
		MetaExpedientEntity metaExpedientEntity = entityComprovarHelper.comprovarMetaExpedient(
				entitat,
				metaExpedient.getId(),
				false,
				false,
				false,
				false);
		MetaExpedientEntity metaExpedientPare = null;
		if (metaExpedient.getPareId() != null) {
			metaExpedientPare = entityComprovarHelper.comprovarMetaExpedient(
					entitat,
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
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);
		MetaExpedientEntity metaExpedient = entityComprovarHelper.comprovarMetaExpedient(
				entitat,
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
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);
		MetaExpedientEntity metaExpedient = entityComprovarHelper.comprovarMetaExpedient(
				entitat,
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
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);
		MetaExpedientEntity metaExpedient = entityComprovarHelper.comprovarMetaExpedient(
				entitat,
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
	public PaginaDto<MetaExpedientDto> findByEntitat(
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

	@Transactional
	@Override
	public List<PermisDto> permisFind(
			Long entitatId,
			Long id) {
		logger.debug("Consulta dels permisos del meta-expedient ("
				+ "entitatId=" + entitatId +  ", "
				+ "id=" + id +  ")"); 
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);
		entityComprovarHelper.comprovarMetaExpedient(
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
	public void permisUpdate(
			Long entitatId,
			Long id,
			PermisDto permis) {
		logger.debug("Modificació del permis del meta-expedient ("
				+ "entitatId=" + entitatId +  ", "
				+ "id=" + id + ", "
				+ "permis=" + permis + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);
		entityComprovarHelper.comprovarMetaExpedient(
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
	public void permisDelete(
			Long entitatId,
			Long id,
			Long permisId) {
		logger.debug("Eliminació del permis del meta-expedient ("
				+ "entitatId=" + entitatId +  ", "
				+ "id=" + id + ", "
				+ "permisId=" + permisId + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);
		entityComprovarHelper.comprovarMetaExpedient(
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



	private void omplirMetaDocumentsPerMetaExpedients(
			List<MetaExpedientDto> metaExpedients) {
		List<Long> metaExpedientIds = new ArrayList<Long>();
		for (MetaExpedientDto metaExpedient: metaExpedients) {
			metaExpedientIds.add(metaExpedient.getId());
		}
		List<MetaDocumentEntity> metaDocumentsDelsMetaExpedients = null;
		// Si passam una llista buida dona un error a la consulta.
		if (metaExpedientIds.size() > 0) {
			metaDocumentsDelsMetaExpedients = metaDocumentRepository.findByMetaExpedientIdIn(metaExpedientIds);
		}
		for (MetaExpedientDto metaExpedient: metaExpedients) {
			List<MetaDocumentDto> metaDocuments = new ArrayList<MetaDocumentDto>();
			if (metaDocumentsDelsMetaExpedients != null) {
				for (MetaDocumentEntity metaDocument: metaDocumentsDelsMetaExpedients) {
					if (metaDocument.getMetaExpedient().getId().equals(metaExpedient.getId())) {
						metaDocuments.add(conversioTipusHelper.convertir(
								metaDocument,
								MetaDocumentDto.class));
					}
				}
			}
			metaExpedient.setMetaDocuments(metaDocuments);
		}
	}
	private void omplirMetaDocumentsPerMetaExpedient(
			MetaExpedientEntity metaExpedient,
			MetaExpedientDto dto) {
		List<MetaDocumentEntity> metaDocumentsDelMetaExpedient = metaDocumentRepository.findByMetaExpedient(
				metaExpedient);
		List<MetaDocumentDto> metaDocuments = new ArrayList<MetaDocumentDto>();
		for (MetaDocumentEntity metaDocument: metaDocumentsDelMetaExpedient) {
			metaDocuments.add(conversioTipusHelper.convertir(
					metaDocument,
					MetaDocumentDto.class));
		}
		dto.setMetaDocuments(metaDocuments);
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