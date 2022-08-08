/**
 * 
 */
package es.caib.ripea.core.service;

import com.google.common.base.Strings;
import es.caib.distribucio.rest.client.domini.AnotacioRegistreId;
import es.caib.distribucio.rest.client.domini.Estat;
import es.caib.plugins.arxiu.api.Document;
import es.caib.plugins.arxiu.api.DocumentContingut;
import es.caib.plugins.arxiu.api.Firma;
import es.caib.plugins.arxiu.api.FirmaTipus;
import es.caib.ripea.core.api.dto.*;
import es.caib.ripea.core.api.service.ExpedientPeticioService;
import es.caib.ripea.core.config.PropertiesConstants;
import es.caib.ripea.core.entity.EntitatEntity;
import es.caib.ripea.core.entity.ExpedientEntity;
import es.caib.ripea.core.entity.ExpedientPeticioEntity;
import es.caib.ripea.core.entity.InteressatEntity;
import es.caib.ripea.core.entity.MetaExpedientEntity;
import es.caib.ripea.core.entity.OrganGestorEntity;
import es.caib.ripea.core.entity.RegistreAnnexEntity;
import es.caib.ripea.core.entity.RegistreInteressatEntity;
import es.caib.ripea.core.helper.*;
import es.caib.ripea.core.repository.EntitatRepository;
import es.caib.ripea.core.repository.ExpedientPeticioRepository;
import es.caib.ripea.core.repository.ExpedientRepository;
import es.caib.ripea.core.repository.MetaExpedientRepository;
import es.caib.ripea.core.repository.OrganGestorRepository;
import es.caib.ripea.core.repository.RegistreAnnexRepository;
import es.caib.ripea.core.repository.RegistreRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
	private MetaExpedientHelper metaExpedientHelper;
	@Autowired
	private ConfigHelper configHelper;
	@Resource
	private OrganGestorRepository organGestorRepository;
	@Autowired
	private DistribucioHelper distribucioHelper;
	@Autowired
	private ContingutHelper contingutHelper;

	
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
		Page<ExpedientPeticioEntity> paginaExpedientPeticios;

		// enum with states accesibles from filter in the view (without create state)
		ExpedientPeticioEstatViewEnumDto estatView = filtre.getEstat();
		
		MetaExpedientEntity metaExpedient = null;
		if (filtre.getMetaExpedientId() != null) {
			metaExpedient = entityComprovarHelper.comprovarMetaExpedient(entitat, filtre.getMetaExpedientId());
		}
		List<Long> createWritePermIds = metaExpedientHelper.getIdsCreateWritePermesos(entitatId); 
		List<MetaExpedientEntity> procedimentsPermesAdminOrgan = null;
		if (organActualId != null) {
			
			OrganGestorEntity organGestor = organGestorRepository.findOne(organActualId);
			List<OrganGestorEntity> organsPermesAdminOrgan = organGestorRepository.findFills(entitat, Arrays.asList(organActualId));
			procedimentsPermesAdminOrgan = new ArrayList<>();
			procedimentsPermesAdminOrgan.addAll(organGestor.getMetaExpedients());
			
			for (OrganGestorEntity organGestorEntity : organsPermesAdminOrgan) {
				procedimentsPermesAdminOrgan.addAll(organGestorEntity.getMetaExpedients());
			}
			
			if (procedimentsPermesAdminOrgan.isEmpty()) {
				procedimentsPermesAdminOrgan = null;
			}
		}
		
		paginaExpedientPeticios = expedientPeticioRepository.findByEntitatAndFiltre(
				entitat,
				rolActual,
				procedimentsPermesAdminOrgan,
				createWritePermIds ,
				metaExpedient == null,
				metaExpedient,
				filtre.getProcediment() == null ||
						filtre.getProcediment().isEmpty(),
				filtre.getProcediment() != null ? filtre.getProcediment().trim() : "",
				filtre.getNumero() == null ||
						filtre.getNumero().isEmpty(),
				filtre.getNumero() != null ? filtre.getNumero().trim() : "",
				filtre.getExtracte() == null ||
						filtre.getExtracte().isEmpty(),
				filtre.getExtracte() != null ? filtre.getExtracte().trim() : "",
				filtre.getDestinacio() == null ||
						filtre.getDestinacio().isEmpty(),
				filtre.getDestinacio() != null ? filtre.getDestinacio().trim() : "",
				filtre.getDataInicial() == null,
				filtre.getDataInicial(),
				filtre.getDataFinal() == null,
				DateHelper.toDateFinalDia(filtre.getDataFinal()),
				estatView == null,
				estatView != null ? estatView.toString() : null,
				filtre.getAccioEnum() == null,
				filtre.getAccioEnum(),
				paginacioHelper.toSpringDataPageable(
						paginacioParams,
						ordenacioMap));

		PaginaDto<ExpedientPeticioListDto> result = paginacioHelper.toPaginaDto(paginaExpedientPeticios,
				ExpedientPeticioListDto.class);

		return result;

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
				List<MetaExpedientEntity> metaExpedients = metaExpedientRepository.findByEntitatAndClassificacioSia(entitat,
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
			return expedientHelper.toExpedientDto(expedientEntity,
					false);
		}
	}

	@Transactional(readOnly = true)
	@Override
	public List<ExpedientPeticioListDto> findByExpedientAmbFiltre(
			Long entitatId,
			Long expedientId,
			PaginacioParamsDto paginacioParams) {
		ExpedientEntity expedient = entityComprovarHelper.comprovarExpedient(
				entitatId,
				expedientId,
				false,
				true,
				false,
				false,
				false, 
				false, null);
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
				fitxer.setNom(versioImprimible ? documentContingut.getArxiuNom() : registreAnnex.getNom());
				fitxer.setContentType(documentContingut.getTipusMime());
				fitxer.setContingut(documentContingut.getContingut());
				fitxer.setTamany(documentContingut.getContingut().length);
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
				fitxer.setTamany(documentContingut.getContingut().length);
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

		for (Firma arxiuFirma : document.getFirmes()) {

			if (!FirmaTipus.CSV.equals(arxiuFirma.getTipus())) {

				byte[] documentContingut = document.getContingut() != null ? document.getContingut().getContingut() : null;
				byte[] firmaContingut = arxiuFirma.getContingut();
				String contentType = document.getContingut().getTipusMime();

				return pluginHelper.validaSignaturaObtenirFirmes(documentContingut,
						firmaContingut,
						contentType);
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

		AnotacioRegistreId anotacioRegistreId = new AnotacioRegistreId();
		anotacioRegistreId.setClauAcces(expedientPeticioEntity.getClauAcces());
		anotacioRegistreId.setIndetificador(expedientPeticioEntity.getIdentificador());

		try {
			DistribucioHelper.getBackofficeIntegracioRestClient().canviEstat(anotacioRegistreId,
					Estat.REBUTJADA,
					observacions);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		EntitatEntity entitatAnotacio = expedientPeticioEntity.getRegistre().getEntitat();
		if (entitatAnotacio != null)
			cacheHelper.evictCountAnotacionsPendents(entitatAnotacio);
	}

	@Transactional(readOnly = true)
	@Override
	public FitxerDto getAnnexFirmaContingut(Long annexId) {
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
					arxiu.setTamany(firma.getContingut().length);
				}
			}
		}
		return arxiu;
	}

	@Transactional(readOnly = true)
	@Override
	public ExpedientPeticioDto findOne(Long expedientPeticioId) {
		log.debug("Consultant el expedient peticio " +
				"expedientPeticioId=" +
				expedientPeticioId +
				")");

		ExpedientPeticioEntity expedientPeticioEntity = expedientPeticioRepository.findOne(expedientPeticioId);
		
		expedientPeticioEntity.getRegistre().getAnnexos();

		ExpedientPeticioDto expedientPeticioDto = conversioTipusHelper.convertir(expedientPeticioEntity,
				ExpedientPeticioDto.class);
		
		String arxiuUuid = expedientPeticioDto.getRegistre().getJustificantArxiuUuid();
		if (arxiuUuid != null && isIncorporacioJustificantActiva()) {
			RegistreJustificantDto justificantInfo = new RegistreJustificantDto();
			Document documentDetalls = pluginHelper.arxiuDocumentConsultar(
					null, 
					arxiuUuid, 
					null, 
					false, 
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
		Set<InteressatEntity> existingInteressats = expedientEntity.getInteressats();
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
			ResultEnumDto resultEnum) {
		
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
		ordenacioMap.put("expedientCreatedDate", new String[] {"ep.expedient.createdDate"});
		
		MetaExpedientEntity metaExpedient = null;
		if (filtre.getMetaExpedientId() != null) {
			metaExpedient = metaExpedientRepository.findOne(filtre.getMetaExpedientId());
		}
		
		if (resultEnum == ResultEnumDto.PAGE) {
			Page<RegistreAnnexEntity> pagina = registreAnnexRepository.findPendentsProcesar(
					entitat,
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
					paginacioHelper.toSpringDataPageable(paginacioParams, ordenacioMap));
			PaginaDto<RegistreAnnexDto> paginaDto = paginacioHelper.toPaginaDto(
					pagina,
					RegistreAnnexDto.class);
			result.setPagina(paginaDto);
			
		} else {
			
			List<Long> documentsIds = registreAnnexRepository.findIdsPendentsProcesar(
					entitat,
					filtre.getNom() == null,
					filtre.getNom() != null ? filtre.getNom().trim() : "",
					filtre.getNumero() == null,
					filtre.getNumero() != null ? filtre.getNumero().trim() : "",
					dataInici == null,
					dataInici,
					dataFi == null,
					dataFi,
					metaExpedient == null,
					metaExpedient);
			
			result.setIds(documentsIds);
		}
		return result;
	}

	@Transactional(readOnly = true)
	@Override
	public PaginaDto<ExpedientPeticioPendentDist>  findPendentsCanviEstatAnotacioDistribucio(Long entitatId, ContingutMassiuFiltreDto filtre, PaginacioParamsDto paginacioParams) {

		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId, false, false, false, true, false);
		boolean identNull = Strings.isNullOrEmpty(filtre.getIdentificador());
		boolean nomNull = Strings.isNullOrEmpty(filtre.getNom());
		boolean dataIniciNull = filtre.getDataInici() == null;
		boolean dataFiNull = filtre.getDataFi() == null;
		Page<ExpedientPeticioPendentDist> pagina = expedientPeticioRepository.findPendentsCanviEstat(entitat, identNull, filtre.getIdentificador(),
				nomNull, filtre.getNom(), dataIniciNull, filtre.getDataInici(), dataFiNull, filtre.getDataFi(), paginacioHelper.toSpringDataPageable(paginacioParams));
		return paginacioHelper.toPaginaDto(pagina, ExpedientPeticioPendentDist.class);
	}

	@Transactional(readOnly = true)
	@Override
	public List<Long> findIdsPendentsCanviEstatAnotacioDistribucio(Long entitatId, ContingutMassiuFiltreDto filtre) {

		boolean identNull = Strings.isNullOrEmpty(filtre.getIdentificador());
		boolean nomNull = Strings.isNullOrEmpty(filtre.getNom());
		boolean dataIniciNull = filtre.getDataInici() == null;
		boolean dataFiNull = filtre.getDataFi() == null;
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId, false, false, false, true, false);
		return expedientPeticioRepository.findIdsPendentsCanviEstat(entitat, identNull, filtre.getIdentificador(),
				nomNull, filtre.getNom(), dataIniciNull, filtre.getDataInici(), dataFiNull, filtre.getDataFi());
	}

	@Transactional
	@Override
	public boolean canviarEstatAnotacionsDistribucio(List<Long> ids) {

		boolean ok = true;
		for (Long id : ids) {
			Throwable exception = canviarEstatAnotacioDistribucio(id);
			if (exception != null) {
				ok = false;
			}
		}
		return ok;
	}

	@Transactional
	@Override
	public Throwable canviarEstatAnotacioDistribucio(Long id) {

		ExpedientPeticioEntity pendent = expedientPeticioRepository.findOne(id);
		Throwable exception = null;
		AnotacioRegistreId anotacio = new AnotacioRegistreId();
		anotacio.setIndetificador(pendent.getIdentificador());
		anotacio.setClauAcces(pendent.getClauAcces());
		try {
			DistribucioHelper.getBackofficeIntegracioRestClient().canviEstat(anotacio, Estat.REBUDA, "");
			pendent.setPendentEnviarDistribucio(false);
			expedientPeticioRepository.save(pendent);
		} catch (Throwable ex) {
			exception = ex;
			log.error("No s'ha guardat la anotació pendent a Distribució amb id " + pendent.getId(), ex);
			Integer reintents = pendent.getReintentsEnviarDistribucio() - 1;
			reintents = reintents > 0 ? reintents : configHelper.getAsInt(PropertiesConstants.REINTENTS_ANOTACIONS_PETICIONS_PENDENTS);
			pendent.setReintentsEnviarDistribucio(reintents);
			expedientPeticioRepository.save(pendent);
		}
		return exception;
	}


}
