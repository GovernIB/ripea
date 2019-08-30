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
	private String expressioNumero;
	private boolean notificacioActiva;
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
	public String getExpressioNumero() {
		return expressioNumero;
	}
	public void setExpressioNumero(String expressioNumero) {
		this.expressioNumero = expressioNumero;
	}
	public boolean isNotificacioActiva() {
		return notificacioActiva;
	}
	public void setNotificacioActiva(boolean notificacioActiva) {
		this.notificacioActiva = notificacioActiva;
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
