package es.caib.ripea.service.intf.dto.explotacio;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Fets d'explotació d'anotacions obtinguts amb una única consulta agregada sobre ExpedientPeticioEntity.
 *
 * Només conté indicadors acumulats a la data demanada: les dades diàries es calculen després,
 * restant a cada total el mateix indicador de la mateixa dimensió al dia anterior
 * (SegonPlaServiceImpl.calcularParcialsDiaris).
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
	private Long processadesTotal;
	private Long rebutjadesTotal;

}
