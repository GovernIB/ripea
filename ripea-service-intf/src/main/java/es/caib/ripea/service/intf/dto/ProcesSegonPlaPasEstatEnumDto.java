package es.caib.ripea.service.intf.dto;

/**
 * Estat d'un pas d'un procés en segon pla.
 */
public enum ProcesSegonPlaPasEstatEnumDto {
	EN_CURS,
	OK,
	/** El pas no ha impedit continuar, però hi ha alguna cosa que l'usuari ha de saber. */
	AVIS,
	ERROR
}
