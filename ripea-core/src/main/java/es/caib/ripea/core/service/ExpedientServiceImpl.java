/**
 * 
 */
package es.caib.ripea.core.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.jopendocument.dom.spreadsheet.SpreadSheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Persistable;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.caib.distribucio.ws.backofficeintegracio.AnotacioRegistreId;
import es.caib.distribucio.ws.backofficeintegracio.Estat;
import es.caib.ripea.core.api.dto.ExpedientComentariDto;
import es.caib.ripea.core.api.dto.ExpedientDto;
import es.caib.ripea.core.api.dto.ExpedientEstatDto;
import es.caib.ripea.core.api.dto.ExpedientEstatEnumDto;
import es.caib.ripea.core.api.dto.ExpedientFiltreDto;
import es.caib.ripea.core.api.dto.ExpedientPeticioEstatEnumDto;
import es.caib.ripea.core.api.dto.ExpedientSelectorDto;
import es.caib.ripea.core.api.dto.FitxerDto;
import es.caib.ripea.core.api.dto.LogObjecteTipusEnumDto;
import es.caib.ripea.core.api.dto.LogTipusEnumDto;
import es.caib.ripea.core.api.dto.PaginaDto;
import es.caib.ripea.core.api.dto.PaginacioParamsDto;
import es.caib.ripea.core.api.exception.NotFoundException;
import es.caib.ripea.core.api.exception.ValidationException;
import es.caib.ripea.core.api.service.ExpedientService;
import es.caib.ripea.core.entity.ContingutEntity;
import es.caib.ripea.core.entity.DadaEntity;
import es.caib.ripea.core.entity.EntitatEntity;
import es.caib.ripea.core.entity.ExpedientComentariEntity;
import es.caib.ripea.core.entity.ExpedientEntity;
import es.caib.ripea.core.entity.ExpedientEstatEntity;
import es.caib.ripea.core.entity.ExpedientPeticioEntity;
import es.caib.ripea.core.entity.MetaDadaEntity;
import es.caib.ripea.core.entity.MetaExpedientEntity;
import es.caib.ripea.core.entity.MetaNodeEntity;
import es.caib.ripea.core.entity.RegistreAnnexEntity;
import es.caib.ripea.core.entity.UsuariEntity;
import es.caib.ripea.core.helper.ContingutHelper;
import es.caib.ripea.core.helper.ContingutLogHelper;
import es.caib.ripea.core.helper.ConversioTipusHelper;
import es.caib.ripea.core.helper.CsvHelper;
import es.caib.ripea.core.helper.DateHelper;
import es.caib.ripea.core.helper.DistribucioHelper;
import es.caib.ripea.core.helper.DocumentHelper;
import es.caib.ripea.core.helper.EmailHelper;
import es.caib.ripea.core.helper.EntityComprovarHelper;
import es.caib.ripea.core.helper.ExpedientHelper;
import es.caib.ripea.core.helper.ExpedientPeticioHelper;
import es.caib.ripea.core.helper.MessageHelper;
import es.caib.ripea.core.helper.PaginacioHelper;
import es.caib.ripea.core.helper.PaginacioHelper.Converter;
import es.caib.ripea.core.helper.PermisosHelper;
import es.caib.ripea.core.helper.PermisosHelper.ObjectIdentifierExtractor;
import es.caib.ripea.core.helper.PluginHelper;
import es.caib.ripea.core.helper.UsuariHelper;
import es.caib.ripea.core.repository.AlertaRepository;
import es.caib.ripea.core.repository.ContingutRepository;
import es.caib.ripea.core.repository.DadaRepository;
import es.caib.ripea.core.repository.ExpedientComentariRepository;
import es.caib.ripea.core.repository.ExpedientEstatRepository;
import es.caib.ripea.core.repository.ExpedientPeticioRepository;
import es.caib.ripea.core.repository.ExpedientRepository;
import es.caib.ripea.core.repository.MetaExpedientRepository;
import es.caib.ripea.core.security.ExtendedPermission;

