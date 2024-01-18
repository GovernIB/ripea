/**
 * 
 */
package es.caib.ripea.core.api.dto;

import java.io.Serializable;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;


@Getter @Setter
public class UsuariTascaFiltreDto implements Serializable {

	private TascaEstatEnumDto estat;
	private Long expedientId;
	private Date dataInici;
	private Date dataFi;
	private Date dataLimitInici;
	private Date dataLimitFi;

	private static final long serialVersionUID = 1L;
}
