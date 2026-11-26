package es.caib.ripea.service.helper;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.caib.ripea.service.intf.config.PropertyConfig;
import es.caib.ripea.service.intf.dto.historic.AccesHistoricDto;
import lombok.extern.slf4j.Slf4j;

/**
 * Registre d'accessos a la consulta d'estadístiques (taules IPA_HIST*): la pantalla JSP {@code /historic} i l'API
 * interna {@code /api/historic}. Serveix per saber, durant una temporada, si algú en fa ús abans de decidir si es
 * deixen de generar els històrics.
 *
 * Si la propietat {@value PropertyConfig#HISTORIC_REGISTRE_ACCES_ACTIU} és {@code true}, cada petició afegeix una línia
 * al CSV {@code <es.caib.ripea.app.data.dir>/informes/historic/acces_historic_<node>_<yyyyMM>.csv}: un fitxer per mes
 * i per node del servidor, perquè dos nodes no escriguin al mateix fitxer si el directori és compartit. El format és el
 * dels informes del procés de justificants de registre ({@link InformeJustificantsRegistreHelper#liniaCsv}): separat
 * per ";", camps entre cometes i UTF-8 amb BOM. Els fitxers no s'esborren ni s'envien.
 *
 * Un error del registre mai no es propaga a la petició: només es deixa al log.
 */
@Slf4j
@Component
public class RegistreAccesHistoricHelper {

	static final String DIRECTORI = "informes/historic";
	static final String[] CAPCALERA = {
			"Data",
			"Aplicació",
			"Mètode HTTP",
			"Ruta",
			"Paràmetres",
			"IP",
			"X-Forwarded-For",
			"Usuari",
			"Rol actual",
			"User-Agent",
			"Estat HTTP",
			"Durada (ms)",
			"Error" };

	private static final String PREFIX_FITXER = "acces_historic_";
	private static final DateTimeFormatter FORMAT_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
	private static final DateTimeFormatter FORMAT_MES = DateTimeFormatter.ofPattern("yyyyMM");

	@Autowired private ConfigHelper configHelper;

	private final String node = nomNode();
	/** Per avisar només un cop al log si no es pot desar per manca de directori. */
	private volatile boolean avisSenseDirectori;

	/** Afegeix l'accés al CSV del mes, si el registre és actiu. No llança mai cap excepció. */
	public void registrar(AccesHistoricDto acces) {
		if (acces == null) {
			return;
		}
		try {
			if (!configHelper.getAsBoolean(PropertyConfig.HISTORIC_REGISTRE_ACCES_ACTIU)) {
				return;
			}
			escriure(acces);
		} catch (Exception ex) {
			log.error("No s'ha pogut desar al registre d'accessos a la consulta d'estadístiques: " + acces, ex);
		}
	}

	/** Sincronitzat: arriben peticions concurrents i totes les del node escriuen al mateix fitxer. */
	private synchronized void escriure(AccesHistoricDto acces) throws IOException {
		Path directori = directori();
		if (directori == null) {
			return;
		}
		LocalDateTime data = aLocalDateTime(acces.getData());
		Path fitxer = directori.resolve(nomFitxer(node, data));
		boolean nou = !Files.exists(fitxer) || Files.size(fitxer) == 0;
		try (BufferedWriter writer = Files.newBufferedWriter(
				fitxer,
				StandardCharsets.UTF_8,
				StandardOpenOption.CREATE,
				StandardOpenOption.APPEND)) {
			if (nou) {
				// BOM perquè Excel llegeixi els accents
				writer.write('﻿');
				writer.write(InformeJustificantsRegistreHelper.liniaCsv(CAPCALERA));
			}
			writer.write(InformeJustificantsRegistreHelper.liniaCsv(camps(acces, data)));
		}
	}

	static String[] camps(AccesHistoricDto acces, LocalDateTime data) {
		return new String[] {
				data.format(FORMAT_DATA),
				acces.getAplicacio(),
				acces.getMetodeHttp(),
				acces.getRuta(),
				acces.getParametres(),
				acces.getIpRemota(),
				acces.getXForwardedFor(),
				acces.getUsuari(),
				acces.getRolActual(),
				acces.getUserAgent(),
				acces.getEstatHttp() != null ? acces.getEstatHttp().toString() : null,
				acces.getDuradaMs() != null ? acces.getDuradaMs().toString() : null,
				acces.getError() };
	}

	static String nomFitxer(String node, LocalDateTime data) {
		return PREFIX_FITXER + node + "_" + data.format(FORMAT_MES) + ".csv";
	}

	private Path directori() {
		String base = configHelper.getConfig(PropertyConfig.APP_DATA_DIR);
		if (StringUtils.isBlank(base)) {
			if (!avisSenseDirectori) {
				avisSenseDirectori = true;
				log.error("La propietat " + PropertyConfig.APP_DATA_DIR + " està buida: no es pot desar el registre"
						+ " d'accessos a la consulta d'estadístiques");
			}
			return null;
		}
		try {
			return Files.createDirectories(Paths.get(base, DIRECTORI));
		} catch (IOException ex) {
			log.error("No s'ha pogut crear el directori del registre d'accessos a la consulta d'estadístiques", ex);
			return null;
		}
	}

	private static LocalDateTime aLocalDateTime(Date data) {
		Date valor = data != null ? data : new Date();
		return LocalDateTime.ofInstant(valor.toInstant(), ZoneId.systemDefault());
	}

	/** Nom del node de JBoss o, si no n'hi ha, el del servidor; només amb caràcters vàlids en un nom de fitxer. */
	static String nomNode() {
		String nom = System.getProperty("jboss.node.name");
		if (StringUtils.isBlank(nom)) {
			try {
				nom = InetAddress.getLocalHost().getHostName();
			} catch (Exception ex) {
				nom = null;
			}
		}
		return StringUtils.isBlank(nom) ? "node" : nom.replaceAll("[^A-Za-z0-9._-]", "-");
	}

}
