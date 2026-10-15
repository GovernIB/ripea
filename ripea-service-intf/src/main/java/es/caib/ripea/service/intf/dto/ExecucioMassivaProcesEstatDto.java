package es.caib.ripea.service.intf.dto;

/**
 * Estat del procés d'una execució massiva (capçalera, taula IPA_EXECUCIO_MASSIVA).
 * No s'ha de confondre amb {@link ExecucioMassivaEstatDto}, que és l'estat de cada element.
 *
 * Una execució EN_CURS amb data de cancel·lació informada té la cancel·lació sol·licitada:
 * l'element que s'està processant acaba i la resta passen a ESTAT_CANCELAT.
 */
public enum ExecucioMassivaProcesEstatDto {
	PENDENT,
	EN_CURS,
	FINALITZADA,
	CANCELADA
}
