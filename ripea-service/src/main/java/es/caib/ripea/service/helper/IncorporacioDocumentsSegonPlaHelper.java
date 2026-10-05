package es.caib.ripea.service.helper;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Bucle dels processos d'incorporació de documents en segon pla (certificats de remeses i justificants de
 * registre), que substitueixen l'execució des del navegador de /userajax/initCertificatsRemeses i
 * /userajax/initJustificantsRegistre per no dependre de la sessió de l'administrador.
 *
 * Cada procés s'inicia a la data i hora (dd/MM/yyyy HH:mm) indicada a la seva propietat. Una data que ja ha passat
 * quan es detecta no s'executa: per tornar-lo a executar (també després d'un reinici a mitges) cal posar-hi una data
 * futura. El bucle rellegeix la propietat a cada element i s'atura si es buida o s'hi posa una data futura.
 *
 * El bucle NO és transaccional: cada element es processa amb la seva pròpia transacció (el processador ha de ser
 * una crida a través del proxy de Spring), de manera que un error només afecta aquell element.
 */
@Component
public class IncorporacioDocumentsSegonPlaHelper {

	private static final Logger logger = LoggerFactory.getLogger(IncorporacioDocumentsSegonPlaHelper.class);

	public static final String FORMAT_DATA_INICI = "dd/MM/yyyy HH:mm";
	private static final DateTimeFormatter FORMATTER_DATA_INICI = DateTimeFormatter.ofPattern("dd/MM/uuuu HH:mm")
			.withResolverStyle(ResolverStyle.STRICT);
	/** Espera entre elements per no saturar l'Arxiu ni NOTIB (la mateixa que feia el navegador). */
	private static final long ESPERA_ENTRE_ELEMENTS_MS = 5000L;

	@Autowired private ConfigHelper configHelper;

	/** Moment d'arrencada: les dates anteriors no s'executen. */
	private final Date arrencada = new Date();
	/** Darrera comprovació de cada procés: només s'executa una data posterior a la comprovació anterior. */
	private final Map<String, Date> darreraComprovacio = new ConcurrentHashMap<>();
	/** Darrer valor incorrecte avisat de cada propietat, per no repetir l'avís a cada comprovació. */
	private final Map<String, String> darrerValorIncorrecte = new ConcurrentHashMap<>();

	/** Processa un element i retorna el missatge del resultat; una excepció indica que l'element ha fallat. */
	@FunctionalInterface
	public interface ProcessadorElement {
		String processar(Long id) throws Exception;
	}

	/**
	 * Indica si el procés s'ha d'iniciar ara: la data de la propietat ha arribat després de la comprovació anterior.
	 * Registra aquesta comprovació com a darrera.
	 */
	public boolean isMomentInici(String codiProces, String propietat) {
		Date ara = new Date();
		Date anterior = darreraComprovacio.getOrDefault(codiProces, arrencada);
		darreraComprovacio.put(codiProces, ara);
		Date dataInici = getDataInici(propietat);
		return dataInici != null && dataInici.after(anterior) && !dataInici.after(ara);
	}

	/** Data i hora d'inici configurada a la propietat, o null si està buida o no té el format correcte. */
	public Date getDataInici(String propietat) {
		String valor = configHelper.getConfig(propietat);
		if (StringUtils.isBlank(valor)) {
			darrerValorIncorrecte.remove(propietat);
			return null;
		}
		try {
			LocalDateTime dataHora = LocalDateTime.parse(valor.trim(), FORMATTER_DATA_INICI);
			darrerValorIncorrecte.remove(propietat);
			return Date.from(dataHora.atZone(ZoneId.systemDefault()).toInstant());
		} catch (DateTimeParseException ex) {
			if (!valor.equals(darrerValorIncorrecte.put(propietat, valor))) {
				logger.error("El valor \"" + valor + "\" de la propietat " + propietat + " no té el format "
						+ FORMAT_DATA_INICI + ": el procés no s'executarà");
			}
			return null;
		}
	}

	/**
	 * Processa tots els elements pendents, un per un i amb una espera entre elements. Escriu al log una línia per
	 * element amb el resultat i, en acabar, un resum. El context del fil (usuari i entitat) l'ha de netejar qui crida,
	 * amb {@link #netejarContextFil()}.
	 *
	 * @param codiProces codi del procés, per als logs.
	 * @param propietat propietat amb la data d'inici, que es rellegeix a cada element per poder aturar el procés.
	 * @param tipusElement nom del tipus d'element, per als logs.
	 * @param obtenirElements consulta dels identificadors pendents de processar.
	 * @param processador processament d'un element, amb transacció pròpia.
	 * @param progres rep el progrés després de cada element (p.ex. per mostrar-lo al monitor de tasques).
	 */
	public void executar(
			String codiProces,
			String propietat,
			String tipusElement,
			Supplier<List<Long>> obtenirElements,
			ProcessadorElement processador,
			Consumer<String> progres) {
		long t0 = System.currentTimeMillis();
		int ok = 0;
		int ko = 0;
		int processats = 0;
		String motiuFi = "finalitzat";
		try {
			List<Long> ids = obtenirElements.get();
			int total = ids.size();
			logger.info("[" + codiProces + "] Inici del procés: " + total + " elements pendents");
			for (Long id : ids) {
				if (processats > 0) {
					Thread.sleep(ESPERA_ENTRE_ELEMENTS_MS);
				}
				// Cada element informa la seva entitat: es buida abans perquè no se n'hereti la de l'anterior
				ConfigHelper.setEntitat(null);
				ConfigHelper.setOrganCodi(null);
				Date dataInici = getDataInici(propietat);
				if (dataInici == null || dataInici.after(new Date())) {
					motiuFi = "aturat perquè la propietat " + propietat + " s'ha buidat o té una data futura";
					break;
				}
				processats++;
				try {
					String resultat = processador.processar(id);
					ok++;
					logger.info("[" + codiProces + "] " + processats + "/" + total + " " + tipusElement + "=" + id
							+ " entitat=" + getEntitatActualCodi() + " OK: " + resultat);
				} catch (Exception ex) {
					ko++;
					logger.error("[" + codiProces + "] " + processats + "/" + total + " " + tipusElement + "=" + id
							+ " entitat=" + getEntitatActualCodi() + " ERROR: " + ex.getMessage());
				}
				progres.accept("Processats " + processats + " de " + total + " (OK: " + ok + ", ERROR: " + ko + ")");
			}
		} catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
			motiuFi = "interromput (aturada o redesplegament del servidor)";
		} catch (RuntimeException ex) {
			// Error fora d'un element (p.ex. la consulta dels pendents): el tracta la tasca que l'ha llançat
			motiuFi = "aturat per un error: " + ex.getMessage();
			throw ex;
		} finally {
			// Les dates que han passat mentre s'executava ja no s'han d'executar
			darreraComprovacio.put(codiProces, new Date());
			String resum = "Procés " + motiuFi + ": " + processats + " elements processats (OK: " + ok + ", ERROR: " + ko
					+ ") en " + ((System.currentTimeMillis() - t0) / 1000) + " s";
			logger.info("[" + codiProces + "] " + resum);
			progres.accept(resum);
		}
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

	private String getEntitatActualCodi() {
		return ConfigHelper.getEntitat().get() != null ? ConfigHelper.getEntitat().get().getCodi() : null;
	}

}
