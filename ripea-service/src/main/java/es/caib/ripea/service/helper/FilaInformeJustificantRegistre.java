package es.caib.ripea.service.helper;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;
import java.util.function.Consumer;

import org.apache.commons.lang3.exception.ExceptionUtils;

import es.caib.plugins.arxiu.api.ArxiuNotFoundException;
import es.caib.ripea.persistence.entity.CarpetaEntity;
import es.caib.ripea.persistence.entity.DocumentEntity;
import es.caib.ripea.persistence.entity.EntitatEntity;
import es.caib.ripea.persistence.entity.ExpedientEntity;
import es.caib.ripea.persistence.entity.ExpedientPeticioEntity;
import es.caib.ripea.persistence.entity.MetaExpedientEntity;
import es.caib.ripea.persistence.entity.RegistreEntity;
import es.caib.ripea.service.intf.utils.Utils;
import lombok.Getter;

/**
 * Fila de l'informe del procés en segon pla que incorpora els justificants de registre: què ha passat amb el
 * justificant d'una anotació (veure {@link InformeJustificantsRegistreHelper}).
 *
 * La fila de l'anotació que s'està tractant és del fil: el codi del procés hi afegeix les dades amb
 * {@link #actualitzar}. Fora del procés (p.ex. quan un usuari accepta una anotació) no hi ha cap fila i
 * {@link #actualitzar} no fa res. Les dades es copien en el moment (no es guarden entitats), perquè la fila s'escriu
 * quan la transacció de l'element ja ha acabat, també si ha fet rollback.
 */
@Getter
public class FilaInformeJustificantRegistre {

	/**
	 * Valor d'una de les columnes d'acció de l'informe: el text amb què surt al CSV i al recompte del correu, i la
	 * descripció amb què s'explica a la llegenda del correu.
	 */
	public interface ValorAccio {
		String getText();
		String getDescripcio();
	}

	/** Avís que mostra el document quan el justificant no s'ha pogut moure a l'expedient de RIPEA a l'Arxiu. */
	static final String AVIS_NO_MOGUT = "\"No s'ha pogut moure el justificant de registre a l'expedient documental de RIPEA\"";

	/** Consulta del justificant original a l'Arxiu (sèrie de Distribució). */
	public enum AccioConsulta implements ValorAccio {
		TROBAT("S'ha obtingut de l'Arxiu el justificant original, amb el contingut, per incorporar-lo a l'expedient. Si"
				+ " després falla un altre pas, l'anotació acaba amb ERROR."),
		NO_TROBAT("L'Arxiu respon que el justificant no existeix. L'anotació acaba amb ERROR."),
		ERROR("Error en consultar el justificant a l'Arxiu (per exemple, temps d'espera esgotat o servei no disponible)."
				+ " L'anotació acaba amb ERROR."),
		NO_CONSULTAT("No ha calgut consultar l'Arxiu: l'expedient ja tenia el justificant, el justificant o la carpeta del"
				+ " registre estan esborrats, ja hi ha un document amb el mateix nom de fitxer o l'anotació no s'ha tractat."
				+ " També surt si l'anotació ha fallat abans de consultar-lo (per exemple, si l'expedient no està guardat a"
				+ " l'Arxiu).");

		private final String descripcio;

		AccioConsulta(String descripcio) {
			this.descripcio = descripcio;
		}

		@Override
		public String getText() {
			return name();
		}

		@Override
		public String getDescripcio() {
			return descripcio;
		}
	}

