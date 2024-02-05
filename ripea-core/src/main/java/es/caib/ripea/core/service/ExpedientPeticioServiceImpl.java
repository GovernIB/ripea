/**
 * 
 */
package es.caib.ripea.core.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Strings;

import es.caib.distribucio.rest.client.integracio.domini.AnotacioRegistreId;
import es.caib.distribucio.rest.client.integracio.domini.Estat;
import es.caib.plugins.arxiu.api.Document;
import es.caib.plugins.arxiu.api.DocumentContingut;
import es.caib.plugins.arxiu.api.Firma;
import es.caib.plugins.arxiu.api.FirmaTipus;
import es.caib.ripea.core.api.dto.ArxiuFirmaDto;
import es.caib.ripea.core.api.dto.DocumentDto;
import es.caib.ripea.core.api.dto.ExpedientDto;
import es.caib.ripea.core.api.dto.ExpedientEstatEnumDto;
import es.caib.ripea.core.api.dto.ExpedientPeticioDto;
import es.caib.ripea.core.api.dto.ExpedientPeticioEstatEnumDto;
import es.caib.ripea.core.api.dto.ExpedientPeticioEstatViewEnumDto;
import es.caib.ripea.core.api.dto.ExpedientPeticioFiltreDto;
import es.caib.ripea.core.api.dto.ExpedientPeticioListDto;
import es.caib.ripea.core.api.dto.FitxerDto;
import es.caib.ripea.core.api.dto.MassiuAnnexProcesarFiltreDto;
import es.caib.ripea.core.api.dto.MetaExpedientDto;
import es.caib.ripea.core.api.dto.MetaExpedientSelectDto;
import es.caib.ripea.core.api.dto.PaginaDto;
import es.caib.ripea.core.api.dto.PaginacioParamsDto;
import es.caib.ripea.core.api.dto.RegistreAnnexDto;
import es.caib.ripea.core.api.dto.RegistreDto;
import es.caib.ripea.core.api.dto.RegistreJustificantDto;
import es.caib.ripea.core.api.dto.ResultDto;
import es.caib.ripea.core.api.dto.ResultEnumDto;
import es.caib.ripea.core.api.service.ExpedientPeticioService;
import es.caib.ripea.core.entity.DocumentEntity;
import es.caib.ripea.core.entity.EntitatEntity;
import es.caib.ripea.core.entity.ExpedientEntity;
import es.caib.ripea.core.entity.ExpedientPeticioEntity;
import es.caib.ripea.core.entity.InteressatEntity;
import es.caib.ripea.core.entity.MetaExpedientEntity;
import es.caib.ripea.core.entity.RegistreAnnexEntity;
import es.caib.ripea.core.entity.RegistreInteressatEntity;
import es.caib.ripea.core.helper.CacheHelper;
import es.caib.ripea.core.helper.ConfigHelper;
import es.caib.ripea.core.helper.ConversioTipusHelper;
import es.caib.ripea.core.helper.DateHelper;
import es.caib.ripea.core.helper.DistribucioHelper;
import es.caib.ripea.core.helper.EntityComprovarHelper;
import es.caib.ripea.core.helper.ExpedientHelper;
import es.caib.ripea.core.helper.ExpedientPeticioHelper;
import es.caib.ripea.core.helper.ExpedientPeticioHelper0;
import es.caib.ripea.core.helper.MetaExpedientHelper;
import es.caib.ripea.core.helper.OrganGestorHelper;
import es.caib.ripea.core.helper.PaginacioHelper;
import es.caib.ripea.core.helper.PermisosHelper;
import es.caib.ripea.core.helper.PermisosPerAnotacions;
import es.caib.ripea.core.helper.PluginHelper;
import es.caib.ripea.core.helper.SynchronizationHelper;
import es.caib.ripea.core.repository.DocumentRepository;
import es.caib.ripea.core.repository.EntitatRepository;
import es.caib.ripea.core.repository.ExpedientPeticioRepository;
import es.caib.ripea.core.repository.ExpedientRepository;
import es.caib.ripea.core.repository.MetaExpedientRepository;
import es.caib.ripea.core.repository.OrganGestorRepository;
import es.caib.ripea.core.repository.RegistreAnnexRepository;
import es.caib.ripea.core.repository.RegistreRepository;
import es.caib.ripea.core.repository.UsuariRepository;
import lombok.extern.slf4j.Slf4j;


