package es.caib.ripea.service.helper;

import java.net.ConnectException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Date;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonParseException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;

import es.caib.plugins.arxiu.caib.ArxiuCaibException;

/**
 * Bucle dels processos d'incorporació de documents en segon pla (certificats de remeses i justificants de registre),
 * que substitueixen l'execució des del navegador de /userajax/initCertificatsRemeses i /userajax/initJustificantsRegistre
 * per no dependre de la sessió de l'administrador.
 *
 * Són una càrrega de dades històriques que es fa per lots: cada procés s'inicia segons el cron de la seva propietat i
 * tracta els elements pendents dels més recents als més antics (id descendent), fins que no en queden o fins que
 * l'execució arriba a la durada màxima. Després de cada element es desa el seu id a la propietat del darrer id, i
 * l'execució següent continua pels elements d'id inferior: els elements tractats, amb èxit o amb error, no es tornen
 * a intentar. Buidar la propietat del darrer id fa que el procés torni a començar pels més recents.
 *
 * L'única excepció són els errors de disponibilitat de l'Arxiu o de NOTIB (veure {@link #isErrorDisponibilitat}): si
 * se'n produeixen {@value #MAX_ERRORS_DISPONIBILITAT_SEGUITS} seguits, l'execució s'atura i el darrer id torna al valor
 * anterior a aquests errors, perquè l'execució següent els torni a intentar.
 *
 * El bucle NO és transaccional: cada element es processa amb la seva pròpia transacció (el processador ha de ser
 * una crida a través del proxy de Spring), de manera que un error només afecta aquell element.
 */
@Component
public class IncorporacioDocumentsSegonPlaHelper {

	private static final Logger logger = LoggerFactory.getLogger(IncorporacioDocumentsSegonPlaHelper.class);

	/** Espera entre elements per no saturar l'Arxiu ni NOTIB (la mateixa que feia el navegador). */
	private static final long ESPERA_ENTRE_ELEMENTS_MS = 5000L;
	/** Durada màxima d'una execució: amb l'inici a les 21:00, acaba a les 05:00. */
	private static final int DURADA_MAXIMA_EXECUCIO_HORES = 8;
	/** Errors de disponibilitat seguits que aturen l'execució sense donar per tractats els elements afectats. */
	public static final int MAX_ERRORS_DISPONIBILITAT_SEGUITS = 10;
	private static final DateTimeFormatter FORMATTER_LOG = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
	private static final Pattern PATRO_HTTP_STATUS_ARXIU = Pattern.compile("^\\[HTTP_(\\d{3}),");

	@Autowired private ConfigHelper configHelper;

	/** Moment d'arrencada: les execucions programades abans no s'executen. */
	private final Date arrencada = new Date();
	/** Darrera comprovació de cada procés: només s'executa si el cron té una execució posterior a la comprovació anterior. */
	private final Map<String, Date> darreraComprovacio = new ConcurrentHashMap<>();
	/** Darrer valor incorrecte avisat de cada propietat, per no repetir l'avís a cada comprovació. */
	private final Map<String, String> darrerValorIncorrecte = new ConcurrentHashMap<>();

	/** Processa un element i retorna el missatge del resultat; una excepció indica que l'element ha fallat. */
	@FunctionalInterface
	public interface ProcessadorElement {
		String processar(Long id) throws Exception;
	}

	/**
	 * Indica si el procés s'ha d'iniciar ara: el cron de la propietat té una execució entre la comprovació anterior i
	 * ara. Registra aquesta comprovació com a darrera.
	 */
	public boolean isMomentInici(String codiProces, String propietatCron) {
		Date ara = new Date();
		Date anterior = darreraComprovacio.getOrDefault(codiProces, arrencada);
		darreraComprovacio.put(codiProces, ara);
		CronExpression cron = getCron(propietatCron);
		if (cron == null) {
			return false;
		}
		LocalDateTime seguent = cron.next(toLocalDateTime(anterior));
		return seguent != null && !seguent.isAfter(toLocalDateTime(ara));
	}

