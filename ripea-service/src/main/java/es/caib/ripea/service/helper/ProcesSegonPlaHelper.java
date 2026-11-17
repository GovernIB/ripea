package es.caib.ripea.service.helper;

import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import es.caib.ripea.persistence.entity.UsuariEntity;
import es.caib.ripea.persistence.repository.UsuariRepository;
import es.caib.ripea.service.intf.dto.ElementTipusEnumDto;
import es.caib.ripea.service.intf.dto.EntitatDto;
import es.caib.ripea.service.intf.dto.ProcesSegonPlaDto;
import es.caib.ripea.service.intf.dto.ProcesSegonPlaTipusEnumDto;
import es.caib.ripea.service.intf.exception.ValidationException;
import es.caib.ripea.service.intf.model.sse.ProcesSegonPlaEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * Execució de processos llargs en segon pla amb seguiment en temps real des de la interfície REACT (p.ex. el
 * tancament d'un expedient, que pot haver de firmar en servidor i desar a l'Arxiu molts documents).
 *
 * Funcionament:
 *  1. L'acció que llança el procés fa les validacions síncrones (perquè els errors arribin a la mateixa modal) i
 *     crida {@link #iniciar}, que bloqueja l'element i retorna de seguida l'estat inicial amb l'operacioId.
 *  2. La tasca s'executa en un fil propi, amb el context de l'usuari que l'ha llançada. La lògica de negoci
 *     informa del progrés amb {@link ProcesSegonPlaContext} (passos, fases i avisos).
 *  3. L'usuari consulta el progrés periòdicament amb l'operacioId ({@link #consultar}).
 *  4. Mentre el procés és en curs, ningú més pot llançar cap altre procés sobre l'element ni modificar-lo
 *     ({@link #comprovarNoBloquejat}, {@link #isBloquejatPerAltri}); el propi fil del procés n'està exempt.
 *  5. En començar i en acabar s'envia un event SSE perquè els clients que veuen l'element el refresquin.
 *
 * Tot l'estat és en memòria: només val amb un sol node (o amb sessions enganxades a un node). Si l'aplicació es
 * reinicia, els processos en curs es perden i el bloqueig desapareix amb ells. Els processos acabats es
 * conserven una estona perquè l'usuari en pugui veure el resultat encara que hagi tancat el diàleg.
 *
 * No fa servir @Async: els processos van en un pool propi, de manera que no competeixen amb la resta de tasques
 * asíncrones de l'aplicació i no s'altera la configuració de l'executor per defecte (que es desactivaria si es
 * declarés un altre bean Executor). A més, així el helper no queda embolcallat per cap proxy d'@Async, que dona
 * problemes amb les dependències circulars entre helpers.
 */
@Slf4j
@Component
public class ProcesSegonPlaHelper {

	/** Processos que es poden executar alhora. La resta esperen (EN_CURS, sense passos) que quedi un fil lliure. */
	private static final int MAX_PROCESSOS_SIMULTANIS = 10;
	/** Temps que es conserva l'estat d'un procés acabat perquè el pugui consultar l'usuari que l'ha llançat. */
	private static final long RETENCIO_FINALITZATS_MS = 60L * 60L * 1000L;

	@Autowired private UsuariRepository usuariRepository;
	@Autowired private MessageHelper messageHelper;
	@Autowired private ExcepcioLogHelper excepcioLogHelper;
	@Autowired private EventHelper eventHelper;
	@Autowired private ConfigHelper configHelper;

	/** Tots els processos (en curs i acabats recentment), per operacioId. */
	private final ConcurrentMap<String, ProcesSegonPla> processos = new ConcurrentHashMap<>();
	/** Processos en curs, per element: és el bloqueig. */
	private final ConcurrentMap<String, ProcesSegonPla> enCurs = new ConcurrentHashMap<>();

	private ThreadPoolTaskExecutor executor;

	/** Tasca que executa el procés. Les excepcions marquen el procés com a ERROR. */
	@FunctionalInterface
	public interface Tasca {
		void executar() throws Exception;
	}

	@PostConstruct
	public void init() {
		executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(MAX_PROCESSOS_SIMULTANIS);
		executor.setMaxPoolSize(MAX_PROCESSOS_SIMULTANIS);
		executor.setAllowCoreThreadTimeOut(true);
		executor.setKeepAliveSeconds(60);
		executor.setThreadNamePrefix("ripea-proces-");
		executor.initialize();
	}

	@PreDestroy
	public void destroy() {
		if (executor != null) {
			executor.shutdown();
		}
	}

	/**
	 * Registra el procés, bloqueja l'element i programa l'execució de la tasca en segon pla.
	 *
	 * Si hi ha una transacció activa, la tasca no comença fins que s'ha confirmat (així veu tot el que hagi
	 * escrit qui la llança); si es desfà, el procés no s'executa, es desbloqueja l'element i queda com a ERROR.
	 *
	 * @return l'estat inicial del procés, amb l'operacioId per consultar-ne el progrés.
	 * @throws ValidationException si l'element ja té un procés en curs.
	 */
	public ProcesSegonPlaDto iniciar(
			ProcesSegonPlaTipusEnumDto tipus,
			ElementTipusEnumDto elementTipus,
			Long elementId,
			Tasca tasca) {
		purgarFinalitzats();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String usuariCodi = auth.getName();
		String usuariNom = usuariRepository.findById(usuariCodi).map(UsuariEntity::getNom).orElse(usuariCodi);
		ProcesSegonPla proces = new ProcesSegonPla(
				UUID.randomUUID().toString(),
				tipus,
				elementTipus,
				elementId,
				usuariCodi,
				usuariNom);
		ProcesSegonPla existent = enCurs.putIfAbsent(clau(elementTipus, elementId), proces);
		if (existent != null) {
			throw excepcioBloqueig(existent);
		}
		processos.put(proces.getOperacioId(), proces);
		log.info("Procés en segon pla registrat (operacioId={}, tipus={}, element={}#{}, usuari={})",
				proces.getOperacioId(), tipus, elementTipus, elementId, usuariCodi);

		ContextFil context = ContextFil.capturar(auth, configHelper.getRolActual());
		if (TransactionSynchronizationManager.isSynchronizationActive()) {
			TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
				// Amb JTA aquesta callback pot arribar més d'una vegada (veure TransactionAfterCommitUtils): només
				// compta la primera.
				private final AtomicBoolean decidit = new AtomicBoolean(false);
				@Override
				public void afterCompletion(int status) {
					if (decidit.compareAndSet(false, true)) {
						if (status == STATUS_COMMITTED) {
							llancar(proces, context, tasca);
						} else {
							finalitzar(proces, messageHelper.getMessage("proces.segon.pla.no.iniciat"));
						}
					}
				}
			});
		} else {
			llancar(proces, context, tasca);
		}
		return proces.copia(true);
	}

	/**
	 * Estat complet (amb els passos) d'un procés. Només el pot veure l'usuari que l'ha llançat.
	 *
	 * @return l'estat, o null si no existeix, ja s'ha descartat o és d'un altre usuari.
	 */
	public ProcesSegonPlaDto consultar(String operacioId) {
		purgarFinalitzats();
		ProcesSegonPla proces = operacioId != null ? processos.get(operacioId) : null;
		if (proces == null) {
			return null;
		}
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null || !proces.getUsuariCodi().equals(auth.getName())) {
			return null;
		}
		return proces.copia(true);
	}

	/** Resum (sense passos) del procés en curs sobre l'element, o null si no n'hi ha cap. */
	public ProcesSegonPlaDto getEnCurs(ElementTipusEnumDto elementTipus, Long elementId) {
		ProcesSegonPla proces = getProcesEnCurs(elementTipus, elementId);
		return proces != null ? proces.copia(false) : null;
	}

	/**
	 * Indica si l'element té un procés en curs que no és el que s'executa en el fil actual, és a dir, si des
	 * d'aquí no s'hi poden fer canvis.
	 */
	public boolean isBloquejatPerAltri(ElementTipusEnumDto elementTipus, Long elementId) {
		return getProcesBloquejant(elementTipus, elementId) != null;
	}

	/**
	 * Llança una ValidationException (amb el motiu i l'usuari que ha llançat el procés) si l'element té un procés
	 * en curs que no és el que s'executa en el fil actual.
	 */
	public void comprovarNoBloquejat(ElementTipusEnumDto elementTipus, Long elementId) {
		ProcesSegonPla proces = getProcesBloquejant(elementTipus, elementId);
		if (proces != null) {
			throw excepcioBloqueig(proces);
		}
	}

	private ProcesSegonPla getProcesBloquejant(ElementTipusEnumDto elementTipus, Long elementId) {
		ProcesSegonPla proces = getProcesEnCurs(elementTipus, elementId);
		if (proces == null || proces.getOperacioId().equals(ProcesSegonPlaContext.getOperacioActual())) {
			return null;
		}
		return proces;
	}

	private ProcesSegonPla getProcesEnCurs(ElementTipusEnumDto elementTipus, Long elementId) {
		if (elementTipus == null || elementId == null) {
			return null;
		}
		return enCurs.get(clau(elementTipus, elementId));
	}

	private void llancar(ProcesSegonPla proces, ContextFil context, Tasca tasca) {
		try {
			executor.execute(() -> executar(proces, context, tasca));
		} catch (RejectedExecutionException ex) {
			log.error("No s'ha pogut llançar el procés en segon pla (operacioId=" + proces.getOperacioId() + ")", ex);
			finalitzar(proces, ProcesSegonPlaContext.missatgeError(ex));
			return;
		}
		notificar(proces);
	}

	private void executar(ProcesSegonPla proces, ContextFil context, Tasca tasca) {
		String errorMsg = null;
		context.establir();
		ProcesSegonPlaContext.establir(proces);
		try {
			log.info("Inici del procés en segon pla (operacioId={}, tipus={}, element={}#{})",
					proces.getOperacioId(), proces.getTipus(), proces.getElementTipus(), proces.getElementId());
			tasca.executar();
		} catch (Throwable ex) {
			errorMsg = ex instanceof Exception ? messageHelper.getMissatgeError((Exception)ex) : String.valueOf(ex);
			log.error("Error en el procés en segon pla (operacioId=" + proces.getOperacioId() + ")", ex);
			try {
				excepcioLogHelper.addExcepcio(
						"/procesSegonPla/" + proces.getTipus() + "/" + proces.getElementId(),
						ex);
			} catch (Exception logEx) {
				log.error("No s'ha pogut registrar l'excepció del procés en segon pla (operacioId=" + proces.getOperacioId() + ")", logEx);
			}
		} finally {
			ProcesSegonPlaContext.netejar();
			context.netejar();
			finalitzar(proces, errorMsg);
			log.info("Fi del procés en segon pla (operacioId={}, estat={})", proces.getOperacioId(), proces.getEstat());
		}
	}

	/** Marca el procés com a acabat, desbloqueja l'element i ho notifica. */
	private void finalitzar(ProcesSegonPla proces, String errorMsg) {
		proces.finalitzar(errorMsg);
		enCurs.remove(clau(proces.getElementTipus(), proces.getElementId()), proces);
		notificar(proces);
	}

	private void notificar(ProcesSegonPla proces) {
		eventHelper.notifyProcesSegonPla(ProcesSegonPlaEvent.builder()
				.elementTipus(proces.getElementTipus())
				.elementId(proces.getElementId())
				.operacioId(proces.getOperacioId())
				.tipus(proces.getTipus())
				.estat(proces.getEstat())
				.build());
	}

	private ValidationException excepcioBloqueig(ProcesSegonPla proces) {
		return new ValidationException(messageHelper.getMessage(
				"proces.segon.pla.bloquejat." + proces.getTipus(),
				new Object[] { proces.getUsuariNom() }));
	}

	/** Descarta l'estat dels processos acabats fa més de RETENCIO_FINALITZATS_MS. */
	private void purgarFinalitzats() {
		long limit = System.currentTimeMillis() - RETENCIO_FINALITZATS_MS;
		Iterator<Map.Entry<String, ProcesSegonPla>> it = processos.entrySet().iterator();
		while (it.hasNext()) {
			ProcesSegonPla proces = it.next().getValue();
			if (proces.isFinalitzat() && proces.getDataFi() != null && proces.getDataFi().getTime() < limit) {
				it.remove();
			}
		}
	}

	private static String clau(ElementTipusEnumDto elementTipus, Long elementId) {
		return elementTipus + "#" + elementId;
	}

	/**
	 * Context de l'usuari que llança el procés (identitat, entitat, òrgan, rol i idioma), que s'ha de reproduir al
	 * fil del procés perquè la lògica de negoci (permisos ACL, configuració per entitat/òrgan, registre d'accions,
	 * missatges...) es comporti igual que si s'executés dins la petició.
	 *
	 * Es reutilitza l'objecte Authentication de la petició, amb totes les autoritats de la sessió: reconstruir-lo
	 * només amb el rol actual deixaria fora els permisos ACL donats a altres rols de l'usuari.
	 *
	 * Els fils del pool es reutilitzen: en acabar cal netejar-ho tot perquè no arribi al procés següent.
	 */
	private static final class ContextFil {
		private Authentication auth;
		private EntitatDto entitat;
		private String organCodi;
		private String rol;
		private Locale locale;

		static ContextFil capturar(Authentication auth, String rol) {
			ContextFil context = new ContextFil();
			context.auth = auth;
			context.entitat = ConfigHelper.getEntitat().get();
			context.organCodi = ConfigHelper.getOrganCodi().get();
			context.rol = rol;
			context.locale = LocaleContextHolder.getLocale();
			return context;
		}

		void establir() {
			SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
			securityContext.setAuthentication(auth);
			SecurityContextHolder.setContext(securityContext);
			ConfigHelper.setEntitat(entitat);
			ConfigHelper.setOrganCodi(organCodi);
			ConfigHelper.setRol(rol);
			LocaleContextHolder.setLocale(locale);
		}

		void netejar() {
			SecurityContextHolder.clearContext();
			ConfigHelper.setEntitat(null);
			ConfigHelper.setOrganCodi(null);
			ConfigHelper.setRol(null);
			LocaleContextHolder.resetLocaleContext();
		}
	}

}
