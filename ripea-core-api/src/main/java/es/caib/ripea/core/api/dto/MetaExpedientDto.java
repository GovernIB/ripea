/**
 * 
 */
package es.caib.ripea.core.api.dto;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Informació d'un MetaExpedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class MetaExpedientDto extends MetaNodeAmbMetaDadesDto implements Serializable {

	private String classificacioSia;
	private String serieDocumental;
	private boolean notificacioActiva;
	private String notificacioSeuProcedimentCodi;
	private String notificacioSeuRegistreLlibre;
	private String notificacioSeuRegistreOficina;
	private String notificacioSeuRegistreOrgan;
	private String notificacioSeuExpedientUnitatOrganitzativa;
	private String notificacioAvisTitol;
	private String notificacioAvisText;
	private String notificacioAvisTextMobil;
	private String notificacioOficiTitol;
	private String notificacioOficiText;
	private Long pareId;
	private List<MetaDocumentDto> metaDocuments;
	
	private int expedientEstatsCount;
	

	public int getExpedientEstatsCount() {
		return expedientEstatsCount;
	}
	public void setExpedientEstatsCount(int expedientEstatsCount) {
		this.expedientEstatsCount = expedientEstatsCount;
	}
	public String getClassificacioSia() {
		return classificacioSia;
	}
	public void setClassificacioSia(String classificacioSia) {
		this.classificacioSia = classificacioSia;
	}
	public String getSerieDocumental() {
		return serieDocumental;
	}
	public void setSerieDocumental(String serieDocumental) {
		this.serieDocumental = serieDocumental;
	}
	public boolean isNotificacioActiva() {
		return notificacioActiva;
	}
	public void setNotificacioActiva(boolean notificacioActiva) {
		this.notificacioActiva = notificacioActiva;
	}
	public String getNotificacioSeuProcedimentCodi() {
		return notificacioSeuProcedimentCodi;
	}
	public void setNotificacioSeuProcedimentCodi(String notificacioSeuProcedimentCodi) {
		this.notificacioSeuProcedimentCodi = notificacioSeuProcedimentCodi;
	}
	public String getNotificacioSeuRegistreLlibre() {
		return notificacioSeuRegistreLlibre;
	}
	public void setNotificacioSeuRegistreLlibre(String notificacioSeuRegistreLlibre) {
		this.notificacioSeuRegistreLlibre = notificacioSeuRegistreLlibre;
	}
	public String getNotificacioSeuRegistreOficina() {
		return notificacioSeuRegistreOficina;
	}
	public void setNotificacioSeuRegistreOficina(String notificacioSeuRegistreOficina) {
		this.notificacioSeuRegistreOficina = notificacioSeuRegistreOficina;
	}
	public String getNotificacioSeuRegistreOrgan() {
		return notificacioSeuRegistreOrgan;
	}
	public void setNotificacioSeuRegistreOrgan(String notificacioSeuRegistreOrgan) {
		this.notificacioSeuRegistreOrgan = notificacioSeuRegistreOrgan;
	}
	public String getNotificacioSeuExpedientUnitatOrganitzativa() {
		return notificacioSeuExpedientUnitatOrganitzativa;
	}
	public void setNotificacioSeuExpedientUnitatOrganitzativa(String notificacioSeuExpedientUnitatOrganitzativa) {
		this.notificacioSeuExpedientUnitatOrganitzativa = notificacioSeuExpedientUnitatOrganitzativa;
	}
	public String getNotificacioAvisTitol() {
		return notificacioAvisTitol;
	}
	public void setNotificacioAvisTitol(String notificacioAvisTitol) {
		this.notificacioAvisTitol = notificacioAvisTitol;
	}
	public String getNotificacioAvisText() {
		return notificacioAvisText;
	}
	public void setNotificacioAvisText(String notificacioAvisText) {
		this.notificacioAvisText = notificacioAvisText;
	}
	public String getNotificacioAvisTextMobil() {
		return notificacioAvisTextMobil;
	}
	public void setNotificacioAvisTextMobil(String notificacioAvisTextMobil) {
		this.notificacioAvisTextMobil = notificacioAvisTextMobil;
	}
	public String getNotificacioOficiTitol() {
		return notificacioOficiTitol;
	}
	public void setNotificacioOficiTitol(String notificacioOficiTitol) {
		this.notificacioOficiTitol = notificacioOficiTitol;
	}
	public String getNotificacioOficiText() {
		return notificacioOficiText;
	}
	public void setNotificacioOficiText(String notificacioOficiText) {
		this.notificacioOficiText = notificacioOficiText;
	}
	public Long getPareId() {
		return pareId;
	}
	public void setPareId(Long pareId) {
		this.pareId = pareId;
	}
	public List<MetaDocumentDto> getMetaDocuments() {
		return metaDocuments;
	}
	public void setMetaDocuments(List<MetaDocumentDto> metaDocuments) {
		this.metaDocuments = metaDocuments;
	}

	public int getMetaDocumentsCount() {
		if  (metaDocuments == null)
			return 0;
		else
			return metaDocuments.size();
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MetaExpedientDto other = (MetaExpedientDto) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	private static final long serialVersionUID = -139254994389509932L;

}
