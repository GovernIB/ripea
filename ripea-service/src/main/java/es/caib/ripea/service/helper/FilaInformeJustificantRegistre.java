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

	/** Consulta del justificant original a l'Arxiu (sèrie de Distribució). */
	public enum AccioConsulta {
		TROBAT,
		NO_TROBAT,
		ERROR,
		/** No s'ha consultat: el document ja existia d'una execució anterior, o no es descarrega. */
		NO_CONSULTAT
	}

	/** Moviment del justificant a l'expedient de RIPEA a l'Arxiu. */
	public enum AccioMoure {
		MOGUT("MOGUT"),
		/** El document ja era a la carpeta de l'Arxiu. */
		JA_A_L_ARXIU("JA_A_L_ARXIU"),
		/** No s'ha pogut moure per falta de permisos: el document queda amb l'avís (justificantMoureError). */
		ERROR_PERMISOS_WARN("ERROR_PERMISOS (WARN)"),
		/** No s'ha pogut moure per un altre error: el document queda amb l'avís (justificantMoureError). */
		ERROR_WARN("ERROR (WARN)"),
		/** Justificant ja incorporat sense l'avís que no s'ha pogut moure a la carpeta de l'Arxiu: no s'ha mogut enlloc. */
		ERROR_PERMISOS_NO_MOGUT("ERROR_PERMISOS (NO MOGUT)"),
		ERROR_NO_MOGUT("ERROR (NO MOGUT)"),
		/** Ja tenia l'avís d'una execució anterior: no es reintenta. */
		WARN_ANTERIOR("WARN_ANTERIOR"),
		NO_APLICA("NO_APLICA");

		private final String text;

		AccioMoure(String text) {
			this.text = text;
		}

		public String getText() {
			return text;
		}
	}

	/** Què s'ha fet amb el justificant a RIPEA. */
	public enum AccioRipea {
		CREAT_A_CARPETA_EXISTENT,
		CREAT_A_CARPETA_NOVA,
		MOGUT_A_CARPETA_EXISTENT,
		MOGUT_A_CARPETA_NOVA,
		JA_A_LA_CARPETA,
		CARPETA_ESBORRADA_JUSTIFICANT_ESBORRAT,
		CARPETA_ESBORRADA_NO_DESCARREGAT,
		/** El justificant està esborrat: no es torna a incorporar ni es mou. */
		JUSTIFICANT_ESBORRAT,
		/** Ja hi ha un document amb el mateix nom de fitxer, però amb un altre tipus de document o número de registre. */
		JA_INCORPORAT_AMB_ALTRE_TIPUS,
		/** El procés no ha fet res amb l'anotació (p.ex. no té expedient): el motiu és al detall. */
		NO_TRACTAT,
		ERROR
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
				accioConsulta.name(),
				accioMoure.getText(),
				accioRipea != null ? accioRipea.name() : null,
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
