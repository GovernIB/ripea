package es.caib.ripea.service.resourcehelper.inici;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import es.caib.ripea.service.intf.config.BaseConfig;

/**
 * Catàleg de targetes de la pantalla d'inici (issue #1510, versió bàsica: sense configuració per usuari).
 *
 * Cada rol veu sempre les mateixes targetes, en aquest ordre (files de 4). Si dos rols comparteixen una targeta,
 * surt a la mateixa posició en tots dos (p. ex. «Anotacions pendents», sempre la quarta). El codi és el que rep el front,
 * que en deriva la presentació (títol, descripció, icona, color i pantalla de destinació).
 */
public enum IndicadorInici {

	// Expedients (llistat d'expedients)
	/** Expedients oberts agafats per l'usuari actual. */
	EXPEDIENTS_AGAFATS,
	/** Expedients oberts creats fa més dels dies de la propietat dels expedients antics (90 per defecte). */
	EXPEDIENTS_ANTICS,
	/** Expedients oberts. */
	EXPEDIENTS_OBERTS,
	/** Expedients tancats els darrers 7 dies. */
	EXPEDIENTS_TANCATS_RECENTS,
	/** Alertes no llegides dels expedients visibles (les que fan aparèixer la icona del llistat). */
	ALERTES_NO_LLEGIDES,

	// Anotacions (llistat d'anotacions)
	/** Anotacions pendents de processar: el mateix comptador que el menú. */
	ANOTACIONS_PENDENTS,

	// Tasques de l'usuari (Les meves tasques): responsable, observador o delegat, també d'expedients tancats
	/** Tasques iniciades o agafades. */
	TASQUES_EN_CURS,
	/** Tasques pendents d'iniciar. */
	TASQUES_PENDENTS,
	/** Tasques pendents amb la data límit dins del preavís (propietat de preavís, 3 dies per defecte). */
	TASQUES_VENCIMENT_PROPER,
	/** Tasques pendents amb la data límit vençuda. */
	TASQUES_FORA_TERMINI,

	// Tasques de l'entitat (Consulta de tasques assignades)
	/** Tasques pendents de l'entitat sense modificacions ni comentaris els darrers dies de la propietat (7 per defecte). */
	TASQUES_SENSE_ACTIVITAT,

	// Procediments (llistat de procediments): només amb la revisió de procediments activa
	PROCEDIMENTS_PENDENTS_REVISIO,
	PROCEDIMENTS_EN_DISSENY,
	PROCEDIMENTS_REBUTJATS,
	PROCEDIMENTS_REVISATS,

	// Procediments de l'entitat (llistat de procediments): parteixen el total en dues targetes
	/** Procediments actius i, amb la revisió activa, revisats: els que es poden fer servir per crear expedients. */
	PROCEDIMENTS_ACTIUS,
	/** La resta: inactius o, amb la revisió activa, no revisats (en disseny, pendents de revisió o rebutjats). */
	PROCEDIMENTS_INACTIUS,

	// Superadministrador (monitor d'integracions, registre d'excepcions i monitor del sistema)
	/** Crides a integracions amb error les darreres 24 hores. */
	INTEGRACIONS_ERROR,
	/** Registres d'excepció amb alguna aparició les darreres 24 hores. */
	EXCEPCIONS,
	/** Memòria heap lliure de la JVM del node que respon. */
	MEMORIA_LLIURE,
	/** Espai lliure del disc arrel del node que respon. */
	DISC_LLIURE;

	/**
	 * Targetes del rol, en l'ordre en què s'han de mostrar.
	 *
	 * @param revisioActiva si la revisió de procediments està activada: sense, el revisor i el dissenyador no
	 *                      tenen cap targeta (només la llista de comentaris).
	 */
	public static List<IndicadorInici> targetes(String rol, boolean revisioActiva) {
		if (rol == null) {
			return Collections.emptyList();
		}
		switch (rol) {
			case BaseConfig.ROLE_USER:
				return Arrays.asList(
						EXPEDIENTS_AGAFATS, EXPEDIENTS_ANTICS, ALERTES_NO_LLEGIDES, ANOTACIONS_PENDENTS,
						TASQUES_EN_CURS, TASQUES_PENDENTS, TASQUES_VENCIMENT_PROPER, TASQUES_FORA_TERMINI);
			case BaseConfig.ROLE_ORGAN_ADMIN:
				// Sense les tasques: «Les meves tasques» no és accessible per a aquest rol
				return Arrays.asList(
						EXPEDIENTS_AGAFATS, EXPEDIENTS_ANTICS, ALERTES_NO_LLEGIDES, ANOTACIONS_PENDENTS);
			case BaseConfig.ROLE_ADMIN:
				return Arrays.asList(
						EXPEDIENTS_OBERTS, EXPEDIENTS_TANCATS_RECENTS, TASQUES_SENSE_ACTIVITAT, ANOTACIONS_PENDENTS);
			case BaseConfig.ROLE_ADMIN_LECTURA:
				// Només el que pot consultar: no té accés a anotacions ni a la consulta de tasques
				return Arrays.asList(
						EXPEDIENTS_OBERTS, EXPEDIENTS_TANCATS_RECENTS, PROCEDIMENTS_ACTIUS, PROCEDIMENTS_INACTIUS);
			case BaseConfig.ROLE_SUPER:
				return Arrays.asList(
						INTEGRACIONS_ERROR, EXCEPCIONS, MEMORIA_LLIURE, DISC_LLIURE);
			case BaseConfig.ROLE_REVISIO:
			case BaseConfig.ROLE_DISSENY:
				return revisioActiva
						? Arrays.asList(PROCEDIMENTS_PENDENTS_REVISIO, PROCEDIMENTS_EN_DISSENY, PROCEDIMENTS_REBUTJATS, PROCEDIMENTS_REVISATS)
						: Collections.emptyList();
			default:
				return Collections.emptyList();
		}
	}

}
