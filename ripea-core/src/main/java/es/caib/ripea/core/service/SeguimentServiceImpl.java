package es.caib.ripea.core.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.caib.ripea.core.api.dto.ExpedientPeticioDto;
import es.caib.ripea.core.api.dto.ExpedientPeticioFiltreDto;
import es.caib.ripea.core.api.dto.PaginaDto;
import es.caib.ripea.core.api.dto.PaginacioParamsDto;
import es.caib.ripea.core.api.dto.SeguimentArxiuPendentsDto;
import es.caib.ripea.core.api.dto.SeguimentArxiuPendentsFiltreDto;
import es.caib.ripea.core.api.dto.SeguimentDto;
import es.caib.ripea.core.api.dto.SeguimentFiltreDto;
import es.caib.ripea.core.api.service.SeguimentService;
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
import es.caib.ripea.core.helper.PaginacioHelper;
import es.caib.ripea.core.helper.UsuariHelper;
import es.caib.ripea.core.repository.DocumentNotificacioRepository;
import es.caib.ripea.core.repository.DocumentPortafirmesRepository;
import es.caib.ripea.core.repository.DocumentRepository;
import es.caib.ripea.core.repository.ExpedientPeticioRepository;
import es.caib.ripea.core.repository.ExpedientRepository;
import es.caib.ripea.core.repository.ExpedientTascaRepository;
import es.caib.ripea.core.repository.InteressatRepository;
import es.caib.ripea.core.repository.MetaExpedientTascaRepository;

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
				filtre.getExpedientNom(),
				filtre.getDocumentNom() == null || filtre.getDocumentNom().isEmpty(),
				filtre.getDocumentNom(),
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
	public PaginaDto<SeguimentDto> findNotificacionsEnviaments(
			Long entitatId,
			SeguimentFiltreDto filtre, 
			PaginacioParamsDto paginacioParams) {
		
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId, false, true, false, false, false);
		
		Map<String, String[]> ordenacioMap = new HashMap<String, String[]>();
		ordenacioMap.put("expedientNom", new String[] { "expedient.nom" });
		ordenacioMap.put("documentNom", new String[] { "document.nom" });
		ordenacioMap.put("estatEnviament", new String[] { "estat" });
		ordenacioMap.put("dataEnviament", new String[] { "createdDate" });
		
		Page<DocumentNotificacioEntity> docsEnvs = documentNotificacioRepository.findAmbFiltrePaginat(
				entitat,
				filtre.getExpedientNom() == null || filtre.getExpedientNom().isEmpty(),
				filtre.getExpedientNom(),
				filtre.getDocumentNom() == null || filtre.getDocumentNom().isEmpty(),
				filtre.getDocumentNom(),
				filtre.getDataEnviamentInici() == null, 
				filtre.getDataEnviamentInici(), 
				filtre.getDataEnviamentFinal() == null, 
				DateHelper.toDateFinalDia(filtre.getDataEnviamentFinal()), 
				filtre.getNotificacioEstat() == null, 
				filtre.getNotificacioEstat(), 
				paginacioHelper.toSpringDataPageable(paginacioParams, ordenacioMap));
		
		return paginacioHelper.toPaginaDto(docsEnvs, SeguimentDto.class);
		
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
		ordenacioMap.put("responsableNom", new String[] { "responsable.nom" });
		ordenacioMap.put("data", new String[] { "dataInici" });
		
		UsuariEntity responsable = filtre.getResponsableCodi() != null ? usuariHelper.getUsuariByCodi(filtre.getResponsableCodi()) : null;
		MetaExpedientTascaEntity metaExpedientTascaEntity = filtre.getMetaExpedientTascaId() != null ? metaExpedientTascaRepository.findOne(filtre.getMetaExpedientTascaId()) : null;
		
		
		Page<ExpedientTascaEntity> docsEnvs = expedientTascaRepository.findAmbFiltrePaginat(
				entitat,
				filtre.getExpedientNom() == null || filtre.getExpedientNom().isEmpty(),
				filtre.getExpedientNom(),
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
	public PaginaDto<ExpedientPeticioDto> findExpedientsPendents(
			Long entitatId,
			ExpedientPeticioFiltreDto filtre, 
			PaginacioParamsDto paginacioParams) {
		
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId, false, true, false, false, false);
		
		MetaExpedientEntity metaExpedientFiltre = null;
		if (filtre.getMetaExpedientId() != null) {
			metaExpedientFiltre = entityComprovarHelper.comprovarMetaExpedientPerExpedient(
					entitat,
					filtre.getMetaExpedientId(),
					true,
					false,
					false,
					false,
					false, null);
		}
		
		
		Page<ExpedientPeticioEntity> paginaExpedientPeticios = expedientPeticioRepository.findByEntitatAndFiltre(
				entitat,
				true,
				null,
				metaExpedientFiltre == null,
				metaExpedientFiltre,
				true,
				null,
				filtre.getNumero() == null || filtre.getNumero().isEmpty(),
				filtre.getNumero(),
				filtre.getExtracte() == null ||
				filtre.getExtracte().isEmpty(),
				filtre.getExtracte(),
				true,
				null,
				filtre.getDataInicial() == null,
				filtre.getDataInicial(),
				filtre.getDataFinal() == null,
				DateHelper.toDateFinalDia(filtre.getDataFinal()),
				false,
				"PENDENT",
				true,
				null,
				paginacioHelper.toSpringDataPageable(
						paginacioParams,
						null));
		
		return paginacioHelper.toPaginaDto(paginaExpedientPeticios, ExpedientPeticioDto.class);
		
	}
	
	
	
	@Override
	@Transactional(readOnly = true)
	public PaginaDto<SeguimentArxiuPendentsDto> findArxiuPendentsExpedients(
			Long entitatId,
			SeguimentArxiuPendentsFiltreDto filtre, 
			PaginacioParamsDto paginacioParams) {
		
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId, false, true, false, false, false);
		
		MetaExpedientEntity metaExpedient = null;
		if (filtre.getMetaExpedientId() != null) {
			metaExpedient = entityComprovarHelper.comprovarMetaExpedientPerExpedient(
					entitat,
					filtre.getMetaExpedientId(),
					true,
					false,
					false,
					false, 
					false, 
					null);
		}
		
		Map<String, String[]> ordenacioMap = new HashMap<String, String[]>();
		ordenacioMap.put("expedientNumeroNom", new String[] { "codi", "any", "sequencia", "nom" });
		ordenacioMap.put("dataDarrerIntent", new String[] { "arxiuIntentData" });
		

		Page<ExpedientEntity> exps = expedientRepository.findArxiuPendents(
				entitat,
				filtre.getElementNom() == null || filtre.getElementNom().isEmpty(),
				filtre.getElementNom(),
				metaExpedient == null,
				metaExpedient,
				paginacioHelper.toSpringDataPageable(paginacioParams, ordenacioMap));

		
		return paginacioHelper.toPaginaDto(exps, SeguimentArxiuPendentsDto.class);
		
	}
	
	
	@Override
	@Transactional(readOnly = true)
	public PaginaDto<SeguimentArxiuPendentsDto> findArxiuPendentsDocuments(
			Long entitatId,
			SeguimentArxiuPendentsFiltreDto filtre, 
			PaginacioParamsDto paginacioParams) {
		
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId, false, false, false, false, false);
		
		MetaExpedientEntity metaExpedient = null;
		if (filtre.getMetaExpedientId() != null) {
			metaExpedient = entityComprovarHelper.comprovarMetaExpedientPerExpedient(
					entitat,
					filtre.getMetaExpedientId(),
					false,
					false,
					false,
					false, 
					false,
					null);
		}
		ExpedientEntity expedient = null;
		if (filtre.getExpedientId() != null) {
			expedient = entityComprovarHelper.comprovarExpedient(
					entitatId,
					filtre.getExpedientId(),
					false,
					false,
					false,
					false,
					false,
					false,
					null);
		}
		
		Map<String, String[]> ordenacioMap = new HashMap<String, String[]>();
		ordenacioMap.put("expedientNumeroNom", new String[] { "expedient.codi", "expedient.any", "expedient.sequencia"});
		ordenacioMap.put("elementNom", new String[] { "nom" });
		ordenacioMap.put("dataDarrerIntent", new String[] { "arxiuIntentData" });
		

		Page<DocumentEntity> docs = documentRepository.findArxiuPendents(
				entitat,
				filtre.getElementNom() == null || filtre.getElementNom().isEmpty(),
				filtre.getElementNom(),
				expedient == null,
				expedient,
				metaExpedient == null,
				metaExpedient,
				paginacioHelper.toSpringDataPageable(paginacioParams, ordenacioMap));

		
		return paginacioHelper.toPaginaDto(docs, SeguimentArxiuPendentsDto.class);
		
	}
	

	@Override
	@Transactional(readOnly = true)
	public PaginaDto<SeguimentArxiuPendentsDto> findArxiuPendentsInteressats(
			Long entitatId,
			SeguimentArxiuPendentsFiltreDto filtre, 
			PaginacioParamsDto paginacioParams) {
		
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId, false, false, false, false, false);
		
		MetaExpedientEntity metaExpedient = null;
		if (filtre.getMetaExpedientId() != null) {
			metaExpedient = entityComprovarHelper.comprovarMetaExpedientPerExpedient(
					entitat,
					filtre.getMetaExpedientId(),
					false,
					false,
					false,
					false, 
					false,
					null);
		}
		ExpedientEntity expedient = null;
		if (filtre.getExpedientId() != null) {
			expedient = entityComprovarHelper.comprovarExpedient(
					entitatId,
					filtre.getExpedientId(),
					false,
					false,
					false,
					false,
					false,
					false,
					null);
		}
		
		Map<String, String[]> ordenacioMap = new HashMap<String, String[]>();
		ordenacioMap.put("expedientNumeroNom", new String[] { "expedient.codi", "expedient.any", "expedient.sequencia"});
		ordenacioMap.put("elementNom", new String[] { "nom" });
		ordenacioMap.put("dataDarrerIntent", new String[] { "arxiuIntentData" });
		

		Page<InteressatEntity> ints = interessatRepository.findArxiuPendents(
				entitat,
				filtre.getElementNom() == null || filtre.getElementNom().isEmpty(),
				filtre.getElementNom(),
				expedient == null,
				expedient,
				metaExpedient == null,
				metaExpedient,
				paginacioHelper.toSpringDataPageable(paginacioParams, ordenacioMap));

		
		return paginacioHelper.toPaginaDto(ints, SeguimentArxiuPendentsDto.class);
		
	}


}
