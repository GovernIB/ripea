package es.caib.ripea.service.intf.dto.explotacio;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Fets d'explotació de notificacions obtinguts amb una única consulta agregada sobre DocumentNotificacioEntity.
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
public class ExplotFetsNotificacionsDto {

	private Long entitatId;
	private Long procedimentId;
	private Long organId;
	private String usuariCodi;

	private Long enviadesTotal;
	private Long pendentsTotal;
	private Long registradesTotal;
	private Long finalitzadesTotal;
	private Long processadesTotal;
	private Long enviadesErrorTotal;
	private Long finalitzadesErrorTotal;

}
