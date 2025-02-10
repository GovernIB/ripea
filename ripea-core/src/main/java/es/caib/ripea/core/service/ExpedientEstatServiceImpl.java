package es.caib.ripea.core.service;

import es.caib.ripea.core.api.dto.ContingutMassiuFiltreDto;
import es.caib.ripea.core.api.dto.ExpedientDto;
import es.caib.ripea.core.api.dto.ExpedientEstatDto;
import es.caib.ripea.core.api.dto.ExpedientEstatEnumDto;
import es.caib.ripea.core.api.dto.LogTipusEnumDto;
import es.caib.ripea.core.api.dto.PaginaDto;
import es.caib.ripea.core.api.dto.PaginacioParamsDto;
import es.caib.ripea.core.api.dto.ResultDto;
import es.caib.ripea.core.api.dto.ResultEnumDto;
import es.caib.ripea.core.api.exception.NotFoundException;
import es.caib.ripea.core.api.exception.ValidationException;
import es.caib.ripea.core.api.service.ExpedientEstatService;
import es.caib.ripea.core.api.utils.Utils;
import es.caib.ripea.core.persistence.EntitatEntity;
import es.caib.ripea.core.persistence.ExpedientEntity;
import es.caib.ripea.core.persistence.ExpedientEstatEntity;
import es.caib.ripea.core.persistence.MetaExpedientEntity;
import es.caib.ripea.core.persistence.UsuariEntity;
import es.caib.ripea.core.helper.ContingutHelper;
import es.caib.ripea.core.helper.ContingutLogHelper;
import es.caib.ripea.core.helper.ConversioTipusHelper;
import es.caib.ripea.core.helper.DateHelper;
import es.caib.ripea.core.helper.EmailHelper;
import es.caib.ripea.core.helper.EntityComprovarHelper;
import es.caib.ripea.core.helper.ExpedientEstatHelper;
import es.caib.ripea.core.helper.MessageHelper;
import es.caib.ripea.core.helper.MetaExpedientHelper;
import es.caib.ripea.core.helper.PaginacioHelper;
import es.caib.ripea.core.helper.PaginacioHelper.Converter;
import es.caib.ripea.core.helper.UsuariHelper;
import es.caib.ripea.core.repository.ExpedientEstatRepository;
import es.caib.ripea.core.repository.ExpedientRepository;
import es.caib.ripea.core.repository.UsuariRepository;
import es.caib.ripea.core.repository.command.ExpedientRepositoryCommnand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ExpedientEstatServiceImpl implements ExpedientEstatService {

	@Autowired
	private ExpedientEstatRepository expedientEstatRepository;
	@Autowired
	private ConversioTipusHelper conversioTipusHelper;
	@Autowired
	private ContingutHelper contingutHelper;
	@Autowired
	private PaginacioHelper paginacioHelper;
	@Autowired
	private UsuariHelper usuariHelper;
	@Autowired
	private EmailHelper emailHelper;
	@Autowired
	private EntityComprovarHelper entityComprovarHelper;
	@Autowired
	private MessageHelper messageHelper;
	@Autowired
	private ContingutLogHelper contingutLogHelper;
	@Autowired
	private MetaExpedientHelper metaExpedientHelper;
	@Autowired
	private ExpedientRepository expedientRepository;
	@Autowired
	private UsuariRepository usuariRepository;
	@Autowired
	private ExpedientEstatHelper expedientEstatHelper;
    @Autowired
    private ExpedientRepositoryCommnand expedientRepositoryCommnand;


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
				false, false, false);
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
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitatPerMetaExpedients(entitatId);
		MetaExpedientEntity metaExpedient = null;
		if (metaExpedientId != null) {
			metaExpedient = entityComprovarHelper.comprovarMetaExpedient(entitat, metaExpedientId);
		}
		Page<ExpedientEstatEntity> paginaExpedientEstats = expedientEstatRepository.findByMetaExpedientOrderByOrdreAsc(
					metaExpedient,
					paginacioHelper.toSpringDataPageable(paginacioParams));
		PaginaDto<ExpedientEstatDto> result = paginacioHelper.toPaginaDto(
				paginaExpedientEstats,
				ExpedientEstatDto.class);
		return result;

	}
	
	
	@Transactional(readOnly = true)
	@Override
	public List<ExpedientEstatDto> findExpedientEstatsByMetaExpedient(
			Long entitatId,
			Long metaExpedientId) {
		logger.debug("Consultant els estats del expedient ("
				+ "entitatId=" + entitatId + ", "
				+ "metaExpedientId=" + metaExpedientId + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				false,
				false, 
				true, false);
		MetaExpedientEntity metaExpedient = null;
		if (metaExpedientId != null) {
			metaExpedient = entityComprovarHelper.comprovarMetaExpedient(entitat, metaExpedientId);
		}
		List<ExpedientEstatEntity> expedientEstats = expedientEstatRepository.findByMetaExpedientOrderByOrdreAsc(metaExpedient);
		return conversioTipusHelper.convertirList(
				expedientEstats,
				ExpedientEstatDto.class);
	}

	@Transactional(readOnly = true)
	@Override
	public List<ExpedientEstatDto> findExpedientEstats(
			Long entitatId,
			Long expedientId, 
			String rolActual) {
		logger.debug("Consultant els estas dels expedients ("
				+ "entitatId=" + entitatId + ", "
				+ "expedientId=" + expedientId + ")");
		entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				false,
				false, 
				true, false);
		ExpedientEntity expedient = entityComprovarHelper.comprovarExpedient(
				expedientId,
				false,
				false,
				true,
				false,
				false,
				rolActual);
		List<ExpedientEstatEntity> expedientEstats = expedientEstatRepository.findByMetaExpedientOrderByOrdreAsc(expedient.getMetaExpedient());
		return conversioTipusHelper.convertirList(
				expedientEstats,
				ExpedientEstatDto.class);
	}

	@Transactional
	@Override
	public ExpedientEstatDto createExpedientEstat(
			Long entitatId,
			ExpedientEstatDto estat, String rolActual, Long organId) {

		return expedientEstatHelper.createExpedientEstat(
				entitatId,
				estat,
				rolActual,
				organId);
	}

	@Transactional
	@Override
	public ExpedientEstatDto updateExpedientEstat(
			Long entitatId,
			ExpedientEstatDto estat, String rolActual, Long organId) {
		logger.debug("Actualitzant estat d'expedient (" +
				"entitatId=" + entitatId + ", " +
				"estat=" + estat + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitatPerMetaExpedients(entitatId);
		MetaExpedientEntity metaExpedient = entityComprovarHelper.comprovarMetaExpedient(entitat, estat.getMetaExpedientId());
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
		
		if (rolActual.equals("IPA_ORGAN_ADMIN")) {
			metaExpedientHelper.canviarRevisioADisseny(entitatId, metaExpedient.getId(), organId);
		}
		return conversioTipusHelper.convertir(
				expedientEstat,
				ExpedientEstatDto.class);
	}

	@Transactional
	@Override
	public ExpedientDto changeExpedientEstat(
			Long entitatId,
			Long expedientId,
			Long estatId) {
		
		boolean throwException = false; // throwException = true;
		if (throwException) {
			throw new RuntimeException("Mock exception change estat ");
		}
		
		logger.debug("Canviant estat del expedient (" +
				"entitatId=" + entitatId + ", " +
				"expedientId=" + expedientId + ", " +
				"estatId=" + estatId + ")");
		entityComprovarHelper.comprovarEntitatPerMetaExpedients(entitatId);
		ExpedientEntity expedient = entityComprovarHelper.comprovarExpedient(
				expedientId,
				false,
				false,
				true,
				false,
				false,
				null);
		entityComprovarHelper.comprovarEstatExpedient(entitatId, expedientId, ExpedientEstatEnumDto.OBERT);
		ExpedientEstatEntity estat;
		if (estatId != null) {
			estat = expedientEstatRepository.findOne(estatId);
		} else { // if it is null it means that "OBERT" state was choosen
			estat = null;
		}
		String codiEstatAnterior;
		if (expedient.getEstatAdditional() != null) {
			codiEstatAnterior = expedient.getEstatAdditional().getCodi();
		} else {
			codiEstatAnterior = messageHelper.getMessage("expedient.estat.enum.OBERT");
		}
		expedient.updateEstatAdditional(
				estat);
		// log change of state
		String codiEstatNou;
		if (expedient.getEstatAdditional() != null) {
			codiEstatNou = expedient.getEstatAdditional().getCodi();
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
			Long expedientEstatId, 
			String rolActual, Long organId) throws NotFoundException {
		logger.debug("Esborrant esta del expedient ("
				+ "entitatId=" + entitatId + ", "
				+ "expedientEstatId=" + expedientEstatId + ")");
		entityComprovarHelper.comprovarEntitatPerMetaExpedients(entitatId);
		ExpedientEstatEntity entity = expedientEstatRepository.findOne(expedientEstatId);
		if (!entity.getExpedients().isEmpty()) {
			throw new ValidationException("");
		}
		expedientEstatRepository.delete(entity);
		
		if (rolActual.equals("IPA_ORGAN_ADMIN")) {
			metaExpedientHelper.canviarRevisioADisseny(entitatId, entity.getMetaExpedient().getId(), organId);
		}
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
		ExpedientEntity expedient = expedientRepository.findOne(expedientId);

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
			int posicio, String rolActual) throws NotFoundException {
		logger.debug("Movent estat del expedient a la posició especificada ("
				+ "entitatId=" + entitatId + ", "
				+ "expedientEstatId=" + expedientEstatId + ", "
				+ "posicio=" + posicio + ")");
		entityComprovarHelper.comprovarEntitatPerMetaExpedients(entitatId);
		ExpedientEstatEntity estat = expedientEstatRepository.findOne(expedientEstatId);
		canviPosicio(
				estat,
				posicio);
		return conversioTipusHelper.convertir(
				estat,
				ExpedientEstatDto.class);
	}
	
	
	
	
	@Transactional(readOnly = true)
	@Override
	public ResultDto<ExpedientDto> findExpedientsPerCanviEstatMassiu(
			Long entitatId,
			ContingutMassiuFiltreDto filtre,
			PaginacioParamsDto paginacioParams, 
			String rolActual, 
			ResultEnumDto resultEnum) throws NotFoundException {
		
		ResultDto<ExpedientDto> result = new ResultDto<ExpedientDto>();
		
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				false,
				false, 
				true, false);
		MetaExpedientEntity metaExpedient = null;
		if (filtre.getMetaExpedientId() != null) {
			metaExpedient = entityComprovarHelper.comprovarMetaExpedient(entitat, filtre.getMetaExpedientId());
		}
		ExpedientEntity expedient = null;
		if (filtre.getExpedientId() != null) {
			expedient = expedientRepository.findOne(filtre.getExpedientId());
		}
		boolean nomesAgafats = true;
		if (rolActual.equals("IPA_ADMIN") || rolActual.equals("IPA_ORGAN_ADMIN")) {
			nomesAgafats = false;
		} 
		
		List<MetaExpedientEntity> metaExpedientsPermesos = metaExpedientHelper.findPermesosAccioMassiva(entitatId, rolActual);
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UsuariEntity usuariActual = usuariRepository.findOne(auth.getName());
		Date dataInici = DateHelper.toDateInicialDia(filtre.getDataInici());
		Date dataFi = DateHelper.toDateFinalDia(filtre.getDataFi());
		
		// estats
		ExpedientEstatEnumDto chosenEstatEnum = null;
		ExpedientEstatEntity chosenEstat = null;
		Long estatId = filtre.getExpedientEstatId();
		if (estatId != null) {
			if (estatId.intValue() <= 0) { // if estat is 0 or less the given estat is enum
				int estatIdInt = -estatId.intValue();
				chosenEstatEnum = ExpedientEstatEnumDto.values()[estatIdInt];
			} else { // given estat is estat from database
				chosenEstat = expedientEstatRepository.findOne(estatId);
			}
		}
		
		
		if (resultEnum == ResultEnumDto.PAGE) {
			// ================================  RETURNS PAGE (DATATABLE) ==========================================
			Map<String, String[]> ordenacioMap = new HashMap<String, String[]>();
			ordenacioMap.put("createdBy.codiAndNom", new String[] {"createdBy.nom"});
            ordenacioMap.put("estat", new String[] {"estatAdditional", "estat", "id"});
            ordenacioMap.put("numeroINom", new String[] {"numero", "nom", "id"});
            ordenacioMap.put("metaExpedient.codiSiaINom", new String[] {"metaExpedient.classificacio", "metaExpedient.nom"});
			
			Page<ExpedientEntity> paginaDocuments = expedientRepositoryCommnand.findExpedientsPerCanviEstatMassiu(
					entitat,
					nomesAgafats,
					usuariActual,
					metaExpedient,
					expedient,
					dataInici,
					dataFi,
					filtre.getNom(),
					chosenEstatEnum,
					chosenEstat,
					filtre.getPrioritat(),
					Utils.getNullIfEmpty(metaExpedientsPermesos),
					paginacioHelper.toSpringDataPageable(paginacioParams,ordenacioMap));
			PaginaDto<ExpedientDto> paginaDto = paginacioHelper.toPaginaDto(
					paginaDocuments,
					ExpedientDto.class,
					new Converter<ExpedientEntity, ExpedientDto>() {
						@Override
						public ExpedientDto convert(ExpedientEntity source) {
							ExpedientDto dto = (ExpedientDto)contingutHelper.toContingutDto(
									source,
									false,
									false);
							return dto;
						}
					});
			
			result.setPagina(paginaDto);
		
		} else {
			// ==================================  RETURNS IDS (SELECCIONAR TOTS) ============================================
			List<Long> idsDocuments = expedientRepositoryCommnand.findIdsExpedientsPerCanviEstatMassiu(
					entitat,
					nomesAgafats,
					usuariActual,
					metaExpedient,
					expedient,
					dataInici,
					dataFi,
					chosenEstatEnum,
					chosenEstat,
					Utils.getNullIfEmpty(metaExpedientsPermesos));
			result.setIds(idsDocuments);
		}
		return result;
		
	}




	private void canviPosicio(
			ExpedientEstatEntity estat,
			int posicio) {
		List<ExpedientEstatEntity> estats = expedientEstatRepository.findByMetaExpedientOrderByOrdreAsc(
				estat.getMetaExpedient());
		
		moveTo(
				estat,
				estats,
				posicio);
	}
	
	
	public void moveTo(
			ExpedientEstatEntity elementToMove,
			List<ExpedientEstatEntity> elements,
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
	
	

	private ExpedientDto toExpedientDto(
			ExpedientEntity expedient,
			boolean ambPathIPermisos) {
		ExpedientDto expedientDto = (ExpedientDto) contingutHelper.toContingutDto(
				expedient, false, false);
		
		return expedientDto;
	}
	
	
	
	
	
	
	private static final Logger logger = LoggerFactory.getLogger(ExpedientEstatServiceImpl.class);
}
