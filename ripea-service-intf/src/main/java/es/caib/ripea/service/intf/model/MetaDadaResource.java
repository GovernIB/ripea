package es.caib.ripea.service.intf.model;

import es.caib.ripea.service.intf.base.annotation.ResourceConfig;
import es.caib.ripea.service.intf.base.annotation.ResourceConfigArtifact;
import es.caib.ripea.service.intf.base.model.BaseAuditableResource;
import es.caib.ripea.service.intf.base.model.ResourceArtifactType;
import es.caib.ripea.service.intf.base.model.ResourceReference;
import es.caib.ripea.service.intf.dto.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.Transient;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ResourceConfig(
        quickFilterFields = { "codi", "nom" },
        descriptionField = "nom"
)
public class MetaDadaResource extends BaseAuditableResource<Long> {

    private String codi;
    private String nom;
    private MetaDadaTipusEnumDto tipus;
    private String descripcio;
    private MultiplicitatEnumDto multiplicitat;
    private boolean readOnly;
    private int ordre;
    private boolean activa;

//    private DominiDto domini;

    private Long valorSencer;
    private Double valorFlotant;
    private BigDecimal valorImport;
    private Date valorData;
    private Boolean valorBoolea;
    private String valorString;

    private boolean noAplica;

    private boolean enviable;
    private String metadadaArxiu;

    protected ResourceReference<MetaNodeResource, Long> metaNode;

    public String getCodiNom(){
        return this.codi + " - " + this.nom;
    }

    @Transient
    private List<DadaResource> dades;
}