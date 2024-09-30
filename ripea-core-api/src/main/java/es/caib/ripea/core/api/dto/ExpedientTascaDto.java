package es.caib.ripea.core.api.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.StringUtils;

import es.caib.ripea.core.api.utils.Utils;

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
	@SuppressWarnings("unused")
	private String dataLimitString;
	private boolean shouldNotifyAboutDeadline;
	private String comentari;
	private long numComentaris;
	private boolean usuariActualResponsable;
	private String titol;
	private String observacions;
	private Integer duracio;
	@SuppressWarnings("unused")
	private String duracioFormat;
	private PrioritatEnumDto prioritat;
	private List<String> observadorsCodi;
	private List<UsuariDto> observadors;
	private boolean usuariActualObservador;
	private boolean usuariActualDelegat;
	private UsuariDto delegat;
	
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
	
	public boolean isAgafada() {
		return responsableActual != null;
	}
	
	public boolean isDelegada() {
		return delegat != null;
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
