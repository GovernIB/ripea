/**
 * 
 */
package es.caib.ripea.core.service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.caib.distribucio.ws.backofficeintegracio.AnotacioRegistreId;
import es.caib.distribucio.ws.backofficeintegracio.Estat;
import es.caib.plugins.arxiu.api.Document;
import es.caib.plugins.arxiu.api.DocumentContingut;
import es.caib.plugins.arxiu.api.Firma;
import es.caib.plugins.arxiu.api.FirmaTipus;
import es.caib.ripea.core.api.dto.ArxiuFirmaDto;
import es.caib.ripea.core.api.dto.DocumentDto;
import es.caib.ripea.core.api.dto.ExpedientDto;
import es.caib.ripea.core.api.dto.ExpedientPeticioDto;
import es.caib.ripea.core.api.dto.ExpedientPeticioEstatEnumDto;
import es.caib.ripea.core.api.dto.ExpedientPeticioEstatViewEnumDto;
import es.caib.ripea.core.api.dto.ExpedientPeticioFiltreDto;
import es.caib.ripea.core.api.dto.FitxerDto;
import es.caib.ripea.core.api.dto.MetaExpedientDto;
import es.caib.ripea.core.api.dto.MetaExpedientSelectDto;
import es.caib.ripea.core.api.dto.PaginaDto;
import es.caib.ripea.core.api.dto.PaginacioParamsDto;
import es.caib.ripea.core.api.dto.RegistreAnnexDto;
import es.caib.ripea.core.api.dto.RegistreDto;
import es.caib.ripea.core.api.dto.RegistreJustificantDto;
import es.caib.ripea.core.api.service.ExpedientPeticioService;
import es.caib.ripea.core.entity.EntitatEntity;
import es.caib.ripea.core.entity.ExpedientEntity;
import es.caib.ripea.core.entity.ExpedientPeticioEntity;
import es.caib.ripea.core.entity.InteressatEntity;
import es.caib.ripea.core.entity.MetaExpedientEntity;
import es.caib.ripea.core.entity.RegistreAnnexEntity;
import es.caib.ripea.core.entity.RegistreInteressatEntity;
import es.caib.ripea.core.helper.CacheHelper;
import es.caib.ripea.core.helper.ConversioTipusHelper;
import es.caib.ripea.core.helper.DateHelper;
import es.caib.ripea.core.helper.DistribucioHelper;
import es.caib.ripea.core.helper.EntityComprovarHelper;
import es.caib.ripea.core.helper.ExpedientHelper;
import es.caib.ripea.core.helper.MetaExpedientHelper;
import es.caib.ripea.core.helper.PaginacioHelper;
import es.caib.ripea.core.helper.PluginHelper;
import es.caib.ripea.core.helper.PropertiesHelper;
import es.caib.ripea.core.repository.EntitatRepository;
import es.caib.ripea.core.repository.ExpedientPeticioRepository;
import es.caib.ripea.core.repository.ExpedientRepository;
import es.caib.ripea.core.repository.MetaExpedientRepository;
import es.caib.ripea.core.repository.RegistreAnnexRepository;
import es.caib.ripea.core.repository.RegistreRepository;

