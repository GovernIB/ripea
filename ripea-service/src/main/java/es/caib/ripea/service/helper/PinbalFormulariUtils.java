package es.caib.ripea.service.helper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.NodeTraversor;
import org.jsoup.select.NodeVisitor;

import es.caib.pinbal.client.recobriment.v2.DadaEspecifica;
import es.caib.pinbal.client.recobriment.v2.DadaTipusEnum;
import es.caib.pinbal.client.recobriment.v2.GrupDadesEspecifiques;
import es.caib.pinbal.client.recobriment.v2.PeticioRespostaSincrona;
import es.caib.ripea.service.intf.dto.PinbalFormulariDto;
import es.caib.ripea.service.intf.dto.PinbalFormulariDto.Camp;
import es.caib.ripea.service.intf.dto.PinbalFormulariDto.CampTipus;
import es.caib.ripea.service.intf.dto.PinbalFormulariDto.Seccio;
import es.caib.ripea.service.intf.dto.PinbalFormulariDto.Validacio;
import es.caib.ripea.service.intf.dto.PinbalFormulariDto.ValidacioOperacio;
import es.caib.ripea.service.intf.dto.PinbalFormulariDto.ValidacioTipus;
import es.caib.ripea.service.intf.dto.PinbalFormulariDto.ValidacioUnitat;

