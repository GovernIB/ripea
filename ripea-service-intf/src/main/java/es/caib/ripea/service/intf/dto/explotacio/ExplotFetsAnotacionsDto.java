package es.caib.ripea.service.intf.dto.explotacio;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Fets d'explotació d'anotacions obtinguts amb una única consulta agregada sobre ExpedientPeticioEntity.
 *
 * Els camps acabats en "Ahir" contenen el mateix total calculat amb el tall del dia anterior;
 * la dada parcial del dia s'obté restant-los del total (equival a la resta que abans es feia
 * amb dues consultes i el mètode restarDadaMateixaDimensio).
 *
 * IMPORTANT: l'ordre dels camps forma part del contracte amb l'expressió constructora
 * (`select new ...`) de la consulta corresponent a ExplotacioFetsRepository. Si es reordenen
 * o s'afegeixen camps, s'ha d'actualitzar també la consulta.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@AllArgsConstructor
public class ExplotFetsAnotacionsDto {

	private Long entitatId;
	private Long procedimentId;
	private Long organId;
	private String usuariCodi;

	private Long novesTotal;
	private Long noves;
	private Long processadesTotal;
	private Long processadesTotalAhir;
	private Long rebutjadesTotal;
	private Long rebutjadesTotalAhir;

}
