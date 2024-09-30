/**
 * 
 */
package es.caib.ripea.core.api.dto;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import es.caib.ripea.core.api.utils.Utils;
import lombok.Data;
import lombok.NoArgsConstructor;

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
	@SuppressWarnings("unused")
	private String dataLimitString;
	private Integer duracio = 10;
	@SuppressWarnings("unused")
	private String duracioFormat;
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

	public String getDuracioFormat() {
		if (this.duracio!=null) {
			return Utils.duracioEnDiesToString(this.duracio);
		} else {
			return getDataLimitString();
		}
	}
	
	private static final long serialVersionUID = -139254994389509932L;

}