/**
 * Funcions sense estat per al formulari dinàmic de consulta PINBAL: construcció del formulari a partir de les dades
 * específiques i dels grups d'un servei (API v2 de recobriment) i conversió dels valors del formulari a les dades
 * específiques de la petició.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public final class PinbalFormulariUtils {

	/** Província preseleccionada quan el camp no té valor per defecte a PINBAL (Illes Balears). */
	public static final String PROVINCIA_PER_DEFECTE = "07";
	/** País preseleccionat quan el camp no té valor per defecte a PINBAL (Espanya, codi numèric que torna PINBAL). */
	public static final String PAIS_PER_DEFECTE = "724";

	/**
	 * Format de les dates a la petició. PINBAL interpreta les dates de manera permissiva provant primer dd/MM/yyyy i
	 * dd-MM-yyyy: una data en ISO (2026-11-03) es convertiria, sense error, en una data de l'any 9.
	 */
	private static final DateTimeFormatter FORMAT_DATA_PETICIO = DateTimeFormatter.ofPattern("dd/MM/uuuu")
			.withResolverStyle(ResolverStyle.STRICT);
	/** Formats de data acceptats al formulari i als valors per defecte de PINBAL, a més de l'ISO. */
	private static final DateTimeFormatter[] FORMATS_DATA_ENTRADA = {
			DateTimeFormatter.ofPattern("d/M/uuuu").withResolverStyle(ResolverStyle.STRICT),
			DateTimeFormatter.ofPattern("d-M-uuuu").withResolverStyle(ResolverStyle.STRICT) };
	/** Data ISO, amb hora o sense (el component de data del formulari l'envia com a yyyy-MM-dd'T'HH:mm:ss). */
	private static final Pattern DATA_ISO = Pattern.compile("^(\\d{4}-\\d{2}-\\d{2})(T.*)?$");
	/** Clau d'un error de validació de PINBAL sobre una dada específica: ...dadesEspecifiques[path]. */
	private static final Pattern ERROR_DADA_ESPECIFICA = Pattern.compile("dadesEspecifiques\\[(.+)]$");
	/** Durada d'una validació DATA_DIFERENCIA tal com la dona l'API de PINBAL: "6 MESOS" (la unitat és opcional). */
	private static final Pattern DIFERENCIA = Pattern.compile("^\\s*(\\d+)\\s*([a-zA-Z]+)?\\s*$");

	private static final Set<String> BOOLEA_CERT = Set.of("true", "on", "si", "sí", "s", "yes", "1");
	private static final Set<String> BOOLEA_FALS = Set.of("false", "off", "no", "n", "0");

	private PinbalFormulariUtils() {
	}

	/**
	 * Tipus del camp a PINBAL. L'API el dona amb dues propietats: els camps de llista (ENUM, PAIS, PROVINCIA,
	 * MUNICIPI_3 i MUNICIPI_5) arriben com a tipus ENUM amb el tipus concret a enumTipus, i les etiquetes com a tipus
	 * ENUM sense enumTipus. ADJUNT_BINARI i ADJUNT_XML arriben tots dos com a FILE.
	 */
	public static CampTipus tipusCamp(DadaEspecifica dada) {
		DadaTipusEnum tipus = dada.getTipus();
		if (tipus == null) {
			return CampTipus.TEXT;
		}
		switch (tipus) {
		case NUMERIC:
			return CampTipus.NUMERIC;
		case BOOLEAN:
			return CampTipus.BOOLEA;
		case DATE:
			return CampTipus.DATA;
		case DOC_IDENTITAT:
			return CampTipus.DOC_IDENT;
		case FILE:
			return CampTipus.ADJUNT;
		case ENUM:
			if (dada.getEnumTipus() == null) {
				return CampTipus.ETIQUETA;
			}
			switch (dada.getEnumTipus()) {
			case PAIS:
				return CampTipus.PAIS;
			case PROVINCIA:
				return CampTipus.PROVINCIA;
			case MUNICIPI_3:
				return CampTipus.MUNICIPI_3;
			case MUNICIPI_5:
				return CampTipus.MUNICIPI_5;
			default:
				return CampTipus.ENUM;
			}
		default:
			return CampTipus.TEXT;
		}
	}

	/** Text del camp: l'etiqueta o, si no n'hi ha, el nom o, si tampoc, el codi. */
	public static String textCamp(DadaEspecifica dada) {
		String etiqueta = senseBlancs(dada.getEtiqueta());
		if (etiqueta != null) {
			return etiqueta;
		}
		String nom = senseBlancs(dada.getNom());
		return nom != null ? nom : dada.getCodi();
	}

	/**
	 * Construeix el formulari d'un servei. Els camps sense grup van a la secció arrel; els grups segueixen l'arbre
	 * de getGrups, en l'ordre dels grups, i els camps de cada grup van en l'ordre del camp. Un camp amb un grup que no
	 * és a l'arbre té una secció pròpia al final de l'arrel. Les seccions sense camps no s'hi inclouen.
	 *
	 * @param dades
	 *            dades específiques del servei (pot ser null si el servei no en té).
	 * @param grups
	 *            arbre de grups del servei (pot ser null).
	 */
	public static PinbalFormulariDto construirFormulari(
			String serveiCodi,
			List<DadaEspecifica> dades,
			List<GrupDadesEspecifiques> grups) {
		List<DadaEspecifica> totes = dades != null ? dades.stream().filter(Objects::nonNull).collect(Collectors.toList()) : new ArrayList<>();
		Map<String, DadaEspecifica> dadesPerCodi = new HashMap<>();
		totes.forEach(d -> dadesPerCodi.putIfAbsent(d.getCodi(), d));
		List<DadaEspecifica> visibles = totes.stream()
				.filter(PinbalFormulariUtils::esMostra)
				.sorted(Comparator.comparingInt(DadaEspecifica::getOrdre))
				.collect(Collectors.toList());
		Set<String> codisVisibles = visibles.stream().map(DadaEspecifica::getCodi).collect(Collectors.toSet());

		Seccio arrel = new Seccio();
		Map<String, Seccio> seccionsPerCodi = new LinkedHashMap<>();
		afegirSeccions(arrel, grups, seccionsPerCodi);
		for (DadaEspecifica dada: visibles) {
			Seccio seccio = arrel;
			String grup = senseBlancs(dada.getGrup());
			if (grup != null) {
				seccio = seccionsPerCodi.get(grup);
				if (seccio == null) {
					seccio = new Seccio();
					seccio.setCodi(grup);
					seccio.setNom(grup.substring(grup.lastIndexOf('/') + 1));
					arrel.getSubseccions().add(seccio);
					seccionsPerCodi.put(grup, seccio);
				}
			}
			seccio.getCamps().add(toCamp(dada, dadesPerCodi, codisVisibles));
		}
		eliminarSeccionsBuides(arrel);

		PinbalFormulariDto formulari = new PinbalFormulariDto();
		formulari.setServeiCodi(serveiCodi);
		formulari.setArrel(arrel);
		return formulari;
	}

	/**
	 * Dades específiques de la petició a partir dels valors del formulari, per path de la dada:
	 * <ul>
	 * <li>Els camps ocults o no modificables porten el seu valor per defecte, independentment del que arribi.</li>
	 * <li>Les dates s'envien com a dd/MM/yyyy, els booleans com a true/false (un booleà del formulari sense valor
	 * s'envia com a false, com fa el formulari de PINBAL) i els fitxers amb el contingut en base64.</li>
	 * <li>No s'envien les etiquetes, els camps buits ni els valors que no corresponen a cap camp del servei.</li>
	 * <li>Si un document identificatiu no té número, no s'envia el seu tipus de document (el camp condicionant),
	 * com fa el formulari de PINBAL.</li>
	 * </ul>
	 */
	public static Map<String, String> construirDadesEspecifiques(List<DadaEspecifica> dades, Map<String, Object> valors) {
		Map<String, String> resultat = new LinkedHashMap<>();
		if (dades == null) {
			return resultat;
		}
		Map<String, Object> valorsFormulari = valors != null ? valors : new HashMap<>();
		for (DadaEspecifica dada: dades) {
			if (dada == null || dada.getCodi() == null) {
				continue;
			}
			CampTipus tipus = tipusCamp(dada);
			if (tipus == CampTipus.ETIQUETA) {
				continue;
			}
			boolean campDelFormulari = dada.isVisible() && dada.isModificable();
			String valor = valorPeticio(
					dada,
					tipus,
					campDelFormulari ? valorsFormulari.get(dada.getCodi()) : dada.getValorDefecte());
			if (valor == null && tipus == CampTipus.BOOLEA && campDelFormulari) {
				valor = Boolean.FALSE.toString();
			}
			if (valor != null) {
				resultat.put(dada.getCodi(), valor);
			}
		}
		for (DadaEspecifica dada: dades) {
			if (dada != null && tipusCamp(dada) == CampTipus.DOC_IDENT && !resultat.containsKey(dada.getCodi())) {
				String campTipusDocument = senseBlancs(dada.getCampCondicionant());
				if (campTipusDocument != null) {
					resultat.remove(campTipusDocument);
				}
			}
		}
		return resultat;
	}

	/**
	 * Descripció de l'error d'una petició: el missatge de PINBAL i els errors de validació per camp, amb el text del
	 * camp en lloc del path quan l'error és d'una dada específica.
	 */
	public static String descripcioError(PeticioRespostaSincrona resposta, List<DadaEspecifica> dades) {
		if (resposta == null) {
			return "PINBAL no ha retornat cap resposta";
		}
		List<String> parts = new ArrayList<>();
		String missatge = senseBlancs(resposta.getMissatge());
		if (missatge != null) {
			parts.add(missatge);
		}
		if (resposta.getErrorsValidacio() != null) {
			Map<String, String> textsPerCodi = new HashMap<>();
			if (dades != null) {
				dades.stream().filter(Objects::nonNull).forEach(d -> textsPerCodi.putIfAbsent(d.getCodi(), textCamp(d)));
			}
			resposta.getErrorsValidacio().forEach((camp, errors) -> {
				String nomCamp = camp;
				Matcher matcher = camp != null ? ERROR_DADA_ESPECIFICA.matcher(camp) : null;
				if (matcher != null && matcher.find()) {
					nomCamp = textsPerCodi.getOrDefault(matcher.group(1), matcher.group(1));
				}
				parts.add(nomCamp + ": " + (errors != null ? String.join(", ", errors) : ""));
			});
		}
		if (parts.isEmpty()) {
			return "Estat de la petició: " + resposta.getEstat();
		}
		return String.join("; ", parts);
	}

	/**
	 * Data d'un text en dd/MM/yyyy (també d/M/yyyy i d-M-yyyy), en ISO (amb hora o sense) o en el format del camp a
	 * PINBAL.
	 *
	 * @return la data, o null si el text és buit o no és una data vàlida en cap d'aquests formats.
	 */
	static LocalDate parseData(String text, String formatCamp) {
		String data = senseBlancs(text);
		if (data == null) {
			return null;
		}
		Matcher iso = DATA_ISO.matcher(data);
		if (iso.matches()) {
			try {
				return LocalDate.parse(iso.group(1));
			} catch (DateTimeParseException ex) {
				return null;
			}
		}
		for (DateTimeFormatter format: FORMATS_DATA_ENTRADA) {
			try {
				return LocalDate.parse(data, format);
			} catch (DateTimeParseException ex) {
				// Es prova el format següent
			}
		}
		String format = senseBlancs(formatCamp);
		if (format != null) {
			try {
				return LocalDate.parse(data, DateTimeFormatter.ofPattern(format));
			} catch (IllegalArgumentException | DateTimeParseException ex) {
				// Format del camp invàlid o data en un altre format
			}
		}
		return null;
	}

	/** Booleà d'un text (true/on/si/s/yes/1 o false/off/no/n/0), o null si el text és buit o no és cap d'aquests. */
	static Boolean parseBoolea(String text) {
		String valor = senseBlancs(text);
		if (valor == null) {
			return null;
		}
		valor = valor.toLowerCase();
		if (BOOLEA_CERT.contains(valor)) {
			return Boolean.TRUE;
		}
		return BOOLEA_FALS.contains(valor) ? Boolean.FALSE : null;
	}

	private static boolean esMostra(DadaEspecifica dada) {
		if (!dada.isVisible() || dada.getCodi() == null) {
			return false;
		}
		// Una etiqueta només mostra el seu comentari: sense comentari no hi ha res a mostrar
		return tipusCamp(dada) != CampTipus.ETIQUETA || textPla(dada.getComentari()) != null;
	}

	private static void afegirSeccions(Seccio pare, List<GrupDadesEspecifiques> grups, Map<String, Seccio> seccionsPerCodi) {
		if (grups == null) {
			return;
		}
		List<GrupDadesEspecifiques> ordenats = grups.stream()
				.filter(g -> g != null && senseBlancs(g.getCodi()) != null)
				.sorted(Comparator.comparingInt(GrupDadesEspecifiques::getOrdre))
				.collect(Collectors.toList());
		for (GrupDadesEspecifiques grup: ordenats) {
			String codi = grup.getCodi().trim();
			if (seccionsPerCodi.containsKey(codi)) {
				continue;
			}
			Seccio seccio = new Seccio();
			seccio.setCodi(codi);
			seccio.setNom(senseBlancs(grup.getNom()) != null ? grup.getNom().trim() : codi.substring(codi.lastIndexOf('/') + 1));
			seccio.setAjuda(textPla(grup.getAjuda()));
			pare.getSubseccions().add(seccio);
			seccionsPerCodi.put(codi, seccio);
			afegirSeccions(seccio, grup.getSubgrups(), seccionsPerCodi);
		}
	}

	/** @return true si la secció, després d'eliminar-ne les subseccions buides, no té cap camp ni subsecció. */
	private static boolean eliminarSeccionsBuides(Seccio seccio) {
		seccio.getSubseccions().removeIf(PinbalFormulariUtils::eliminarSeccionsBuides);
		return seccio.getCamps().isEmpty() && seccio.getSubseccions().isEmpty();
	}

	private static Camp toCamp(DadaEspecifica dada, Map<String, DadaEspecifica> dadesPerCodi, Set<String> codisVisibles) {
		CampTipus tipus = tipusCamp(dada);
		Camp camp = new Camp();
		camp.setCodi(dada.getCodi());
		camp.setText(textCamp(dada));
		camp.setTipus(tipus);
		camp.setComentari(textPla(dada.getComentari()));
		camp.setObligatori(dada.isObligatori());
		camp.setModificable(dada.isModificable());
		camp.setValorInicial(valorInicial(dada, tipus));
		if (dada.isModificable()) {
			camp.setValidacio(validacio(dada, tipus, dadesPerCodi, codisVisibles));
		}
		if (tipus == CampTipus.MUNICIPI_3 || tipus == CampTipus.MUNICIPI_5) {
			String campProvincia = senseBlancs(dada.getCampCondicionant());
			if (campProvincia != null && codisVisibles.contains(campProvincia)) {
				camp.setCampProvincia(campProvincia);
			} else {
				// La província és la del camp condicionant ocult (el seu valor per defecte), la del valor
				// condicionant o, si no n'hi ha cap, la de per defecte
				DadaEspecifica dadaProvincia = campProvincia != null ? dadesPerCodi.get(campProvincia) : null;
				String provincia = dadaProvincia != null ? senseBlancs(dadaProvincia.getValorDefecte()) : null;
				if (provincia == null) {
					provincia = senseBlancs(dada.getValorCondicionant());
				}
				camp.setProvincia(provincia != null ? provincia : PROVINCIA_PER_DEFECTE);
			}
		}
		return camp;
	}

	/**
	 * Validació del camp per al formulari, a partir de la de PINBAL. No se'n torna cap (PINBAL la seguirà aplicant en
	 * rebre la petició) si no correspon al tipus del camp, si li falta alguna dada o si el camp amb què es compara una
	 * data no és al formulari: el formulari no pot fer referència a un camp que l'usuari no veu.
	 */
	static Validacio validacio(
			DadaEspecifica dada,
			CampTipus tipus,
			Map<String, DadaEspecifica> dadesPerCodi,
			Set<String> codisVisibles) {
		es.caib.pinbal.client.recobriment.v2.Validacio validacioPinbal = dada.getValidacio();
		if (validacioPinbal == null || validacioPinbal.getTipus() == null) {
			return null;
		}
		// Un tipus o una operació que RIPEA no coneix (d'una versió més nova de PINBAL) no s'han de mostrar
		ValidacioTipus validacioTipus = valorEnum(ValidacioTipus.class, validacioPinbal.getTipus().name());
		if (validacioTipus == null) {
			return null;
		}
		Validacio validacio = new Validacio();
		validacio.setTipus(validacioTipus);
		switch (validacioTipus) {
		case EXPRESSIO_REGULAR:
			// Sense retallar: els espais poden formar part de l'expressió
			String expressio = validacioPinbal.getRegexp();
			if (tipus != CampTipus.TEXT || expressio == null || expressio.isEmpty()) {
				return null;
			}
			validacio.setExpressioRegular(expressio);
			return validacio;
		case NUM_MINIM:
		case NUM_MAXIM:
		case NUM_RANG:
			boolean ambMinim = validacioTipus != ValidacioTipus.NUM_MAXIM;
			boolean ambMaxim = validacioTipus != ValidacioTipus.NUM_MINIM;
			if (tipus != CampTipus.NUMERIC
					|| (ambMinim && validacioPinbal.getMin() == null)
					|| (ambMaxim && validacioPinbal.getMax() == null)) {
				return null;
			}
			validacio.setMinim(ambMinim ? validacioPinbal.getMin() : null);
			validacio.setMaxim(ambMaxim ? validacioPinbal.getMax() : null);
			return validacio;
		case DATA_COMPARACIO:
		case DATA_DIFERENCIA:
			String campComparacio = senseBlancs(validacioPinbal.getCampComparacio());
			DadaEspecifica dadaComparacio = campComparacio != null ? dadesPerCodi.get(campComparacio) : null;
			ValidacioOperacio operacio = validacioPinbal.getOperacio() != null
					? valorEnum(ValidacioOperacio.class, validacioPinbal.getOperacio().name())
					: null;
			if (tipus != CampTipus.DATA
					|| operacio == null
					|| dadaComparacio == null
					|| !codisVisibles.contains(campComparacio)) {
				return null;
			}
			validacio.setOperacio(operacio);
			validacio.setCampComparacio(campComparacio);
			validacio.setCampComparacioText(textCamp(dadaComparacio));
			if (validacioTipus == ValidacioTipus.DATA_DIFERENCIA) {
				Matcher diferencia = DIFERENCIA.matcher(validacioPinbal.getDiferencia() != null ? validacioPinbal.getDiferencia() : "");
				if (!diferencia.matches()) {
					return null;
				}
				try {
					validacio.setDiferencia(Integer.valueOf(diferencia.group(1)));
					// Sense unitat, PINBAL la considera en dies
					validacio.setUnitat(diferencia.group(2) != null
							? ValidacioUnitat.valueOf(diferencia.group(2).toUpperCase(Locale.ROOT))
							: ValidacioUnitat.DIES);
				} catch (IllegalArgumentException ex) {
					// Nombre massa gran o unitat desconeguda
					return null;
				}
			}
			return validacio;
		default:
			return null;
		}
	}

	/** Constant de l'enumeració amb aquest nom o null si no n'hi ha cap. */
	private static <E extends Enum<E>> E valorEnum(Class<E> enumClass, String nom) {
		try {
			return Enum.valueOf(enumClass, nom);
		} catch (IllegalArgumentException ex) {
			return null;
		}
	}

	/** Valor inicial del camp al formulari, en el format del component del formulari. */
	private static String valorInicial(DadaEspecifica dada, CampTipus tipus) {
		String valorDefecte = senseBlancs(dada.getValorDefecte());
		if (valorDefecte != null) {
			switch (tipus) {
			case DATA:
				LocalDate data = parseData(valorDefecte, dada.getFormat());
				return data != null ? data.format(DateTimeFormatter.ISO_LOCAL_DATE) + "T00:00:00" : null;
			case BOOLEA:
				Boolean boolea = parseBoolea(valorDefecte);
				return boolea != null ? boolea.toString() : null;
			case ETIQUETA:
			case ADJUNT:
				return null;
			default:
				return valorDefecte;
			}
		}
		if (tipus == CampTipus.PROVINCIA) {
			return PROVINCIA_PER_DEFECTE;
		}
		if (tipus == CampTipus.PAIS) {
			return PAIS_PER_DEFECTE;
		}
		return null;
	}

	/** Valor d'una dada a la petició (null si no s'ha d'enviar). */
	private static String valorPeticio(DadaEspecifica dada, CampTipus tipus, Object valor) {
		if (valor == null) {
			return null;
		}
		switch (tipus) {
		case ADJUNT:
			if (valor instanceof Map) {
				Object contingut = ((Map<?, ?>)valor).get("content");
				return contingut != null ? senseBlancs(contingut.toString()) : null;
			}
			return senseBlancs(valor.toString());
		case BOOLEA:
			Boolean boolea = valor instanceof Boolean ? (Boolean)valor : parseBoolea(valor.toString());
			return boolea != null ? boolea.toString() : null;
		case DATA:
			String text = senseBlancs(valor.toString());
			LocalDate data = parseData(text, dada.getFormat());
			return data != null ? data.format(FORMAT_DATA_PETICIO) : text;
		case NUMERIC:
			if (valor instanceof Number) {
				// Sense zeros decimals sobrants (un 5.0 de JSON s'envia com a 5) ni notació científica
				return new BigDecimal(valor.toString()).stripTrailingZeros().toPlainString();
			}
			return senseBlancs(valor.toString());
		default:
			return senseBlancs(valor.toString());
		}
	}

	private static String senseBlancs(String text) {
		if (text == null) {
			return null;
		}
		String resultat = text.trim();
		return resultat.isEmpty() ? null : resultat;
	}

	/** Etiquetes o entitats HTML: indiquen que un text d'ajuda de PINBAL és HTML (l'editor de PINBAL en genera). */
	private static final Pattern HTML = Pattern.compile("<\\s*/?\\s*[a-zA-Z][^>]*>|&[a-zA-Z]+;|&#[0-9]+;|&#[xX][0-9a-fA-F]+;");

	/**
	 * Text d'ajuda de PINBAL (ajuda dels grups i comentari dels camps) com a text pla: els formularis de RIPEA el
	 * mostren amb els seus estils. Si és HTML se'n treuen les etiquetes, les entitats es converteixen en caràcters i
	 * cada bloc (div, p, títols, elements de llista) o salt de línia (br) passa a ser una línia. Les línies buides no
	 * es conserven. Un text sense etiquetes ni entitats es deixa tal com és.
	 *
	 * @return el text pla, o null si no en queda cap.
	 */
	public static String textPla(String text) {
		String net = senseBlancs(text);
		if (net == null || !HTML.matcher(net).find()) {
			return net;
		}
		StringBuilder resultat = new StringBuilder();
		new NodeTraversor(new NodeVisitor() {
			@Override
			public void head(Node node, int depth) {
				if (node instanceof TextNode) {
					resultat.append(((TextNode)node).text());
				} else if (node instanceof Element && (((Element)node).isBlock() || "br".equals(((Element)node).tagName()))) {
					resultat.append('\n');
				}
			}
			@Override
			public void tail(Node node, int depth) {
				if (node instanceof Element && ((Element)node).isBlock()) {
					resultat.append('\n');
				}
			}
		}).traverse(Jsoup.parseBodyFragment(net).body());
		String pla = Arrays.stream(resultat.toString().replace(' ', ' ').split("\n"))
				.map(linia -> linia.replaceAll("[ \\t]+", " ").trim())
				.filter(linia -> !linia.isEmpty())
				.collect(Collectors.joining("\n"));
		return pla.isEmpty() ? null : pla;
	}

}
