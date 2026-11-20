package es.caib.ripea.service.resourcehelper.inici;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import es.caib.ripea.service.intf.config.BaseConfig;

/**
 * Catàleg de llistes resum (mini-graelles) de la pantalla d'inici (issue #1510, versió bàsica: sense configuració
 * per usuari). Cada rol veu sempre les mateixes llistes, en aquest ordre (dues columnes). El codi és el que rep el
 * front, que en deriva el títol, la descripció, les columnes i els enllaços.
 */
public enum LlistaInici {

	/** Tasques pendents de «Les meves tasques», per data límit (les vençudes primer; les que no en tenen, al final). */
	TASQUES_PER_DATA_LIMIT,
	/** Expedients que l'acció massiva de tancament permet tancar, del més antic al més recent. */
	EXPEDIENTS_LLESTOS_TANCAR,
	/** Tasques pendents de l'entitat sense activitat, de la que fa més temps que no en té a la que menys. */
	TASQUES_SENSE_ACTIVITAT,
	/** Darreres crides a integracions (correctes i amb error), de la més recent a la més antiga. */
	DARRERES_INTEGRACIONS,
	/** Darreres excepcions registrades, de la més recent a la més antiga. */
	DARRERES_EXCEPCIONS,
	/** Darrers comentaris dels procediments visibles, del més recent al més antic. */
	DARRERS_COMENTARIS,
	/** Procediments pendents de revisió, del que fa més temps que espera al que menys (només amb la revisió activa). */
	PROCEDIMENTS_PENDENTS_REVISIO,
	/** Procediments rebutjats, del més recent al més antic (només amb la revisió activa). */
	PROCEDIMENTS_REBUTJATS;

	/** Nombre màxim de files de cada llista. */
	public static final int MAXIM_FILES = 10;

	/**
	 * Llistes del rol, en l'ordre en què s'han de mostrar. Si dos rols comparteixen una llista, surt a la mateixa
	 * posició en tots dos (p. ex. «Expedients llestos per tancar», sempre la primera).
	 *
	 * @param revisioActiva si la revisió de procediments està activada: sense, el revisor i el dissenyador només
	 *                      tenen la llista de comentaris.
	 */
	public static List<LlistaInici> llistes(String rol, boolean revisioActiva) {
		if (rol == null) {
			return Collections.emptyList();
		}
		switch (rol) {
			case BaseConfig.ROLE_USER:
				return Arrays.asList(EXPEDIENTS_LLESTOS_TANCAR, TASQUES_PER_DATA_LIMIT);
			case BaseConfig.ROLE_ORGAN_ADMIN:
				return Collections.singletonList(EXPEDIENTS_LLESTOS_TANCAR);
			case BaseConfig.ROLE_ADMIN:
				return Arrays.asList(EXPEDIENTS_LLESTOS_TANCAR, TASQUES_SENSE_ACTIVITAT);
			case BaseConfig.ROLE_SUPER:
				return Arrays.asList(DARRERES_INTEGRACIONS, DARRERES_EXCEPCIONS);
			case BaseConfig.ROLE_REVISIO:
				return ambRevisio(revisioActiva, PROCEDIMENTS_PENDENTS_REVISIO);
			case BaseConfig.ROLE_DISSENY:
				return ambRevisio(revisioActiva, PROCEDIMENTS_REBUTJATS);
			default:
				// L'administrador de lectura no en té cap: no té accés a cap de les pantalles de destinació
				return Collections.emptyList();
		}
	}

	private static List<LlistaInici> ambRevisio(boolean revisioActiva, LlistaInici llistaRevisio) {
		List<LlistaInici> llistes = new ArrayList<>();
		llistes.add(DARRERS_COMENTARIS);
		if (revisioActiva) {
			llistes.add(llistaRevisio);
		}
		return llistes;
	}

}
