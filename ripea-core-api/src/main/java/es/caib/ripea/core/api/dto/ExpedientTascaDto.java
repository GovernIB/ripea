package es.caib.ripea.core.api.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
public class ExpedientTascaDto {
	
	private Long id;
	private ExpedientDto expedient;
	private MetaExpedientTascaDto metaExpedientTasca;
	private UsuariDto responsableActual; 
	private List<UsuariDto> responsables;
	private Date dataInici;
	private Date dataFi;
	private TascaEstatEnumDto estat;
	private List<String> responsablesCodi;
	private Long metaExpedientTascaId;
	private String motiuRebuig;
	private UsuariDto createdBy;
	private Date dataLimit;
	private boolean shouldNotifyAboutDeadline;
	private String comentari;
	private long numComentaris;
	
	public String getDataLimitString() {
		if (dataLimit != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			return sdf.format(this.dataLimit);
		} else {
			return "";
		}
	}
	
	public boolean isAgafada() {
		return responsableActual != null;
	}

	public String getResponsablesStr() {
		List<String> responsablesStr = new ArrayList<String>();
		for (UsuariDto usuariDto : responsables) {
			responsablesStr.add(usuariDto.getCodi());
		}
		return StringUtils.join(responsablesStr, ",");
	}
	
	public String getMetaExpedientTascaDescAbrv() {
		return StringUtils.abbreviate(metaExpedientTasca.getDescripcio(), 70);
	}

}
