package es.caib.ripea.service.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.caib.ripea.persistence.entity.ContingutEntity;
import es.caib.ripea.persistence.entity.ExpedientEntity;
import es.caib.ripea.persistence.entity.ExpedientTascaComentariEntity;
import es.caib.ripea.persistence.entity.ExpedientTascaEntity;
import es.caib.ripea.persistence.entity.MetaExpedientEntity;
import es.caib.ripea.persistence.entity.MetaExpedientTascaEntity;
import es.caib.ripea.persistence.entity.UsuariEntity;
import es.caib.ripea.persistence.repository.AlertaRepository;
import es.caib.ripea.persistence.repository.ExpedientTascaComentariRepository;
import es.caib.ripea.persistence.repository.ExpedientTascaRepository;
import es.caib.ripea.persistence.repository.MetaExpedientRepository;
import es.caib.ripea.persistence.repository.MetaExpedientTascaRepository;
import es.caib.ripea.persistence.repository.UsuariRepository;
import es.caib.ripea.service.helper.CacheHelper;
import es.caib.ripea.service.helper.ContingutHelper;
import es.caib.ripea.service.helper.ConversioTipusHelper;
import es.caib.ripea.service.helper.DateHelper;
import es.caib.ripea.service.helper.EmailHelper;
import es.caib.ripea.service.helper.EntityComprovarHelper;
import es.caib.ripea.service.helper.PaginacioHelper;
import es.caib.ripea.service.helper.TascaHelper;
import es.caib.ripea.service.helper.UsuariHelper;
import es.caib.ripea.service.intf.dto.ContingutDto;
import es.caib.ripea.service.intf.dto.ExpedientTascaComentariDto;
import es.caib.ripea.service.intf.dto.ExpedientTascaDto;
import es.caib.ripea.service.intf.dto.LogTipusEnumDto;
import es.caib.ripea.service.intf.dto.MetaExpedientTascaDto;
import es.caib.ripea.service.intf.dto.MetaExpedientTascaValidacioDto;
import es.caib.ripea.service.intf.dto.PaginaDto;
import es.caib.ripea.service.intf.dto.PaginacioParamsDto;
import es.caib.ripea.service.intf.dto.PrioritatEnumDto;
import es.caib.ripea.service.intf.dto.TascaEstatEnumDto;
import es.caib.ripea.service.intf.dto.UsuariDto;
import es.caib.ripea.service.intf.dto.UsuariTascaFiltreDto;
import es.caib.ripea.service.intf.exception.NotFoundException;
import es.caib.ripea.service.intf.service.EventService;
import es.caib.ripea.service.intf.service.ExpedientTascaService;

@Service
public class ExpedientTascaServiceImpl implements ExpedientTascaService {

	@Autowired private MetaExpedientRepository metaExpedientRepository;
	@Autowired private ExpedientTascaRepository expedientTascaRepository;
	@Autowired private MetaExpedientTascaRepository metaExpedientTascaRepository;
	@Autowired private ExpedientTascaComentariRepository expedientTascaComentariRepository;
	
	@Autowired private EventService eventService;
	
	@Autowired private ConversioTipusHelper conversioTipusHelper;
	@Autowired private EntityComprovarHelper entityComprovarHelper;
	@Autowired private UsuariRepository usuariRepository;
	@Autowired private CacheHelper cacheHelper;
	@Autowired private ContingutHelper contingutHelper;
	@Autowired private AlertaRepository alertaRepository;
	@Autowired private PaginacioHelper paginacioHelper;
	@Autowired private TascaHelper tascaHelper;

