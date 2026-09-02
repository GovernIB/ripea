package es.caib.ripea.service.intf.dto.explotacio;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Fets d'explotació d'expedients creats (ancorats a la data de creació) obtinguts amb una única consulta agregada sobre ExpedientEntity.
 *
 * IMPORTANT: l'ordre dels camps forma part del contracte amb l'expressió constructora
 * (`select new ...`) de la consulta corresponent a ExplotacioFetsRepository. Si es reordenen
 * o s'afegeixen camps, s'ha d'actualitzar també la consulta.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@AllArgsConstructor
public class ExplotFetsExpedientsCreatsDto {

	private Long entitatId;
	private Long procedimentId;
	private Long organId;
	private String usuariCodi;

	private Long expedientsCreatsTotal;

}
