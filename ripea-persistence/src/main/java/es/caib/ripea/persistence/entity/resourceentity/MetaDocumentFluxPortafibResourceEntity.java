package es.caib.ripea.persistence.entity.resourceentity;

import es.caib.ripea.persistence.base.entity.BaseAuditableEntity;
import es.caib.ripea.service.intf.config.BaseConfig;
import es.caib.ripea.service.intf.model.MetaDocumentFluxPortafibResource;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Entity
@Table(name = BaseConfig.DB_PREFIX + "metaDocumentFlux")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
public class MetaDocumentFluxPortafibResourceEntity extends BaseAuditableEntity<MetaDocumentFluxPortafibResource> {

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "metaDocument_id", foreignKey = @javax.persistence.ForeignKey(name = BaseConfig.DB_PREFIX + "metaDocument_fk"))
    private MetaDocumentResourceEntity metaDocument;
    
	@Column(name = "portafirmes_flux_id")
	private String portafirmesFluxId;
	
	@Column(name = "portafirmes_flux_desc")
	private String portafirmesFluxDesc;
	
}