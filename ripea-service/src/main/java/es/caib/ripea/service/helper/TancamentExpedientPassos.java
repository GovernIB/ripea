package es.caib.ripea.service.helper;

/**
 * Codis dels passos i fases que el tancament d'un expedient informa al registre de progrés
 * (veure {@link ProcesSegonPlaContext}).
 *
 * El text el posa el front: procesSegonPla.pas.&lt;codi&gt;, procesSegonPla.fase.&lt;fase&gt; i
 * procesSegonPla.resultat.&lt;resultat&gt; a les traduccions (translationCa/Es/En.ts). Un codi nou necessita la seva
 * traducció.
 */
public final class TancamentExpedientPassos {

	private TancamentExpedientPassos() {
	}

	// Passos
	public static final String COMPROVAR = "TANCAMENT_COMPROVAR";
	public static final String NOTIFICACIONS_CADUCADES = "TANCAMENT_NOTIFICACIONS_CADUCADES";
	public static final String REPROCESSAR_ANNEXOS = "TANCAMENT_REPROCESSAR_ANNEXOS";
	/** Paràmetre: document. */
	public static final String FIRMAR_DOCUMENT = "TANCAMENT_FIRMAR_DOCUMENT";
	public static final String ESBORRAR_NO_SELECCIONATS = "TANCAMENT_ESBORRAR_NO_SELECCIONATS";
	public static final String MARCAR_DEFINITIUS = "TANCAMENT_MARCAR_DEFINITIUS";
	public static final String ESBORRAR_ESBORRANYS_ARXIU = "TANCAMENT_ESBORRAR_ESBORRANYS_ARXIU";
	public static final String TANCAR_EXPEDIENT = "TANCAMENT_TANCAR_EXPEDIENT";

	// Avisos
	/** Paràmetres: notificacio, document. */
	public static final String AVIS_NOTIFICACIO_NO_ACTUALITZADA = "TANCAMENT_AVIS_NOTIFICACIO_NO_ACTUALITZADA";
	/** Paràmetre: document. */
	public static final String AVIS_ANNEX_FIRMA_SERVIDOR = "TANCAMENT_AVIS_ANNEX_FIRMA_SERVIDOR";

	// Línies informatives (OK)
	/** Paràmetre: document. */
	public static final String INFO_ANNEX_MOGUT = "TANCAMENT_INFO_ANNEX_MOGUT";

	// Resultats d'un pas (es mostren a continuació del text del pas)
	public static final String RESULTAT_CAP_NOTIFICACIO = "CAP_NOTIFICACIO";
	/** Paràmetre: num. */
	public static final String RESULTAT_NOTIFICACIONS_ACTUALITZADES = "NOTIFICACIONS_ACTUALITZADES";
	public static final String RESULTAT_CAP_ANNEX = "CAP_ANNEX";
	/** Paràmetres: num, moguts, perFirmar. */
	public static final String RESULTAT_ANNEXOS_REPROCESSATS = "ANNEXOS_REPROCESSATS";

	// Fases
	/** Paràmetre: document. */
	public static final String FASE_MOURE_ANNEX = "MOURE_ANNEX";
	public static final String FASE_COMPROVAR_FIRMES = "COMPROVAR_FIRMES";
	public static final String FASE_ELIMINAR_FIRMES = "ELIMINAR_FIRMES";
	public static final String FASE_FIRMAR_SERVIDOR = "FIRMAR_SERVIDOR";
	public static final String FASE_DESAR_ARXIU = "DESAR_ARXIU";
	public static final String FASE_TANCAR_ARXIU = "TANCAR_ARXIU";

}