/**
 * Implementació dels mètodes per a gestionar expedient peticions.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
@Slf4j
public class ExpedientPeticioServiceImpl implements ExpedientPeticioService {

	@Autowired
	private EntityComprovarHelper entityComprovarHelper;
	@Autowired
	private PaginacioHelper paginacioHelper;
	@Autowired
	private ExpedientPeticioRepository expedientPeticioRepository;
	@Autowired
	private RegistreRepository registreRepository;
	@Autowired
	private RegistreAnnexRepository registreAnnexRepository;
	@Autowired
	private ConversioTipusHelper conversioTipusHelper;
	@Autowired
	private MetaExpedientRepository metaExpedientRepository;
	@Autowired
	private ExpedientRepository expedientRepository;
	@Autowired
	private PluginHelper pluginHelper;
	@Autowired
	private EntitatRepository entitatRepository;
	@Autowired
	private ExpedientHelper  expedientHelper;
	@Autowired
	private CacheHelper cacheHelper;
	@Autowired
	private ConfigHelper configHelper;
	@Resource
	private OrganGestorRepository organGestorRepository;
	@Resource
	private ExpedientPeticioHelper expedientPeticioHelper;
	@Resource
	private DocumentRepository documentRepository;
	@Resource
	private OrganGestorHelper organGestorHelper;
	@Autowired
	private PermisosHelper permisosHelper;
	@Autowired
	private MetaExpedientHelper metaExpedientHelper;
	
	@Autowired
	private ExpedientPeticioHelper0 expedientPeticioHelper0;
	@Autowired
	private UsuariRepository usuariRepository;

	
	@Transactional(readOnly = true)
	@Override
	public PaginaDto<ExpedientPeticioListDto> findAmbFiltre(
			Long entitatId,
			ExpedientPeticioFiltreDto filtre,
			PaginacioParamsDto paginacioParams, 
			String rolActual, 
			Long organActualId) {
		log.debug("Consultant els expedient peticions segons el filtre (" +
				"entitatId=" +
				entitatId +
				", filtre=" +
				filtre +
				", paginacioParams=" +
				paginacioParams +
				")");

		final EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				false,
				false, 
				true, 
				false);

		Map<String, String[]> ordenacioMap = new HashMap<String, String[]>();
		ordenacioMap.put("numero", new String[] { "codi", "any", "sequencia" });
		ordenacioMap.put("registre.destiCodiINom", new String[] {"registre.destiCodi"});

		// enum with states accesibles from filter in the view (without create state)
		ExpedientPeticioEstatViewEnumDto estatView = filtre.getEstat();
		
		MetaExpedientEntity metaExpedient = null;
		if (filtre.getMetaExpedientId() != null) {
			metaExpedient = entityComprovarHelper.comprovarMetaExpedient(entitat, filtre.getMetaExpedientId());
		}
		
		PermisosPerAnotacions permisosPerAnotacions = expedientPeticioHelper.findPermisosPerAnotacions(
				entitatId,
				rolActual, 
				organActualId);
		
		Page<ExpedientPeticioEntity> paginaExpedientPeticios = expedientPeticioRepository.findByEntitatAndFiltre(
				entitat,
				rolActual,
				permisosPerAnotacions.getProcedimentsPermesos(),
				permisosPerAnotacions.getAdminOrganCodisOrganAmbDescendents(),
				permisosPerAnotacions.isAdminOrganHasPermisAdminComu(),
				metaExpedient == null,
				metaExpedient,
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
				estatView == null,
				estatView != null ? estatView.toString() : null,
				filtre.getAccioEnum() == null,
				filtre.getAccioEnum(), 
				StringUtils.isEmpty(filtre.getInteressat()), 
				filtre.getInteressat() != null ? StringUtils.trim(filtre.getInteressat()) : "", 
				paginacioHelper.toSpringDataPageable(
						paginacioParams,
						ordenacioMap));
		


		PaginaDto<ExpedientPeticioListDto> result = paginacioHelper.toPaginaDto(
				paginaExpedientPeticios,
				ExpedientPeticioListDto.class);

		return result;

	}
	
	
	@Transactional(readOnly = true)
	@Override
	public ResultDto<ExpedientPeticioListDto> findComunicadesAmbFiltre(
			ExpedientPeticioFiltreDto filtre,
			PaginacioParamsDto paginacioParams, 
			ResultEnumDto resultEnum) {
		log.debug("Consultant els expedient peticions comunicades segons el filtre (" +
				"filtre=" +
				filtre +
				", paginacioParams=" +
				paginacioParams +
				")");
		
		ResultDto<ExpedientPeticioListDto> result = new ResultDto<ExpedientPeticioListDto>();



		if (resultEnum == ResultEnumDto.PAGE) {
			
			Map<String, String[]> ordenacioMap = new HashMap<String, String[]>();
			// ================================  RETURNS PAGE (DATATABLE) ==========================================
		Page<ExpedientPeticioEntity> paginaExpedientPeticios = expedientPeticioRepository.findComunicadesByFiltre(
				StringUtils.isEmpty(filtre.getNumero()),
				filtre.getNumero() != null ? StringUtils.trim(filtre.getNumero()) : "",		
				filtre.getDataInicial() == null,
				filtre.getDataInicial(),
				filtre.getDataFinal() == null,
				DateHelper.toDateFinalDia(filtre.getDataFinal()),
				filtre.getEstatAll() == null,
				filtre.getEstatAll(),
				filtre.isNomesAmbErrorsConsulta(),
				paginacioHelper.toSpringDataPageable(
						paginacioParams,
						ordenacioMap));


		PaginaDto<ExpedientPeticioListDto> paginaDto = paginacioHelper.toPaginaDto(
				paginaExpedientPeticios,
				ExpedientPeticioListDto.class);
		result.setPagina(paginaDto);
		} else {
			
			// ==================================  RETURNS IDS (SELECCIONAR TOTS) ============================================
			List<Long> idsExpedientPeticios = expedientPeticioRepository.findIdsComunicadesByFiltre(
					StringUtils.isEmpty(filtre.getNumero()),
					filtre.getNumero() != null ? StringUtils.trim(filtre.getNumero()) : "",		
					filtre.getDataInicial() == null,
					filtre.getDataInicial(),
					filtre.getDataFinal() == null,
					DateHelper.toDateFinalDia(filtre.getDataFinal()),
					filtre.getEstatAll() == null,
					filtre.getEstatAll(),
					filtre.isNomesAmbErrorsConsulta());
			
			result.setIds(idsExpedientPeticios);
		}

		return result;

	}


	@Transactional
	@Override
	public void comunicadaReprocessar(Long expedientPeticioId) throws Throwable {

		synchronized (SynchronizationHelper.get0To99Lock(expedientPeticioId, SynchronizationHelper.locksAnnotacions)) {
			expedientPeticioHelper0.consultarIGuardarAnotacioPeticioPendent(expedientPeticioId, true);
		}
		
		
	}

	
	@Transactional(readOnly = true)
	@Override
	public MetaExpedientDto findMetaExpedientByEntitatAndProcedimentCodi(
			String entitatCodi,
			String procedimentCodi) {

		MetaExpedientDto metaExpedientDto = null;

		if (procedimentCodi != null) {

			EntitatEntity entitat = entitatRepository.findByUnitatArrel(entitatCodi);

			if (entitat != null) {
				// set metaexpedient to which expedient will belong if peticion is accepted
				List<MetaExpedientEntity> metaExpedients = metaExpedientRepository.findByEntitatAndClassificacio(
						entitat,
						procedimentCodi);

				if (!metaExpedients.isEmpty()) {
					metaExpedientDto = conversioTipusHelper.convertir(metaExpedients.get(0),
							MetaExpedientDto.class);
				}
			}
		}
		return metaExpedientDto;
	}

	@Transactional
	@Override
	public ExpedientDto findByEntitatAndMetaExpedientAndExpedientNumero(
			Long entitatId,
			Long metaExpedientId,
			String expedientNumero) {
		log.debug("Consultant el expedient("
				+ "entitatId=" + entitatId + ", "
				+ "expedientNumero=" + expedientNumero + ", "
				+ "metaExpedientId=" + metaExpedientId + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				false,
				false, 
				true, 
				false);
		MetaExpedientEntity metaExpedient = null;
		if (metaExpedientId != null) {
			metaExpedient = entityComprovarHelper.comprovarMetaExpedient(entitat, metaExpedientId);
		}
		ExpedientEntity expedientEntity = expedientRepository.findByEntitatAndMetaNodeAndNumero(
				entitat,
				metaExpedient,
				expedientNumero);
		if (expedientEntity == null) {
			return null;
		} else {
			return expedientHelper.toExpedientDto(expedientEntity);
		}
	}

	@Transactional(readOnly = true)
	@Override
	public List<ExpedientPeticioListDto> findByExpedientAmbFiltre(
			Long entitatId,
			Long expedientId,
			PaginacioParamsDto paginacioParams) {
		ExpedientEntity expedient = entityComprovarHelper.comprovarExpedient(
				expedientId,
				false,
				true,
				false,
				false,
				false,
				null);
		List<ExpedientPeticioEntity> peticions = expedientPeticioRepository.findByExpedient(
				expedient, 
				paginacioHelper.toSpringDataPageable(paginacioParams));
		return conversioTipusHelper.convertirList(
				peticions,
				ExpedientPeticioListDto.class);
	}

	@Transactional(readOnly = true)
	@Override
	public List<MetaExpedientSelectDto> findMetaExpedientSelect(String entitatCodi) {

		EntitatEntity entitat = entitatRepository.findByUnitatArrel(entitatCodi);

		List<MetaExpedientEntity> metaExpedients = metaExpedientRepository.findByEntitatAndActiuTrueOrderByNomAsc(entitat);

		return conversioTipusHelper.convertirList(metaExpedients,
				MetaExpedientSelectDto.class);
	}

	@Transactional(readOnly = true)
	@Override
	public FitxerDto getAnnexContent(Long annexId, boolean versioImprimible) {
		
		organGestorHelper.actualitzarOrganCodi(organGestorHelper.getOrganCodiFromAnnexId(annexId));
		RegistreAnnexEntity annex = registreAnnexRepository.findOne(annexId);
		FitxerDto fitxer = new FitxerDto();

		Document document = null;
		document = pluginHelper.arxiuDocumentConsultar(
				null,
				annex.getUuid(),
				null,
				true,
				versioImprimible);

		RegistreAnnexEntity registreAnnex = registreAnnexRepository.findOne(annexId);
		
		if (document != null) {
			DocumentContingut documentContingut = document.getContingut();
			if (documentContingut != null) {
				
				fitxer.setNom(versioImprimible ? registreAnnex.getNom().replace(".pdf", "_imprimible.pdf") : registreAnnex.getNom());
				fitxer.setContentType(documentContingut.getTipusMime());
				fitxer.setContingut(documentContingut.getContingut());
				fitxer.setTamany(documentContingut.getContingut() != null ? Long.valueOf(documentContingut.getContingut().length) : null);
			}
		}
		return fitxer;
	}
	
	@Transactional(readOnly = true)
	@Override
	public FitxerDto getJustificantContent(String arxiuUuid) {
		FitxerDto fitxer = new FitxerDto();
		Document document = null;
		document = pluginHelper.arxiuDocumentConsultar(
				null,
				arxiuUuid,
				null,
				true,
				true);

		if (document != null) {
			DocumentContingut documentContingut = document.getContingut();
			if (documentContingut != null) {
				fitxer.setNom(documentContingut.getArxiuNom());
				fitxer.setContentType(documentContingut.getTipusMime());
				fitxer.setContingut(documentContingut.getContingut());
				fitxer.setTamany(documentContingut.getContingut() != null ? Long.valueOf(documentContingut.getContingut().length) : null);
			}
		}
		return fitxer;
	}

	@Transactional(readOnly = true)
	@Override
	public RegistreAnnexDto findAnnexById(Long annexId) {
		RegistreAnnexEntity annexEntity = registreAnnexRepository.findById(annexId);

		RegistreAnnexDto annexDto = conversioTipusHelper.convertir(
				annexEntity,
				RegistreAnnexDto.class);
		
		if (annexEntity.getDocument() != null) {
			annexDto.setDocumentId(annexEntity.getDocument().getId());
		}
		return annexDto;
	}

	@Transactional(readOnly = true)
	@Override
	public List<ArxiuFirmaDto> annexFirmaInfo(String fitxerArxiuUuid) {
		log.debug("Obtenint annex firma info (" +
				"fitxerArxiuUuid=" +
				fitxerArxiuUuid +
				")");

		Document document = pluginHelper.arxiuDocumentConsultar(null,
				fitxerArxiuUuid,
				null,
				true);

		if (document != null) {
			for (Firma arxiuFirma : document.getFirmes()) {

				if (!FirmaTipus.CSV.equals(arxiuFirma.getTipus())) {

					byte[] documentContingut = document.getContingut() != null ? document.getContingut().getContingut() : null;
					byte[] firmaContingut = arxiuFirma.getContingut();
					String contentType = document.getContingut().getTipusMime();

					return pluginHelper.validaSignaturaObtenirFirmes(
							documentContingut,
							firmaContingut,
							contentType, 
							false);
				}
			}
		}

		return null;
	}
	
	@Transactional
	@Override
	public RegistreDto findRegistreById(Long registreId) {
		return conversioTipusHelper.convertir(
				registreRepository.findOne(registreId),
				RegistreDto.class);
	}
	

	@Transactional
	@Override
	public void rebutjar(Long expedientPeticioId,
			String observacions) {
		log.debug("Reutjant el expedient peticio " +
				"expedientPeticioId=" +
				expedientPeticioId +
				")");

		ExpedientPeticioEntity expedientPeticioEntity = expedientPeticioRepository.findOne(expedientPeticioId);
		expedientPeticioEntity.updateEstat(ExpedientPeticioEstatEnumDto.REBUTJAT);
		expedientPeticioEntity.setDataActualitzacio(new Date());
		expedientPeticioEntity.setUsuariActualitzacio(usuariRepository.findByCodi(SecurityContextHolder.getContext().getAuthentication().getName()));
		expedientPeticioEntity.setObservacions(observacions);

		AnotacioRegistreId anotacioRegistreId = new AnotacioRegistreId();
		anotacioRegistreId.setClauAcces(expedientPeticioEntity.getClauAcces());
		anotacioRegistreId.setIndetificador(expedientPeticioEntity.getIdentificador());

		try {
			DistribucioHelper.getBackofficeIntegracioRestClient().canviEstat(anotacioRegistreId,
					Estat.REBUTJADA,
					observacions);
			expedientPeticioEntity.setEstatCanviatDistribucio(true);
		} catch (Exception e) {
			expedientPeticioEntity.setEstatCanviatDistribucio(false);
		}
		EntitatEntity entitatAnotacio = expedientPeticioEntity.getRegistre().getEntitat();
		if (entitatAnotacio != null)
			cacheHelper.evictCountAnotacionsPendents(entitatAnotacio);
	}
	
	
	@Transactional(readOnly = true)
	@Override
	public void evictCountAnotacionsPendents(Long entitatId) {
		EntitatEntity entitat = entitatRepository.findOne(entitatId);
		cacheHelper.evictCountAnotacionsPendents(entitat);
	}
	
	
	@Transactional
	@Override
	public void retornarPendent(Long expedientPeticioId) {

		ExpedientPeticioEntity expedientPeticioEntity = expedientPeticioRepository.findOne(expedientPeticioId);
		
//TODO change back to BACK_REBUDA in distribucio		
//		try {
//			AnotacioRegistreId anotacioRegistreId = new AnotacioRegistreId();
//			anotacioRegistreId.setClauAcces(expedientPeticioEntity.getClauAcces());
//			anotacioRegistreId.setIndetificador(expedientPeticioEntity.getIdentificador());
//			
//			// change state of registre in DISTRIBUCIO to BACK_REBUDA
//			DistribucioHelper.getBackofficeIntegracioRestClient().canviEstat(anotacioRegistreId, Estat.REBUDA, "");
//			expedientPeticioEntity.setEstatCanviatDistribucio(true);
//
//		} catch (Exception e) {
//			throw e;
//		}
		
		ExpedientEntity expedient = expedientPeticioEntity.getExpedient();
		
		if (expedient.getEstat() == ExpedientEstatEnumDto.TANCAT) {
			throw new RuntimeException("No es pot retornar l'anotació a estat pendent. Expedient relacionat ja se ha tancat.");
		}
		
		// indicates if expedient contains documents not related with this anotacion (documents from other annotacions or created manually)
		boolean containsDocumentsNotRelated = false;
		List<DocumentEntity> documents = documentRepository.findByExpedient(expedient);
		for (DocumentEntity documentEntity : documents) {
			 if (CollectionUtils.isEmpty(documentEntity.getAnnexos())){
				 containsDocumentsNotRelated = true;
			 }
		}
		
		if (containsDocumentsNotRelated) {
			throw new RuntimeException("No es pot retornar l'anotació a estat pendent. Expedient relacionat conté documents no relacionts amb l'anotació.");
		}
		

		for (RegistreAnnexEntity registreAnnexEntity : expedientPeticioEntity.getRegistre().getAnnexos()) {
			

			DocumentEntity document = registreAnnexEntity.getDocument();
			
//			registreAnnexEntity.updateUuidDispatched(document.getArxiuUuid());
			
			registreAnnexEntity.updateDocument(null);
			
//			documentRepository.delete(document);
			

		}
//		if (!containsDocumentsNotRelated) {
			expedientRepository.delete(expedient);
//		}

		
		expedientPeticioEntity.setExpedient(null);
		
		expedientPeticioEntity.updateEstat(ExpedientPeticioEstatEnumDto.PENDENT);

	}
	

	@Transactional(readOnly = true)
	@Override
	public FitxerDto getAnnexFirmaContingut(Long annexId) {
		organGestorHelper.actualitzarOrganCodi(organGestorHelper.getOrganCodiFromAnnexId(annexId));
		RegistreAnnexEntity annex = registreAnnexRepository.findOne(annexId);
		FitxerDto arxiu = new FitxerDto();

		Document document = null;
		document = pluginHelper.arxiuDocumentConsultar(null,
				annex.getUuid(),
				null,
				true);

		if (document != null) {
			List<Firma> firmes = document.getFirmes();
			if (firmes != null &&
					firmes.size() > 0) {

				Iterator<Firma> it = firmes.iterator();
				while (it.hasNext()) {
					Firma firma = it.next();
					if (firma.getTipus() == FirmaTipus.CSV) {
						it.remove();
					}
				}

				Firma firma = firmes.get(0);

				if (firma != null) {
					arxiu.setNom(annex.getFirmaNom());
					arxiu.setContentType(firma.getTipusMime());
					arxiu.setContingut(firma.getContingut());
					arxiu.setTamany(firma.getContingut() != null ? Long.valueOf(firma.getContingut().length) : null);
				}
			}
		}
		return arxiu;
	}

	
	@Transactional
	@Override
	public void canviarProcediment(Long expedientPeticioId, Long procedimentId) {

		ExpedientPeticioEntity expedientPeticioEntity = expedientPeticioRepository.findOne(expedientPeticioId);
		
		if (procedimentId != null) {
			MetaExpedientEntity metaExpedient = metaExpedientRepository.findOne(procedimentId);
			expedientPeticioEntity.updateMetaExpedient(metaExpedient);
		} else {
			expedientPeticioEntity.updateMetaExpedient(null);
		}
	
	}
	
	
	
	@Transactional
	@Override
	public ExpedientPeticioDto findOne(Long expedientPeticioId) {
		log.debug("Consultant el expedient peticio " +
				"expedientPeticioId=" +
				expedientPeticioId +
				")");

		ExpedientPeticioEntity expedientPeticioEntity = expedientPeticioRepository.findOne(expedientPeticioId);
		
		for (RegistreAnnexEntity registreAnnex : expedientPeticioEntity.getRegistre().getAnnexos()) {
			if (registreAnnex.getTamany() == 0) {
				organGestorHelper.actualitzarOrganCodi(organGestorHelper.getOrganCodiFromAnnexId(registreAnnex.getId()));
				Document documentDetalls = pluginHelper.arxiuDocumentConsultar(
						null, 
						registreAnnex.getUuid(), 
						null, 
						true, 
						false);
				
				registreAnnex.updateTamany(documentDetalls.getContingut().getTamany());
				
			}
		}

		ExpedientPeticioDto expedientPeticioDto = conversioTipusHelper.convertir(expedientPeticioEntity,
				ExpedientPeticioDto.class);
		
		String arxiuUuid = expedientPeticioDto.getRegistre().getJustificantArxiuUuid();
		if (arxiuUuid != null && isIncorporacioJustificantActiva()) {
			RegistreJustificantDto justificantInfo = new RegistreJustificantDto();
			Document documentDetalls = pluginHelper.arxiuDocumentConsultar(
					null, 
					arxiuUuid, 
					null, 
					true, 
					false);
			DocumentDto document = expedientHelper.toDocumentDto(
					documentDetalls, 
					expedientPeticioDto.getIdentificador());
			justificantInfo.setTitol(document.getNom());
			justificantInfo.setNtiFechaCaptura(document.getDataCaptura());
			justificantInfo.setNtiOrigen(documentDetalls.getMetadades().getOrigen().name());
			justificantInfo.setNtiTipoDocumental(documentDetalls.getMetadades().getTipusDocumental().name());
			justificantInfo.setUuid(document.getArxiuUuid());
			if (documentDetalls.getFirmes() != null && !documentDetalls.getFirmes().isEmpty()) {
				justificantInfo.setFirmaPerfil(documentDetalls.getFirmes().get(0).getPerfil().name());
				justificantInfo.setFirmaTipus(documentDetalls.getFirmes().get(0).getTipus().name());
			}
			if (document.getNtiTipoFirma() != null)
				justificantInfo.setFirmaTipus(document.getNtiTipoFirma().name());
			expedientPeticioDto.getRegistre().setJustificant(justificantInfo);
		}

		return expedientPeticioDto;

	}
	
	
	@Transactional(readOnly = true)
	@Override
	public long countAnotacionsPendents(Long entitatId, String rolActual, Long organActualId) {
		EntitatEntity entitatActual = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				false,
				false, 
				true, 
				false);
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		return cacheHelper.countAnotacionsPendents(entitatActual, rolActual, auth.getName(), organActualId);
	}

	private boolean isIncorporacioJustificantActiva() {
		return configHelper.getAsBoolean("es.caib.ripea.incorporar.justificant");
	}
	
	@Transactional
	@Override
	public boolean comprovarExistenciaInteressatsPeticio(Long entitatId, Long expedientId, Long expedientPeticioId) {
		boolean alreadyExists = false;
		ExpedientPeticioEntity expedientPeticioEntity = expedientPeticioRepository.findOne(expedientPeticioId);
		ExpedientEntity expedientEntity = expedientRepository.findOne(expedientId);
		Set<InteressatEntity> existingInteressats = expedientEntity.getInteressatsORepresentants();
		//### Si alguns dels interessats existeix sol·licitar confirmació usuari
		for (RegistreInteressatEntity registreInteressatEntity : expedientPeticioEntity.getRegistre().getInteressats()) {
			for (InteressatEntity interessatExpedient : existingInteressats) {
				if (interessatExpedient.getDocumentNum().equals(registreInteressatEntity.getDocumentNumero())) {
					alreadyExists = true;
					break;
				}
			}
			if (alreadyExists)
				break;
		}
		return alreadyExists;
	}
	
	
	@Transactional(readOnly = true)
	@Override
	public ResultDto<RegistreAnnexDto> findAnnexosPendentsProcesarMassiu(
			Long entitatId,
			MassiuAnnexProcesarFiltreDto filtre,
			PaginacioParamsDto paginacioParams,
			ResultEnumDto resultEnum, 
			String rolActual, 
			Long organActualId) {
		
		ResultDto<RegistreAnnexDto> result = new ResultDto<RegistreAnnexDto>();
		
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				false, 
				false, 
				true, 
				false);
		
		Date dataInici = DateHelper.toDateInicialDia(filtre.getDataInici());
		Date dataFi = DateHelper.toDateFinalDia(filtre.getDataFi());
		
		Map<String, String[]> ordenacioMap = new HashMap<String, String[]>();
		ordenacioMap.put("expedientCreatedDate", new String[] {"e.expedient.createdDate"});
		
		MetaExpedientEntity metaExpedient = null;
		if (filtre.getMetaExpedientId() != null) {
			metaExpedient = metaExpedientRepository.findOne(filtre.getMetaExpedientId());
		}
		
		ExpedientEntity expedient = null;
		if (filtre.getExpedientId() != null) {
			expedient = expedientRepository.findOne(filtre.getExpedientId());
		}
		
		PermisosPerAnotacions permisosPerAnotacions = expedientPeticioHelper.findPermisosPerAnotacions(
				entitatId,
				rolActual, 
				organActualId);
		

		if (resultEnum == ResultEnumDto.PAGE) {
			// ================================  RETURNS PAGE (DATATABLE) ==========================================
			Page<RegistreAnnexEntity> pagina = registreAnnexRepository.findPendentsProcesar(
					entitat,
					rolActual,
					permisosPerAnotacions.getProcedimentsPermesos(),
					permisosPerAnotacions.getAdminOrganCodisOrganAmbDescendents(),
					permisosPerAnotacions.isAdminOrganHasPermisAdminComu(),
					filtre.getNom() == null,
					filtre.getNom() != null ? filtre.getNom().trim() : "",
					filtre.getNumero() == null,
					filtre.getNumero() != null ? filtre.getNumero().trim() : "",
					dataInici == null,
					dataInici,
					dataFi == null,
					dataFi,
					metaExpedient == null,
					metaExpedient,
					expedient == null,
					expedient,
					paginacioHelper.toSpringDataPageable(paginacioParams, ordenacioMap));
			PaginaDto<RegistreAnnexDto> paginaDto = paginacioHelper.toPaginaDto(
					pagina,
					RegistreAnnexDto.class);
			result.setPagina(paginaDto);
			
		} else {
			// ==================================  RETURNS IDS (SELECCIONAR TOTS) ============================================
			List<Long> documentsIds = registreAnnexRepository.findIdsPendentsProcesar(
					entitat,
					rolActual,
					permisosPerAnotacions.getProcedimentsPermesos(),
					permisosPerAnotacions.getAdminOrganCodisOrganAmbDescendents(),
					permisosPerAnotacions.isAdminOrganHasPermisAdminComu(),
					filtre.getNom() == null,
					filtre.getNom() != null ? filtre.getNom().trim() : "",
					filtre.getNumero() == null,
					filtre.getNumero() != null ? filtre.getNumero().trim() : "",
					dataInici == null,
					dataInici,
					dataFi == null,
					dataFi,
					metaExpedient == null,
					metaExpedient,
					expedient == null,
					expedient);
			
			result.setIds(documentsIds);
		}
		return result;
	}

	@Transactional(readOnly = true)
	@Override
	public ResultDto<ExpedientPeticioListDto> findPendentsCanviEstatDistribucio(
			Long entitatId,
			ExpedientPeticioFiltreDto filtre,
			PaginacioParamsDto paginacioParams, 
			ResultEnumDto resultEnum) {
		
		ResultDto<ExpedientPeticioListDto> result = new ResultDto<ExpedientPeticioListDto>();

		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				false,
				false,
				true,
				false);


		
		if (resultEnum == ResultEnumDto.PAGE) {

			Page<ExpedientPeticioEntity> pagina = expedientPeticioRepository.findPendentsCanviEstatDistribucio(
					entitat,
					Strings.isNullOrEmpty(filtre.getNumero()),
					filtre.getNumero() != null ? filtre.getNumero() : "",
					filtre.getDataInicial() == null,
					filtre.getDataInicial(),
					filtre.getDataFinal() == null,
					DateHelper.toDateFinalDia(filtre.getDataFinal()),
					filtre.getEstatPendentEnviarDistribucio() == null,
					filtre.getEstatPendentEnviarDistribucio() != null ? filtre.getEstatPendentEnviarDistribucio().toString() : null,
					filtre.isNomesPendentEnviarDistribucio(),
					paginacioHelper.toSpringDataPageable(paginacioParams));

			result.setPagina(
					paginacioHelper.toPaginaDto(
							pagina,
							ExpedientPeticioListDto.class));
			
		} else if (resultEnum == ResultEnumDto.IDS) {
			
			List<Long> ids = expedientPeticioRepository.findIdsPendentsCanviEstatDistribucio(
					entitat,
					Strings.isNullOrEmpty(filtre.getNumero()),
					filtre.getNumero() != null ? filtre.getNumero() : "",
					filtre.getDataInicial() == null,
					filtre.getDataInicial(),
					filtre.getDataFinal() == null,
					DateHelper.toDateFinalDia(filtre.getDataFinal()),
					filtre.getEstatPendentEnviarDistribucio() == null,
					filtre.getEstatPendentEnviarDistribucio() != null ? filtre.getEstatPendentEnviarDistribucio().toString() : null,
					filtre.isNomesPendentEnviarDistribucio());

			result.setIds(ids);
		}

		return result;
	}




	@Transactional
	@Override
	public Exception canviarEstatAnotacioDistribucio(Long entitatId, Long id) {

		entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				false,
				false,
				true,
				false);
		
		return expedientPeticioHelper.reintentarCanviEstatDistribucio(id);
	}

	@Transactional
	@Override
	public List<MetaExpedientDto> findMetaExpedientsPermesosPerAnotacions(
			Long entitatId,
			Long organActualId,
			String rolActual) {
		
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				false,
				false,
				true,
				false);
		
		
		List<MetaExpedientEntity> metaExpedientsPermesos = null;
		if (rolActual.equals("IPA_ADMIN")) {
			metaExpedientsPermesos = metaExpedientRepository.findByEntitat(entitat);
		} else if (rolActual.equals("IPA_ORGAN_ADMIN")) {
			metaExpedientsPermesos = metaExpedientHelper.findProcedimentsDeOrganIDeDescendentsDeOrgan(organActualId);
			if (organGestorHelper.hasPermisAdminComu(organActualId)) {
				List<MetaExpedientEntity> procedimentsComuns = metaExpedientRepository.findProcedimentsComunsActive(entitat);
				metaExpedientsPermesos.addAll(procedimentsComuns);
			}
		} else if (rolActual.equals("tothom")) {
			metaExpedientsPermesos = metaExpedientHelper.getCreateWritePermesos(entitat.getId()); 
		}

		return conversioTipusHelper.convertirList(
				metaExpedientsPermesos,
				MetaExpedientDto.class);
	}

}