/**
 * 
 */
package es.caib.ripea.core.service;

import es.caib.ripea.core.api.dto.ContingutDto;
import es.caib.ripea.core.api.dto.ExpedientTascaComentariDto;
import es.caib.ripea.core.api.dto.ExpedientTascaDto;
import es.caib.ripea.core.api.dto.LogObjecteTipusEnumDto;
import es.caib.ripea.core.api.dto.LogTipusEnumDto;
import es.caib.ripea.core.api.dto.MetaExpedientTascaDto;
import es.caib.ripea.core.api.dto.PaginaDto;
import es.caib.ripea.core.api.dto.PaginacioParamsDto;
import es.caib.ripea.core.api.dto.TascaEstatEnumDto;
import es.caib.ripea.core.api.dto.UsuariTascaFiltreDto;
import es.caib.ripea.core.api.exception.NotFoundException;
import es.caib.ripea.core.api.service.ExpedientTascaService;
import es.caib.ripea.core.api.utils.Utils;
import es.caib.ripea.core.entity.ContingutEntity;
import es.caib.ripea.core.entity.ExpedientEntity;
import es.caib.ripea.core.entity.ExpedientTascaComentariEntity;
import es.caib.ripea.core.entity.ExpedientTascaEntity;
import es.caib.ripea.core.entity.MetaExpedientEntity;
import es.caib.ripea.core.entity.MetaExpedientTascaEntity;
import es.caib.ripea.core.entity.UsuariEntity;
import es.caib.ripea.core.helper.CacheHelper;
import es.caib.ripea.core.helper.ContingutHelper;
import es.caib.ripea.core.helper.ContingutLogHelper;
import es.caib.ripea.core.helper.ConversioTipusHelper;
import es.caib.ripea.core.helper.DateHelper;
import es.caib.ripea.core.helper.EmailHelper;
import es.caib.ripea.core.helper.EntityComprovarHelper;
import es.caib.ripea.core.helper.PaginacioHelper;
import es.caib.ripea.core.helper.TascaHelper;
import es.caib.ripea.core.helper.UsuariHelper;
import es.caib.ripea.core.repository.AlertaRepository;
import es.caib.ripea.core.repository.ExpedientTascaComentariRepository;
import es.caib.ripea.core.repository.ExpedientTascaRepository;
import es.caib.ripea.core.repository.MetaExpedientRepository;
import es.caib.ripea.core.repository.MetaExpedientTascaRepository;
import es.caib.ripea.core.repository.UsuariRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Implementació dels mètodes per a gestionar expedient peticions.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class ExpedientTascaServiceImpl implements ExpedientTascaService {

	@Autowired
	private MetaExpedientRepository metaExpedientRepository;
	@Autowired
	private ExpedientTascaRepository expedientTascaRepository;
	@Autowired
	private MetaExpedientTascaRepository metaExpedientTascaRepository;
	@Autowired
	private ExpedientTascaComentariRepository expedientTascaComentariRepository;
	@Autowired
	private ConversioTipusHelper conversioTipusHelper;
	@Autowired
	private EntityComprovarHelper entityComprovarHelper;
	@Autowired
	private UsuariRepository usuariRepository;
	@Autowired
	private CacheHelper cacheHelper;
	@Autowired
	private EmailHelper emailHelper;
	@Autowired
	private ContingutHelper contingutHelper;
	@Autowired
	private AlertaRepository alertaRepository;
	@Autowired
	private PaginacioHelper paginacioHelper;
	@Autowired
	private UsuariHelper usuariHelper;
	@Autowired
	private ContingutLogHelper contingutLogHelper;
	@Autowired
	private TascaHelper tascaHelper;

	
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
		expedientTascaRepository.findOne(expedientTascaDto.getId()).setPrioritat(expedientTascaDto.getPrioritat());
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
		
		MetaExpedientEntity metaExpedient = metaExpedientRepository.findOne(
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
		logger.debug("Consultant el expedient tasca " +
				"expedientTascaId=" +
				expedientTascaId +
				")");

		ExpedientTascaEntity expedientTascaEntity = expedientTascaRepository.findOne(expedientTascaId);

		return conversioTipusHelper.convertir(
				expedientTascaEntity,
				ExpedientTascaDto.class);

	}

	@Transactional
	@Override
	public ExpedientTascaDto canviarTascaEstat(
			Long tascaId,
			TascaEstatEnumDto tascaEstat,
			String motiu,
			String rolActual) {
		logger.debug("Canviant estat del tasca " +
				"tascaId=" + tascaId +", "+
				"tascaEstat=" + tascaEstat +
				")");

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UsuariEntity responsableActual = usuariHelper.getUsuariByCodiDades(auth.getName(), true, true);
		
//		ExpedientTascaEntity tasca = expedientTascaRepository.findOne(tascaId);
		ExpedientTascaEntity tasca = null;

		try {
			tasca = tascaHelper.comprovarTasca(tascaId);
		} catch (Exception e) {
			contingutHelper.comprovarContingutDinsExpedientModificable(
					tasca.getExpedient().getEntitat().getId(),
					tasca.getExpedient().getId(),
					false,
					true,
					false,
					false, 
					false, 
					true, 
					rolActual);
		}
		
		
		TascaEstatEnumDto tascaEstatAnterior = tasca.getEstat();
		
		if (tascaEstat == TascaEstatEnumDto.REBUTJADA) {
			tasca.updateRebutjar(motiu);
		} else {
			tasca.updateEstat(tascaEstat);
		}
		
		if (tascaEstat == TascaEstatEnumDto.FINALITZADA || tascaEstat == TascaEstatEnumDto.CANCELLADA || tascaEstat == TascaEstatEnumDto.REBUTJADA) {
			tasca.updateDelegat(null);
		}
		
		if(tascaEstat == TascaEstatEnumDto.INICIADA) {
			tasca.updateResponsableActual(responsableActual);
		}

		ExpedientEntity expedientEntity = tasca.getExpedient();
		
		if (tascaEstat == TascaEstatEnumDto.FINALITZADA && tasca.getMetaTasca().getEstatFinalitzarTasca() != null) {
			expedientEntity.updateEstatAdditional(tasca.getMetaTasca().getEstatFinalitzarTasca());
		}
		
		// Tornar a l'estat inicial 'OBERT' si no hi ha un estat addicional en finalitzar tasca
		if (tascaEstat == TascaEstatEnumDto.FINALITZADA 
				&& tasca.getMetaTasca().getEstatFinalitzarTasca() == null
				&& expedientEntity.getEstatAdditional() != null) {
			expedientEntity.updateEstatAdditional(null);
		}
		
		emailHelper.enviarEmailCanviarEstatTasca(tasca, tascaEstatAnterior);
		
		for (UsuariEntity responsable: tasca.getResponsables()) {
			cacheHelper.evictCountTasquesPendents(responsable.getCodi());	
		}

		log(tasca, LogTipusEnumDto.CANVI_ESTAT);
		
		return conversioTipusHelper.convertir(tasca,
				ExpedientTascaDto.class);
	}

	@Transactional
	@Override
	public ExpedientTascaDto updateResponsables(Long expedientTascaId, List<String> responsablesCodi) {
		logger.debug("Canviant responsable de la tasca " +
				"expedientTascaId=" + expedientTascaId +", "+
				"responsablesCodi=" + responsablesCodi +
				")");

		ExpedientTascaEntity expedientTascaEntity = expedientTascaRepository.findOne(expedientTascaId);
		
		List<UsuariEntity> responsables = new ArrayList<UsuariEntity>();
		for (String responsableCodi: responsablesCodi) {
			UsuariEntity responsable = usuariHelper.getUsuariByCodiDades(responsableCodi, true, true);
			responsables.add(responsable);
		}
		
		expedientTascaEntity.updateResponsables(responsables);	
		
		emailHelper.enviarEmailReasignarResponsableTasca(expedientTascaEntity);
		
		for (UsuariEntity responsable: expedientTascaEntity.getResponsables()) {
			cacheHelper.evictCountTasquesPendents(responsable.getCodi());	
		}
		
		log(expedientTascaEntity, LogTipusEnumDto.CANVI_RESPONSABLES);
		
		return conversioTipusHelper.convertir(expedientTascaEntity,
				ExpedientTascaDto.class);
	}
	
	@Transactional
	@Override
	public ExpedientTascaDto updateDelegat(
			Long expedientTascaId, 
			String delegatCodi,
			String comentari) {
		ExpedientTascaEntity expedientTascaEntity = expedientTascaRepository.findOne(expedientTascaId);
		
		UsuariEntity delegat = usuariHelper.getUsuariByCodiDades(delegatCodi, true, true);
		
		expedientTascaEntity.updateDelegat(delegat);
		
		if (comentari != null) {
			ExpedientTascaComentariEntity comentariTasca = ExpedientTascaComentariEntity.getBuilder(expedientTascaEntity, comentari).build();
			expedientTascaEntity.addComentari(comentariTasca);
		}
		
		emailHelper.enviarEmailDelegarTasca(expedientTascaEntity);
		
		cacheHelper.evictCountTasquesPendents(delegat.getCodi());
		
		log(expedientTascaEntity, LogTipusEnumDto.DELEGAR_TASCA);
		
		return conversioTipusHelper.convertir(expedientTascaEntity,
				ExpedientTascaDto.class);
	}
	
	@Transactional
	@Override
	public ExpedientTascaDto cancelarDelegacio(
			Long expedientTascaId, 
			String comentari) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		ExpedientTascaEntity expedientTascaEntity = expedientTascaRepository.findOne(expedientTascaId);
		logger.debug("Obtenint la llista del usuari tasques (" +
				"auth=" + auth.getName() + ")");
		
		UsuariEntity delegat = expedientTascaEntity.getDelegat();
		
		expedientTascaEntity.updateDelegat(null);
		
		if (comentari != null) {
			ExpedientTascaComentariEntity comentariTasca = ExpedientTascaComentariEntity.getBuilder(expedientTascaEntity, comentari).build();
			expedientTascaEntity.addComentari(comentariTasca);
		}
		
		emailHelper.enviarEmailCancelarDelegacioTasca(
				expedientTascaEntity, 
				delegat,
				comentari);
		
		cacheHelper.evictCountTasquesPendents(auth.getName());
		
		log(expedientTascaEntity, LogTipusEnumDto.CANCELAR_DELEGACIO_TASCA);
		
		return conversioTipusHelper.convertir(expedientTascaEntity,
				ExpedientTascaDto.class);
	}
	
	@Transactional
	@Override
	public ExpedientTascaDto reobrirTasca(
			Long expedientTascaId, 
			List<String> responsablesCodi, 
			String motiu,
			String rolActual) {
		logger.debug("Reobrint tasca (expedientTascaId=" + expedientTascaId + ")");
		
		ExpedientTascaEntity expedientTascaEntity = expedientTascaRepository.findOne(expedientTascaId);
		
		if (motiu != null) {
			ExpedientTascaComentariEntity comentariTasca = ExpedientTascaComentariEntity.getBuilder(expedientTascaEntity, motiu).build();
			expedientTascaEntity.addComentari(comentariTasca);
		}
		
		List<UsuariEntity> responsables = new ArrayList<UsuariEntity>();
		for (String responsableCodi: responsablesCodi) {
			UsuariEntity responsable = usuariHelper.getUsuariByCodiDades(responsableCodi, true, true);
			responsables.add(responsable);
		}
		
		expedientTascaEntity.updateResponsables(responsables);	
		
		canviarTascaEstat(
				expedientTascaId, 
				TascaEstatEnumDto.PENDENT, 
				motiu, 
				rolActual);
		
				
		return conversioTipusHelper.convertir(
				expedientTascaEntity,
				ExpedientTascaDto.class);
	}
	
	@Transactional(readOnly = true)
	@Override
	public MetaExpedientTascaDto findMetaExpedientTascaById(Long metaExpedientTascaId) {
		logger.debug("Consultant el metaexpedient tasca " +
				"expedientTascaId=" +
				metaExpedientTascaId +
				")");

		MetaExpedientTascaEntity metaExpedientTascaEntity = metaExpedientTascaRepository.findOne(metaExpedientTascaId);

		return conversioTipusHelper.convertir(
				metaExpedientTascaEntity,
				MetaExpedientTascaDto.class);

	}

	@Override
	@Transactional
	public ExpedientTascaDto createTasca(
			Long entitatId,
			Long expedientId,
			ExpedientTascaDto expedientTasca){
		
		logger.debug("Creant nou representant ("
					+ "entitatId=" + entitatId + ", "
					+ "expedientId=" + expedientId + ", "
					+ "expedientTasca=" + expedientTasca + ")");
		
		ExpedientEntity expedient = entityComprovarHelper.comprovarExpedient(
				expedientId,
				false,
				false,
				false,
				false,
				false,
				null);

		MetaExpedientTascaEntity metaExpedientTascaEntity = metaExpedientTascaRepository.findOne(expedientTasca.getMetaExpedientTascaId());
		List<UsuariEntity> responsables = new ArrayList<UsuariEntity>();
		for (String responsableCodi: expedientTasca.getResponsablesCodi()) {
			UsuariEntity responsable = usuariHelper.getUsuariByCodiDades(responsableCodi, true, true);
			responsables.add(responsable);
		}

		List<UsuariEntity> observadors = new ArrayList<UsuariEntity>(); // Per coneixement
		
		if (expedientTasca.getObservadorsCodi() != null) {
			for (String observadorCodi : expedientTasca.getObservadorsCodi()) {
			    UsuariEntity observador = usuariHelper.getUsuariByCodiDades(observadorCodi, true, true);
			    observadors.add(observador);
			}
		}
		
		ExpedientTascaEntity expedientTascaEntity = ExpedientTascaEntity.getBuilder(
				expedient, 
				metaExpedientTascaEntity, 
				responsables, 
				observadors,
				expedientTasca.getDataLimit(),
				expedientTasca.getTitol(),
				expedientTasca.getDuracio(),
				expedientTasca.getPrioritat(),
				expedientTasca.getObservacions()).build();
		
		if (expedientTasca.getComentari() != null && !expedientTasca.getComentari().isEmpty()) {
			ExpedientTascaComentariEntity comentari = ExpedientTascaComentariEntity.getBuilder(expedientTascaEntity, expedientTasca.getComentari()).build();
			expedientTascaEntity.addComentari(comentari);
		}
		
		String titol = expedientTasca.getTitol();
		String observacions = expedientTasca.getObservacions();
		boolean isTitolNotEmtpy = titol != null && ! titol.isEmpty();
		boolean isObservacionsNotEmpty = observacions != null && ! observacions.isEmpty();
		
		if (isTitolNotEmtpy || isObservacionsNotEmpty) {
			String comentariTitol = (isTitolNotEmtpy ? "Títol: " + titol + "\n" : "") +
									(isObservacionsNotEmpty ? "\tObservacions: " + observacions + "\n" : "");
			
 			ExpedientTascaComentariEntity comentari = ExpedientTascaComentariEntity.getBuilder(expedientTascaEntity, comentariTitol).build();
			expedientTascaEntity.addComentari(comentari);
		}
		if (metaExpedientTascaEntity.getEstatCrearTasca() != null) {
			expedient.updateEstatAdditional(metaExpedientTascaEntity.getEstatCrearTasca());
		}
		
		for (String responsableCodi: expedientTasca.getResponsablesCodi()) {
			cacheHelper.evictCountTasquesPendents(responsableCodi);	
		}
		
		if (expedientTasca.getObservadorsCodi() != null) {
			for (String observadorCodi: expedientTasca.getObservadorsCodi()) {
				cacheHelper.evictCountTasquesPendents(observadorCodi);	
			}
		}

		expedientTascaRepository.save(expedientTascaEntity);
		log(expedientTascaEntity, LogTipusEnumDto.CREACIO);
		
		emailHelper.enviarEmailCanviarEstatTasca(
				expedientTascaEntity,
				null);

		return conversioTipusHelper.convertir(
				expedientTascaEntity,
					ExpedientTascaDto.class);
	}	


	@Transactional
	@Override
	@CacheEvict(value = "errorsValidacioNode", key = "#contingutId")
	public void deleteTascaReversible(
			Long entitatId,
			Long tascaId,
			Long contingutId) throws IOException {
		logger.debug("Esborrant el contingut ("
				+ "entitatId=" + entitatId + ", "
				+ "contingutId=" + contingutId + ")");
		
		ContingutEntity contingut = contingutHelper.comprovarContingutPertanyTascaAccesible(
				tascaId,
				contingutId);
		
		contingutHelper.deleteReversible(
				entitatId,
				contingut, null);
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

		ExpedientTascaEntity tasca = expedientTascaRepository.findOne(expedientTascaId);
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

		ExpedientTascaEntity tasca = expedientTascaRepository.findOne(expedientTascaId);
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

		return conversioTipusHelper.convertirList(tascacoms, ExpedientTascaComentariDto.class);
	}


	@Transactional
	@Override
	public ExpedientTascaDto updateDataLimit(ExpedientTascaDto expedientTascaDto) {
		logger.debug("Canviant responsable de la tasca " +
				"expedientTascaId=" + expedientTascaDto.getId() +", "+
				"dataLimit=" + expedientTascaDto.getDataLimit() + ")");

		ExpedientTascaEntity expedientTascaEntity = expedientTascaRepository.findOne(expedientTascaDto.getId());

		//Si no ha canviat res en el DTO respecte del entity (info a BBDD), no fer cap acció
		if (Utils.sonValorsDiferentsControlantNulls(expedientTascaEntity.getDataLimit(),  expedientTascaDto.getDataLimit()) ||
			Utils.sonValorsDiferentsControlantNulls(expedientTascaEntity.getDuracio(),  expedientTascaDto.getDuracio())) {
			
			expedientTascaEntity.updateDataLimit(expedientTascaDto.getDataLimit());
			expedientTascaEntity.setDuracio(expedientTascaDto.getDuracio());
			
			emailHelper.enviarEmailModificacioDataLimitTasca(expedientTascaEntity);
		}
		
		log(expedientTascaEntity, LogTipusEnumDto.CANVI_DATALIMIT_TASCA);
		
		return conversioTipusHelper.convertir(expedientTascaEntity,
				ExpedientTascaDto.class);
	}
	
	private void log (ExpedientTascaEntity expedientTascaEntity, LogTipusEnumDto tipusLog) {
		contingutLogHelper.log(
				expedientTascaEntity.getExpedient(),
				LogTipusEnumDto.MODIFICACIO,
				expedientTascaEntity,
				LogObjecteTipusEnumDto.TASCA,
				tipusLog,
				expedientTascaEntity.getMetaTasca().getNom(),
				expedientTascaEntity.getComentaris().size() == 1 ? expedientTascaEntity.getComentaris().get(0).getText() : null, // expedientTascaEntity.getComentari(),
				false,
				false);
	}
	

	/*private String getIdiomaPerDefecte() {
		return PropertiesHelper.getProperties().getProperty(
				"es.caib.ripea.usuari.idioma.defecte",
				"CA");
	}*/

	private static final Logger logger = LoggerFactory.getLogger(ExpedientTascaServiceImpl.class);

}
