package es.caib.ripea.service.intf.dto.explotacio;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Fets d'explotació de consultes PINBAL obtinguts amb una única consulta agregada sobre ConsultaPinbalEntity.
 *
 * Els camps "ok" i "error" són les dades parcials del dia, comptades pel rang de la data de
 * creació (igual que feia la consulta getPinbalEnviamentsPerDimensio); no s'obtenen per resta.
 *
 * IMPORTANT: l'ordre dels camps forma part del contracte amb l'expressió constructora
 * (`select new ...`) de la consulta corresponent a ExplotacioFetsRepository. Si es reordenen
 * o s'afegeixen camps, s'ha d'actualitzar també la consulta.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@AllArgsConstructor
public class ExplotFetsPinbalDto {

	private Long entitatId;
	private Long procedimentId;
	private Long organId;
	private String usuariCodi;

	private Long okTotal;
	private Long ok;
	private Long errorTotal;
	private Long error;

}
