package es.caib.ripea.persistence.entity.resourceentity;

import es.caib.ripea.persistence.base.entity.BaseAuditableEntity;
import es.caib.ripea.service.intf.config.BaseConfig;
import es.caib.ripea.service.intf.model.TipusDocumentalResource;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = BaseConfig.DB_PREFIX + "tipus_documental")
@Getter
@Setter
@NoArgsConstructor
@Inheritance(strategy=InheritanceType.JOINED)
public class TipusDocumentalResourceEntity extends BaseAuditableEntity<TipusDocumentalResource> {

    @Column(name = "codi", length = 64, nullable = false)
    private String codi;
    //	@Column(name = "codi_especific", length = 64)
//	private String codiEspecific;
    @Column(name = "nom", length = 256, nullable = false)
    private String nomEspanyol;
    @Column(name = "nom_catala", length = 256)
    private String nomCatala;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "entitat_id")
    protected EntitatResourceEntity entitat;

}
