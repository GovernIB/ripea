/**
 *
 */
package es.caib.ripea.service.intf.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;


@Getter
@Setter
public class UsuariTascaFiltreDto implements Serializable {

	private TascaEstatEnumDto[] estats;
	private Long expedientId;
	private Date dataInici;
	private Date dataFi;
	private Date dataLimitInici;
	private Date dataLimitFi;

	private String titol;
	private Long metaExpedientId;
	private Long metaExpedientTascaId;
	private PrioritatEnumDto prioritat;

	private static final long serialVersionUID = 1L;
}