	@Transactional(readOnly = true)
	@Override
	public List<ExpedientTascaDto> findAmbExpedient(
		Long entitatId,
		Long expedientId,
		PaginacioParamsDto paginacioParams) {
		logger.debug("Obtenint la llista de l'expedient tasques (" +
			"entitatId=" + entitatId + ", " +
			"expedientId=" + expedientId + ")");
		ExpedientEntity expedient = entityComprovarHelper.comprovarExpedient(
			expedientId,
			false,
			true,
			false,
			false,
			false,
			null);

		paginacioParams.canviaCampOrdenacio("dataLimitString", "dataLimit");
		paginacioParams.canviaCampOrdenacio("duracioFormat", "duracio");

		List<ExpedientTascaEntity> tasques = expedientTascaRepository.findByExpedient(
			expedient,
			paginacioHelper.toSpringDataPageable(paginacioParams));

		return conversioTipusHelper.convertirList(tasques, ExpedientTascaDto.class);
	}

	@Transactional(readOnly = true)
	@Override
	public PaginaDto<ExpedientTascaDto> findAmbAuthentication(
		Long entitatId,
		UsuariTascaFiltreDto filtre,
		PaginacioParamsDto paginacioParams) {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		logger.debug("Obtenint la llista del usuari tasques (" +
			"auth=" + auth.getName() + ")");

		UsuariEntity usuariEntity = usuariRepository.findByCodi(auth.getName());

		ExpedientEntity expedient = null;
		if (filtre.getExpedientId() != null) {
			expedient = entityComprovarHelper.comprovarExpedient(
				filtre.getExpedientId(),
				false,
				false,
				false,
				false,
				false,
				null);
		}

		Page<ExpedientTascaEntity> tasques = null;
		Date dataInici = DateHelper.toDateInicialDia(filtre.getDataInici());
		Date dataFi = DateHelper.toDateFinalDia(filtre.getDataFi());
		Date dataLimitInici = DateHelper.toDateInicialDia(filtre.getDataLimitInici());
		Date dataLimitFi = DateHelper.toDateFinalDia(filtre.getDataLimitFi());

		String titol = filtre.getTitol();
		PrioritatEnumDto prioritat = filtre.getPrioritat();
		MetaExpedientTascaEntity tipusTasca = null;
		if (filtre.getMetaExpedientTascaId() != null) {
			tipusTasca = metaExpedientTascaRepository.getOne(filtre.getMetaExpedientTascaId());
		}
		MetaExpedientEntity procediment = null;
		if (filtre.getMetaExpedientId() != null) {
			procediment = metaExpedientRepository.getOne(filtre.getMetaExpedientId());
		}

		if (filtre.getEstats().length == 0) {
			tasques = expedientTascaRepository.findByResponsable(
				usuariEntity,
				expedient == null,
				expedient,
				dataInici == null,
				dataInici,
				dataFi == null,
				dataFi,
				dataLimitInici == null,
				dataLimitInici,
				dataLimitFi == null,
				dataLimitFi,
				titol == null || titol.isEmpty(),
				titol,
				prioritat == null,
				prioritat,
				tipusTasca == null,
				tipusTasca,
				procediment == null,
				procediment,
				paginacioHelper.toSpringDataPageable(
					paginacioParams));
		} else {
			tasques = expedientTascaRepository.findByResponsableAndEstat(
				usuariEntity,
				filtre.getEstats(),
				expedient == null,
				expedient,
				dataInici == null,
				dataInici,
				dataFi == null,
				dataFi,
				dataLimitInici == null,
				dataLimitInici,
				dataLimitFi == null,
				dataLimitFi,
				titol == null || titol.isEmpty(),
				titol,
				prioritat == null,
				prioritat,
				tipusTasca == null,
				tipusTasca,
				procediment == null,
				procediment,
				paginacioHelper.toSpringDataPageable(
					paginacioParams));
		}

		return paginacioHelper.toPaginaDto(tasques, ExpedientTascaDto.class);

	}

