package es.caib.ripea.service.intf.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Transient;

import es.caib.ripea.service.intf.base.annotation.ResourceConfig;
import es.caib.ripea.service.intf.base.model.BaseAuditableResource;
import es.caib.ripea.service.intf.base.model.ResourceReference;
import es.caib.ripea.service.intf.dto.MetaDadaTipusEnumDto;
import es.caib.ripea.service.intf.dto.MultiplicitatEnumDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

@Getter
@Setter
@FieldNameConstants
@NoArgsConstructor
@ResourceConfig(
        quickFilterFields = { "codi", "nom", "tipus" },
        descriptionField = "nom"
)
public class MetaDadaResource extends BaseAuditableResource<Long> {

	@NotNull private String codi;
    @NotNull private String nom;
    @NotNull private MetaDadaTipusEnumDto tipus = MetaDadaTipusEnumDto.TEXT;
    private String descripcio;
    @NotNull private MultiplicitatEnumDto multiplicitat = MultiplicitatEnumDto.M_1;
    private boolean readOnly;
    private int ordre;
    private boolean activa;

    private Long valorSencer;
    private Double valorFlotant;
    private BigDecimal valorImport;
    private LocalDateTime valorData;
    private Boolean valorBoolea;
    private String valorString;
    private ResourceReference<DominiResource, Long> domini;
    
    private boolean noAplica;

    private boolean enviable;
    private String metadadaArxiu;

    protected ResourceReference<MetaNodeResource, Long> metaNode;

    public String getCodiNom(){
        return this.codi + " - " + this.nom;
    }

    @Transient private List<DadaResource> dades;
    
    @Transient private boolean importar = true;
    
    private static final long serialVersionUID = 752278996924931848L;
}