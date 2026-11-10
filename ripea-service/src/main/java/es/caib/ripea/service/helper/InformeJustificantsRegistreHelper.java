package es.caib.ripea.service.helper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.caib.ripea.persistence.entity.EntitatEntity;
import es.caib.ripea.persistence.repository.EntitatRepository;
import es.caib.ripea.service.helper.FilaInformeJustificantRegistre.AccioConsulta;
import es.caib.ripea.service.helper.FilaInformeJustificantRegistre.AccioMoure;
import es.caib.ripea.service.helper.FilaInformeJustificantRegistre.AccioRipea;
import es.caib.ripea.service.helper.FilaInformeJustificantRegistre.ValorAccio;
import es.caib.ripea.service.intf.config.PropertyConfig;
import lombok.extern.slf4j.Slf4j;

/**
 * Informe del procés en segon pla que incorpora els justificants de registre de les anotacions dels expedients oberts:
 * una fila per anotació tractada amb què ha passat amb el seu justificant ({@link FilaInformeJustificantRegistre}), que
 * s'envia per correu als administradors de cada entitat en acabar l'execució. No substitueix les línies del log.
 *
 * Les files s'escriuen a mesura que es tracten les anotacions, en un CSV per entitat i execució al directori
 * {@code <es.caib.ripea.app.data.dir>/informes/justificants/pendents}. En acabar l'execució, cada CSV s'envia comprimit
 * (ZIP) i es desa a {@code enviats/}, d'on s'esborra al cap de {@value #MESOS_RETENCIO} mesos. Si no s'ha pogut enviar
 * (error del correu o aturada del servidor a mitja execució), el CSV es queda a {@code pendents/} i s'envia en començar
 * l'execució següent. Un error de l'informe mai no atura el procés: només es registra al log.
 *
 * Només es pot fer servir des del fil del procés: l'estat de l'execució en curs no està protegit per a accessos
 * concurrents.
 */
@Slf4j
@Component
public class InformeJustificantsRegistreHelper {

	static final String DIRECTORI = "informes/justificants";
	static final String PENDENTS = "pendents";
	static final String ENVIATS = "enviats";
	/** Mida màxima del ZIP per adjuntar-lo: si la supera, el correu indica on és al servidor. */
	static final long MIDA_MAXIMA_ADJUNT = 10L * 1024 * 1024;
	static final int MESOS_RETENCIO = 3;

	private static final String PREFIX_FITXER = "justificants_";
	private static final String SENSE_ENTITAT = "SENSE_ENTITAT";
	private static final String SEPARADOR = ";";
	private static final String SALT_LINIA = "\r\n";
	private static final DateTimeFormatter FORMAT_EXECUCIO = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
	private static final DateTimeFormatter FORMAT_TEXT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
	/** justificants_&lt;entitat&gt;_&lt;yyyyMMdd_HHmmss&gt;.csv */
	private static final Pattern PATRO_FITXER = Pattern.compile("^" + PREFIX_FITXER + "(.+)_(\\d{8}_\\d{6})\\.csv$");

	private static final String TITOL_CONSULTA = "Consulta del justificant original a l'Arxiu (sèrie de Distribució)";
	private static final String TITOL_MOURE = "Moviment del justificant a l'expedient de RIPEA a l'Arxiu";
	private static final String TITOL_RIPEA = "Acció a RIPEA";
	/** Conceptes de la llegenda que fan servir les descripcions dels valors. */
	private static final String LLEGENDA_CONCEPTES = "Justificant: document de l'expedient de tipus"
			+ " REGISTRE_JUSTIFICANT_ENTRADA amb el número de registre de l'anotació. No hi compten els annexos de"
			+ " l'anotació, encara que tinguin aquest tipus.\n"
			+ "Carpeta del registre: la carpeta \"Registre entrada: <número de registre>\" no esborrada; si no n'hi ha, la"
			+ " carpeta de l'expedient (esborrada o no) que conté més annexos de l'anotació; si tampoc, la carpeta"
			+ " esborrada amb aquest nom.\n";
	private static final String LLEGENDA_DETALL = "Columna \"Detall\" del CSV: resultat o error de cada anotació; si el"
			+ " moviment a l'Arxiu ha fallat, hi afegeix \"Moure: <error>\".\n";
	private static final String LLEGENDA_RESUM = "Resum de l'execució: OK i ERROR són les anotacions de totes les"
			+ " entitats que han acabat bé o amb error; \"sense cridar cap servei extern\", les que no han cridat l'Arxiu"
			+ " ni cap altre servei extern; \"Darrer id\", el punt on continuarà l'execució següent (les anotacions d'id"
			+ " inferior).\n";

