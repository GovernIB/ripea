package es.caib.ripea.core.service;

import es.caib.ripea.core.api.dto.ArxiuPendentTipusEnumDto;
import es.caib.ripea.core.api.dto.DocumentNotificacioEstatEnumDto;
import es.caib.ripea.core.api.dto.DocumentNotificacioTipusEnumDto;
import es.caib.ripea.core.api.dto.ExpedientPeticioFiltreDto;
import es.caib.ripea.core.api.dto.ExpedientPeticioListDto;
import es.caib.ripea.core.api.dto.PaginaDto;
import es.caib.ripea.core.api.dto.PaginacioParamsDto;
import es.caib.ripea.core.api.dto.PermisosPerExpedientsDto;
import es.caib.ripea.core.api.dto.ResultDto;
import es.caib.ripea.core.api.dto.ResultEnumDto;
import es.caib.ripea.core.api.dto.SeguimentArxiuPendentsDto;
import es.caib.ripea.core.api.dto.SeguimentArxiuPendentsFiltreDto;
import es.caib.ripea.core.api.dto.SeguimentConsultaFiltreDto;
import es.caib.ripea.core.api.dto.SeguimentConsultaPinbalDto;
import es.caib.ripea.core.api.dto.SeguimentDto;
import es.caib.ripea.core.api.dto.SeguimentFiltreDto;
import es.caib.ripea.core.api.dto.SeguimentNotificacionsFiltreDto;
import es.caib.ripea.core.api.service.SeguimentService;
import es.caib.ripea.core.api.utils.Utils;
import es.caib.ripea.core.entity.ConsultaPinbalEntity;
import es.caib.ripea.core.entity.DocumentEntity;
import es.caib.ripea.core.entity.DocumentNotificacioEntity;
import es.caib.ripea.core.entity.DocumentPortafirmesEntity;
import es.caib.ripea.core.entity.EntitatEntity;
import es.caib.ripea.core.entity.ExpedientEntity;
import es.caib.ripea.core.entity.ExpedientPeticioEntity;
import es.caib.ripea.core.entity.ExpedientTascaEntity;
import es.caib.ripea.core.entity.InteressatEntity;
import es.caib.ripea.core.entity.MetaExpedientEntity;
import es.caib.ripea.core.entity.MetaExpedientTascaEntity;
import es.caib.ripea.core.entity.UsuariEntity;
import es.caib.ripea.core.helper.DateHelper;
import es.caib.ripea.core.helper.EntityComprovarHelper;
import es.caib.ripea.core.helper.ExpedientHelper;
import es.caib.ripea.core.helper.ExpedientPeticioHelper;
import es.caib.ripea.core.helper.MetaExpedientHelper;
import es.caib.ripea.core.helper.PaginacioHelper;
import es.caib.ripea.core.helper.PermisosPerAnotacions;
import es.caib.ripea.core.helper.UsuariHelper;
import es.caib.ripea.core.repository.ConsultaPinbalRepository;
import es.caib.ripea.core.repository.DocumentNotificacioRepository;
import es.caib.ripea.core.repository.DocumentPortafirmesRepository;
import es.caib.ripea.core.repository.DocumentRepository;
import es.caib.ripea.core.repository.ExpedientPeticioRepository;
import es.caib.ripea.core.repository.ExpedientRepository;
import es.caib.ripea.core.repository.ExpedientTascaRepository;
import es.caib.ripea.core.repository.InteressatRepository;
import es.caib.ripea.core.repository.MetaExpedientRepository;
import es.caib.ripea.core.repository.MetaExpedientTascaRepository;
import es.caib.ripea.core.repository.UsuariRepository;
import es.caib.ripea.core.repository.command.ExpedientRepositoryCommnand;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SeguimentServiceImpl implements SeguimentService {

	@Autowired
	private EntityComprovarHelper entityComprovarHelper;
	@Autowired
	private PaginacioHelper paginacioHelper;
	@Autowired
	private DocumentPortafirmesRepository documentPortafirmesRepository;
	@Autowired
	private DocumentNotificacioRepository documentNotificacioRepository;
	@Autowired
	private ExpedientTascaRepository expedientTascaRepository;
	@Autowired
	private UsuariHelper usuariHelper;
	@Autowired
	private MetaExpedientTascaRepository metaExpedientTascaRepository;
	@Autowired
	private ExpedientPeticioRepository expedientPeticioRepository;
	@Autowired
	private ExpedientRepository expedientRepository;
	@Autowired
	private DocumentRepository documentRepository;
	@Autowired
	private InteressatRepository interessatRepository;
	@Autowired
	private MetaExpedientHelper metaExpedientHelper;
	@Autowired
	private ExpedientHelper expedientHelper;
	@Resource
	private ExpedientPeticioHelper expedientPeticioHelper;
	@Resource
	private ConsultaPinbalRepository consultaPinbalRepository;
	@Resource
	private UsuariRepository usuariRepository;
	@Autowired
	private MetaExpedientRepository metaExpedientRepository;
    @Autowired
    private ExpedientRepositoryCommnand expedientRepositoryCommnand;

	@Override
	@Transactional(readOnly = true)
	public PaginaDto<SeguimentDto> findPortafirmesEnviaments(
			Long entitatId,
			SeguimentFiltreDto filtre, 
			PaginacioParamsDto paginacioParams) {
		
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId, false, true, false, false, false);
		
		Map<String, String[]> ordenacioMap = new HashMap<String, String[]>();
		ordenacioMap.put("expedientNom", new String[] { "expedient.nom" });
		ordenacioMap.put("documentNom", new String[] { "document.nom" });
		ordenacioMap.put("estatEnviament", new String[] { "estat" });
		ordenacioMap.put("dataEnviament", new String[] { "enviatData" });
		ordenacioMap.put("portafirmesEstat", new String[] { "estat" });
		
		
		Page<DocumentPortafirmesEntity> docsEnvs = documentPortafirmesRepository.findAmbFiltrePaginat(
				entitat,
				filtre.getExpedientNom() == null || filtre.getExpedientNom().isEmpty(),
				filtre.getExpedientNom() != null ? filtre.getExpedientNom().trim() : "",
				filtre.getDocumentNom() == null || filtre.getDocumentNom().isEmpty(),
				filtre.getDocumentNom() != null ? filtre.getDocumentNom().trim() : "",
				filtre.getDataEnviamentInici() == null, 
				filtre.getDataEnviamentInici(), 
				filtre.getDataEnviamentFinal() == null, 
				DateHelper.toDateFinalDia(filtre.getDataEnviamentFinal()), 
				filtre.getPortafirmesEstat() == null, 
				filtre.getPortafirmesEstat(), 
				paginacioHelper.toSpringDataPageable(paginacioParams, ordenacioMap));
		
		return paginacioHelper.toPaginaDto(docsEnvs, SeguimentDto.class);
		
	}
	
	
	@Override
	@Transactional(readOnly = true)
	public PaginaDto<SeguimentConsultaPinbalDto> findConsultesPinbal(
			Long entitatId,
			SeguimentConsultaFiltreDto filtre, 
			PaginacioParamsDto paginacioParams) {
		
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId, false, true, false, false, false);
		
		Map<String, String[]> ordenacioMap = new HashMap<String, String[]>();
		ordenacioMap.put("expedientNumeroTitol", new String[] { "exp.nom" });
		ordenacioMap.put("procedimentCodiNom", new String[] { "metaexp.nom" });
		
		
		Page<ConsultaPinbalEntity> cons = consultaPinbalRepository.findAmbFiltrePaginat(
				entitat,
				filtre.getExpedientId() == null,
				filtre.getExpedientId(),
				filtre.getMetaExpedientId() == null,
				filtre.getMetaExpedientId(),
				filtre.getServei() == null, 
				filtre.getServei(),
				filtre.getCreatedByCodi() == null,
				filtre.getCreatedByCodi() != null ? usuariRepository.findOne(filtre.getCreatedByCodi()) : null,
				filtre.getDataInici() == null, 
				DateHelper.toDateInicialDia(filtre.getDataInici()), 
				filtre.getDataFinal() == null, 
				DateHelper.toDateFinalDia(filtre.getDataFinal()), 
				filtre.getEstat() == null, 
				filtre.getEstat(), 
				paginacioHelper.toSpringDataPageable(paginacioParams, ordenacioMap));
		
		return paginacioHelper.toPaginaDto(cons, SeguimentConsultaPinbalDto.class);
		
	}
	
	
	
	
	@Override
	@Transactional(readOnly = true)
	public ResultDto<SeguimentDto> findNotificacionsEnviaments(
			Long entitatId,
			SeguimentNotificacionsFiltreDto filtre, 
			PaginacioParamsDto paginacioParams, 
			ResultEnumDto resultEnum) {
		
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId, false, true, false, false, false);
		
		ResultDto<SeguimentDto> result = new ResultDto<SeguimentDto>();
		
		DocumentNotificacioTipusEnumDto tipus = null;
		if (filtre.getEnviamentTipus() != null) {
			switch (filtre.getEnviamentTipus()) {
			case NOTIFICACIO:
				tipus = DocumentNotificacioTipusEnumDto.NOTIFICACIO;
				break;
			case COMUNICACIO:
				tipus = DocumentNotificacioTipusEnumDto.COMUNICACIO;
				break;
			}
		}
		
		DocumentNotificacioEstatEnumDto estatNotificacio = null;
		String estatEnviament = null;
		if (filtre.getNotificacioEstat() != null) {
			switch (filtre.getNotificacioEstat()) {
			case PENDENT:
				estatNotificacio = DocumentNotificacioEstatEnumDto.PENDENT;
				break;
			case ENVIADA:
				estatNotificacio = DocumentNotificacioEstatEnumDto.ENVIADA;
				break;
			case REGISTRADA:
				estatNotificacio = DocumentNotificacioEstatEnumDto.REGISTRADA;
				break;				
			case FINALITZADA:
				estatNotificacio = DocumentNotificacioEstatEnumDto.FINALITZADA;
				break;
			case PROCESSADA:
				estatNotificacio = DocumentNotificacioEstatEnumDto.PROCESSADA;
				break;
			case NOTIFICADA:
				estatEnviament = "NOTIFICADA";
				break;
			case REBUTJADA:
				estatEnviament = "REBUTJADA";
				break;
			case ENVIADA_SIR:
				estatEnviament = "ENVIAT_SIR";
				break;
			}
		}

		if (resultEnum == ResultEnumDto.PAGE) {
			
			Map<String, String[]> ordenacioMap = new HashMap<String, String[]>();
			ordenacioMap.put("expedientNom", new String[] { "expedient.nom" });
			ordenacioMap.put("documentNom", new String[] { "document.nom" });
			ordenacioMap.put("estatEnviament", new String[] { "estat" });
			ordenacioMap.put("dataEnviament", new String[] { "createdDate" });
			
			// ================================  RETURNS PAGE (DATATABLE) ==========================================
			Page<DocumentNotificacioEntity> docsEnvs = documentNotificacioRepository.findAmbFiltrePaginat(
					entitat,
					filtre.getExpedientId() == null,
					filtre.getExpedientId(),
					Utils.isEmpty(filtre.getDocumentNom()),
					filtre.getDocumentNom() != null ? filtre.getDocumentNom() : "",
					filtre.getDataInici() == null, 
					filtre.getDataInici(), 
					filtre.getDataFinal() == null, 
					DateHelper.toDateFinalDia(filtre.getDataFinal()), 
					estatNotificacio == null, 
					estatNotificacio, 
					estatEnviament == null, 
					estatEnviament, 					
					tipus == null,
					tipus,
					filtre.getConcepte() == null,
					filtre.getConcepte() != null ? filtre.getConcepte() : "",
					filtre.getInteressat() == null,
					filtre.getInteressat() != null ? filtre.getInteressat() : "",
					filtre.getOrganId() == null,
					filtre.getOrganId(),
					filtre.getProcedimentId() == null,
					filtre.getProcedimentId(),	
					filtre.isNomesAmbError(),
					paginacioHelper.toSpringDataPageable(paginacioParams, ordenacioMap));
			
	
			PaginaDto<SeguimentDto> paginaDto = paginacioHelper.toPaginaDto(
					docsEnvs,
					SeguimentDto.class);
			result.setPagina(paginaDto);
		} else {
			
			// ==================================  RETURNS IDS (SELECCIONAR TOTS) ============================================
			List<Long> idsDocsEnvs = documentNotificacioRepository.findIdsAmbFiltrePaginat(
					entitat,
					filtre.getExpedientId() == null,
					filtre.getExpedientId(),
					Utils.isEmpty(filtre.getDocumentNom()),
					filtre.getDocumentNom() != null ? filtre.getDocumentNom() : "",
					filtre.getDataInici() == null, 
					filtre.getDataInici(), 
					filtre.getDataFinal() == null, 
					DateHelper.toDateFinalDia(filtre.getDataFinal()), 
					estatNotificacio == null, 
					estatNotificacio, 
					estatEnviament == null, 
					estatEnviament, 					
					tipus == null,
					tipus,
					filtre.getConcepte() == null,
					filtre.getConcepte() != null ? filtre.getConcepte() : "",
					filtre.getInteressat() == null,
					filtre.getInteressat() != null ? filtre.getInteressat() : "",
					filtre.getOrganId() == null,
					filtre.getOrganId(),
					filtre.getProcedimentId() == null,
					filtre.getProcedimentId());			
			
			result.setIds(idsDocsEnvs);
		}

		return result;
		
	}
	
	
	@Override
	@Transactional(readOnly = true)
	public PaginaDto<SeguimentDto> findTasques(
			Long entitatId,
			SeguimentFiltreDto filtre, 
			PaginacioParamsDto paginacioParams) {
		
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId, false, true, false, false, false);
		
		Map<String, String[]> ordenacioMap = new HashMap<String, String[]>();
		ordenacioMap.put("expedientNom", new String[] { "expedient.nom" });
		ordenacioMap.put("tascaNom", new String[] { "metaExpedientTasca.nom" });
		ordenacioMap.put("tascaEstat", new String[] { "estat" });
		ordenacioMap.put("responsableActualNom", new String[] { "responsable.nom" });
		ordenacioMap.put("data", new String[] { "dataInici" });
		
		UsuariEntity responsable = filtre.getResponsableCodi() != null ? usuariHelper.getUsuariByCodi(filtre.getResponsableCodi()) : null;
		MetaExpedientTascaEntity metaExpedientTascaEntity = filtre.getMetaExpedientTascaId() != null ? metaExpedientTascaRepository.findOne(filtre.getMetaExpedientTascaId()) : null;
		
		
		Page<ExpedientTascaEntity> docsEnvs = expedientTascaRepository.findAmbFiltrePaginat(
				entitat,
				filtre.getExpedientNom() == null || filtre.getExpedientNom().isEmpty(),
				filtre.getExpedientNom() != null ? filtre.getExpedientNom().trim() : "",
				metaExpedientTascaEntity == null, 
				metaExpedientTascaEntity, 
				filtre.getDataInici() == null, 
				filtre.getDataInici(), 
				filtre.getDataFinal() == null, 
				DateHelper.toDateFinalDia(filtre.getDataFinal()), 
				responsable == null, 
				responsable, 
				filtre.getTascaEstat() == null, 
				filtre.getTascaEstat(), 
				paginacioHelper.toSpringDataPageable(paginacioParams, ordenacioMap));
		
		return paginacioHelper.toPaginaDto(docsEnvs, SeguimentDto.class);
		
	}
	
	
	
	
	@Override
	@Transactional(readOnly = true)
	public PaginaDto<ExpedientPeticioListDto> findAnotacionsPendents(
			Long entitatId,
			ExpedientPeticioFiltreDto filtre, 
			PaginacioParamsDto paginacioParams, 
			String rolActual) {
		
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId, false, true, false, false, false);
		
		MetaExpedientEntity metaExpedientFiltre = null;
		if (filtre.getMetaExpedientId() != null) {
			metaExpedientFiltre = metaExpedientRepository.findOne(filtre.getMetaExpedientId());
		}
		
		PermisosPerAnotacions permisosPerAnotacions = expedientPeticioHelper.findPermisosPerAnotacions(
				entitatId,
				rolActual, 
				null);
		
		Page<ExpedientPeticioEntity> paginaExpedientPeticios = expedientPeticioRepository.findByEntitatAndFiltre(
				entitat,
				rolActual,
				permisosPerAnotacions.getProcedimentsPermesos(0),
				permisosPerAnotacions.getProcedimentsPermesos(1),
				permisosPerAnotacions.getProcedimentsPermesos(2),
				permisosPerAnotacions.getProcedimentsPermesos(3),
				permisosPerAnotacions.getAdminOrganCodisOrganAmbDescendents(0),
				permisosPerAnotacions.getAdminOrganCodisOrganAmbDescendents(1),
				permisosPerAnotacions.getAdminOrganCodisOrganAmbDescendents(2),
				permisosPerAnotacions.getAdminOrganCodisOrganAmbDescendents(3),
				permisosPerAnotacions.getIdsGrupsPermesos() == null,
				permisosPerAnotacions.getIdsGrupsPermesos(),
				metaExpedientFiltre == null,
				metaExpedientFiltre,
				StringUtils.isEmpty(filtre.getNumero()),
				filtre.getNumero() != null ? StringUtils.trim(filtre.getNumero()) : "",		
				StringUtils.isEmpty(filtre.getExtracte()),
				filtre.getExtracte() != null ? StringUtils.trim(filtre.getExtracte()) : "",							
				StringUtils.isEmpty(filtre.getDestinacioCodi()),
				StringUtils.trim(filtre.getDestinacioCodi()),
				filtre.getDataInicial() == null,
				filtre.getDataInicial(),
				filtre.getDataFinal() == null,
				DateHelper.toDateFinalDia(filtre.getDataFinal()),
				false,
				"PENDENT",
				filtre.getAccioEnum() == null,
				filtre.getAccioEnum(), 
				StringUtils.isEmpty(filtre.getInteressat()), 
				filtre.getInteressat() != null ? StringUtils.trim(filtre.getInteressat()) : "", 
				paginacioHelper.toSpringDataPageable(
						paginacioParams,
						null));
		

		return paginacioHelper.toPaginaDto(paginaExpedientPeticios, ExpedientPeticioListDto.class);
		
	}
	
	
	

	@Override
	@Transactional(readOnly = true)
	public ResultDto<SeguimentArxiuPendentsDto> findPendentsArxiu(
			Long entitatId,
			SeguimentArxiuPendentsFiltreDto filtre,
			PaginacioParamsDto paginacioParams,
			String rolActual,
			ResultEnumDto resultEnum,
			ArxiuPendentTipusEnumDto arxiuPendentTipusEnum,
			Long organActual) {
		ResultDto<SeguimentArxiuPendentsDto> result = new ResultDto<SeguimentArxiuPendentsDto>();
	
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId);

		MetaExpedientEntity metaExpedient = null;
		if (filtre.getMetaExpedientId() != null) {
			metaExpedient = entityComprovarHelper.comprovarMetaExpedient(
					entitat,
					filtre.getMetaExpedientId());
		}
		ExpedientEntity expedient = null;
		if (filtre.getExpedientId() != null) {
			expedient = entityComprovarHelper.comprovarExpedient(
					entitatId,
					filtre.getExpedientId());
		}

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		boolean nomesAgafats = true;
		if (rolActual.equals("IPA_ADMIN") || rolActual.equals("IPA_ORGAN_ADMIN")) {
			nomesAgafats = false;
		}
		List<MetaExpedientEntity> metaExpedientsPermesos = metaExpedientHelper.findPermesosAccioMassiva(
				entitatId,
				rolActual);
		if (CollectionUtils.isEmpty(metaExpedientsPermesos)) {
			metaExpedientsPermesos = null;
		}
		
		PermisosPerExpedientsDto permisosPerExpedients = expedientHelper.findPermisosPerExpedients(
				entitatId,
				rolActual,
				organActual);

		// =========================================== EXPEDIENT =======================================================
		if (arxiuPendentTipusEnum == ArxiuPendentTipusEnumDto.EXPEDIENT) {

			Map<String, String[]> ordenacioMap = new HashMap<String, String[]>();
			ordenacioMap.put("expedientNumeroNom", new String[] { "codi", "any", "sequencia", "nom" });
			ordenacioMap.put("dataDarrerIntent", new String[] { "arxiuIntentData" });

			if (resultEnum == ResultEnumDto.PAGE) {
				
//				Page<ExpedientEntity> exps = expedientRepository.findArxiuPendents(
//						entitat,
//						permisosPerExpedients.getIdsMetaExpedientsPermesos() == null,
//						permisosPerExpedients.getIdsMetaExpedientsPermesos(),
//						permisosPerExpedients.getIdsOrgansPermesos() == null,
//						permisosPerExpedients.getIdsOrgansPermesos(),
//						permisosPerExpedients.getIdsMetaExpedientOrganPairsPermesos() == null,
//						permisosPerExpedients.getIdsMetaExpedientOrganPairsPermesos(),
//						permisosPerExpedients.getIdsOrgansAmbProcedimentsComunsPermesos() == null,
//						permisosPerExpedients.getIdsOrgansAmbProcedimentsComunsPermesos(),	
//						permisosPerExpedients.getIdsProcedimentsComuns(),
//						nomesAgafats,
//						auth.getName(),
//						filtre.getElementNom() == null || filtre.getElementNom().isEmpty(),
//						filtre.getElementNom() != null ? filtre.getElementNom().trim() : "",
//						metaExpedient == null,
//						metaExpedient,
//						paginacioHelper.toSpringDataPageable(paginacioParams, ordenacioMap));

				
				// ============ RETURNS PAGE (DATATABLE) ===============
				Page<ExpedientEntity> exps = expedientRepositoryCommnand.findArxiuPendents(
						entitat,
						nomesAgafats,
						auth.getName(),
						metaExpedient,
						filtre.getElementNom(),
						filtre.getDataCreacioInici(),
						filtre.getDataCreacioFi(),
						metaExpedientsPermesos,
						paginacioHelper.toSpringDataPageable(paginacioParams, ordenacioMap));
				result.setPagina(paginacioHelper.toPaginaDto(exps, SeguimentArxiuPendentsDto.class));

			} else {
				// =========== RETURNS IDS (SELECCIONAR TOTS) ==============
				List<Long> exps = expedientRepositoryCommnand.findIdsArxiuPendents(
						entitat,
						nomesAgafats,
						auth.getName(),
						metaExpedient,
						filtre.getElementNom(),
						filtre.getDataCreacioInici(),
						filtre.getDataCreacioFi(),
						metaExpedientsPermesos);
				result.setIds(exps);
			}

		// =========================================== DOCUMENT =========================================================
		} else if (arxiuPendentTipusEnum == ArxiuPendentTipusEnumDto.DOCUMENT) {

			Map<String, String[]> ordenacioMap = new HashMap<String, String[]>();
			ordenacioMap.put("expedientNumeroNom", new String[] { "expedient.codi", "expedient.any", "expedient.sequencia" });
			ordenacioMap.put("elementNom", new String[] { "nom" });
			ordenacioMap.put("dataDarrerIntent", new String[] { "c1.arxiuIntentData" });

			if (resultEnum == ResultEnumDto.PAGE) {

//				List<DocumentEntity> docs1 = documentRepository.findArxiuPendents2();
				
				
				// ============ RETURNS PAGE (DATATABLE) ================
				Page<DocumentEntity> docs = documentRepository.findArxiuPendents(
						entitat,
						metaExpedientsPermesos,
						nomesAgafats,
						auth.getName(),
						filtre.getElementNom() == null || filtre.getElementNom().isEmpty(),
						filtre.getElementNom() != null ? filtre.getElementNom().trim() : "",
						expedient == null,
						expedient,
						metaExpedient == null,
						metaExpedient, 
						filtre.getDataCreacioInici() == null,
						filtre.getDataCreacioInici(),
						filtre.getDataCreacioFi() == null,
						DateHelper.toDateFinalDia(filtre.getDataCreacioFi()),
						paginacioHelper.toSpringDataPageable(paginacioParams, ordenacioMap));
				
				
//				Page<DocumentEntity> docs = documentRepository.findArxiuPendents(
//						entitat,
//						permisosPerExpedients.getIdsMetaExpedientsPermesos() == null,
//						permisosPerExpedients.getIdsMetaExpedientsPermesos(),
//						permisosPerExpedients.getIdsOrgansPermesos() == null,
//						permisosPerExpedients.getIdsOrgansPermesos(),
//						permisosPerExpedients.getIdsMetaExpedientOrganPairsPermesos() == null,
//						permisosPerExpedients.getIdsMetaExpedientOrganPairsPermesos(),
//						permisosPerExpedients.getIdsOrgansAmbProcedimentsComunsPermesos() == null,
//						permisosPerExpedients.getIdsOrgansAmbProcedimentsComunsPermesos(),	
//						permisosPerExpedients.getIdsProcedimentsComuns(),
//						nomesAgafats,
//						auth.getName(),
//						filtre.getElementNom() == null || filtre.getElementNom().isEmpty(),
//						filtre.getElementNom() != null ? filtre.getElementNom().trim() : "",
//						expedient == null,
//						expedient,
//						metaExpedient == null,
//						metaExpedient, 
//						paginacioHelper.toSpringDataPageable(paginacioParams, ordenacioMap));

				result.setPagina(paginacioHelper.toPaginaDto(docs, SeguimentArxiuPendentsDto.class));

			} else {

				// =========== RETURNS IDS (SELECCIONAR TOTS) ==============
				List<Long> docs = documentRepository.findIdsArxiuPendents(
						entitat,
						metaExpedientsPermesos,
						nomesAgafats,
						auth.getName(),
						filtre.getElementNom() == null || filtre.getElementNom().isEmpty(),
						filtre.getElementNom() != null ? filtre.getElementNom().trim() : "",
						expedient == null,
						expedient,
						metaExpedient == null,
						metaExpedient,
						filtre.getDataCreacioInici() == null,
						filtre.getDataCreacioInici(),
						filtre.getDataCreacioFi() == null,
						DateHelper.toDateFinalDia(filtre.getDataCreacioFi()));
				result.setIds(docs);
			}

		// =========================================== INTERESSAT =======================================================
		} else if (arxiuPendentTipusEnum == ArxiuPendentTipusEnumDto.INTERESSAT) {

			Map<String, String[]> ordenacioMap = new HashMap<String, String[]>();
			ordenacioMap.put("expedientNumeroNom", new String[] { "expedient.codi", "expedient.any", "expedient.sequencia" });
			ordenacioMap.put("elementNom", new String[] { "nom" });
			ordenacioMap.put("dataDarrerIntent", new String[] { "arxiuIntentData" });

			if (resultEnum == ResultEnumDto.PAGE) {

				// ============ RETURNS PAGE (DATATABLE) ================
				Page<InteressatEntity> ints = interessatRepository.findArxiuPendents(
						entitat,
						metaExpedientsPermesos,
						nomesAgafats,
						auth.getName(),
						filtre.getElementNom() == null || filtre.getElementNom().isEmpty(),
						filtre.getElementNom() != null ? filtre.getElementNom().trim() : "",
						expedient == null,
						expedient,
						metaExpedient == null,
						metaExpedient,
						filtre.getDataCreacioInici() == null,
						filtre.getDataCreacioInici(),
						filtre.getDataCreacioFi() == null,
						DateHelper.toDateFinalDia(filtre.getDataCreacioFi()),
						paginacioHelper.toSpringDataPageable(paginacioParams, ordenacioMap));

				result.setPagina(paginacioHelper.toPaginaDto(ints, SeguimentArxiuPendentsDto.class));
			} else {

				// =========== RETURNS IDS (SELECCIONAR TOTS) ==============
				List<Long> ints = interessatRepository.findIdsArxiuPendents(
						entitat,
						metaExpedientsPermesos,
						nomesAgafats,
						auth.getName(),
						filtre.getElementNom() == null || filtre.getElementNom().isEmpty(),
						filtre.getElementNom() != null ? filtre.getElementNom().trim() : "",
						expedient == null,
						expedient,
						metaExpedient == null,
						metaExpedient,
						filtre.getDataCreacioInici() == null,
						filtre.getDataCreacioInici(),
						filtre.getDataCreacioFi() == null,
						DateHelper.toDateFinalDia(filtre.getDataCreacioFi()));

				result.setIds(ints);

			}
		} else {
			throw new RuntimeException("Enum not supported");
		}

		return result;

	}



}
