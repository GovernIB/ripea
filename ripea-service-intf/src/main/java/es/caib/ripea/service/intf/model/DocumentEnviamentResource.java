package es.caib.ripea.service.intf.model;

import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Transient;

import es.caib.ripea.service.intf.base.annotation.ResourceConfig;
import es.caib.ripea.service.intf.base.model.BaseAuditableResource;
import es.caib.ripea.service.intf.base.model.ResourceReference;
import es.caib.ripea.service.intf.dto.DocumentEnviamentEstatEnumDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

@Getter
@Setter
@FieldNameConstants
@NoArgsConstructor
@ResourceConfig(quickFilterFields = { "assumpte"}, descriptionField = "assumpte")
public abstract class DocumentEnviamentResource extends BaseAuditableResource<Long> {

	private DocumentEnviamentEstatEnumDto estat;
    private String assumpte;
    private String observacions;
    private Date enviatData;
    private Date processatData;
    private Date cancelatData;
    private boolean error;
    private String errorDescripcio;
    private int intentNum;
    private Date intentData;
    private Date intentProximData;

    @Transient
    private String fitxerNom;

    private ResourceReference<ExpedientResource, Long> expedient;
    private ResourceReference<DocumentResource, Long> document;
    private List<ResourceReference<DocumentResource, Long>> annexos;
    
    private static final long serialVersionUID = 8126062976200294740L;
}