	@Autowired private ConfigHelper configHelper;
	@Autowired private EmailHelper emailHelper;
	@Autowired private EntitatRepository entitatRepository;

	/** Execució en curs (yyyyMMdd_HHmmss), o null si no n'hi ha cap. */
	private String execucio;
	private Path directoriPendents;
	private final Map<String, BufferedWriter> fitxers = new HashMap<>();

	/**
	 * Comença l'informe d'una execució: abans envia els informes que s'hagin quedat pendents d'execucions anteriors i
	 * esborra els enviats fa més de {@value #MESOS_RETENCIO} mesos.
	 */
	public void iniciarExecucio() {
		try {
			tancarFitxers();
			execucio = LocalDateTime.now().format(FORMAT_EXECUCIO);
			directoriPendents = crearDirectori(PENDENTS);
			if (directoriPendents != null) {
				enviarPendents(null);
				esborrarEnviatsAntics();
			}
		} catch (Exception ex) {
			log.error("Error en començar l'informe del procés de justificants de registre", ex);
		}
	}

	/**
	 * Tracta una anotació i n'escriu la fila a l'informe, tant si va bé com si falla. La fila s'escriu fora de la
	 * transacció de l'element (el processador ha de ser una crida a través del proxy).
	 *
	 * @return el resultat del processador.
	 * @throws Exception l'error del processador, sense canvis.
	 */
	public String processarElement(Long anotacioId, Callable<String> processador) throws Exception {
		FilaInformeJustificantRegistre fila = FilaInformeJustificantRegistre.iniciar(anotacioId);
		try {
			String resultat = processador.call();
			fila.finalitzarOk(resultat);
			return resultat;
		} catch (Exception ex) {
			fila.finalitzarError(ex);
			throw ex;
		} finally {
			FilaInformeJustificantRegistre.treure();
			escriure(fila);
		}
	}

	/**
	 * Acaba l'informe de l'execució i l'envia als administradors de cada entitat.
	 *
	 * @param resumExecucio resum del procés (totes les entitats), per al correu.
	 */
	public void finalitzarExecucio(String resumExecucio) {
		try {
			tancarFitxers();
			if (execucio != null && directoriPendents != null) {
				enviarPendents(resumExecucio);
			}
		} catch (Exception ex) {
			log.error("Error en acabar l'informe del procés de justificants de registre", ex);
		} finally {
			execucio = null;
		}
	}

	private void escriure(FilaInformeJustificantRegistre fila) {
		if (execucio == null || directoriPendents == null) {
			return;
		}
		String entitat = fila.getEntitatCodi() != null ? fila.getEntitatCodi() : SENSE_ENTITAT;
		try {
			BufferedWriter writer = fitxers.get(entitat);
			if (writer == null) {
				Path fitxer = directoriPendents.resolve(nomFitxer(entitat, execucio));
				writer = Files.newBufferedWriter(
						fitxer,
						StandardCharsets.UTF_8,
						StandardOpenOption.CREATE,
						StandardOpenOption.APPEND);
				fitxers.put(entitat, writer);
				if (Files.size(fitxer) == 0) {
					// BOM perquè Excel llegeixi els accents
					writer.write('﻿');
					writer.write(liniaCsv(FilaInformeJustificantRegistre.CAPCALERA));
				}
			}
			writer.write(liniaCsv(fila.getCamps()));
			// A disc a cada fila: si el servidor s'atura, l'informe s'envia a l'execució següent amb el que hi hagi
			writer.flush();
		} catch (Exception ex) {
			log.error("No s'ha pogut escriure a l'informe del procés de justificants de registre la fila de l'anotació "
					+ fila.getAnotacioId(), ex);
		}
	}

	private void tancarFitxers() {
		for (Map.Entry<String, BufferedWriter> fitxer : fitxers.entrySet()) {
			try {
				fitxer.getValue().close();
			} catch (IOException ex) {
				log.error("No s'ha pogut tancar l'informe de justificants de registre de l'entitat " + fitxer.getKey(), ex);
			}
		}
		fitxers.clear();
	}

