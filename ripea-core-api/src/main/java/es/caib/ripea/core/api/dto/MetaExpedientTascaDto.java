/**
 * 
 */
package es.caib.ripea.core.api.dto;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Informació d'una tasca d'un meta-expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
@NoArgsConstructor
public class MetaExpedientTascaDto implements Serializable {

	private Long id;
	private String codi;
	private String nom;
	private String descripcio;
	private String responsable;
	private boolean activa;
	private Date dataLimit;
	private String duracio = "1d";
	private PrioritatEnumDto prioritat = PrioritatEnumDto.B_NORMAL;
	private Long estatIdCrearTasca;
	private String estatNomCrearTasca;
	private String estatColorCrearTasca;
	private Long estatIdFinalitzarTasca;
	private String estatNomFinalitzarTasca;
	private String estatColorFinalitzarTasca;

	public String getDataLimitString() {
		if (dataLimit != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			return sdf.format(this.dataLimit);
		} else {
			return "";
		}
	}

	private static final long serialVersionUID = -139254994389509932L;

}
