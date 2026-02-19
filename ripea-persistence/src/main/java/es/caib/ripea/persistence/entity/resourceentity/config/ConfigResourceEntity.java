package es.caib.ripea.persistence.entity.resourceentity.config;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.persistence.*;

import es.caib.ripea.persistence.entity.resourceentity.UsuariResourceEntity;
import org.hibernate.annotations.ForeignKey;

import es.caib.ripea.persistence.base.entity.ResourceEntity;
import es.caib.ripea.service.intf.config.BaseConfig;
import es.caib.ripea.service.intf.model.ConfigResource;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Classe del model de dades que representa una alerta d'error en segón pla.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(	name = BaseConfig.DB_PREFIX + "config")
@Getter
@Setter
@NoArgsConstructor
public class ConfigResourceEntity implements ResourceEntity<ConfigResource, String> {
    @Id
    @Column(name = "KEY", length = 256, updatable = false, nullable = false)
    private String key;

    @Column(name = "KEY", insertable = false, updatable = false)
    private String id;

    @Column(name = "VALUE", length = 2048, nullable = true)
    private String value;

    @Column(name = "DESCRIPTION", length = 2048, nullable = true)
    private String description;

    @Column(name = "JBOSS_PROPERTY", nullable = false)
    private boolean jbossProperty;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "GROUP_CODE", updatable = false)
    private ConfigGroupResourceEntity group;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "TYPE_CODE", updatable = false)
    @ForeignKey(name = "NOT_CONFIG_TYPE_FK")
    private ConfigTypeResourceEntity type;
    
    @Column(name = "ENTITAT_CODI", length = 64)
    private String entitatCodi;
    
    @Column(name = "CONFIGURABLE")
    private boolean configurable;
    
    @Column(name = "configurable_entitat_actiu")
    private boolean configurableEntitatActiu;
    
    @Column(name = "configurable_organ")
    private boolean configurableOrgan;
    
    @Column(name = "configurable_organ_actiu")
    private boolean configurableOrganActiu;

    @Column(name = "configurable_org_descendents")
    private boolean configurableOrgansDescendents;
    
    @Column(name = "organ_codi", length = 64)
    private String organCodi;

    @Column(name = "POSITION")
    private int position;

    @ManyToOne
    @JoinColumn(name = "LASTMODIFIEDBY_CODI", updatable = false)
    private UsuariResourceEntity lastModifiedBy;

    @Temporal(TemporalType.TIMESTAMP)
    @JoinColumn(name = "LASTMODIFIEDDATE", updatable = false)
    private Date lastModifiedDate;

    /**
     * Per a mapejar el Dto de la vista.
     *
     * @return El llistat de possibles valors que pot prendre la propietat
     */
    public List<String> getValidValues() {
       return type == null ? Collections.<String>emptyList() : type.getValidValues();
    }
    public String getTypeCode() {
        return type == null ? "" : type.getCode();
    }

    public void update(String value) {
        this.value = value;
    }

	@Override
	public String getId() {
		return this.key;
	}
	@Override
	public boolean isNew() {
		return this.key == null;
	}
   
}