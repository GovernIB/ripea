package es.caib.ripea.service.resourceservice;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import javax.annotation.PostConstruct;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import es.caib.comanda.ms.salut.helper.MonitorHelper;
import es.caib.ripea.persistence.entity.OrganGestorEntity;
import es.caib.ripea.persistence.entity.resourceentity.AlertaResourceEntity;
import es.caib.ripea.persistence.entity.resourceentity.ExcepcioLogResourceEntity;
import es.caib.ripea.persistence.entity.resourceentity.ExpedientResourceEntity;
import es.caib.ripea.persistence.entity.resourceentity.ExpedientTascaResourceEntity;
import es.caib.ripea.persistence.entity.resourceentity.IniciResourceEntity;
import es.caib.ripea.persistence.entity.resourceentity.IntegracioResourceEntity;
import es.caib.ripea.persistence.entity.resourceentity.MetaExpedientResourceEntity;
import es.caib.ripea.persistence.repository.OrganGestorRepository;
import es.caib.ripea.service.base.service.BaseReadonlyResourceService;
import es.caib.ripea.service.helper.ConfigHelper;
import es.caib.ripea.service.helper.MetaExpedientHelper;
import es.caib.ripea.service.intf.base.exception.AnswerRequiredException.AnswerValue;
import es.caib.ripea.service.intf.base.exception.ArtifactNotFoundException;
import es.caib.ripea.service.intf.base.exception.ReportGenerationException;
import es.caib.ripea.service.intf.config.PropertyConfig;
import es.caib.ripea.service.intf.dto.EntitatDto;
import es.caib.ripea.service.intf.dto.MetaExpedientRevisioEstatEnumDto;
import es.caib.ripea.service.intf.dto.TascaEstatEnumDto;
import es.caib.ripea.service.intf.model.AlertaResource;
import es.caib.ripea.service.intf.model.IniciResource;
import es.caib.ripea.service.intf.resourceservice.IniciResourceService;
import es.caib.ripea.service.intf.service.ExpedientPeticioService;
import es.caib.ripea.service.resourcehelper.inici.CriterisIndicadorsInici;
import es.caib.ripea.service.resourcehelper.inici.IndicadorInici;
import es.caib.ripea.service.resourcehelper.inici.RecomptesRecurs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Servei de la pantalla d'inici (veure {@link IniciResource}).
 *
 * Recurs sense taula: el llistat surt sempre buit i el detall no existeix. Tota la informació es
 * genera als informes {@link IniciResource#REPORT_INDICADORS} i {@link IniciResource#REPORT_LLISTES}.
 *
 * Cada targeta es compta sobre el servei del seu llistat de destinació ({@link RecomptesRecurs}), amb les
 * mateixes regles de visibilitat. Els recomptes d'un mateix llistat formen un grup: cada grup va en una
 * transacció pròpia i, si falla, només les seves targetes surten amb error.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class IniciResourceServiceImpl extends BaseReadonlyResourceService<IniciResource, Long, IniciResourceEntity> implements IniciResourceService {

	private static final int CONFIG_DIES_AVIS_EXPEDIENT_ANTIC_DEFAULT = 90;
	private static final int CONFIG_DIES_TASCA_SENSE_ACTIVITAT_DEFAULT = 7;
	private static final int TASCA_PREAVIS_DATA_LIMIT_DEFAULT = 3;
	private static final int DIES_EXPEDIENTS_TANCATS_RECENTS = 7;
	private static final int HORES_MONITOR = 24;
	/** Named query del llistat «Les meves tasques» (ExpedientTascaResourceServiceImpl): responsable, observador o delegat. */
	private static final String NAMED_QUERY_TASQUES_USUARI = "USUARI_RELACIONAT";

	private final RecomptesRecurs<ExpedientResourceEntity> recomptesExpedients;
	private final RecomptesRecurs<AlertaResourceEntity> recomptesAlertes;
	private final RecomptesRecurs<ExpedientTascaResourceEntity> recomptesTasques;
	private final RecomptesRecurs<MetaExpedientResourceEntity> recomptesProcediments;
	private final RecomptesRecurs<IntegracioResourceEntity> recomptesIntegracions;
	private final RecomptesRecurs<ExcepcioLogResourceEntity> recomptesExcepcions;
	private final ExpedientPeticioService expedientPeticioService;
	private final OrganGestorRepository organGestorRepository;
	private final ConfigHelper configHelper;
	private final MetaExpedientHelper metaExpedientHelper;

	@PostConstruct
	public void init() {
		register(IniciResource.REPORT_INDICADORS, new IndicadorsReportGenerator());
		register(IniciResource.REPORT_LLISTES, new LlistesReportGenerator());
	}

	/**
	 * Sense transacció: cada grup de recomptes obre la seva (els mètodes comptar són transaccionals). Així, un
	 * grup que falla no marca per a rollback una transacció compartida, que faria fallar tota la pantalla.
	 */
	@Override
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public <P extends Serializable> List<?> artifactReportGenerateData(
			Long id,
			String code,
			P params) throws ArtifactNotFoundException, ReportGenerationException {
		return super.artifactReportGenerateData(id, code, params);
	}

	@Override
	protected boolean isEntityRepositoryOptional() {
		return true;
	}

	@Override
	protected Optional<IniciResourceEntity> entityRepositoryFindOne(Long id) {
		return Optional.empty();
	}

	@Override
	protected Page<IniciResourceEntity> entityRepositoryFindEntities(
			String quickFilter,
			String filter,
			String[] namedQueries,
			Pageable pageable) {
		return Page.empty(pageable);
	}

	/** Targetes d'indicadors del rol actiu, en l'ordre en què s'han de mostrar. */
	private class IndicadorsReportGenerator implements ReportGenerator<IniciResourceEntity, Serializable, IniciResource.Indicador> {

		@Override
		public List<IniciResource.Indicador> generateData(
				String code,
				IniciResourceEntity entity,
				Serializable params) throws ReportGenerationException {
			return calcularIndicadors();
		}

		@Override
		public void onChange(Serializable id, Serializable previous, String fieldName, Object fieldValue, Map<String, AnswerValue> answers, String[] previousFieldNames, Serializable target) {}
	}

	/** Llistes resum del rol actiu, en l'ordre en què s'han de mostrar. */
	private class LlistesReportGenerator implements ReportGenerator<IniciResourceEntity, Serializable, IniciResource.Llista> {

		@Override
		public List<IniciResource.Llista> generateData(
				String code,
				IniciResourceEntity entity,
				Serializable params) throws ReportGenerationException {
			// Les consultes de les llistes de cada rol s'afegeixen a la fase 3.
			return new ArrayList<>();
		}

		@Override
		public void onChange(Serializable id, Serializable previous, String fieldName, Object fieldValue, Map<String, AnswerValue> answers, String[] previousFieldNames, Serializable target) {}
	}

	private List<IniciResource.Indicador> calcularIndicadors() {
		String rol = configHelper.getRolActual();
		List<IndicadorInici> cataleg = IndicadorInici.targetes(rol, metaExpedientHelper.isRevisioActiva());
		if (cataleg.isEmpty()) {
			return new ArrayList<>();
		}
		String usuariCodi = SecurityContextHolder.getContext().getAuthentication().getName();
		Date ara = new Date();
		int diesAntic = configHelper.getAsInt(PropertyConfig.CONFIG_DIES_AVIS_EXPEDIENT_ANTIC, CONFIG_DIES_AVIS_EXPEDIENT_ANTIC_DEFAULT);
		int diesSenseActivitat = configHelper.getAsInt(PropertyConfig.CONFIG_DIES_TASCA_SENSE_ACTIVITAT, CONFIG_DIES_TASCA_SENSE_ACTIVITAT_DEFAULT);
		int diesPreavis = configHelper.getAsInt(PropertyConfig.TASCA_PREAVIS_DATA_LIMIT, TASCA_PREAVIS_DATA_LIMIT_DEFAULT);
		Date fa24Hores = new Date(ara.getTime() - TimeUnit.HOURS.toMillis(HORES_MONITOR));

		Recull recull = new Recull(cataleg);
		recull.dies.put(IndicadorInici.EXPEDIENTS_ANTICS, diesAntic);
		recull.dies.put(IndicadorInici.EXPEDIENTS_TANCATS_RECENTS, DIES_EXPEDIENTS_TANCATS_RECENTS);
		recull.dies.put(IndicadorInici.TASQUES_VENCIMENT_PROPER, diesPreavis);
		recull.dies.put(IndicadorInici.TASQUES_SENSE_ACTIVITAT, diesSenseActivitat);

		// Llistat d'expedients
		Map<IndicadorInici, Specification<ExpedientResourceEntity>> expedients = new EnumMap<>(IndicadorInici.class);
		expedients.put(IndicadorInici.EXPEDIENTS_AGAFATS, CriterisIndicadorsInici.expedientsAgafats(usuariCodi));
		expedients.put(IndicadorInici.EXPEDIENTS_ANTICS, CriterisIndicadorsInici.expedientsAntics(LocalDateTime.now().minusDays(diesAntic)));
		expedients.put(IndicadorInici.EXPEDIENTS_OBERTS, CriterisIndicadorsInici.expedientsOberts());
		expedients.put(IndicadorInici.EXPEDIENTS_TANCATS_RECENTS, CriterisIndicadorsInici.expedientsTancatsDesDe(
				new Date(ara.getTime() - TimeUnit.DAYS.toMillis(DIES_EXPEDIENTS_TANCATS_RECENTS))));
		recull.comptar(expedients, recomptesExpedients, null);

		// Consulta «Alertes d'expedients»: les no llegides són les de la icona del llistat d'expedients
		Map<IndicadorInici, Specification<AlertaResourceEntity>> alertes = new EnumMap<>(IndicadorInici.class);
		alertes.put(IndicadorInici.ALERTES_NO_LLEGIDES, CriterisIndicadorsInici.alertesNoLlegides());
		recull.comptar(alertes, recomptesAlertes, new String[] { AlertaResource.NAMED_QUERY_CONSULTA });

		// Llistat d'anotacions: el mateix comptador (amb caché) que el menú
		recull.valor(IndicadorInici.ANOTACIONS_PENDENTS, this::comptarAnotacionsPendents);

		// «Les meves tasques»: la mateixa named query que el llistat (responsable, observador o delegat). També les
		// d'expedients tancats, que el llistat no distingeix i marca igual (taronja i vermell).
		Specification<ExpedientTascaResourceEntity> tasquesUsuari = CriterisIndicadorsInici.tasquesExpedientNoEsborrat();
		Specification<ExpedientTascaResourceEntity> tasquesUsuariPendents = tasquesUsuari
				.and(CriterisIndicadorsInici.tasquesEnEstat(CriterisIndicadorsInici.ESTATS_TASCA_PENDENT));
		Map<IndicadorInici, Specification<ExpedientTascaResourceEntity>> tasques = new EnumMap<>(IndicadorInici.class);
		tasques.put(IndicadorInici.TASQUES_EN_CURS, tasquesUsuari
				.and(CriterisIndicadorsInici.tasquesEnEstat(CriterisIndicadorsInici.ESTATS_TASCA_EN_CURS)));
		tasques.put(IndicadorInici.TASQUES_PENDENTS, tasquesUsuari
				.and(CriterisIndicadorsInici.tasquesEnEstat(Collections.singletonList(TascaEstatEnumDto.PENDENT))));
		// La data límit és un dia sense hora: límits en dies sencers (veure CriterisIndicadorsInici)
		LocalDate avui = LocalDate.now();
		Date dema = toDate(avui.plusDays(1));
		tasques.put(IndicadorInici.TASQUES_VENCIMENT_PROPER, tasquesUsuariPendents
				.and(CriterisIndicadorsInici.tasquesVencimentProper(dema, toDate(avui.plusDays(diesPreavis + 1L)))));
		tasques.put(IndicadorInici.TASQUES_FORA_TERMINI, tasquesUsuariPendents
				.and(CriterisIndicadorsInici.tasquesForaTermini(dema)));
		recull.comptar(tasques, recomptesTasques, new String[] { NAMED_QUERY_TASQUES_USUARI });

		// Consulta de tasques assignades de l'entitat (administrador): totes, sigui qui sigui el responsable
		Map<IndicadorInici, Specification<ExpedientTascaResourceEntity>> tasquesEntitat = new EnumMap<>(IndicadorInici.class);
		tasquesEntitat.put(IndicadorInici.TASQUES_SENSE_ACTIVITAT, CriterisIndicadorsInici.tasquesExpedientNoEsborrat()
				.and(CriterisIndicadorsInici.tasquesEnEstat(CriterisIndicadorsInici.ESTATS_TASCA_PENDENT))
				.and(CriterisIndicadorsInici.tasquesSenseActivitatDesDe(LocalDateTime.now().minusDays(diesSenseActivitat))));
		recull.comptar(tasquesEntitat, recomptesTasques, null);

		// Llistat de procediments (només hi són al catàleg amb la revisió activa)
		Map<IndicadorInici, Specification<MetaExpedientResourceEntity>> procediments = new EnumMap<>(IndicadorInici.class);
		procediments.put(IndicadorInici.PROCEDIMENTS_PENDENTS_REVISIO, CriterisIndicadorsInici.procedimentsEnEstatRevisio(MetaExpedientRevisioEstatEnumDto.PENDENT));
		procediments.put(IndicadorInici.PROCEDIMENTS_EN_DISSENY, CriterisIndicadorsInici.procedimentsEnEstatRevisio(MetaExpedientRevisioEstatEnumDto.DISSENY));
		procediments.put(IndicadorInici.PROCEDIMENTS_REBUTJATS, CriterisIndicadorsInici.procedimentsEnEstatRevisio(MetaExpedientRevisioEstatEnumDto.REBUTJAT));
		procediments.put(IndicadorInici.PROCEDIMENTS_REVISATS, CriterisIndicadorsInici.procedimentsEnEstatRevisio(MetaExpedientRevisioEstatEnumDto.REVISAT));
		recull.comptar(procediments, recomptesProcediments, null);

		// Monitor d'integracions, registre d'excepcions i monitor del sistema (del node que respon)
		Map<IndicadorInici, Specification<IntegracioResourceEntity>> integracions = new EnumMap<>(IndicadorInici.class);
		integracions.put(IndicadorInici.INTEGRACIONS_ERROR, CriterisIndicadorsInici.integracionsErrorDesDe(fa24Hores));
		recull.comptar(integracions, recomptesIntegracions, null);
		Map<IndicadorInici, Specification<ExcepcioLogResourceEntity>> excepcions = new EnumMap<>(IndicadorInici.class);
		excepcions.put(IndicadorInici.EXCEPCIONS, CriterisIndicadorsInici.excepcionsDesDe(fa24Hores));
		recull.comptar(excepcions, recomptesExcepcions, null);
		recull.valor(IndicadorInici.MEMORIA_LLIURE, () -> {
			MonitorHelper.MemoryUsage memoria = MonitorHelper.getJvmMemory();
			recull.totals.put(IndicadorInici.MEMORIA_LLIURE, memoria.getTotalMemory());
			return memoria.getFreeMemory();
		});
		recull.valor(IndicadorInici.DISC_LLIURE, () -> {
			MonitorHelper.DiskUsage disc = MonitorHelper.getRootDiskUsage();
			recull.totals.put(IndicadorInici.DISC_LLIURE, disc.getTotalSpace());
			return disc.getFreeSpace();
		});

		return recull.resultat();
	}

	private static Date toDate(LocalDate dia) {
		return Date.from(dia.atStartOfDay(ZoneId.systemDefault()).toInstant());
	}

	private Long comptarAnotacionsPendents() {
		EntitatDto entitat = ConfigHelper.getEntitat().get();
		OrganGestorEntity organActual = organGestorRepository.findByEntitatIdAndCodi(entitat.getId(), configHelper.getOrganActualCodi());
		return expedientPeticioService.countAnotacionsPendents(
				entitat.getId(),
				configHelper.getRolActual(),
				organActual != null ? organActual.getId() : null);
	}

	/**
	 * Valors de les targetes del catàleg del rol. Només es calcula el que hi ha al catàleg; un grup de
	 * recomptes que falla marca totes les seves targetes amb error i la resta es mostra igualment.
	 */
	private static class Recull {

		private final List<IndicadorInici> cataleg;
		private final Map<IndicadorInici, Long> valors = new EnumMap<>(IndicadorInici.class);
		private final Map<IndicadorInici, Long> totals = new EnumMap<>(IndicadorInici.class);
		private final Map<IndicadorInici, Integer> dies = new EnumMap<>(IndicadorInici.class);
		private final Set<IndicadorInici> errors = EnumSet.noneOf(IndicadorInici.class);

		private Recull(List<IndicadorInici> cataleg) {
			this.cataleg = cataleg;
		}

		private <E> void comptar(
				Map<IndicadorInici, Specification<E>> criteris,
				RecomptesRecurs<E> recurs,
				String[] namedQueries) {
			Map<String, Specification<E>> delCataleg = new LinkedHashMap<>();
			criteris.forEach((indicador, criteri) -> {
				if (cataleg.contains(indicador)) {
					delCataleg.put(indicador.name(), criteri);
				}
			});
			if (delCataleg.isEmpty()) {
				return;
			}
			try {
				recurs.comptar(delCataleg, namedQueries).forEach((codi, valor) -> valors.put(IndicadorInici.valueOf(codi), valor));
			} catch (Exception ex) {
				log.error("Error calculant els indicadors de la pantalla d'inici " + delCataleg.keySet(), ex);
				delCataleg.keySet().forEach(codi -> errors.add(IndicadorInici.valueOf(codi)));
			}
		}

		private void valor(IndicadorInici indicador, Supplier<Long> calcul) {
			if (!cataleg.contains(indicador)) {
				return;
			}
			try {
				valors.put(indicador, calcul.get());
			} catch (Exception ex) {
				log.error("Error calculant l'indicador de la pantalla d'inici " + indicador, ex);
				errors.add(indicador);
			}
		}

		private List<IniciResource.Indicador> resultat() {
			List<IniciResource.Indicador> resultat = new ArrayList<>();
			for (IndicadorInici indicador: cataleg) {
				IniciResource.Indicador targeta = new IniciResource.Indicador();
				targeta.setCodi(indicador.name());
				targeta.setError(errors.contains(indicador));
				targeta.setValor(targeta.isError() ? null : valors.get(indicador));
				targeta.setTotal(totals.get(indicador));
				targeta.setDies(dies.get(indicador));
				resultat.add(targeta);
			}
			return resultat;
		}
	}

}