	@Transactional(readOnly = true)
	@Override
	public ContingutDto findTascaExpedient(
		Long entitatId,
		Long contingutId,
		Long tascaId,
		boolean ambFills,
		boolean ambVersions) {
		logger.debug("Obtenint expedient per tasca amb id per usuari ("
			+ "entitatId=" + entitatId + ", "
			+ "contingutId=" + contingutId + ", "
			+ "ambFills=" + ambFills + ", "
			+ "ambVersions=" + ambVersions + ")");


		ContingutEntity contingut = contingutHelper.comprovarContingutPertanyTascaAccesible(
			tascaId,
			contingutId);

		ContingutDto dto = contingutHelper.toContingutDto(
			contingut,
			false,
			ambFills,
			true,
			true,
			true,
			ambVersions,
			null,
			false,
			null,
			false,
			0,
			null,
			null,
			true,
			true,
			true,
			false);
		dto.setAlerta(alertaRepository.countByLlegidaAndContingutId(
			false,
			dto.getId()) > 0);

		return dto;
	}

	@Transactional(readOnly = true)
	@Override
	public ContingutDto findByTascaBasicInfo(
		Long contingutId,
		Long tascaId) {

		ContingutEntity contingut = contingutHelper.comprovarContingutPertanyTascaAccesible(
			tascaId,
			contingutId);

		return contingutHelper.getBasicInfo(contingut);
	}

	@Transactional
	@Override
	public void changeTascaPrioritat(ExpedientTascaDto expedientTascaDto) {
		expedientTascaRepository.getOne(expedientTascaDto.getId()).setPrioritat(expedientTascaDto.getPrioritat());
	}

	@Transactional(readOnly = true)
	@Override
	public long countTasquesPendents() {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		logger.debug("Obtenint la llista del usuari tasques (" +
			"auth=" + auth.getName() + ")");

		return cacheHelper.countTasquesPendents(
			auth.getName());
	}

	@Transactional(readOnly = true)
	@Override
	public List<MetaExpedientTascaDto> findAmbMetaExpedient(
		Long entitatId,
		Long metaExpedientId) {
		logger.debug("Obtenint la llista de l'expedient tasques (" +
			"entitatId=" + entitatId + ", " +
			"metaExpedientId=" + metaExpedientId + ")");

		MetaExpedientEntity metaExpedient = metaExpedientRepository.getOne(
			metaExpedientId);

		List<MetaExpedientTascaEntity> tasques = metaExpedientTascaRepository.findByMetaExpedientAndActivaTrue(
			metaExpedient);

		return conversioTipusHelper.convertirList(
			tasques,
			MetaExpedientTascaDto.class);
	}

	@Transactional(readOnly = true)
	@Override
	public List<MetaExpedientTascaDto> findAmbEntitat(
		Long entitatId) {
		logger.debug("Obtenint la llista de l'expedient tasques (" +
			"entitatId=" + entitatId + ")");

		List<MetaExpedientTascaEntity> tasques = metaExpedientTascaRepository.findByActivaTrue();

		return conversioTipusHelper.convertirList(
			tasques,
			MetaExpedientTascaDto.class);
	}

	@Transactional(readOnly = true)
	@Override
	public ExpedientTascaDto findOne(Long expedientTascaId) {
		logger.debug("Consultant el expedient tasca expedientTascaId="+expedientTascaId+")");
		ExpedientTascaEntity expedientTascaEntity = expedientTascaRepository.getOne(expedientTascaId);
		return conversioTipusHelper.convertir(expedientTascaEntity, ExpedientTascaDto.class);
	}

	@Transactional
	@Override
	public ExpedientTascaDto canviarTascaEstat(
		Long tascaId,
		TascaEstatEnumDto tascaEstat,
		String motiu,
		String rolActual) {
		logger.debug("Canviant estat del tasca " +
			"tascaId=" + tascaId + ", " +
			"tascaEstat=" + tascaEstat +
			")");
		ExpedientTascaEntity tasca = tascaHelper.canviarEstatTasca(tascaId, tascaEstat, motiu, rolActual);
		eventService.notifyTasquesPendents();
		return conversioTipusHelper.convertir(tasca, ExpedientTascaDto.class);
	}