	/**
	 * Envia els CSV de pendents/. Els de l'execució en curs porten el resum de l'execució; els d'execucions anteriors,
	 * un avís que no es van poder enviar en acabar.
	 */
	private void enviarPendents(String resumExecucio) throws IOException {
		List<Path> pendents = new ArrayList<>();
		try (DirectoryStream<Path> fitxersPendents = Files.newDirectoryStream(directoriPendents, PREFIX_FITXER + "*.csv")) {
			for (Path fitxer : fitxersPendents) {
				pendents.add(fitxer);
			}
		}
		pendents.sort(null);
		for (Path csv : pendents) {
			Matcher matcher = PATRO_FITXER.matcher(csv.getFileName().toString());
			if (!matcher.matches()) {
				log.warn("Fitxer inesperat a l'informe de justificants de registre, no s'envia: " + csv);
				continue;
			}
			String execucioFitxer = matcher.group(2);
			String resum = execucioFitxer.equals(execucio) && resumExecucio != null
					? resumExecucio
					: "Aquest informe no es va poder enviar en acabar la seva execució (aturada del servidor o error en "
							+ "enviar el correu): s'envia en començar l'execució següent, amb les anotacions que s'havien tractat.";
			enviarFitxer(csv, matcher.group(1), execucioFitxer, resum);
		}
	}

	/** Envia un CSV comprimit i el desa a enviats/. Si falla, el deixa a pendents/ per tornar-lo a enviar. */
	private void enviarFitxer(Path csv, String entitatCodi, String execucioFitxer, String resum) {
		try {
			Recompte recompte = comptar(csv);
			String nomZip = csv.getFileName().toString().replaceFirst("\\.csv$", ".zip");
			byte[] zip = comprimir(csv);
			Path directoriEnviats = crearDirectori(ENVIATS);
			if (directoriEnviats == null) {
				return;
			}
			Path zipEnviat = directoriEnviats.resolve(nomZip);
			EntitatEntity entitat = SENSE_ENTITAT.equals(entitatCodi) ? null : entitatRepository.findByCodi(entitatCodi);
			if (entitat == null) {
				log.warn("L'informe de justificants de registre " + csv.getFileName() + " no és de cap entitat existent:"
						+ " no s'envia i es desa a " + zipEnviat);
			} else {
				boolean adjuntar = zip.length <= MIDA_MAXIMA_ADJUNT;
				int destinataris = emailHelper.enviarInformeJustificantsRegistre(
						entitat.getId(),
						"Incorporació de justificants de registre: informe de l'execució del " + dataText(execucioFitxer)
								+ " (entitat " + entitatCodi + ")",
						textCorreu(entitatCodi, execucioFitxer, resum, recompte, nomZip, adjuntar ? null : zipEnviat, zip.length),
						nomZip,
						adjuntar ? zip : null);
				if (destinataris == 0) {
					log.warn("L'entitat " + entitatCodi + " no té cap administrador amb correu: l'informe de justificants de"
							+ " registre no s'envia i es desa a " + zipEnviat);
				} else {
					log.info("Enviat l'informe de justificants de registre " + nomZip + " (" + recompte.total
							+ " anotacions) a " + destinataris + " administradors de l'entitat " + entitatCodi);
				}
			}
			Files.write(zipEnviat, zip);
			Files.delete(csv);
		} catch (Exception ex) {
			log.error("No s'ha pogut enviar l'informe de justificants de registre " + csv
					+ ": es tornarà a intentar a l'execució següent", ex);
		}
	}

	private void esborrarEnviatsAntics() throws IOException {
		Path directoriEnviats = crearDirectori(ENVIATS);
		if (directoriEnviats == null) {
			return;
		}
		long limit = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(MESOS_RETENCIO * 31L);
		try (DirectoryStream<Path> enviats = Files.newDirectoryStream(directoriEnviats, PREFIX_FITXER + "*.zip")) {
			for (Path fitxer : enviats) {
				if (Files.getLastModifiedTime(fitxer).toMillis() < limit) {
					Files.delete(fitxer);
				}
			}
		}
	}

