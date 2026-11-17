package es.caib.ripea.service.intf.service;

import es.caib.ripea.service.intf.dto.ProcesSegonPlaDto;

/**
 * Consulta de l'estat dels processos en segon pla amb seguiment en temps real (p.ex. el tancament d'un
 * expedient des de REACT). El llançament de cada procés el fa l'acció corresponent del recurs; aquest
 * servei només exposa el progrés, de manera comuna per a tots els tipus de procés.
 */
public interface ProcesSegonPlaService {

	/**
	 * Retorna l'estat complet (amb tots els passos) d'un procés. Només el pot consultar l'usuari que l'ha llançat.
	 *
	 * @param operacioId identificador retornat en llançar el procés.
	 * @return l'estat del procés.
	 * @throws es.caib.ripea.service.intf.exception.NotFoundException si no existeix o ja s'ha descartat de memòria.
	 * @throws es.caib.ripea.service.intf.exception.PermissionDeniedException si el procés és d'un altre usuari.
	 */
	ProcesSegonPlaDto consultar(String operacioId);

}