	/** Moviment del justificant a l'expedient de RIPEA a l'Arxiu. */
	public enum AccioMoure implements ValorAccio {
		MOGUT("MOGUT", "S'ha mogut el justificant a l'Arxiu: si s'ha creat en aquesta execució (CREAT_...), de la sèrie de"
				+ " Distribució a la carpeta del registre de l'expedient de RIPEA; si ja era a l'expedient (MOGUT_...), de"
				+ " l'arrel o d'una altra carpeta a la carpeta del registre."),
		JA_A_L_ARXIU("JA_A_L_ARXIU", "En crear el justificant, al destí de l'Arxiu ja hi havia un document amb el mateix"
				+ " nom (d'un intent anterior): el justificant passa a ser aquest document i no es mou res."),
		ERROR_PERMISOS_WARN("ERROR_PERMISOS (WARN)", "En crear el justificant, l'Arxiu no l'ha deixat moure a l'expedient"
				+ " de RIPEA per falta de permisos. El document s'ha creat igualment, apunta al justificant original de"
				+ " Distribució i es mostra amb l'avís " + AVIS_NO_MOGUT + ". El moviment es pot tornar a intentar amb"
				+ " \"Guardar en arxiu\" des del document."),
		ERROR_WARN("ERROR (WARN)", "En crear el justificant, l'Arxiu no l'ha deixat moure a l'expedient de RIPEA per un"
				+ " error que no és de permisos. El document s'ha creat igualment, apunta al justificant original de"
				+ " Distribució i es mostra amb l'avís " + AVIS_NO_MOGUT + ". El moviment es pot tornar a intentar amb"
				+ " \"Guardar en arxiu\" des del document."),
		ERROR_PERMISOS_NO_MOGUT("ERROR_PERMISOS (NO MOGUT)", "En canviar de carpeta un justificant que ja era a l'expedient"
				+ " de RIPEA a l'Arxiu, l'Arxiu no l'ha deixat moure per falta de permisos: no s'ha mogut ni a l'Arxiu ni a"
				+ " RIPEA, i l'anotació acaba amb ERROR."),
		ERROR_NO_MOGUT("ERROR (NO MOGUT)", "En canviar de carpeta un justificant que ja era a l'expedient de RIPEA a"
				+ " l'Arxiu, l'Arxiu no l'ha deixat moure per un error que no és de permisos: no s'ha mogut ni a l'Arxiu ni a"
				+ " RIPEA, i l'anotació acaba amb ERROR."),
		WARN_ANTERIOR("WARN_ANTERIOR", "El justificant ja tenia l'avís " + AVIS_NO_MOGUT + " d'un intent anterior: a"
				+ " l'Arxiu continua a la sèrie de Distribució. El procés no torna a intentar moure'l a l'Arxiu (es pot fer"
				+ " amb \"Guardar en arxiu\" des del document) i, si calia canviar-lo de carpeta, només ho fa a RIPEA. No"
				+ " compta entre els justificants no moguts per falta de permisos d'aquesta execució."),
		NO_APLICA("NO_APLICA", "No s'ha fet ni comprovat res a l'Arxiu: el justificant ja era a la carpeta del registre,"
				+ " només s'ha mogut a RIPEA, no s'ha incorporat (esborrat, carpeta esborrada, ja incorporat amb un altre"
				+ " tipus o anotació no tractada) o l'anotació ha fallat abans d'arribar a l'Arxiu.");

		private final String text;
		private final String descripcio;

		AccioMoure(String text, String descripcio) {
			this.text = text;
			this.descripcio = descripcio;
		}

		@Override
		public String getText() {
			return text;
		}

		@Override
		public String getDescripcio() {
			return descripcio;
		}
	}

