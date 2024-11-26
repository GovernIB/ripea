/**
 * 
 */
package es.caib.ripea.core.api.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

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
@EqualsAndHashCode(callSuper=true)
public class ExecucioMassivaDto extends AuditoriaDto implements Serializable {

	private Long id;
	private ExecucioMassivaTipusDto tipus;
	private Date dataInici;
	private Date dataFi;
//	Paràmetres generació de ZIP
	private Boolean carpetes;
	private Boolean versioImprimible;
	private FileNameOption nomFitxer;
//	Paràmetres enviament portafirmes
	private String motiu;
	private PortafirmesPrioritatEnumDto prioritat = PortafirmesPrioritatEnumDto.NORMAL;
	private Date dataCaducitat;
	private String[] portafirmesResponsables;
	private MetaDocumentFirmaSequenciaTipusEnumDto portafirmesSequenciaTipus;
	private String portafirmesFluxId;
	private String portafirmesTransaccioId;
	private Boolean enviarCorreu;
	private List<Long> contingutIds = new ArrayList<Long>();
	private String rolActual;
	private Boolean portafirmesAvisFirmaParcial;
	private Boolean portafirmesFirmaParcial;
	private int errors;
	private int cancelats;
	private double executades;
	private String documentNom;

	public ExecucioMassivaDto() {}

	public ExecucioMassivaDto(
			ExecucioMassivaTipusDto tipus,
			Date dataInici,
			Date dataFi,
			String rolActual) {
		this.tipus = tipus;
		this.dataInici = dataInici;
		this.dataFi = dataFi;
		this.rolActual = rolActual;
	}
	
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