	private String textCorreu(
			String entitatCodi,
			String execucioFitxer,
			String resum,
			Recompte recompte,
			String nomZip,
			Path rutaNoAdjuntat,
			long midaZip) {
		StringBuilder text = new StringBuilder();
		text.append("Informe del procés en segon pla que incorpora els justificants de registre de les anotacions dels"
				+ " expedients oberts.\n\n");
		text.append("Entitat: ").append(entitatCodi).append("\n");
		text.append("Execució: ").append(dataText(execucioFitxer)).append("\n");
		text.append("Resum de l'execució (totes les entitats): ").append(StringUtils.defaultString(resum, "-")).append("\n\n");
		text.append("Anotacions d'aquesta entitat tractades: ").append(recompte.total).append("\n");
		afegirRecompte(text, TITOL_CONSULTA, recompte.consulta);
		afegirRecompte(text, TITOL_MOURE, recompte.moure);
		afegirRecompte(text, TITOL_RIPEA, recompte.ripea);
		int errorsPermisos = recompte.moure.getOrDefault(AccioMoure.ERROR_PERMISOS_WARN.getText(), 0);
		text.append("\nJustificants que no s'han pogut moure a l'expedient de RIPEA a l'Arxiu per falta de permisos: ")
				.append(errorsPermisos).append(".\n");
		if (errorsPermisos > 0) {
			text.append("Es mostren amb l'avís " + FilaInformeJustificantRegistre.AVIS_NO_MOGUT + " i continuen apuntant"
					+ " al justificant original de Distribució. Són les files amb l'acció moure \""
					+ AccioMoure.ERROR_PERMISOS_WARN.getText() + "\", amb l'identificador de l'anotació i l'UUID del"
					+ " justificant.\n");
		}
		text.append("\n");
		if (rutaNoAdjuntat == null) {
			text.append("El detall de cada anotació és al fitxer adjunt ").append(nomZip)
					.append(" (CSV separat per \"").append(SEPARADOR).append("\" i codificat en UTF-8).\n");
		} else {
			text.append("El fitxer amb el detall de cada anotació (").append(midaZip / (1024 * 1024))
					.append(" MB comprimit) supera la mida màxima per adjuntar-lo (")
					.append(MIDA_MAXIMA_ADJUNT / (1024 * 1024)).append(" MB). Es pot obtenir del servidor, a ")
					.append(rutaNoAdjuntat).append(".\n");
		}
		afegirLlegenda(text, recompte);
		return text.toString();
	}

	/**
	 * Llegenda al final del correu: explica només els valors que apareixen a l'informe (els del recompte), que són
	 * també els únics que hi ha al CSV adjunt.
	 */
	private static void afegirLlegenda(StringBuilder text, Recompte recompte) {
		text.append("\n----------------------------------------\n");
		text.append("Llegenda dels valors que apareixen en aquest informe\n\n");
		text.append(LLEGENDA_CONCEPTES);
		afegirLlegendaColumna(text, TITOL_CONSULTA, FilaInformeJustificantRegistre.COLUMNA_ACCIO_CONSULTA,
				AccioConsulta.values(), recompte.consulta);
		afegirLlegendaColumna(text, TITOL_MOURE, FilaInformeJustificantRegistre.COLUMNA_ACCIO_MOURE,
				AccioMoure.values(), recompte.moure);
		afegirLlegendaColumna(text, TITOL_RIPEA, FilaInformeJustificantRegistre.COLUMNA_ACCIO_RIPEA,
				AccioRipea.values(), recompte.ripea);
		text.append("\n").append(LLEGENDA_DETALL).append(LLEGENDA_RESUM);
	}

	private static void afegirLlegendaColumna(
			StringBuilder text,
			String titol,
			int columna,
			ValorAccio[] valors,
			Map<String, Integer> recompte) {
		StringBuilder linies = new StringBuilder();
		for (ValorAccio valor : valors) {
			if (recompte.getOrDefault(valor.getText(), 0) > 0) {
				linies.append("    ").append(valor.getText()).append(": ").append(valor.getDescripcio()).append("\n");
			}
		}
		if (linies.length() > 0) {
			text.append("\n").append(titol).append(", columna \"")
					.append(FilaInformeJustificantRegistre.CAPCALERA[columna]).append("\" del CSV:\n")
					.append(linies);
		}
	}

	private static void afegirRecompte(StringBuilder text, String titol, Map<String, Integer> recompte) {
		text.append("\n").append(titol).append(":\n");
		for (Map.Entry<String, Integer> valor : recompte.entrySet()) {
			if (valor.getValue() > 0) {
				text.append("    ").append(valor.getKey()).append(": ").append(valor.getValue()).append("\n");
			}
		}
	}

	/** Recompte de les accions de les files d'un CSV de l'informe. */
	static Recompte comptar(Path csv) throws IOException {
		Recompte recompte = new Recompte();
		// Línia a línia: la primera execució pot tenir desenes de milers de files
		try (BufferedReader lector = Files.newBufferedReader(csv, StandardCharsets.UTF_8)) {
			// La primera línia és la capçalera
			lector.readLine();
			String linia;
			while ((linia = lector.readLine()) != null) {
				comptarLinia(recompte, linia);
			}
		}
		return recompte;
	}

