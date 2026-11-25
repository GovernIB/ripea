package es.caib.ripea.service.intf.utils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Traça de temps per petició: diagnòstic de rendiment de les peticions a l'API REST.
 *
 * <p>Un filtre web obre la traça quan comença la petició ({@link #iniciaPeticio(String)}) i, quan acaba, escriu
 * al log (categoria {@value #CATEGORIA_LOG}, nivell INFO) l'arbre de passos amb el temps de cada un. Cada pas
 * es marca amb un tram:</p>
 * <pre>
 * try (TempsTraca.Tram tram = TempsTraca.inicia("additionalSpringFilter")) {
 *     ...
 * }
 * </pre>
 * <p>o, per a una sola crida, amb {@link #mesura(String, Supplier)} i {@link #executa(String, Runnable)}.</p>
 *
 * <p>Les crides a un mateix pas dins el mateix pare s'agrupen en una sola línia (nombre de crides, temps total i
 * màxim): una perspectiva aplicada fila a fila surt una sola vegada. Cada línia mostra també el temps propi (el
 * que no és de cap pas fill) i les consultes SQL que Hibernate ha executat dins el pas, amb el seu temps
 * d'execució. Les consultes més lentes surten al final amb el text SQL.</p>
 *
 * <p>Fora d'una petició traçada (tasques de segon pla, altres fils) o amb la categoria de log per sota d'INFO,
 * els trams no fan res: el cost és llegir un ThreadLocal.</p>
 *
 * <p>Propietat de sistema opcional: {@code es.caib.ripea.diag.temps.sql.llindar}, temps mínim en ms perquè una
 * consulta SQL surti a la llista de consultes lentes (per defecte 10).</p>
 */
public final class TempsTraca {

	public static final String CATEGORIA_LOG = "es.caib.ripea.diag.temps";

	private static final Logger log = LoggerFactory.getLogger(CATEGORIA_LOG);
	private static final ThreadLocal<Traca> TRACA_ACTUAL = new ThreadLocal<>();
	private static final Tram TRAM_BUIT = new Tram(null, null);
	private static final long LLINDAR_SQL_LENTA_NANOS = Long.getLong("es.caib.ripea.diag.temps.sql.llindar", 10L) * 1_000_000L;
	private static final int MAX_SQL_LENTES = 10;
	private static final int MAX_PASSOS_RESUM = 15;
	private static final int MAX_LONGITUD_SQL = 4000;

	private TempsTraca() {}

	/**
	 * Indica si s'han de traçar les peticions (categoria de log a INFO o inferior).
	 */
	public static boolean isActiva() {
		return log.isInfoEnabled();
	}

	/**
	 * Comença la traça de la petició del fil actual. En tancar el tram retornat s'escriu el log.
	 * Si el fil ja té una traça en curs, el tram és un pas més d'aquella traça.
	 */
	public static Tram iniciaPeticio(String descripcio) {
		if (!log.isInfoEnabled()) {
			return TRAM_BUIT;
		}
		if (TRACA_ACTUAL.get() != null) {
			return inicia(descripcio);
		}
		Traca traca = new Traca(descripcio);
		TRACA_ACTUAL.set(traca);
		return new Tram(traca, traca.arrel);
	}

	/**
	 * Comença un pas dins el pas actual. Sense traça en curs retorna un tram que no fa res.
	 */
	public static Tram inicia(String nom) {
		Traca traca = TRACA_ACTUAL.get();
		if (traca == null) {
			return TRAM_BUIT;
		}
		Node node = traca.actual.fill(nom);
		traca.actual = node;
		return new Tram(traca, node);
	}

	/**
	 * Mesura una crida que retorna un valor.
	 */
	public static <T> T mesura(String nom, Supplier<T> crida) {
		try (Tram tram = inicia(nom)) {
			return crida.get();
		}
	}

	/**
	 * Mesura una crida sense valor de retorn.
	 */
	public static void executa(String nom, Runnable crida) {
		try (Tram tram = inicia(nom)) {
			crida.run();
		}
	}

	/**
	 * Comença un pas que no es pot tancar amb try-with-resources perquè s'obre i es tanca des de llocs diferents
	 * (per exemple, la serialització de la resposta). Només n'hi pot haver un d'obert: si n'hi havia un altre,
	 * primer el tanca.
	 */
	public static void iniciaFase(String nom) {
		Traca traca = TRACA_ACTUAL.get();
		if (traca != null) {
			tancaFase();
			traca.fase = inicia(nom);
		}
	}

	/**
	 * Tanca el pas obert amb {@link #iniciaFase(String)}, si n'hi ha.
	 */
	public static void tancaFase() {
		Traca traca = TRACA_ACTUAL.get();
		if (traca != null && traca.fase != null) {
			Tram fase = traca.fase;
			traca.fase = null;
			fase.close();
		}
	}

	/**
	 * Afegeix un text curt a la capçalera de la traça (estat HTTP, usuari, rol...). Els repetits s'ignoren.
	 */
	public static void anota(String text) {
		Traca traca = TRACA_ACTUAL.get();
		if (traca != null && text != null) {
			traca.anotacions.add(text);
		}
	}

	/**
	 * Indica els paràmetres de la petició, que s'escriuen en una línia pròpia sota la capçalera.
	 */
	public static void parametres(String parametres) {
		Traca traca = TRACA_ACTUAL.get();
		if (traca != null) {
			traca.parametres = parametres;
		}
	}

	/**
	 * L'invoca Hibernate (StatementInspector) en preparar cada sentència SQL.
	 */
	public static void sqlPreparada(String sql) {
		Traca traca = TRACA_ACTUAL.get();
		if (traca != null) {
			traca.darreraSql = sql;
		}
	}

	/**
	 * L'invoca Hibernate (SessionEventListener) quan acaba d'executar una sentència SQL.
	 */
	public static void sqlExecutada(long nanos) {
		Traca traca = TRACA_ACTUAL.get();
		if (traca == null) {
			return;
		}
		Node node = traca.actual;
		node.sqlCrides++;
		node.sqlNanos += nanos;
		if (nanos >= LLINDAR_SQL_LENTA_NANOS) {
			traca.afegeixSqlLenta(nanos, node, traca.darreraSql);
		}
	}

	/**
	 * Pas en curs. S'ha de tancar (try-with-resources) al mateix fil on s'ha obert.
	 */
	public static final class Tram implements AutoCloseable {

		private final Traca traca;
		private final Node node;
		private final long inici;
		private boolean tancat;

		private Tram(Traca traca, Node node) {
			this.traca = traca;
			this.node = node;
			this.inici = System.nanoTime();
		}

		@Override
		public void close() {
			if (traca == null || tancat) {
				return;
			}
			tancat = true;
			if (node == traca.arrel) {
				tancaFase();
			}
			long nanos = System.nanoTime() - inici;
			node.crides++;
			node.nanos += nanos;
			if (nanos > node.maxNanos) {
				node.maxNanos = nanos;
			}
			if (node == traca.arrel) {
				if (TRACA_ACTUAL.get() == traca) {
					TRACA_ACTUAL.remove();
				}
				try {
					traca.escriu();
				} catch (RuntimeException ex) {
					log.warn("No s'ha pogut escriure la traça de temps de " + traca.arrel.nom, ex);
				}
			} else {
				traca.actual = node.pare;
			}
		}
	}

	private static final class Node {

		private final String nom;
		private final Node pare;
		private final Map<String, Node> fills = new LinkedHashMap<>();
		private int crides;
		private long nanos;
		private long maxNanos;
		private int sqlCrides;
		private long sqlNanos;

		private Node(String nom, Node pare) {
			this.nom = nom;
			this.pare = pare;
		}

		private Node fill(String nom) {
			return fills.computeIfAbsent(nom != null ? nom : "?", n -> new Node(n, this));
		}

		private long nanosPropis() {
			long nanosFills = 0;
			for (Node fill: fills.values()) {
				nanosFills += fill.nanos;
			}
			return Math.max(0, nanos - nanosFills);
		}

		private int sqlCridesTotals() {
			int total = sqlCrides;
			for (Node fill: fills.values()) {
				total += fill.sqlCridesTotals();
			}
			return total;
		}

		private long sqlNanosTotals() {
			long total = sqlNanos;
			for (Node fill: fills.values()) {
				total += fill.sqlNanosTotals();
			}
			return total;
		}

		/** Camí des del primer pas sota l'arrel (l'arrel és la petició i ja surt a la capçalera). */
		private String cami() {
			if (pare == null) {
				return "(peticio)";
			}
			StringBuilder sb = new StringBuilder(nom);
			for (Node n = pare; n != null && n.pare != null; n = n.pare) {
				sb.insert(0, " > ").insert(0, n.nom);
			}
			return sb.toString();
		}
	}

	private static final class SqlLenta {
		private final long nanos;
		private final Node node;
		private final String sql;
		private SqlLenta(long nanos, Node node, String sql) {
			this.nanos = nanos;
			this.node = node;
			this.sql = sql;
		}
	}

	private static final class Traca {

		private final Node arrel;
		private Node actual;
		private Tram fase;
		private String darreraSql;
		private String parametres;
		private final Set<String> anotacions = new LinkedHashSet<>();
		private final List<SqlLenta> sqlLentes = new ArrayList<>();

		private Traca(String descripcio) {
			this.arrel = new Node(descripcio, null);
			this.actual = arrel;
		}

		private void afegeixSqlLenta(long nanos, Node node, String sql) {
			sqlLentes.add(new SqlLenta(nanos, node, sql));
			if (sqlLentes.size() > MAX_SQL_LENTES * 5) {
				sqlLentes.sort((a, b) -> Long.compare(b.nanos, a.nanos));
				sqlLentes.subList(MAX_SQL_LENTES, sqlLentes.size()).clear();
			}
		}

		private void escriu() {
			long total = arrel.nanos;
			StringBuilder sb = new StringBuilder(8192);
			sb.append("TEMPS ").append(ms(total)).append(" ms | ").append(arrel.nom);
			for (String anotacio: anotacions) {
				sb.append(" | ").append(anotacio);
			}
			sb.append(" | SQL ").append(arrel.sqlCridesTotals()).append(" (").append(ms(arrel.sqlNanosTotals())).append(" ms)\n");
			if (parametres != null && !parametres.isEmpty()) {
				sb.append("  parametres: ").append(parametres).append('\n');
			}
			sb.append(String.format(Locale.ROOT, "%10s %6s %6s %9s %9s | %5s %9s | %s%n",
					"total ms", "%", "crides", "max ms", "propi ms", "SQL", "SQL ms", "pas"));
			escriuNode(sb, arrel, 0, total);

			List<Node> nodes = new ArrayList<>();
			recull(arrel, nodes);
			nodes.sort((a, b) -> Long.compare(b.nanosPropis(), a.nanosPropis()));
			sb.append("Passos amb mes temps propi:\n");
			for (int i = 0; i < nodes.size() && i < MAX_PASSOS_RESUM; i++) {
				Node node = nodes.get(i);
				if (node.nanosPropis() <= 0) {
					break;
				}
				sb.append(String.format(Locale.ROOT, "%10s ms %5.1f %% x%-5d %s%n",
						ms(node.nanosPropis()),
						percentatge(node.nanosPropis(), total),
						node.crides,
						node.cami()));
			}

			if (!sqlLentes.isEmpty()) {
				sqlLentes.sort((a, b) -> Long.compare(b.nanos, a.nanos));
				sb.append("SQL mes lentes (>= ").append(LLINDAR_SQL_LENTA_NANOS / 1_000_000L).append(" ms):\n");
				for (int i = 0; i < sqlLentes.size() && i < MAX_SQL_LENTES; i++) {
					SqlLenta sqlLenta = sqlLentes.get(i);
					sb.append(String.format(Locale.ROOT, "%10s ms [%s] %s%n",
							ms(sqlLenta.nanos),
							sqlLenta.node.cami(),
							compacta(sqlLenta.sql)));
				}
			}
			log.info(sb.toString());
		}

		private void escriuNode(StringBuilder sb, Node node, int nivell, long total) {
			StringBuilder sagnat = new StringBuilder();
			for (int i = 0; i < nivell; i++) {
				sagnat.append("  ");
			}
			sb.append(String.format(Locale.ROOT, "%10s %6.1f %6s %9s %9s | %5d %9s | %s%s%n",
					ms(node.nanos),
					percentatge(node.nanos, total),
					node.crides > 0 ? String.valueOf(node.crides) : "obert",
					node.crides > 1 ? ms(node.maxNanos) : "",
					node.fills.isEmpty() ? "" : ms(node.nanosPropis()),
					node.sqlCridesTotals(),
					ms(node.sqlNanosTotals()),
					sagnat,
					node.nom));
			for (Node fill: node.fills.values()) {
				escriuNode(sb, fill, nivell + 1, total);
			}
		}

		private void recull(Node node, List<Node> nodes) {
			nodes.add(node);
			for (Node fill: node.fills.values()) {
				recull(fill, nodes);
			}
		}
	}

	private static String ms(long nanos) {
		return String.format(Locale.ROOT, "%.1f", nanos / 1_000_000d);
	}

	private static double percentatge(long nanos, long total) {
		return total > 0 ? nanos * 100d / total : 0d;
	}

	private static String compacta(String sql) {
		if (sql == null) {
			return "(sense text)";
		}
		String compactada = sql.replaceAll("\\s+", " ").trim();
		return compactada.length() > MAX_LONGITUD_SQL ? compactada.substring(0, MAX_LONGITUD_SQL) + "..." : compactada;
	}

}