	@Transactional
	@Override
	public ExpedientTascaDto updateResponsables(Long expedientTascaId, List<String> responsablesCodi) {
		logger.debug("Canviant responsable de la tasca " +
			"expedientTascaId=" + expedientTascaId + ", " +
			"responsablesCodi=" + responsablesCodi +
			")");
		ExpedientTascaEntity expedientTascaEntity = tascaHelper.reassignarTasca(expedientTascaId, responsablesCodi);
		eventService.notifyTasquesPendents();
		return conversioTipusHelper.convertir(expedientTascaEntity, ExpedientTascaDto.class);
	}

	@Transactional
	@Override
	public ExpedientTascaDto updateDelegat(
		Long expedientTascaId,
		String delegatCodi,
		String comentari) {
		ExpedientTascaEntity expedientTascaEntity = tascaHelper.delegarTasca(expedientTascaId, delegatCodi, comentari);
		eventService.notifyTasquesPendents();
		return conversioTipusHelper.convertir(expedientTascaEntity, ExpedientTascaDto.class);
	}

	@Transactional
	@Override
	public ExpedientTascaDto cancelarDelegacio(Long expedientTascaId, String comentari) {
		ExpedientTascaEntity expedientTascaEntity = tascaHelper.retomarTasca(expedientTascaId, comentari);
		eventService.notifyTasquesPendents();
		return conversioTipusHelper.convertir(expedientTascaEntity, ExpedientTascaDto.class);
	}

	@Transactional
	@Override
	public ExpedientTascaDto reobrirTasca(Long expedientTascaId, List<String> responsablesCodi, String motiu, String rolActual) {
		logger.debug("Reobrint tasca (expedientTascaId=" + expedientTascaId + ")");
		ExpedientTascaEntity expedientTascaEntity = tascaHelper.reobrirTasca(expedientTascaId, responsablesCodi, motiu, rolActual);
		return conversioTipusHelper.convertir(expedientTascaEntity, ExpedientTascaDto.class);
	}

	@Transactional(readOnly = true)
	@Override
	public MetaExpedientTascaDto findMetaExpedientTascaById(Long metaExpedientTascaId) {
		logger.debug("Consultant el metaexpedient tasca expedientTascaId="+metaExpedientTascaId+")");
		MetaExpedientTascaEntity metaExpedientTascaEntity = metaExpedientTascaRepository.getOne(metaExpedientTascaId);
		return conversioTipusHelper.convertir(metaExpedientTascaEntity, MetaExpedientTascaDto.class);
	}

	@Override
	@Transactional
	public ExpedientTascaDto createTasca(Long entitatId, Long expedientId, ExpedientTascaDto expedientTasca) {
		logger.debug("Creant nou representant (entitatId=" + entitatId + ", expedientId=" + expedientId + ", expedientTasca=" + expedientTasca + ")");
		ExpedientTascaEntity expedientTascaEntity = tascaHelper.createTasca(entitatId, expedientId, expedientTasca);
		eventService.notifyTasquesPendents();
		return conversioTipusHelper.convertir(expedientTascaEntity, ExpedientTascaDto.class);
	}

	@Transactional
	@Override
	@CacheEvict(value = "errorsValidacioNode", key = "#contingutId")
	public void deleteTascaReversible(
		Long entitatId,
		Long tascaId,
		Long contingutId) throws IOException {
		logger.debug("Esborrant el contingut (entitatId=" + entitatId + ", contingutId=" + contingutId + ")");
		contingutHelper.comprovarContingutPertanyTascaAccesible(tascaId, contingutId);
		contingutHelper.deleteReversible(entitatId, contingutId, tascaId, null);
	}

