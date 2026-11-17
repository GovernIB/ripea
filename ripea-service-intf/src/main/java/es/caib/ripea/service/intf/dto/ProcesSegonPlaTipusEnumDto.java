package es.caib.ripea.service.intf.dto;

/**
 * Tipus de procés que s'executa en segon pla amb seguiment en temps real des de la interfície REACT
 * (veure ProcesSegonPlaHelper).
 *
 * El front tradueix el títol del diàleg de progrés i el missatge de bloqueig amb aquest codi
 * (procesSegonPla.tipus.&lt;TIPUS&gt;.*), i el backend el missatge de bloqueig amb la clau
 * proces.segon.pla.bloquejat.&lt;TIPUS&gt;. Un tipus nou necessita aquestes traduccions.
 */
public enum ProcesSegonPlaTipusEnumDto {
	TANCAMENT_EXPEDIENT
}
