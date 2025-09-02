package es.caib.ripea.persistence.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import es.caib.ripea.service.intf.config.BaseConfig;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = BaseConfig.DB_PREFIX + "metaDocumentFlux")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
public class MetaDocumentFluxPortafibEntity extends RipeaAuditable<Long> {

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "metaDocument_id", foreignKey = @javax.persistence.ForeignKey(name = BaseConfig.DB_PREFIX + "metaDocument_fk"))
    private MetaDocumentEntity metaDocument;
    
	@Column(name = "portafirmes_flux_id")
	private String portafirmesFluxId;
	
	@Column(name = "portafirmes_flux_desc")
	private String portafirmesFluxDesc;
	
}