package es.caib.ripea.core.api.dto;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;



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
	public Date getDataLimit() {
		return dataLimit;
	}
	public void setDataLimit(Date dataLimit) {
		this.dataLimit = dataLimit;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public ExpedientDto getExpedient() {
		return expedient;
	}
	public void setExpedient(ExpedientDto expedient) {
		this.expedient = expedient;
	}
	public List<UsuariDto> getResponsables() {
		return responsables;
	}
	public void setResponsables(List<UsuariDto> responsables) {
		this.responsables = responsables;
	}
	public Date getDataInici() {
		return dataInici;
	}
	public void setDataInici(Date dataInici) {
		this.dataInici = dataInici;
	}
	public Date getDataFi() {
		return dataFi;
	}
	public void setDataFi(Date dataFi) {
		this.dataFi = dataFi;
	}
	public TascaEstatEnumDto getEstat() {
		return estat;
	}
	public void setEstat(TascaEstatEnumDto estat) {
		this.estat = estat;
	}
	public List<String> getResponsablesCodi() {
		return responsablesCodi;
	}
	public void setResponsablesCodi(List<String> responsablesCodi) {
		this.responsablesCodi = responsablesCodi;
	}
	public MetaExpedientTascaDto getMetaExpedientTasca() {
		return metaExpedientTasca;
	}
	public void setMetaExpedientTasca(MetaExpedientTascaDto metaExpedientTasca) {
		this.metaExpedientTasca = metaExpedientTasca;
	}
	public Long getMetaExpedientTascaId() {
		return metaExpedientTascaId;
	}
	public void setMetaExpedientTascaId(Long metaExpedientTascaId) {
		this.metaExpedientTascaId = metaExpedientTascaId;
	}
	public String getMotiuRebuig() {
		return motiuRebuig;
	}
	public void setMotiuRebuig(String motiuRebuig) {
		this.motiuRebuig = motiuRebuig;
	}
	public UsuariDto getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(UsuariDto createdBy) {
		this.createdBy = createdBy;
	}
	public boolean isShouldNotifyAboutDeadline() {
		return shouldNotifyAboutDeadline;
	}
	public void setShouldNotifyAboutDeadline(boolean shouldNotifyAboutDeadline) {
		this.shouldNotifyAboutDeadline = shouldNotifyAboutDeadline;
	}
	public String getComentari() {
		return comentari;
	}
	public void setComentari(String comentari) {
		this.comentari = comentari;
	}

	public UsuariDto getResponsableActual() {
		return responsableActual;
	}

	public void setResponsableActual(UsuariDto responsableActual) {
		this.responsableActual = responsableActual;
	}
	
}