	/** Què s'ha fet amb el justificant a RIPEA. */
	public enum AccioRipea implements ValorAccio {
		CREAT_A_CARPETA_EXISTENT("L'expedient no tenia el justificant: s'ha descarregat de l'Arxiu i s'ha creat a la"
				+ " carpeta del registre."),
		CREAT_A_CARPETA_NOVA("L'expedient no tenia el justificant ni la carpeta del registre: s'ha creat la carpeta"
				+ " \"Registre entrada: <número de registre>\" i s'hi ha creat el justificant, descarregat de l'Arxiu."),
		MOGUT_A_CARPETA_EXISTENT("El justificant ja era a l'expedient, però a l'arrel o a una altra carpeta (també la"
				+ " d'una altra anotació): s'ha mogut a la carpeta del registre. Si també s'ha mogut a l'Arxiu, ho indica la"
				+ " columna \"Acció moure\"."),
		MOGUT_A_CARPETA_NOVA("El justificant ja era a l'expedient, però no hi havia carpeta del registre: s'ha creat la"
				+ " carpeta \"Registre entrada: <número de registre>\" i s'hi ha mogut el justificant. Si també s'ha mogut a"
				+ " l'Arxiu, ho indica la columna \"Acció moure\"."),
		JA_A_LA_CARPETA("El justificant ja era a la carpeta del registre: no s'ha fet res."),
		CARPETA_ESBORRADA_JUSTIFICANT_ESBORRAT("La carpeta del registre està esborrada, però el justificant no: s'ha"
				+ " esborrat el justificant, només a RIPEA, com la resta del contingut de la carpeta."),
		CARPETA_ESBORRADA_NO_DESCARREGAT("La carpeta del registre està esborrada i l'expedient no té el justificant: no"
				+ " s'ha descarregat."),
		JUSTIFICANT_ESBORRAT("L'expedient només té el justificant esborrat: no es torna a incorporar ni es mou."),
		JA_INCORPORAT_AMB_ALTRE_TIPUS("L'expedient no té el justificant, però sí un document (esborrat o no) amb el nom de"
				+ " fitxer que el procés dona al justificant (REGISTRE_JUSTIFICANT_ENTRADA_<id anotació>_<número de"
				+ " registre>.pdf): es considera que ja s'hi va incorporar amb un altre tipus de document o un altre número"
				+ " de registre, i no s'ha fet res."),
		NO_TRACTAT("L'anotació no té justificant a l'Arxiu o no té expedient: no s'ha fet res. El motiu és a la columna"
				+ " \"Detall\"."),
		ERROR("L'anotació ha fallat i no s'ha canviat res a l'expedient. El motiu és a la columna \"Detall\" (per exemple,"
				+ " més d'un justificant actiu, un altre document amb el mateix nom a la carpeta, expedient no guardat a"
				+ " l'Arxiu o error de l'Arxiu). Si ha fallat en incorporar el justificant, l'error queda a l'anotació i es"
				+ " pot tornar a intentar amb \"Afegir justificant a l'expedient\". El procés no torna a tractar l'anotació a"
				+ " les execucions següents, llevat que s'hagi aturat per errors de disponibilitat.");

		private final String descripcio;

		AccioRipea(String descripcio) {
			this.descripcio = descripcio;
		}

		@Override
		public String getText() {
			return name();
		}

		@Override
		public String getDescripcio() {
			return descripcio;
		}
	}

	/** Columnes del CSV, en el mateix ordre que {@link #getCamps()}. */
	public static final String[] CAPCALERA = {
			"Entitat",
			"ID anotació",
			"Identificador anotació",
			"UUID justificant (Distribució)",
			"ID expedient",
			"Número expedient",
			"ID procediment",
			"Procediment",
			"ID document",
			"Nom document",
			"Nom fitxer",
			"UUID document",
			"Acció consulta",
			"Acció moure",
			"Acció RIPEA",
			"ID carpeta",
			"Carpeta",
			"Detall"
	};
	public static final int COLUMNA_ACCIO_CONSULTA = 12;
	public static final int COLUMNA_ACCIO_MOURE = 13;
	public static final int COLUMNA_ACCIO_RIPEA = 14;

	private static final int LONGITUD_MAXIMA_DETALL = 2000;
	private static final ThreadLocal<FilaInformeJustificantRegistre> ACTUAL = new ThreadLocal<>();

