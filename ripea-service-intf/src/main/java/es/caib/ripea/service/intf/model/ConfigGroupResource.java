package es.caib.ripea.service.intf.model;

import es.caib.ripea.service.intf.base.annotation.ResourceConfig;
import es.caib.ripea.service.intf.base.model.BaseResource;
import es.caib.ripea.service.intf.base.model.ResourceReference;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.Transient;

@SuppressWarnings("serial")
@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@ResourceConfig(
        quickFilterFields = { "key", "description" },
        descriptionField = "description")
public class ConfigGroupResource extends BaseResource<String> {
    private String key;
    private String description;
    private int position;
    private ResourceReference<ConfigGroupResource, String> parent;
    private int childrens;

    @Transient
    public String getId() {
        return key;
    }
}