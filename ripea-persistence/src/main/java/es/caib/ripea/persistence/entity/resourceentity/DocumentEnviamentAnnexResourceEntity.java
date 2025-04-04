package es.caib.ripea.persistence.entity.resourceentity;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;

import es.caib.ripea.persistence.base.entity.ResourceEntity;
import es.caib.ripea.service.intf.config.BaseConfig;
import es.caib.ripea.service.intf.dto.DocumentAnnexId;
import es.caib.ripea.service.intf.model.DocumentEnviamentAnnexResource;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = BaseConfig.DB_PREFIX + "DOCUMENT_ENVIAMENT_DOC")
@Getter
@Setter
@NoArgsConstructor
public class DocumentEnviamentAnnexResourceEntity implements ResourceEntity<DocumentEnviamentAnnexResource, DocumentAnnexId> {

	@EmbeddedId
    private DocumentAnnexId id;
	
	@Override	
	public DocumentAnnexId getId() {
		return id;
	}

	@Override
	public boolean isNew() {
		return null == getId();
	}
	
    @SuppressWarnings("rawtypes")
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
    @MapsId("document_enviament_id")
    @JoinColumn(name = "DOCUMENT_ENVIAMENT_ID", referencedColumnName = "id")
	private DocumentEnviamentResourceEntity documentEnviament;
	
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @MapsId("document_id")
    @JoinColumn(name = "DOCUMENT_ID", referencedColumnName = "id")
	private DocumentResourceEntity document;
}