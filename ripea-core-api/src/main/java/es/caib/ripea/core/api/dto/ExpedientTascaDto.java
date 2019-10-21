package es.caib.ripea.core.api.dto;

import java.util.Date;


public class ExpedientTascaDto {
	
	private Long id;
	private ExpedientDto expedient;
	private MetaExpedientTascaDto metaExpedientTasca;
	private UsuariDto responsable;
	private Date dataInici;
	private Date dataFi;
	private TascaEstatEnumDto estat;
	private String responsableCodi;
	private Long metaExpedientTascaId;
	private String motiuRebuig;
	
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
	public UsuariDto getResponsable() {
		return responsable;
	}
	public void setResponsable(UsuariDto responsable) {
		this.responsable = responsable;
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
	public String getResponsableCodi() {
		return responsableCodi;
	}
	public void setResponsableCodi(String responsableCodi) {
		this.responsableCodi = responsableCodi;
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
	
}
