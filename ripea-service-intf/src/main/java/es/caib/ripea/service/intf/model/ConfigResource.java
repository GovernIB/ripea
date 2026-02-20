package es.caib.ripea.service.intf.model;

import java.util.Date;

import org.springframework.data.annotation.Transient;

import es.caib.ripea.service.intf.base.annotation.ResourceConfig;
import es.caib.ripea.service.intf.base.annotation.ResourceField;
import es.caib.ripea.service.intf.base.model.BaseResource;
import es.caib.ripea.service.intf.base.model.ResourceReference;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

@SuppressWarnings("serial")
@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@ResourceConfig(
        quickFilterFields = { "key", "value", "description" },
        descriptionField = "description")
public class ConfigResource extends BaseResource<String> {

    private String key;
    private String value;
    private String description;
    private boolean jbossProperty;
    
    private ResourceReference<ConfigGroupResource, String> group;
    private ResourceReference<ConfigTypeResource, String> type;
    
    @ResourceField(onChangeActive = true)
    private ResourceReference<EntitatResource, Long> entitat;
    @ResourceField(onChangeActive = true)
    private ResourceReference<OrganGestorResource, Long> organ;
    private String entitatCodi;
    private String organCodi;
    
    private boolean configurable;
    private boolean configurableEntitatActiu;
    private boolean configurableOrgan;
    private boolean configurableOrganActiu;
    private boolean configurableOrgansDescendents;    
    private int position;
    
    private ResourceReference<UsuariResource, String> lastModifiedBy;
    private Date lastModifiedDate;

    @Transient
    public String getId() {
        return key;
    }
}