/**
 * Implementació dels mètodes per a gestionar expedient peticions.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
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
	
	@Transactional(readOnly = true)
	@Override
	public PaginaDto<ExpedientPeticioDto> findAmbFiltre(
			Long entitatId,
			ExpedientPeticioFiltreDto filtre,
			PaginacioParamsDto paginacioParams, boolean isAdmin) {
		logger.debug("Consultant els expedient peticions segons el filtre (" +
				"entitatId=" +
				entitatId +
				", filtre=" +
				filtre +
				", paginacioParams=" +
				paginacioParams +
				")");

		final EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId,
				true,
				false,
				false, false, false);

		Map<String, String[]> ordenacioMap = new HashMap<String, String[]>();
		ordenacioMap.put("numero",
				new String[] { "codi", "any", "sequencia" });
		Page<ExpedientPeticioEntity> paginaExpedientPeticios;

		// enum with states accesibles from filter in the view (without create state)
		ExpedientPeticioEstatViewEnumDto estatView = filtre.getEstat();

		
		List<Long> createWritePermIds = metaExpedientHelper.getIdsCreateWritePermesos(entitatId); 
		
		paginaExpedientPeticios = expedientPeticioRepository.findByEntitatAndFiltre(
				entitat,
				isAdmin,
				createWritePermIds,
				filtre.getProcediment() == null ||
						filtre.getProcediment().isEmpty(),
				filtre.getProcediment(),
				filtre.getNumero() == null ||
						filtre.getNumero().isEmpty(),
				filtre.getNumero(),
				filtre.getExtracte() == null ||
						filtre.getExtracte().isEmpty(),
				filtre.getExtracte(),
				filtre.getDestinacio() == null ||
						filtre.getDestinacio().isEmpty(),
				filtre.getDestinacio(),
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

		PaginaDto<ExpedientPeticioDto> result = paginacioHelper.toPaginaDto(paginaExpedientPeticios,
				ExpedientPeticioDto.class);

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
		logger.debug("Consultant el expedient("
				+ "entitatId=" + entitatId + ", "
				+ "expedientNumero=" + expedientNumero + ", "
				+ "metaExpedientId=" + metaExpedientId + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				true,
				false,
				false, false, false);
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
	public List<ExpedientPeticioDto> findByExpedientAmbFiltre(
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
				false);
		List<ExpedientPeticioEntity> peticions = expedientPeticioRepository.findByExpedient(
				expedient, 
				paginacioHelper.toSpringDataPageable(paginacioParams));
		return conversioTipusHelper.convertirList(
				peticions,
				ExpedientPeticioDto.class);
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
	public FitxerDto getAnnexContent(Long annexId) {
		RegistreAnnexEntity annex = registreAnnexRepository.findOne(annexId);
		FitxerDto fitxer = new FitxerDto();

		Document document = null;
		document = pluginHelper.arxiuDocumentConsultar(null,
				annex.getUuid(),
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
		RegistreAnnexEntity annexEntity = registreAnnexRepository.findOne(annexId);

		return conversioTipusHelper.convertir(annexEntity,
				RegistreAnnexDto.class);
	}

	@Transactional(readOnly = true)
	@Override
	public List<ArxiuFirmaDto> annexFirmaInfo(String fitxerArxiuUuid) {
		logger.debug("Obtenint annex firma info (" +
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
		logger.debug("Reutjant el expedient peticio " +
				"expedientPeticioId=" +
				expedientPeticioId +
				")");

		ExpedientPeticioEntity expedientPeticioEntity = expedientPeticioRepository.findOne(expedientPeticioId);
		expedientPeticioEntity.updateEstat(ExpedientPeticioEstatEnumDto.REBUTJAT);

		AnotacioRegistreId anotacioRegistreId = new AnotacioRegistreId();
		anotacioRegistreId.setClauAcces(expedientPeticioEntity.getClauAcces());
		anotacioRegistreId.setIndetificador(expedientPeticioEntity.getIdentificador());

		try {
			DistribucioHelper.getBackofficeIntegracioServicePort().canviEstat(anotacioRegistreId,
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
		logger.debug("Consultant el expedient peticio " +
				"expedientPeticioId=" +
				expedientPeticioId +
				")");

		ExpedientPeticioEntity expedientPeticioEntity = expedientPeticioRepository.findOne(expedientPeticioId);

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
	public long countAnotacionsPendents(Long entitatId, boolean isAdmin) {
		EntitatEntity entitatActual = entityComprovarHelper.comprovarEntitat(
				entitatId,
				true,
				false,
				false, 
				false, 
				false);
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		return cacheHelper.countAnotacionsPendents(entitatActual, isAdmin, auth.getName());
	}

	private boolean isIncorporacioJustificantActiva() {
		boolean isPropagarRelacio = Boolean.parseBoolean(PropertiesHelper.getProperties().getProperty("es.caib.ripea.incorporar.justificant"));
		return isPropagarRelacio;
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
	
	
	

	


	private static final Logger logger = LoggerFactory.getLogger(ExpedientPeticioServiceImpl.class);

}
