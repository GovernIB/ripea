package es.caib.ripea.persistence.entity.resourceentity.config;

import java.util.Set;

import javax.persistence.*;

import es.caib.ripea.persistence.base.entity.ResourceEntity;
import es.caib.ripea.service.intf.config.BaseConfig;
import es.caib.ripea.service.intf.model.ConfigGroupResource;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(	name = BaseConfig.DB_PREFIX + "CONFIG_GROUP")
@Getter
@Setter
@NoArgsConstructor
public class ConfigGroupResourceEntity implements ResourceEntity<ConfigGroupResource, String> {

    @Id
    @Column(name = "CODE", length = 128, nullable = false)
    private String key;

    @Column(name = "DESCRIPTION", length = 512, nullable = true)
    private String description;

    @Column(name = "POSITION")
    private int position;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_CODE", updatable = false)
    private ConfigGroupResourceEntity parent;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "GROUP_CODE")
    @OrderBy("position ASC")
    private Set<ConfigResourceEntity> configs;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_CODE")
    @OrderBy("position ASC")
    private Set<ConfigGroupResourceEntity> children;

	@Override
	public String getId() {
		return this.key;
	}

	@Override
	public boolean isNew() {
		return this.key == null;
	}
}
