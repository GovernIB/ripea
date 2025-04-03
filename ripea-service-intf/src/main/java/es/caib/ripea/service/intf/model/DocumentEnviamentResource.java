package es.caib.ripea.service.intf.model;

import es.caib.ripea.service.intf.base.annotation.ResourceConfig;
import es.caib.ripea.service.intf.base.model.BaseAuditableResource;
import es.caib.ripea.service.intf.base.model.Resource;
import es.caib.ripea.service.intf.base.model.ResourceReference;
import es.caib.ripea.service.intf.dto.*;
import es.caib.ripea.service.intf.resourcevalidation.InteressatValid;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Transient;

import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@InteressatValid(groups = {Resource.OnCreate.class, Resource.OnUpdate.class})
@ResourceConfig(quickFilterFields = { "documentNum", "nom" }, descriptionField = "nom")
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
}