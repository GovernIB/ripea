package es.caib.ripea.service.intf.dto;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;
import java.util.Date;

/**
 * Informació del filtre d'expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class ExpedientFiltreDto implements Serializable {

	private Long organGestorId;
	private Long metaExpedientId;
	private String metaExpedientDominiCodi;
	private String nom;
	private Date dataCreacioInici;
	private Date dataCreacioFi;
	private String numero;
	private Long expedientEstatId;
	private ExpedientEstatEnumDto estat;
	private Date dataTancatInici;	
	private Date dataTancatFi;
	private boolean meusExpedients;
	private String agafatPer;
	private String search;	
	private Long tipusId;
	private String interessat;
	private String metaExpedientDominiValor;
	private boolean ambFirmaPendent;
	private String numeroRegistre;	
	private Long grupId;	
	private boolean expedientsSeguits;
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	private static final long serialVersionUID = -139254994389509932L;
}