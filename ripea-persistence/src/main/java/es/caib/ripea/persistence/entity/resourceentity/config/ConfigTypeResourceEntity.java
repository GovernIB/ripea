package es.caib.ripea.persistence.entity.resourceentity.config;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import es.caib.ripea.persistence.base.entity.ResourceEntity;
import es.caib.ripea.service.intf.config.BaseConfig;
import es.caib.ripea.service.intf.model.ConfigTypeResource;
import lombok.Getter;

/**
 * Classe del model de dades que representa un del tipus de dades possibles per a una propietat de configuració.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Entity
@Table(	name = BaseConfig.DB_PREFIX + "CONFIG_TYPE")
public class ConfigTypeResourceEntity implements ResourceEntity<ConfigTypeResource, String> {
    @Id
    @Column(name = "CODE", length = 128, nullable = false)
    private String code;

    @Column(name = "VALUE", length = 2048, nullable = false)
    private String value;

    public List<String> getValidValues() {
        if (value == null || value.isEmpty()) {
            return Collections.emptyList();
        }

        String[] values = value.split(",");
        return Arrays.asList(values);
    }
    
	@Override
	public String getId() {
		return this.code;
	}
	@Override
	public boolean isNew() {
		return this.code == null;
	}
}
