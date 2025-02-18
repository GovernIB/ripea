package es.caib.ripea.service.intf.dto;

import es.caib.ripea.service.intf.utils.Utils;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
public class ExpedientTascaDto {
	
	private Long id;
	private ExpedientDto expedient;
	private MetaExpedientTascaDto metaExpedientTasca;
	private Date dataInici;
	private Date dataFi;
	private TascaEstatEnumDto estat;
	private List<String> responsablesCodi;
	private Long metaExpedientTascaId;
	private String motiuRebuig;
	private Date dataLimit;
	@SuppressWarnings("unused")
	private String dataLimitString;
	private boolean shouldNotifyAboutDeadline;
	@SuppressWarnings("unused")
	private boolean dataLimitExpirada;
	private String comentari;
	private long numComentaris;
	
	private boolean usuariActualResponsable;
	private boolean usuariActualObservador;
	private boolean usuariActualDelegat;
	
	private String titol;
	private String observacions;
	private Integer duracio;
	@SuppressWarnings("unused")
	private String duracioFormat;
	private PrioritatEnumDto prioritat;
	
	private UsuariDto createdBy;
	private UsuariDto responsableActual; 
	private List<UsuariDto> responsables;
	private List<UsuariDto> observadors;
	private List<UsuariDto> seguidors;
	private List<String> observadorsCodi;
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
		String resultat = Utils.duracioEnDiesToString(this.duracio);
		String restants = Utils.diesRestantsToString(this.dataLimit);
		if (resultat!=null) {
			if (this.dataLimit==null) {
				return resultat;
			} else {
				resultat+=" ("+restants+")";
			}
		} else {
			return restants;
		}
		return resultat;
	}	
	
	public boolean isDataLimitExpirada() {
		return (this.dataLimit!=null && this.dataLimit.before(Calendar.getInstance().getTime()));
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
		if (metaExpedientTasca!=null) {
			return StringUtils.abbreviate(metaExpedientTasca.getDescripcio(), 70);
		} else {
			return "";
		}
	}
}
