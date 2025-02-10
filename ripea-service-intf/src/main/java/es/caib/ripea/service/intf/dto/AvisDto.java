/**
 * 
 */
package es.caib.ripea.service.intf.dto;

import lombok.Data;

import java.util.Date;

/**
 * Informació d'una avis.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
public class AvisDto {

	private Long id;
	private String assumpte;
	private String missatge;
	private Date dataInici;
	private Date dataFinal;
	private Boolean actiu;
	private AvisNivellEnumDto avisNivell;
	private Boolean avisAdministrador;
	private Long entitatId;

}