/**
 * Implementació dels mètodes per a gestionar expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class ExpedientServiceImpl implements ExpedientService {

	@Autowired
	private MetaExpedientRepository metaExpedientRepository;
	@Autowired
	private ExpedientRepository expedientRepository;
	@Autowired
	private ExpedientComentariRepository expedientComentariRepository;
	@Autowired
	private ExpedientEstatRepository expedientEstatRepository;
	@Autowired
	private ExpedientPeticioRepository expedientPeticioRepository;
	@Autowired
	private DadaRepository dadaRepository;
	@Autowired
	private AlertaRepository alertaRepository;
	@Autowired
	private ContingutRepository contingutRepository;
	@Autowired
	private ExpedientHelper expedientHelper;
	@Autowired
	private ConversioTipusHelper conversioTipusHelper;
	@Autowired
	private PermisosHelper permisosHelper;
	@Autowired
	private ContingutHelper contingutHelper;
	@Autowired
	private PaginacioHelper paginacioHelper;
	@Autowired
	private UsuariHelper usuariHelper;
	@Autowired
	private EmailHelper emailHelper;
	@Autowired
	private PluginHelper pluginHelper;
	@Autowired
	private CsvHelper csvHelper;
	@Autowired
	private EntityComprovarHelper entityComprovarHelper;
	@Autowired
	private MessageHelper messageHelper;
	@Autowired
	private ExpedientPeticioHelper expedientPeticioHelper;
	@Autowired
	private ContingutLogHelper contingutLogHelper;
	@Autowired
	private DocumentHelper documentHelper;

	@Override
	public ExpedientDto create(
			Long entitatId,
			Long metaExpedientId,
			Long metaExpedientDominiId,
			Long pareId,
			Integer any,
			Long sequencia,
			String nom,
			Long expedientPeticioId,
			boolean associarInteressats) {
		logger.debug("Creant nou expedient (" +
				"entitatId=" + entitatId + ", " +
				"metaExpedientId=" + metaExpedientId + ", " +
				"pareId=" + pareId + ", " +
				"any=" + any + ", " +
				"sequencia=" + sequencia + ", " +
				"nom=" + nom + ", " +
				"expedientPeticioId=" + expedientPeticioId +")");
		// if expedient comes from distribucio
		ExpedientPeticioEntity expedientPeticioEntity = null;
		if (expedientPeticioId != null) {
			expedientPeticioEntity = expedientPeticioRepository.findOne(expedientPeticioId);
		}
		// create expedient in db and in arxiu
		ExpedientDto expedientDto = expedientHelper.create(
				entitatId,
				metaExpedientId,
				metaExpedientDominiId,
				pareId,				
				any,
				sequencia,
				nom,
				expedientPeticioId,
				associarInteressats);
		boolean processatOk = true;
		// if expedient comes from distribucio
		if (expedientPeticioId != null) {
			for (RegistreAnnexEntity registeAnnexEntity : expedientPeticioEntity.getRegistre().getAnnexos()) {
				try {
					expedientHelper.createDocFromAnnex(
							registeAnnexEntity.getId(),
							expedientPeticioEntity.getId());
				} catch (Exception e) {
					processatOk = false;
					logger.info(ExceptionUtils.getStackTrace(e));
					expedientHelper.updateRegistreAnnexError(registeAnnexEntity.getId(), ExceptionUtils.getStackTrace(e));
					
				}
			}
			canviEstatToProcessatPendent(expedientPeticioEntity);
			if (processatOk) {
				notificarICanviEstatToProcessatNotificat(expedientPeticioEntity.getId());
			}
		}
		expedientDto.setProcessatOk(processatOk);
		return expedientDto;
	}

	@Override
	public boolean incorporar(
			Long entitatId,
			Long expedientId,
			Long expedientPeticioId,
			boolean associarInteressats) {
		logger.debug("Incorporant a l'expedient existent (" +
				"entitatId=" + entitatId + ", " +
				"expedientId=" + expedientId + ", " +
				"expedientPeticioId=" + expedientPeticioId +  ")");


		ExpedientPeticioEntity expedientPeticioEntity = expedientPeticioRepository.findOne(expedientPeticioId);
		
		expedientHelper.relateExpedientWithPeticioAndSetAnnexosPendentNewTransaction(
				expedientPeticioId,
				expedientId);
		
		expedientHelper.associateInteressats(expedientId, entitatId, expedientPeticioId);

		boolean processatOk = true;
		for (RegistreAnnexEntity registeAnnexEntity : expedientPeticioEntity.getRegistre().getAnnexos()) {
			try {
				
				boolean throwException1 = false;
				if(throwException1)
					throw new RuntimeException("EXCEPION BEFORE INCORPORAR !!!!!! ");
				
				expedientHelper.createDocFromAnnex(
						registeAnnexEntity.getId(),
						expedientPeticioEntity.getId());

			} catch (Exception e) {
				processatOk = false;
				logger.error(ExceptionUtils.getStackTrace(e));
				expedientHelper.updateRegistreAnnexError(registeAnnexEntity.getId(), ExceptionUtils.getStackTrace(e));
			}
		}
		
		canviEstatToProcessatPendent(expedientPeticioEntity);
		
		if (processatOk) {
			notificarICanviEstatToProcessatNotificat(expedientPeticioEntity.getId());
		}	
		
		return processatOk;
	}	
	
	
	public void canviEstatToProcessatPendent(ExpedientPeticioEntity expedientPeticioEntity) {

		expedientPeticioHelper.canviEstatExpedientPeticio(
				expedientPeticioEntity.getId(),
				ExpedientPeticioEstatEnumDto.PROCESSAT_PENDENT);
		
	}
	
	
	public void notificarICanviEstatToProcessatNotificat(Long expedientPeticioId) {
			
		ExpedientPeticioEntity expedientPeticioEntity  = expedientPeticioRepository.findOne(expedientPeticioId);
		AnotacioRegistreId anotacioRegistreId = new AnotacioRegistreId();
		anotacioRegistreId.setClauAcces(expedientPeticioEntity.getClauAcces());
		anotacioRegistreId.setIndetificador(expedientPeticioEntity.getIdentificador());

		try {
			// change state of registre in DISTRIBUCIO to BACK_PROCESSADA
			DistribucioHelper.getBackofficeIntegracioServicePort().canviEstat(
					anotacioRegistreId,
					Estat.PROCESSADA,
					"");
			
			// change state of expedient peticion to processat and notificat to DISTRIBUCIO
			expedientPeticioHelper.canviEstatExpedientPeticio(
					expedientPeticioEntity.getId(),
					ExpedientPeticioEstatEnumDto.PROCESSAT_NOTIFICAT);
			
		} catch (Exception e) {
			expedientHelper.updateNotificarError(
					expedientPeticioEntity.getId(),
					ExceptionUtils.getStackTrace(e));
		}	
		
	}	
	
	
	@Override
	public boolean retryCreateDocFromAnnex(
			Long registreAnnexId,
			Long expedientPeticioId) {
		
		boolean processatOk = true;
		try {
			 expedientHelper.createDocFromAnnex(
					registreAnnexId,
					expedientPeticioId);
			 expedientHelper.updateRegistreAnnexError(registreAnnexId, null);
		} catch (Exception e) {
			processatOk = false;
			logger.debug(ExceptionUtils.getStackTrace(e));
			expedientHelper.updateRegistreAnnexError(registreAnnexId, ExceptionUtils.getStackTrace(e));
		}
		
		notificarICanviEstatToProcessatNotificat(expedientPeticioId);

		return processatOk;
	}
	
	
	@Override
	public boolean retryNotificarDistribucio(
			Long expedientPeticioId) {
		
		ExpedientPeticioEntity expedientPeticioEntity = new ExpedientPeticioEntity();
		
		boolean processatOk = true;
		try {
			expedientPeticioEntity = expedientPeticioRepository.findOne(expedientPeticioId);
			
			AnotacioRegistreId anotacioRegistreId = new AnotacioRegistreId();
			anotacioRegistreId.setClauAcces(expedientPeticioEntity.getClauAcces());
			anotacioRegistreId.setIndetificador(expedientPeticioEntity.getIdentificador());
		
			// change state of registre in DISTRIBUCIO to BACK_PROCESSADA
			DistribucioHelper.getBackofficeIntegracioServicePort().canviEstat(
					anotacioRegistreId,
					Estat.PROCESSADA,
					"");
			expedientPeticioEntity.updateNotificaDistError(null);
			
		} catch (Exception e) {
			expedientPeticioEntity.updateNotificaDistError(ExceptionUtils.getStackTrace(e));
			processatOk = false;
		}

		return processatOk;
	}
	
	
	
	

	@Transactional
	@Override
	public ExpedientDto update(
			Long entitatId,
			Long id,
			String nom
			) {
		logger.debug("Actualitzant dades de l'expedient ("
				+ "entitatId=" + entitatId + ", "
				+ "id=" + id + ", "
				+ "nom=" + nom + ")");
		contingutHelper.comprovarContingutDinsExpedientModificable(
				entitatId,
				id,
				false,
				true,
				false,
				false);
		ExpedientEntity expedient = entityComprovarHelper.comprovarExpedient(
				entitatId,
				id,
				false,
				false,
				true,
				false,
				false);
		contingutHelper.comprovarNomValid(
				expedient.getPare(),
				nom,
				id,
				ExpedientEntity.class);
		String nomOriginal = expedient.getNom();
		expedient.update(nom);
		contingutLogHelper.log(
				expedient,
				LogTipusEnumDto.MODIFICACIO,
				(!nomOriginal.equals(expedient.getNom())) ? expedient.getNom() : null,
				null,
				false,
				false);
		ExpedientDto dto = toExpedientDto(
				expedient,
				true);
		contingutHelper.arxiuPropagarModificacio(
				expedient,
				null,
				false,
				false,
				null);
		return dto;
	}
	
	@Transactional
	@Override
	public ExpedientDto update(
			Long entitatId,
			Long id,
			String nom,
			int any,
			Long metaExpedientDominiId
			) {
		logger.debug("Actualitzant dades de l'expedient ("
				+ "entitatId=" + entitatId + ", "
				+ "id=" + id + ", "
				+ "nom=" + nom + ")");
		contingutHelper.comprovarContingutDinsExpedientModificable(
				entitatId,
				id,
				false,
				true,
				false,
				false);
		ExpedientEntity expedient = entityComprovarHelper.comprovarExpedient(
				entitatId,
				id,
				false,
				false,
				true,
				false,
				false);
		contingutHelper.comprovarNomValid(
				expedient.getPare(),
				nom,
				id,
				ExpedientEntity.class);
		String nomOriginal = expedient.getNom();
		expedient.update(nom);
		contingutLogHelper.log(
				expedient,
				LogTipusEnumDto.MODIFICACIO,
				(!nomOriginal.equals(expedient.getNom())) ? expedient.getNom() : null,
				null,
				false,
				false);
		
		int anyOriginal = expedient.getAny();
		expedient.updateAny(any);
		contingutLogHelper.log(
				expedient,
				LogTipusEnumDto.MODIFICACIO,
				(anyOriginal!=(expedient.getAny())) ? String.valueOf(expedient.getAny()) : null,
				null,
				false,
				false);
		
		ExpedientDto dto = toExpedientDto(
				expedient,
				true);
		contingutHelper.arxiuPropagarModificacio(
				expedient,
				null,
				false,
				false,
				null);
		return dto;
	}


	@Transactional(readOnly = true)
	@Override
	public ExpedientDto findById(
			Long entitatId,
			Long id) {
		logger.debug("Obtenint l'expedient ("
				+ "entitatId=" + entitatId + ", "
				+ "id=" + id + ")");
		ExpedientEntity expedient = entityComprovarHelper.comprovarExpedient(
				entitatId,
				id,
				false,
				true,
				false,
				false,
				false);
		return toExpedientDto(
				expedient,
				true);
	}
	
	@Transactional(readOnly = true)
	public ExpedientDto findByMetaExpedientAndPareAndNomAndEsborrat(
			Long entitatId,
			Long metaExpedientId,
			Long pareId,
			String nom,
			int esborrat)
	{
		logger.debug("Consultant expedient ("
				+ "entitatId=" + entitatId + ", "
				+ "metaExpedientId=" + metaExpedientId + ", "
				+ "pareId=" + pareId + ", "
				+ "nom=" + nom + ", "
				+ "esborrat=" + esborrat + ")");
		
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				true,
				false,
				false);
		MetaExpedientEntity metaExpedient = entityComprovarHelper.comprovarMetaExpedient(
				entitat,
				metaExpedientId,
				false,
				false,
				true,
				false);
		
		ContingutEntity contingutPare = null;
		if (pareId != null) {
			contingutPare = contingutHelper.comprovarContingutDinsExpedientModificable(
					entitatId,
					pareId,
					false,
					false,
					true,
					false);
		}
		ExpedientEntity expedient = expedientRepository.findByMetaExpedientAndPareAndNomAndEsborrat(
				metaExpedient,
				contingutPare,
				nom,
				esborrat);
		return expedient == null ? null : toExpedientDto(expedient, true);
	}
	
	@Transactional(readOnly = true)
	@Override
	public PaginaDto<ExpedientDto> findAmbFiltreAdmin(
			Long entitatId,
			ExpedientFiltreDto filtre,
			PaginacioParamsDto paginacioParams) {
		logger.debug("Consultant els expedients segons el filtre per admins ("
				+ "entitatId=" + entitatId + ", "
				+ "filtre=" + filtre + ", "
				+ "paginacioParams=" + paginacioParams + ")");
		entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);
		return findAmbFiltrePaginat(
				entitatId,
				filtre,
				paginacioParams,
				true,
				false);
	}

	
	
	@Transactional
	@Override
	public boolean publicarComentariPerExpedient(
			Long entitatId,
			Long expedientId,
			String text) {
		logger.debug("Obtenint els comentaris pel contingut ("
				+ "entitatId=" + entitatId + ", "
				+ "nodeId=" + expedientId + ")");
		entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				false,
				true);
		ExpedientEntity expedient = entityComprovarHelper.comprovarExpedient(
				entitatId,
				expedientId,
				false,
				false,
				true,
				false,
				false);
		//truncam a 1024 caracters
		if (text.length() > 1024)
			text = text.substring(0, 1024);
		ExpedientComentariEntity comentari = ExpedientComentariEntity.getBuilder(
				expedient, 
				text).build();
		expedientComentariRepository.save(comentari);
		return true;
	}
	
	
	@Transactional(readOnly = true)
	@Override
	public List<ExpedientComentariDto> findComentarisPerContingut(
			Long entitatId,
			Long expedientId) {
		logger.debug("Obtenint els comentaris pel expedient ("
				+ "entitatId=" + entitatId + ", "
				+ "nodeId=" + expedientId + ")");
		entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				false, 
				true);
		ExpedientEntity expedient = entityComprovarHelper.comprovarExpedient(
				entitatId,
				expedientId,
				false,
				true,
				false,
				false,
				false);
		
		List<ExpedientComentariEntity> expcoms = 
				expedientComentariRepository.findByExpedientOrderByCreatedDateAsc(expedient);

		return conversioTipusHelper.convertirList(
				expcoms, 
				ExpedientComentariDto.class);
	}	
	
	@Transactional(readOnly = true)
	@Override
	public boolean hasWritePermission(
			Long expedientId) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//		if (comprovarPermisRead) {
//			boolean granted = permisosHelper.isGrantedAll(
//					metaNode.getId(),
//					MetaNodeEntity.class,
//					new Permission[] {ExtendedPermission.READ},
//					auth);
//			if (!granted) {
//				throw new SecurityException("Sense permisos per accedir al node ("
//						+ "id=" + nodeId + ", "
//						+ "usuari=" + auth.getName() + ")");
//			}
//		}
		
		ExpedientEntity expedient = expedientRepository.findOne(expedientId);
	
		boolean granted = permisosHelper.isGrantedAll(
				expedient.getMetaExpedient().getId(),
				MetaNodeEntity.class,
				new Permission[] {ExtendedPermission.WRITE},
				auth);	
		
		if (!granted && entityComprovarHelper.hasEstatWritePermissons(expedient.getExpedientEstat().getId())){ 
			granted = true;
		}
		
		return granted;

	}
	


	
	@Transactional(readOnly = true)
	@Override
	public PaginaDto<ExpedientDto> findAmbFiltreUser(
			Long entitatId,
			ExpedientFiltreDto filtre,
			PaginacioParamsDto paginacioParams) {
		logger.debug("Consultant els expedients segons el filtre per usuaris ("
				+ "entitatId=" + entitatId + ", "
				+ "filtre=" + filtre + ", "
				+ "paginacioParams=" + paginacioParams + ")");
		entityComprovarHelper.comprovarEntitat(
				entitatId,
				true,
				false,
				false);
		return findAmbFiltrePaginat(
				entitatId,
				filtre,
				paginacioParams,
				false,
				true);
	}

	@Transactional(readOnly = true)
	@Override
	public ExpedientEstatDto findExpedientEstatById(
			Long entitatId,
			Long id) {
		logger.debug("Obtenint l'estat del expedient ("
				+ "entitatId=" + entitatId + ", "
				+ "id=" + id + ")");
		entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				false,
				false);
		ExpedientEstatEntity estat =  expedientEstatRepository.findOne(id);
		ExpedientEstatDto dto = conversioTipusHelper.convertir(
				estat,
				ExpedientEstatDto.class);
		dto.setMetaExpedientId(estat.getMetaExpedient().getId());
		return dto;
	}

	@Transactional(readOnly = true)
	@Override
	public PaginaDto<ExpedientEstatDto> findExpedientEstatByMetaExpedientPaginat(
			Long entitatId,
			Long metaExpedientId,
			PaginacioParamsDto paginacioParams) {
		logger.debug("Consultant els estats del expedient ("
				+ "entitatId=" + entitatId + ", "
				+ "metaExpedientId=" + metaExpedientId + ", "
				+ "paginacioParams=" + paginacioParams + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);
		MetaExpedientEntity metaExpedient = null;
		if (metaExpedientId != null) {
			metaExpedient = entityComprovarHelper.comprovarMetaExpedient(
					entitat,
					metaExpedientId,
					false,
					false,
					false,
					false);
		}
		
		Page<ExpedientEstatEntity> paginaExpedientEstats = expedientEstatRepository.findByMetaExpedientOrderByOrdreAsc(
					metaExpedient,
					paginacioHelper.toSpringDataPageable(
							paginacioParams));
		 
		PaginaDto<ExpedientEstatDto> result = paginacioHelper.toPaginaDto(
				paginaExpedientEstats,
				ExpedientEstatDto.class);
		
		return result;

	}
	
	
	@Transactional(readOnly = true)
	@Override
	public List<ExpedientEstatDto> findExpedientEstatByMetaExpedient(
			Long entitatId,
			Long metaExpedientId) {
		logger.debug("Consultant els estats del expedient ("
				+ "entitatId=" + entitatId + ", "
				+ "metaExpedientId=" + metaExpedientId + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				true,
				false,
				false);
		MetaExpedientEntity metaExpedient = null;
		if (metaExpedientId != null) {
			metaExpedient = entityComprovarHelper.comprovarMetaExpedient(
					entitat,
					metaExpedientId,
					true,
					false,
					false,
					false);
		}
		
		List<ExpedientEstatEntity> expedientEstats = expedientEstatRepository.findByMetaExpedientOrderByOrdreAsc(
					metaExpedient);
		 
		return conversioTipusHelper.convertirList(
				expedientEstats,
				ExpedientEstatDto.class);
	}

	@Transactional(readOnly = true)
	@Override
	public List<ExpedientEstatDto> findExpedientEstats(
			Long entitatId,
			Long expedientId) {
		logger.debug("Consultant els estas dels expedients ("
				+ "entitatId=" + entitatId + ", "
				+ "expedientId=" + expedientId + ")");
		entityComprovarHelper.comprovarEntitat(
				entitatId,
				true,
				false,
				false);
		ExpedientEntity expedient = entityComprovarHelper.comprovarExpedient(
				entitatId,
				expedientId,
				false,
				false,
				true,
				false,
				false);
		List<ExpedientEstatEntity> expedientEstats = expedientEstatRepository.findByMetaExpedientOrderByOrdreAsc(expedient.getMetaExpedient());
		return conversioTipusHelper.convertirList(
				expedientEstats,
				ExpedientEstatDto.class);
	}

	@Transactional
	@Override
	public ExpedientEstatDto createExpedientEstat(
			Long entitatId,
			ExpedientEstatDto estat) {
		logger.debug("Creant un nou estat d'expedient (" +
				"entitatId=" + entitatId + ", " +
				"estat=" + estat + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);
		
		MetaExpedientEntity metaExpedient = entityComprovarHelper.comprovarMetaExpedient(
				entitat,
				estat.getMetaExpedientId(),
				false,
				false,
				false,
				false);
		
		int ordre = expedientEstatRepository.countByMetaExpedient(metaExpedient);
		
		
		ExpedientEstatEntity expedientEstat = ExpedientEstatEntity.getBuilder(
				estat.getCodi(),
				estat.getNom(),
				ordre,
				estat.getColor(),
				metaExpedient,
				estat.getResponsableCodi()).
				build();
		
		//if inicial of the modified state is true set inicial of other states to false
		if(estat.isInicial()){
			List<ExpedientEstatEntity> expedientEstats =  expedientEstatRepository.findByMetaExpedientOrderByOrdreAsc(metaExpedient);
			for (ExpedientEstatEntity expEst: expedientEstats){
				if(!expEst.equals(expedientEstat)){
					expEst.updateInicial(false);
				}
			}
			expedientEstat.updateInicial(true);
		} else {
			expedientEstat.updateInicial(false);
		}
		
		return conversioTipusHelper.convertir(
				expedientEstatRepository.save(expedientEstat),
				ExpedientEstatDto.class);
	}

	@Transactional
	@Override
	public ExpedientEstatDto updateExpedientEstat(
			Long entitatId,
			ExpedientEstatDto estat) {
		logger.debug("Actualitzant estat d'expedient (" +
				"entitatId=" + entitatId + ", " +
				"estat=" + estat + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);
		MetaExpedientEntity metaExpedient = entityComprovarHelper.comprovarMetaExpedient(
				entitat,
				estat.getMetaExpedientId(),
				false,
				false,
				false,
				false);
		ExpedientEstatEntity expedientEstat = expedientEstatRepository.findOne(estat.getId());
		expedientEstat.update(
				estat.getCodi(),
				estat.getNom(),
				estat.getColor(),
				metaExpedient,
				estat.getResponsableCodi());
		//if inicial of the modified state is true set inicial of other states to false
		if (estat.isInicial()){
			List<ExpedientEstatEntity> expedientEstats =  expedientEstatRepository.findByMetaExpedientOrderByOrdreAsc(metaExpedient);
			for (ExpedientEstatEntity expEst: expedientEstats){
				if(!expEst.equals(expedientEstat)){
					expEst.updateInicial(false);
				}
			}
			expedientEstat.updateInicial(true);
		} else {
			expedientEstat.updateInicial(false);
		}
		return conversioTipusHelper.convertir(
				expedientEstat,
				ExpedientEstatDto.class);
	}

	@Transactional
	@Override
	public ExpedientDto changeEstatOfExpedient(
			Long entitatId,
			Long expedientId,
			Long expedientEstatId) {
		logger.debug("Canviant estat del expedient (" +
				"entitatId=" + entitatId + ", " +
				"expedientId=" + expedientId + ", " +
				"expedientEstatId=" + expedientEstatId + ")");
		entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				false,
				true);
		ExpedientEntity expedient = entityComprovarHelper.comprovarExpedient(
				entitatId,
				expedientId,
				false,
				false,
				true,
				false,
				false);
		ExpedientEstatEntity estat;
		if (expedientEstatId!=null){
			estat = expedientEstatRepository.findOne(expedientEstatId);
		} else { // if it is null it means that "OBERT" state was choosen
			estat = null;
		}
		String codiEstatAnterior;
		if (expedient.getExpedientEstat()!=null){
			codiEstatAnterior = expedient.getExpedientEstat().getCodi();
		} else {
			codiEstatAnterior = messageHelper.getMessage("expedient.estat.enum.OBERT");
		}
		expedient.updateExpedientEstat(
				estat);
		// log change of state
		String codiEstatNou;
		if(expedient.getExpedientEstat()!=null){
			codiEstatNou = expedient.getExpedientEstat().getCodi();
		} else {
			codiEstatNou = messageHelper.getMessage("expedient.estat.enum.OBERT");
		}
		if(!codiEstatAnterior.equals(codiEstatNou)){
			contingutLogHelper.log(
					expedient,
					LogTipusEnumDto.CANVI_ESTAT,
					codiEstatAnterior,
					codiEstatNou,
					false,
					false);
		}
		
		// if new state has usuari responsable agafar by this user
		if (estat != null && estat.getResponsableCodi() != null) {
			agafarByUserWithCodi(
					entitatId, 
					expedientId,
					estat.getResponsableCodi());
		}
		
		return toExpedientDto(
				expedient,
				false);
	}

	@Override
	@Transactional
	public ExpedientEstatDto deleteExpedientEstat(
			Long entitatId,
			Long expedientEstatId) throws NotFoundException {
		logger.debug("Esborrant esta del expedient ("
				+ "entitatId=" + entitatId + ", "
				+ "expedientEstatId=" + expedientEstatId + ")");
		entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);
		ExpedientEstatEntity entity = expedientEstatRepository.findOne(expedientEstatId);
		expedientEstatRepository.delete(entity);
		return conversioTipusHelper.convertir(
				entity,
				ExpedientEstatDto.class);
	}





	private void agafarByUserWithCodi(
			Long entitatId,
			Long expedientId,
			String codi) {
		logger.debug("Agafant l'expedient com a usuari ("
				+ "entitatId=" + entitatId + ", "
				+ "expedientId=" + expedientId + ", "
				+ "usuari=" + codi + ")");
		ExpedientEntity expedient = entityComprovarHelper.comprovarExpedient(
				entitatId,
				expedientId,
				false,
				false,
				true,
				false,
				false);
		ExpedientEntity expedientSuperior = contingutHelper.getExpedientSuperior(
				expedient,
				false,
				false,
				false);
		if (expedientSuperior != null) {
			logger.error("No es pot agafar un expedient no arrel (id=" + expedientId + ")");
			throw new ValidationException(
					expedientId,
					ExpedientEntity.class,
					"No es pot agafar un expedient no arrel");
		}
		// Agafa l'expedient. Si l'expedient pertany a un altre usuari li pren
		UsuariEntity usuariOriginal = expedient.getAgafatPer();
		UsuariEntity usuariNou = usuariHelper.getUsuariByCodi(codi);
		
		 
		
		expedient.updateAgafatPer(usuariNou);
		if (usuariOriginal != null) {
			// Avisa a l'usuari que li han pres
			emailHelper.contingutAgafatPerAltreUsusari(
					expedient,
					usuariOriginal,
					usuariNou);
		}
		contingutLogHelper.log(
				expedient,
				LogTipusEnumDto.AGAFAR,
				null,
				null,
				false,
				false);
	}

	@Override
	@Transactional
	public ExpedientEstatDto moveTo(
			Long entitatId,
			Long metaExpedientId,
			Long expedientEstatId,
			int posicio) throws NotFoundException {
		logger.debug("Movent estat del expedient a la posició especificada ("
				+ "entitatId=" + entitatId + ", "
				+ "expedientEstatId=" + expedientEstatId + ", "
				+ "posicio=" + posicio + ")");
		entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);
		ExpedientEstatEntity estat = expedientEstatRepository.findOne(expedientEstatId);
		canviPosicio(
				estat,
				posicio);
		return conversioTipusHelper.convertir(
				estat,
				ExpedientEstatDto.class);
	}

	private void canviPosicio(
			ExpedientEstatEntity estat,
			int posicio) {
		List<ExpedientEstatEntity> estats = expedientEstatRepository.findByMetaExpedientOrderByOrdreAsc(
				estat.getMetaExpedient());
		if (posicio >= 0 && posicio < estats.size()) {
			if (posicio < estat.getOrdre()) {
				for (ExpedientEstatEntity est: estats) {
					if (est.getOrdre() >= posicio && est.getOrdre() < estat.getOrdre()) {
						est.updateOrdre(est.getOrdre() + 1);
					}
				}
			} else if (posicio > estat.getOrdre()) {
				for (ExpedientEstatEntity est: estats) {
					if (est.getOrdre() > estat.getOrdre() && est.getOrdre() <= posicio) {
						est.updateOrdre(est.getOrdre() - 1);
					}
				}
			}
			estat.updateOrdre(posicio);
		}
	}
	
	
	
	
	@Transactional(readOnly = true)
	@Override
	public PaginaDto<ExpedientDto> findAmbFiltreNoRelacionat(
			Long entitatId,
			ExpedientFiltreDto filtre,
			Long expedientId,
			PaginacioParamsDto paginacioParams) {
		logger.debug("Consultant els expedients segons el filtre per usuaris ("
				+ "entitatId=" + entitatId + ", "
				+ "filtre=" + filtre + ", "
				+ "paginacioParams=" + paginacioParams + 
				"id del expedient relacionat" + expedientId +")");
		entityComprovarHelper.comprovarEntitat(
				entitatId,
				true,
				false,
				false);
		
		return findAmbFiltrePaginat(
				entitatId,
				filtre,
				paginacioParams,
				false,
				true,
				expedientId);
	}
	
	
	
	
	@Transactional
	@Override
	public List<ExpedientDto> findByEntitatAndMetaExpedient(
			Long entitatId,
			Long metaExpedientId) {
		logger.debug("Consultant els expedients("
				+ "entitatId=" + entitatId + ", "
				+ "metaExpedientId=" + metaExpedientId + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				true,
				false,
				false);
		MetaExpedientEntity metaExpedient = null;
		if (metaExpedientId != null) {						
			
			metaExpedient = entityComprovarHelper.comprovarMetaExpedient(
					entitat,
					metaExpedientId,
					true,
					false,
					false,
					false);
		}
		
		List<ContingutEntity> expedientsEnt = contingutRepository.findByEntitatAndMetaExpedient(
				entitat, 
				metaExpedient);
		
		

		List<ExpedientDto> expedientsDto = new ArrayList<>(); 

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		permisosHelper.isGrantedAll(
				metaExpedientId,
				MetaNodeEntity.class,
				new Permission[] {ExtendedPermission.WRITE},
				auth);

		
		// if meta expedient has write permissions add all expedients
		if(entityComprovarHelper.hasMetaExpedientWritePermissons(metaExpedientId)){
			
			for(ContingutEntity exp: expedientsEnt){
					expedientsDto.add((ExpedientDto)contingutHelper.toContingutDto(
							exp,
							true,
							true,
							true,
							false,
							true,
							false,
							false));
					
			} 
		} else { //if not add only expedients having estat with permisions 
			
			for(ContingutEntity exp: expedientsEnt){
				ExpedientEntity expedient = (ExpedientEntity) exp;
				if (expedient.getExpedientEstat()!=null && entityComprovarHelper.hasEstatWritePermissons(expedient.getExpedientEstat().getId())){
					expedientsDto.add((ExpedientDto)contingutHelper.toContingutDto(
							exp,
							true,
							true,
							true,
							false,
							true,
							false,
							false));
					}
			}
		}
		
		return expedientsDto;
	}
	

	@Transactional(readOnly = true)
	@Override
	public List<ExpedientSelectorDto> findPerUserAndTipus(
			Long entitatId,
			Long metaExpedientId) {
		logger.debug("Consultant els expedients segons el tipus per usuaris ("
				+ "entitatId=" + entitatId + ", "
				+ "metaExpedientId=" + metaExpedientId + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				true,
				false,
				false);
		MetaExpedientEntity metaExpedient = null;
		if (metaExpedientId != null) {
			metaExpedient = entityComprovarHelper.comprovarMetaExpedient(
					entitat,
					metaExpedientId,
					false,
					true,
					false,
					false);
		}
		List<MetaExpedientEntity> metaExpedientsPermesos = metaExpedientRepository.findByEntitatOrderByNomAsc(
				entitat);
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		permisosHelper.filterGrantedAll(
				metaExpedientsPermesos,
				new ObjectIdentifierExtractor<MetaExpedientEntity>() {
					@Override
					public Long getObjectIdentifier(MetaExpedientEntity metaExpedient) {
						return metaExpedient.getId();
					}
				},
				MetaNodeEntity.class,
				new Permission[] {ExtendedPermission.READ},
				auth);
		if (!metaExpedientsPermesos.isEmpty()) {
			return conversioTipusHelper.convertirList(
					expedientRepository.findByEntitatAndMetaExpedientOrderByNomAsc(
							entitat, 
							metaExpedientsPermesos,
							metaExpedient == null,
							metaExpedient), 
					ExpedientSelectorDto.class);
		} else {
			return new ArrayList<ExpedientSelectorDto>();
		}
	}

	@Transactional(readOnly = true)
	@Override
	public List<Long> findIdsAmbFiltre(
			Long entitatId,
			ExpedientFiltreDto filtre) throws NotFoundException {
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
				filtre,
				false,
				true);
	}

	@Transactional
	@Override
	public void agafarUser(
			Long entitatId,
			Long id) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		logger.debug("Agafant l'expedient com a usuari ("
				+ "entitatId=" + entitatId + ", "
				+ "id=" + id + ", "
				+ "usuari=" + auth.getName() + ")");
		
		
		
		ExpedientEntity expedient = entityComprovarHelper.comprovarExpedient(
				entitatId,
				id,
				false,
				false,
				true,
				false,
				false);
		

		
		ExpedientEntity expedientSuperior = contingutHelper.getExpedientSuperior(
				expedient,
				false,
				false,
				false);
		if (expedientSuperior != null) {
			logger.error("No es pot agafar un expedient no arrel (id=" + id + ")");
			throw new ValidationException(
					id,
					ExpedientEntity.class,
					"No es pot agafar un expedient no arrel");
		}
		// Agafa l'expedient. Si l'expedient pertany a un altre usuari li pren
		UsuariEntity usuariOriginal = expedient.getAgafatPer();
		UsuariEntity usuariNou = usuariHelper.getUsuariAutenticat();
		expedient.updateAgafatPer(usuariNou);
		if (usuariOriginal != null) {
			// Avisa a l'usuari que li han pres
			emailHelper.contingutAgafatPerAltreUsusari(
					expedient,
					usuariOriginal,
					usuariNou);
		}
		contingutLogHelper.log(
				expedient,
				LogTipusEnumDto.AGAFAR,
				null,
				null,
				false,
				false);
	}
	
	
	
	@Transactional
	@Override
	public void agafarAdmin(
			Long entitatId,
			Long arxiuId,
			Long id,
			String usuariCodi) {
		logger.debug("Agafant l'expedient com a administrador ("
				+ "entitatId=" + entitatId + ", "
				+ "arxiuId=" + arxiuId + ", "
				+ "id=" + id + ", "
				+ "usuariCodi=" + usuariCodi + ")");
		ExpedientEntity expedient = entityComprovarHelper.comprovarExpedient(
				entitatId,
				id,
				false,
				false,
				false,
				false,
				false);
		ExpedientEntity expedientSuperior = contingutHelper.getExpedientSuperior(
				expedient,
				false,
				false,
				false);
		if (expedientSuperior != null) {
			throw new ValidationException(
					id,
					ExpedientEntity.class,
					"No es pot agafar un expedient no arrel");
		}
		// Agafa l'expedient. Si l'expedient pertany a un altre usuari li pren
		UsuariEntity usuariOriginal = expedient.getAgafatPer();
		UsuariEntity usuariNou = usuariHelper.getUsuariAutenticat();
		expedient.updateAgafatPer(usuariNou);
		if (usuariOriginal != null) {
			// Avisa a l'altre l'usuari que li han pres
			emailHelper.contingutAgafatPerAltreUsusari(
					expedient,
					usuariOriginal,
					usuariNou);
		}
		contingutLogHelper.log(
				expedient,
				LogTipusEnumDto.AGAFAR,
				null,
				null,
				false,
				false);
	}

	@Transactional
	@Override
	public void alliberarUser(
			Long entitatId,
			Long id) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		logger.debug("Alliberant l'expedient com a usuari ("
				+ "entitatId=" + entitatId + ", "
				+ "id=" + id + ", "
				+ "usuari=" + auth.getName() + ")");
		ExpedientEntity expedient = entityComprovarHelper.comprovarExpedient(
				entitatId,
				id,
				true,
				false,
				false,
				false,
				false);
		expedient.updateAgafatPer(null);
		contingutLogHelper.log(
				expedient,
				LogTipusEnumDto.ALLIBERAR,
				null,
				null,
				false,
				false);
	}

	@Transactional
	@Override
	public void alliberarAdmin(
			Long entitatId,
			Long id) {
		logger.debug("Alliberant l'expedient com a administrador ("
				+ "entitatId=" + entitatId + ", "
				+ "id=" + id + ")");
		ExpedientEntity expedient = entityComprovarHelper.comprovarExpedient(
				entitatId,
				id,
				false,
				false,
				false,
				false,
				false);
		expedient.updateAgafatPer(null);
		contingutLogHelper.log(
				expedient,
				LogTipusEnumDto.ALLIBERAR,
				null,
				null,
				false,
				false);
	}

	@Transactional
	@Override
	public void tancar(
			Long entitatId,
			Long id,
			String motiu) {
		logger.debug("Tancant l'expedient ("
				+ "entitatId=" + entitatId + ", "
				+ "id=" + id + ","
				+ "motiu=" + motiu + ")");
		ExpedientEntity expedient = entityComprovarHelper.comprovarExpedient(
				entitatId,
				id,
				true,
				false,
				true,
				false,
				false);
		if (documentHelper.hasFillsEsborranys(expedient)) {
			throw new ValidationException("No es pot tancar un expedient que contengui esborranys");
		}
		if (!documentHelper.hasAnyDocumentDefinitiu(expedient)) {
			throw new ValidationException("No es pot tancar un expedient sense cap document definitiu");
		}
		expedient.updateEstat(
				ExpedientEstatEnumDto.TANCAT,
				motiu);
		expedient.updateExpedientEstat(null);
		contingutLogHelper.log(
				expedient,
				LogTipusEnumDto.TANCAMENT,
				null,
				null,
				false,
				false);
		if (pluginHelper.isArxiuPluginActiu()) {
			pluginHelper.arxiuExpedientTancar(expedient);
		}
	}

	@Transactional
	@Override
	public void reobrir(
			Long entitatId,
			Long id) {
		logger.debug("Reobrint l'expedient ("
				+ "entitatId=" + entitatId + ", "
				+ "id=" + id + ")");
		ExpedientEntity expedient = entityComprovarHelper.comprovarExpedient(
				entitatId,
				id,
				true,
				false,
				true,
				false,
				false);
		expedient.updateEstat(
				ExpedientEstatEnumDto.OBERT,
				null);
		contingutLogHelper.log(
				expedient,
				LogTipusEnumDto.REOBERTURA,
				null,
				null,
				false,
				false);
		if (pluginHelper.isArxiuPluginActiu()) {
			pluginHelper.arxiuExpedientReobrir(expedient);
		}
	}

	@Transactional
	@Override
	@SuppressWarnings("serial")
	public void relacioCreate(
			Long entitatId,
			final Long id,
			final Long relacionatId) {
		logger.debug("Relacionant l'expedient ("
				+ "entitatId=" + entitatId + ", "
				+ "id=" + id + ", "
				+ "relacionatId=" + relacionatId + ")");
		ExpedientEntity expedient = entityComprovarHelper.comprovarExpedient(
				entitatId,
				id,
				true,
				false,
				true,
				false,
				false);
		ExpedientEntity toRelate = entityComprovarHelper.comprovarExpedient(
				entitatId,
				relacionatId,
				true,
				false,
				true,
				false,
				false);
		
		boolean alreadyRelatedTo = false;
		for (ExpedientEntity relacionatPer : toRelate.getRelacionatsAmb()) {
			if (relacionatPer.getId().equals(expedient.getId())) {
				alreadyRelatedTo = true;
			}
		}
		// checking if inverse relation doesnt already exist
		if (alreadyRelatedTo) {
			throw new ValidationException("Expedient ja relacionat");
		}

		expedient.addRelacionat(toRelate);
		contingutLogHelper.log(
				expedient,
				LogTipusEnumDto.MODIFICACIO,
				new Persistable<String>() {
					@Override
					public String getId() {
						return id + "#" + relacionatId;
					}
					@Override
					public boolean isNew() {
						return false;
					}
				},
				LogObjecteTipusEnumDto.RELACIO,
				LogTipusEnumDto.CREACIO,
				id.toString(),
				relacionatId.toString(),
				false,
				false);
	}

	@Transactional
	@Override
	@SuppressWarnings("serial")
	public boolean relacioDelete(
			Long entitatId,
			final Long id,
			final Long relacionatId) {
		logger.debug("Esborrant la relació de l'expedient amb un altre expedient ("
				+ "entitatId=" + entitatId + ", "
				+ "id=" + id + ", "
				+ "relacionatId=" + relacionatId + ")");
		ExpedientEntity expedient = entityComprovarHelper.comprovarExpedient(
				entitatId,
				id,
				true,
				false,
				true,
				false,
				false);
		ExpedientEntity relacionat = entityComprovarHelper.comprovarExpedient(
				entitatId,
				relacionatId,
				true,
				false,
				true,
				false,
				false);
		boolean trobat = true;
		if (expedient.getRelacionatsAmb().contains(relacionat)) {
			expedient.removeRelacionat(relacionat);
		} else if (relacionat.getRelacionatsAmb().contains(expedient)) {
			relacionat.removeRelacionat(expedient);
		} else {
			trobat = false;
		}
		if (trobat) {
			contingutLogHelper.log(
					expedient,
					LogTipusEnumDto.MODIFICACIO,
					new Persistable<String>() {
						@Override
						public String getId() {
							return id + "#" + relacionatId;
						}
						@Override
						public boolean isNew() {
							return false;
						}
					},
					LogObjecteTipusEnumDto.RELACIO,
					LogTipusEnumDto.ELIMINACIO,
					id.toString(),
					relacionatId.toString(),
					false,
					false);
		}
		return trobat;
	}

	@Transactional(readOnly = true)
	@Override
	public List<ExpedientDto> relacioFindAmbExpedient(
			Long entitatId,
			Long expedientId) {
		logger.debug("Obtenint la llista d'expedients relacionats (" +
				"entitatId=" + entitatId + ", " +
				"expedientId=" + expedientId + ")");
		ExpedientEntity expedient = entityComprovarHelper.comprovarExpedient(
				entitatId,
				expedientId,
				false,
				true,
				false,
				false,
				false);
		List<ExpedientEntity> relacionats = new ArrayList<ExpedientEntity>();
		relacionats.addAll(expedient.getRelacionatsAmb());
		relacionats.addAll(expedient.getRelacionatsPer());
		Collections.sort(
				relacionats, 
				new Comparator<ExpedientEntity>() {
				    @Override
				    public int compare(ExpedientEntity e1, ExpedientEntity e2) {
				        return e1.getNom().compareTo(e2.getNom());
				    }
				});
		List<ExpedientDto> relacionatsDto = new ArrayList<ExpedientDto>();
		for (ExpedientEntity e: relacionats)
			relacionatsDto.add(toExpedientDto(e, false));		
		return relacionatsDto;
	}

	@Transactional(readOnly = true)
	@Override
	public FitxerDto exportacio(
			Long entitatId,
			Long metaExpedientId,
			Collection<Long> expedientIds,
			String format) throws IOException {
		logger.debug("Exportant informació dels expedients (" +
				"entitatId=" + entitatId + ", " +
				"metaExpedientId=" + metaExpedientId + ", " +
				"expedientIds=" + expedientIds + ", " +
				"format=" + format + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				true,
				false,
				false);
		MetaExpedientEntity metaExpedient = entityComprovarHelper.comprovarMetaExpedient(
				entitat,
				metaExpedientId,
				true,
				false,
				false,
				false);
		List<ExpedientEntity> expedients = expedientRepository.findByEntitatAndAndMetaNodeAndIdInOrderByIdAsc(
				metaExpedient.getEntitat(),
				metaExpedient,
				expedientIds);
		List<MetaDadaEntity> metaDades = dadaRepository.findDistinctMetaDadaByNodeIdInOrderByMetaDadaCodiAsc(expedientIds);
		List<DadaEntity> dades = dadaRepository.findByNodeIdInOrderByNodeIdAscMetaDadaCodiAsc(expedientIds);
		int numColumnes = 5 + metaDades.size();
		String[] columnes = new String[numColumnes];
		columnes[0] = messageHelper.getMessage("expedient.service.exportacio.numero");
		columnes[1] = messageHelper.getMessage("expedient.service.exportacio.titol");
		columnes[2] = messageHelper.getMessage("expedient.service.exportacio.estat");
		columnes[3] = messageHelper.getMessage("expedient.service.exportacio.datcre");
		columnes[4] = messageHelper.getMessage("expedient.service.exportacio.idnti");
		for (int i = 0; i < metaDades.size(); i++) {
			MetaDadaEntity metaDada = metaDades.get(i);
			columnes[5 + i] = metaDada.getNom() + " (" + metaDada.getCodi() + ")";
		}
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		List<String[]> files = new ArrayList<String[]>();
		int dadesIndex = 0;
		for (ExpedientEntity expedient: expedients) {
			String[] fila = new String[numColumnes];
			fila[0] = expedientHelper.calcularNumero(expedient);
			fila[1] = expedient.getNom();
			fila[2] = expedient.getEstat().name();
			fila[3] = sdf.format(expedient.getCreatedDate().toDate());
			fila[4] = expedient.getNtiIdentificador();
			if (!dades.isEmpty()) {
				DadaEntity dadaActual = dades.get(dadesIndex);
				if (dadaActual.getNode().getId().equals(expedient.getId())) {
					for (int i = 0; i < metaDades.size(); i++) {
						MetaDadaEntity metaDada = metaDades.get(i);
						int dadesIndexIncrement = 1;
						while (dadaActual.getNode().getId().equals(expedient.getId())) {
							if (dadaActual.getMetaDada().getCodi().equals(metaDada.getCodi())) {
								break;
							}
							dadaActual = dades.get(dadesIndex + dadesIndexIncrement++);
						}
						if (dadaActual.getMetaDada().getCodi().equals(metaDada.getCodi())) {
							fila[5 + i] = dadaActual.getValorComString();
						}
					}
				}
			}
			files.add(fila);
		}
		FitxerDto fitxer = new FitxerDto();
		if ("ODS".equalsIgnoreCase(format)) {
			Object[][] filesArray = files.toArray(new Object[files.size()][numColumnes]);
			TableModel model = new DefaultTableModel(filesArray, columnes);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			SpreadSheet.createEmpty(model).getPackage().save(baos);
			fitxer.setNom("exportacio.ods");
			fitxer.setContentType("application/vnd.oasis.opendocument.spreadsheet");
			fitxer.setContingut(baos.toByteArray());
		} else if ("CSV".equalsIgnoreCase(format)) {
			fitxer.setNom("exportacio.csv");
			fitxer.setContentType("text/csv");
			StringBuilder sb = new StringBuilder();
			csvHelper.afegirLinia(sb, columnes, ';');
			for (String[] fila: files) {
				csvHelper.afegirLinia(sb, fila, ';');
			}
			fitxer.setContingut(sb.toString().getBytes());
		} else {
			throw new ValidationException("Format de fitxer no suportat: " + format);
		}
		return fitxer;
	}

	private PaginaDto<ExpedientDto> findAmbFiltrePaginat(
			Long entitatId,
			ExpedientFiltreDto filtre,
			PaginacioParamsDto paginacioParams,
			boolean accesAdmin,
			boolean comprovarAccesMetaExpedients) {
		return findAmbFiltrePaginat(
				entitatId,
				filtre,
				paginacioParams,
				accesAdmin,
				comprovarAccesMetaExpedients,
				null
				);
	}

	private PaginaDto<ExpedientDto> findAmbFiltrePaginat(
			Long entitatId,
			ExpedientFiltreDto filtre,
			PaginacioParamsDto paginacioParams,
			boolean accesAdmin,
			boolean comprovarAccesMetaExpedients,
			Long expedientId) {
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				(!accesAdmin),
				accesAdmin,
				false);
		MetaExpedientEntity metaExpedient = null;
		
		if (filtre.getMetaExpedientId() != null) {
			metaExpedient = entityComprovarHelper.comprovarMetaExpedient(
					entitat,
					filtre.getMetaExpedientId(),
					true,
					false,
					false,
					false);
		}
		List<MetaExpedientEntity> metaExpedientsPermesos = metaExpedientRepository.findByEntitatOrderByNomAsc(
				entitat);
		if (comprovarAccesMetaExpedients) {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			permisosHelper.filterGrantedAll(
					metaExpedientsPermesos,
					new ObjectIdentifierExtractor<MetaExpedientEntity>() {
						@Override
						public Long getObjectIdentifier(MetaExpedientEntity metaExpedient) {
							return metaExpedient.getId();
						}
					},
					MetaNodeEntity.class,
					new Permission[] {ExtendedPermission.READ},
					auth);
		}
		if (!metaExpedientsPermesos.isEmpty()) {
			UsuariEntity agafatPer = null;
			if (filtre.isMeusExpedients()) {
				agafatPer = usuariHelper.getUsuariAutenticat();
			}
			ExpedientEstatEnumDto chosenEstatEnum = null;
			ExpedientEstatEntity chosenEstat = null;
			Long estatId = filtre.getExpedientEstatId();
			if(estatId != null){
				if (estatId.intValue() <= 0) { // if estat is 0 or less the given estat is enum
					int estatIdInt = -estatId.intValue();
					chosenEstatEnum = ExpedientEstatEnumDto.values()[estatIdInt];
				} else { //given estat is estat from database
					chosenEstat = expedientEstatRepository.findOne(estatId);
				}
			}
			Map<String, String[]> ordenacioMap = new HashMap<String, String[]>();
			ordenacioMap.put("numero", new String[] {"codi", "any", "sequencia"});
			Page<ExpedientEntity> paginaExpedients;
			List<ExpedientEntity> expedientsToBeExluded = new ArrayList<>();
			if (expedientId != null) {
				// expedient for which "Relacionar expedient" list is shown
				ExpedientEntity expedient = entityComprovarHelper.comprovarExpedient(
						entitatId,
						expedientId,
						false,
						false,
						true,
						false,
						false);
				expedientsToBeExluded.addAll(expedient.getRelacionatsAmb());
				expedientsToBeExluded.addAll(expedient.getRelacionatsPer());
				expedientsToBeExluded.add(expedient);
			}
			if (!expedientsToBeExluded.isEmpty()) {
				agafatPer = usuariHelper.getUsuariAutenticat();
				paginaExpedients = expedientRepository.findByEntitatAndFiltre(
						entitat,
						metaExpedientsPermesos,
						metaExpedient == null,
						metaExpedient,
						filtre.getNumero() == null || "".equals(filtre.getNumero().trim()),
						filtre.getNumero() == null ? "" : filtre.getNumero(),
						filtre.getNom() == null || filtre.getNom().isEmpty(),
						filtre.getNom() == null ? "" : filtre.getNom(),
						filtre.getDataCreacioInici() == null,
						filtre.getDataCreacioInici(),
						filtre.getDataCreacioFi() == null,
						DateHelper.toDateFinalDia(filtre.getDataCreacioFi()),
						filtre.getDataTancatInici() == null,
						filtre.getDataTancatInici(),
						filtre.getDataTancatFi() == null,
						filtre.getDataTancatFi(),
						chosenEstatEnum == null,
						chosenEstatEnum,
						chosenEstat == null,
						chosenEstat,
						agafatPer == null,
						agafatPer,
						filtre.getSearch() == null,
						filtre.getSearch() == null ? "" : filtre.getSearch(),
						filtre.getTipusId() == null,
						filtre.getTipusId(),
						expedientsToBeExluded,
						filtre.getInteressat() == null || filtre.getInteressat().isEmpty(),
						filtre.getInteressat(),
						filtre.getMetaExpedientDominiValor() == null || filtre.getMetaExpedientDominiValor().isEmpty(),
						filtre.getMetaExpedientDominiValor(),
						paginacioHelper.toSpringDataPageable(
								paginacioParams,
								ordenacioMap));
				
			} else {
				paginaExpedients = expedientRepository.findByEntitatAndFiltre(
						entitat,
						metaExpedientsPermesos,
						metaExpedient == null,
						metaExpedient,
						filtre.getNumero() == null || "".equals(filtre.getNumero().trim()),
						filtre.getNumero() == null ? "" : filtre.getNumero(),
						filtre.getNom() == null || filtre.getNom().isEmpty(),
						filtre.getNom() == null ? "" : filtre.getNom(),
						filtre.getDataCreacioInici() == null,
						filtre.getDataCreacioInici(),
						filtre.getDataCreacioFi() == null,
						DateHelper.toDateFinalDia(filtre.getDataCreacioFi()),
						filtre.getDataTancatInici() == null,
						filtre.getDataTancatInici(),
						filtre.getDataTancatFi() == null,
						filtre.getDataTancatFi(),
						chosenEstatEnum == null,
						chosenEstatEnum,
						chosenEstat == null,
						chosenEstat,
						agafatPer == null,
						agafatPer,
						filtre.getSearch() == null,
						filtre.getSearch() == null ? "" : filtre.getSearch(),
						filtre.getTipusId() == null,
						filtre.getTipusId(),
						filtre.getInteressat() == null || filtre.getInteressat().isEmpty(),
						filtre.getInteressat(),
						filtre.getMetaExpedientDominiValor() == null || filtre.getMetaExpedientDominiValor().isEmpty(),
						filtre.getMetaExpedientDominiValor(),
						paginacioHelper.toSpringDataPageable(
								paginacioParams,
								ordenacioMap));
			}
			PaginaDto<ExpedientDto> result = paginacioHelper.toPaginaDto(
					paginaExpedients,
					ExpedientDto.class,
					new Converter<ExpedientEntity, ExpedientDto>() {
						@Override
						public ExpedientDto convert(ExpedientEntity source) {
							return toExpedientDto(
									source,
									true);
						}
					});
			for (ExpedientDto expedient: result) {
				boolean enAlerta = alertaRepository.countByLlegidaAndContingutId(
						false,
						expedient.getId()
						) > 0;
						expedient.setAlerta(enAlerta);
			}
			return result;
		} else {
			return paginacioHelper.getPaginaDtoBuida(
					ExpedientDto.class);
		}
	}

	private List<Long> findIdsAmbFiltrePaginat(
			Long entitatId,
			ExpedientFiltreDto filtre,
			boolean accesAdmin,
			boolean comprovarAccesMetaExpedients) {
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				(!accesAdmin),
				accesAdmin,
				false);
		MetaExpedientEntity metaExpedient = null;
		if (filtre.getMetaExpedientId() != null) {
			metaExpedient = entityComprovarHelper.comprovarMetaExpedient(
					entitat,
					filtre.getMetaExpedientId(),
					true,
					false,
					false,
					false);
		}
		List<MetaExpedientEntity> metaExpedientsPermesos = metaExpedientRepository.findByEntitatOrderByNomAsc(
				entitat);
		if (comprovarAccesMetaExpedients) {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			permisosHelper.filterGrantedAll(
					metaExpedientsPermesos,
					new ObjectIdentifierExtractor<MetaExpedientEntity>() {
						@Override
						public Long getObjectIdentifier(MetaExpedientEntity metaExpedient) {
							return metaExpedient.getId();
						}
					},
					MetaNodeEntity.class,
					new Permission[] {ExtendedPermission.READ},
					auth);
		}
		if (!metaExpedientsPermesos.isEmpty()) {
			return expedientRepository.findIdByEntitatAndFiltre(
					entitat,
					metaExpedientsPermesos,
					metaExpedient == null,
					metaExpedient,
					filtre.getNumero() == null || "".equals(filtre.getNumero().trim()),
					filtre.getNumero(),
					filtre.getNom() == null || filtre.getNom().isEmpty(),
					filtre.getNom(),
					filtre.getDataCreacioInici() == null,
					filtre.getDataCreacioInici(),
					filtre.getDataCreacioFi() == null,
					filtre.getDataCreacioFi(),
					filtre.getDataTancatInici() == null,
					filtre.getDataTancatInici(),
					filtre.getDataTancatFi() == null,
					filtre.getDataTancatFi(),
					filtre.getEstat() == null,
					filtre.getEstat(),
					filtre.getInteressat() == null || filtre.getInteressat().isEmpty(),
					filtre.getInteressat());
		} else {
			return new ArrayList<Long>();
		}
	}

	private ExpedientDto toExpedientDto(
			ExpedientEntity expedient,
			boolean ambPathIPermisos) {
		ExpedientDto expedientDto = (ExpedientDto) contingutHelper.toContingutDto(
				expedient,
				ambPathIPermisos,
				false,
				false,
				false,
				ambPathIPermisos,
				false,
				false);
		
		return expedientDto;
	}

	private static final Logger logger = LoggerFactory.getLogger(ExpedientServiceImpl.class);

}
