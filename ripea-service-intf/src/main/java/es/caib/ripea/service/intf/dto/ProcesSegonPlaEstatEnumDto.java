package es.caib.ripea.service.intf.dto;

/**
 * Estat global d'un procés en segon pla.
 */
public enum ProcesSegonPlaEstatEnumDto {
	/** Registrat i en execució (o esperant un fil lliure per començar). */
	EN_CURS,
	/** Acabat sense errors. */
	FINALITZAT,
	/** Acabat amb error: el detall és a errorMsg i als passos marcats amb error. */
	ERROR
}