	private static void comptarLinia(Recompte recompte, String linia) {
		List<String> camps = llegirCamps(linia);
		// Una línia incompleta (el servidor s'ha aturat mentre s'escrivia) no compta
		if (camps.size() < FilaInformeJustificantRegistre.CAPCALERA.length) {
			return;
		}
		recompte.total++;
		recompte.consulta.merge(camps.get(FilaInformeJustificantRegistre.COLUMNA_ACCIO_CONSULTA), 1, Integer::sum);
		recompte.moure.merge(camps.get(FilaInformeJustificantRegistre.COLUMNA_ACCIO_MOURE), 1, Integer::sum);
		recompte.ripea.merge(camps.get(FilaInformeJustificantRegistre.COLUMNA_ACCIO_RIPEA), 1, Integer::sum);
	}

	static class Recompte {
		int total;
		/** Els valors possibles, en l'ordre de les enumeracions. */
		final Map<String, Integer> consulta = new LinkedHashMap<>();
		final Map<String, Integer> moure = new LinkedHashMap<>();
		final Map<String, Integer> ripea = new LinkedHashMap<>();

		Recompte() {
			for (AccioConsulta accio : AccioConsulta.values()) {
				consulta.put(accio.getText(), 0);
			}
			for (AccioMoure accio : AccioMoure.values()) {
				moure.put(accio.getText(), 0);
			}
			for (AccioRipea accio : AccioRipea.values()) {
				ripea.put(accio.getText(), 0);
			}
		}
	}

	/** Línia del CSV: tots els camps entre cometes dobles (les internes es dupliquen) i sense salts de línia. */
	static String liniaCsv(String[] camps) {
		StringBuilder linia = new StringBuilder();
		for (int i = 0; i < camps.length; i++) {
			if (i > 0) {
				linia.append(SEPARADOR);
			}
			String valor = camps[i] != null ? camps[i] : "";
			linia.append('"')
					.append(valor.replace("\"", "\"\"").replace('\r', ' ').replace('\n', ' '))
					.append('"');
		}
		return linia.append(SALT_LINIA).toString();
	}

	/** Camps d'una línia escrita per {@link #liniaCsv}. */
	static List<String> llegirCamps(String linia) {
		List<String> camps = new ArrayList<>();
		StringBuilder actual = new StringBuilder();
		boolean entreCometes = false;
		for (int i = 0; i < linia.length(); i++) {
			char c = linia.charAt(i);
			if (entreCometes) {
				if (c == '"') {
					if (i + 1 < linia.length() && linia.charAt(i + 1) == '"') {
						actual.append('"');
						i++;
					} else {
						entreCometes = false;
					}
				} else {
					actual.append(c);
				}
			} else if (c == '"') {
				entreCometes = true;
			} else if (SEPARADOR.charAt(0) == c) {
				camps.add(actual.toString());
				actual.setLength(0);
			} else {
				actual.append(c);
			}
		}
		camps.add(actual.toString());
		return camps;
	}

	private static byte[] comprimir(Path csv) throws IOException {
		ByteArrayOutputStream zip = new ByteArrayOutputStream();
		try (ZipOutputStream zos = new ZipOutputStream(zip)) {
			zos.putNextEntry(new ZipEntry(csv.getFileName().toString()));
			Files.copy(csv, zos);
			zos.closeEntry();
		}
		return zip.toByteArray();
	}

	private Path crearDirectori(String subdirectori) {
		String base = configHelper.getConfig(PropertyConfig.APP_DATA_DIR);
		if (StringUtils.isBlank(base)) {
			log.error("La propietat " + PropertyConfig.APP_DATA_DIR + " està buida: no es pot desar l'informe del procés"
					+ " de justificants de registre");
			return null;
		}
		try {
			return Files.createDirectories(Paths.get(base, DIRECTORI, subdirectori));
		} catch (IOException ex) {
			log.error("No s'ha pogut crear el directori de l'informe del procés de justificants de registre", ex);
			return null;
		}
	}

	static String nomFitxer(String entitatCodi, String execucio) {
		// Només es reemplacen els caràcters que no admet un nom de fitxer: el codi es torna a llegir del nom
		return PREFIX_FITXER + entitatCodi.replaceAll("[\\\\/:*?\"<>|]", "-") + "_" + execucio + ".csv";
	}

	private static String dataText(String execucio) {
		try {
			return LocalDateTime.parse(execucio, FORMAT_EXECUCIO).format(FORMAT_TEXT);
		} catch (DateTimeParseException ex) {
			return execucio;
		}
	}

}
