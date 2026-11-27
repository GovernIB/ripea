package es.caib.ripea.service.resourceservice;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Root;

import org.springframework.context.i18n.LocaleContextHolder;
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
import es.caib.ripea.persistence.entity.resourceentity.MetaExpedientComentariResourceEntity;
import es.caib.ripea.persistence.entity.resourceentity.MetaExpedientResourceEntity;
import es.caib.ripea.persistence.entity.resourceentity.UsuariResourceEntity;
import es.caib.ripea.persistence.repository.OrganGestorRepository;
import es.caib.ripea.service.base.service.BaseReadonlyResourceService;
import es.caib.ripea.service.helper.ConfigHelper;
import es.caib.ripea.service.helper.ExcepcioLogHelper;
import es.caib.ripea.service.helper.IntegracioHelper;
import es.caib.ripea.service.helper.MetaExpedientHelper;
import es.caib.ripea.service.intf.base.exception.AnswerRequiredException.AnswerValue;
import es.caib.ripea.service.intf.base.exception.ArtifactNotFoundException;
import es.caib.ripea.service.intf.base.exception.ReportGenerationException;
import es.caib.ripea.service.intf.config.BaseConfig;
import es.caib.ripea.service.intf.config.PropertyConfig;
import es.caib.ripea.service.intf.dto.EntitatDto;
import es.caib.ripea.service.intf.dto.IntegracioCodiEnum;
import es.caib.ripea.service.intf.dto.MetaExpedientRevisioEstatEnumDto;
import es.caib.ripea.service.intf.dto.TascaEstatEnumDto;
import es.caib.ripea.service.intf.model.AlertaResource;
import es.caib.ripea.service.intf.model.IniciResource;
import es.caib.ripea.service.intf.resourceservice.IniciResourceService;
import es.caib.ripea.service.intf.service.ExpedientPeticioService;
import es.caib.ripea.service.resourcehelper.PerspectivesLotHelper;
import es.caib.ripea.service.resourcehelper.inici.CacheInici;
import es.caib.ripea.service.resourcehelper.inici.ConsultesInici;
import es.caib.ripea.service.resourcehelper.inici.CriterisIndicadorsInici;
import es.caib.ripea.service.resourcehelper.inici.ExpedientsPerTancarRecurs;
import es.caib.ripea.service.resourcehelper.inici.FilesLlistesInici;
import es.caib.ripea.service.resourcehelper.inici.IndicadorInici;
import es.caib.ripea.service.resourcehelper.inici.LlistaInici;
import es.caib.ripea.service.resourcehelper.inici.VisibilitatRecurs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Servei de la pantalla d'inici (veure {@link IniciResource}).
 *
 * Recurs sense taula: el llistat surt sempre buit i el detall no existeix. Tota la informació es
 * genera als informes {@link IniciResource#REPORT_INDICADORS}, {@link IniciResource#REPORT_LLISTES} (quines
 * llistes té el rol) i {@link IniciResource#REPORT_LLISTA} (una llista).
 *
 * Cada targeta es compta sobre el servei del seu llistat de destinació ({@link VisibilitatRecurs}), amb les
 * mateixes regles de visibilitat. Els recomptes d'un mateix llistat formen un grup: cada grup va en una
 * transacció pròpia i, si falla, només les seves targetes surten amb error. Les llistes, igual, una per una.
 *
 * <ul>
 *   <li><b>Caché</b> ({@link CacheInici}): el resultat de cada grup de targetes i de cada llista es guarda per a
 *   l'usuari i el context durant {@link PropertyConfig#INICI_CACHE_SEGONS} segons
 *   ({@link PropertyConfig#INICI_CACHE_EXPEDIENTS_TANCAR_SEGONS} la llista d'expedients per tancar, la més costosa).
 *   Els errors no es guarden; el temps esgotat, sí (amb la caducitat general), perquè recarregar la pantalla no
 *   torni a llançar la mateixa consulta. No es guarden les anotacions pendents (ja tenen la caché del comptador del
 *   menú) ni la memòria i el disc del monitor del sistema (són valors del moment).</li>
 *   <li><b>Límit de temps</b> ({@link ConsultesInici}): una consulta que el supera es cancel·la i les targetes del
 *   grup o la llista surten amb l'avís de temps esgotat.</li>
 * </ul>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class IniciResourceServiceImpl extends BaseReadonlyResourceService<IniciResource, Long, IniciResourceEntity> implements IniciResourceService {

	private static final int CONFIG_DIES_AVIS_EXPEDIENT_ANTIC_DEFAULT = 90;
	private static final int TASCA_PREAVIS_DATA_LIMIT_DEFAULT = 3;
	private static final int DIES_EXPEDIENTS_TANCATS_RECENTS = 7;
	private static final int HORES_MONITOR = 24;
	/** Caducitat per defecte de la caché de targetes i llistes, si la propietat no hi és. */
	static final int CACHE_SEGONS_DEFECTE = 120;
	/** Caducitat per defecte de la caché de la llista d'expedients per tancar, si la propietat no hi és. */
	static final int CACHE_EXPEDIENTS_TANCAR_SEGONS_DEFECTE = 900;

	private final VisibilitatRecurs<ExpedientResourceEntity> recursExpedients;
	private final VisibilitatRecurs<AlertaResourceEntity> recursAlertes;
	private final VisibilitatRecurs<ExpedientTascaResourceEntity> recursTasques;
	private final VisibilitatRecurs<MetaExpedientResourceEntity> recursProcediments;
	private final VisibilitatRecurs<IntegracioResourceEntity> recursIntegracions;
	private final VisibilitatRecurs<ExcepcioLogResourceEntity> recursExcepcions;
	private final ExpedientsPerTancarRecurs expedientsPerTancar;
	private final ConsultesInici consultes;
	private final CacheInici cache;
	private final PerspectivesLotHelper perspectivesLotHelper;
	private final IntegracioHelper integracioHelper;
	private final ExpedientPeticioService expedientPeticioService;
	private final OrganGestorRepository organGestorRepository;
	private final ConfigHelper configHelper;
	private final MetaExpedientHelper metaExpedientHelper;

	@PostConstruct
	public void init() {
		register(IniciResource.REPORT_INDICADORS, new IndicadorsReportGenerator());
		register(IniciResource.REPORT_LLISTES, new LlistesReportGenerator());
		register(IniciResource.REPORT_LLISTA, new LlistaReportGenerator());
	}

	/**
	 * Sense transacció: cada grup de recomptes i cada llista obre la seva (VisibilitatRecurs#ambVisibles és
	 * transaccional). Així, un grup que falla no marca per a rollback una transacció compartida, que faria fallar
	 * tota la pantalla.
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

	/** Quines llistes resum té el rol actiu, en l'ordre en què s'han de mostrar (només els codis, sense consultes). */
	private class LlistesReportGenerator implements ReportGenerator<IniciResourceEntity, Serializable, IniciResource.Llista> {

		@Override
		public List<IniciResource.Llista> generateData(
				String code,
				IniciResourceEntity entity,
				Serializable params) throws ReportGenerationException {
			return calcularLlistes();
		}

		@Override
		public void onChange(Serializable id, Serializable previous, String fieldName, Object fieldValue, Map<String, AnswerValue> answers, String[] previousFieldNames, Serializable target) {}
	}

	/** Una llista resum del rol actiu amb les seves primeres files. */
	private class LlistaReportGenerator implements ReportGenerator<IniciResourceEntity, IniciResource.LlistaForm, IniciResource.Llista> {

		@Override
		public List<IniciResource.Llista> generateData(
				String code,
				IniciResourceEntity entity,
				IniciResource.LlistaForm params) throws ReportGenerationException {
			return calcularLlista(params != null ? params.getCodi() : null);
		}

		@Override
		public void onChange(Serializable id, IniciResource.LlistaForm previous, String fieldName, Object fieldValue, Map<String, AnswerValue> answers, String[] previousFieldNames, IniciResource.LlistaForm target) {}
	}

	private List<IniciResource.Indicador> calcularIndicadors() {
		String rol = configHelper.getRolActual();
		boolean revisioActiva = metaExpedientHelper.isRevisioActiva();
		List<IndicadorInici> cataleg = senseEntitat(rol)
				? new ArrayList<IndicadorInici>()
				: IndicadorInici.targetes(rol, revisioActiva);
		if (cataleg.isEmpty()) {
			return new ArrayList<>();
		}
		String usuariCodi = SecurityContextHolder.getContext().getAuthentication().getName();
		Date ara = new Date();
		int diesAntic = configHelper.getAsInt(PropertyConfig.CONFIG_DIES_AVIS_EXPEDIENT_ANTIC, CONFIG_DIES_AVIS_EXPEDIENT_ANTIC_DEFAULT);
		int diesSenseActivitat = diesTascaSenseActivitat();
		int diesPreavis = diesPreavisDataLimit();
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
		recull.comptar("EXPEDIENTS", ExpedientResourceEntity.class, expedients, recursExpedients, null);

		// Consulta «Alertes d'expedients»: les no llegides són les de la icona del llistat d'expedients
		Map<IndicadorInici, Specification<AlertaResourceEntity>> alertes = new EnumMap<>(IndicadorInici.class);
		alertes.put(IndicadorInici.ALERTES_NO_LLEGIDES, CriterisIndicadorsInici.alertesNoLlegides());
		recull.comptar("ALERTES", AlertaResourceEntity.class, alertes, recursAlertes, new String[] { AlertaResource.NAMED_QUERY_CONSULTA });

		// Llistat d'anotacions: el mateix comptador que el menú, amb la seva pròpia caché (no es guarda aquí: la
		// targeta ha de coincidir amb el comptador del menú)
		recull.valor(IndicadorInici.ANOTACIONS_PENDENTS, this::comptarAnotacionsPendents);

		// «Les meves tasques»: la mateixa named query que el llistat (responsable, observador o delegat). També les
		// d'expedients tancats, que el llistat no distingeix i marca igual (taronja i vermell).
		Specification<ExpedientTascaResourceEntity> tasquesUsuari = CriterisIndicadorsInici.tasquesExpedientNoEsborrat();
		Specification<ExpedientTascaResourceEntity> tasquesUsuariPendents = CriterisIndicadorsInici.tasquesPendents();
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
		recull.comptar("TASQUES_USUARI", ExpedientTascaResourceEntity.class, tasques, recursTasques, new String[] { CriterisIndicadorsInici.NAMED_QUERY_TASQUES_USUARI });

		// Consulta de tasques assignades de l'entitat (administrador): totes, sigui qui sigui el responsable
		Map<IndicadorInici, Specification<ExpedientTascaResourceEntity>> tasquesEntitat = new EnumMap<>(IndicadorInici.class);
		tasquesEntitat.put(IndicadorInici.TASQUES_SENSE_ACTIVITAT, CriterisIndicadorsInici.tasquesSenseActivitat(
				LocalDateTime.now().minusDays(diesSenseActivitat)));
		recull.comptar("TASQUES_ENTITAT", ExpedientTascaResourceEntity.class, tasquesEntitat, recursTasques, null);

		// Llistat de procediments (els d'estat de revisió només hi són al catàleg amb la revisió activa)
		Map<IndicadorInici, Specification<MetaExpedientResourceEntity>> procediments = new EnumMap<>(IndicadorInici.class);
		procediments.put(IndicadorInici.PROCEDIMENTS_PENDENTS_REVISIO, CriterisIndicadorsInici.procedimentsEnEstatRevisio(MetaExpedientRevisioEstatEnumDto.PENDENT));
		procediments.put(IndicadorInici.PROCEDIMENTS_EN_DISSENY, CriterisIndicadorsInici.procedimentsEnEstatRevisio(MetaExpedientRevisioEstatEnumDto.DISSENY));
		procediments.put(IndicadorInici.PROCEDIMENTS_REBUTJATS, CriterisIndicadorsInici.procedimentsEnEstatRevisio(MetaExpedientRevisioEstatEnumDto.REBUTJAT));
		procediments.put(IndicadorInici.PROCEDIMENTS_REVISATS, CriterisIndicadorsInici.procedimentsEnEstatRevisio(MetaExpedientRevisioEstatEnumDto.REVISAT));
		procediments.put(IndicadorInici.PROCEDIMENTS_ACTIUS, CriterisIndicadorsInici.procedimentsActius(revisioActiva));
		procediments.put(IndicadorInici.PROCEDIMENTS_INACTIUS, CriterisIndicadorsInici.procedimentsInactius(revisioActiva));
		recull.comptar("PROCEDIMENTS", MetaExpedientResourceEntity.class, procediments, recursProcediments, null);

		// Monitor d'integracions, registre d'excepcions i monitor del sistema (del node que respon; aquest, sense
		// caché: són valors del moment i no fan consultes)
		Map<IndicadorInici, Specification<IntegracioResourceEntity>> integracions = new EnumMap<>(IndicadorInici.class);
		integracions.put(IndicadorInici.INTEGRACIONS_ERROR, CriterisIndicadorsInici.integracionsErrorDesDe(fa24Hores));
		recull.comptar("INTEGRACIONS", IntegracioResourceEntity.class, integracions, recursIntegracions, null);
		Map<IndicadorInici, Specification<ExcepcioLogResourceEntity>> excepcions = new EnumMap<>(IndicadorInici.class);
		excepcions.put(IndicadorInici.EXCEPCIONS, CriterisIndicadorsInici.excepcionsDesDe(fa24Hores));
		recull.comptar("EXCEPCIONS", ExcepcioLogResourceEntity.class, excepcions, recursExcepcions, null);
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

	/**
	 * Usuari sense cap entitat accessible (sense entitat actual): els llistats de destinació no li poden mostrar res
	 * (la majoria fallen), així que la pantalla d'inici no calcula targetes ni llistes. El superusuari no treballa
	 * amb entitat.
	 */
	private boolean senseEntitat(String rol) {
		return !BaseConfig.ROLE_SUPER.equals(rol) && configHelper.getEntitatActualCodi() == null;
	}

	private int diesPreavisDataLimit() {
		return configHelper.getAsInt(PropertyConfig.TASCA_PREAVIS_DATA_LIMIT, TASCA_PREAVIS_DATA_LIMIT_DEFAULT);
	}

	private int diesTascaSenseActivitat() {
		return configHelper.getAsInt(PropertyConfig.CONFIG_DIES_TASCA_SENSE_ACTIVITAT, CriterisIndicadorsInici.DIES_TASCA_SENSE_ACTIVITAT_DEFECTE);
	}

	// --- Llistes resum ---------------------------------------------------------------------------------

	/** Llistes del catàleg del rol, només amb el codi: el front demana després cada una (REPORT_LLISTA). */
	private List<IniciResource.Llista> calcularLlistes() {
		List<IniciResource.Llista> resultat = new ArrayList<>();
		for (LlistaInici codi: catalegLlistes()) {
			IniciResource.Llista llista = new IniciResource.Llista();
			llista.setCodi(codi.name());
			resultat.add(llista);
		}
		return resultat;
	}

	/**
	 * Una llista del catàleg del rol, amb les seves files (de la caché si n'hi ha). Si el codi no és una llista del
	 * rol actiu (p. ex. l'usuari ha canviat de rol mentre el front la demanava) no torna res.
	 */
	private List<IniciResource.Llista> calcularLlista(String codi) {
		LlistaInici llista = catalegLlistes().stream()
				.filter(delRol -> delRol.name().equals(codi))
				.findFirst()
				.orElse(null);
		if (llista == null) {
			return new ArrayList<>();
		}
		Duration caducitat = llista == LlistaInici.EXPEDIENTS_LLESTOS_TANCAR
				? caducitat(PropertyConfig.INICI_CACHE_EXPEDIENTS_TANCAR_SEGONS, CACHE_EXPEDIENTS_TANCAR_SEGONS_DEFECTE)
				: caducitatGeneral();
		IniciResource.Llista resultat = cache.obtenir(
				clau("LLISTA." + llista.name()),
				caducitat,
				() -> consultarLlista(llista),
				calculada -> caducitatResultat(calculada.isError(), calculada.isTempsEsgotat()));
		return Collections.singletonList(resultat);
	}

	private List<LlistaInici> catalegLlistes() {
		String rol = configHelper.getRolActual();
		return senseEntitat(rol)
				? new ArrayList<LlistaInici>()
				: LlistaInici.llistes(rol, metaExpedientHelper.isRevisioActiva());
	}

	/**
	 * Consulta d'una llista sobre el servei del seu llistat de destinació, en una transacció pròpia i amb les
	 * mateixes regles de visibilitat. Si falla, la llista surt amb error (i amb temps esgotat, si n'és la causa).
	 */
	private IniciResource.Llista consultarLlista(LlistaInici codi) {
		IniciResource.Llista llista = new IniciResource.Llista();
		llista.setCodi(codi.name());
		try {
			llista.setFiles(files(codi, llista));
		} catch (Exception ex) {
			llista.setFiles(new ArrayList<>());
			llista.setError(true);
			llista.setTempsEsgotat(registrarError("la llista " + codi, ex));
		}
		return llista;
	}

	private List<Map<String, Object>> files(LlistaInici codi, IniciResource.Llista llista) {
		int maxim = LlistaInici.MAXIM_FILES;
		switch (codi) {
			case TASQUES_PER_DATA_LIMIT:
				return tasquesPerDataLimit(maxim);
			case EXPEDIENTS_LLESTOS_TANCAR:
				return expedientsPerTancar.mesAnticsPerTancar(maxim, FilesLlistesInici::expedientPerTancar);
			case TASQUES_SENSE_ACTIVITAT:
				int diesSenseActivitat = diesTascaSenseActivitat();
				llista.setDies(diesSenseActivitat);
				return tasquesSenseActivitat(diesSenseActivitat, maxim);
			case DARRERES_INTEGRACIONS:
				// Només per data (sense desempat per id): així les llegeix en ordre de l'índex de DATA (1.1.0_DDL_3)
				// sense haver d'ordenar tota la taula
				return recursIntegracions.ambVisibles(null, visibles -> files(
						consultes.primeres(
								IntegracioResourceEntity.class,
								visibles,
								(root, cb) -> Collections.singletonList(cb.desc(root.get("data"))),
								maxim),
						integracio -> FilesLlistesInici.integracio(integracio, nomIntegracio(integracio.getCodi()))));
			case DARRERES_EXCEPCIONS:
				// Les excepcions repetides s'agrupen en una fila: l'ordre és el de la darrera ocurrència
				int minutsAgrupacio = configHelper.getAsInt(PropertyConfig.EXCEPCIONS_DEDUP_MINUTS, ExcepcioLogHelper.DEDUP_MINUTS_DEFECTE);
				llista.setMinuts(minutsAgrupacio > 0 ? minutsAgrupacio : null);
				return recursExcepcions.ambVisibles(null, visibles -> files(
						consultes.primeres(
								ExcepcioLogResourceEntity.class,
								visibles,
								(root, cb) -> Arrays.asList(
										cb.desc(cb.coalesce(root.<Date>get("dataDarrera"), root.<Date>get("data"))),
										cb.desc(root.get("id"))),
								maxim),
						FilesLlistesInici::excepcio));
			case DARRERS_COMENTARIS:
				return darrersComentaris(maxim);
			case PROCEDIMENTS_PENDENTS_REVISIO:
				return procedimentsPendentsRevisio(maxim);
			case PROCEDIMENTS_REBUTJATS:
				return procedimentsRebutjats(maxim);
			default:
				throw new IllegalStateException("Llista de la pantalla d'inici sense consulta: " + codi);
		}
	}

	/**
	 * Tasques pendents de «Les meves tasques» (la mateixa named query i el mateix criteri que les targetes), de
	 * la data límit més antiga a la més recent: primer les vençudes; les que no en tenen, al final.
	 */
	private List<Map<String, Object>> tasquesPerDataLimit(int maxim) {
		LocalDate avui = LocalDate.now();
		Date dema = toDate(avui.plusDays(1));
		Date fiPreavisExclos = toDate(avui.plusDays(diesPreavisDataLimit() + 1L));
		return recursTasques.ambVisibles(
				new String[] { CriterisIndicadorsInici.NAMED_QUERY_TASQUES_USUARI },
				visibles -> files(
						consultes.primeres(
								ExpedientTascaResourceEntity.class,
								visibles.and(CriterisIndicadorsInici.tasquesPendents()),
								(root, cb) -> Arrays.asList(
										cb.asc(cb.selectCase().when(cb.isNull(root.get("dataLimit")), 1).otherwise(0)),
										cb.asc(root.get("dataLimit")),
										cb.asc(root.get("id"))),
								maxim,
								"expedient", "metaExpedientTasca"),
						tasca -> FilesLlistesInici.tascaPerDataLimit(tasca, dema, fiPreavisExclos)));
	}

	/**
	 * Tasques pendents de tota l'entitat sense activitat (el mateix criteri que la targeta), de la que fa més
	 * temps que no en té a la que menys, amb els responsables.
	 */
	private List<Map<String, Object>> tasquesSenseActivitat(int dies, int maxim) {
		Specification<ExpedientTascaResourceEntity> criteri = CriterisIndicadorsInici.tasquesSenseActivitat(
				LocalDateTime.now().minusDays(dies));
		return recursTasques.ambVisibles(null, visibles -> {
			Map<Long, LocalDateTime> darreraActivitat = consultes.tasquesMesInactives(visibles.and(criteri), maxim);
			Map<Long, ExpedientTascaResourceEntity> tasques = consultes.tasquesAmbResponsables(darreraActivitat.keySet());
			List<Map<String, Object>> files = new ArrayList<>();
			darreraActivitat.forEach((id, activitat) -> {
				ExpedientTascaResourceEntity tasca = tasques.get(id);
				if (tasca != null) {
					files.add(FilesLlistesInici.tascaSenseActivitat(tasca, activitat));
				}
			});
			return files;
		});
	}

	/** Darrers comentaris dels procediments que l'usuari veu al llistat de procediments. */
	private List<Map<String, Object>> darrersComentaris(int maxim) {
		return recursProcediments.ambVisibles(null, visibles -> {
			List<MetaExpedientComentariResourceEntity> comentaris = consultes.darrersComentaris(visibles, maxim);
			Map<String, UsuariResourceEntity> autors = perspectivesLotHelper.findUsuaris(comentaris.stream()
					.map(MetaExpedientComentariResourceEntity::getCreatedBy)
					.filter(Objects::nonNull)
					.collect(Collectors.toSet()));
			return files(comentaris, comentari -> FilesLlistesInici.comentari(comentari, autors.get(comentari.getCreatedBy())));
		});
	}

	/** Procediments pendents de revisió, del que fa més temps que espera (darrera modificació) al que menys. */
	private List<Map<String, Object>> procedimentsPendentsRevisio(int maxim) {
		return recursProcediments.ambVisibles(null, visibles -> files(
				consultes.primeres(
						MetaExpedientResourceEntity.class,
						visibles.and(CriterisIndicadorsInici.procedimentsEnEstatRevisio(MetaExpedientRevisioEstatEnumDto.PENDENT)),
						(root, cb) -> Arrays.asList(cb.asc(darreraModificacio(root, cb)), cb.asc(root.get("id"))),
						maxim,
						"organGestor"),
				FilesLlistesInici::procedimentPendentRevisio));
	}

	/** Procediments rebutjats, del rebutjat més recentment (darrera modificació) al més antic, amb el darrer comentari. */
	private List<Map<String, Object>> procedimentsRebutjats(int maxim) {
		return recursProcediments.ambVisibles(null, visibles -> {
			List<MetaExpedientResourceEntity> procediments = consultes.primeres(
					MetaExpedientResourceEntity.class,
					visibles.and(CriterisIndicadorsInici.procedimentsEnEstatRevisio(MetaExpedientRevisioEstatEnumDto.REBUTJAT)),
					(root, cb) -> Arrays.asList(cb.desc(darreraModificacio(root, cb)), cb.desc(root.get("id"))),
					maxim);
			Map<Long, MetaExpedientComentariResourceEntity> comentaris = consultes.darrerComentariPerProcediment(
					PerspectivesLotHelper.ids(procediments));
			Map<String, UsuariResourceEntity> autors = perspectivesLotHelper.findUsuaris(comentaris.values().stream()
					.map(MetaExpedientComentariResourceEntity::getCreatedBy)
					.filter(Objects::nonNull)
					.collect(Collectors.toSet()));
			return files(procediments, procediment -> {
				MetaExpedientComentariResourceEntity comentari = comentaris.get(procediment.getId());
				return FilesLlistesInici.procedimentRebutjat(
						procediment,
						comentari,
						comentari != null ? autors.get(comentari.getCreatedBy()) : null);
			});
		});
	}

	/** Nom de la integració, el mateix de les pestanyes del monitor d'integracions (sense traducció, el codi). */
	private String nomIntegracio(IntegracioCodiEnum codi) {
		if (codi == null) {
			return null;
		}
		String nom = integracioHelper.novaIntegracio(codi.name()).getNom();
		return nom == null || nom.startsWith("???") ? codi.name() : nom;
	}

	private static Expression<LocalDateTime> darreraModificacio(Root<MetaExpedientResourceEntity> root, CriteriaBuilder cb) {
		return cb.coalesce(root.<LocalDateTime>get("lastModifiedDate"), root.<LocalDateTime>get("createdDate"));
	}

	private static <E> List<Map<String, Object>> files(List<E> elements, Function<E, Map<String, Object>> fila) {
		return elements.stream().map(fila).collect(Collectors.toList());
	}

	/** Clau de la caché: usuari i context actuals (els mateixos que fan servir les consultes) i element. */
	private CacheInici.Clau clau(String element) {
		return new CacheInici.Clau(
				SecurityContextHolder.getContext().getAuthentication().getName(),
				configHelper.getEntitatActualCodi(),
				configHelper.getRolActual(),
				configHelper.getOrganActualCodi(),
				LocaleContextHolder.getLocale().getLanguage(),
				element);
	}

	/** Caducitat de la caché de targetes i llistes (es llegeix a cada petició: es pot canviar sense desplegar). */
	private Duration caducitatGeneral() {
		return caducitat(PropertyConfig.INICI_CACHE_SEGONS, CACHE_SEGONS_DEFECTE);
	}

	private Duration caducitat(String propietat, int defecte) {
		return Duration.ofSeconds(Math.max(configHelper.getAsInt(propietat, defecte), 0));
	}

	/**
	 * Temps que es guarda un resultat calculat: els errors no es guarden (la petició següent ho torna a provar); el
	 * temps esgotat, amb la caducitat general, perquè recarregar la pantalla no torni a llançar la mateixa consulta;
	 * la resta, amb la caducitat de l'element (null).
	 */
	private Duration caducitatResultat(boolean error, boolean tempsEsgotat) {
		if (tempsEsgotat) {
			return caducitatGeneral();
		}
		return error ? Duration.ZERO : null;
	}

	/**
	 * Registra l'error d'una part de la pantalla.
	 *
	 * @return cert si la causa és el límit de temps de les consultes.
	 */
	private static boolean registrarError(String part, Exception ex) {
		if (ConsultesInici.esTempsEsgotat(ex)) {
			log.warn("Temps esgotat calculant " + part + " de la pantalla d'inici: " + ex.getMessage());
			return true;
		}
		log.error("Error calculant " + part + " de la pantalla d'inici", ex);
		return false;
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
	private class Recull {

		private final List<IndicadorInici> cataleg;
		private final Map<IndicadorInici, Long> valors = new EnumMap<>(IndicadorInici.class);
		private final Map<IndicadorInici, Long> totals = new EnumMap<>(IndicadorInici.class);
		private final Map<IndicadorInici, Integer> dies = new EnumMap<>(IndicadorInici.class);
		private final Set<IndicadorInici> errors = EnumSet.noneOf(IndicadorInici.class);
		private final Set<IndicadorInici> tempsEsgotats = EnumSet.noneOf(IndicadorInici.class);

		private Recull(List<IndicadorInici> cataleg) {
			this.cataleg = cataleg;
		}

		/**
		 * Recomptes d'un grup (un llistat de destinació) amb la caché de la pantalla d'inici.
		 *
		 * @param grup nom del grup a la clau de la caché.
		 */
		private <E> void comptar(
				String grup,
				Class<E> classe,
				Map<IndicadorInici, Specification<E>> criteris,
				VisibilitatRecurs<E> recurs,
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
			RecomptesGrup recomptes = cache.obtenir(
					clau("INDICADORS." + grup),
					caducitatGeneral(),
					() -> consultarGrup(grup, classe, delCataleg, recurs, namedQueries),
					calculat -> caducitatResultat(calculat.error, calculat.tempsEsgotat));
			if (recomptes.error) {
				delCataleg.keySet().forEach(codi -> {
					errors.add(IndicadorInici.valueOf(codi));
					if (recomptes.tempsEsgotat) {
						tempsEsgotats.add(IndicadorInici.valueOf(codi));
					}
				});
			} else {
				recomptes.valors.forEach((codi, valor) -> valors.put(IndicadorInici.valueOf(codi), valor));
			}
		}

		/** Tots els recomptes del grup en una transacció, amb una sola especificació de visibilitat. */
		private <E> RecomptesGrup consultarGrup(
				String grup,
				Class<E> classe,
				Map<String, Specification<E>> criteris,
				VisibilitatRecurs<E> recurs,
				String[] namedQueries) {
			try {
				Map<String, Long> recomptes = recurs.ambVisibles(namedQueries, visibles -> {
					Map<String, Long> perCodi = new LinkedHashMap<>();
					criteris.forEach((codi, criteri) -> perCodi.put(codi, consultes.comptar(classe, visibles.and(criteri))));
					return perCodi;
				});
				return new RecomptesGrup(Collections.unmodifiableMap(recomptes), false, false);
			} catch (Exception ex) {
				boolean tempsEsgotat = registrarError("els indicadors " + grup + " " + criteris.keySet(), ex);
				return new RecomptesGrup(Collections.<String, Long>emptyMap(), true, tempsEsgotat);
			}
		}

		/** Una targeta que no surt d'un llistat (comptador del menú, monitor del sistema): sense caché. */
		private void valor(IndicadorInici indicador, Supplier<Long> calcul) {
			if (!cataleg.contains(indicador)) {
				return;
			}
			try {
				valors.put(indicador, calcul.get());
			} catch (Exception ex) {
				errors.add(indicador);
				if (registrarError("l'indicador " + indicador, ex)) {
					tempsEsgotats.add(indicador);
				}
			}
		}

		private List<IniciResource.Indicador> resultat() {
			List<IniciResource.Indicador> resultat = new ArrayList<>();
			for (IndicadorInici indicador: cataleg) {
				IniciResource.Indicador targeta = new IniciResource.Indicador();
				targeta.setCodi(indicador.name());
				targeta.setError(errors.contains(indicador));
				targeta.setTempsEsgotat(tempsEsgotats.contains(indicador));
				targeta.setValor(targeta.isError() ? null : valors.get(indicador));
				targeta.setTotal(totals.get(indicador));
				targeta.setDies(dies.get(indicador));
				resultat.add(targeta);
			}
			return resultat;
		}
	}

	/** Resultat d'un grup de recomptes, tal com es guarda a la caché (no es modifica un cop calculat). */
	private static class RecomptesGrup {

		private final Map<String, Long> valors;
		private final boolean error;
		private final boolean tempsEsgotat;

		private RecomptesGrup(Map<String, Long> valors, boolean error, boolean tempsEsgotat) {
			this.valors = valors;
			this.error = error;
			this.tempsEsgotat = tempsEsgotat;
		}
	}

}