	private final Long anotacioId;
	private String entitatCodi;
	private String identificadorAnotacio;
	private String uuidJustificant;
	private Long expedientId;
	private String expedientNumero;
	private Long procedimentId;
	private String procedimentNom;
	private Long documentId;
	private String documentNom;
	private String documentFitxerNom;
	private String documentUuid;
	/** El document s'ha creat en aquest element: si l'element falla, s'ha desfet. */
	private boolean documentNou;
	private Long carpetaId;
	private String carpetaNom;
	private AccioConsulta accioConsulta = AccioConsulta.NO_CONSULTAT;
	private AccioMoure accioMoure = AccioMoure.NO_APLICA;
	private AccioRipea accioRipea;
	private String errorMoure;
	private String detall;

	FilaInformeJustificantRegistre(Long anotacioId) {
		this.anotacioId = anotacioId;
	}

	/** Comença la fila de l'anotació que es tracta al fil actual. */
	public static FilaInformeJustificantRegistre iniciar(Long anotacioId) {
		FilaInformeJustificantRegistre fila = new FilaInformeJustificantRegistre(anotacioId);
		ACTUAL.set(fila);
		return fila;
	}

	/** Aplica el canvi a la fila del fil actual, si n'hi ha (només dins el procés en segon pla). */
	public static void actualitzar(Consumer<FilaInformeJustificantRegistre> canvi) {
		FilaInformeJustificantRegistre fila = ACTUAL.get();
		if (fila != null) {
			canvi.accept(fila);
		}
	}

	/** Treu la fila del fil. Els fils del planificador es reutilitzen i la tasca següent no l'ha d'heretar. */
	public static void treure() {
		ACTUAL.remove();
	}

	public void setAnotacio(ExpedientPeticioEntity peticio) {
		identificadorAnotacio = peticio.getIdentificador();
		RegistreEntity registre = peticio.getRegistre();
		if (registre != null) {
			uuidJustificant = registre.getJustificantArxiuUuid();
		}
		ExpedientEntity expedient = peticio.getExpedient();
		if (expedient != null) {
			expedientId = expedient.getId();
			expedientNumero = expedient.getNumero();
			MetaExpedientEntity procediment = expedient.getMetaExpedient();
			if (procediment != null) {
				procedimentId = procediment.getId();
				procedimentNom = procediment.getNom();
			}
			EntitatEntity entitat = expedient.getEntitat();
			if (entitat != null) {
				entitatCodi = entitat.getCodi();
			}
		}
	}

	/** Dades actuals del document (s'ha de tornar a cridar si canvia, p.ex. l'uuid després de moure'l). */
	public void setDocument(DocumentEntity document, boolean nou) {
		documentId = document.getId();
		documentNom = document.getNom();
		documentFitxerNom = document.getFitxerNom();
		documentUuid = document.getArxiuUuid();
		documentNou = nou;
	}

	public void setCarpeta(CarpetaEntity carpeta) {
		carpetaId = carpeta.getId();
		carpetaNom = carpeta.getNom();
	}

	public void setAccioConsulta(AccioConsulta accioConsulta) {
		this.accioConsulta = accioConsulta;
	}

	/** La consulta del justificant a l'Arxiu ha fallat: distingeix si l'Arxiu no el troba. */
	public void setErrorConsulta(Throwable error) {
		accioConsulta = isNoTrobat(error) ? AccioConsulta.NO_TROBAT : AccioConsulta.ERROR;
	}

	public void setAccioMoure(AccioMoure accioMoure) {
		this.accioMoure = accioMoure;
	}

	/**
	 * El moviment del justificant a l'expedient de RIPEA a l'Arxiu ha fallat.
	 *
	 * @param ambAvis el document queda amb l'avís justificantMoureError (en incorporar-lo), o no s'ha mogut enlloc
	 *                (en recol·locar un justificant que ja era a l'expedient de l'Arxiu).
	 */
	public void setErrorMoure(Throwable error, boolean ambAvis) {
		boolean permisos = isErrorPermisos(error);
		if (ambAvis) {
			accioMoure = permisos ? AccioMoure.ERROR_PERMISOS_WARN : AccioMoure.ERROR_WARN;
		} else {
			accioMoure = permisos ? AccioMoure.ERROR_PERMISOS_NO_MOGUT : AccioMoure.ERROR_NO_MOGUT;
		}
		errorMoure = missatge(error);
	}