	@Transactional
	@Override
	public boolean publicarComentariPerExpedientTasca(
		Long entitatId,
		Long expedientTascaId,
		String text,
		String rolActual) {
		logger.debug("Obtenint els comentaris per la tasca (" + "entitatId=" + entitatId + ", " + "tascaId=" + expedientTascaId + ")");

		entityComprovarHelper.comprovarEntitat(entitatId, false, false, true, false, false);

		ExpedientTascaEntity tasca = expedientTascaRepository.findById(expedientTascaId).orElse(null);
		if (tasca == null) {
			throw new NotFoundException(expedientTascaId, ExpedientTascaEntity.class);
		}

		Exception exception = null;
		try {
			entityComprovarHelper.comprovarExpedient(
				tasca.getExpedient().getId(),
				false,
				false,
				true,
				false,
				false,
				rolActual);
		} catch (Exception e) {
			exception = e;
		}

		if (exception != null) {
			contingutHelper.comprovarContingutPertanyTascaAccesible(
				expedientTascaId,
				tasca.getExpedient().getId());
		}

		// truncam a 1024 caracters
		if (text.length() > 1024)
			text = text.substring(0, 1021) + "...";
		ExpedientTascaComentariEntity comentari = ExpedientTascaComentariEntity.getBuilder(tasca, text).build();
		expedientTascaComentariRepository.save(comentari);
		return true;
	}

	@Transactional(readOnly = true)
	@Override
	public List<ExpedientTascaComentariDto> findComentarisPerTasca(Long entitatId, Long expedientTascaId) {
		logger.debug("Obtenint els comentaris per la tasca (" + "entitatId=" + entitatId + ", " + "tascaId=" + expedientTascaId + ")");
		entityComprovarHelper.comprovarEntitat(entitatId, false, false, true, false, false);

		ExpedientTascaEntity tasca = expedientTascaRepository.findById(expedientTascaId).orElse(null);
		if (tasca == null) {
			throw new NotFoundException(expedientTascaId, ExpedientTascaEntity.class);
		}

		Exception exception = null;
		try {
			entityComprovarHelper.comprovarExpedient(
				tasca.getExpedient().getId(),
				false,
				true,
				false,
				false,
				false,
				null);
		} catch (Exception e) {
			exception = e;
		}

		if (exception != null) {
			contingutHelper.comprovarContingutPertanyTascaAccesible(
				expedientTascaId,
				tasca.getExpedient().getId());
		}

		List<ExpedientTascaComentariEntity> tascacoms = expedientTascaComentariRepository.findByExpedientTascaOrderByCreatedDateAsc(tasca);
		List<ExpedientTascaComentariDto> resultat = new ArrayList<ExpedientTascaComentariDto>();
		
		if (tascacoms!=null) {
			for (ExpedientTascaComentariEntity etc: tascacoms) {
				ExpedientTascaComentariDto etcDto = conversioTipusHelper.convertir(etc, ExpedientTascaComentariDto.class);
				if (etc.getCreatedBy().isPresent()) {
					UsuariEntity ue = usuariRepository.findByCodi(etc.getCreatedBy().get());
					UsuariDto ucb = new UsuariDto();
					ucb.setCodi(ue.getCodi());
					ucb.setNom(ue.getNom());
					ucb.setNif(ue.getNif());
					ucb.setEmail(ue.getEmail());
					etcDto.setCreatedBy(ucb);
				}
				resultat.add(etcDto);
			}
		}
		
		return resultat;
	}

	@Transactional
	@Override
	public ExpedientTascaDto updateDataLimit(ExpedientTascaDto expedientTascaDto) {
		logger.debug("Canviant responsable de la tasca " +
			"expedientTascaId=" + expedientTascaDto.getId() + ", " +
			"dataLimit=" + expedientTascaDto.getDataLimit() + ")");

		ExpedientTascaEntity expedientTascaEntity = tascaHelper.updateDataLimit(
				expedientTascaDto.getId(),
				expedientTascaDto.getDataLimit(),
				expedientTascaDto.getDuracio());

		return conversioTipusHelper.convertir(expedientTascaEntity, ExpedientTascaDto.class);
	}

	@Override
	@Transactional(readOnly = true)
	public List<MetaExpedientTascaValidacioDto> getValidacionsPendentsTasca(Long expedientTascaId) {
		return tascaHelper.getValidacionsPendentsTasca(expedientTascaId);
	}

	private static final Logger logger = LoggerFactory.getLogger(ExpedientTascaServiceImpl.class);
}