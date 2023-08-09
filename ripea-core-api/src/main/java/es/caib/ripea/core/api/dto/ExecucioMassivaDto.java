/**
 * 
 */
package es.caib.ripea.core.api.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * DTO amb informació d'una execució massiva
 * de continguts.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
public class ExecucioMassivaDto extends AuditoriaDto implements Serializable {

	public enum ExecucioMassivaTipusDto {
		PORTASIGNATURES
	}

	private Long id;
	private ExecucioMassivaTipusDto tipus;
	private Date dataInici;
	private Date dataFi;
	
//	Paràmetres enviament portafirmes
	private String motiu;
	private PortafirmesPrioritatEnumDto prioritat = PortafirmesPrioritatEnumDto.NORMAL;
	private Date dataCaducitat;
	private String[] portafirmesResponsables;
	private MetaDocumentFirmaSequenciaTipusEnumDto portafirmesSequenciaTipus;
	private String portafirmesFluxId;
	private String portafirmesTransaccioId;

//	////////////////////////////////////
	
	private Boolean enviarCorreu;
	private List<Long> contingutIds = new ArrayList<Long>();
	private String rolActual;
	
	private Boolean portafirmesAvisFirmaParcial;
	
	private int errors;
	private double executades;

	public String getPortafirmesResponsablesString() {
		if (portafirmesResponsables == null || portafirmesResponsables.length == 0) {
			return null;
		}

		String result = "";
		StringBuilder sb = new StringBuilder();

		for (String responsable : portafirmesResponsables) {
			sb.append(responsable).append(",");
		}

		return sb.deleteCharAt(sb.length() - 1).toString();
	}

	private static final long serialVersionUID = 4061379951434174596L;
}