	public void setAccioRipea(AccioRipea accioRipea) {
		this.accioRipea = accioRipea;
	}

	/** L'element ha acabat bé: el detall és l'error en moure a l'Arxiu, si n'hi ha hagut, o el resultat. */
	void finalitzarOk(String resultat) {
		if (accioRipea == null) {
			accioRipea = AccioRipea.NO_TRACTAT;
		}
		detall = Utils.left(errorMoure != null ? "Moure: " + errorMoure : resultat, LONGITUD_MAXIMA_DETALL);
	}

	/**
	 * L'element ha fallat. Si el document s'havia creat en aquest element, la seva transacció l'ha desfet i no se'n
	 * donen les dades.
	 */
	void finalitzarError(Throwable error) {
		accioRipea = AccioRipea.ERROR;
		if (documentNou) {
			documentId = null;
			documentNom = null;
			documentFitxerNom = null;
			documentUuid = null;
		}
		String missatge = missatge(error);
		detall = Utils.left(errorMoure != null ? missatge + " | Moure: " + errorMoure : missatge, LONGITUD_MAXIMA_DETALL);
	}

	/** Valors de la fila per al CSV, en l'ordre de {@link #CAPCALERA}. */
	public String[] getCamps() {
		return new String[] {
				entitatCodi,
				text(anotacioId),
				identificadorAnotacio,
				uuidJustificant,
				text(expedientId),
				expedientNumero,
				text(procedimentId),
				procedimentNom,
				text(documentId),
				documentNom,
				documentFitxerNom,
				documentUuid,
				accioConsulta.getText(),
				accioMoure.getText(),
				accioRipea != null ? accioRipea.getText() : null,
				text(carpetaId),
				carpetaNom,
				detall
		};
	}

	/**
	 * Indica si l'error és de permisos de l'Arxiu. L'Arxiu no en torna cap codi: es reconeix pel missatge de
	 * qualsevol excepció de la cadena de causes ("permission of Write", "permission of Delete", "Access Denied").
	 */
	static boolean isErrorPermisos(Throwable error) {
		for (Throwable t : causes(error)) {
			String missatge = t.getMessage() != null ? t.getMessage().toLowerCase() : "";
			if (missatge.contains("permission") || missatge.contains("access denied") || missatge.contains("accessdenied")) {
				return true;
			}
		}
		return false;
	}

	/** Indica si l'Arxiu no troba el document: ArxiuNotFoundException o un missatge de "no trobat" a la cadena. */
	static boolean isNoTrobat(Throwable error) {
		for (Throwable t : causes(error)) {
			if (t instanceof ArxiuNotFoundException) {
				return true;
			}
			String missatge = t.getMessage() != null ? t.getMessage().toLowerCase() : "";
			if (missatge.contains("no trobat") || missatge.contains("not found") || missatge.contains("no encontrado")) {
				return true;
			}
		}
		return false;
	}

	/** Missatge de l'error, amb el de la causa original si no hi és. */
	static String missatge(Throwable error) {
		String missatge = error.getMessage() != null ? error.getMessage() : error.getClass().getName();
		Throwable causa = ExceptionUtils.getRootCause(error);
		if (causa != null && causa != error && causa.getMessage() != null && !missatge.contains(causa.getMessage())) {
			missatge = missatge + " (" + causa.getMessage() + ")";
		}
		return missatge;
	}

	/** L'error i totes les seves causes, sense repetir-ne cap (una causa cíclica no penja el recorregut). */
	private static Set<Throwable> causes(Throwable error) {
		Set<Throwable> causes = Collections.newSetFromMap(new IdentityHashMap<>());
		Throwable t = error;
		while (t != null && causes.add(t)) {
			t = t.getCause();
		}
		return causes;
	}

	private static String text(Long valor) {
		return valor != null ? valor.toString() : null;
	}

}