	/** Propera execució segons el cron de la propietat, o null si està buida o no és vàlida. */
	public Date getProperaExecucio(String propietatCron) {
		CronExpression cron = getCron(propietatCron);
		LocalDateTime seguent = cron != null ? cron.next(LocalDateTime.now()) : null;
		return seguent != null ? Date.from(seguent.atZone(ZoneId.systemDefault()).toInstant()) : null;
	}

	/**
	 * Processa els elements pendents per sota del darrer id desat, un per un i amb una espera entre elements, fins que
	 * no en queden o fins a la durada màxima de l'execució. Escriu al log una línia per element amb el resultat i, en
	 * acabar, un resum. El context del fil (usuari i entitat) l'ha de netejar qui crida, amb
	 * {@link #netejarContextFil()}.
	 *
	 * @param codiProces codi del procés, per als logs.
	 * @param propietatCron propietat amb el cron d'inici, que es rellegeix a cada element per poder aturar el procés.
	 * @param propietatDarrerId propietat on es desa l'id del darrer element tractat.
	 * @param tipusElement nom del tipus d'element, per als logs.
	 * @param obtenirElements consulta dels identificadors pendents amb id inferior al rebut, en ordre descendent.
	 * @param processador processament d'un element, amb transacció pròpia.
	 * @param progres rep el progrés després de cada element (p.ex. per mostrar-lo al monitor de tasques).
	 */
	public void executar(
			String codiProces,
			String propietatCron,
			String propietatDarrerId,
			String tipusElement,
			Function<Long, List<Long>> obtenirElements,
			ProcessadorElement processador,
			Consumer<String> progres) {
		long t0 = System.currentTimeMillis();
		LocalDateTime limit = LocalDateTime.now().plusHours(DURADA_MAXIMA_EXECUCIO_HORES);
		int ok = 0;
		int ko = 0;
		int processats = 0;
		int errorsDisponibilitatSeguits = 0;
		Long darrerIdAbansErrorsDisponibilitat = null;
		Long darrerId = null;
		String motiuFi = "finalitzat: s'han tractat tots els elements pendents";
		try {
			darrerId = getDarrerId(propietatDarrerId);
			String perSotaDe = darrerId != null ? " per sota de l'id " + darrerId : "";
			List<Long> ids = obtenirElements.apply(darrerId != null ? darrerId : Long.MAX_VALUE);
			int total = ids.size();
			if (total == 0) {
				motiuFi = "completat: no queden elements pendents" + perSotaDe;
				return;
			}
			logger.info("[" + codiProces + "] Inici del procés: " + total + " elements pendents" + perSotaDe
					+ ". S'aturarà com a màxim a les " + limit.format(FORMATTER_LOG));
			for (Long id : ids) {
				if (processats > 0) {
					Thread.sleep(ESPERA_ENTRE_ELEMENTS_MS);
				}
				// Cada element informa la seva entitat: es buida abans perquè no se n'hereti la de l'anterior
				ConfigHelper.setEntitat(null);
				ConfigHelper.setOrganCodi(null);
				if (getCron(propietatCron) == null) {
					motiuFi = "aturat perquè la propietat " + propietatCron + " s'ha buidat o no és vàlida";
					break;
				}
				if (!LocalDateTime.now().isBefore(limit)) {
					motiuFi = "aturat per haver arribat a la durada màxima de " + DURADA_MAXIMA_EXECUCIO_HORES + " hores";
					break;
				}
				processats++;
				try {
					String resultat = processador.processar(id);
					ok++;
					errorsDisponibilitatSeguits = 0;
					logger.info("[" + codiProces + "] " + processats + "/" + total + " " + tipusElement + "=" + id
							+ " entitat=" + getEntitatActualCodi() + " OK: " + resultat);
				} catch (Exception ex) {
					ko++;
					boolean errorDisponibilitat = isErrorDisponibilitat(ex);
					if (errorDisponibilitat) {
						if (errorsDisponibilitatSeguits == 0) {
							darrerIdAbansErrorsDisponibilitat = darrerId;
						}
						errorsDisponibilitatSeguits++;
					} else {
						errorsDisponibilitatSeguits = 0;
					}
					logger.error("[" + codiProces + "] " + processats + "/" + total + " " + tipusElement + "=" + id
							+ " entitat=" + getEntitatActualCodi() + " ERROR"
							+ (errorDisponibilitat ? " de disponibilitat (" + errorsDisponibilitatSeguits + "/" + MAX_ERRORS_DISPONIBILITAT_SEGUITS + ")" : "")
							+ ": " + ex.getMessage());
				}
				// Fora del try de l'element: si no es pot desar el darrer id, s'atura el procés
				darrerId = id;
				guardarDarrerId(propietatDarrerId, darrerId);
				if (errorsDisponibilitatSeguits >= MAX_ERRORS_DISPONIBILITAT_SEGUITS) {
					darrerId = darrerIdAbansErrorsDisponibilitat;
					guardarDarrerId(propietatDarrerId, darrerId);
					motiuFi = "aturat per " + MAX_ERRORS_DISPONIBILITAT_SEGUITS + " errors de disponibilitat seguits de l'Arxiu"
							+ " o de NOTIB: l'execució següent tornarà a intentar aquests elements";
					break;
				}
				progres.accept("Processats " + processats + " de " + total + " (OK: " + ok + ", ERROR: " + ko + "). Darrer id: " + darrerId);
			}
		} catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
			motiuFi = "interromput (aturada o redesplegament del servidor)";
		} catch (RuntimeException ex) {
			// Error fora d'un element (p.ex. la consulta dels pendents o desar el darrer id): el tracta la tasca que l'ha llançat
			motiuFi = "aturat per un error: " + ex.getMessage();
			throw ex;
		} finally {
			// Les execucions programades mentre s'executava ja no s'han d'executar
			darreraComprovacio.put(codiProces, new Date());
			String resum = "Procés " + motiuFi + ": " + processats + " elements processats (OK: " + ok + ", ERROR: " + ko
					+ ") en " + ((System.currentTimeMillis() - t0) / 1000) + " s. Darrer id: " + (darrerId != null ? darrerId : "cap");
			logger.info("[" + codiProces + "] " + resum);
			progres.accept(resum);
		}
	}

	/**
	 * Indica si l'error és de disponibilitat de l'Arxiu o de NOTIB, i no de l'element: un timeout, una connexió
	 * rebutjada o una resposta HTTP 5xx. Es recorre tota la cadena de causes perquè els helpers i els plugins
	 * embolcallen l'error original.
	 *
	 * Una resposta 5xx de l'Arxiu amb codi d'error de l'Arxiu és un error de negoci de l'element (p.ex. un expedient
	 * tancat) i no compta. Sí que compta una resposta d'error que no és el JSON de l'Arxiu (p.ex. la pàgina d'error
	 * d'un proxy quan el servei està aturat), que el client de l'Arxiu no pot interpretar.
	 */
	static boolean isErrorDisponibilitat(Throwable error) {
		Set<Throwable> visitats = Collections.newSetFromMap(new IdentityHashMap<>());
		for (Throwable t = error; t != null && visitats.add(t); t = t.getCause()) {
			// SocketTimeoutException, ConnectTimeoutException...; i ConnectException inclou la connexió rebutjada
			if (t instanceof ConnectException || t.getClass().getSimpleName().contains("Timeout")) {
				return true;
			}
			// Client de NOTIB (Jersey)
			if (t instanceof UniformInterfaceException) {
				ClientResponse resposta = ((UniformInterfaceException) t).getResponse();
				if (resposta != null && resposta.getStatus() >= 500) {
					return true;
				}
			}
			// Client de l'Arxiu
			if (t instanceof ArxiuCaibException) {
				ArxiuCaibException arxiuException = (ArxiuCaibException) t;
				if (getHttpStatus(arxiuException) >= 500 && StringUtils.isBlank(arxiuException.getArxiuCodi())) {
					return true;
				}
			}
			if (t instanceof JsonParseException && isRespostaErrorArxiu(t)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Neteja el context que el procés deixa al fil (usuari autenticat i entitat, òrgan i rol actuals). Els fils del
	 * planificador es reutilitzen: sense netejar-lo, la tasca següent que s'executés en aquest fil l'heretaria.
	 */
	public static void netejarContextFil() {
		SecurityContextHolder.clearContext();
		ConfigHelper.setEntitat(null);
		ConfigHelper.setOrganCodi(null);
		ConfigHelper.setRol(null);
	}

	/** Cron de la propietat, o null si està buida o no és una expressió vàlida. */
	private CronExpression getCron(String propietat) {
		String valor = configHelper.getConfig(propietat);
		if (StringUtils.isBlank(valor)) {
			darrerValorIncorrecte.remove(propietat);
			return null;
		}
		try {
			CronExpression cron = CronExpression.parse(valor.trim());
			darrerValorIncorrecte.remove(propietat);
			return cron;
		} catch (IllegalArgumentException ex) {
			if (!valor.equals(darrerValorIncorrecte.put(propietat, valor))) {
				logger.error("El valor \"" + valor + "\" de la propietat " + propietat + " no és una expressió cron vàlida"
						+ " (6 camps: segon minut hora dia mes dia_setmana, p.ex. \"0 0 21 * * *\"): el procés no s'executarà. "
						+ ex.getMessage());
			}
			return null;
		}
	}

	/** Id del darrer element tractat, o null si el procés ha de començar pels elements més recents. */
	private Long getDarrerId(String propietat) {
		String valor = configHelper.getConfig(propietat);
		if (StringUtils.isBlank(valor)) {
			return null;
		}
		try {
			return Long.valueOf(valor.trim());
		} catch (NumberFormatException ex) {
			throw new IllegalStateException("El valor \"" + valor + "\" de la propietat " + propietat
					+ " no és un identificador vàlid: cal buidar-la per començar pels elements més recents");
		}
	}

	private void guardarDarrerId(String propietat, Long darrerId) {
		configHelper.updateConfigNewTransaction(propietat, darrerId != null ? darrerId.toString() : null);
	}

	/**
	 * Estat HTTP de la resposta de l'Arxiu. El constructor d'ArxiuCaibException amb l'estat no l'assigna al camp
	 * (getHttpStatus() torna 0): només queda al missatge, amb el format "[HTTP_&lt;estat&gt;,&lt;codi&gt;]&lt;descripció&gt;".
	 */
	private static int getHttpStatus(ArxiuCaibException arxiuException) {
		if (arxiuException.getHttpStatus() > 0) {
			return arxiuException.getHttpStatus();
		}
		Matcher matcher = PATRO_HTTP_STATUS_ARXIU.matcher(StringUtils.defaultString(arxiuException.getMessage()));
		return matcher.find() ? Integer.parseInt(matcher.group(1)) : 0;
	}

	/** El client de l'Arxiu interpreta com a JSON el cos de les respostes amb error (ArxiuCaibClient.generarExcepcioJson). */
	private static boolean isRespostaErrorArxiu(Throwable t) {
		for (StackTraceElement element : t.getStackTrace()) {
			if ("generarExcepcioJson".equals(element.getMethodName())) {
				return true;
			}
		}
		return false;
	}

	private static LocalDateTime toLocalDateTime(Date data) {
		return LocalDateTime.ofInstant(data.toInstant(), ZoneId.systemDefault());
	}

	private String getEntitatActualCodi() {
		return ConfigHelper.getEntitat().get() != null ? ConfigHelper.getEntitat().get().getCodi() : null;
	}

}
